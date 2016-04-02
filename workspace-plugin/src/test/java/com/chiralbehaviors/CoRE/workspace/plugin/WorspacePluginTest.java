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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;
import com.chiralbehaviors.CoRE.workspace.plugin.WorkspaceSnapshotGenerator.Export;
import com.hellblazer.utils.Utils;

/**
 * @author hhildebrand
 *
 */
public class WorspacePluginTest extends AbstractModelTest {

    @Test
    public void testDslLoad() throws Exception {
        assertNull(model.getWorkspaceModel()
                        .getScoped(WorkspaceAccessor.uuidOf(THING_URI)));
        Configuration config = Configuration.fromYaml(getClass().getResourceAsStream("/db-configuration.yml"));
        config.set(model.create());
        List<String> toLoad = Arrays.asList("/thing.wsp", "/thing.2.wsp");
        WorkspaceDslLoader loader = new WorkspaceDslLoader(config, toLoad);
        loader.execute();
        WorkspaceScope scope = model.getWorkspaceModel()
                                    .getScoped(WorkspaceAccessor.uuidOf(THING_URI));
        assertNotNull(scope.lookup("TheDude"));

    }

    @Test
    public void testSnapshotGenerator() throws Exception {
        Configuration config = Configuration.fromYaml(getClass().getResourceAsStream("/db-configuration.yml"));
        config.set(model.create());
        URL resource = Utils.resolveResourceURL(getClass(), "/thing.1.json");
        assertNotNull(resource);
        List<URL> toLoad = Arrays.asList(resource);
        WorkspaceSnapshot.load(model.create(), toLoad);
        File cloneFileName = new File("target/thing.1.clone.json");
        cloneFileName.delete();
        List<Export> exports = Arrays.asList(new Export(THING_URI,
                                                        cloneFileName));
        WorkspaceSnapshotGenerator generator = new WorkspaceSnapshotGenerator(config,
                                                                              exports);
        generator.execute();
        assertTrue("Clone did not generate", cloneFileName.exists());
    }

    @Test
    public void testSnapshotLoad() throws Exception {
        assertNull(model.getWorkspaceModel()
                        .getScoped(WorkspaceAccessor.uuidOf(THING_URI)));
        Configuration config = Configuration.fromYaml(getClass().getResourceAsStream("/db-configuration.yml"));
        config.set(model.create());
        List<String> toLoad = Arrays.asList("/thing.1.json", "/thing.1.2.json");
        WorkspaceSnapshotLoader loader = new WorkspaceSnapshotLoader(config,
                                                                     toLoad);
        loader.execute();
        WorkspaceScope scope = model.getWorkspaceModel()
                                    .getScoped(WorkspaceAccessor.uuidOf(THING_URI));
        assertNotNull(scope.lookup("TheDude"));

    }
}
