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
package com.chiralbehaviors.CoRE.relationship;

import static com.chiralbehaviors.CoRE.relationship.Relationship.FIND_BY_NAME;
import static com.chiralbehaviors.CoRE.relationship.Relationship.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS;
import static com.chiralbehaviors.CoRE.relationship.Relationship.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE;
import static com.chiralbehaviors.CoRE.relationship.Relationship.FIND_CLASSIFIED_ATTRIBUTE_VALUES;
import static com.chiralbehaviors.CoRE.relationship.Relationship.GET_ALL_PARENT_RELATIONSHIPS;
import static com.chiralbehaviors.CoRE.relationship.Relationship.GET_CHILD;
import static com.chiralbehaviors.CoRE.relationship.Relationship.GET_CHILD_RULES_BY_RELATIONSHIP;
import static com.chiralbehaviors.CoRE.relationship.Relationship.ORDERED_ATTRIBUTES;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Triggers;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownRelationship;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hellblazer.utils.Tuple;

/**
 * The existential rule form that defines relationships between existential rule
 * form instances, providing the edge connecting two nodes in a directed graph.
 *
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "relationship", schema = "ruleform")
@NamedQueries({ @NamedQuery(name = ORDERED_ATTRIBUTES, query = "select ca from RelationshipAttribute as ca where ca.relationship = :relationship"),
                @NamedQuery(name = FIND_BY_NAME, query = "select e from Agency e where e.name = :name"),
                @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_VALUES, query = "SELECT "
                                                                             + "  attrValue "
                                                                             + "FROM "
                                                                             + "       RelationshipAttribute attrValue, "
                                                                             + "       RelationshipAttributeAuthorization auth, "
                                                                             + "       RelationshipNetworkAuthorization na, "
                                                                             + "       RelationshipNetwork network "
                                                                             + "WHERE "
                                                                             + "        auth.networkAuthorization = na "
                                                                             + "    AND auth.authorizedAttribute = attrValue.attribute "
                                                                             + "    AND network.relationship = na.classifier "
                                                                             + "    AND network.child = na.classification"
                                                                             + "    AND attrValue.relationship = :ruleform "
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
                @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS, query = "select auth from RelationshipAttributeAuthorization auth "
                                                                                     + "WHERE auth.networkAuthorization.classifier = :classifier "
                                                                                     + "AND auth.networkAuthorization.classification = :classification "
                                                                                     + "AND auth.authorizedAttribute IS NOT NULL"),
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
public class Relationship
        extends ExistentialRuleform<Relationship, RelationshipNetwork> {

    public static final String AGENCY_ATTRIBUTES_BY_CLASSIFICATION = "relationship.RelationshipAttributesByClassification";

    public static final String AUTHORIZED_AGENCY_ATTRIBUTES                           = "relationship.authorizedAttributes";
    public static final String FIND_BY_NAME                                           = "relationship"
                                                                                        + FIND_BY_NAME_SUFFIX;
    public static final String FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS               = "relationship"
                                                                                        + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
    public static final String FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE = "relationship"
                                                                                        + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE_SUFFIX;
    public static final String FIND_CLASSIFIED_ATTRIBUTE_VALUES                       = "relationship"
                                                                                        + FIND_CLASSIFIED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String GET_ALL_PARENT_RELATIONSHIPS                           = "relationship"
                                                                                        + GET_ALL_PARENT_RELATIONSHIPS_SUFFIX;
    public static final String GET_CHILD                                              = "relationship"
                                                                                        + GET_CHILDREN_SUFFIX;
    public static final String GET_CHILD_RULES_BY_RELATIONSHIP                        = "relationship"
                                                                                        + GET_CHILD_RULES_BY_RELATIONSHIP_SUFFIX;
    public static final String ORDERED_ATTRIBUTES                                     = "relationship.orderedAttributes";
    public static final String QUALIFIED_ENTITY_NETWORK_RULES                         = "relationship.qualifiedEntityNetworkRules";

    private static final long serialVersionUID = 1L;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "relationship")
    @JsonIgnore
    private Set<RelationshipAttribute> attributes;

    @OneToOne(cascade = { CascadeType.PERSIST,
                          CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "inverse", unique = true)
    private Relationship inverse;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "child")
    @JsonIgnore
    private Set<RelationshipNetwork> networkByChild;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @JsonIgnore
    private Set<RelationshipNetwork> networkByParent;

    public Relationship() {
    }

    /**
     * @param updatedBy
     */
    public Relationship(Agency updatedBy) {
        super(updatedBy);
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
     * @param inverse
     */
    public Relationship(String name, String description, Agency updatedBy,
                        Relationship inverse) {
        super(name, description, updatedBy);
        setInverse(inverse);
    }

    /**
     * @param id
     */
    public Relationship(UUID id) {
        super(id);
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

    @Override
    public void delete(Triggers triggers) {
        triggers.delete(this);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getAnyId()
     */
    @Override
    public UUID getAnyId() {
        return WellKnownRelationship.ANY.id();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<RelationshipAttribute> getAttributes() {
        return attributes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<RelationshipAttribute> getAttributeValueClass() {
        return RelationshipAttribute.class;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getCopyId()
     */
    @Override
    public UUID getCopyId() {
        return WellKnownRelationship.COPY.id();
    }

    @JsonGetter
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

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkChildAttribute()
     */
    @Override
    public SingularAttribute<RelationshipNetwork, Relationship> getNetworkChildAttribute() {
        return RelationshipNetwork_.child;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkClass()
     */
    @Override
    public Class<RelationshipNetwork> getNetworkClass() {
        return RelationshipNetwork.class;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkParentAttribute()
     */
    @Override
    public SingularAttribute<RelationshipNetwork, Relationship> getNetworkParentAttribute() {
        return RelationshipNetwork_.parent;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNotApplicableId()
     */
    @Override
    public UUID getNotApplicableId() {
        return WellKnownRelationship.NOT_APPLICABLE.id();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getSameId()
     */
    @Override
    public UUID getSameId() {
        return WellKnownRelationship.SAME.id();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isAny()
     */
    @Override
    public boolean isAny() {
        return WellKnownRelationship.ANY.id()
                                        .equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isAnyOrSame()
     */
    @Override
    public boolean isAnyOrSame() {
        return WellKnownRelationship.ANY.id()
                                        .equals(getId())
               || WellKnownRelationship.SAME.id()
                                            .equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isCopy()
     */
    @Override
    public boolean isCopy() {
        return WellKnownRelationship.COPY.id()
                                         .equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isNotApplicable()
     */
    @Override
    public boolean isNotApplicable() {
        return WellKnownRelationship.NOT_APPLICABLE.id()
                                                   .equals(getId());
    }

    @Override
    public boolean isSame() {
        return WellKnownRelationship.SAME.id()
                                         .equals(getId());
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
    public Tuple<RelationshipNetwork, RelationshipNetwork> link(Relationship r,
                                                                Relationship child,
                                                                Agency updatedBy,
                                                                Agency inverseSoftware,
                                                                EntityManager em) {
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
        return new Tuple<>(link, inverse);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends AttributeValue<Relationship>> void setAttributes(Set<A> attributes) {
        this.attributes = (Set<RelationshipAttribute>) attributes;
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
}
