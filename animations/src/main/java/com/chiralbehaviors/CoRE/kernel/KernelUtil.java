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
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;

import com.chiralbehaviors.CoRE.kernel.phantasm.agency.CoreInstance;
import com.chiralbehaviors.CoRE.kernel.phantasm.agency.ThisCoreInstance;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;

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

    public static final List<URL> KERNEL_LOADS;
    public static final String    SELECT_TABLE    = "SELECT table_schema || '.' || table_name AS name FROM information_schema.tables WHERE table_schema='ruleform' AND table_type='BASE TABLE' ORDER BY table_name";

    private static final String[] KERNEL_VERSIONS = { "/kernel.2.json" };

    static {
        KERNEL_LOADS = Collections.unmodifiableList(Arrays.asList(KERNEL_VERSIONS)
                                                          .stream()
                                                          .map(s -> KernelUtil.class.getResource(s))
                                                          .collect(Collectors.toList()));
    }

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

    public static void clearAndLoadKernel(DSLContext em) throws SQLException,
                                                         IOException {
        clear(em);
        loadKernel(em);
    }

    public static void initializeInstance(Model model, String name,
                                          String description) throws InstantiationException {
        ThisCoreInstance core = model.construct(ThisCoreInstance.class, name,
                                                description);
        model.apply(CoreInstance.class, core);
    }

    public static void loadKernel(DSLContext em) throws IOException {
        if (!em.getTransaction()
               .isActive()) {
            em.getTransaction()
              .begin();
        }
        WorkspaceSnapshot.load(em, KERNEL_LOADS);
        em.getTransaction()
          .commit();
    }
}
