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
package com.hellblazer.CoRE.coordinate;

import static com.hellblazer.CoRE.Ruleform.NAME_SEARCH_SUFFIX;
import static com.hellblazer.CoRE.coordinate.Coordinate.NESTING_QUERY;
import static com.hellblazer.CoRE.coordinate.Coordinate.ORDERED_ATTRIBUTES;

import java.util.List;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.NameSearchResult;
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.attribute.Attributable;
import com.hellblazer.CoRE.network.Relationship;

/**
 * The location coordinate.
 * 
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "coordinate", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "coordinate_id_seq", sequenceName = "coordinate_id_seq")
@NamedQueries({ @NamedQuery(name = ORDERED_ATTRIBUTES, query = "select ca from CoordinateAttribute as ca where ca.coordinate = :coordinate") })
@NamedNativeQueries({
// ?1 = #inner, ?2 = #outer
                     @NamedNativeQuery(name = NESTING_QUERY, query = "SELECT * FROM ruleform.nest_coordinates(?1, ?2)", resultClass = Coordinate.class),
                     // ?1 = :queryString, ?2 = :numberOfMatches
                     @NamedNativeQuery(name = "coordinate" + NAME_SEARCH_SUFFIX, query = "SELECT id, name, description FROM ruleform.existential_name_search('coordinate', ?1, ?2)", resultClass = NameSearchResult.class) })
public class Coordinate extends
        ExistentialRuleform<Coordinate, CoordinateNetwork> implements
        Attributable<CoordinateAttribute> {
    private static final long        serialVersionUID                 = 1L;
    public static final String       IMMEDIATE_CHILDREN_NETWORK_RULES = "interval.immediateChildrenNetworkRules";

    public static final String       NESTING_QUERY                    = "coordinate.nestCoordinates";
    public static final String       ORDERED_ATTRIBUTES               = "coordinate.orderedAttributes";

    // bi-directional many-to-one association to CoordinateAttribute
    @OneToMany(mappedBy = "coordinate")
    @JsonIgnore
    private Set<CoordinateAttribute> attributes;

    @Id
    @GeneratedValue(generator = "coordinate_id_seq", strategy = GenerationType.SEQUENCE)
    private Long                     id;

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
    public Coordinate(Long id) {
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

    @Override
    public Set<CoordinateAttribute> getAttributes() {
        return attributes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.attribute.Attributable#getAttributeType()
     */
    @Override
    public Class<CoordinateAttribute> getAttributeType() {
        return CoordinateAttribute.class;
    }

    @Override
    public Long getId() {
        return id;
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

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.ExistentialRuleform#addChildRelationship(com.hellblazer
     * .CoRE.network.NetworkRuleform)
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
     * com.hellblazer.CoRE.ExistentialRuleform#addParentRelationship(com.hellblazer
     * .CoRE.network.NetworkRuleform)
     */
    @Override
    public void addParentRelationship(CoordinateNetwork relationship) {
        relationship.setParent(this);
        networkByParent.add(relationship);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.ExistentialRuleform#getImmediateChildren(javax.
     * persistence.EntityManager)
     */
    @Override
    public List<CoordinateNetwork> getImmediateChildren(EntityManager em) {
        return em.createNamedQuery(IMMEDIATE_CHILDREN_NETWORK_RULES,
                                   CoordinateNetwork.class).setParameter("coordinate",
                                                                         this).getResultList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.ExistentialRuleform#getNetworkByChild()
     */
    @Override
    public Set<CoordinateNetwork> getNetworkByChild() {
        return networkByChild;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.ExistentialRuleform#getNetworkByParent()
     */
    @Override
    public Set<CoordinateNetwork> getNetworkByParent() {
        return networkByParent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.ExistentialRuleform#link(com.hellblazer.CoRE.network
     * .Relationship, com.hellblazer.CoRE.ExistentialRuleform,
     * com.hellblazer.CoRE.agency.Agency, com.hellblazer.CoRE.agency.Agency,
     * javax.persistence.EntityManager)
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.ExistentialRuleform#setNetworkByChild(java.util.Set)
     */
    @Override
    public void setNetworkByChild(Set<CoordinateNetwork> theNetworkByChild) {
        networkByChild = theNetworkByChild;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.ExistentialRuleform#setNetworkByParent(java.util.Set)
     */
    @Override
    public void setNetworkByParent(Set<CoordinateNetwork> theNetworkByParent) {
        networkByParent = theNetworkByParent;
    }
}