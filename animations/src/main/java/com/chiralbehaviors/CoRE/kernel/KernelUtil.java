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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.EntityManager;

import org.hibernate.internal.SessionImpl;

import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceSnapshot;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module.Feature;
import com.hellblazer.utils.Utils;

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

    public static void clear(EntityManager em) throws SQLException {
        boolean committed = false;
        Connection connection = em.unwrap(SessionImpl.class)
                                  .connection();
        em.getTransaction()
          .begin();
        try {
            connection.setAutoCommit(false);
            connection.createStatement()
                      .execute("TRUNCATE TABLE ruleform.agency CASCADE");
            ResultSet r = connection.createStatement()
                                    .executeQuery(KernelUtil.SELECT_TABLE);
            while (r.next()) {
                String table = r.getString("name");
                String query = String.format("TRUNCATE TABLE %s CASCADE",
                                             table);
                connection.createStatement()
                          .execute(query);
            }
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
        loadKernel(em,
                   getBits(KernelUtil.class.getResourceAsStream(KernelUtil.KERNEL_WORKSPACE_RESOURCE)));
    }

    public static void loadKernel(EntityManager em,
                                  InputStream is) throws IOException {
        if (!em.getTransaction()
               .isActive()) {
            em.getTransaction()
              .begin();
        }
        WorkspaceSnapshot workspace = rehydrateKernel(is);
        workspace.retarget(em);
        em.getTransaction()
          .commit();
    }

    /**
     * This is a work around, as there's a weird edge case where the resource
     * has been closed by someone (probably dropwizard).
     * 
     * @param resourceAsStream
     * @return
     * @throws IOException
     */
    private static InputStream getBits(InputStream resourceAsStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Utils.copy(resourceAsStream, baos);
        return new ByteArrayInputStream(baos.toByteArray());
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
}
