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

import static com.chiralbehaviors.CoRE.Ruleform.FIND_BY_NAME_SUFFIX;
import static com.chiralbehaviors.CoRE.Ruleform.FIND_FLAGGED_SUFFIX;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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
import com.chiralbehaviors.CoRE.kernel.KernelUtil;
import com.chiralbehaviors.CoRE.meta.AgencyModel;
import com.chiralbehaviors.CoRE.meta.AttributeModel;
import com.chiralbehaviors.CoRE.meta.IntervalModel;
import com.chiralbehaviors.CoRE.meta.JobModel;
import com.chiralbehaviors.CoRE.meta.LocationModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.meta.ProductModel;
import com.chiralbehaviors.CoRE.meta.RelationshipModel;
import com.chiralbehaviors.CoRE.meta.StatusCodeModel;
import com.chiralbehaviors.CoRE.meta.UnitModel;
import com.chiralbehaviors.CoRE.meta.WorkspaceModel;
import com.chiralbehaviors.CoRE.meta.workspace.Workspace;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;

/**
 * @author hhildebrand
 *
 */
public class ModelImpl implements Model {

    public static String prefixFor(Class<?> ruleform) {
        String simpleName = ruleform.getSimpleName();
        StringBuilder builder = new StringBuilder(simpleName.length());
        builder.append(Character.toLowerCase(simpleName.charAt(0)));
        builder.append(simpleName.substring(1));
        return builder.toString();
    }

    private final AgencyModel       agencyModel;
    private final AttributeModel    attributeModel;
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
    private final List<Workspace>   workspaces = new ArrayList<>();

    public ModelImpl(EntityManagerFactory emf) {
        EntityManager entityManager = emf.createEntityManager();
        em = new EmWrapper(new Animations(this, entityManager), entityManager);
        Workspace kernelWorkspace = KernelUtil.getKernelWorkspace(entityManager);
        register(kernelWorkspace);
        kernelWorkspace.replaceFrom(em);
        kernel = kernelWorkspace.getAccessor(Kernel.class);
        attributeModel = new AttributeModelImpl(this);
        productModel = new ProductModelImpl(this);
        intervalModel = new IntervalModelImpl(this);
        locationModel = new LocationModelImpl(this);
        agencyModel = new AgencyModelImpl(this);
        jobModel = new JobModelImpl(this);
        relationshipModel = new RelationshipModelImpl(this);
        statusCodeModel = new StatusCodeModelImpl(this);
        unitModel = new UnitModelImpl(this);
        workspaceModel = new WorkspaceModelImpl(this);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#deregister(com.chiralbehaviors.CoRE.workspace.Workspace)
     */
    @Override
    public void deregister(Workspace workspace) {
        workspaces.remove(workspace);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.Model#find(com.chiralbehaviors.CoRE.attribute.
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

        Predicate whereAttribute = criteriaBuilder.equal(attributeValue_.get("attribute"),
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
     * @see com.chiralbehaviors.CoRE.meta.Model#find(java.lang.Long, java.lang.Class)
     */
    @Override
    public <RuleForm extends Ruleform> RuleForm find(Long id,
                                                     Class<RuleForm> clazz) {
        return em.find(clazz, id);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.Model#find(java.lang.String,
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
     * @see com.chiralbehaviors.CoRE.meta.Model#findFlagged(java.lang.Class)
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
     * com.chiralbehaviors.CoRE.meta.Model#findUpdatedBy(com.chiralbehaviors.CoRE.agency
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
     * @see com.chiralbehaviors.CoRE.meta.Model#getAgencyModel()
     */
    @Override
    public AgencyModel getAgencyModel() {
        return agencyModel;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.Model#getAttributeModel()
     */
    @Override
    public AttributeModel getAttributeModel() {
        return attributeModel;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.Model#getEntityManager()
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
     * @see com.chiralbehaviors.CoRE.meta.Model#getJobModel()
     */
    @Override
    public JobModel getJobModel() {
        return jobModel;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.Model#getKernel()
     */
    @Override
    public Kernel getKernel() {
        return kernel;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.Model#getLocationModel()
     */
    @Override
    public LocationModel getLocationModel() {
        return locationModel;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> NetworkedModel<RuleForm, Network, ?, ?> getNetworkedModel(ExistentialRuleform<RuleForm, Network> ruleform) {
        switch (ruleform.getClass().getSimpleName()) {
            case "Agency":
                return (NetworkedModel<RuleForm, Network, ?, ?>) getAgencyModel();
            case "Attribute":
                return (NetworkedModel<RuleForm, Network, ?, ?>) getAttributeModel();
            case "Interval":
                return (NetworkedModel<RuleForm, Network, ?, ?>) getIntervalModel();
            case "Location":
                return (NetworkedModel<RuleForm, Network, ?, ?>) getLocationModel();
            case "Product":
                return (NetworkedModel<RuleForm, Network, ?, ?>) getProductModel();
            case "Relationship":
                return (NetworkedModel<RuleForm, Network, ?, ?>) getRelationshipModel();
            case "StatusCode":
                return (NetworkedModel<RuleForm, Network, ?, ?>) getStatusCodeModel();
            case "Unit":
                return (NetworkedModel<RuleForm, Network, ?, ?>) getUnitModel();
            default:
                throw new IllegalArgumentException(
                                                   String.format("Not a known existential ruleform: %s",
                                                                 ruleform.getClass()));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.Model#getProductModel()
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
     * @see com.chiralbehaviors.CoRE.meta.Model#getUnitModel()
     */
    @Override
    public UnitModel getUnitModel() {
        return unitModel;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#getWorkspaceModel()
     */
    @Override
    public WorkspaceModel getWorkspaceModel() {
        return workspaceModel;
    }

    @Override
    public void refreshWorkspaces() {
        for (Workspace workspace : workspaces) {
            workspace.refreshFrom(em);
        }
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#register(com.chiralbehaviors.CoRE.workspace.Workspace)
     */
    @Override
    public void register(Workspace workspace) {
        workspaces.add(workspace);
    }
}
