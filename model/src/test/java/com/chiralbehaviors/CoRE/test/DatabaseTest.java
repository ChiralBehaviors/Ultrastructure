/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.test;

import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.util.postgres.PostgresDSL;
import org.junit.After;
import org.junit.Before;

import com.chiralbehaviors.CoRE.RecordsFactory;
import com.chiralbehaviors.CoRE.domain.Agency;

/**
 * @author hhildebrand
 * 
 */
abstract public class DatabaseTest {
    public static final String SELECT_TABLE = "SELECT table_schema || '.' || table_name AS name FROM information_schema.tables WHERE table_schema='ruleform' AND table_type='BASE TABLE' ORDER BY table_name";

    protected DSLContext       create;
    protected RecordsFactory   RECORDS;

    @After
    public void after() throws DataAccessException, SQLException {
        create.configuration()
              .connectionProvider()
              .acquire()
              .rollback();
        create.close();
    }

    @Before
    public void setup() throws Exception {
        Properties properties = new Properties();
        properties.load(DatabaseTest.class.getResourceAsStream("/db.properties"));
        Connection conn = DriverManager.getConnection((String) properties.get("url"),
                                                      (String) properties.get("user"),
                                                      (String) properties.get("password"));
        conn.setAutoCommit(false);

        create = PostgresDSL.using(conn);

        AtomicReference<Agency> core = new AtomicReference<>();
        RECORDS = new RecordsFactory() {

            @Override
            public DSLContext create() {
                return create;
            }

            @Override
            public Agency currentPrincipal() {
                return core.get();
            }
        };
        clear(create);
        Agency c = RECORDS.newAgency("Ye CoRE");
        c.setUpdatedBy(c.getId());
        create.insertInto(EXISTENTIAL)
              .set(c);
        core.set(c);

    }

    private void clear(DSLContext em) throws SQLException {
        em.transaction(c -> {
            Connection connection = c.connectionProvider()
                                     .acquire();
            ResultSet r = connection.createStatement()
                                    .executeQuery(SELECT_TABLE);
            while (r.next()) {
                String table = r.getString("name");
                String query = String.format("TRUNCATE TABLE %s CASCADE",
                                             table);
                connection.createStatement()
                          .execute(query);
            }
            r.close();
        });
    }

    protected final void commitTransaction() throws DataAccessException,
                                             SQLException {
        create.configuration()
              .connectionProvider()
              .acquire()
              .commit();
    }
}
