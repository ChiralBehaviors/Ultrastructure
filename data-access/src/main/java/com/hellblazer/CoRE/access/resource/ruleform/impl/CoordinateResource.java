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
package com.hellblazer.CoRE.access.resource.ruleform.impl;

import javax.persistence.EntityManager;
import javax.ws.rs.Path;

import com.hellblazer.CoRE.coordinate.Coordinate;
import com.hellblazer.CoRE.coordinate.CoordinateAttribute;
import com.hellblazer.CoRE.coordinate.CoordinateAttributeAuthorization;
import com.hellblazer.CoRE.coordinate.CoordinateNetwork;
import com.hellblazer.CoRE.meta.models.CoordinateModelImpl;

/**
 * @author hparry
 * 
 */
@Path("/v{version : \\d+}/services/data/ruleform/Coordinate")
public class CoordinateResource
		extends
		AbstractRuleformResource<Coordinate, CoordinateNetwork, CoordinateAttributeAuthorization, CoordinateAttribute> {

	public CoordinateResource(EntityManager em) {
		super(em, new CoordinateModelImpl(em));
	}
}
