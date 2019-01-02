/**
 * Copyright (c) 2017 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 *  This file is part of Ultrastructure.
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

package com.chiralbehaviors.CoRE.handiNavi;

import static ru.yandex.qatools.embed.postgresql.EmbeddedPostgres.cachedRuntimeConfig;
import static ru.yandex.qatools.embed.postgresql.distribution.Version.Main.PRODUCTION;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.loader.Loader;
import com.chiralbehaviors.CoRE.phantasm.service.config.PhantasmConfiguration;
import com.chiralbehaviors.CoRE.utils.DbaConfiguration;

import io.dropwizard.db.DataSourceFactory;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig.Credentials;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig.Net;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig.Storage;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig.Timeout;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;

public class EmbeddedConfiguration extends PhantasmConfiguration {

    public static final String  NAVI_PASSWORD = "navi.password";
    private static final Logger log           = LoggerFactory.getLogger(EmbeddedConfiguration.class);
    private static final String NAVI          = "navi";
    private static final String UAAS_POSTGRES = ".uaas/postgres";
    private static final String UAAS_STATE    = ".uaas/state";

    private static int findFreePort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            socket.setReuseAddress(true);
            int port = socket.getLocalPort();
            try {
                socket.close();
            } catch (IOException ignored) {
                // Ignore IOException on close()
            }
            return port;
        } catch (IOException ignored) {
            // Ignore IOException on open
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ignored) {
                    // Ignore IOException on close()
                }
            }
        }
        throw new IllegalStateException("Could not find a free TCP/IP port to start embedded Postgresql Server on");
    }

    @Override
    public DataSourceFactory getDatabase() {
        DataSourceFactory database = super.getDatabase();
        try {
            database.setUrl(initializePostgresql());
        } catch (SQLException | IOException | URISyntaxException e) {
            throw new IllegalStateException("Cannot initialize postgres", e);
        }
        return database;
    }

    String initializePostgresql() throws SQLException, IOException,
                                  URISyntaxException {

        String password = System.getProperty(NAVI_PASSWORD, "changeMe"); // TODO no default 
        // the cached directory should contain pgsql folder
        final Path cachedDir = Paths.get(UAAS_POSTGRES);

        PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getInstance(cachedRuntimeConfig(cachedDir));

        log.info("Starting Postgres");
        final PostgresConfig config = new PostgresConfig(PRODUCTION,
                                                         new Net("localhost",
                                                                 findFreePort()),
                                                         new Storage(NAVI,
                                                                     UAAS_STATE),
                                                         new Timeout(),
                                                         new Credentials(NAVI,
                                                                         password));
        // pass info regarding encoding, locale, collate, ctype, instead of setting global environment settings
        config.getAdditionalInitDbParams()
              .addAll(Arrays.asList("-E", "UTF-8", "--locale=en_US.UTF-8",
                                    "--lc-collate=en_US.UTF-8",
                                    "--lc-ctype=en_US.UTF-8"));
        PostgresExecutable exec = runtime.prepare(config);
        PostgresProcess process = exec.start();
        Runtime.getRuntime()
               .addShutdownHook(new Thread(() -> process.stop(),
                                           "Local NAVI shutdown"));

        String uri = String.format("jdbc:postgresql://%s:%s/%s?user=%s&password=%s",
                                   config.net()
                                         .host(),
                                   config.net()
                                         .port(),
                                   config.storage()
                                         .dbName(),
                                   config.credentials()
                                         .username(),
                                   config.credentials()
                                         .password());

        DbaConfiguration dbaConfig = new DbaConfiguration();
        dbaConfig.coreDb = config.storage()
                                 .dbName();
        dbaConfig.corePort = config.net()
                                   .port();
        dbaConfig.coreServer = config.net()
                                     .host();
        dbaConfig.coreUsername = config.credentials()
                                       .username();
        dbaConfig.corePassword = config.credentials()
                                       .password();
        try {
            new Loader(dbaConfig).bootstrap();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot bootstrap CORE", e);
        }
        return uri;
    }
}
