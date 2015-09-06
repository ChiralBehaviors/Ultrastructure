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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.job.Job;
import com.chiralbehaviors.CoRE.job.JobChronology;
import com.chiralbehaviors.CoRE.job.ProductChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.job.status.StatusCode;
import com.chiralbehaviors.CoRE.job.status.StatusCodeSequencing;
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

        em.flush();

        assertTrue(String.format("%s is not a terminal state", terminalState),
                   jobModel.isTerminalState(terminalState, service));
        assertFalse(String.format("%s is a terminal state", startState),
                    jobModel.isTerminalState(startState, service));
        assertFalse(String.format("%s is a terminal state", state1),
                    jobModel.isTerminalState(state1, service));
        assertFalse(String.format("%s is a terminal state", state2),
                    jobModel.isTerminalState(state2, service));
        assertEquals(4, jobModel.getStatusCodesFor(service).size());

        StatusCodeSequencing loop = new StatusCodeSequencing(service,
                                                             terminalState,
                                                             state1,
                                                             kernel.getCore());
        em.persist(loop);
        try {
            em.flush();
            fail("Expected failure due to circularity");
        } catch (Exception e) {
            em.remove(loop);
        }
        em.refresh(terminalState);
        em.refresh(service);
        em.refresh(state1);
        em.refresh(state2);
        Agency core = kernel.getCore();
        assertTrue(String.format("%s is not a terminal state", terminalState),
                   jobModel.isTerminalState(terminalState, service));

        StatusCode loopState = new StatusCode("loop-state", core);
        em.persist(loopState);

        loop = new StatusCodeSequencing(service, state2, loopState, core);
        em.persist(loop);

        StatusCodeSequencing terminate = new StatusCodeSequencing(
                                                                  service,
                                                                  loopState,
                                                                  terminalState,
                                                                  core);
        em.persist(terminate);

        StatusCodeSequencing back = new StatusCodeSequencing(service,
                                                             loopState, state1,
                                                             core);
        em.persist(back);
        em.persist(terminate);
        em.flush();
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
        em.flush();
        em.refresh(parent);
        em.refresh(child);
        assertNotNull("Parent is null", child.getParent());
        assertTrue("Child is not considered active", jobModel.isActive(child));
        assertEquals(1, jobModel.getActiveSubJobsOf(parent).size());
        jobModel.changeStatus(parent, startState, kernel.getCore(),
                              "transition from test");
        em.flush();
        List<JobChronology> chronology = jobModel.getChronologyForJob(child);
        assertEquals(chronology.toString(), 2, chronology.size());
        for (JobChronology crumb : chronology) {
            assertEquals(kernel.getUnset(), crumb.getStatus());
        }
    }

}
