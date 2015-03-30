/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.meta.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.TypedQuery;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyAttribute;
import com.chiralbehaviors.CoRE.agency.AgencyAttributeAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.meta.AgencyModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Facet;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * @author hhildebrand
 *
 */
public class AgencyModelImpl
        extends
        AbstractNetworkedModel<Agency, AgencyNetwork, AgencyAttributeAuthorization, AgencyAttribute>
        implements AgencyModel {

    /**
     * @param em
     */
    public AgencyModelImpl(Model model) {
        super(model);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#authorize(com.chiralbehaviors.CoRE
     * .meta.Aspect, com.chiralbehaviors.CoRE.attribute.Attribute[])
     */
    @Override
    public void authorize(Aspect<Agency> aspect, Attribute... attributes) {
        for (Attribute attribute : attributes) {
            AgencyAttributeAuthorization authorization = new AgencyAttributeAuthorization(
                                                                                          aspect.getClassification(),
                                                                                          aspect.getClassifier(),
                                                                                          attribute,
                                                                                          kernel.getCoreModel());
            em.persist(authorization);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(com.chiralbehaviors.CoRE.network
     * .Networked)
     */
    @Override
    public Agency create(Agency prototype) {
        Agency copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(kernel.getCoreModel());
        for (AgencyNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(), copy,
                                     kernel.getCoreModel(),
                                     kernel.getInverseSoftware(), em);
        }
        for (AttributeValue<Agency> attribute : prototype.getAttributes()) {
            AgencyAttribute clone = (AgencyAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setAgency(copy);
            clone.setUpdatedBy(kernel.getCoreModel());
        }
        return copy;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#create(java.lang.String, java.lang.String, com.chiralbehaviors.CoRE.network.Aspect)
     */
    @Override
    public Facet<Agency, AgencyAttribute> create(String name,
                                                 String description,
                                                 Aspect<Agency> aspect) {
        Agency agency = new Agency(name, description, kernel.getCoreModel());
        em.persist(agency);
        return new Facet<Agency, AgencyAttribute>(aspect, agency,
                                                  initialize(agency, aspect)) {
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
    public final Agency create(String name, String description,
                               Aspect<Agency> aspect, Aspect<Agency>... aspects) {
        Agency agency = new Agency(name, description, kernel.getCoreModel());
        em.persist(agency);
        initialize(agency, aspect);
        if (aspects != null) {
            for (Aspect<Agency> a : aspects) {
                initialize(agency, a);
            }
        }
        return agency;
    }

    @Override
    public List<AgencyNetwork> getInterconnections(Collection<Agency> parents,
                                                   Collection<Relationship> relationships,
                                                   Collection<Agency> children) {
        if (parents == null || parents.size() == 0 || relationships == null
            || relationships.size() == 0 || children == null
            || children.size() == 0) {
            return null;
        }
        TypedQuery<AgencyNetwork> query = em.createNamedQuery(AgencyNetwork.GET_NETWORKS,
                                                              AgencyNetwork.class);
        query.setParameter("parents", parents);
        query.setParameter("relationships", relationships);
        query.setParameter("children", children);
        return query.getResultList();
    }

    /**
     * @param agency
     * @param aspect
     */
    protected List<AgencyAttribute> initialize(Agency agency,
                                               Aspect<Agency> aspect) {
        List<AgencyAttribute> attributes = new ArrayList<>();
        agency.link(aspect.getClassification(), aspect.getClassifier(),
                    kernel.getCoreModel(), kernel.getInverseSoftware(), em);
        for (AgencyAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
            AgencyAttribute attribute = new AgencyAttribute(
                                                            authorization.getAuthorizedAttribute(),
                                                            kernel.getCoreModel());
            attributes.add(attribute);
            attribute.setAgency(agency);
            defaultValue(attribute);
            em.persist(attribute);
        }
        return attributes;
    }
}
