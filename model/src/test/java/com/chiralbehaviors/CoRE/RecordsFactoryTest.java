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

package com.chiralbehaviors.CoRE;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.UUID;

import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.Interval;
import com.chiralbehaviors.CoRE.domain.Location;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.domain.StatusCode;
import com.chiralbehaviors.CoRE.domain.Unit;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.enums.ValueType;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.test.DatabaseTest;
import com.hellblazer.utils.Tuple;

/**
 * @author hhildebrand
 *
 */
public class RecordsFactoryTest extends DatabaseTest {

    @Test
    public void testRecordsFactory() throws Exception {
        RECORDS.newAgency();
        RECORDS.newAgency("");
        Agency agency = RECORDS.newAgency("foo", "bar");
        agency.insert();
        RECORDS.newAttribute();
        Attribute attribute = (Attribute) RECORDS.newAttribute("", "",
                                                               ValueType.Integer);
        attribute.insert();
        RECORDS.newChildSequencingAuthorization();
        RECORDS.newChildSequencingAuthorization(RECORDS.newProduct(),
                                                RECORDS.newStatusCode(),
                                                RECORDS.newProduct(),
                                                RECORDS.newStatusCode());
        RECORDS.newChildSequencingAuthorization(UUID.randomUUID(), null, null,
                                                null);
        RECORDS.newExistential(ExistentialDomain.Agency);
        RECORDS.newExistential(ExistentialDomain.Attribute);
        Interval interval = (Interval) RECORDS.newExistential(ExistentialDomain.Interval);
        interval.setName("foo");
        interval.insert();
        Location location = (Location) RECORDS.newExistential(ExistentialDomain.Location);
        location.setName("foo");
        location.insert();
        Product product = (Product) RECORDS.newExistential(ExistentialDomain.Product);
        product.setName("foo");
        product.insert();
        RECORDS.newExistential(ExistentialDomain.Relationship);
        Tuple<Relationship, Relationship> relationships = RECORDS.newRelationship("a",
                                                                                  "a",
                                                                                  "b",
                                                                                  "b");
        relationships.a.insert();
        relationships.b.insert();
        StatusCode statusCode = (StatusCode) RECORDS.newExistential(ExistentialDomain.StatusCode);
        statusCode.setName("foo");
        statusCode.insert();
        Unit unit = (Unit) RECORDS.newExistential(ExistentialDomain.Unit);
        unit.setName("foo");
        unit.insert();
        RECORDS.newExistential(ExistentialDomain.Agency, "", "");
        RECORDS.newExistentialAttribute();
        RECORDS.newExistentialAttribute(RECORDS.newAttribute());
        RECORDS.newExistentialAttribute(RECORDS.newAttribute(),
                                        RECORDS.newAttribute());
        FacetRecord facet = RECORDS.newFacet();

        facet.setClassification(agency.getId());
        facet.setClassifier(relationships.a.getId());
        facet.insert();
        RECORDS.newExistentialAttributeAuthorization(facet,
                                                     RECORDS.newAttribute());
        RECORDS.newExistentialNetworkAttributeAuthorization(RECORDS.newExistentialNetworkAuthorization(),
                                                            attribute);
        RECORDS.newAgencyExistential();
        RECORDS.newExistentialNetwork();
        RECORDS.newExistentialNetworkAuthorization();
        RECORDS.newExistentialNetworkAttributeAuthorization();

        RECORDS.newExistentialNetworkAttributeAuthorization(RECORDS.newExistentialNetworkAuthorization(),
                                                            attribute);
        RECORDS.newExistentialNetwork(agency, RECORDS.newRelationship(),
                                      agency);
        RECORDS.newExistentialNetworkAttribute();
        RECORDS.newExistentialNetworkAttribute(RECORDS.newAttribute());
        RECORDS.newExistentialAttributeAuthorization();
        RECORDS.newExistentialAttributeAuthorization(facet,
                                                     RECORDS.newAttribute());
        RECORDS.newFacet(RECORDS.newRelationship(), agency);
        RECORDS.newInterval();
        RECORDS.newJob();
        RECORDS.newJobChronology();
        RECORDS.newJobChronology(RECORDS.newJob(), "");
        RECORDS.newLocation();
        RECORDS.newLocation("");
        RECORDS.newLocation("", "");
        RECORDS.newMetaProtocol();
        RECORDS.newNetworkInferrence();
        RECORDS.newNetworkInference(RECORDS.newRelationship(),
                                    RECORDS.newRelationship(),
                                    RECORDS.newRelationship());
        RECORDS.newNetworkInference(UUID.randomUUID(), UUID.randomUUID(),
                                    UUID.randomUUID());
        RECORDS.newParentSequencingAuthorization();
        RECORDS.newParentSequencingAuthorization(RECORDS.newProduct(),
                                                 RECORDS.newStatusCode(),
                                                 RECORDS.newProduct(),
                                                 RECORDS.newStatusCode());
        RECORDS.newParentSequencingAuthorization(UUID.randomUUID(),
                                                 UUID.randomUUID(),
                                                 UUID.randomUUID(),
                                                 UUID.randomUUID());
        RECORDS.newProduct();
        RECORDS.newProduct("");
        RECORDS.newProduct("", "");
        RECORDS.newProtocol();
        RECORDS.newRelationship();
        RECORDS.newRelationship("");
        RECORDS.newRelationship("", "");
        RECORDS.newRelationship("", RECORDS.newRelationship());
        RECORDS.newRelationship("", "", RECORDS.newRelationship());
        RECORDS.newSelfSequencingAuthorization();
        RECORDS.newSelfSequencingAuthorization(RECORDS.newProduct(),
                                               RECORDS.newStatusCode(),
                                               RECORDS.newStatusCode());
        RECORDS.newSiblingSequencingAuthorization();
        RECORDS.newSiblingSequencingAuthorization(UUID.randomUUID(),
                                                  UUID.randomUUID(),
                                                  UUID.randomUUID(),
                                                  UUID.randomUUID());
        RECORDS.newStatusCode();
        RECORDS.newStatusCode("");
        RECORDS.newStatusCode("", "");
        RECORDS.newStatusCodeSequencing();
        RECORDS.newStatusCodeSequencing(RECORDS.newProduct(),
                                        RECORDS.newStatusCode(),
                                        RECORDS.newStatusCode());
        RECORDS.newUnit();
        RECORDS.newUnit("", "");
        RECORDS.newWorkspaceAuthorization();
        Product p = RECORDS.newProduct();
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newChildSequencingAuthorization());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newExistentialAttribute());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newExistentialAttributeAuthorization());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newAgencyExistential());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newExistentialNetwork());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newExistentialNetworkAttribute());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newExistentialNetworkAttributeAuthorization());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newExistentialNetworkAuthorization());
        RECORDS.newWorkspaceAuthorization("", p, facet);
        RECORDS.newWorkspaceAuthorization("", p, RECORDS.newJob());
        RECORDS.newWorkspaceAuthorization("", p, RECORDS.newJobChronology());
        RECORDS.newWorkspaceAuthorization("", p, RECORDS.newMetaProtocol());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newNetworkInferrence());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newParentSequencingAuthorization());
        RECORDS.newWorkspaceAuthorization("", p, RECORDS.newProtocol());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newSelfSequencingAuthorization());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newSiblingSequencingAuthorization());
        RECORDS.newWorkspaceAuthorization("", p,
                                          RECORDS.newStatusCodeSequencing());
        RECORDS.newWorkspaceAuthorization("", p, agency);
        assertNotNull(RECORDS.createExistential(agency.getId(), "foo", "bar"));
        assertNotNull(RECORDS.copy(agency));
        assertEquals("foo", RECORDS.existentialName(agency.getId()));
        assertEquals(facet.getId(), RECORDS.findFacetRecord(facet.getId())
                                           .getId());
        assertEquals(agency.getId(), RECORDS.resolve(agency)
                                            .getId());
        assertEquals(attribute.getId(), RECORDS.resolve(attribute)
                                               .getId());
        assertEquals(interval.getId(), RECORDS.resolve(interval)
                                              .getId());
        assertEquals(location.getId(), RECORDS.resolve(location)
                                              .getId());
        assertEquals(product.getId(), RECORDS.resolve(product)
                                             .getId());
        assertEquals(relationships.a.getId(), RECORDS.resolve(relationships.a)
                                                     .getId());
        assertEquals(statusCode.getId(), RECORDS.resolve(statusCode)
                                                .getId());
        assertEquals(unit.getId(), RECORDS.resolve(unit)
                                          .getId());

        assertEquals(agency.getId(), RECORDS.resolve(agency.getId())
                                            .getId());
        assertEquals(attribute.getId(), RECORDS.resolve(attribute.getId())
                                               .getId());
        assertEquals(interval.getId(), RECORDS.resolve(interval.getId())
                                              .getId());
        assertEquals(location.getId(), RECORDS.resolve(location.getId())
                                              .getId());
        assertEquals(product.getId(), RECORDS.resolve(product.getId())
                                             .getId());
        assertEquals(relationships.a.getId(),
                     RECORDS.resolve(relationships.a.getId())
                            .getId());
        assertEquals(statusCode.getId(), RECORDS.resolve(statusCode.getId())
                                                .getId());
        assertEquals(unit.getId(), RECORDS.resolve(unit.getId())
                                          .getId());
        assertNull(RECORDS.resolveJob(UUID.randomUUID()));
        RECORDS.newExistentialNetworkAttribute(RECORDS.newExistentialNetwork(),
                                               attribute);
    }
}
