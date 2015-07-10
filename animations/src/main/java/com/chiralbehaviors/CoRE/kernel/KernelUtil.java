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

package com.chiralbehaviors.CoRE.kernel;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.EntityManager;

import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceSnapshot;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module.Feature;

/**
 * Repository of immutable kernal rules
 *
 * This used to be the standard. Now we use workspaces. However, kernel is a
 * fundamental workspace, and it's needed a lot. Consequently, because of the
 * way we do Java stored procedures, reentrancy requires a new image of the
 * kernel workspace in the context of the entity manager. Sucks to be us.
 *
 * Utilities for the Kernel
 *
 * @author hhildebrand
 *
 */
public class KernelUtil {

    public static final String KERNEL_WORKSPACE_RESOURCE = "/kernel-workspace.json";

    public static final String SELECT_TABLE = "SELECT table_schema || '.' || table_name AS name FROM information_schema.tables WHERE table_schema='ruleform' AND table_type='BASE TABLE' ORDER BY table_name";

    public static void loadKernel(EntityManager em) throws IOException {
        loadKernel(em,
                   KernelUtil.class.getResourceAsStream(KernelUtil.KERNEL_WORKSPACE_RESOURCE));
    }

    public static void clear(Connection connection) throws SQLException {
        boolean committed = false;
        try {
            connection.setAutoCommit(false);
            // alterTriggers(connection, false);
            ResultSet r = connection.createStatement().executeQuery(KernelUtil.SELECT_TABLE);
            while (r.next()) {
                String table = r.getString("name");
                String query = String.format("DELETE FROM %s", table);
                connection.createStatement().execute(query);
            }
            r.close();
            // KernelUtil.alterTriggers(connection, true);
            connection.commit();
            committed = true;
        } finally {
            if (!committed) {
                connection.rollback();
            }
        }
    }

    public static void loadKernel(EntityManager em,
                                  InputStream is) throws IOException {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
        WorkspaceSnapshot workspace = rehydrateKernel(is);
        workspace.retarget(em);
        em.getTransaction().commit();
    }

    private static WorkspaceSnapshot rehydrateKernel(InputStream is) throws IOException,
                                                                     JsonParseException,
                                                                     JsonMappingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new CoREModule());
        Hibernate4Module module = new Hibernate4Module();
        module.enable(Feature.FORCE_LAZY_LOADING);
        mapper.registerModule(module);
        WorkspaceSnapshot workspace = mapper.readValue(is,
                                                       WorkspaceSnapshot.class);
        return workspace;
    }

    public static void alterTriggers(Connection connection,
                                     boolean enable) throws SQLException {
        ResultSet r = connection.createStatement().executeQuery(KernelUtil.SELECT_TABLE);
        while (r.next()) {
            String table = r.getString("name");
            String query = String.format("ALTER TABLE %s %s TRIGGER ALL", table,
                                         enable ? "ENABLE" : "DISABLE");
            connection.createStatement().execute(query);
        }
        r.close();
    }

    public static void clearAndLoadKernel(EntityManager em,
                                          Connection connection) throws SQLException,
                                                                 IOException {
        clear(connection);
        loadKernel(em);
    }
}
