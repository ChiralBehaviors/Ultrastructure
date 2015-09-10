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

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.utils.Util;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hellblazer.utils.collections.OaHashSet;

/**
 * @author hhildebrand
 *
 */
@JsonPropertyOrder({ "name", "description", "version", "definingProduct",
                     "frontier", "ruleforms" })
public class WorkspaceSnapshot {
    public static List<WorkspaceAuthorization> getAuthorizations(Product definingProduct,
                                                                 EntityManager em) {
        TypedQuery<WorkspaceAuthorization> query = em.createQuery("SELECT auth FROM WorkspaceAuthorization auth "
                                                                  + "WHERE auth.definingProduct = :product",
                                                                  WorkspaceAuthorization.class);
        query.setParameter("product", definingProduct);
        List<WorkspaceAuthorization> authorizations = new ArrayList<>(query.getResultList());
        return authorizations;
    }

    @JsonProperty
    protected String         name;
    @JsonProperty
    protected String         description;
    @JsonProperty
    protected int            version;
    @JsonProperty
    protected Product        definingProduct;
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
        this.name = definingProduct.getName();
        this.description = definingProduct.getDescription();
        this.version = definingProduct.getVersion();
        Predicate<Ruleform> systemDefinition = traversing -> {
            return (traversing instanceof WorkspaceAuthorization
                    && definingProduct.equals(((WorkspaceAuthorization) traversing).getDefiningProduct()))
                   || (traversing.getWorkspace() != null
                       && definingProduct.equals(traversing.getWorkspace()
                                                           .getDefiningProduct()));
        };

        this.ruleforms = new ArrayList<>(auths.size());
        Set<UUID> included = new OaHashSet<>(1024);
        Map<Ruleform, Ruleform> exits = new HashMap<>();
        Set<UUID> traversed = new OaHashSet<UUID>(1024);

        included.add(definingProduct.getId());

        for (WorkspaceAuthorization auth : auths) {
            Ruleform ruleform = auth.getRuleform(em);
            included.add(ruleform.getId());
            ruleforms.add(ruleform);
        }

        for (Ruleform ruleform : ruleforms) {
            Util.slice(ruleform, systemDefinition, exits, traversed);
        }

        frontier = new ArrayList<>(exits.values());
    }

    public List<Ruleform> getFrontier() {
        return frontier;
    }

    public List<Ruleform> getRuleforms() {
        return ruleforms;
    }

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
}
