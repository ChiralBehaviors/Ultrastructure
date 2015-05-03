/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE.time;

import static com.chiralbehaviors.CoRE.time.Interval.FIND_BY_NAME;
import static com.chiralbehaviors.CoRE.time.Interval.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS;
import static com.chiralbehaviors.CoRE.time.Interval.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE;
import static com.chiralbehaviors.CoRE.time.Interval.FIND_CLASSIFIED_ATTRIBUTE_VALUES;
import static com.chiralbehaviors.CoRE.time.Interval.GET_ALL_PARENT_RELATIONSHIPS;
import static com.chiralbehaviors.CoRE.time.Interval.GET_CHILD;
import static com.chiralbehaviors.CoRE.time.Interval.GET_CHILD_RULES_BY_RELATIONSHIP;
import static com.chiralbehaviors.CoRE.time.Interval.ORDERED_ATTRIBUTES;

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
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownInterval;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * An interval in time.
 *
 * @author hhildebrand
 *
 */
@NamedQueries({
               @NamedQuery(name = ORDERED_ATTRIBUTES, query = "select ca from IntervalAttribute as ca where ca.interval = :interval"),
               @NamedQuery(name = FIND_BY_NAME, query = "select e from Agency e where e.name = :name"),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_VALUES, query = "SELECT "
                                                                            + "  attrValue "
                                                                            + "FROM "
                                                                            + "       IntervalAttribute attrValue, "
                                                                            + "       IntervalAttributeAuthorization auth, "
                                                                            + "       IntervalNetworkAuthorization na, "
                                                                            + "       IntervalNetwork network "
                                                                            + "WHERE "
                                                                            + "        auth.networkAuthorization = na "
                                                                            + "    AND auth.authorizedAttribute = attrValue.attribute "
                                                                            + "    AND network.relationship = na.classification "
                                                                            + "    AND network.child = na.classifier"
                                                                            + "    AND attrValue.interval = :ruleform "
                                                                            + "    AND na.classification = :classification "
                                                                            + "    AND na.classifier= :classifier "),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE, query = "SELECT "
                                                                                                  + "  auth "
                                                                                                  + "FROM "
                                                                                                  + "       IntervalAttributeAuthorization auth, "
                                                                                                  + "       IntervalNetworkAuthorization na, "
                                                                                                  + "       IntervalNetwork network "
                                                                                                  + "WHERE "
                                                                                                  + "        auth.networkAuthorization = na "
                                                                                                  + "    AND auth.authorizedAttribute = :attribute "
                                                                                                  + "    AND network.relationship = na.classification "
                                                                                                  + "    AND network.child = na.classifier"
                                                                                                  + "    AND na.classification = :classification "
                                                                                                  + "    AND na.classifier= :classifier "),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS, query = "select auth from IntervalAttributeAuthorization auth "
                                                                                    + "WHERE auth.networkAuthorization.classification = :classification "
                                                                                    + "AND auth.networkAuthorization.classifier = :classifier "
                                                                                    + "AND auth.authorizedAttribute IS NOT NULL"),
               @NamedQuery(name = GET_CHILD, query = "SELECT n.child "
                                                     + "FROM IntervalNetwork n "
                                                     + "WHERE n.parent = :p "
                                                     + "AND n.relationship = :r"),
               @NamedQuery(name = GET_ALL_PARENT_RELATIONSHIPS, query = "SELECT n "
                                                                        + "FROM IntervalNetwork n "
                                                                        + "WHERE n.child = :c"),
               @NamedQuery(name = GET_CHILD_RULES_BY_RELATIONSHIP, query = "SELECT n FROM IntervalNetwork n "
                                                                           + "WHERE n.parent = :interval "
                                                                           + "AND n.relationship IN :relationships "
                                                                           + "ORDER by n.parent.name, n.relationship.name, n.child.name") })
@Entity
@Table(name = "interval", schema = "ruleform")
public class Interval extends ExistentialRuleform<Interval, IntervalNetwork> {

    public static final String     AGENCY_ATTRIBUTES_BY_CLASSIFICATION                    = "interval.IntervalAttributesByClassification";

    public static final String     AUTHORIZED_AGENCY_ATTRIBUTES                           = "interval.authorizedAttributes";
    public static final String     FIND_BY_NAME                                           = "interval"
                                                                                            + FIND_BY_NAME_SUFFIX;
    public static final String     FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS               = "interval"
                                                                                            + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
    public static final String     FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE = "interval"
                                                                                            + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE_SUFFIX;
    public static final String     FIND_CLASSIFIED_ATTRIBUTE_VALUES                       = "interval"
                                                                                            + FIND_CLASSIFIED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String     FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS                  = "interval"
                                                                                            + FIND_GROUPED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String     GET_ALL_PARENT_RELATIONSHIPS                           = "interval"
                                                                                            + GET_ALL_PARENT_RELATIONSHIPS_SUFFIX;
    public static final String     GET_CHILD                                              = "interval"
                                                                                            + GET_CHILDREN_SUFFIX;
    public static final String     GET_CHILD_RULES_BY_RELATIONSHIP                        = "interval"
                                                                                            + GET_CHILD_RULES_BY_RELATIONSHIP_SUFFIX;
    public static final String     ORDERED_ATTRIBUTES                                     = "interval.orderedAttributes";
    public static final String     QUALIFIED_ENTITY_NETWORK_RULES                         = "interval.qualifiedEntityNetworkRules";
    private static final long      serialVersionUID                                       = 1L;

    // bi-directional many-to-one association to IntervalAttribute
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "interval")
    @JsonIgnore
    private Set<IntervalAttribute> attributes;

    // bi-directional many-to-one association to IntervalNetwork
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "child")
    @JsonIgnore
    private Set<IntervalNetwork>   networkByChild;

    // bi-directional many-to-one association to IntervalNetwork
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @JsonIgnore
    private Set<IntervalNetwork>   networkByParent;

    public Interval() {
        super();
    }

    public Interval(Agency updatedBy) {
        super(updatedBy);
    }

    public Interval(String name) {
        super(name);
    }

    public Interval(String name, Agency updatedBy) {
        super(name, updatedBy);
    }

    public Interval(String name, String description, Agency updatedBy) {
        super(name, description, updatedBy);
    }

    public Interval(UUID id) {
        super(id);
    }

    public Interval(UUID id, Agency updatedBy) {
        super(id, updatedBy);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.network.Networked#addChildRelationship(com.
     * chiralbehaviors .CoRE.network.NetworkRuleform)
     */
    @Override
    public void addChildRelationship(IntervalNetwork relationship) {
        relationship.setChild(this);
        networkByChild.add(relationship);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.network.Networked#addParentRelationship(com.
     * chiralbehaviors .CoRE.network.NetworkRuleform)
     */
    @Override
    public void addParentRelationship(IntervalNetwork relationship) {
        relationship.setParent(this);
        networkByParent.add(relationship);
    }

    @Override
    public Interval clone() {
        Interval clone = (Interval) super.clone();
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
        return WellKnownInterval.ANY.id();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.attribute.Attributable#getAttributes()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Set<IntervalAttribute> getAttributes() {
        return attributes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<IntervalAttribute> getAttributeValueClass() {
        return IntervalAttribute.class;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getCopyId()
     */
    @Override
    public UUID getCopyId() {
        return WellKnownInterval.COPY.id();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.network.Networked#getNetworkByChild()
     */
    @Override
    public Set<IntervalNetwork> getNetworkByChild() {
        if (networkByChild == null) {
            return Collections.emptySet();
        }
        return networkByChild;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.network.Networked#getNetworkByParent()
     */
    @Override
    public Set<IntervalNetwork> getNetworkByParent() {
        if (networkByParent == null) {
            return Collections.emptySet();
        }
        return networkByParent;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkChildAttribute()
     */
    @Override
    public SingularAttribute<IntervalNetwork, Interval> getNetworkChildAttribute() {
        return IntervalNetwork_.child;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkClass()
     */
    @Override
    public Class<IntervalNetwork> getNetworkClass() {
        return IntervalNetwork.class;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkParentAttribute()
     */
    @Override
    public SingularAttribute<IntervalNetwork, Interval> getNetworkParentAttribute() {
        return IntervalNetwork_.parent;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkWorkspaceAttribute()
     */
    @Override
    public SingularAttribute<WorkspaceAuthorization, IntervalNetwork> getNetworkWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.intervalNetwork;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNotApplicableId()
     */
    @Override
    public UUID getNotApplicableId() {
        return WellKnownInterval.NOT_APPLICABLE.id();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getSameId()
     */
    @Override
    public UUID getSameId() {
        return WellKnownInterval.SAME.id();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, Interval> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.interval;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isAny()
     */
    @Override
    public boolean isAny() {
        return WellKnownInterval.ANY.id().equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isAnyOrSame()
     */
    @Override
    public boolean isAnyOrSame() {
        return WellKnownInterval.ANY.id().equals(getId())
               || WellKnownInterval.SAME.id().equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isCopy()
     */
    @Override
    public boolean isCopy() {
        return WellKnownInterval.COPY.id().equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isNotApplicable()
     */
    @Override
    public boolean isNotApplicable() {
        return WellKnownInterval.NOT_APPLICABLE.id().equals(getId());
    }

    @Override
    public boolean isSame() {
        return WellKnownInterval.SAME.id().equals(getId());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.network.Networked#link(com.chiralbehaviors.CoRE
     * .network .Relationship, com.chiralbehaviors.CoRE.network.Networked,
     * com.chiralbehaviors.CoRE.agency.Agency,
     * com.chiralbehaviors.CoRE.agency.Agency, javax.persistence.EntityManager)
     */
    @Override
    public IntervalNetwork link(Relationship r, Interval child,
                                Agency updatedBy, Agency inverseSoftware,
                                EntityManager em) {
        assert r != null : "Relationship cannot be null";
        assert child != null;
        assert updatedBy != null;
        assert em != null;

        IntervalNetwork link = new IntervalNetwork(this, r, child, updatedBy);
        em.persist(link);
        IntervalNetwork inverse = new IntervalNetwork(child, r.getInverse(),
                                                      this, inverseSoftware);
        em.persist(inverse);
        return link;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends AttributeValue<Interval>> void setAttributes(Set<A> attributes) {
        this.attributes = (Set<IntervalAttribute>) attributes;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.network.Networked#setNetworkByChild(java.util
     * .Set)
     */
    @Override
    public void setNetworkByChild(Set<IntervalNetwork> networkByChild) {
        this.networkByChild = networkByChild;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.network.Networked#setNetworkByParent(java.util
     * .Set)
     */
    @Override
    public void setNetworkByParent(Set<IntervalNetwork> networkByParent) {
        this.networkByParent = networkByParent;
    }
}
