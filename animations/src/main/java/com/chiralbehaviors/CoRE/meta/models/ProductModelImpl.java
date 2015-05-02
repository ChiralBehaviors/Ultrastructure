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
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyProduct;
import com.chiralbehaviors.CoRE.agency.AgencyProduct_;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.ProductModel;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductAttribute;
import com.chiralbehaviors.CoRE.product.ProductAttributeAuthorization;
import com.chiralbehaviors.CoRE.product.ProductLocation;
import com.chiralbehaviors.CoRE.product.ProductLocation_;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.chiralbehaviors.CoRE.product.ProductNetworkAuthorization;
import com.chiralbehaviors.CoRE.relationship.Relationship;

/**
 * @author hhildebrand
 *
 */
public class ProductModelImpl
        extends
        AbstractNetworkedModel<Product, ProductNetwork, ProductAttributeAuthorization, ProductAttribute>
        implements ProductModel {

    /**
     * @param em
     */
    public ProductModelImpl(Model model) {
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
    public void authorize(Aspect<Product> aspect, Attribute... attributes) {
        ProductNetworkAuthorization auth = new ProductNetworkAuthorization(
                                                                           kernel.getCore());
        auth.setClassifier(aspect.getClassifier());
        auth.setClassification(aspect.getClassification());
        em.persist(auth);
        for (Attribute attribute : attributes) {
            ProductAttributeAuthorization authorization = new ProductAttributeAuthorization(
                                                                                            attribute,
                                                                                            kernel.getCoreModel());
            em.persist(authorization);
        }
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#authorize(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship, com.chiralbehaviors.CoRE.agency.Agency)
     */
    @Override
    public void authorize(Product ruleform, Relationship relationship,
                          Agency authorized) {
        AgencyProduct a = new AgencyProduct(kernel.getCoreAnimationSoftware());
        a.setAgency(authorized);
        a.setRelationship(relationship);
        a.setProduct(ruleform);
        em.persist(a);
        AgencyProduct b = new AgencyProduct(kernel.getCoreAnimationSoftware());
        b.setAgency(authorized);
        b.setRelationship(relationship.getInverse());
        b.setProduct(ruleform);
        em.persist(b);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#authorize(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship, com.chiralbehaviors.CoRE.location.Location)
     */
    @Override
    public void authorize(Product ruleform, Relationship relationship,
                          Location authorized) {
        assert ruleform != null : "ruleform is null";
        assert relationship != null : "relationshp is null";
        assert authorized != null : "authorized is null";
        ProductLocation a = new ProductLocation(
                                                kernel.getCoreAnimationSoftware());
        a.setProduct(ruleform);
        a.setRelationship(relationship);
        a.setLocation(authorized);
        em.persist(a);
        ProductLocation b = new ProductLocation(
                                                kernel.getCoreAnimationSoftware());
        b.setProduct(ruleform);
        b.setRelationship(relationship.getInverse());
        b.setLocation(authorized);
        em.persist(b);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#authorize(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship, com.chiralbehaviors.CoRE.product.Product)
     */
    @Override
    public void authorize(Product ruleform, Relationship relationship,
                          Product authorized) {
        throw new UnsupportedOperationException(
                                                "Product -> Product authorizations are modeled with Product Networks");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(com.chiralbehaviors.CoRE.network
     * .Networked)
     */
    @Override
    public Product create(Product prototype) {
        Product copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(kernel.getCoreModel());
        for (ProductNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(), copy,
                                     kernel.getCoreModel(),
                                     kernel.getInverseSoftware(), em);
        }
        for (ProductAttribute attribute : prototype.getAttributes()) {
            ProductAttribute clone = (ProductAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setProduct(copy);
            clone.setUpdatedBy(kernel.getCoreModel());
        }
        return copy;
    }

    @Override
    public ProductAttribute create(Product ruleform, Attribute attribute,
                                   Agency updatedBy) {
        return new ProductAttribute(ruleform, attribute, updatedBy);
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
    final public Product create(String name, String description,
                                Aspect<Product> aspect, Agency updatedBy,
                                Aspect<Product>... aspects) {
        Product product = new Product(name, description, kernel.getCoreModel());
        em.persist(product);
        initialize(product, aspect, updatedBy);
        if (aspects != null) {
            for (Aspect<Product> a : aspects) {
                initialize(product, a, updatedBy);
            }
        }
        return product;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getAuthorizedAgencies(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship)
     */
    @Override
    public List<Agency> getAuthorizedAgencies(Product ruleform,
                                              Relationship relationship) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Agency> query = cb.createQuery(Agency.class);
        Root<AgencyProduct> plRoot = query.from(AgencyProduct.class);
        Path<Agency> path;
        try {
            path = plRoot.get(AgencyProduct_.agency);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        query.select(path).where(cb.and(cb.equal(plRoot.get(AgencyProduct_.product),
                                                 ruleform),
                                        cb.equal(plRoot.get(AgencyProduct_.relationship),
                                                 relationship)));
        TypedQuery<Agency> q = em.createQuery(query);
        return q.getResultList();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getAuthorizedLocations(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship)
     */
    @Override
    public List<Location> getAuthorizedLocations(Product ruleform,
                                                 Relationship relationship) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Location> query = cb.createQuery(Location.class);
        Root<ProductLocation> plRoot = query.from(ProductLocation.class);
        Path<Location> path;
        try {
            path = plRoot.get(ProductLocation_.location);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        query.select(path).where(cb.and(cb.equal(plRoot.get(ProductLocation_.product),
                                                 ruleform),
                                        cb.equal(plRoot.get(ProductLocation_.relationship),
                                                 relationship)));
        TypedQuery<Location> q = em.createQuery(query);
        return q.getResultList();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getAuthorizedProducts(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship)
     */
    @Override
    public List<Product> getAuthorizedProducts(Product ruleform,
                                               Relationship relationship) {
        throw new UnsupportedOperationException(
                                                "Product -> Product authorizations are modeled with Product Networks");
    }

    @Override
    public List<ProductNetwork> getInterconnections(Collection<Product> parents,
                                                    Collection<Relationship> relationships,
                                                    Collection<Product> children) {
        if (parents == null || parents.size() == 0 || relationships == null
            || relationships.size() == 0 || children == null
            || children.size() == 0) {
            return null;
        }
        TypedQuery<ProductNetwork> query = em.createNamedQuery(ProductNetwork.GET_NETWORKS,
                                                               ProductNetwork.class);
        query.setParameter("parents", parents);
        query.setParameter("relationships", relationships);
        query.setParameter("children", children);
        return query.getResultList();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#deauthorize(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship, com.chiralbehaviors.CoRE.agency.Agency)
     */
    @Override
    public void deauthorize(Product ruleform, Relationship relationship,
                            Agency authorized) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AgencyProduct> query = cb.createQuery(AgencyProduct.class);
        Root<AgencyProduct> plRoot = query.from(AgencyProduct.class);
        query.select(plRoot).where(cb.and(cb.equal(plRoot.get(AgencyProduct_.agency),
                                                   authorized),
                                          cb.equal(plRoot.get(AgencyProduct_.relationship),
                                                   relationship),
                                          cb.equal(plRoot.get(AgencyProduct_.product),
                                                   ruleform)));
        TypedQuery<AgencyProduct> q = em.createQuery(query);
        try {
            em.remove(q.getSingleResult());
        } catch (NoResultException e) {
            // no need to remove
        }
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#deauthorize(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship, com.chiralbehaviors.CoRE.location.Location)
     */
    @Override
    public void deauthorize(Product ruleform, Relationship relationship,
                            Location authorized) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProductLocation> query = cb.createQuery(ProductLocation.class);
        Root<ProductLocation> plRoot = query.from(ProductLocation.class);
        query.select(plRoot).where(cb.and(cb.equal(plRoot.get(ProductLocation_.product),
                                                   ruleform),
                                          cb.equal(plRoot.get(ProductLocation_.relationship),
                                                   relationship),
                                          cb.equal(plRoot.get(ProductLocation_.location),
                                                   authorized)));
        TypedQuery<ProductLocation> q = em.createQuery(query);
        try {
            em.remove(q.getSingleResult());
        } catch (NoResultException e) {
            // no need to remove
        }
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#deauthorize(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.relationship.Relationship, com.chiralbehaviors.CoRE.product.Product)
     */
    @Override
    public void deauthorize(Product ruleform, Relationship relationship,
                            Product authorized) {
        throw new UnsupportedOperationException(
                                                "Product -> Product authorizations are modeled with Product Networks");
    }
}
