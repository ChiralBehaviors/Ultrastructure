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

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.server.AbstractNetworkConnector;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.RecordsFactory;
import com.chiralbehaviors.CoRE.kernel.KernelUtil;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.phantasm.authentication.AgencyBasicAuthenticator;
import com.chiralbehaviors.CoRE.phantasm.authentication.AgencyBearerTokenAuthenticator;
import com.chiralbehaviors.CoRE.phantasm.authentication.NullAuthFilter;
import com.chiralbehaviors.CoRE.phantasm.authentication.NullAuthenticator;
import com.chiralbehaviors.CoRE.phantasm.graphql.schemas.FacetFields;
import com.chiralbehaviors.CoRE.phantasm.resources.AuthxResource;
import com.chiralbehaviors.CoRE.phantasm.resources.WorkspaceResource;
import com.chiralbehaviors.CoRE.phantasm.service.commands.BootstrapCommand;
import com.chiralbehaviors.CoRE.phantasm.service.commands.ClearCommand;
import com.chiralbehaviors.CoRE.phantasm.service.commands.LoadSnapshotCommand;
import com.chiralbehaviors.CoRE.phantasm.service.commands.LoadWorkspaceCommand;
import com.chiralbehaviors.CoRE.phantasm.service.commands.ManifestCommand;
import com.chiralbehaviors.CoRE.phantasm.service.commands.SnapshotCommand;
import com.chiralbehaviors.CoRE.phantasm.service.config.CORSConfiguration;
import com.chiralbehaviors.CoRE.phantasm.service.config.PhantasmConfiguration;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.chiralbehaviors.CoRE.utils.CoreDbConfiguration;
import com.google.common.base.Joiner;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.PermitAllAuthorizer;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter.Builder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * @author hhildebrand
 *
 */
public class PhantasmBundle implements ConfiguredBundle<PhantasmConfiguration> {
    public interface ModelAuthenticator {
        public void setModel(Model model);
    }

    public final static Logger log = LoggerFactory.getLogger(PhantasmBundle.class);

    private Environment        environment;
    private ModelAuthenticator authenticator;

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
        environment.jersey()
                   .register(new WorkspaceResource(FacetFields.configureExecutionScope(configuration.getExecutionScope())));

        environment.lifecycle()
                   .manage(new AbstractLifeCycle() {
                       @Override
                       protected void doStart() throws Exception {
                           if (configuration.isClear()) {
                               log.info("Reinitializing database state");
                               try (DSLContext create = configuration.create()) {
                                   create.transaction(config -> RecordsFactory.clear(DSL.using(config)));
                               }
                           }
                           try (DSLContext create = configuration.create()) {
                               if (configuration.isClear()) {
                                   create.transaction(config -> {
                                       log.info("Loading kernel");
                                       DSLContext txnlCreate = DSL.using(config);
                                       KernelUtil.loadKernel(txnlCreate);
                                       log.info("Initializing instance");
                                       KernelUtil.initializeInstance(new ModelImpl(txnlCreate),
                                                                     CoreDbConfiguration.CORE,
                                                                     "CoRE instance");
                                   });
                               }
                               log.info("Loading workspace state: {}",
                                        configuration.getWorkspaces());
                               LoadWorkspaceCommand.loadWorkspaces(configuration.getWorkspaces(),
                                                                   create);
                               log.info("Loading snapshot state: {}",
                                        configuration.getSnapshots());
                               LoadSnapshotCommand.loadSnapshots(configuration.getSnapshots(),
                                                                 create);
                           }
                           authenticator.setModel(new ModelImpl(configuration.create()));
                       }
                   });
    }

    private void configureAuth(PhantasmConfiguration configuration,
                               Environment environment) {
        switch (configuration.getAuth()) {
            case NULL_AUTH: {
                log.warn("Setting authentication to NULL");
                NullAuthFilter<AuthorizedPrincipal> filter;
                NullAuthenticator auth = new NullAuthenticator();
                filter = new NullAuthFilter.Builder<AuthorizedPrincipal>().setAuthenticator(auth)
                                                                          .setAuthorizer(new PermitAllAuthorizer<>())
                                                                          .setPrefix("Null")
                                                                          .buildAuthFilter();
                environment.jersey()
                           .register(new AuthDynamicFeature(filter));
                authenticator = auth;
                break;
            }
            case BASIC_DIGEST: {
                log.warn("Setting authentication to US basic authentication");
                AgencyBasicAuthenticator auth = new AgencyBasicAuthenticator();
                BasicCredentialAuthFilter<AuthorizedPrincipal> filter;
                filter = new BasicCredentialAuthFilter.Builder<AuthorizedPrincipal>().setAuthenticator(auth)
                                                                                     .setAuthorizer(new PermitAllAuthorizer<>())
                                                                                     .setPrefix("Basic")
                                                                                     .buildAuthFilter();
                environment.jersey()
                           .register(new AuthDynamicFeature(filter));
                authenticator = auth;
                break;
            }
            case BEARER_TOKEN: {
                log.warn("Setting authentication to US capability OAuth2 bearer token");
                AgencyBearerTokenAuthenticator auth = new AgencyBearerTokenAuthenticator();
                auth.register(environment);
                environment.jersey()
                           .register(new AuthDynamicFeature(new Builder<AuthorizedPrincipal>().setAuthenticator(auth)
                                                                                              .setAuthorizer(new PermitAllAuthorizer<>())
                                                                                              .setPrefix("Bearer")
                                                                                              .buildAuthFilter()));
                authenticator = auth;
                break;
            }
        }
        environment.jersey()
                   .register(new AuthxResource());
        environment.jersey()
                   .register(new AuthValueFactoryProvider.Binder<>(AuthorizedPrincipal.class));
    }

    private void configureCORS(PhantasmConfiguration configuration,
                               Environment environment) {
        if (configuration.isUseCORS()) {
            CORSConfiguration cors = configuration.getCORS();
            FilterRegistration.Dynamic filter = environment.servlets()
                                                           .addFilter("CORS",
                                                                      CrossOriginFilter.class);
            log.warn(String.format("Using CORS configuration: %s", cors));
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
}
