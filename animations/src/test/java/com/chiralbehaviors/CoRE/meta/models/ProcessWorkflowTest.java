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
package com.chiralbehaviors.CoRE.meta.models;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityTransaction;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.event.Job;
import com.chiralbehaviors.CoRE.meta.JobModel;

/**
 * @author hparry
 * 
 */
public class ProcessWorkflowTest extends AbstractModelTest {

	private JobModel jobModel;
	private ProcessWorkflowLoader scenario;

	@Override
	@Before
	public void initialize() throws Exception {
		super.initialize();
		jobModel = model.getJobModel();
		EntityTransaction txn = em.getTransaction();
		scenario = new ProcessWorkflowLoader(em);
		txn.begin();
		scenario.load();
		txn.commit();
	}

	@Test
	public void testDeploySteelThread() {
		em.getTransaction().begin();
		Job deployJob = new Job(scenario.DEPLOYER_ANY, scenario.core,
				scenario.deploySystem, scenario.DISTRIBUTED_SYSTEM,
				scenario.anyLocation, scenario.anyLocation, scenario.core);
		em.persist(deployJob);
		em.getTransaction().commit();
		List<Job> activeJobsFor = jobModel.getActiveJobsFor(scenario.DEPLOYER_ANY);
		assertEquals(5, activeJobsFor.size());
	}

}
