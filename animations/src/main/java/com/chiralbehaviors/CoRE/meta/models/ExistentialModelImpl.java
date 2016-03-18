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

package com.chiralbehaviors.CoRE.meta.models;

import static com.chiralbehaviors.CoRE.jooq.Tables.AGENCY_EXISTENTIAL_GROUPING;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_ATTRIBUTE;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_ATTRIBUTE_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK_ATTRIBUTE;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.WORKSPACE_AUTHORIZATION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.Query;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectOnConditionStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.AgencyExistentialGrouping;
import com.chiralbehaviors.CoRE.jooq.tables.Existential;
import com.chiralbehaviors.CoRE.jooq.tables.ExistentialAttributeAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.ExistentialNetworkAttributeAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.ExistentialNetworkAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.ExistentialModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace;
import com.hellblazer.utils.Tuple;

/**
 * @author hhildebrand
 *
 */
abstract public class ExistentialModelImpl<RuleForm extends ExistentialRuleform>
        implements ExistentialModel<RuleForm> {

    private static Logger        log            = LoggerFactory.getLogger(ExistentialModelImpl.class);
    private static int           MAX_DEDUCTIONS = 1000;
    private static final Integer ZERO           = Integer.valueOf(0);

    protected final DSLContext   create;
    protected final Kernel       kernel;
    protected final Model        model;

    @SuppressWarnings("unchecked")
    public ExistentialModelImpl(Model model, DSLContext create) {
        this.model = model;
        this.create = model.getEntityManager();
        this.kernel = model.getKernel();
    }

    @Override
    public void authorize(RuleForm ruleform, Relationship relationship,
                          ExistentialRuleform authorized) {
        ExistentialNetworkRecord auth = create.newRecord(EXISTENTIAL_NETWORK);
        auth.setParent(ruleform.getId());
        auth.setRelationship(relationship.getId());
        auth.setChild(authorized.getId());
        auth.setUpdatedBy(model.getCurrentPrincipal()
                               .getPrincipal()
                               .getId());
        auth.insert();

        ExistentialNetworkRecord inverse = create.newRecord(EXISTENTIAL_NETWORK);
        inverse.setParent(authorized.getId());
        inverse.setRelationship(relationship.getInverse());
        inverse.setChild(ruleform.getId());
        inverse.setUpdatedBy(model.getCurrentPrincipal()
                                  .getPrincipal()
                                  .getId());
        inverse.insert();
    }

    @Override
    public void authorizeAll(RuleForm ruleform, Relationship relationship,
                             List<? extends ExistentialRuleform> authorized) {
        for (ExistentialRuleform agency : authorized) {
            authorize(ruleform, relationship, agency);
        }
    }

    @Override
    public void authorizeSingular(RuleForm ruleform, Relationship relationship,
                                  ExistentialRuleform authorized) {
        deauthorize(ruleform, relationship,
                    getAuthorized(ruleform, relationship));
        authorize(ruleform, relationship, authorized);
    }

    @Override
    public boolean checkCapability(ExistentialAttributeAuthorizationRecord stateAuth,
                                   Relationship capability) {
        return checkCapability(model.getCurrentPrincipal()
                                    .getCapabilities(),
                               stateAuth, capability);
    }

    @Override
    public boolean checkCapability(ExistentialNetworkAuthorizationRecord auth,
                                   Relationship capability) {
        return checkCapability(model.getCurrentPrincipal()
                                    .getCapabilities(),
                               auth, capability);
    }

    /**
     * Check the capability of an agency on an attribute of a ruleform.
     */
    @Override
    public boolean checkCapability(List<Agency> agencies,
                                   ExistentialAttributeAuthorizationRecord stateAuth,
                                   Relationship capability) {
        ExistentialAttributeAuthorization required = EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.as("required");
        return ZERO.equals(create.selectCount()
                                 .from(required)
                                 .where(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.AUTHORITY.isNotNull())
                                 .and(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.NETWORK_AUTHORIZATION.equal(stateAuth.getNetworkAuthorization()))
                                 .and(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.AUTHORIZED_ATTRIBUTE.equal(stateAuth.getAuthorizedAttribute()))
                                 .andNotExists(create.select(required.field(EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION.AUTHORITY))
                                                     .from(EXISTENTIAL_NETWORK)
                                                     .where(EXISTENTIAL_NETWORK.PARENT.in(agencies.stream()
                                                                                                  .map(a -> a.getId())
                                                                                                  .collect(Collectors.toList())))
                                                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(capability.getId()))
                                                     .and(EXISTENTIAL_NETWORK.CHILD.equal(required.field(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.AUTHORITY))))
                                 .fetchOne()
                                 .value1());

    }

    /**
     * Check the capability of an agency on the authorized relationship of the
     * facet child relationship.
     */
    @Override
    public boolean checkCapability(List<Agency> agencies,
                                   ExistentialNetworkAuthorizationRecord stateAuth,
                                   Relationship capability) {
        ExistentialNetworkAuthorization required = EXISTENTIAL_NETWORK_AUTHORIZATION.as("required");
        return ZERO.equals(create.selectCount()
                                 .from(required)
                                 .where(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORITY.isNotNull())
                                 .and(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFIER.equal(stateAuth.getClassifier()))
                                 .and(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFICATION.equal(stateAuth.getClassification()))
                                 .and(EXISTENTIAL_NETWORK_AUTHORIZATION.CHILD_RELATIONSHIP.equal(stateAuth.getChildRelationship()))
                                 .and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORIZED_RELATIONSHIP.equal(stateAuth.getAuthorizedRelationship()))
                                 .and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORIZED_PARENT.equal(stateAuth.getAuthorizedParent()))
                                 .andNotExists(create.select(required.field(EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION.AUTHORITY))
                                                     .from(EXISTENTIAL_NETWORK)
                                                     .where(EXISTENTIAL_NETWORK.PARENT.in(agencies.stream()
                                                                                                  .map(a -> a.getId())
                                                                                                  .collect(Collectors.toList())))
                                                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(capability.getId()))
                                                     .and(EXISTENTIAL_NETWORK.CHILD.equal(required.field(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.AUTHORITY))))
                                 .fetchOne()
                                 .value1());
    }

    /**
     * Check the capability of an agency on an instance.
     */
    @Override
    public boolean checkCapability(List<Agency> agencies, RuleForm instance,
                                   Relationship capability) {
        AgencyExistentialGrouping required = AGENCY_EXISTENTIAL_GROUPING.as("required");
        return ZERO.equals(create.selectCount()
                                 .from(required)
                                 .where(required.ENTITY.equal(instance.getId()))
                                 .andNotExists(create.select(required.field(EXISTENTIAL_NETWORK.AUTHORITY))
                                                     .from(EXISTENTIAL_NETWORK)
                                                     .where(EXISTENTIAL_NETWORK.PARENT.in(agencies.stream()
                                                                                                  .map(a -> a.getId())
                                                                                                  .collect(Collectors.toList())))
                                                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(capability.getId()))
                                                     .and(EXISTENTIAL_NETWORK.CHILD.equal(required.field(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.AUTHORITY))))
                                 .fetchOne()
                                 .value1());
    }

    @Override
    public boolean checkCapability(RuleForm instance, Relationship capability) {
        return checkCapability(model.getCurrentPrincipal()
                                    .getCapabilities(),
                               instance, capability);
    }

    @Override
    public boolean checkFacetCapability(ExistentialNetworkAuthorizationRecord facet,
                                        Relationship capability) {
        return checkFacetCapability(model.getCurrentPrincipal()
                                         .getCapabilities(),
                                    facet, capability);
    }

    /**
     * Check the capability of an agency on the facet.
     */
    @Override
    public boolean checkFacetCapability(List<Agency> agencies,
                                        ExistentialNetworkAuthorizationRecord facet,
                                        Relationship capability) {
        ExistentialNetworkAuthorization required = EXISTENTIAL_NETWORK_AUTHORIZATION.as("required");
        return ZERO.equals(create.selectCount()
                                 .from(required)
                                 .where(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORITY.isNotNull())
                                 .and(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFIER.equal(facet.getClassifier()))
                                 .and(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFICATION.equal(facet.getClassification()))
                                 .and(EXISTENTIAL_NETWORK_AUTHORIZATION.CHILD_RELATIONSHIP.isNull())
                                 .and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORIZED_RELATIONSHIP.isNull())
                                 .and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORIZED_PARENT.isNull())
                                 .andNotExists(create.select(required.field(EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION.AUTHORITY))
                                                     .from(EXISTENTIAL_NETWORK)
                                                     .where(EXISTENTIAL_NETWORK.PARENT.in(agencies.stream()
                                                                                                  .map(a -> a.getId())
                                                                                                  .collect(Collectors.toList())))
                                                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(capability.getId()))
                                                     .and(EXISTENTIAL_NETWORK.CHILD.equal(required.field(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.AUTHORITY))))
                                 .fetchOne()
                                 .value1());
    }

    @Override
    public boolean checkNetworkCapability(ExistentialAttributeAuthorizationRecord stateAuth,
                                          Relationship capability) {
        return checkNetworkCapability(model.getCurrentPrincipal()
                                           .getCapabilities(),
                                      stateAuth, capability);
    }

    /**
     * Check the capability of an agency on an attribute of the authorized
     * relationship of the facet child relationship.
     */
    @Override
    public boolean checkNetworkCapability(List<Agency> agencies,
                                          ExistentialAttributeAuthorizationRecord stateAuth,
                                          Relationship capability) {
        ExistentialNetworkAttributeAuthorization required = EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION.as("required");
        return ZERO.equals(create.selectCount()
                                 .from(required)
                                 .where(EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION.AUTHORITY.isNotNull())
                                 .and(EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION.NETWORK_AUTHORIZATION.equal(stateAuth.getNetworkAuthorization()))
                                 .and(EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION.AUTHORIZED_ATTRIBUTE.equal(stateAuth.getAuthorizedAttribute()))
                                 .andNotExists(create.select(required.field(EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION.AUTHORITY))
                                                     .from(EXISTENTIAL_NETWORK)
                                                     .where(EXISTENTIAL_NETWORK.PARENT.in(agencies.stream()
                                                                                                  .map(a -> a.getId())
                                                                                                  .collect(Collectors.toList())))
                                                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(capability.getId()))
                                                     .and(EXISTENTIAL_NETWORK.CHILD.equal(required.field(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.AUTHORITY))))
                                 .fetchOne()
                                 .value1());
    }

    @Override
    public void deauthorize(RuleForm ruleform, Relationship relationship,
                            ExistentialRuleform authorized) {
        create.deleteFrom(EXISTENTIAL_NETWORK)
              .where(EXISTENTIAL_NETWORK.PARENT.equal(ruleform.getId()))
              .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
              .and(EXISTENTIAL_NETWORK.PARENT.equal(authorized.getId()))
              .execute();
        create.deleteFrom(EXISTENTIAL_NETWORK)
              .where(EXISTENTIAL_NETWORK.PARENT.equal(authorized.getId()))
              .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getInverse()))
              .and(EXISTENTIAL_NETWORK.PARENT.equal(ruleform.getId()))
              .execute();
    }

    @Override
    public void deauthorizeAll(RuleForm ruleform, Relationship relationship,
                               List<? extends ExistentialRuleform> authorized) {
        for (ExistentialRuleform agency : authorized) {
            deauthorize(ruleform, relationship, agency);
        }
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#find(long)
     */
    @SuppressWarnings("unchecked")
    @Override
    public RuleForm find(UUID id) {
        return (RuleForm) create.selectFrom(EXISTENTIAL)
                                .where(EXISTENTIAL.ID.equal(id))
                                .and(EXISTENTIAL.DOMAIN.equal(domain()))
                                .fetchOne()
                                .into(domainClass());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<RuleForm> findAll() {
        return (List<RuleForm>) create.selectFrom(EXISTENTIAL)
                                      .where(EXISTENTIAL.DOMAIN.equal(domain()))
                                      .fetch()
                                      .into(domainClass());
    }

    @Override
    public List<Aspect<RuleForm>> getAllFacets() {
        return create.selectDistinct(EXISTENTIAL_NETWORK_AUTHORIZATION.fields())
                     .from(EXISTENTIAL_NETWORK_AUTHORIZATION)
                     .join(EXISTENTIAL)
                     .on(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFICATION.equal(EXISTENTIAL.ID))
                     .where(EXISTENTIAL.DOMAIN.equal(domain()))
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORIZED_PARENT.isNull())
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORIZED_RELATIONSHIP.isNull())
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORIZED_PARENT.isNull())
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORITY.isNull())
                     .fetch()
                     .into(EXISTENTIAL_NETWORK_AUTHORIZATION)
                     .stream()
                     .map(auth -> new Aspect<RuleForm>(auth.getClassifier(),
                                                       auth.getClassification()))
                     .collect(Collectors.toList());
    }

    @Override
    public <ValueType> List<ValueType> getAllowedValues(Attribute attribute,
                                                        Agency groupingAgency) {
        return getAllowedValues(attribute,
                                getAttributeAuthorizations(groupingAgency,
                                                           attribute));
    }

    @Override
    public <ValueType> List<ValueType> getAllowedValues(Attribute attribute,
                                                        Aspect<RuleForm> aspect) {
        return getAllowedValues(attribute,
                                getAttributeAuthorizations(aspect, attribute));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#getAttributeAuthorizations(com
     * .hellblazer.CoRE.agency.Agency, com.chiralbehaviors.CoRE.attribute.Attribute)
     */
    @Override
    public List<ExistentialAttributeAuthorizationRecord> getAttributeAuthorizations(Agency groupingAgency,
                                                                                    Attribute attribute) {

        return create.selectDistinct(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.fields())
                     .from(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.as("auth"))
                     .join(EXISTENTIAL_NETWORK_AUTHORIZATION.as("na"))
                     .on(EXISTENTIAL_NETWORK_AUTHORIZATION.ID.eq(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.field(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.NETWORK_AUTHORIZATION)))
                     .join(EXISTENTIAL_NETWORK.as("network"))
                     .on(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFIER))
                     .and(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFICATION))
                     .and(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.AUTHORIZED_ATTRIBUTE.equal(attribute.getId()))
                     .fetch()
                     .into(ExistentialAttributeAuthorizationRecord.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#getAttributeAuthorizations(com
     * .hellblazer.CoRE.meta.Aspect, com.chiralbehaviors.CoRE.attribute.Attribute)
     */
    @Override
    public List<ExistentialAttributeAuthorizationRecord> getAttributeAuthorizations(Aspect<RuleForm> aspect,
                                                                                    Attribute attribute) {

        return create.selectDistinct(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.fields())
                     .from(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.as("auth"))
                     .join(EXISTENTIAL_NETWORK_AUTHORIZATION.as("na"))
                     .on(EXISTENTIAL_NETWORK_AUTHORIZATION.ID.eq(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.field(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.NETWORK_AUTHORIZATION)))
                     .join(EXISTENTIAL_NETWORK.as("network"))
                     .on(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFIER))
                     .and(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFICATION))
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFIER.eq(aspect.getClassifier()))
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFICATION.eq(aspect.getClassification()))
                     .and(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.AUTHORIZED_ATTRIBUTE.equal(attribute.getId()))
                     .and(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.AUTHORITY.isNull())
                     .fetch()
                     .into(ExistentialAttributeAuthorizationRecord.class);
    }

    @Override
    public List<ExistentialAttributeAuthorizationRecord> getAttributeAuthorizations(Aspect<RuleForm> aspect,
                                                                                    boolean includeGrouping) {

        SelectOnConditionStep<Record> and = create.selectDistinct(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.fields())
                                                  .from(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.as("auth"))
                                                  .join(EXISTENTIAL_NETWORK_AUTHORIZATION.as("na"))
                                                  .on(EXISTENTIAL_NETWORK_AUTHORIZATION.ID.eq(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.field(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.NETWORK_AUTHORIZATION)))
                                                  .join(EXISTENTIAL_NETWORK.as("network"))
                                                  .on(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFIER))
                                                  .and(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFICATION))
                                                  .and(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFIER.eq(aspect.getClassifier()))
                                                  .and(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFICATION.eq(aspect.getClassification()));
        if (!includeGrouping) {
            and = and.and(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.AUTHORITY.isNull());
        }
        return and.fetch()
                  .into(ExistentialAttributeAuthorizationRecord.class)
                  .stream()
                  .collect(Collectors.toList());
    }

    @Override
    public List<ExistentialAttributeAuthorizationRecord> getAttributeAuthorizations(ExistentialNetworkAuthorizationRecord facet,
                                                                                    boolean includeGrouping) {
        return getAttributeAuthorizations(new Aspect<RuleForm>(facet.getClassifier(),
                                                               facet.getClassification()),
                                          includeGrouping);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#getAttributesClassifiedBy(com
     * .hellblazer.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.meta.Aspect)
     */
    @Override
    public List<ExistentialAttributeRecord> getAttributesClassifiedBy(RuleForm ruleform,
                                                                      Aspect<RuleForm> aspect) {
        return create.selectDistinct(EXISTENTIAL_ATTRIBUTE.fields())
                     .from(EXISTENTIAL_ATTRIBUTE.as("attrValue"))
                     .join(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.as("auth"))
                     .on(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFIER))
                     .join(EXISTENTIAL_NETWORK_AUTHORIZATION.as("na"))
                     .on(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.NETWORK_AUTHORIZATION.eq(EXISTENTIAL_NETWORK_AUTHORIZATION.ID))
                     .join(EXISTENTIAL_NETWORK.as("network"))
                     .on(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFIER))
                     .and(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFICATION))
                     .and(EXISTENTIAL_ATTRIBUTE.EXISTENTIAL.eq(ruleform.getId()))
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFICATION.eq(aspect.getClassification()))
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFIER.eq(aspect.getClassifier()))
                     .and(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.AUTHORITY.isNull())
                     .fetch()
                     .into(ExistentialAttributeRecord.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#getAttributesGroupedBy(com.chiralbehaviors
     * .CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.agency.Agency)
     */
    @Override
    public List<ExistentialAttributeRecord> getAttributesGroupedBy(RuleForm ruleform,
                                                                   Agency groupingAgency) {
        return create.selectDistinct(EXISTENTIAL_ATTRIBUTE.fields())
                     .from(EXISTENTIAL_ATTRIBUTE.as("attrValue"))
                     .join(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.as("auth"))
                     .on(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFIER))
                     .join(EXISTENTIAL_NETWORK_AUTHORIZATION.as("na"))
                     .on(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.NETWORK_AUTHORIZATION.eq(EXISTENTIAL_NETWORK_AUTHORIZATION.ID))
                     .join(EXISTENTIAL_NETWORK.as("network"))
                     .on(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFIER))
                     .and(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFICATION))
                     .and(EXISTENTIAL_ATTRIBUTE.EXISTENTIAL.eq(ruleform.getId()))
                     .and(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.AUTHORITY.eq(groupingAgency.getId()))
                     .fetch()
                     .into(ExistentialAttributeRecord.class);
    }

    @Override
    public ExistentialNetworkAttributeRecord getAttributeValue(ExistentialNetworkRecord edge,
                                                               Attribute attribute) {
        return create.selectFrom(EXISTENTIAL_NETWORK_ATTRIBUTE.as("attrValue"))
                     .where(EXISTENTIAL_NETWORK_ATTRIBUTE.EDGE.eq(edge.getId()))
                     .and(EXISTENTIAL_NETWORK_ATTRIBUTE.ATTRIBUTE.eq(attribute.getId()))
                     .fetchOne()
                     .into(ExistentialNetworkAttributeRecord.class);
    }

    @Override
    public ExistentialAttributeRecord getAttributeValue(RuleForm ruleform,
                                                        Attribute attribute) {
        List<ExistentialAttributeRecord> values = getAttributeValues(ruleform,
                                                                     attribute);
        if (values.size() > 1) {
            throw new IllegalStateException(String.format("%s has multiple values for %s",
                                                          attribute, ruleform));
        }
        if (values.size() == 0) {
            return null;
        }
        return values.get(0);
    }

    @Override
    public ExistentialNetworkAttributeRecord getAttributeValue(RuleForm parent,
                                                               Relationship r,
                                                               RuleForm child,
                                                               Attribute attribute) {
        ExistentialNetworkRecord edge = getImmediateChildLink(parent, r, child);
        if (edge == null) {
            return null;
        }
        return getAttributeValue(edge, attribute);
    }

    @Override
    public List<ExistentialAttributeRecord> getAttributeValues(RuleForm ruleform,
                                                               Attribute attribute) {
        return create.selectFrom(EXISTENTIAL_ATTRIBUTE.as("attrValue"))
                     .where(EXISTENTIAL_ATTRIBUTE.EXISTENTIAL.eq(ruleform.getId()))
                     .and(EXISTENTIAL_ATTRIBUTE.ATTRIBUTE.eq(attribute.getId()))
                     .fetch()
                     .into(ExistentialAttributeRecord.class);
    }

    @Override
    public <T extends ExistentialRuleform> List<T> getAllAuthorized(RuleForm ruleform,
                                                                    Relationship relationship) {
        throw new UnsupportedOperationException(String.format("%s to Agency authorizations are undefined",
                                                              ruleform.getClass()
                                                                      .getSimpleName()));
    }

    @Override
    public <T extends ExistentialRuleform> T getAuthorized(RuleForm ruleform,
                                                           Relationship relationship) {
        List<T> result = getAllAuthorized(ruleform, relationship);
        if (result.isEmpty()) {
            return null;
        } else if (result.size() > 1) {
            throw new IllegalStateException(String.format("%s is a non singular authorization of %s",
                                                          relationship,
                                                          ruleform));
        }
        return result.get(0);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getChild(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @SuppressWarnings("unchecked")
    @Override
    public RuleForm getChild(RuleForm parent, Relationship relationship) {
        return (RuleForm) create.selectDistinct(EXISTENTIAL.fields())
                                .from(EXISTENTIAL)
                                .join(EXISTENTIAL_NETWORK)
                                .on(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                                .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                                .and(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL.ID))
                                .fetchOne()
                                .into(domainClass());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#getNetwork(com.chiralbehaviors.CoRE
     * .network.Networked, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<RuleForm> getChildren(RuleForm parent,
                                      Relationship relationship) {
        return (List<RuleForm>) create.selectDistinct(EXISTENTIAL.fields())
                                      .from(EXISTENTIAL)
                                      .join(EXISTENTIAL_NETWORK)
                                      .on(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                                      .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                                      .and(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL.ID))
                                      .fetch()
                                      .into(domainClass());
    }

    @Override
    public ExistentialNetworkAuthorizationRecord getFacetDeclaration(@SuppressWarnings("rawtypes") Aspect aspect) {
        return create.selectFrom(EXISTENTIAL_NETWORK_AUTHORIZATION)
                     .where(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFIER.equal(aspect.getClassifier()))
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFICATION.equal(aspect.getClassification()))
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORIZED_PARENT.isNull())
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORIZED_RELATIONSHIP.isNull())
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.CHILD_RELATIONSHIP.isNull())
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORITY.isNull())
                     .fetchOne()
                     .into(ExistentialNetworkAuthorizationRecord.class);
    }

    @Override
    public List<ExistentialNetworkAuthorizationRecord> getFacets(Product workspace) {
        return create.selectDistinct(EXISTENTIAL_NETWORK_AUTHORIZATION.fields())
                     .from(EXISTENTIAL_NETWORK_AUTHORIZATION)
                     .join(WORKSPACE_AUTHORIZATION)
                     .on(WORKSPACE_AUTHORIZATION.DEFINING_PRODUCT.equal(workspace.getId()))
                     .and(WORKSPACE_AUTHORIZATION.REFERENCE.equal(EXISTENTIAL_NETWORK_AUTHORIZATION.ID))
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORIZED_PARENT.isNull())
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORIZED_RELATIONSHIP.isNull())
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.CHILD_RELATIONSHIP.isNull())
                     .and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORITY.isNull())
                     .fetch()
                     .into(ExistentialNetworkAuthorizationRecord.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RuleForm getImmediateChild(RuleForm parent,
                                      Relationship relationship) {
        return (RuleForm) create.selectDistinct(EXISTENTIAL.fields())
                                .from(EXISTENTIAL)
                                .join(EXISTENTIAL_NETWORK)
                                .on(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                                .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                                .and(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL.ID))
                                .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
                                .fetchOne()
                                .into(domainClass());
    }

    @Override
    @SuppressWarnings("unchecked")
    public ExistentialNetworkRecord getImmediateChildLink(RuleForm parent,
                                                          Relationship relationship,
                                                          RuleForm child) {
        return create.selectFrom(EXISTENTIAL_NETWORK)
                     .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                     .and(EXISTENTIAL_NETWORK.CHILD.equal(child.getId()))
                     .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
                     .fetchOne()
                     .into(ExistentialNetworkRecord.class);
    }

    @Override
    public List<RuleForm> getImmediateChildren(RuleForm parent,
                                               Relationship relationship) {
        return getImmediateChildren(parent.getId(), relationship.getId());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ExistentialNetworkRecord> getImmediateChildrenLinks(RuleForm parent,
                                                                    Relationship relationship) {
        return create.selectFrom(EXISTENTIAL_NETWORK)
                     .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                     .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
                     .fetch()
                     .into(ExistentialNetworkRecord.class);
    }

    /**
     * @param parent
     * @param relationship
     * @return
     */
    @Override
    public ExistentialNetworkRecord getImmediateLink(RuleForm parent,
                                                     Relationship relationship,
                                                     RuleForm child) {
        return create.selectFrom(EXISTENTIAL_NETWORK)
                     .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                     .and(EXISTENTIAL_NETWORK.CHILD.equal(child.getId()))
                     .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
                     .fetchOne()
                     .into(ExistentialNetworkRecord.class);
    }

    @Override
    public Collection<ExistentialNetworkRecord> getImmediateNetworkEdges(RuleForm parent) {
        return create.selectFrom(EXISTENTIAL_NETWORK)
                     .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                     .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
                     .fetch()
                     .into(ExistentialNetworkRecord.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#getImmediateRelationships(com
     * .hellblazer.CoRE.ExistentialRuleform)
     */
    @Override
    public Collection<Relationship> getImmediateRelationships(RuleForm parent) {
        return create.selectDistinct(EXISTENTIAL.fields())
                     .from(EXISTENTIAL)
                     .join(EXISTENTIAL_NETWORK)
                     .on(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(EXISTENTIAL_NETWORK.ID))
                     .fetch()
                     .into(Relationship.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<RuleForm> getInferredChildren(RuleForm parent,
                                              Relationship relationship) {
        return (List<RuleForm>) create.selectDistinct(EXISTENTIAL.fields())
                                      .from(EXISTENTIAL)
                                      .join(EXISTENTIAL_NETWORK)
                                      .on(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                                      .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                                      .and(EXISTENTIAL_NETWORK.INFERENCE.isNotNull())
                                      .fetch()
                                      .into(domainClass());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<RuleForm> getInGroup(RuleForm parent,
                                     Relationship relationship) {
        return (List<RuleForm>) create.selectDistinct(EXISTENTIAL.fields())
                                      .from(EXISTENTIAL)
                                      .join(EXISTENTIAL_NETWORK)
                                      .on(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL.ID))
                                      .and(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                                      .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                                      .and(EXISTENTIAL_NETWORK.CHILD.notEqual(parent.getId()))
                                      .fetch()
                                      .into(domainClass());
    }

    @Override
    public List<ExistentialNetworkAuthorizationRecord> getNetworkAuthorizations(Aspect<RuleForm> aspect,
                                                                                boolean includeGrouping) {

        SelectConditionStep<ExistentialNetworkAuthorizationRecord> and = create.selectFrom(EXISTENTIAL_NETWORK_AUTHORIZATION)
                                                                               .where(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFIER.equal(aspect.getClassifier()))
                                                                               .and(EXISTENTIAL_NETWORK_AUTHORIZATION.CLASSIFICATION.equal(aspect.getClassification()))
                                                                               .and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORIZED_PARENT.isNotNull())
                                                                               .and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORIZED_RELATIONSHIP.isNotNull())
                                                                               .and(EXISTENTIAL_NETWORK_AUTHORIZATION.CHILD_RELATIONSHIP.isNotNull());
        if (!includeGrouping) {
            and = and.and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORITY.isNull());
        }
        return and.fetch()
                  .into(ExistentialNetworkAuthorizationRecord.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<RuleForm> getNotInGroup(RuleForm parent,
                                        Relationship relationship) {

        Existential e = EXISTENTIAL.as("e");
        return (List<RuleForm>) create.selectFrom(e)
                                      .whereNotExists(create.selectFrom(EXISTENTIAL_NETWORK)
                                                            .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                                                            .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                                                            .and(EXISTENTIAL_NETWORK.CHILD.equal(e.field(EXISTENTIAL.ID))))
                                      .fetch()
                                      .into(domainClass());
    }

    @SuppressWarnings("unchecked")
    @Override
    public RuleForm getSingleChild(RuleForm parent, Relationship relationship) {
        return (RuleForm) create.selectDistinct(EXISTENTIAL.fields())
                                .from(EXISTENTIAL)
                                .join(EXISTENTIAL_NETWORK)
                                .on(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                                .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                                .and(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL.ID))
                                .fetchOne()
                                .into(domainClass());
    }

    @Override
    public final void initialize(RuleForm ruleform, Aspect<RuleForm> aspect) {
        initialize(ruleform, aspect, null);
    }

    @Override
    public final void initialize(RuleForm ruleform, Aspect<RuleForm> aspect,
                                 EditableWorkspace workspace) {
        Agency principal = model.getCurrentPrincipal()
                                .getPrincipal();
        if (getImmediateChildren(ruleform.getId(),
                                 aspect.getClassifier()).isEmpty()) {
            Tuple<ExistentialNetworkRecord, ExistentialNetworkRecord> links = link(ruleform.getId(),
                                                                                   aspect.getClassifier(),
                                                                                   aspect.getClassification(),
                                                                                   principal.getId());
            if (workspace != null) {
                workspace.add(links.a);
                workspace.add(links.b);
            }
        }
        for (ExistentialAttributeAuthorizationRecord authorization : getAttributeAuthorizations(aspect,
                                                                                                false)) {
            Attribute authorizedAttribute = create.selectFrom(EXISTENTIAL)
                                                  .where(EXISTENTIAL.ID.equal(authorization.getAuthorizedAttribute()))
                                                  .fetchOne()
                                                  .into(Attribute.class);
            if (!authorizedAttribute.getKeyed()
                && !authorizedAttribute.getIndexed()) {
                if (getAttributeValue(ruleform, authorizedAttribute) == null) {
                    ExistentialAttributeRecord attribute = create(ruleform,
                                                                  authorizedAttribute,
                                                                  principal);
                    setValue(attribute, authorization);
                    if (workspace != null) {
                        workspace.add(attribute);
                    }
                }
            }
        }
    }

    private void setValue(ExistentialAttributeRecord attribute,
                          ExistentialAttributeAuthorizationRecord authorization) {
        // TODO Auto-generated method stub

    }

    @Override
    public void initialize(RuleForm instance,
                           ExistentialNetworkAuthorizationRecord facet,
                           Agency principal) {
        initialize(instance, new Aspect<RuleForm>(facet.getClassifier(),
                                                  facet.getClassification()));

    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#isAccessible(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.network.Relationship, com.chiralbehaviors.CoRE.network.Relationship, com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public boolean isAccessible(RuleForm parent, Relationship relationship,
                                RuleForm child) {
        return !ZERO.equals(create.selectCount()
                                  .from(EXISTENTIAL)
                                  .join(EXISTENTIAL_NETWORK)
                                  .on(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL.ID))
                                  .and(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                                  .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                                  .and(EXISTENTIAL_NETWORK.CHILD.notEqual(parent.getId()))
                                  .fetchOne()
                                  .value1());
    }

    @Override
    public Tuple<ExistentialNetworkRecord, ExistentialNetworkRecord> link(RuleForm parent,
                                                                          Relationship r,
                                                                          RuleForm child,
                                                                          Agency updatedBy) {
        return link(parent.getId(), r.getId(), child.getId(),
                    updatedBy.getId());
    }

    public Tuple<ExistentialNetworkRecord, ExistentialNetworkRecord> link(UUID parent,
                                                                          UUID r,
                                                                          UUID child,
                                                                          UUID updatedBy) {
        ExistentialNetworkRecord forward = create.newRecord(EXISTENTIAL_NETWORK);
        forward.setParent(parent);
        forward.setRelationship(r);
        forward.setChild(child);
        forward.setUpdatedBy(updatedBy);

        ExistentialNetworkRecord inverse = create.newRecord(EXISTENTIAL_NETWORK);
        inverse.setParent(child);
        inverse.setRelationship(r);
        inverse.setChild(parent);
        inverse.setUpdatedBy(updatedBy);
        return new Tuple<>(forward, inverse);
    }

    @Override
    public void propagate(boolean initial) {
        createDeductionTemporaryTables(initial);
        boolean firstPass = true;
        do {
            if (infer(firstPass) == 0) {
                break;
            }
            firstPass = false;
            deduce();
            if (insert() == 0) {
                break;
            }
            alterDeductionTablesForNextPass();
        } while (true);
        generateInverses();
    }

    @Override
    public void setAttributeValue(ExistentialAttributeRecord value) {
        //        Attribute attribute = value.getAttribute();
        //        Attribute validatingAttribute = model.getAttributeModel()
        //                                             .getSingleChild(attribute,
        //                                                             model.getKernel()
        //                                                                  .getIsValidatedBy());
        //        if (validatingAttribute != null) {
        //            TypedQuery<AttributeMetaAttribute> query = em.createNamedQuery(AttributeMetaAttribute.GET_ATTRIBUTE,
        //                                                                           AttributeMetaAttribute.class);
        //            query.setParameter("ruleform", validatingAttribute);
        //            query.setParameter("attribute", attribute);
        //            List<AttributeMetaAttribute> attrs = query.getResultList();
        //            if (attrs == null || attrs.size() == 0) {
        //                throw new IllegalArgumentException("No valid values for attribute "
        //                                                   + attribute.getName());
        //            }
        //            boolean valid = false;
        //            for (AttributeMetaAttribute ama : attrs) {
        //                if (ama.getValue() != null && ama.getValue()
        //                                                 .equals(value.getValue())) {
        //                    valid = true;
        //                    em.persist(value);
        //                }
        //            }
        //            if (!valid) {
        //                throw new IllegalArgumentException(String.format("%s is not a valid value for attribute %s",
        //                                                                 value.getValue(),
        //                                                                 attribute));
        //            }
        //        }

    }

    @Override
    public void setAuthorized(RuleForm ruleform, Relationship relationship,
                              List<? extends ExistentialRuleform> authorized) {
        deauthorizeAll(ruleform, relationship,
                       getAllAuthorized(ruleform, relationship));
        authorizeAll(ruleform, relationship, authorized);
    }

    @Override
    public void setImmediateChild(RuleForm parent, Relationship relationship,
                                  RuleForm child, Agency updatedBy) {

        unlink(parent, relationship, child);
        link(parent, relationship, child, updatedBy);

    }

    @Override
    public void unlink(RuleForm parent, Relationship relationship,
                       RuleForm child) {
        create.deleteFrom(EXISTENTIAL_NETWORK)
              .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
              .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
              .and(EXISTENTIAL_NETWORK.CHILD.equal(child.getId()))
              .execute();
    }

    @Override
    public void unlinkImmediate(RuleForm parent, Relationship relationship) {
        create.deleteFrom(EXISTENTIAL_NETWORK)
              .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
              .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
              .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
              .execute();
    }

    private void alterDeductionTablesForNextPass() {
        em.createNativeQuery("TRUNCATE TABLE last_pass_rules")
          .executeUpdate();
        em.createNativeQuery("ALTER TABLE current_pass_rules RENAME TO temp_last_pass_rules")
          .executeUpdate();
        em.createNativeQuery("ALTER TABLE last_pass_rules RENAME TO current_pass_rules")
          .executeUpdate();
        em.createNativeQuery("ALTER TABLE temp_last_pass_rules RENAME TO last_pass_rules")
          .executeUpdate();
        em.createNativeQuery("TRUNCATE working_memory")
          .executeUpdate();
    }

    private void createCurrentPassRules() {
        em.createNativeQuery("CREATE TEMPORARY TABLE IF NOT EXISTS current_pass_rules ("
                             + "id uuid NOT NULL," + "parent uuid NOT NULL,"
                             + "relationship uuid NOT NULL,"
                             + "child uuid NOT NULL,"
                             + "premise1 uuid NOT NULL,"
                             + "premise2 uuid NOT NULL,"
                             + "inference uuid NOT NULL )")
          .executeUpdate();
    }

    private void createDeductionTemporaryTables(boolean initial) {
        if (initial) {
            createWorkingMemory();
            createCurrentPassRules();
            createLastPassRules();
        }
        em.createNativeQuery("TRUNCATE working_memory")
          .executeUpdate();
        em.createNativeQuery("TRUNCATE current_pass_rules")
          .executeUpdate();
        em.createNativeQuery("TRUNCATE last_pass_rules")
          .executeUpdate();
    }

    private void createLastPassRules() {
        em.createNativeQuery("CREATE TEMPORARY TABLE IF NOT EXISTS last_pass_rules ("
                             + "id uuid NOT NULL," + "parent uuid NOT NULL,"
                             + "relationship uuid NOT NULL,"
                             + "child uuid NOT NULL,"
                             + "premise1 uuid NOT NULL,"
                             + "premise2 uuid NOT NULL,"
                             + "inference uuid NOT NULL )")
          .executeUpdate();
    }

    private void createWorkingMemory() {
        em.createNativeQuery("CREATE TEMPORARY TABLE IF NOT EXISTS working_memory("
                             + "parent uuid NOT NULL,"
                             + "relationship uuid NOT NULL,"
                             + "child uuid NOT NULL,"
                             + "premise1 uuid NOT NULL,"
                             + "premise2 uuid NOT NULL,"
                             + "inference uuid NOT NULL )")
          .executeUpdate();
    }

    // Deduce the new rules
    private void deduce() {
        int deductions = em.createNamedQuery(networkPrefix
                                             + DEDUCE_NEW_NETWORK_RULES_SUFFIX)
                           .executeUpdate();
        if (log.isTraceEnabled()) {
            log.trace(String.format("deduced %s rules", deductions));

        }
    }

    private void generateInverses() {
        long then = System.currentTimeMillis();
        int inverses = em.createNamedQuery(String.format("%s%s", networkPrefix,
                                                         GENERATE_NETWORK_INVERSES_SUFFIX))
                         .executeUpdate();
        if (log.isTraceEnabled()) {
            log.trace(String.format("created %s inverse rules of %s in %s ms",
                                    inverses, networkPrefix,
                                    System.currentTimeMillis() - then));
        }
    }

    @SuppressWarnings("unchecked")
    private List<RuleForm> getImmediateChildren(UUID parent,
                                                UUID relationship) {
        return (List<RuleForm>) create.selectDistinct(EXISTENTIAL.fields())
                                      .from(EXISTENTIAL)
                                      .join(EXISTENTIAL_NETWORK)
                                      .on(EXISTENTIAL_NETWORK.PARENT.equal(parent))
                                      .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship))
                                      .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
                                      .fetch()
                                      .into(domainClass());
    }

    // Infer all possible rules
    private int infer(boolean firstPass) {
        int newRules;
        if (firstPass) {
            newRules = em.createNamedQuery(networkPrefix
                                           + INFERENCE_STEP_SUFFIX)
                         .executeUpdate();
            firstPass = false;
        } else {
            newRules = em.createNamedQuery(networkPrefix
                                           + INFERENCE_STEP_FROM_LAST_PASS_SUFFIX)
                         .executeUpdate();
        }
        if (log.isTraceEnabled()) {
            log.trace(String.format("inferred %s new rules", newRules));
        }
        return newRules;
    }

    /**
     * @return
     */
    private int insert() {// Insert the new rules
        Query insert = em.createNamedQuery(networkPrefix
                                           + INSERT_NEW_NETWORK_RULES_SUFFIX);
        int inserted = insert.executeUpdate();
        if (log.isTraceEnabled()) {
            log.trace(String.format("inserted %s new rules", inserted));
        }
        if (inserted > MAX_DEDUCTIONS) {
            throw new IllegalStateException(String.format("Inserted more than %s deductions: %s, possible runaway inference",
                                                          MAX_DEDUCTIONS,
                                                          inserted));
        }
        return inserted;
    }

    abstract protected ExistentialDomain domain();

    abstract protected Class<? extends ExistentialRecord> domainClass();

    abstract protected Class<?> getAgencyGroupingClass();

    /**
     * @param attribute
     * @param authorizations
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <ValueType> List<ValueType> getAllowedValues(Attribute attribute,
                                                           List<ExistentialAttributeAuthorizationRecord> authorizations) {
        switch (attribute.getValueType()) {
            case BOOLEAN: {
                return (List<ValueType>) Arrays.asList(Boolean.TRUE,
                                                       Boolean.FALSE);
            }
            case BINARY: {
                return Collections.EMPTY_LIST;
            }
            default:
        }

        List<ValueType> allowedValues = new ArrayList<ValueType>();
        for (ExistentialAttributeAuthorizationRecord authorization : authorizations) {
            allowedValues.add((ValueType) authorization.getValue());
        }
        return allowedValues;
    }
}
