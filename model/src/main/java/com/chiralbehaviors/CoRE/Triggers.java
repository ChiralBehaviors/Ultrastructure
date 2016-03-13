package com.chiralbehaviors.CoRE;

import com.chiralbehaviors.CoRE.existential.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.existential.domain.Agency;
import com.chiralbehaviors.CoRE.existential.domain.Attribute;
import com.chiralbehaviors.CoRE.existential.domain.Interval;
import com.chiralbehaviors.CoRE.existential.domain.Location;
import com.chiralbehaviors.CoRE.existential.domain.Product;
import com.chiralbehaviors.CoRE.existential.domain.Relationship;
import com.chiralbehaviors.CoRE.existential.domain.StatusCode;
import com.chiralbehaviors.CoRE.existential.domain.Unit;
import com.chiralbehaviors.CoRE.existential.network.NetworkInference;
import com.chiralbehaviors.CoRE.job.ChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.job.Job;
import com.chiralbehaviors.CoRE.job.ParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.job.SelfSequencingAuthorization;
import com.chiralbehaviors.CoRE.job.SiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.job.StatusCodeSequencing;

/**
 * (C) Copyright 2015 Chiral Behaviors, LLC. All Rights Reserved
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

/**
 * @author hhildebrand
 *
 */
public interface Triggers {
    void delete(Agency a);

    void delete(Attribute a);

    void delete(Interval i);

    void delete(Location l);

    void delete(NetworkInference inference);

    void delete(Product p);

    void delete(Relationship r);

    void delete(StatusCode s);

    void delete(Unit u);

    void persist(ChildSequencingAuthorization pcsa);

    void persist(Job j);

    void persist(ParentSequencingAuthorization ppsa);

    void persist(SelfSequencingAuthorization pssa);

    void persist(SiblingSequencingAuthorization pssa);

    void persist(StatusCodeSequencing statusCodeSequencing);

    <T extends AttributeValue<?>> void persist(T value);

    void update(Job j);
}
