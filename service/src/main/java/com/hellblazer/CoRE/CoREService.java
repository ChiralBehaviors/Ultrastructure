/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hellblazer.CoRE;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hellblazer.CoRE.access.DataAccessBundle;
import com.hellblazer.CoRE.agency.AgencyAttribute;
import com.hellblazer.CoRE.authentication.AgencyAuthenticator;
import com.hellblazer.CoRE.configuration.CoREServiceConfiguration;
import com.hellblazer.CoRE.configuration.JpaConfiguration;
import com.hellblazer.CoRE.json.AttributeValueSerializer;
import com.hellblazer.CoRE.meta.models.ModelImpl;
import com.hellblazer.CoRE.security.AuthenticatedPrincipal;
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
