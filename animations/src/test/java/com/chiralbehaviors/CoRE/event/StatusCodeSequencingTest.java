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
import java.util.stream.Collectors;

import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.StatusCode;
import com.chiralbehaviors.CoRE.jooq.tables.records.StatusCodeSequencingRecord;
import com.chiralbehaviors.CoRE.meta.JobModel;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.meta.models.JobModelImpl;

/**
 * @author hhildebrand
 *
 */
public class StatusCodeSequencingTest extends AbstractModelTest {

    @Test
    public void testHasNoSccs() throws Exception {
        JobModel jobModel = model.getJobModel();

        StatusCode startState = model.records()
                                     .newStatusCode("top-level",
                                                    kernel.getCore());
        startState.insert();

        StatusCode state1 = model.records()
                                 .newStatusCode("state-1", kernel.getCore());
        state1.insert();

        StatusCode state2 = model.records()
                                 .newStatusCode("state-2", kernel.getCore());
        state2.insert();

        StatusCode terminalState = model.records()
                                        .newStatusCode("terminal state",
                                                       kernel.getCore());
        terminalState.insert();

        Product service = model.records()
                               .newProduct("My Service", kernel.getCore());
        service.insert();

        StatusCodeSequencingRecord sequence1 = model.records()
                                                    .newStatusCodeSequencing(service,
                                                                             startState,
                                                                             state1,
                                                                             kernel.getCore());
        sequence1.insert();

        StatusCodeSequencingRecord sequence2 = model.records()
                                                    .newStatusCodeSequencing(service,
                                                                             state1,
                                                                             state2,
                                                                             kernel.getCore());
        sequence2.insert();

        StatusCodeSequencingRecord sequence3 = model.records()
                                                    .newStatusCodeSequencing(service,
                                                                             state2,
                                                                             terminalState,
                                                                             kernel.getCore());
        sequence3.insert();

        StatusCode loopState = model.records()
                                    .newStatusCode("loop-state",
                                                   kernel.getCore());
        loopState.insert();

        StatusCodeSequencingRecord loop = model.records()
                                               .newStatusCodeSequencing(service,
                                                                        state2,
                                                                        loopState,
                                                                        kernel.getCore());
        loop.insert();

        StatusCodeSequencingRecord terminate = model.records()
                                                    .newStatusCodeSequencing(service,
                                                                             loopState,
                                                                             terminalState,
                                                                             kernel.getCore());
        terminate.insert();

        StatusCodeSequencingRecord back = model.records()
                                               .newStatusCodeSequencing(service,
                                                                        loopState,
                                                                        state1,
                                                                        kernel.getCore());
        back.insert();
        assertTrue(jobModel.hasScs(service));
        jobModel.validateStateGraph(Arrays.asList(service));
    }

    @Test
    public void testHasNoTerminalSCCs() {
        Map<StatusCode, List<StatusCode>> graph = new HashMap<StatusCode, List<StatusCode>>();
        StatusCode[] codes = new StatusCode[] { new StatusCode(UUID.randomUUID()),
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

        JobModel jobModel = model.getJobModel();

        StatusCode startState = model.records()
                                     .newStatusCode("top-level",
                                                    kernel.getCore());
        startState.insert();

        StatusCode state1 = model.records()
                                 .newStatusCode("state-1", kernel.getCore());
        state1.insert();

        StatusCode state2 = model.records()
                                 .newStatusCode("state-2", kernel.getCore());
        state2.insert();

        StatusCode terminalState = model.records()
                                        .newStatusCode("terminal state",
                                                       kernel.getCore());
        terminalState.insert();

        Product service = model.records()
                               .newProduct("My Service", kernel.getCore());
        service.insert();

        StatusCodeSequencingRecord sequence1 = model.records()
                                                    .newStatusCodeSequencing(service,
                                                                             startState,
                                                                             state1,
                                                                             kernel.getCore());
        sequence1.insert();

        StatusCodeSequencingRecord sequence2 = model.records()
                                                    .newStatusCodeSequencing(service,
                                                                             state1,
                                                                             state2,
                                                                             kernel.getCore());
        sequence2.insert();

        StatusCodeSequencingRecord sequence3 = model.records()
                                                    .newStatusCodeSequencing(service,
                                                                             state2,
                                                                             terminalState,
                                                                             kernel.getCore());
        sequence3.insert();

        StatusCodeSequencingRecord loop = model.records()
                                               .newStatusCodeSequencing(service,
                                                                        terminalState,
                                                                        state1,
                                                                        kernel.getCore());
        loop.insert();

        assertTrue(jobModel.hasNonTerminalSCCs(service));

        assertTrue(jobModel.hasScs(service));
        try {
            jobModel.validateStateGraph(Arrays.asList(service));
            fail("Did not catch event with non terminal loop");
        } catch (SQLException e) {
            // expected
            assertTrue(e.getMessage(), e.getMessage()
                                        .endsWith("has at least one non terminal SCC defined in its status code graph"));
        }
    }

    @Test
    public void testHasTerminalSCCs() {
        Map<StatusCode, List<StatusCode>> graph = new HashMap<StatusCode, List<StatusCode>>();
        StatusCode[] codes = new StatusCode[] { new StatusCode(UUID.randomUUID()),
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
        StatusCode[] codes = new StatusCode[] { new StatusCode(UUID.randomUUID()),
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

        JobModel jobModel = model.getJobModel();

        StatusCode startState = model.records()
                                     .newStatusCode("top-level",
                                                    kernel.getCore());
        startState.insert();

        StatusCode startState2 = model.records()
                                      .newStatusCode("top-level 2",
                                                     kernel.getCore());
        startState2.insert();

        StatusCode state1 = model.records()
                                 .newStatusCode("state-1", kernel.getCore());
        state1.insert();

        StatusCode state2 = model.records()
                                 .newStatusCode("state-2", kernel.getCore());
        state2.insert();

        StatusCode terminalState = model.records()
                                        .newStatusCode("terminal state",
                                                       kernel.getCore());
        terminalState.insert();

        Product service = model.records()
                               .newProduct("My Service", kernel.getCore());
        service.insert();

        StatusCodeSequencingRecord sequence1 = model.records()
                                                    .newStatusCodeSequencing(service,
                                                                             startState,
                                                                             state1,
                                                                             kernel.getCore());
        sequence1.insert();

        StatusCodeSequencingRecord sequence1a = model.records()
                                                     .newStatusCodeSequencing(service,
                                                                              startState2,
                                                                              state1,
                                                                              kernel.getCore());
        sequence1a.insert();

        StatusCodeSequencingRecord sequence2 = model.records()
                                                    .newStatusCodeSequencing(service,
                                                                             state1,
                                                                             state2,
                                                                             kernel.getCore());
        sequence2.insert();

        StatusCodeSequencingRecord sequence3 = model.records()
                                                    .newStatusCodeSequencing(service,
                                                                             state2,
                                                                             terminalState,
                                                                             kernel.getCore());
        sequence3.insert();

        create.configuration()
              .connectionProvider()
              .acquire()
              .commit();

        List<StatusCode> initialStates = jobModel.getInitialStates(service);
        assertEquals(initialStates.stream()
                                  .map(s -> s.getName())
                                  .collect(Collectors.toList())
                                  .toString(),
                     2, initialStates.size());
        assertTrue(initialStates.contains(startState));
        assertTrue(initialStates.contains(startState2));
        try {
            jobModel.validateStateGraph(Arrays.asList(service));
            fail("Did not catch event with non terminal loop");
        } catch (SQLException e) {
            // expected
            assertTrue(e.getMessage(), e.getMessage()
                                        .contains("has multiple initial states defined in its status code graph"));
        }
    }
}
