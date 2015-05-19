/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.phantasm.generator;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceImporter;

/**
 * @author hhildebrand
 *
 */
public class TestGenerator extends AbstractModelTest {
    private static final String THING_WSP = "/thing.wsp";

    @Before
    public void initializeWorkspace() throws IOException {
        model.getEntityManager().getTransaction().begin();
        WorkspaceImporter.createWorkspace(TestGenerator.class.getResourceAsStream(THING_WSP),
                                          model);
        model.getEntityManager().flush();
    }

    @Test
    public void testIt() throws IOException {
        Configuration configuration = new Configuration();
        configuration.resource = THING_WSP;
        PhantasmGenerator generator = new PhantasmGenerator(model,
                                                            configuration);
        generator.generate();
    }
}
