/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
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
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Facet;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * @author hhildebrand
 * 
 */
public class ModelTest extends AbstractModelTest {
    @Test
    public void testCreateFromAspects() {
        em.getTransaction().begin();

        Agency classifier = new Agency("aspect classifer", kernel.getCore());
        em.persist(classifier);
        Relationship classification = new Relationship("aspect classification",
                                                       kernel.getCore());
        classification.setInverse(classification);
        em.persist(classification);

        Attribute attribute = new Attribute("aspect attribute",
                                            kernel.getCore());
        attribute.setValueType(ValueType.TEXT);
        em.persist(attribute);

        Aspect<Agency> aspect = new Aspect<Agency>(classification, classifier);

        model.getAgencyModel().authorize(aspect, attribute);
        em.getTransaction().commit();

        em.getTransaction().begin();

        @SuppressWarnings("unchecked")
        Agency agency = model.getAgencyModel().create("aspect test", "testy",
                                                      aspect);

        em.getTransaction().commit();

        assertNotNull(agency);

        Facet<Agency, AgencyAttribute> facet = model.getAgencyModel().getFacet(agency,
                                                                               aspect);

        assertEquals(1, facet.getAttributes().size());

        for (Attribute value : facet.getAttributes().keySet()) {
            assertEquals(attribute, value);
        }
    }

    @Test
    public void testFindAgencyViaAttribute() {
        em.getTransaction().begin();

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

        em.getTransaction().commit();

        AgencyAttribute queryAttribute = new AgencyAttribute(attribute);
        queryAttribute.setTextValue("Hello World");
        List<Agency> foundAgencies = model.find(queryAttribute);
        assertNotNull(foundAgencies);
        assertEquals(1, foundAgencies.size());
        assertEquals(agency, foundAgencies.get(0));
    }
}
