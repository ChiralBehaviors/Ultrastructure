/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chiralbehaviors.CoRE.product;

import static com.chiralbehaviors.CoRE.product.ProductLocation.PRODUCTS_AT_LOCATION;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
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
import com.chiralbehaviors.CoRE.network.Relationship;
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
public class ProductLocation extends Ruleform implements
        Attributable<ProductLocationAttribute> {
    public static final String            PRODUCTS_AT_LOCATION = "productLocation.productsAtLocation";
    private static final long             serialVersionUID     = 1L;

    // bi-directional many-to-one association to Agency
    @ManyToOne
    @JoinColumn(name = "agency")
    private Agency                        agency;

    // bi-directional many-to-one association to ProductLocationAttribute
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "productLocation")
    @JsonIgnore
    private Set<ProductLocationAttribute> attributes;

    // bi-directional many-to-one association to Location
    @ManyToOne
    @JoinColumn(name = "location")
    private Location                      location;

    // bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "product")
    private Product                       product;

    // bi-directional many-to-one association to Relationship
    @ManyToOne
    @JoinColumn(name = "relationship")
    private Relationship                  relationship;

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

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.Ruleform#traverseForeignKeys(javax.persistence
     * .EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (product != null) {
            product = (Product) product.manageEntity(em, knownObjects);
        }
        if (location != null) {
            location = (Location) location.manageEntity(em, knownObjects);
        }
        if (relationship != null) {
            relationship = (Relationship) relationship.manageEntity(em,
                                                                    knownObjects);
        }
        if (agency != null) {
            agency = (Agency) agency.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}