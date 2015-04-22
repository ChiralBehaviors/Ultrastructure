/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.meta.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import javax.persistence.TypedQuery;

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeMetaAttribute;
import com.chiralbehaviors.CoRE.attribute.AttributeNetwork;
import com.chiralbehaviors.CoRE.attribute.ValueType;
import com.chiralbehaviors.CoRE.network.NetworkInference;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductAttribute;
import com.chiralbehaviors.CoRE.relationship.Relationship;

/**
 * @author hhildebrand
 *
 */
public class AttributeModelTest extends AbstractModelTest {

    @Test
    public void testSimpleNetworkPropagation() {
        Agency core = model.getKernel().getCore();
        Relationship equals = model.getKernel().getEquals();

        em.getTransaction().begin();

        Relationship equals2 = new Relationship("equals 2",
                                                "an alias for equals", core);
        equals2.setInverse(equals2);
        em.persist(equals2);
        NetworkInference aEqualsA = new NetworkInference(equals, equals2,
                                                         equals, core);
        em.persist(aEqualsA);
        Attribute a = new Attribute("A", "A", ValueType.BOOLEAN, core);
        em.persist(a);
        Attribute b = new Attribute("B", "B", ValueType.BOOLEAN, core);
        em.persist(b);
        Attribute c = new Attribute("C", "C", ValueType.BOOLEAN, core);
        em.persist(c);
        AttributeNetwork edgeA = new AttributeNetwork(a, equals, b, core);
        em.persist(edgeA);
        AttributeNetwork edgeB = new AttributeNetwork(b, equals2, c, core);
        em.persist(edgeB);

        em.flush();

        TypedQuery<AttributeNetwork> query = em.createQuery("SELECT edge FROM AttributeNetwork edge WHERE edge.inference IS NOT NULL",
                                                            AttributeNetwork.class);
        List<AttributeNetwork> edges = query.getResultList();
        assertEquals(2, edges.size());
    }

    @Test
    public void testEnumValues() {
        Agency core = model.getKernel().getCore();
        em.getTransaction().begin();
        Attribute attr = new Attribute("Attribute", "A", ValueType.TEXT, core);
        em.persist(attr);

        Attribute validValues = new Attribute(
                                              "ValidValues",
                                              "Valid enumeration values for this attribute",
                                              ValueType.TEXT, core);
        em.persist(validValues);

        AttributeMetaAttribute a = new AttributeMetaAttribute(attr, "a",
                                                              core);
        a.setMetaAttribute(validValues);
        em.persist(a);
        AttributeMetaAttribute b = new AttributeMetaAttribute(attr, "b",
                                                              core);
        b.setMetaAttribute(validValues);
        b.setSequenceNumber(10);
        em.persist(b);
        AttributeMetaAttribute c = new AttributeMetaAttribute(attr, "c",
                                                              core);
        c.setSequenceNumber(100);
        c.setMetaAttribute(validValues);
        em.persist(c);
        model.getAttributeModel().link(attr,
                                       model.getKernel().getIsValidatedBy(),
                                       validValues, core);
        
        Product validatedProduct = new Product(
                                               "ValidatedProduct",
                                               "A product supertype with validation",
                                               core);
        em.persist(validatedProduct);

        Product myProduct = new Product("MyProduct", "my product", core);
        em.persist(myProduct);

        // set value
        ProductAttribute attributeValue = new ProductAttribute(
                                                               attr,
                                                               "a",
                                                               model.getKernel().getCore());
        attributeValue.setProduct(myProduct);

        em.persist(attributeValue);
        em.flush();
        attributeValue.setTextValue("aaa");
        try {
            em.persist(attributeValue);
            em.flush();
            fail();
        } catch (IllegalArgumentException e) {

        }

    }
    
}
