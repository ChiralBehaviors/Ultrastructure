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

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.DatabaseBackedWorkspace;
import com.chiralbehaviors.CoRE.workspace.Workspace;

/**
 * @author hhildebrand
 *
 */
public class OrderProcessingWorkspace {
    protected Relationship area;
    protected Relationship areaOf;
    protected Relationship city;
    protected Relationship cityOf;
    protected Relationship customerType;
    protected Relationship customerTypeOf;
    protected Relationship region;
    protected Relationship regionOf;
    protected Relationship state;
    protected Relationship stateOf;
    protected Relationship salesTaxStatus;
    protected Relationship salesTaxStatusOf;
    protected Relationship storageType;
    protected Relationship storageTypeOf;
    protected Relationship street;
    protected Relationship streetOf;
    protected Relationship notApplicableRelationship;
    protected Relationship sameRelationship;
    protected Relationship anyRelationship;
    protected StatusCode   unset;
    protected StatusCode   abandoned;
    protected StatusCode   completed;
    protected StatusCode   failure;
    protected StatusCode   active;
    protected StatusCode   available;
    protected StatusCode   pickCompleted;
    protected StatusCode   waitingOnFee;
    protected StatusCode   waitingOnPricing;
    protected StatusCode   waitingOnPurchaseOrder;
    protected StatusCode   waitingOnCreditCheck;
    protected Product      orderEntryWorkspace;
    protected Product      abc486;
    protected Product      checkCredit;
    protected Product      checkLetterOfCredit;
    protected Product      chemB;
    protected Product      deliver;
    protected Product      discount;
    protected Product      frozen;
    protected Product      fee;
    protected Product      printCustomsDeclaration;
    protected Product      printPurchaseOrder;
    protected Product      roomTemp;
    protected Product      pick;
    protected Product      salesTax;
    protected Product      ship;
    protected Product      nonExempt;
    protected Product      anyProduct;
    protected Product      sameProduct;
    protected Location     bht37;
    protected Location     bin1;
    protected Location     bin15;
    protected Location     dc;
    protected Location     east_coast;
    protected Location     euro;
    protected Location     france;
    protected Location     paris;
    protected Location     rc31;
    protected Location     rsb225;
    protected Location     factory1;
    protected Location     us;
    protected Location     anyLocation;
    protected Agency       billingComputer;
    protected Agency       cafleurBon;
    protected Agency       core;
    protected Agency       cpu;
    protected Agency       creditDept;
    protected Agency       exempt;
    protected Agency       externalCust;
    protected Agency       factory1Agency;
    protected Agency       georgeTownUniversity;
    protected Agency       manufacturer;
    protected Agency       nonExemptAgency;
    protected Agency       orderFullfillment;
    protected Agency       orgA;
    protected Agency       anyAgency;
    protected Attribute    priceAttribute;
    protected Attribute    taxRateAttribute;
    protected Attribute    discountAttribute;

    public OrderProcessingWorkspace() {
        super();
    }

    public Workspace createWorkspace(EntityManager em)
                                                      throws IllegalArgumentException,
                                                      IllegalAccessException {
        DatabaseBackedWorkspace workspace = new DatabaseBackedWorkspace(
                                                                        orderEntryWorkspace,
                                                                        em);
        for (Field field : OrderProcessingWorkspace.class.getDeclaredFields()) {
            ExistentialRuleform<?, ?> extRuleform = (ExistentialRuleform<?, ?>) field.get(this);
            workspace.put(extRuleform.getName(), extRuleform);
        }
        return workspace;
    }
}