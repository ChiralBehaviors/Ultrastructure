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
package com.hellblazer.CoRE.attribute;

import static com.hellblazer.CoRE.attribute.Attribute.FIND_BY_NAME;
import static com.hellblazer.CoRE.attribute.Attribute.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS;
import static com.hellblazer.CoRE.attribute.Attribute.FIND_CLASSIFIED_ATTRIBUTE_VALUES;
import static com.hellblazer.CoRE.attribute.Attribute.GET_CHILD;
import static com.hellblazer.CoRE.attribute.Attribute.GET_CHILD_RULES_BY_RELATIONSHIP;
import static com.hellblazer.CoRE.attribute.Attribute.NAME_SEARCH;
import static com.hellblazer.CoRE.attribute.Attribute.UNLINKED;
import static com.hellblazer.CoRE.attribute.AttributeNetwork.IMMEDIATE_CHILDREN_NETWORK_RULES;

import java.util.Collections;
import java.util.List;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.NameSearchResult;
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.network.Relationship;

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
    public static final String          UNLINKED                                 = "attribute.unlinked";
    private static final long           serialVersionUID                         = 1L;

    // bi-directional many-to-one association to AttributeMetaAttribute
    @OneToMany(mappedBy = "attribute")
    @JsonIgnore
    private Set<AttributeMetaAttribute> attributes;

    @Id
    @GeneratedValue(generator = "attribute_id_seq", strategy = GenerationType.SEQUENCE)
    private Long                        id;

    private Boolean                     inheritable                              = false;

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
     * com.hellblazer.CoRE.Networked#addChildRelationship(com.hellblazer.CoRE
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
     * com.hellblazer.CoRE.Networked#addParentRelationship(com.hellblazer.CoRE
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
     * @see com.hellblazer.CoRE.attribute.Attributable#getAttributeType()
     */
    @Override
    public Class<AttributeMetaAttribute> getAttributeType() {
        return AttributeMetaAttribute.class;
    }

    @Override
    public Long getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.Networked#getImmediateChildren()
     */
    @Override
    public List<AttributeNetwork> getImmediateChildren(EntityManager em) {

        return em.createNamedQuery(IMMEDIATE_CHILDREN_NETWORK_RULES,
                                   AttributeNetwork.class).setParameter("attribute",
                                                                        this).getResultList();
    }

    public Boolean getInheritable() {
        return inheritable;
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
     * com.hellblazer.CoRE.network.Networked#link(com.hellblazer.CoRE.network
     * .Relationship, com.hellblazer.CoRE.network.Networked,
     * com.hellblazer.CoRE.agency.Agency, javax.persistence.EntityManager)
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
        this.inheritable = inheritable;
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