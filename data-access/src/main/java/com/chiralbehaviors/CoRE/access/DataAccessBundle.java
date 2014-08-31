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
package com.chiralbehaviors.CoRE.access;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.openjpa.persistence.OpenJPAEntityManagerFactory;

import com.chiralbehaviors.CoRE.access.health.JpaHealthCheck;
import com.chiralbehaviors.CoRE.access.resource.CollectionResource;
import com.chiralbehaviors.CoRE.access.resource.CrudGuiResource;
import com.chiralbehaviors.CoRE.access.resource.CrudResource;
import com.chiralbehaviors.CoRE.access.resource.DomainResource;
import com.chiralbehaviors.CoRE.access.resource.RuleformResource;
import com.chiralbehaviors.CoRE.access.resource.TraversalResource;
import com.chiralbehaviors.CoRE.access.resource.WorkspaceResource;
import com.chiralbehaviors.CoRE.access.resource.ruleform.impl.AgencyResource;
import com.chiralbehaviors.CoRE.access.resource.ruleform.impl.AttributeResource;
import com.chiralbehaviors.CoRE.access.resource.ruleform.impl.IntervalResource;
import com.chiralbehaviors.CoRE.access.resource.ruleform.impl.LocationResource;
import com.chiralbehaviors.CoRE.access.resource.ruleform.impl.ProductResource;
import com.chiralbehaviors.CoRE.access.resource.ruleform.impl.RelationshipResource;
import com.chiralbehaviors.CoRE.access.resource.ruleform.impl.StatusCodeResource;
import com.chiralbehaviors.CoRE.access.resource.ruleform.impl.UnitResource;
import com.chiralbehaviors.CoRE.access.resource.ruleform.workflow.JobResource;
import com.chiralbehaviors.CoRE.configuration.CoREServiceConfiguration;
import com.chiralbehaviors.CoRE.configuration.JpaConfiguration;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;

/**
 * @author hhildebrand
 *
 */
public class DataAccessBundle implements
        ConfiguredBundle<CoREServiceConfiguration> {

    private EntityManagerFactory emf;

    public EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    /*
     * (non-Javadoc)
     *
     * @see io.dropwizard.ConfiguredBundle#initialize(java.lang.Object,
     * io.dropwizard.config.Environment)
     */
    @Override
    public void run(CoREServiceConfiguration configuration,
                    Environment environment) {

        JpaConfiguration jpaConfig = configuration.getCrudServiceConfiguration();

        String unit = jpaConfig.getPersistenceUnit();
        Map<String, String> properties = jpaConfig.getProperties();
        properties.put("openjpa.EntityManagerFactoryPool", "true");
        emf = Persistence.createEntityManagerFactory(unit, properties);
        // necessary for polymorphic ruleform deserialization
        CoREModule module = new CoREModule();
        environment.getObjectMapper().registerModule(module);
        environment.jersey().register(new CrudResource(emf));
        environment.jersey().register(new CrudGuiResource(unit));
        environment.jersey().register(new DomainResource(
                                                         unit,
                                                         (OpenJPAEntityManagerFactory) emf));
        environment.jersey().register(new TraversalResource(emf));
        environment.jersey().register(new JpaHealthCheck(emf));
        environment.jersey().register(new CollectionResource(emf));
        environment.jersey().register(new WorkspaceResource(emf));
        environment.jersey().register(new RuleformResource(emf));
        environment.jersey().register(new ProductResource(
                                                          emf.createEntityManager()));
        environment.jersey().register(new AgencyResource(
                                                         emf.createEntityManager()));
        environment.jersey().register(new AttributeResource(
                                                            emf.createEntityManager()));
        environment.jersey().register(new IntervalResource(
                                                           emf.createEntityManager()));
        environment.jersey().register(new LocationResource(
                                                           emf.createEntityManager()));
        environment.jersey().register(new RelationshipResource(
                                                               emf.createEntityManager()));
        environment.jersey().register(new StatusCodeResource(
                                                             emf.createEntityManager()));
        environment.jersey().register(new UnitResource(
                                                       emf.createEntityManager()));
        environment.jersey().register(new JobResource(
                                                      new ModelImpl(
                                                                    emf.createEntityManager())));

    }
}
