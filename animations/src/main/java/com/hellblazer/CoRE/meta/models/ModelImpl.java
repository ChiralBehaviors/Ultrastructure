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

import static com.hellblazer.CoRE.Ruleform.FIND_BY_NAME_SUFFIX;
import static com.hellblazer.CoRE.Ruleform.FIND_FLAGGED_SUFFIX;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.attribute.AttributeValue;
import com.hellblazer.CoRE.attribute.AttributeValue_;
import com.hellblazer.CoRE.kernel.Kernel;
import com.hellblazer.CoRE.kernel.KernelImpl;
import com.hellblazer.CoRE.meta.AttributeModel;
import com.hellblazer.CoRE.meta.JobModel;
import com.hellblazer.CoRE.meta.LocationModel;
import com.hellblazer.CoRE.meta.Model;
import com.hellblazer.CoRE.meta.ProductModel;
import com.hellblazer.CoRE.meta.ResourceModel;
import com.hellblazer.CoRE.resource.Resource;
import com.hellblazer.CoRE.security.AuthenticatedPrincipal;

/**
 * @author hhildebrand
 * 
 */
public class ModelImpl implements Model {
    private static AuthenticatedPrincipal principal;

    public static AuthenticatedPrincipal getPrincipal() {
        return principal;
    }

    public static String prefixFor(Class<?> ruleform) {
        String simpleName = ruleform.getSimpleName();
        StringBuilder builder = new StringBuilder(simpleName.length());
        builder.append(Character.toLowerCase(simpleName.charAt(0)));
        builder.append(simpleName.substring(1));
        return builder.toString();
    }

    /**
     * Sets up the authenticated principal to use in the animation's in database
     * context. Will throw all sorts of errors if used outside of the context of
     * a Java stored proceedure.
     * 
     * @param resource
     *            - the id of the resource corresponding to the principal
     * @param activeRoleRelationships
     *            - the ids of the relationships of the active role aspects. If
     *            null, then no active role aspects are set.
     * @param activeRoleResources
     *            - the ids of the resources of the active role aspects. If
     *            null, then no active role aspects are set.
     * 
     * @throws IllegalArgumentException
     *             if the activeRoleRelationships.length !=
     *             activeRoleResources.length
     */
    public static void setPrincipal(Long resource,
                                    Long[] activeRoleRelationships,
                                    Long[] activeRoleResources) {
        /*
        EntityManager em = JSP.getEm();
        if (activeRoleRelationships == null || activeRoleResources == null) {
            principal = new AuthenticatedPrincipal(em.find(Resource.class,
                                                           resource));
        } else {
            if (activeRoleRelationships.length != activeRoleResources.length) {
                throw new IllegalArgumentException(
                                                   "active role relationships and resources must be of the same length");
            }
            List<Aspect<Resource>> aspects = new ArrayList<Aspect<Resource>>();
            for (int i = 0; i < activeRoleRelationships.length; i++) {
                aspects.add(new Aspect<Resource>(
                                                 em.find(Relationship.class,
                                                         activeRoleRelationships[i]),
                                                 em.find(Resource.class,
                                                         activeRoleResources[i])));
            }
            principal = new AuthenticatedPrincipal(em.find(Resource.class,
                                                           resource), aspects);
        }
        */
    }

    private final AttributeModel attributeModel;
    private final EntityManager  em;
    private final ProductModel   productModel;
    private final Kernel         kernel;
    private final LocationModel  locationModel;
    private final ResourceModel  resourceModel;
    private final JobModel       jobModel;

    public ModelImpl(EntityManager entityManager) {
        this(entityManager, new KernelImpl(entityManager));
    }

    public ModelImpl(EntityManager entityManager, Kernel k) {
        em = entityManager;
        kernel = k;
        attributeModel = new AttributeModelImpl(em, kernel);
        productModel = new ProductModelImpl(em, kernel);
        locationModel = new LocationModelImpl(em, kernel);
        resourceModel = new ResourceModelImpl(em, kernel);
        jobModel = new JobModelImpl(this);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.Model#find(com.hellblazer.CoRE.attribute.AttributeValue)
     */
    @Override
    public <AttributeType extends AttributeValue<RuleForm>, RuleForm extends Ruleform> List<RuleForm> find(AttributeType attributeValue) {
        /*
         * SELECT e FROM Product e, ProductAttribute ea, Attribute a WHERE ea.product = e, ea.value = :value, a = :attribute;
         */

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

        Class<RuleForm> ruleformClass = attributeValue.getRuleformClass();

        CriteriaQuery<RuleForm> query = criteriaBuilder.createQuery(ruleformClass);
        Root<RuleForm> ruleform = query.from(ruleformClass);
        Expression<RuleForm> rf = ruleform.as(ruleformClass);

        query.select(ruleform);

        @SuppressWarnings("unchecked")
        Root<AttributeValue<?>> attributeValue_ = (Root<AttributeValue<?>>) query.from(attributeValue.getClass());

        Predicate whereAttribute = criteriaBuilder.equal(attributeValue_.get(AttributeValue_.attribute),
                                                         attributeValue.getAttribute());

        @SuppressWarnings("unchecked")
        SingularAttribute<? super AttributeValue<?>, RuleForm> ruleformAttribute = (SingularAttribute<? super AttributeValue<?>, RuleForm>) (SingularAttribute<AttributeType, RuleForm>) attributeValue.getRuleformAttribute();
        Predicate whereRuleform = criteriaBuilder.equal(attributeValue_.join(ruleformAttribute),
                                                        rf);

        Predicate whereAttributeValue = null;
        switch (attributeValue.getAttribute().getValueType()) {
            case BINARY: {
                whereAttributeValue = criteriaBuilder.equal(attributeValue_.get(AttributeValue_.binaryValue),
                                                            attributeValue.getBinaryValue());
                break;
            }
            case INTEGER: {
                whereAttributeValue = criteriaBuilder.equal(attributeValue_.get(AttributeValue_.integerValue),
                                                            attributeValue.getIntegerValue());
                break;
            }
            case BOOLEAN: {
                whereAttributeValue = criteriaBuilder.equal(attributeValue_.get(AttributeValue_.booleanValue),
                                                            attributeValue.getBooleanValue());
                break;
            }
            case NUMERIC: {
                whereAttributeValue = criteriaBuilder.equal(attributeValue_.get(AttributeValue_.numericValue),
                                                            attributeValue.getNumericValue());
                break;
            }
            case TEXT: {
                whereAttributeValue = criteriaBuilder.equal(attributeValue_.get(AttributeValue_.textValue),
                                                            attributeValue.getTextValue());
                break;
            }
            case TIMESTAMP: {
                whereAttributeValue = criteriaBuilder.equal(attributeValue_.get(AttributeValue_.timestampValue),
                                                            attributeValue.getTimestampValue());
                break;
            }
        }
        query.where(criteriaBuilder.and(whereAttribute, whereAttributeValue,
                                        whereRuleform));

        return em.createQuery(query).getResultList();
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.Model#find(java.lang.Long, java.lang.Class)
     */
    @Override
    public <RuleForm extends Ruleform> RuleForm find(Long id,
                                                     Class<RuleForm> clazz) {
        return em.find(clazz, id);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.Model#find(java.lang.String, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <RuleForm extends ExistentialRuleform> RuleForm find(String name,
                                                                Class<RuleForm> ruleform) {
        try {
            return (RuleForm) em.createNamedQuery(prefixFor(ruleform)
                                                          + FIND_BY_NAME_SUFFIX).setParameter("name",
                                                                                              name).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.Model#findFlagged(java.lang.Class)
     */
    @Override
    public <RuleForm extends Ruleform> List<RuleForm> findFlagged(Class<RuleForm> ruleform) {
        return em.createNamedQuery(prefixFor(ruleform) + FIND_FLAGGED_SUFFIX,
                                   ruleform).getResultList();
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.Model#findUpdatedBy(com.hellblazer.CoRE.resource.Resource, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <RuleForm extends Ruleform> List<RuleForm> findUpdatedBy(Resource updatedBy,
                                                                    Class<Ruleform> ruleform) {
        return em.createNamedQuery(prefixFor(ruleform) + FIND_BY_NAME_SUFFIX).setParameter("resource",
                                                                                           updatedBy).getResultList();
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.Model#getAttributeModel()
     */
    @Override
    public AttributeModel getAttributeModel() {
        return attributeModel;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.Model#getEntityManager()
     */
    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.Model#getProductModel()
     */
    @Override
    public ProductModel getProductModel() {
        return productModel;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.Model#getJobModel()
     */
    @Override
    public JobModel getJobModel() {
        return jobModel;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.Model#getKernel()
     */
    @Override
    public Kernel getKernel() {
        return kernel;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.Model#getLocationModel()
     */
    @Override
    public LocationModel getLocationModel() {
        return locationModel;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.Model#getResourceModel()
     */
    @Override
    public ResourceModel getResourceModel() {
        return resourceModel;
    }
}
