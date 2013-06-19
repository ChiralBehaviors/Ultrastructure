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

package com.hellblazer.CoRE.meta.models;

import static com.hellblazer.CoRE.network.Networked.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.FIND_CLASSIFIED_ATTRIBUTE_VALUES_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_FOR_ATTRIBUTE_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.FIND_GROUPED_ATTRIBUTE_VALUES_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.GET_CHILD_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.UNLINKED_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.USED_RELATIONSHIPS_SUFFIX;

import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.AttributeNetwork;
import com.hellblazer.CoRE.attribute.AttributeValue;
import com.hellblazer.CoRE.attribute.ClassifiedAttributeAuthorization;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.location.LocationNetwork;
import com.hellblazer.CoRE.meta.Kernel;
import com.hellblazer.CoRE.meta.NetworkedModel;
import com.hellblazer.CoRE.network.Aspect;
import com.hellblazer.CoRE.network.Facet;
import com.hellblazer.CoRE.network.NetworkRuleform;
import com.hellblazer.CoRE.network.Networked;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.product.ProductNetwork;
import com.hellblazer.CoRE.resource.Resource;
import com.hellblazer.CoRE.resource.ResourceNetwork;

/**
 * @author hhildebrand
 * 
 */
abstract public class AbstractNetworkedModel<RuleForm extends Networked<RuleForm, ?>, AttributeAuthorization extends ClassifiedAttributeAuthorization<RuleForm>, AttributeType extends AttributeValue<?>>
        implements
        NetworkedModel<RuleForm, AttributeAuthorization, AttributeType> {

    private static final Logger log = LoggerFactory.getLogger(AbstractNetworkedModel.class);

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

    /**
     * @param product
     * @return
     */
    private static String tableName(Class<?> product) {
        StringBuilder builder = new StringBuilder();
        builder.append("ruleform.");
        boolean first = true;
        for (char c : product.getSimpleName().toCharArray()) {
            if (Character.isUpperCase(c)) {
                if (!first) {
                    builder.append('_');
                } else {
                    first = false;
                }
                builder.append(Character.toLowerCase(c));
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    private final Class<AttributeAuthorization> authorization;

    private final Class<AttributeType>          attribute;
    protected final Kernel                      kernel;
    protected final EntityManager               em;
    private final Class<RuleForm>               product;
    private final Class<Networked<RuleForm, ?>> network;
    private final String                        prefix;
    private final String                        networkPrefix;
    @SuppressWarnings("unused")
    private final String                        productTable;
    private final String                        networkTable;

    @SuppressWarnings("unchecked")
    public AbstractNetworkedModel(EntityManager em, Kernel kernel) {
        this.em = em;
        this.kernel = kernel;
        product = extractedEntity();
        authorization = extractedAuthorization();
        attribute = extractedAttribute();
        network = (Class<Networked<RuleForm, ?>>) getNetworkOf(product);
        prefix = ModelImpl.prefixFor(product);
        networkPrefix = ModelImpl.prefixFor(network);
        productTable = tableName(product);
        networkTable = tableName(network);
    }

    @Override
    public void clearDeducedRules() {
        /*
         * All original rules are defined to have a distance of 1 (i.e. users
         * should only enter parent-child links directly, and allow the
         * inference algorithm to fill in the rest). Inverse rules also have a
         * distance of 1, but are declared by 'Inverse Software'.
         * 
         * Thus, delete all rules with distance > 1, as well as all rules with
         * updated_by = 'Inverse Software'
         */
        Query clear = em.createQuery(String.format("DELETE FROM %s as net WHERE net.distance > 1 OR net.updatedBy = :inverseSoftware",
                                                   network.getSimpleName()));
        clear.setParameter("inverseSoftware", kernel.getInverseSoftware());
        long then = System.currentTimeMillis();
        int deleted = clear.executeUpdate();
        if (log.isInfoEnabled()) {
            log.info(String.format("cleared %s deduced rules from %s in %s ms",
                                   deleted, networkPrefix,
                                   System.currentTimeMillis() - then));
        }

        /*
         * Since the current method of generating network rules can burn through
         * a lot of sequence numbers, there's no reason not to reclaim them once
         * we delete inferred rules. Once all that remains in the network
         * ruleform is original rules, we just figure out what highest allocated
         * serial id thus far is and then set the sequence accordingly.
         */
        Query update = em.createNativeQuery(String.format("SELECT setval('%s_id_seq', (SELECT max(net.id) FROM %s as net))",
                                                          networkTable,
                                                          networkTable),
                                            Long.class);
        long maxId = (Long) update.getSingleResult();
        if (log.isInfoEnabled()) {
            log.info(String.format("New max id for %s is %s", networkPrefix,
                                   maxId));
        }

        /*
         * We'll analyze the table, since we are potentially dramatically
         * changing the distribution of data. This will ensure that the query
         * optimizer will have the most up-to-date information possible for
         * future queries on the table
         */

        then = System.currentTimeMillis();
        em.createNativeQuery(String.format("ANALYZE %s", networkTable)).executeUpdate();
        if (log.isInfoEnabled()) {
            log.info(String.format("Analyzed %s in %s ms",
                                   network.getSimpleName(),
                                   System.currentTimeMillis() - then));
        }
    }

    @Override
    public void generateInverses() {
        String q = String.format("INSERT INTO %s(parent, relationship, child, updated_by, distance) "
                                         + "SELECT net.child as parent, "
                                         + "    rel.inverse as relationship, "
                                         + "    net.parent as child, "
                                         + "    ?1 as updated_by,"
                                         + "    net.distance "
                                         + "FROM %s AS net "
                                         + "JOIN ruleform.relationship AS rel ON net.relationship = rel.id "
                                         + "LEFT OUTER JOIN %s AS exist "
                                         + "    ON net.child = exist.parent "
                                         + "    AND rel.inverse = exist.relationship "
                                         + "    AND net.parent = exist.child "
                                         + " WHERE exist.parent IS NULL "
                                         + "  AND exist.relationship IS NULL "
                                         + "  AND exist.child IS NULL",
                                 networkTable, networkTable, networkTable);
        Query query = em.createNativeQuery(q);
        query.setParameter(1, kernel.getInverseSoftware().getId());
        long then = System.currentTimeMillis();
        int created = query.executeUpdate();
        if (log.isInfoEnabled()) {
            log.info(String.format("created %s inverse rules of %s in %s ms",
                                   created, networkPrefix,
                                   System.currentTimeMillis() - then));
        }
    }

    @Override
    public <ValueType> List<ValueType> getAllowedValues(Attribute attribute,
                                                        Aspect<RuleForm> aspect) {
        return getAllowedValues(attribute,
                                getAttributeAuthorizations(aspect, attribute));
    }

    @Override
    public <ValueType> List<ValueType> getAllowedValues(Attribute attribute,
                                                        Resource groupingResource) {
        return getAllowedValues(attribute,
                                getAttributeAuthorizations(groupingResource,
                                                           attribute));
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

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.NetworkedModel#getAttributeAuthorizations(com.hellblazer.CoRE.meta.Aspect, com.hellblazer.CoRE.attribute.Attribute)
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

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.NetworkedModel#getAttributeAuthorizations(com.hellblazer.CoRE.resource.Resource)
     */
    @Override
    public List<AttributeAuthorization> getAttributeAuthorizations(Resource groupingResource) {
        TypedQuery<AttributeAuthorization> query = em.createNamedQuery(prefix
                                                                               + FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_SUFFIX,
                                                                       authorization);
        query.setParameter("groupingResource", groupingResource);
        return query.getResultList();
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.NetworkedModel#getAttributeAuthorizations(com.hellblazer.CoRE.resource.Resource, com.hellblazer.CoRE.attribute.Attribute)
     */
    @Override
    public List<AttributeAuthorization> getAttributeAuthorizations(Resource groupingResource,
                                                                   Attribute attribute) {
        TypedQuery<AttributeAuthorization> query = em.createNamedQuery(prefix
                                                                               + FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_FOR_ATTRIBUTE_SUFFIX,
                                                                       authorization);
        query.setParameter("groupingResource", groupingResource);
        query.setParameter("attribute", attribute);
        return query.getResultList();
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.NetworkedModel#getAttributesClassifiedBy(com.hellblazer.CoRE.network.Networked, com.hellblazer.CoRE.meta.Aspect)
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

    @Override
    public List<AttributeType> getAttributesClassifiedBy(RuleForm ruleform,
                                                         Resource groupingResource) {
        TypedQuery<AttributeType> query = em.createNamedQuery(prefix
                                                                      + FIND_GROUPED_ATTRIBUTE_VALUES_SUFFIX,
                                                              attribute);
        query.setParameter("ruleform", ruleform);
        query.setParameter("resource", groupingResource);
        return query.getResultList();
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.NetworkedModel#getAttributesGroupedBy(com.hellblazer.CoRE.network.Networked, com.hellblazer.CoRE.resource.Resource)
     */
    @Override
    public List<AttributeType> getAttributesGroupedBy(RuleForm ruleform,
                                                      Resource groupingResource) {
        TypedQuery<AttributeType> query = em.createNamedQuery(prefix
                                                                      + FIND_GROUPED_ATTRIBUTE_VALUES_SUFFIX,
                                                              attribute);
        query.setParameter("ruleform", ruleform);
        query.setParameter("resource", groupingResource);
        return query.getResultList();
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.NetworkedModel#getChild(com.hellblazer.CoRE.network.Networked, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public RuleForm getChild(RuleForm parent, Relationship r) {
        TypedQuery<RuleForm> query = em.createNamedQuery(prefix
                                                                 + GET_CHILD_SUFFIX,
                                                         product);
        query.setParameter("parent", parent);
        query.setParameter("relationship", r);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.NetworkedModel#getFacet(com.hellblazer.CoRE.network.Networked, com.hellblazer.CoRE.meta.Aspect)
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

    @SuppressWarnings("unchecked")
    @Override
    public List<RuleForm> getInGroup(RuleForm parent, Relationship relationship) {
        /*
         *  select n.child from <networkTable> n 
         *      where n.parent = :parent 
         *              and n.relationship = :relationship 
         *              and n.child <> :parent
         */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RuleForm> query = cb.createQuery(product);
        Root<Networked<RuleForm, ?>> networkForm = query.from(network);
        query.select((Selection<? extends RuleForm>) networkForm.fetch("child"));
        query.where(cb.equal(networkForm.get("relationship"), relationship),
                    cb.notEqual(networkForm.get("child"), parent));
        return em.createQuery(query).getResultList();
    }

    public Class<?> getNetworkOf(Class<?> networked) {
        if (networked == Attribute.class) {
            return AttributeNetwork.class;
        }
        if (networked == Product.class) {
            return ProductNetwork.class;
        }
        if (networked == Location.class) {
            return LocationNetwork.class;
        }
        if (networked == Resource.class) {
            return ResourceNetwork.class;
        }
        throw new IllegalArgumentException(
                                           String.format("Class %s is not a subclass of %s",
                                                         networked,
                                                         NetworkRuleform.class));
    }

    @Override
    public List<RuleForm> getNotInGroup(RuleForm parent,
                                        Relationship relationship) {
        /*
         * SELECT e 
         *      FROM 
         *        productTable AS e, 
         *        ProductNetwork AS n
         *      WHERE 
         *          n.parent = :parent 
         *          AND n.relationship = :relationship
         *          AND n.child <> e;
         */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RuleForm> query = cb.createQuery(product);
        Root<RuleForm> form = query.from(product);
        Root<Networked<RuleForm, ?>> networkForm = query.from(network);
        query.where(cb.equal(networkForm.get("parent"), parent),
                    cb.equal(networkForm.get("relationship"), relationship),
                    cb.notEqual(networkForm.get("child"), form));
        query.select(form);
        return em.createQuery(query).getResultList();
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.NetworkedModel#getUnlinked()
     */
    @Override
    public List<RuleForm> getUnlinked() {
        return em.createNamedQuery(prefix + UNLINKED_SUFFIX, product).getResultList();
    }

    @Override
    public List<Relationship> getUsedRelationships() {
        return em.createNamedQuery(prefix + USED_RELATIONSHIPS_SUFFIX,
                                   Relationship.class).getResultList();
    }

    @Override
    public void link(RuleForm parent, Relationship r, RuleForm child,
                     Resource updatedBy) {
        parent.link(r, child, updatedBy, kernel.getInverseSoftware(), em);
    }

    public void createInverseRelationshipo(RuleForm parent, Relationship r, RuleForm child, Resource updatedBy) {
    	child.link(r.getInverse(), parent, updatedBy, kernel.getInverseSoftware(), em);
    }
    
    @SuppressWarnings("unchecked")
    private Class<AttributeType> extractedAttribute() {
        return (Class<AttributeType>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[2];
    }

    @SuppressWarnings("unchecked")
    private Class<AttributeAuthorization> extractedAuthorization() {
        return (Class<AttributeAuthorization>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    @SuppressWarnings("unchecked")
    private Class<RuleForm> extractedEntity() {
        return (Class<RuleForm>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
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
                default:
            }
        }
        return allowedValues;
    }
}
