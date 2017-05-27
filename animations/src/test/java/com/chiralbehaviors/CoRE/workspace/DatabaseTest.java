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

package com.chiralbehaviors.CoRE.workspace;

import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.util.postgres.PostgresDSL;
import org.junit.After;
import org.junit.Before;

import com.chiralbehaviors.CoRE.RecordsFactory;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownAgency;
import com.chiralbehaviors.CoRE.domain.Agency;

/**
 * @author hhildebrand
 * 
 */
abstract public class DatabaseTest {
    protected DSLContext     create;
    protected RecordsFactory RECORDS;

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

        AtomicReference<UUID> core = new AtomicReference<>();
        RECORDS = new RecordsFactory() {

            @Override
            public DSLContext create() {
                return create;
            }

            @Override
            public UUID currentPrincipalId() {
                return core.get();
            }
        };
        RecordsFactory.clear(create);
        Agency c = RECORDS.newAgency("Ye CoRE");
        c.setId(WellKnownAgency.CORE.id());
        c.setUpdatedBy(c.getId());
        create.insertInto(EXISTENTIAL)
              .set(c);
        core.set(WellKnownAgency.CORE.id());

    }

    protected final void commitTransaction() throws DataAccessException,
                                             SQLException {
        create.configuration()
              .connectionProvider()
              .acquire()
              .commit();
    }
}
