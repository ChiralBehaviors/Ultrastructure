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

package com.chiralbehaviors.phantasm;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductRelationship;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceLexer;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.WorkspaceContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspacePresentation;
import com.chiralbehaviors.phantasm.demo.MavenArtifact;
import com.chiralbehaviors.phantasm.demo.Thing1;
import com.chiralbehaviors.phantasm.demo.Thing2;
import com.chiralbehaviors.phantasm.demo.Thing3;

/**
 * @author hhildebrand
 *
 */
public class TestPhantasm extends AbstractModelTest {

    @Before
    public void before() throws Exception {
        WorkspaceLexer l = new WorkspaceLexer(
                                              new ANTLRInputStream(
                                                                   getClass().getResourceAsStream("/thing.wsp")));
        WorkspaceParser p = new WorkspaceParser(new CommonTokenStream(l));
        p.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer,
                                    Object offendingSymbol, int line,
                                    int charPositionInLine, String msg,
                                    RecognitionException e) {
                throw new IllegalStateException("failed to parse at line "
                                                + line + " due to " + msg, e);
            }
        });
        WorkspaceContext ctx = p.workspace();

        WorkspaceImporter importer = new WorkspaceImporter(
                                                           new WorkspacePresentation(
                                                                                     ctx),
                                                           model);
        em.getTransaction().begin();
        importer.loadWorkspace();
        em.flush();
    }

    @Test
    public void testDemo() throws Exception {

        Thing1 thing1 = (Thing1) model.construct(Thing1.class, "testy", "test");
        Thing2 thing2 = (Thing2) model.construct(Thing2.class, "tasty", "chips");
        assertNotNull(thing1);
        assertEquals(thing1, thing1.doSomethingElse());
        thing1.doSomething("hello");
        assertNotNull(thing1.getRuleform());
        assertEquals(thing1.getRuleform().getName(), thing1.getName());
        assertNull(thing1.getThing2());
        thing1.setThing2(thing2);
        assertNotNull(thing1.getThing2());
        assertEquals(thing1, thing1.scopedAccess());
        assertNull(thing1.getPercentage());
        thing1.setPercentage(BigDecimal.ONE);
        assertEquals(BigDecimal.ONE, thing1.getPercentage());
        String[] aliases = new String[] { "foo", "bar", "baz" };
        thing1.setAliases(aliases);
        em.flush();
        String[] alsoKnownAs = thing1.getAliases();
        assertNotNull(alsoKnownAs);
        assertArrayEquals(aliases, alsoKnownAs);

        Map<String, String> properties = new HashMap<>();
        properties.put("foo", "bar");
        properties.put("baz", "bozo");

        assertEquals(0, thing1.getProperties().size());
        thing1.setProperties(properties);
        em.flush();
        Map<String, String> newProps = thing1.getProperties();
        assertEquals(String.format("got: %s", newProps), properties.size(),
                     newProps.size());

        Thing3 thing3a = (Thing3) model.construct(Thing3.class, "uncle it",
                                                  "one of my favorite things");
        assertNotNull(thing2.getThing3s());
        assertEquals(0, thing2.getThing3s().size());
        thing2.add(thing3a);
        assertEquals(1, thing2.getThing3s().size());
        thing2.remove(thing3a);
        assertEquals(0, thing2.getThing3s().size());

        Thing3 thing3b = (Thing3) model.construct(Thing3.class, "cousin it",
                                                  "another one of my favorite things");

        List<Thing3> aFewOfMyFavoriteThings = new ArrayList<>();
        aFewOfMyFavoriteThings.add(thing3a);
        aFewOfMyFavoriteThings.add(thing3b);

        thing2.add(aFewOfMyFavoriteThings);
        assertEquals(2, thing2.getThing3s().size());
        thing2.remove(aFewOfMyFavoriteThings);
        assertEquals(0, thing2.getThing3s().size());

        assertNull(thing1.getArtifact());
        MavenArtifact artifact = (MavenArtifact) model.construct(MavenArtifact.class,
                                                                 "myartifact",
                                                                 "artifact");
        assertEquals("jar", artifact.getType());
        em.flush();
        thing1.setArtifact(artifact);
        em.flush();
        assertNotNull(thing1.getArtifact());

        assertEquals(0, thing2.getArtifacts().size());
        thing2.addArtifact(artifact);
        assertEquals(1, thing2.getArtifacts().size());
        thing2.removeArtifact(artifact);
        assertEquals(0, thing2.getArtifacts().size());
        thing2.addArtifacts(Arrays.asList(artifact));
        assertEquals(1, thing2.getArtifacts().size());

        MavenArtifact artifact2 = (MavenArtifact) model.construct(MavenArtifact.class,
                                                                  "myartifact2",
                                                                  "artifact2");
        artifact2.setType("jar");

        thing2.setArtifacts(Arrays.asList(artifact2));
        assertEquals(1, thing2.getArtifacts().size());
        thing2.addArtifact(artifact);
        assertEquals(2, thing2.getArtifacts().size());
        thing2.setArtifacts(Collections.emptyList());
        assertEquals(0, thing2.getArtifacts().size());

        assertNotNull(thing1.getScope());
    }

    @Test
    public void testEnums() throws Exception {
        MavenArtifact artifact = (MavenArtifact) model.construct(MavenArtifact.class,
                                                                 "myartifact",
                                                                 "artifact");
        artifact.setType("jar");
        em.flush();
        artifact.setType("invalid");
        try {
            em.flush();
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testThis() throws InstantiationException {

        Thing1 thing1 = (Thing1) model.construct(Thing1.class, "testy", "test");
        Product workspace = thing1.getScope().getWorkspace().getDefiningProduct();
        Product child = model.getProductModel().getChild(workspace,
                                                         model.getKernel().getHasMember());
        assertEquals("Thing1", child.getName());
        TypedQuery<Product> query = em.createNamedQuery(ProductRelationship.PRODUCTS_AT_RELATIONSHIP,
                                                        Product.class);
        query.setParameter("relationship", kernel.getHasMember());
        query.setParameter("child",
                           thing1.getScope().getWorkspace().get("derivedFrom"));
        assertNotNull(query.getSingleResult());

    }

}
