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
package com.hellblazer.CoRE;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.network.NetworkRuleform;
import com.hellblazer.CoRE.network.Relationship;

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
    public static final String GATHER_EXISTING_NETWORK_RULES_SUFFIX                          = ".gatherExistingNetworkRules";
    public static final String GENERATE_NETWORK_INVERSES_SUFFIX                              = ".generateInverses";
    public static final String GET_ALL_PARENT_RELATIONSHIPS_SUFFIX                           = ".getAllParentRelationships";
    public static final String GET_CHILD_RULES_BY_RELATIONSHIP_SUFFIX                        = ".getChildRulesByRelationship";
    public static final String GET_CHILDREN_SUFFIX                                           = ".getChildren";
    public static final String INFERENCE_STEP_FROM_LAST_PASS_SUFFIX                          = ".inferenceStepFromLastPass";
    public static final String INFERENCE_STEP_SUFFIX                                         = ".inference";
    public static final String INSERT_NEW_NETWORK_RULES_SUFFIX                               = ".insertNewNetworkRules";
    public static final String UNLINKED_SUFFIX                                               = ".unlinked";
    public static final String USED_RELATIONSHIPS_SUFFIX                                     = ".getUsedRelationships";

    private static final long  serialVersionUID                                              = 1L;

    private String             description;
    private String             name;
    private Boolean            pinned                                                        = Boolean.FALSE;

    public ExistentialRuleform() {
    }

    /**
     * @param updatedBy
     */
    public ExistentialRuleform(Agency updatedBy) {
        super(updatedBy);
    }

    public ExistentialRuleform(Long id) {
        super(id);
    }

    public ExistentialRuleform(Long id, Agency updatedBy) {
        super(id, updatedBy);
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

    abstract public void addChildRelationship(Network relationship);

    abstract public void addParentRelationship(Network relationship);

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    abstract public List<Network> getImmediateChildren(EntityManager em);

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
        return pinned;
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
        this.pinned = pinned;
    }

    @Override
    public String toString() {
        return String.format("%s [name=%s, id=%s]", getClass().getSimpleName(),
                             name, getId());
    }
}
