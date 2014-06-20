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

package com.chiralbehaviors.CoRE.meta.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.chiralbehaviors.CoRE.event.Job;
import com.chiralbehaviors.CoRE.event.MetaProtocol;
import com.chiralbehaviors.CoRE.event.Protocol;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.meta.JobModel;
import com.chiralbehaviors.CoRE.meta.models.debug.JobModelDebugger;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 * 
 */
public class JobModelTest extends AbstractModelTest {

    private static JobModelDebugger      jobModel;
    private static OrderProcessingLoader scenario;

    @BeforeClass
    public static void before() throws Exception {
        EntityTransaction txn = em.getTransaction();
        scenario = new OrderProcessingLoader(em);
        txn.begin();
        scenario.load();
        txn.commit();
        jobModel = new JobModelDebugger(model);
    }

    @After
    public void after() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
            em.clear();
        }
    }

    @Test
    public void testDeliverWithoutMetaProtocol() {
        em.getTransaction().begin();

        StatusCode startState = new StatusCode("begin", kernel.getCore());
        startState.setPropagateChildren(true);
        em.persist(startState);

        StatusCode delivered = new StatusCode("delivered", kernel.getCore());
        em.persist(delivered);

        StatusCode shipState = new StatusCode("shipping", kernel.getCore());
        em.persist(shipState);

        Product kiki = new Product("Kiki's Delivery Service", kernel.getCore());
        em.persist(kiki);

        Product shipping = new Product("Kiki's Shipping Service",
                                       kernel.getCore());
        em.persist(shipping);

        Product bento = new Product("Tonkatsu Bento Box", kernel.getCore());
        em.persist(bento);

        StatusCodeSequencing sequence = new StatusCodeSequencing(
                                                                 kiki,
                                                                 startState,
                                                                 delivered,
                                                                 kernel.getCore());
        em.persist(sequence);

        StatusCodeSequencing childSequence = new StatusCodeSequencing(
                                                                      shipping,
                                                                      shipState,
                                                                      delivered,
                                                                      kernel.getCore());
        em.persist(childSequence);

        Protocol p = jobModel.newInitializedProtocol(kiki, kernel.getCore());
        p.setRequestedProduct(bento);
        p.setRequester(scenario.core);
        p.setDeliverTo(kernel.getAnyLocation());
        p.setDeliverFrom(kernel.getAnyLocation());
        p.setAssignTo(scenario.core);
        p.setService(shipping);
        p.setProduct(bento);
        em.persist(p);

        em.getTransaction().commit();

        em.getTransaction().begin();

        Job job = model.getJobModel().newInitializedJob(kiki, scenario.core);
        job.setAssignTo(scenario.core);
        job.setProduct(bento);
        job.setDeliverTo(scenario.anyLocation);
        job.setDeliverFrom(scenario.anyLocation);
        job.setRequester(scenario.core);
        job.setStatus(scenario.unset);
        em.persist(job);

        em.getTransaction().commit();

        em.getTransaction().begin();
        job.setStatus(startState);
        em.getTransaction().commit();

        TypedQuery<Job> query = em.createQuery("select j from Job j where j.service = :service",
                                               Job.class);
        query.setParameter("service", shipping);
        Job j = query.getSingleResult();
        assertNotNull(j);
    }

    @Test
    public void testEuOrder() throws Exception {
        clearJobs();
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        Job order = model.getJobModel().newInitializedJob(scenario.deliver,
                                                          scenario.core);
        order.setAssignTo(scenario.orderFullfillment);
        order.setProduct(scenario.abc486);
        order.setDeliverTo(scenario.rc31);
        order.setDeliverFrom(scenario.factory1);
        order.setRequester(scenario.cafleurBon);
        em.persist(order);
        txn.commit();
        txn.begin();
        order.setStatus(scenario.available);
        txn.commit();
        txn.begin();
        order.setStatus(scenario.active);
        txn.commit();
        List<MetaProtocol> metaProtocols = jobModel.getMetaprotocols(order);
        assertEquals(1, metaProtocols.size());
        List<Protocol> protocols = jobModel.getProtocols(order);
        assertEquals(2, protocols.size());
        List<Job> jobs = findAllJobs();
        assertEquals(7, jobs.size());
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
        em.persist(back);
        em.persist(terminate);
        em.getTransaction().commit();
    }

    @Test
    public void testNetworkInference() {
        List<LocationNetwork> edges = em.createQuery("SELECT edge FROM LocationNetwork edge WHERE edge.inference.id <> 'AAAAAAAAAAAAAAAAAAAAAA'",
                                                     LocationNetwork.class).getResultList();
        assertEquals(22, edges.size());

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
    public void testNonExemptOrder() throws Exception {
        clearJobs();
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        Job order = model.getJobModel().newInitializedJob(scenario.deliver,
                                                          scenario.core);
        order.setAssignTo(scenario.orderFullfillment);
        order.setProduct(scenario.abc486);
        order.setDeliverTo(scenario.bht378);
        order.setDeliverFrom(scenario.factory1);
        order.setRequester(scenario.orgA);
        em.persist(order);
        txn.commit();
        txn.begin();
        order.setStatus(scenario.available);
        txn.commit();
        txn.begin();
        order.setStatus(scenario.active);
        txn.commit();
        List<MetaProtocol> metaProtocols = jobModel.getMetaprotocols(order);
        assertEquals(1, metaProtocols.size());
        List<Protocol> protocols = jobModel.getProtocols(order);
        assertEquals(2, protocols.size());
        List<Job> jobs = findAllJobs();
        assertEquals(7, jobs.size());
    }

    @Test
    public void testGenerateJobsFromProtocols() {
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        Product service = new Product("test service", kernel.getCore());
        em.persist(service);
        Protocol p = jobModel.newInitializedProtocol(service, kernel.getCore());
        em.persist(p);
        txn.commit();
        Job order = jobModel.newInitializedJob(service, kernel.getCore());
        em.persist(order);
        TestDebuggingUtil.printProtocolGaps(jobModel.findProtocolGaps(order));
        List<Protocol> protocols = model.getJobModel().getProtocols(order);
        assertEquals(1, protocols.size());
        List<Job> jobs = model.getJobModel().generateImplicitJobs(order);
        for (Job j : jobs) {
            assertNotNull(j.getAssignToAttribute());
            assertNotNull(j.getAssignTo());
            assertNotNull(j.getService());
            assertNotNull(j.getServiceAttribute());
            assertNotNull(j.getProduct());
            assertNotNull(j.getProductAttribute());
            assertNotNull(j.getDeliverTo());
            assertNotNull(j.getDeliverToAttribute());
            assertNotNull(j.getDeliverFrom());
            assertNotNull(j.getDeliverFromAttribute());
            assertNotNull(j.getRequester());
            assertNotNull(j.getRequesterAttribute());
            assertNotNull(j.getUpdatedBy());

        }

    }

    @Test
    public void testOrder() throws Exception {
        clearJobs();
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        Job order = model.getJobModel().newInitializedJob(scenario.deliver,
                                                          scenario.core);
        order.setAssignTo(scenario.orderFullfillment);
        order.setProduct(scenario.abc486);
        order.setDeliverTo(scenario.rsb225);
        order.setDeliverFrom(scenario.factory1);
        order.setRequester(scenario.georgeTownUniversity);
        order.setStatus(kernel.getUnset());
        //List<Job> jobs2 = model.getJobModel().generateImplicitJobs(order);
        em.persist(order);
        txn.commit();
        txn.begin();
        order.setStatus(scenario.available);
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

        txn.begin();
        TypedQuery<Job> query = em.createQuery("select j from Job j where j.service = :service",
                                               Job.class);
        query.setParameter("service", scenario.checkCredit);
        Job creditCheck = query.getSingleResult();
        creditCheck.setStatus(scenario.active);
        txn.commit();
        txn.begin();
        creditCheck.setStatus(scenario.completed);
        txn.commit();
        em.clear();
        txn.begin();
        query.setParameter("service", scenario.pick);
        Job pick = query.getSingleResult();
        assertEquals(scenario.available, pick.getStatus());
        pick.setStatus(scenario.active);
        txn.commit();
        txn.begin();
        pick.setStatus(scenario.completed);
        txn.commit();
        txn.begin();
        em.clear();
        query.setParameter("service", scenario.ship);
        Job ship = query.getSingleResult();
        assertEquals(scenario.waitingOnPurchaseOrder, ship.getStatus());
        query.setParameter("service", scenario.fee);
        Job fee = query.getSingleResult();
        fee.setStatus(scenario.active);
        txn.commit();
        txn.begin();
        fee.setStatus(scenario.completed);
        txn.commit();
        txn.begin();
        em.clear();
        query.setParameter("service", scenario.printPurchaseOrder);
        Job printPO = query.getSingleResult();
        assertEquals(scenario.available, printPO.getStatus());
        printPO.setStatus(scenario.active);
        txn.commit();
        txn.begin();
        printPO.setStatus(scenario.completed);
        txn.commit();
        txn.begin();
        em.clear();
        query.setParameter("service", scenario.ship);
        ship = query.getSingleResult();
        assertEquals(scenario.available, ship.getStatus());
        ship.setStatus(scenario.active);
        txn.commit();
        txn.begin();
        ship.setStatus(scenario.completed);
        txn.commit();
        txn.begin();
        em.clear();
        query.setParameter("service", scenario.deliver);
        Job deliver = query.getSingleResult();
        assertEquals(scenario.completed, deliver.getStatus());
    }

    @Test
    public void testMetaProtocols() throws Exception {
        Job job = model.getJobModel().newInitializedJob(scenario.deliver,
                                                        scenario.core);
        job.setAssignTo(scenario.orderFullfillment);
        job.setProduct(scenario.abc486);
        job.setDeliverTo(scenario.rsb225);
        job.setDeliverFrom(scenario.factory1);
        job.setRequester(scenario.georgeTownUniversity);
        job.setStatus(scenario.available);
        List<MetaProtocol> metaProtocols = jobModel.getMetaprotocols(job);
        assertEquals(1, metaProtocols.size());
        List<Protocol> protocols = jobModel.getProtocols(job);
        assertEquals(2, protocols.size());
        assertEquals(scenario.deliver, protocols.get(0).getRequestedService());
        assertEquals(scenario.deliver, protocols.get(1).getRequestedService());
        assertEquals(scenario.anyAgency, protocols.get(0).getRequester());
        assertEquals(scenario.anyAgency, protocols.get(1).getRequester());
        assertEquals(scenario.anyProduct,
                     protocols.get(0).getRequestedProduct());
        assertEquals(scenario.anyProduct,
                     protocols.get(1).getRequestedProduct());
        assertEquals(scenario.anyLocation, protocols.get(0).getDeliverFrom());
        assertEquals(scenario.anyLocation, protocols.get(1).getDeliverFrom());
        assertEquals(scenario.anyLocation, protocols.get(0).getDeliverTo());
        assertEquals(scenario.anyLocation, protocols.get(1).getDeliverTo());
        assertEquals(scenario.factory1Agency, protocols.get(0).getAssignTo());
        assertEquals(scenario.factory1Agency, protocols.get(1).getAssignTo());
        if (protocols.get(0).getService().equals(scenario.pick)) {
            assertEquals(scenario.ship, protocols.get(1).getService());
        } else {
            assertEquals(scenario.ship, protocols.get(0).getService());
            assertEquals(scenario.pick, protocols.get(1).getService());
        }

        job = model.getJobModel().newInitializedJob(scenario.printPurchaseOrder,
                                                    scenario.core);
        job.setAssignTo(scenario.orderFullfillment);
        job.setProduct(scenario.abc486);
        job.setDeliverTo(scenario.rsb225);
        job.setDeliverFrom(scenario.factory1);
        job.setRequester(scenario.georgeTownUniversity);
        metaProtocols = jobModel.getMetaprotocols(job);
        assertEquals(1, metaProtocols.size());
        protocols = jobModel.getProtocols(job);
        assertEquals(1, protocols.size());
    }

    private void clearJobs() throws SQLException {
        Connection connection = em.unwrap(Connection.class);
        boolean prev = connection.getAutoCommit();
        connection.setAutoCommit(false);
        alterTriggers(false);
        String query = "DELETE FROM ruleform.job";
        connection.createStatement().execute(query);
        alterTriggers(true);
        connection.commit();
        connection.setAutoCommit(prev);
    }

    private List<Job> findAllJobs() {
        TypedQuery<Job> query = model.getEntityManager().createQuery("select j from Job j",
                                                                     Job.class);
        return query.getResultList();
    }
}
