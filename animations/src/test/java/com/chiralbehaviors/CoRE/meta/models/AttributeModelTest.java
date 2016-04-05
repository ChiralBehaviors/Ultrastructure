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
        Attribute attr = model.records()
                              .newAttribute("Attribute", "A", ValueType.Text);
        attr.insert();

        Attribute validValues = model.records()
                                     .newAttribute("ValidValues",
                                                   "Valid enumeration values for this attribute",
                                                   ValueType.Text);
        validValues.insert();

        ExistentialAttributeRecord a = model.records()
                                            .newExistentialAttribute(validValues);
        a.setExistential(core.getId());
        a.insert();
        ExistentialAttributeRecord b = model.records()
                                            .newExistentialAttribute(validValues);
        b.setExistential(core.getId());
        b.setSequenceNumber(10);
        b.insert();
        ExistentialAttributeRecord c = model.records()
                                            .newExistentialAttribute(validValues);
        c.setExistential(core.getId());
        c.setSequenceNumber(100);
        c.insert();
        model.getPhantasmModel()
             .link(attr, model.getKernel()
                              .getIsValidatedBy(),
                   validValues);

        Product validatedProduct = model.records()
                                        .newProduct("ValidatedProduct",
                                                    "A product supertype with validation");
        validatedProduct.insert();

        Product myProduct = model.records()
                                 .newProduct("MyProduct", "my product");
        myProduct.insert();

        // set value
        ExistentialAttributeRecord attributeValue = model.records()
                                                         .newExistentialAttribute(attr);
        attributeValue.setExistential(myProduct.getId());
        attributeValue.insert();
        attributeValue.setTextValue("a");
        attributeValue.setTextValue("aaa");
        attributeValue.update();
        try {
            model.flush();
            fail();
        } catch (IllegalArgumentException e) {

        }

    }

}
