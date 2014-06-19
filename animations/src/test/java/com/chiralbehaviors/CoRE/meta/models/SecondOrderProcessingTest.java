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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.chiralbehaviors.CoRE.event.Job;
import com.chiralbehaviors.CoRE.event.MetaProtocol;
import com.chiralbehaviors.CoRE.event.ProductSiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.Protocol;

/**
 * @author hparry
 * 
 */
public class SecondOrderProcessingTest extends AbstractModelTest {

    private static OrderProcessingLoader w;

    @BeforeClass
    public static void initialize() throws Exception {
        em.getTransaction().begin();
        w = new OrderProcessingLoader(em);
        w.createAgencys();
        w.createAttributes();
        w.createLocations();
        w.createProducts();
        w.createServices();
        w.createRelationships();
        w.createNetworkInferences();
        w.createStatusCodes();
        w.createAgencyNetworks();
        w.createLocationNetworks();
        w.createProductNetworks();
        w.createStatusCodeSequencing();
        createProtocols();
        createMetaProtocols();
        createSequencingAuthorizations();
        em.getTransaction().commit();

    }

    /**
     * 
     */
    private static void createMetaProtocols() {
        //        Product service, int sequenceNumber,
        //        Relationship productOrdered,
        //        Relationship requestingAgency,
        //        Relationship serviceType, Relationship deliverTo,
        //        Relationship deliverFrom, Agency updatedBy

        //create pick and ship jobs
        MetaProtocol mp1 = new MetaProtocol(w.deliver, 1, w.anyRelationship,
                                            kernel.getAnyRelationship(),
                                            w.anyRelationship,
                                            kernel.getAnyRelationship(),
                                            w.sameRelationship, kernel.getAnyRelationship(), w.anyRelationship, kernel.getAnyRelationship(), w.anyRelationship, kernel.getAnyRelationship(), w.core);
        em.persist(mp1);

        //if in US, check credit, if EU, check LOC
        MetaProtocol mp2 = new MetaProtocol(w.pick, 2, w.sameRelationship,
                                            kernel.getAnyRelationship(), w.customerType,
                                            kernel.getAnyRelationship(), w.sameRelationship, kernel.getAnyRelationship(), w.area, kernel.getAnyRelationship(), w.area, kernel.getAnyRelationship(), w.core);
        em.persist(mp2);

        //print purchase order and customs declaration, if applicable
        MetaProtocol mp3 = new MetaProtocol(w.ship, 3, w.sameRelationship,
                                            kernel.getAnyRelationship(), w.customerType,
                                            kernel.getAnyRelationship(), w.sameRelationship, kernel.getAnyRelationship(), w.area, kernel.getAnyRelationship(), w.area, kernel.getAnyRelationship(), w.core);
        em.persist(mp3);

        //generate fee for georgetown
        MetaProtocol mp4 = new MetaProtocol(w.printPurchaseOrder, 4,
                                            w.sameRelationship,
                                            kernel.getAnyRelationship(),
                                            w.sameRelationship, kernel.getAnyRelationship(), w.sameRelationship,
                                            kernel.getAnyRelationship(), w.city, kernel.getAnyRelationship(), w.area, kernel.getAnyRelationship(), w.core);
        mp4.setStopOnMatch(true);
        em.persist(mp4);

        //generate fee for everyone else
        MetaProtocol mp5 = new MetaProtocol(w.printPurchaseOrder, 5,
                                            w.sameRelationship, kernel.getAnyRelationship(),
                                            w.customerType, kernel.getAnyRelationship(),
                                            w.sameRelationship, kernel.getAnyRelationship(), w.area, kernel.getAnyRelationship(), w.anyRelationship, kernel.getAnyRelationship(), w.core);
        em.persist(mp5);

        //create sales tax
        MetaProtocol mp6 = new MetaProtocol(w.fee, 6, w.anyRelationship,
                                            kernel.getAnyRelationship(), w.customerType,
                                            kernel.getAnyRelationship(), w.sameRelationship, kernel.getAnyRelationship(), w.region, kernel.getAnyRelationship(), w.anyRelationship, kernel.getAnyRelationship(), w.core);
        em.persist(mp6);

    }

    /**
     * 
     */
    private static void createProtocols() {
        //        Product requestedService, Agency requester,
        //        Product requestedProduct, Location deliverTo,
        //        Location deliverFrom, Agency assignTo, Product service,
        //        Product product, Agency updatedBy

        Protocol checkCreditProtocol = new Protocol(w.pick, w.externalCust,
                                                    kernel.getAnyAttribute(), w.anyProduct, w.us,
                                                    kernel.getAnyAttribute(), w.us,
                                                    kernel.getAnyAttribute(), w.cpu, kernel.getAnyAttribute(), w.checkCredit, kernel.getAnyAttribute(), w.anyProduct, kernel.getAnyAttribute(), w.core);
        em.persist(checkCreditProtocol);

        Protocol checkLetterOfCreditProtocol = new Protocol(
                                                            w.pick,
                                                            w.externalCust,
                                                            kernel.getAnyAttribute(),
                                                            w.anyProduct,
                                                            w.euro,
                                                            kernel.getAnyAttribute(),
                                                            w.us,
                                                            kernel.getAnyAttribute(),
                                                            w.creditDept, kernel.getAnyAttribute(), w.checkLetterOfCredit, kernel.getAnyAttribute(), w.anyProduct, kernel.getAnyAttribute(), w.core);
        em.persist(checkLetterOfCreditProtocol);

        Protocol printPurchaseOrder = new Protocol(w.ship, w.externalCust,
                                                   kernel.getAnyAttribute(), w.abc486,
                                                   w.anyLocation, kernel.getAnyAttribute(),
                                                   w.anyLocation,
                                                   kernel.getAnyAttribute(), w.creditDept, kernel.getAnyAttribute(), w.printPurchaseOrder, kernel.getAnyAttribute(), w.anyProduct, kernel.getAnyAttribute(), w.core);
        em.persist(printPurchaseOrder);

        Protocol printCustomsDecl = new Protocol(w.ship, w.externalCust,
                                                 kernel.getAnyAttribute(), w.abc486, w.euro,
                                                 kernel.getAnyAttribute(),
                                                 w.us,
                                                 kernel.getAnyAttribute(), w.creditDept, kernel.getAnyAttribute(), w.printCustomsDeclaration, kernel.getAnyAttribute(), w.anyProduct, kernel.getAnyAttribute(), w.core);
        em.persist(printCustomsDecl);

        Protocol pickProtocol = new Protocol(w.deliver, w.anyAgency,
                                             kernel.getAnyAttribute(), w.anyProduct,
                                             w.anyLocation, kernel.getAnyAttribute(),
                                             w.anyLocation, kernel.getAnyAttribute(), w.factory1Agency, kernel.getAnyAttribute(), w.pick, kernel.getAnyAttribute(), w.anyProduct, kernel.getAnyAttribute(), w.core);
        em.persist(pickProtocol);

        Protocol shipProtocol = new Protocol(w.deliver, w.anyAgency,
                                             kernel.getAnyAttribute(), w.anyProduct,
                                             w.anyLocation, kernel.getAnyAttribute(),
                                             w.anyLocation, kernel.getAnyAttribute(), w.factory1Agency, kernel.getAnyAttribute(), w.ship, kernel.getAnyAttribute(), w.anyProduct, kernel.getAnyAttribute(), w.core);
        em.persist(shipProtocol);

        Protocol feeProtocol = new Protocol(w.printPurchaseOrder,
                                            w.externalCust, kernel.getAnyAttribute(),
                                            w.abc486, w.anyLocation,
                                            kernel.getAnyAttribute(), w.us,
                                            kernel.getAnyAttribute(), w.billingComputer, kernel.getAnyAttribute(), w.fee, kernel.getAnyAttribute(), w.anyProduct, kernel.getAnyAttribute(), w.core);
        em.persist(feeProtocol);

        Protocol nonExemptProtocol = new Protocol(w.fee, w.nonExemptAgency,
                                                  kernel.getAnyAttribute(), w.nonExempt,
                                                  w.dc,
                                                  kernel.getAnyAttribute(),
                                                  w.anyLocation, kernel.getAnyAttribute(),
                                                  w.billingComputer, kernel.getAnyAttribute(), w.salesTax, kernel.getAnyAttribute(), w.nonExempt, kernel.getAnyAttribute(), w.core);
        em.persist(nonExemptProtocol);

        Protocol discountProtocol = new Protocol(w.fee, w.externalCust,
                                                 kernel.getAnyAttribute(), w.abc486, w.euro,
                                                 kernel.getAnyAttribute(), w.us,
                                                 kernel.getAnyAttribute(), w.billingComputer, kernel.getAnyAttribute(), w.discount, kernel.getAnyAttribute(), w.abc486, kernel.getAnyAttribute(), w.core);
        em.persist(discountProtocol);

        Protocol georgetownFeeProtocol = new Protocol(w.printPurchaseOrder,
                                                      w.georgeTownUniversity,
                                                      kernel.getAnyAttribute(), w.abc486, w.dc,
                                                      kernel.getAnyAttribute(), w.us,
                                                      kernel.getAnyAttribute(), w.billingComputer, kernel.getAnyAttribute(), w.fee, kernel.getAnyAttribute(), w.abc486, kernel.getAnyAttribute(), w.core);
        em.persist(georgetownFeeProtocol);

    }

    /**
     * 
     */
    private static void createSequencingAuthorizations() {
        ProductSiblingSequencingAuthorization pickToShip = new ProductSiblingSequencingAuthorization(
                                                                                                     w.pick,
                                                                                                     w.completed,
                                                                                                     w.ship,
                                                                                                     w.available,
                                                                                                     w.core);
        em.persist(pickToShip);
    }

    @Test
    public void testCreateGeorgetownWorkflow() {
        em.getTransaction().begin();
        //        Agency assignTo, Agency requester, Product service,
        //        Product product, Location deliverTo, Location deliverFrom,
        //        Agency updatedBy
        Job job = new Job(w.georgeTownUniversity, kernel.getAnyAttribute(),
                          w.deliver, kernel.getAnyAttribute(), w.abc486, kernel.getAnyAttribute(), w.rsb225, kernel.getAnyAttribute(), w.factory1, kernel.getAnyAttribute(), w.georgeTownUniversity, kernel.getAnyAttribute(), w.core);
        job.setStatus(w.unset);
        em.persist(job);

        em.getTransaction().commit();
        List<Job> jobs = model.getJobModel().getAllChildren(job);
        assertEquals(5, jobs.size());
        boolean hasCorrectService = false;
        for (Job j : jobs) {
            if (j.getService().equals(w.fee)) {
                hasCorrectService = true;
            }
        }
        assertTrue("Did not find fee service in US job tree", hasCorrectService);
    }

    @Test
    public void testFirstSeqAuth() {
        em.getTransaction().begin();
        Job job = new Job(w.georgeTownUniversity, kernel.getAnyAttribute(),
                          w.deliver, kernel.getAnyAttribute(), w.abc486, kernel.getAnyAttribute(), w.rsb225, kernel.getAnyAttribute(), w.factory1, kernel.getAnyAttribute(), w.georgeTownUniversity, kernel.getAnyAttribute(), w.core);
        job.setStatus(w.unset);
        em.persist(job);

        em.getTransaction().commit();

        em.getTransaction().begin();
        Job pick = model.getJobModel().getChildJobsByService(job, w.pick).get(0);

        //delete all child jobs so that we can move pick into a terminal state
        List<Job> childJobs = model.getJobModel().getAllChildren(pick);
        for (Job j : childJobs) {
            em.remove(j);
        }
        em.getTransaction().commit();
        em.getTransaction().begin();
        pick.setStatus(w.available);
        em.getTransaction().commit();
        em.getTransaction().begin();
        pick.setStatus(w.active);
        em.getTransaction().commit();
        em.getTransaction().begin();
        pick.setStatus(w.completed);
        em.getTransaction().commit();

        Job ship = model.getJobModel().getChildJobsByService(job, w.ship).get(0);
        assertEquals(w.waitingOnPurchaseOrder, ship.getStatus());
    }

    @Test
    public void testShipToEU() {
        em.getTransaction().begin();
        //        Agency assignTo, Agency requester, Product service,
        //        Product product, Location deliverTo, Location deliverFrom,
        //        Agency updatedBy
        Job job = new Job(w.factory1Agency, kernel.getAnyAttribute(), w.deliver, kernel.getAnyAttribute(),
                          w.abc486, kernel.getAnyAttribute(), w.paris, kernel.getAnyAttribute(), w.factory1, kernel.getAnyAttribute(), w.cafleurBon, kernel.getAnyAttribute(), w.core);
        job.setStatus(w.unset);
        em.persist(job);

        em.getTransaction().commit();
        List<Job> jobs = model.getJobModel().getAllChildren(job);
        assertEquals(5, jobs.size());
        boolean hasCorrectService = false;
        for (Job j : jobs) {
            if (j.getService().equals(w.checkLetterOfCredit)) {
                hasCorrectService = true;
            }
        }
        assertTrue("Did not find checkLetterOfCredit service in EU job tree",
                   hasCorrectService);
    }

}
