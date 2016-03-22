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
import static com.chiralbehaviors.CoRE.jooq.Tables.FACET;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectOnConditionStep;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.tables.AgencyExistentialGrouping;
import com.chiralbehaviors.CoRE.jooq.tables.Existential;
import com.chiralbehaviors.CoRE.jooq.tables.ExistentialAttributeAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.ExistentialNetworkAttributeAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.ExistentialNetworkAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.Facet;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.PhantasmModel;
import com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace;
import com.hellblazer.utils.Tuple;

/**
 * @author hhildebrand
 *
 */
public class PhantasmModelImpl implements PhantasmModel {
    private static final Integer ZERO = Integer.valueOf(0);

    private final DSLContext     create;
    private final Model          model;

    public PhantasmModelImpl(Model model) {
        this.model = model;
        this.create = model.create();
    }

    @Override
    public void authorize(FacetRecord aspect, Attribute... attributes) {
        // TODO Auto-generated method stub

    }

    @Override
    public void authorize(ExistentialRuleform ruleform,
                          Relationship relationship,
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
    public void authorizeAll(ExistentialRuleform ruleform,
                             Relationship relationship,
                             List<? extends ExistentialRuleform> authorized) {
        for (ExistentialRuleform agency : authorized) {
            authorize(ruleform, relationship, agency);
        }
    }

    @Override
    public void authorizeSingular(ExistentialRuleform ruleform,
                                  Relationship relationship,
                                  ExistentialRuleform authorized) {
        deauthorize(ruleform, relationship,
                    getImmediateChild(ruleform, relationship));
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

    @Override
    public boolean checkCapability(ExistentialRuleform instance,
                                   Relationship capability) {
        return checkCapability(model.getCurrentPrincipal()
                                    .getCapabilities(),
                               instance, capability);
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
                                 .and(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.FACET.equal(stateAuth.getFacet()))
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
                                 .and(EXISTENTIAL_NETWORK_AUTHORIZATION.PARENT.equal(stateAuth.getParent()))
                                 .and(EXISTENTIAL_NETWORK_AUTHORIZATION.RELATIONSHIP.equal(stateAuth.getRelationship()))
                                 .and(EXISTENTIAL_NETWORK_AUTHORIZATION.CHILD.equal(stateAuth.getChild()))
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
    public boolean checkCapability(List<Agency> agencies,
                                   ExistentialRuleform instance,
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
    public boolean checkCapability(FacetRecord facet, Relationship capability) {
        return checkCapability(model.getCurrentPrincipal()
                                    .getCapabilities(),
                               facet, capability);
    }

    /**
     * Check the capability of an agency on the facet.
     */
    @Override
    public boolean checkCapability(List<Agency> agencies, FacetRecord facet,
                                   Relationship capability) {
        Facet required = FACET.as("required");
        return ZERO.equals(create.selectCount()
                                 .from(required)
                                 .where(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORITY.isNotNull())
                                 .and(FACET.CLASSIFIER.equal(facet.getClassifier()))
                                 .and(FACET.CLASSIFICATION.equal(facet.getClassification()))
                                 .andNotExists(create.select(required.field(FACET.AUTHORITY))
                                                     .from(EXISTENTIAL_NETWORK)
                                                     .where(EXISTENTIAL_NETWORK.PARENT.in(agencies.stream()
                                                                                                  .map(a -> a.getId())
                                                                                                  .collect(Collectors.toList())))
                                                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(capability.getId()))
                                                     .and(EXISTENTIAL_NETWORK.CHILD.equal(required.field(FACET.AUTHORITY))))
                                 .fetchOne()
                                 .value1());
    }

    @Override
    public boolean checkCapability(ExistentialNetworkAttributeAuthorizationRecord stateAuth,
                                   Relationship capability) {
        return checkCapability(model.getCurrentPrincipal()
                                    .getCapabilities(),
                               stateAuth, capability);
    }

    /**
     * Check the capability of an agency on an attribute of the authorized
     * relationship of the facet child relationship.
     */
    @Override
    public boolean checkCapability(List<Agency> agencies,
                                   ExistentialNetworkAttributeAuthorizationRecord stateAuth,
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
    public ExistentialAttributeRecord create(ExistentialRuleform ruleform,
                                             Attribute attribute,
                                             Agency updateBy) {
        return null;
    }

    @Override
    public void deauthorize(ExistentialRuleform ruleform,
                            Relationship relationship,
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
    public void deauthorizeAll(ExistentialRuleform ruleform,
                               Relationship relationship,
                               List<? extends ExistentialRuleform> authorized) {
        for (ExistentialRuleform agency : authorized) {
            deauthorize(ruleform, relationship, agency);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#getAttributeAuthorizations(com
     * .hellblazer.CoRE.meta.Aspect, com.chiralbehaviors.CoRE.attribute.Attribute)
     */
    @Override
    public List<ExistentialAttributeAuthorizationRecord> getAttributeAuthorizations(FacetRecord aspect,
                                                                                    Attribute attribute) {

        return create.selectDistinct(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.fields())
                     .from(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.as("auth"))
                     .join(FACET.as("na"))
                     .on(FACET.ID.eq(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.FACET))
                     .join(EXISTENTIAL_NETWORK.as("network"))
                     .on(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(aspect.getClassifier()))
                     .and(EXISTENTIAL_NETWORK.CHILD.equal(aspect.getClassification()))
                     .and(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.AUTHORIZED_ATTRIBUTE.equal(attribute.getId()))
                     .and(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.AUTHORITY.isNull())
                     .fetch()
                     .into(ExistentialAttributeAuthorizationRecord.class);
    }

    @Override
    public List<ExistentialAttributeAuthorizationRecord> getAttributeAuthorizations(FacetRecord aspect,
                                                                                    boolean includeGrouping) {

        SelectOnConditionStep<Record> and = create.selectDistinct(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.fields())
                                                  .from(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION)
                                                  .join(FACET)
                                                  .on(FACET.ID.eq(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.FACET))
                                                  .join(EXISTENTIAL_NETWORK)
                                                  .on(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(aspect.getClassifier()))
                                                  .and(EXISTENTIAL_NETWORK.CHILD.equal(aspect.getClassification()))
                                                  .and(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.AUTHORITY.isNull());
        if (!includeGrouping) {
            and = and.and(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.AUTHORITY.isNull());
        }
        return and.fetch()
                  .into(ExistentialAttributeAuthorizationRecord.class)
                  .stream()
                  .collect(Collectors.toList());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#getAttributesClassifiedBy(com
     * .hellblazer.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.meta.Aspect)
     */
    @Override
    public List<ExistentialAttributeRecord> getAttributesClassifiedBy(ExistentialRuleform ruleform,
                                                                      FacetRecord aspect) {
        return create.selectDistinct(EXISTENTIAL_ATTRIBUTE.fields())
                     .from(EXISTENTIAL_ATTRIBUTE.as("attrValue"))

                     .join(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.as("auth"))
                     .on(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.FACET.equal(FACET.ID))

                     .join(FACET.as("na"))
                     .on(FACET.ID.eq(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.ID))
                     .and(FACET.CLASSIFICATION.eq(aspect.getClassification()))
                     .and(FACET.CLASSIFIER.eq(aspect.getClassifier()))

                     .join(EXISTENTIAL_NETWORK.as("network"))
                     .on(EXISTENTIAL_NETWORK.PARENT.equal(ruleform.getId()))
                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(FACET.CLASSIFIER))
                     .and(EXISTENTIAL_NETWORK.CHILD.equal(FACET.CLASSIFICATION))

                     .where(EXISTENTIAL_ATTRIBUTE.ATTRIBUTE.equal(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.AUTHORIZED_ATTRIBUTE))
                     .and(EXISTENTIAL_ATTRIBUTE.EXISTENTIAL.eq(ruleform.getId()))
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
    public List<ExistentialAttributeRecord> getAttributesGroupedBy(ExistentialRuleform ruleform,
                                                                   Agency groupingAgency) {
        return create.selectDistinct(EXISTENTIAL_ATTRIBUTE.fields())
                     .from(EXISTENTIAL_ATTRIBUTE.as("attrValue"))
                     .join(FACET.as("auth"))
                     .on(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(FACET.CLASSIFIER))
                     .join(FACET.as("na"))
                     .on(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.FACET.eq(FACET.ID))
                     .join(EXISTENTIAL_NETWORK.as("network"))
                     .on(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(FACET.CLASSIFIER))
                     .and(EXISTENTIAL_NETWORK.CHILD.equal(FACET.CLASSIFICATION))
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
    public ExistentialAttributeRecord getAttributeValue(ExistentialRuleform ruleform,
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
    public ExistentialNetworkAttributeRecord getAttributeValue(ExistentialRuleform parent,
                                                               Relationship r,
                                                               ExistentialRuleform child,
                                                               Attribute attribute) {
        ExistentialNetworkRecord edge = getImmediateChildLink(parent, r, child);
        if (edge == null) {
            return null;
        }
        return getAttributeValue(edge, attribute);
    }

    @Override
    public List<ExistentialAttributeRecord> getAttributeValues(ExistentialRuleform ruleform,
                                                               Attribute attribute) {
        return create.selectFrom(EXISTENTIAL_ATTRIBUTE)
                     .where(EXISTENTIAL_ATTRIBUTE.EXISTENTIAL.eq(ruleform.getId()))
                     .and(EXISTENTIAL_ATTRIBUTE.ATTRIBUTE.eq(attribute.getId()))
                     .fetch()
                     .into(ExistentialAttributeRecord.class);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getChild(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public ExistentialRuleform getChild(ExistentialRuleform parent,
                                        Relationship relationship) {
        return model.records()
                    .resolve(create.selectDistinct(EXISTENTIAL.fields())
                                   .from(EXISTENTIAL)
                                   .join(EXISTENTIAL_NETWORK)
                                   .on(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                                   .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                                   .and(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL.ID))
                                   .fetchOne()
                                   .into(ExistentialRecord.class));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#getNetwork(com.chiralbehaviors.CoRE
     * .network.Networked, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public List<ExistentialRuleform> getChildren(ExistentialRuleform parent,
                                                 Relationship relationship) {
        return create.selectDistinct(EXISTENTIAL.fields())
                     .from(EXISTENTIAL)
                     .join(EXISTENTIAL_NETWORK)
                     .on(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                     .and(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL.ID))
                     .fetch()
                     .into(ExistentialRecord.class)
                     .stream()
                     .map(r -> model.records()
                                    .resolve(r))
                     .collect(Collectors.toList());
    }

    @Override
    public FacetRecord getFacetDeclaration(Relationship classifier,
                                           ExistentialRuleform classification) {
        return create.selectFrom(FACET)
                     .where(FACET.CLASSIFIER.equal(classifier.getId()))
                     .and(FACET.CLASSIFICATION.equal(classification.getId()))
                     .fetchOne();
    }

    @Override
    public ExistentialRuleform getImmediateChild(ExistentialRuleform parent,
                                                 Relationship relationship) {
        return model.records()
                    .resolve(create.selectDistinct(EXISTENTIAL.fields())
                                   .from(EXISTENTIAL)
                                   .join(EXISTENTIAL_NETWORK)
                                   .on(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                                   .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                                   .and(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL.ID))
                                   .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
                                   .fetchOne()
                                   .into(ExistentialRecord.class));
    }

    @Override
    public ExistentialNetworkRecord getImmediateChildLink(ExistentialRuleform parent,
                                                          Relationship relationship,
                                                          ExistentialRuleform child) {
        return create.selectFrom(EXISTENTIAL_NETWORK)
                     .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                     .and(EXISTENTIAL_NETWORK.CHILD.equal(child.getId()))
                     .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
                     .fetchOne()
                     .into(ExistentialNetworkRecord.class);
    }

    @Override
    public List<ExistentialRuleform> getImmediateChildren(ExistentialRuleform parent,
                                                          Relationship relationship) {
        return getImmediateChildren(parent.getId(), relationship.getId());
    }

    @Override
    public List<ExistentialNetworkRecord> getImmediateChildrenLinks(ExistentialRuleform parent,
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
    public ExistentialNetworkRecord getImmediateLink(ExistentialRuleform parent,
                                                     Relationship relationship,
                                                     ExistentialRuleform child) {
        return create.selectFrom(EXISTENTIAL_NETWORK)
                     .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                     .and(EXISTENTIAL_NETWORK.CHILD.equal(child.getId()))
                     .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
                     .fetchOne()
                     .into(ExistentialNetworkRecord.class);
    }

    @Override
    public Collection<ExistentialNetworkRecord> getImmediateNetworkEdges(ExistentialRuleform parent) {
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
    public Collection<Relationship> getImmediateRelationships(ExistentialRuleform parent) {
        return create.selectDistinct(EXISTENTIAL.fields())
                     .from(EXISTENTIAL)
                     .join(EXISTENTIAL_NETWORK)
                     .on(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(EXISTENTIAL_NETWORK.ID))
                     .fetch()
                     .into(Relationship.class);
    }

    @Override
    public List<ExistentialRuleform> getInferredChildren(ExistentialRuleform parent,
                                                         Relationship relationship) {
        return create.selectDistinct(EXISTENTIAL.fields())
                     .from(EXISTENTIAL)
                     .join(EXISTENTIAL_NETWORK)
                     .on(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                     .and(EXISTENTIAL_NETWORK.INFERENCE.isNotNull())
                     .fetch()
                     .into(ExistentialRecord.class)
                     .stream()
                     .map(r -> model.records()
                                    .resolve(r))
                     .collect(Collectors.toList());
    }

    @Override
    public List<ExistentialRuleform> getInGroup(ExistentialRuleform parent,
                                                Relationship relationship) {
        return create.selectDistinct(EXISTENTIAL.fields())
                     .from(EXISTENTIAL)
                     .join(EXISTENTIAL_NETWORK)
                     .on(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL.ID))
                     .and(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                     .and(EXISTENTIAL_NETWORK.CHILD.notEqual(parent.getId()))
                     .fetch()
                     .into(ExistentialRecord.class)
                     .stream()
                     .map(r -> model.records()
                                    .resolve(r))
                     .collect(Collectors.toList());
    }

    @Override
    public List<ExistentialNetworkRecord> getInterconnections(Collection<ExistentialRuleform> parents,
                                                              Collection<Relationship> relationships,
                                                              Collection<ExistentialRuleform> children) {
        //        if (parents == null || parents.size() == 0 || relationships == null
        //            || relationships.size() == 0 || children == null
        //            || children.size() == 0) {
        //            return null;
        //        }
        //        TypedQuery<RelationshipNetwork> query = em.createNamedQuery(RelationshipNetwork.GET_NETWORKS,
        //                                                                    RelationshipNetwork.class);
        //        query.setParameter("parents", parents);
        //        query.setParameter("relationships", relationships);
        //        query.setParameter("children", children);
        //        return query.getResultList();
        return null;
    }

    @Override
    public List<ExistentialNetworkAuthorizationRecord> getNetworkAuthorizations(FacetRecord aspect,
                                                                                boolean includeGrouping) {

        SelectConditionStep<ExistentialNetworkAuthorizationRecord> and = create.selectFrom(EXISTENTIAL_NETWORK_AUTHORIZATION)
                                                                               .where(EXISTENTIAL_NETWORK_AUTHORIZATION.PARENT.equal(aspect.getId()));

        if (!includeGrouping) {
            and = and.and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORITY.isNull());
        }
        return and.fetch()
                  .into(ExistentialNetworkAuthorizationRecord.class);
    }

    @Override
    public List<ExistentialRuleform> getNotInGroup(ExistentialRuleform parent,
                                                   Relationship relationship) {

        Existential e = EXISTENTIAL.as("e");
        return create.selectFrom(e)
                     .whereNotExists(create.selectFrom(EXISTENTIAL_NETWORK)
                                           .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                                           .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                                           .and(EXISTENTIAL_NETWORK.CHILD.equal(e.field(EXISTENTIAL.ID))))
                     .fetch()
                     .into(ExistentialRecord.class)
                     .stream()
                     .map(r -> model.records()
                                    .resolve(r))
                     .collect(Collectors.toList());
    }

    @Override
    public ExistentialRuleform getSingleChild(ExistentialRuleform parent,
                                              Relationship relationship) {
        return model.records()
                    .resolve(create.selectDistinct(EXISTENTIAL.fields())
                                   .from(EXISTENTIAL)
                                   .join(EXISTENTIAL_NETWORK)
                                   .on(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                                   .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                                   .and(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL.ID))
                                   .fetchOne()
                                   .into(ExistentialRecord.class));
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.PhantasmModel#getValue(com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord)
     */
    @Override
    public Object getValue(ExistentialAttributeRecord attributeValue) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public final void initialize(ExistentialRuleform ruleform,
                                 FacetRecord aspect) {
        initialize(ruleform, aspect, null);
    }

    @Override
    public final void initialize(ExistentialRuleform ruleform,
                                 FacetRecord aspect,
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

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#isAccessible(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.network.Relationship, com.chiralbehaviors.CoRE.network.Relationship, com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public boolean isAccessible(ExistentialRuleform parent,
                                Relationship relationship,
                                ExistentialRuleform child) {
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
    public Tuple<ExistentialNetworkRecord, ExistentialNetworkRecord> link(ExistentialRuleform parent,
                                                                          Relationship r,
                                                                          ExistentialRuleform child,
                                                                          Agency updatedBy) {
        return link(parent.getId(), r.getId(), child.getId(),
                    updatedBy.getId());
    }

    public Tuple<ExistentialNetworkRecord, ExistentialNetworkRecord> link(UUID parent,
                                                                          UUID r,
                                                                          UUID child,
                                                                          UUID updatedBy) {
        ExistentialNetworkRecord forward = model.records()
                                                .newExistentialNetwork();
        forward.setParent(parent);
        forward.setRelationship(r);
        forward.setChild(child);
        forward.setUpdatedBy(updatedBy);

        ExistentialNetworkRecord inverse = model.records()
                                                .newExistentialNetwork();
        inverse.setParent(child);
        inverse.setRelationship(r);
        inverse.setChild(parent);
        inverse.setUpdatedBy(updatedBy);
        return new Tuple<>(forward, inverse);
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
    public void setAuthorized(ExistentialRuleform ruleform,
                              Relationship relationship,
                              List<? extends ExistentialRuleform> authorized) {
        deauthorizeAll(ruleform, relationship,
                       getAllAuthorized(ruleform, relationship));
        authorizeAll(ruleform, relationship, authorized);
    }

    @Override
    public List<? extends ExistentialRuleform> getAllAuthorized(ExistentialRuleform ruleform,
                                                                Relationship relationship) {
        return create.selectDistinct()
                     .from(EXISTENTIAL)
                     .join(EXISTENTIAL_NETWORK)
                     .on(EXISTENTIAL_NETWORK.PARENT.equal(ruleform.getId()))
                     .fetch()
                     .into(ExistentialRecord.class)
                     .stream()
                     .map(r -> model.records()
                                    .resolve(r))
                     .collect(Collectors.toList());
    }

    @Override
    public void setImmediateChild(ExistentialRuleform parent,
                                  Relationship relationship,
                                  ExistentialRuleform child, Agency updatedBy) {

        unlink(parent, relationship, child);
        link(parent, relationship, child, updatedBy);

    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.PhantasmModel#setValue(com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord, java.lang.Object)
     */
    @Override
    public void setValue(ExistentialAttributeRecord attributeValue,
                         Object value) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unlink(ExistentialRuleform parent, Relationship relationship,
                       ExistentialRuleform child) {
        create.deleteFrom(EXISTENTIAL_NETWORK)
              .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
              .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
              .and(EXISTENTIAL_NETWORK.CHILD.equal(child.getId()))
              .execute();
    }

    @Override
    public void unlinkImmediate(ExistentialRuleform parent,
                                Relationship relationship) {
        create.deleteFrom(EXISTENTIAL_NETWORK)
              .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
              .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
              .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
              .execute();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.PhantasmModel#valueClass(com.chiralbehaviors.CoRE.domain.Attribute)
     */
    @Override
    public Class<?> valueClass(Attribute attribute) {
        // TODO Auto-generated method stub
        return null;
    }

    private List<ExistentialRuleform> getImmediateChildren(UUID parent,
                                                           UUID relationship) {
        return create.selectDistinct(EXISTENTIAL.fields())
                     .from(EXISTENTIAL)
                     .join(EXISTENTIAL_NETWORK)
                     .on(EXISTENTIAL_NETWORK.PARENT.equal(parent))
                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship))
                     .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
                     .fetch()
                     .into(ExistentialRecord.class)
                     .stream()
                     .map(r -> model.records()
                                    .resolve(r))
                     .collect(Collectors.toList());
    }

    private void setValue(ExistentialAttributeRecord attribute,
                          ExistentialAttributeAuthorizationRecord authorization) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.PhantasmModel#getAllAuthorized(com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord)
     */
    @Override
    public <T extends ExistentialRuleform> List<T> getAllAuthorized(FacetRecord facet) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.PhantasmModel#getAuthorized(com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord)
     */
    @Override
    public <T extends ExistentialRuleform> T getAuthorized(FacetRecord facet) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.PhantasmModel#initialize(com.chiralbehaviors.CoRE.domain.ExistentialRuleform, com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord, com.chiralbehaviors.CoRE.domain.Agency)
     */
    @Override
    public void initialize(ExistentialRuleform instance,
                           ExistentialNetworkAuthorizationRecord facet,
                           Agency principal) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.PhantasmModel#setValue(com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeRecord, java.lang.String)
     */
    @Override
    public void setValue(ExistentialNetworkAttributeRecord attribute,
                         String name) {
        // TODO Auto-generated method stub

    }
}
