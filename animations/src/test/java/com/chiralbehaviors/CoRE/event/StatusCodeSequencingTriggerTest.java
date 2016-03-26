/**
 * Copyright 2014, Chiral Behaviors, LLC.
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
package com.chiralbehaviors.CoRE.event;

import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.StatusCode;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.hellblazer.utils.Tuple;

/**
 * @author hparry
 *
 */
public class StatusCodeSequencingTriggerTest extends AbstractModelTest {

    @Test
    public void test2InitialStates() throws SQLException {
        Product service = model.getKernel()
                               .getNotApplicableProduct();
        StatusCode a = model.records()
                            .newStatusCode("A", null);
        a.insert();
        StatusCode b = model.records()
                            .newStatusCode("B", null);
        b.insert();
        StatusCode x = model.records()
                            .newStatusCode("X", null);
        x.insert();

        List<Tuple<StatusCode, StatusCode>> codes = new ArrayList<>();
        codes.add(new Tuple<StatusCode, StatusCode>(a, x));
        codes.add(new Tuple<StatusCode, StatusCode>(b, x));
        model.getJobModel()
             .createStatusCodeSequencings(service, codes);
        try {
            model.flush();
            fail("Insert should not have succeeded");
        } catch (Exception e) {
            // expected
        }
    }
}
