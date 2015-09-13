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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.chiralbehaviors.CoRE.WellKnownObject;
import com.hellblazer.utils.Utils;

/**
 * @author hhildebrand
 *
 */
public class Configuration {
    private static final String JPA_TEMPLATE_PROPERTIES = "/jpa-template.properties";

    /**
     * the name the core database
     * 
     * @parameter
     */
    public String coreDb = "core";

    /**
     * the password of the core user
     * 
     * @parameter
     */
    public String corePassword;

    /**
     * the port of the core database
     * 
     * @parameter
     */
    public int corePort;

    /**
     * the server host of the core database
     * 
     * @parameter
     */
    public String coreServer;

    /**
     * the core user name
     * 
     * @parameter
     */
    public String coreUsername;

    // Used in testing to avoid creating emf and out of band txns
    private final transient EntityManagerFactory emf;

    public Configuration() {
        emf = null;
    }

    public Configuration(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManagerFactory getEmf() throws IOException {
        if (emf != null) {
            return emf;
        }
        String txfmd;
        try (InputStream is = getClass().getResourceAsStream(JPA_TEMPLATE_PROPERTIES)) {
            if (is == null) {
                throw new IllegalStateException("jpa properties missing");
            }
            Map<String, String> props = new HashMap<>();
            props.put("init.db.login", coreUsername);
            props.put("init.db.password", corePassword);
            props.put("init.db.server", coreServer);
            props.put("init.db.port", Integer.toString(corePort));
            props.put("init.db.database", coreDb);
            txfmd = Utils.getDocument(is, props);
        }
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(txfmd.getBytes()));
        return Persistence.createEntityManagerFactory(WellKnownObject.CORE,
                                                      properties);

    }

}
