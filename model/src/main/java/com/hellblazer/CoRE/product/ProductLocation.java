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
package com.hellblazer.CoRE.product;

import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.attribute.Attributable;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.network.Relationship;

/**
 * The authorization rule form that defines rules for relating products to
 * locations.
 * 
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "product_location", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "product_location_id_seq", sequenceName = "product_location_id_seq")
public class ProductLocation extends Ruleform implements
        Attributable<ProductLocationAttribute> {
    private static final long             serialVersionUID = 1L;

    //bi-directional many-to-one association to Agency
    @ManyToOne
    @JoinColumn(name = "agency")
    private Agency                        agency;

    //bi-directional many-to-one association to ProductLocationAttribute
    @OneToMany(mappedBy = "productLocation")
    @JsonIgnore
    private Set<ProductLocationAttribute> attributes;

    @Id
    @GeneratedValue(generator = "product_location_id_seq", strategy = GenerationType.SEQUENCE)
    private Long                          id;

    //bi-directional many-to-one association to Location
    @ManyToOne
    @JoinColumn(name = "location")
    private Location                      location;

    //bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "product")
    private Product                       product;

    //bi-directional many-to-one association to Relationship
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

    /**
     * @param id
     */
    public ProductLocation(Long id) {
        super(id);
    }

    public Agency getAgency() {
        return agency;
    }

    @Override
    public Set<ProductLocationAttribute> getAttributes() {
        return attributes;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.Attributable#getAttributeType()
     */
    @Override
    public Class<ProductLocationAttribute> getAttributeType() {
        return ProductLocationAttribute.class;
    }

    @Override
    public Long getId() {
        return id;
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

    @Override
    public void setAttributes(Set<ProductLocationAttribute> productLocationAttributes) {
        attributes = productLocationAttributes;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
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