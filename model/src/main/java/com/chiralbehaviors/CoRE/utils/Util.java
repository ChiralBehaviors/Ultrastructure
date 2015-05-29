/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;

/**
 * @author hhildebrand
 *
 */
public final class Util {

    public static Map<Ruleform, Ruleform> slice(Ruleform ruleform,
                                                Predicate<Ruleform> systemDefinition,
                                                Map<Ruleform, Ruleform> sliced,
                                                Set<UUID> traversed) {
        map(ruleform, systemDefinition, sliced, traversed);
        return sliced;
    }

    public static <T extends Ruleform> T smartMerge(EntityManager em,
                                                    T ruleform,
                                                    Map<Ruleform, Ruleform> mapped) {
        return map(em, ruleform, mapped);
    }

    private static <T extends Ruleform> Ruleform find(EntityManager em,
                                                      T ruleform) {
        //        if (ruleform instanceof WorkspaceAuthorization) {
        //            TypedQuery<Long> existQueury = em.createNamedQuery(WorkspaceAuthorization.DOES_WORKSPACE_AUTH_EXIST,
        //                                                               Long.class);
        //            existQueury.setParameter("id", ruleform.getId());
        //            if (existQueury.getFirstResult() == 0) {
        //                return null;
        //            }
        //        }
        return em.find(ruleform.getClass(), ruleform.getId());
    }

    private static List<Field> getInheritedFields(Class<?> type) {
        List<Field> fields = new ArrayList<Field>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    private static Ruleform map(Ruleform value,
                                Predicate<Ruleform> systemDefinition,
                                Map<Ruleform, Ruleform> sliced,
                                Set<UUID> traversed) {
        Ruleform mappedValue = sliced.get(value);
        if (mappedValue != null) {
            // this value has already been determined to be part of another system
            return mappedValue;
        }
        if (!traversed.add(value.getId())) {
            // We've already traversed this value
            return value;
        }
        if (systemDefinition.test(value)) {
            // This value is in the system, traverse it
            traverseBoundary(value, systemDefinition, sliced, traversed);
            return value;
        }

        // This value is not in the system and has not been traversed, create a mapped value that stands for an exit from the system
        try {
            mappedValue = value.getClass().getConstructor().newInstance();
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException(
                                            String.format("Unable to get no argument constructor on ruleform: %s",
                                                          value.getClass()), e);
        } catch (InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalStateException(
                                            String.format("Unable to instantiate copy of ruleform: %s",
                                                          value.getClass()), e);
        }

        // Mapped value has the same id, but null fields for everything else
        mappedValue.setId(value.getId());
        sliced.put(value, mappedValue);
        traverseBoundary(value, systemDefinition, sliced, traversed);
        return mappedValue;
    }

    @SuppressWarnings("unchecked")
    protected static <T extends Ruleform> T map(EntityManager em, T ruleform,
                                                Map<Ruleform, Ruleform> mapped) {
        if (mapped.containsKey(ruleform)) {
            return (T) mapped.get(ruleform);
        }

        Ruleform reference = find(em, ruleform);
        if (reference != null) {
            mapped.put(ruleform, reference);
        } else {
            mapped.put(ruleform, ruleform);
            traverse(em, ruleform, mapped);
            em.persist(ruleform);
        }

        return (T) mapped.get(ruleform);
    }

    protected static void traverse(EntityManager em, Ruleform ruleform,
                                   Map<Ruleform, Ruleform> mapped) {
        if (ruleform instanceof WorkspaceAuthorization) {
            WorkspaceAuthorization auth = (WorkspaceAuthorization) ruleform;
            auth.setDefiningProduct(map(em, auth.getDefiningProduct(), mapped));
            auth.setRuleform(map(em, auth.getRuleform(), mapped));
            return;
        }
        for (Field field : getInheritedFields(ruleform.getClass())) {
            if (field.getAnnotation(JoinColumn.class) == null) {
                continue;
            }
            try {
                field.setAccessible(true);
                Ruleform value = (Ruleform) field.get(ruleform);
                if (value != null && !ruleform.equals(value)) {
                    Ruleform mappedValue = map(em, value, mapped);
                    if (mappedValue == null) {
                        throw new IllegalStateException(
                                                        String.format("%s mapped to null",
                                                                      value));
                    }
                    if (mappedValue != value) {
                        field.set(ruleform, mappedValue);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(
                                                String.format("IllegalAccess access foreign key field: %s",
                                                              field), e);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException(
                                                String.format("Illegal mapped value for field: %s",
                                                              field), e);
            }
        }
    }

    protected static void traverseBoundary(Ruleform ruleform,
                                           Predicate<Ruleform> systemDefinition,
                                           Map<Ruleform, Ruleform> sliced,
                                           Set<UUID> traversed) {
        for (Field field : getInheritedFields(ruleform.getClass())) {
            if (field.getAnnotation(JoinColumn.class) == null) {
                continue;
            }
            try {
                field.setAccessible(true);
                Ruleform value = (Ruleform) field.get(ruleform);
                if (value != null && !ruleform.equals(value)) {
                    field.set(ruleform,
                              map(value, systemDefinition, sliced, traversed));
                }
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(
                                                String.format("IllegalAccess access foreign key field: %s",
                                                              field), e);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException(
                                                String.format("Illegal mapped value for field: %s",
                                                              field), e);
            }
        }
    }

    private Util() {

    }
}
