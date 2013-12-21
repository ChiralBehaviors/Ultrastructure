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

package com.hellblazer.CoRE.meta.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;

import org.junit.Before;
import org.junit.Test;

import com.hellblazer.CoRE.event.Job;
import com.hellblazer.CoRE.event.MetaProtocol;
import com.hellblazer.CoRE.event.Protocol;
import com.hellblazer.CoRE.event.status.StatusCode;
import com.hellblazer.CoRE.event.status.StatusCodeSequencing;
import com.hellblazer.CoRE.location.LocationNetwork;
import com.hellblazer.CoRE.meta.JobModel;
import com.hellblazer.CoRE.product.Product;

/**
 * @author hhildebrand
 * 
 */
public class JobModelTest extends AbstractModelTest {

    private JobModel              jobModel;
    private OrderProcessingLoader scenario;

    @Override
    @Before
    public void initialize() throws Exception {
        super.initialize();
        jobModel = model.getJobModel();
        EntityTransaction txn = em.getTransaction();
        scenario = new OrderProcessingLoader(em);
        txn.begin();
        scenario.load();
        txn.commit();
    }

    @Test
    public void testIsTerminalState() throws Exception {
        em.getTransaction().begin();

        JobModel jobModel = model.getJobModel();
        StatusCode startState = new StatusCode("top-level", kernel.getCore());
        em.persist(startState);

        StatusCode state1 = new StatusCode("state-1", kernel.getCore());
        em.persist(state1);

        StatusCode state2 = new StatusCode("state-2", kernel.getCore());
        em.persist(state2);

        StatusCode terminalState = new StatusCode("terminal state",
                                                  kernel.getCore());
        em.persist(terminalState);

        Product service = new Product("My Service", kernel.getCore());
        em.persist(service);

        StatusCodeSequencing sequence1 = new StatusCodeSequencing(
                                                                  service,
                                                                  startState,
                                                                  state1,
                                                                  kernel.getCore());
        em.persist(sequence1);

        StatusCodeSequencing sequence2 = new StatusCodeSequencing(
                                                                  service,
                                                                  state1,
                                                                  state2,
                                                                  kernel.getCore());
        em.persist(sequence2);

        StatusCodeSequencing sequence3 = new StatusCodeSequencing(
                                                                  service,
                                                                  state2,
                                                                  terminalState,
                                                                  kernel.getCore());
        em.persist(sequence3);

        em.getTransaction().commit();

        assertTrue(String.format("%s is not a terminal state", terminalState),
                   jobModel.isTerminalState(terminalState, service));
        assertFalse(String.format("%s is a terminal state", startState),
                    jobModel.isTerminalState(startState, service));
        assertFalse(String.format("%s is a terminal state", state1),
                    jobModel.isTerminalState(state1, service));
        assertFalse(String.format("%s is a terminal state", state2),
                    jobModel.isTerminalState(state2, service));

        em.getTransaction().begin();

        StatusCodeSequencing loop = new StatusCodeSequencing(service,
                                                             terminalState,
                                                             state1,
                                                             kernel.getCore());
        em.persist(loop);
        try {
            em.getTransaction().commit();
            fail("Expected failure due to circularity");
        } catch (RollbackException e) {
            // expected
        }

        assertTrue(String.format("%s is not a terminal state", terminalState),
                   jobModel.isTerminalState(terminalState, service));

        em.getTransaction().begin();

        StatusCode loopState = new StatusCode("loop-state", kernel.getCore());
        em.persist(loopState);

        loop = new StatusCodeSequencing(service, state2, loopState,
                                        kernel.getCore());
        loop.setSequenceNumber(2);
        em.persist(loop);

        StatusCodeSequencing terminate = new StatusCodeSequencing(
                                                                  service,
                                                                  loopState,
                                                                  terminalState,
                                                                  kernel.getCore());
        em.persist(terminate);

        StatusCodeSequencing back = new StatusCodeSequencing(service,
                                                             loopState, state1,
                                                             kernel.getCore());
        back.setSequenceNumber(2);
        em.persist(back);
        em.persist(terminate);
        em.getTransaction().commit();
    }

    @Test
    public void testNetworkInference() {
        List<LocationNetwork> edges = em.createQuery("SELECT edge FROM LocationNetwork edge WHERE edge.inferred = TRUE",
                                                     LocationNetwork.class).getResultList();
        assertEquals(18, edges.size());

        TypedQuery<LocationNetwork> edgeQuery = em.createQuery("select edge FROM LocationNetwork edge WHERE edge.parent = :parent AND edge.relationship = :relationship AND edge.child = :child",
                                                               LocationNetwork.class);
        edgeQuery.setParameter("parent", scenario.factory1);
        edgeQuery.setParameter("relationship", scenario.city);
        edgeQuery.setParameter("child", scenario.dc);
        LocationNetwork edge = edgeQuery.getSingleResult();
        assertEquals(true, edge.isInferred());
        assertEquals(kernel.getInverseSoftware(), edge.getUpdatedBy());

        edgeQuery.setParameter("parent", scenario.dc);
        edgeQuery.setParameter("relationship", scenario.cityOf);
        edgeQuery.setParameter("child", scenario.factory1);
        edge = edgeQuery.getSingleResult();
        assertEquals(true, edge.isInferred());
        assertEquals(kernel.getPropagationSoftware(), edge.getUpdatedBy());

        edgeQuery.setParameter("parent", scenario.us);
        edgeQuery.setParameter("relationship", scenario.areaOf);
        edgeQuery.setParameter("child", scenario.dc);
        edge = edgeQuery.getSingleResult();
        assertEquals(true, edge.isInferred());
        assertEquals(kernel.getPropagationSoftware(), edge.getUpdatedBy());

        edgeQuery.setParameter("parent", scenario.dc);
        edgeQuery.setParameter("relationship", scenario.area);
        edgeQuery.setParameter("child", scenario.us);
        edge = edgeQuery.getSingleResult();
        assertEquals(true, edge.isInferred());
        assertEquals(kernel.getInverseSoftware(), edge.getUpdatedBy());

        edgeQuery.setParameter("parent", scenario.paris);
        edgeQuery.setParameter("relationship", scenario.area);
        edgeQuery.setParameter("child", scenario.euro);
        edge = edgeQuery.getSingleResult();
        assertEquals(true, edge.isInferred());
        assertEquals(kernel.getInverseSoftware(), edge.getUpdatedBy());

        edgeQuery.setParameter("parent", scenario.euro);
        edgeQuery.setParameter("relationship", scenario.areaOf);
        edgeQuery.setParameter("child", scenario.paris);
        edge = edgeQuery.getSingleResult();
        assertEquals(true, edge.isInferred());
        assertEquals(kernel.getPropagationSoftware(), edge.getUpdatedBy());
    }

    @Test
    public void testOrder() throws Exception {
        EntityTransaction txn = em.getTransaction();
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
        assertEquals(1, metaProtocols.size());
        List<Protocol> protocols = jobModel.getProtocols(order);
        assertEquals(2, protocols.size());
        List<Job> jobs = findAllJobs();
        assertEquals(6, jobs.size());
    }

    private List<Job> findAllJobs() {
        TypedQuery<Job> query = model.getEntityManager().createQuery("select j from Job j",
                                                                     Job.class);
        return query.getResultList();
    }
}
