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
package com.chiralbehaviors.CoRE.phantasm.jsonld.resources.test;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.jetty.server.AbstractNetworkConnector;
import org.eclipse.jetty.server.Server;

import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.phantasm.jsonld.resources.FacetContextResource;
import com.chiralbehaviors.CoRE.phantasm.jsonld.resources.FacetNodeResource;
import com.chiralbehaviors.CoRE.phantasm.jsonld.resources.RuleformResource;
import com.chiralbehaviors.CoRE.phantasm.jsonld.resources.WorkspaceResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module.Feature;

import io.dropwizard.Application;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * @author hhildebrand
 * 
 */
public class TestApplication extends Application<TestServiceConfiguration> {

    public static void main(String[] argv) throws Exception {
        new TestApplication().run(argv);
    }

    private Environment          environment;
    private Server               jettyServer;
    private EntityManagerFactory emf;

    public TestApplication() {
    }

    public int getPort() {
        return ((AbstractNetworkConnector) environment.getApplicationContext().getServer().getConnectors()[0]).getLocalPort();
    }

    /* (non-Javadoc)
     * @see io.dropwizard.AbstractService#initialize(io.dropwizard.config.Configuration, io.dropwizard.config.Environment)
     */
    @Override
    public void run(TestServiceConfiguration configuration,
                    Environment environment) throws Exception {
        if (configuration.isRandomPort()) {
            ((HttpConnectorFactory) ((DefaultServerFactory) configuration.getServerFactory()).getApplicationConnectors().get(0)).setPort(0);
            ((HttpConnectorFactory) ((DefaultServerFactory) configuration.getServerFactory()).getAdminConnectors().get(0)).setPort(0);
        }
        this.environment = environment;
        environment.lifecycle().addServerLifecycleListener(server -> jettyServer = server);
        JpaConfiguration jpaConfig = configuration.getCrudServiceConfiguration();

        String unit = jpaConfig.getPersistenceUnit();
        Map<String, String> properties = jpaConfig.getProperties();
        emf = Persistence.createEntityManagerFactory(unit, properties);
        environment.jersey().register(new FacetContextResource(emf));
        environment.jersey().register(new FacetNodeResource(emf));
        environment.jersey().register(new WorkspaceResource(emf));
        environment.jersey().register(new RuleformResource(emf));
        environment.healthChecks().register("EMF Health",
                                            new EmfHealthCheck(emf));
    }

    public void stop() {
        emf.close();
        if (jettyServer != null) {
            try {
                jettyServer.setStopTimeout(100);
                jettyServer.stop();
            } catch (Throwable e) {
                // ignore
            }
        }
    }

    @Override
    public void initialize(Bootstrap<TestServiceConfiguration> bootstrap) {
        ObjectMapper objMapper = bootstrap.getObjectMapper();
        objMapper.registerModule(new CoREModule());
        Hibernate4Module module = new Hibernate4Module();
        module.enable(Feature.FORCE_LAZY_LOADING);
        objMapper.registerModule(module);
    }
}
