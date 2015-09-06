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

import static com.chiralbehaviors.CoRE.product.ProductLocation.PRODUCTS_AT_LOCATION;

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

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attributable;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The authorization rule form that defines rules for relating products to
 * locations.
 *
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "product_location", schema = "ruleform")
@NamedQueries({ @NamedQuery(name = PRODUCTS_AT_LOCATION, query = "SELECT n.product "
                                                                 + "FROM ProductLocation n "
                                                                 + "WHERE n.relationship = :relationship "
                                                                 + "AND n.location = :location"), })
public class ProductLocation extends Ruleform
        implements Attributable<ProductLocationAttribute> {
    public static final String PRODUCTS_AT_LOCATION = "productLocation.productsAtLocation";
    private static final long  serialVersionUID     = 1L;

    // bi-directional many-to-one association to Agency
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "agency")
    private Agency agency;

    // bi-directional many-to-one association to ProductLocationAttribute
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "productLocation")
    @JsonIgnore
    private Set<ProductLocationAttribute> attributes;

    // bi-directional many-to-one association to Location
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "location")
    private Location location;

    // bi-directional many-to-one association to Product
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "product")
    private Product product;

    // bi-directional many-to-one association to Relationship
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "relationship")
    private Relationship relationship;

    public ProductLocation() {
    }

    /**
     * @param updatedBy
     */
    public ProductLocation(Agency updatedBy) {
        super(updatedBy);
    }

    public ProductLocation(Agency agency, Product product,
                           Relationship relationship, Location location,
                           Agency updatedBy) {
        super(updatedBy);
        this.product = product;
        this.relationship = relationship;
        this.location = location;
        this.agency = agency;
    }

    /**
     * @param id
     */
    public ProductLocation(UUID id) {
        super(id);
    }

    public Agency getAgency() {
        return agency;
    }

    @Override
    public Set<ProductLocationAttribute> getAttributes() {
        return attributes;
    }

    public Location getLocation() {
        return location;
    }

    public Product getProduct() {
        return product;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setAgency(Agency agency2) {
        agency = agency2;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends ProductLocationAttribute> void setAttributes(Set<A> attributes) {
        this.attributes = (Set<ProductLocationAttribute>) attributes;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }
}