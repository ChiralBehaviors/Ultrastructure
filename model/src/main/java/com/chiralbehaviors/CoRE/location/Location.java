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
package com.chiralbehaviors.CoRE.location;

import static com.chiralbehaviors.CoRE.Ruleform.FIND_BY_NAME_SUFFIX;
import static com.chiralbehaviors.CoRE.Ruleform.NAME_SEARCH_SUFFIX;
import static com.chiralbehaviors.CoRE.location.Location.FIND_ATTRIBUTE_AUTHORIZATIONS;
import static com.chiralbehaviors.CoRE.location.Location.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS;
import static com.chiralbehaviors.CoRE.location.Location.FIND_CLASSIFIED_ATTRIBUTE_VALUES;
import static com.chiralbehaviors.CoRE.location.Location.FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS;
import static com.chiralbehaviors.CoRE.location.Location.GET_ALL_PARENT_RELATIONSHIPS;
import static com.chiralbehaviors.CoRE.location.Location.GET_CHILD;
import static com.chiralbehaviors.CoRE.location.Location.GET_CHILD_RULES_BY_RELATIONSHIP;
import static com.chiralbehaviors.CoRE.location.Location.LOCATION_NAME;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
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
import com.chiralbehaviors.CoRE.product.ProductLocation;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * General idea of a location or address; where some agency, product or event
 * can be found in a variety of spaces
 * 
 */
@Entity
@Table(name = "location", schema = "ruleform")
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
                                                                                 + "WHERE la.groupingAgency = :groupingAgency"),
               @NamedQuery(name = FIND_ATTRIBUTE_AUTHORIZATIONS, query = "select la from LocationAttributeAuthorization la "
                                                                         + "WHERE la.classification = :classification "
                                                                         + "AND la.classifier = :classifier "
                                                                         + "AND la.groupingAgency = :groupingAgency"),
               @NamedQuery(name = LOCATION_NAME, query = "SELECT la.name FROM Location la WHERE la.id = :id"),
               @NamedQuery(name = GET_CHILD, query = "SELECT n.child "
                                                     + "FROM LocationNetwork n "
                                                     + "WHERE n.parent = :p "
                                                     + "AND n.relationship = :r"),
               @NamedQuery(name = GET_ALL_PARENT_RELATIONSHIPS, query = "SELECT n "
                                                                        + "FROM LocationNetwork n "
                                                                        + "WHERE n.child = :c"),
               @NamedQuery(name = GET_CHILD_RULES_BY_RELATIONSHIP, query = "SELECT n FROM LocationNetwork n "
                                                                           + "WHERE n.parent = :location "
                                                                           + "AND n.relationship IN :relationships "
                                                                           + "ORDER by n.parent.name, n.relationship.name, n.child.name") })
@NamedNativeQueries({
// ?1 = :queryString, ?2 = :numberOfMatches
@NamedNativeQuery(name = "location" + NAME_SEARCH_SUFFIX, query = "SELECT id, name, description FROM ruleform.existential_name_search('location', ?1, ?2)", resultClass = NameSearchResult.class) })
public class Location extends ExistentialRuleform<Location, LocationNetwork>
        implements Attributable<LocationAttribute> {
    public static final String     FIND_ATTRIBUTE_AUTHORIZATIONS            = "location.findAttributeAuthorizations";
    public static final String     FIND_BY_ID                               = "location.findById";
    public static final String     FIND_BY_NAME                             = "location.findByName";
    public static final String     FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS = "location"
                                                                              + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
    public static final String     FIND_CLASSIFIED_ATTRIBUTE_VALUES         = "location.findClassifiedAttributes";
    public static final String     FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS    = "location.findGroupedAttributeAuthorizations";
    public static final String     GET_ALL_PARENT_RELATIONSHIPS             = "location"
                                                                              + GET_ALL_PARENT_RELATIONSHIPS_SUFFIX;
    public static final String     GET_CHILD                                = "location"
                                                                              + GET_CHILDREN_SUFFIX;
    public static final String     GET_CHILD_RULES_BY_RELATIONSHIP          = "location"
                                                                              + GET_CHILD_RULES_BY_RELATIONSHIP_SUFFIX;
    public static final String     LOCATION_NAME                            = "location.getName";
    public static final String     NAME_SEARCH                              = "location"
                                                                              + NAME_SEARCH_SUFFIX;
    private static final long      serialVersionUID                         = 1L;

    // bi-directional many-to-one association to LocationAttribute
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "location")
    @JsonIgnore
    private Set<LocationAttribute> attributes;

    // bi-directional many-to-one association to ProductLocation
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "location")
    @JsonIgnore
    private Set<ProductLocation>   entities;

    // bi-directional many-to-one association to LocationNetwork
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "child", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<LocationNetwork>   networkByChild;

    // bi-directional many-to-one association to LocationNetwork
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<LocationNetwork>   networkByParent;

    public Location() {
    }

    /**
     * @param updatedBy
     */
    public Location(Agency updatedBy) {
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
    public Location(String name, Agency updatedBy) {
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
    public Location(String name, String description, Agency updatedBy) {
        super(name, description, updatedBy);
    }

    /**
     * @param id
     */
    public Location(UUID id) {
        super(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.Networked#addChildRelationship(com.chiralbehaviors
     * .CoRE .NetworkRuleform)
     */
    @Override
    public void addChildRelationship(LocationNetwork relationship) {
        relationship.setChild(this);
        networkByChild.add(relationship);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.Networked#addParentRelationship(com.chiralbehaviors
     * .CoRE .NetworkRuleform)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.attribute.Attributable#getAttributeType()
     */
    @Override
    public Class<LocationAttribute> getAttributeType() {
        return LocationAttribute.class;
    }

    public Set<ProductLocation> getEntities() {
        return entities;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.Networked#getNetworkByChild()
     */
    @Override
    public Set<LocationNetwork> getNetworkByChild() {
        if (networkByChild == null) {
            return Collections.emptySet();
        }
        return networkByChild;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.Networked#getNetworkByParent()
     */
    @Override
    public Set<LocationNetwork> getNetworkByParent() {
        if (networkByParent == null) {
            return Collections.emptySet();
        }
        return networkByParent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.network.Networked#link(com.chiralbehaviors.CoRE
     * .network .Relationship, com.chiralbehaviors.CoRE.network.Networked,
     * com.chiralbehaviors.CoRE.agency.Agency, javax.persistence.EntityManager)
     */
    @Override
    public void link(Relationship r, Location child, Agency updatedBy,
                     Agency inverseSoftware, EntityManager em) {
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

    public void setEntities(Set<ProductLocation> productLocations) {
        entities = productLocations;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.Networked#setNetworkByChild(java.util.Set)
     */
    @Override
    public void setNetworkByChild(Set<LocationNetwork> theNetworkByChild) {
        networkByChild = theNetworkByChild;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.Networked#setNetworkByParent(java.util.Set)
     */
    @Override
    public void setNetworkByParent(Set<LocationNetwork> theNetworkByParent) {
        networkByParent = theNetworkByParent;
    }
}
