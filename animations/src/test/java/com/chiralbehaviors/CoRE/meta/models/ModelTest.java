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

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyAttribute;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.ValueType;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.relationship.Relationship;

/**
 * @author hhildebrand
 *
 */
public class ModelTest extends AbstractModelTest {
    @Test
    public void testCreateFromAspects() {

        Agency classification = new Agency("aspect classifer",
                                           kernel.getCore());
        em.persist(classification);
        Relationship classifier = new Relationship("aspect classifier",
                                                   kernel.getCore());
        classifier.setInverse(classifier);
        em.persist(classifier);

        Attribute attribute = new Attribute("aspect attribute",
                                            kernel.getCore());
        attribute.setValueType(ValueType.TEXT);
        em.persist(attribute);

        Aspect<Agency> aspect = new Aspect<Agency>(classifier, classification);

        model.getAgencyModel()
             .authorize(aspect, attribute);
        em.flush();

        @SuppressWarnings("unchecked")
        Agency agency = model.getAgencyModel()
                             .create("aspect test", "testy", aspect,
                                     kernel.getCore());
        em.flush();

        assertNotNull(agency);

        List<AgencyAttribute> attributes = model.getAgencyModel()
                                                .getAttributesClassifiedBy(agency,
                                                                           aspect);

        assertEquals(1, attributes.size());

        assertEquals(attribute, attributes.get(0)
                                          .getAttribute());
    }

    @Test
    public void testFindAgencyViaAttribute() {
        Agency agency = new Agency("Test Agency", kernel.getCore());
        em.persist(agency);
        Attribute attribute = new Attribute("Test Attribute", kernel.getCore());
        attribute.setValueType(ValueType.TEXT);
        em.persist(attribute);
        AgencyAttribute agencyAttribute = new AgencyAttribute(kernel.getCore());
        agencyAttribute.setAgency(agency);
        agencyAttribute.setAttribute(attribute);
        agencyAttribute.setTextValue("Hello World");
        em.persist(agencyAttribute);
        em.flush();

        AgencyAttribute queryAttribute = new AgencyAttribute(attribute);
        queryAttribute.setTextValue("Hello World");
        List<Agency> foundAgencies = model.find(queryAttribute);
        assertNotNull(foundAgencies);
        assertEquals(1, foundAgencies.size());
        assertEquals(agency, foundAgencies.get(0));
    }
}
