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

package com.hellblazer.CoRE.meta.models;

import javax.persistence.EntityManager;

import com.hellblazer.CoRE.Kernel;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.entity.Entity;
import com.hellblazer.CoRE.entity.EntityAttribute;
import com.hellblazer.CoRE.entity.EntityAttributeAuthorization;
import com.hellblazer.CoRE.entity.EntityNetwork;
import com.hellblazer.CoRE.meta.EntityModel;
import com.hellblazer.CoRE.network.Aspect;

/**
 * @author hhildebrand
 * 
 */
public class EntityModelImpl
        extends
        AbstractNetworkedModel<Entity, EntityAttributeAuthorization, EntityAttribute>
        implements EntityModel {

    /**
     * @param em
     */
    public EntityModelImpl(EntityManager em, Kernel kernel) {
        super(em, kernel);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.NetworkedModel#authorize(com.hellblazer.CoRE.meta.Aspect, com.hellblazer.CoRE.attribute.Attribute[])
     */
    @Override
    public void authorize(Aspect<Entity> aspect, Attribute... attributes) {
        for (Attribute attribute : attributes) {
            EntityAttributeAuthorization authorization = new EntityAttributeAuthorization(
                                                                                          aspect.getClassification(),
                                                                                          aspect.getClassifier(),
                                                                                          attribute,
                                                                                          kernel.getCoreModel());
            em.persist(authorization);
        }
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.NetworkedModel#create(com.hellblazer.CoRE.network.Networked)
     */
    @Override
    public Entity create(Entity prototype) {
        Entity copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(kernel.getCoreModel());
        for (EntityNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(), copy,
                                     kernel.getCoreModel(),
                                     kernel.getInverseSoftware(), em);
        }
        for (EntityAttribute attribute : prototype.getAttributes()) {
            EntityAttribute clone = (EntityAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setEntity(copy);
            clone.setUpdatedBy(kernel.getCoreModel());
        }
        return copy;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.NetworkedModel#create(com.hellblazer.CoRE.meta.Aspect<RuleForm>[])
     */
    @Override
    final public Entity create(String name, String description,
                               Aspect<Entity> aspect, Aspect<Entity>... aspects) {
        Entity resource = new Entity(name, description, kernel.getCoreModel());
        em.persist(resource);
        initialize(resource, aspect);
        if (aspects != null) {
            for (Aspect<Entity> a : aspects) {
                initialize(resource, a);
            }
        }
        return resource;
    }

    /**
     * @param resource
     * @param aspect
     */
    protected void initialize(Entity resource, Aspect<Entity> aspect) {
        resource.link(aspect.getClassification(), aspect.getClassifier(),
                      kernel.getCoreModel(), kernel.getInverseSoftware(), em);
        for (EntityAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
            EntityAttribute attribute = new EntityAttribute(
                                                            authorization.getAuthorizedAttribute(),
                                                            kernel.getCoreModel());
            attribute.setEntity(resource);
            defaultValue(attribute);
            em.persist(attribute);
        }
    }
}
