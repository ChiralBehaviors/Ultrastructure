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

import com.hellblazer.CoRE.event.status.StatusCode;
import com.hellblazer.CoRE.event.status.StatusCodeAttribute;
import com.hellblazer.CoRE.event.status.StatusCodeAttributeAuthorization;
import com.hellblazer.CoRE.event.status.StatusCodeNetwork;
import com.hellblazer.CoRE.meta.models.StatusCodeModelImpl;

/**
 * @author hparry
 * 
 */
@Path("/v{version : \\d+}/services/data/ruleform/StatusCode")
public class StatusCodeResource
        extends
        AbstractRuleformResource<StatusCode, StatusCodeNetwork, StatusCodeAttributeAuthorization, StatusCodeAttribute> {

    public StatusCodeResource(EntityManager em) {
        super(em, new StatusCodeModelImpl(em));
    }
}
