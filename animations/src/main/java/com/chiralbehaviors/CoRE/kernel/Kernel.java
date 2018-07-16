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

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Interval;
import com.chiralbehaviors.CoRE.domain.Location;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.domain.StatusCode;
import com.chiralbehaviors.CoRE.domain.Unit;

/**
 * @author hhildebrand
 *
 */
public interface Kernel {

    Agency getAnyAgency();

    Interval getAnyInterval();

    Location getAnyLocation();

    Product getAnyProduct();

    Relationship getAnyRelationship();

    StatusCode getAnyStatusCode();

    Unit getAnyUnit();

    Relationship getAPPLY();

    Relationship getCapability();

    Product getCodeSource();

    Relationship getContains();

    Agency getCopyAgency();

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

    Relationship getCREATE();

    Relationship getCreateMeta();

    Unit getDays();

    Relationship getDELETE();

    Relationship getDeveloped();

    Relationship getDevelopedBy();

    Relationship getEquals();

    Relationship getEXECUTE_QUERY();

    Relationship getFormerMemberOf();

    Relationship getGreaterThan();

    Relationship getGreaterThanOrEqual();

    Relationship getHadMember();

    Relationship getHasArgument();

    Relationship getHasCodeSource();

    Relationship getHasConstructor();

    Relationship getHasException();

    Relationship getHasHead();

    Relationship getHasMember();

    Relationship getHasMethod();

    Relationship getHasVersion();

    Relationship getHeadOf();

    Unit getHours();

    Relationship getImportedBy();

    Relationship getImports();

    Relationship getIncludes();

    Relationship getInstanceOf();

    Agency getInverseSoftware();

    Relationship getINVOKE();

    Relationship getInWorkspace();

    Relationship getIsA();

    Relationship getIsContainedIn();

    Relationship getIsExceptionTo();

    Relationship getIsLocationOf();

    Relationship getIsValidatedBy();

    Product getKernelWorkspace();

    Relationship getLessThan();

    Relationship getLessThanOrEqual();

    Relationship getLOGIN_TO();

    Agency getLoginRole();

    Relationship getMapsToLocation();

    Relationship getMemberOf();

    Unit getMicroseconds();

    Unit getMilliseconds();

    Unit getMinutes();

    Unit getNanoseconds();

    Agency getNotApplicableAgency();

    Interval getNotApplicableInterval();

    Location getNotApplicableLocation();

    Product getNotApplicableProduct();

    Relationship getNotApplicableRelationship();

    StatusCode getNotApplicableStatusCode();

    Unit getNotApplicableUnit();

    Relationship getOwnedBy();

    Relationship getOwns();

    Agency getPropagationSoftware();

    Relationship getPrototype();

    Relationship getPrototypeOf();

    Relationship getREAD();

    Relationship getREMOVE();

    Agency getRole();

    Agency getSameAgency();

    Interval getSameInterval();

    Location getSameLocation();

    Product getSameProduct();

    Relationship getSameRelationship();

    StatusCode getSameStatusCode();

    Unit getSameUnit();

    Unit getSeconds();

    Relationship getSingletonOf();

    Agency getSpecialSystemAgency();

    Agency getSuperUser();

    Agency getUnauthenticatedAgency();

    StatusCode getUnset();

    Unit getUnsetUnit();

    Relationship getUPDATE();

    Relationship getValidates();

    Relationship getVersionOf();

    Product getWorkspace();

    Relationship getWorkspaceOf();

}
