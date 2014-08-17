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
package com.chiralbehaviors.CoRE;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.chiralbehaviors.CoRE.access.DataAccessBundle;
import com.chiralbehaviors.CoRE.agency.AgencyAttribute;
import com.chiralbehaviors.CoRE.authentication.AgencyAuthenticator;
import com.chiralbehaviors.CoRE.configuration.CoREServiceConfiguration;
import com.chiralbehaviors.CoRE.configuration.JpaConfiguration;
import com.chiralbehaviors.CoRE.json.AttributeValueSerializer;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.security.AuthenticatedPrincipal;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author hhildebrand
 * 
 */
public class CoREService extends Application<CoREServiceConfiguration> {

    public static void main(String[] argv) throws Exception {
        new CoREService().run(argv);
    }

    protected CoREService() {

    }

    @Override
    public void initialize(Bootstrap<CoREServiceConfiguration> bootstrap) {
        bootstrap.addBundle(new DataAccessBundle());

        //CacheBuilderSpec spec = AssetsBundle.DEFAULT_CACHE_SPEC;

        bootstrap.addBundle(new AssetsBundle("/ui/", "/ui/"));
        SimpleModule testModule = new SimpleModule("MyModule",
                                                   new Version(1, 0, 0, null,
                                                               null, null));
        testModule.addSerializer(new AttributeValueSerializer<AgencyAttribute>(
                                                                               AgencyAttribute.class,
                                                                               true)); // assuming serializer declares correct class to bind to
        bootstrap.getObjectMapper().registerModule(testModule);
    }

    /* (non-Javadoc)
     * @see io.dropwizard.AbstractService#initialize(io.dropwizard.config.Configuration, io.dropwizard.config.Environment)
     */
    @Override
    public void run(CoREServiceConfiguration configuration,
                    Environment environment) throws Exception {
        JpaConfiguration jpaConfig = configuration.getCrudServiceConfiguration();

        String unit = jpaConfig.getPersistenceUnit();
        Map<String, String> properties = jpaConfig.getProperties();
        properties.put("openjpa.EntityManagerFactoryPool", "true");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(unit,
                                                                          properties);
        environment.jersey().register(new BasicAuthProvider<AuthenticatedPrincipal>(
                                                                                    new AgencyAuthenticator(
                                                                                                            new ModelImpl(
                                                                                                                          emf.createEntityManager())),
                                                                                    "CoRE"));
    }
}
