/** 
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.event;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.product.Product;
import com.hellblazer.utils.Tuple;

/**
 * @author hparry
 * 
 */
public class WorkflowTest extends AbstractModelTest {

    @Test
    public void testDeploySystem() {

        em.getTransaction().begin();
        Agency core = kernel.getCore();
        Agency admin = new Agency("Administrator", "Administrator", core);
        em.persist(admin);

        StatusCode notStarted = new StatusCode("Not started", "not started",
                                               core);
        notStarted.setPropagateChildren(true);
        em.persist(notStarted);
        StatusCode inProgress = new StatusCode("In progress", "in progress",
                                               core);
        em.persist(inProgress);
        StatusCode completed = new StatusCode("Completed", "completed", core);
        em.persist(completed);

        Product deployWorkflow = new Product("Deploy service",
                                             "deploy service", core);
        em.persist(deployWorkflow);

        Product shutdownProcesses = new Product(
                                                "Shudown Process service",
                                                "Service for shutting down processes",
                                                core);
        em.persist(shutdownProcesses);

        Product deployProcesses = new Product(
                                              "Deploy Process service",
                                              "Deploy new processes to containers",
                                              core);
        em.persist(deployProcesses);

        Product restartProcesses = new Product(
                                               "Restart Process service",
                                               "Service to restart deployed processes",
                                               core);
        em.persist(restartProcesses);

        List<Tuple<StatusCode, StatusCode>> codes = new LinkedList<Tuple<StatusCode, StatusCode>>();
        codes.add(new Tuple<StatusCode, StatusCode>(notStarted, inProgress));
        codes.add(new Tuple<StatusCode, StatusCode>(inProgress, completed));
        model.getJobModel().createStatusCodeSequencings(deployWorkflow, codes,
                                                        80, core);

        model.getJobModel().createStatusCodeSequencings(shutdownProcesses,
                                                        codes, 10, core);
        model.getJobModel().createStatusCodeSequencings(deployProcesses, codes,
                                                        20, core);
        model.getJobModel().createStatusCodeSequencings(restartProcesses,
                                                        codes, 30, core);

        em.getTransaction().commit();

        em.getTransaction().begin();
        Protocol shutdownProtocol = new Protocol(deployWorkflow, admin,
                                                 kernel.getSameProduct(),
                                                 kernel.getAnyLocation(),
                                                 kernel.getAnyLocation(),
                                                 admin, shutdownProcesses,
                                                 kernel.getAnyProduct(), core);
        em.persist(shutdownProtocol);
        Protocol deployProtocol = new Protocol(deployWorkflow, admin,
                                               kernel.getSameProduct(),
                                               kernel.getAnyLocation(),
                                               kernel.getAnyLocation(), admin,
                                               deployProcesses,
                                               kernel.getAnyProduct(), core);
        em.persist(deployProtocol);
        Protocol restartProtocol = new Protocol(deployWorkflow, admin,
                                                kernel.getSameProduct(),
                                                kernel.getAnyLocation(),
                                                kernel.getAnyLocation(), admin,
                                                restartProcesses,
                                                kernel.getAnyProduct(), core);
        em.persist(restartProtocol);
        em.getTransaction().commit();

        em.getTransaction().begin();
        Job deployJob = new Job(admin, admin, deployWorkflow,
                                kernel.getAnyProduct(),
                                kernel.getAnyLocation(),
                                kernel.getAnyLocation(), core);
        deployJob.setStatus(notStarted);
        em.persist(deployJob);

        em.getTransaction().commit();

        List<Job> activeJobsFor = model.getJobModel().getActiveJobsFor(admin);
        assertEquals(4, activeJobsFor.size());

    }

}
