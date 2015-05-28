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
package com.chiralbehaviors.CoRE.job.status;

import static com.chiralbehaviors.CoRE.job.status.StatusCode.FIND_BY_NAME;
import static com.chiralbehaviors.CoRE.job.status.StatusCode.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS;
import static com.chiralbehaviors.CoRE.job.status.StatusCode.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE;
import static com.chiralbehaviors.CoRE.job.status.StatusCode.FIND_CLASSIFIED_ATTRIBUTE_VALUES;
import static com.chiralbehaviors.CoRE.job.status.StatusCode.FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS;
import static com.chiralbehaviors.CoRE.job.status.StatusCode.GET_ALL_PARENT_RELATIONSHIPS;
import static com.chiralbehaviors.CoRE.job.status.StatusCode.GET_CHILD;
import static com.chiralbehaviors.CoRE.job.status.StatusCode.GET_CHILD_RULES_BY_RELATIONSHIP;
import static com.chiralbehaviors.CoRE.job.status.StatusCode.IS_TERMINAL_STATE;
import static com.chiralbehaviors.CoRE.job.status.StatusCode.ORDERED_ATTRIBUTES;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
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
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownStatusCode;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The persistent class for the status_code database table.
 *
 */

@Entity
@Table(name = "status_code", schema = "ruleform")
@NamedQueries({
               @NamedQuery(name = ORDERED_ATTRIBUTES, query = "select ca from StatusCodeAttribute as ca where ca.statusCode = :statusCode"),
               @NamedQuery(name = FIND_BY_NAME, query = "select e from Agency e where e.name = :name"),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_VALUES, query = "SELECT "
                                                                            + "  attrValue "
                                                                            + "FROM "
                                                                            + "       StatusCodeAttribute attrValue, "
                                                                            + "       StatusCodeAttributeAuthorization auth, "
                                                                            + "       StatusCodeNetworkAuthorization na, "
                                                                            + "       StatusCodeNetwork network "
                                                                            + "WHERE "
                                                                            + "        auth.networkAuthorization = na "
                                                                            + "    AND auth.authorizedAttribute = attrValue.attribute "
                                                                            + "    AND network.relationship = na.classifier "
                                                                            + "    AND network.child = na.classification"
                                                                            + "    AND attrValue.statusCode = :ruleform "
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
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS, query = "select auth from StatusCodeAttributeAuthorization auth "
                                                                                    + "WHERE auth.networkAuthorization.classifier = :classifier "
                                                                                    + "AND auth.networkAuthorization.classification = :classification "
                                                                                    + "AND auth.authorizedAttribute IS NOT NULL"),
               @NamedQuery(name = FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS, query = "select ra from StatusCodeAttributeAuthorization ra "
                                                                                 + "WHERE ra.groupingAgency = :groupingAgency"),
               @NamedQuery(name = GET_CHILD, query = "SELECT n.child "
                                                     + "FROM StatusCodeNetwork n "
                                                     + "WHERE n.parent = :p "
                                                     + "AND n.relationship = :r"),
               @NamedQuery(name = GET_ALL_PARENT_RELATIONSHIPS, query = "SELECT n "
                                                                        + "FROM StatusCodeNetwork n "
                                                                        + "WHERE n.child = :c"),
               @NamedQuery(name = GET_CHILD_RULES_BY_RELATIONSHIP, query = "SELECT n FROM StatusCodeNetwork n "
                                                                           + "WHERE n.parent = :statusCode "
                                                                           + "AND n.relationship IN :relationships "
                                                                           + "ORDER by n.parent.name, n.relationship.name, n.child.name"),
               @NamedQuery(name = IS_TERMINAL_STATE, query = "SELECT COUNT(seq) "
                                                             + "FROM StatusCodeSequencing AS seq"
                                                             + " WHERE seq.childCode = :statusCode"
                                                             + "  AND NOT EXISTS ( "
                                                             + "    SELECT seq2.parentCode FROM StatusCodeSequencing seq2"
                                                             + "    WHERE seq2.service = seq.service "
                                                             + "      AND seq2.parentCode = seq.childCode "
                                                             + "  ) "
                                                             + "  AND seq.service = :service ") })
public class StatusCode extends
        ExistentialRuleform<StatusCode, StatusCodeNetwork> {

    public static final String       AGENCY_ATTRIBUTES_BY_CLASSIFICATION                    = "statusCode.AgencyAttributesByClassification";
    public static final String       AUTHORIZED_AGENCY_ATTRIBUTES                           = "statusCode.authorizedAttributes";

    public static final String       FIND_BY_NAME                                           = "statusCode"
                                                                                              + FIND_BY_NAME_SUFFIX;
    public static final String       FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS               = "statusCode"
                                                                                              + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
    public static final String       FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE = "statusCode"
                                                                                              + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE_SUFFIX;
    public static final String       FIND_CLASSIFIED_ATTRIBUTE_VALUES                       = "statusCode"
                                                                                              + FIND_CLASSIFIED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String       FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS                  = "statusCode"
                                                                                              + FIND_GROUPED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String       GET_ALL_PARENT_RELATIONSHIPS                           = "statusCode"
                                                                                              + GET_ALL_PARENT_RELATIONSHIPS_SUFFIX;
    public static final String       GET_CHILD                                              = "statusCode"
                                                                                              + GET_CHILDREN_SUFFIX;
    public static final String       GET_CHILD_RULES_BY_RELATIONSHIP                        = "statusCode"
                                                                                              + GET_CHILD_RULES_BY_RELATIONSHIP_SUFFIX;
    public static final String       IS_TERMINAL_STATE                                      = "statusCode.isTerminalState";
    public static final String       ORDERED_ATTRIBUTES                                     = "statusCode.orderedAttributes";
    public static final String       QUALIFIED_ENTITY_NETWORK_RULES                         = "statusCode.qualifiedEntityNetworkRules";

    private static final long        serialVersionUID                                       = 1L;

    // bi-directional many-to-one association to AgencyAttribute
    @OneToMany(mappedBy = "statusCode", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<StatusCodeAttribute> attributes;

    @Column(name = "fail_parent")
    private Integer                  failParent                                             = TRUE;

    @OneToMany(mappedBy = "child", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<StatusCodeNetwork>   networkByChild;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<StatusCodeNetwork>   networkByParent;

    @Column(name = "propagate_children")
    private Integer                  propagateChildren                                      = FALSE;

    public StatusCode() {
    }

    /**
     * @param updatedBy
     */
    public StatusCode(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param name
     */
    public StatusCode(String name) {
        super(name);
    }

    /**
     * @param name
     * @param updatedBy
     */
    public StatusCode(String name, Agency updatedBy) {
        super(name, updatedBy);
    }

    /**
     * @param name
     * @param description
     */
    public StatusCode(String name, String description) {
        super(name, description);
    }

    /**
     * @param name
     * @param description
     * @param updatedBy
     */
    public StatusCode(String name, String description, Agency updatedBy) {
        super(name, description, updatedBy);
    }

    /**
     * @param id
     */
    public StatusCode(UUID id) {
        super(id);
    }

    public StatusCode(UUID l, String name) {
        super(l, name);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.Networked#addChildRelationship(com.chiralbehaviors
     * .CoRE.NetworkRuleform)
     */
    @Override
    public void addChildRelationship(StatusCodeNetwork relationship) {
        relationship.setChild(this);
        networkByChild.add(relationship);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.Networked#addParentRelationship(com.chiralbehaviors
     * .CoRE.NetworkRuleform)
     */
    @Override
    public void addParentRelationship(StatusCodeNetwork relationship) {
        relationship.setParent(this);
        networkByParent.add(relationship);
    }

    @Override
    public StatusCode clone() {
        StatusCode clone = (StatusCode) super.clone();
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
        return WellKnownStatusCode.ANY.id();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<StatusCodeAttribute> getAttributes() {
        return attributes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<StatusCodeAttribute> getAttributeValueClass() {
        return StatusCodeAttribute.class;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getCopyId()
     */
    @Override
    public UUID getCopyId() {
        return WellKnownStatusCode.COPY.id();
    }

    public Boolean getFailParent() {
        return toBoolean(failParent);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkByChild()
     */
    @Override
    public Set<StatusCodeNetwork> getNetworkByChild() {
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
    public Set<StatusCodeNetwork> getNetworkByParent() {
        if (networkByParent == null) {
            return Collections.emptySet();
        }
        return networkByParent;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkChildAttribute()
     */
    @Override
    public SingularAttribute<StatusCodeNetwork, StatusCode> getNetworkChildAttribute() {
        return StatusCodeNetwork_.child;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkClass()
     */
    @Override
    public Class<StatusCodeNetwork> getNetworkClass() {
        return StatusCodeNetwork.class;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkParentAttribute()
     */
    @Override
    public SingularAttribute<StatusCodeNetwork, StatusCode> getNetworkParentAttribute() {
        return StatusCodeNetwork_.parent;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNetworkWorkspaceAttribute()
     */
    @Override
    public SingularAttribute<WorkspaceAuthorization, StatusCodeNetwork> getNetworkWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.statusCodeNetwork;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNotApplicableId()
     */
    @Override
    public UUID getNotApplicableId() {
        return WellKnownStatusCode.NOT_APPLICABLE.id();
    }

    public Boolean getPropagateChildren() {
        return toBoolean(propagateChildren);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getSameId()
     */
    @Override
    public UUID getSameId() {
        return WellKnownStatusCode.SAME.id();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, StatusCode> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.statusCode;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isAny()
     */
    @Override
    public boolean isAny() {
        return WellKnownStatusCode.ANY.id().equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isAnyOrSame()
     */
    @Override
    public boolean isAnyOrSame() {
        return WellKnownStatusCode.ANY.id().equals(getId())
               || WellKnownStatusCode.SAME.id().equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isCopy()
     */
    @Override
    public boolean isCopy() {
        return WellKnownStatusCode.COPY.id().equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isNotApplicable()
     */
    @Override
    public boolean isNotApplicable() {
        return WellKnownStatusCode.NOT_APPLICABLE.id().equals(getId());
    }

    @Override
    public boolean isSame() {
        return WellKnownStatusCode.SAME.id().equals(getId());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.network.Networked#link(com.chiralbehaviors.CoRE
     * .network.Relationship, com.chiralbehaviors.CoRE.network.Networked,
     * com.chiralbehaviors.CoRE.agency.Agency, javax.persistence.EntityManager)
     */
    @Override
    public StatusCodeNetwork link(Relationship r, StatusCode child,
                                  Agency updatedBy, Agency inverseSoftware,
                                  EntityManager em) {
        assert r != null : "Relationship cannot be null";
        assert child != null;
        assert updatedBy != null;
        assert em != null;

        StatusCodeNetwork link = new StatusCodeNetwork(this, r, child,
                                                       updatedBy);
        em.persist(link);
        StatusCodeNetwork inverse = new StatusCodeNetwork(child,
                                                          r.getInverse(), this,
                                                          inverseSoftware);
        em.persist(inverse);
        return link;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends AttributeValue<StatusCode>> void setAttributes(Set<A> attributes) {
        this.attributes = (Set<StatusCodeAttribute>) attributes;
    }

    public void setFailParent(Boolean failParent) {
        this.failParent = toInteger(failParent);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.ExistentialRuleform#setNetworkByChild(java.util
     * .Set)
     */
    @Override
    public void setNetworkByChild(Set<StatusCodeNetwork> theNetworkByChild) {
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
    public void setNetworkByParent(Set<StatusCodeNetwork> theNetworkByParent) {
        networkByParent = theNetworkByParent;
    }

    public void setPropagateChildren(Boolean propagateChildren) {
        this.propagateChildren = toInteger(propagateChildren);
    }
}
