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

import static org.junit.Assert.fail;

import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.enums.ValueType;
import com.chiralbehaviors.CoRE.jooq.tables.ExistentialAttribute;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;

/**
 * @author hhildebrand
 *
 */
public class AttributeModelTest extends AbstractModelTest {

    @Test
    public void testEnumValues() {
        Agency core = model.getKernel()
                           .getCore();
        Attribute attr = new Attribute("Attribute", "A", ValueType.TEXT, core);
        em.persist(attr);

        Attribute validValues = new Attribute("ValidValues",
                                              "Valid enumeration values for this attribute",
                                              ValueType.TEXT, core);
        em.persist(validValues);

        ExistentialAttributeRecord a = model.records()
                                            .newExistentialAttribute(attr, "a",
                                                                     core);
        a.setMetaAttribute(validValues);
        em.persist(a);
        ExistentialAttributeRecord b = new ExistentialAttribute(attr, "b",
                                                                core);
        b.setMetaAttribute(validValues);
        b.setSequenceNumber(10);
        em.persist(b);
        ExistentialAttributeRecord c = new ExistentialAttribute(attr, "c",
                                                                core);
        c.setSequenceNumber(100);
        c.setMetaAttribute(validValues);
        em.persist(c);
        model.getAttributeModel()
             .link(attr, model.getKernel()
                              .getIsValidatedBy(),
                   validValues, core);

        Product validatedProduct = new Product("ValidatedProduct",
                                               "A product supertype with validation",
                                               core);
        em.persist(validatedProduct);

        Product myProduct = new Product("MyProduct", "my product", core);
        em.persist(myProduct);

        // set value
        ProductAttribute attributeValue = new ProductAttribute(attr, "a",
                                                               model.getKernel()
                                                                    .getCore());
        attributeValue.setProduct(myProduct);

        em.persist(attributeValue);
        em.flush();
        attributeValue.setValue("aaa");
        try {
            em.persist(attributeValue);
            em.flush();
            fail();
        } catch (IllegalArgumentException e) {

        }

    }

}
