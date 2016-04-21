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

package com.chiralbehaviors.CoRE.occular;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import com.chiralbehaviors.CoRE.phantasm.service.PhantasmApplication;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hellblazer.utils.Utils;

import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 * @author hhildebrand
 *
 */
public class OccularTest extends ApplicationTest {

    protected PhantasmApplication application = new PhantasmApplication();

    @After
    public void after() {
        application.stop();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.show();
    }

    @Test
    public void testIt() throws Exception {
        application.run("server", "target/test-classes/null.yml");
        ApplicationTest.launch(Occular.class);
        assertTrue(Utils.waitForCondition(5000,
                                          () -> lookup("#facets").query() != null));
        ListView<ObjectNode> facets = lookup("#facets").query();
        assertNotNull(facets);
        facets.getSelectionModel()
              .selectFirst();
    }
}
