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

package com.chiralbehaviors.CoRE.meta.workspace;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.phantasm.TestPhantasm;
import com.chiralbehaviors.CoRE.phantasm.test.location.MavenArtifact;
import com.chiralbehaviors.CoRE.phantasm.test.product.Thing1;
import com.chiralbehaviors.CoRE.phantasm.test.product.Thing2;
import com.chiralbehaviors.CoRE.phantasm.test.product.Thing3;
import com.chiralbehaviors.CoRE.workspace.StateSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author hhildebrand
 *
 */
public class StateSnapshotTest extends AbstractModelTest {

    @Before
    public void before() throws Exception {
        try {
            WorkspaceImporter.manifest(TestPhantasm.class.getResourceAsStream("/thing.wsp"),
                                       model);
        } catch (IllegalStateException e) {
            LoggerFactory.getLogger(TestPhantasm.class)
                         .warn(String.format("Not loading thing workspace: %s",
                                             e.getMessage()));
        }
    }

    @Test
    public void testSnap() throws Exception {

        Thing1 thing1 = model.construct(Thing1.class, "testy", "test");
        Thing2 thing2 = model.construct(Thing2.class, "tasty", "chips");
        thing1.setThing2(thing2);
        thing1.setPercentage(BigDecimal.ONE);
        String[] aliases = new String[] { "foo", "bar", "baz" };
        thing1.setAliases(aliases);
        em.flush();

        Map<String, String> properties = new HashMap<>();
        properties.put("foo", "bar");
        properties.put("baz", "bozo");
        thing1.setProperties(properties);
        em.flush();

        Thing3 thing3a = model.construct(Thing3.class, "uncle it",
                                         "one of my favorite things");
        thing2.addThing3(thing3a);

        Thing3 thing3 = model.construct(Thing3.class, "cousin it",
                                        "another one of my favorite things");

        List<Thing3> aFewOfMyFavoriteThings = new ArrayList<>();
        aFewOfMyFavoriteThings.add(thing3a);
        aFewOfMyFavoriteThings.add(thing3);

        thing2.addThing3s(aFewOfMyFavoriteThings);
        MavenArtifact artifact = model.construct(MavenArtifact.class,
                                                 "myartifact", "artifact");
        artifact.setType("jar");
        em.flush();
        thing1.setDerivedFrom(artifact);
        em.flush();
        thing2.addDerivedFroms(Arrays.asList(artifact));

        MavenArtifact artifact2 = model.construct(MavenArtifact.class,
                                                  "myartifact2", "artifact2");
        artifact2.setType("jar");

        thing2.setDerivedFroms(Arrays.asList(artifact2));
        thing2.addDerivedFrom(artifact);
        StateSnapshot snap = new StateSnapshot(em);
        try (OutputStream os = new FileOutputStream(TARGET_CLASSES_THINGS_JSON)) {
            new ObjectMapper().registerModule(new CoREModule())
                              .writeValue(os, snap);
        }
    }
}