package com.chiralbehaviors.CoRE.pgTesting;

import static ru.yandex.qatools.embed.postgresql.EmbeddedPostgres.cachedRuntimeConfig;
import static ru.yandex.qatools.embed.postgresql.distribution.Version.Main.PRODUCTION;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig.Credentials;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig.Net;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig.Storage;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig.Timeout;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;

abstract public class AbstractPgTest {
    public final static String           JDBC_URI;
    private final static Logger          LOG = LoggerFactory.getLogger(AbstractPgTest.class);
    private final static PostgresProcess PROCESS;

    static {
        String username = "core";
        String password = "core";
        // the cached directory should contain pgsql folder
        final Path cachedDir = Paths.get(".postgres");

        PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getInstance(cachedRuntimeConfig(cachedDir));

        LOG.info("Starting Postgres");
        Storage storage;
        try {
            storage = new Storage("core");
        } catch (IOException e) {
            throw new IllegalStateException("Unable to start embedded postgres",
                                            e);
        }
        final PostgresConfig config = new PostgresConfig(PRODUCTION,
                                                         new Net("localhost",
                                                                 findFreePort()),
                                                         storage, new Timeout(),
                                                         new Credentials(username,
                                                                         password));
        // pass info regarding encoding, locale, collate, ctype, instead of setting global environment settings
        config.getAdditionalInitDbParams()
              .addAll(Arrays.asList("-E", "UTF-8", "--locale=en_US.UTF-8",
                                    "--lc-collate=en_US.UTF-8",
                                    "--lc-ctype=en_US.UTF-8"));
        PostgresExecutable exec = runtime.prepare(config);
        try {
            PROCESS = exec.start();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to start embedded postgres",
                                            e);
        }
        Runtime.getRuntime()
               .addShutdownHook(new Thread(() -> PROCESS.stop(), username));

        JDBC_URI = String.format("jdbc:postgresql://%s:%s/%s?user=%s&password=%s",
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
    }

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

}
