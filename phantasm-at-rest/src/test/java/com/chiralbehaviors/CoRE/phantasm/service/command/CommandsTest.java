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

package com.chiralbehaviors.CoRE.phantasm.service.command;

import java.sql.Connection;
import java.util.Properties;

import org.junit.After;
import org.junit.Test;

import com.chiralbehaviors.CoRE.phantasm.service.commands.BootstrapCommand;
import com.chiralbehaviors.CoRE.phantasm.service.commands.ClearCommand;
import com.chiralbehaviors.CoRE.phantasm.service.commands.RollbackCommand;
import com.chiralbehaviors.CoRE.utils.CoreDbConfiguration;
import com.chiralbehaviors.CoRE.utils.DbaConfiguration;

/**
 * @author hhildebrand
 *
 */
public class CommandsTest {
    @After
    public void after() {
        CoreDbConfiguration.TEST_ENV_CONFIGURATION = null;
    }

    @Test
    public void testCommands() throws Exception {

        Properties properties = new Properties();
        properties.load(CommandsTest.class.getResourceAsStream("/db.properties"));
        DbaConfiguration config = new DbaConfiguration();
        config.dbaPassword = (String) properties.get("dba.password");
        config.dbaServer = (String) properties.get("dba.server");
        config.dbaDb = (String) properties.get("dba.db");
        config.dbaPort = Integer.parseInt((String) properties.get("dba.port"));
        config.dbaUsername = (String) properties.get("dba.login");

        CoreDbConfiguration coreConfig = new CoreDbConfiguration();

        coreConfig.corePassword = "tiger";
        coreConfig.coreServer = (String) properties.get("core.server");
        coreConfig.coreDb = "testme";
        coreConfig.corePort = Integer.parseInt((String) properties.get("core.port"));
        coreConfig.coreUsername = "scott";
        CoreDbConfiguration.TEST_ENV_CONFIGURATION = coreConfig;

        try (Connection dbaConnection = config.getDbaConnection()) {
            dbaConnection.setAutoCommit(true);
            dbaConnection.prepareStatement("DROP DATABASE IF EXISTS testme")
                         .execute();
            dbaConnection.prepareStatement("DROP ROLE IF EXISTS scott")
                         .execute();
            dbaConnection.prepareStatement("CREATE ROLE scott WITH SUPERUSER LOGIN PASSWORD 'tiger'")
                         .execute();
            dbaConnection.prepareStatement("CREATE DATABASE testme ENCODING 'UTF8'")
                         .execute();
            dbaConnection.prepareStatement("GRANT CREATE ON DATABASE testme TO scott")
                         .execute();
        }

        new BootstrapCommand().run(null, null);
        new ClearCommand().run(null, null);
        new BootstrapCommand().run(null, null);
        new RollbackCommand().run(null, null);
    }
}
