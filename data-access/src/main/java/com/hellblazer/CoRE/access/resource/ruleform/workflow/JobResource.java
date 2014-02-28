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

import java.security.InvalidParameterException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.event.Job;
import com.hellblazer.CoRE.event.JobChronology;
import com.hellblazer.CoRE.event.Protocol;
import com.hellblazer.CoRE.meta.JobModel;
import com.hellblazer.CoRE.meta.Model;
import com.hellblazer.CoRE.meta.models.JobModelImpl;

/**
 * @author hparry
 * 
 */
@Path("/v{version : \\d+}/services/data/ruleform/Job")
public class JobResource {

	private EntityManager em;
	private JobModel model;

	public JobResource(Model model) {
		this.model = new JobModelImpl(model);
		this.em = model.getEntityManager();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, "text/json" })
	public List<Job> getJobsForAgency(@QueryParam("agencyId") Long agencyId) {
		if (agencyId == null) {
			TypedQuery<Job> query = em.createQuery("select j from Job j",
					Job.class);
			return query.getResultList();
		}
		Agency agency = em.find(Agency.class, agencyId);
		if (agency == null) {
			throw new InvalidParameterException(String.format(
					"Agency with id %d does not exist", agencyId));
		}
		return model.getActiveJobsFor(agency);
	}

	@POST
	@Produces({ MediaType.APPLICATION_JSON, "text/json" })
	public Job insertJob(Job parent, Protocol protocol) {
		em.getTransaction().begin();
		try {
			Job job = model.insertJob(parent, protocol);
			em.getTransaction().commit();
			return job;
		} catch (Exception e) {
			em.getTransaction().rollback();
			throw e;
		}
	}
	@GET
	@Path("/{id}/chronology")
	@Produces({ MediaType.APPLICATION_JSON, "text/json" })
	public List<JobChronology> getChronology(@PathParam("id") long jobId) {
		Job job = em.find(Job.class, jobId);
		if (job == null) {
			throw new InvalidParameterException(String.format("No job with %d exists", jobId));
		}
		
		return model.getChronologyForJob(job);
	}

}
