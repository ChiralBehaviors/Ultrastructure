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

import java.util.Collection;
import java.util.List;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
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
 * The meta model for phantasms.
 *
 * @author hhildebrand
 *
 */
public interface PhantasmModel {

    /**
     * Assign the attributes as authorized atrributes of the aspect
     *
     * @param aspect
     * @param attributes
     */
    void authorize(Aspect<ExistentialRuleform> aspect, Attribute... attributes);

    void authorize(ExistentialRuleform ruleform, Relationship relationship,
                   ExistentialRuleform authorized);

    void authorizeAll(ExistentialRuleform ruleform, Relationship relationship,
                      List<? extends ExistentialRuleform> authorized);

    void authorizeSingular(ExistentialRuleform ruleform,
                           Relationship relationship,
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
     * Check the capability of the current principal on an instance.
     */
    boolean checkCapability(ExistentialRuleform instance,
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
    boolean checkCapability(List<Agency> agencies, ExistentialRuleform instance,
                            Relationship capability);

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

    ExistentialAttributeRecord create(ExistentialRuleform ruleform,
                                      Attribute attribute, Agency updatedBy);

    void deauthorize(ExistentialRuleform ruleform, Relationship relationship,
                     ExistentialRuleform authorized);

    void deauthorizeAll(ExistentialRuleform ruleform, Relationship relationship,
                        List<? extends ExistentialRuleform> authorized);

    <T extends ExistentialRuleform> List<T> getAllAuthorized(ExistentialRuleform ruleform,
                                                             Relationship relationship);

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
    List<ExistentialAttributeAuthorizationRecord> getAttributeAuthorizations(Aspect<ExistentialRuleform> aspect,
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
    List<ExistentialAttributeAuthorizationRecord> getAttributeAuthorizations(Aspect<ExistentialRuleform> aspect,
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
    List<ExistentialAttributeRecord> getAttributesClassifiedBy(ExistentialRuleform ruleform,
                                                               Aspect<ExistentialRuleform> aspect);

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
    List<ExistentialAttributeRecord> getAttributesGroupedBy(ExistentialRuleform ruleform,
                                                            Agency groupingAgency);

    ExistentialNetworkAttributeRecord getAttributeValue(ExistentialNetworkRecord edge,
                                                        Attribute attribute);

    ExistentialAttributeRecord getAttributeValue(ExistentialRuleform ruleform,
                                                 Attribute attribute);

    ExistentialNetworkAttributeRecord getAttributeValue(ExistentialRuleform parent,
                                                        Relationship r,
                                                        ExistentialRuleform child,
                                                        Attribute attribute);

    /**
     * Answer the list of attribute values of the attribute on the ruleform
     *
     * @param attribute
     * @return
     */
    List<ExistentialAttributeRecord> getAttributeValues(ExistentialRuleform ruleform,
                                                        Attribute attribute);

    <T extends ExistentialRuleform> T getAuthorized(ExistentialRuleform ruleform,
                                                    Relationship relationship);

    /**
     * Answer the child that is connected to the parent via the relationship
     *
     * @param parent
     * @param relationship
     * @return the child that is connected to the parent via the relationship
     */
    ExistentialRuleform getChild(ExistentialRuleform parent,
                                 Relationship relationship);

    /**
     *
     * @param parent
     * @param relationship
     * @return
     */
    List<ExistentialRuleform> getChildren(ExistentialRuleform parent,
                                          Relationship relationship);

    ExistentialNetworkAuthorizationRecord getFacetDeclaration(@SuppressWarnings("rawtypes") Aspect aspect);

    /**
     * Answer the non inferred child that is connected to the parent via the
     * relationship
     *
     * @param parent
     * @param relationship
     * @return the non inferred child that is connected to the parent via the
     *         relationship
     */
    ExistentialRuleform getImmediateChild(ExistentialRuleform parent,
                                          Relationship relationship);

    /**
     * 
     * @param parent
     * @param relationship
     * @param child
     * @return
     */
    ExistentialNetworkRecord getImmediateChildLink(ExistentialRuleform parent,
                                                   Relationship relationship,
                                                   ExistentialRuleform child);

    /**
     *
     * @param parent
     * @param relationship
     * @return
     */
    List<ExistentialRuleform> getImmediateChildren(ExistentialRuleform parent,
                                                   Relationship relationship);

    /**
     *
     * @param parent
     * @param relationship
     * @return
     */
    List<ExistentialNetworkRecord> getImmediateChildrenLinks(ExistentialRuleform parent,
                                                             Relationship relationship);

    ExistentialNetworkRecord getImmediateLink(ExistentialRuleform parent,
                                              Relationship relationship,
                                              ExistentialRuleform child);

    /**
     *
     * @param parent
     * @return
     */
    Collection<ExistentialNetworkRecord> getImmediateNetworkEdges(ExistentialRuleform parent);

    /**
     *
     * @param parent
     * @return
     */
    Collection<Relationship> getImmediateRelationships(ExistentialRuleform parent);

    /**
     * 
     * @param parent
     * @param relationship
     * @param child
     * @return
     */
    List<ExistentialRuleform> getInferredChildren(ExistentialRuleform parent,
                                                  Relationship relationship);

    /**
     * @param parent
     * @param relationship
     * @return
     */
    List<ExistentialRuleform> getInGroup(ExistentialRuleform parent,
                                         Relationship relationship);

    List<ExistentialNetworkRecord> getInterconnections(Collection<ExistentialRuleform> parents,
                                                       Collection<Relationship> relationships,
                                                       Collection<ExistentialRuleform> children);

    /**
     * 
     * @param aspect
     * @param includeGrouping
     * @return the list of network authorizations for this aspect
     */
    List<ExistentialNetworkAuthorizationRecord> getNetworkAuthorizations(Aspect<? extends ExistentialRuleform> aspect,
                                                                         boolean includeGrouping);

    /**
     *
     * @param parent
     * @param relationship
     * @return
     */
    List<ExistentialRuleform> getNotInGroup(ExistentialRuleform parent,
                                            Relationship relationship);

    ExistentialRuleform getSingleChild(ExistentialRuleform parent,
                                       Relationship r);

    /**
     * Initialize the ruleform with the classified attributes for this aspect
     * 
     * @param ruleform
     * @param aspect
     */
    void initialize(ExistentialRuleform ruleform,
                    Aspect<ExistentialRuleform> aspect);

    /**
     * Initialize the ruleform with the classified attributes for this aspect,
     * record new objects in workspace
     * 
     * @param ruleform
     * @param aspect
     */
    void initialize(ExistentialRuleform ruleform,
                    Aspect<ExistentialRuleform> aspect,
                    EditableWorkspace workspace);

    /**
     * @param instance
     * @param facet
     * @param principal
     */
    void initialize(ExistentialRuleform instance,
                    ExistentialNetworkAuthorizationRecord facet,
                    Agency principal);

    /**
     *
     * @param parent
     * @param relationship
     * @param child
     * @return
     */
    boolean isAccessible(ExistentialRuleform parent, Relationship relationship,
                         ExistentialRuleform child);

    /**
     *
     * @param parent
     * @param r
     * @param child
     * @param updatedBy
     */
    Tuple<ExistentialNetworkRecord, ExistentialNetworkRecord> link(ExistentialRuleform parent,
                                                                   Relationship r,
                                                                   ExistentialRuleform child,
                                                                   Agency updatedBy);

    /**
     * Sets a value for an attribute after validating it first
     * 
     * @param value
     */
    void setAttributeValue(ExistentialAttributeRecord value);

    void setAuthorized(ExistentialRuleform ruleform, Relationship relationship,
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
    void setImmediateChild(ExistentialRuleform parent,
                           Relationship relationship, ExistentialRuleform child,
                           Agency updatedBy);

    void unlink(ExistentialRuleform parent, Relationship r,
                ExistentialRuleform child);

    void unlinkImmediate(ExistentialRuleform parent, Relationship relationship);
}
