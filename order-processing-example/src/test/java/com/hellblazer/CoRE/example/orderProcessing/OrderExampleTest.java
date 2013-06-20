/**
 * Copyright (c) 2012, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.hellblazer.CoRE.example.orderProcessing;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.Before;
import org.junit.Test;

import com.hellblazer.CoRE.event.Job;
import com.hellblazer.CoRE.meta.BootstrapLoader;

/**
 * @author hhildebrand
 * 
 */
public class OrderExampleTest {

    private EntityManager em;

    public OrderExampleTest() throws IOException {
        Properties properties = new Properties();
        InputStream is = ExampleLoader.class.getResourceAsStream("/jpa.properties");
        properties.load(is);
        em = Persistence.createEntityManagerFactory("CoRE", properties).createEntityManager();
    }

    @Before
    public void load() throws SQLException {
        EntityTransaction txn = em.getTransaction();
        BootstrapLoader bootstrap = new BootstrapLoader(em);
        txn.begin();
        bootstrap.clear();
        txn.commit();
        txn.begin();
        bootstrap.bootstrap();
        txn.commit();
    }

    @Test
    public void testOrder() throws Exception {
        EntityTransaction txn = em.getTransaction();
        ExampleLoader scenario = new ExampleLoader(em);
        txn.begin();
        scenario.load();
        txn.commit();
        txn.begin();
        Job order = new Job(scenario.orderFullfillment,
                            scenario.georgeTownUniversity, scenario.deliver,
                            scenario.abc486, scenario.rsb225,
                            scenario.factory1, scenario.core);
        em.persist(order);
        txn.commit();
        txn.begin();
        order.setStatus(scenario.active);
        txn.commit();
    }
}
