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

package com.chiralbehaviors.CoRE.kernel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import com.chiralbehaviors.CoRE.WellKnownObject;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownProduct;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;

/**
 * @author hhildebrand
 *
 */
public class BootstrapModel extends Bootstrap {

    public static final String KERNEL_3_WSP = "/kernel.3.wsp";

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

        Connection conn = DriverManager.getConnection((String) properties.get("url"),
                                                      (String) properties.get("user"),
                                                      (String) properties.get("password"));
        conn.setAutoCommit(false);

        try (DSLContext create = DSL.using(conn)) {
            boostrap(outputFile, new BootstrapModel(create));
        }
    }

    public BootstrapModel(DSLContext create) throws SQLException {
        super(create);
    }

    @Override
    protected Product constructKernelWorkspace() throws IOException,
                                                 SQLException {

        Product kernelWorkspace = super.constructKernelWorkspace();

        // Ain Soph
        Model model = new ModelImpl(create.configuration()
                                          .connectionProvider()
                                          .acquire());

        new WorkspaceImporter(getClass().getResourceAsStream(KERNEL_3_WSP),
                              model).initialize()
                                    .load(kernelWorkspace);
        ExistentialAttributeRecord attributeValue = model.getPhantasmModel()
                                                         .getAttributeValue(kernelWorkspace,
                                                                            model.getKernel()
                                                                                 .getIRI());
        model.getPhantasmModel()
             .setValue(attributeValue, WellKnownObject.KERNEL_IRI);

        // Ain Soph Aur

        return kernelWorkspace;

    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.kernel.Bootstrap#serialize(java.lang.String)
     */
    @Override
    protected void serialize(String fileName) throws IOException {
        Product kernelWorkspace = find(WellKnownProduct.KERNEL_WORKSPACE);
        WorkspaceSnapshot snapshot = new WorkspaceSnapshot(kernelWorkspace,
                                                           create);
        try (FileOutputStream os = new FileOutputStream(new File(fileName))) {
            snapshot.serializeTo(os);
        }
    }
}
