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

import com.google.common.cache.CacheBuilderSpec;
import com.hellblazer.CoRE.access.DataAccessBundle;
import com.hellblazer.CoRE.authentication.ResourceAuthenticator;
import com.hellblazer.CoRE.configuration.CoREServiceConfiguration;
import com.hellblazer.CoRE.configuration.JpaConfiguration;
import com.hellblazer.CoRE.meta.models.ModelImpl;
import com.hellblazer.CoRE.meta.security.AuthenticatedPrincipal;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.auth.basic.BasicAuthProvider;
import com.yammer.dropwizard.bundles.AssetsBundle;
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
        super("CoRE");
        addBundle(new DataAccessBundle());

        // By default a restart will be required to pick up any changes to
        // assets.
        // Use the following spec to disable that behavior. Useful for
        // developing
        CacheBuilderSpec spec = CacheBuilderSpec.disableCaching();

        //CacheBuilderSpec spec = AssetsBundle.DEFAULT_CACHE_SPEC;
        addBundle(new AssetsBundle("/ui/", spec, "/ui/"));

    }

    /* (non-Javadoc)
     * @see com.yammer.dropwizard.AbstractService#initialize(com.yammer.dropwizard.config.Configuration, com.yammer.dropwizard.config.Environment)
     */
    @Override
    protected void initialize(CoREServiceConfiguration configuration,
                              Environment environment) throws Exception {
        JpaConfiguration jpaConfig = configuration.getCrudServiceConfiguration();

        String unit = jpaConfig.getPersistenceUnit();
        Map<String, String> properties = jpaConfig.getProperties();
        properties.put("openjpa.EntityManagerFactoryPool", "true");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(unit,
                                                                          properties);
        environment.addProvider(new BasicAuthProvider<AuthenticatedPrincipal>(new ResourceAuthenticator(
                                                          new ModelImpl(
                                                                        emf.createEntityManager())), "CORE650"));
    }
}
