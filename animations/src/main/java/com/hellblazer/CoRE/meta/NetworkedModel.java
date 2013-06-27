/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.hellblazer.CoRE.meta;

import java.util.List;

import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.AttributeValue;
import com.hellblazer.CoRE.attribute.ClassifiedAttributeAuthorization;
import com.hellblazer.CoRE.network.Aspect;
import com.hellblazer.CoRE.network.Facet;
import com.hellblazer.CoRE.network.Networked;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

/**
 * 
 * The meta model for a networked ruleform.
 * 
 * @author hhildebrand
 * 
 */
public interface NetworkedModel<RuleForm extends Networked<RuleForm, ?>, AttributeAuthorization extends ClassifiedAttributeAuthorization<RuleForm>, AttributeType extends AttributeValue<?>> {

    /**
     * Create a new instance with the supplied aspects
     * 
     * @param name
     *            The name of the new instance
     * @param description
     *            the description of the new instance
     * @param aspects
     *            - the initial aspects of the instance
     * 
     * @return the new instance
     */
    public RuleForm create(String name, String description,
                           Aspect<RuleForm> aspect, Aspect<RuleForm>... aspects);

    /**
     * Answer the list of unlinked
     * 
     * @return
     */
    public List<RuleForm> getUnlinked();

    /**
     * Answer the list of relationships used in this ruleform's networks.
     * 
     * @return
     */
    public List<Relationship> getUsedRelationships();

    /**
     * Assign the attributes as authorized atrributes of the aspect
     * 
     * @param aspect
     * @param attributes
     */
    void authorize(Aspect<RuleForm> aspect, Attribute... attributes);

    /**
     * This function removes all deduced rules from the given network table.
     * While this can be simply done by 'DELETE FROM
     * <table>
     * WHERE ...', this function adds some error checking, and helpfully resets
     * the sequence backing the 'id' column to reclaim the ids used by the
     * deleted inferred rules.
     * 
     * This function pins the 'Inverse Software' resource.
     */
    void clearDeducedRules();

    /**
     * Create a new instance of the RuleForm based on the provided prototype
     * 
     * @param prototype
     *            - the model for the new instance
     * @return the new instance
     */
    RuleForm create(RuleForm prototype);

    void generateInverses();

    /**
     * Answer the allowed values for an Attribute, classified by the supplied
     * aspect
     * 
     * @param attribute
     *            - the Attribute
     * @param aspect
     *            - the classifying aspect
     * @return the List of allowed values for this attribute
     */
    <ValueType> List<ValueType> getAllowedValues(Attribute attribute,
                                                 Aspect<RuleForm> aspect);

    /**
     * Answer the allowed values for an Attribute, classified by the supplied
     * aspect
     * 
     * @param attribute
     *            - the Attribute
     * @param groupingResource
     *            - the grouping Resource
     * @return the List of allowed values for this attribute
     */
    <ValueType> List<ValueType> getAllowedValues(Attribute attribute,
                                                 Resource groupingResource);

    /**
     * Answer the list of attribute authorizations that are classified by an
     * aspect
     * 
     * @param aspect
     *            - the classifying aspect.
     * @return
     */
    List<AttributeAuthorization> getAttributeAuthorizations(Aspect<RuleForm> aspect);

    /**
     * Answer the list of attribute authorizations that are classified by an
     * aspect, defined for the particular attribute
     * 
     * @param aspect
     *            - the classifying aspect.
     * @param attribute
     * @return
     */
    List<AttributeAuthorization> getAttributeAuthorizations(Aspect<RuleForm> aspect,
                                                            Attribute attribute);

    /**
     * Answer the list of attribute authorizations that are classified by the
     * grouping resource
     * 
     * @param groupingResource
     * @return
     */
    List<AttributeAuthorization> getAttributeAuthorizations(Resource groupingResource);

    /**
     * Answer the list of attribute authorizations that are classified by the
     * grouping resource, defined for the particular attribute
     * 
     * @param groupingResource
     * @param attribute
     * @return
     */
    List<AttributeAuthorization> getAttributeAuthorizations(Resource groupingResource,
                                                            Attribute attribute);

    /**
     * Answer the list of existing attributes for the ruleform instance that are
     * authorized by the classification relationship described by the aspect
     * 
     * @param ruleform
     *            - the instance
     * @param aspect
     *            - the classifying aspect
     * @return the list of existing attributes authorized by this classification
     */
    List<AttributeType> getAttributesClassifiedBy(RuleForm ruleform,
                                                  Aspect<RuleForm> aspect);

    /**
     * Answer the list of existing attributes for the ruleform instance that are
     * authorized by the groupingResource
     * 
     * @param ruleform
     *            - the instance
     * @param groupingResource
     *            - the classifying resource
     * @return the list of existing attributes authorized by this classification
     */
    List<AttributeType> getAttributesClassifiedBy(RuleForm ruleform,
                                                  Resource groupingResource);

    /**
     * Answer the list of existing attributes for the ruleform instance that are
     * grouped by the given resource
     * 
     * @param ruleform
     *            - the instance
     * @param groupingResource
     *            - the resource
     * @return the list of existing attributes for this instance that are
     *         grouped by the given resource
     */
    List<AttributeType> getAttributesGroupedBy(RuleForm ruleform,
                                               Resource groupingResource);

    /**
     * 
     * @param parent
     * @param r
     * @return
     */
    RuleForm getChild(RuleForm parent, Relationship r);

    /**
     * Answer the Facet of the ruleform instance containing the authorized
     * attributes as classified
     * 
     * @param ruleform
     *            - the instance
     * @param classifier
     *            - the parent ruleform
     * @param classification
     *            - the classifying relationship
     * @return
     */
    Facet<RuleForm, AttributeType> getFacet(RuleForm ruleform,
                                            Aspect<RuleForm> aspect);

    /**
     * @param parent
     * @param relationship
     * @return
     */
    List<RuleForm> getInGroup(RuleForm parent, Relationship relationship);

    /**
     * 
     * @param parent
     * @param relationship
     * @return
     */
    List<RuleForm> getNotInGroup(RuleForm parent, Relationship relationship);

    /**
     * 
     * @param parent
     * @param r
     * @param child
     * @param updatedBy
     */
    void link(RuleForm parent, Relationship r, RuleForm child,
              Resource updatedBy);

    /**
     * Propagate the network inferences based on the tracked additions,
     * deletions and modifications
     */
    void propagate();

    /**
     * Propagate the deductions for the inserted network relationship
     * 
     * @param parent
     * @param relationship
     * @param child
     */
    void propagate(RuleForm parent, Relationship relationship, RuleForm child);

    /**
     * Track the added network edge
     * 
     * @param ruleform
     */
    void trackNetworkEdgeAdded(Long ruleform);

    /**
     * Track the deleted network edge
     * 
     * @param parent
     * @param relationship TODO
     */
    void networkEdgeDeleted(long parent, long relationship);

    /**
     * Track the modifed network edge
     * 
     * @param oldParent
     * @param oldRelationship
     * @param oldChild
     * @param newParent
     * @param newRelationship
     * @param newChild
     */
    void trackNetworkEdgeModified(Long oldParent, Long oldRelationship, Long oldChild,
                       Long newParent, Long newRelationship, Long newChild);
}
