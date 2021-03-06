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

import org.eclipse.jetty.server.Server;

import com.bendb.dropwizard.jooq.JooqBundle;
import com.bendb.dropwizard.jooq.JooqFactory;
import com.chiralbehaviors.CoRE.phantasm.service.config.PhantasmConfiguration;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Ultrastructure as Phantasm
 *
 * @author hhildebrand
 *
 */
public class NAVI<T extends PhantasmConfiguration> extends Application<T> {

    public static void main(String[] argv) throws Exception {
        new NAVI<>().run(argv);
    }

    private PhantasmConfiguration             configuration;
    private Server                            jettyServer;
    private JooqBundle<PhantasmConfiguration> jooqBundle;
    private PhantasmBundle                    service;

    public PhantasmConfiguration getConfiguration() {
        return configuration;
    }

    public int getPort() {
        return service.getPort();
    }

    @Override
    public void initialize(Bootstrap<T> bootstrap) {
        service = new PhantasmBundle();
        bootstrap.addBundle(service);
        jooqBundle = new JooqBundle<PhantasmConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(PhantasmConfiguration configuration) {
                return configuration.getDatabase();
            }

            @Override
            public JooqFactory getJooqFactory(PhantasmConfiguration configuration) {
                return configuration.getJooq();
            }
        };
        bootstrap.addBundle(jooqBundle);
    }

    /* (non-Javadoc)
     * @see io.dropwizard.AbstractService#initialize(io.dropwizard.config.Configuration, io.dropwizard.config.Environment)
     */
    @Override
    public void run(PhantasmConfiguration config,
                    Environment environment) throws Exception {
        configuration = config;
        configuration.setJooqBundle(jooqBundle);
        environment.lifecycle()
                   .addServerLifecycleListener(server -> jettyServer = server);
    }

    public void stop() {
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