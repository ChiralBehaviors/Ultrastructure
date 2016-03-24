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

import java.util.List;

import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.enums.ValueType;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;

/**
 * @author hhildebrand
 *
 */
public class ModelTest extends AbstractModelTest {
    @Test
    public void testCreateFromAspects() {

        Agency classification = model.records()
                                     .newAgency("aspect classifer");
        classification.insert();
        Relationship classifier = model.records()
                                       .newRelationship("aspect classifier");
        classifier.setInverse(classifier.getId());
        classifier.insert();

        Attribute attribute = model.records()
                                   .newAttribute("aspect attribute", "foo");
        attribute.setValueType(ValueType.Text);
        attribute.insert();

        FacetRecord facet = model.records()
                                 .newFacet(classifier, classification);
        facet.insert();

        model.getPhantasmModel()
             .authorize(facet, attribute);

        Agency agency = model.getAgencyModel()
                             .create("aspect test", "testy", facet);
        assertNotNull(agency);

        List<ExistentialAttributeRecord> attributes = model.getPhantasmModel()
                                                           .getAttributesClassifiedBy(agency,
                                                                                      facet);

        assertEquals(1, attributes.size());

        assertEquals(attribute, attributes.get(0)
                                          .getAttribute());
    }

    @Test
    public void testFindAgencyViaAttribute() {
        Agency agency = model.records()
                             .newAgency("Test Agency");
        agency.insert();
        Attribute attribute = model.records()
                                   .newAttribute("Test Attribute", "foo");
        attribute.setValueType(ValueType.Text);
        attribute.insert();
        ExistentialAttributeRecord agencyAttribute = model.records()
                                                          .newExistentialAttribute(attribute);
        agencyAttribute.setTextValue("Hello World");
        agencyAttribute.setExistential(agency.getId());
        agencyAttribute.insert();

        Object queryText = "Hello World";
        List<Agency> foundAgencies = model.getPhantasmModel()
                                          .findByAttributeValue(attribute,
                                                                queryText,
                                                                ExistentialDomain.Agency);
        assertNotNull(foundAgencies);
        assertEquals(1, foundAgencies.size());
        assertEquals(agency, foundAgencies.get(0));
    }
}
