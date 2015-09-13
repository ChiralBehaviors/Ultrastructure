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
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;

/**
 * @author hhildebrand
 *
 */
public class WorspaceSnapshotLoaderTest extends AbstractModelTest {

    @Test
    public void testLoad() throws Exception {
        try {
            model.getWorkspaceModel()
                 .getScoped(WorkspaceAccessor.uuidOf(THING_URI));
            fail("Should not exist");
        } catch (IllegalArgumentException e) {
            // expected
        }
        Configuration config = Configuration.fromYaml(getClass().getResourceAsStream("/db-configuration.yml"));
        config.setEmf(mockedEmf());
        List<String> toLoad = Arrays.asList("/thing.1.json", "/thing.2.json");
        WorkspaceSnapshotLoader loader = new WorkspaceSnapshotLoader(config,
                                                                     toLoad);
        loader.execute();
        WorkspaceScope scope = model.getWorkspaceModel()
                                    .getScoped(WorkspaceAccessor.uuidOf(THING_URI));
        assertNotNull(scope.lookup("TheDude"));

    }
}
