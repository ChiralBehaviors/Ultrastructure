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

import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_ATTRIBUTE_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK_AUTHORIZATION;
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

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.AgencyAttrAuthRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.AgencyExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.AgencyFacetRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.AgencyNetAttrAuthRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.AgencyNetAuthRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.phantasm.test.location.MavenArtifact;
import com.chiralbehaviors.CoRE.phantasm.test.product.OtherThing;
import com.chiralbehaviors.CoRE.phantasm.test.product.Thing1;
import com.chiralbehaviors.CoRE.phantasm.test.product.Thing2;
import com.chiralbehaviors.CoRE.phantasm.test.product.Thing3;

/**
 * @author hhildebrand
 *
 */
public class RbacTest extends AbstractModelTest {

    @Before
    public void loadThingOntology() throws Exception {
        WorkspaceImporter.manifest(RbacTest.class.getResourceAsStream("/thing.wsp"),
                                   model);
    }

    @Test
    public void testAttributePermissions() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, ExistentialDomain.Product,
                                        "testy", "test");

        WorkspaceScope scope = thing1.getScope();
        FacetRecord facet = model.getPhantasmModel()
                                 .getFacetDeclaration(model.getKernel()
                                                           .getIsA(),
                                                      scope.lookup("Thing1"));
        assertNotNull(facet);
        Attribute percentage = (Attribute) scope.lookup("discount");
        assertNotNull(percentage);

        ExistentialAttributeAuthorizationRecord stateAuth = model.create()
                                                                 .selectFrom(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION)
                                                                 .where(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.AUTHORIZED_ATTRIBUTE.equal(percentage.getId()))
                                                                 .and(EXISTENTIAL_ATTRIBUTE_AUTHORIZATION.FACET.equal(facet.getId()))
                                                                 .fetchOne();
        assertNotNull(stateAuth);

        assertTrue(model.getPhantasmModel()
                        .checkPermission(asList(model.getKernel()
                                                     .getCore()),
                                         stateAuth, model.getKernel()
                                                         .getHadMember()));

        AgencyAttrAuthRecord accessAuth = model.records()
                                               .newAgencyAttrAuth();
        accessAuth.setAuthorization(stateAuth.getId());
        accessAuth.setAuthority(model.getKernel()
                                     .getAnyAgency()
                                     .getId());
        accessAuth.insert();

        assertFalse(model.getPhantasmModel()
                         .checkPermission(asList(model.getKernel()
                                                      .getCore()),
                                          stateAuth, model.getKernel()
                                                          .getHadMember()));

        model.getPhantasmModel()
             .link(model.getKernel()
                        .getCore(),
                   model.getKernel()
                        .getHadMember(),
                   model.getKernel()
                        .getAnyAgency());

        assertTrue(model.getPhantasmModel()
                        .checkPermission(asList(model.getKernel()
                                                     .getCore()),
                                         stateAuth, model.getKernel()
                                                         .getHadMember()));

        accessAuth = model.records()
                          .newAgencyAttrAuth();
        accessAuth.setAuthorization(stateAuth.getId());
        accessAuth.setAuthority(model.getKernel()
                                     .getSameAgency()
                                     .getId());
        accessAuth.insert();

        assertFalse(model.getPhantasmModel()
                         .checkPermission(asList(model.getKernel()
                                                      .getCore()),
                                          stateAuth, model.getKernel()
                                                          .getHadMember()));

        model.getPhantasmModel()
             .link(model.getKernel()
                        .getCore(),
                   model.getKernel()
                        .getHadMember(),
                   model.getKernel()
                        .getSameAgency());

        assertTrue(model.getPhantasmModel()
                        .checkPermission(asList(model.getKernel()
                                                     .getCore()),
                                         stateAuth, model.getKernel()
                                                         .getHadMember()));

        assertFalse(model.getPhantasmModel()
                         .checkPermission(asList(model.getKernel()
                                                      .getNotApplicableAgency()),
                                          stateAuth, model.getKernel()
                                                          .getHadMember()));

        model.getPhantasmModel()
             .link(model.getKernel()
                        .getNotApplicableAgency(),
                   model.getKernel()
                        .getMemberOf(),
                   model.getKernel()
                        .getCore());

        assertTrue(model.getPhantasmModel()
                        .checkPermission(asList(model.getKernel()
                                                     .getNotApplicableAgency()),
                                         stateAuth, model.getKernel()
                                                         .getHadMember()));
    }

    @Test
    public void testChildPermissions() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, ExistentialDomain.Product,
                                        "testy", "test");

        WorkspaceScope scope = thing1.getScope();
        FacetRecord facet = model.getPhantasmModel()
                                 .getFacetDeclaration(model.getKernel()
                                                           .getIsA(),
                                                      scope.lookup("Thing1"));
        assertNotNull(facet);

        Relationship relationship = scope.lookup("thing1Of");
        assertNotNull(relationship);
        ExistentialNetworkAuthorizationRecord stateAuth = model.create()
                                                               .selectFrom(EXISTENTIAL_NETWORK_AUTHORIZATION)
                                                               .where(EXISTENTIAL_NETWORK_AUTHORIZATION.PARENT.equal(facet.getId()))
                                                               .and(EXISTENTIAL_NETWORK_AUTHORIZATION.RELATIONSHIP.equal(relationship.getId()))
                                                               .fetchOne();

        assertNotNull(stateAuth);

        assertTrue(model.getPhantasmModel()
                        .checkPermission(asList(model.getKernel()
                                                     .getCore()),
                                         stateAuth, model.getKernel()
                                                         .getHadMember()));

        AgencyNetAuthRecord accessAuth = model.records()
                                              .newAgencyNetAuth();
        accessAuth.setAuthorization(stateAuth.getId());
        accessAuth.setAuthority(model.getKernel()
                                     .getAnyAgency()
                                     .getId());
        accessAuth.insert();

        assertFalse(model.getPhantasmModel()
                         .checkPermission(asList(model.getKernel()
                                                      .getCore()),
                                          stateAuth, model.getKernel()
                                                          .getHadMember()));

        model.getPhantasmModel()
             .link(model.getKernel()
                        .getCore(),
                   model.getKernel()
                        .getHadMember(),
                   model.getKernel()
                        .getAnyAgency());

        assertTrue(model.getPhantasmModel()
                        .checkPermission(asList(model.getKernel()
                                                     .getCore()),
                                         stateAuth, model.getKernel()
                                                         .getHadMember()));

        accessAuth = model.records()
                          .newAgencyNetAuth();
        accessAuth.setAuthorization(stateAuth.getId());
        accessAuth.setAuthority(model.getKernel()
                                     .getSameAgency()
                                     .getId());
        accessAuth.insert();

        assertFalse(model.getPhantasmModel()
                         .checkPermission(asList(model.getKernel()
                                                      .getCore()),
                                          stateAuth, model.getKernel()
                                                          .getHadMember()));

        model.getPhantasmModel()
             .link(model.getKernel()
                        .getCore(),
                   model.getKernel()
                        .getHadMember(),
                   model.getKernel()
                        .getSameAgency());

        assertTrue(model.getPhantasmModel()
                        .checkPermission(asList(model.getKernel()
                                                     .getCore()),
                                         stateAuth, model.getKernel()
                                                         .getHadMember()));

        assertFalse(model.getPhantasmModel()
                         .checkPermission(asList(model.getKernel()
                                                      .getNotApplicableAgency()),
                                          stateAuth, model.getKernel()
                                                          .getHadMember()));

        model.getPhantasmModel()
             .link(model.getKernel()
                        .getNotApplicableAgency(),
                   model.getKernel()
                        .getMemberOf(),
                   model.getKernel()
                        .getCore());

        assertTrue(model.getPhantasmModel()
                        .checkPermission(asList(model.getKernel()
                                                     .getNotApplicableAgency()),
                                         stateAuth, model.getKernel()
                                                         .getHadMember()));
    }

    @Test
    public void testThingOntology() throws Exception {

        Thing1 thing1 = model.construct(Thing1.class, ExistentialDomain.Product,
                                        "testy", "test");
        Thing2 thing2 = model.construct(Thing2.class, ExistentialDomain.Product,
                                        "tasty", "chips");
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

        Thing3 thing3a = model.construct(Thing3.class,
                                         ExistentialDomain.Product, "uncle it",
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

        Thing3 thing3b = model.construct(Thing3.class,
                                         ExistentialDomain.Product, "cousin it",
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
                                                 ExistentialDomain.Location,
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
                                                  ExistentialDomain.Location,
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
                                                 ExistentialDomain.Location,
                                                 "myartifact", "artifact");
        artifact.setType("jar");
        artifact.setType("invalid");
        try {
            model.flush();
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testFacetPermissions() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, ExistentialDomain.Product,
                                        "testy", "test");

        WorkspaceScope scope = thing1.getScope();
        FacetRecord facet = model.getPhantasmModel()
                                 .getFacetDeclaration(model.getKernel()
                                                           .getIsA(),
                                                      scope.lookup("Thing1"));
        assertNotNull(facet);

        assertTrue(model.getPhantasmModel()
                        .checkPermission(asList(model.getKernel()
                                                     .getCore()),
                                         facet, model.getKernel()
                                                     .getHadMember()));

        AgencyFacetRecord accessAuth = model.records()
                                            .newAgencyFacet();
        accessAuth.setFacet(facet.getId());
        accessAuth.setAuthority(model.getKernel()
                                     .getAnyAgency()
                                     .getId());
        accessAuth.insert();

        assertFalse(model.getPhantasmModel()
                         .checkPermission(asList(model.getKernel()
                                                      .getCore()),
                                          facet, model.getKernel()
                                                      .getHadMember()));

        model.getPhantasmModel()
             .link(model.getKernel()
                        .getCore(),
                   model.getKernel()
                        .getHadMember(),
                   model.getKernel()
                        .getAnyAgency());

        assertTrue(model.getPhantasmModel()
                        .checkPermission(asList(model.getKernel()
                                                     .getCore()),
                                         facet, model.getKernel()
                                                     .getHadMember()));

        accessAuth = model.records()
                          .newAgencyFacet();
        accessAuth.setFacet(facet.getId());
        accessAuth.setAuthority(model.getKernel()
                                     .getSameAgency()
                                     .getId());
        accessAuth.insert();

        assertFalse(model.getPhantasmModel()
                         .checkPermission(asList(model.getKernel()
                                                      .getCore()),
                                          facet, model.getKernel()
                                                      .getHadMember()));

        model.getPhantasmModel()
             .link(model.getKernel()
                        .getCore(),
                   model.getKernel()
                        .getHadMember(),
                   model.getKernel()
                        .getSameAgency());

        assertTrue(model.getPhantasmModel()
                        .checkPermission(asList(model.getKernel()
                                                     .getCore()),
                                         facet, model.getKernel()
                                                     .getHadMember()));

        assertFalse(model.getPhantasmModel()
                         .checkPermission(asList(model.getKernel()
                                                      .getNotApplicableAgency()),
                                          facet, model.getKernel()
                                                      .getHadMember()));

        model.getPhantasmModel()
             .link(model.getKernel()
                        .getNotApplicableAgency(),
                   model.getKernel()
                        .getMemberOf(),
                   model.getKernel()
                        .getCore());

        assertTrue(model.getPhantasmModel()
                        .checkPermission(asList(model.getKernel()
                                                     .getNotApplicableAgency()),
                                         facet, model.getKernel()
                                                     .getHadMember()));
    }

    @Test
    public void testInstancePermissions() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, ExistentialDomain.Product,
                                        "testy", "test");

        Product instance = (Product) thing1.getRuleform();
        assertTrue(model.getPhantasmModel()
                        .checkPermission(asList(model.getKernel()
                                                     .getCore()),
                                         instance, model.getKernel()
                                                        .getHadMember()));

        AgencyExistentialRecord accessAuth = model.records()
                                                  .newAgencyExistential();
        accessAuth.setUpdatedBy(model.getKernel()
                                     .getCore()
                                     .getId());
        accessAuth.setAuthority(model.getKernel()
                                     .getAnyAgency()
                                     .getId());
        accessAuth.setEntity(instance.getId());
        accessAuth.insert();

        assertFalse(model.getPhantasmModel()
                         .checkPermission(asList(model.getKernel()
                                                      .getCore()),
                                          instance, model.getKernel()
                                                         .getHadMember()));

        model.getPhantasmModel()
             .link(model.getKernel()
                        .getCore(),
                   model.getKernel()
                        .getHadMember(),
                   model.getKernel()
                        .getAnyAgency());

        assertTrue(model.getPhantasmModel()
                        .checkPermission(asList(model.getKernel()
                                                     .getCore()),
                                         instance, model.getKernel()
                                                        .getHadMember()));

        accessAuth = model.records()
                          .newAgencyExistential();
        accessAuth.setUpdatedBy(model.getKernel()
                                     .getCore()
                                     .getId());
        accessAuth.setAuthority(model.getKernel()
                                     .getSameAgency()
                                     .getId());
        accessAuth.setEntity(instance.getId());
        accessAuth.insert();

        assertFalse(model.getPhantasmModel()
                         .checkPermission(asList(model.getKernel()
                                                      .getCore()),
                                          instance, model.getKernel()
                                                         .getHadMember()));

        model.getPhantasmModel()
             .link(model.getKernel()
                        .getCore(),
                   model.getKernel()
                        .getHadMember(),
                   model.getKernel()
                        .getSameAgency());

        assertTrue(model.getPhantasmModel()
                        .checkPermission(asList(model.getKernel()
                                                     .getCore()),
                                         instance, model.getKernel()
                                                        .getHadMember()));
        assertFalse(model.getPhantasmModel()
                         .checkPermission(asList(model.getKernel()
                                                      .getNotApplicableAgency()),
                                          instance, model.getKernel()
                                                         .getHadMember()));

        model.getPhantasmModel()
             .link(model.getKernel()
                        .getNotApplicableAgency(),
                   model.getKernel()
                        .getMemberOf(),
                   model.getKernel()
                        .getCore());

        assertTrue(model.getPhantasmModel()
                        .checkPermission(asList(model.getKernel()
                                                     .getNotApplicableAgency()),
                                         instance, model.getKernel()
                                                        .getHadMember()));
    }

    @Test
    public void testNetworkAttributePermissions() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, ExistentialDomain.Product,
                                        "testy", "test");
        WorkspaceScope scope = thing1.getScope();

        FacetRecord facet = model.getPhantasmModel()
                                 .getFacetDeclaration(model.getKernel()
                                                           .getIsA(),
                                                      scope.lookup("Thing1"));

        Attribute aliases = (Attribute) scope.lookup("aliases");
        assertNotNull(aliases);

        Relationship relationship = scope.lookup("thing1Of");
        assertNotNull(relationship);

        ExistentialNetworkAuthorizationRecord auth = model.create()
                                                          .selectFrom(EXISTENTIAL_NETWORK_AUTHORIZATION)
                                                          .where(EXISTENTIAL_NETWORK_AUTHORIZATION.PARENT.equal(facet.getId()))
                                                          .and(EXISTENTIAL_NETWORK_AUTHORIZATION.RELATIONSHIP.equal(relationship.getId()))
                                                          .fetchOne();

        assertNotNull(auth);

        ExistentialNetworkAttributeAuthorizationRecord stateAuth = model.create()
                                                                        .selectFrom(EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION)
                                                                        .where(EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION.NETWORK_AUTHORIZATION.equal(auth.getId()))
                                                                        .and(EXISTENTIAL_NETWORK_ATTRIBUTE_AUTHORIZATION.AUTHORIZED_ATTRIBUTE.equal(aliases.getId()))
                                                                        .fetchOne();

        assertNotNull(stateAuth);

        assertTrue(model.getPhantasmModel()
                        .checkPermission(asList(model.getKernel()
                                                     .getCore()),
                                         stateAuth, model.getKernel()
                                                         .getHadMember()));

        AgencyNetAttrAuthRecord accessAuth = model.records()
                                                  .newAgencyNetAttrAuth();
        accessAuth.setAuthorization(stateAuth.getId());
        accessAuth.setAuthority(model.getKernel()
                                     .getAnyAgency()
                                     .getId());
        accessAuth.insert();

        assertFalse(model.getPhantasmModel()
                         .checkPermission(asList(model.getKernel()
                                                      .getCore()),
                                          stateAuth, model.getKernel()
                                                          .getHadMember()));

        model.getPhantasmModel()
             .link(model.getKernel()
                        .getCore(),
                   model.getKernel()
                        .getHadMember(),
                   model.getKernel()
                        .getAnyAgency());

        assertTrue(model.getPhantasmModel()
                        .checkPermission(asList(model.getKernel()
                                                     .getCore()),
                                         stateAuth, model.getKernel()
                                                         .getHadMember()));

        accessAuth = model.records()
                          .newAgencyNetAttrAuth();
        accessAuth.setAuthorization(stateAuth.getId());

        accessAuth.setAuthority(model.getKernel()
                                     .getSameAgency()
                                     .getId());
        accessAuth.insert();

        assertFalse(model.getPhantasmModel()
                         .checkPermission(asList(model.getKernel()
                                                      .getCore()),
                                          stateAuth, model.getKernel()
                                                          .getHadMember()));

        model.getPhantasmModel()
             .link(model.getKernel()
                        .getCore(),
                   model.getKernel()
                        .getHadMember(),
                   model.getKernel()
                        .getSameAgency());

        assertTrue(model.getPhantasmModel()
                        .checkPermission(asList(model.getKernel()
                                                     .getCore()),
                                         stateAuth, model.getKernel()
                                                         .getHadMember()));
        model.executeAs(model.principalFrom(model.getKernel()
                                                 .getCore(),
                                            Collections.emptyList()),
                        () -> {
                            assertTrue(model.getPhantasmModel()
                                            .checkPermission(stateAuth,
                                                             model.getKernel()
                                                                  .getHadMember()));
                            return null;
                        });

        model.getPhantasmModel()
             .getAttributeValue(model.getKernel()
                                     .getCore(),
                                model.getKernel()
                                     .getHadMember(),
                                model.getKernel()
                                     .getSameAgency(),
                                aliases);

        assertFalse(model.getPhantasmModel()
                         .checkPermission(asList(model.getKernel()
                                                      .getNotApplicableAgency()),
                                          stateAuth, model.getKernel()
                                                          .getHadMember()));

        model.getPhantasmModel()
             .link(model.getKernel()
                        .getNotApplicableAgency(),
                   model.getKernel()
                        .getMemberOf(),
                   model.getKernel()
                        .getCore());

        assertTrue(model.getPhantasmModel()
                        .checkPermission(asList(model.getKernel()
                                                     .getNotApplicableAgency()),
                                         stateAuth, model.getKernel()
                                                         .getHadMember()));
    }

    @Test
    public void testThis() throws InstantiationException {

        Thing1 thing1 = model.construct(Thing1.class, ExistentialDomain.Product,
                                        "testy", "test");
        Product child = (Product) model.getPhantasmModel()
                                       .getChild(thing1.getScope()
                                                       .getWorkspace()
                                                       .getDefiningProduct(),
                                                 model.getKernel()
                                                      .getHasMember(),
                                                 ExistentialDomain.Product);
        assertEquals("Thing1", child.getName());
    }

    @Test
    public void testApplyFacet() throws Exception {
        Thing1 thing1 = model.construct(Thing1.class, ExistentialDomain.Product,
                                        "testy", "test");
        model.apply(OtherThing.class, thing1);
    }
}
