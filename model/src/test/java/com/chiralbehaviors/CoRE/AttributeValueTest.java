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
package com.chiralbehaviors.CoRE;

import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_ATTRIBUTE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.enums.ValueType;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.test.DatabaseTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;;

/**
 * @author hhildebrand
 *
 */

public class AttributeValueTest extends DatabaseTest {

    @Test
    public void setInverseTest() throws Exception {
        Attribute attr = RECORDS.newAttribute("Attribute", "A", ValueType.JSON);
        attr.insert();

        Product myProduct = RECORDS.newProduct("MyProduct", "my product");
        myProduct.insert();

        // set value
        ExistentialAttributeRecord attributeValue = RECORDS.newExistentialAttribute(attr);
        attributeValue.setExistential(myProduct.getId());
        attributeValue.insert();
        Map<String, String> test = new HashMap<>();
        test.put("foo", "bar");
        attributeValue.setJsonValue(new ObjectMapper().valueToTree(test));
        attributeValue.update();
        ExistentialAttributeRecord fetched = RECORDS.create()
                                                    .selectFrom(EXISTENTIAL_ATTRIBUTE)
                                                    .where(EXISTENTIAL_ATTRIBUTE.ID.equal(attributeValue.getId()))
                                                    .fetchOne();
        assertNotNull(fetched);
        JsonNode node = fetched.getJsonValue();
        assertNotNull(fetched);
        @SuppressWarnings("rawtypes")
        Map rehydrated = new ObjectMapper().treeToValue(node, Map.class);
        assertNotNull(rehydrated);
        assertEquals("bar", rehydrated.get("foo"));
    }
}
