/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.meta.models;

import java.lang.reflect.Field;

import javax.persistence.EntityManager;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 *
 */
public class OrderProcessingWorkspace {
    public Relationship area;
    public Relationship areaOf;
    public Relationship city;
    public Relationship cityOf;
    public Relationship customerType;
    public Relationship customerTypeOf;
    public Relationship region;
    public Relationship regionOf;
    public Relationship state;
    public Relationship stateOf;
    public Relationship salesTaxStatus;
    public Relationship salesTaxStatusOf;
    public Relationship storageType;
    public Relationship storageTypeOf;
    public Relationship street;
    public Relationship streetOf;
    public Relationship notApplicableRelationship;
    public Relationship sameRelationship;
    public Relationship anyRelationship;
    public StatusCode   unset;
    public StatusCode   abandoned;
    public StatusCode   completed;
    public StatusCode   failure;
    public StatusCode   active;
    public StatusCode   available;
    public StatusCode   pickCompleted;
    public StatusCode   waitingOnFee;
    public StatusCode   waitingOnPricing;
    public StatusCode   waitingOnPurchaseOrder;
    public StatusCode   waitingOnCreditCheck;
    public Product      abc486;
    public Product      checkCredit;
    public Product      checkLetterOfCredit;
    public Product      chemB;
    public Product      deliver;
    public Product      discount;
    public Product      frozen;
    public Product      fee;
    public Product      printCustomsDeclaration;
    public Product      printPurchaseOrder;
    public Product      roomTemp;
    public Product      pick;
    public Product      salesTax;
    public Product      ship;
    public Product      nonExempt;
    public Product      anyProduct;
    public Product      sameProduct;
    public Location     bht378;
    public Location     bin1;
    public Location     bin15;
    public Location     dc;
    public Location     east_coast;
    public Location     euro;
    public Location     france;
    public Location     paris;
    public Location     rc31;
    public Location     rsb225;
    public Location     factory1;
    public Location     us;
    public Location     anyLocation;
    public Agency       billingComputer;
    public Agency       cafleurBon;
    public Agency       core;
    public Agency       cpu;
    public Agency       creditDept;
    public Agency       exempt;
    public Agency       externalCust;
    public Agency       factory1Agency;
    public Agency       georgeTownUniversity;
    public Agency       manufacturer;
    public Agency       nonExemptAgency;
    public Agency       orderFullfillment;
    public Agency       orgA;
    public Agency       anyAgency;
    public Attribute    priceAttribute;
    public Attribute    taxRateAttribute;
    public Attribute    discountAttribute;

    public OrderProcessingWorkspace() {
        super();
    }

    public void merge(EntityManager em) throws IllegalArgumentException,
                                       IllegalAccessException {
        for (Field field : OrderProcessingWorkspace.class.getDeclaredFields()) {
            System.out.println(String.format("Merging: %s", field.getName()));
            em.persist(field.get(this));
        }
    }
}