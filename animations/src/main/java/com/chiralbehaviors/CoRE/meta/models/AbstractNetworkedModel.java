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
import static com.chiralbehaviors.CoRE.ExistentialRuleform.UNLINKED_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.USED_RELATIONSHIPS_SUFFIX;

import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.sql.Timestamp;
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
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.attribute.ClassifiedAttributeAuthorization;
import com.chiralbehaviors.CoRE.authorization.AccessAuthorization;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Facet;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * @author hhildebrand
 *
 */
abstract public class AbstractNetworkedModel<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>, AttributeAuthorization extends ClassifiedAttributeAuthorization<RuleForm>, AttributeType extends AttributeValue<RuleForm>>
        implements
        NetworkedModel<RuleForm, Network, AttributeAuthorization, AttributeType> {

    /**
     * @param attr
     */
    public static void defaultValue(AttributeValue<?> attr) {
        switch (attr.getAttribute().getValueType()) {
            case BINARY: {
                attr.setBinaryValue(new byte[0]);
                break;
            }
            case BOOLEAN: {
                attr.setBooleanValue(false);
                break;
            }
            case INTEGER: {
                attr.setIntegerValue(-1);
                break;
            }
            case NUMERIC: {
                attr.setNumericValue(new BigDecimal(-1));
                break;
            }
            case TEXT: {
                attr.setTextValue("");
                break;
            }
            case TIMESTAMP: {
                attr.setTimestampValue(new Timestamp(0));
                break;
            }
        }
    }

    private static Logger                          log = LoggerFactory.getLogger(AbstractNetworkedModel.class);

    private final Class<AttributeType>             attribute;

    private final Class<AttributeAuthorization>    authorization;
    private final Class<RuleForm>                  entity;
    private final Class<NetworkRuleform<RuleForm>> network;
    private final String                           networkPrefix;
    private final String                           prefix;
    protected final EntityManager                  em;
    protected final Kernel                         kernel;

    @SuppressWarnings("unchecked")
    public AbstractNetworkedModel(EntityManager em, Kernel kernel) {
        this.em = em;
        this.kernel = kernel;
        entity = extractedEntity();
        authorization = extractedAuthorization();
        attribute = extractedAttribute();
        network = (Class<NetworkRuleform<RuleForm>>) extractedNetwork();
        prefix = ModelImpl.prefixFor(entity);
        networkPrefix = ModelImpl.prefixFor(network);
    }

    public void createInverseRelationship(RuleForm parent, Relationship r,
                                          RuleForm child, Agency updatedBy) {
        child.link(r.getInverse(), parent, updatedBy,
                   kernel.getInverseSoftware(), em);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#find(long)
     */
    @Override
    public RuleForm find(UUID id) {
        RuleForm rf = em.find(entity, id.toString());
        return rf;
    }

    @Override
    public List<RuleForm> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RuleForm> cq = cb.createQuery(entity);
        cq.from(entity);
        return em.createQuery(cq).getResultList();
    }

    public void generateInverses() {
        Query query = em.createNamedQuery(String.format("%s%s", networkPrefix,
                                                        GENERATE_NETWORK_INVERSES_SUFFIX));
        query.setParameter(1, kernel.getInverseSoftware().getPrimaryKey());
        long then = System.currentTimeMillis();
        int created = query.executeUpdate();
        if (log.isTraceEnabled()) {
            log.trace(String.format("created %s inverse rules of %s in %s ms",
                                    created, networkPrefix,
                                    System.currentTimeMillis() - then));
        }
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
    public List<AttributeAuthorization> getAttributeAuthorizations(Agency groupingAgency) {
        TypedQuery<AttributeAuthorization> query = em.createNamedQuery(prefix
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
    public List<AttributeAuthorization> getAttributeAuthorizations(Agency groupingAgency,
                                                                   Attribute attribute) {
        TypedQuery<AttributeAuthorization> query = em.createNamedQuery(prefix
                                                                               + FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_FOR_ATTRIBUTE_SUFFIX,
                                                                       authorization);
        query.setParameter("groupingAgency", groupingAgency);
        query.setParameter("attribute", attribute);
        return query.getResultList();
    }

    @Override
    public List<AttributeAuthorization> getAttributeAuthorizations(Aspect<RuleForm> aspect) {
        TypedQuery<AttributeAuthorization> query = em.createNamedQuery(prefix
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
    public List<AttributeAuthorization> getAttributeAuthorizations(Aspect<RuleForm> aspect,
                                                                   Attribute attribute) {
        TypedQuery<AttributeAuthorization> query = em.createNamedQuery(prefix
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

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#getFacet(com.chiralbehaviors.CoRE.
     * ExistentialRuleform, com.chiralbehaviors.CoRE.meta.Aspect)
     */
    @Override
    public Facet<RuleForm, AttributeType> getFacet(RuleForm ruleform,
                                                   Aspect<RuleForm> aspect) {
        return new Facet<RuleForm, AttributeType>(
                                                  aspect,
                                                  ruleform,
                                                  getAttributesClassifiedBy(ruleform,
                                                                            aspect)) {
        };
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

    @SuppressWarnings("unchecked")
    @Override
    public List<RuleForm> getInGroup(RuleForm parent, Relationship relationship) {
        /*
         * select n.child from <networkTable> n where n.parent = :parent and
         * n.relationship = :relationship and n.child <> :parent
         */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RuleForm> query = cb.createQuery(entity);
        Root<NetworkRuleform<RuleForm>> networkForm = query.from(network);
        query.select((Selection<? extends RuleForm>) networkForm.fetch("child"));
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

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getUnlinked()
     */
    @Override
    public List<RuleForm> getUnlinked() {
        return em.createNamedQuery(prefix + UNLINKED_SUFFIX, entity).getResultList();
    }

    @Override
    public List<Relationship> getUsedRelationships() {
        return em.createNamedQuery(prefix + USED_RELATIONSHIPS_SUFFIX,
                                   Relationship.class).getResultList();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#isAccessible(com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.network.Relationship, com.chiralbehaviors.CoRE.network.Relationship, com.chiralbehaviors.CoRE.ExistentialRuleform, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public boolean isAccessible(RuleForm parent,
                                Relationship parentRelationship,
                                Relationship authorizingRelationship,
                                ExistentialRuleform<?, ?> child,
                                Relationship childRelationship) {
        String queryPrefix = constructQueryPrefix(parent, child);
        return isRuleformAccessible(parent, parentRelationship,
                                    authorizingRelationship, child,
                                    childRelationship, queryPrefix);
    }

    @Override
    public void link(RuleForm parent, Relationship r, RuleForm child,
                     Agency updatedBy) {
        parent.link(r, child, updatedBy, kernel.getInverseSoftware(), em);
    }

    @Override
    public void propagate() {
        createDeductionTemporaryTables();
        boolean firstPass = true;
        boolean derived = false;
        do {
            int newRules;
            // Deduce all possible rules
            if (firstPass) {
                newRules = em.createNamedQuery(networkPrefix
                                                       + INFERENCE_STEP_SUFFIX).executeUpdate();
                firstPass = false;
            } else {
                newRules = em.createNamedQuery(networkPrefix
                                                       + INFERENCE_STEP_FROM_LAST_PASS_SUFFIX).executeUpdate();
            }
            if (log.isInfoEnabled()) {
                log.info(String.format("inferred %s new rules", newRules));
            }
            if (newRules == 0) {
                break;
            }
            // Deduce the new rules
            int deduced = em.createNamedQuery(networkPrefix
                                                      + DEDUCE_NEW_NETWORK_RULES_SUFFIX).executeUpdate();
            if (log.isInfoEnabled()) {
                log.info(String.format("deduced %s rules", deduced));
            }
            // Insert the new rules
            Query insert = em.createNamedQuery(networkPrefix
                                               + INSERT_NEW_NETWORK_RULES_SUFFIX);
            insert.setParameter(1,
                                kernel.getPropagationSoftware().getPrimaryKey());
            int inserted = insert.executeUpdate();
            if (log.isInfoEnabled()) {
                log.info(String.format("inserted %s new rules", inserted));
            }
            derived |= inserted > 0;
            if (inserted == 0) {
                break;
            }
            alterDeductionTablesForNextPass();
        } while (true);
        if (derived) {
            generateInverses();
        }
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

    /**
     * @param parent
     * @param child
     * @return
     */
    private String constructQueryPrefix(RuleForm parent,
                                        ExistentialRuleform<?, ?> child) {
        if (parent.getClass().equals(child.getClass())) {
            return parent.getClass().getSimpleName().toLowerCase() + "Network";
        }
        return parent.getClass().getSimpleName().toLowerCase()
               + child.getClass().getSimpleName() + "AccessAuthorization";
    }

    private void createCurrentPassRules() {
        em.createNativeQuery("CREATE TEMPORARY TABLE current_pass_rules ("
                                     + "id CHAR(22) NOT NULL,"
                                     + "parent CHAR(22) NOT NULL,"
                                     + "relationship CHAR(22) NOT NULL,"
                                     + "child CHAR(22) NOT NULL,"
                                     + "premise1 CHAR(22) NOT NULL,"
                                     + "premise2 CHAR(22) NOT NULL,"
                                     + "inference CHAR(22) NOT NULL )").executeUpdate();
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
                                     + "id CHAR(22) NOT NULL,"
                                     + "parent CHAR(22) NOT NULL,"
                                     + "relationship CHAR(22) NOT NULL,"
                                     + "child CHAR(22) NOT NULL,"
                                     + "premise1 CHAR(22) NOT NULL,"
                                     + "premise2 CHAR(22) NOT NULL,"
                                     + "inference CHAR(22) NOT NULL )").executeUpdate();
    }

    private void createWorkingMemory() {
        em.createNativeQuery("CREATE TEMPORARY TABLE working_memory("
                                     + "parent CHAR(22) NOT NULL,"
                                     + "relationship CHAR(22) NOT NULL,"
                                     + "child CHAR(22) NOT NULL,"
                                     + "premise1 CHAR(22) NOT NULL,"
                                     + "premise2 CHAR(22) NOT NULL,"
                                     + "inference CHAR(22) NOT NULL )").executeUpdate();
    }

    @SuppressWarnings("unchecked")
    private Class<AttributeType> extractedAttribute() {
        return (Class<AttributeType>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[3];
    }

    @SuppressWarnings("unchecked")
    private Class<AttributeAuthorization> extractedAuthorization() {
        return (Class<AttributeAuthorization>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[2];
    }

    @SuppressWarnings("unchecked")
    private Class<RuleForm> extractedEntity() {
        return (Class<RuleForm>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @SuppressWarnings("unchecked")
    private Class<Network> extractedNetwork() {
        return (Class<Network>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    private boolean isRuleformAccessible(RuleForm parent,
                                         Relationship parentRelationship,
                                         Relationship authorizingRelationship,
                                         Ruleform child,
                                         Relationship childRelationship,
                                         String queryPrefix) {
        Query query;

        //if parent and child share a class, this is a network query rather than an access auth query
        if (parent.getClass().equals(child.getClass())) {
            query = em.createNamedQuery(String.format("%s%s",
                                                      queryPrefix,
                                                      ExistentialRuleform.GET_NETWORKS_SUFFIX));
            query.setParameter("parent", parent);
            query.setParameter("relationship", authorizingRelationship);
            query.setParameter("child", child);
        } else if (parentRelationship == null && childRelationship == null) {
            query = em.createNamedQuery(queryPrefix
                                        + AccessAuthorization.FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD_SUFFIX);
            query.setParameter("parent", parent);
            query.setParameter("relationship", authorizingRelationship);
            query.setParameter("child", child);
        } else if (childRelationship == null) {
            query = em.createNamedQuery(queryPrefix
                                        + AccessAuthorization.FIND_AUTHS_FOR_INDIRECT_PARENT_SUFFIX);
            query.setParameter("relationship", authorizingRelationship);
            query.setParameter("child", child);
            query.setParameter("netRelationship", parentRelationship);
            query.setParameter("netChild", parent);

        } else if (parentRelationship == null) {
            query = em.createNamedQuery(queryPrefix
                                        + AccessAuthorization.FIND_AUTHS_FOR_INDIRECT_CHILD_SUFFIX);
            query.setParameter("relationship", authorizingRelationship);
            query.setParameter("parent", parent);
            query.setParameter("netRelationship", childRelationship);
            query.setParameter("netChild", child);

        } else {
            query = em.createNamedQuery(queryPrefix
                                        + AccessAuthorization.FIND_AUTHS_FOR_INDIRECT_PARENT_AND_CHILD_SUFFIX);
            query.setParameter("relationship", authorizingRelationship);
            query.setParameter("parentNetRelationship", parentRelationship);
            query.setParameter("parentNetChild", parent);
            query.setParameter("childNetRelationship", childRelationship);
            query.setParameter("childNetChild", child);

        }
        List<?> results = query.getResultList();

        return results.size() > 0;

    }

    /**
     * @param attribute
     * @param authorizations
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <ValueType> List<ValueType> getAllowedValues(Attribute attribute,
                                                           List<AttributeAuthorization> authorizations) {
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
        for (AttributeAuthorization authorization : authorizations) {
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
}
