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
package com.chiralbehaviors.CoRE.product;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Set;

import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.ValueType;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.test.DatabaseTest;

/**
 * @author hhildebrand
 *
 */

public class ProductTest extends DatabaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(ProductTest.class);

    @Override
    @After
    public void after() {
        em.getTransaction()
          .rollback();
        em.clear();
    }

    @Test
    public void createEntity() {
        TypedQuery<Agency> query = em.createNamedQuery("agency.findByName",
                                                       Agency.class)
                                     .setParameter("name", "CoREd");
        Agency r = query.getSingleResult();

        LOG.debug(String.format("Agency: %s", r));

        assertNotNull("Agency was null!", r);
        assertEquals("CoREd", r.getName());

        Product b = new Product();

        String name = "New Product";
        b.setName(name);
        b.setDescription("An Product created solely for testing purposes");
        b.setUpdatedBy(r);

        em.persist(b);
        em.flush();

        // Now check to see that the Product you just made actually got into
        // the database.

        em.clear();

        TypedQuery<Product> productQuery = em.createNamedQuery("product.findByName",
                                                               Product.class)
                                             .setParameter("name", name);

        Product b2 = productQuery.getSingleResult();

        assertNotNull("Retrieved Product was null!", b2);
        assertTrue(b != b2);
        assertEquals(b, b2);
    }

    @Before
    public void initData() {
        Agency core = new Agency("CoREd");
        core.setUpdatedBy(core);
        em.persist(core);

        Product peptideFoo = new Product("Peptide Foo",
                                         "The Foo peptide is lethal!  Do not eat!",
                                         core);
        em.persist(peptideFoo);

        Product peptideBar = new Product("Peptide Bar",
                                         "The Foo peptide is lethal!  Do not eat!",
                                         core);
        em.persist(peptideBar);

        Attribute diagram = new Attribute("Diagram",
                                          "The D3 Net of the molecule",
                                          ValueType.JSON, core);
        em.persist(diagram);

        Unit aminoAcids = new Unit("Amino Acids",
                                   "A unit of length for protein primary sequences",
                                   core);
        aminoAcids.setAbbreviation("aa");
        em.persist(aminoAcids);

        ProductAttribute attribute = new ProductAttribute(peptideFoo, diagram,
                                                          core);
        attribute.setUnit(aminoAcids);
        attribute.setValue("Fooled ya");
        em.persist(attribute);
        em.flush();
    }

    @SuppressWarnings("boxing")
    @Test
    public void testAttributes() {
        TypedQuery<Product> findProduct = em.createNamedQuery("product.findByName",
                                                              Product.class)
                                            .setParameter("name",
                                                          "Peptide Foo");
        Product b = findProduct.getSingleResult();
        assertNotNull(b);
        assertEquals(b.getName(), "Peptide Foo");
        LOG.debug(String.format("Product is: %s", b));

        TypedQuery<Attribute> findAttributeValue = em.createNamedQuery("attribute.findByName",
                                                                       Attribute.class)
                                                     .setParameter("name",
                                                                   "Diagram");
        TypedQuery<Attribute> findAttribute = findAttributeValue;

        Attribute a = findAttribute.getSingleResult();
        assertNotNull(a);
        assertEquals(a.getName(), "Diagram");
        LOG.debug(String.format("Attribute is: %s", a));
        em.refresh(b);
        Set<ProductAttribute> productAttributes = b.getAttributes();
        assertNotNull(productAttributes);
        assertEquals(1, productAttributes.size());

        Iterator<ProductAttribute> iter = productAttributes.iterator();
        ProductAttribute bea = iter.next();
        assertNotNull(bea);
        assertEquals(b, bea.getProduct());
        assertEquals(a, bea.getAttribute());
        em.flush();
        em.clear();
        b = em.merge(b);
        a = em.merge(a);
        ProductAttribute value = em.createNamedQuery("productAttribute.getAttribute",
                                                     ProductAttribute.class)
                                   .setParameter("ruleform", b)
                                   .setParameter("attribute", a)
                                   .getSingleResult();

        assertEquals("Fooled ya", value.getValue());
    }
}
