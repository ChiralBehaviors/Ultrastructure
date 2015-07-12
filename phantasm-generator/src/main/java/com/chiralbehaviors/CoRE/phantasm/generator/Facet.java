/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.phantasm.generator;

import java.util.Map;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.job.status.StatusCode;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspacePresentation;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.time.Interval;

/**
 * @author hhildebrand
 *
 */
public interface Facet {
    Facet ANY_AGENCY       = new AnyFacet(Agency.class);
    Facet ANY_ATTRIBUTE    = new AnyFacet(Attribute.class);
    Facet ANY_INTERVAL     = new AnyFacet(Interval.class);
    Facet ANY_LOCATION     = new AnyFacet(Location.class);
    Facet ANY_PRODUCT      = new AnyFacet(Product.class);
    Facet ANY_RELATIONSHIP = new AnyFacet(Relationship.class);
    Facet ANY_STATUS_CODE  = new AnyFacet(StatusCode.class);
    Facet ANY_UNIT         = new AnyFacet(Unit.class);

    String getClassName();

    String getImport();

    String getPackageName();

    String getParameterName();

    void resolve(Map<FacetKey, Facet> facets, WorkspacePresentation presentation,
                 Map<ScopedName, MappedAttribute> mapped);

}