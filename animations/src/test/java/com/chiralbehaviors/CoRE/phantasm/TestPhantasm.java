/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 *

 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.phantasm;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.ExistentialNetworkAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.records.AgencyExistentialGroupingRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.phantasm.test.location.MavenArtifact;
import com.chiralbehaviors.CoRE.phantasm.test.product.Thing1;
import com.chiralbehaviors.CoRE.phantasm.test.product.Thing2;
import com.chiralbehaviors.CoRE.phantasm.test.product.Thing3;

/**
 * @author hhildebrand
 *
 */
public class TestPhantasm extends AbstractModelTest {

    @BeforeClass
    public static void before() throws Exception {
        create.transaction(c -> {
            try {
                WorkspaceImporter.manifest(TestPhantasm.class.getResourceAsStream("/thing.wsp"),
                                           model);
            } catch (IllegalStateException e) {
                LoggerFactory.getLogger(TestPhantasm.class)
                             .warn(String.format("Not loading thing workspace: %s",
                                                 e.getMessage()));
            }
        });

    }

    @Test
    public void testAttributeCapabilities() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, "testy", "test");

        WorkspaceScope scope = thing1.getScope();
        FacetRecord facet = model.getPhantasmModel()
                                 .getFacetDeclaration(kernel.getIsA(),
                                                      scope.lookup("Thing1"));
        assertNotNull(facet);
        Attribute percentage = (Attribute) scope.lookup("discount");
        assertNotNull(percentage);

        TypedQuery<ExistentialAttributeAuthorizationRecord> query = em.createQuery("select paa from ExistentialAttributeAuthorizationRecord paa "
                                                                                   + "where paa.networkAuthorization = :a "
                                                                                   + "and paa.authorizedAttribute = :b",
                                                                                   ExistentialAttributeAuthorizationRecord.class);
        query.setParameter("a", facet);
        query.setParameter("b", percentage);
        ExistentialAttributeAuthorizationRecord stateAuth = query.getSingleResult();
        assertNotNull(stateAuth);

        assertTrue(model.getPhantasmModel()
                        .checkCapability(asList(kernel.getCore()), stateAuth,
                                         kernel.getHadMember()));

        ExistentialAttributeAuthorizationRecord accessAuth = model.records()
                                                                  .newExistentialAttributeAuthorization(kernel.getCore());
        accessAuth.setAuthorizedAttribute(stateAuth.getAuthorizedAttribute());
        accessAuth.setFacet(stateAuth.getFacet());
        accessAuth.setAuthority(kernel.getAnyAgency()
                                      .getId());
        accessAuth.insert();

        assertFalse(model.getPhantasmModel()
                         .checkCapability(asList(kernel.getCore()), stateAuth,
                                          kernel.getHadMember()));

        model.getPhantasmModel()
             .link(kernel.getCore(), kernel.getHadMember(),
                   kernel.getAnyAgency(), kernel.getCore());

        assertTrue(model.getPhantasmModel()
                        .checkCapability(asList(kernel.getCore()), stateAuth,
                                         kernel.getHadMember()));

        accessAuth = model.records()
                          .newExistentialAttributeAuthorization(kernel.getCore());
        accessAuth.setAuthorizedAttribute(stateAuth.getAuthorizedAttribute());
        accessAuth.setFacet(stateAuth.getFacet());
        accessAuth.setAuthority(kernel.getSameAgency()
                                      .getId());
        accessAuth.insert();

        assertFalse(model.getPhantasmModel()
                         .checkCapability(asList(kernel.getCore()), stateAuth,
                                          kernel.getHadMember()));

        model.getPhantasmModel()
             .link(kernel.getCore(), kernel.getHadMember(),
                   kernel.getSameAgency(), kernel.getCore());

        assertTrue(model.getPhantasmModel()
                        .checkCapability(asList(kernel.getCore()), stateAuth,
                                         kernel.getHadMember()));
    }

    @Test
    public void testChildCapabilities() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, "testy", "test");

        WorkspaceScope scope = thing1.getScope();
        FacetRecord facet = model.getPhantasmModel()
                                 .getFacetDeclaration(kernel.getIsA(),
                                                      scope.lookup("Thing1"));
        assertNotNull(facet);

        TypedQuery<ExistentialNetworkAuthorization> query = em.createQuery("select auth from ExistentialNetworkAuthorization auth "
                                                                           + "where auth.classifier = :classifier "
                                                                           + "and auth.classification = :classification "
                                                                           + "and auth.childRelationship = :relationship "
                                                                           + "and auth.authorizedRelationship = :authRel "
                                                                           + "and auth.authorizedParent = :authParent ",
                                                                           ExistentialNetworkAuthorization.class);
        query.setParameter("classifier", kernel.getIsA());
        query.setParameter("classification", scope.lookup("Thing1"));
        query.setParameter("relationship", scope.lookup("thing1Of"));
        query.setParameter("authRel", kernel.getIsA());
        query.setParameter("authParent", scope.lookup("Thing2"));
        ExistentialNetworkAuthorizationRecord stateAuth = query.getSingleResult();
        assertNotNull(stateAuth);

        assertTrue(model.getPhantasmModel()
                        .checkCapability(asList(kernel.getCore()), stateAuth,
                                         kernel.getHadMember()));

        ExistentialNetworkAuthorizationRecord accessAuth = new ExistentialNetworkAuthorization(kernel.getCore());
        accessAuth.setParent(stateAuth.getParent());
        accessAuth.setRelationship(stateAuth.getRelationship());
        accessAuth.setChild(stateAuth.getChild());
        accessAuth.setAuthority(kernel.getAnyAgency()
                                      .getId());
        em.persist(accessAuth);

        assertFalse(model.getPhantasmModel()
                         .checkCapability(asList(kernel.getCore()), stateAuth,
                                          kernel.getHadMember()));

        model.getPhantasmModel()
             .link(kernel.getCore(), kernel.getHadMember(),
                   kernel.getAnyAgency(), kernel.getCore());

        assertTrue(model.getPhantasmModel()
                        .checkCapability(asList(kernel.getCore()), stateAuth,
                                         kernel.getHadMember()));

        accessAuth = new ExistentialNetworkAuthorization(kernel.getCore());
        accessAuth.setParent(stateAuth.getParent());
        accessAuth.setRelationship(stateAuth.getRelationship());
        accessAuth.setChild(stateAuth.getChild());
        accessAuth.setAuthority(kernel.getSameAgency()
                                      .getId());
        accessAuth.insert();

        assertFalse(model.getPhantasmModel()
                         .checkCapability(asList(kernel.getCore()), stateAuth,
                                          kernel.getHadMember()));

        model.getPhantasmModel()
             .link(kernel.getCore(), kernel.getHadMember(),
                   kernel.getSameAgency(), kernel.getCore());

        assertTrue(model.getPhantasmModel()
                        .checkCapability(asList(kernel.getCore()), stateAuth,
                                         kernel.getHadMember()));
    }

    @Test
    public void testThingOntology() throws Exception {

        Thing1 thing1 = model.construct(Thing1.class, "testy", "test");
        Thing2 thing2 = model.construct(Thing2.class, "tasty", "chips");
        assertNotNull(thing1);
        assertNotNull(thing1.getRuleform());
        assertEquals(thing1.getRuleform()
                           .getName(),
                     thing1.getName());
        assertNull(thing1.getThing2());
        thing1.setThing2(thing2);
        assertNotNull(thing1.getThing2());
        assertNull(thing1.getPercentage());
        thing1.setPercentage(BigDecimal.ONE);
        assertEquals(BigDecimal.ONE, thing1.getPercentage());
        String[] aliases = new String[] { "foo", "bar", "baz" };
        thing1.setAliases(aliases);
        String[] alsoKnownAs = thing1.getAliases();
        assertNotNull(alsoKnownAs);
        assertArrayEquals(aliases, alsoKnownAs);

        Map<String, String> properties = new HashMap<>();
        properties.put("foo", "bar");
        properties.put("baz", "bozo");

        assertEquals(0, thing1.getProperties()
                              .size());
        thing1.setProperties(properties);
        Map<String, String> newProps = thing1.getProperties();
        assertEquals(String.format("got: %s", newProps), properties.size(),
                     newProps.size());

        Thing3 thing3a = model.construct(Thing3.class, "uncle it",
                                         "one of my favorite things");
        assertNotNull(thing2.getThing3s());
        assertEquals(0, thing2.getThing3s()
                              .size());
        thing2.addThing3(thing3a);
        assertEquals(1, thing2.getThing3s()
                              .size());
        thing2.removeThing3(thing3a);
        assertEquals(0, thing2.getThing3s()
                              .size());

        Thing3 thing3b = model.construct(Thing3.class, "cousin it",
                                         "another one of my favorite things");

        List<Thing3> aFewOfMyFavoriteThings = new ArrayList<>();
        aFewOfMyFavoriteThings.add(thing3a);
        aFewOfMyFavoriteThings.add(thing3b);

        thing2.addThing3s(aFewOfMyFavoriteThings);
        assertEquals(2, thing2.getThing3s()
                              .size());
        thing2.removeThing3s(aFewOfMyFavoriteThings);
        assertEquals(0, thing2.getThing3s()
                              .size());

        assertNull(thing1.getDerivedFrom());
        MavenArtifact artifact = model.construct(MavenArtifact.class,
                                                 "myartifact", "artifact");
        artifact.setType("jar");
        assertEquals("jar", artifact.getType());
        thing1.setDerivedFrom(artifact);
        assertNotNull(thing1.getDerivedFrom());

        assertEquals(0, thing2.getDerivedFroms()
                              .size());
        thing2.addDerivedFrom(artifact);
        assertEquals(1, thing2.getDerivedFroms()
                              .size());
        thing2.removeDerivedFrom(artifact);
        assertEquals(0, thing2.getDerivedFroms()
                              .size());
        thing2.addDerivedFroms(Arrays.asList(artifact));
        assertEquals(1, thing2.getDerivedFroms()
                              .size());

        MavenArtifact artifact2 = model.construct(MavenArtifact.class,
                                                  "myartifact2", "artifact2");
        artifact2.setType("jar");

        thing2.setDerivedFroms(Arrays.asList(artifact2));
        assertEquals(1, thing2.getDerivedFroms()
                              .size());
        thing2.addDerivedFrom(artifact);
        assertEquals(2, thing2.getDerivedFroms()
                              .size());
        thing2.setDerivedFroms(Collections.emptyList());
        assertEquals(0, thing2.getDerivedFroms()
                              .size());

        assertNotNull(thing1.getScope());
    }

    @Test
    public void testEnums() throws Exception {
        MavenArtifact artifact = model.construct(MavenArtifact.class,
                                                 "myartifact", "artifact");
        artifact.setType("jar");
        try {
            artifact.setType("invalid");
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testFacetCapabilities() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, "testy", "test");

        WorkspaceScope scope = thing1.getScope();
        FacetRecord facet = model.getPhantasmModel()
                                 .getFacetDeclaration(kernel.getIsA(),
                                                      scope.lookup("Thing1"));
        assertNotNull(facet);

        assertTrue(model.getPhantasmModel()
                        .checkCapability(asList(kernel.getCore()), facet,
                                         kernel.getHadMember()));

        FacetRecord accessAuth = model.records()
                                      .newFacet(kernel.getCore());
        accessAuth.setClassifier(facet.getClassifier());
        accessAuth.setClassification(facet.getClassification());
        accessAuth.setAuthority(kernel.getAnyAgency()
                                      .getId());
        accessAuth.insert();

        assertFalse(model.getPhantasmModel()
                         .checkCapability(asList(kernel.getCore()), facet,
                                          kernel.getHadMember()));

        model.getPhantasmModel()
             .link(kernel.getCore(), kernel.getHadMember(),
                   kernel.getAnyAgency(), kernel.getCore());

        assertTrue(model.getPhantasmModel()
                        .checkCapability(asList(kernel.getCore()), facet,
                                         kernel.getHadMember()));

        accessAuth = model.records()
                          .newFacet(kernel.getCore());
        accessAuth.setClassifier(facet.getClassifier());
        accessAuth.setClassification(facet.getClassification());
        accessAuth.setAuthority(kernel.getSameAgency()
                                      .getId());
        accessAuth.insert();

        assertFalse(model.getPhantasmModel()
                         .checkCapability(asList(kernel.getCore()), facet,
                                          kernel.getHadMember()));

        model.getPhantasmModel()
             .link(kernel.getCore(), kernel.getHadMember(),
                   kernel.getSameAgency(), kernel.getCore());

        assertTrue(model.getPhantasmModel()
                        .checkCapability(asList(kernel.getCore()), facet,
                                         kernel.getHadMember()));
    }

    @Test
    public void testInstanceCapabilities() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, "testy", "test");

        Product instance = thing1.getRuleform();
        assertTrue(model.getPhantasmModel()
                        .checkCapability(asList(kernel.getCore()), instance,
                                         kernel.getHadMember()));

        AgencyExistentialGroupingRecord accessAuth = model.records()
                                                          .newExistentialGrouping(kernel.getCore());
        accessAuth.setUpdatedBy(kernel.getCore()
                                      .getId());
        accessAuth.setAuthority(kernel.getAnyAgency()
                                      .getId());
        accessAuth.setEntity(instance.getId());
        accessAuth.insert();

        assertFalse(model.getPhantasmModel()
                         .checkCapability(asList(kernel.getCore()), instance,
                                          kernel.getHadMember()));

        model.getPhantasmModel()
             .link(kernel.getCore(), kernel.getHadMember(),
                   kernel.getAnyAgency(), kernel.getCore());

        assertTrue(model.getPhantasmModel()
                        .checkCapability(asList(kernel.getCore()), instance,
                                         kernel.getHadMember()));

        accessAuth = model.records()
                          .newExistentialGrouping(kernel.getCore());
        accessAuth.setUpdatedBy(kernel.getCore()
                                      .getId());
        accessAuth.setAuthority(kernel.getSameAgency()
                                      .getId());
        accessAuth.setEntity(instance.getId());
        accessAuth.insert();

        assertFalse(model.getPhantasmModel()
                         .checkCapability(asList(kernel.getCore()), instance,
                                          kernel.getHadMember()));

        model.getPhantasmModel()
             .link(kernel.getCore(), kernel.getHadMember(),
                   kernel.getSameAgency(), kernel.getCore());

        assertTrue(model.getPhantasmModel()
                        .checkCapability(asList(kernel.getCore()), instance,
                                         kernel.getHadMember()));
    }

    @Test
    public void testNetworkAttributeCapabilities() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, "testy", "test");

        WorkspaceScope scope = thing1.getScope();

        TypedQuery<ExistentialNetworkAuthorization> query1 = em.createQuery("select auth from ExistentialNetworkAuthorization auth "
                                                                            + "where auth.classifier = :classifier "
                                                                            + "and auth.classification = :classification "
                                                                            + "and auth.childRelationship = :relationship "
                                                                            + "and auth.authorizedRelationship = :authRel "
                                                                            + "and auth.authorizedParent = :authParent ",
                                                                            ExistentialNetworkAuthorization.class);
        query1.setParameter("classifier", kernel.getIsA());
        query1.setParameter("classification", scope.lookup("Thing1"));
        query1.setParameter("relationship", scope.lookup("thing1Of"));
        query1.setParameter("authRel", kernel.getIsA());
        query1.setParameter("authParent", scope.lookup("Thing2"));
        ExistentialNetworkAuthorizationRecord auth = query1.getSingleResult();

        assertNotNull(auth);

        Attribute aliases = (Attribute) scope.lookup("aliases");
        assertNotNull(aliases);

        TypedQuery<ExistentialAttributeAuthorizationRecord> query = em.createQuery("select paa from ExistentialAttributeAuthorizationRecord paa "
                                                                                   + "where paa.networkAuthorization = :a "
                                                                                   + "and paa.authorizedNetworkAttribute = :b",
                                                                                   ExistentialAttributeAuthorizationRecord.class);
        query.setParameter("a", auth);
        query.setParameter("b", aliases);
        ExistentialAttributeAuthorizationRecord stateAuth = query.getSingleResult();
        assertNotNull(stateAuth);

        assertTrue(model.getPhantasmModel()
                        .checkCapability(asList(kernel.getCore()), stateAuth,
                                         kernel.getHadMember()));

        ExistentialAttributeAuthorizationRecord accessAuth = model.records()
                                                                  .newExistentialAttributeAuthorization(kernel.getCore());
        accessAuth.setAuthorizedAttribute(stateAuth.getAuthorizedAttribute());
        accessAuth.setFacet(stateAuth.getFacet());
        accessAuth.setAuthority(kernel.getAnyAgency()
                                      .getId());
        accessAuth.insert();

        assertFalse(model.getPhantasmModel()
                         .checkCapability(asList(kernel.getCore()), stateAuth,
                                          kernel.getHadMember()));

        model.getPhantasmModel()
             .link(kernel.getCore(), kernel.getHadMember(),
                   kernel.getAnyAgency(), kernel.getCore());

        assertTrue(model.getPhantasmModel()
                        .checkCapability(asList(kernel.getCore()), stateAuth,
                                         kernel.getHadMember()));

        accessAuth = model.records()
                          .newExistentialAttributeAuthorization(kernel.getCore());
        accessAuth.setAuthorizedAttribute(stateAuth.getAuthorizedAttribute());
        accessAuth.setFacet(stateAuth.getFacet());
        accessAuth.setAuthority(kernel.getSameAgency()
                                      .getId());
        accessAuth.insert();

        assertFalse(model.getPhantasmModel()
                         .checkCapability(asList(kernel.getCore()), stateAuth,
                                          kernel.getHadMember()));

        model.getPhantasmModel()
             .link(kernel.getCore(), kernel.getHadMember(),
                   kernel.getSameAgency(), kernel.getCore());

        assertTrue(model.getPhantasmModel()
                        .checkCapability(asList(kernel.getCore()), stateAuth,
                                         kernel.getHadMember()));
    }

    @Test
    public void testThis() throws InstantiationException {

        Thing1 thing1 = model.construct(Thing1.class, "testy", "test");
        Product child = (Product) model.getPhantasmModel()
                                       .getChild(thing1.getScope()
                                                       .getWorkspace()
                                                       .getDefiningProduct(),
                                                 model.getKernel()
                                                      .getHasMember(),
                                                 ExistentialDomain.Product);
        assertEquals("Thing1", child.getName());
    }

}
