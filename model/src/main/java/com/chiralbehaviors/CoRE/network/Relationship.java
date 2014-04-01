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
package com.chiralbehaviors.CoRE.network;

import static com.chiralbehaviors.CoRE.Ruleform.NAME_SEARCH_SUFFIX;
import static com.chiralbehaviors.CoRE.network.Relationship.FIND_BY_NAME;
import static com.chiralbehaviors.CoRE.network.Relationship.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS;
import static com.chiralbehaviors.CoRE.network.Relationship.FIND_CLASSIFIED_ATTRIBUTE_VALUES;
import static com.chiralbehaviors.CoRE.network.Relationship.FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS;
import static com.chiralbehaviors.CoRE.network.Relationship.GET_ALL_PARENT_RELATIONSHIPS;
import static com.chiralbehaviors.CoRE.network.Relationship.GET_CHILD;
import static com.chiralbehaviors.CoRE.network.Relationship.GET_CHILD_RULES_BY_RELATIONSHIP;
import static com.chiralbehaviors.CoRE.network.Relationship.ORDERED_ATTRIBUTES;
import static com.chiralbehaviors.CoRE.network.Relationship.UNLINKED;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.NameSearchResult;
import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The existential rule form that defines relationships between existential rule
 * form instances, providing the edge connecting two nodes in a directed graph.
 * 
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "relationship", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "relationship_id_seq", sequenceName = "relationship_id_seq", allocationSize = 1)
@NamedNativeQueries({
                     @NamedNativeQuery(name = UNLINKED, query = "SELECT unlinked.* "
                                                                + "FROM relationship AS unlinked "
                                                                + "JOIN ("
                                                                + "     SELECT id "
                                                                + "     FROM relationship "
                                                                + "     EXCEPT ("
                                                                + "             SELECT distinct(net.child) "
                                                                + "             FROM relationship_network as net "
                                                                + "             WHERE net.parent = relationship_id('Agency') "
                                                                + "             AND relationship = relationship_id('includes') "
                                                                + "     )"
                                                                + ") AS linked ON unlinked.id = linked.id "
                                                                + "WHERE unlinked.id != relationship_id('Agency');", resultClass = Agency.class),
                     // ?1 = :queryString, ?2 = :numberOfMatches
                     @NamedNativeQuery(name = "relationship"
                                              + NAME_SEARCH_SUFFIX, query = "SELECT id, name, description FROM ruleform.existential_name_search('relationship', :queryString, :numberOfMatches)", resultClass = NameSearchResult.class) })
@NamedQueries({
               @NamedQuery(name = ORDERED_ATTRIBUTES, query = "select ca from RelationshipAttribute as ca where ca.relationship = :relationship"),
               @NamedQuery(name = FIND_BY_NAME, query = "select e from Agency e where e.name = :name"),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_VALUES, query = "SELECT "
                                                                            + "  attrValue "
                                                                            + "FROM "
                                                                            + "       RelationshipAttribute attrValue, "
                                                                            + "       RelationshipAttributeAuthorization auth, "
                                                                            + "       RelationshipNetwork network "
                                                                            + "WHERE "
                                                                            + "        auth.authorizedAttribute = attrValue.attribute AND "
                                                                            + "        network.relationship = auth.classification AND "
                                                                            + "        network.child = auth.classifier AND"
                                                                            + "        attrValue.relationship = :ruleform AND "
                                                                            + "        auth.classification = :classification AND "
                                                                            + "        auth.classifier = :classifier "),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS, query = "select ra from RelationshipAttributeAuthorization ra "
                                                                                    + "WHERE ra.classifier = :classification "
                                                                                    + "AND ra.classifier = :classifier"),
               @NamedQuery(name = FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS, query = "select ra from RelationshipAttributeAuthorization ra "
                                                                                 + "WHERE ra.groupingAgency = :groupingAgency"),
               @NamedQuery(name = GET_CHILD, query = "SELECT n.child "
                                                     + "FROM RelationshipNetwork n "
                                                     + "WHERE n.parent = :p "
                                                     + "AND n.relationship = :r"),
               @NamedQuery(name = GET_ALL_PARENT_RELATIONSHIPS, query = "SELECT n "
                                                                        + "FROM RelationshipNetwork n "
                                                                        + "WHERE n.child = :c"),
               @NamedQuery(name = GET_CHILD_RULES_BY_RELATIONSHIP, query = "SELECT n FROM RelationshipNetwork n "
                                                                           + "WHERE n.parent = :relationship "
                                                                           + "AND n.relationship IN :relationships "
                                                                           + "ORDER by n.parent.name, n.relationship.name, n.child.name") })
public class Relationship extends
        ExistentialRuleform<Relationship, RelationshipNetwork> {

    public static final String         AGENCY_ATTRIBUTES_BY_CLASSIFICATION      = "relationship.RelationshipAttributesByClassification";

    public static final String         AUTHORIZED_AGENCY_ATTRIBUTES             = "relationship.authorizedAttributes";
    public static final String         FIND_BY_NAME                             = "relationship"
                                                                                  + FIND_BY_NAME_SUFFIX;
    public static final String         FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS = "relationship"
                                                                                  + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
    public static final String         FIND_CLASSIFIED_ATTRIBUTE_VALUES         = "relationship"
                                                                                  + FIND_CLASSIFIED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String         FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS    = "relationship"
                                                                                  + FIND_GROUPED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String         GET_ALL_PARENT_RELATIONSHIPS             = "relationship"
                                                                                  + GET_ALL_PARENT_RELATIONSHIPS_SUFFIX;
    public static final String         GET_CHILD                                = "relationship"
                                                                                  + GET_CHILDREN_SUFFIX;
    public static final String         GET_CHILD_RULES_BY_RELATIONSHIP          = "relationship"
                                                                                  + GET_CHILD_RULES_BY_RELATIONSHIP_SUFFIX;
    public static final String         ORDERED_ATTRIBUTES                       = "relationship.orderedAttributes";
    public static final String         QUALIFIED_ENTITY_NETWORK_RULES           = "relationship.qualifiedEntityNetworkRules";
    public static final String         UNLINKED                                 = "relationship"
                                                                                  + UNLINKED_SUFFIX;

    private static final long          serialVersionUID                         = 1L;

    @OneToMany(mappedBy = "relationship", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<RelationshipAttribute> attributes;

    @Id
    @GeneratedValue(generator = "relationship_id_seq", strategy = GenerationType.SEQUENCE)
    private Long                       id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "inverse")
    @JsonIgnore
    private Relationship               inverse;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<RelationshipNetwork>   networkByChild;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<RelationshipNetwork>   networkByParent;

    private String                     operator;

    private Integer                    preferred                                = FALSE;

    public Relationship() {
    }

    /**
     * @param updatedBy
     */
    public Relationship(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param id
     */
    public Relationship(Long id) {
        super(id);
    }

    /**
     * @param name
     */
    public Relationship(String name) {
        super(name);
    }

    /**
     * @param name
     * @param updatedBy
     */
    public Relationship(String name, Agency updatedBy) {
        super(name, updatedBy);
    }

    /**
     * @param name
     * @param description
     */
    public Relationship(String name, String description) {
        super(name, description);
    }

    /**
     * @param name
     * @param description
     * @param updatedBy
     */
    public Relationship(String name, String description, Agency updatedBy) {
        super(name, description, updatedBy);
    }

    /**
     * @param name
     * @param description
     * @param updatedBy
     * @param preferred
     */
    public Relationship(String name, String description, Agency updatedBy,
                        boolean preferred) {
        super(name, description, updatedBy);
        setPreferred(preferred);
    }

    /**
     * @param name
     * @param description
     * @param updatedBy
     * @param inverse
     */
    public Relationship(String name, String description, Agency updatedBy,
                        Relationship inverse) {
        super(name, description, updatedBy);
        setInverse(inverse);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.ExistentialRuleform#addChildRelationship(com
     * .chiralbehaviors.CoRE.network.NetworkRuleform)
     */
    @Override
    public void addChildRelationship(RelationshipNetwork relationship) {
        relationship.setChild(this);
        networkByChild.add(relationship);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.ExistentialRuleform#addParentRelationship(com
     * .chiralbehaviors.CoRE.network.NetworkRuleform)
     */
    @Override
    public void addParentRelationship(RelationshipNetwork relationship) {
        relationship.setParent(this);
        networkByChild.add(relationship);
    }

    @Override
    public Relationship clone() {
        Relationship clone = (Relationship) super.clone();
        clone.networkByChild = null;
        clone.networkByParent = null;
        clone.attributes = null;
        return clone;
    }

    public Set<RelationshipAttribute> getAttributes() {
        return attributes;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Relationship getInverse() {
        return inverse;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkByChild()
     */
    @Override
    public Set<RelationshipNetwork> getNetworkByChild() {
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
    public Set<RelationshipNetwork> getNetworkByParent() {
        if (networkByParent == null) {
            return Collections.emptySet();
        }
        return networkByParent;
    }

    public String getOperator() {
        return operator;
    }

    public Boolean getPreferred() {
        return toBoolean(preferred);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.ExistentialRuleform#link(com.chiralbehaviors
     * .CoRE.network.Relationship, com.chiralbehaviors.CoRE.ExistentialRuleform,
     * com.chiralbehaviors.CoRE.agency.Agency,
     * com.chiralbehaviors.CoRE.agency.Agency, javax.persistence.EntityManager)
     */
    @Override
    public void link(Relationship r, Relationship child, Agency updatedBy,
                     Agency inverseSoftware, EntityManager em) {
        assert r != null : "Relationship cannot be null";
        assert child != null;
        assert updatedBy != null;
        assert em != null;

        RelationshipNetwork link = new RelationshipNetwork(this, r, child,
                                                           updatedBy);
        em.persist(link);
        RelationshipNetwork inverse = new RelationshipNetwork(child,
                                                              r.getInverse(),
                                                              this,
                                                              inverseSoftware);
        em.persist(inverse);
    }

    public void setAttributes(Set<RelationshipAttribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setInverse(Relationship relationship) {
        inverse = relationship;
        relationship.inverse = this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.ExistentialRuleform#setNetworkByChild(java.util
     * .Set)
     */
    @Override
    public void setNetworkByChild(Set<RelationshipNetwork> theNetworkByChild) {
        networkByChild = theNetworkByChild;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.ExistentialRuleform#setNetworkByParent(java.
     * util.Set)
     */
    @Override
    public void setNetworkByParent(Set<RelationshipNetwork> theNetworkByParent) {
        networkByParent = theNetworkByParent;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setPreferred(Boolean preferred) {
        this.preferred = toInteger(preferred);
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
        if (inverse != null) {
            inverse = (Relationship) inverse.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }

}