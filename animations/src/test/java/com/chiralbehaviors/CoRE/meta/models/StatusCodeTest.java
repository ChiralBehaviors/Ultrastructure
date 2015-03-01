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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.event.Job;
import com.chiralbehaviors.CoRE.event.JobChronology;
import com.chiralbehaviors.CoRE.event.ProductChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.meta.JobModel;
import com.chiralbehaviors.CoRE.product.Product;
import com.hellblazer.utils.Tuple;

/**
 * @author hhildebrand
 *
 */
public class StatusCodeTest extends AbstractModelTest {

    @Before
    public void before() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.getTransaction().begin();
    }

    @Test
    public void testIsTerminalState() throws Exception {
        // model.setLogConfiguration(Utils.getDocument(getClass().getResourceAsStream("/test-log-config.xml")));
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

        List<Tuple<StatusCode, StatusCode>> sequences = new ArrayList<Tuple<StatusCode, StatusCode>>();
        sequences.add(new Tuple<StatusCode, StatusCode>(startState, state1));
        sequences.add(new Tuple<StatusCode, StatusCode>(state1, state2));
        sequences.add(new Tuple<StatusCode, StatusCode>(state2, terminalState));

        model.getJobModel().createStatusCodeSequencings(service, sequences,
                                                        kernel.getCore());

        em.getTransaction().commit();

        assertTrue(String.format("%s is not a terminal state", terminalState),
                   jobModel.isTerminalState(terminalState, service));
        assertFalse(String.format("%s is a terminal state", startState),
                    jobModel.isTerminalState(startState, service));
        assertFalse(String.format("%s is a terminal state", state1),
                    jobModel.isTerminalState(state1, service));
        assertFalse(String.format("%s is a terminal state", state2),
                    jobModel.isTerminalState(state2, service));
        assertEquals(4, jobModel.getStatusCodesFor(service).size());

        em.getTransaction().begin();

        StatusCodeSequencing loop = new StatusCodeSequencing(service,
                                                             terminalState,
                                                             state1,
                                                             kernel.getCore());
        em.persist(loop);
        try {
            em.getTransaction().commit();
            fail("Expected failure due to circularity");
        } catch (Exception e) {
            // expected
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.getTransaction().begin();
        }

        assertTrue(String.format("%s is not a terminal state", terminalState),
                   jobModel.isTerminalState(terminalState, service));

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
    public void testLogInvalidSequencingTransition() throws Exception {
        // model.setLogConfiguration(Utils.getDocument(getClass().getResourceAsStream("/test-log-config.xml")));
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
        Product service2 = new Product("Service 2", kernel.getCore());
        em.persist(service2);

        List<Tuple<StatusCode, StatusCode>> sequences = new ArrayList<Tuple<StatusCode, StatusCode>>();
        sequences.add(new Tuple<StatusCode, StatusCode>(startState, state1));
        sequences.add(new Tuple<StatusCode, StatusCode>(state1, state2));
        sequences.add(new Tuple<StatusCode, StatusCode>(state2, terminalState));

        model.getJobModel().createStatusCodeSequencings(service, sequences,
                                                        kernel.getCore());
        model.getJobModel().createStatusCodeSequencings(service2, sequences,
                                                        kernel.getCore());

        ProductChildSequencingAuthorization invalidSeq = new ProductChildSequencingAuthorization(
                                                                                                 service,
                                                                                                 startState,
                                                                                                 service2,
                                                                                                 terminalState,
                                                                                                 kernel.getCore());
        em.persist(invalidSeq);
        Job parent = jobModel.newInitializedJob(service, kernel.getCore());
        Job child = jobModel.newInitializedJob(service2, kernel.getCore());
        child.setParent(parent);
        em.getTransaction().commit();
        em.getTransaction().begin();
        em.refresh(parent);
        assertTrue("Child is not considered active", jobModel.isActive(child));
        assertEquals(0, jobModel.getActiveSubJobsOf(parent).size());
        jobModel.changeStatus(parent, startState, kernel.getCore(),
                              "transition from test");
        em.getTransaction().commit();
        List<JobChronology> chronology = jobModel.getChronologyForJob(child);
        assertEquals(2, chronology.size());
        for (JobChronology crumb : chronology) {
            assertEquals(kernel.getUnset(), crumb.getStatus());
        }
    }

}
