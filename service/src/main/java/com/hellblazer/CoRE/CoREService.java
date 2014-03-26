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
package com.hellblazer.CoRE;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.chiralbehaviors.CoRE.agency.access.AgencyAttribute;
import com.chiralbehaviors.CoRE.security.AuthenticatedPrincipal;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hellblazer.CoRE.access.DataAccessBundle;
import com.hellblazer.CoRE.authentication.AgencyAuthenticator;
import com.hellblazer.CoRE.configuration.CoREServiceConfiguration;
import com.hellblazer.CoRE.configuration.JpaConfiguration;
import com.hellblazer.CoRE.json.AttributeValueSerializer;
import com.hellblazer.CoRE.meta.models.ModelImpl;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

/**
 * @author hhildebrand
 * 
 */
public class CoREService extends Service<CoREServiceConfiguration> {

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
        bootstrap.getObjectMapperFactory().registerModule(testModule);
    }

    /* (non-Javadoc)
     * @see com.yammer.dropwizard.AbstractService#initialize(com.yammer.dropwizard.config.Configuration, com.yammer.dropwizard.config.Environment)
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
        environment.addProvider(new BasicAuthProvider<AuthenticatedPrincipal>(
                                                                              new AgencyAuthenticator(
                                                                                                      new ModelImpl(
                                                                                                                    emf.createEntityManager())),
                                                                              "CoRE"));
    }
}
