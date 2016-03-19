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

package com.chiralbehaviors.CoRE.meta;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkRecord;
import com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace;
import com.hellblazer.utils.Tuple;

/**
 *
 * The meta model for a networked ruleform.
 *
 * @author hhildebrand
 *
 */
public interface ExistentialModel<RuleForm extends ExistentialRuleform> {

    /**
     * Assign the attributes as authorized atrributes of the aspect
     *
     * @param aspect
     * @param attributes
     */
    void authorize(Aspect<RuleForm> aspect, Attribute... attributes);

    void authorize(RuleForm ruleform, Relationship relationship,
                   ExistentialRuleform authorized);

    void authorizeAll(RuleForm ruleform, Relationship relationship,
                      List<? extends ExistentialRuleform> authorized);

    void authorizeSingular(RuleForm ruleform, Relationship relationship,
                           ExistentialRuleform authorized);

    /**
     * Check the capability of the current principal on an attribute of a
     * ruleform.
     */
    boolean checkCapability(ExistentialAttributeAuthorizationRecord stateAuth,
                            Relationship capability);

    /**
     * Check the capability of the current principal on the authorized
     * relationship of the facet child relationship.
     */
    boolean checkCapability(ExistentialNetworkAuthorizationRecord auth,
                            Relationship capability);

    /**
     * Check the capability of the agencies on an attribute of a ruleform.
     */
    boolean checkCapability(List<Agency> agencies,
                            ExistentialAttributeAuthorizationRecord stateAuth,
                            Relationship capability);

    /**
     * Check the capability of the agencies on the authorized relationship of
     * the facet child relationship.
     */
    boolean checkCapability(List<Agency> agencies,
                            ExistentialNetworkAuthorizationRecord auth,
                            Relationship capability);

    /**
     * Check the capability of the agencies on an instance.
     */
    boolean checkCapability(List<Agency> agencies, RuleForm instance,
                            Relationship capability);

    /**
     * Check the capability of the current principal on an instance.
     */
    boolean checkCapability(RuleForm instance, Relationship capability);

    /**
     * Check the capability of the current principal on the facet.
     */
    boolean checkFacetCapability(ExistentialNetworkAuthorizationRecord facet,
                                 Relationship capability);

    /**
     * Check the capability of the agencies on the facet.
     */
    boolean checkFacetCapability(List<Agency> agencies,
                                 ExistentialNetworkAuthorizationRecord facet,
                                 Relationship capability);

    /**
     * Check the capability of the current principal on an attribute of the
     * authorized relationship of the facet child relationship.
     */
    boolean checkNetworkCapability(ExistentialAttributeAuthorizationRecord stateAuth,
                                   Relationship capability);

    /**
     * Check the capability of the agencies on an attribute of the authorized
     * relationship of the facet child relationship.
     */
    boolean checkNetworkCapability(List<Agency> agencies,
                                   ExistentialAttributeAuthorizationRecord stateAuth,
                                   Relationship capability);

    /**
     * Create a new instance of the RuleForm based on the provided prototype
     *
     * @param prototype
     *            - the model for the new instance
     * @return the new instance
     */
    RuleForm create(RuleForm prototype);

    abstract ExistentialAttributeRecord create(RuleForm ruleform,
                                               Attribute attribute,
                                               Agency updatedBy);

    /**
     * Create a new instance with the supplied aspects
     *
     * @param name
     *            The name of the new instance
     * @param description
     *            the description of the new instance
     * @return the new instance
     */
    RuleForm create(String name, String description);

    /**
     * Create a new instance with the supplied aspects
     *
     * @param name
     *            The name of the new instance
     * @param description
     *            the description of the new instance
     * @param updatedBy
     * @param aspects
     *            - the initial aspects of the instance
     * @return the new instance
     */
    RuleForm create(String name, String description, Aspect<RuleForm> aspect,
                    Agency updatedBy,
                    @SuppressWarnings("unchecked") Aspect<RuleForm>... aspects);

    void deauthorize(RuleForm ruleform, Relationship relationship,
                     ExistentialRuleform authorized);

    void deauthorizeAll(RuleForm ruleform, Relationship relationship,
                        List<? extends ExistentialRuleform> authorized);

    /**
     * @param id
     * @return the ruleform with the specified id
     */
    RuleForm find(UUID id);

    /**
     *
     * @return all existential ruleforms that exist for this model
     */
    List<RuleForm> findAll();

    /**
     * @return the list of aspects representing all facets for the RuleForm
     */
    List<Aspect<RuleForm>> getAllFacets();

    /**
     * Answer the list of attribute authorizations that are classified by the
     * grouping agency, defined for the particular attribute
     *
     * @param groupingAgency
     * @param attribute
     * @return
     */
    List<ExistentialAttributeAuthorizationRecord> getAttributeAuthorizations(Agency groupingAgency,
                                                                             Attribute attribute);

    /**
     * Answer the list of attribute authorizations that are classified by an
     * aspect, defined for the particular attribute
     *
     * @param aspect
     *            - the classifying aspect.
     * @param attribute
     * @return
     */
    List<ExistentialAttributeAuthorizationRecord> getAttributeAuthorizations(Aspect<RuleForm> aspect,
                                                                             Attribute attribute);

    /**
     * Answer the list of attribute authorizations that are classified by an
     * aspect
     *
     * @param aspect
     *            - the classifying aspect.
     * @param includeGrouping
     * @return
     */
    List<ExistentialAttributeAuthorizationRecord> getAttributeAuthorizations(Aspect<RuleForm> aspect,
                                                                             boolean includeGrouping);

    /**
     * Answer the list of attribute authorizations that are classified by a
     * facet
     *
     * @param facet
     *            - the classifying aspect.
     * @param includeGrouping
     * @return
     */
    List<ExistentialAttributeAuthorizationRecord> getAttributeAuthorizations(ExistentialNetworkAuthorizationRecord facet,
                                                                             boolean includeGrouping);

    /**
     * Answer the list of existing attributes for the ruleform instance that are
     * authorized by the classifier relationship described by the aspect
     *
     * @param ruleform
     *            - the instance
     * @param aspect
     *            - the classifying aspect
     * @return the list of existing attributes authorized by this classifier
     */
    List<ExistentialAttributeRecord> getAttributesClassifiedBy(RuleForm ruleform,
                                                               Aspect<RuleForm> aspect);

    /**
     * Answer the list of existing attributes for the ruleform instance that are
     * grouped by the given agency
     *
     * @param ruleform
     *            - the instance
     * @param groupingAgency
     *            - the agency
     * @return the list of existing attributes for this instance that are
     *         grouped by the given agency
     */
    List<ExistentialAttributeRecord> getAttributesGroupedBy(RuleForm ruleform,
                                                            Agency groupingAgency);

    ExistentialNetworkAttributeRecord getAttributeValue(ExistentialNetworkRecord edge,
                                                        Attribute attribute);

    ExistentialAttributeRecord getAttributeValue(RuleForm ruleform,
                                                 Attribute attribute);

    ExistentialNetworkAttributeRecord getAttributeValue(RuleForm parent,
                                                        Relationship r,
                                                        RuleForm child,
                                                        Attribute attribute);

    /**
     * Answer the list of attribute values of the attribute on the ruleform
     *
     * @param attribute
     * @return
     */
    List<ExistentialAttributeRecord> getAttributeValues(RuleForm ruleform,
                                                        Attribute attribute);

    <T extends ExistentialRuleform> List<T> getAllAuthorized(RuleForm ruleform,
                                                             Relationship relationship);

    <T extends ExistentialRuleform> T getAuthorized(RuleForm ruleform,
                                                    Relationship relationship);

    /**
     * Answer the child that is connected to the parent via the relationship
     *
     * @param parent
     * @param relationship
     * @return the child that is connected to the parent via the relationship
     */
    RuleForm getChild(RuleForm parent, Relationship relationship);

    /**
     *
     * @param parent
     * @param relationship
     * @return
     */
    List<RuleForm> getChildren(RuleForm parent, Relationship relationship);

    ExistentialNetworkAuthorizationRecord getFacetDeclaration(@SuppressWarnings("rawtypes") Aspect aspect);

    /**
     * Answer the list of network authorizations that represent a facet defined
     * in the workspace.
     * 
     * @param workspace
     * @return the list of facet network authorizations in the workspace
     */
    List<ExistentialNetworkAuthorizationRecord> getFacets(Product workspace);

    /**
     * Answer the non inferred child that is connected to the parent via the
     * relationship
     *
     * @param parent
     * @param relationship
     * @return the non inferred child that is connected to the parent via the
     *         relationship
     */
    RuleForm getImmediateChild(RuleForm parent, Relationship relationship);

    /**
     * 
     * @param parent
     * @param relationship
     * @param child
     * @return
     */
    ExistentialNetworkRecord getImmediateChildLink(RuleForm parent,
                                                   Relationship relationship,
                                                   RuleForm child);

    /**
     *
     * @param parent
     * @param relationship
     * @return
     */
    List<RuleForm> getImmediateChildren(RuleForm parent,
                                        Relationship relationship);

    /**
     *
     * @param parent
     * @param relationship
     * @return
     */
    List<ExistentialNetworkRecord> getImmediateChildrenLinks(RuleForm parent,
                                                             Relationship relationship);

    ExistentialNetworkRecord getImmediateLink(RuleForm parent,
                                              Relationship relationship,
                                              RuleForm child);

    /**
     *
     * @param parent
     * @return
     */
    Collection<ExistentialNetworkRecord> getImmediateNetworkEdges(RuleForm parent);

    /**
     *
     * @param parent
     * @return
     */
    Collection<Relationship> getImmediateRelationships(RuleForm parent);

    /**
     * 
     * @param parent
     * @param relationship
     * @param child
     * @return
     */
    List<RuleForm> getInferredChildren(RuleForm parent,
                                       Relationship relationship);

    /**
     * @param parent
     * @param relationship
     * @return
     */
    List<RuleForm> getInGroup(RuleForm parent, Relationship relationship);

    List<ExistentialNetworkRecord> getInterconnections(Collection<RuleForm> parents,
                                                       Collection<Relationship> relationships,
                                                       Collection<RuleForm> children);

    /**
     * 
     * @param aspect
     * @param includeGrouping
     * @return the list of network authorizations for this aspect
     */
    List<ExistentialNetworkAuthorizationRecord> getNetworkAuthorizations(Aspect<RuleForm> aspect,
                                                                         boolean includeGrouping);

    /**
     *
     * @param parent
     * @param relationship
     * @return
     */
    List<RuleForm> getNotInGroup(RuleForm parent, Relationship relationship);

    RuleForm getSingleChild(RuleForm parent, Relationship r);

    /**
     * Initialize the ruleform with the classified attributes for this aspect
     * 
     * @param ruleform
     * @param aspect
     */
    void initialize(RuleForm ruleform, Aspect<RuleForm> aspect);

    /**
     * Initialize the ruleform with the classified attributes for this aspect,
     * record new objects in workspace
     * 
     * @param ruleform
     * @param aspect
     */
    void initialize(RuleForm ruleform, Aspect<RuleForm> aspect,
                    EditableWorkspace workspace);

    /**
     * @param instance
     * @param facet
     * @param principal
     */
    void initialize(RuleForm instance,
                    ExistentialNetworkAuthorizationRecord facet,
                    Agency principal);

    /**
     *
     * @param parent
     * @param relationship
     * @param child
     * @return
     */
    boolean isAccessible(RuleForm parent, Relationship relationship,
                         RuleForm child);

    /**
     *
     * @param parent
     * @param r
     * @param child
     * @param updatedBy
     */
    Tuple<ExistentialNetworkRecord, ExistentialNetworkRecord> link(RuleForm parent,
                                                                   Relationship r,
                                                                   RuleForm child,
                                                                   Agency updatedBy);

    /**
     * Propagate the network inferences based on the tracked additions,
     * deletions and modifications
     * 
     * @param initial
     *
     * @throws SQLException
     */
    void propagate(boolean initial);

    /**
     * Sets a value for an attribute after validating it first
     * 
     * @param value
     */
    void setAttributeValue(ExistentialAttributeRecord value);

    void setAuthorized(RuleForm ruleform, Relationship relationship,
                       List<? extends ExistentialRuleform> authorized);

    /**
     * Sets the child of the immediate relationship defined by the parent and
     * relationship. This is done by first deleting the edge, and then inserting
     * the edge with the new child
     * 
     * @param parent
     * @param relationship
     * @param child
     * @param updatedBy
     */
    void setImmediateChild(RuleForm parent, Relationship relationship,
                           RuleForm child, Agency updatedBy);

    void unlink(RuleForm parent, Relationship r, RuleForm child);

    /**
     * @param parent
     * @param relationship
     */
    void unlinkImmediate(RuleForm parent, Relationship relationship);

}
