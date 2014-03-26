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
package com.chiralbehaviors.CoRE.access.resource.ruleform;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.attribute.ClassifiedAttributeAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;

/**
 * @author hparry
 * 
 */
public interface ExistentialRuleformResource<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>, AttributeAuthorization extends ClassifiedAttributeAuthorization<RuleForm>, AttributeType extends AttributeValue<RuleForm>> {

	@Path("/")
	@GET
	@Produces({ MediaType.APPLICATION_JSON, "text/json" })
	List<RuleForm> getAll() throws ClassNotFoundException;

	@Path("/{id}/networks")
	@GET
	@Produces({ MediaType.APPLICATION_JSON, "text/json" })
	Collection<Network> getNetworks(@PathParam("id") long id);

	@Path("/{id}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON, "text/json" })
	RuleForm getResource(@PathParam("id") long id)
			throws ClassNotFoundException;

	@Path("/")
	@PUT
	long insert(RuleForm rf);

}