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

package com.hellblazer.CoRE.event;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.meta.JobModel;
import com.hellblazer.CoRE.meta.Model;
import com.hellblazer.CoRE.meta.models.JobModelImpl;
import com.hellblazer.CoRE.meta.models.ModelImpl;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.test.DatabaseTest;

/**
 * @author hhildebrand
 * 
 */
public class StatusCodeSequencingTest extends DatabaseTest {
    @AfterClass
    public static void teardown() throws SQLException {
        alterTrigger(true);
    }

    protected static void alterTrigger(boolean enable) throws SQLException {
        String query = String.format("ALTER TABLE ruleform.status_code_sequencing %s TRIGGER ensure_valid_state_graph",
                                     enable ? "ENABLE" : "DISABLE");
        connection.createStatement().execute(query);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.test.DatabaseTest#clear()
     */
    @Override
    @Before
    public void clear() throws SQLException {
        super.clear();
        alterTrigger(false);
    }

    @Test
    public void testHasNoTerminalSCCs() {
        Map<StatusCode, List<StatusCode>> graph = new HashMap<StatusCode, List<StatusCode>>();
        StatusCode[] codes = new StatusCode[] { new StatusCode(0L),
                new StatusCode(1L), new StatusCode(2L), new StatusCode(3L),
                new StatusCode(4L), new StatusCode(5L), new StatusCode(6L),
                new StatusCode(7L), new StatusCode(8L) };
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

        Agency core = new Agency("CoRE");
        core.setUpdatedBy(core);
        em.persist(core);

        Model model = new ModelImpl(em);
        JobModel jobModel = model.getJobModel();

        StatusCode startState = new StatusCode("top-level", core);
        em.persist(startState);

        StatusCode state1 = new StatusCode("state-1", core);
        em.persist(state1);

        StatusCode state2 = new StatusCode("state-2", core);
        em.persist(state2);

        StatusCode terminalState = new StatusCode("terminal state", core);
        em.persist(terminalState);

        Product service = new Product("My Service", core);
        em.persist(service);

        StatusCodeSequencing sequence1 = new StatusCodeSequencing(service,
                                                                  startState,
                                                                  state1, core);
        em.persist(sequence1);

        StatusCodeSequencing sequence2 = new StatusCodeSequencing(service,
                                                                  state1,
                                                                  state2, core);
        em.persist(sequence2);

        StatusCodeSequencing sequence3 = new StatusCodeSequencing(
                                                                  service,
                                                                  state2,
                                                                  terminalState,
                                                                  core);
        em.persist(sequence3);

        em.getTransaction().commit();

        em.getTransaction().begin();

        StatusCodeSequencing loop = new StatusCodeSequencing(service,
                                                             terminalState,
                                                             state1, core);
        em.persist(loop);

        assertTrue(jobModel.hasTerminalSCCs(service));
        service = em.merge(service);

        assertTrue(jobModel.hasScs(service));
        try {
            jobModel.validateStateGraph(Arrays.asList(service));
            fail("Did not catch event with non terminal loop");
        } catch (SQLException e) {
            // expected 
            assertTrue(e.getMessage().endsWith("has at least one terminal SCC defined in its status code graph"));
        }

        em.getTransaction().rollback();

        em.getTransaction().begin();

        StatusCode loopState = new StatusCode("loop-state", core);
        em.persist(loopState);

        loop = new StatusCodeSequencing(service, state2, loopState, core);
        loop.setSequenceNumber(2);
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
        back.setSequenceNumber(2);
        em.persist(back);
        em.persist(terminate);
        assertTrue(jobModel.hasScs(service));
        jobModel.validateStateGraph(Arrays.asList(service));
        em.getTransaction().commit();
    }

    @Test
    public void testHasTerminalSCCs() {
        Map<StatusCode, List<StatusCode>> graph = new HashMap<StatusCode, List<StatusCode>>();
        StatusCode[] codes = new StatusCode[] { new StatusCode(0L),
                new StatusCode(1L), new StatusCode(2L), new StatusCode(3L),
                new StatusCode(4L), new StatusCode(5L), new StatusCode(6L),
                new StatusCode(7L), new StatusCode(8L) };
        graph.put(codes[0], asList(codes[1]));
        graph.put(codes[1], asList(codes[2]));
        graph.put(codes[2], asList(codes[0], codes[6]));
        graph.put(codes[6], new ArrayList<StatusCode>());

        assertFalse(JobModelImpl.hasScc(graph));
    }

    @Test
    public void testLoop() {
        Map<StatusCode, List<StatusCode>> graph = new HashMap<StatusCode, List<StatusCode>>();
        StatusCode[] codes = new StatusCode[] { new StatusCode(0L),
                new StatusCode(1L), new StatusCode(2L), new StatusCode(3L),
                new StatusCode(4L), new StatusCode(5L), new StatusCode(6L),
                new StatusCode(7L), new StatusCode(8L) };
        graph.put(codes[0], asList(codes[1]));
        graph.put(codes[1], asList(codes[2]));
        graph.put(codes[2], asList(codes[3]));
        graph.put(codes[3], asList(codes[4]));
        graph.put(codes[4], asList(codes[2]));

        assertTrue(JobModelImpl.hasScc(graph));
    }
}
