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
package com.hellblazer.CoRE.product;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.TypedQuery;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.ValueType;
import com.hellblazer.CoRE.attribute.unit.Unit;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.test.DatabaseTest;

/**
 * @author hhildebrand
 * 
 */

public class ProductTest extends DatabaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(ProductTest.class);

    @Test
    public void createEntity() {
        beginTransaction();

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
        b.setPinned(false);

        em.persist(b);
        commitTransaction();

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
        beginTransaction();

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
                                            "is-a",
                                            "Taxonomic relationship indicating membership in a group or category.",
                                            core);
        em.persist(isA);

        Relationship includes = new Relationship(
                                                 "includes",
                                                 "Taxonomic relationship defining membership in a group or category.  In 'A includes B', A is the more general product, while B is some specialization or grouping of A",
                                                 core, isA);
        em.persist(includes);

        commitTransaction();
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
