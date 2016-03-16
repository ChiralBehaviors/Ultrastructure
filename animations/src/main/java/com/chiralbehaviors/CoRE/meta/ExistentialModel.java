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

import com.chiralbehaviors.CoRE.jooq.tables.ExistentialAttributeAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.ExistentialNetwork;
import com.chiralbehaviors.CoRE.jooq.tables.ExistentialNetworkAttribute;
import com.chiralbehaviors.CoRE.jooq.tables.ExistentialNetworkAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace;
import com.hellblazer.utils.Tuple;

/**
 *
 * The meta model for a networked ruleform.
 *
 * @author hhildebrand
 *
 */
public interface ExistentialModel {

    public abstract ExistentialAttributeRecord create(ExistentialRecord ruleform,
                                                      ExistentialRecord attribute,
                                                      ExistentialRecord updatedBy);

    /**
     * Create a new instance with the supplied aspects
     *
     * @param name
     *            The name of the new instance
     * @param description
     *            the description of the new instance
     * @return the new instance
     */
    public ExistentialRecord create(String name, String description);

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
    public ExistentialRecord create(String name, String description,
                                    Aspect aspect, ExistentialRecord updatedBy,
                                    Aspect... aspects);

    /**
     * Answer the list of attribute values of the attribute on the ruleform
     *
     * @param attribute
     * @return
     */
    public List<ExistentialAttributeRecord> getAttributeValues(ExistentialRecord ruleform,
                                                               ExistentialRecord attribute);

    public ExistentialRecord getSingleChild(ExistentialRecord parent,
                                            ExistentialRecord r);

    /**
     * Answer the list of relationships used in this ruleform's networks.
     *
     * @return
     */
    public List<ExistentialRecord> getUsedExistentialRecords();

    /**
     * Assign the attributes as authorized atrributes of the aspect
     *
     * @param aspect
     * @param attributes
     */
    void authorize(Aspect aspect, ExistentialRecord... attributes);

    void authorize(ExistentialRecord ruleform, ExistentialRecord relationship,
                   ExistentialRecord authorized);

    void authorize(ExistentialRecord ruleform, ExistentialRecord relationship,
                   List<ExistentialRecord> authorized);

    /**
     * Check the capability of the agencies on an attribute of a ruleform.
     */
    boolean checkCapability(List<ExistentialRecord> agencies,
                            ExistentialAttributeAuthorizationRecord stateAuth,
                            ExistentialRecord capability);

    /**
     * Check the capability of the agencies on an instance.
     */
    boolean checkCapability(List<ExistentialRecord> agencies,
                            ExistentialRecord instance,
                            ExistentialRecord capability);

    /**
     * Check the capability of the agencies on the authorized relationship of
     * the facet child relationship.
     */
    boolean checkCapability(List<ExistentialRecord> agencies,
                            ExistentialNetworkAuthorizationRecord auth,
                            ExistentialRecord capability);

    /**
     * Check the capability of the current principal on the facet.
     */
    boolean checkFacetCapability(ExistentialNetworkAuthorizationRecord facet,
                                 ExistentialRecord capability);

    /**
     * Create a new instance of the ExistentialRecord based on the provided
     * prototype
     *
     * @param prototype
     *            - the model for the new instance
     * @return the new instance
     */
    ExistentialRecord create(ExistentialRecord prototype);

    void deauthorize(ExistentialRecord ruleform, ExistentialRecord relationship,
                     ExistentialRecord authorized);

    void deauthorize(ExistentialRecord ruleform, ExistentialRecord relationship,
                     List<ExistentialRecord> authorized);

    /**
     * @param id
     * @return the ruleform with the specified id
     */
    ExistentialRecord find(UUID id);

    /**
     *
     * @return all existential ruleforms that exist for this model
     */
    List<ExistentialRecord> findAll();

    /**
     * @return the list of aspects representing all facets for the
     *         ExistentialRecord
     */
    List<Aspect> getAllFacets();

    /**
     * Answer the allowed values for an ExistentialRecord, classified by the
     * supplied aspect
     *
     * @param attribute
     *            - the ExistentialRecord
     * @param groupingExistentialRecord
     *            - the grouping ExistentialRecord
     * @return the List of allowed values for this attribute
     */
    <ValueType> List<ValueType> getAllowedValues(ExistentialRecord attribute,
                                                 ExistentialRecord groupingExistentialRecord);

    /**
     * Answer the allowed values for an ExistentialRecord, classified by the
     * supplied aspect
     *
     * @param attribute
     *            - the ExistentialRecord
     * @param aspect
     *            - the classifying aspect
     * @return the List of allowed values for this attribute
     */
    <ValueType> List<ValueType> getAllowedValues(ExistentialRecord attribute,
                                                 Aspect aspect);

    /**
     * Answer the aspect identified by the primary keys
     * 
     * @param classifier
     * @param classification
     * @return
     */

    Aspect getAspect(UUID classifier, UUID classification);

    /**
     * Answer the list of attribute authorizations that are classified by the
     * grouping agency
     *
     * @param groupingExistentialRecord
     * @return
     */
    List<ExistentialAttributeAuthorization> getAttributeAuthorizations(ExistentialRecord groupingExistentialRecord);

    /**
     * Answer the list of attribute authorizations that are classified by the
     * grouping agency, defined for the particular attribute
     *
     * @param groupingExistentialRecord
     * @param attribute
     * @return
     */
    List<ExistentialAttributeAuthorization> getAttributeAuthorizations(ExistentialRecord groupingExistentialRecord,
                                                                       ExistentialRecord attribute);

    /**
     * Answer the list of attribute authorizations that are classified by an
     * aspect, defined for the particular attribute
     *
     * @param aspect
     *            - the classifying aspect.
     * @param attribute
     * @return
     */
    List<ExistentialAttributeAuthorization> getAttributeAuthorizations(Aspect aspect,
                                                                       ExistentialRecord attribute);

    /**
     * Answer the list of attribute authorizations that are classified by an
     * aspect
     *
     * @param aspect
     *            - the classifying aspect.
     * @param includeGrouping
     * @return
     */
    List<ExistentialAttributeAuthorization> getAttributeAuthorizations(Aspect aspect,
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
    List<ExistentialAttributeAuthorization> getAttributeAuthorizations(ExistentialNetworkAuthorization facet,
                                                                       boolean includeGrouping);

    /**
     * Answer the list of existing attributes for the ruleform instance that are
     * authorized by the groupingExistentialRecord
     *
     * @param ruleform
     *            - the instance
     * @param groupingExistentialRecord
     *            - the classifying agency
     * @return the list of existing attributes authorized by this classifier
     */
    List<ExistentialAttributeRecord> getAttributesClassifiedBy(ExistentialRecord ruleform,
                                                               ExistentialRecord groupingExistentialRecord);

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
    List<ExistentialAttributeRecord> getAttributesClassifiedBy(ExistentialRecord ruleform,
                                                               Aspect aspect);

    /**
     * Answer the list of existing attributes for the ruleform instance that are
     * grouped by the given agency
     *
     * @param ruleform
     *            - the instance
     * @param groupingExistentialRecord
     *            - the agency
     * @return the list of existing attributes for this instance that are
     *         grouped by the given agency
     */
    List<ExistentialAttributeRecord> getAttributesGroupedBy(ExistentialRecord ruleform,
                                                            ExistentialRecord groupingExistentialRecord);

    ExistentialNetworkAttribute getAttributeValue(ExistentialNetwork edge,
                                                  ExistentialRecord attribute);

    ExistentialAttributeRecord getAttributeValue(ExistentialRecord ruleform,
                                                 ExistentialRecord attribute);

    ExistentialNetworkAttribute getAttributeValue(ExistentialRecord parent,
                                                  ExistentialRecord r,
                                                  ExistentialRecord child,
                                                  ExistentialRecord attribute);

    ExistentialRecord getAuthorized(ExistentialRecord ruleform,
                                    ExistentialRecord relationship);

    List<ExistentialRecord> getAllAuthorized(ExistentialRecord ruleform,
                                             ExistentialRecord relationship);

    /**
     * Answer the child that is connected to the parent via the relationship
     *
     * @param parent
     * @param relationship
     * @return the child that is connected to the parent via the relationship
     */
    ExistentialRecord getChild(ExistentialRecord parent,
                               ExistentialRecord relationship);

    /**
     *
     * @param parent
     * @param relationship
     * @return
     */
    List<ExistentialRecord> getChildren(ExistentialRecord parent,
                                        ExistentialRecord relationship);

    ExistentialNetworkAuthorizationRecord getFacetDeclaration(Aspect aspect);

    /**
     * Answer the list of network authorizations that represent a facet defined
     * in the workspace.
     * 
     * @param workspace
     * @return the list of facet network authorizations in the workspace
     */
    List<ExistentialNetworkAuthorizationRecord> getFacets(ExistentialRecord workspace);

    /**
     * Answer the non inferred child that is connected to the parent via the
     * relationship
     *
     * @param parent
     * @param relationship
     * @return the non inferred child that is connected to the parent via the
     *         relationship
     */
    ExistentialRecord getImmediateChild(ExistentialRecord parent,
                                        ExistentialRecord relationship);

    /**
     * 
     * @param parent
     * @param relationship
     * @param child
     * @return
     */
    ExistentialNetwork getImmediateChildLink(ExistentialRecord parent,
                                             ExistentialRecord relationship,
                                             ExistentialRecord child);

    /**
     *
     * @param parent
     * @param relationship
     * @return
     */
    List<ExistentialRecord> getImmediateChildren(ExistentialRecord parent,
                                                 ExistentialRecord relationship);

    /**
     *
     * @param parent
     * @param relationship
     * @return
     */
    List<ExistentialNetwork> getImmediateChildrenLinks(ExistentialRecord parent,
                                                       ExistentialRecord relationship);

    ExistentialNetwork getImmediateLink(ExistentialRecord parent,
                                        ExistentialRecord relationship,
                                        ExistentialRecord child);

    /**
     *
     * @param parent
     * @return
     */
    Collection<ExistentialNetwork> getImmediateNetworkEdges(ExistentialRecord parent);

    /**
     *
     * @param parent
     * @return
     */
    Collection<ExistentialRecord> getImmediateExistentialRecords(ExistentialRecord parent);

    /**
     * 
     * @param parent
     * @param relationship
     * @param child
     * @return
     */
    List<ExistentialRecord> getInferredChildren(ExistentialRecord parent,
                                                ExistentialRecord relationship);

    /**
     * @param parent
     * @param relationship
     * @return
     */
    List<ExistentialRecord> getInGroup(ExistentialRecord parent,
                                       ExistentialRecord relationship);

    List<ExistentialNetwork> getInterconnections(Collection<ExistentialRecord> parents,
                                                 Collection<ExistentialRecord> relationships,
                                                 Collection<ExistentialRecord> children);

    /**
     * 
     * @param aspect
     * @param includeGrouping
     * @return the list of network authorizations for this aspect
     */
    List<ExistentialNetworkAuthorizationRecord> getNetworkAuthorizations(Aspect aspect,
                                                                         boolean includeGrouping);

    /**
     *
     * @param parent
     * @param relationship
     * @return
     */
    List<ExistentialRecord> getNotInGroup(ExistentialRecord parent,
                                          ExistentialRecord relationship);

    /**
     *
     * @param parent
     * @return
     */
    Collection<ExistentialRecord> getTransitiveExistentialRecords(ExistentialRecord parent);

    /**
     * Initialize the ruleform with the classified attributes for this aspect
     * 
     * @param ruleform
     * @param aspect
     */
    void initialize(ExistentialRecord ruleform, Aspect aspect);

    /**
     * Initialize the ruleform with the classified attributes for this aspect,
     * record new objects in workspace
     * 
     * @param ruleform
     * @param aspect
     */
    void initialize(ExistentialRecord ruleform, Aspect aspect,
                    EditableWorkspace workspace);

    /**
     * @param instance
     * @param facet
     * @param principal
     */
    void initialize(ExistentialRecord instance,
                    ExistentialNetworkAuthorizationRecord facet,
                    ExistentialRecord principal);

    /**
     *
     * @param parent
     * @param relationship
     * @param child
     * @return
     */
    boolean isAccessible(ExistentialRecord parent,
                         ExistentialRecord relationship,
                         ExistentialRecord child);

    /**
     *
     * @param parent
     * @param r
     * @param child
     * @param updatedBy
     */
    Tuple<ExistentialNetwork, ExistentialNetwork> link(ExistentialRecord parent,
                                                       ExistentialRecord r,
                                                       ExistentialRecord child,
                                                       ExistentialRecord updatedBy);

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

    void setAuthorizedAgencies(ExistentialRecord ruleform,
                               ExistentialRecord relationship,
                               List<ExistentialRecord> authorized);

    void setAuthorized(ExistentialRecord ruleform,
                       ExistentialRecord relationship,
                       List<ExistentialRecord> authorized);

    void setAuthorizedExistentialRecords(ExistentialRecord ruleform,
                                         ExistentialRecord relationship,
                                         List<ExistentialRecord> authorized);

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
    void setImmediateChild(ExistentialRecord parent,
                           ExistentialRecord relationship,
                           ExistentialRecord child,
                           ExistentialRecord updatedBy);

    void unlink(ExistentialRecord parent, ExistentialRecord r,
                ExistentialRecord child);

    /**
     * @param parent
     * @param relationship
     */
    void unlinkImmediate(ExistentialRecord parent,
                         ExistentialRecord relationship);

}
