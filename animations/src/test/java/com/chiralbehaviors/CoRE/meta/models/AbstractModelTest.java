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

package com.chiralbehaviors.CoRE.meta.models;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.jooq.DSLContext;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;
import org.jooq.util.postgres.PostgresDSL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;

import com.chiralbehaviors.CoRE.kernel.KernelUtil;
import com.chiralbehaviors.CoRE.meta.Model;

/**
 * Common superclass for tests that need the initialized Kernel and model.
 *
 * @author hparry, hhildebrand
 *
 */
public class AbstractModelTest {
    public static final String TARGET_CLASSES_THING_1_2_JSON = "target/test-classes/thing.1.2.json";
    public static final String TARGET_CLASSES_THING_1_JSON   = "target/test-classes/thing.1.json";
    public static final String TARGET_CLASSES_THING_2_JSON   = "target/test-classes/thing.2.json";
    public static final String TARGET_THINGS_JSON            = "target/things.json";
    public static final String THING_URI                     = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/phantasm";

    protected Model            model;

    @AfterClass
    public static void afterClass() throws DataAccessException, SQLException {
    }

    public static DSLContext newCreate() throws IOException, SQLException {
        Properties properties = new Properties();
        properties.load(AbstractModelTest.class.getResourceAsStream("/db.properties"));
        System.out.println(String.format(" ---------> Connecting to DB: %s",
                                         properties.get("url")));
        Connection conn = DriverManager.getConnection((String) properties.get("url"),
                                                      (String) properties.get("user"),
                                                      (String) properties.get("password"));
        conn.setAutoCommit(false);
        Settings settings = new Settings();
        settings.setExecuteWithOptimisticLocking(true);
        return PostgresDSL.using(conn, settings);
    }

    public AbstractModelTest() {
        super();
    }

    @After
    public void after() throws DataAccessException, SQLException {
        if (model != null) {
            model.close();
        }
        ModelImpl.clearPhantasmCache();
    }

    @Before
    public void before() throws DataAccessException, SQLException,
                         InstantiationException, IOException {
        DSLContext create = newCreate();
        KernelUtil.clearAndLoadKernel(create);
        model = new ModelImpl(create);
        KernelUtil.initializeInstance(model,
                                      "Abstract Model Test CoRE Instance",
                                      "CoRE instance for an Abstract Model Test");
    }
}
