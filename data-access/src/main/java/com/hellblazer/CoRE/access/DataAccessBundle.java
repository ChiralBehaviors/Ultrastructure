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
package com.hellblazer.CoRE.access;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.openjpa.persistence.OpenJPAEntityManagerFactory;

import com.hellblazer.CoRE.access.health.JpaHealthCheck;
import com.hellblazer.CoRE.access.resource.CollectionResource;
import com.hellblazer.CoRE.access.resource.CrudGuiResource;
import com.hellblazer.CoRE.access.resource.CrudResource;
import com.hellblazer.CoRE.access.resource.DomainResource;
import com.hellblazer.CoRE.access.resource.RuleformResource;
import com.hellblazer.CoRE.access.resource.TraversalResource;
import com.hellblazer.CoRE.access.resource.WorkspaceResource;
import com.hellblazer.CoRE.access.resource.ruleform.impl.AgencyResource;
import com.hellblazer.CoRE.access.resource.ruleform.impl.AttributeResource;
import com.hellblazer.CoRE.access.resource.ruleform.impl.CoordinateResource;
import com.hellblazer.CoRE.access.resource.ruleform.impl.IntervalResource;
import com.hellblazer.CoRE.access.resource.ruleform.impl.LocationResource;
import com.hellblazer.CoRE.access.resource.ruleform.impl.ProductResource;
import com.hellblazer.CoRE.access.resource.ruleform.impl.RelationshipResource;
import com.hellblazer.CoRE.access.resource.ruleform.impl.StatusCodeResource;
import com.hellblazer.CoRE.access.resource.ruleform.impl.UnitResource;
import com.hellblazer.CoRE.access.resource.ruleform.workflow.JobResource;
import com.hellblazer.CoRE.configuration.CoREServiceConfiguration;
import com.hellblazer.CoRE.configuration.JpaConfiguration;
import com.hellblazer.CoRE.meta.models.ModelImpl;
import com.yammer.dropwizard.ConfiguredBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

/**
 * @author hhildebrand
 * 
 */
public class DataAccessBundle implements
		ConfiguredBundle<CoREServiceConfiguration> {

	@Override
	public void initialize(Bootstrap<?> bootstrap) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yammer.dropwizard.ConfiguredBundle#initialize(java.lang.Object,
	 * com.yammer.dropwizard.config.Environment)
	 */
	@Override
	public void run(CoREServiceConfiguration configuration,
			Environment environment) {

		JpaConfiguration jpaConfig = configuration
				.getCrudServiceConfiguration();

		String unit = jpaConfig.getPersistenceUnit();
		Map<String, String> properties = jpaConfig.getProperties();
		properties.put("openjpa.EntityManagerFactoryPool", "true");
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(unit,
				properties);
		// necessary for polymorphic ruleform deserialization
		CoREModule module = new CoREModule();
		environment.getObjectMapperFactory().registerModule(module);
		environment.addResource(new CrudResource(emf));
		environment.addResource(new CrudGuiResource(unit));
		environment.addResource(new DomainResource(unit,
				(OpenJPAEntityManagerFactory) emf));
		environment.addResource(new TraversalResource(emf));
		environment.addHealthCheck(new JpaHealthCheck(emf));
		environment.addResource(new CollectionResource(emf));
		environment.addResource(new WorkspaceResource(emf));
		environment.addResource(new RuleformResource(emf));
		environment.addResource(new ProductResource(emf.createEntityManager()));
		environment.addResource(new AgencyResource(emf.createEntityManager()));
		environment
				.addResource(new AttributeResource(emf.createEntityManager()));
		environment.addResource(new CoordinateResource(emf
				.createEntityManager()));
		environment
				.addResource(new IntervalResource(emf.createEntityManager()));
		environment
				.addResource(new LocationResource(emf.createEntityManager()));
		environment.addResource(new RelationshipResource(emf
				.createEntityManager()));
		environment.addResource(new StatusCodeResource(emf
				.createEntityManager()));
		environment.addResource(new UnitResource(emf.createEntityManager()));
		environment.addResource(new JobResource(new ModelImpl(emf
				.createEntityManager())));

	}
}
