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
package com.chiralbehaviors.CoRE.product;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
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
import com.chiralbehaviors.CoRE.network.Relationship;
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
        em.getTransaction().rollback();
        em.clear();
    }

    @Test
    public void createEntity() {
        TypedQuery<Agency> query = em.createNamedQuery("agency.findByName",
                                                       Agency.class).setParameter("name",
                                                                                  "CoRE");
        Agency r = query.getSingleResult();

        LOG.debug(String.format("Agency: %s", r));

        assertNotNull("Agency was null!", r);
        assertEquals("CoRE", r.getName());

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
                                                               Product.class).setParameter("name",
                                                                                           name);

        Product b2 = productQuery.getSingleResult();

        assertNotNull("Retrieved Product was null!", b2);
        assertTrue(b != b2);
        assertEquals(b, b2);
    }

    @Before
    public void initData() {
        Agency core = new Agency("CoRE");
        core.setUpdatedBy(core);
        em.persist(core);

        Product peptideFoo = new Product(
                                         "Peptide Foo",
                                         "The Foo peptide is lethal!  Do not eat!",
                                         core);
        em.persist(peptideFoo);

        Product peptideBar = new Product(
                                         "Peptide Bar",
                                         "The Foo peptide is lethal!  Do not eat!",
                                         core);
        em.persist(peptideBar);

        Attribute length = new Attribute(
                                         "Length",
                                         "Denotes the linear length of a thing",
                                         ValueType.NUMERIC, core);
        em.persist(length);

        Unit aminoAcids = new Unit(
                                   "Amino Acids",
                                   "A unit of length for protein primary sequences",
                                   core);
        aminoAcids.setAbbreviation("aa");
        em.persist(aminoAcids);

        ProductAttribute attribute = new ProductAttribute(length, core);
        attribute.setUnit(aminoAcids);
        attribute.setProduct(peptideFoo);
        attribute.setNumericValue(BigDecimal.valueOf(123));
        em.persist(attribute);

        Relationship isA = new Relationship(
                                            "is a",
                                            "Taxonomic relationship indicating membership in a group or category.",
                                            core);
        em.persist(isA);

        Relationship includes = new Relationship(
                                                 "includes",
                                                 "Taxonomic relationship defining membership in a group or category.  In 'A includes B', A is the more general product, while B is some specialization or grouping of A",
                                                 core, isA);
        em.persist(includes);
        em.flush();
        em.clear();
    }

    @SuppressWarnings("boxing")
    @Test
    public void testAttributes() {
        TypedQuery<Product> findProduct = em.createNamedQuery("product.findByName",
                                                              Product.class).setParameter("name",
                                                                                          "Peptide Foo");
        Product b = findProduct.getSingleResult();
        assertNotNull(b);
        assertEquals(b.getName(), "Peptide Foo");
        LOG.debug(String.format("Product is: %s", b));

        TypedQuery<Attribute> findAttribute = em.createNamedQuery("attribute.findByName",
                                                                  Attribute.class).setParameter("name",
                                                                                                "Length");

        Attribute a = findAttribute.getSingleResult();
        assertNotNull(a);
        assertEquals(a.getName(), "Length");
        LOG.debug(String.format("Attribute is: %s", a));

        Set<ProductAttribute> productAttributes = b.getAttributes();
        assertNotNull(productAttributes);
        assertEquals(1, productAttributes.size());

        Iterator<ProductAttribute> iter = productAttributes.iterator();
        ProductAttribute bea = iter.next();
        assertNotNull(bea);
        assertEquals(b, bea.getProduct());
        assertEquals(a, bea.getAttribute());

        assertEquals(new BigDecimal("123"), bea.getNumericValue());
    }
}
