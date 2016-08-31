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

package com.chiralbehaviors.CoRE.ocular;

import org.junit.After;
import org.junit.Test;

import com.chiralbehaviors.CoRE.phantasm.service.PhantasmApplication;
import com.chiralbehaviors.graphql.layout.AutoLayout;
import com.hellblazer.utils.Utils;

import graphql.language.Document;
import graphql.parser.Parser;

/**
 * @author hhildebrand
 *
 */
public class TestParser {

    protected PhantasmApplication application = new PhantasmApplication();

    @After
    public void after() {
        application.stop();
    }

    @Test
    public void testSimple() throws Exception {
        String input = Utils.getDocument(Utils.resolveResource(TestParser.class,
                                                               "/testQuery.gql"));
        AutoLayout.buildLayout(input, "hero");
    }

    @Test
    public void testIt() throws Exception {
        String input = Utils.getDocument(Utils.resolveResource(TestParser.class,
                                                               "/testQuery.gql"));
        Document document = new Parser().parseDocument(input);

        System.out.println(document);
    }
}
