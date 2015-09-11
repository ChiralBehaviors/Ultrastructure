/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.meta.workspace;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.utils.Util;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellblazer.utils.collections.OaHashSet;

/**
 * @author hhildebrand
 *
 */
@JsonPropertyOrder({ "definingProduct", "frontier", "ruleforms" })
public class WorkspaceSnapshot {
    private final static Logger log = LoggerFactory.getLogger(WorkspaceSnapshot.class);

    public static List<WorkspaceAuthorization> getAuthorizations(Product definingProduct,
                                                                 EntityManager em) {
        TypedQuery<WorkspaceAuthorization> query = em.createQuery("SELECT auth FROM WorkspaceAuthorization auth "
                                                                  + "WHERE auth.definingProduct = :product",
                                                                  WorkspaceAuthorization.class);
        query.setParameter("product", definingProduct);
        List<WorkspaceAuthorization> authorizations = new ArrayList<>(query.getResultList());
        return authorizations;
    }

    public static boolean load(EntityManager em, List<URL> resources) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new CoREModule());
        for (URL resource : resources) {
            WorkspaceSnapshot workspace;
            try (InputStream is = resource.openStream();) {
                workspace = mapper.readValue(is, WorkspaceSnapshot.class);
            } catch (IOException e) {
                log.warn("Unable to load workspace: {}",
                         resource.toExternalForm(), e);
                return false;
            }
            Product definingProduct = workspace.getDefiningProduct();
            Product existing = em.find(Product.class, definingProduct.getId());
            if (existing == null) {
                log.info("Creating workspace [{}] version: {} from: {}",
                         definingProduct.getName(),
                         definingProduct.getVersion(),
                         resource.toExternalForm());
                workspace.retarget(em);
            } else if (existing.getVersion() < definingProduct.getVersion()) {
                log.info("Updating workspace [{}] from version:{} to version: {} from: {}",
                         definingProduct.getName(), existing.getVersion(),
                         definingProduct.getVersion(),
                         resource.toExternalForm());
                workspace.retarget(em);
            } else {
                log.info("Not updating workspace [{}] existing version: {} is higher than version: {} from: {}",
                         definingProduct.getName(), existing.getVersion(),
                         definingProduct.getVersion(),
                         resource.toExternalForm());
            }
            em.flush();
        }
        return true;
    }

    protected Product definingProduct;

    @JsonProperty
    protected List<Ruleform> frontier;
    @JsonProperty
    protected List<Ruleform> ruleforms;

    public WorkspaceSnapshot() {
        ruleforms = null;
        definingProduct = null;
        frontier = new ArrayList<>();
    }

    public WorkspaceSnapshot(Product definingProduct, EntityManager em) {
        this(definingProduct, getAuthorizations(definingProduct, em), em);
    }

    public WorkspaceSnapshot(Product definingProduct,
                             List<WorkspaceAuthorization> auths,
                             EntityManager em) {
        this.definingProduct = definingProduct;
        Predicate<Ruleform> systemDefinition = traversing -> sameWorkspace(traversing);

        this.ruleforms = new ArrayList<>(auths.size());
        Set<UUID> included = new OaHashSet<>(1024);
        Map<Ruleform, Ruleform> exits = new HashMap<>();
        Set<UUID> traversed = new OaHashSet<UUID>(1024);

        included.add(definingProduct.getId());

        for (WorkspaceAuthorization auth : auths) {
            if (!auth.getDefiningProduct()
                     .equals(definingProduct)) {
                throw new IllegalStateException(String.format("%s is not in the workspace %s",
                                                              auth.getDefiningProduct()
                                                                  .getName(),
                                                              definingProduct.getName()));
            }
            Ruleform ruleform = auth.getRuleform(em);
            included.add(ruleform.getId());
            ruleforms.add(ruleform);
        }

        for (Ruleform ruleform : ruleforms) {
            Util.slice(ruleform, systemDefinition, exits, traversed);
        }

        frontier = new ArrayList<>(exits.values());
    }

    /**
     * Calculate the delta graph between this workspace and a different version.
     * The delta graph's frontier will include any references to ruleforms
     * defined in the previous workspace.
     * 
     * The delta graph is defined to be everything that is in this workspace
     * minus the things that are in the other version of the workspace.
     * Ruleforms remaining in this delta graph will have references to ruleforms
     * defined in the other version replaced with ruleforms in the frontier of
     * the returned workspace.
     * 
     * Note that this is a destructive operation, operating in place not copies
     * of the ruleforms. Consequently, this workspace and the other workspace
     * will no longer be valid and need to be discarded.
     * 
     * @param otherVersion
     *            - the other version of the workspace
     * @return the workspace snapshot containing the delta graph between this
     *         version and the other version
     */
    public WorkspaceSnapshot deltaFrom(WorkspaceSnapshot otherVersion) {
        if (!otherVersion.getDefiningProduct()
                         .equals(definingProduct)) {
            throw new IllegalArgumentException(String.format("%s:%s is not the same workspace as %s%s",
                                                             otherVersion.getDefiningProduct()
                                                                         .getName(),
                                                             otherVersion.getDefiningProduct()
                                                                         .getId(),
                                                             definingProduct.getName(),
                                                             definingProduct.getId()));
        }
        Predicate<Ruleform> systemDefinition = traversing -> isInSameVersionOfWorkspace(traversing);

        WorkspaceSnapshot delta = new WorkspaceSnapshot();
        delta.ruleforms = new ArrayList<>();
        delta.definingProduct = definingProduct;

        delta.ruleforms = new ArrayList<>();
        Set<UUID> included = new OaHashSet<>(1024);
        Map<Ruleform, Ruleform> exits = new HashMap<>();
        Set<UUID> traversed = new OaHashSet<UUID>(1024);

        included.add(definingProduct.getId());

        for (Ruleform ruleform : ruleforms) {
            if (isInSameVersionOfWorkspace(ruleform)) {
                included.add(ruleform.getId());
                delta.ruleforms.add(ruleform);
            }
        }

        for (Ruleform ruleform : delta.ruleforms) {
            Util.slice(ruleform, systemDefinition, exits, traversed);
        }

        delta.frontier = new ArrayList<>(exits.values());
        return delta;
    }

    public Product getDefiningProduct() {
        return definingProduct;
    }

    public List<Ruleform> getFrontier() {
        return frontier;
    }

    public List<Ruleform> getRuleforms() {
        return ruleforms;
    }

    /**
     * Merge the state of the workspace into the database
     * 
     * @param em
     */
    public void retarget(EntityManager em) {
        WorkspaceAuthorization defining = definingProduct.getWorkspace();
        definingProduct.setWorkspace(null);
        Map<Ruleform, Ruleform> theReplacements = new HashMap<>();
        for (Ruleform exit : frontier) {
            theReplacements.put(exit, em.find(exit.getClass(), exit.getId()));
        }
        definingProduct = Util.smartMerge(em, definingProduct, theReplacements);
        defining = Util.smartMerge(em, defining, theReplacements);
        for (ListIterator<Ruleform> iterator = ruleforms.listIterator(); iterator.hasNext();) {
            iterator.set(Util.smartMerge(em, iterator.next(), theReplacements));
        }
        definingProduct.setWorkspace(defining);
    }

    private boolean isInSameVersionOfWorkspace(Ruleform ruleform) {
        return (ruleform instanceof WorkspaceAuthorization
                && sameProductAndVersion(((WorkspaceAuthorization) ruleform).getDefiningProduct()))
               || (ruleform.getWorkspace() != null
                   && sameProductAndVersion((ruleform.getWorkspace()
                                                     .getDefiningProduct())));
    }

    private boolean sameProductAndVersion(Product product) {
        return definingProduct.equals(product)
               && definingProduct.getVersion() == product.getVersion();
    }

    private boolean sameWorkspace(Ruleform traversing) {
        return (traversing instanceof WorkspaceAuthorization
                && this.definingProduct.equals(((WorkspaceAuthorization) traversing).getDefiningProduct()))
               || (traversing.getWorkspace() != null
                   && this.definingProduct.equals(traversing.getWorkspace()
                                                            .getDefiningProduct()));
    }
}
