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
package com.chiralbehaviors.CoRE.attribute.unit;

import static com.chiralbehaviors.CoRE.attribute.unit.Unit.FIND_BY_NAME;
import static com.chiralbehaviors.CoRE.attribute.unit.Unit.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS;
import static com.chiralbehaviors.CoRE.attribute.unit.Unit.FIND_CLASSIFIED_ATTRIBUTE_VALUES;
import static com.chiralbehaviors.CoRE.attribute.unit.Unit.GET_CHILD;
import static com.chiralbehaviors.CoRE.attribute.unit.Unit.GET_CHILD_RULES_BY_RELATIONSHIP;
import static com.chiralbehaviors.CoRE.attribute.unit.Unit.NAME_SEARCH;
import static com.chiralbehaviors.CoRE.attribute.unit.Unit.UNLINKED;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
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
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The attribute unit.
 * 
 * @author hhildebrand
 * 
 */
@NamedQueries({
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_VALUES, query = "SELECT "
                                                                            + "  attrValue "
                                                                            + "FROM "
                                                                            + "       UnitAttribute attrValue, "
                                                                            + "       UnitAttributeAuthorization auth, "
                                                                            + "       UnitNetwork network "
                                                                            + "WHERE "
                                                                            + "        auth.authorizedAttribute = attrValue.attribute AND "
                                                                            + "        network.relationship = auth.classification AND "
                                                                            + "        network.child = auth.classifier AND"
                                                                            + "        attrValue.attribute = :ruleform AND "
                                                                            + "        auth.classification = :classification AND "
                                                                            + "        auth.classifier = :classifier "),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS, query = "select ama from UnitAttributeAuthorization ama "
                                                                                    + "WHERE ama.classification = :classification "
                                                                                    + "AND ama.classifier = :classifier"),
               @NamedQuery(name = FIND_BY_NAME, query = "select e from Attribute e where e.name = :name"),
               @NamedQuery(name = GET_CHILD, query = "SELECT rn.child "
                                                     + "FROM UnitNetwork rn "
                                                     + "WHERE rn.parent = :parent "
                                                     + "AND rn.relationship = :relationship"),
               @NamedQuery(name = GET_CHILD_RULES_BY_RELATIONSHIP, query = "SELECT n FROM UnitNetwork n "
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
                                                                + "FROM unit_network as net "
                                                                + "WHERE net.parent = attribute_id('Attribute') "
                                                                + "AND relationship = relationship_id('includes') "
                                                                + ")"
                                                                + ") AS linked ON unlinked.id = linked.id "
                                                                + "WHERE unlinked.id != attribute_id('Attribute');", resultClass = Attribute.class),
                     // ?1 = :queryString, ?2 = :numberOfMatches
                     @NamedNativeQuery(name = NAME_SEARCH, query = "SELECT id, name, description FROM ruleform.existential_name_search('attribute', ?1, ?2)", resultClass = NameSearchResult.class) })
@Entity
@Table(name = "unit", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "unit_id_seq", sequenceName = "unit_id_seq")
public class Unit extends ExistentialRuleform<Unit, UnitNetwork> {
    public static final String FIND_BY_NAME                             = "unit.findByName";
    public static final String FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS = "unit"
                                                                          + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
    public static final String FIND_CLASSIFIED_ATTRIBUTE_VALUES         = "unit"
                                                                          + FIND_CLASSIFIED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String GET_CHILD                                = "unit"
                                                                          + GET_CHILDREN_SUFFIX;
    public static final String GET_CHILD_RULES_BY_RELATIONSHIP          = "unit"
                                                                          + GET_CHILD_RULES_BY_RELATIONSHIP_SUFFIX;
    public static final String NAME_SEARCH                              = "unit"
                                                                          + NAME_SEARCH_SUFFIX;
    public static final String UNLINKED                                 = "unit"
                                                                          + UNLINKED_SUFFIX;
    private static final long  serialVersionUID                         = 1L;

    private String             abbreviation;

    @OneToMany(mappedBy = "unitRf", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<UnitAttribute> attributes;

    private String             datatype;

    private Integer            enumerated                               = FALSE;

    @Id
    @GeneratedValue(generator = "unit_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    private BigDecimal         max;

    private BigDecimal         min;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<UnitNetwork>   networkByChild;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<UnitNetwork>   networkByParent;

    public Unit() {
    }

    public Unit(Agency updatedBy) {
        super(updatedBy);
    }

    public Unit(Long id) {
        super(id);
    }

    public Unit(Long id, Agency updatedBy) {
        super(id, updatedBy);
    }

    public Unit(String name) {
        setName(name);
    }

    public Unit(String name, Agency updatedBy) {
        super(updatedBy);
        setName(name);
    }

    public Unit(String name, String description, Agency updatedBy) {
        super(updatedBy);
        setName(name);
        setDescription(description);
    }

    public Unit(String name, String description, String notes, Agency updatedBy) {
        super(notes, updatedBy);
        setName(name);
        setDescription(description);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.ExistentialRuleform#addChildRelationship(com.chiralbehaviors
     * .CoRE.network.NetworkRuleform)
     */
    @Override
    public void addChildRelationship(UnitNetwork relationship) {
        relationship.setChild(this);
        networkByChild.add(relationship);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.ExistentialRuleform#addParentRelationship(com.chiralbehaviors
     * .CoRE.network.NetworkRuleform)
     */
    @Override
    public void addParentRelationship(UnitNetwork relationship) {
        relationship.setParent(this);
        networkByParent.add(relationship);
    }

    @Override
    public Unit clone() {
        Unit clone = (Unit) super.clone();
        clone.networkByChild = null;
        clone.networkByParent = null;
        clone.attributes = null;
        return clone;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public Set<UnitAttribute> getAttributes() {
        return attributes;
    }

    public String getDatatype() {
        return datatype;
    }

    public Boolean getEnumerated() {
        return toBoolean(enumerated);
    }

    @Override
    public Long getId() {
        return id;
    }

    public BigDecimal getMax() {
        return max;
    }

    public BigDecimal getMin() {
        return min;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkByChild()
     */
    @Override
    public Set<UnitNetwork> getNetworkByChild() {
        if (networkByChild == null) {
            return Collections.emptySet();
        }
        return networkByChild;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkByParent()
     */
    @Override
    public Set<UnitNetwork> getNetworkByParent() {
        if (networkByParent == null) {
            return Collections.emptySet();
        }
        return networkByParent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.ExistentialRuleform#link(com.chiralbehaviors.CoRE.network
     * .Relationship, com.chiralbehaviors.CoRE.ExistentialRuleform,
     * com.chiralbehaviors.CoRE.agency.Agency, com.chiralbehaviors.CoRE.agency.Agency,
     * javax.persistence.EntityManager)
     */
    @Override
    public void link(Relationship r, Unit child, Agency updatedBy,
                     Agency inverseSoftware, EntityManager em) {
        assert r != null : "Relationship cannot be null";
        assert child != null;
        assert updatedBy != null;
        assert em != null;

        UnitNetwork link = new UnitNetwork(this, r, child, updatedBy);
        em.persist(link);
        UnitNetwork inverse = new UnitNetwork(child, r.getInverse(), this,
                                              inverseSoftware);
        em.persist(inverse);
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public void setAttributes(Set<UnitAttribute> attributes) {
        this.attributes = attributes;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public void setEnumerated(Boolean enumerated) {
        this.enumerated = toInteger(enumerated);
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setMax(BigDecimal max) {
        this.max = max;
    }

    public void setMin(BigDecimal min) {
        this.min = min;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.ExistentialRuleform#setNetworkByChild(java.util.Set)
     */
    @Override
    public void setNetworkByChild(Set<UnitNetwork> theNetworkByChild) {
        networkByChild = theNetworkByChild;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.ExistentialRuleform#setNetworkByParent(java.util.Set)
     */
    @Override
    public void setNetworkByParent(Set<UnitNetwork> theNetworkByParent) {
        networkByParent = theNetworkByParent;
    }
}