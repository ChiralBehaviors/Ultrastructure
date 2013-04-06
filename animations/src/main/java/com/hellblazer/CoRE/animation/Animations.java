/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.hellblazer.CoRE.animation;

import java.util.ArrayList;
import java.util.List;

import com.hellblazer.CoRE.meta.security.AuthenticatedPrincipal;
import com.hellblazer.CoRE.network.Aspect;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

/**
 * Abstract superclass of animations. Provides a mechanism to set up the
 * animation context from within the DB or manually, outside of the DB
 * 
 * @author hhildebrand
 * 
 */
abstract public class Animations {

    /**
     * Used to initialize the context from within the DB
     * 
     * @author hhildebrand
     * 
     */
    private static class InDatabaseContext {
        private static final AnimationContext CONTEXT;
        static {
            CONTEXT = new AnimationContext();
        }

        private static AnimationContext get() {
            return CONTEXT;
        }
    }

    /**
     * Establish the class loading context
     */
    public static void establishContext() {
        Thread.currentThread().setContextClassLoader(Animations.class.getClassLoader());
    }

    /**
     * Sets up the authenticated principal to use in the animation's in database
     * context. Will throw all sorts of errors if used outside of the context of
     * a Java stored proceedure.
     * 
     * @param resource
     *            - the id of the resource corresponding to the principal
     * @param activeRoleRelationships
     *            - the ids of the relationships of the active role aspects. If
     *            null, then no active role aspects are set.
     * @param activeRoleResources
     *            - the ids of the resources of the active role aspects. If
     *            null, then no active role aspects are set.
     * 
     * @throws IllegalArgumentException
     *             if the activeRoleRelationships.length !=
     *             activeRoleResources.length
     */
    public static void setPrincipal(Long resource,
                                    Long[] activeRoleRelationships,
                                    Long[] activeRoleResources) {
        AnimationContext context = InDatabaseContext.get();
        AuthenticatedPrincipal principal;
        if (activeRoleRelationships == null || activeRoleResources == null) {
            principal = new AuthenticatedPrincipal(
                                                   context.getModel().find(resource,
                                                                           Resource.class));
        } else {
            if (activeRoleRelationships.length != activeRoleResources.length) {
                throw new IllegalArgumentException(
                                                   "active role relationships and resources must be of the same length");
            }
            List<Aspect<Resource>> aspects = new ArrayList<Aspect<Resource>>();
            for (int i = 0; i < activeRoleRelationships.length; i++) {
                aspects.add(new Aspect<Resource>(
                                                 context.getModel().find(activeRoleRelationships[i],
                                                                         Relationship.class),
                                                 context.getModel().find(activeRoleResources[i],
                                                                         Resource.class)));
            }
            principal = new AuthenticatedPrincipal(
                                                   context.getModel().find(resource,
                                                                           Resource.class),
                                                   aspects);
        }

        context.setPrincipal(principal);
    }

    /**
     * The context in which the animation runs
     */
    protected final AnimationContext context;

    public Animations(AnimationContext context) {
        this.context = context;
    }

    /**
     * Should only be called from within the DB
     */
    protected Animations() {
        this(InDatabaseContext.get());
    }

}
