/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chiralbehaviors.CoRE.meta.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.attribute.unit.UnitAttribute;
import com.chiralbehaviors.CoRE.attribute.unit.UnitAttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.unit.UnitNetwork;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.KernelUtil;
import com.chiralbehaviors.CoRE.meta.UnitModel;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Facet;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * @author hhildebrand
 *
 */
public class UnitModelImpl
        extends
        AbstractNetworkedModel<Unit, UnitNetwork, UnitAttributeAuthorization, UnitAttribute>
        implements UnitModel {

    /**
     * @param em
     */
    public UnitModelImpl(EntityManager em) {
        super(em, KernelUtil.getKernel());
    }

    /**
     * @param em
     */
    public UnitModelImpl(EntityManager em, Kernel kernel) {
        super(em, kernel);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#authorize(com.chiralbehaviors.CoRE
     * .meta.Aspect, com.chiralbehaviors.CoRE.attribute.Attribute[])
     */
    @Override
    public void authorize(Aspect<Unit> aspect, Attribute... attributes) {
        for (Attribute attribute : attributes) {
            UnitAttributeAuthorization authorization = new UnitAttributeAuthorization(
                                                                                      aspect.getClassification(),
                                                                                      aspect.getClassifier(),
                                                                                      attribute,
                                                                                      kernel.getCoreModel());
            em.persist(authorization);
        }
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#create(java.lang.String, java.lang.String, com.chiralbehaviors.CoRE.network.Aspect)
     */
    @Override
    public Facet<Unit, UnitAttribute> create(String name, String description,
                                             Aspect<Unit> aspect) {
        Unit unit = new Unit(name, description, kernel.getCoreModel());
        em.persist(unit);
        return new Facet<Unit, UnitAttribute>(aspect, unit, initialize(unit,
                                                                       aspect)) {
        };
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(com.chiralbehaviors.CoRE.meta
     * .Aspect<RuleForm>[])
     */
    @SafeVarargs
    @Override
    public final Unit create(String name, String description,
                             Aspect<Unit> aspect, Aspect<Unit>... aspects) {
        Unit agency = new Unit(name, description, kernel.getCoreModel());
        em.persist(agency);
        initialize(agency, aspect);
        if (aspects != null) {
            for (Aspect<Unit> a : aspects) {
                initialize(agency, a);
            }
        }
        return agency;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(com.chiralbehaviors.CoRE.network
     * .Networked)
     */
    @Override
    public Unit create(Unit prototype) {
        Unit copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(kernel.getCoreModel());
        for (UnitNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(), copy,
                                     kernel.getCoreModel(),
                                     kernel.getInverseSoftware(), em);
        }
        for (UnitAttribute attribute : prototype.getAttributes()) {
            UnitAttribute clone = (UnitAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setUnit(copy);
            clone.setUpdatedBy(kernel.getCoreModel());
        }
        return copy;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getInterconnections(java.util.List, java.util.List, java.util.List)
     */
    @Override
    public List<UnitNetwork> getInterconnections(Collection<Unit> parents,
                                                 Collection<Relationship> relationships,
                                                 Collection<Unit> children) {
        if (parents == null || parents.size() == 0 || relationships == null
            || relationships.size() == 0 || children == null
            || children.size() == 0) {
            return null;
        }
        TypedQuery<UnitNetwork> query = em.createNamedQuery(UnitNetwork.GET_NETWORKS,
                                                            UnitNetwork.class);
        query.setParameter("parents", parents);
        query.setParameter("relationships", relationships);
        query.setParameter("children", children);
        return query.getResultList();
    }

    /**
     * @param agency
     * @param aspect
     */
    protected List<UnitAttribute> initialize(Unit agency, Aspect<Unit> aspect) {
        agency.link(aspect.getClassification(), aspect.getClassifier(),
                    kernel.getCoreModel(), kernel.getInverseSoftware(), em);
        List<UnitAttribute> attributes = new ArrayList<>();
        for (UnitAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
            UnitAttribute attribute = new UnitAttribute(
                                                        authorization.getAuthorizedAttribute(),
                                                        kernel.getCoreModel());
            attributes.add(attribute);
            attribute.setUnit(agency);
            defaultValue(attribute);
            em.persist(attribute);
        }
        return attributes;
    }
}
