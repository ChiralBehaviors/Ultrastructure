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
package com.chiralbehaviors.CoRE.access.resource.ruleform.impl;

import javax.persistence.EntityManager;
import javax.ws.rs.Path;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyAttributeAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.agency.access.AgencyAttribute;
import com.chiralbehaviors.CoRE.meta.models.AgencyModelImpl;

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
