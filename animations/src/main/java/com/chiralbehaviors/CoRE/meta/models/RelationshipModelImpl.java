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

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.RelationshipModel;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductRelationship;
import com.chiralbehaviors.CoRE.product.ProductRelationship_;
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
        auth.setClassification(aspect.getClassifier());
        auth.setClassifier(aspect.getClassification());
        em.persist(auth);
        for (Attribute attribute : attributes) {
            RelationshipAttributeAuthorization authorization = new RelationshipAttributeAuthorization(
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
    public Relationship create(Relationship prototype) {
        Relationship copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(model.getCurrentPrincipal().getPrincipal());
        for (RelationshipNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(),
                                     copy,
                                     model.getCurrentPrincipal().getPrincipal(),
                                     model.getCurrentPrincipal().getPrincipal(),
                                     em);
        }
        for (RelationshipAttribute attribute : prototype.getAttributes()) {
            RelationshipAttribute clone = (RelationshipAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setRelationship(copy);
            clone.setUpdatedBy(model.getCurrentPrincipal().getPrincipal());
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
        Relationship relationship = new Relationship(
                                                     name,
                                                     description,
                                                     model.getCurrentPrincipal().getPrincipal());
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
        Relationship relationship = new Relationship(
                                                     rel1Name,
                                                     rel1Description,
                                                     model.getCurrentPrincipal().getPrincipal());

        Relationship relationship2 = new Relationship(
                                                      rel2Name,
                                                      rel2Description,
                                                      model.getCurrentPrincipal().getPrincipal());

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

    @Override
    public void authorize(Relationship ruleform, Relationship relationship,
                          Product authorized) {
        assert ruleform != null : "ruleform is null";
        assert relationship != null : "relationshp is null";
        assert authorized != null : "authorized is null";
        ProductRelationship a = new ProductRelationship(
                                                        model.getCurrentPrincipal().getPrincipal());
        a.setChild(ruleform);
        a.setRelationship(relationship);
        a.setProduct(authorized);
        em.persist(a);
        ProductRelationship b = new ProductRelationship(
                                                        model.getCurrentPrincipal().getPrincipal());
        b.setChild(ruleform);
        b.setRelationship(relationship.getInverse());
        b.setProduct(authorized);
        em.persist(b);
    }

    @Override
    public void deauthorize(Relationship ruleform, Relationship relationship,
                            Product authorized) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProductRelationship> query = cb.createQuery(ProductRelationship.class);
        Root<ProductRelationship> plRoot = query.from(ProductRelationship.class);
        ParameterExpression<Relationship> relationshipParam = cb.parameter(Relationship.class);
        query.select(plRoot).where(cb.and(cb.equal(plRoot.get(ProductRelationship_.child),
                                                   ruleform),
                                          cb.equal(plRoot.get(ProductRelationship_.relationship),
                                                   relationshipParam),
                                          cb.equal(plRoot.get(ProductRelationship_.product),
                                                   authorized)));
        TypedQuery<ProductRelationship> q = em.createQuery(query);
        q.setParameter(relationshipParam, relationship);
        try {
            em.remove(q.getSingleResult());
        } catch (NoResultException e) {
            return;
        }
        q.setParameter(relationshipParam, relationship.getInverse());
        try {
            em.remove(q.getSingleResult());
        } catch (NoResultException e) {
        }
    }

    @Override
    public List<Product> getAuthorizedProducts(Relationship ruleform,
                                               Relationship relationship) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<ProductRelationship> plRoot = query.from(ProductRelationship.class);
        Path<Product> path;
        try {
            path = plRoot.get(ProductRelationship_.product);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        query.select(path).where(cb.and(cb.equal(plRoot.get(ProductRelationship_.child),
                                                 ruleform),
                                        cb.equal(plRoot.get(ProductRelationship_.relationship),
                                                 relationship)));
        TypedQuery<Product> q = em.createQuery(query);
        return q.getResultList();
    }
}
