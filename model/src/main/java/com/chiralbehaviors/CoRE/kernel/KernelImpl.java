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

import java.util.UUID;

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

/**
 * Repository of immutable kernal rules
 *
 * @author hhildebrand
 *
 */
public class KernelImpl implements Kernel {

    private static final String       ZERO = UuidGenerator.toBase64(new UUID(0,
                                                                             0));
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

        attribute = find(em, WellKnownAttribute.ATTRIBUTE);
        anyAttribute = find(em, WellKnownAttribute.ANY);
        notApplicableAttribute = find(em, WellKnownAttribute.NOT_APPLICABLE);
        sameAttribute = find(em, WellKnownAttribute.SAME);
        loginAttribute = find(em, WellKnownAttribute.LOGIN);
        passwordHashAttribute = find(em, WellKnownAttribute.PASSWORD_HASH);

        product = find(em, WellKnownProduct.ENTITY);
        anyProduct = find(em, WellKnownProduct.ANY);
        sameProduct = find(em, WellKnownProduct.SAME);
        notApplicableProduct = find(em, WellKnownProduct.NOT_APPLICABLE);
        workspace = find(em, WellKnownProduct.WORKSPACE);

        location = find(em, WellKnownLocation.LOCATION);
        anyLocation = find(em, WellKnownLocation.ANY);
        notApplicableLocation = find(em, WellKnownLocation.NOT_APPLICABLE);
        sameLocation = find(em, WellKnownLocation.SAME);

        coreUser = find(em, WellKnownAgency.CORE_USER);
        agency = find(em, WellKnownAgency.AGENCY);
        anyAgency = find(em, WellKnownAgency.ANY);
        core = find(em, WellKnownAgency.CORE);
        coreAnimationSoftware = find(em,
                                     WellKnownAgency.CORE_ANIMATION_SOFTWARE);
        propagationSoftware = find(em, WellKnownAgency.PROPAGATION_SOFTWARE);
        specialSystemAgency = find(em, WellKnownAgency.SPECIAL_SYSTEM_AGENCY);
        coreModel = find(em, WellKnownAgency.CORE_MODEL);
        superUser = find(em, WellKnownAgency.SUPER_USER);
        inverseSoftware = find(em, WellKnownAgency.INVERSE_SOFTWARE);
        sameAgency = find(em, WellKnownAgency.SAME);
        notApplicableAgency = find(em, WellKnownAgency.NOT_APPLICABLE);

        anyRelationship = find(em, WellKnownRelationship.ANY);
        sameRelationship = find(em, WellKnownRelationship.SAME);
        isContainedIn = find(em, WellKnownRelationship.IS_CONTAINED_IN);
        contains = find(em, WellKnownRelationship.CONTAINS);
        isA = find(em, WellKnownRelationship.IS_A);
        includes = find(em, WellKnownRelationship.INCLUDES);
        hasException = find(em, WellKnownRelationship.HAS_EXCEPTION);
        isExceptionTo = find(em, WellKnownRelationship.IS_EXCEPTION_TO);
        isLocationOf = find(em, WellKnownRelationship.IS_LOCATION_OF);
        mapsToLocation = find(em, WellKnownRelationship.MAPS_TO_LOCATION);
        prototype = find(em, WellKnownRelationship.PROTOTYPE);
        prototypeOf = find(em, WellKnownRelationship.PROTOTYPE_OF);
        greaterThan = find(em, WellKnownRelationship.GREATER_THAN);
        lessThan = find(em, WellKnownRelationship.LESS_THAN);
        equals = find(em, WellKnownRelationship.EQUALS);
        lessThanOrEqual = find(em, WellKnownRelationship.LESS_THAN_OR_EQUAL);
        greaterThanOrEqual = find(em,
                                  WellKnownRelationship.GREATER_THAN_OR_EQUAL);
        developed = find(em, WellKnownRelationship.DEVELOPED);
        developedBy = find(em, WellKnownRelationship.DEVELOPED_BY);
        versionOf = find(em, WellKnownRelationship.VERSION_OF);
        hasVersion = find(em, WellKnownRelationship.HAS_VERSION);
        hasMember = find(em, WellKnownRelationship.HAS_MEMBER);
        memberOf = find(em, WellKnownRelationship.MEMBER_OF);
        headOf = find(em, WellKnownRelationship.HEAD_OF);
        hasHead = find(em, WellKnownRelationship.HAS_HEAD);
        hadMember = find(em, WellKnownRelationship.HAD_MEMBER);
        formerMemberOf = find(em, WellKnownRelationship.FORMER_MEMBER_OF);
        notApplicableRelationship = find(em,
                                         WellKnownRelationship.NOT_APPLICABLE);
        ownedBy = find(em, WellKnownRelationship.OWNED_BY);
        owns = find(em, WellKnownRelationship.OWNS);
        inWorkspace = find(em, WellKnownRelationship.IN_WORKSPACE);
        workspaceOf = find(em, WellKnownRelationship.WORKSPACE_OF);

        unset = find(em, WellKnownStatusCode.UNSET);
        anyStatusCode = find(em, WellKnownStatusCode.ANY);
        sameStatusCode = find(em, WellKnownStatusCode.SAME);
        notApplicableStatusCode = find(em, WellKnownStatusCode.NOT_APPLICABLE);

        unsetUnit = find(em, WellKnownUnit.UNSET);
        anyUnit = find(em, WellKnownUnit.ANY);
        sameUnit = find(em, WellKnownUnit.SAME);
        notApplicableUnit = find(em, WellKnownUnit.NOT_APPLICABLE);

        anyInterval = find(em, WellKnownInterval.ANY);
        sameInterval = find(em, WellKnownInterval.SAME);
        notApplicableInterval = find(em, WellKnownInterval.NOT_APPLICABLE);

        rootAgencyNetwork = em.find(AgencyNetwork.class, ZERO);
        rootAttributeNetwork = em.find(AttributeNetwork.class, ZERO);
        rootIntervalNetwork = em.find(IntervalNetwork.class, ZERO);
        rootLocationNetwork = em.find(LocationNetwork.class, ZERO);
        rootProductNetwork = em.find(ProductNetwork.class, ZERO);
        rootRelationshipNetwork = em.find(RelationshipNetwork.class, ZERO);
        rootStatusCodeNetwork = em.find(StatusCodeNetwork.class, ZERO);
        rootUnitNetwork = em.find(UnitNetwork.class, ZERO);
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
