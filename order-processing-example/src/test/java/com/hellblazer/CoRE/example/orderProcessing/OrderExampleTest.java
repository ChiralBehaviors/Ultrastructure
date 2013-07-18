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

package com.hellblazer.CoRE.example.orderProcessing;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.Before;
import org.junit.Test;

import com.hellblazer.CoRE.event.Job;
import com.hellblazer.CoRE.event.MetaProtocol;
import com.hellblazer.CoRE.event.Protocol;
import com.hellblazer.CoRE.meta.BootstrapLoader;
import com.hellblazer.CoRE.meta.JobModel;
import com.hellblazer.CoRE.meta.Model;
import com.hellblazer.CoRE.meta.models.ModelImpl;

/**
 * @author hhildebrand
 * 
 */
public class OrderExampleTest {

    private EntityManager em;
    private Model         model;
    private JobModel      jobModel;

    public OrderExampleTest() throws IOException {
        Properties properties = new Properties();
        InputStream is = ExampleLoader.class.getResourceAsStream("/jpa.properties");
        properties.load(is);
        em = Persistence.createEntityManagerFactory("CoRE", properties).createEntityManager();
        model = new ModelImpl(em);
        jobModel = model.getJobModel();
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
        List<MetaProtocol> metaProtocols = jobModel.getMetaprotocols(order);
        assertEquals(6, metaProtocols.size());
        List<Protocol> protocols = jobModel.getProtocols(order);
        assertEquals(2, protocols.size());
        List<Job> jobs = em.createQuery("select j from Job j", Job.class).getResultList();
        assertEquals(3, jobs.size());
    }
}
