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
