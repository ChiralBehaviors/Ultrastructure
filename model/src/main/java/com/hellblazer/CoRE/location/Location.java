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

import static com.hellblazer.CoRE.Ruleform.FIND_BY_NAME_SUFFIX;
import static com.hellblazer.CoRE.Ruleform.NAME_SEARCH_SUFFIX;
import static com.hellblazer.CoRE.location.Location.FIND_ATTRIBUTE_AUTHORIZATIONS;
import static com.hellblazer.CoRE.location.Location.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS;
import static com.hellblazer.CoRE.location.Location.FIND_CLASSIFIED_ATTRIBUTE_VALUES;
import static com.hellblazer.CoRE.location.Location.FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS;
import static com.hellblazer.CoRE.location.Location.LOCATION_NAME;
import static com.hellblazer.CoRE.location.LocationRelationship.TARGET_CONTEXTS;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.NameSearchResult;
import com.hellblazer.CoRE.attribute.Attributable;
import com.hellblazer.CoRE.entity.EntityLocation;
import com.hellblazer.CoRE.network.Networked;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

/**
 * General idea of a location or address; where some resource, entity or event
 * can be found in a variety of spaces
 * 
 */
@javax.persistence.Entity
@Table(name = "location", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "location_id_seq", sequenceName = "location_id_seq")
@NamedQueries({
               @NamedQuery(name = "location" + FIND_BY_NAME_SUFFIX, query = "select l from Location l where l.name = :name"),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_VALUES, query = "SELECT "
                                                                            + "  attrValue "
                                                                            + "FROM "
                                                                            + "       LocationAttribute attrValue, "
                                                                            + "       LocationAttributeAuthorization auth, "
                                                                            + "       LocationNetwork network "
                                                                            + "WHERE "
                                                                            + "        auth.authorizedAttribute = attrValue.attribute AND "
                                                                            + "        network.relationship = auth.classification AND "
                                                                            + "        network.child = auth.classifier AND"
                                                                            + "        attrValue.location = :ruleform AND "
                                                                            + "        auth.classification = :classification AND "
                                                                            + "        auth.classifier = :classifier "),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS, query = "select la from LocationAttributeAuthorization la "
                                                                                    + "WHERE la.classification = :classification "
                                                                                    + "AND la.classifier = :classifier"),
               @NamedQuery(name = FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS, query = "select la from LocationAttributeAuthorization la "
                                                                                 + "WHERE la.groupingResource = :groupingResource"),
               @NamedQuery(name = FIND_ATTRIBUTE_AUTHORIZATIONS, query = "select la from LocationAttributeAuthorization la "
                                                                         + "WHERE la.classification = :classification "
                                                                         + "AND la.classifier = :classifier "
                                                                         + "AND la.groupingResource = :groupingResource"),
               @NamedQuery(name = LOCATION_NAME, query = "SELECT la.name FROM Location la WHERE la.id = :id") })
@NamedNativeQueries({
// ?1 = :queryString, ?2 = :numberOfMatches
@NamedNativeQuery(name = "location" + NAME_SEARCH_SUFFIX, query = "SELECT id, name, description FROM ruleform.existential_name_search('location', ?1, ?2)", resultClass = NameSearchResult.class) })
public class Location extends ExistentialRuleform implements
        Networked<Location, LocationNetwork>, Attributable<LocationAttribute> {
    private static final long      serialVersionUID                         = 1L;
    public static final String     FIND_ATTRIBUTE_AUTHORIZATIONS            = "location.findAttributeAuthorizations";
    public static final String     FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS = "location"
                                                                              + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
    public static final String     FIND_CLASSIFIED_ATTRIBUTE_VALUES         = "location.findClassifiedAttributes";
    public static final String     FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS    = "location.findGroupedAttributeAuthorizations";
    public static final String     LOCATION_NAME                            = "location.getName";
    public static final String     NAME_SEARCH                              = "location"
                                                                              + NAME_SEARCH_SUFFIX;
    public static final String     FIND_BY_ID                               = "location.findById";
    public static final String     FIND_BY_NAME                             = "location.findByName";

    //bi-directional many-to-one association to LocationAttribute
    @OneToMany(mappedBy = "location")
    @JsonIgnore
    private Set<LocationAttribute> attributes;

    @ManyToOne
    @JoinColumn(name = "context")
    private LocationContext        context;

    //bi-directional many-to-one association to EntityLocation
    @OneToMany(mappedBy = "location")
    @JsonIgnore
    private Set<EntityLocation>    entities;

    @Id
    @GeneratedValue(generator = "location_id_seq", strategy = GenerationType.SEQUENCE)
    private Long                   id;

    //bi-directional many-to-one association to LocationNetwork
    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<LocationNetwork>   networkByChild;

    //bi-directional many-to-one association to LocationNetwork
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<LocationNetwork>   networkByParent;

    public Location() {
    }

    /**
     * @param id
     */
    public Location(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public Location(Resource updatedBy) {
        super(updatedBy);
    }

    /**
     * @param name
     */
    public Location(String name) {
        super(name);
    }

    /**
     * @param name
     * @param updatedBy
     */
    public Location(String name, Resource updatedBy) {
        super(name, updatedBy);
    }

    /**
     * @param name
     * @param description
     */
    public Location(String name, String description) {
        super(name, description);
    }

    /**
     * @param name
     * @param description
     * @param updatedBy
     */
    public Location(String name, String description, LocationContext context,
                    Resource updatedBy) {
        this(name, description, updatedBy);
        this.context = context;
    }

    /**
     * @param name
     * @param description
     * @param updatedBy
     */
    public Location(String name, String description, Resource updatedBy) {
        super(name, description, updatedBy);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#addChildRelationship(com.hellblazer.CoRE.NetworkRuleform)
     */
    @Override
    public void addChildRelationship(LocationNetwork relationship) {
        relationship.setChild(this);
        networkByChild.add(relationship);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#addParentRelationship(com.hellblazer.CoRE.NetworkRuleform)
     */
    @Override
    public void addParentRelationship(LocationNetwork relationship) {
        relationship.setParent(this);
        networkByParent.add(relationship);
    }

    @Override
    public Location clone() {
        Location clone = (Location) super.clone();
        clone.attributes = null;
        clone.networkByChild = null;
        clone.networkByParent = null;
        clone.entities = null;
        return clone;
    }

    @Override
    public Set<LocationAttribute> getAttributes() {
        return attributes;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.Attributable#getAttributeType()
     */
    @Override
    public Class<LocationAttribute> getAttributeType() {
        return LocationAttribute.class;
    }

    public List<LocationContext> getAvailableTargetContexts(EntityManager em) {
        return em.createNamedQuery(TARGET_CONTEXTS, LocationContext.class).setParameter("context",
                                                                                        this).getResultList();
    }

    public LocationContext getContext() {
        return context;
    }

    public Set<EntityLocation> getEntities() {
        return entities;
    }

    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.Networked#getImmediateChildren()
     */
    @Override
    public List<LocationNetwork> getImmediateChildren(EntityManager em) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#getNetworkByChild()
     */
    @Override
    public Set<LocationNetwork> getNetworkByChild() {
        return networkByChild;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#getNetworkByParent()
     */
    @Override
    public Set<LocationNetwork> getNetworkByParent() {
        return networkByParent;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.Networked#link(com.hellblazer.CoRE.network.Relationship, com.hellblazer.CoRE.network.Networked, com.hellblazer.CoRE.resource.Resource, javax.persistence.EntityManager)
     */
    @Override
    public void link(Relationship r, Location child, Resource updatedBy,
                     Resource inverseSoftware, EntityManager em) {
        LocationNetwork link = new LocationNetwork(this, r, child, updatedBy);
        em.persist(link);
        LocationNetwork inverse = new LocationNetwork(child, r.getInverse(),
                                                      this, inverseSoftware);
        em.persist(inverse);
    }

    @Override
    public void setAttributes(Set<LocationAttribute> locationAttributes) {
        attributes = locationAttributes;
    }

    public void setContext(LocationContext locationContext) {
        context = locationContext;
    }

    public void setEntities(Set<EntityLocation> entityLocations) {
        entities = entityLocations;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#setNetworkByChild(java.util.Set)
     */
    @Override
    public void setNetworkByChild(Set<LocationNetwork> theNetworkByChild) {
        networkByChild = theNetworkByChild;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#setNetworkByParent(java.util.Set)
     */
    @Override
    public void setNetworkByParent(Set<LocationNetwork> theNetworkByParent) {
        networkByParent = theNetworkByParent;
    }

}