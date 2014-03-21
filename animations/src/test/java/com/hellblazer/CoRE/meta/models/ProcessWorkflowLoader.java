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

import javax.persistence.EntityManager;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.event.status.StatusCode;
import com.hellblazer.CoRE.event.status.StatusCodeSequencing;
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
@SuppressWarnings("unused")
public class ProcessWorkflowLoader {

    public Relationship   partOf;
    public Relationship   hasPart;

    public Agency         EC2_API;
    public Agency         DEPLOYER_ANY;

    public Product        DEPLOY_SERVICE;

    public Product        ANY_PROCESS;
    public Product        proc73;

    public StatusCode     deployed;
    public StatusCode     started;
    public StatusCode     failed;
    public StatusCode     destroyed;

    public Location       DATA_CENTER;
    public Location       dc421;
    public Location       CONTAINER;
    public Location       c192_67_78_48;

    private ModelImpl     model;
    private Kernel        kernel;
    private Agency        core;
    private Product       sameProduct;
    private Product       anyProduct;
    private Agency        anyAgency;
    private Location      anyLocation;
    private Relationship  sameRelationship;
    private Relationship  anyRelationship;
    private Relationship  notApplicableRelationship;
    private Relationship  isA;
    private StatusCode    unset;
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
                                  "Any agency that deploys configurations",
                                  core);
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

        ANY_PROCESS = new Product("Any Process", "Any process", core);
        em.persist(ANY_PROCESS);
        proc73 = new Product("Process 73", "Process 73", core);
        em.persist(proc73);
    }

    public void createProductNetworks() {
        ProductModel pm = model.getProductModel();
        pm.link(proc73, isA, ANY_PROCESS, core);
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
                                     "The container located at 192.168.78.48",
                                     core);
        em.persist(c192_67_78_48);
    }

    public void createLocationNetworks() {
        LocationModel lm = model.getLocationModel();
        lm.link(dc421, isA, DATA_CENTER, core);
        lm.link(c192_67_78_48, isA, CONTAINER, core);
    }

    public void createStatusCodes() {
        deployed = new StatusCode("Deployed", "Deployed status", core);
        em.persist(deployed);
        started = new StatusCode("Started", "Started status", core);
        em.persist(started);
        failed = new StatusCode("Failed", "Failed status", core);
        em.persist(failed);
        destroyed = new StatusCode("Destroyed", "destroyed status", core);
        em.persist(destroyed);
    }

    public void createStatusCodeSequencing() {
        StatusCode[] codes = new StatusCode[] { deployed, started, failed,
                destroyed };
        int seqNum = model.getJobModel().createStatusCodeChain(ANY_PROCESS,
                                                               codes, 1, core);
        StatusCodeSequencing failed_started = new StatusCodeSequencing(
                                                                       ANY_PROCESS,
                                                                       failed,
                                                                       started,
                                                                       ++seqNum,
                                                                       core);
        em.persist(failed_started);
        StatusCodeSequencing started_destroyed = new StatusCodeSequencing(
                                                                          ANY_PROCESS,
                                                                          started,
                                                                          destroyed,
                                                                          ++seqNum,
                                                                          core);
        em.persist(started_destroyed);
    }

    public void createProtocols() {
        //deploy a cluster - use job attribute for cardinality
        //deploy a process to a cluster - no cardinality because everything in the cluster has the same config
    }

}
