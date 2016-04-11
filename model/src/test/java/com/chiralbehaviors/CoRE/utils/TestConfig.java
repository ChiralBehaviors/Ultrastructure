/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.utils;

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.util.Properties;

import org.junit.Test;

import com.chiralbehaviors.CoRE.test.DatabaseTest;

/**
 * @author hhildebrand
 *
 */
public class TestConfig extends DatabaseTest {

    @Test
    public void testDbaConfiguration() throws Exception {
        Properties properties = new Properties();
        properties.load(DatabaseTest.class.getResourceAsStream("/db.properties"));
        DbaConfiguration config = new DbaConfiguration();
        config.corePassword = (String) properties.get("password");
        config.coreServer = (String) properties.get("core.server");
        config.coreDb = (String) properties.get("core.db");
        config.corePort = Integer.parseInt((String) properties.get("core.port"));
        config.coreUsername = (String) properties.get("user");
        try (Connection connection = config.getCoreConnection()) {
            assertNotNull(connection);
        }
        config.dbaPassword = (String) properties.get("dba.password");
        config.dbaServer = (String) properties.get("dba.server");
        config.dbaDb = (String) properties.get("dba.db");
        config.dbaPort = Integer.parseInt((String) properties.get("dba.port"));
        config.dbaUsername = (String) properties.get("dba.login");
        try (Connection connection = config.getDbaConnection()) {
            assertNotNull(connection);
        }
    }
}
