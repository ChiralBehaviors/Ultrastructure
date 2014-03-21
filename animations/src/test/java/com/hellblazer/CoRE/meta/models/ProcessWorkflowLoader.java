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
package com.hellblazer.CoRE.meta.models;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.tuple.Pair;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.event.Protocol;
import com.hellblazer.CoRE.event.status.StatusCode;
import com.hellblazer.CoRE.kernel.Kernel;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.meta.LocationModel;
import com.hellblazer.CoRE.meta.ProductModel;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;

/**
 * @author hparry
 * 
 */
public class ProcessWorkflowLoader {

	public Relationship instanceOf;
	public Relationship classOf;

	public Agency EC2_API;
	public Agency DEPLOYER_ANY;

	public Product DEPLOY_SERVICE;
	public Product createTopology;
	public Product pushJars;

	public Product ANY_PROCESS;
	public Product proc73;
	public Product DISTRIBUTED_SYSTEM;
	public Product ds1;
	public Product CLUSTER;
	public Product cluster1;

	
	public StatusCode unset;
	public StatusCode deploying;
	public StatusCode deployed;
	public StatusCode starting;
	public StatusCode running;
	public StatusCode stopping;
	public StatusCode stopped;
	public StatusCode failed;
	public StatusCode undeployed;

	public Location DATA_CENTER;
	public Location dc421;
	public Location CONTAINER;
	public Location c192_67_78_48;

	private ModelImpl model;
	private Kernel kernel;
	public Agency core;
	public Product sameProduct;
	public Product anyProduct;
	public Agency anyAgency;
	public Location anyLocation;
	public Relationship sameRelationship;
	public Relationship anyRelationship;
	public Relationship notApplicableRelationship;
	public Relationship isA;
	private EntityManager em;

	public ProcessWorkflowLoader(EntityManager em) throws Exception {
		this.em = em;
		model = new ModelImpl(em);
		kernel = model.getKernel();
		core = kernel.getCore();
		sameProduct = kernel.getSameProduct();
		anyProduct = kernel.getAnyProduct();
		anyAgency = kernel.getAnyAgency();
		anyLocation = kernel.getAnyLocation();
		sameRelationship = kernel.getSameRelationship();
		anyRelationship = kernel.getAnyRelationship();
		notApplicableRelationship = kernel.getNotApplicableRelationship();
		isA = kernel.getIsA();
		unset = kernel.getUnset();
	}

	public void createAgencies() {
		EC2_API = new Agency("EC2 API", "The EC2 API", core);
		em.persist(EC2_API);
		DEPLOYER_ANY = new Agency("DEPLOYER ANY",
				"Any agency that deploys configurations", core);
		em.persist(DEPLOYER_ANY);
	}

	public void createAgencyNetworks() {
		model.getAgencyModel().link(EC2_API, kernel.getIsA(), DEPLOYER_ANY,
				core);
	}

	public void createProducts() {

		DEPLOY_SERVICE = new Product("Any Deploy Service",
				"Any deploy service", core);
		em.persist(DEPLOY_SERVICE);
		createTopology = new Product("Create Topology", "The service that creates a network topology", core);
		em.persist(createTopology);
		pushJars = new Product("Push Jars", "The service that pushes process jars to a topology", core);
		em.persist(pushJars);

		ANY_PROCESS = new Product("Any Process", "Any process", core);
		em.persist(ANY_PROCESS);
		proc73 = new Product("Process 73", "Process 73", core);
		em.persist(proc73);

		DISTRIBUTED_SYSTEM = new Product("Distributed System", "Distributed system", core);
		em.persist(DISTRIBUTED_SYSTEM);
		ds1 = new Product("DS1", "Distributed system 1", core);
		em.persist(ds1);
		CLUSTER = new Product("Cluster", "Cluster", core);
		em.persist(CLUSTER);
		cluster1 = new Product("Cluster1", "Cluster 1", core);
		em.persist(ANY_PROCESS);
	}

	public void createProductNetworks() {
		ProductModel pm = model.getProductModel();
		pm.link(proc73, instanceOf, ANY_PROCESS, core);
		pm.link(cluster1, instanceOf, CLUSTER, core);
		pm.link(ds1, instanceOf, DISTRIBUTED_SYSTEM, core);
	}

	public void createLocations() {
		DATA_CENTER = new Location("Data Center", "Any data center location",
				core);
		em.persist(DATA_CENTER);
		dc421 = new Location("dc421", "The dc421 datacenter", core);
		em.persist(dc421);
		CONTAINER = new Location("Container", "Any container location", core);
		em.persist(CONTAINER);
		c192_67_78_48 = new Location("c192_67_78_48",
				"The container located at 192.168.78.48", core);
		em.persist(c192_67_78_48);
	}

	public void createLocationNetworks() {
		LocationModel lm = model.getLocationModel();
		lm.link(dc421, isA, DATA_CENTER, core);
		lm.link(c192_67_78_48, isA, CONTAINER, core);
	}

	public void createStatusCodes() {
		unset = new StatusCode("unset", "unset", core);
		em.persist(unset);
		deploying = new StatusCode("deploying", "deploying", core);
		em.persist(deploying);
		deployed = new StatusCode("deployed", "deployed", core);
		em.persist(deployed);
		starting = new StatusCode("started", "started", core);
		em.persist(starting);
		running = new StatusCode("starting", "starting", core);
		em.persist(running);
		stopping = new StatusCode("stopping", "stopping", core);
		em.persist(stopping);
		stopped = new StatusCode("stopped", "stopped", core);
		em.persist(stopped);
		failed = new StatusCode("failed", "failed", core);
		em.persist(failed);
		undeployed = new StatusCode("undeployed", "undeployed", core);
		em.persist(undeployed);
	}

	public void createStatusCodeSequencing() {
		List<Pair<StatusCode, StatusCode>> pairs = new LinkedList<Pair<StatusCode, StatusCode>>();
//		pairs.add(Pair.of(unset, deploying));
//		pairs.add(Pair.of(deploying, deployed));
//		pairs.add(Pair.of(deployed, starting));
//		pairs.add(Pair.of(starting, running));
//		pairs.add(Pair.of(running, stopping));
//		pairs.add(Pair.of(stopping, stopped));
//		pairs.add(Pair.of(stopped, undeployed));
//		pairs.add(Pair.of(deploying, failed));
//		pairs.add(Pair.of(deployed, failed));
//		pairs.add(Pair.of(starting, failed));
//		pairs.add(Pair.of(running, failed));
//		pairs.add(Pair.of(stopping, failed));
//		pairs.add(Pair.of(stopped, failed));
//		pairs.add(Pair.of(failed, undeployed));
		
		pairs.add(Pair.of(unset, deploying));
		pairs.add(Pair.of(deploying, deployed));
		pairs.add(Pair.of(deploying, failed));
		model.getJobModel().createStatusCodeSequencings(DEPLOY_SERVICE, pairs, 0, core);
	
	}
	
	public void createStatusCodeSequencingAuthorizations() {
		
	}
	
	public void createRelationships() {
		instanceOf = new Relationship("instance of", "A is an instance of B", core);
		instanceOf.setInverse(classOf);
		em.persist(instanceOf);
		classOf = new Relationship("class of", "A is the class of B", core);
		classOf.setInverse(instanceOf);
		em.persist(classOf);
	}

	public void createProtocols() {
		Protocol createTopoProtocol = new Protocol(createTopology, anyAgency, sameProduct, anyLocation, anyLocation, DEPLOYER_ANY, DEPLOY_SERVICE, sameProduct, false, core);
		em.persist(createTopoProtocol);
		Protocol pushJarsProtocol = new Protocol(pushJars, anyAgency, sameProduct, anyLocation, anyLocation, DEPLOYER_ANY, DEPLOY_SERVICE, sameProduct, false, core);
		em.persist(pushJarsProtocol);
	}
}
