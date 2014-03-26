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

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.ValueType;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductAttribute;
import com.chiralbehaviors.CoRE.test.DatabaseTest;

/**
 * @author hhildebrand
 * 
 */
public class ProductSearchTest extends DatabaseTest {

    @Test()
    public void findByIdTest() {
        Model model = new ModelImpl(em, null);
        Product b = new ModelImpl(em, null).find("Mass List 1", Product.class);
        assertNotNull(b);
        Product c = model.find(b.getId(), Product.class);
        assertEquals("Mass List 1", c.getName());
    }

    @Test()
    public void findByNameTest() {
        Product b = new ModelImpl(em, null).find("Mass List 1", Product.class);
        assertNotNull(b);
        assertEquals("Mass List 1", b.getName());
    }

    @Before
    public void initData() {
        beginTransaction();

        Agency core = new Agency("CoRE");
        core.setUpdatedBy(core);
        em.persist(core);

        Agency pklFile1 = new Agency("PKL File 1", core);
        em.persist(pklFile1);

        Agency pklFile2 = new Agency("PKL File 2", core);
        em.persist(pklFile2);

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

        Relationship massList = new Relationship(
                                                 "mass-list",
                                                 "A is a member of the mass list B",
                                                 core);
        em.persist(massList);

        Relationship massListOf = new Relationship(
                                                   "mass-list-of",
                                                   "A is a mass list that has B as a member",
                                                   core, massList);
        em.persist(massListOf);

        Relationship ionType = new Relationship("ion-type",
                                                "A is an ion of type B", core);
        em.persist(ionType);

        Relationship ionTypeOf = new Relationship(
                                                  "ion-type-of",
                                                  "A is the kind of ion that B is",
                                                  core, ionType);
        em.persist(ionTypeOf);

        Relationship collectionType = new Relationship(
                                                       "collection-type",
                                                       "A is a collection of type B",
                                                       core);
        em.persist(collectionType);

        Relationship collectionTypeOf = new Relationship(
                                                         "collection-type-of",
                                                         "A is the kind of collection that B is",
                                                         core, collectionType);
        em.persist(collectionTypeOf);

        Product massList1 = new Product("Mass List 1", core);
        em.persist(massList1);

        Product precursorIon1 = new Product("Precursor Ion 1", core);
        em.persist(precursorIon1);

        Product precursorIon2 = new Product("Precursor Ion 2", core);
        em.persist(precursorIon2);

        Product massList2 = new Product("Mass List 2", core);
        em.persist(massList2);

        Product precursorIon3 = new Product("Precursor Ion 3", core);
        em.persist(precursorIon3);

        Product precursorIon4 = new Product("Precursor Ion 4", core);
        em.persist(precursorIon4);

        Product productIon = new Product("Product Ion", core);
        em.persist(productIon);

        Attribute groupNamePrefix = new Attribute(
                                                  "CoRE Group Name Prefix",
                                                  "The string used to generate sequential Product names for an object",
                                                  ValueType.TEXT, core);
        em.persist(groupNamePrefix);

        Attribute groupNameSequenceObject = new Attribute(
                                                          "CoRE Group Name Sequence Object",
                                                          "The Postgres Sequence Object used to generate sequential Product names for an object",
                                                          ValueType.TEXT, core);
        em.persist(groupNameSequenceObject);

        ProductAttribute av1 = new ProductAttribute(groupNamePrefix,
                                                    "Product Ion", core);
        av1.setProduct(precursorIon1);
        em.persist(av1);

        ProductAttribute av2 = new ProductAttribute(groupNameSequenceObject,
                                                    "precursor_ion_seq", core);
        av2.setProduct(precursorIon1);
        em.persist(av2);

        precursorIon1.link(massList, massList1, core, core, em);
        precursorIon2.link(massList, massList1, core, core, em);
        precursorIon3.link(massList, massList1, core, core, em);
        precursorIon4.link(massList, massList1, core, core, em);
        commitTransaction();
    }
}
