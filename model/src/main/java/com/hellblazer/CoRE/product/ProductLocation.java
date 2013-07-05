/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hellblazer.CoRE.product;

import java.util.Set;

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
import com.hellblazer.CoRE.attribute.Attributable;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The authorization rule form that defines rules for relating products to
 * locations.
 * 
 * @author hhildebrand
 * 
 */
@javax.persistence.Entity
@Table(name = "product_location", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "product_location_id_seq", sequenceName = "product_location_id_seq")
public class ProductLocation extends Ruleform implements
        Attributable<ProductLocationAttribute> {
    private static final long             serialVersionUID = 1L;

    //bi-directional many-to-one association to ProductLocationAttribute
    @OneToMany(mappedBy = "productLocation")
    @JsonIgnore
    private Set<ProductLocationAttribute> attributes;

    //bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "product")
    private Product                       product;

    @Id
    @GeneratedValue(generator = "product_location_id_seq", strategy = GenerationType.SEQUENCE)
    private Long                          id;

    //bi-directional many-to-one association to Location
    @ManyToOne
    @JoinColumn(name = "location")
    private Location                      location;

    //bi-directional many-to-one association to Relationship
    @ManyToOne
    @JoinColumn(name = "relationship")
    private Relationship                  relationship;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "resource")
    private Resource                      resource;

    public ProductLocation() {
    }

    /**
     * @param id
     */
    public ProductLocation(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public ProductLocation(Resource updatedBy) {
        super(updatedBy);
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

    public Resource getResource() {
        return resource;
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

    public void setResource(Resource resource2) {
        resource = resource2;
    }
}