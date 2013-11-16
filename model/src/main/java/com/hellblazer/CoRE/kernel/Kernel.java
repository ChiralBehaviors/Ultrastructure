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

import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.event.StatusCode;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.resource.Resource;

/**
 * @author hhildebrand
 * 
 */
public interface Kernel {

    Attribute getAnyAttribute();

    Location getAnyLocation();

    Product getAnyProduct();

    Relationship getAnyRelationship();

    Resource getAnyResource();

    Attribute getAttribute();

    Relationship getContains();

    Resource getCore();

    Resource getCoreAnimationSoftware();

    Resource getCoreModel();

    Resource getCoreUser();

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

    Resource getInverseSoftware();

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

    Attribute getNotApplicableAttribute();

    Location getNotApplicableLocation();

    Product getNotApplicableProduct();

    Relationship getNotApplicableRelationship();

    Resource getNotApplicableResource();

    Attribute getOriginalAttribute();

    Location getOriginalLocation();

    Product getOriginalProduct();

    Resource getOriginalResource();

    Relationship getOwnedBy();

    Relationship getOwns();

    Attribute getPasswordHashAttribute();

    Product getProduct();

    Resource getPropagationSoftware();

    Relationship getPrototype();

    Relationship getPrototypeOf();

    Resource getResource();

    Location getSameLocation();

    Product getSameProduct();

    Relationship getSameRelationship();

    Resource getSameResource();

    Resource getSpecialSystemResource();

    Resource getSuperUser();

    StatusCode getUnset();

    Relationship getVersionOf();

	Product getWorkspace();

}
