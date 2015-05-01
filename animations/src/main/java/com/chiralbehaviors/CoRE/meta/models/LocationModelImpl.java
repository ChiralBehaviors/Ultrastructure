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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyLocation;
import com.chiralbehaviors.CoRE.agency.AgencyLocation_;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationAttribute;
import com.chiralbehaviors.CoRE.location.LocationAttributeAuthorization;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.location.LocationNetworkAuthorization;
import com.chiralbehaviors.CoRE.meta.LocationModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductLocation;
import com.chiralbehaviors.CoRE.product.ProductLocation_;
import com.chiralbehaviors.CoRE.relationship.Relationship;

/**
 * @author hhildebrand
 *
 */
public class LocationModelImpl
        extends
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
        LocationNetworkAuthorization auth = new LocationNetworkAuthorization(
                                                                             kernel.getCore());
        auth.setClassifier(aspect.getClassifier());
        auth.setClassification(aspect.getClassification());
        em.persist(auth);
        for (Attribute attribute : attributes) {
            LocationAttributeAuthorization authorization = new LocationAttributeAuthorization(
                                                                                              attribute,
                                                                                              kernel.getCoreModel());
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
        AgencyLocation a = new AgencyLocation(kernel.getCoreAnimationSoftware());
        a.setAgency(authorized);
        a.setRelationship(relationship);
        a.setLocation(ruleform);
        em.persist(a);
        AgencyLocation b = new AgencyLocation(kernel.getCoreAnimationSoftware());
        b.setAgency(authorized);
        a.setRelationship(relationship.getInverse());
        b.setLocation(ruleform);
        em.persist(b);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#authorize(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship, com.chiralbehaviors.CoRE.location.Location)
     */
    @Override
    public void authorize(Location ruleform, Relationship relationship,
                          Location authorized) {
        throw new UnsupportedOperationException(
                                                "Location -> Location authorizations are modeled with Location Networks");
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#authorize(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship, com.chiralbehaviors.CoRE.product.Product)
     */
    @Override
    public void authorize(Location ruleform, Relationship relationship,
                          Product authorized) {
        ProductLocation a = new ProductLocation(
                                                kernel.getCoreAnimationSoftware());
        a.setProduct(authorized);
        a.setRelationship(relationship);
        a.setLocation(ruleform);
        em.persist(a);
        ProductLocation b = new ProductLocation(
                                                kernel.getCoreAnimationSoftware());
        b.setProduct(authorized);
        a.setRelationship(relationship.getInverse());
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
        copy.setUpdatedBy(kernel.getCoreModel());
        for (LocationNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(), copy,
                                     kernel.getCoreModel(),
                                     kernel.getInverseSoftware(), em);
        }
        for (LocationAttribute attribute : prototype.getAttributes()) {
            LocationAttribute clone = (LocationAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setLocation(copy);
            clone.setUpdatedBy(kernel.getCoreModel());
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
                                         kernel.getCoreModel());
        em.persist(location);
        initialize(location, aspect, updatedBy);
        if (aspects != null) {
            for (Aspect<Location> a : aspects) {
                initialize(location, a, updatedBy);
            }
        }
        return location;
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
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getAuthorizedAgency(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship)
     */
    @Override
    public Agency getAuthorizedAgency(Location ruleform,
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
        query.select(path).where(cb.and(cb.equal(plRoot.get(AgencyLocation_.location),
                                                 ruleform),
                                        cb.equal(plRoot.get(AgencyLocation_.relationship),
                                                 relationship)));
        TypedQuery<Agency> q = em.createQuery(query);
        List<Agency> result = q.getResultList();
        if (result.isEmpty()) {
            return null;
        } else if (result.size() > 1) {
            throw new IllegalStateException(
                                            String.format("%s is a non singular authorization of %s",
                                                          relationship,
                                                          ruleform));
        }
        return result.get(0);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getAuthorizedLocation(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship)
     */
    @Override
    public Location getAuthorizedLocation(Location ruleform,
                                          Relationship relationship) {
        throw new UnsupportedOperationException(
                                                "Location -> Location authorizations are modeled with Location Networks");
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getAuthorizedProduct(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship)
     */
    @Override
    public Product getAuthorizedProduct(Location ruleform,
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
        query.select(path).where(cb.and(cb.equal(plRoot.get(ProductLocation_.location),
                                                 ruleform),
                                        cb.equal(plRoot.get(ProductLocation_.relationship),
                                                 relationship)));
        TypedQuery<Product> q = em.createQuery(query);
        List<Product> result = q.getResultList();
        if (result.isEmpty()) {
            return null;
        } else if (result.size() > 1) {
            throw new IllegalStateException(
                                            String.format("%s is a non singular authorization of %s",
                                                          relationship,
                                                          ruleform));
        }
        return result.get(0);
    }
}
