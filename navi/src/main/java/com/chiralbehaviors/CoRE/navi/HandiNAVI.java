/** 
 * (C) Copyright 2015 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.navi;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.jetty.server.AbstractNetworkConnector;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.navi.HandiNAVIConfiguration.Asset;
import com.chiralbehaviors.CoRE.phantasm.authentication.AgencyBasicAuthenticator;
import com.chiralbehaviors.CoRE.phantasm.authentication.AgencyBearerTokenAuthenticator;
import com.chiralbehaviors.CoRE.phantasm.authentication.NullAuthenticationFactory;
import com.chiralbehaviors.CoRE.phantasm.resources.FacetResource;
import com.chiralbehaviors.CoRE.phantasm.resources.GraphQlResource;
import com.chiralbehaviors.CoRE.phantasm.resources.LoginResource;
import com.chiralbehaviors.CoRE.phantasm.resources.RuleformResource;
import com.chiralbehaviors.CoRE.phantasm.resources.WorkspaceMediatedResource;
import com.chiralbehaviors.CoRE.phantasm.resources.WorkspaceResource;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthFactory;
import io.dropwizard.auth.basic.BasicAuthFactory;
import io.dropwizard.auth.oauth.OAuthFactory;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Ultrastructure as a Service
 * 
 * @author hhildebrand
 * 
 */
public class HandiNAVI extends Application<HandiNAVIConfiguration> {
    private static final Logger log = LoggerFactory.getLogger(HandiNAVI.class);

    public static void main(String[] argv) throws Exception {
        new HandiNAVI().run(argv);
    }

    private EntityManagerFactory emf;
    private Environment          environment;
    private Server               jettyServer;
    private String               name;

    public EntityManagerFactory getEmf() {
        return emf;
    }

    public int getPort() {
        return ((AbstractNetworkConnector) environment.getApplicationContext()
                                                      .getServer()
                                                      .getConnectors()[0]).getLocalPort();
    }

    @Override
    public void initialize(Bootstrap<HandiNAVIConfiguration> bootstrap) {
        bootstrap.getObjectMapper()
                 .registerModule(new CoREModule());
    }

    /* (non-Javadoc)
     * @see io.dropwizard.AbstractService#initialize(io.dropwizard.config.Configuration, io.dropwizard.config.Environment)
     */
    @Override
    public void run(HandiNAVIConfiguration configuration,
                    Environment environment) throws Exception {
        if (configuration.randomPort) {
            ((HttpConnectorFactory) ((DefaultServerFactory) configuration.getServerFactory()).getApplicationConnectors()
                                                                                             .get(0)).setPort(0);
            ((HttpConnectorFactory) ((DefaultServerFactory) configuration.getServerFactory()).getAdminConnectors()
                                                                                             .get(0)).setPort(0);
        }
        this.environment = environment;
        environment.lifecycle()
                   .addServerLifecycleListener(server -> jettyServer = server);
        Map<String, String> properties = configuration.jpa.getProperties();
        if (emf == null) { // allow tests to set this if needed
            emf = Persistence.createEntityManagerFactory(configuration.jpa.getPersistenceUnit(),
                                                         properties);
        }
        switch (configuration.auth) {
            case NULL:
                log.warn("Setting authentication to NULL");
                environment.jersey()
                           .register(AuthFactory.binder(new NullAuthenticationFactory()));
                break;
            case BASIC_DIGEST:
                log.warn("Setting authentication to Agnecy based basic authentication");
                environment.jersey()
                           .register(AuthFactory.binder(new BasicAuthFactory<AuthorizedPrincipal>(new AgencyBasicAuthenticator(emf),
                                                                                                  configuration.realm,
                                                                                                  AuthorizedPrincipal.class)));
            case BEARER_TOKEN:
                log.warn("Setting authentication to Ultrastructure OAuth2 bearer tokens");
                environment.jersey()
                           .register(AuthFactory.binder(new OAuthFactory<AuthorizedPrincipal>(new AgencyBearerTokenAuthenticator(emf),
                                                                                              configuration.realm,
                                                                                              AuthorizedPrincipal.class)));
        }
        for (Asset asset : configuration.assets) {
            new AssetsBundle(asset.path, asset.uri, asset.index,
                             asset.name).run(environment);
        }
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
        environment.jersey()
                   .register(new LoginResource(emf));
        environment.healthChecks()
                   .register("EMF Health", new EmfHealthCheck(emf));
    }

    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
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
}
