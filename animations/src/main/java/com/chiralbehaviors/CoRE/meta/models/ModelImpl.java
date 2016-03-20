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

import static com.chiralbehaviors.CoRE.RecordsFactory.resolve;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.NoResultException;

import org.jooq.DSLContext;

import com.chiralbehaviors.CoRE.RecordsFactory;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownAgency;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownProduct;
import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Interval;
import com.chiralbehaviors.CoRE.domain.Location;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Unit;
import com.chiralbehaviors.CoRE.jooq.Ruleform;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.ExistentialAttribute;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.phantasm.agency.CoreInstance;
import com.chiralbehaviors.CoRE.meta.ExistentialModel;
import com.chiralbehaviors.CoRE.meta.JobModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.PhantasmModel;
import com.chiralbehaviors.CoRE.meta.RelationshipModel;
import com.chiralbehaviors.CoRE.meta.StatusCodeModel;
import com.chiralbehaviors.CoRE.meta.WorkspaceModel;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.java.PhantasmDefinition;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;

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

    private final ExistentialModel<Agency>    agencyModel;
    private final Animations                  animations;
    private final ExistentialModel<Attribute> attributeModel;
    private AuthorizedPrincipal               currentPrincipal;
    private final DSLContext                  create;
    private final ExistentialModel<Interval>  intervalModel;
    private final JobModel                    jobModel;
    private final PhantasmModel               phantasmModel;
    private final Kernel                      kernel;
    private final ExistentialModel<Location>  locationModel;
    private final ExistentialModel<Product>   productModel;
    private final RelationshipModel           relationshipModel;
    private final StatusCodeModel             statusCodeModel;
    private final ExistentialModel<Unit>      unitModel;

    private final WorkspaceModel              workspaceModel;

    public ModelImpl(DSLContext create) {
        animations = new Animations(this, null);
        this.create = create;
        workspaceModel = new WorkspaceModelImpl(this);
        kernel = workspaceModel.getScoped(WellKnownProduct.KERNEL_WORKSPACE.id())
                               .getWorkspace()
                               .getAccessor(Kernel.class);
        attributeModel = new ExistentialModelImpl<Attribute>(this) {
            @Override
            protected ExistentialDomain domain() {
                return ExistentialDomain.T;
            }

            @Override
            protected Class<Attribute> domainClass() {
                return Attribute.class;
            }
        };
        productModel = new ExistentialModelImpl<Product>(this) {
            @Override
            protected ExistentialDomain domain() {
                return ExistentialDomain.P;
            }

            @Override
            protected Class<Product> domainClass() {
                return Product.class;
            }
        };
        intervalModel = new ExistentialModelImpl<Interval>(this) {
            @Override
            protected ExistentialDomain domain() {
                return ExistentialDomain.I;
            }

            @Override
            protected Class<Interval> domainClass() {
                return Interval.class;
            }
        };
        locationModel = new ExistentialModelImpl<Location>(this) {
            @Override
            protected ExistentialDomain domain() {
                return ExistentialDomain.L;
            }

            @Override
            protected Class<Location> domainClass() {
                return Location.class;
            }
        };
        agencyModel = new ExistentialModelImpl<Agency>(this) {
            @Override
            protected ExistentialDomain domain() {
                return ExistentialDomain.A;
            }

            @Override
            protected Class<Agency> domainClass() {
                return Agency.class;
            }
        };
        jobModel = new JobModelImpl(this);
        relationshipModel = new RelationshipModelImpl(this);
        statusCodeModel = new StatusCodeModelImpl(this);
        unitModel = new ExistentialModelImpl<Unit>(this) {
            @Override
            protected ExistentialDomain domain() {
                return ExistentialDomain.U;
            }

            @Override
            protected Class<Unit> domainClass() {
                return Unit.class;
            }
        };
        phantasmModel = new PhantasmModelImpl(this);
        initializeCurrentPrincipal();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#apply(com.chiralbehaviors.CoRE.phantasm.Phantasm, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends ExistentialRuleform, R extends Phantasm<T>> R apply(Class<R> phantasm,
                                                                          Phantasm<T> target) {
        PhantasmDefinition<T> definition = (PhantasmDefinition<T>) cached(phantasm,
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
    public <T extends ExistentialRuleform, R extends Phantasm<T>> R cast(T source,
                                                                         Class<R> phantasm) {
        return wrap(phantasm, source);
    }

    /* (non-Javadoc)
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() {
        create.close();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#construct(java.lang.Class, java.lang.String, java.lang.String, com.chiralbehaviors.CoRE.agency.Agency)
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T extends ExistentialRuleform, R extends Phantasm<T>> R construct(Class<R> phantasm,
                                                                              String name,
                                                                              String description) throws InstantiationException {
        PhantasmDefinition<? extends T> definition = (PhantasmDefinition) cached(phantasm,
                                                                                 this);
        T ruleform;
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
        ((ExistentialRecord) ruleform).insert();
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
    public ExistentialModel<Agency> getAgencyModel() {
        return agencyModel;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.Model#getAttributeModel()
     */
    @Override
    public ExistentialModel<Attribute> getAttributeModel() {
        return attributeModel;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#getCoreInstance()
     */
    @SuppressWarnings("unchecked")
    @Override
    public CoreInstance getCoreInstance() {
        try {
            return wrap(CoreInstance.class,
                        phantasmModel.getChild(getKernel().getCore(),
                                               resolve(create,
                                                       getKernel().getSingletonOf()
                                                                  .getInverse())));
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
    public DSLContext getDSLContext() {
        return create;
    }

    @Override
    public ExistentialModel<Interval> getIntervalModel() {
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
    public ExistentialModel<Location> getLocationModel() {
        return locationModel;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.Model#getProductModel()
     */
    @Override
    public ExistentialModel<Product> getProductModel() {
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
    public ExistentialModel<Unit> getUnitModel() {
        return unitModel;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#getUnknownNetworkedModel(com.chiralbehaviors.CoRE.ExistentialRuleform)
     */
    public <RuleForm extends ExistentialRuleform> ExistentialModel<?> getUnknownNetworkedModel(RuleForm ruleform) {
        switch (ruleform.getClass()
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
                                                                 ruleform.getClass()));
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
    public void inferNetworks() {
        animations.inferNetworks();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ExistentialRuleform, R extends Phantasm<T>> R lookup(Class<R> phantasm,
                                                                           UUID uuid) {
        T ruleform = (T) RecordsFactory.resolve(create.selectFrom(EXISTENTIAL)
                                                      .where(EXISTENTIAL.ID.equal(uuid))
                                                      .fetchOne());
        if (ruleform == null) {
            return null;
        }
        PhantasmDefinition<? extends T> definition = (PhantasmDefinition<? extends T>) cached(phantasm,
                                                                                              this);
        return (R) definition.wrap(ruleform, this);
    }

    @SuppressWarnings("unused")
    private Collection<?> excludeThisSingleton() {
        List<Object> excluded = new ArrayList<>();
        Agency instance = getCoreInstance().getRuleform();
        excluded.add(phantasmModel.getImmediateLink(instance,
                                                    kernel.getSingletonOf(),
                                                    kernel.getCore()));
        excluded.add(phantasmModel.getImmediateLink(kernel.getCore(),
                                                    resolve(create,
                                                            kernel.getSingletonOf()
                                                                  .getInverse()),
                                                    instance));
        return excluded;
    }

    private void initializeCurrentPrincipal() {
        currentPrincipal = new AuthorizedPrincipal(create.selectFrom(EXISTENTIAL)
                                                         .where(EXISTENTIAL.ID.equal(WellKnownAgency.CORE_ANIMATION_SOFTWARE.id()))
                                                         .fetchOne()
                                                         .into(Agency.class));
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#find(com.chiralbehaviors.CoRE.jooq.tables.ExistentialAttribute)
     */
    @Override
    public ExistentialRecord find(ExistentialAttribute attributeValue) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#findUpdatedBy(com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord, java.lang.Class)
     */
    @Override
    public <RuleForm extends Ruleform> List<RuleForm> findUpdatedBy(ExistentialRecord updatedBy,
                                                                    Class<Ruleform> ruleform) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#findAll(java.lang.Class)
     */
    @Override
    public <RuleForm extends Ruleform> List<RuleForm> findAll(Class<RuleForm> ruleform) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#inferNetworks(com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord)
     */
    @Override
    public void inferNetworks(ExistentialRecord ruleform) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#principalFrom(com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord, java.util.List)
     */
    @Override
    public AuthorizedPrincipal principalFrom(ExistentialRecord principal,
                                             List<UUID> capabilities) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#getPhantasmModel()
     */
    @Override
    public PhantasmModel getPhantasmModel() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#wrap(java.lang.Class, com.chiralbehaviors.CoRE.phantasm.Phantasm)
     */
    @Override
    public <T extends ExistentialRuleform, R extends Phantasm<T>> R wrap(Class<R> phantasm,
                                                                         Phantasm<T> ruleform) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#lookupExistential(java.util.UUID)
     */
    @Override
    public ExistentialRecord lookupExistential(UUID id) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#getAttribute(java.util.UUID)
     */
    @Override
    public Attribute getAttribute(UUID id) {
        // TODO Auto-generated method stub
        return null;
    }
}
