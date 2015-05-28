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
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.utils.Util;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hellblazer.utils.collections.OaHashSet;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceSnapshot {
    public static ArrayList<WorkspaceAuthorization> getAuthorizations(Product definingProduct,
                                                                      EntityManager em) {
        TypedQuery<WorkspaceAuthorization> query = em.createQuery("SELECT auth FROM WorkspaceAuthorization auth "
                                                                          + "WHERE auth.definingProduct = :product",
                                                                  WorkspaceAuthorization.class);
        query.setParameter("product", definingProduct);
        ArrayList<WorkspaceAuthorization> authorizations = new ArrayList<>(
                                                                           query.getResultList());
        return authorizations;
    }

    @JsonSerialize
    protected final List<WorkspaceAuthorization> auths;
    @JsonSerialize
    protected final List<Ruleform>               frontier;

    public WorkspaceSnapshot() {
        auths = null;
        frontier = new ArrayList<>();
    }

    public WorkspaceSnapshot(List<WorkspaceAuthorization> auths) {
        this.auths = auths;
        Set<UUID> included = new OaHashSet<UUID>(1024);
        for (WorkspaceAuthorization auth : auths) {
            included.add(auth.getRuleform().getId());
        }
        Map<Ruleform, Ruleform> exits = new HashMap<>();
        Set<UUID> traversed = new OaHashSet<UUID>(1024);
        for (WorkspaceAuthorization auth : auths) {
            Util.slice(auth.getRuleform(),
                       ruleform -> included.contains(ruleform.getId()), exits,
                       traversed);
        }
        frontier = new ArrayList<>(exits.values());
    }

    public WorkspaceSnapshot(Product definingProduct, EntityManager em) {
        this(getAuthorizations(definingProduct, em));
    }

    public List<WorkspaceAuthorization> getAuths() {
        return auths;
    }

    public List<Ruleform> getFrontier() {
        return frontier;
    }

    public void retarget(EntityManager em) {
        Map<Ruleform, Ruleform> theReplacements = new HashMap<>();
        for (Ruleform exit : frontier) {
            theReplacements.put(exit, em.find(exit.getClass(), exit.getId()));
        }
        for (WorkspaceAuthorization auth : auths) {
            Util.smartMerge(em, auth, theReplacements);
        }
    }
}
