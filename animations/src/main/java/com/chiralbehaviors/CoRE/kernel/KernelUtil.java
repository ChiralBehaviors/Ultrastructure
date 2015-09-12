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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import javax.persistence.EntityManager;

import org.hibernate.internal.SessionImpl;

import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceSnapshot;

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

    public static final String KERNEL_WORKSPACE_RESOURCE = "/kernel-workspace.2.json";

    public static final String SELECT_TABLE = "SELECT table_schema || '.' || table_name AS name FROM information_schema.tables WHERE table_schema='ruleform' AND table_type='BASE TABLE' ORDER BY table_name";

    public static void clear(EntityManager em) throws SQLException {
        boolean committed = false;
        Connection connection = em.unwrap(SessionImpl.class)
                                  .connection();
        em.getTransaction()
          .begin();
        try {
            connection.setAutoCommit(false);
            ResultSet r = connection.createStatement()
                                    .executeQuery(KernelUtil.SELECT_TABLE);
            while (r.next()) {
                String table = r.getString("name");
                if (!table.equals("ruleform.agency")) {
                    String query = String.format("TRUNCATE TABLE %s CASCADE",
                                                 table);
                    connection.createStatement()
                              .execute(query);
                }
            }
            connection.createStatement()
                      .execute("TRUNCATE TABLE ruleform.agency CASCADE");
            r.close();
            connection.commit();
            em.getTransaction()
              .commit();
            committed = true;
        } finally {
            if (!committed) {
                connection.rollback();
                em.getTransaction()
                  .rollback();
            }
        }
    }

    public static void clearAndLoadKernel(EntityManager em) throws SQLException,
                                                            IOException {
        clear(em);
        loadKernel(em);
    }

    public static void loadKernel(EntityManager em) throws IOException {
        if (!em.getTransaction()
               .isActive()) {
            em.getTransaction()
              .begin();
        }
        WorkspaceSnapshot.load(em,
                               Arrays.asList(KernelUtil.class.getResource(KERNEL_WORKSPACE_RESOURCE)));
        em.getTransaction()
          .commit();
    }
}
