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

package com.hellblazer.CoRE.kernel;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.event.status.StatusCode;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;

/**
 * @author hhildebrand
 * 
 */
public interface Kernel {

    Agency getAgency();

    Agency getAnyAgency();

    Attribute getAnyAttribute();

    Location getAnyLocation();

    Product getAnyProduct();

    Relationship getAnyRelationship();

    Attribute getAttribute();

    Relationship getContains();

    Agency getCore();

    Agency getCoreAnimationSoftware();

    Agency getCoreModel();

    Agency getCoreUser();

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

    Relationship getIncludes();

    Agency getInverseSoftware();

    Relationship getInWorkspace();

    Relationship getIsA();

    Relationship getIsContainedIn();

    Relationship getIsExceptionTo();

    Relationship getIsLocationOf();

    Relationship getLessThan();

    Relationship getLessThanOrEqual();

    Location getLocation();

    Attribute getLoginAttribute();

    Relationship getMapsToLocation();

    Relationship getMemberOf();

    Agency getNotApplicableAgency();

    Attribute getNotApplicableAttribute();

    Location getNotApplicableLocation();

    Product getNotApplicableProduct();

    Relationship getNotApplicableRelationship();

    Agency getOriginalAgency();

    Attribute getOriginalAttribute();

    Location getOriginalLocation();

    Product getOriginalProduct();

    Relationship getOwnedBy();

    Relationship getOwns();

    Attribute getPasswordHashAttribute();

    Product getProduct();

    Agency getPropagationSoftware();

    Relationship getPrototype();

    Relationship getPrototypeOf();

    Agency getSameAgency();

    Location getSameLocation();

    Product getSameProduct();

    Relationship getSameRelationship();

    Agency getSpecialSystemAgency();

    Agency getSuperUser();

    StatusCode getUnset();

    Relationship getVersionOf();

    Product getWorkspace();

    Relationship getWorkspaceOf();

}
