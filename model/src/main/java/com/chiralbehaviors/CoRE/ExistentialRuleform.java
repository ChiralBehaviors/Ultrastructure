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
package com.chiralbehaviors.CoRE;

import java.util.Set;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * A ruleform that declares existence.
 * 
 * @author hhildebrand
 * 
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract public class ExistentialRuleform<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>>
        extends Ruleform {
    public static final String DEDUCE_NEW_NETWORK_RULES_SUFFIX                               = ".deduceNewNetworkRules";
    public static final String FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE_SUFFIX = ".findClassifiedAttributeAuthorizationsForAttribute";
    public static final String FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX               = ".findClassifiedAttributeAuthorizations";
    public static final String FIND_CLASSIFIED_ATTRIBUTE_VALUES_SUFFIX                       = ".findClassifiedAttributes";
    public static final String FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_FOR_ATTRIBUTE_SUFFIX     = ".findGroupedAttributeAuthorizationsForAttribute";
    public static final String FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_SUFFIX                   = ".findGroupedAttributeAuthorizations";
    public static final String FIND_GROUPED_ATTRIBUTE_VALUES_SUFFIX                          = ".findGroupedAttributes";
    public static final String GENERATE_NETWORK_INVERSES_SUFFIX                              = ".generateInverses";
    public static final String GET_ALL_PARENT_RELATIONSHIPS_SUFFIX                           = ".getAllParentRelationships";
    public static final String GET_CHILD_RULES_BY_RELATIONSHIP_SUFFIX                        = ".getChildRulesByRelationship";
    public static final String GET_CHILDREN_SUFFIX                                           = ".getChildren";
    public static final String GET_NETWORKS_SUFFIX                                           = ".getNetworks";
    public static final String INFERENCE_STEP_FROM_LAST_PASS_SUFFIX                          = ".inferenceStepFromLastPass";
    public static final String INFERENCE_STEP_SUFFIX                                         = ".inference";
    public static final String INSERT_NEW_NETWORK_RULES_SUFFIX                               = ".insertNewNetworkRules";
    public static final String UNLINKED_SUFFIX                                               = ".unlinked";
    public static final String USED_RELATIONSHIPS_SUFFIX                                     = ".getUsedRelationships";

    private static final long  serialVersionUID                                              = 1L;

    @Basic(fetch = FetchType.LAZY)
    private String             description;
    @Basic(fetch = FetchType.LAZY)
    private String             name;
    @Basic(fetch = FetchType.LAZY)
    private Integer            pinned                                                        = FALSE;

    public ExistentialRuleform() {
    }

    /**
     * @param updatedBy
     */
    public ExistentialRuleform(Agency updatedBy) {
        super(updatedBy);
    }

    public ExistentialRuleform(String name) {
        this.name = name;
    }

    public ExistentialRuleform(String name, Agency updatedBy) {
        super(updatedBy);
        this.name = name;
    }

    public ExistentialRuleform(String name, String description) {
        this(name);
        this.description = description;
    }

    public ExistentialRuleform(String name, String description, Agency updatedBy) {
        this(name, updatedBy);
        this.description = description;
    }

    public ExistentialRuleform(UUID id) {
        super(id);
    }

    public ExistentialRuleform(UUID id, Agency updatedBy) {
        super(id, updatedBy);
    }

    public ExistentialRuleform(UUID id, String name) {
        super(id);
        this.name = name;
    }

    abstract public void addChildRelationship(Network relationship);

    abstract public void addParentRelationship(Network relationship);

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    abstract public Set<Network> getNetworkByChild();

    abstract public Set<Network> getNetworkByParent();

    /**
     * @return the pinned
     */
    public Boolean getPinned() {
        return toBoolean(pinned);
    }

    abstract public void link(Relationship r, RuleForm child, Agency updatedBy,
                              Agency inverseSoftware, EntityManager em);

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    abstract public void setNetworkByChild(Set<Network> theNetworkByChild);

    abstract public void setNetworkByParent(Set<Network> theNetworkByParent);

    /**
     * @param pinned
     *            the pinned to set
     */
    public void setPinned(Boolean pinned) {
        this.pinned = toInteger(pinned);
    }

    @Override
    public String toString() {
        return String.format("%s [name=%s, id=%s]", getClass().getSimpleName(),
                             name, getId());
    }
}
