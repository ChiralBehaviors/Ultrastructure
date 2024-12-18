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

import static com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot.CONVERT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;

import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetPropertyRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

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

        FacetRecord facet = model.records()
                                 .newFacet(classifier, classification);
        facet.insert(); 

        Agency agency = model.getPhantasmModel()
                             .create(ExistentialDomain.Agency, "aspect test",
                                     "testy", facet);
        assertNotNull(agency);

        FacetPropertyRecord attribute = model.records()
                                   .newFacetProperty();
        attribute.setFacet(facet.getId());
        attribute.setExistential(agency.getId());
        attribute.setProperties(CONVERT.to(JsonNodeFactory.instance.textNode("foo")));
        attribute.insert();  
    }

    @Test
    public void testFindAgencyViaAttribute() {
//        Agency agency = model.records()
//                             .newAgency("Test Agency");
//        agency.insert();
//        Attribute attribute = model.records()
//                                   .newAttribute("Test Attribute", "foo");
//        attribute.setValueType(ValueType.Text);
//        attribute.insert();
//        ExistentialAttributeRecord agencyAttribute = model.records()
//                                                          .newExistentialAttribute(attribute);
//        agencyAttribute.setTextValue("Hello World");
//        agencyAttribute.setExistential(agency.getId());
//        agencyAttribute.insert();
//        agency.refresh();
//
//        Object queryText = "Hello World";
//        List<? extends ExistentialRuleform> foundAgencies = model.getPhantasmModel()
//                                                                 .findByAttributeValue(attribute,
//                                                                                       queryText);
//        assertNotNull(foundAgencies);
//        assertEquals(1, foundAgencies.size());
//        assertEquals(agency, foundAgencies.get(0));
    }

    @Test
    public void testExecuteAs() {
        Agency agency = model.records()
                             .newAgency("Test Agency");
        agency.insert();
        assertEquals(model.getKernel()
                          .getCoreAnimationSoftware()
                          .getId(),
                     agency.getUpdatedBy());
    }

    @Test
    public void testPrincipalFrom() {
        Agency agency = model.records()
                             .newAgency("Test Agency");
        agency.insert();
        assertNotNull(model.principalFrom(agency, Collections.emptyList()));
    }
}
