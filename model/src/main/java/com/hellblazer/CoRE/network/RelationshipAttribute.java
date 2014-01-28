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
package com.hellblazer.CoRE.network;

import static com.hellblazer.CoRE.network.RelationshipAttribute.GET_ATTRIBUTE;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.AttributeValue;
import com.hellblazer.CoRE.attribute.unit.Unit;

/**
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "relationship_attribute", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "relationship_attribute_id_seq", sequenceName = "relationship_attribute_id_seq")
@NamedQueries({ @NamedQuery(name = GET_ATTRIBUTE, query = "select ra from RelationshipAttribute ra where ra.relationship = :relationship and ra.attribute = :attribute") })
public class RelationshipAttribute extends AttributeValue<Relationship> {
    public static final String GET_ATTRIBUTE    = "relationshipAttribute.getAttribute";
    private static final long  serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "relationship_attribute_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    //bi-directional many-to-one association to Relationship

    @ManyToOne
    @JoinColumn(name = "relationship")
    private Relationship       relationship;

    public RelationshipAttribute() {
        super();
    }

    public RelationshipAttribute(Agency updatedBy) {
        super(updatedBy);
    }

    public RelationshipAttribute(Attribute attribute) {
        super(attribute);
    }

    public RelationshipAttribute(Attribute attribute, Agency updatedBy) {
        super(attribute, updatedBy);
    }

    public RelationshipAttribute(Attribute attribute, BigDecimal value,
                                 Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public RelationshipAttribute(Attribute attribute, boolean value,
                                 Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public RelationshipAttribute(Attribute attribute, int value,
                                 Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public RelationshipAttribute(Attribute attribute, String value,
                                 Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public RelationshipAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    public RelationshipAttribute(Long id) {
        super(id);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<? extends AttributeValue<Relationship>, Relationship> getRuleformAttribute() {
        return RelationshipAttribute_.relationship;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<Relationship> getRuleformClass() {
        return Relationship.class;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setRelationship(Relationship interval) {
        relationship = interval;
    }
}
