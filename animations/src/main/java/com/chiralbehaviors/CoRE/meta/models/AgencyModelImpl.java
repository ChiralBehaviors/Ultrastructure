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

package com.chiralbehaviors.CoRE.meta.models;

import java.util.Collection;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyAttribute;
import com.chiralbehaviors.CoRE.agency.AgencyAttributeAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyLocation;
import com.chiralbehaviors.CoRE.agency.AgencyLocationAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyLocationAuthorization_;
import com.chiralbehaviors.CoRE.agency.AgencyLocation_;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.agency.AgencyNetworkAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyProduct;
import com.chiralbehaviors.CoRE.agency.AgencyProductAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyProductAuthorization_;
import com.chiralbehaviors.CoRE.agency.AgencyProduct_;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.AgencyModel;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.security.AgencyAgencyGrouping;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;

/**
 * @author hhildebrand
 *
 */
public class AgencyModelImpl extends
        AbstractNetworkedModel<Agency, AgencyNetwork, AgencyAttributeAuthorization, AgencyAttribute>
        implements AgencyModel {

    /**
     * @param em
     */
    public AgencyModelImpl(Model model) {
        super(model);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#authorize(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship, com.chiralbehaviors.CoRE.agency.Agency)
     */
    @Override
    public void authorize(Agency ruleform, Relationship relationship,
                          Agency authorized) {
        throw new UnsupportedOperationException("Agency -> Agency authorizations are modeled with Agency Networks");
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#authorize(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship, com.chiralbehaviors.CoRE.location.Location)
     */
    @Override
    public void authorize(Agency ruleform, Relationship relationship,
                          Location authorized) {
        AgencyLocation a = new AgencyLocation(model.getCurrentPrincipal()
                                                   .getPrincipal());
        a.setAgency(ruleform);
        a.setRelationship(relationship);
        a.setLocation(authorized);
        em.persist(a);
        AgencyLocation b = new AgencyLocation(model.getCurrentPrincipal()
                                                   .getPrincipal());
        b.setAgency(ruleform);
        b.setRelationship(relationship.getInverse());
        b.setLocation(authorized);
        em.persist(b);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#authorize(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship, com.chiralbehaviors.CoRE.product.Product)
     */
    @Override
    public void authorize(Agency ruleform, Relationship relationship,
                          Product authorized) {
        AgencyProduct a = new AgencyProduct(model.getCurrentPrincipal()
                                                 .getPrincipal());
        a.setAgency(ruleform);
        a.setRelationship(relationship);
        a.setProduct(authorized);
        em.persist(a);
        AgencyProduct b = new AgencyProduct(model.getCurrentPrincipal()
                                                 .getPrincipal());
        b.setAgency(ruleform);
        b.setRelationship(relationship.getInverse());
        b.setProduct(authorized);
        em.persist(b);
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
        AgencyNetworkAuthorization auth = new AgencyNetworkAuthorization(model.getCurrentPrincipal()
                                                                              .getPrincipal());
        auth.setClassifier(aspect.getClassifier());
        auth.setClassification(aspect.getClassification());
        em.persist(auth);
        for (Attribute attribute : attributes) {
            AgencyAttributeAuthorization authorization = new AgencyAttributeAuthorization(attribute,
                                                                                          model.getCurrentPrincipal()
                                                                                               .getPrincipal());
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
    public Agency create(Agency prototype) {
        Agency copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(model.getCurrentPrincipal()
                               .getPrincipal());
        for (AgencyNetwork network : prototype.getNetworkByParent()) {
            network.getParent()
                   .link(network.getRelationship(), copy,
                         model.getCurrentPrincipal()
                              .getPrincipal(),
                         model.getCurrentPrincipal()
                              .getPrincipal(),
                         em);
        }
        for (AttributeValue<Agency> attribute : prototype.getAttributes()) {
            AgencyAttribute clone = (AgencyAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setAgency(copy);
            clone.setUpdatedBy(model.getCurrentPrincipal()
                                    .getPrincipal());
        }
        return copy;
    }

    @Override
    public AgencyAttribute create(Agency ruleform, Attribute attribute,
                                  Agency updateBy) {
        return new AgencyAttribute(ruleform, attribute, updateBy);
    }

    @Override
    public final Agency create(String name, String description) {
        Agency agency = new Agency(name, description,
                                   model.getCurrentPrincipal()
                                        .getPrincipal());
        em.persist(agency);
        return agency;
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
                               Aspect<Agency> aspect, Agency updatedBy,
                               Aspect<Agency>... aspects) {
        Agency agency = new Agency(name, description,
                                   model.getCurrentPrincipal()
                                        .getPrincipal());
        em.persist(agency);
        initialize(agency, aspect);
        if (aspects != null) {
            for (Aspect<Agency> a : aspects) {
                initialize(agency, a);
            }
        }
        return agency;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#deauthorize(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship, com.chiralbehaviors.CoRE.agency.Agency)
     */
    @Override
    public void deauthorize(Agency ruleform, Relationship relationship,
                            Agency authorized) {
        throw new UnsupportedOperationException("Agency -> Agency authorizations are modeled with Agency Networks");
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#deauthorize(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship, com.chiralbehaviors.CoRE.location.Location)
     */
    @Override
    public void deauthorize(Agency ruleform, Relationship relationship,
                            Location authorized) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AgencyLocation> query = cb.createQuery(AgencyLocation.class);
        Root<AgencyLocation> plRoot = query.from(AgencyLocation.class);
        ParameterExpression<Relationship> relationshipParam = cb.parameter(Relationship.class);
        query.select(plRoot)
             .where(cb.and(cb.equal(plRoot.get(AgencyLocation_.agency),
                                    ruleform),
                           cb.equal(plRoot.get(AgencyLocation_.relationship),
                                    relationshipParam),
                           cb.equal(plRoot.get(AgencyLocation_.location),
                                    authorized)));
        TypedQuery<AgencyLocation> q = em.createQuery(query);
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

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#deauthorize(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship, com.chiralbehaviors.CoRE.product.Product)
     */
    @Override
    public void deauthorize(Agency ruleform, Relationship relationship,
                            Product authorized) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AgencyProduct> query = cb.createQuery(AgencyProduct.class);
        Root<AgencyProduct> plRoot = query.from(AgencyProduct.class);
        ParameterExpression<Relationship> relationshipParam = cb.parameter(Relationship.class);
        query.select(plRoot)
             .where(cb.and(cb.equal(plRoot.get(AgencyProduct_.agency),
                                    ruleform),
                           cb.equal(plRoot.get(AgencyProduct_.relationship),
                                    relationshipParam),
                           cb.equal(plRoot.get(AgencyProduct_.product),
                                    authorized)));
        TypedQuery<AgencyProduct> q = em.createQuery(query);
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

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.AgencyModel#getAgencyLocationAuths(com.chiralbehaviors.CoRE.meta.Aspect)
     */
    @Override
    public List<AgencyLocationAuthorization> getAgencyLocationAuths(Aspect<Agency> aspect,
                                                                    boolean includeGrouping) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AgencyLocationAuthorization> query = cb.createQuery(AgencyLocationAuthorization.class);
        Root<AgencyLocationAuthorization> networkRoot = query.from(AgencyLocationAuthorization.class);
        Predicate match = cb.and(cb.equal(networkRoot.get(AgencyLocationAuthorization_.fromParent),
                                          aspect.getClassification()),
                                 cb.equal(networkRoot.get(AgencyLocationAuthorization_.fromRelationship),
                                          aspect.getClassifier()),
                                 cb.equal(networkRoot.get(AgencyLocationAuthorization_.forward),
                                          true));
        if (!includeGrouping) {
            match = cb.and(match,
                           cb.isNull(networkRoot.get(AgencyLocationAuthorization_.groupingAgency)));
        }
        query.select(networkRoot)
             .where(match);
        TypedQuery<AgencyLocationAuthorization> q = em.createQuery(query);
        return q.getResultList();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.AgencyModel#getAgencyProductAuths(com.chiralbehaviors.CoRE.meta.Aspect)
     */
    @Override
    public List<AgencyProductAuthorization> getAgencyProductAuths(Aspect<Agency> aspect,
                                                                  boolean includeGrouping) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AgencyProductAuthorization> query = cb.createQuery(AgencyProductAuthorization.class);
        Root<AgencyProductAuthorization> networkRoot = query.from(AgencyProductAuthorization.class);
        Predicate match = cb.and(cb.equal(networkRoot.get(AgencyProductAuthorization_.fromParent),
                                          aspect.getClassification()),
                                 cb.equal(networkRoot.get(AgencyProductAuthorization_.fromRelationship),
                                          aspect.getClassifier()),
                                 cb.equal(networkRoot.get(AgencyProductAuthorization_.forward),
                                          true));
        if (!includeGrouping) {
            match = cb.and(match,
                           cb.isNull(networkRoot.get(AgencyProductAuthorization_.groupingAgency)));
        }
        query.select(networkRoot)
             .where(match);
        TypedQuery<AgencyProductAuthorization> q = em.createQuery(query);
        return q.getResultList();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getAuthorizedAgencies(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship)
     */
    @Override
    public List<Agency> getAuthorizedAgencies(Agency ruleform,
                                              Relationship relationship) {
        throw new UnsupportedOperationException("Agency -> Agency authorizations are modeled with Agency Networks");
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getAuthorizedLocations(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship)
     */
    @Override
    public List<Location> getAuthorizedLocations(Agency ruleform,
                                                 Relationship relationship) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Location> query = cb.createQuery(Location.class);
        Root<AgencyLocation> plRoot = query.from(AgencyLocation.class);
        Path<Location> path;
        try {
            path = plRoot.get(AgencyLocation_.location);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        query.select(path)
             .where(cb.and(cb.equal(plRoot.get(AgencyLocation_.agency),
                                    ruleform),
                           cb.equal(plRoot.get(AgencyLocation_.relationship),
                                    relationship)));
        TypedQuery<Location> q = em.createQuery(query);
        return q.getResultList();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getAuthorizedProducts(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship)
     */
    @Override
    public List<Product> getAuthorizedProducts(Agency ruleform,
                                               Relationship relationship) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<AgencyProduct> plRoot = query.from(AgencyProduct.class);
        Path<Product> path;
        try {
            path = plRoot.get(AgencyProduct_.product);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        query.select(path)
             .where(cb.and(cb.equal(plRoot.get(AgencyProduct_.agency),
                                    ruleform),
                           cb.equal(plRoot.get(AgencyProduct_.relationship),
                                    relationship)));
        TypedQuery<Product> q = em.createQuery(query);
        return q.getResultList();
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

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.models.AbstractNetworkedModel#getAgencyGroupingClass()
     */
    @Override
    protected Class<?> getAgencyGroupingClass() {
        return AgencyAgencyGrouping.class;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.models.AbstractNetworkedModel#getAttributeAuthorizationClass()
     */
    @Override
    protected Class<?> getAttributeAuthorizationClass() {
        return AgencyAttributeAuthorization.class;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.models.AbstractNetworkedModel#getNetAuthWorkspaceAttribute()
     */
    @Override
    protected SingularAttribute<? super WorkspaceAuthorization, ?> getNetAuthWorkspaceAttribute() {
        return WorkspaceAuthorization_.agencyNetworkAuthorization;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.models.AbstractNetworkedModel#getNetworkAuthClass()
     */
    @Override
    protected Class<?> getNetworkAuthClass() {
        return AgencyNetworkAuthorization.class;
    }
}
