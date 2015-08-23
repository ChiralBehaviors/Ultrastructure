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

package com.chiralbehaviors.phantasm;

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

import javax.persistence.TypedQuery;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductAttributeAuthorization;
import com.chiralbehaviors.CoRE.product.ProductLocationAttributeAuthorization;
import com.chiralbehaviors.CoRE.product.ProductLocationAuthorization;
import com.chiralbehaviors.CoRE.product.ProductNetworkAuthorization;
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
        em.getTransaction()
          .begin();
        WorkspaceImporter.createWorkspace(TestPhantasm.class.getResourceAsStream("/thing.wsp"),
                                          model);
        em.flush();
    }

    @Test
    public void testAttributeAccess() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, "testy", "test");

        WorkspaceScope scope = thing1.getScope();
        NetworkAuthorization<Product> facet = model.getProductModel()
                                                   .getFacetDeclaration(new Aspect<>(kernel.getIsA(),
                                                                                     (Product) scope.lookup("Thing1")));
        assertNotNull(facet);
        Attribute percentage = (Attribute) scope.lookup("discount");
        assertNotNull(percentage);

        TypedQuery<ProductAttributeAuthorization> query = em.createQuery("select paa from ProductAttributeAuthorization paa "
                                                                         + "where paa.networkAuthorization = :a "
                                                                         + "and paa.authorizedAttribute = :b",
                                                                         ProductAttributeAuthorization.class);
        query.setParameter("a", facet);
        query.setParameter("b", percentage);
        ProductAttributeAuthorization stateAuth = query.getSingleResult();
        assertNotNull(stateAuth);

        assertTrue(model.getProductModel()
                        .checkCapability(kernel.getCore(), stateAuth,
                                         kernel.getHadMember()));

        ProductAttributeAuthorization accessAuth = new ProductAttributeAuthorization(kernel.getCore());
        accessAuth.setAuthorizedAttribute(stateAuth.getAuthorizedAttribute());
        accessAuth.setNetworkAuthorization(stateAuth.getNetworkAuthorization());
        accessAuth.setSequenceNumber(1);
        accessAuth.setGroupingAgency(kernel.getAnyAgency());
        em.persist(accessAuth);

        assertFalse(model.getProductModel()
                         .checkCapability(kernel.getCore(), stateAuth,
                                          kernel.getHadMember()));

        model.getAgencyModel()
             .link(kernel.getCore(), kernel.getHadMember(),
                   kernel.getAnyAgency(), kernel.getCore());

        assertTrue(model.getProductModel()
                        .checkCapability(kernel.getCore(), stateAuth,
                                         kernel.getHadMember()));

        accessAuth = new ProductAttributeAuthorization(kernel.getCore());
        accessAuth.setAuthorizedAttribute(stateAuth.getAuthorizedAttribute());
        accessAuth.setNetworkAuthorization(stateAuth.getNetworkAuthorization());
        accessAuth.setSequenceNumber(2);
        accessAuth.setGroupingAgency(kernel.getSameAgency());
        em.persist(accessAuth);

        assertFalse(model.getProductModel()
                         .checkCapability(kernel.getCore(), stateAuth,
                                          kernel.getHadMember()));

        model.getAgencyModel()
             .link(kernel.getCore(), kernel.getHadMember(),
                   kernel.getSameAgency(), kernel.getCore());

        assertTrue(model.getProductModel()
                        .checkCapability(kernel.getCore(), stateAuth,
                                         kernel.getHadMember()));
    }

    @Test
    public void testChildAccess() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, "testy", "test");

        WorkspaceScope scope = thing1.getScope();
        NetworkAuthorization<Product> facet = model.getProductModel()
                                                   .getFacetDeclaration(new Aspect<>(kernel.getIsA(),
                                                                                     (Product) scope.lookup("Thing1")));
        assertNotNull(facet);

        TypedQuery<ProductNetworkAuthorization> query = em.createQuery("select auth from ProductNetworkAuthorization auth "
                                                                       + "where auth.classifier = :classifier "
                                                                       + "and auth.classification = :classification "
                                                                       + "and auth.childRelationship = :relationship "
                                                                       + "and auth.authorizedRelationship = :authRel "
                                                                       + "and auth.authorizedParent = :authParent ",
                                                                       ProductNetworkAuthorization.class);
        query.setParameter("classifier", kernel.getIsA());
        query.setParameter("classification", scope.lookup("Thing1"));
        query.setParameter("relationship", scope.lookup("thing1Of"));
        query.setParameter("authRel", kernel.getIsA());
        query.setParameter("authParent", scope.lookup("Thing2"));
        ProductNetworkAuthorization stateAuth = query.getSingleResult();
        assertNotNull(stateAuth);

        assertTrue(model.getProductModel()
                        .checkCapability(kernel.getCore(), stateAuth,
                                         kernel.getHadMember()));

        ProductNetworkAuthorization accessAuth = new ProductNetworkAuthorization(kernel.getCore());
        accessAuth.setClassifier(stateAuth.getClassifier());
        accessAuth.setClassification(stateAuth.getClassification());
        accessAuth.setChildRelationship(stateAuth.getChildRelationship());
        accessAuth.setAuthorizedRelationship(stateAuth.getAuthorizedRelationship());
        accessAuth.setAuthorizedParent(stateAuth.getAuthorizedParent());
        accessAuth.setSequenceNumber(1);
        accessAuth.setGroupingAgency(kernel.getAnyAgency());
        em.persist(accessAuth);
        assertNotNull(stateAuth);

        assertFalse(model.getProductModel()
                         .checkCapability(kernel.getCore(), stateAuth,
                                          kernel.getHadMember()));

        model.getAgencyModel()
             .link(kernel.getCore(), kernel.getHadMember(),
                   kernel.getAnyAgency(), kernel.getCore());

        assertTrue(model.getProductModel()
                        .checkCapability(kernel.getCore(), stateAuth,
                                         kernel.getHadMember()));

        accessAuth = new ProductNetworkAuthorization(kernel.getCore());
        accessAuth.setClassifier(stateAuth.getClassifier());
        accessAuth.setClassification(stateAuth.getClassification());
        accessAuth.setChildRelationship(stateAuth.getChildRelationship());
        accessAuth.setAuthorizedRelationship(stateAuth.getAuthorizedRelationship());
        accessAuth.setAuthorizedParent(stateAuth.getAuthorizedParent());
        accessAuth.setSequenceNumber(2);
        accessAuth.setGroupingAgency(kernel.getSameAgency());
        em.persist(accessAuth);
        assertNotNull(stateAuth);

        assertFalse(model.getProductModel()
                         .checkCapability(kernel.getCore(), stateAuth,
                                          kernel.getHadMember()));

        model.getAgencyModel()
             .link(kernel.getCore(), kernel.getHadMember(),
                   kernel.getSameAgency(), kernel.getCore());

        assertTrue(model.getProductModel()
                        .checkCapability(kernel.getCore(), stateAuth,
                                         kernel.getHadMember()));
    }

    @Test
    public void testDemo() throws Exception {

        Thing1 thing1 = model.construct(Thing1.class, "testy", "test");
        Thing2 thing2 = model.construct(Thing2.class, "tasty", "chips");
        assertNotNull(thing1);
        assertEquals(thing1, thing1.doSomethingElse());
        thing1.doSomething("hello");
        assertNotNull(thing1.getRuleform());
        assertEquals(thing1.getRuleform()
                           .getName(),
                     thing1.getName());
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

        assertEquals(0, thing1.getProperties()
                              .size());
        thing1.setProperties(properties);
        em.flush();
        Map<String, String> newProps = thing1.getProperties();
        assertEquals(String.format("got: %s", newProps), properties.size(),
                     newProps.size());

        Thing3 thing3a = model.construct(Thing3.class, "uncle it",
                                         "one of my favorite things");
        assertNotNull(thing2.getThing3s());
        assertEquals(0, thing2.getThing3s()
                              .size());
        thing2.add(thing3a);
        assertEquals(1, thing2.getThing3s()
                              .size());
        thing2.remove(thing3a);
        assertEquals(0, thing2.getThing3s()
                              .size());

        Thing3 thing3b = model.construct(Thing3.class, "cousin it",
                                         "another one of my favorite things");

        List<Thing3> aFewOfMyFavoriteThings = new ArrayList<>();
        aFewOfMyFavoriteThings.add(thing3a);
        aFewOfMyFavoriteThings.add(thing3b);

        thing2.add(aFewOfMyFavoriteThings);
        assertEquals(2, thing2.getThing3s()
                              .size());
        thing2.remove(aFewOfMyFavoriteThings);
        assertEquals(0, thing2.getThing3s()
                              .size());

        assertNull(thing1.getArtifact());
        MavenArtifact artifact = model.construct(MavenArtifact.class,
                                                 "myartifact", "artifact");
        assertEquals("jar", artifact.getType());
        em.flush();
        thing1.setArtifact(artifact);
        em.flush();
        assertNotNull(thing1.getArtifact());

        assertEquals(0, thing2.getArtifacts()
                              .size());
        thing2.addArtifact(artifact);
        assertEquals(1, thing2.getArtifacts()
                              .size());
        thing2.removeArtifact(artifact);
        assertEquals(0, thing2.getArtifacts()
                              .size());
        thing2.addArtifacts(Arrays.asList(artifact));
        assertEquals(1, thing2.getArtifacts()
                              .size());

        MavenArtifact artifact2 = model.construct(MavenArtifact.class,
                                                  "myartifact2", "artifact2");
        artifact2.setType("jar");

        thing2.setArtifacts(Arrays.asList(artifact2));
        assertEquals(1, thing2.getArtifacts()
                              .size());
        thing2.addArtifact(artifact);
        assertEquals(2, thing2.getArtifacts()
                              .size());
        thing2.setArtifacts(Collections.emptyList());
        assertEquals(0, thing2.getArtifacts()
                              .size());

        assertNotNull(thing1.getScope());
    }

    @Test
    public void testEnums() throws Exception {
        MavenArtifact artifact = model.construct(MavenArtifact.class,
                                                 "myartifact", "artifact");
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
    public void testFacetAccess() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, "testy", "test");

        WorkspaceScope scope = thing1.getScope();
        NetworkAuthorization<Product> facet = model.getProductModel()
                                                   .getFacetDeclaration(new Aspect<>(kernel.getIsA(),
                                                                                     (Product) scope.lookup("Thing1")));
        assertNotNull(facet);

        assertTrue(model.getProductModel()
                        .checkFacetCapability(kernel.getCore(), facet,
                                              kernel.getHadMember()));

        ProductNetworkAuthorization accessAuth = new ProductNetworkAuthorization(kernel.getCore());
        accessAuth.setClassifier(facet.getClassifier());
        accessAuth.setClassification(facet.getClassification());
        accessAuth.setSequenceNumber(1);
        accessAuth.setGroupingAgency(kernel.getAnyAgency());
        em.persist(accessAuth);

        assertFalse(model.getProductModel()
                         .checkFacetCapability(kernel.getCore(), facet,
                                               kernel.getHadMember()));

        model.getAgencyModel()
             .link(kernel.getCore(), kernel.getHadMember(),
                   kernel.getAnyAgency(), kernel.getCore());

        assertTrue(model.getProductModel()
                        .checkFacetCapability(kernel.getCore(), facet,
                                              kernel.getHadMember()));

        accessAuth = new ProductNetworkAuthorization(kernel.getCore());
        accessAuth.setClassifier(facet.getClassifier());
        accessAuth.setClassification(facet.getClassification());
        accessAuth.setSequenceNumber(2);
        accessAuth.setGroupingAgency(kernel.getSameAgency());
        em.persist(accessAuth);

        assertFalse(model.getProductModel()
                         .checkFacetCapability(kernel.getCore(), facet,
                                               kernel.getHadMember()));

        model.getAgencyModel()
             .link(kernel.getCore(), kernel.getHadMember(),
                   kernel.getSameAgency(), kernel.getCore());

        assertTrue(model.getProductModel()
                        .checkFacetCapability(kernel.getCore(), facet,
                                              kernel.getHadMember()));
    }

    @Test
    public void testNetworkAttributeAccess() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, "testy", "test");

        WorkspaceScope scope = thing1.getScope();

        TypedQuery<ProductNetworkAuthorization> query1 = em.createQuery("select auth from ProductNetworkAuthorization auth "
                                                                        + "where auth.classifier = :classifier "
                                                                        + "and auth.classification = :classification "
                                                                        + "and auth.childRelationship = :relationship "
                                                                        + "and auth.authorizedRelationship = :authRel "
                                                                        + "and auth.authorizedParent = :authParent ",
                                                                        ProductNetworkAuthorization.class);
        query1.setParameter("classifier", kernel.getIsA());
        query1.setParameter("classification", scope.lookup("Thing1"));
        query1.setParameter("relationship", scope.lookup("thing1Of"));
        query1.setParameter("authRel", kernel.getIsA());
        query1.setParameter("authParent", scope.lookup("Thing2"));
        ProductNetworkAuthorization auth = query1.getSingleResult();

        assertNotNull(auth);

        Attribute aliases = (Attribute) scope.lookup("aliases");
        assertNotNull(aliases);

        TypedQuery<ProductAttributeAuthorization> query = em.createQuery("select paa from ProductAttributeAuthorization paa "
                                                                         + "where paa.networkAuthorization = :a "
                                                                         + "and paa.authorizedNetworkAttribute = :b",
                                                                         ProductAttributeAuthorization.class);
        query.setParameter("a", auth);
        query.setParameter("b", aliases);
        ProductAttributeAuthorization stateAuth = query.getSingleResult();
        assertNotNull(stateAuth);

        assertTrue(model.getProductModel()
                        .checkNetworkCapability(kernel.getCore(), stateAuth,
                                                kernel.getHadMember()));

        ProductAttributeAuthorization accessAuth = new ProductAttributeAuthorization(kernel.getCore());
        accessAuth.setAuthorizedNetworkAttribute(stateAuth.getAuthorizedNetworkAttribute());
        accessAuth.setNetworkAuthorization(stateAuth.getNetworkAuthorization());
        accessAuth.setSequenceNumber(1);
        accessAuth.setGroupingAgency(kernel.getAnyAgency());
        em.persist(accessAuth);

        assertFalse(model.getProductModel()
                         .checkNetworkCapability(kernel.getCore(), stateAuth,
                                                 kernel.getHadMember()));

        model.getAgencyModel()
             .link(kernel.getCore(), kernel.getHadMember(),
                   kernel.getAnyAgency(), kernel.getCore());

        assertTrue(model.getProductModel()
                        .checkNetworkCapability(kernel.getCore(), stateAuth,
                                                kernel.getHadMember()));

        accessAuth = new ProductAttributeAuthorization(kernel.getCore());
        accessAuth.setAuthorizedNetworkAttribute(stateAuth.getAuthorizedNetworkAttribute());
        accessAuth.setNetworkAuthorization(stateAuth.getNetworkAuthorization());
        accessAuth.setSequenceNumber(2);
        accessAuth.setGroupingAgency(kernel.getSameAgency());
        em.persist(accessAuth);

        assertFalse(model.getProductModel()
                         .checkNetworkCapability(kernel.getCore(), stateAuth,
                                                 kernel.getHadMember()));

        model.getAgencyModel()
             .link(kernel.getCore(), kernel.getHadMember(),
                   kernel.getSameAgency(), kernel.getCore());

        assertTrue(model.getProductModel()
                        .checkNetworkCapability(kernel.getCore(), stateAuth,
                                                kernel.getHadMember()));
    }

    @Test
    public void testThis() throws InstantiationException {

        Thing1 thing1 = model.construct(Thing1.class, "testy", "test");
        Product child = model.getProductModel()
                             .getChild(thing1.getScope()
                                             .getWorkspace()
                                             .getDefiningProduct(),
                                       model.getKernel()
                                            .getHasMember());
        assertEquals("Thing1", child.getName());
    }

    @Test
    public void testXdChildAccess() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, "testy", "test");

        WorkspaceScope scope = thing1.getScope();
        NetworkAuthorization<Product> facet = model.getProductModel()
                                                   .getFacetDeclaration(new Aspect<>(kernel.getIsA(),
                                                                                     (Product) scope.lookup("Thing1")));
        assertNotNull(facet);

        TypedQuery<ProductLocationAuthorization> query = em.createQuery("select auth from ProductLocationAuthorization auth "
                                                                        + "where auth.fromRelationship = :fromRelationship "
                                                                        + "and auth.fromParent = :fromParent "
                                                                        + "and auth.connection = :connection "
                                                                        + "and auth.toRelationship = :toRelationship "
                                                                        + "and auth.toParent = :toParent ",
                                                                        ProductLocationAuthorization.class);
        query.setParameter("fromRelationship", kernel.getIsA());
        query.setParameter("fromParent", scope.lookup("Thing1"));
        query.setParameter("connection", scope.lookup("derivedFrom"));
        query.setParameter("toRelationship", kernel.getIsA());
        query.setParameter("toParent", scope.lookup("MavenArtifact"));
        ProductLocationAuthorization stateAuth = query.getSingleResult();
        assertNotNull(stateAuth);

        assertTrue(model.getProductModel()
                        .checkCapability(kernel.getCore(), stateAuth,
                                         kernel.getHadMember()));

        ProductLocationAuthorization accessAuth = new ProductLocationAuthorization(kernel.getCore());
        accessAuth.setFromRelationship(stateAuth.getFromRelationship());
        accessAuth.setFromParent(stateAuth.getFromParent());
        accessAuth.setConnection(stateAuth.getConnection());
        accessAuth.setToRelationship(stateAuth.getToRelationship());
        accessAuth.setToParent(stateAuth.getToParent());
        accessAuth.setSequenceNumber(1);
        accessAuth.setCardinality(stateAuth.getCardinality());
        accessAuth.setGroupingAgency(kernel.getAnyAgency());
        em.persist(accessAuth);
        assertNotNull(stateAuth);

        assertFalse(model.getProductModel()
                         .checkCapability(kernel.getCore(), stateAuth,
                                          kernel.getHadMember()));

        model.getAgencyModel()
             .link(kernel.getCore(), kernel.getHadMember(),
                   kernel.getAnyAgency(), kernel.getCore());

        assertTrue(model.getProductModel()
                        .checkCapability(kernel.getCore(), stateAuth,
                                         kernel.getHadMember()));

        accessAuth = new ProductLocationAuthorization(kernel.getCore());
        accessAuth.setFromRelationship(stateAuth.getFromRelationship());
        accessAuth.setFromParent(stateAuth.getFromParent());
        accessAuth.setConnection(stateAuth.getConnection());
        accessAuth.setToRelationship(stateAuth.getToRelationship());
        accessAuth.setToParent(stateAuth.getToParent());
        accessAuth.setSequenceNumber(2);
        accessAuth.setCardinality(stateAuth.getCardinality());
        accessAuth.setGroupingAgency(kernel.getSameAgency());
        em.persist(accessAuth);
        assertNotNull(stateAuth);

        assertFalse(model.getProductModel()
                         .checkCapability(kernel.getCore(), stateAuth,
                                          kernel.getHadMember()));

        model.getAgencyModel()
             .link(kernel.getCore(), kernel.getHadMember(),
                   kernel.getSameAgency(), kernel.getCore());

        assertTrue(model.getProductModel()
                        .checkCapability(kernel.getCore(), stateAuth,
                                         kernel.getHadMember()));
    }

    @Test
    public void testXdNetworkAttributeAccess() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, "testy", "test");

        WorkspaceScope scope = thing1.getScope();
        Attribute aliases = (Attribute) scope.lookup("aliases");
        assertNotNull(aliases);

        TypedQuery<ProductLocationAttributeAuthorization> query = em.createQuery("select paa from ProductLocationAttributeAuthorization paa "
                                                                                 + "where paa.authorizedAttribute = :b",
                                                                                 ProductLocationAttributeAuthorization.class);
        query.setParameter("b", aliases);
        ProductLocationAttributeAuthorization stateAuth = query.getSingleResult();
        assertNotNull(stateAuth);

        assertTrue(model.getProductModel()
                        .checkCapability(kernel.getCore(), stateAuth,
                                         kernel.getHadMember()));

        ProductLocationAttributeAuthorization accessAuth = new ProductLocationAttributeAuthorization(kernel.getCore());
        accessAuth.setAuthorizedAttribute(stateAuth.getAuthorizedAttribute());
        accessAuth.setNetworkAuthorization(stateAuth.getNetworkAuthorization());
        accessAuth.setSequenceNumber(1);
        accessAuth.setGroupingAgency(kernel.getAnyAgency());
        em.persist(accessAuth);

        assertFalse(model.getProductModel()
                         .checkCapability(kernel.getCore(), stateAuth,
                                          kernel.getHadMember()));

        model.getAgencyModel()
             .link(kernel.getCore(), kernel.getHadMember(),
                   kernel.getAnyAgency(), kernel.getCore());

        assertTrue(model.getProductModel()
                        .checkCapability(kernel.getCore(), stateAuth,
                                         kernel.getHadMember()));

        accessAuth = new ProductLocationAttributeAuthorization(kernel.getCore());
        accessAuth.setAuthorizedAttribute(stateAuth.getAuthorizedAttribute());
        accessAuth.setNetworkAuthorization(stateAuth.getNetworkAuthorization());
        accessAuth.setSequenceNumber(2);
        accessAuth.setGroupingAgency(kernel.getSameAgency());
        em.persist(accessAuth);

        assertFalse(model.getProductModel()
                         .checkCapability(kernel.getCore(), stateAuth,
                                          kernel.getHadMember()));

        model.getAgencyModel()
             .link(kernel.getCore(), kernel.getHadMember(),
                   kernel.getSameAgency(), kernel.getCore());

        assertTrue(model.getProductModel()
                        .checkCapability(kernel.getCore(), stateAuth,
                                         kernel.getHadMember()));
    }

}
