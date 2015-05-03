/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.event;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import com.chiralbehaviors.CoRE.job.status.StatusCode;
import com.chiralbehaviors.CoRE.job.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.meta.JobModel;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.models.JobModelImpl;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 *
 */
public class StatusCodeSequencingTest extends AbstractModelTest {

    @Test
    public void testHasNoSccs() throws Exception {
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
        em.flush();

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

        StatusCode loopState = new StatusCode("loop-state", kernel.getCore());
        em.persist(loopState);

        StatusCodeSequencing loop = new StatusCodeSequencing(service, state2,
                                                             loopState,
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
        em.flush();
        assertTrue(jobModel.hasScs(service));
        jobModel.validateStateGraph(Arrays.asList(service));
    }

    @Test
    public void testHasNoTerminalSCCs() {
        Map<StatusCode, List<StatusCode>> graph = new HashMap<StatusCode, List<StatusCode>>();
        StatusCode[] codes = new StatusCode[] {
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()) };
        graph.put(codes[0], asList(codes[1]));
        graph.put(codes[1], asList(codes[2]));
        graph.put(codes[2], asList(codes[0], codes[6]));
        graph.put(codes[3], asList(codes[4]));
        graph.put(codes[4], asList(codes[5], codes[6]));
        graph.put(codes[5], asList(codes[3]));
        graph.put(codes[6], asList(codes[7]));
        graph.put(codes[7], asList(codes[8]));
        graph.put(codes[8], asList(codes[6]));

        assertTrue(JobModelImpl.hasScc(graph));
    }

    @Test
    public void testHasSccs() throws SQLException {
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
        em.flush();

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

        em.flush();

        StatusCodeSequencing loop = new StatusCodeSequencing(service,
                                                             terminalState,
                                                             state1,
                                                             kernel.getCore());
        em.persist(loop);

        assertTrue(jobModel.hasNonTerminalSCCs(service));
        service = em.merge(service);

        assertTrue(jobModel.hasScs(service));
        try {
            jobModel.validateStateGraph(Arrays.asList(service));
            fail("Did not catch event with non terminal loop");
        } catch (SQLException e) {
            // expected
            assertTrue(e.getMessage(),
                       e.getMessage().endsWith("has at least one non terminal SCC defined in its status code graph"));
        }
    }

    @Test
    public void testHasTerminalSCCs() {
        Map<StatusCode, List<StatusCode>> graph = new HashMap<StatusCode, List<StatusCode>>();
        StatusCode[] codes = new StatusCode[] {
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()) };
        graph.put(codes[0], asList(codes[1]));
        graph.put(codes[1], asList(codes[2]));
        graph.put(codes[2], asList(codes[0], codes[6]));
        graph.put(codes[6], new ArrayList<StatusCode>());

        assertFalse(JobModelImpl.hasScc(graph));
    }

    @Test
    public void testLoop() {
        Map<StatusCode, List<StatusCode>> graph = new HashMap<StatusCode, List<StatusCode>>();
        StatusCode[] codes = new StatusCode[] {
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()),
                new StatusCode(UUID.randomUUID()) };
        graph.put(codes[0], asList(codes[1]));
        graph.put(codes[1], asList(codes[2]));
        graph.put(codes[2], asList(codes[3]));
        graph.put(codes[3], asList(codes[4]));
        graph.put(codes[4], asList(codes[2]));

        assertTrue(JobModelImpl.hasScc(graph));
    }

    @Test
    public void testMultipleInitialStates() throws SQLException {
        em.getTransaction().begin();

        JobModel jobModel = model.getJobModel();

        StatusCode startState = new StatusCode("top-level", kernel.getCore());
        em.persist(startState);

        StatusCode startState2 = new StatusCode("top-level 2", kernel.getCore());
        em.persist(startState2);

        StatusCode state1 = new StatusCode("state-1", kernel.getCore());
        em.persist(state1);

        StatusCode state2 = new StatusCode("state-2", kernel.getCore());
        em.persist(state2);

        StatusCode terminalState = new StatusCode("terminal state",
                                                  kernel.getCore());
        em.persist(terminalState);

        Product service = new Product("My Service", kernel.getCore());
        em.persist(service);
        em.flush();

        StatusCodeSequencing sequence1 = new StatusCodeSequencing(
                                                                  service,
                                                                  startState,
                                                                  state1,
                                                                  kernel.getCore());
        em.persist(sequence1);

        StatusCodeSequencing sequence1a = new StatusCodeSequencing(
                                                                   service,
                                                                   startState2,
                                                                   state1,
                                                                   kernel.getCore());
        em.persist(sequence1a);

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

        List<StatusCode> initialStates = jobModel.getInitialStates(service);
        assertEquals(2, initialStates.size());
        assertTrue(initialStates.contains(startState));
        assertTrue(initialStates.contains(startState2));
        service = em.merge(service);
        try {
            jobModel.validateStateGraph(Arrays.asList(service));
            fail("Did not catch event with non terminal loop");
        } catch (SQLException e) {
            // expected
            assertTrue(e.getMessage(),
                       e.getMessage().contains("has multiple initial state defined in its status code graph"));
        }
    }
}
