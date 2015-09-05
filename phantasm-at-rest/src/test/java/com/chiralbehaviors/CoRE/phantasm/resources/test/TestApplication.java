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
package com.chiralbehaviors.CoRE.phantasm.resources.test;

import javax.persistence.EntityManagerFactory;

import org.eclipse.jetty.server.AbstractNetworkConnector;
import org.eclipse.jetty.server.Server;

import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.phantasm.authentication.NullAuthenticationFactory;
import com.chiralbehaviors.CoRE.phantasm.jsonld.health.EmfHealthCheck;
import com.chiralbehaviors.CoRE.phantasm.resources.FacetResource;
import com.chiralbehaviors.CoRE.phantasm.resources.GraphQlResource;
import com.chiralbehaviors.CoRE.phantasm.resources.RuleformResource;
import com.chiralbehaviors.CoRE.phantasm.resources.WorkspaceMediatedResource;
import com.chiralbehaviors.CoRE.phantasm.resources.WorkspaceResource;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthFactory;
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

    private EntityManagerFactory emf;
    private Environment          environment;
    private Server               jettyServer;

    public TestApplication() {
    }

    public int getPort() {
        return ((AbstractNetworkConnector) environment.getApplicationContext()
                                                      .getServer()
                                                      .getConnectors()[0]).getLocalPort();
    }

    @Override
    public void initialize(Bootstrap<TestServiceConfiguration> bootstrap) {
        ObjectMapper objMapper = bootstrap.getObjectMapper();
        objMapper.registerModule(new CoREModule());
    }

    /* (non-Javadoc)
     * @see io.dropwizard.AbstractService#initialize(io.dropwizard.config.Configuration, io.dropwizard.config.Environment)
     */
    @Override
    public void run(TestServiceConfiguration configuration,
                    Environment environment) throws Exception {
        if (configuration.isRandomPort()) {
            ((HttpConnectorFactory) ((DefaultServerFactory) configuration.getServerFactory()).getApplicationConnectors()
                                                                                             .get(0)).setPort(0);
            ((HttpConnectorFactory) ((DefaultServerFactory) configuration.getServerFactory()).getAdminConnectors()
                                                                                             .get(0)).setPort(0);
        }
        this.environment = environment;
        environment.lifecycle()
                   .addServerLifecycleListener(server -> jettyServer = server);
        environment.jersey()
                   .register(AuthFactory.binder(new NullAuthenticationFactory()));
        environment.jersey()
                   .register(new FacetResource(emf));
        environment.jersey()
                   .register(new WorkspaceResource(emf));
        environment.jersey()
                   .register(new RuleformResource(emf));
        environment.jersey()
                   .register(new WorkspaceMediatedResource(emf));
        environment.jersey()
                   .register(new GraphQlResource(emf));
        environment.healthChecks()
                   .register("EMF Health", new EmfHealthCheck(emf));
    }

    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void stop() {
        if (emf != null) {
            emf.close();
        }
        if (jettyServer != null) {
            try {
                jettyServer.setStopTimeout(100);
                jettyServer.stop();
            } catch (Throwable e) {
                // ignore
            }
        }
    }
}
