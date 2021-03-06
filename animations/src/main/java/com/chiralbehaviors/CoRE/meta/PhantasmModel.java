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

import java.util.List;
import java.util.UUID;

import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.EdgePropertyRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.EdgeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.EdgeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetPropertyRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
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

    public List<FacetRecord> getFacets(Product workspace);

    <T extends ExistentialRuleform> T create(ExistentialDomain domain,
                                             String name, String description,
                                             FacetRecord aspect,
                                             FacetRecord... aspects);

    /**
     * Answer the child that is connected to the parent via the relationship
     *
     * @param parent
     * @param relationship
     * @return the child that is connected to the parent via the relationship
     */
    ExistentialRuleform getChild(ExistentialRuleform parent,
                                 Relationship relationship,
                                 ExistentialDomain domain);

    /**
     *
     * @param parent
     * @param relationship
     * @return
     */
    List<ExistentialRuleform> getChildren(ExistentialRuleform parent,
                                          Relationship relationship,
                                          ExistentialDomain domain);

    /**
     *
     * @param parent
     * @param relationship
     * @return
     */
    List<EdgeRecord> getChildrenLinks(ExistentialRuleform parent,
                                                    Relationship relationship);

    List<ExistentialRuleform> getChildrenUuid(UUID id, UUID inverse,
                                              ExistentialDomain domain);

    /**
     * @param parent
     * @param relationship
     * @param classifier
     * @param classification
     * @param existentialDomain
     * @return
     */
    List<ExistentialRuleform> getConstrainedChildren(ExistentialRuleform parent,
                                                     Relationship relationship,
                                                     Relationship classifier,
                                                     ExistentialRuleform classification,
                                                     ExistentialDomain existentialDomain);

    EdgePropertyRecord getEdgeProperties(EdgeAuthorizationRecord edgeAuth,
                                         EdgeRecord edge);

    EdgePropertyRecord getEdgeProperties(ExistentialRuleform parent,
                                         EdgeAuthorizationRecord auth,
                                         ExistentialRuleform child);

    FacetRecord getFacetDeclaration(Relationship classifier,
                                    ExistentialRuleform classification);

    FacetPropertyRecord getFacetProperties(FacetRecord record,
                                           ExistentialRuleform existential);

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
                                          Relationship relationship,
                                          ExistentialDomain domain);

    /**
     *
     * @param parent
     * @param relationship
     * @param child
     * @return
     */
    EdgeRecord getImmediateChildLink(ExistentialRuleform parent,
                                                   Relationship relationship,
                                                   ExistentialRuleform child);

    /**
     *
     * @param parent
     * @param relationship
     * @param existentialDomain
     * @return
     */
    List<ExistentialRuleform> getImmediateChildren(ExistentialRuleform parent,
                                                   Relationship relationship,
                                                   ExistentialDomain existentialDomain);

    /**
     *
     * @param parent
     * @param relationship
     * @return
     */
    List<EdgeRecord> getImmediateChildrenLinks(ExistentialRuleform parent,
                                                             Relationship relationship,
                                                             ExistentialDomain domain);

    /**
     * 
     * @param parent
     * @param relationship
     * @param classifier
     * @param classification
     * @param existentialDomain
     * @return
     */
    List<ExistentialRuleform> getImmediateConstrainedChildren(ExistentialRuleform parent,
                                                              Relationship relationship,
                                                              Relationship classifier,
                                                              ExistentialRuleform classification,
                                                              ExistentialDomain existentialDomain);

    EdgeRecord getImmediateLink(ExistentialRuleform parent,
                                              Relationship relationship,
                                              ExistentialRuleform child);

    EdgeAuthorizationRecord getNetworkAuthorization(FacetRecord aspect,
                                                                  Relationship relationship,
                                                                  boolean includeGrouping);

    /**
     *
     * @param aspect
     * @param includeGrouping
     * @return the list of network authorizations for this aspect
     */
    List<EdgeAuthorizationRecord> getNetworkAuthorizations(FacetRecord aspect,
                                                                         boolean includeGrouping);

    /**
     *
     * @param parent
     * @param relationship
     * @return
     */
    List<ExistentialRuleform> getNotInGroup(ExistentialRuleform parent,
                                            Relationship relationship,
                                            ExistentialDomain domain);

    FacetPropertyRecord getProperties(ExistentialRuleform existential,
                                      FacetRecord facet);

    <T extends ExistentialRuleform> T getSingleChild(ExistentialRuleform parent,
                                                     Relationship r,
                                                     ExistentialDomain domain);

    /**
     * Initialize the ruleform with the classified attributes for this aspect
     *
     * @param ruleform
     * @param aspect
     */
    void initialize(ExistentialRuleform ruleform, FacetRecord aspect);

    /**
     * Initialize the ruleform with the classified attributes for this aspect,
     * record new objects in workspace
     *
     * @param ruleform
     * @param aspect
     */
    void initialize(ExistentialRuleform ruleform, FacetRecord aspect,
                    EditableWorkspace workspace);

    /**
     *
     * @param parent
     * @param relationship
     * @param child
     * @return
     */
    boolean isAccessible(UUID parent, UUID relationship, UUID child);

    /**
     *
     * @param parent
     * @param r
     * @param child
     */
    Tuple<EdgeRecord, EdgeRecord> link(ExistentialRuleform parent,
                                                                   Relationship r,
                                                                   ExistentialRuleform child);

    /**
     * Sets the child of the immediate relationship defined by the parent and
     * relationship. This is done by first deleting the edge, and then inserting
     * the edge with the new child
     *
     * @param parent
     * @param relationship
     * @param child
     */
    void setImmediateChild(ExistentialRuleform parent,
                           Relationship relationship,
                           ExistentialRuleform child);

    void unlink(ExistentialRuleform parent, Relationship r,
                ExistentialRuleform child);

    void unlinkImmediate(ExistentialRuleform parent, Relationship relationship);
}
