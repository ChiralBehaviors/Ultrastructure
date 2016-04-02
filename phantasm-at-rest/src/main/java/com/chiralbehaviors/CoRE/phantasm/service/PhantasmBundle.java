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

import java.util.EnumSet;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.server.AbstractNetworkConnector;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bazaarvoice.dropwizard.assets.ConfiguredAssetsBundle;
import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.phantasm.authentication.AgencyBasicAuthenticator;
import com.chiralbehaviors.CoRE.phantasm.authentication.AgencyBearerTokenAuthenticator;
import com.chiralbehaviors.CoRE.phantasm.authentication.NullAuthFilter;
import com.chiralbehaviors.CoRE.phantasm.graphql.FacetType;
import com.chiralbehaviors.CoRE.phantasm.resources.AuthxResource;
import com.chiralbehaviors.CoRE.phantasm.resources.GraphQlResource;
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
import com.google.common.base.Joiner;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.PermitAllAuthorizer;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter.Builder;
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
    public static final String JAVAX_PERSISTENCE_JDBC_DRIVER   = "javax.persistence.jdbc.driver";
    public static final String JAVAX_PERSISTENCE_JDBC_PASSWORD = "javax.persistence.jdbc.password";
    public static final String JAVAX_PERSISTENCE_JDBC_URL      = "javax.persistence.jdbc.url";
    public static final String JAVAX_PERSISTENCE_JDBC_USER     = "javax.persistence.jdbc.user";
    public static final String ORG_POSTGRESQL_DRIVER           = "org.postgresql.Driver";

    public final static Logger log                             = LoggerFactory.getLogger(PhantasmBundle.class);

    private Environment        environment;

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
        this.environment = environment;

        configureAuth(configuration, environment);
        configureCORS(configuration, environment);
        configureServices(environment,
                          FacetType.configureExecutionScope(configuration.executionScope));

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
    }

    private void configureAuth(PhantasmConfiguration configuration,
                               Environment environment) {
        switch (configuration.auth) {
            case NULL: {
                log.warn("Setting authentication to NULL");
                AgencyBearerTokenAuthenticator authenticator = new AgencyBearerTokenAuthenticator(null);
                NullAuthFilter<AuthorizedPrincipal> filter;
                filter = new NullAuthFilter.Builder<AuthorizedPrincipal>().setAuthenticator(authenticator)
                                                                          .setAuthorizer(new PermitAllAuthorizer<>())
                                                                          .setPrefix("Bearer")
                                                                          .buildAuthFilter();
                environment.jersey()
                           .register(new AuthDynamicFeature(filter));
                break;
            }
            case BASIC_DIGEST: {
                log.warn("Setting authentication to US basic authentication");
                AgencyBasicAuthenticator authenticator = new AgencyBasicAuthenticator(null);
                BasicCredentialAuthFilter<AuthorizedPrincipal> filter;
                filter = new BasicCredentialAuthFilter.Builder<AuthorizedPrincipal>().setAuthenticator(authenticator)
                                                                                     .setAuthorizer(new PermitAllAuthorizer<>())
                                                                                     .setPrefix("Basic")
                                                                                     .buildAuthFilter();
                environment.jersey()
                           .register(new AuthDynamicFeature(filter));
                break;
            }
            case BEARER_TOKEN: {
                log.warn("Setting authentication to US capability OAuth2 bearer token");
                AgencyBearerTokenAuthenticator authenticator = new AgencyBearerTokenAuthenticator(null);
                OAuthCredentialAuthFilter<AuthorizedPrincipal> filter;
                filter = new Builder<AuthorizedPrincipal>().setAuthenticator(authenticator)
                                                           .setAuthorizer(new PermitAllAuthorizer<>())
                                                           .setPrefix("Bearer")
                                                           .buildAuthFilter();
                environment.jersey()
                           .register(new AuthDynamicFeature(filter));
                break;
            }
        }
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
                   .register(new GraphQlResource(executionScope));
    }

}
