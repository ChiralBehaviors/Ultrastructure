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

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.agency.AgencyAttributeAuthorization;
import com.hellblazer.CoRE.agency.AgencyNetwork;
import com.hellblazer.CoRE.agency.access.AgencyAttribute;
import com.hellblazer.CoRE.meta.models.AgencyModelImpl;

/**
 * @author hparry
 *
 */
@Path("/v{version : \\d+}/services/data/ruleform/Agency")
public class AgencyResource
        extends
        AbstractRuleformResource<Agency, AgencyNetwork, AgencyAttributeAuthorization, AgencyAttribute> {

    public AgencyResource(EntityManager em) {
        super(em, new AgencyModelImpl(em));
    }
}
