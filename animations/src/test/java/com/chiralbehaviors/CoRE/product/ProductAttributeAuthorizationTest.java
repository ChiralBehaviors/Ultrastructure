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

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.ValueType;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.relationship.Relationship;

/**
 * @author hhildebrand
 *
 */
public class ProductAttributeAuthorizationTest extends AbstractModelTest {

    @Test
    public void testAllowedNumericValues() throws Exception {
        em.getTransaction()
          .begin();

        Agency agency = new Agency();
        agency.setName("Primordial Agency");
        agency.setDescription("Just ye olde time agency");
        agency.setUpdatedBy(agency);
        em.persist(agency);

        Relationship classifier = new Relationship("My classifier",
                                                   "A classifier", agency);
        em.persist(classifier);
        Relationship inverse = new Relationship("inverse classifier",
                                                "The inverse classifier",
                                                agency, classifier);
        em.persist(inverse);

        Product classificationProduct = new Product();
        classificationProduct.setName("Classification Product");
        classificationProduct.setUpdatedBy(agency);
        em.persist(classificationProduct);

        Attribute authorizedAttribute = new Attribute();
        authorizedAttribute.setName("My classifier");
        authorizedAttribute.setUpdatedBy(agency);
        authorizedAttribute.setValueType(ValueType.NUMERIC);
        em.persist(authorizedAttribute);

        model.getProductModel()
             .getAllowedValues(authorizedAttribute,
                               new Aspect<Product>(classifier,
                                                   classificationProduct));
        em.getTransaction()
          .rollback();
    }
}
