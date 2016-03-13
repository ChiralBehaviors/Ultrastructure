/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.existential;

import static com.chiralbehaviors.CoRE.existential.ExistentialRuleform.FIND_ALL;
import static com.chiralbehaviors.CoRE.existential.ExistentialRuleform.FIND_BY_NAME;
import static com.chiralbehaviors.CoRE.existential.ExistentialRuleform.FIND_CLASSIFIED_ATTRIBUTES;
import static com.chiralbehaviors.CoRE.existential.ExistentialRuleform.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS;
import static com.chiralbehaviors.CoRE.existential.ExistentialRuleform.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE;
import static com.chiralbehaviors.CoRE.existential.ExistentialRuleform.GET_ALL_PARENT_RELATIONSHIPS;
import static com.chiralbehaviors.CoRE.existential.ExistentialRuleform.GET_CHILDREN;
import static com.chiralbehaviors.CoRE.existential.ExistentialRuleform.GET_CHILD_RULES_BY_RELATIONSHIP;

import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.existential.domain.Agency;
import com.chiralbehaviors.CoRE.existential.domain.Relationship;
import com.chiralbehaviors.CoRE.existential.network.ExistentialNetwork;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hellblazer.utils.Tuple;

/**
 * @author hhildebrand
 *
 */
@NamedQueries({ @NamedQuery(name = FIND_ALL, query = "select a from ExistentialRuleform a where a.domain = :domain"),
                @NamedQuery(name = FIND_BY_NAME, query = "select e from ExistentialRuleform e where e.name = :name and a.domain = :domain"),
                @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTES, query = "SELECT "
                                                                       + "  attrValue "
                                                                       + "FROM "
                                                                       + "       ExistentialAttribute attrValue, "
                                                                       + "       ExistentialAttributeAuthorization auth, "
                                                                       + "       ExistentialNetworkAuthorization na, "
                                                                       + "       ExistentialNetwork network "
                                                                       + "WHERE "
                                                                       + "    auth.authorizedAttribute = attrValue.attribute "
                                                                       + "    AND na = auth.networkAuthorization "
                                                                       + "    AND network.relationship = na.classifier "
                                                                       + "    AND network.child = na.classification"
                                                                       + "    AND attrValue.existential = :existential "
                                                                       + "    AND na.classifier = :classifier "
                                                                       + "    AND na.classification= :classification "),
                @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE, query = "SELECT "
                                                                                                   + "  auth "
                                                                                                   + "FROM "
                                                                                                   + "    ExistentialAttributeAuthorization auth, "
                                                                                                   + "    ExistentialNetworkAuthorization na, "
                                                                                                   + "    ExistentialNetwork network "
                                                                                                   + "WHERE "
                                                                                                   + "    auth.networkAuthorization = na "
                                                                                                   + "    AND auth.attribute = :attribute "
                                                                                                   + "    AND network.relationship = na.classifier "
                                                                                                   + "    AND network.child = na.classification"
                                                                                                   + "    AND na.classifier = :classifier "
                                                                                                   + "    AND na.classification= :classification "),
                @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS, query = "select auth from ExistentialAttributeAuthorization auth "
                                                                                     + "WHERE auth.networkAuthorization.classifier = :classifier "
                                                                                     + "AND auth.networkAuthorization.classification = :classification "
                                                                                     + "AND auth.authorizedAttribute IS NOT NULL"),
                @NamedQuery(name = GET_CHILDREN, query = "SELECT n.child "
                                                         + "FROM ExistentialNetwork n "
                                                         + "WHERE n.parent = :p "
                                                         + "AND n.relationship = :r"),
                @NamedQuery(name = GET_ALL_PARENT_RELATIONSHIPS, query = "SELECT n "
                                                                         + "FROM ExistentialNetwork n "
                                                                         + "WHERE n.child = :c"),
                @NamedQuery(name = GET_CHILD_RULES_BY_RELATIONSHIP, query = "SELECT n FROM ExistentialNetwork n "
                                                                            + "WHERE n.parent = :parent "
                                                                            + "AND n.relationship IN :relationships "
                                                                            + "ORDER by n.parent.name, n.relationship.name, n.child.name") })

@MappedSuperclass
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "domain", discriminatorType = DiscriminatorType.CHAR, length = 1)
abstract public class ExistentialRuleform<RuleForm extends ExistentialRuleform<RuleForm>>
        extends Ruleform implements Phantasm<RuleForm> {
    public static final String CHECK_ATTRIBUTE_CAP                                    = "checkAttributeCap";
    public static final String CHECK_CHILD_CAP                                        = "checkChildCap";
    public static final String CHECK_FACET_CAP                                        = "checkFacetCap";
    public static final String CHECK_INSTANCE_CAP                                     = "checkInstanceCap";
    public static final String CHECK_NETWORK_ATTRIBUTE_CAP                            = "checkNetworkAttributeCap";
    public static final String DEDUCE_NEW_NETWORK_RULES                               = "deduceNewNetworkRules";
    public static final String FIND_ALL                                               = "findAll";
    public static final String FIND_BY_NAME                                           = "findByName";
    public static final String FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS               = "findClassifiedAttributeAuthorizations";
    public static final String FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE = "findClassifiedAttributeAuthorizationsForAttribute";
    public static final String FIND_CLASSIFIED_ATTRIBUTES                             = "findClassifiedAttributes";
    public static final String FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS                   = "findGroupedAttributeAuthorizations";
    public static final String FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_FOR_ATTRIBUTE     = "findGroupedAttributeAuthorizationsForAttribute";
    public static final String FIND_GROUPED_ATTRIBUTE_VALUES                          = "findGroupedAttributes";
    public static final String GENERATE_NETWORK_INVERSES                              = "generateInverses";
    public static final String GET_ALL_PARENT_RELATIONSHIPS                           = "getAllParentRelationships";
    public static final String GET_CHILD_RULES_BY_RELATIONSHIP                        = "getChildRulesByRelationship";
    public static final String GET_CHILDREN                                           = "getChildren";
    public static final String GET_NETWORKS                                           = "getNetworks";
    public static final String INFERENCE_STEP                                         = "inference";
    public static final String INFERENCE_STEP_FROM_LAST_PASS                          = "inferenceStepFromLastPass";
    public static final String INSERT_DEDUCTIONS                                      = "insertDeductions";
    public static final String INSERT_INVERSES                                        = "insertInverses";
    public static final String INSERT_NEW_NETWORK_RULES                               = "insertNewNetworkRules";
    public static final String USED_RELATIONSHIPS                                     = "getUsedRelationships";
    private static final long  serialVersionUID                                       = 1L;

    @Basic(fetch = FetchType.LAZY)
    private String             description;

    @Column(insertable = false, updatable = false)
    private char               domain;

    @NotNull
    @Basic(fetch = FetchType.LAZY)
    private String             name;

    public ExistentialRuleform() {
    }

    public ExistentialRuleform(String name) {
        this.name = name;
    }

    public ExistentialRuleform(String name, Agency updatedBy) {
        this.name = name;
        setUpdatedBy(updatedBy);
    }

    public ExistentialRuleform(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public ExistentialRuleform(String name, String description,
                               Agency updatedBy) {
        this.name = name;
        this.description = description;
        setUpdatedBy(updatedBy);
    }

    @Override
    public ExistentialRuleform<RuleForm> clone() {
        @SuppressWarnings("unchecked")
        ExistentialRuleform<RuleForm> clone = (ExistentialRuleform<RuleForm>) super.clone();
        return clone;
    }

    @JsonIgnore
    abstract public UUID getAnyId();

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

    public Tuple<ExistentialNetwork<RuleForm, RuleForm>, ExistentialNetwork<RuleForm, RuleForm>> link(Relationship r,
                                                                                                      RuleForm child,
                                                                                                      Agency updatedBy,
                                                                                                      Agency inverseSoftware,
                                                                                                      EntityManager em) {
        assert r != null : "Relationship cannot be null";
        assert child != null;
        assert updatedBy != null;
        assert em != null;

        @SuppressWarnings({ "unchecked", "rawtypes" })
        ExistentialNetwork<RuleForm, RuleForm> link = new ExistentialNetwork(this,
                                                                             r,
                                                                             child,
                                                                             updatedBy);
        em.persist(link);
        @SuppressWarnings({ "unchecked", "rawtypes" })
        ExistentialNetwork<RuleForm, RuleForm> inverse = new ExistentialNetwork(child,
                                                                                r.getInverse(),
                                                                                this,
                                                                                inverseSoftware);
        em.persist(inverse);
        return new Tuple<>(link, inverse);

    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s [%s]", getClass().getSimpleName(), getName());
    }
}
