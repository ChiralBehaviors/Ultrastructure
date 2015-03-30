/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.kernel;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.UuidGenerator;
import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.workspace.RehydratedWorkspace;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Repository of immutable kernal rules
 *
 * This used to be the standard. Now we use workspaces. However, kernel is a
 * fundamental workspace, and it's needed a lot. Consequently, because of the
 * way we do Java stored procedures, reentrancy requires a new image of the
 * kernel workspace in the context of the entity manager. Sucks to be us.
 *
 * Utilities for the Kerenl
 *
 * @author hhildebrand
 *
 */
public class KernelUtil {

    public static Kernel cacheKernel(EntityManager em) {
        if (CACHED_KERNEL.get() != null) {
            return CACHED_KERNEL.get();
        }
        Kernel kernel;
        try (InputStream is = KernelUtil.class.getResourceAsStream(KernelUtil.KERNEL_WORKSPACE_RESOURCE)) {
            RehydratedWorkspace kernelSnapshot = readKernel(is);
            kernelSnapshot.replaceFrom(em);
            kernel = kernelSnapshot.getAccesor(Kernel.class);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to rehydrate kernel", e);
        }
        if (!CACHED_KERNEL.compareAndSet(null, kernel)) {
            log.debug("Kernel has already been cached");
            kernel = CACHED_KERNEL.get();
        }
        return kernel;
    }

    public static void clear(EntityManager em) throws SQLException {
        em.getTransaction().begin();
        Connection connection = em.unwrap(Connection.class);
        connection.setAutoCommit(false);
        alterTriggers(connection, false);
        ResultSet r = connection.createStatement().executeQuery(KernelUtil.SELECT_TABLE);
        while (r.next()) {
            String table = r.getString("name");
            String query = String.format("DELETE FROM %s", table);
            connection.createStatement().execute(query);
        }
        r.close();
        alterTriggers(connection, true);
        CACHED_KERNEL.set(null);
        em.getTransaction().commit();
    }

    public static Kernel clearAndLoadKernel(EntityManager em)
                                                             throws SQLException,
                                                             IOException {
        clear(em);
        return loadKernel(em);
    }

    public synchronized static Kernel getKernel() {
        Kernel kernel = CACHED_KERNEL.get();
        assert kernel != null;
        return kernel;
    }

    public static Kernel loadKernel(EntityManager em) throws IOException {
        return loadKernel(em,
                          KernelUtil.class.getResourceAsStream(KernelUtil.KERNEL_WORKSPACE_RESOURCE));
    }

    public static Kernel loadKernel(EntityManager em, InputStream is)
                                                                     throws IOException {
        em.getTransaction().begin();
        RehydratedWorkspace workspace = rehydrateKernel(is);
        workspace.retarget(em);
        Kernel kernel = workspace.getAccesor(Kernel.class);
        CACHED_KERNEL.set(kernel);
        em.getTransaction().commit();
        workspace.detach(em);
        return kernel;
    }

    private static RehydratedWorkspace readKernel(InputStream is)
                                                                 throws IOException,
                                                                 JsonParseException,
                                                                 JsonMappingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new CoREModule());
        RehydratedWorkspace workspace = mapper.readValue(is,
                                                         RehydratedWorkspace.class);
        return workspace;
    }

    private static RehydratedWorkspace rehydrateKernel(InputStream is)
                                                                      throws IOException,
                                                                      JsonParseException,
                                                                      JsonMappingException {
        RehydratedWorkspace workspace = readKernel(is);
        workspace.cache();
        return workspace;
    }

    static void alterTriggers(Connection connection, boolean enable)
                                                                    throws SQLException {
        for (String table : new String[] { "ruleform.agency",
                "ruleform.product", "ruleform.location" }) {
            String query = String.format("ALTER TABLE %s %s TRIGGER ALL",
                                         table, enable ? "ENABLE" : "DISABLE");
            connection.createStatement().execute(query);
        }
        ResultSet r = connection.createStatement().executeQuery(KernelUtil.SELECT_TABLE);
        while (r.next()) {
            String table = r.getString("name");
            String query = String.format("ALTER TABLE %s %s TRIGGER ALL",
                                         table, enable ? "ENABLE" : "DISABLE");
            connection.createStatement().execute(query);
        }
        r.close();
    }

    private static final Logger                  log                       = LoggerFactory.getLogger(KernelUtil.class);

    public static final String                   SELECT_TABLE              = "SELECT table_schema || '.' || table_name AS name FROM information_schema.tables WHERE table_schema='ruleform' AND table_type='BASE TABLE' ORDER BY table_name";

    public static final String                   ZERO                      = UuidGenerator.toBase64(new UUID(
                                                                                                             0,
                                                                                                             0));

    private static final AtomicReference<Kernel> CACHED_KERNEL             = new AtomicReference<>();

    static final String                          KERNEL_WORKSPACE_RESOURCE = "/kernel-workspace.json";
}
