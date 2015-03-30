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

package com.chiralbehaviors.CoRE.meta;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.attribute.ClassifiedAttributeAuthorization;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Facet;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 *
 * The meta model for a networked ruleform.
 *
 * @author hhildebrand
 *
 */
public interface NetworkedModel<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>, AttributeAuthorization extends ClassifiedAttributeAuthorization<RuleForm>, AttributeType extends AttributeValue<RuleForm>> {

    /**
     * Create a new instance with the supplied aspect
     *
     * @param name
     *            The name of the new instance
     * @param description
     *            the description of the new instance
     * @param aspect
     *            - the initial aspect of the instance
     *
     * @return the new instance
     */
    public Facet<RuleForm, AttributeType> create(String name,
                                                 String description,
                                                 Aspect<RuleForm> aspect);

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
    public RuleForm create(String name,
                           String description,
                           Aspect<RuleForm> aspect,
                           @SuppressWarnings("unchecked") Aspect<RuleForm>... aspects);

    public RuleForm getSingleChild(RuleForm parent, Relationship r);

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
     * Create a new instance of the RuleForm based on the provided prototype
     *
     * @param prototype
     *            - the model for the new instance
     * @return the new instance
     */
    RuleForm create(RuleForm prototype);

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
     * Answer the allowed values for an Attribute, classified by the supplied
     * aspect
     *
     * @param attribute
     *            - the Attribute
     * @param groupingAgency
     *            - the grouping Agency
     * @return the List of allowed values for this attribute
     */
    <ValueType> List<ValueType> getAllowedValues(Attribute attribute,
                                                 Agency groupingAgency);

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
     * Answer the list of attribute authorizations that are classified by the
     * grouping agency
     *
     * @param groupingAgency
     * @return
     */
    List<AttributeAuthorization> getAttributeAuthorizations(Agency groupingAgency);

    /**
     * Answer the list of attribute authorizations that are classified by the
     * grouping agency, defined for the particular attribute
     *
     * @param groupingAgency
     * @param attribute
     * @return
     */
    List<AttributeAuthorization> getAttributeAuthorizations(Agency groupingAgency,
                                                            Attribute attribute);

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
     * Answer the list of existing attributes for the ruleform instance that are
     * authorized by the groupingAgency
     *
     * @param ruleform
     *            - the instance
     * @param groupingAgency
     *            - the classifying agency
     * @return the list of existing attributes authorized by this classification
     */
    List<AttributeType> getAttributesClassifiedBy(RuleForm ruleform,
                                                  Agency groupingAgency);

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
     * grouped by the given agency
     *
     * @param ruleform
     *            - the instance
     * @param groupingAgency
     *            - the agency
     * @return the list of existing attributes for this instance that are
     *         grouped by the given agency
     */
    List<AttributeType> getAttributesGroupedBy(RuleForm ruleform,
                                               Agency groupingAgency);

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
     * @return
     */
    List<RuleForm> getImmediateChildren(RuleForm parent,
                                        Relationship relationship);

    /**
     *
     * @param parent
     * @return
     */
    Collection<Network> getImmediateNetworkEdges(RuleForm parent);

    /**
     *
     * @param parent
     * @return
     */
    Collection<Relationship> getImmediateRelationships(RuleForm parent);

    /**
     * @param parent
     * @param relationship
     * @return
     */
    List<RuleForm> getInGroup(RuleForm parent, Relationship relationship);

    List<Network> getInterconnections(Collection<RuleForm> parents,
                                      Collection<Relationship> relationships,
                                      Collection<RuleForm> children);

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
     * @return
     */
    Collection<Relationship> getTransitiveRelationships(RuleForm parent);

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
    void link(RuleForm parent, Relationship r, RuleForm child, Agency updatedBy);

    /**
     * Propagate the network inferences based on the tracked additions,
     * deletions and modifications
     *
     * @throws SQLException
     */
    void propagate();
}
