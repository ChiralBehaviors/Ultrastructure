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
package com.chiralbehaviors.CoRE.event.status;

import static com.chiralbehaviors.CoRE.Ruleform.NAME_SEARCH_SUFFIX;
import static com.chiralbehaviors.CoRE.event.status.StatusCode.FIND_BY_NAME;
import static com.chiralbehaviors.CoRE.event.status.StatusCode.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS;
import static com.chiralbehaviors.CoRE.event.status.StatusCode.FIND_CLASSIFIED_ATTRIBUTE_VALUES;
import static com.chiralbehaviors.CoRE.event.status.StatusCode.FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS;
import static com.chiralbehaviors.CoRE.event.status.StatusCode.GET_ALL_PARENT_RELATIONSHIPS;
import static com.chiralbehaviors.CoRE.event.status.StatusCode.GET_CHILD;
import static com.chiralbehaviors.CoRE.event.status.StatusCode.GET_CHILD_RULES_BY_RELATIONSHIP;
import static com.chiralbehaviors.CoRE.event.status.StatusCode.IS_TERMINAL_STATE;
import static com.chiralbehaviors.CoRE.event.status.StatusCode.ORDERED_ATTRIBUTES;
import static com.chiralbehaviors.CoRE.event.status.StatusCode.UNLINKED;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import com.chiralbehaviors.CoRE.network.Relationship;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The persistent class for the status_code database table.
 * 
 */

@Entity
@Table(name = "status_code", schema = "ruleform")
@NamedNativeQueries({
                     @NamedNativeQuery(name = UNLINKED, query = "SELECT unlinked.* "
                                                                + "FROM statusCode AS unlinked "
                                                                + "JOIN ("
                                                                + "     SELECT id "
                                                                + "     FROM statusCode "
                                                                + "     EXCEPT ("
                                                                + "             SELECT distinct(net.child) "
                                                                + "             FROM statusCode_network as net "
                                                                + "             WHERE net.parent = statusCode_id('Agency') "
                                                                + "             AND relationship = relationship_id('includes') "
                                                                + "     )"
                                                                + ") AS linked ON unlinked.id = linked.id "
                                                                + "WHERE unlinked.id != statusCode_id('Agency');", resultClass = Agency.class),
                     @NamedNativeQuery(name = "statusCode" + NAME_SEARCH_SUFFIX, query = "SELECT id, name, description FROM ruleform.existential_name_search('status_code', ?1, ?2)", resultClass = NameSearchResult.class),
                     @NamedNativeQuery(name = IS_TERMINAL_STATE, query = "SELECT EXISTS( "
                                                                         + "SELECT sc.id "
                                                                         + "FROM ruleform.status_code_sequencing AS seq "
                                                                         + "    JOIN ruleform.status_code AS sc ON seq.child_code = sc.id "
                                                                         + " WHERE "
                                                                         + "  NOT EXISTS ( "
                                                                         + "    SELECT parent_code FROM ruleform.status_code_sequencing "
                                                                         + "    WHERE service = seq.service "
                                                                         + "      AND parent_code = seq.child_code "
                                                                         + "  ) "
                                                                         + "  AND service = ? "
                                                                         + "  AND sc.id = ? "
                                                                         + " )") })
@NamedQueries({
               @NamedQuery(name = ORDERED_ATTRIBUTES, query = "select ca from StatusCodeAttribute as ca where ca.statusCode = :statusCode"),
               @NamedQuery(name = FIND_BY_NAME, query = "select e from Agency e where e.name = :name"),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_VALUES, query = "SELECT "
                                                                            + "  attrValue "
                                                                            + "FROM "
                                                                            + "       StatusCodeAttribute attrValue, "
                                                                            + "       StatusCodeAttributeAuthorization auth, "
                                                                            + "       StatusCodeNetwork network "
                                                                            + "WHERE "
                                                                            + "        auth.authorizedAttribute = attrValue.attribute AND "
                                                                            + "        network.relationship = auth.classification AND "
                                                                            + "        network.child = auth.classifier AND"
                                                                            + "        attrValue.statusCode = :ruleform AND "
                                                                            + "        auth.classification = :classification AND "
                                                                            + "        auth.classifier = :classifier "),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS, query = "select ra from StatusCodeAttributeAuthorization ra "
                                                                                    + "WHERE ra.classifier = :classification "
                                                                                    + "AND ra.classifier = :classifier"),
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
                                                                           + "ORDER by n.parent.name, n.relationship.name, n.child.name") })
public class StatusCode extends
        ExistentialRuleform<StatusCode, StatusCodeNetwork> {

    public static final String       AGENCY_ATTRIBUTES_BY_CLASSIFICATION      = "statusCode.AgencyAttributesByClassification";
    public static final String       AUTHORIZED_AGENCY_ATTRIBUTES             = "statusCode.authorizedAttributes";

    public static final String       FIND_BY_NAME                             = "statusCode"
                                                                                + FIND_BY_NAME_SUFFIX;
    public static final String       FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS = "statusCode"
                                                                                + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
    public static final String       FIND_CLASSIFIED_ATTRIBUTE_VALUES         = "statusCode"
                                                                                + FIND_CLASSIFIED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String       FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS    = "statusCode"
                                                                                + FIND_GROUPED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String       GET_ALL_PARENT_RELATIONSHIPS             = "statusCode"
                                                                                + GET_ALL_PARENT_RELATIONSHIPS_SUFFIX;
    public static final String       GET_CHILD                                = "statusCode"
                                                                                + GET_CHILDREN_SUFFIX;
    public static final String       GET_CHILD_RULES_BY_RELATIONSHIP          = "statusCode"
                                                                                + GET_CHILD_RULES_BY_RELATIONSHIP_SUFFIX;
    public static final String       IS_TERMINAL_STATE                        = "statusCode.isTerminalState";
    public static final String       ORDERED_ATTRIBUTES                       = "statusCode.orderedAttributes";
    public static final String       QUALIFIED_ENTITY_NETWORK_RULES           = "statusCode.qualifiedEntityNetworkRules";
    public static final String       UNLINKED                                 = "statusCode"
                                                                                + UNLINKED_SUFFIX;

    private static final long        serialVersionUID                         = 1L;

    // bi-directional many-to-one association to AgencyAttribute
    @OneToMany(mappedBy = "statusCode", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<StatusCodeAttribute> attributes;

    @Column(name = "fail_parent")
    private Integer                  failParent                               = TRUE;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<StatusCodeNetwork>   networkByChild;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<StatusCodeNetwork>   networkByParent;

    @Column(name = "propagate_children")
    private Integer                  propagateChildren                        = TRUE;

    public StatusCode() {
    }

    /**
     * @param updatedBy
     */
    public StatusCode(Agency updatedBy) {
        super(updatedBy);
    }

    public StatusCode(UUID l, String name) {
        super(l, name);
    }

    /**
     * @param id
     */
    public StatusCode(UUID id) {
        super(id);
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

    public Set<StatusCodeAttribute> getAttributes() {
        return attributes;
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

    public Boolean getPropagateChildren() {
        return toBoolean(propagateChildren);
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
    public void link(Relationship r, StatusCode child, Agency updatedBy,
                     Agency inverseSoftware, EntityManager em) {
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
    }

    public void setAttributes(Set<StatusCodeAttribute> attributes) {
        this.attributes = attributes;
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
