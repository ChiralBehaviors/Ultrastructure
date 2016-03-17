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
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_ATTRIBUTE_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK_AUTHORIZATION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.ExistentialDomain;
import com.chiralbehaviors.CoRE.domain.Location;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.tables.AgencyExistentialGrouping;
import com.chiralbehaviors.CoRE.jooq.tables.ExistentialAttributeAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.ExistentialNetworkAttributeAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.ExistentialNetworkAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkRecord;
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
abstract public class ExistentialModelImpl<RuleForm extends ExistentialDomain>
        implements ExistentialModel<RuleForm> {

    private static final String   CHECK_CAP      = ".checkCap";
    private static Logger         log            = LoggerFactory.getLogger(ExistentialModelImpl.class);
    private static int            MAX_DEDUCTIONS = 1000;
    private static final Long     ZERO           = Long.valueOf(0);

    protected final EntityManager em;
    protected final Kernel        kernel;
    protected final Model         model;
    protected final DSLContext    create;

    @SuppressWarnings("unchecked")
    public ExistentialModelImpl(Model model, DSLContext create) {
        this.model = model;
        this.em = model.getEntityManager();
        this.kernel = model.getKernel();
        this.create = create;
    }

    @Override
    public void authorize(RuleForm ruleform, Relationship relationship,
                          ExistentialDomain authorized) {
        throw new UnsupportedOperationException(String.format("Authorizations between %s and Agency are not defined",
                                                              ruleform.getClass()
                                                                      .getSimpleName()));
    }

    @Override
    public void authorize(RuleForm ruleform, Relationship relationship,
                          List<ExistentialDomain> authorized) {
        for (ExistentialDomain agency : authorized) {
            authorize(ruleform, relationship, agency);
        }
    }

    @Override
    public void authorizeSingular(RuleForm ruleform, Relationship relationship,
                                  ExistentialDomain authorized) {
        deauthorize(ruleform, relationship,
                    getAuthorizedAgency(ruleform, relationship));
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
    public boolean checkCapability(RuleForm instance, Relationship capability) {
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
        return create.selectCount()
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
                     .value1() == 0;

    }

    /**
     * Check the capability of an agency on an instance.
     */
    @Override
    public boolean checkCapability(List<Agency> agencies, RuleForm instance,
                                   Relationship capability) {
        AgencyExistentialGrouping required = AGENCY_EXISTENTIAL_GROUPING.as("required");
        return create.selectCount()
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
                     .value1() == 0;
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
        return create.selectCount()
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
                     .value1() == 0;
    }

    @Override
    public boolean checkCapability(ExistentialNetworkAuthorizationRecord auth,
                                   Relationship capability) {
        return checkCapability(model.getCurrentPrincipal()
                                    .getCapabilities(),
                               auth, capability);
    }

    /**
     * Check the capability of an agency on the facet.
     */
    @Override
    public boolean checkFacetCapability(List<Agency> agencies,
                                        ExistentialNetworkAuthorizationRecord facet,
                                        Relationship capability) {
        ExistentialNetworkAuthorization required = EXISTENTIAL_NETWORK_AUTHORIZATION.as("required");
        return create.selectCount()
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
                     .value1() == 0;
    }

    @Override
    public boolean checkFacetCapability(ExistentialNetworkAuthorizationRecord facet,
                                        Relationship capability) {
        return checkFacetCapability(model.getCurrentPrincipal()
                                         .getCapabilities(),
                                    facet, capability);
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
        return create.selectCount()
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
                     .value1() == 0;
    }

    @Override
    public void deauthorize(RuleForm ruleform, Relationship relationship,
                            ExistentialDomain authorized) {
        throw new UnsupportedOperationException(String.format("Authorizations between %s and Agency are not defined",
                                                              ruleform.getClass()
                                                                      .getSimpleName()));
    }

    @Override
    public void deauthorize(RuleForm ruleform, Relationship relationship,
                            List<ExistentialDomain> authorized) {
        for (ExistentialDomain agency : authorized) {
            deauthorize(ruleform, relationship, agency);
        }
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#find(long)
     */
    @Override
    public RuleForm find(UUID id) {
        RuleForm rf = em.find(entity, id);
        return rf;
    }

    @Override
    public List<RuleForm> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RuleForm> cq = cb.createQuery(entity);
        cq.from(entity);
        return em.createQuery(cq)
                 .getResultList();
    }

    @Override
    public List<Aspect<RuleForm>> getAllFacets() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        Class<ExistentialNetworkAuthorizationRecord<RuleForm>> clazz = (Class<ExistentialNetworkAuthorizationRecord<RuleForm>>) getNetworkAuthClass();
        CriteriaQuery<ExistentialNetworkAuthorizationRecord<RuleForm>> query = cb.createQuery(clazz);
        Root<ExistentialNetworkAuthorizationRecord<RuleForm>> networkRoot = query.from(clazz);
        query.select(networkRoot)
             .where(cb.and(cb.isNull(networkRoot.get("authorizedParent")),
                           cb.isNull(networkRoot.get("authorizedRelationship")),
                           cb.isNull(networkRoot.get("childRelationship")),
                           cb.isNull(networkRoot.get("groupingAgency"))));
        TypedQuery<ExistentialNetworkAuthorizationRecord<RuleForm>> q = em.createQuery(query);
        List<Aspect<RuleForm>> facets = new ArrayList<>();
        for (ExistentialNetworkAuthorizationRecord<RuleForm> auth : q.getResultList()) {
            facets.add(new Aspect<>(auth.getClassifier(),
                                    auth.getClassification()));
        }
        return facets;
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

    @Override
    public Aspect<RuleForm> getAspect(UUID classifier, UUID classification) {
        Relationship rel = em.find(Relationship.class, classifier);
        if (rel == null) {
            throw new IllegalArgumentException(String.format("classifying relationship %s does not exist",
                                                             classifier));
        }
        RuleForm rf = find(classification);
        if (rf == null) {
            throw new IllegalArgumentException(String.format("classification %s does not exist",
                                                             classification));
        }
        return new Aspect<RuleForm>(rel, rf);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#getAttributeAuthorizations(com
     * .hellblazer.CoRE.agency.Agency)
     */
    @Override
    public List<ExistentialAttributeAuthorizationRecord> getAttributeAuthorizations(Agency groupingAgency) {
        TypedQuery<ExistentialAttributeAuthorizationRecord> query = em.createNamedQuery(prefix
                                                                                        + FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_SUFFIX,
                                                                                        authorization);
        query.setParameter("groupingAgency", groupingAgency);
        return query.getResultList();
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
        TypedQuery<ExistentialAttributeAuthorizationRecord> query = em.createNamedQuery(prefix
                                                                                        + FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_FOR_ATTRIBUTE_SUFFIX,
                                                                                        authorization);
        query.setParameter("groupingAgency", groupingAgency);
        query.setParameter("attribute", attribute);
        return query.getResultList();
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
        TypedQuery<ExistentialAttributeAuthorizationRecord> query = em.createNamedQuery(prefix
                                                                                        + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE_SUFFIX,
                                                                                        authorization);
        query.setParameter("classifier", aspect.getClassifier());
        query.setParameter("classification", aspect.getClassification());
        query.setParameter("attribute", attribute);
        return query.getResultList();
    }

    @Override
    public List<ExistentialAttributeAuthorizationRecord> getAttributeAuthorizations(Aspect<RuleForm> aspect,
                                                                                    boolean includeGrouping) {
        TypedQuery<ExistentialAttributeAuthorizationRecord> query = em.createNamedQuery(prefix
                                                                                        + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX,
                                                                                        authorization);
        query.setParameter("classifier", aspect.getClassifier());
        query.setParameter("classification", aspect.getClassification());
        List<ExistentialAttributeAuthorizationRecord> result = query.getResultList();
        return includeGrouping ? result.stream()
                                       .filter(classifier -> classifier.getGroupingAgency() == null)
                                       .collect(Collectors.toList())
                               : result;
    }

    @Override
    public List<ExistentialAttributeAuthorizationRecord> getAttributeAuthorizations(ExistentialNetworkAuthorizationRecord facet,
                                                                                    boolean includeGrouping) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ExistentialAttributeAuthorizationRecord> query = cb.createQuery(authorization);
        Root<ExistentialAttributeAuthorizationRecord> authRoot = query.from(authorization);
        query.select(authRoot)
             .where(cb.and(cb.equal(authRoot.get("networkAuthorization"),
                                    facet),
                           cb.isNull(authRoot.get(AttributeAuthorization_.groupingAgency))));
        TypedQuery<ExistentialAttributeAuthorizationRecord> q = em.createQuery(query);
        return q.getResultList();
    }

    @Override
    public List<AttributeType> getAttributesClassifiedBy(RuleForm ruleform,
                                                         Agency groupingAgency) {
        TypedQuery<AttributeType> query = em.createNamedQuery(prefix
                                                              + FIND_GROUPED_ATTRIBUTE_VALUES_SUFFIX,
                                                              attribute);
        query.setParameter("ruleform", ruleform);
        query.setParameter("agency", groupingAgency);
        return query.getResultList();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#getAttributesClassifiedBy(com
     * .hellblazer.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.meta.Aspect)
     */
    @Override
    public List<AttributeType> getAttributesClassifiedBy(RuleForm ruleform,
                                                         Aspect<RuleForm> aspect) {
        TypedQuery<AttributeType> query = em.createNamedQuery(prefix
                                                              + FIND_CLASSIFIED_ATTRIBUTE_VALUES_SUFFIX,
                                                              attribute);
        query.setParameter("ruleform", ruleform);
        query.setParameter("classifier", aspect.getClassifier());
        query.setParameter("classification", aspect.getClassification());
        return query.getResultList();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#getAttributesGroupedBy(com.chiralbehaviors
     * .CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.agency.Agency)
     */
    @Override
    public List<AttributeType> getAttributesGroupedBy(RuleForm ruleform,
                                                      Agency groupingAgency) {
        TypedQuery<AttributeType> query = em.createNamedQuery(prefix
                                                              + FIND_GROUPED_ATTRIBUTE_VALUES_SUFFIX,
                                                              attribute);
        query.setParameter("ruleform", ruleform);
        query.setParameter("agency", groupingAgency);
        return query.getResultList();
    }

    @Override
    public NetworkAttribute<?> getAttributeValue(ExistentialNetworkRecord edge,
                                                 Attribute attribute) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        CriteriaQuery<NetworkAttribute<?>> query = (CriteriaQuery<NetworkAttribute<?>>) cb.createQuery(edge.getAttributeClass());
        @SuppressWarnings("unchecked")
        Root<NetworkAttribute<?>> attributeRoot = (Root<NetworkAttribute<?>>) query.from(edge.getAttributeClass());
        query.select(attributeRoot)
             .where(cb.and(cb.equal(attributeRoot.get("attribute"), attribute),
                           cb.equal(attributeRoot.get("network"), edge)));
        TypedQuery<NetworkAttribute<?>> q = em.createQuery(query);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public AttributeType getAttributeValue(RuleForm ruleform,
                                           Attribute attribute) {
        List<AttributeType> values = getAttributeValues(ruleform, attribute);
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
    public NetworkAttribute<?> getAttributeValue(RuleForm parent,
                                                 Relationship r, RuleForm child,
                                                 Attribute attribute) {
        ExistentialNetworkRecord edge = getImmediateChildLink(parent, r, child);
        if (edge == null) {
            return null;
        }
        return getAttributeValue(edge, attribute);
    }

    @Override
    public List<AttributeType> getAttributeValues(RuleForm ruleform,
                                                  Attribute attribute) {
        TypedQuery<AttributeType> q = em.createNamedQuery(attributePrefix
                                                          + AttributeValue.GET_ATTRIBUTE_SUFFIX,
                                                          this.attribute);
        q.setParameter("ruleform", ruleform);
        q.setParameter("attribute", attribute);
        return q.getResultList();
    }

    @Override
    public List<Agency> getAuthorizedAgencies(RuleForm ruleform,
                                              Relationship relationship) {
        throw new UnsupportedOperationException(String.format("%s to Agency authorizations are undefined",
                                                              ruleform.getClass()
                                                                      .getSimpleName()));
    }

    @Override
    public Agency getAuthorizedAgency(RuleForm ruleform,
                                      Relationship relationship) {
        List<Agency> result = getAuthorizedAgencies(ruleform, relationship);
        if (result.isEmpty()) {
            return null;
        } else if (result.size() > 1) {
            throw new IllegalStateException(String.format("%s is a non singular authorization of %s",
                                                          relationship,
                                                          ruleform));
        }
        return result.get(0);
    }

    @Override
    public Location getAuthorizedLocation(RuleForm ruleform,
                                          Relationship relationship) {
        List<Location> result = getAuthorizedLocations(ruleform, relationship);
        if (result.isEmpty()) {
            return null;
        } else if (result.size() > 1) {
            throw new IllegalStateException(String.format("%s is a non singular authorization of %s",
                                                          relationship,
                                                          ruleform));
        }
        return result.get(0);
    }

    @Override
    public List<Location> getAuthorizedLocations(RuleForm ruleform,
                                                 Relationship relationship) {
        throw new UnsupportedOperationException(String.format("%s to Location authorizations are undefined",
                                                              ruleform.getClass()
                                                                      .getSimpleName()));
    }

    @Override
    public Product getAuthorizedProduct(RuleForm ruleform,
                                        Relationship relationship) {
        List<Product> result = getAuthorizedProducts(ruleform, relationship);
        if (result.isEmpty()) {
            return null;
        } else if (result.size() > 1) {
            throw new IllegalStateException(String.format("%s is a non singular authorization of %s",
                                                          relationship,
                                                          ruleform));
        }
        return result.get(0);
    }

    @Override
    public List<Product> getAuthorizedProducts(RuleForm ruleform,
                                               Relationship relationship) {
        throw new UnsupportedOperationException(String.format("%s to Product authorizations are undefined",
                                                              ruleform.getClass()
                                                                      .getSimpleName()));
    }

    @Override
    public Relationship getAuthorizedRelationship(RuleForm ruleform,
                                                  Relationship relationship) {
        List<Relationship> result = getAuthorizedRelationships(ruleform,
                                                               relationship);
        if (result.isEmpty()) {
            return null;
        } else if (result.size() > 1) {
            throw new IllegalStateException(String.format("%s is a non singular authorization of %s",
                                                          relationship,
                                                          ruleform));
        }
        return result.get(0);
    }

    @Override
    public List<Relationship> getAuthorizedRelationships(RuleForm ruleform,
                                                         Relationship relationship) {
        throw new UnsupportedOperationException(String.format("%s to Product authorizations are undefined",
                                                              ruleform.getClass()
                                                                      .getSimpleName()));
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getChild(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public RuleForm getChild(RuleForm parent, Relationship relationship) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RuleForm> query = cb.createQuery(entity);
        Root<ExistentialNetworkRecord> networkRoot = query.from(network);
        Path<RuleForm> path;
        try {
            path = networkRoot.get("child");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        query.select(path)
             .where(cb.and(cb.equal(networkRoot.get("parent"), parent),
                           cb.equal(networkRoot.get("relationship"),
                                    relationship)));
        TypedQuery<RuleForm> q = em.createQuery(query);
        return q.getSingleResult();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#getNetwork(com.chiralbehaviors.CoRE
     * .network.Networked, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public List<RuleForm> getChildren(RuleForm parent,
                                      Relationship relationship) {
        String prefix = entity.getSimpleName()
                              .toLowerCase()
                        + "ExistentialNetworkRecord";
        TypedQuery<RuleForm> q = em.createNamedQuery(prefix
                                                     + ExistentialRuleform.GET_CHILDREN_SUFFIX,
                                                     entity);
        q.setParameter("parent", parent);
        q.setParameter("relationship", relationship);
        List<RuleForm> resultList = q.getResultList();
        return resultList;
    }

    @Override
    public ExistentialNetworkAuthorizationRecord getFacetDeclaration(@SuppressWarnings("rawtypes") Aspect aspect) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        Class<ExistentialNetworkAuthorizationRecord<RuleForm>> clazz = (Class<ExistentialNetworkAuthorizationRecord<RuleForm>>) getNetworkAuthClass();
        CriteriaQuery<ExistentialNetworkAuthorizationRecord<RuleForm>> query = cb.createQuery(clazz);
        Root<ExistentialNetworkAuthorizationRecord<RuleForm>> networkRoot = query.from(clazz);
        query.select(networkRoot)
             .where(cb.and(cb.equal(networkRoot.get("classification"),
                                    aspect.getClassification()),
                           cb.equal(networkRoot.get("classifier"),
                                    aspect.getClassifier()),
                           cb.isNull(networkRoot.get("childRelationship")),
                           cb.isNull(networkRoot.get("authorizedRelationship")),
                           cb.isNull(networkRoot.get("authorizedParent")),
                           cb.isNull(networkRoot.get("groupingAgency"))));
        TypedQuery<ExistentialNetworkAuthorizationRecord<RuleForm>> q = em.createQuery(query);
        List<ExistentialNetworkAuthorizationRecord<RuleForm>> results = q.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<ExistentialNetworkAuthorizationRecord> getFacets(Product workspace) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        Class<ExistentialNetworkAuthorizationRecord<RuleForm>> clazz = (Class<ExistentialNetworkAuthorizationRecord<RuleForm>>) getNetworkAuthClass();
        CriteriaQuery<ExistentialNetworkAuthorizationRecord<RuleForm>> query = cb.createQuery(clazz);
        Root<ExistentialNetworkAuthorizationRecord<RuleForm>> networkRoot = query.from(clazz);
        Root<WorkspaceAuthorization> workspaces = query.from(WorkspaceAuthorization.class);
        query.select(networkRoot)
             .where(cb.and(cb.and(cb.equal(workspaces.get(WorkspaceAuthorization_.definingProduct),
                                           workspace),
                                  cb.equal(networkRoot.get(Ruleform_.workspace),
                                           workspaces)),
                           cb.and(cb.isNotNull(networkRoot.get("classifier")),
                                  cb.isNotNull(networkRoot.get("classification")),
                                  cb.isNull(networkRoot.get("childRelationship")),
                                  cb.isNull(networkRoot.get("authorizedRelationship")),
                                  cb.isNull(networkRoot.get("groupingAgency")))));
        TypedQuery<ExistentialNetworkAuthorizationRecord<RuleForm>> q = em.createQuery(query);
        return q.getResultList();
    }

    @Override
    public RuleForm getImmediateChild(RuleForm parent,
                                      Relationship relationship) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RuleForm> query = cb.createQuery(entity);
        Root<ExistentialNetworkRecord> networkRoot = query.from(network);
        Path<RuleForm> path;
        try {
            path = networkRoot.get("child");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        query.select(path)
             .where(cb.and(cb.equal(networkRoot.get("parent"), parent),
                           cb.equal(networkRoot.get("relationship"),
                                    relationship),
                           cb.isNull(networkRoot.get("inference"))));
        TypedQuery<RuleForm> q = em.createQuery(query);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public ExistentialNetworkRecord getImmediateChildLink(RuleForm parent,
                                                          Relationship relationship,
                                                          RuleForm child) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ExistentialNetworkRecord> query = cb.createQuery(network);
        Root<ExistentialNetworkRecord> networkRoot = query.from(network);
        query.select(networkRoot)
             .where(cb.and(cb.equal(networkRoot.get("parent"), parent),
                           cb.equal(networkRoot.get("relationship"),
                                    relationship),
                           cb.equal(networkRoot.get("child"), child),
                           cb.isNull(networkRoot.get("inference"))));
        TypedQuery<ExistentialNetworkRecord> q = em.createQuery(query);
        try {
            return (ExistentialNetworkRecord) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<RuleForm> getImmediateChildren(RuleForm parent,
                                               Relationship relationship) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RuleForm> query = cb.createQuery(entity);
        Root<ExistentialNetworkRecord> networkRoot = query.from(network);
        Path<RuleForm> path;
        try {
            path = networkRoot.get("child");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        query.select(path)
             .where(cb.and(cb.equal(networkRoot.get("parent"), parent),
                           cb.equal(networkRoot.get("relationship"),
                                    relationship),
                           cb.isNull(networkRoot.get("inference"))));
        TypedQuery<RuleForm> q = em.createQuery(query);
        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ExistentialNetworkRecord> getImmediateChildrenLinks(RuleForm parent,
                                                                    Relationship relationship) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ExistentialNetworkRecord> query = cb.createQuery(network);
        Root<ExistentialNetworkRecord> networkRoot = query.from(network);
        query.select(networkRoot)
             .where(cb.and(cb.equal(networkRoot.get("parent"), parent),
                           cb.equal(networkRoot.get("relationship"),
                                    relationship),
                           cb.isNull(networkRoot.get("inference"))));
        TypedQuery<ExistentialNetworkRecord> q = em.createQuery(query);
        return (List<ExistentialNetworkRecord>) q.getResultList();
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
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ExistentialNetworkRecord> query = cb.createQuery(network);
        Root<ExistentialNetworkRecord> networkRoot = query.from(network);
        query.select(networkRoot)
             .where(cb.and(cb.equal(networkRoot.get("parent"), parent),
                           cb.equal(networkRoot.get("relationship"),
                                    relationship),
                           cb.equal(networkRoot.get("child"), child),
                           cb.isNull(networkRoot.get("inference"))));
        TypedQuery<ExistentialNetworkRecord> q = em.createQuery(query);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Collection<ExistentialNetworkRecord> getImmediateNetworkEdges(RuleForm parent) {
        List<ExistentialNetworkRecord> edges = new ArrayList<ExistentialNetworkRecord>();
        for (ExistentialNetworkRecord edge : parent.getNetworkByParent()) {
            if (!edge.isInferred()) {
                edges.add(edge);
            }
        }
        return edges;
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
        Set<Relationship> relationships = new HashSet<Relationship>();
        Set<Relationship> inverses = new HashSet<Relationship>();
        for (ExistentialNetworkRecord network : parent.getNetworkByParent()) {
            if (!network.isInferred()) {
                Relationship relationship = network.getRelationship();
                if (!inverses.contains(relationship)) {
                    relationships.add(relationship);
                    inverses.add(relationship.getInverse());
                }
            }
        }
        return relationships;
    }

    @Override
    public List<RuleForm> getInferredChildren(RuleForm parent,
                                              Relationship relationship) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RuleForm> query = cb.createQuery(entity);
        Root<RuleForm> networkRoot = query.from(entity);
        query.select(networkRoot)
             .where(cb.and(cb.equal(networkRoot.get("parent"), parent),
                           cb.equal(networkRoot.get("relationship"),
                                    relationship),
                           cb.isNotNull(networkRoot.get("inference"))));
        TypedQuery<RuleForm> q = em.createQuery(query);
        return q.getResultList();
    }

    @Override
    public List<RuleForm> getInGroup(RuleForm parent,
                                     Relationship relationship) {
        /*
         * select n.child from <networkTable> n where n.parent = :parent and
         * n.relationship = :relationship and n.child <> :parent
         */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RuleForm> query = cb.createQuery(entity);
        Root<ExistentialNetworkRecord> networkForm = query.from(network);
        query.select(networkForm.get("child"));
        query.where(cb.equal(networkForm.get("relationship"), relationship),
                    cb.notEqual(networkForm.get("child"), parent));
        return em.createQuery(query)
                 .getResultList();
    }

    @Override
    public List<ExistentialNetworkAuthorizationRecord> getNetworkAuthorizations(Aspect<RuleForm> aspect,
                                                                                boolean includeGrouping) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        Class<ExistentialNetworkAuthorizationRecord<RuleForm>> clazz = (Class<ExistentialNetworkAuthorizationRecord<RuleForm>>) getNetworkAuthClass();
        CriteriaQuery<ExistentialNetworkAuthorizationRecord<RuleForm>> query = cb.createQuery(clazz);
        Root<ExistentialNetworkAuthorizationRecord<RuleForm>> networkRoot = query.from(clazz);
        Predicate match = cb.and(cb.equal(networkRoot.get("classification"),
                                          aspect.getClassification()),
                                 cb.equal(networkRoot.get("classifier"),
                                          aspect.getClassifier()),
                                 cb.isNotNull(networkRoot.get("childRelationship")),
                                 cb.isNotNull(networkRoot.get("authorizedRelationship")),
                                 cb.isNotNull(networkRoot.get("authorizedParent")));
        if (!includeGrouping) {
            match = cb.and(cb.isNull(networkRoot.get("groupingAgency")), match);
        }
        query.select(networkRoot)
             .where(match);
        TypedQuery<ExistentialNetworkAuthorizationRecord<RuleForm>> q = em.createQuery(query);
        return q.getResultList();
    }

    @Override
    public List<RuleForm> getNotInGroup(RuleForm parent,
                                        Relationship relationship) {
        TypedQuery<RuleForm> query = em.createQuery(String.format("SELECT DISTINCT e from %s AS e WHERE NOT EXISTS("
                                                                  + "SELECT n from %s AS n  WHERE n.parent = :parent "
                                                                  + " AND n.relationship = :relationship "
                                                                  + " AND n.child = e)",
                                                                  entity.getSimpleName(),
                                                                  network.getSimpleName()),
                                                    entity);
        query.setParameter("parent", parent);
        query.setParameter("relationship", relationship);
        return query.getResultList();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#getChild(com.chiralbehaviors.CoRE.
     * ExistentialRuleform, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public RuleForm getSingleChild(RuleForm parent, Relationship r) {
        TypedQuery<RuleForm> query = em.createNamedQuery(prefix
                                                         + GET_CHILDREN_SUFFIX,
                                                         entity);
        query.setParameter("p", parent);
        query.setParameter("r", r);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("%s has no child for relationship %s",
                                        parent, r));
            }
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#getTransitiveRelationships(com
     * .hellblazer.CoRE.ExistentialRuleform)
     */
    @Override
    public Collection<Relationship> getTransitiveRelationships(RuleForm parent) {
        Set<Relationship> relationships = new HashSet<Relationship>();
        Set<Relationship> inverses = new HashSet<Relationship>();
        Set<RuleForm> visited = new HashSet<RuleForm>();
        visited.add(parent);
        for (ExistentialNetworkRecord network : parent.getNetworkByParent()) {
            addTransitiveRelationships(network, inverses, visited,
                                       relationships);
        }
        return relationships;
    }

    @Override
    public List<Relationship> getUsedRelationships() {
        return em.createNamedQuery(prefix + USED_RELATIONSHIPS_SUFFIX,
                                   Relationship.class)
                 .getResultList();
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
        if (getImmediateChildren(ruleform, aspect.getClassifier()).isEmpty()) {
            Tuple<ExistentialNetworkRecord, ExistentialNetworkRecord> links = ruleform.link(aspect.getClassifier(),
                                                                                            aspect.getClassification(),
                                                                                            principal,
                                                                                            principal,
                                                                                            em);
            if (workspace != null) {
                workspace.add(links.a);
                workspace.add(links.b);
            }
        }
        for (ExistentialAttributeAuthorizationRecord authorization : getAttributeAuthorizations(aspect,
                                                                                                false)) {
            Attribute authorizedAttribute = authorization.getAuthorizedAttribute();
            if (!authorizedAttribute.getKeyed()
                && !authorizedAttribute.getIndexed()) {
                if (getAttributeValue(ruleform, authorizedAttribute) == null) {
                    AttributeType attribute = create(ruleform,
                                                     authorizedAttribute,
                                                     principal);
                    attribute.setValue(authorization.getValue());
                    em.persist(attribute);
                    if (workspace != null) {
                        workspace.add(attribute);
                    }
                }
            }
        }
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
        Query query = em.createNamedQuery(String.format("%s%s", networkPrefix,
                                                        ExistentialRuleform.GET_NETWORKS_SUFFIX));
        query.setParameter("parent", parent);
        query.setParameter("relationship", relationship);
        query.setParameter("child", child);
        List<?> results = query.getResultList();

        return results.size() > 0;
    }

    @Override
    public Tuple<ExistentialNetworkRecord, ExistentialNetworkRecord> link(RuleForm parent,
                                                                          Relationship r,
                                                                          RuleForm child,
                                                                          Agency updatedBy) {
        return parent.link(r, child, updatedBy, model.getCurrentPrincipal()
                                                     .getPrincipal(),
                           em);
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
        Attribute attribute = value.getAttribute();
        Attribute validatingAttribute = model.getAttributeModel()
                                             .getSingleChild(attribute,
                                                             model.getKernel()
                                                                  .getIsValidatedBy());
        if (validatingAttribute != null) {
            TypedQuery<AttributeMetaAttribute> query = em.createNamedQuery(AttributeMetaAttribute.GET_ATTRIBUTE,
                                                                           AttributeMetaAttribute.class);
            query.setParameter("ruleform", validatingAttribute);
            query.setParameter("attribute", attribute);
            List<AttributeMetaAttribute> attrs = query.getResultList();
            if (attrs == null || attrs.size() == 0) {
                throw new IllegalArgumentException("No valid values for attribute "
                                                   + attribute.getName());
            }
            boolean valid = false;
            for (AttributeMetaAttribute ama : attrs) {
                if (ama.getValue() != null && ama.getValue()
                                                 .equals(value.getValue())) {
                    valid = true;
                    em.persist(value);
                }
            }
            if (!valid) {
                throw new IllegalArgumentException(String.format("%s is not a valid value for attribute %s",
                                                                 value.getValue(),
                                                                 attribute));
            }
        }

    }

    @Override
    public void setAuthorizedAgencies(RuleForm ruleform,
                                      Relationship relationship,
                                      List<Agency> authorized) {
        deauthorizeAgencies(ruleform, relationship,
                            getAuthorizedAgencies(ruleform, relationship));
        authorizeAgencies(ruleform, relationship, authorized);
    }

    @Override
    public void setAuthorizedLocations(RuleForm ruleform,
                                       Relationship relationship,
                                       List<Location> authorized) {
        deauthorizeLocations(ruleform, relationship,
                             getAuthorizedLocations(ruleform, relationship));
        authorizeLocations(ruleform, relationship, authorized);
    }

    @Override
    public void setAuthorizedProducts(RuleForm ruleform,
                                      Relationship relationship,
                                      List<Product> authorized) {
        deauthorizeProducts(ruleform, relationship,
                            getAuthorizedProducts(ruleform, relationship));
        authorizeProducts(ruleform, relationship, authorized);
    }

    @Override
    public void setAuthorizedRelationships(RuleForm ruleform,
                                           Relationship relationship,
                                           List<Relationship> authorized) {
        deauthorizeRelationships(ruleform, relationship,
                                 getAuthorizedRelationships(ruleform,
                                                            relationship));
        authorizeRelationships(ruleform, relationship, authorized);
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
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<ExistentialNetworkRecord> query = cb.createCriteriaDelete(network);
        Root<ExistentialNetworkRecord> networkRoot = query.from(network);
        query.where(cb.or(cb.and(cb.equal(networkRoot.get("parent"), parent),
                                 cb.equal(networkRoot.get("relationship"),
                                          relationship),
                                 cb.equal(networkRoot.get("child"), child),
                                 cb.isNull(networkRoot.get("inference"))),
                          cb.and(cb.equal(networkRoot.get("parent"), child),
                                 cb.equal(networkRoot.get("relationship"),
                                          relationship.getInverse()),
                                 cb.equal(networkRoot.get("child"), parent),
                                 cb.isNull(networkRoot.get("inference")))));
        em.createQuery(query)
          .executeUpdate();
        model.inferNetworks(parent);
    }

    @Override
    public void unlinkImmediate(RuleForm parent, Relationship relationship) {
        ExistentialNetworkRecord link = getImmediateLink(parent, relationship);
        em.remove(link);
        em.remove(getImmediateChildLink(link.getChild(),
                                        relationship.getInverse(), parent));
        model.inferNetworks(parent);
    }

    private void addTransitiveRelationships(ExistentialNetworkRecord edge,
                                            Set<Relationship> inverses,
                                            Set<RuleForm> visited,
                                            Set<Relationship> relationships) {
        Relationship relationship = edge.getRelationship();
        if (inverses.contains(relationship)) {
            return;
        }
        if (!relationships.add(relationship)) {
            return;
        }
        inverses.add(relationship.getInverse());
        RuleForm child = edge.getChild();
        for (ExistentialNetworkRecord network : child.getNetworkByParent()) {
            RuleForm traversing = network.getChild();
            if (visited.add(traversing)) {
                addTransitiveRelationships(network, inverses, visited,
                                           relationships);
            }
        }
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

    protected ExistentialNetworkRecord getImmediateLink(RuleForm parent,
                                                        Relationship relationship) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ExistentialNetworkRecord> query = cb.createQuery(network);
        Root<ExistentialNetworkRecord> networkRoot = query.from(network);
        query.select(networkRoot)
             .where(cb.and(cb.equal(networkRoot.get("parent"), parent),
                           cb.equal(networkRoot.get("relationship"),
                                    relationship),
                           cb.isNull(networkRoot.get("inference"))));
        TypedQuery<ExistentialNetworkRecord> q = em.createQuery(query);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    abstract protected Class<?> getNetworkAuthClass();
}
