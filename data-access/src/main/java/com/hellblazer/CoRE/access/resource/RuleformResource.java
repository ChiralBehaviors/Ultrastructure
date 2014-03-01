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
package com.hellblazer.CoRE.access.resource;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.reflections.Reflections;

import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.Ruleform;

/**
 * @author hparry
 * 
 */
@Path("/v{version : \\d+}/services/data/ruleform")
public class RuleformResource {

	protected EntityManager em;
	private final Map<String, Class<? extends Ruleform>> entityMap = new HashMap<String, Class<? extends Ruleform>>();

	public RuleformResource(EntityManagerFactory emf) {
		Reflections reflections = new Reflections(Ruleform.class.getPackage()
				.getName());
		for (@SuppressWarnings("rawtypes")
		Class<? extends ExistentialRuleform> form : reflections
				.getSubTypesOf(ExistentialRuleform.class)) {
			if (!Modifier.isAbstract(form.getModifiers())) {
				Class<?> prev = entityMap.put(form.getSimpleName(), form);
				assert prev == null : String.format(
						"Found previous mapping %s of: %s", prev, form);
			}
		}
		em = emf.createEntityManager();
	}

	@GET
	@Path("/")
	@Produces({ MediaType.APPLICATION_JSON, "text/json" })
	public Set<String> getRuleformTypes() {
		return entityMap.keySet();
	}

}
