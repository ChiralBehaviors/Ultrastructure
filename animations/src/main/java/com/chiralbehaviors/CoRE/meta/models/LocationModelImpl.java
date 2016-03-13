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

import com.chiralbehaviors.CoRE.agency.AgencyLocationAuthorization_;
import com.chiralbehaviors.CoRE.agency.AgencyLocation_;
import com.chiralbehaviors.CoRE.existential.domain.Agency;
import com.chiralbehaviors.CoRE.existential.domain.AgencyLocation;
import com.chiralbehaviors.CoRE.existential.domain.AgencyLocationAuthorization;
import com.chiralbehaviors.CoRE.existential.domain.Attribute;
import com.chiralbehaviors.CoRE.existential.domain.Location;
import com.chiralbehaviors.CoRE.existential.domain.Product;
import com.chiralbehaviors.CoRE.existential.domain.Relationship;
import com.chiralbehaviors.CoRE.location.LocationAttribute;
import com.chiralbehaviors.CoRE.location.LocationAttributeAuthorization;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.location.LocationNetworkAuthorization;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.LocationModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.product.ProductLocation;
import com.chiralbehaviors.CoRE.product.ProductLocationAuthorization;
import com.chiralbehaviors.CoRE.product.ProductLocationAuthorization_;
import com.chiralbehaviors.CoRE.product.ProductLocation_;
import com.chiralbehaviors.CoRE.security.AgencyLocationGrouping;

/**
 * @author hhildebrand
 *
 */
public class LocationModelImpl extends
        AbstractNetworkedModel<Location, LocationNetwork, LocationAttributeAuthorization, LocationAttribute>
        implements LocationModel {

    /**
     * @param em
     */
    public LocationModelImpl(Model model) {
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
    public void authorize(Aspect<Location> aspect, Attribute... attributes) {
        LocationNetworkAuthorization auth = new LocationNetworkAuthorization(model.getCurrentPrincipal()
                                                                                  .getPrincipal());
        auth.setClassifier(aspect.getClassifier());
        auth.setClassification(aspect.getClassification());
        em.persist(auth);
        for (Attribute attribute : attributes) {
            LocationAttributeAuthorization authorization = new LocationAttributeAuthorization(attribute,
                                                                                              model.getCurrentPrincipal()
                                                                                                   .getPrincipal());
            authorization.setNetworkAuthorization(auth);
            em.persist(authorization);
        }
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#authorize(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship, com.chiralbehaviors.CoRE.agency.Agency)
     */
    @Override
    public void authorize(Location ruleform, Relationship relationship,
                          Agency authorized) {
        AgencyLocation a = new AgencyLocation(model.getCurrentPrincipal()
                                                   .getPrincipal());
        a.setAgency(authorized);
        a.setRelationship(relationship);
        a.setLocation(ruleform);
        em.persist(a);
        AgencyLocation b = new AgencyLocation(model.getCurrentPrincipal()
                                                   .getPrincipal());
        b.setAgency(authorized);
        b.setRelationship(relationship.getInverse());
        b.setLocation(ruleform);
        em.persist(b);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#authorize(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship, com.chiralbehaviors.CoRE.location.Location)
     */
    @Override
    public void authorize(Location ruleform, Relationship relationship,
                          Location authorized) {
        throw new UnsupportedOperationException("Location -> Location authorizations are modeled with Location Networks");
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#authorize(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship, com.chiralbehaviors.CoRE.product.Product)
     */
    @Override
    public void authorize(Location ruleform, Relationship relationship,
                          Product authorized) {
        ProductLocation a = new ProductLocation(model.getCurrentPrincipal()
                                                     .getPrincipal());
        a.setProduct(authorized);
        a.setRelationship(relationship);
        a.setLocation(ruleform);
        em.persist(a);
        ProductLocation b = new ProductLocation(model.getCurrentPrincipal()
                                                     .getPrincipal());
        b.setProduct(authorized);
        b.setRelationship(relationship.getInverse());
        b.setLocation(ruleform);
        em.persist(b);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(com.chiralbehaviors.CoRE.network
     * .Networked)
     */
    @Override
    public Location create(Location prototype) {
        Location copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(model.getCurrentPrincipal()
                               .getPrincipal());
        for (LocationNetwork network : prototype.getNetworkByParent()) {
            network.getParent()
                   .link(network.getRelationship(), copy,
                         model.getCurrentPrincipal()
                              .getPrincipal(),
                         model.getCurrentPrincipal()
                              .getPrincipal(),
                         em);
        }
        for (LocationAttribute attribute : prototype.getAttributes()) {
            LocationAttribute clone = (LocationAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setLocation(copy);
            clone.setUpdatedBy(model.getCurrentPrincipal()
                                    .getPrincipal());
        }
        return copy;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.models.AbstractNetworkedModel#create(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.attribute.ClassifiedAttributeAuthorization)
     */
    @Override
    public LocationAttribute create(Location ruleform, Attribute attribute,
                                    Agency updatedBy) {
        return new LocationAttribute(ruleform, attribute, updatedBy);
    }

    @Override
    public final Location create(String name, String description) {
        Location location = new Location(name, description,
                                         model.getCurrentPrincipal()
                                              .getPrincipal());
        em.persist(location);
        return location;
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
    public final Location create(String name, String description,
                                 Aspect<Location> aspect, Agency updatedBy,
                                 Aspect<Location>... aspects) {
        Location location = new Location(name, description,
                                         model.getCurrentPrincipal()
                                              .getPrincipal());
        em.persist(location);
        initialize(location, aspect);
        if (aspects != null) {
            for (Aspect<Location> a : aspects) {
                initialize(location, a);
            }
        }
        return location;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#deauthorize(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship, com.chiralbehaviors.CoRE.agency.Agency)
     */
    @Override
    public void deauthorize(Location ruleform, Relationship relationship,
                            Agency authorized) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AgencyLocation> query = cb.createQuery(AgencyLocation.class);
        Root<AgencyLocation> plRoot = query.from(AgencyLocation.class);
        ParameterExpression<Relationship> relationshipParam = cb.parameter(Relationship.class);
        query.select(plRoot)
             .where(cb.and(cb.equal(plRoot.get(AgencyLocation_.agency),
                                    authorized),
                           cb.equal(plRoot.get(AgencyLocation_.relationship),
                                    relationshipParam),
                           cb.equal(plRoot.get(AgencyLocation_.location),
                                    ruleform)));
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
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#deauthorize(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship, com.chiralbehaviors.CoRE.location.Location)
     */
    @Override
    public void deauthorize(Location ruleform, Relationship relationship,
                            Location authorized) {
        throw new UnsupportedOperationException("Location -> Location authorizations are modeled with Location Networks");
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#deauthorize(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship, com.chiralbehaviors.CoRE.product.Product)
     */
    @Override
    public void deauthorize(Location ruleform, Relationship relationship,
                            Product authorized) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProductLocation> query = cb.createQuery(ProductLocation.class);
        Root<ProductLocation> plRoot = query.from(ProductLocation.class);
        ParameterExpression<Relationship> relationshipParam = cb.parameter(Relationship.class);
        query.select(plRoot)
             .where(cb.and(cb.equal(plRoot.get(ProductLocation_.product),
                                    authorized),
                           cb.equal(plRoot.get(ProductLocation_.relationship),
                                    relationshipParam),
                           cb.equal(plRoot.get(ProductLocation_.location),
                                    ruleform)));
        TypedQuery<ProductLocation> q = em.createQuery(query);
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
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getAuthorizedAgencies(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship)
     */
    @Override
    public List<Agency> getAuthorizedAgencies(Location ruleform,
                                              Relationship relationship) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Agency> query = cb.createQuery(Agency.class);
        Root<AgencyLocation> plRoot = query.from(AgencyLocation.class);
        Path<Agency> path;
        try {
            path = plRoot.get(AgencyLocation_.agency);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        query.select(path)
             .where(cb.and(cb.equal(plRoot.get(AgencyLocation_.location),
                                    ruleform),
                           cb.equal(plRoot.get(AgencyLocation_.relationship),
                                    relationship)));
        TypedQuery<Agency> q = em.createQuery(query);
        return q.getResultList();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getAuthorizedLocations(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship)
     */
    @Override
    public List<Location> getAuthorizedLocations(Location ruleform,
                                                 Relationship relationship) {
        throw new UnsupportedOperationException("Location -> Location authorizations are modeled with Location Networks");
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getAuthorizedProducts(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship)
     */
    @Override
    public List<Product> getAuthorizedProducts(Location ruleform,
                                               Relationship relationship) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<ProductLocation> plRoot = query.from(ProductLocation.class);
        Path<Product> path;
        try {
            path = plRoot.get(ProductLocation_.product);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        query.select(path)
             .where(cb.and(cb.equal(plRoot.get(ProductLocation_.location),
                                    ruleform),
                           cb.equal(plRoot.get(ProductLocation_.relationship),
                                    relationship)));
        TypedQuery<Product> q = em.createQuery(query);
        return q.getResultList();
    }

    @Override
    public List<LocationNetwork> getInterconnections(Collection<Location> parents,
                                                     Collection<Relationship> relationships,
                                                     Collection<Location> children) {
        TypedQuery<LocationNetwork> query = em.createNamedQuery(LocationNetwork.GET_NETWORKS,
                                                                LocationNetwork.class);
        query.setParameter("parents", parents);
        query.setParameter("relationship", relationships);
        query.setParameter("children", children);
        return query.getResultList();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.LocationModel#getLocationAgencyAuths(com.chiralbehaviors.CoRE.meta.Aspect)
     */
    @Override
    public List<AgencyLocationAuthorization> getLocationAgencyAuths(Aspect<Location> aspect,
                                                                    boolean includeGrouping) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AgencyLocationAuthorization> query = cb.createQuery(AgencyLocationAuthorization.class);
        Root<AgencyLocationAuthorization> networkRoot = query.from(AgencyLocationAuthorization.class);
        Predicate match = cb.and(cb.equal(networkRoot.get(AgencyLocationAuthorization_.toParent),
                                          aspect.getClassification()),
                                 cb.equal(networkRoot.get(AgencyLocationAuthorization_.toRelationship),
                                          aspect.getClassifier()),
                                 cb.equal(networkRoot.get(AgencyLocationAuthorization_.forward),
                                          false));
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
     * @see com.chiralbehaviors.CoRE.meta.LocationModel#getLocationProductAuths(com.chiralbehaviors.CoRE.meta.Aspect)
     */
    @Override
    public List<ProductLocationAuthorization> getLocationProductAuths(Aspect<Location> aspect,
                                                                      boolean includeGrouping) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProductLocationAuthorization> query = cb.createQuery(ProductLocationAuthorization.class);
        Root<ProductLocationAuthorization> networkRoot = query.from(ProductLocationAuthorization.class);
        Predicate match = cb.and(cb.equal(networkRoot.get(ProductLocationAuthorization_.toParent),
                                          aspect.getClassification()),
                                 cb.equal(networkRoot.get(ProductLocationAuthorization_.toRelationship),
                                          aspect.getClassifier()),
                                 cb.equal(networkRoot.get(ProductLocationAuthorization_.forward),
                                          false));
        if (!includeGrouping) {
            match = cb.and(match,
                           cb.isNull(networkRoot.get(ProductLocationAuthorization_.groupingAgency)));
        }
        query.select(networkRoot)
             .where(match);
        TypedQuery<ProductLocationAuthorization> q = em.createQuery(query);
        return q.getResultList();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.models.AbstractNetworkedModel#getAgencyGroupingClass()
     */
    @Override
    protected Class<?> getAgencyGroupingClass() {
        return AgencyLocationGrouping.class;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.models.AbstractNetworkedModel#getAttributeAuthorizationClass()
     */
    @Override
    protected Class<?> getAttributeAuthorizationClass() {
        return LocationAttributeAuthorization.class;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.models.AbstractNetworkedModel#getNetworkAuthClass()
     */
    @Override
    protected Class<?> getNetworkAuthClass() {
        return LocationNetworkAuthorization.class;
    }
}
