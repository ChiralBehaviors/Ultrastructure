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

import javax.persistence.TypedQuery;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.RelationshipModel;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.relationship.RelationshipAttribute;
import com.chiralbehaviors.CoRE.relationship.RelationshipAttributeAuthorization;
import com.chiralbehaviors.CoRE.relationship.RelationshipNetwork;
import com.chiralbehaviors.CoRE.relationship.RelationshipNetworkAuthorization;

/**
 * @author hhildebrand
 *
 */
public class RelationshipModelImpl
        extends
        AbstractNetworkedModel<Relationship, RelationshipNetwork, RelationshipAttributeAuthorization, RelationshipAttribute>
        implements RelationshipModel {

    /**
     * @param em
     */
    public RelationshipModelImpl(Model model) {
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
    public void authorize(Aspect<Relationship> aspect, Attribute... attributes) {
        RelationshipNetworkAuthorization auth = new RelationshipNetworkAuthorization(
                                                                                     model.getCurrentPrincipal().getPrincipal());
        auth.setClassifier(aspect.getClassifier());
        auth.setClassification(aspect.getClassification());
        em.persist(auth);
        for (Attribute attribute : attributes) {
            RelationshipAttributeAuthorization authorization = new RelationshipAttributeAuthorization(
                                                                                                      attribute,
                                                                                                      kernel.getCoreModel());
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
    public Relationship create(Relationship prototype) {
        Relationship copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(kernel.getCoreModel());
        for (RelationshipNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(), copy,
                                     kernel.getCoreModel(),
                                     kernel.getInverseSoftware(), em);
        }
        for (RelationshipAttribute attribute : prototype.getAttributes()) {
            RelationshipAttribute clone = (RelationshipAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setRelationship(copy);
            clone.setUpdatedBy(kernel.getCoreModel());
        }
        return copy;
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
    public final Relationship create(String name, String description,
                                     Aspect<Relationship> aspect,
                                     Agency updatedBy,
                                     Aspect<Relationship>... aspects) {
        Relationship relationship = new Relationship(name, description,
                                                     kernel.getCoreModel());
        em.persist(relationship);
        initialize(relationship, aspect, updatedBy);
        if (aspects != null) {
            for (Aspect<Relationship> a : aspects) {
                initialize(relationship, a, updatedBy);
            }
        }
        return relationship;
    }

    @Override
    public final Relationship create(String rel1Name, String rel1Description,
                                     String rel2Name, String rel2Description) {
        Relationship relationship = new Relationship(rel1Name, rel1Description,
                                                     kernel.getCoreModel());

        Relationship relationship2 = new Relationship(rel2Name,
                                                      rel2Description,
                                                      kernel.getCoreModel());

        relationship.setInverse(relationship2);
        relationship2.setInverse(relationship);
        em.persist(relationship);
        em.persist(relationship2);

        return relationship;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getInterconnections(java.util.List, java.util.List, java.util.List)
     */
    @Override
    public List<RelationshipNetwork> getInterconnections(Collection<Relationship> parents,
                                                         Collection<Relationship> relationships,
                                                         Collection<Relationship> children) {
        if (parents == null || parents.size() == 0 || relationships == null
            || relationships.size() == 0 || children == null
            || children.size() == 0) {
            return null;
        }
        TypedQuery<RelationshipNetwork> query = em.createNamedQuery(RelationshipNetwork.GET_NETWORKS,
                                                                    RelationshipNetwork.class);
        query.setParameter("parents", parents);
        query.setParameter("relationships", relationships);
        query.setParameter("children", children);
        return query.getResultList();
    }

    @Override
    public RelationshipAttribute create(Relationship ruleform,
                                        Attribute attribute, Agency updatedBy) {
        return new RelationshipAttribute(ruleform, attribute, updatedBy);
    }
}
