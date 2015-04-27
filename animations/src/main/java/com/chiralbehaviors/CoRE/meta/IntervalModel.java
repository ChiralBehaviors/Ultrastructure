/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.meta;

import java.math.BigDecimal;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.time.Interval;
import com.chiralbehaviors.CoRE.time.IntervalAttribute;
import com.chiralbehaviors.CoRE.time.IntervalAttributeAuthorization;
import com.chiralbehaviors.CoRE.time.IntervalNetwork;

/**
 * @author hhildebrand
 *
 */
public interface IntervalModel
        extends
        NetworkedModel<Interval, IntervalNetwork, IntervalAttributeAuthorization, IntervalAttribute> {

    Interval create(String name, String description, BigDecimal start,
                    Unit startUnit, Aspect<Interval> aspect, Agency updatedBy,
                    @SuppressWarnings("unchecked") Aspect<Interval>... aspects);

    Interval create(String name, String description, BigDecimal start,
                    Unit startUnit, BigDecimal duration, Unit durationUnit,
                    Aspect<Interval> aspect, Agency updatedBy,
                    @SuppressWarnings("unchecked") Aspect<Interval>... aspects);

    Interval newDefaultInterval(String name, String description);

}
