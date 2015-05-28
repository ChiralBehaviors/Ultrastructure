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
package com.chiralbehaviors.CoRE.product;

import static com.chiralbehaviors.CoRE.ExistentialRuleform.GET_CHILDREN_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GET_NETWORKS_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.USED_RELATIONSHIPS_SUFFIX;
import static com.chiralbehaviors.CoRE.product.ProductNetwork.GET_CHILDREN;
import static com.chiralbehaviors.CoRE.product.ProductNetwork.GET_NETWORKS;
import static com.chiralbehaviors.CoRE.product.ProductNetwork.GET_USED_RELATIONSHIPS;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.Triggers;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attributable;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The network relationships of products.
 *
 * @author hhildebrand
 *
 */
@NamedQueries({
               @NamedQuery(name = GET_USED_RELATIONSHIPS, query = "select distinct n.relationship from ProductNetwork n"),
               @NamedQuery(name = GET_CHILDREN, query = "SELECT n.child FROM ProductNetwork n "
                                                        + "WHERE n.parent = :parent "
                                                        + "AND n.relationship = :relationship"),
               @NamedQuery(name = GET_NETWORKS, query = "SELECT n FROM ProductNetwork n "
                                                        + "WHERE n.parent = :parent "
                                                        + "AND n.relationship = :relationship "
                                                        + "AND n.child = :child") })
@Entity
@Table(name = "product_network", schema = "ruleform")
public class ProductNetwork extends NetworkRuleform<Product> implements
        Attributable<ProductNetworkAttribute> {
    public static final String GET_CHILDREN           = "productNetwork"
                                                        + GET_CHILDREN_SUFFIX;
    public static final String GET_NETWORKS           = "productNetwork"
                                                        + GET_NETWORKS_SUFFIX;
    public static final String GET_USED_RELATIONSHIPS = "productNetwork"
                                                        + USED_RELATIONSHIPS_SUFFIX;
    private static final long  serialVersionUID       = 1L;

    public static List<Relationship> getUsedRelationships(EntityManager em) {
        return em.createNamedQuery(GET_USED_RELATIONSHIPS, Relationship.class).getResultList();
    }

    // bi-directional many-to-one association to ProductNetworkAttribute
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "network")
    @JsonIgnore
    private Set<ProductNetworkAttribute> attributes;

    // bi-directional many-to-one association to Product
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "child")
    private Product                      child;

    //bi-directional many-to-one association to Product
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "parent")
    private Product                      parent;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "premise1")
    private ProductNetwork               premise1;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "premise2")
    private ProductNetwork               premise2;

    public ProductNetwork() {
    }

    /**
     * @param updatedBy
     */
    public ProductNetwork(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public ProductNetwork(Product parent, Relationship relationship,
                          Product child, Agency updatedBy) {
        super(relationship, updatedBy);
        this.parent = parent;
        this.child = child;
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public ProductNetwork(Relationship relationship, Agency updatedBy) {
        super(relationship, updatedBy);
    }

    /**
     * @param id
     */
    public ProductNetwork(UUID id) {
        super(id);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public ProductNetwork(UUID id, Product parent, Relationship relationship,
                          Product child) {
        super(id);
        this.parent = parent;
        this.child = child;
        setRelationship(relationship);
    }

    @Override
    public void delete(Triggers triggers) {
        triggers.delete(this);
    }

    @Override
    @JsonIgnore
    public Class<?> getAttributeClass() {
        return ProductNetworkAttribute.class;
    }

    @Override
    public Set<ProductNetworkAttribute> getAttributes() {
        return attributes;
    }

    @Override
    @JsonGetter
    public Product getChild() {
        return child;
    }

    @Override
    @JsonGetter
    public Product getParent() {
        return parent;
    }

    /**
     * @return the premise1
     */
    @Override
    @JsonGetter
    public ProductNetwork getPremise1() {
        return premise1;
    }

    /**
     * @return the premise2
     */
    @Override
    @JsonGetter
    public ProductNetwork getPremise2() {
        return premise2;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, ProductNetwork> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.productNetwork;
    }

    @Override
    public void persist(Triggers triggers) {
        triggers.persist(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends ProductNetworkAttribute> void setAttributes(Set<A> attributes) {
        this.attributes = (Set<ProductNetworkAttribute>) attributes;
    }

    @Override
    public void setChild(Product child) {
        this.child = child;
    }

    @Override
    public void setParent(Product parent) {
        this.parent = parent;
    }
}
