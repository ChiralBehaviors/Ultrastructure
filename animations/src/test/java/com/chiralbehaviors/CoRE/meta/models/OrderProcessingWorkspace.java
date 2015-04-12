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

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.DatabaseBackedWorkspace;
import com.chiralbehaviors.CoRE.meta.workspace.Workspace;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 *
 */
public class OrderProcessingWorkspace {
    protected StatusCode   abandoned;
    protected Product      abc486;
    protected StatusCode   active;
    protected Agency       anyAgency;
    protected Location     anyLocation;
    protected Product      anyProduct;
    protected Relationship anyRelationship;
    protected Relationship area;
    protected Relationship areaOf;
    protected StatusCode   available;
    protected Location     bht37;
    protected Agency       billingComputer;
    protected Location     bin1;
    protected Location     bin15;
    protected Agency       cafleurBon;
    protected Product      checkCredit;
    protected Product      checkLetterOfCredit;
    protected Product      chemB;
    protected Relationship city;
    protected Relationship cityOf;
    protected StatusCode   completed;
    protected Agency       core;
    protected Agency       cpu;
    protected Agency       creditDept;
    protected Relationship customerType;
    protected Relationship customerTypeOf;
    protected Location     dc;
    protected Product      deliver;
    protected Product      discount;
    protected Attribute    discountAttribute;
    protected Location     east_coast;
    protected Location     euro;
    protected Agency       exempt;
    protected Agency       externalCust;
    protected Location     factory1;
    protected Agency       factory1Agency;
    protected StatusCode   failure;
    protected Product      fee;
    protected Location     france;
    protected Product      frozen;
    protected Agency       georgeTownUniversity;
    protected Agency       manufacturer;
    protected Product      nonExempt;
    protected Agency       nonExemptAgency;
    protected Relationship notApplicableRelationship;
    protected Product      orderEntryWorkspace;
    protected Agency       orderFullfillment;
    protected Agency       orgA;
    protected Location     paris;
    protected Product      pick;
    protected StatusCode   pickCompleted;
    protected Attribute    priceAttribute;
    protected Product      printCustomsDeclaration;
    protected Product      printPurchaseOrder;
    protected Location     rc31;
    protected Relationship region;
    protected Relationship regionOf;
    protected Product      roomTemp;
    protected Location     rsb225;
    protected Product      salesTax;
    protected Relationship salesTaxStatus;
    protected Relationship salesTaxStatusOf;
    protected Product      sameProduct;
    protected Relationship sameRelationship;
    protected Product      ship;
    protected Relationship state;
    protected Relationship stateOf;
    protected Relationship storageType;
    protected Relationship storageTypeOf;
    protected Relationship street;
    protected Relationship streetOf;
    protected Attribute    taxRateAttribute;
    protected StatusCode   unset;
    protected Location     us;
    protected StatusCode   waitingOnCreditCheck;
    protected StatusCode   waitingOnFee;
    protected StatusCode   waitingOnPricing;
    protected StatusCode   waitingOnPurchaseOrder;

    public OrderProcessingWorkspace() {
        super();
    }

    public Workspace createWorkspace(Model model)
                                                 throws IllegalArgumentException,
                                                 IllegalAccessException {
        DatabaseBackedWorkspace workspace = new DatabaseBackedWorkspace(
                                                                        orderEntryWorkspace,
                                                                        model);
        for (Field field : OrderProcessingWorkspace.class.getDeclaredFields()) {
            ExistentialRuleform<?, ?> extRuleform = (ExistentialRuleform<?, ?>) field.get(this);
            workspace.put(extRuleform.getName(), extRuleform);
        }
        return workspace;
    }
}