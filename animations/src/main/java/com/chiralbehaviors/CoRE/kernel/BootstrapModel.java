/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
 *
 *
 * This file is part of Ultrastructure.
 *
 * Ultrastructure is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * ULtrastructure is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Ultrastructure.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.kernel;

import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownProduct;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.meta.workspace.JsonImporter;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author hhildebrand
 *
 */
public class BootstrapModel extends Bootstrap {

    public static final String KERNEL_DEF_3_JSON = "/kernel-def.3.json";

    public BootstrapModel(DSLContext create) throws SQLException {
        super(create);
    }

    public static void main(String[] argv) throws Exception {
        if (argv.length != 2) {
            System.err.println("Usage: Bootstrap <db.properties> <output file>");
            System.exit(1);
        }
        Properties properties = new Properties();
        String outputFile = argv[1];
        try (InputStream is = new FileInputStream(new File(argv[0]))) {
            properties.load(is);
        }

        Connection conn = DriverManager.getConnection((String) properties.get("url"), (String) properties.get("user"),
                                                      (String) properties.get("password"));
        conn.setAutoCommit(false);

        DSLContext create = DSL.using(conn, SQLDialect.POSTGRES);
        boostrap(outputFile, new BootstrapModel(create));
    }

    @Override
    protected Product constructKernelWorkspace() throws IOException, SQLException {

        Product kernelWorkspace = super.constructKernelWorkspace();

        // Ain Soph
        Model model = new ModelImpl(create.configuration().connectionProvider().acquire());

        new JsonImporter(getClass().getResourceAsStream(KERNEL_DEF_3_JSON), model).initialize().load(kernelWorkspace);

        // Ain Soph Aur

        return kernelWorkspace;

    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.kernel.Bootstrap#serialize(java.lang.String)
     */
    @Override
    protected void serialize(String fileName) throws IOException {
        Product kernelWorkspace = find(WellKnownProduct.KERNEL_WORKSPACE);
        WorkspaceSnapshot snapshot = new WorkspaceSnapshot(kernelWorkspace, create);
        try (FileOutputStream os = new FileOutputStream(new File(fileName))) {
            snapshot.serializeTo(os);
        }
    }
}
