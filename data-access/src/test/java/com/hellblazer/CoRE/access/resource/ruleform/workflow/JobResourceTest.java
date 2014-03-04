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
package com.hellblazer.CoRE.access.resource.ruleform.workflow;

import javax.persistence.EntityTransaction;

import org.junit.Before;
import org.junit.Test;

import com.hellblazer.CoRE.event.Job;
import com.hellblazer.CoRE.meta.JobModel;
import com.hellblazer.CoRE.meta.models.AbstractModelTest;
import com.hellblazer.CoRE.meta.models.HalliesOrderProcessingLoader;

import static org.junit.Assert.*;

/**
 * @author hparry
 *
 */
public class JobResourceTest extends AbstractModelTest{
	
	private HalliesOrderProcessingLoader scenario;
	private JobResource resource;

	@Override
	@Before
	public void initialize() throws Exception {
		super.initialize();
		resource = new JobResource(model);
		EntityTransaction txn = em.getTransaction();
		scenario = new HalliesOrderProcessingLoader(em);
		txn.begin();
		scenario.load();
		txn.commit();
	}
	
	//@Test
//	public void testDeployShoggoth() {
//		
//		Job order = new Job(scenario.armyOfDarkness, scenario.admin,
//				scenario.deploy, scenario.shoggoth, scenario.node21, kernel.getNotApplicableLocation(),
//				scenario.core);
//		
//		order = resource.insertJob(order, null);
//		assertNotNull(order.getId());
//		
//		resource.changeJobStatus(order.getId(), scenario.active);
//		order = resource.getJob(order.getId());
//		
//		assertEquals(scenario.active, order.getStatus());
//		
//	}

}
