/**
 * Copyright (C) 2014 Hal Hildebrand. All rights reserved.
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

import org.junit.Test;

import com.hellblazer.CoRE.test.DatabaseTest;

/**
 * @author hhildebrand
 * 
 */
public class ProtocolTest extends DatabaseTest {

    @Test
    public void testProtocolAttributes() {
//        beginTransaction();
//
//        Agency core = new Agency("CoRE");
//        core.setUpdatedBy(core);
//        em.persist(core);
//
//        Attribute priceAttribute = new Attribute("price", "price", core,
//                                                 ValueType.NUMERIC);
//        em.persist(priceAttribute);
//
//        Product fee = new Product("Fee", "Compute fee", core);
//        em.persist(fee);
//
//        Product printPurchaseOrder = new Product("PrintPurchaseOrder",
//                                                 "Print the purchase order",
//                                                 core);
//        em.persist(printPurchaseOrder);
//
//        Agency billingComputer = new Agency("Billing CPU",
//                                            "The Billing Computer", core);
//        em.persist(billingComputer);
//
//        Agency externalCust = new Agency("Ext Customer",
//                                         "External (Paying) Customer", core);
//        em.persist(externalCust);
//
//        Location us = new Location("US", "U.S. Locations", core);
//        em.persist(us);
//
//        Product abc486 = new Product("ABC486", "Laptop Computer", core);
//        em.persist(abc486);
//
//        Protocol feeProtocol = new Protocol(printPurchaseOrder, externalCust,
//                                            abc486, us, us, billingComputer,
//                                            fee, abc486, core);
//        em.persist(feeProtocol);
//
//        ProtocolAttribute price = new ProtocolAttribute(priceAttribute, core);
//        price.setNumericValue(1500);
//        price.setProtocol(feeProtocol);
//        em.persist(price);
//        commitTransaction();
//        beginTransaction();
//        price = em.find(ProtocolAttribute.class, price.getId());
//        StatusCode available = new StatusCode(
//                                              "Available",
//                                              "The job is available for execution",
//                                              core);
//        em.persist(available);
//
//        Job job = new Job(core, billingComputer, printPurchaseOrder, abc486,
//                          us, us, externalCust);
//        job.setStatus(available);
//
//        em.persist(job);
//
//        JobAttribute attribute = price.createJobAttribute();
//        attribute.setUpdatedBy(core);
//        attribute.setJob(job);
//        em.persist(attribute);
//        commitTransaction();
    }
}
