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
package com.chiralbehaviors.CoRE.attribute;

import static com.chiralbehaviors.CoRE.attribute.Attribute.FIND_BY_NAME;
import static com.chiralbehaviors.CoRE.attribute.Attribute.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS;
import static com.chiralbehaviors.CoRE.attribute.Attribute.FIND_CLASSIFIED_ATTRIBUTE_VALUES;
import static com.chiralbehaviors.CoRE.attribute.Attribute.GET_CHILD;
import static com.chiralbehaviors.CoRE.attribute.Attribute.GET_CHILD_RULES_BY_RELATIONSHIP;
import static com.chiralbehaviors.CoRE.attribute.Attribute.NAME_SEARCH;
import static com.chiralbehaviors.CoRE.attribute.Attribute.UNLINKED;

import java.util.Collections;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.NameSearchResult;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Existential ruleform for all attributes in the CoRE database. This table
 * defines and describes all attributes.
 * 
 * @author hhildebrand
 * 
 */
@NamedQueries({
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_VALUES, query = "SELECT "
                                                                            + "  attrValue "
                                                                            + "FROM "
                                                                            + "       AttributeMetaAttribute attrValue, "
                                                                            + "       AttributeMetaAttributeAuthorization auth, "
                                                                            + "       AttributeNetwork network "
                                                                            + "WHERE "
                                                                            + "        auth.authorizedAttribute = attrValue.attribute AND "
                                                                            + "        network.relationship = auth.classification AND "
                                                                            + "        network.child = auth.classifier AND"
                                                                            + "        attrValue.attribute = :ruleform AND "
                                                                            + "        auth.classification = :classification AND "
                                                                            + "        auth.classifier = :classifier "),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS, query = "select ama from AttributeMetaAttributeAuthorization ama "
                                                                                    + "WHERE ama.classification = :classification "
                                                                                    + "AND ama.classifier = :classifier"),
               @NamedQuery(name = FIND_BY_NAME, query = "select e from Attribute e where e.name = :name"),
               @NamedQuery(name = GET_CHILD, query = "SELECT rn.child "
                                                     + "FROM AttributeNetwork rn "
                                                     + "WHERE rn.parent = :parent "
                                                     + "AND rn.relationship = :relationship"),
               @NamedQuery(name = GET_CHILD_RULES_BY_RELATIONSHIP, query = "SELECT n FROM AttributeNetwork n "
                                                                           + "WHERE n.parent = :attribute "
                                                                           + "AND n.relationship IN :relationships "
                                                                           + "ORDER by n.parent.name, n.relationship.name, n.child.name") })
@NamedNativeQueries({
                     @NamedNativeQuery(name = UNLINKED, query = "SELECT unlinked.* "
                                                                + "FROM attribute AS unlinked "
                                                                + "JOIN ("
                                                                + "SELECT id "
                                                                + "FROM attribute "
                                                                + "EXCEPT ("
                                                                + "SELECT distinct(net.child) "
                                                                + "FROM attribute_network as net "
                                                                + "WHERE net.parent = attribute_id('Attribute') "
                                                                + "AND relationship = relationship_id('includes') "
                                                                + ")"
                                                                + ") AS linked ON unlinked.id = linked.id "
                                                                + "WHERE unlinked.id != attribute_id('Attribute');", resultClass = Attribute.class),
                     // ?1 = :queryString, ?2 = :numberOfMatches
                     @NamedNativeQuery(name = NAME_SEARCH, query = "SELECT id, name, description FROM ruleform.existential_name_search('attribute', ?1, ?2)", resultClass = NameSearchResult.class) })
@Entity
@Table(name = "attribute", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "attribute_id_seq", sequenceName = "attribute_id_seq")
public class Attribute extends ExistentialRuleform<Attribute, AttributeNetwork>
        implements Attributable<AttributeMetaAttribute> {
    public static final String          FIND_BY_NAME                             = "attribute.findByName";
    public static final String          FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS = "attribute"
                                                                                   + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
    public static final String          FIND_CLASSIFIED_ATTRIBUTE_VALUES         = "attribute"
                                                                                   + FIND_CLASSIFIED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String          GET_CHILD                                = "attribute"
                                                                                   + GET_CHILDREN_SUFFIX;
    public static final String          GET_CHILD_RULES_BY_RELATIONSHIP          = "attribute"
                                                                                   + GET_CHILD_RULES_BY_RELATIONSHIP_SUFFIX;
    public static final String          NAME_SEARCH                              = "attribute"
                                                                                   + NAME_SEARCH_SUFFIX;
    public static final String          UNLINKED                                 = "attribute"
                                                                                   + UNLINKED_SUFFIX;
    private static final long           serialVersionUID                         = 1L;

    // bi-directional many-to-one association to AttributeMetaAttribute
    @OneToMany(mappedBy = "attribute")
    @JsonIgnore
    private Set<AttributeMetaAttribute> attributes;

    @Id
    @GeneratedValue(generator = "attribute_id_seq", strategy = GenerationType.SEQUENCE)
    private Long                        id;

    private Integer                     inheritable                              = FALSE;

    // bi-directional many-to-one association to AttributeNetwork
    @JsonIgnore
    @OneToMany(mappedBy = "child")
    private Set<AttributeNetwork>       networkByChild;

    // bi-directional many-to-one association to AttributeNetwork
    @JsonIgnore
    @OneToMany(mappedBy = "parent")
    private Set<AttributeNetwork>       networkByParent;

    @Column(name = "value_type")
    @Enumerated(EnumType.ORDINAL)
    private ValueType                   valueType;

    public Attribute() {
    }

    /**
     * @param updatedBy
     */
    public Attribute(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param id
     */
    public Attribute(Long id) {
        super(id);
    }

    /**
     * @param name
     */
    public Attribute(String name) {
        super(name);
    }

    /**
     * @param name
     * @param updatedBy
     */
    public Attribute(String name, Agency updatedBy) {
        super(name, updatedBy);
    }

    /**
     * @param name
     * @param description
     */
    public Attribute(String name, String description) {
        super(name, description);
    }

    /**
     * @param name
     * @param description
     * @param updatedBy
     */
    public Attribute(String name, String description, Agency updatedBy) {
        super(name, description, updatedBy);
    }

    /**
     * @param name
     * @param description
     * @param updatedBy
     * @param valueType
     */
    public Attribute(String name, String description, Agency updatedBy,
                     ValueType valueType) {
        super(name, description, updatedBy);
        setValueType(valueType);
    }

    /**
     * @param name
     * @param description
     * @param updatedBy
     */
    public Attribute(String name, String description, ValueType valueType,
                     Agency updatedBy) {
        this(name, description, updatedBy);
        this.valueType = valueType;
    }

    public void addAttribute(AttributeMetaAttribute attribute) {
        attribute.setAttribute(this);
        attributes.add(attribute);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.Networked#addChildRelationship(com.chiralbehaviors.CoRE
     * .NetworkRuleform)
     */
    @Override
    public void addChildRelationship(AttributeNetwork relationship) {
        relationship.setChild(this);
        networkByChild.add(relationship);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.Networked#addParentRelationship(com.chiralbehaviors.CoRE
     * .NetworkRuleform)
     */
    @Override
    public void addParentRelationship(AttributeNetwork relationship) {
        relationship.setParent(this);
        networkByParent.add(relationship);
    }

    @Override
    public Attribute clone() {
        Attribute clone = (Attribute) super.clone();
        clone.attributes = null;
        clone.networkByChild = null;
        clone.networkByParent = null;
        return clone;
    }

    @Override
    public Set<AttributeMetaAttribute> getAttributes() {
        return attributes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.attribute.Attributable#getAttributeType()
     */
    @Override
    public Class<AttributeMetaAttribute> getAttributeType() {
        return AttributeMetaAttribute.class;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Boolean getInheritable() {
        return toBoolean(inheritable);
    }

    @Override
    public Set<AttributeNetwork> getNetworkByChild() {
        if (networkByChild == null) {
            return Collections.emptySet();
        }
        return networkByChild;
    }

    @Override
    public Set<AttributeNetwork> getNetworkByParent() {
        if (networkByParent == null) {
            return Collections.emptySet();
        }
        return networkByParent;
    }

    public ValueType getValueType() {
        return valueType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.network.Networked#link(com.chiralbehaviors.CoRE.network
     * .Relationship, com.chiralbehaviors.CoRE.network.Networked,
     * com.chiralbehaviors.CoRE.agency.Agency, javax.persistence.EntityManager)
     */
    @Override
    public void link(Relationship r, Attribute child, Agency updatedBy,
                     Agency inverseSoftware, EntityManager em) {
        AttributeNetwork link = new AttributeNetwork(this, r, child, updatedBy);
        em.persist(link);
        AttributeNetwork inverse = new AttributeNetwork(child, r.getInverse(),
                                                        this, inverseSoftware);
        em.persist(inverse);
    }

    @Override
    public void setAttributes(Set<AttributeMetaAttribute> attributeMetaAttributes1) {
        attributes = attributeMetaAttributes1;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setInheritable(Boolean inheritable) {
        this.inheritable = toInteger(inheritable);
    }

    @Override
    public void setNetworkByChild(Set<AttributeNetwork> children) {
        networkByChild = children;
    }

    @Override
    public void setNetworkByParent(Set<AttributeNetwork> parent) {
        networkByParent = parent;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

}