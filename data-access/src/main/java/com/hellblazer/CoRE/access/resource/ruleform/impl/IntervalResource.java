/**
 * Copyright (C) 2014 Halloran Parry. All rights reserved.
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
package com.hellblazer.CoRE.access.resource.ruleform.impl;

import javax.persistence.EntityManager;
import javax.ws.rs.Path;

import com.hellblazer.CoRE.meta.models.IntervalModelImpl;
import com.hellblazer.CoRE.time.Interval;
import com.hellblazer.CoRE.time.IntervalAttribute;
import com.hellblazer.CoRE.time.IntervalAttributeAuthorization;
import com.hellblazer.CoRE.time.IntervalNetwork;

/**
 * @author hparry
 *
 */
@Path("/v{version : \\d+}/services/data/ruleform/Interval")
public class IntervalResource
        extends
        AbstractRuleformResource<Interval, IntervalNetwork, IntervalAttributeAuthorization, IntervalAttribute> {
    
    public IntervalResource(EntityManager em) {
        super(em, new IntervalModelImpl(em));
    }
}
