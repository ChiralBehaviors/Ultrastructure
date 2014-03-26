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

package com.hellblazer.CoRE.meta.models;

import static com.chiralbehaviors.CoRE.Ruleform.FIND_BY_NAME_SUFFIX;
import static com.chiralbehaviors.CoRE.Ruleform.FIND_FLAGGED_SUFFIX;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.attribute.AttributeValue_;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.KernelImpl;
import com.chiralbehaviors.CoRE.security.AuthenticatedPrincipal;
import com.hellblazer.CoRE.meta.AgencyModel;
import com.hellblazer.CoRE.meta.AttributeModel;
import com.hellblazer.CoRE.meta.CoordinateModel;
import com.hellblazer.CoRE.meta.IntervalModel;
import com.hellblazer.CoRE.meta.JobModel;
import com.hellblazer.CoRE.meta.LocationModel;
import com.hellblazer.CoRE.meta.Model;
import com.hellblazer.CoRE.meta.ProductModel;
import com.hellblazer.CoRE.meta.RelationshipModel;
import com.hellblazer.CoRE.meta.StatusCodeModel;
import com.hellblazer.CoRE.meta.UnitModel;
import com.hellblazer.CoRE.meta.WorkspaceModel;

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
     * @param agency
     *            - the id of the agency corresponding to the principal
     * @param activeRoleRelationships
     *            - the ids of the relationships of the active role aspects. If
     *            null, then no active role aspects are set.
     * @param activeRoleAgencys
     *            - the ids of the agencys of the active role aspects. If null,
     *            then no active role aspects are set.
     * 
     * @throws IllegalArgumentException
     *             if the activeRoleRelationships.length !=
     *             activeRoleAgencys.length
     */
    public static void setPrincipal(Long agency,
                                    Long[] activeRoleRelationships,
                                    Long[] activeRoleAgencys) {
        /*
         * EntityManager em = JSP.getEm(); if (activeRoleRelationships == null
         * || activeRoleAgencys == null) { principal = new
         * AuthenticatedPrincipal(em.find(Agency.class, agency)); } else { if
         * (activeRoleRelationships.length != activeRoleAgencys.length) { throw
         * new IllegalArgumentException(
         * "active role relationships and agencys must be of the same length");
         * } List<Aspect<Agency>> aspects = new ArrayList<Aspect<Agency>>(); for
         * (int i = 0; i < activeRoleRelationships.length; i++) {
         * aspects.add(new Aspect<Agency>( em.find(Relationship.class,
         * activeRoleRelationships[i]), em.find(Agency.class,
         * activeRoleAgencys[i]))); } principal = new
         * AuthenticatedPrincipal(em.find(Agency.class, agency), aspects); }
         */
    }

    private final AgencyModel       agencyModel;
    private final AttributeModel    attributeModel;
    private final CoordinateModel   coordinateModel;
    private final EntityManager     em;
    private final IntervalModel     intervalModel;
    private final JobModel          jobModel;

    private final Kernel            kernel;

    private final LocationModel     locationModel;
    private final ProductModel      productModel;
    private final RelationshipModel relationshipModel;
    private final StatusCodeModel   statusCodeModel;
    private final UnitModel         unitModel;
    private final WorkspaceModel    workspaceModel;

    public ModelImpl(EntityManager entityManager) {
        this(entityManager, new KernelImpl(entityManager));
    }

    public ModelImpl(EntityManager entityManager, Kernel k) {
        em = entityManager;
        kernel = k;
        attributeModel = new AttributeModelImpl(em, kernel);
        productModel = new ProductModelImpl(em, kernel);
        intervalModel = new IntervalModelImpl(em, kernel);
        locationModel = new LocationModelImpl(em, kernel);
        agencyModel = new AgencyModelImpl(em, kernel);
        jobModel = new JobModelImpl(this);
        relationshipModel = new RelationshipModelImpl(em, kernel);
        statusCodeModel = new StatusCodeModelImpl(em, kernel);
        coordinateModel = new CoordinateModelImpl(em, kernel);
        unitModel = new UnitModelImpl(em, kernel);
        workspaceModel = new WorkspaceModelImpl(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.meta.Model#find(com.hellblazer.CoRE.attribute.
     * AttributeValue)
     */
    @Override
    public <AttributeType extends AttributeValue<RuleForm>, RuleForm extends Ruleform> List<RuleForm> find(AttributeType attributeValue) {
        /*
         * SELECT e FROM Product e, ProductAttribute ea, Attribute a WHERE
         * ea.product = e, ea.value = :value, a = :attribute;
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

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.meta.Model#find(java.lang.Long, java.lang.Class)
     */
    @Override
    public <RuleForm extends Ruleform> RuleForm find(Long id,
                                                     Class<RuleForm> clazz) {
        return em.find(clazz, id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.meta.Model#find(java.lang.String,
     * java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <RuleForm extends ExistentialRuleform<?, ?>> RuleForm find(String name,
                                                                      Class<RuleForm> ruleform) {
        try {
            return (RuleForm) em.createNamedQuery(prefixFor(ruleform)
                                                          + FIND_BY_NAME_SUFFIX).setParameter("name",
                                                                                              name).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.meta.Model#findFlagged(java.lang.Class)
     */
    @Override
    public <RuleForm extends Ruleform> List<RuleForm> findFlagged(Class<RuleForm> ruleform) {
        return em.createNamedQuery(prefixFor(ruleform) + FIND_FLAGGED_SUFFIX,
                                   ruleform).getResultList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.Model#findUpdatedBy(com.hellblazer.CoRE.agency
     * .Agency, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <RuleForm extends Ruleform> List<RuleForm> findUpdatedBy(Agency updatedBy,
                                                                    Class<Ruleform> ruleform) {
        return em.createNamedQuery(prefixFor(ruleform) + FIND_BY_NAME_SUFFIX).setParameter("agency",
                                                                                           updatedBy).getResultList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.meta.Model#getAgencyModel()
     */
    @Override
    public AgencyModel getAgencyModel() {
        return agencyModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.meta.Model#getAttributeModel()
     */
    @Override
    public AttributeModel getAttributeModel() {
        return attributeModel;
    }

    @Override
    public CoordinateModel getCoordinateModel() {
        return coordinateModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.meta.Model#getEntityManager()
     */
    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public IntervalModel getIntervalModel() {
        return intervalModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.meta.Model#getJobModel()
     */
    @Override
    public JobModel getJobModel() {
        return jobModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.meta.Model#getKernel()
     */
    @Override
    public Kernel getKernel() {
        return kernel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.meta.Model#getLocationModel()
     */
    @Override
    public LocationModel getLocationModel() {
        return locationModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.meta.Model#getProductModel()
     */
    @Override
    public ProductModel getProductModel() {
        return productModel;
    }

    @Override
    public RelationshipModel getRelationshipModel() {
        return relationshipModel;
    }

    @Override
    public StatusCodeModel getStatusCodeModel() {
        return statusCodeModel;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.Model#getUnitModel()
     */
    @Override
    public UnitModel getUnitModel() {
        return unitModel;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.Model#getWorkspaceModel()
     */
    @Override
    public WorkspaceModel getWorkspaceModel() {
        return workspaceModel;
    }

}
