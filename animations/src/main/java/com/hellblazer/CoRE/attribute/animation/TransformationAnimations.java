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

package com.hellblazer.CoRE.attribute.animation;

import java.util.List;

import javax.persistence.TypedQuery;

import com.hellblazer.CoRE.Kernel;
import com.hellblazer.CoRE.animation.AnimationContext;
import com.hellblazer.CoRE.animation.Animations;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.Transformation;
import com.hellblazer.CoRE.attribute.TransformationMetarule;
import com.hellblazer.CoRE.entity.Entity;
import com.hellblazer.CoRE.entity.EntityAttribute;
import com.hellblazer.CoRE.entity.EntityNetwork;
import com.hellblazer.CoRE.resource.Resource;
import com.hellblazer.CoRE.resource.ResourceNetwork;

/**
 * @author hhildebrand
 * 
 */
public class TransformationAnimations extends Animations {

    /**
     * Used to initialize a singleton for use within the DB
     * 
     * @author hhildebrand
     * 
     */
    @SuppressWarnings("unused")
    private static class InDatabase {
        private static final TransformationAnimations SINGLETON;
        static {
            SINGLETON = new TransformationAnimations();
        }

        public static TransformationAnimations get() {
            establishContext();
            return SINGLETON;
        }
    }

    /**
     * @param context
     */
    public TransformationAnimations(AnimationContext context) {
        super(context);
    }

    /**
     * Only called within the DB
     */
    private TransformationAnimations() {
        super();
    }

    public Attribute transform(Entity service, Resource resource, Entity entity) {
        Kernel kernel = context.getModel().getKernel();

        Attribute txfmd = null;
        for (TransformationMetarule transfromationMetarule : getTransformationMetarules(service)) {
            Resource mappedResource;
            if (kernel.getSameResource().equals(transfromationMetarule.getRelationshipMap())) {
                mappedResource = kernel.getSameResource();
            } else {
                mappedResource = getMappedResource(transfromationMetarule,
                                                   resource);
            }
            Entity mappedEntity;
            if (kernel.getSameEntity().equals(transfromationMetarule.getEntityMap())) {
                mappedEntity = kernel.getSameEntity();
            } else {
                mappedEntity = getMappedEntity(transfromationMetarule, entity);
            }
            for (Transformation transformation : getTransformations(service,
                                                                    mappedResource,
                                                                    mappedEntity)) {
                txfmd = null;
                Resource txfmResource;
                if (kernel.getOriginalResource().equals(transformation.getResourceKey())) {
                    txfmResource = resource;
                } else {
                    txfmResource = transformation.getResourceKey();
                }
                Entity txfmEntity;
                if (kernel.getOriginalEntity().equals(transformation.getEntityKey())) {
                    txfmEntity = entity;
                } else {
                    txfmEntity = transformation.getEntityKey();
                }
                Entity foundEntity = findEntity(transformation, txfmResource,
                                                txfmEntity);

                txfmd = findAttribute(transformation, foundEntity);
                if (txfmd != null) {
                    break;
                }
            }
            if (txfmd != null && transfromationMetarule.getStopOnMatch()) {
                break;
            }
        }
        return txfmd;
    }

    /**
     * @param transformation
     * @param entity
     * @return
     */
    private Attribute findAttribute(Transformation transformation, Entity entity) {
        TypedQuery<Attribute> attrQuery = context.getEm().createNamedQuery(EntityAttribute.FIND_ATTRIBUTE_VALUE_FROM_RESOURCE,
                                                                           Attribute.class);
        attrQuery.setParameter("resource",
                               transformation.getEntityAttributeResource());
        attrQuery.setParameter("entity", entity);
        attrQuery.setParameter("attribute", transformation.getAttribute());
        return attrQuery.getSingleResult();
    }

    /**
     * @param transformation
     * @param resource
     * @param entity
     * @return
     */
    private Entity findEntity(Transformation transformation, Resource resource,
                              Entity entity) {
        TypedQuery<Entity> entityNetworkQuery = context.getEm().createQuery(EntityNetwork.GET_CHILD,
                                                                            Entity.class);
        entityNetworkQuery.setParameter("parent", entity);
        entityNetworkQuery.setParameter("relationship",
                                        transformation.getRelationshipKey());
        // entityNetworkQuery.setParameter("resource", resource);
        return entityNetworkQuery.getSingleResult();
    }

    /**
     * @param transfromationMetarule
     * @param entity
     * @return
     */
    private Entity getMappedEntity(TransformationMetarule transfromationMetarule,
                                   Entity entity) {
        TypedQuery<Entity> entityNetworkQuery = context.getEm().createQuery(EntityNetwork.GET_CHILD,
                                                                            Entity.class);
        entityNetworkQuery.setParameter("parent", entity);
        entityNetworkQuery.setParameter("relationship",
                                        transfromationMetarule.getRelationshipMap());
        // entityNetworkQuery.setParameter("resource", transfromationMetarule.getEntityNetworkResource());
        return entityNetworkQuery.getSingleResult();
    }

    /**
     * @param transfromationMetarule
     * @param resource
     * @return
     */
    private Resource getMappedResource(TransformationMetarule transfromationMetarule,
                                       Resource resource) {
        TypedQuery<Resource> resourceNetworkQuery = context.getEm().createQuery(ResourceNetwork.GET_CHILD,
                                                                                Resource.class);
        resourceNetworkQuery.setParameter("parent", resource);
        resourceNetworkQuery.setParameter("relationship",
                                          transfromationMetarule.getRelationshipMap());
        return resourceNetworkQuery.getSingleResult();
    }

    /**
     * @param service
     * @return
     */
    private List<TransformationMetarule> getTransformationMetarules(Entity service) {
        TypedQuery<TransformationMetarule> txfmMetaruleQuery = context.getEm().createQuery(TransformationMetarule.GET_BY_EVENT,
                                                                                           TransformationMetarule.class);
        txfmMetaruleQuery.setParameter("event", service);
        return txfmMetaruleQuery.getResultList();
    }

    /**
     * @param service
     * @param mappedResource
     * @param mappedEntity
     * @return
     */
    private List<Transformation> getTransformations(Entity service,
                                                    Resource mappedResource,
                                                    Entity mappedEntity) {
        TypedQuery<Transformation> txfmQuery = context.getEm().createQuery(Transformation.GET,
                                                                           Transformation.class);
        txfmQuery.setParameter("event", service);
        txfmQuery.setParameter("entity", mappedEntity);
        txfmQuery.setParameter("resource", mappedResource);

        return txfmQuery.getResultList();
    }
}
