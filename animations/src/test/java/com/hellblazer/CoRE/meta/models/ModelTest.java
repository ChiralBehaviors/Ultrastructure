/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.hellblazer.CoRE.meta.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.ValueType;
import com.hellblazer.CoRE.network.Aspect;
import com.hellblazer.CoRE.network.Facet;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;
import com.hellblazer.CoRE.resource.ResourceAttribute;

/**
 * @author hhildebrand
 * 
 */
public class ModelTest extends AbstractModelTest {
    @Test
    public void testCreateFromAspects() {
        em.getTransaction().begin();

        Resource classifier = new Resource("aspect classifer", kernel.getCore());
        em.persist(classifier);
        Relationship classification = new Relationship("aspect classification",
                                                       kernel.getCore());
        classification.setInverse(classification);
        em.persist(classification);

        Attribute attribute = new Attribute("aspect attribute",
                                            kernel.getCore());
        attribute.setValueType(ValueType.TEXT);
        em.persist(attribute);

        Aspect<Resource> aspect = new Aspect<Resource>(classification,
                                                       classifier);

        model.getResourceModel().authorize(aspect, attribute);
        em.getTransaction().commit();

        em.getTransaction().begin();

        @SuppressWarnings("unchecked")
        Resource resource = model.getResourceModel().create("aspect test",
                                                            "testy", aspect);

        em.getTransaction().commit();

        assertNotNull(resource);

        Facet<Resource, ResourceAttribute> facet = model.getResourceModel().getFacet(resource,
                                                                                     aspect);

        assertEquals(1, facet.getAttributes().size());

        for (Attribute value : facet.getAttributes().keySet()) {
            assertEquals(attribute, value);
        }
    }

    @Test
    public void testFindResourceViaAttribute() {
        em.getTransaction().begin();

        Resource resource = new Resource("Test Resource", kernel.getCore());
        em.persist(resource);
        Attribute attribute = new Attribute("Test Attribute", kernel.getCore());
        attribute.setValueType(ValueType.TEXT);
        em.persist(attribute);
        ResourceAttribute resourceAttribute = new ResourceAttribute(
                                                                    kernel.getCore());
        resourceAttribute.setResource(resource);
        resourceAttribute.setAttribute(attribute);
        resourceAttribute.setTextValue("Hello World");
        em.persist(resourceAttribute);

        em.getTransaction().commit();

        ResourceAttribute queryAttribute = new ResourceAttribute(attribute);
        queryAttribute.setTextValue("Hello World");
        List<Resource> foundResources = model.find(queryAttribute);
        assertNotNull(foundResources);
        assertEquals(1, foundResources.size());
        assertEquals(resource, foundResources.get(0));
    }
}
