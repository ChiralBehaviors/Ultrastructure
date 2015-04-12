package com.chiralbehaviors.CoRE;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeNetwork;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.attribute.unit.UnitNetwork;
import com.chiralbehaviors.CoRE.event.Job;
import com.chiralbehaviors.CoRE.event.ProductChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductSelfSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductSiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeNetwork;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.network.NetworkInference;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.network.RelationshipNetwork;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.chiralbehaviors.CoRE.time.Interval;
import com.chiralbehaviors.CoRE.time.IntervalNetwork;

/**
 * (C) Copyright 2015 Chiral Behaviors, LLC. All Rights Reserved
 *

 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @author hhildebrand
 *
 */
public interface Triggers {
    void delete(Agency a);

    void delete(AgencyNetwork a);

    void delete(Attribute a);

    void delete(AttributeNetwork a);

    void delete(Interval i);

    void delete(IntervalNetwork i);

    void delete(Location l);

    void delete(LocationNetwork l);

    void delete(NetworkInference inference);

    void delete(Product p);

    void delete(ProductNetwork p);

    void delete(Relationship r);

    void delete(RelationshipNetwork r);

    void delete(StatusCode s);

    void delete(StatusCodeNetwork s);

    void delete(Unit u);

    void delete(UnitNetwork u);

    void persist(AgencyNetwork a);

    void persist(AttributeNetwork a);

    void persist(IntervalNetwork i);

    void persist(Job j);

    void persist(LocationNetwork l);

    void persist(ProductChildSequencingAuthorization pcsa);

    void persist(ProductNetwork p);

    void persist(ProductParentSequencingAuthorization ppsa);

    void persist(ProductSelfSequencingAuthorization pssa);

    void persist(ProductSiblingSequencingAuthorization pssa);

    void persist(RelationshipNetwork r);

    void persist(StatusCodeNetwork sc);

    void persist(StatusCodeSequencing scs);

    void persist(UnitNetwork u);

    void update(Job j);
}
