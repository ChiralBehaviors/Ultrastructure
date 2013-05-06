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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;

import org.junit.Test;

import com.hellblazer.CoRE.event.Job;
import com.hellblazer.CoRE.event.StatusCode;
import com.hellblazer.CoRE.event.StatusCodeSequencing;
import com.hellblazer.CoRE.meta.JobModel;
import com.hellblazer.CoRE.product.Product;

/**
 * @author hhildebrand
 * 
 */
public class JobModelTest extends AbstractModelTest {

    @Test
    public void testJobScenario() throws Exception {

        JobModel jobModel = model.getJobModel();
        @SuppressWarnings("unused")
        JobScenario scenario = new JobScenario(model);
        model.getEntityManager().clear();
        List<Job> jobs = findAllJobs();
        assertEquals(7, jobs.size());

        List<Job> topLevelJobs = jobModel.getTopLevelJobs();
        assertEquals(1, topLevelJobs.size());
        Job topLevelJob = topLevelJobs.get(0);

        assertNull(topLevelJob.getParent());
        assertTrue(jobModel.isActive(topLevelJob));
        assertFalse(topLevelJob.getStatus().equals(model.getKernel().getUnset()));
        assertFalse(jobModel.isTerminalState(topLevelJob.getStatus(),
                                             topLevelJob.getService()));
        List<Job> activeExplicit = jobModel.getActiveExplicitJobs();
        assertEquals(1, activeExplicit.size());
        assertEquals(0, jobModel.getChildActions(topLevelJob).size());
        assertEquals(0, jobModel.getSiblingActions(topLevelJob).size());
        assertEquals(1, jobModel.getActiveSubJobsOf(topLevelJob).size());
        assertEquals(0, jobModel.getActiveJobsFor(scenario.htsfTech).size());
    }

    private List<Job> findAllJobs() {
        TypedQuery<Job> query = model.getEntityManager().createQuery("select j from Job j",
                                                                     Job.class);
        return query.getResultList();
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
}
