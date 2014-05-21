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
package com.chiralbehaviors.CoRE.coordinate;

import static com.chiralbehaviors.CoRE.Ruleform.NAME_SEARCH_SUFFIX;
import static com.chiralbehaviors.CoRE.coordinate.Coordinate.FIND_BY_NAME;
import static com.chiralbehaviors.CoRE.coordinate.Coordinate.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS;
import static com.chiralbehaviors.CoRE.coordinate.Coordinate.FIND_CLASSIFIED_ATTRIBUTE_VALUES;
import static com.chiralbehaviors.CoRE.coordinate.Coordinate.FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS;
import static com.chiralbehaviors.CoRE.coordinate.Coordinate.GET_ALL_PARENT_RELATIONSHIPS;
import static com.chiralbehaviors.CoRE.coordinate.Coordinate.GET_CHILD;
import static com.chiralbehaviors.CoRE.coordinate.Coordinate.GET_CHILD_RULES_BY_RELATIONSHIP;
import static com.chiralbehaviors.CoRE.coordinate.Coordinate.NESTING_QUERY;
import static com.chiralbehaviors.CoRE.coordinate.Coordinate.ORDERED_ATTRIBUTES;
import static com.chiralbehaviors.CoRE.coordinate.Coordinate.UNLINKED;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.NameSearchResult;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attributable;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The location coordinate.
 * 
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "coordinate", schema = "ruleform")
@NamedNativeQueries({
                     @NamedNativeQuery(name = UNLINKED, query = "SELECT unlinked.* "
                                                                + "FROM coordinate AS unlinked "
                                                                + "JOIN ("
                                                                + "     SELECT id "
                                                                + "     FROM coordinate "
                                                                + "     EXCEPT ("
                                                                + "             SELECT distinct(net.child) "
                                                                + "             FROM coordinate_network as net "
                                                                + "             WHERE net.parent = coordinate_id('Agency') "
                                                                + "             AND relationship = relationship_id('includes') "
                                                                + "     )"
                                                                + ") AS linked ON unlinked.id = linked.id "
                                                                + "WHERE unlinked.id != coordinate_id('Agency');", resultClass = Agency.class),
                     // ?1 = :queryString, ?2 = :numberOfMatches
                     @NamedNativeQuery(name = "coordinate" + NAME_SEARCH_SUFFIX, query = "SELECT id, name, description FROM ruleform.existential_name_search('coordinate', :queryString, :numberOfMatches)", resultClass = NameSearchResult.class),
                     // ?1 = #inner, ?2 = #outer
                     @NamedNativeQuery(name = NESTING_QUERY, query = "SELECT * FROM ruleform.nest_coordinates(?1, ?2)", resultClass = Coordinate.class) })
@NamedQueries({
               @NamedQuery(name = ORDERED_ATTRIBUTES, query = "select ca from CoordinateAttribute as ca where ca.coordinate = :coordinate"),
               @NamedQuery(name = FIND_BY_NAME, query = "select e from Agency e where e.name = :name"),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_VALUES, query = "SELECT "
                                                                            + "  attrValue "
                                                                            + "FROM "
                                                                            + "       CoordinateAttribute attrValue, "
                                                                            + "       CoordinateAttributeAuthorization auth, "
                                                                            + "       CoordinateNetwork network "
                                                                            + "WHERE "
                                                                            + "        auth.authorizedAttribute = attrValue.attribute AND "
                                                                            + "        network.relationship = auth.classification AND "
                                                                            + "        network.child = auth.classifier AND"
                                                                            + "        attrValue.coordinate = :ruleform AND "
                                                                            + "        auth.classification = :classification AND "
                                                                            + "        auth.classifier = :classifier "),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS, query = "select ra from CoordinateAttributeAuthorization ra "
                                                                                    + "WHERE ra.classifier = :classification "
                                                                                    + "AND ra.classifier = :classifier"),
               @NamedQuery(name = FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS, query = "select ra from CoordinateAttributeAuthorization ra "
                                                                                 + "WHERE ra.groupingAgency = :groupingAgency"),
               @NamedQuery(name = GET_CHILD, query = "SELECT n.child "
                                                     + "FROM CoordinateNetwork n "
                                                     + "WHERE n.parent = :p "
                                                     + "AND n.relationship = :r"),
               @NamedQuery(name = GET_ALL_PARENT_RELATIONSHIPS, query = "SELECT n "
                                                                        + "FROM CoordinateNetwork n "
                                                                        + "WHERE n.child = :c"),
               @NamedQuery(name = GET_CHILD_RULES_BY_RELATIONSHIP, query = "SELECT n FROM CoordinateNetwork n "
                                                                           + "WHERE n.parent = :coordinate "
                                                                           + "AND n.relationship IN :relationships "
                                                                           + "ORDER by n.parent.name, n.relationship.name, n.child.name") })
public class Coordinate extends
        ExistentialRuleform<Coordinate, CoordinateNetwork> implements
        Attributable<CoordinateAttribute> {

    public static final String       AGENCY_ATTRIBUTES_BY_CLASSIFICATION      = "coordinate.CoordinateAttributesByClassification";

    public static final String       AUTHORIZED_AGENCY_ATTRIBUTES             = "coordinate.authorizedAttributes";
    public static final String       FIND_BY_NAME                             = "coordinate"
                                                                                + FIND_BY_NAME_SUFFIX;
    public static final String       FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS = "coordinate"
                                                                                + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
    public static final String       FIND_CLASSIFIED_ATTRIBUTE_VALUES         = "coordinate"
                                                                                + FIND_CLASSIFIED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String       FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS    = "coordinate"
                                                                                + FIND_GROUPED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String       GET_ALL_PARENT_RELATIONSHIPS             = "coordinate"
                                                                                + GET_ALL_PARENT_RELATIONSHIPS_SUFFIX;
    public static final String       GET_CHILD                                = "coordinate"
                                                                                + GET_CHILDREN_SUFFIX;
    public static final String       GET_CHILD_RULES_BY_RELATIONSHIP          = "coordinate"
                                                                                + GET_CHILD_RULES_BY_RELATIONSHIP_SUFFIX;
    public static final String       NESTING_QUERY                            = "coordinate.nestCoordinates";
    public static final String       ORDERED_ATTRIBUTES                       = "coordinate.orderedAttributes";
    public static final String       QUALIFIED_ENTITY_NETWORK_RULES           = "coordinate.qualifiedEntityNetworkRules";

    public static final String       UNLINKED                                 = "coordinate"
                                                                                + UNLINKED_SUFFIX;
    private static final long        serialVersionUID                         = 1L;

    // bi-directional many-to-one association to CoordinateAttribute
    @OneToMany(mappedBy = "coordinate")
    @JsonIgnore
    private Set<CoordinateAttribute> attributes;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<CoordinateNetwork>   networkByChild;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<CoordinateNetwork>   networkByParent;

    public Coordinate() {
    }

    /**
     * @param updatedBy
     */
    public Coordinate(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param id
     */
    public Coordinate(UUID id) {
        super(id);
    }

    /**
     * @param name
     */
    public Coordinate(String name) {
        super(name);
    }

    /**
     * @param name
     * @param updatedBy
     */
    public Coordinate(String name, Agency updatedBy) {
        super(name, updatedBy);
    }

    /**
     * @param name
     * @param description
     */
    public Coordinate(String name, String description) {
        super(name, description);
    }

    /**
     * @param name
     * @param description
     * @param updatedBy
     */
    public Coordinate(String name, String description, Agency updatedBy) {
        super(name, description, updatedBy);
    }

    public void addAttribute(CoordinateAttribute attribute) {
        attribute.setCoordinate(this);
        attributes.add(attribute);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.ExistentialRuleform#addChildRelationship(com
     * .chiralbehaviors .CoRE.network.NetworkRuleform)
     */
    @Override
    public void addChildRelationship(CoordinateNetwork relationship) {
        relationship.setChild(this);
        networkByChild.add(relationship);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.ExistentialRuleform#addParentRelationship(com
     * .chiralbehaviors .CoRE.network.NetworkRuleform)
     */
    @Override
    public void addParentRelationship(CoordinateNetwork relationship) {
        relationship.setParent(this);
        networkByParent.add(relationship);
    }

    @Override
    public Coordinate clone() {
        Coordinate clone = (Coordinate) super.clone();
        clone.networkByChild = null;
        clone.networkByParent = null;
        clone.attributes = null;
        return clone;
    }

    @Override
    public Set<CoordinateAttribute> getAttributes() {
        return attributes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.attribute.Attributable#getAttributeType()
     */
    @Override
    public Class<CoordinateAttribute> getAttributeType() {
        return CoordinateAttribute.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkByChild()
     */
    @Override
    public Set<CoordinateNetwork> getNetworkByChild() {
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
    public Set<CoordinateNetwork> getNetworkByParent() {
        if (networkByParent == null) {
            return Collections.emptySet();
        }
        return networkByParent;
    }

    /**
     * <p>
     * Returns a List of <code>coordinate</code>'s CoordinateAttributes (as
     * obtained from {@link Coordinate#getAttributes()}, but ordered according
     * to the declaration of this Coordinate's CoordinateKind definition rules
     * (e.g. {@link CoordinateKindDefinition})
     * </p>
     * 
     * @return a {@code List<CoordinateAttribute>} containing the attributes of
     *         the receiver, ordered according to the definition rules for this
     *         kind of Coordinate.
     */
    public List<CoordinateAttribute> getOrderedAttributes(EntityManager em) {
        return em.createNamedQuery(ORDERED_ATTRIBUTES,
                                   CoordinateAttribute.class).setParameter("coordinate",
                                                                           this).getResultList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.ExistentialRuleform#link(com.chiralbehaviors
     * .CoRE.network .Relationship,
     * com.chiralbehaviors.CoRE.ExistentialRuleform,
     * com.chiralbehaviors.CoRE.agency.Agency,
     * com.chiralbehaviors.CoRE.agency.Agency, javax.persistence.EntityManager)
     */
    @Override
    public void link(Relationship r, Coordinate child, Agency updatedBy,
                     Agency inverseSoftware, EntityManager em) {
        assert r != null : "Relationship cannot be null";
        assert child != null;
        assert updatedBy != null;
        assert em != null;

        CoordinateNetwork link = new CoordinateNetwork(this, r, child,
                                                       updatedBy);
        em.persist(link);
        CoordinateNetwork inverse = new CoordinateNetwork(child,
                                                          r.getInverse(), this,
                                                          inverseSoftware);
        em.persist(inverse);
    }

    /**
     * Computes a Coordinate that represents the inner coordinate relative to
     * this outer coordinate. For example if <code>outer</code> represents a
     * nucleotide region from bases 123&#8211;456 on some DNA molecule
     * <em>X</em>, and <code>inner</code> represents a region extending from
     * bases 20&#8211;30 of <code>outer</code>, then this method will return a
     * Coordinate representing the region from 143&#8211;153 of DNA molecule
     * <em>X</em>. If such a Coordinate already exists in the database, it is
     * returned, otherwise a new Coordinate is created and it is returned.
     * 
     * @param outer
     *            the second Coordinate. Can be relative to anything, but should
     *            contain the receiver coordinate
     * @return Coordinate representing the location of <code>inner</code>
     *         relative to the receiver coordinate
     */
    public Coordinate nestCoordinates(EntityManager em, Coordinate outer) {
        return em.createNamedQuery(NESTING_QUERY, Coordinate.class).setParameter("inner",
                                                                                 this).setParameter("outer",
                                                                                                    outer).getSingleResult();
    }

    @Override
    public void setAttributes(Set<CoordinateAttribute> coordinateAttributes) {
        attributes = coordinateAttributes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.ExistentialRuleform#setNetworkByChild(java.util
     * .Set)
     */
    @Override
    public void setNetworkByChild(Set<CoordinateNetwork> theNetworkByChild) {
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
    public void setNetworkByParent(Set<CoordinateNetwork> theNetworkByParent) {
        networkByParent = theNetworkByParent;
    }
}