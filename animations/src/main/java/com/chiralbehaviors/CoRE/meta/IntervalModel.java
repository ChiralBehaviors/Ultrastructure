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
package com.chiralbehaviors.CoRE.meta;

import java.math.BigDecimal;

import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Facet;
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

    Facet<Interval, IntervalAttribute> create(String name,
                                              String description,
                                              BigDecimal start,
                                              Unit startUnit,
                                              Aspect<Interval> aspect,
                                              @SuppressWarnings("unchecked") Aspect<Interval>... aspects);

    Facet<Interval, IntervalAttribute> create(String name,
                                              String description,
                                              BigDecimal start,
                                              Unit startUnit,
                                              BigDecimal duration,
                                              Unit durationUnit,
                                              Aspect<Interval> aspect,
                                              @SuppressWarnings("unchecked") Aspect<Interval>... aspects);

    Interval newDefaultInterval(String name, String description);

}
