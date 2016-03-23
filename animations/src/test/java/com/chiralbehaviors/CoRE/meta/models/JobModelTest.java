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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Location;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.domain.StatusCode;
import com.chiralbehaviors.CoRE.jooq.tables.ChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.ParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.Protocol;
import com.chiralbehaviors.CoRE.jooq.tables.SelfSequencingAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobChronologyRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.MetaProtocolRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ProtocolRecord;
import com.chiralbehaviors.CoRE.meta.InferenceMap;
import com.chiralbehaviors.CoRE.meta.JobModel;
import com.hellblazer.utils.Tuple;

/**
 * @author hhildebrand
 *
 */
public class JobModelTest extends AbstractModelTest {

    private static JobModel        jobModel;

    private static OrderProcessing scenario;

    @BeforeClass
    public static void before() throws Exception {
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        OrderProcessingLoader loader = new OrderProcessingLoader(model);
        loader.load();
        scenario = loader.createWorkspace(model)
                         .getAccessor(OrderProcessing.class);
        txn.commit();
        jobModel = model.getJobModel();
        // model.setLogConfiguration(Utils.getDocument(JobModelTest.class.getResourceAsStream("/test-log-db.xml")));
    }

    @Override
    @After
    public void after() {
        if (em.getTransaction()
              .isActive()) {
            em.getTransaction()
              .rollback();
        }
    }

    @Test
    public void testDeliverWithoutMetaProtocol() {
        StatusCode startState = model.records()
                                     .newStatusCode("begin", kernel.getCore());
        // startState.setPropagateChildren(true);
        em.persist(startState);

        StatusCode delivered = model.records()
                                    .newStatusCode("delivered",
                                                   kernel.getCore());
        em.persist(delivered);

        StatusCode shipState = model.records()
                                    .newStatusCode("shipping",
                                                   kernel.getCore());
        em.persist(shipState);

        Product kiki = model.records()
                            .newProduct("Kiki's Delivery Service",
                                        kernel.getCore());
        em.persist(kiki);

        Product shipping = model.records()
                                .newProduct("Kiki's Shipping Service",
                                            kernel.getCore());
        em.persist(shipping);

        Product bento = model.records()
                             .newProduct("Tonkatsu Bento Box",
                                         kernel.getCore());
        em.persist(bento);

        StatusCodeSequencing sequence = model.records()
                                             .newStatusCodeSequencing(kiki,
                                                                      startState,
                                                                      delivered,
                                                                      kernel.getCore());
        em.persist(sequence);

        StatusCodeSequencing childSequence = model.records()
                                                  .newStatusCodeSequencing(shipping,
                                                                           shipState,
                                                                           delivered,
                                                                           kernel.getCore());
        em.persist(childSequence);

        ProtocolRecord p = jobModel.newInitializedProtocol(kiki,
                                                           kernel.getCore());
        p.setProduct(bento.getId());
        p.setRequester(kernel.getCore()
                             .getId());
        p.setDeliverTo(kernel.getAnyLocation()
                             .getId());
        p.setDeliverFrom(kernel.getAnyLocation()
                               .getId());
        p.setAssignTo(kernel.getCore()
                            .getId());
        p.setChildService(shipping.getId());
        p.setChildProduct(bento.getId());
        em.persist(p);

        em.flush();

        JobRecord job = model.getJobModel()
                             .newInitializedJob(kiki, kernel.getCore());
        job.setAssignTo(kernel.getCore()
                              .getId());
        job.setProduct(bento.getId());
        job.setDeliverTo(kernel.getAnyLocation()
                               .getId());
        job.setDeliverFrom(kernel.getAnyLocation()
                                 .getId());
        job.setRequester(kernel.getCore()
                               .getId());
        jobModel.changeStatus(job, kernel.getUnset(), kernel.getCore(),
                              "transition during test");
        em.persist(job);

        em.flush();
        jobModel.changeStatus(job, startState, kernel.getCore(),
                              "transition during test");
        em.flush();

        TypedQuery<JobRecord> query = em.createQuery("select j from JobRecord j where j.service = :service",
                                                     JobRecord.class);
        query.setParameter("service", shipping);
        JobRecord j = query.getSingleResult();
        assertNotNull(j);
    }

    @Test
    public void testEuOrder() throws Exception {
        JobRecord order = model.getJobModel()
                               .newInitializedJob(scenario.getDeliver(),
                                                  kernel.getCore());
        order.setAssignTo(scenario.getOrderFullfillment()
                                  .getId());
        order.setProduct(scenario.getABC486()
                                 .getId());
        order.setDeliverTo(scenario.getRC31()
                                   .getId());
        order.setDeliverFrom(scenario.getFactory1()
                                     .getId());
        order.setRequester(scenario.getCarfleurBon()
                                   .getId());
        em.persist(order);
        em.flush();
        jobModel.changeStatus(order, scenario.getAvailable(), kernel.getCore(),
                              "transition during test");
        em.flush();
        jobModel.changeStatus(order, scenario.getActive(), kernel.getCore(),
                              "transition during test");
        em.flush();
        List<MetaProtocolRecord> metaProtocols = jobModel.getMetaprotocols(order);
        assertEquals(1, metaProtocols.size());
        Map<Protocol, InferenceMap> protocols = jobModel.getProtocols(order);
        assertEquals(2, protocols.size());
        List<JobRecord> jobs = jobModel.getAllChildren(order);
        assertEquals(6, jobs.size());
    }

    @Test
    public void testGenerateJobs() throws Exception {
        JobRecord job = model.getJobModel()
                             .newInitializedJob(scenario.getDeliver(),
                                                kernel.getCore());
        job.setAssignTo(scenario.getOrderFullfillment()
                                .getId());
        job.setProduct(scenario.getABC486()
                               .getId());
        job.setDeliverTo(scenario.getRSB225()
                                 .getId());
        job.setDeliverFrom(scenario.getFactory1()
                                   .getId());
        job.setRequester(scenario.getGeorgetownUniversity()
                                 .getId());
        jobModel.changeStatus(job, scenario.getAvailable(), kernel.getCore(),
                              "Test transition");

        List<JobRecord> jobs = jobModel.generateImplicitJobs(job,
                                                             kernel.getCore());
        TestDebuggingUtil.printJobs(jobs);
    }

    @Test
    public void testGenerateJobsFromProtocols() throws Exception {
        Product service = model.records()
                               .newProduct("test service", kernel.getCore());
        em.persist(service);
        MetaProtocolRecord mp = jobModel.newInitializedMetaProtocol(service,
                                                                    kernel.getCore());
        mp.setAssignTo(kernel.getDevelopedBy());
        mp.setDeliverTo(kernel.getGreaterThanOrEqual());
        em.persist(mp);
        Protocol p = jobModel.newInitializedProtocol(service, kernel.getCore());
        p.setAssignTo(kernel.getPropagationSoftware());
        em.persist(p);
        em.flush();
        JobRecord order = jobModel.newInitializedJob(service, kernel.getCore());
        order.setAssignTo(kernel.getCoreUser());
        Location loc = new Location("crap location", null, kernel.getCore());
        em.persist(loc);
        order.setDeliverTo(loc);
        em.persist(order);
        TestDebuggingUtil.printProtocolGaps(jobModel.findProtocolGaps(order));
        TestDebuggingUtil.printMetaProtocolGaps(jobModel.findMetaProtocolGaps(order));
        List<Protocol> protocols = model.getJobModel()
                                        .getProtocolsFor(order.getService());
        assertEquals(1, protocols.size());
        List<JobRecord> jobs = model.getJobModel()
                                    .generateImplicitJobs(order,
                                                          kernel.getCore());
        for (JobRecord j : jobs) {
            assertNotNull(j.getAssignTo());
            assertNotNull(j.getService());
            assertNotNull(j.getProduct());
            assertNotNull(j.getDeliverTo());
            assertNotNull(j.getDeliverFrom());
            assertNotNull(j.getRequester());
            assertNotNull(j.getUpdatedBy());

        }

    }

    @Test
    public void testGetActiveJobs() throws Exception {
        JobRecord order = model.getJobModel()
                               .newInitializedJob(scenario.getDeliver(),
                                                  kernel.getCore());
        order.setAssignTo(scenario.getOrderFullfillment());
        order.setProduct(scenario.getABC486());
        order.setDeliverTo(scenario.getRSB225());
        order.setDeliverFrom(scenario.getFactory1());
        order.setRequester(scenario.getGeorgetownUniversity());
        em.persist(order);
        em.flush();
        jobModel.changeStatus(order, scenario.getAvailable(), kernel.getCore(),
                              "transition during test");
        em.flush();
        jobModel.changeStatus(order, scenario.getActive(), kernel.getCore(),
                              "transition during test");
        em.flush();

        List<StatusCode> states = Arrays.asList(scenario.getActive(),
                                                scenario.getAvailable(),
                                                scenario.getAbandoned());
        List<JobRecord> active = jobModel.getActiveJobsFor(scenario.getOrderFullfillment(),
                                                           states);
        assertEquals(1, active.size());
    }

    @Test
    public void testIteration() throws Exception {
        Product service = model.records()
                               .newProduct("test service", kernel.getCore());
        em.persist(service);
        Product childService = model.records()
                                    .newProduct("child test service",
                                                kernel.getCore());
        em.persist(childService);
        Product parent = model.records()
                              .newProduct("Parent", kernel.getCore());
        em.persist(parent);
        Product child1 = model.records()
                              .newProduct("Child 1", kernel.getCore());
        em.persist(child1);
        Product child2 = model.records()
                              .newProduct("Child 2", kernel.getCore());
        em.persist(child2);
        Product child3 = model.records()
                              .newProduct("Child 3", kernel.getCore());
        em.persist(child3);
        Relationship childRelationship = model.getRelationshipModel()
                                              .create("child of",
                                                      "test relationship",
                                                      "parentOf",
                                                      "test relationship inverse");
        parent.link(childRelationship, child1, kernel.getCore(),
                    kernel.getCore(), em);
        parent.link(childRelationship, child2, kernel.getCore(),
                    kernel.getCore(), em);
        parent.link(childRelationship, child3, kernel.getCore(),
                    kernel.getCore(), em);

        List<Tuple<StatusCode, StatusCode>> sequencings = new ArrayList<>();
        sequencings.add(new Tuple<StatusCode, StatusCode>(scenario.getAvailable(),
                                                          scenario.getCompleted()));
        model.getJobModel()
             .createStatusCodeSequencings(service, sequencings,
                                          kernel.getCore());
        model.getJobModel()
             .createStatusCodeSequencings(childService, sequencings,
                                          kernel.getCore());

        ChildSequencingAuthorization auth = new ChildSequencingAuthorization(service,
                                                                             scenario.getAvailable(),
                                                                             childService,
                                                                             scenario.getAvailable(),
                                                                             kernel.getCore());
        em.persist(auth);
        Protocol p = jobModel.newInitializedProtocol(service, kernel.getCore());
        p.setChildrenRelationship(childRelationship);
        p.setChildService(childService);
        em.persist(p);
        JobRecord order = jobModel.newInitializedJob(service, kernel.getCore());
        order.setProduct(parent);
        order.setStatus(kernel.getUnset());
        em.persist(order);
        List<Protocol> protocols = model.getJobModel()
                                        .getProtocolsFor(order.getService());
        assertEquals(1, protocols.size());
        List<JobRecord> jobs = model.getJobModel()
                                    .insert(order, protocols.get(0));
        assertEquals(3, jobs.size());
        em.flush();
        em.refresh(order);
        jobModel.changeStatus(order, scenario.getAvailable(), kernel.getCore(),
                              null);
        em.flush();

        for (JobRecord j : jobs) {
            em.refresh(j);
            assertEquals(scenario.getAvailable(), j.getStatus());
        }
    }

    @Test
    public void testJobChronologyOnStatusUpdate() throws Exception {
        JobRecord order = model.getJobModel()
                               .newInitializedJob(scenario.getDeliver(),
                                                  kernel.getCore());
        order.setAssignTo(scenario.getOrderFullfillment());
        order.setProduct(scenario.getABC486());
        order.setDeliverTo(scenario.getRSB225());
        order.setDeliverFrom(scenario.getFactory1());
        order.setRequester(scenario.getGeorgetownUniversity());
        jobModel.changeStatus(order, scenario.getAvailable(), kernel.getCore(),
                              "Test transition");
        em.persist(order);
        em.flush();
        em.refresh(order);
        List<JobChronologyRecord> chronologies = model.getJobModel()
                                                      .getChronologyForJob(order);
        assertEquals(String.format("Invalid number of chronologies: %s",
                                   chronologies),
                     2, chronologies.size());
        List<String> fieldErrors = verifyChronologyFields(order,
                                                          chronologies.get(1));

        assertEquals(fieldErrors.toString(), 0, fieldErrors.size());
        model.getJobModel()
             .changeStatus(order, scenario.getActive(), kernel.getCore(), null);
        em.flush();
        chronologies = model.getJobModel()
                            .getChronologyForJob(order);
        assertEquals(3, chronologies.size());
        for (JobChronologyRecord c : chronologies) {
            fieldErrors = verifyChronologyFields(order, c);
            if (fieldErrors == null || fieldErrors.size() == 0) {
                break;
            }
        }
        assertEquals(0, fieldErrors.size());
    }

    @Test
    public void testJobGenerationAndSequencing() throws Exception {
        Product pushit = model.records()
                              .newProduct("Pushit Service", null,
                                          kernel.getCore());
        em.persist(pushit);

        Product shoveit = model.records()
                               .newProduct("Shoveit Service", null,
                                           kernel.getCore());
        em.persist(shoveit);

        Product pullIt = model.records()
                              .newProduct("Pullit Service", null,
                                          kernel.getCore());
        em.persist(pullIt);

        StatusCode pushingMe = model.records()
                                    .newStatusCode("Pushing Me", null,
                                                   kernel.getCore());
        pushingMe.setPropagateChildren(true);
        em.persist(pushingMe);

        StatusCode shovingMe = model.records()
                                    .newStatusCode("Shoving Me", null,
                                                   kernel.getCore());
        em.persist(shovingMe);

        Protocol p = model.getJobModel()
                          .newInitializedProtocol(pushit, kernel.getCore());
        p.setProduct(pushit);
        p.setChildService(shoveit);
        em.persist(p);
        model.getJobModel()
             .createStatusCodeChain(pushit,
                                    new StatusCode[] { pushingMe, shovingMe,
                                                       scenario.getCompleted() },
                                    kernel.getCore());

        SelfSequencingAuthorization auth = new SelfSequencingAuthorization(pushit,
                                                                           pushingMe,
                                                                           shovingMe,
                                                                           kernel.getCore());
        em.persist(auth);
        em.flush();

        JobRecord push = model.getJobModel()
                              .newInitializedJob(pushit, kernel.getCore());

        em.flush();

        List<JobRecord> children = model.getJobModel()
                                        .getAllChildren(push);
        assertEquals(0, children.size());

        model.getJobModel()
             .changeStatus(push, pushingMe, kernel.getCore(), null);
        push.setProduct(pushit);
        em.flush();
        em.refresh(push);
        children = model.getJobModel()
                        .getAllChildren(push);
        assertEquals(1, children.size());
        assertEquals(shovingMe, push.getStatus());
    }

    @Test
    public void testMetaProtocols() throws Exception {
        JobRecord job = model.getJobModel()
                             .newInitializedJob(scenario.getDeliver(),
                                                kernel.getCore());
        job.setAssignTo(scenario.getOrderFullfillment());
        job.setProduct(scenario.getABC486());
        job.setDeliverTo(scenario.getRSB225());
        job.setDeliverFrom(scenario.getFactory1());
        job.setRequester(scenario.getGeorgetownUniversity());
        jobModel.changeStatus(job, scenario.getAvailable(), kernel.getCore(),
                              "Test transition");
        List<MetaProtocolRecord> metaProtocols = jobModel.getMetaprotocols(job);
        assertEquals(1, metaProtocols.size());
        Map<Protocol, InferenceMap> txfm = jobModel.getProtocols(job);
        assertEquals(2, txfm.size());
        List<Protocol> protocols = new ArrayList<>(txfm.keySet());
        assertEquals(scenario.getDeliver(), protocols.get(0)
                                                     .getService());
        assertEquals(scenario.getDeliver(), protocols.get(1)
                                                     .getService());
        assertEquals(kernel.getAnyAgency(), protocols.get(0)
                                                     .getRequester());
        assertEquals(kernel.getAnyAgency(), protocols.get(1)
                                                     .getRequester());
        assertEquals(kernel.getAnyProduct(), protocols.get(0)
                                                      .getProduct());
        assertEquals(kernel.getAnyProduct(), protocols.get(1)
                                                      .getProduct());
        assertEquals(kernel.getAnyLocation(), protocols.get(0)
                                                       .getDeliverFrom());
        assertEquals(kernel.getAnyLocation(), protocols.get(1)
                                                       .getDeliverFrom());
        assertEquals(kernel.getAnyLocation(), protocols.get(0)
                                                       .getDeliverTo());
        assertEquals(kernel.getAnyLocation(), protocols.get(1)
                                                       .getDeliverTo());
        assertEquals(scenario.getFactory1Agency(), protocols.get(0)
                                                            .getChildAssignTo());
        assertEquals(scenario.getFactory1Agency(), protocols.get(1)
                                                            .getChildAssignTo());
        if (protocols.get(0)
                     .getChildService()
                     .equals(scenario.getPick())) {
            assertEquals(scenario.getShip(), protocols.get(1)
                                                      .getChildService());
        } else {
            assertEquals(scenario.getShip(), protocols.get(0)
                                                      .getChildService());
            assertEquals(scenario.getPick(), protocols.get(1)
                                                      .getChildService());
        }

        job = model.getJobModel()
                   .newInitializedJob(scenario.getPrintPurchaseOrder(),
                                      kernel.getCore());
        job.setAssignTo(scenario.getOrderFullfillment());
        job.setProduct(scenario.getABC486());
        job.setDeliverTo(scenario.getRSB225());
        job.setDeliverFrom(scenario.getFactory1());
        job.setRequester(scenario.getCarfleurBon());
        jobModel.changeStatus(job, kernel.getUnset(), kernel.getCore(),
                              "Transition from test");
        em.flush();
        metaProtocols = jobModel.getMetaprotocols(job);
        assertEquals(1, metaProtocols.size());
        txfm = jobModel.getProtocols(job);
        assertEquals(1, txfm.size());
    }

    @Test
    public void testNonExemptOrder() throws Exception {
        JobRecord order = model.getJobModel()
                               .newInitializedJob(scenario.getDeliver(),
                                                  kernel.getCore());
        order.setAssignTo(scenario.getOrderFullfillment());
        order.setProduct(scenario.getABC486());
        order.setDeliverTo(scenario.getBHT37());
        order.setDeliverFrom(scenario.getFactory1());
        order.setRequester(scenario.getOrgA());
        em.persist(order);
        em.flush();
        jobModel.changeStatus(order, scenario.getAvailable(), kernel.getCore(),
                              "transition during test");
        em.flush();
        jobModel.changeStatus(order, scenario.getActive(), kernel.getCore(),
                              "transition during test");
        em.flush();
        List<MetaProtocolRecord> metaProtocols = jobModel.getMetaprotocols(order);
        assertEquals(1, metaProtocols.size());
        Map<Protocol, InferenceMap> protocols = jobModel.getProtocols(order);
        assertEquals(2, protocols.size());
        List<JobRecord> jobs = jobModel.getAllChildren(order);
        assertEquals(6, jobs.size());
    }

    @Test
    public void testOrder() throws Exception {
        JobRecord order = model.getJobModel()
                               .newInitializedJob(scenario.getDeliver(),
                                                  kernel.getCore());
        order.setAssignTo(scenario.getOrderFullfillment());
        order.setProduct(scenario.getABC486());
        order.setDeliverTo(scenario.getRSB225());
        order.setDeliverFrom(scenario.getFactory1());
        order.setRequester(scenario.getGeorgetownUniversity());
        em.persist(order);
        em.flush();
        jobModel.changeStatus(order, scenario.getAvailable(), kernel.getCore(),
                              "transition during test");
        em.flush();
        jobModel.changeStatus(order, scenario.getActive(), kernel.getCore(),
                              "transition during test");
        em.flush();
        List<MetaProtocolRecord> metaProtocols = jobModel.getMetaprotocols(order);
        assertEquals(1, metaProtocols.size());
        Map<Protocol, InferenceMap> protocols = jobModel.getProtocols(order);
        assertEquals(2, protocols.size());
        List<JobRecord> jobs = jobModel.getAllChildren(order);
        assertEquals(5, jobs.size());

        TypedQuery<JobRecord> query = em.createQuery("select j from JobRecord j where j.service = :service",
                                                     JobRecord.class);
        query.setParameter("service", scenario.getCheckCredit());
        JobRecord creditCheck = query.getSingleResult();
        assertEquals(scenario.getAvailable(), creditCheck.getStatus());
        jobModel.changeStatus(creditCheck, scenario.getActive(),
                              kernel.getCore(), "transition during test");
        em.flush();
        jobModel.changeStatus(creditCheck, scenario.getCompleted(),
                              kernel.getCore(), "transition during test");
        em.flush();
        query.setParameter("service", scenario.getPick());
        JobRecord pick = query.getSingleResult();
        assertEquals(scenario.getAvailable(), pick.getStatus());
        jobModel.changeStatus(pick, scenario.getActive(), kernel.getCore(),
                              "transition during test");
        em.flush();
        jobModel.changeStatus(pick, scenario.getCompleted(), kernel.getCore(),
                              "transition during test");
        em.flush();
        query.setParameter("service", scenario.getPick());
        pick = query.getSingleResult();
        query.setParameter("service", scenario.getShip());
        JobRecord ship = query.getSingleResult();
        List<JobRecord> pickSiblings = jobModel.getActiveSubJobsForService(pick.getParent(),
                                                                           scenario.getShip());
        assertEquals(1, pickSiblings.size());
        assertEquals(scenario.getWaitingOnPurchaseOrder(), ship.getStatus());
        query.setParameter("service", scenario.getFee());
        JobRecord fee = query.getSingleResult();
        jobModel.changeStatus(fee, scenario.getActive(), kernel.getCore(),
                              "transition during test");
        em.flush();
        jobModel.changeStatus(fee, scenario.getCompleted(), kernel.getCore(),
                              "transition during test");
        em.flush();
        query.setParameter("service", scenario.getPrintPurchaseOrder());
        JobRecord printPO = query.getSingleResult();
        assertEquals(scenario.getAvailable(), printPO.getStatus());
        jobModel.changeStatus(printPO, scenario.getActive(), kernel.getCore(),
                              "transition during test");
        em.flush();
        jobModel.changeStatus(printPO, scenario.getCompleted(),
                              kernel.getCore(), "transition during test");
        em.flush();
        query.setParameter("service", scenario.getShip());
        ship = query.getSingleResult();
        assertEquals(scenario.getAvailable(), ship.getStatus());
        jobModel.changeStatus(ship, scenario.getActive(), kernel.getCore(),
                              "transition during test");
        em.flush();
        jobModel.changeStatus(ship, scenario.getCompleted(), kernel.getCore(),
                              "transition during test");
        em.flush();
        query.setParameter("service", scenario.getDeliver());
        JobRecord deliver = query.getSingleResult();
        assertEquals(scenario.getCompleted(), deliver.getStatus());
    }

    public void testSelfSequencing() {
        Product service = model.records()
                               .newProduct("My Service", null,
                                           kernel.getCore());
        em.persist(service);
        StatusCode a = model.records()
                            .newStatusCode("A", null, kernel.getCore());
        em.persist(a);
        StatusCode b = model.records()
                            .newStatusCode("B", null, kernel.getCore());
        em.persist(b);
        StatusCode c = model.records()
                            .newStatusCode("C", null, kernel.getCore());
        em.persist(c);

        List<Tuple<StatusCode, StatusCode>> sequences = new ArrayList<>();
        sequences.add(new Tuple<StatusCode, StatusCode>(a, b));
        sequences.add(new Tuple<StatusCode, StatusCode>(b, c));
        model.getJobModel()
             .createStatusCodeSequencings(service, sequences, kernel.getCore());

        SelfSequencingAuthorization auth = new SelfSequencingAuthorization();
        auth.setService(service);
        auth.setStatusCode(b);
        auth.setStatusToSet(c);
        auth.setUpdatedBy(kernel.getCore());
        em.persist(auth);

        em.flush();
        JobRecord job = model.getJobModel()
                             .newInitializedJob(service, kernel.getCore());
        em.persist(job);
        model.getJobModel()
             .changeStatus(job, a, kernel.getCore(), null);
        em.flush();
        model.getJobModel()
             .changeStatus(job, b, kernel.getCore(), null);
        em.flush();

        em.refresh(job);

        assertEquals(c, job.getStatus());

    }

    @Test
    public void testSelfSequencingAuthorization() {
        Product service = model.records()
                               .newProduct("Kick ass", null, kernel.getCore());
        em.persist(service);

        StatusCode kickingAss = model.records()
                                     .newStatusCode("Kicking Ass", null,
                                                    kernel.getCore());
        em.persist(kickingAss);

        StatusCode takingNames = model.records()
                                      .newStatusCode("Taking Names", null,
                                                     kernel.getCore());
        em.persist(takingNames);

        StatusCodeSequencing sequence = model.records()
                                             .newStatusCodeSequencing(service,
                                                                      kickingAss,
                                                                      takingNames,
                                                                      kernel.getCore());
        em.persist(sequence);

        SelfSequencingAuthorization auth = new SelfSequencingAuthorization(service,
                                                                           kickingAss,
                                                                           takingNames,
                                                                           kernel.getCore());
        em.persist(auth);
        em.flush();

        JobRecord job = model.getJobModel()
                             .newInitializedJob(service, kernel.getCore());
        em.persist(job);

        em.flush();
        model.getJobModel()
             .changeStatus(job, kickingAss, kernel.getCore(), "taking names");
        em.flush();

        em.refresh(job);

        assertEquals(takingNames, job.getStatus());
    }

    @Test
    public void testTerminateChildrenParent() throws IOException {
        Product pushit = model.records()
                              .newProduct("Pushit Service", null,
                                          kernel.getCore());
        em.persist(pushit);

        Product shoveit = model.records()
                               .newProduct("shoveit Service", null,
                                           kernel.getCore());
        em.persist(shoveit);

        Product pullit = model.records()
                              .newProduct("Pullit Service", null,
                                          kernel.getCore());
        em.persist(pullit);

        StatusCode pushingMe = model.records()
                                    .newStatusCode("Pushing Me", null,
                                                   kernel.getCore());
        pushingMe.setPropagateChildren(true);
        em.persist(pushingMe);

        StatusCode shovingMe = model.records()
                                    .newStatusCode("Shoving Me", null,
                                                   kernel.getCore());
        em.persist(shovingMe);

        Protocol p = model.getJobModel()
                          .newInitializedProtocol(pushit, kernel.getCore());
        p.setChildService(shoveit);
        em.persist(p);

        Protocol p2 = model.getJobModel()
                           .newInitializedProtocol(shoveit, kernel.getCore());
        p2.setChildService(pullit);
        em.persist(p2);

        model.getJobModel()
             .createStatusCodeChain(pushit,
                                    new StatusCode[] { pushingMe, shovingMe },
                                    kernel.getCore());
        model.getJobModel()
             .createStatusCodeChain(shoveit,
                                    new StatusCode[] { pushingMe, shovingMe },
                                    kernel.getCore());
        model.getJobModel()
             .createStatusCodeChain(pullit,
                                    new newStatusCode[] { pushingMe,
                                                          shovingMe },
                                    kernel.getCore());

        ChildSequencingAuthorization auth = new ChildSequencingAuthorization(shoveit,
                                                                             shovingMe,
                                                                             pullit,
                                                                             shovingMe,
                                                                             kernel.getCore());
        em.persist(auth);
        ParentSequencingAuthorization auth2 = new ParentSequencingAuthorization(shoveit,
                                                                                shovingMe,
                                                                                pushit,
                                                                                shovingMe,
                                                                                kernel.getCore());
        em.persist(auth2);
        em.flush();

        JobRecord push = model.getJobModel()
                              .newInitializedJob(pushit, kernel.getCore());

        em.flush();
        em.refresh(push);
        List<JobRecord> children = model.getJobModel()
                                        .getAllChildren(push);
        assertEquals(2, children.size());

        for (JobRecord j : children) {
            model.getJobModel()
                 .changeStatus(j, pushingMe, kernel.getCore(), null);
        }
        model.getJobModel()
             .changeStatus(push, pushingMe, kernel.getCore(), null);
        em.flush();
        List<JobRecord> active = model.getJobModel()
                                      .getActiveSubJobsForService(push,
                                                                  shoveit);
        assertEquals(1, active.size());
        JobRecord shovingJob = active.get(0);

        model.getJobModel()
             .changeStatus(shovingJob, shovingMe, null, null);
        em.flush();
        em.refresh(push);
        assertEquals(shovingMe, push.getStatus());
    }

    /**
     * Returns a list of fields that do not match between job and chronology
     *
     * @param job
     * @param jobChronology
     * @return
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    private List<String> verifyChronologyFields(JobRecord job,
                                                JobChronologyRecord jobChronology) throws Exception {
        String[] fieldsToMatch = new String[] { "status", "requester",
                                                "assignTo", "deliverFrom",
                                                "deliverTo" };
        List<String> unmatchedFields = new LinkedList<>();
        if (!jobChronology.getJob()
                          .equals(job)) {
            unmatchedFields.add("job");
            return unmatchedFields;
        }
        for (String field : fieldsToMatch) {
            ExistentialRuleform<?, ?> jobRf = (ExistentialRuleform<?, ?>) PropertyUtils.getSimpleProperty(job,
                                                                                                          field);
            ExistentialRuleform<?, ?> chronoRf = (ExistentialRuleform<?, ?>) PropertyUtils.getSimpleProperty(jobChronology,
                                                                                                             field);
            if (chronoRf == null && jobRf == null) {
                continue;
            }
            if (!chronoRf.equals(jobRf)) {
                unmatchedFields.add(field);
            }
        }

        return unmatchedFields;
    }
}
