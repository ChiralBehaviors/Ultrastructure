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
package com.chiralbehaviors.CoRE.authorization;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * @author hparry
 * 
 */

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "access_authorization", schema = "ruleform")
@DiscriminatorColumn(name = "authorization_type")
public abstract class AccessAuthorization<Parent extends ExistentialRuleform<Parent, ?>, Child extends ExistentialRuleform<Child, ?>>
        extends Ruleform {

    public static final String AGENCY_LOCATION                                                                = "1";
    // IMPORTANT: DON'T CHANGE THESE VALUES IF YOU HAVE DATA IN THE DATABASE
    public static final String AGENCY_PRODUCT                                                                 = "0";
    public static final String FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD_MATCH_ON_ALL_RELATIONSHIPS_SUFFIX = ".findAllAuthsForParentRelationshipChildMatchOnAllRelationships";
    public static final String FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD_SUFFIX                            = ".findAllAuthsForParentRelationshipChild";
    public static final String FIND_AUTHORIZATION_SUFFIX                                                      = ".findAuthorization";
    public static final String FIND_AUTHS_FOR_INDIRECT_CHILD_SUFFIX                                           = ".findAllAuthsForIndirectChild";

    public static final String FIND_AUTHS_FOR_INDIRECT_PARENT_AND_CHILD_SUFFIX                                = ".findAllAuthsForIndirectParentAndChild";
    public static final String FIND_AUTHS_FOR_INDIRECT_PARENT_SUFFIX                                          = ".findAllAuthsForIndirectParent";
    public static final String GET_ALL_AUTHORIZATIONS_FOR_PARENT_AND_RELATIONSHIP_SUFFIX                      = ".getAllAuthorizationsForParentAndRelationship";
    public static final String GET_ALL_AUTHORIZATIONS_FOR_RELATIONSHIP_AND_CHILD_SUFFIX                       = ".getAllAuthorizationsForRelationshipAndChild";
    public static final String LOCATION_AGENCY                                                                = "4";
    public static final String LOCATION_PRODUCT                                                               = "5";
    public static final String PRODUCT_AGENCY                                                                 = "2";
    public static final String PRODUCT_ATTRIBUTE                                                              = "6";
    public static final String PRODUCT_LOCATION                                                               = "3";
    public static final String PRODUCT_RELATIONSHIP                                                           = "7";
    public static final String PRODUCT_STATUS_CODE                                                            = "8";
    public static final String PRODUCT_UNIT                                                                   = "9";
    private static final long  serialVersionUID                                                               = 1L;

    @Column(name = "authorization_type")
    private String             authorizationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_transitive_relationship")
    protected Relationship     childTransitiveRelationship;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_transitive_relationship")
    protected Relationship     parentTransitiveRelationship;

    // bi-directional many-to-one association to Relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relationship")
    protected Relationship     relationship;

    abstract public Child getChild();

    /**
     * @return the childTransitiveRelationship
     */
    public Relationship getChildTransitiveRelationship() {
        return childTransitiveRelationship;
    }

    abstract public Parent getParent();

    /**
     * @return the parentTransitiveRelationship
     */
    public Relationship getParentTransitiveRelationship() {
        return parentTransitiveRelationship;
    }

    /**
     * @return the relationship
     */
    public Relationship getRelationship() {
        return relationship;
    }

    /**
     * @param childTransitiveRelationship
     *            the childTransitiveRelationship to set
     */
    public void setChildTransitiveRelationship(Relationship childTransitiveRelationship) {
        this.childTransitiveRelationship = childTransitiveRelationship;
    }

    /**
     * @param parentTransitiveRelationship
     *            the parentTransitiveRelationship to set
     */
    public void setParentTransitiveRelationship(Relationship parentTransitiveRelationship) {
        this.parentTransitiveRelationship = parentTransitiveRelationship;
    }

    /**
     * @param relationship
     *            the relationship to set
     */
    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.Ruleform#traverseForeignKeys(javax.persistence.
     * EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {

        if (relationship != null) {
            relationship = (Relationship) relationship.manageEntity(em,
                                                                    knownObjects);
        }
        if (parentTransitiveRelationship != null) {
            parentTransitiveRelationship = (Relationship) parentTransitiveRelationship.manageEntity(em,
                                                                                                    knownObjects);
        }

        if (childTransitiveRelationship != null) {
            childTransitiveRelationship = (Relationship) childTransitiveRelationship.manageEntity(em,
                                                                                                  knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);
    }

    protected void setAuthorizationType(String type) {
        authorizationType = type;
    }
}
