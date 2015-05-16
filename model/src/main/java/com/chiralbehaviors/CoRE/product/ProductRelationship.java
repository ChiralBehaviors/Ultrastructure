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
package com.chiralbehaviors.CoRE.product;

import static com.chiralbehaviors.CoRE.product.ProductRelationship.PRODUCTS_AT_RELATIONSHIP;

import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attributable;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The authorization rule form that defines rules for relating products to
 * relationships.
 *
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "product_relationship", schema = "ruleform")
@NamedQueries({ @NamedQuery(name = PRODUCTS_AT_RELATIONSHIP, query = "SELECT n.product "
                                                                 + "FROM ProductRelationship n "
                                                                 + "WHERE n.relationship = :relationship "
                                                                 + "AND n.child = :child"), })
public class ProductRelationship extends Ruleform implements
        Attributable<ProductRelationshipAttribute> {
    public static final String            PRODUCTS_AT_RELATIONSHIP = "productRelationship.productsAtRelationship";
    private static final long             serialVersionUID     = 1L;

    // bi-directional many-to-one association to Agency
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "agency")
    private Agency                        agency;

    // bi-directional many-to-one association to ProductRelationshipAttribute
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "productRelationship")
    @JsonIgnore
    private Set<ProductRelationshipAttribute> attributes;

    // bi-directional many-to-one association to Relationship
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "relationship")
    private Relationship                      relationship;

    // bi-directional many-to-one association to Product
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "product")
    private Product                       product;

    // bi-directional many-to-one association to Relationship
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "child")
    private Relationship                  child;

    public ProductRelationship() {
    }

    /**
     * @param updatedBy
     */
    public ProductRelationship(Agency updatedBy) {
        super(updatedBy);
    }

    public ProductRelationship(Agency agency, Product product,
                           Relationship relationship, Relationship child,
                           Agency updatedBy) {
        super(updatedBy);
        this.product = product;
        this.relationship = relationship;
        this.child = child;
        this.agency = agency;
    }

    /**
     * @param id
     */
    public ProductRelationship(UUID id) {
        super(id);
    }

    public Agency getAgency() {
        return agency;
    }

    @Override
    public Set<ProductRelationshipAttribute> getAttributes() {
        return attributes;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public Product getProduct() {
        return product;
    }

    public Relationship getChild() {
        return child;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, ProductRelationship> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.productRelationship;
    }

    public void setAgency(Agency agency2) {
        agency = agency2;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends ProductRelationshipAttribute> void setAttributes(Set<A> attributes) {
        this.attributes = (Set<ProductRelationshipAttribute>) attributes;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setChild(Relationship child) {
        this.child = child;
    }
}