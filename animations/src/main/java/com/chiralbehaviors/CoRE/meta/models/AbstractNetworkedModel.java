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

package com.chiralbehaviors.CoRE.meta.models;

import static com.chiralbehaviors.CoRE.ExistentialRuleform.DEDUCE_NEW_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.FIND_CLASSIFIED_ATTRIBUTE_VALUES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_FOR_ATTRIBUTE_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.FIND_GROUPED_ATTRIBUTE_VALUES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GENERATE_NETWORK_INVERSES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GET_CHILDREN_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INFERENCE_STEP_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INSERT_NEW_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.USED_RELATIONSHIPS_SUFFIX;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.AttributeMetaAttribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.NetworkAttribute;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.relationship.Relationship;

/**
 * @author hhildebrand
 *
 */
abstract public class AbstractNetworkedModel<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>, AttributeAuth extends AttributeAuthorization<RuleForm, Network>, AttributeType extends AttributeValue<RuleForm>>
        implements
        NetworkedModel<RuleForm, Network, AttributeAuth, AttributeType> {

    private static Logger                          log            = LoggerFactory.getLogger(AbstractNetworkedModel.class);

    private static int                             MAX_DEDUCTIONS = 1000;

    private final Class<AttributeType>             attribute;
    private final String                           attributePrefix;
    private final Class<AttributeAuth>             authorization;
    private final Class<RuleForm>                  entity;
    private final Model                            model;
    private final Class<NetworkRuleform<RuleForm>> network;
    private final String                           networkPrefix;
    private final String                           prefix;
    protected final EntityManager                  em;
    protected final Kernel                         kernel;

    @SuppressWarnings("unchecked")
    public AbstractNetworkedModel(Model model) {
        this.model = model;
        this.em = model.getEntityManager();
        this.kernel = model.getKernel();
        entity = extractedEntity();
        authorization = extractedAuthorization();
        attribute = extractedAttribute();
        network = (Class<NetworkRuleform<RuleForm>>) extractedNetwork();
        prefix = ModelImpl.prefixFor(entity);
        networkPrefix = ModelImpl.prefixFor(network);
        attributePrefix = ModelImpl.prefixFor(attribute);
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
        return em.createQuery(cq).getResultList();
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
     * .hellblazer.CoRE.agency.Agency)
     */
    @Override
    public List<AttributeAuth> getAttributeAuthorizations(Agency groupingAgency) {
        TypedQuery<AttributeAuth> query = em.createNamedQuery(prefix
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
    public List<AttributeAuth> getAttributeAuthorizations(Agency groupingAgency,
                                                          Attribute attribute) {
        TypedQuery<AttributeAuth> query = em.createNamedQuery(prefix
                                                                      + FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_FOR_ATTRIBUTE_SUFFIX,
                                                              authorization);
        query.setParameter("groupingAgency", groupingAgency);
        query.setParameter("attribute", attribute);
        return query.getResultList();
    }

    @Override
    public List<AttributeAuth> getAttributeAuthorizations(Aspect<RuleForm> aspect) {
        TypedQuery<AttributeAuth> query = em.createNamedQuery(prefix
                                                                      + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX,
                                                              authorization);
        query.setParameter("classification", aspect.getClassification());
        query.setParameter("classifier", aspect.getClassifier());
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
    public List<AttributeAuth> getAttributeAuthorizations(Aspect<RuleForm> aspect,
                                                          Attribute attribute) {
        TypedQuery<AttributeAuth> query = em.createNamedQuery(prefix
                                                                      + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE_SUFFIX,
                                                              authorization);
        query.setParameter("classification", aspect.getClassification());
        query.setParameter("classifier", aspect.getClassifier());
        query.setParameter("attribute", attribute);
        return query.getResultList();
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
    public NetworkAttribute<?> getAttributeValue(Network edge,
                                                 Attribute attribute) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        @SuppressWarnings("unchecked")
        CriteriaQuery<NetworkAttribute<?>> query = (CriteriaQuery<NetworkAttribute<?>>) cb.createQuery(edge.getAttributeClass());
        @SuppressWarnings("unchecked")
        Root<NetworkAttribute<?>> attributeRoot = (Root<NetworkAttribute<?>>) query.from(edge.getAttributeClass());
        query.select(attributeRoot).where(cb.and(cb.equal(attributeRoot.get("attribute"),
                                                          attribute),
                                                 cb.equal(attributeRoot.get("network"),
                                                          edge)));
        TypedQuery<NetworkAttribute<?>> q = em.createQuery(query);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public AttributeValue<RuleForm> getAttributeValue(RuleForm ruleform,
                                                      Attribute attribute) {
        List<AttributeType> values = getAttributeValues(ruleform, attribute);
        if (values.size() > 1) {
            throw new IllegalStateException(
                                            String.format("%s has multiple values for %s",
                                                          attribute, ruleform));
        }
        if (values.size() == 0) {
            return null;
        }
        return values.get(0);
    }

    @Override
    public NetworkAttribute<?> getAttributeValue(RuleForm parent,
                                                 Relationship r,
                                                 RuleForm child,
                                                 Attribute attribute) {
        Network edge = getImmediateChildLink(parent, r, child);
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

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getChild(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public RuleForm getChild(RuleForm parent, Relationship relationship) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RuleForm> query = cb.createQuery(entity);
        Root<NetworkRuleform<RuleForm>> networkRoot = query.from(network);
        Path<RuleForm> path;
        try {
            path = networkRoot.get("child");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        query.select(path).where(cb.and(cb.equal(networkRoot.get("parent"),
                                                 parent),
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
    public List<RuleForm> getChildren(RuleForm parent, Relationship relationship) {
        String prefix = parent.getClass().getSimpleName().toLowerCase()
                        + "Network";
        @SuppressWarnings("unchecked")
        TypedQuery<RuleForm> q = (TypedQuery<RuleForm>) em.createNamedQuery(prefix
                                                                                    + ExistentialRuleform.GET_CHILDREN_SUFFIX,
                                                                            parent.getClass());
        q.setParameter("parent", parent);
        q.setParameter("relationship", relationship);
        List<RuleForm> resultList = q.getResultList();
        return resultList;
    }

    @Override
    public RuleForm getImmediateChild(RuleForm parent, Relationship relationship) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RuleForm> query = cb.createQuery(entity);
        Root<NetworkRuleform<RuleForm>> networkRoot = query.from(network);
        Path<RuleForm> path;
        try {
            path = networkRoot.get("child");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        query.select(path).where(cb.and(cb.equal(networkRoot.get("parent"),
                                                 parent),
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
    public Network getImmediateChildLink(RuleForm parent,
                                         Relationship relationship,
                                         RuleForm child) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<NetworkRuleform<RuleForm>> query = cb.createQuery(network);
        Root<NetworkRuleform<RuleForm>> networkRoot = query.from(network);
        query.select(networkRoot).where(cb.and(cb.equal(networkRoot.get("parent"),
                                                        parent),
                                               cb.equal(networkRoot.get("relationship"),
                                                        relationship),
                                               cb.equal(networkRoot.get("child"),
                                                        child),
                                               cb.isNull(networkRoot.get("inference"))));
        TypedQuery<NetworkRuleform<RuleForm>> q = em.createQuery(query);
        try {
            return (Network) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<RuleForm> getImmediateChildren(RuleForm parent,
                                               Relationship relationship) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RuleForm> query = cb.createQuery(entity);
        Root<NetworkRuleform<RuleForm>> networkRoot = query.from(network);
        Path<RuleForm> path;
        try {
            path = networkRoot.get("child");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        query.select(path).where(cb.and(cb.equal(networkRoot.get("parent"),
                                                 parent),
                                        cb.equal(networkRoot.get("relationship"),
                                                 relationship),
                                        cb.isNull(networkRoot.get("inference"))));
        TypedQuery<RuleForm> q = em.createQuery(query);
        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Network> getImmediateChildrenLinks(RuleForm parent,
                                                   Relationship relationship) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<NetworkRuleform<RuleForm>> query = cb.createQuery(network);
        Root<NetworkRuleform<RuleForm>> networkRoot = query.from(network);
        query.select(networkRoot).where(cb.and(cb.equal(networkRoot.get("parent"),
                                                        parent),
                                               cb.equal(networkRoot.get("relationship"),
                                                        relationship),
                                               cb.isNull(networkRoot.get("inference"))));
        TypedQuery<NetworkRuleform<RuleForm>> q = em.createQuery(query);
        return (List<Network>) q.getResultList();
    }

    @Override
    public Collection<Network> getImmediateNetworkEdges(RuleForm parent) {
        List<Network> edges = new ArrayList<Network>();
        for (Network edge : parent.getNetworkByParent()) {
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
        for (Network network : parent.getNetworkByParent()) {
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
    public List<RuleForm> getInGroup(RuleForm parent, Relationship relationship) {
        /*
         * select n.child from <networkTable> n where n.parent = :parent and
         * n.relationship = :relationship and n.child <> :parent
         */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RuleForm> query = cb.createQuery(entity);
        Root<NetworkRuleform<RuleForm>> networkForm = query.from(network);
        query.select(networkForm.get("child"));
        query.where(cb.equal(networkForm.get("relationship"), relationship),
                    cb.notEqual(networkForm.get("child"), parent));
        return em.createQuery(query).getResultList();
    }

    @Override
    public List<RuleForm> getNotInGroup(RuleForm parent,
                                        Relationship relationship) {
        /*
         * SELECT e FROM product AS e, ProductNetwork AS n WHERE n.parent <>
         * :parent AND n.relationship = :relationship AND n.child <> e;
         */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RuleForm> query = cb.createQuery(entity);
        Root<RuleForm> form = query.from(entity);
        Root<NetworkRuleform<RuleForm>> networkForm = query.from(network);
        query.where(cb.equal(networkForm.get("parent"), parent),
                    cb.equal(networkForm.get("relationship"), relationship),
                    cb.notEqual(networkForm.get("child"), form));
        query.select(form);
        return em.createQuery(query).getResultList();
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
        for (Network network : parent.getNetworkByParent()) {
            addTransitiveRelationships(network, inverses, visited,
                                       relationships);
        }
        return relationships;
    }

    @Override
    public List<Relationship> getUsedRelationships() {
        return em.createNamedQuery(prefix + USED_RELATIONSHIPS_SUFFIX,
                                   Relationship.class).getResultList();
    }

    @Override
    public final List<AttributeType> initialize(RuleForm ruleform,
                                                Aspect<RuleForm> aspect,
                                                Agency updatedBy) {
        List<AttributeType> attributes = new ArrayList<>();
        ruleform.link(aspect.getClassification(), aspect.getClassifier(),
                      kernel.getCoreModel(), kernel.getCoreAnimationSoftware(),
                      em);
        for (AttributeAuth authorization : getAttributeAuthorizations(aspect)) {
            if (!authorization.getAuthorizedAttribute().getKeyed()
                && !authorization.getAuthorizedAttribute().getIndexed()) {
                AttributeType attribute = create(ruleform,
                                                 authorization.getAuthorizedAttribute(),
                                                 updatedBy);
                attributes.add(attribute);
                attribute.setValue(authorization.getValue());
                em.persist(attribute);
            }
        }
        return attributes;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#isAccessible(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.network.Relationship, com.chiralbehaviors.CoRE.network.Relationship, com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public boolean isAccessible(RuleForm parent, Relationship relationship,
                                RuleForm child) {
        Query query = em.createNamedQuery(String.format("%s%s",
                                                        networkPrefix,
                                                        ExistentialRuleform.GET_NETWORKS_SUFFIX));
        query.setParameter("parent", parent);
        query.setParameter("relationship", relationship);
        query.setParameter("child", child);
        List<?> results = query.getResultList();

        return results.size() > 0;
    }

    @Override
    public Network link(RuleForm parent, Relationship r, RuleForm child,
                        Agency updatedBy) {
        return parent.link(r, child, updatedBy, kernel.getInverseSoftware(), em);
    }

    @Override
    public void propagate() {
        createDeductionTemporaryTables();
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
    public void setAttributeValue(AttributeType value) {
        Attribute attribute = value.getAttribute();
        Attribute validatingAttribute = model.getAttributeModel().getSingleChild(attribute,
                                                                                 model.getKernel().getIsValidatedBy());
        if (validatingAttribute != null) {
            TypedQuery<AttributeMetaAttribute> query = em.createNamedQuery(AttributeMetaAttribute.GET_ATTRIBUTE,
                                                                           AttributeMetaAttribute.class);
            query.setParameter("ruleform", validatingAttribute);
            query.setParameter("attribute", attribute);
            List<AttributeMetaAttribute> attrs = query.getResultList();
            if (attrs == null || attrs.size() == 0) {
                throw new IllegalArgumentException(
                                                   "No valid values for attribute "
                                                           + attribute.getName());
            }
            boolean valid = false;
            for (AttributeMetaAttribute ama : attrs) {
                if (ama.getTextValue() != null
                    && ama.getTextValue().equals(value.getTextValue())) {
                    valid = true;
                    em.persist(value);
                }
            }
            if (!valid) {
                throw new IllegalArgumentException(
                                                   String.format("%s is not a valid value for attribute %s",
                                                                 value.getTextValue(),
                                                                 attribute));
            }
        }

    }

    @Override
    public void setImmediateChild(RuleForm parent, Relationship relationship,
                                  RuleForm child, Agency updatedBy) {
        NetworkRuleform<RuleForm> link = getImmediateLink(parent, relationship);
        if (link != null) {
            model.getEntityManager().remove(link);
        }
        link(parent, relationship, child, updatedBy);

    }

    @Override
    public void unlink(RuleForm parent, Relationship relationship,
                       RuleForm child) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<NetworkRuleform<RuleForm>> query = cb.createCriteriaDelete(network);
        Root<NetworkRuleform<RuleForm>> networkRoot = query.from(network);
        query.where(cb.and(cb.equal(networkRoot.get("parent"), parent),
                           cb.equal(networkRoot.get("relationship"),
                                    relationship),
                           cb.equal(networkRoot.get("child"), child),
                           cb.isNull(networkRoot.get("inference"))));
        em.createQuery(query).executeUpdate();
        model.inferNetworks(parent);
    }

    private void addTransitiveRelationships(Network edge,
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
        for (Network network : child.getNetworkByParent()) {
            RuleForm traversing = network.getChild();
            if (visited.add(traversing)) {
                addTransitiveRelationships(network, inverses, visited,
                                           relationships);
            }
        }
    }

    private void alterDeductionTablesForNextPass() {
        em.createNativeQuery("TRUNCATE TABLE last_pass_rules").executeUpdate();
        em.createNativeQuery("ALTER TABLE current_pass_rules RENAME TO temp_last_pass_rules").executeUpdate();
        em.createNativeQuery("ALTER TABLE last_pass_rules RENAME TO current_pass_rules").executeUpdate();
        em.createNativeQuery("ALTER TABLE temp_last_pass_rules RENAME TO last_pass_rules").executeUpdate();
        em.createNativeQuery("TRUNCATE working_memory").executeUpdate();
    }

    private void createCurrentPassRules() {
        em.createNativeQuery("CREATE TEMPORARY TABLE current_pass_rules ("
                                     + "id uuid NOT NULL,"
                                     + "parent uuid NOT NULL,"
                                     + "relationship uuid NOT NULL,"
                                     + "child uuid NOT NULL,"
                                     + "premise1 uuid NOT NULL,"
                                     + "premise2 uuid NOT NULL,"
                                     + "inference uuid NOT NULL )").executeUpdate();
    }

    private void createDeductionTemporaryTables() {
        em.createNativeQuery("DROP TABLE IF EXISTS last_pass_rules").executeUpdate();
        em.createNativeQuery("DROP TABLE IF EXISTS current_pass_rules").executeUpdate();
        em.createNativeQuery("DROP TABLE IF EXISTS working_memory").executeUpdate();
        createWorkingMemory();
        createCurrentPassRules();
        createLastPassRules();
    }

    private void createLastPassRules() {
        em.createNativeQuery("CREATE TEMPORARY TABLE last_pass_rules ("
                                     + "id uuid NOT NULL,"
                                     + "parent uuid NOT NULL,"
                                     + "relationship uuid NOT NULL,"
                                     + "child uuid NOT NULL,"
                                     + "premise1 uuid NOT NULL,"
                                     + "premise2 uuid NOT NULL,"
                                     + "inference uuid NOT NULL )").executeUpdate();
    }

    private void createWorkingMemory() {
        em.createNativeQuery("CREATE TEMPORARY TABLE working_memory("
                                     + "parent uuid NOT NULL,"
                                     + "relationship uuid NOT NULL,"
                                     + "child uuid NOT NULL,"
                                     + "premise1 uuid NOT NULL,"
                                     + "premise2 uuid NOT NULL,"
                                     + "inference uuid NOT NULL )").executeUpdate();
    }

    // Deduce the new rules
    private void deduce() {
        int deductions = em.createNamedQuery(networkPrefix
                                                     + DEDUCE_NEW_NETWORK_RULES_SUFFIX).executeUpdate();
        if (log.isTraceEnabled()) {
            log.trace(String.format("deduced %s rules", deductions));

        }
    }

    @SuppressWarnings("unchecked")
    private Class<AttributeType> extractedAttribute() {
        return (Class<AttributeType>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[3];
    }

    @SuppressWarnings("unchecked")
    private Class<AttributeAuth> extractedAuthorization() {
        return (Class<AttributeAuth>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[2];
    }

    @SuppressWarnings("unchecked")
    private Class<RuleForm> extractedEntity() {
        return (Class<RuleForm>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @SuppressWarnings("unchecked")
    private Class<Network> extractedNetwork() {
        return (Class<Network>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    private void generateInverses() {
        long then = System.currentTimeMillis();
        int inverses = em.createNamedQuery(String.format("%s%s", networkPrefix,
                                                         GENERATE_NETWORK_INVERSES_SUFFIX)).executeUpdate();
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
                                                   + INFERENCE_STEP_SUFFIX).executeUpdate();
            firstPass = false;
        } else {
            newRules = em.createNamedQuery(networkPrefix
                                                   + INFERENCE_STEP_FROM_LAST_PASS_SUFFIX).executeUpdate();
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
            throw new IllegalStateException(
                                            String.format("Inserted more than %s deductions: %s, possible runaway inference",
                                                          MAX_DEDUCTIONS,
                                                          inserted));
        }
        return inserted;
    }

    /**
     * @param attribute
     * @param authorizations
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <ValueType> List<ValueType> getAllowedValues(Attribute attribute,
                                                           List<AttributeAuth> authorizations) {
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
        for (AttributeAuth authorization : authorizations) {
            switch (attribute.getValueType()) {
                case BOOLEAN: {
                    allowedValues.add((ValueType) authorization.getBooleanValue());
                    break;
                }
                case INTEGER: {
                    allowedValues.add((ValueType) authorization.getIntegerValue());
                    break;
                }
                case NUMERIC: {
                    allowedValues.add((ValueType) authorization.getNumericValue());
                    break;
                }
                case TEXT: {
                    allowedValues.add((ValueType) authorization.getTextValue());
                    break;
                }
                case TIMESTAMP: {
                    allowedValues.add((ValueType) authorization.getTimestampValue());
                    break;
                }
                case BINARY: {
                    allowedValues.add((ValueType) authorization.getBinaryValue());
                    break;
                }
            }
        }
        return allowedValues;
    }

    /**
     * @param parent
     * @param relationship
     * @return
     */
    protected NetworkRuleform<RuleForm> getImmediateLink(RuleForm parent,
                                                         Relationship relationship) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<NetworkRuleform<RuleForm>> query = cb.createQuery(network);
        Root<NetworkRuleform<RuleForm>> networkRoot = query.from(network);
        query.select(networkRoot).where(cb.and(cb.equal(networkRoot.get("parent"),
                                                        parent),
                                               cb.equal(networkRoot.get("relationship"),
                                                        relationship),
                                               cb.isNull(networkRoot.get("inference"))));
        TypedQuery<NetworkRuleform<RuleForm>> q = em.createQuery(query);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
