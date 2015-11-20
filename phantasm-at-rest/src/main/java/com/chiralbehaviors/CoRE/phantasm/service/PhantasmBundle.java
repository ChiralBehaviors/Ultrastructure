/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 *

 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.phantasm.service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.server.AbstractNetworkConnector;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.hk2.utilities.Binder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bazaarvoice.dropwizard.assets.ConfiguredAssetsBundle;
import com.chiralbehaviors.CoRE.json.CoREModule;
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
import com.chiralbehaviors.CoRE.phantasm.service.commands.BootstrapCommand;
import com.chiralbehaviors.CoRE.phantasm.service.commands.ClearCommand;
import com.chiralbehaviors.CoRE.phantasm.service.commands.LoadSnapshotCommand;
import com.chiralbehaviors.CoRE.phantasm.service.commands.LoadWorkspaceCommand;
import com.chiralbehaviors.CoRE.phantasm.service.commands.ManifestCommand;
import com.chiralbehaviors.CoRE.phantasm.service.commands.SnapshotCommand;
import com.chiralbehaviors.CoRE.phantasm.service.config.CORSConfiguration;
import com.chiralbehaviors.CoRE.phantasm.service.config.JpaConfiguration;
import com.chiralbehaviors.CoRE.phantasm.service.config.PhantasmConfiguration;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.chiralbehaviors.CoRE.utils.CoreDbConfiguration;
import com.google.common.base.Joiner;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.auth.AuthFactory;
import io.dropwizard.auth.CachingAuthenticator;
import io.dropwizard.auth.basic.BasicAuthFactory;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.server.ServerFactory;
import io.dropwizard.server.SimpleServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * @author hhildebrand
 *
 */
public class PhantasmBundle implements ConfiguredBundle<PhantasmConfiguration> {
    public static final String  JAVAX_PERSISTENCE_JDBC_DRIVER   = "javax.persistence.jdbc.driver";
    public static final String  JAVAX_PERSISTENCE_JDBC_PASSWORD = "javax.persistence.jdbc.password";
    public static final String  JAVAX_PERSISTENCE_JDBC_URL      = "javax.persistence.jdbc.url";
    public static final String  JAVAX_PERSISTENCE_JDBC_USER     = "javax.persistence.jdbc.user";
    public static final String  ORG_POSTGRESQL_DRIVER           = "org.postgresql.Driver";

    private final static Logger log                             = LoggerFactory.getLogger(PhantasmBundle.class);

    public static ClassLoader configureExecutionScope(List<String> urlStrings) {
        ClassLoader parent = Thread.currentThread()
                                   .getContextClassLoader();
        if (parent == null) {
            parent = PhantasmBundle.class.getClassLoader();
        }
        List<URL> urls = new ArrayList<>();
        for (String url : urlStrings) {
            URL resolved;
            try {
                resolved = new URL(url);
            } catch (MalformedURLException e) {
                try {
                    resolved = new File(url).toURI()
                                            .toURL();
                } catch (MalformedURLException e1) {
                    log.error("Invalid configured execution scope url: {}", url,
                              e1);
                    throw new IllegalArgumentException(String.format("Invalid configured execution scope url: %s",
                                                                     url),
                                                       e1);
                }
            }
            urls.add(resolved);
        }
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), parent);
    }

    public static EntityManagerFactory getEmfFromEnvironment(Map<String, String> configuredProperties,
                                                             String persistenceUnity) {

        Map<String, String> properties = JpaConfiguration.getDefaultProperties();
        properties.putAll(configuredProperties);

        CoreDbConfiguration coreConfig = new CoreDbConfiguration();
        coreConfig.initializeFromEnvironment();
        properties.put(JAVAX_PERSISTENCE_JDBC_USER, coreConfig.coreUsername);
        properties.put(JAVAX_PERSISTENCE_JDBC_PASSWORD,
                       coreConfig.corePassword);
        properties.put(JAVAX_PERSISTENCE_JDBC_URL, coreConfig.getCoreJdbcURL());
        properties.put(JAVAX_PERSISTENCE_JDBC_DRIVER, ORG_POSTGRESQL_DRIVER);
        return Persistence.createEntityManagerFactory(persistenceUnity,
                                                      properties);
    }

    private EntityManagerFactory emf;

    private Environment          environment;

    public PhantasmBundle(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public int getPort() {
        return ((AbstractNetworkConnector) environment.getApplicationContext()
                                                      .getServer()
                                                      .getConnectors()[0]).getLocalPort();
    }

    /* (non-Javadoc)
     * @see io.dropwizard.ConfiguredBundle#initialize(io.dropwizard.setup.Bootstrap)
     */
    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        bootstrap.getObjectMapper()
                 .registerModule(new CoREModule());
        bootstrap.addCommand(new BootstrapCommand());
        bootstrap.addCommand(new ClearCommand());
        bootstrap.addCommand(new LoadWorkspaceCommand());
        bootstrap.addCommand(new ManifestCommand());
        bootstrap.addCommand(new SnapshotCommand());
        bootstrap.addCommand(new LoadSnapshotCommand());
    }

    /* (non-Javadoc)
     * @see io.dropwizard.AbstractService#initialize(io.dropwizard.config.Configuration, io.dropwizard.config.Environment)
     */
    @Override
    public void run(PhantasmConfiguration configuration,
                    Environment environment) throws Exception {
        //        environment.jersey()
        //                   .setUrlPattern(null);
        this.environment = environment;
        if (configuration.jpa.configureFromEnvironment()) {
            configureFromEnvironment(configuration);
        } else {
            configure(configuration);
        }

        configureAuth(configuration, environment);
        configureCORS(configuration, environment);
        configureServices(environment,
                          configureExecutionScope(configuration.executionScope));

        configuration.assets.forEach(asset -> {
            try {
                log.info("Configuring {}", asset);
                new ConfiguredAssetsBundle(asset.path, asset.uri, asset.index,
                                           asset.name).run(configuration,
                                                           environment);
            } catch (Exception e) {
                log.error(String.format("Cannot configure asset: %s", asset),
                          e);
            }
        });

    }

    private void configure(PhantasmConfiguration configuration) throws Exception {
        if (configuration.randomPort) {
            configureRandomPort(configuration);
        }
        Map<String, String> properties = JpaConfiguration.getDefaultProperties();
        properties.putAll(configuration.jpa.getProperties());

        if (emf == null) { // allow tests to set this if needed
            emf = Persistence.createEntityManagerFactory(configuration.jpa.getPersistenceUnit(),
                                                         properties);
        }
    }

    private void configureAuth(PhantasmConfiguration configuration,
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

        environment.jersey()
                   .register(authBinder);
        environment.jersey()
                   .register(new AuthxResource(emf));
    }

    private void configureCORS(PhantasmConfiguration configuration,
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

    private void configureFromEnvironment(PhantasmConfiguration configuration) throws Exception {
        if (emf == null) {
            emf = getEmfFromEnvironment(configuration.jpa.getProperties(),
                                        configuration.jpa.getPersistenceUnit());
        }
    }

    private void configureRandomPort(PhantasmConfiguration configuration) {
        ServerFactory serverFactory = configuration.getServerFactory();
        if (serverFactory instanceof DefaultServerFactory) {
            ((HttpConnectorFactory) ((DefaultServerFactory) serverFactory).getApplicationConnectors()
                                                                          .get(0)).setPort(0);
            ((HttpConnectorFactory) ((DefaultServerFactory) serverFactory).getAdminConnectors()
                                                                          .get(0)).setPort(0);
        } else if (serverFactory instanceof SimpleServerFactory) {
            ((HttpConnectorFactory) ((SimpleServerFactory) serverFactory).getConnector()).setPort(0);
        } else {
            log.warn("Unknown server factory type: {}, unable to set random port",
                     serverFactory.getClass()
                                  .getSimpleName());
        }
    }

    private void configureServices(Environment environment,
                                   ClassLoader executionScope) {
        environment.jersey()
                   .register(new FacetResource(emf));
        environment.jersey()
                   .register(new WorkspaceResource(emf));
        environment.jersey()
                   .register(new RuleformResource(emf));
        environment.jersey()
                   .register(new WorkspaceMediatedResource(emf));
        environment.jersey()
                   .register(new GraphQlResource(emf, executionScope));
        environment.healthChecks()
                   .register("EMF Health", new EmfHealthCheck(emf));
    }

}
