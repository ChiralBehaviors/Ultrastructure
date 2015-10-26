/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
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

import java.util.ArrayList;
import java.util.Collection;
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
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hellblazer.utils.collections.OaHashSet;

/**
 * @author hhildebrand
 *
 */
public class StateSnapshot {
    @JsonProperty
    private List<Ruleform> frontier  = new ArrayList<>(1024);
    @JsonProperty
    private List<Ruleform> ruleforms = new ArrayList<>(1024);

    public StateSnapshot() {
    }

    public StateSnapshot(EntityManager em) {
        Predicate<Ruleform> systemDefinition = traversing -> traversing.getWorkspace() == null;
        Map<UUID, Ruleform> exits = new HashMap<>();
        Set<UUID> traversed = new OaHashSet<UUID>(1024);

        Ruleform.CONCRETE_SUBCLASSES.values()
                                    .forEach(form -> {
                                        ruleforms.addAll(findAll(form, em));
                                    });

        for (Ruleform ruleform : ruleforms) {
            Ruleform.slice(ruleform, systemDefinition, exits, traversed);
        }

        frontier = new ArrayList<>(exits.values());
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
                throw new IllegalStateException(String.format("Unable to locate frontier: %s:%s",
                                                              exit,
                                                              exit.getId()));
            }
            theReplacements.put(exit.getId(), someDudeIKnow);
        }
        for (ListIterator<Ruleform> iterator = ruleforms.listIterator(); iterator.hasNext();) {
            iterator.set(Ruleform.smartMerge(em, iterator.next(),
                                             theReplacements));
        }
    }

    private Collection<? extends Ruleform> findAll(Class<? extends Ruleform> form,
                                                   EntityManager em) {
        TypedQuery<? extends Ruleform> query = NetworkRuleform.class.isAssignableFrom(form) ? em.createQuery(String.format("SELECT f FROM %s f WHERE f.workspace IS NULL AND f.inference IS NULL",
                                                                                                                           form.getSimpleName()),
                                                                                                             form)
                                                                                            : em.createQuery(String.format("SELECT f FROM %s f WHERE f.workspace IS NULL",
                                                                                                                           form.getSimpleName()),
                                                                                                             form);
        return query.getResultList();
    }

}
