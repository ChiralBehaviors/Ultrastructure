
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.server.AbstractNetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.hk2.utilities.Binder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.loader.Configuration;
import com.chiralbehaviors.CoRE.loader.Loader;
import com.chiralbehaviors.CoRE.navi.CORSConfiguration;
import com.chiralbehaviors.CoRE.navi.EmfHealthCheck;
import com.chiralbehaviors.CoRE.navi.HandiNAVIConfiguration;
import com.chiralbehaviors.CoRE.navi.HandiNAVIConfiguration.Asset;
import com.chiralbehaviors.CoRE.navi.JpaConfiguration;
import com.chiralbehaviors.CoRE.phantasm.authentication.AgencyBasicAuthenticator;
import com.chiralbehaviors.CoRE.phantasm.authentication.AgencyBearerTokenAuthenticator;
import com.chiralbehaviors.CoRE.phantasm.authentication.NullAuthenticationFactory;
import com.chiralbehaviors.CoRE.phantasm.authentication.UsOAuthFactory;
import com.chiralbehaviors.CoRE.phantasm.resources.AuthxResource;
import com.chiralbehaviors.CoRE.phantasm.resources.FacetResource;
import com.chiralbehaviors.CoRE.phantasm.resources.GraphQlResource;
import com.chiralbehaviors.CoRE.phantasm.resources.RuleformResource;
import com.chiralbehaviors.CoRE.phantasm.resources.WorkspaceMediatedResource;
import com.chiralbehaviors.CoRE.phantasm.resources.WorkspaceResource;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.google.common.base.Joiner;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthFactory;
import io.dropwizard.auth.CachingAuthenticator;
import io.dropwizard.auth.basic.BasicAuthFactory;
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
        this.environment = environment;
        environment.lifecycle()
                   .addServerLifecycleListener(server -> jettyServer = server);
        if (configuration.configureFromEnvironment) {
            configureFromEnvironment(configuration);
        } else {
            configure(configuration);
        }
        Binder authBinder = configureAuth(configuration, environment);

        environment.jersey()
                   .register(authBinder);
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
                   .register(new AuthxResource(emf));
        environment.healthChecks()
                   .register("EMF Health", new EmfHealthCheck(emf));

        configureCORS(configuration, environment);
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

    private void bootstrap(String server, int port, String database,
                           String username, String password) throws Exception {
        Configuration loaderConfig = new Configuration();
        loaderConfig.coreDb = database.substring(1);
        loaderConfig.corePassword = password;
        loaderConfig.corePort = port;
        loaderConfig.coreServer = server;
        loaderConfig.coreUsername = username;
        new Loader(loaderConfig).bootstrap();
    }

    private void configure(HandiNAVIConfiguration configuration) throws Exception {
        if (configuration.randomPort) {
            ((HttpConnectorFactory) ((DefaultServerFactory) configuration.getServerFactory()).getApplicationConnectors()
                                                                                             .get(0)).setPort(0);
            ((HttpConnectorFactory) ((DefaultServerFactory) configuration.getServerFactory()).getAdminConnectors()
                                                                                             .get(0)).setPort(0);
        }
        Map<String, String> properties = JpaConfiguration.getDefaultProperties();
        properties.putAll(configuration.jpa.getProperties());

        if (emf == null) { // allow tests to set this if needed
            emf = Persistence.createEntityManagerFactory(configuration.jpa.getPersistenceUnit(),
                                                         properties);
        }
        URI dbUri;
        try {
            dbUri = new URI(properties.get("javax.persistence.jdbc.url"));
        } catch (URISyntaxException e) {
            throw new IllegalStateException(String.format("%s is not a valid URI",
                                                          System.getenv("DATABASE_URL")),
                                            e);
        }

        String server = dbUri.getHost();
        int port = dbUri.getPort();
        String database = dbUri.getPath();
        bootstrap(server, port, database,
                  properties.get("javax.persistence.jdbc.user"),
                  properties.get("javax.persistence.jdbc.password"));
    }

    private Binder configureAuth(HandiNAVIConfiguration configuration,
                                 Environment environment) {
        Binder authBinder = null;
        switch (configuration.auth) {
            case NULL:
                log.warn("Setting authentication to NULL");
                authBinder = AuthFactory.binder(new NullAuthenticationFactory());
                break;
            case BASIC_DIGEST:
                log.warn("Setting authentication to US basic authentication");
                authBinder = AuthFactory.binder(new BasicAuthFactory<AuthorizedPrincipal>(new CachingAuthenticator<>(environment.metrics(),
                                                                                                                     new AgencyBasicAuthenticator(emf),
                                                                                                                     configuration.authenticationCachePolicy),
                                                                                          configuration.realm,
                                                                                          AuthorizedPrincipal.class));
                break;
            case BEARER_TOKEN:
                log.warn("Setting authentication to US capability OAuth2 bearer token");
                authBinder = AuthFactory.binder(new UsOAuthFactory<AuthorizedPrincipal>(new CachingAuthenticator<>(environment.metrics(),
                                                                                                                   new AgencyBearerTokenAuthenticator(emf),
                                                                                                                   configuration.authenticationCachePolicy),
                                                                                        configuration.realm,
                                                                                        AuthorizedPrincipal.class));
                break;
        }
        if (authBinder == null) {
            throw new IllegalStateException("No configuration specified for authentication.  Authentication configuration is required.");
        }
        return authBinder;
    }

    private void configureCORS(HandiNAVIConfiguration configuration,
                               Environment environment) {
        if (configuration.useCORS) {
            CORSConfiguration cors = configuration.CORS;
            FilterRegistration.Dynamic filter = environment.servlets()
                                                           .addFilter("CORS",
                                                                      CrossOriginFilter.class);
            log.warn("Using CORS configuration: %s", cors);
            // Add URL mapping
            filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class),
                                            true, "/*");
            filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM,
                                    Joiner.on(",")
                                          .join(cors.allowedOrigins));
            filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM,
                                    Joiner.on(",")
                                          .join(cors.allowedMethods));
            filter.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM,
                                    Joiner.on(",")
                                          .join(cors.allowedHeaders));
            filter.setInitParameter(CrossOriginFilter.PREFLIGHT_MAX_AGE_PARAM,
                                    Integer.toString(cors.preflightMaxAge));
            filter.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM,
                                    Boolean.toString(cors.allowCredentials));
            filter.setInitParameter(CrossOriginFilter.EXPOSED_HEADERS_PARAM,
                                    Joiner.on(",")
                                          .join(cors.exposedHeaders));
            filter.setInitParameter(CrossOriginFilter.CHAIN_PREFLIGHT_PARAM,
                                    Boolean.toString(cors.chainPreflight));

        }
    }

    private void configureFromEnvironment(HandiNAVIConfiguration configuration) throws Exception {
        HttpConnectorFactory httpConnectorFactory = (HttpConnectorFactory) ((DefaultServerFactory) configuration.getServerFactory()).getApplicationConnectors()
                                                                                                                                    .get(0);
        httpConnectorFactory.setPort(Integer.parseInt(System.getenv("PORT")));

        Map<String, String> properties = JpaConfiguration.getDefaultProperties();
        properties.putAll(configuration.jpa.getProperties());

        URI dbUri;
        try {
            dbUri = new URI(System.getenv("DATABASE_URL"));
        } catch (URISyntaxException e) {
            throw new IllegalStateException(String.format("%s is not a valid URI",
                                                          System.getenv("DATABASE_URL")),
                                            e);
        }

        String[] up = dbUri.getUserInfo()
                           .split(":");
        if (up.length == 0) {
            log.error("Invalid u/p in DATABASE_URL");
            throw new IllegalStateException();
        }

        String server = dbUri.getHost();
        int port = dbUri.getPort();
        String database = dbUri.getPath();
        String dbUrl = String.format("jdbc:postgresql://%s:%s%s", server, port,
                                     database);
        String username = up[0];
        String password = up.length == 2 ? up[1] : "";
        log.info("jdbc url: {} user: {}", dbUrl, username);
        properties.put("javax.persistence.jdbc.user", username);
        properties.put("javax.persistence.jdbc.password", password);
        properties.put("javax.persistence.jdbc.url", dbUrl);
        properties.put("javax.persistence.jdbc.driver",
                       "org.postgresql.Driver");
        emf = Persistence.createEntityManagerFactory(configuration.jpa.getPersistenceUnit(),
                                                     properties);
        bootstrap(server, port, database, username, password);
    }
}
