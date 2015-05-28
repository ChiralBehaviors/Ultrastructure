/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE.location;

import static com.chiralbehaviors.CoRE.Ruleform.FIND_BY_NAME_SUFFIX;
import static com.chiralbehaviors.CoRE.location.Location.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS;
import static com.chiralbehaviors.CoRE.location.Location.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE;
import static com.chiralbehaviors.CoRE.location.Location.FIND_CLASSIFIED_ATTRIBUTE_VALUES;
import static com.chiralbehaviors.CoRE.location.Location.FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS;
import static com.chiralbehaviors.CoRE.location.Location.GET_ALL_PARENT_RELATIONSHIPS;
import static com.chiralbehaviors.CoRE.location.Location.GET_CHILD;
import static com.chiralbehaviors.CoRE.location.Location.GET_CHILD_RULES_BY_RELATIONSHIP;
import static com.chiralbehaviors.CoRE.location.Location.LOCATION_NAME;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Triggers;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownLocation;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.product.ProductLocation;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
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
                                                                            + "       LocationNetworkAuthorization na,"
                                                                            + "       LocationNetwork network "
                                                                            + "WHERE "
                                                                            + "        auth.networkAuthorization = na "
                                                                            + "    AND auth.authorizedAttribute = attrValue.attribute "
                                                                            + "    AND network.relationship = na.classifier "
                                                                            + "    AND network.child = na.classification"
                                                                            + "    AND attrValue.location = :ruleform "
                                                                            + "    AND na.classifier = :classifier "
                                                                            + "    AND na.classification= :classification "),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE, query = "SELECT "
                                                                                                  + "  auth "
                                                                                                  + "FROM "
                                                                                                  + "       IntervalAttributeAuthorization auth, "
                                                                                                  + "       IntervalNetworkAuthorization na, "
                                                                                                  + "       IntervalNetwork network "
                                                                                                  + "WHERE "
                                                                                                  + "        auth.networkAuthorization = na "
                                                                                                  + "    AND auth.authorizedAttribute = :attribute "
                                                                                                  + "    AND network.relationship = na.classifier "
                                                                                                  + "    AND network.child = na.classification"
                                                                                                  + "    AND na.classifier = :classifier "
                                                                                                  + "    AND na.classification= :classification "),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS, query = "select auth from LocationAttributeAuthorization auth "
                                                                                    + "WHERE auth.networkAuthorization.classifier = :classifier "
                                                                                    + "AND auth.networkAuthorization.classification = :classification "
                                                                                    + "AND auth.authorizedAttribute IS NOT NULL"),
               @NamedQuery(name = FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS, query = "select la from LocationAttributeAuthorization la "
                                                                                 + "WHERE la.groupingAgency = :groupingAgency"),
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
public class Location extends ExistentialRuleform<Location, LocationNetwork> {
    public static final String     FIND_BY_ID                                             = "location.findById";
    public static final String     FIND_BY_NAME                                           = "location.findByName";
    public static final String     FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS               = "location"
                                                                                            + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
    public static final String     FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE = "location"
                                                                                            + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE_SUFFIX;
    public static final String     FIND_CLASSIFIED_ATTRIBUTE_VALUES                       = "location.findClassifiedAttributes";
    public static final String     FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS                  = "location.findGroupedAttributeAuthorizations";
    public static final String     GET_ALL_PARENT_RELATIONSHIPS                           = "location"
                                                                                            + GET_ALL_PARENT_RELATIONSHIPS_SUFFIX;
    public static final String     GET_CHILD                                              = "location"
                                                                                            + GET_CHILDREN_SUFFIX;
    public static final String     GET_CHILD_RULES_BY_RELATIONSHIP                        = "location"
                                                                                            + GET_CHILD_RULES_BY_RELATIONSHIP_SUFFIX;
    public static final String     LOCATION_NAME                                          = "location.getName";
    private static final long      serialVersionUID                                       = 1L;

    // bi-directional many-to-one association to LocationAttribute
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "location")
    @JsonIgnore
    private Set<LocationAttribute> attributes;

    // bi-directional many-to-one association to ProductLocation
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "location")
    @JsonIgnore
    private Set<ProductLocation>   entities;

    // bi-directional many-to-one association to LocationNetwork
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "child")
    @JsonIgnore
    private Set<LocationNetwork>   networkByChild;

    // bi-directional many-to-one association to LocationNetwork
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
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
    public void delete(Triggers triggers) {
        triggers.delete(this);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getAnyId()
     */
    @Override
    public UUID getAnyId() {
        return WellKnownLocation.ANY.id();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<LocationAttribute> getAttributes() {
        return attributes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<LocationAttribute> getAttributeValueClass() {
        return LocationAttribute.class;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getCopyId()
     */
    @Override
    public UUID getCopyId() {
        return WellKnownLocation.COPY.id();
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

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkChildAttribute()
     */
    @Override
    public SingularAttribute<LocationNetwork, Location> getNetworkChildAttribute() {
        return LocationNetwork_.child;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkClass()
     */
    @Override
    public Class<LocationNetwork> getNetworkClass() {
        return LocationNetwork.class;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkParentAttribute()
     */
    @Override
    public SingularAttribute<LocationNetwork, Location> getNetworkParentAttribute() {
        return LocationNetwork_.parent;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkWorkspaceAttribute()
     */
    @Override
    public SingularAttribute<WorkspaceAuthorization, LocationNetwork> getNetworkWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.locationNetwork;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNotApplicableId()
     */
    @Override
    public UUID getNotApplicableId() {
        return WellKnownLocation.NOT_APPLICABLE.id();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getSameId()
     */
    @Override
    public UUID getSameId() {
        return WellKnownLocation.SAME.id();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, Location> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.location;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isAny()
     */
    @Override
    public boolean isAny() {
        return WellKnownLocation.ANY.id().equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isAnyOrSame()
     */
    @Override
    public boolean isAnyOrSame() {
        return WellKnownLocation.ANY.id().equals(getId())
               || WellKnownLocation.SAME.id().equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isCopy()
     */
    @Override
    public boolean isCopy() {
        return WellKnownLocation.COPY.id().equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isNotApplicable()
     */
    @Override
    public boolean isNotApplicable() {
        return WellKnownLocation.NOT_APPLICABLE.id().equals(getId());
    }

    @Override
    public boolean isSame() {
        return WellKnownLocation.SAME.id().equals(getId());
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
    public LocationNetwork link(Relationship r, Location child,
                                Agency updatedBy, Agency inverseSoftware,
                                EntityManager em) {
        LocationNetwork link = new LocationNetwork(this, r, child, updatedBy);
        em.persist(link);
        LocationNetwork inverse = new LocationNetwork(child, r.getInverse(),
                                                      this, inverseSoftware);
        em.persist(inverse);
        return link;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends AttributeValue<Location>> void setAttributes(Set<A> attributes) {
        this.attributes = (Set<LocationAttribute>) attributes;
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
