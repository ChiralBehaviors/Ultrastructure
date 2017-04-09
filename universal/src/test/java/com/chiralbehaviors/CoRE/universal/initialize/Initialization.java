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
import java.util.UUID;

import org.junit.Test;

import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeRecord;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.singlePgApp.Launch;
import com.chiralbehaviors.CoRE.singlePgApp.Page;
import com.chiralbehaviors.CoRE.singlePgApp.SinglePageApp;
import com.chiralbehaviors.CoRE.universal.Universal;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;
import com.hellblazer.utils.Utils;

/**
 * 
 * @author halhildebrand
 *
 */
public class Initialization extends AbstractModelTest {
    private static final String SRC_MAIN_RESOURCES     = "src/main/resources";
    private static final String LAUNCH                 = "launch";
    private static final String GET_APPLICATIONS_QUERY = "getApplications.query";
    private static final String SINGLE_PAGE_1_JSON     = "single-page.1.json";
    private static final String SINGLE_PAGE_1_WSP      = "single-page.1.wsp";
    private static final String APP_LAUNCHER           = "AppLauncher";

    @Test
    public void initializeApplicationBrowser() throws Exception {

        WorkspaceScope spa = WorkspaceImporter.manifest(Universal.class.getResourceAsStream(SINGLE_PAGE_1_WSP),
                                                        model)
                                              .getWorkspace()
                                              .getScope();
        SinglePageApp browser = model.wrap(SinglePageApp.class,
                                           spa.lookup(APP_LAUNCHER));
        Page allApplications = model.wrap(Page.class,
                                          spa.lookup("AllApplications"));
        allApplications.setQuery(Utils.getDocument(Universal.class.getResourceAsStream(GET_APPLICATIONS_QUERY)));

        Launch launchApp = model.wrap(Launch.class,
                                      spa.lookup("LaunchApplication"));

        launchApp.setLaunchBy("id");

        allApplications.setName("All applications");
        allApplications.setDescription("Page with all applications");
        allApplications.setTitle("Appplications");
        allApplications.setRelationOfLaunchLaunch(launchApp,
                                                  "singlePageApplications");

        browser.setName("Application Browser");
        browser.setDescription("Initial application browser and launcher");
        browser.setRouteOfPagePage(allApplications, LAUNCH);
        browser.setRoot(LAUNCH);

        WorkspaceSnapshot snap = model.snapshot();
        UUID workspaceId = spa.getWorkspace()
                              .getDefiningProduct()
                              .getId();
        snap.getRecords()
            .forEach(r -> {
                ((ExistentialNetworkAttributeRecord) r).setWorkspace(workspaceId);
            });
        snap.getRecords()
            .forEach(r -> r.update());

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
