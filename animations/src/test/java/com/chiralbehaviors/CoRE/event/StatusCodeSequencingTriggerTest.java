/**
 * Copyright (c) 2014 Chiral Behaviors, LLC, all rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chiralbehaviors.CoRE.event;

import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.product.Product;
import com.hellblazer.utils.Tuple;

/**
 * @author hparry
 *
 */
public class StatusCodeSequencingTriggerTest extends AbstractModelTest {

    @Test
    public void test2InitialStates() throws SQLException {
        em.getTransaction().begin();
        Agency core = kernel.getCore();
        Product service = kernel.getNotApplicableProduct();
        StatusCode a = new StatusCode("A", null, core);
        em.persist(a);
        StatusCode b = new StatusCode("B", null, core);
        em.persist(b);
        StatusCode x = new StatusCode("X", null, core);
        em.persist(x);

        List<Tuple<StatusCode, StatusCode>> codes = new ArrayList<>();
        codes.add(new Tuple<StatusCode, StatusCode>(a, x));
        codes.add(new Tuple<StatusCode, StatusCode>(b, x));
        model.getJobModel().createStatusCodeSequencings(service, codes, core);
        try {
            em.getTransaction().commit();
            fail("Insert should not have succeeded");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }
}
