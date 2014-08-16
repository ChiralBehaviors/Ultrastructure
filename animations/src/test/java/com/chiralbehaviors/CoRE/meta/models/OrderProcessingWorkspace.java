/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import com.chiralbehaviors.CoRE.Ruleform;
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

        Map<Ruleform, Ruleform> translated = new HashMap<>();
        for (Field field : OrderProcessingWorkspace.class.getDeclaredFields()) {
            System.out.println(String.format("Merging: %s", field.getName()));
            field.set(this,
                      ((Ruleform) field.get(this)).manageEntity(em, translated));
        }
    }
}