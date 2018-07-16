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

import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK;
import static com.chiralbehaviors.CoRE.jooq.Tables.JOB_CHRONOLOGY;
import static com.chiralbehaviors.CoRE.jooq.Tables.WORKSPACE_LABEL;
import static org.jooq.impl.DSL.name;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.jooq.CommonTableExpression;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.TransactionContext;
import org.jooq.TransactionProvider;
import org.jooq.UpdatableRecord;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultRecordListenerProvider;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.RecordsFactory;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownProduct;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownRelationship;
import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Interval;
import com.chiralbehaviors.CoRE.domain.Location;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.domain.StatusCode;
import com.chiralbehaviors.CoRE.domain.Unit;
import com.chiralbehaviors.CoRE.jooq.Ruleform;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.ExistentialNetwork;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.phantasm.CoreInstance;
import com.chiralbehaviors.CoRE.meta.JobModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.PhantasmModel;
import com.chiralbehaviors.CoRE.meta.WorkspaceModel;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.java.PhantasmDefinition;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.chiralbehaviors.CoRE.workspace.StateSnapshot;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;

/**
 * @author hhildebrand
 *
 */
public class ModelImpl implements Model {
    private static final String                                                       AGENCY     = "agency";
    private static final Map<Class<?>, MethodHandle>                                  AUTHORITY_HANDLE;
    private static final ConcurrentMap<Class<? extends Phantasm>, PhantasmDefinition> cache      = new ConcurrentHashMap<>();
    private static final String                                                       GROUPS     = "groups";
    private static final String                                                       MEMBERSHIP = "membership";
    private static final Integer                                                      ZERO       = Integer.valueOf(0);
    static {
        AUTHORITY_HANDLE = new HashMap<>();
        Ruleform.RULEFORM.getTables()
                         .stream()
                         .filter(t -> !t.equals(JOB_CHRONOLOGY))
                         .filter(t -> !t.equals(WORKSPACE_LABEL))
                         .forEach(t -> {
                             MethodHandle handle;
                             try {
                                 handle = MethodHandles.lookup()
                                                       .unreflect(t.getRecordType()
                                                                   .getMethod("getAuthority"));
                             } catch (Exception e) {
                                 throw new IllegalStateException(e);
                             }
                             @SuppressWarnings("unchecked")
                             Class<UpdatableRecord<?>> recordType = (Class<UpdatableRecord<?>>) t.getRecordType();
                             AUTHORITY_HANDLE.put(recordType, handle);
                         });
        Arrays.asList(Agency.class, Interval.class, Location.class,
                      Product.class, Relationship.class, StatusCode.class,
                      Unit.class)
              .stream()
              .forEach(c -> {
                  MethodHandle handle;
                  try {
                      handle = MethodHandles.lookup()
                                            .unreflect(c.getMethod("getAuthority"));
                  } catch (Exception e) {
                      throw new IllegalStateException(e);
                  }
                  AUTHORITY_HANDLE.put(c, handle);
              });
    }

    public static PhantasmDefinition cached(Class<? extends Phantasm> phantasm,
                                            Model model) {
        return cache.computeIfAbsent(phantasm,
                                     p -> new PhantasmDefinition(p, model));
    }

    public static void clearPhantasmCache() {
        cache.clear();
    }

    public static Configuration configuration() {
        Configuration configuration = new DefaultConfiguration().set(SQLDialect.POSTGRES_9_5);
        Settings settings = new Settings();
        settings.setExecuteWithOptimisticLocking(true);
        settings.withRenderFormatted(false);
        configuration.set(settings);
        return configuration;
    }

    public static Configuration configuration(Connection connection) throws SQLException {
        Configuration configuration = configuration();
        connection.setAutoCommit(false);
        configuration.set(connection);
        return configuration;
    }

    public static DSLContext newCreate(Connection connection) throws SQLException {
        return DSL.using(configuration(connection));
    }

    private final Animations     animations;
    private final Relationship   applyPerm;
    private final DSLContext     create;
    private final Relationship   createMetaPerm;
    private final Relationship   createPerm;
    private AuthorizedPrincipal  currentPrincipal;
    private final Relationship   deletePerm;
    private final Relationship   executeQueryPerm;
    private final RecordsFactory factory;
    private final Relationship   invokePerm;
    private final JobModel       jobModel;
    private final Kernel         kernel;
    private final Relationship   loginToPerm;
    private final PhantasmModel  phantasmModel;
    private final Relationship   readPerm;
    private final Relationship   removePerm;
    private final Relationship   updatePerm;
    private final WorkspaceModel workspaceModel;

    public ModelImpl(Connection connection) throws SQLException {
        this(newCreate(connection));
    }

    public ModelImpl(DSLContext create) {
        animations = new Animations(this, new Inference() {
            @Override
            public Model model() {
                return ModelImpl.this;
            }
        });
        establish(create);
        this.create = create;
        factory = new RecordsFactory() {

            @Override
            public DSLContext create() {
                return create;
            }

            @Override
            public UUID currentPrincipalId() {
                return getCurrentPrincipal().getPrincipal()
                                            .getId();
            }
        };
        workspaceModel = new WorkspaceModelImpl(this);
        WorkspaceScope workspaceScope = workspaceModel.getScoped(WellKnownProduct.KERNEL_WORKSPACE.id());
        if (workspaceScope == null) {
            LoggerFactory.getLogger(ModelImpl.class)
                         .error("Cannot obtain kernel workspace.  Database is not bootstrapped");
            throw new IllegalStateException("Database has not been boostrapped");
        }
        kernel = workspaceScope.getWorkspace()
                               .getAccessor(Kernel.class);
        phantasmModel = new PhantasmModelImpl(this);
        jobModel = new JobModelImpl(this);

        createMetaPerm = getKernel().getCreateMeta();
        createPerm = getKernel().getCREATE();
        readPerm = getKernel().getREAD();
        updatePerm = getKernel().getUPDATE();
        deletePerm = getKernel().getDELETE();
        applyPerm = getKernel().getAPPLY();
        removePerm = getKernel().getREMOVE();
        invokePerm = getKernel().getINVOKE();
        executeQueryPerm = getKernel().getEXECUTE_QUERY();
        loginToPerm = getKernel().getLOGIN_TO();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ExistentialRuleform, R extends Phantasm> R apply(Class<R> phantasm,
                                                                       Phantasm target) {
        PhantasmDefinition definition = cached(phantasm, this);
        return (R) definition.construct(target.getRuleform(), this,
                                        getCurrentPrincipal().getPrincipal());
    }

    @Override
    public PhantasmDefinition cached(Class<? extends Phantasm> phantasm) {
        return cached(phantasm, this);
    }

    @Override
    public <T extends ExistentialRuleform, R extends Phantasm> R cast(T source,
                                                                      Class<R> phantasm) {
        return wrap(phantasm, source);
    }

    @Override
    public boolean checkApply(UpdatableRecord<?> target) {
        return checkPermission(target, applyPerm);
    }

    @Override
    public boolean checkCreate(UpdatableRecord<?> target) {
        return checkPermission(target, createPerm);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#checkMetaCreate(org.jooq.UpdatableRecord)
     */
    @Override
    public boolean checkCreateMeta(UpdatableRecord<?> target) {
        return checkPermission(target, createMetaPerm);
    }

    @Override
    public boolean checkDelete(UpdatableRecord<?> target) {
        return checkPermission(target, deletePerm);
    }

    @Override
    public boolean checkExecuteQuery(UpdatableRecord<?> target) {
        return checkPermission(target, executeQueryPerm);
    }

    @Override
    public boolean checkInvoke(UpdatableRecord<?> target) {
        return checkPermission(target, invokePerm);
    }

    @Override
    public boolean checkLoginTo(UpdatableRecord<?> target) {
        return checkPermission(target, loginToPerm);
    }

    @Override
    public boolean checkPermission(ExistentialRuleform target,
                                   Relationship permission) {
        return checkPermission((UpdatableRecord<?>) target, permission);
    }

    @Override
    public boolean checkExistentialPermission(List<Agency> roles,
                                              ExistentialRuleform target,
                                              Relationship permission) {
        return checkPermission(roles, (UpdatableRecord<?>) target, permission);

    }

    @Override
    public boolean checkPermission(List<Agency> roles,
                                   UpdatableRecord<?> target,
                                   Relationship permission) {
        if (target == null) {
            return true;
        }
        try {
            return checkPermission(roles, permission,
                                   (UUID) AUTHORITY_HANDLE.get(target.getClass())
                                                          .bindTo(target)
                                                          .invokeExact());
        } catch (Throwable e) {
            throw new IllegalStateException(String.format("Cannot retrieve authority for %s",
                                                          target.getClass()),
                                            e);
        }
    }

    @Override
    public boolean checkPermission(UpdatableRecord<?> target,
                                   Relationship permission) {
        return checkPermission(getCurrentPrincipal().getAsserted(), target,
                               permission);
    }

    @Override
    public boolean checkRead(UpdatableRecord<?> target) {
        return checkPermission(target, readPerm);
    }

    @Override
    public boolean checkRemove(UpdatableRecord<?> target) {
        return checkPermission(target, removePerm);
    }

    @Override
    public boolean checkUpdate(UpdatableRecord<?> target) {
        return checkPermission(target, updatePerm);
    }

    @Override
    public void close() {
        try {
            create.configuration()
                  .connectionProvider()
                  .acquire()
                  .rollback();
        } catch (DataAccessException | SQLException e) {
            LoggerFactory.getLogger(ModelImpl.class)
                         .error("error rolling back transaction during model close",
                                e);
        }
        create.close();
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public <T extends ExistentialRuleform, R extends Phantasm> R construct(Class<R> phantasm,
                                                                           ExistentialDomain domain,
                                                                           String name,
                                                                           String description) throws InstantiationException {
        PhantasmDefinition definition = cached(phantasm, this);
        ExistentialRecord record = (ExistentialRecord) records().newExistential(domain);
        record.setName(name);
        record.setDescription(description);
        record.insert();
        return (R) definition.construct((ExistentialRuleform) record, this,
                                        getCurrentPrincipal().getPrincipal());
    }

    @Override
    public DSLContext create() {
        return create;
    }

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
     * @see com.chiralbehaviors.CoRE.meta.Model#flush()
     */
    @Override
    public void flush() {
        animations.flush();
    }

    @Override
    public void flushWorkspaces() {
        workspaceModel.flush();
    }

    @Override
    public Relationship getApplyPerm() {
        return applyPerm;
    }

    @Override
    public CoreInstance getCoreInstance() {
        return wrap(CoreInstance.class,
                    phantasmModel.getChild(getKernel().getCore(),
                                           factory.resolve(getKernel().getSingletonOf()
                                                                      .getInverse()),
                                           ExistentialDomain.Agency));
    }

    @Override
    public Relationship getCreatePerm() {
        return createPerm;
    }

    @Override
    public AuthorizedPrincipal getCurrentPrincipal() {
        AuthorizedPrincipal authorizedPrincipal = currentPrincipal;
        return authorizedPrincipal == null ? new AuthorizedPrincipal(kernel.getCoreAnimationSoftware())
                                           : authorizedPrincipal;
    }

    @Override
    public Relationship getDeletePerm() {
        return deletePerm;
    }

    @Override
    public Relationship getExecuteQueryPerm() {
        return executeQueryPerm;
    }

    @Override
    public Relationship getInvokePerm() {
        return invokePerm;
    }

    @Override
    public JobModel getJobModel() {
        return jobModel;
    }

    @Override
    public Kernel getKernel() {
        return kernel;
    }

    @Override
    public Relationship getLoginToPerm() {
        return loginToPerm;
    }

    @Override
    public PhantasmModel getPhantasmModel() {
        return phantasmModel;
    }

    @Override
    public Relationship getReadPerm() {
        return readPerm;
    }

    @Override
    public Relationship getRemovePerm() {
        return removePerm;
    }

    @Override
    public Relationship getUpdatePerm() {
        return updatePerm;
    }

    @Override
    public WorkspaceModel getWorkspaceModel() {
        return workspaceModel;
    }

    @Override
    public void inferNetworks() {
        animations.inferNetworks();
    }

    @Override
    public AuthorizedPrincipal principalFrom(Agency principal,
                                             List<Agency> roles) {
        return new AuthorizedPrincipal(principal, roles);
    }

    @Override
    public AuthorizedPrincipal principalFromIds(Agency principal,
                                                List<UUID> roles) {
        return new AuthorizedPrincipal(principal, roles.stream()
                                                       .map(uuid -> records().resolve(uuid))
                                                       .filter(auth -> auth != null)
                                                       .map(e -> (Agency) e)
                                                       .collect(Collectors.toList()));
    }

    @Override
    public RecordsFactory records() {
        return factory;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.Model#snapshot()
     */
    @Override
    public WorkspaceSnapshot snapshot() {
        return new StateSnapshot(create, excludeThisSingleton());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ExistentialRuleform, R extends Phantasm> R wrap(Class<R> phantasm,
                                                                      ExistentialRuleform ruleform) {
        if (ruleform == null) {
            return null;
        }
        PhantasmDefinition definition = cached(phantasm, this);
        return (R) definition.wrap(ruleform.getRuleform(), this);
    }

    private boolean checkPermission(List<Agency> agencies,
                                    Relationship permission,
                                    UUID intrinsic) throws DataAccessException,
                                                    TooManyRowsException {
        if (intrinsic == null) {
            return true;
        }
        List<UUID> roles = agencies.stream()
                                   .map(r -> r.getId())
                                   .collect(Collectors.toList());
        ExistentialNetwork membership = EXISTENTIAL_NETWORK.as(MEMBERSHIP);
        CommonTableExpression<Record1<UUID>> groups = name(GROUPS).fields(AGENCY)
                                                                  .as(create.select(membership.field(membership.CHILD))
                                                                            .from(membership)
                                                                            .where(membership.field(membership.PARENT)
                                                                                             .in(roles))
                                                                            .and(membership.field(membership.RELATIONSHIP)
                                                                                           .equal(WellKnownRelationship.MEMBER_OF.id())));

        return ZERO.equals(create.with(groups)
                                 .selectCount()
                                 .from(EXISTENTIAL)
                                 .where(EXISTENTIAL.ID.equal(intrinsic))
                                 .andNotExists(create.select(EXISTENTIAL_NETWORK.CHILD)
                                                     .from(EXISTENTIAL_NETWORK)
                                                     .where(EXISTENTIAL_NETWORK.PARENT.in(create.selectFrom(groups))
                                                                                      .or(EXISTENTIAL_NETWORK.PARENT.in(roles)))
                                                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(permission.getId()))
                                                     .and(EXISTENTIAL_NETWORK.CHILD.eq(EXISTENTIAL.ID)))
                                 .fetchOne()
                                 .value1());
    }

    private void establish(DSLContext create) {
        Configuration configuration = create.configuration();
        configuration.set(new DefaultRecordListenerProvider(animations));
        configuration.settings()
                     .setExecuteWithOptimisticLocking(true);
        TransactionProvider inner = configuration.transactionProvider();
        configuration.set(new TransactionProvider() {

            @Override
            public void begin(TransactionContext ctx) throws DataAccessException {
                animations.begin();
                inner.begin(ctx);
            }

            @Override
            public void commit(TransactionContext ctx) throws DataAccessException {
                try {
                    animations.commit();
                    inner.commit(ctx);
                } finally {
                    configuration.set(inner);
                }
            }

            @Override
            public void rollback(TransactionContext ctx) throws DataAccessException {
                configuration.set(inner);
                animations.rollback();
                inner.rollback(ctx);
            }
        });
    }

    private Collection<UUID> excludeThisSingleton() {
        List<UUID> excluded = new ArrayList<>();
        Agency instance = (Agency) getCoreInstance().getRuleform();
        excluded.add(instance.getId());

        Relationship relationship = kernel.getSingletonOf();
        excluded.add(phantasmModel.getImmediateLink(instance, relationship,
                                                    kernel.getCore())
                                  .getId());
        excluded.add(phantasmModel.getImmediateLink(kernel.getCore(),
                                                    factory.resolve(relationship.getInverse()),
                                                    instance)
                                  .getId());

        relationship = kernel.getInstanceOf();
        excluded.add(phantasmModel.getImmediateLink(instance, relationship,
                                                    kernel.getCore())
                                  .getId());
        excluded.add(phantasmModel.getImmediateLink(kernel.getCore(),
                                                    factory.resolve(relationship.getInverse()),
                                                    instance)
                                  .getId());

        relationship = kernel.getLOGIN_TO();
        excluded.add(phantasmModel.getImmediateLink(kernel.getLoginRole(),
                                                    relationship, instance)
                                  .getId());
        excluded.add(phantasmModel.getImmediateLink(instance,
                                                    factory.resolve(relationship.getInverse()),
                                                    kernel.getLoginRole())
                                  .getId());
        relationship = kernel.getInstanceOf();
        return excluded;
    }
}
