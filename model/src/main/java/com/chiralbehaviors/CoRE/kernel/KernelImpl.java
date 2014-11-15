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

package com.chiralbehaviors.CoRE.kernel;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import javax.persistence.EntityManager;

import com.chiralbehaviors.CoRE.UuidGenerator;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeNetwork;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.attribute.unit.UnitNetwork;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeNetwork;
import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownAgency;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownAttribute;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownInterval;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownLocation;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownProduct;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownRelationship;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownStatusCode;
import com.chiralbehaviors.CoRE.kernel.WellKnownObject.WellKnownUnit;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.network.RelationshipNetwork;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.chiralbehaviors.CoRE.time.Interval;
import com.chiralbehaviors.CoRE.time.IntervalNetwork;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Repository of immutable kernal rules
 * 
 * This used to be the standard. Now we use workspaces. However, kernel is a
 * fundamental workspace, and it's needed a lot. Consequently, because of the
 * way we do Java stored procedures, reentrancy requires a new image of the
 * kernel workspace in the context of the entity manager. Sucks to be us.
 * 
 * Anyways, this is a much faster load than the CachedWorkspace. Saves a lot of
 * overhead in txns that involve reentrant java calls - which, is like every
 * one.
 *
 * @author hhildebrand
 *
 */
public class KernelImpl implements Kernel {

    public static final String                   SELECT_TABLE              = "SELECT table_schema || '.' || table_name AS name FROM information_schema.tables WHERE table_schema='ruleform' AND table_type='BASE TABLE' ORDER BY table_name";
    public static final String                   ZERO                      = UuidGenerator.toBase64(new UUID(
                                                                                                             0,
                                                                                                             0));
    private static final AtomicReference<Kernel> CACHED_KERNEL             = new AtomicReference<>();
    static final String                          KERNEL_WORKSPACE_RESOURCE = "/kernel-workspace.json";

    public static void clear(EntityManager em) throws SQLException {
        Connection connection = em.unwrap(Connection.class);
        connection.setAutoCommit(false);
        alterTriggers(connection, false);
        ResultSet r = connection.createStatement().executeQuery(KernelImpl.SELECT_TABLE);
        while (r.next()) {
            String table = r.getString("name");
            String query = String.format("DELETE FROM %s", table);
            connection.createStatement().execute(query);
        }
        r.close();
        alterTriggers(connection, true);
        CACHED_KERNEL.set(null);
        connection.commit();
    }

    public static Kernel clearAndLoadKernel(EntityManager em)
                                                             throws SQLException,
                                                             IOException {
        clear(em);
        return loadKernel(em);
    }

    public static Kernel getKernel() {
        Kernel kernel = CACHED_KERNEL.get();
        assert kernel != null;
        return kernel;
    }

    public static Kernel loadKernel(EntityManager em) throws IOException {
        return loadKernel(em,
                          KernelImpl.class.getResourceAsStream(KernelImpl.KERNEL_WORKSPACE_RESOURCE));
    }

    public static Kernel loadKernel(EntityManager em, InputStream is)
                                                                     throws IOException {
        em.getTransaction().begin();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new CoREModule());
        WorkspaceSnapshot workspace = mapper.readValue(is,
                                                       WorkspaceSnapshot.class);
        workspace.retarget(em);
        em.getTransaction().commit();
        return cacheKernel(em);
    }

    public static KernelImpl cacheKernel(EntityManager em) {
        KernelImpl kernel = new KernelImpl(em);
        if (!CACHED_KERNEL.compareAndSet(null, kernel)) {
            throw new IllegalStateException("Kernel has already been cached");
        }
        return kernel;
    }

    static void alterTriggers(Connection connection, boolean enable)
                                                                    throws SQLException {
        for (String table : new String[] { "ruleform.agency",
                "ruleform.product", "ruleform.location" }) {
            String query = String.format("ALTER TABLE %s %s TRIGGER ALL",
                                         table, enable ? "ENABLE" : "DISABLE");
            connection.createStatement().execute(query);
        }
        ResultSet r = connection.createStatement().executeQuery(KernelImpl.SELECT_TABLE);
        while (r.next()) {
            String table = r.getString("name");
            String query = String.format("ALTER TABLE %s %s TRIGGER ALL",
                                         table, enable ? "ENABLE" : "DISABLE");
            connection.createStatement().execute(query);
        }
        r.close();
    }

    private final Agency              agency;
    private final Agency              anyAgency;
    private final Attribute           anyAttribute;
    private final Interval            anyInterval;
    private final Location            anyLocation;
    private final Product             anyProduct;
    private final Relationship        anyRelationship;
    private final StatusCode          anyStatusCode;
    private final Unit                anyUnit;
    private final Attribute           attribute;
    private final Relationship        contains;
    private final Agency              copyAgency;
    private final Attribute           copyAttribute;
    private final Interval            copyInterval;
    private final Location            copyLocation;
    private final Product             copyProduct;
    private final Relationship        copyRelationship;
    private final StatusCode          copyStatusCode;
    private final Unit                copyUnit;
    private final Agency              core;
    private final Agency              coreAnimationSoftware;
    private final Agency              coreModel;
    private final Agency              coreUser;
    private final Relationship        developed;
    private final Relationship        developedBy;
    private final Relationship        equals;
    private final Relationship        formerMemberOf;
    private final Relationship        greaterThan;
    private final Relationship        greaterThanOrEqual;
    private final Relationship        hadMember;
    private final Relationship        hasException;
    private final Relationship        hasHead;
    private final Relationship        hasMember;
    private final Relationship        hasVersion;
    private final Relationship        headOf;
    private final Relationship        includes;
    private final Agency              inverseSoftware;
    private final Relationship        inWorkspace;
    private final Relationship        isA;
    private final Relationship        isContainedIn;
    private final Relationship        isExceptionTo;
    private final Relationship        isLocationOf;
    private final Relationship        lessThan;
    private final Relationship        lessThanOrEqual;
    private final Location            location;
    private final Attribute           loginAttribute;
    private final Relationship        mapsToLocation;
    private final Relationship        memberOf;
    private final Agency              notApplicableAgency;
    private final Attribute           notApplicableAttribute;
    private final Interval            notApplicableInterval;
    private final Location            notApplicableLocation;
    private final Product             notApplicableProduct;
    private final Relationship        notApplicableRelationship;
    private final StatusCode          notApplicableStatusCode;
    private final Unit                notApplicableUnit;
    private final Relationship        ownedBy;
    private final Relationship        owns;
    private final Attribute           passwordHashAttribute;
    private final Product             product;
    private final Agency              propagationSoftware;
    private final Relationship        prototype;
    private final Relationship        prototypeOf;
    private final AgencyNetwork       rootAgencyNetwork;
    private final AttributeNetwork    rootAttributeNetwork;
    private final IntervalNetwork     rootIntervalNetwork;
    private final LocationNetwork     rootLocationNetwork;
    private final ProductNetwork      rootProductNetwork;
    private final RelationshipNetwork rootRelationshipNetwork;
    private final StatusCodeNetwork   rootStatusCodeNetwork;
    private final UnitNetwork         rootUnitNetwork;
    private final Agency              sameAgency;
    private final Attribute           sameAttribute;
    private final Interval            sameInterval;
    private final Location            sameLocation;
    private final Product             sameProduct;
    private final Relationship        sameRelationship;
    private final StatusCode          sameStatusCode;
    private final Unit                sameUnit;
    private final Agency              specialSystemAgency;

    private final Agency              superUser;

    private final StatusCode          unset;

    private final Unit                unsetUnit;

    private final Relationship        versionOf;

    private final Product             workspace;

    private final Relationship        workspaceOf;

    public KernelImpl(EntityManager em) {

        Object test = attribute = find(em, WellKnownAttribute.ATTRIBUTE);
        assert test != null;
        test = anyAttribute = find(em, WellKnownAttribute.ANY);
        assert test != null;
        test = copyAttribute = find(em, WellKnownAttribute.COPY);
        assert test != null;
        test = notApplicableAttribute = find(em,
                                             WellKnownAttribute.NOT_APPLICABLE);
        assert test != null;
        test = sameAttribute = find(em, WellKnownAttribute.SAME);
        assert test != null;
        test = loginAttribute = find(em, WellKnownAttribute.LOGIN);
        assert test != null;
        test = passwordHashAttribute = find(em,
                                            WellKnownAttribute.PASSWORD_HASH);
        assert test != null;

        test = product = find(em, WellKnownProduct.ENTITY);
        assert test != null;
        test = anyProduct = find(em, WellKnownProduct.ANY);
        assert test != null;
        test = copyProduct = find(em, WellKnownProduct.COPY);
        assert test != null;
        test = sameProduct = find(em, WellKnownProduct.SAME);
        assert test != null;
        test = notApplicableProduct = find(em, WellKnownProduct.NOT_APPLICABLE);
        assert test != null;
        test = workspace = find(em, WellKnownProduct.WORKSPACE);
        assert test != null;

        test = location = find(em, WellKnownLocation.LOCATION);
        assert test != null;
        test = anyLocation = find(em, WellKnownLocation.ANY);
        assert test != null;
        test = copyLocation = find(em, WellKnownLocation.COPY);
        assert test != null;
        test = notApplicableLocation = find(em,
                                            WellKnownLocation.NOT_APPLICABLE);
        assert test != null;
        test = sameLocation = find(em, WellKnownLocation.SAME);
        assert test != null;

        test = coreUser = find(em, WellKnownAgency.CORE_USER);
        assert test != null;
        test = agency = find(em, WellKnownAgency.AGENCY);
        assert test != null;
        test = anyAgency = find(em, WellKnownAgency.ANY);
        assert test != null;
        test = copyAgency = find(em, WellKnownAgency.COPY);
        assert test != null;
        test = core = find(em, WellKnownAgency.CORE);
        assert test != null;
        test = coreAnimationSoftware = find(em,
                                            WellKnownAgency.CORE_ANIMATION_SOFTWARE);
        assert test != null;
        test = propagationSoftware = find(em,
                                          WellKnownAgency.PROPAGATION_SOFTWARE);
        assert test != null;
        test = specialSystemAgency = find(em,
                                          WellKnownAgency.SPECIAL_SYSTEM_AGENCY);
        assert test != null;
        test = coreModel = find(em, WellKnownAgency.CORE_MODEL);
        assert test != null;
        test = superUser = find(em, WellKnownAgency.SUPER_USER);
        assert test != null;
        test = inverseSoftware = find(em, WellKnownAgency.INVERSE_SOFTWARE);
        assert test != null;
        test = sameAgency = find(em, WellKnownAgency.SAME);
        assert test != null;
        test = notApplicableAgency = find(em, WellKnownAgency.NOT_APPLICABLE);
        assert test != null;

        test = anyRelationship = find(em, WellKnownRelationship.ANY);
        assert test != null;
        test = copyRelationship = find(em, WellKnownRelationship.COPY);
        assert test != null;
        test = sameRelationship = find(em, WellKnownRelationship.SAME);
        assert test != null;
        test = isContainedIn = find(em, WellKnownRelationship.IS_CONTAINED_IN);
        assert test != null;
        test = contains = find(em, WellKnownRelationship.CONTAINS);
        assert test != null;
        test = isA = find(em, WellKnownRelationship.IS_A);
        assert test != null;
        test = includes = find(em, WellKnownRelationship.INCLUDES);
        assert test != null;
        test = hasException = find(em, WellKnownRelationship.HAS_EXCEPTION);
        assert test != null;
        test = isExceptionTo = find(em, WellKnownRelationship.IS_EXCEPTION_TO);
        assert test != null;
        test = isLocationOf = find(em, WellKnownRelationship.IS_LOCATION_OF);
        assert test != null;
        test = mapsToLocation = find(em, WellKnownRelationship.MAPS_TO_LOCATION);
        assert test != null;
        test = prototype = find(em, WellKnownRelationship.PROTOTYPE);
        assert test != null;
        test = prototypeOf = find(em, WellKnownRelationship.PROTOTYPE_OF);
        assert test != null;
        test = greaterThan = find(em, WellKnownRelationship.GREATER_THAN);
        assert test != null;
        test = lessThan = find(em, WellKnownRelationship.LESS_THAN);
        assert test != null;
        test = equals = find(em, WellKnownRelationship.EQUALS);
        assert test != null;
        test = lessThanOrEqual = find(em,
                                      WellKnownRelationship.LESS_THAN_OR_EQUAL);
        assert test != null;
        test = greaterThanOrEqual = find(em,
                                         WellKnownRelationship.GREATER_THAN_OR_EQUAL);
        assert test != null;
        test = developed = find(em, WellKnownRelationship.DEVELOPED);
        assert test != null;
        test = developedBy = find(em, WellKnownRelationship.DEVELOPED_BY);
        assert test != null;
        test = versionOf = find(em, WellKnownRelationship.VERSION_OF);
        assert test != null;
        test = hasVersion = find(em, WellKnownRelationship.HAS_VERSION);
        assert test != null;
        test = hasMember = find(em, WellKnownRelationship.HAS_MEMBER);
        assert test != null;
        test = memberOf = find(em, WellKnownRelationship.MEMBER_OF);
        assert test != null;
        test = headOf = find(em, WellKnownRelationship.HEAD_OF);
        assert test != null;
        test = hasHead = find(em, WellKnownRelationship.HAS_HEAD);
        assert test != null;
        test = hadMember = find(em, WellKnownRelationship.HAD_MEMBER);
        assert test != null;
        test = formerMemberOf = find(em, WellKnownRelationship.FORMER_MEMBER_OF);
        assert test != null;
        test = notApplicableRelationship = find(em,
                                                WellKnownRelationship.NOT_APPLICABLE);
        assert test != null;
        test = ownedBy = find(em, WellKnownRelationship.OWNED_BY);
        assert test != null;
        test = owns = find(em, WellKnownRelationship.OWNS);
        assert test != null;
        test = inWorkspace = find(em, WellKnownRelationship.IN_WORKSPACE);
        assert test != null;
        test = workspaceOf = find(em, WellKnownRelationship.WORKSPACE_OF);
        assert test != null;

        test = unset = find(em, WellKnownStatusCode.UNSET);
        assert test != null;
        test = anyStatusCode = find(em, WellKnownStatusCode.ANY);
        assert test != null;
        test = copyStatusCode = find(em, WellKnownStatusCode.COPY);
        assert test != null;
        test = sameStatusCode = find(em, WellKnownStatusCode.SAME);
        assert test != null;
        test = notApplicableStatusCode = find(em,
                                              WellKnownStatusCode.NOT_APPLICABLE);
        assert test != null;

        test = unsetUnit = find(em, WellKnownUnit.UNSET);
        assert test != null;
        test = anyUnit = find(em, WellKnownUnit.ANY);
        assert test != null;
        test = copyUnit = find(em, WellKnownUnit.COPY);
        assert test != null;
        test = sameUnit = find(em, WellKnownUnit.SAME);
        assert test != null;
        test = notApplicableUnit = find(em, WellKnownUnit.NOT_APPLICABLE);
        assert test != null;

        test = anyInterval = find(em, WellKnownInterval.ANY);
        assert test != null;
        test = copyInterval = find(em, WellKnownInterval.COPY);
        assert test != null;
        test = sameInterval = find(em, WellKnownInterval.SAME);
        assert test != null;
        test = notApplicableInterval = find(em,
                                            WellKnownInterval.NOT_APPLICABLE);

        test = rootAgencyNetwork = em.find(AgencyNetwork.class, ZERO);
        assert test != null;
        test = rootAttributeNetwork = em.find(AttributeNetwork.class, ZERO);
        assert test != null;
        test = rootIntervalNetwork = em.find(IntervalNetwork.class, ZERO);
        assert test != null;
        test = rootLocationNetwork = em.find(LocationNetwork.class, ZERO);
        assert test != null;
        test = rootProductNetwork = em.find(ProductNetwork.class, ZERO);
        assert test != null;
        test = rootRelationshipNetwork = em.find(RelationshipNetwork.class,
                                                 ZERO);
        assert test != null;
        test = rootStatusCodeNetwork = em.find(StatusCodeNetwork.class, ZERO);
        assert test != null;
        test = rootUnitNetwork = em.find(UnitNetwork.class, ZERO);
        assert test != null;
        detach(em);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getAgency()
     */
    @Override
    public Agency getAgency() {
        return agency;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getAnyAgency()
     */
    @Override
    public Agency getAnyAgency() {
        return anyAgency;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getAnyAttribute()
     */
    @Override
    public Attribute getAnyAttribute() {
        return anyAttribute;
    }

    @Override
    public Interval getAnyInterval() {
        return anyInterval;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getAnyLocation()
     */
    @Override
    public Location getAnyLocation() {
        return anyLocation;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getAnyProduct()
     */
    @Override
    public Product getAnyProduct() {
        return anyProduct;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getAnyRelationship()
     */
    @Override
    public Relationship getAnyRelationship() {
        return anyRelationship;
    }

    @Override
    public StatusCode getAnyStatusCode() {
        return anyStatusCode;
    }

    @Override
    public Unit getAnyUnit() {
        return anyUnit;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getAttribute()
     */
    @Override
    public Attribute getAttribute() {
        return attribute;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getContains()
     */
    @Override
    public Relationship getContains() {
        return contains;
    }

    @Override
    public Agency getCopyAgency() {
        return copyAgency;
    }

    @Override
    public Attribute getCopyAttribute() {
        return copyAttribute;
    }

    @Override
    public Interval getCopyInterval() {
        return copyInterval;
    }

    @Override
    public Location getCopyLocation() {
        return copyLocation;
    }

    @Override
    public Product getCopyProduct() {
        return copyProduct;
    }

    @Override
    public Relationship getCopyRelationship() {
        return copyRelationship;
    }

    @Override
    public StatusCode getCopyStatusCode() {
        return copyStatusCode;
    }

    @Override
    public Unit getCopyUnit() {
        return copyUnit;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getCore()
     */
    @Override
    public Agency getCore() {
        return core;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.models.Kernel#getCoreAnimationProcedure()
     */
    @Override
    public Agency getCoreAnimationSoftware() {
        return coreAnimationSoftware;
    }

    @Override
    public Agency getCoreModel() {
        return coreModel;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getCoreUser()
     */
    @Override
    public Agency getCoreUser() {
        return coreUser;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getDeveloped()
     */
    @Override
    public Relationship getDeveloped() {
        return developed;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getDevelopedBy()
     */
    @Override
    public Relationship getDevelopedBy() {
        return developedBy;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getEquals()
     */
    @Override
    public Relationship getEquals() {
        return equals;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getFormerMemberOf()
     */
    @Override
    public Relationship getFormerMemberOf() {
        return formerMemberOf;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getGreaterThan()
     */
    @Override
    public Relationship getGreaterThan() {
        return greaterThan;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getGreaterThanOrEqual()
     */
    @Override
    public Relationship getGreaterThanOrEqual() {
        return greaterThanOrEqual;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getHadMember()
     */
    @Override
    public Relationship getHadMember() {
        return hadMember;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getHasException()
     */
    @Override
    public Relationship getHasException() {
        return hasException;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getHasHead()
     */
    @Override
    public Relationship getHasHead() {
        return hasHead;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getHasMember()
     */
    @Override
    public Relationship getHasMember() {
        return hasMember;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getHasVersion()
     */
    @Override
    public Relationship getHasVersion() {
        return hasVersion;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getHeadOf()
     */
    @Override
    public Relationship getHeadOf() {
        return headOf;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getIncludes()
     */
    @Override
    public Relationship getIncludes() {
        return includes;
    }

    @Override
    public Agency getInverseSoftware() {
        return inverseSoftware;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.kernel.Kernel#getInWorkspace()
     */
    @Override
    public Relationship getInWorkspace() {
        return inWorkspace;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getIsA()
     */
    @Override
    public Relationship getIsA() {
        return isA;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getIsContainedIn()
     */
    @Override
    public Relationship getIsContainedIn() {
        return isContainedIn;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getIsExceptionTo()
     */
    @Override
    public Relationship getIsExceptionTo() {
        return isExceptionTo;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getIsLocationOf()
     */
    @Override
    public Relationship getIsLocationOf() {
        return isLocationOf;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getLessThan()
     */
    @Override
    public Relationship getLessThan() {
        return lessThan;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getLessThanOrEqual()
     */
    @Override
    public Relationship getLessThanOrEqual() {
        return lessThanOrEqual;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getLocation()
     */
    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public Attribute getLoginAttribute() {
        return loginAttribute;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getMapsToLocation()
     */
    @Override
    public Relationship getMapsToLocation() {
        return mapsToLocation;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getMemberOf()
     */
    @Override
    public Relationship getMemberOf() {
        return memberOf;
    }

    /**
     * @return the notApplicableAgency
     */
    @Override
    public Agency getNotApplicableAgency() {
        return notApplicableAgency;
    }

    /**
     * @return the notApplicableAttribute
     */
    @Override
    public Attribute getNotApplicableAttribute() {
        return notApplicableAttribute;
    }

    @Override
    public Interval getNotApplicableInterval() {
        return notApplicableInterval;
    }

    /**
     * @return the notApplicableLocation
     */
    @Override
    public Location getNotApplicableLocation() {
        return notApplicableLocation;
    }

    /**
     * @return the notApplicableProduct
     */
    @Override
    public Product getNotApplicableProduct() {
        return notApplicableProduct;
    }

    /**
     * @return the notApplicableRelationship
     */
    @Override
    public Relationship getNotApplicableRelationship() {
        return notApplicableRelationship;
    }

    @Override
    public StatusCode getNotApplicableStatusCode() {
        return notApplicableStatusCode;
    }

    @Override
    public Unit getNotApplicableUnit() {
        return notApplicableUnit;
    }

    /**
     * @return the ownedBy
     */
    @Override
    public Relationship getOwnedBy() {
        return ownedBy;
    }

    /**
     * @return the owns
     */
    @Override
    public Relationship getOwns() {
        return owns;
    }

    @Override
    public Attribute getPasswordHashAttribute() {
        return passwordHashAttribute;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getProduct()
     */
    @Override
    public Product getProduct() {
        return product;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getPropagationSoftware()
     */
    @Override
    public Agency getPropagationSoftware() {
        return propagationSoftware;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getPrototype()
     */
    @Override
    public Relationship getPrototype() {
        return prototype;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getPrototypeOf()
     */
    @Override
    public Relationship getPrototypeOf() {
        return prototypeOf;
    }

    /**
     * @return the rootAgencyNetwork
     */
    @Override
    public AgencyNetwork getRootAgencyNetwork() {
        return rootAgencyNetwork;
    }

    /**
     * @return the rootAttributeNetwork
     */
    @Override
    public AttributeNetwork getRootAttributeNetwork() {
        return rootAttributeNetwork;
    }

    /**
     * @return the rootIntervalNetwork
     */
    @Override
    public IntervalNetwork getRootIntervalNetwork() {
        return rootIntervalNetwork;
    }

    /**
     * @return the rootLocationNetwork
     */
    @Override
    public LocationNetwork getRootLocationNetwork() {
        return rootLocationNetwork;
    }

    /**
     * @return the rootProductNetwork
     */
    @Override
    public ProductNetwork getRootProductNetwork() {
        return rootProductNetwork;
    }

    /**
     * @return the rootRelationshipNetwork
     */
    @Override
    public RelationshipNetwork getRootRelationshipNetwork() {
        return rootRelationshipNetwork;
    }

    /**
     * @return the rootStatusCodeNetwork
     */
    @Override
    public StatusCodeNetwork getRootStatusCodeNetwork() {
        return rootStatusCodeNetwork;
    }

    /**
     * @return the rootUnitNetwork
     */
    @Override
    public UnitNetwork getRootUnitNetwork() {
        return rootUnitNetwork;
    }

    /**
     * @return the sameAgency
     */
    @Override
    public Agency getSameAgency() {
        return sameAgency;
    }

    @Override
    public Attribute getSameAttribute() {
        return sameAttribute;
    }

    @Override
    public Interval getSameInterval() {
        return sameInterval;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.Kernel#getSameLocation()
     */
    @Override
    public Location getSameLocation() {
        return sameLocation;
    }

    /**
     * @return the sameProduct
     */
    @Override
    public Product getSameProduct() {
        return sameProduct;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getSameRelationship()
     */
    @Override
    public Relationship getSameRelationship() {
        return sameRelationship;
    }

    @Override
    public StatusCode getSameStatusCode() {
        return sameStatusCode;
    }

    @Override
    public Unit getSameUnit() {
        return sameUnit;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getSpecialSystemAgency()
     */
    @Override
    public Agency getSpecialSystemAgency() {
        return specialSystemAgency;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.Kernel#getSuperUser()
     */
    @Override
    public Agency getSuperUser() {
        return superUser;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.Kernel#getUnset()
     */
    @Override
    public StatusCode getUnset() {
        return unset;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.kernel.Kernel#getUnsetUnit()
     */
    @Override
    public Unit getUnsetUnit() {
        return unsetUnit;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.models.Kernel#getVersionOf()
     */
    @Override
    public Relationship getVersionOf() {
        return versionOf;
    }

    @Override
    public Product getWorkspace() {
        return workspace;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.kernel.Kernel#getWorkspaceOf()
     */
    @Override
    public Relationship getWorkspaceOf() {
        return workspaceOf;
    }

    private void detach(EntityManager em) {
        for (Field field : KernelImpl.class.getDeclaredFields()) {
            try {
                em.detach(field.get(this));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new IllegalStateException(
                                                String.format("Cannot detach %s",
                                                              field.getName()),
                                                e);
            }
        }
    }

    /**
     *
     * @param wko
     * @return the {@link Agency} corresponding to the well known object
     */
    Agency find(EntityManager em, WellKnownAgency wko) {
        return em.find(Agency.class, wko.id());
    }

    /**
     *
     * @param wko
     * @return the {@link Attribute} corresponding to the well known object
     */
    Attribute find(EntityManager em, WellKnownAttribute wko) {
        return em.find(Attribute.class, wko.id());
    }

    Interval find(EntityManager em, WellKnownInterval wko) {
        return em.find(Interval.class, wko.id());
    }

    /**
     *
     * @param wko
     * @return the {@link Location} corresponding to the well known object
     */
    Location find(EntityManager em, WellKnownLocation wko) {
        return em.find(Location.class, wko.id());
    }

    /**
     *
     * @param wko
     * @return the {@link Product} corresponding to the well known object
     */
    Product find(EntityManager em, WellKnownProduct wko) {
        return em.find(Product.class, wko.id());
    }

    /**
     *
     * @param wko
     * @return the {@link Relationship} corresponding to the well known object
     */
    Relationship find(EntityManager em, WellKnownRelationship wko) {
        return em.find(Relationship.class, wko.id());
    }

    /**
     *
     * @param wko
     * @return the {@link StatusCode} corresponding to the well known object
     */
    StatusCode find(EntityManager em, WellKnownStatusCode wko) {
        return em.find(StatusCode.class, wko.id());
    }

    Unit find(EntityManager em, WellKnownUnit wko) {
        return em.find(Unit.class, wko.id());
    }
}
