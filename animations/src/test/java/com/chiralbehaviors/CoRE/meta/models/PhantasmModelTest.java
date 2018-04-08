/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.meta.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.enums.ValueType;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellblazer.utils.Tuple;

/**
 * @author hhildebrand
 *
 */
public class PhantasmModelTest extends AbstractModelTest {

    @Test
    public void testAttributeAuthDefaultValues() {
        FacetRecord facet = model.records()
                                 .newFacet(model.getKernel()
                                                .getAnyRelationship(),
                                           model.getKernel()
                                                .getCore());
        Attribute binary = model.records()
                                .newAttribute("binary", "binary attribute",
                                              ValueType.Binary);
        binary.insert();
        Attribute booleanA = model.records()
                                  .newAttribute("binary", "binary attribute",
                                                ValueType.Boolean);
        booleanA.insert();
        Attribute integer = model.records()
                                 .newAttribute("integer", "integer attribute",
                                               ValueType.Integer);
        integer.insert();
        Attribute json = model.records()
                              .newAttribute("json", "json attribute",
                                            ValueType.JSON);
        json.insert();
        Attribute numeric = model.records()
                                 .newAttribute("numeric", "numeric attribute",
                                               ValueType.Numeric);
        numeric.insert();
        Attribute text = model.records()
                              .newAttribute("text", "text attribute",
                                            ValueType.Text);
        text.insert();
        Attribute timestamp = model.records()
                                   .newAttribute("timestamp",
                                                 "timestamp attribute",
                                                 ValueType.Timestamp);
        timestamp.insert();

        ExistentialAttributeAuthorizationRecord value = model.records()
                                                             .newExistentialAttributeAuthorization(facet,
                                                                                                   binary);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, "hello world".getBytes());

        value = model.records()
                     .newExistentialAttributeAuthorization(facet, booleanA);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, true);

        value = model.records()
                     .newExistentialAttributeAuthorization(facet, integer);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, 1);

        value = model.records()
                     .newExistentialAttributeAuthorization(facet, json);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, new ObjectMapper().valueToTree("Hello"));

        value = model.records()
                     .newExistentialAttributeAuthorization(facet, numeric);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, BigDecimal.valueOf(4.5));

        value = model.records()
                     .newExistentialAttributeAuthorization(facet, text);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, "hello");

        value = model.records()
                     .newExistentialAttributeAuthorization(facet, timestamp);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, OffsetDateTime.now());

    }

    @Test
    public void testAttributeNetworkAuthDefaultValues() {
        Attribute binary = model.records()
                                .newAttribute("binary", "binary attribute",
                                              ValueType.Binary);
        binary.insert();
        Attribute booleanA = model.records()
                                  .newAttribute("binary", "binary attribute",
                                                ValueType.Boolean);
        booleanA.insert();
        Attribute integer = model.records()
                                 .newAttribute("integer", "integer attribute",
                                               ValueType.Integer);
        integer.insert();
        Attribute json = model.records()
                              .newAttribute("json", "json attribute",
                                            ValueType.JSON);
        json.insert();
        Attribute numeric = model.records()
                                 .newAttribute("numeric", "numeric attribute",
                                               ValueType.Numeric);
        numeric.insert();
        Attribute text = model.records()
                              .newAttribute("text", "text attribute",
                                            ValueType.Text);
        text.insert();
        Attribute timestamp = model.records()
                                   .newAttribute("timestamp",
                                                 "timestamp attribute",
                                                 ValueType.Timestamp);
        timestamp.insert();

        ExistentialNetworkAuthorizationRecord auth = model.records()
                                                          .newExistentialNetworkAuthorization();
        ExistentialNetworkAttributeAuthorizationRecord value = model.records()
                                                                    .newExistentialNetworkAttributeAuthorization(auth,
                                                                                                                 binary);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, "hello world".getBytes());

        value = model.records()
                     .newExistentialNetworkAttributeAuthorization(auth,
                                                                  booleanA);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, true);

        value = model.records()
                     .newExistentialNetworkAttributeAuthorization(auth,
                                                                  integer);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, 1);

        value = model.records()
                     .newExistentialNetworkAttributeAuthorization(auth, json);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, new ObjectMapper().valueToTree("Hello"));

        value = model.records()
                     .newExistentialNetworkAttributeAuthorization(auth,
                                                                  numeric);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, BigDecimal.valueOf(4.5));

        value = model.records()
                     .newExistentialNetworkAttributeAuthorization(auth, text);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, "hello");

        value = model.records()
                     .newExistentialNetworkAttributeAuthorization(auth,
                                                                  timestamp);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, OffsetDateTime.now());

    }

    @Test
    public void testAttributeValues() {
        Attribute binary = model.records()
                                .newAttribute("binary", "binary attribute",
                                              ValueType.Binary);
        binary.insert();
        model.getPhantasmModel()
             .valueClass(binary);
        Attribute booleanA = model.records()
                                  .newAttribute("boolean", "boolean attribute",
                                                ValueType.Boolean);
        booleanA.insert();
        model.getPhantasmModel()
             .valueClass(booleanA);
        Attribute integer = model.records()
                                 .newAttribute("integer", "integer attribute",
                                               ValueType.Integer);
        integer.insert();
        model.getPhantasmModel()
             .valueClass(integer);
        Attribute json = model.records()
                              .newAttribute("json", "json attribute",
                                            ValueType.JSON);
        json.insert();
        model.getPhantasmModel()
             .valueClass(json);
        Attribute numeric = model.records()
                                 .newAttribute("numeric", "numeric attribute",
                                               ValueType.Numeric);
        numeric.insert();
        model.getPhantasmModel()
             .valueClass(numeric);
        Attribute text = model.records()
                              .newAttribute("text", "text attribute",
                                            ValueType.Text);
        text.insert();
        model.getPhantasmModel()
             .valueClass(text);
        Attribute timestamp = model.records()
                                   .newAttribute("timestamp",
                                                 "timestamp attribute",
                                                 ValueType.Timestamp);
        timestamp.insert();
        model.getPhantasmModel()
             .valueClass(timestamp);

        Agency core = model.getKernel()
                           .getCore();

        ExistentialAttributeRecord value = model.records()
                                                .newExistentialAttribute(core,
                                                                         binary);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, "hello world".getBytes());
        model.getPhantasmModel()
             .getValue(value);
        model.getPhantasmModel()
             .findByAttributeValue(binary, "hello world".getBytes());

        value = model.records()
                     .newExistentialAttribute(core, booleanA);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, true);
        model.getPhantasmModel()
             .getValue(value);
        model.getPhantasmModel()
             .findByAttributeValue(booleanA, false);

        value = model.records()
                     .newExistentialAttribute(core, integer);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, 1);
        model.getPhantasmModel()
             .getValue(value);
        model.getPhantasmModel()
             .findByAttributeValue(integer, 1);

        value = model.records()
                     .newExistentialAttribute(core, json);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, new ObjectMapper().valueToTree("Hello"));
        model.getPhantasmModel()
             .getValue(value);

        value = model.records()
                     .newExistentialAttribute(core, numeric);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, BigDecimal.valueOf(4.5));
        model.getPhantasmModel()
             .getValue(value);
        model.getPhantasmModel()
             .findByAttributeValue(numeric, BigDecimal.valueOf(4.5));

        value = model.records()
                     .newExistentialAttribute(core, text);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, "hello");
        model.getPhantasmModel()
             .getValue(value);
        model.getPhantasmModel()
             .findByAttributeValue(text, "hello");

        value = model.records()
                     .newExistentialAttribute(core, timestamp);
        value.insert();
        OffsetDateTime ts = OffsetDateTime.now();
        model.getPhantasmModel()
             .setValue(value, ts);
        model.getPhantasmModel()
             .getValue(value);
        model.getPhantasmModel()
             .findByAttributeValue(timestamp, ts);

    }

    @Test
    public void testGetFacets() {
        Product kernelWorkspace = model.getKernel()
                                       .getKernelWorkspace();
        assertEquals(15, model.getPhantasmModel()
                              .getFacets(kernelWorkspace)
                              .size());
    }

    @Test
    public void testNetworkAttributeValues() {
        Attribute binary = model.records()
                                .newAttribute("binary", "binary attribute",
                                              ValueType.Binary);
        binary.insert();
        model.getPhantasmModel()
             .valueClass(binary);
        Attribute booleanA = model.records()
                                  .newAttribute("boolean", "boolean attribute",
                                                ValueType.Boolean);
        booleanA.insert();
        model.getPhantasmModel()
             .valueClass(booleanA);
        Attribute integer = model.records()
                                 .newAttribute("integer", "integer attribute",
                                               ValueType.Integer);
        integer.insert();
        model.getPhantasmModel()
             .valueClass(integer);
        Attribute json = model.records()
                              .newAttribute("json", "json attribute",
                                            ValueType.JSON);
        json.insert();
        model.getPhantasmModel()
             .valueClass(json);
        Attribute numeric = model.records()
                                 .newAttribute("numeric", "numeric attribute",
                                               ValueType.Numeric);
        numeric.insert();
        model.getPhantasmModel()
             .valueClass(numeric);
        Attribute text = model.records()
                              .newAttribute("text", "text attribute",
                                            ValueType.Text);
        text.insert();
        model.getPhantasmModel()
             .valueClass(text);
        Attribute timestamp = model.records()
                                   .newAttribute("timestamp",
                                                 "timestamp attribute",
                                                 ValueType.Timestamp);
        timestamp.insert();
        model.getPhantasmModel()
             .valueClass(timestamp);

        Tuple<ExistentialNetworkRecord, ExistentialNetworkRecord> edges = model.getPhantasmModel()
                                                                               .link(text,
                                                                                     model.getKernel()
                                                                                          .getAnyRelationship(),
                                                                                     timestamp);
        edges.a.insert();
        edges.b.insert();

        ExistentialNetworkRecord edge = edges.a;

        ExistentialNetworkAttributeRecord value = model.records()
                                                       .newExistentialNetworkAttribute(edge,
                                                                                       binary);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, "hello world".getBytes());

        value = model.records()
                     .newExistentialNetworkAttribute(edge, booleanA);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, true);

        value = model.records()
                     .newExistentialNetworkAttribute(edge, integer);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, 1);

        value = model.records()
                     .newExistentialNetworkAttribute(edge, json);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, new ObjectMapper().valueToTree("Hello"));

        value = model.records()
                     .newExistentialNetworkAttribute(edge, numeric);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, BigDecimal.valueOf(4.5));

        value = model.records()
                     .newExistentialNetworkAttribute(edge, text);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, "hello");

        value = model.records()
                     .newExistentialNetworkAttribute(edge, timestamp);
        value.insert();
        model.getPhantasmModel()
             .setValue(value, OffsetDateTime.now());

    }

    @Test
    public void testNotInGroup() {
        Relationship classifier = model.records()
                                       .newRelationship("test not in group classifier");
        classifier.insert();
        Relationship inverse = model.records()
                                    .newRelationship("inverse test not in group classifier");
        inverse.insert();
        classifier.setInverse(inverse.getId());
        inverse.setInverse(classifier.getId());
        Agency classification = model.records()
                                     .newAgency("test not in group agency classification");
        classification.insert();
        List<ExistentialRuleform> notInGroup = model.getPhantasmModel()
                                                    .getNotInGroup(classification,
                                                                   inverse,
                                                                   ExistentialDomain.Agency);
        assertNotNull(notInGroup);
        assertTrue(!notInGroup.isEmpty());
    }
}
