/**
 * Copyright (c) 2017 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.universal.initialize;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;

import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.universal.Universal;

/**
 * 
 * @author halhildebrand
 *
 */
public class Initialization extends AbstractModelTest {
    private static final String SRC_MAIN_RESOURCES = "src/main/resources";
    private static final String SINGLE_PAGE_1_JSON = "single-page.1.json";
    private static final String SINGLE_PAGE_1_WSP  = "single-page.1.wsp";

    @Test
    public void initializeApplicationBrowser() throws Exception {

        WorkspaceScope spa = WorkspaceImporter.manifest(Universal.class.getResourceAsStream(SINGLE_PAGE_1_WSP),
                                                        model)
                                              .getWorkspace()
                                              .getScope();
        assertEquals(0, model.snapshot()
                             .getRecords()
                             .size());

        File universalDir = new File(SRC_MAIN_RESOURCES,
                                     Universal.class.getPackage()
                                                    .getName()
                                                    .replace('.', '/'));
        try (FileOutputStream os = new FileOutputStream(new File(universalDir,
                                                                 SINGLE_PAGE_1_JSON))) {
            spa.getWorkspace()
               .getSnapshot()
               .serializeTo(os);
        }

    }

    @Test
    public void initializeWorkspaceBrowser() {

    }
}
