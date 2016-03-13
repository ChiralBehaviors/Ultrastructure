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

import static com.chiralbehaviors.CoRE.Ruleform.FIND_BY_NAME_SUFFIX;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownAgency;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownProduct;
import com.chiralbehaviors.CoRE.attribute.AttributeValue_;
import com.chiralbehaviors.CoRE.existential.ExistentialRuleform;
import com.chiralbehaviors.CoRE.existential.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.existential.domain.Agency;
import com.chiralbehaviors.CoRE.existential.domain.AgencyNetworkAuthorization;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.phantasm.agency.CoreInstance;
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
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.java.PhantasmDefinition;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.chiralbehaviors.CoRE.workspace.StateSnapshot;

/**
 * @author hhildebrand
 *
 */
public class ModelImpl implements Model {

    private final static ConcurrentMap<Class<?>, PhantasmDefinition<?>> cache = new ConcurrentHashMap<>();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static PhantasmDefinition<?> cached(Class<? extends Phantasm<?>> phantasm,
                                               Model model) {
        return cache.computeIfAbsent(phantasm,
                                     (Class<?> p) -> new PhantasmDefinition(p,
                                                                            model));
    }

    public static void clearPhantasmCache() {
        cache.clear();
    }

    public static String prefixFor(Class<?> ruleform) {
        String simpleName = ruleform.getSimpleName();
        StringBuilder builder = new StringBuilder(simpleName.length());
        builder.append(Character.toLowerCase(simpleName.charAt(0)));
        builder.append(simpleName.substring(1));
        return builder.toString();
    }

    private final AgencyModel       agencyModel;
    private final Animations        animations;
    private final AttributeModel    attributeModel;
    private AuthorizedPrincipal     currentPrincipal;
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

    public ModelImpl(EntityManagerFactory emf) {
        EntityManager entityManager = emf.createEntityManager();
        animations = new Animations(this, entityManager);
        em = new EmWrapper(animations, entityManager);
        workspaceModel = new WorkspaceModelImpl(this);
        kernel = workspaceModel.getScoped(WellKnownProduct.KERNEL_WORKSPACE.id())
                               .getWorkspace()
                               .getAccessor(Kernel.class);
        attributeModel = new AttributeModelImpl(this);
        productModel = new ProductModelImpl(this);
        intervalModel = new IntervalModelImpl(this);
        locationModel = new LocationModelImpl(this);
        agencyModel = new AgencyModelImpl(this);
        jobModel = new JobModelImpl(this);
        relationshipModel = new RelationshipModelImpl(this);
        statusCodeModel = new StatusCodeModelImpl(this);
        unitModel = new UnitModelImpl(this);
        initializeCurrentPrincipal();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#apply(com.chiralbehaviors.CoRE.phantasm.Phantasm, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends ExistentialRuleform<T, ?>, R extends Phantasm<T>> R apply(Class<R> phantasm,
                                                                                Phantasm<? extends T> target) {
        PhantasmDefinition<? extends T> definition = (PhantasmDefinition<? extends T>) cached(phantasm,
                                                                                              this);
        return (R) definition.construct(target.getRuleform(), this,
                                        getCurrentPrincipal().getPrincipal());
    }

    @Override
    public PhantasmDefinition<?> cached(Class<? extends Phantasm<?>> phantasm) {
        return cached(phantasm, this);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#cast(com.chiralbehaviors.CoRE.phantasm.Phantasm, java.lang.Class)
     */
    @Override
    public <T extends ExistentialRuleform<T, ?>, R extends Phantasm<T>> R cast(Phantasm<? extends T> source,
                                                                               Class<R> phantasm) {
        return (R) wrap(phantasm, source.getRuleform());
    }

    /* (non-Javadoc)
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() {
        if (!em.isOpen()) {
            return;
        }
        getEntityManager().close();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#construct(java.lang.Class, java.lang.String, java.lang.String, com.chiralbehaviors.CoRE.agency.Agency)
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T extends ExistentialRuleform<T, ?>, R extends Phantasm<T>> R construct(Class<R> phantasm,
                                                                                    String name,
                                                                                    String description) throws InstantiationException {
        PhantasmDefinition<? extends T> definition = (PhantasmDefinition) cached(phantasm,
                                                                                 this);
        ExistentialRuleform<? extends T, ?> ruleform;
        try {
            ruleform = (T) Model.getExistentialRuleformConstructor(phantasm)
                                .newInstance(name, description,
                                             getCurrentPrincipal().getPrincipal());
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            InstantiationException ex = new InstantiationException(String.format("Cannot construct instance of existential ruleform for %s",
                                                                                 phantasm));
            ex.initCause(e);
            throw ex;
        }
        getEntityManager().persist(ruleform);
        return (R) definition.construct(ruleform, this,
                                        getCurrentPrincipal().getPrincipal());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#executeAs(com.chiralbehaviors.CoRE.security.AuthenticatedPrincipal, java.util.concurrent.Callable)
     */
    @Override
    public <V> V executeAs(AuthorizedPrincipal principal,
                           Callable<V> function) throws Exception {
        V value = null;
        AuthorizedPrincipal previous = currentPrincipal;
        currentPrincipal = principal;
        try {
            value = function.call();
        } finally {
            currentPrincipal = previous;
        }
        return value;
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
        switch (attributeValue.getAttribute()
                              .getValueType()) {
            case BINARY: {
                whereAttributeValue = criteriaBuilder.equal(attributeValue_.get(AttributeValue_.binaryValue),
                                                            (byte[]) attributeValue.getValue());
                break;
            }
            case INTEGER: {
                whereAttributeValue = criteriaBuilder.equal(attributeValue_.get(AttributeValue_.integerValue),
                                                            (Integer) attributeValue.getValue());
                break;
            }
            case BOOLEAN: {
                whereAttributeValue = criteriaBuilder.equal(attributeValue_.get(AttributeValue_.booleanValue),
                                                            (Boolean) attributeValue.getValue());
                break;
            }
            case NUMERIC: {
                whereAttributeValue = criteriaBuilder.equal(attributeValue_.get(AttributeValue_.numericValue),
                                                            (BigDecimal) attributeValue.getValue());
                break;
            }
            case TEXT: {
                whereAttributeValue = criteriaBuilder.equal(attributeValue_.get(AttributeValue_.textValue),
                                                            (String) attributeValue.getValue());
                break;
            }
            case TIMESTAMP: {
                whereAttributeValue = criteriaBuilder.equal(attributeValue_.get(AttributeValue_.timestampValue),
                                                            (Timestamp) attributeValue.getValue());
                break;
            }
            case JSON: {
                whereAttributeValue = criteriaBuilder.equal(attributeValue_.get(AttributeValue_.jsonValue),
                                                            attributeValue.getValue());
                break;
            }
        }
        query.where(criteriaBuilder.and(whereAttribute, whereAttributeValue,
                                        whereRuleform));

        return em.createQuery(query)
                 .getResultList();
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
                                                  + FIND_BY_NAME_SUFFIX)
                                .setParameter("name", name)
                                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.Model#find(java.lang.Long, java.lang.Class)
     */
    @Override
    public <RuleForm extends Ruleform> RuleForm find(UUID id,
                                                     Class<RuleForm> clazz) {
        return em.find(clazz, id);
    }

    @Override
    public <RuleForm extends Ruleform> List<RuleForm> findAll(Class<RuleForm> ruleform) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RuleForm> query = cb.createQuery(ruleform);
        Root<RuleForm> root = query.from(ruleform);
        TypedQuery<RuleForm> q = em.createQuery(query.select(root));
        return q.getResultList();
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
        return em.createNamedQuery(prefixFor(ruleform) + FIND_BY_NAME_SUFFIX)
                 .setParameter("agency", updatedBy)
                 .getResultList();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#flushWorkspaces()
     */
    @Override
    public void flushWorkspaces() {
        workspaceModel.flush();
        initializeCurrentPrincipal();
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

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#getCoreInstance()
     */
    @Override
    public CoreInstance getCoreInstance() {
        try {
            return wrap(CoreInstance.class,
                        getAgencyModel().getChild(getKernel().getCore(),
                                                  getKernel().getSingletonOf()
                                                             .getInverse()));
        } catch (NoResultException e) {
            throw new IllegalStateException("The CoRE system has not been initialized properly",
                                            e);
        }
    }

    @Override
    public AuthorizedPrincipal getCurrentPrincipal() {
        AuthorizedPrincipal authorizedPrincipal = currentPrincipal;
        return authorizedPrincipal == null ? new AuthorizedPrincipal(kernel.getCoreAnimationSoftware())
                                           : authorizedPrincipal;
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
        ExistentialRuleform<RuleForm, Network> unproxied = Ruleform.initializeAndUnproxy(ruleform);
        switch (unproxied.getClass()
                         .getSimpleName()) {
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
                throw new IllegalArgumentException(String.format("Not a known existential ruleform: %s",
                                                                 unproxied.getClass()));
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
     * @see com.chiralbehaviors.CoRE.meta.Model#getUnknownNetworkedModel(com.chiralbehaviors.CoRE.ExistentialRuleform)
     */
    @Override
    public <RuleForm extends ExistentialRuleform<?, ?>> NetworkedModel<?, ?, ?, ?> getUnknownNetworkedModel(RuleForm ruleform) {
        RuleForm unproxied = Ruleform.initializeAndUnproxy(ruleform);
        switch (unproxied.getClass()
                         .getSimpleName()) {
            case "Agency":
                return getAgencyModel();
            case "Attribute":
                return getAttributeModel();
            case "Interval":
                return getIntervalModel();
            case "Location":
                return getLocationModel();
            case "Product":
                return getProductModel();
            case "Relationship":
                return getRelationshipModel();
            case "StatusCode":
                return getStatusCodeModel();
            case "Unit":
                return getUnitModel();
            default:
                throw new IllegalArgumentException(String.format("Not a known existential ruleform: %s",
                                                                 unproxied.getClass()));
        }
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#getWorkspaceModel()
     */
    @Override
    public WorkspaceModel getWorkspaceModel() {
        return workspaceModel;
    }

    @Override
    public void inferNetworks(ExistentialRuleform<?, ?> ruleform) {
        animations.inferNetworks(ruleform);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ExistentialRuleform<T, ?>, RuleForm extends T> Phantasm<? super T> lookup(Class<? extends Phantasm<? extends T>> phantasm,
                                                                                                UUID uuid) {
        RuleForm ruleform = (RuleForm) getEntityManager().find(Model.getExistentialRuleform(phantasm),
                                                               uuid);
        if (ruleform == null) {
            return null;
        }
        PhantasmDefinition<? extends T> definition = (PhantasmDefinition<? extends T>) cached(phantasm,
                                                                                              this);
        return (Phantasm<? super T>) definition.wrap(ruleform, this);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#principalFrom(com.chiralbehaviors.CoRE.agency.Agency, java.util.List)
     */
    @Override
    public AuthorizedPrincipal principalFrom(Agency principal,
                                             List<UUID> capabilities) {
        return new AuthorizedPrincipal(principal, capabilities.stream()
                                                              .map(uuid -> em.find(AgencyNetworkAuthorization.class,
                                                                                   uuid))
                                                              .filter(auth -> auth != null)
                                                              .filter(auth -> agencyModel.isAccessible(principal,
                                                                                                       auth.getClassifier(),
                                                                                                       auth.getClassification()))
                                                              .collect(Collectors.toList()));
    }

    @Override
    public StateSnapshot snapshot() {
        return new StateSnapshot(getEntityManager(), excludeThisSingleton());
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public <T extends ExistentialRuleform<?, ?>, R extends Phantasm<?>> R wrap(Class<R> phantasm,
                                                                               Phantasm<?> ruleform) {
        if (ruleform == null) {
            return null;
        }
        PhantasmDefinition<? extends T> definition = (PhantasmDefinition<? extends T>) cached(phantasm,
                                                                                              this);
        return (R) definition.wrap(ruleform.getRuleform(), this);
    }

    private Collection<? extends Ruleform> excludeThisSingleton() {
        List<Ruleform> excluded = new ArrayList<>();
        Agency instance = getCoreInstance().getRuleform();
        excluded.add(getAgencyModel().getImmediateLink(instance,
                                                       kernel.getSingletonOf(),
                                                       kernel.getCore()));
        excluded.add(getAgencyModel().getImmediateLink(kernel.getCore(),
                                                       kernel.getSingletonOf()
                                                             .getInverse(),
                                                       instance));
        return excluded;
    }

    private void initializeCurrentPrincipal() {
        if (em.getTransaction()
              .isActive()
            && em.getTransaction()
                 .getRollbackOnly()) {
            currentPrincipal = null;
        } else {
            currentPrincipal = new AuthorizedPrincipal(em.getReference(Agency.class,
                                                                       WellKnownAgency.CORE_ANIMATION_SOFTWARE.id()));
        }
    }
}
