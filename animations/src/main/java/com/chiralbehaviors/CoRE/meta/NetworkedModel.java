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
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.NetworkAttribute;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.product.EntityRelationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.relationship.Relationship;

/**
 *
 * The meta model for a networked ruleform.
 *
 * @author hhildebrand
 *
 */
public interface NetworkedModel<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>, AttributeAuth extends AttributeAuthorization<RuleForm, Network>, AttributeType extends AttributeValue<RuleForm>> {

    public abstract AttributeType create(RuleForm ruleform,
                                         Attribute attribute, Agency updatedBy);

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
    public RuleForm create(String name,
                           String description,
                           Aspect<RuleForm> aspect,
                           Agency updatedBy,
                           @SuppressWarnings("unchecked") Aspect<RuleForm>... aspects);

    /**
     * Answer the list of attribute values of the attribute on the ruleform
     *
     * @param attribute
     * @return
     */
    public List<AttributeType> getAttributeValues(RuleForm ruleform,
                                                  Attribute attribute);

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

    void authorize(RuleForm ruleform, Relationship relationship,
                   Agency authorized);

    void authorize(RuleForm ruleform, Relationship relationship,
                   Location authorized);

    void authorize(RuleForm ruleform, Relationship relationship,
                   Product authorized);

    <T extends EntityRelationship> List<T> authorize(RuleForm ruleform, Relationship relationship,
                   Relationship authorized);

    void authorizeAgencies(RuleForm ruleform, Relationship relationship,
                           List<Agency> authorized);

    void authorizeLocations(RuleForm ruleform, Relationship relationship,
                            List<Location> authorized);

    void authorizeProducts(RuleForm ruleform, Relationship relationship,
                           List<Product> authorized);

    <T extends EntityRelationship> List<T> authorizeRelationships(RuleForm ruleform, Relationship relationship,
                                List<Relationship> authorized);

    void authorizeSingular(RuleForm ruleform, Relationship relationship,
                           Agency authorized);

    void authorizeSingular(RuleForm ruleform, Relationship relationship,
                           Location authorized);

    void authorizeSingular(RuleForm ruleform, Relationship relationship,
                           Product authorized);

    <T extends EntityRelationship> List<T> authorizeSingular(RuleForm ruleform, Relationship relationship,
                           Relationship authorized);

    /**
     * Create a new instance of the RuleForm based on the provided prototype
     *
     * @param prototype
     *            - the model for the new instance
     * @return the new instance
     */
    RuleForm create(RuleForm prototype);

    void deauthorize(RuleForm ruleform, Relationship relationship,
                     Agency authorized);

    void deauthorize(RuleForm ruleform, Relationship relationship,
                     Location authorized);

    void deauthorize(RuleForm ruleform, Relationship relationship,
                     Product authorized);

    void deauthorize(RuleForm ruleform, Relationship relationship,
                     Relationship authorized);

    void deauthorizeAgencies(RuleForm ruleform, Relationship relationship,
                             List<Agency> authorized);

    void deauthorizeLocations(RuleForm ruleform, Relationship relationship,
                              List<Location> authorized);

    void deauthorizeProducts(RuleForm ruleform, Relationship relationship,
                             List<Product> authorized);

    void deauthorizeRelationships(RuleForm ruleform, Relationship relationship,
                                  List<Relationship> authorized);

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
    List<AttributeAuth> getAttributeAuthorizations(Agency groupingAgency);

    /**
     * Answer the list of attribute authorizations that are classified by the
     * grouping agency, defined for the particular attribute
     *
     * @param groupingAgency
     * @param attribute
     * @return
     */
    List<AttributeAuth> getAttributeAuthorizations(Agency groupingAgency,
                                                   Attribute attribute);

    /**
     * Answer the list of attribute authorizations that are classified by an
     * aspect
     *
     * @param aspect
     *            - the classifying aspect.
     * @return
     */
    List<AttributeAuth> getAttributeAuthorizations(Aspect<RuleForm> aspect);

    /**
     * Answer the list of attribute authorizations that are classified by an
     * aspect, defined for the particular attribute
     *
     * @param aspect
     *            - the classifying aspect.
     * @param attribute
     * @return
     */
    List<AttributeAuth> getAttributeAuthorizations(Aspect<RuleForm> aspect,
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

    NetworkAttribute<?> getAttributeValue(Network edge, Attribute attribute);

    AttributeValue<RuleForm> getAttributeValue(RuleForm ruleform,
                                               Attribute attribute);

    NetworkAttribute<?> getAttributeValue(RuleForm parent, Relationship r,
                                          RuleForm child, Attribute attribute);

    List<Agency> getAuthorizedAgencies(RuleForm ruleform,
                                       Relationship relationship);

    Agency getAuthorizedAgency(RuleForm ruleform, Relationship relationship);

    Location getAuthorizedLocation(RuleForm ruleform, Relationship relationship);

    List<Location> getAuthorizedLocations(RuleForm ruleform,
                                          Relationship relationship);

    Product getAuthorizedProduct(RuleForm ruleform, Relationship relationship);

    List<Product> getAuthorizedProducts(RuleForm ruleform,
                                        Relationship relationship);

    Relationship getAuthorizedRelationship(RuleForm ruleform,
                                           Relationship relationship);

    List<Relationship> getAuthorizedRelationships(RuleForm ruleform,
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
    Network getImmediateChildLink(RuleForm parent, Relationship relationship,
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
    List<Network> getImmediateChildrenLinks(RuleForm parent,
                                            Relationship relationship);

    NetworkRuleform<RuleForm> getImmediateLink(RuleForm parent,
                                               Relationship relationship,
                                               RuleForm child);

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
     * Initialize the ruleform with the classified attributes for this aspect
     * 
     * @param ruleform
     * @param aspect
     * @param updatedBy
     * @return
     */
    List<AttributeType> initialize(RuleForm ruleform, Aspect<RuleForm> aspect,
                                   Agency updatedBy);

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
    Network link(RuleForm parent, Relationship r, RuleForm child,
                 Agency updatedBy);

    /**
     * Propagate the network inferences based on the tracked additions,
     * deletions and modifications
     *
     * @throws SQLException
     */
    void propagate();

    /**
     * Sets a value for an attribute after validating it first
     * 
     * @param value
     */
    void setAttributeValue(AttributeType value);

    void setAuthorizedAgencies(RuleForm ruleform, Relationship relationship,
                               List<Agency> authorized);

    void setAuthorizedLocations(RuleForm ruleform, Relationship relationship,
                                List<Location> authorized);

    void setAuthorizedProducts(RuleForm ruleform, Relationship relationship,
                               List<Product> authorized);

    void setAuthorizedRelationships(RuleForm ruleform,
                                    Relationship relationship,
                                    List<Relationship> authorized);

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
}
