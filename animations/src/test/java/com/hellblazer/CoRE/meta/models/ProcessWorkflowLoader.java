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

	public Product deploySystem;
	public Product createSystem;
	public Product createCluster;
	public Product createProcess;
	public Product startProcess;

	public Product ANY_PROCESS;
	public Product proc73;
	public Product DISTRIBUTED_SYSTEM;
	public Product ds1;
	public Product CLUSTER;
	public Product cluster1;

	public StatusCode unset;
	public StatusCode started;
	public StatusCode failed;
	public StatusCode completed;

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

		deploySystem = new Product("Any Deploy Service", "Any deploy service",
				core);
		em.persist(deploySystem);
		createSystem = new Product("Create System",
				"The service that creates a distributed system", core);
		em.persist(createSystem);
		createCluster = new Product("Create Cluster",
				"The service that creates a cluster of lxcs", core);
		em.persist(createCluster);
		createProcess = new Product("Create Process",
				"The service to create a process", core);
		em.persist(createProcess);
		startProcess = new Product("Start Process",
				"The service to start a process", core);
		em.persist(startProcess);

		ANY_PROCESS = new Product("Any Process", "Any process", core);
		em.persist(ANY_PROCESS);
		proc73 = new Product("Process 73", "Process 73", core);
		em.persist(proc73);

		DISTRIBUTED_SYSTEM = new Product("Distributed System",
				"Distributed system", core);
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
		started = new StatusCode("started", "started", core);
		em.persist(started);
		failed = new StatusCode("failed", "failed", core);
		em.persist(failed);
		completed = new StatusCode("completed", "completed", core);
		em.persist(completed);
	}

	public void createStatusCodeSequencing() {
		List<Pair<StatusCode, StatusCode>> pairs = new LinkedList<Pair<StatusCode, StatusCode>>();

		pairs.add(Pair.of(unset, started));
		pairs.add(Pair.of(started, completed));
		pairs.add(Pair.of(started, failed));
		model.getJobModel().createStatusCodeSequencings(deploySystem, pairs,
				10, core);
		model.getJobModel().createStatusCodeSequencings(createProcess, pairs,
				20, core);
		model.getJobModel().createStatusCodeSequencings(createSystem, pairs,
				30, core);
		model.getJobModel().createStatusCodeSequencings(createCluster, pairs,
				40, core);
		model.getJobModel().createStatusCodeSequencings(startProcess, pairs,
				50, core);

	}

	public void createStatusCodeSequencingAuthorizations() {
	}

	public void createRelationships() {
		instanceOf = new Relationship("instance of", "A is an instance of B",
				core);
		em.persist(instanceOf);
		classOf = new Relationship("class of", "A is the class of B", core);
		classOf.setInverse(instanceOf);
		em.persist(classOf);

		instanceOf.setInverse(classOf);
		em.persist(instanceOf);
	}

	public void createProtocols() {
		Protocol startProcessProtocol = new Protocol(deploySystem, core,
				sameProduct, anyLocation, anyLocation, DEPLOYER_ANY,
				startProcess, DISTRIBUTED_SYSTEM, false, core);
		em.persist(startProcessProtocol);
		Protocol createProcessProtocol = new Protocol(startProcess, core,
				sameProduct, anyLocation, anyLocation, DEPLOYER_ANY,
				createProcess, DISTRIBUTED_SYSTEM, false, core);
		em.persist(createProcessProtocol);
		Protocol createClusterProtocol = new Protocol(createProcess, core,
				sameProduct, anyLocation, anyLocation, DEPLOYER_ANY,
				createCluster, DISTRIBUTED_SYSTEM, false, core);
		em.persist(createClusterProtocol);
		Protocol createSystemProtocol = new Protocol(createCluster, core,
				sameProduct, anyLocation, anyLocation, DEPLOYER_ANY,
				createSystem, DISTRIBUTED_SYSTEM, false, core);
		em.persist(createSystemProtocol);
	}

	public void load() {
		createAgencies();
		// createAttributes();
		createProducts();
		// createServices();
		createLocations();
		createRelationships();
		// createNetworkInferences();
		// createProductNetworks();
		createAgencyNetworks();
		createLocationNetworks();
		createProtocols();
		// createMetaProtocols();
		createStatusCodes();
		createStatusCodeSequencing();
		// createProductSequencingAuthorizations();
	}
}
