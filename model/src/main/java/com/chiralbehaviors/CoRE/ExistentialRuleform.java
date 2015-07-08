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
package com.chiralbehaviors.CoRE;

import java.util.Set;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attributable;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A ruleform that declares existence.
 *
 * @author hhildebrand
 *
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract public class ExistentialRuleform<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>>
        extends Ruleform
        implements Attributable<AttributeValue<RuleForm>>, Phantasm<RuleForm> {
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
    public static final String INSERT_DEDUCTIONS_SUFFIX                                      = ".insertDeductions";
    public static final String INSERT_INVERSES_SUFFIX                                        = ".insertInverses";
    public static final String INSERT_NEW_NETWORK_RULES_SUFFIX                               = ".insertNewNetworkRules";
    public static final String USED_RELATIONSHIPS_SUFFIX                                     = ".getUsedRelationships";

    private static final long serialVersionUID = 1L;

    private String description;

    @NotNull
    private String name;

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

    public ExistentialRuleform(String name, String description,
                               Agency updatedBy) {
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

    @JsonIgnore
    abstract public UUID getAnyId();

    @JsonIgnore
    abstract public <Value extends AttributeValue<RuleForm>> Class<Value> getAttributeValueClass();

    @JsonIgnore
    abstract public UUID getCopyId();

    /**
     * @return the description
     */
    @Override
    @JsonGetter
    public String getDescription() {
        return description;
    }

    /**
     * @return the name
     */
    @Override
    @JsonGetter
    public String getName() {
        return name;
    }

    @JsonIgnore
    abstract public Set<Network> getNetworkByChild();

    @JsonIgnore
    abstract public Set<Network> getNetworkByParent();

    @JsonIgnore
    abstract public SingularAttribute<Network, RuleForm> getNetworkChildAttribute();

    @JsonIgnore
    abstract public Class<Network> getNetworkClass();

    @JsonIgnore
    abstract public SingularAttribute<Network, RuleForm> getNetworkParentAttribute();

    @JsonIgnore
    abstract public SingularAttribute<WorkspaceAuthorization, Network> getNetworkWorkspaceAuthAttribute();

    @JsonIgnore
    abstract public UUID getNotApplicableId();

    @SuppressWarnings("unchecked")
    @Override
    @JsonIgnore
    public RuleForm getRuleform() {
        return (RuleForm) this;
    }

    @SuppressWarnings("unchecked")
    @JsonIgnore
    public Class<RuleForm> getRuleformClass() {
        return (Class<RuleForm>) getClass();
    }

    @JsonIgnore
    abstract public UUID getSameId();

    @JsonIgnore
    public abstract boolean isAny();

    @JsonIgnore
    public abstract boolean isAnyOrSame();

    @JsonIgnore
    public abstract boolean isCopy();

    @JsonIgnore
    public abstract boolean isNotApplicable();

    @JsonIgnore
    public abstract boolean isSame();

    abstract public Network link(Relationship r, RuleForm child,
                                 Agency updatedBy, Agency inverseSoftware,
                                 EntityManager em);

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

    @Override
    public String toString() {
        return String.format("%s [%s]", getClass().getSimpleName(), getName());
    }
}
