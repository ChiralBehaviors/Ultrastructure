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

package com.chiralbehaviors.CoRE.kernel;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.job.status.StatusCode;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.time.Interval;

/**
 * @author hhildebrand
 *
 */
public interface Kernel {

    Agency getAnyAgency();

    Attribute getAnyAttribute();

    Interval getAnyInterval();

    Location getAnyLocation();

    Product getAnyProduct();

    Relationship getAnyRelationship();

    StatusCode getAnyStatusCode();

    Unit getAnyUnit();

    Relationship getContains();

    Agency getCopyAgency();

    Attribute getCopyAttribute();

    Interval getCopyInterval();

    Location getCopyLocation();

    Product getCopyProduct();

    Relationship getCopyRelationship();

    StatusCode getCopyStatusCode();

    Unit getCopyUnit();

    Agency getCore();

    Agency getCoreAnimationSoftware();

    Agency getCoreModel();

    Agency getCoreUser();

    Unit getDays();

    Relationship getDeveloped();

    Relationship getDevelopedBy();

    Relationship getEquals();

    Relationship getFormerMemberOf();

    Relationship getGreaterThan();

    Relationship getGreaterThanOrEqual();

    Relationship getHadMember();

    Relationship getHasException();

    Relationship getHasHead();

    Relationship getHasMember();

    Relationship getHasVersion();

    Relationship getHeadOf();

    Unit getHours();

    Relationship getImportedBy();

    Relationship getImports();

    Relationship getIncludes();

    Agency getInverseSoftware();

    Relationship getInWorkspace();

    Attribute getIRL();

    Relationship getIsA();

    Relationship getIsContainedIn();

    Relationship getIsExceptionTo();

    Relationship getIsLocationOf();

    Relationship getIsValidatedBy();

    Attribute getJsonldType();

    Product getKernelWorkspace();

    Relationship getLessThan();

    Relationship getLessThanOrEqual();

    Attribute getLogin();

    Relationship getMapsToLocation();

    Relationship getMemberOf();

    Unit getMicroseconds();

    Unit getMilliseconds();

    Unit getMinutes();

    Attribute getNamespaceAttribute();

    Unit getNanoseconds();

    Agency getNotApplicableAgency();

    Attribute getNotApplicableAttribute();

    Interval getNotApplicableInterval();

    Location getNotApplicableLocation();

    Product getNotApplicableProduct();

    Relationship getNotApplicableRelationship();

    StatusCode getNotApplicableStatusCode();

    Unit getNotApplicableUnit();

    Relationship getOwnedBy();

    Relationship getOwns();

    Attribute getPasswordHash();

    Agency getPropagationSoftware();

    Relationship getPrototype();

    Relationship getPrototypeOf();

    Agency getSameAgency();

    Attribute getSameAttribute();

    Interval getSameInterval();

    Location getSameLocation();

    Product getSameProduct();

    Relationship getSameRelationship();

    StatusCode getSameStatusCode();

    Unit getSameUnit();

    Unit getSeconds();

    Agency getSpecialSystemAgency();

    Agency getSuperUser();

    StatusCode getUnset();

    Unit getUnsetUnit();

    Relationship getValidates();

    Relationship getVersionOf();

    Product getWorkspace();

    Relationship getWorkspaceOf();

}
