package com.chiralbehaviors.CoRE.handiNavi;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

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

import com.chiralbehaviors.CoRE.phantasm.service.PhantasmApplication;

import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;

/**
 * Ultrastructure as an Application
 * 
 * @author hhildebrand
 * 
 */
public class HandiNAVI extends PhantasmApplication {

    public static void main(String[] argv) throws Exception {
        new HandiNAVI().run(argv);
    }

    void initializePostgresql() throws SQLException, IOException {
        // define of retrieve db name and credentials
        final String name = "yourDbname";
        final String username = "yourUser";
        final String password = "youPassword";

        // starting Postgres
        final PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getDefaultInstance();
        final PostgresConfig config = PostgresConfig.defaultWithDbName(name,
                                                                       username,
                                                                       password);
        // pass info regarding encoding, locale, collate, ctype, instead of setting global environment settings
        config.getAdditionalInitDbParams()
              .addAll(Arrays.asList("-E", "UTF-8", "--locale=en_US.UTF-8",
                                    "--lc-collate=en_US.UTF-8",
                                    "--lc-ctype=en_US.UTF-8"));
        PostgresExecutable exec = runtime.prepare(config);
        PostgresProcess process = exec.start();

        // connecting to a running Postgres
        String url = String.format("jdbc:postgresql://%s:%s/%s?currentSchema=public&user=%s&password=%s",
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
        Connection conn = DriverManager.getConnection(url);

        // feeding up the database
        conn.createStatement()
            .execute("CREATE TABLE films (code char(5));");
        conn.createStatement()
            .execute("INSERT INTO films VALUES ('movie');");

        // ... or you can execute SQL files...
        //pgProcess.importFromFile(new File("someFile.sql"))
        // ... or even SQL files with PSQL variables in them...
        //pgProcess.importFromFileWithArgs(new File("someFile.sql"), "-v", "tblName=someTable") 

        // close db connection
        conn.close();

        // stop Postgres
        process.stop();
    }
}
