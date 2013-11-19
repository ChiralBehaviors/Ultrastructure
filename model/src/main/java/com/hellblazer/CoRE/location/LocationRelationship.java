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
package com.hellblazer.CoRE.location;

import static com.hellblazer.CoRE.location.LocationRelationship.RULES;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;

/**
 * The persistent class for the location_relationship database table.
 * 
 */
@Entity
@Table(name = "location_relationship", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "location_relationship_id_seq", sequenceName = "location_relationship_id_seq")
@NamedQueries({ @NamedQuery(name = RULES, query = "select lr FROM LocationRelationship AS lr "
                                                  + "WHERE lr.relationship = :relationship "
                                                  + "AND lr.productMappedValue = :mappedEntityValue "
                                                  + "ORDER BY lr.sequenceNumber") })
public class LocationRelationship extends Ruleform {
    private static final long  serialVersionUID        = 1L;
    public static final String AVAILABLE_RELATIONSHIPS = "locationRelationship.availableRelationships";
    public static final String RULES                   = "locationRelationship.rules";
    public static final String FIND_BY_ID              = "locationRelationship.findById";
    public static final String FIND_BY_NAME            = "locationRelationship.findByName";

    //bi-directional many-to-one association to Relationship
    @ManyToOne
    @JoinColumn(name = "attribute_relationship")
    private Relationship       attributeRelationship;

    //bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "product_mapped_value")
    private Product            productMappedValue;

    @Id
    @GeneratedValue(generator = "location_relationship_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    //bi-directional many-to-one association to Attribute
    @ManyToOne
    @JoinColumn(name = "location_1_attribute")
    private Attribute          location1Attribute;

    //bi-directional many-to-one association to Attribute
    @ManyToOne
    @JoinColumn(name = "location_2_attribute")
    private Attribute          location2Attribute;

    //bi-directional many-to-one association to Relationship
    @ManyToOne
    @JoinColumn(name = "relationship")
    private Relationship       relationship;

    @Column(name = "sequence_number")
    private Integer            sequenceNumber;

    public LocationRelationship() {
    }

    /**
     * @param id
     */
    public LocationRelationship(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public LocationRelationship(Agency updatedBy) {
        super(updatedBy);
    }

    public Relationship getAttributeRelationship() {
        return attributeRelationship;
    }

    public Product getEntityMappedValue() {
        return productMappedValue;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Attribute getLocation1Attribute() {
        return location1Attribute;
    }

    public Attribute getLocation2Attribute() {
        return location2Attribute;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setAttributeRelationship(Relationship relationship1) {
        attributeRelationship = relationship1;
    }

    public void setEntityMappedValue(Product product) {
        productMappedValue = product;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setLocation1Attribute(Attribute attribute1) {
        location1Attribute = attribute1;
    }

    public void setLocation2Attribute(Attribute attribute2) {
        location2Attribute = attribute2;
    }

    public void setRelationship(Relationship relationship2) {
        relationship = relationship2;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (attributeRelationship != null) {
            attributeRelationship = (Relationship) attributeRelationship.manageEntity(em,
                                                                                      knownObjects);
        }
        if (productMappedValue != null) {
            productMappedValue = (Product) productMappedValue.manageEntity(em,
                                                                           knownObjects);
        }
        if (location1Attribute != null) {
            location1Attribute = (Attribute) location1Attribute.manageEntity(em,
                                                                             knownObjects);
        }
        if (location2Attribute != null) {
            location2Attribute = (Attribute) location2Attribute.manageEntity(em,
                                                                             knownObjects);
        }
        if (relationship != null) {
            relationship = (Relationship) relationship.manageEntity(em,
                                                                    knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}