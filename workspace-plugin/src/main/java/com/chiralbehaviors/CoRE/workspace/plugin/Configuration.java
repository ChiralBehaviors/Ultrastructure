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

package com.chiralbehaviors.CoRE.workspace.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jooq.DSLContext;
import org.jooq.util.postgres.PostgresDSL;

import com.chiralbehaviors.CoRE.utils.CoreDbConfiguration;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * @author hhildebrand
 *
 */
public class Configuration extends CoreDbConfiguration {

    public static Configuration fromYaml(InputStream yaml) throws JsonParseException,
                                                           JsonMappingException,
                                                           IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(yaml, Configuration.class);
    }

    // Used in testing to avoid creating emf and out of band txns
    private transient DSLContext create;

    public Configuration() {
        create = null;
    }

    public Configuration(DSLContext create) {
        this.create = create;
    }

    public DSLContext getCreate() throws IOException, SQLException {
        if (create != null) {
            return create;
        }
        if (corePassword == null) {
            initializeFromEnvironment();
        }

        String url = String.format("jdbc:postgresql://%s:%s/%s", coreServer,
                                   corePort, coreDb);
        System.out.println(String.format(" ---------> Connecting to DB: %s",
                                         url));
        Connection conn = DriverManager.getConnection(url, coreUsername,
                                                      corePassword);
        return PostgresDSL.using(conn);

    }

    public void set(DSLContext create) {
        this.create = create;
    }
}
