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

import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.StatusCode;
import com.chiralbehaviors.CoRE.jooq.tables.records.ChildSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobChronologyRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.StatusCodeSequencingRecord;
import com.chiralbehaviors.CoRE.meta.JobModel;
import com.hellblazer.utils.Tuple;

/**
 * @author hhildebrand
 *
 */
public class StatusCodeTest extends AbstractModelTest {

    @Test
    public void testIsTerminalState() throws Exception {
        // model.setLogConfiguration(Utils.getDocument(getClass().getResourceAsStream("/test-log-config.xml")));
        JobModel jobModel = model.getJobModel();
        StatusCode startState = model.records()
                                     .newStatusCode("top-level");
        startState.insert();

        StatusCode state1 = model.records()
                                 .newStatusCode("state-1");
        state1.insert();

        StatusCode state2 = model.records()
                                 .newStatusCode("state-2");
        state2.insert();

        StatusCode terminalState = model.records()
                                        .newStatusCode("terminal state");
        terminalState.insert();

        Product service = model.records()
                               .newProduct("My Service");
        service.insert();

        List<Tuple<StatusCode, StatusCode>> sequences = new ArrayList<Tuple<StatusCode, StatusCode>>();
        sequences.add(new Tuple<StatusCode, StatusCode>(startState, state1));
        sequences.add(new Tuple<StatusCode, StatusCode>(state1, state2));
        sequences.add(new Tuple<StatusCode, StatusCode>(state2, terminalState));

        model.getJobModel()
             .createStatusCodeSequencings(service, sequences);

        assertTrue(String.format("%s is not a terminal state", terminalState),
                   jobModel.isTerminalState(terminalState, service));
        assertFalse(String.format("%s is a terminal state", startState),
                    jobModel.isTerminalState(startState, service));
        assertFalse(String.format("%s is a terminal state", state1),
                    jobModel.isTerminalState(state1, service));
        assertFalse(String.format("%s is a terminal state", state2),
                    jobModel.isTerminalState(state2, service));
        assertEquals(4, jobModel.getStatusCodesFor(service)
                                .size());

        StatusCodeSequencingRecord loop = model.records()
                                               .newStatusCodeSequencing(service,
                                                                        terminalState,
                                                                        state1);
        loop.insert();
        try {
            fail("Expected failure due to circularity");
        } catch (Exception e) {
            loop.delete();
        }
        assertTrue(String.format("%s is not a terminal state", terminalState),
                   jobModel.isTerminalState(terminalState, service));

        StatusCode loopState = model.records()
                                    .newStatusCode("loop-state");
        loopState.insert();

        loop = model.records()
                    .newStatusCodeSequencing(service, state2, loopState);
        loop.insert();

        StatusCodeSequencingRecord terminate = model.records()
                                                    .newStatusCodeSequencing(service,
                                                                             loopState,
                                                                             terminalState);
        terminate.insert();

        StatusCodeSequencingRecord back = model.records()
                                               .newStatusCodeSequencing(service,
                                                                        loopState,
                                                                        state1);
        back.insert();
        terminate.insert();
    }

    @Test
    public void testLogInvalidSequencingTransition() throws Exception {
        // model.setLogConfiguration(Utils.getDocument(getClass().getResourceAsStream("/test-log-config.xml")));
        JobModel jobModel = model.getJobModel();
        StatusCode startState = model.records()
                                     .newStatusCode("top-level");
        startState.insert();

        StatusCode state1 = model.records()
                                 .newStatusCode("state-1");
        state1.insert();

        StatusCode state2 = model.records()
                                 .newStatusCode("state-2");
        state2.insert();

        StatusCode terminalState = model.records()
                                        .newStatusCode("terminal state");
        terminalState.insert();

        Product service = model.records()
                               .newProduct("My Service");
        service.insert();
        Product service2 = model.records()
                                .newProduct("Service 2");
        service2.insert();

        List<Tuple<StatusCode, StatusCode>> sequences = new ArrayList<Tuple<StatusCode, StatusCode>>();
        sequences.add(new Tuple<StatusCode, StatusCode>(startState, state1));
        sequences.add(new Tuple<StatusCode, StatusCode>(state1, state2));
        sequences.add(new Tuple<StatusCode, StatusCode>(state2, terminalState));

        model.getJobModel()
             .createStatusCodeSequencings(service, sequences);
        model.getJobModel()
             .createStatusCodeSequencings(service2, sequences);

        ChildSequencingAuthorizationRecord invalidSeq = model.records()
                                                             .newChildSequencingAuthorization(service,
                                                                                              startState,
                                                                                              service2,
                                                                                              terminalState);
        invalidSeq.insert();
        JobRecord parent = jobModel.newInitializedJob(service);
        JobRecord child = jobModel.newInitializedJob(service2);
        child.setParent(parent.getId());
        assertNotNull("Parent is null", child.getParent());
        assertTrue("Child is not considered active", jobModel.isActive(child));
        assertEquals(1, jobModel.getActiveSubJobsOf(parent)
                                .size());
        jobModel.changeStatus(parent, startState, "transition from test");
        List<JobChronologyRecord> chronology = jobModel.getChronologyForJob(child);
        assertEquals(chronology.toString(), 2, chronology.size());
        for (JobChronologyRecord crumb : chronology) {
            assertEquals(kernel.getUnset(), crumb.getStatus());
        }
    }

}
