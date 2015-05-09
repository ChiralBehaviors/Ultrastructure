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

import java.util.Collection;
import java.util.List;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.meta.IntervalModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.time.Interval;
import com.chiralbehaviors.CoRE.time.IntervalAttribute;
import com.chiralbehaviors.CoRE.time.IntervalAttributeAuthorization;
import com.chiralbehaviors.CoRE.time.IntervalNetwork;
import com.chiralbehaviors.CoRE.time.IntervalNetworkAuthorization;

/**
 * @author hhildebrand
 *
 */
public class IntervalModelImpl
        extends
        AbstractNetworkedModel<Interval, IntervalNetwork, IntervalAttributeAuthorization, IntervalAttribute>
        implements IntervalModel {

    /**
     * @param em
     */
    public IntervalModelImpl(Model model) {
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
    public void authorize(Aspect<Interval> aspect, Attribute... attributes) {
        IntervalNetworkAuthorization auth = new IntervalNetworkAuthorization(
                                                                             model.getCurrentPrincipal().getPrincipal());
        auth.setClassifier(aspect.getClassifier());
        auth.setClassification(aspect.getClassification());
        em.persist(auth);
        for (Attribute attribute : attributes) {
            IntervalAttributeAuthorization authorization = new IntervalAttributeAuthorization(
                                                                                              attribute,
                                                                                              model.getCurrentPrincipal().getPrincipal());
            authorization.setNetworkAuthorization(auth);
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
    public Interval create(Interval prototype) {
        Interval copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(model.getCurrentPrincipal().getPrincipal());
        for (IntervalNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(),
                                     copy,
                                     model.getCurrentPrincipal().getPrincipal(),
                                     model.getCurrentPrincipal().getPrincipal(),
                                     em);
        }
        for (IntervalAttribute attribute : prototype.getAttributes()) {
            IntervalAttribute clone = (IntervalAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setInterval(copy);
            clone.setUpdatedBy(model.getCurrentPrincipal().getPrincipal());
        }
        return copy;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.models.AbstractNetworkedModel#create(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.attribute.ClassifiedAttributeAuthorization)
     */
    @Override
    public IntervalAttribute create(Interval ruleform, Attribute attribute,
                                    Agency updatedBy) {
        return new IntervalAttribute(ruleform, attribute, updatedBy);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#create(java.lang.String, java.lang.String, com.chiralbehaviors.CoRE.network.Aspect, com.chiralbehaviors.CoRE.agency.Agency, com.chiralbehaviors.CoRE.network.Aspect[])
     */
    @Override
    public Interval create(String name,
                           String description,
                           Aspect<Interval> aspect,
                           Agency updatedBy,
                           @SuppressWarnings("unchecked") Aspect<Interval>... aspects) {
        Interval agency = new Interval(
                                       name,
                                       description,
                                       model.getCurrentPrincipal().getPrincipal());
        em.persist(agency);
        initialize(agency, aspect, updatedBy);
        if (aspects != null) {
            for (Aspect<Interval> a : aspects) {
                initialize(agency, a, updatedBy);
            }
        }
        return agency;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getInterconnections(java.util.List, java.util.List, java.util.List)
     */
    @Override
    public List<IntervalNetwork> getInterconnections(Collection<Interval> parents,
                                                     Collection<Relationship> relationships,
                                                     Collection<Interval> children) {
        throw new UnsupportedOperationException();
    }
}
