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

package com.chiralbehaviors.CoRE.workspace;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
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

import com.chiralbehaviors.CoRE.jooq.Ruleform;
import com.chiralbehaviors.CoRE.jooq.tables.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.json.CoREModule;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellblazer.utils.collections.OaHashSet;

/**
 * @author hhildebrand
 *
 */
@JsonPropertyOrder({ "frontier", "definingProduct", "ruleforms" })
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

    public static void load(EntityManager em,
                            List<URL> resources) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new CoREModule());
        for (URL resource : resources) {
            WorkspaceSnapshot workspace;
            try (InputStream is = resource.openStream();) {
                workspace = mapper.readValue(is, WorkspaceSnapshot.class);
            } catch (IOException e) {
                log.warn("Unable to load workspace: {}",
                         resource.toExternalForm(), e);
                throw e;
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
        }
    }

    public static void load(EntityManager em, URL resource) throws IOException {
        load(em, Collections.singletonList(resource));
    }

    @JsonProperty
    private Product        definingProduct;
    @JsonProperty
    private List<Ruleform> frontier;
    @JsonProperty
    private List<Ruleform> ruleforms;

    public WorkspaceSnapshot() {
        ruleforms = new ArrayList<>();
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
        this.ruleforms = new ArrayList<>(auths.size());
        Predicate<Ruleform> systemDefinition = traversing -> sameWorkspace(traversing);
        Map<UUID, Ruleform> exits = new HashMap<>();
        Set<UUID> traversed = new OaHashSet<UUID>(1024);

        for (WorkspaceAuthorization auth : auths) {
            if (!auth.getDefiningProduct()
                     .getId()
                     .equals(this.definingProduct.getId())) {
                throw new IllegalStateException(String.format("%s is not in the workspace %s",
                                                              auth.getDefiningProduct()
                                                                  .getName(),
                                                              this.definingProduct.getName()));
            }
            Ruleform ruleform = Ruleform.initializeAndUnproxy(auth.getRuleform(em));
            ruleforms.add(ruleform);
        }

        for (Ruleform ruleform : ruleforms) {
            Ruleform.slice(ruleform, systemDefinition, exits, traversed);
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
            return this; // by workspace graph closure definition
        }

        WorkspaceSnapshot delta = new WorkspaceSnapshot();
        delta.definingProduct = definingProduct;

        Set<UUID> exclude = new OaHashSet<UUID>(1024);
        for (Ruleform ruleform : otherVersion.ruleforms) {
            if (!definingProduct.equals(ruleform)) {
                exclude.add(ruleform.getId());
            }
        }

        for (Ruleform ruleform : ruleforms) {
            if (!exclude.contains(ruleform.getId())) {
                delta.ruleforms.add(ruleform);
            }
        }

        Set<UUID> traversed = new OaHashSet<UUID>(1024);
        Map<UUID, Ruleform> exits = new HashMap<>();
        Predicate<Ruleform> systemDefinition = traversing -> !exclude.contains(traversing.getId())
                                                             && sameWorkspace(traversing);
        for (Ruleform ruleform : delta.ruleforms) {
            Ruleform.slice(ruleform, systemDefinition, exits, traversed);
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
        Map<UUID, Ruleform> theReplacements = new HashMap<>();
        for (Ruleform exit : frontier) {
            Ruleform someDudeIKnow = em.find(exit.getClass(), exit.getId());
            if (someDudeIKnow == null) {
                throw new IllegalStateException(String.format("Workspace: %s, unable to locate frontier: %s:%s",
                                                              definingProduct.getName(),
                                                              exit,
                                                              exit.getId()));
            }
            theReplacements.put(exit.getId(), someDudeIKnow);
        }
        definingProduct = Ruleform.smartMerge(em, definingProduct,
                                              theReplacements);
        for (ListIterator<Ruleform> iterator = ruleforms.listIterator(); iterator.hasNext();) {
            iterator.set(Ruleform.smartMerge(em, iterator.next(),
                                             theReplacements));
        }
    }

    public void serializeTo(OutputStream os) throws JsonGenerationException,
                                             JsonMappingException, IOException {
        ObjectMapper objMapper = new ObjectMapper();
        objMapper.registerModule(new CoREModule());
        objMapper.writerWithDefaultPrettyPrinter()
                 .writeValue(os, this);
    }

    public boolean validate() {
        return ruleforms.stream()
                        .map(ruleform -> ruleform.getWorkspace() != null)
                        .reduce((prev, cur) -> prev && cur)
                        .orElse(false);
    }

    private boolean sameWorkspace(Ruleform traversing) {
        return (traversing instanceof WorkspaceAuthorization
                && this.definingProduct.equals(((WorkspaceAuthorization) traversing).getDefiningProduct()))
               || (traversing.getWorkspace() != null
                   && this.definingProduct.equals(traversing.getWorkspace()
                                                            .getDefiningProduct()));
    }
}
