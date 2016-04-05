/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.meta.models;

import java.lang.reflect.Field;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Location;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.domain.StatusCode;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;

/**
 * @author hhildebrand
 *
 */
public class OrderProcessingWorkspace {
    protected StatusCode   abandoned;
    protected Product      abc486;
    protected StatusCode   active;
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
    protected Product      ship;
    protected Relationship state;
    protected Relationship stateOf;
    protected Relationship storageType;
    protected Relationship storageTypeOf;
    protected Relationship street;
    protected Relationship streetOf;
    protected Attribute    taxRateAttribute;
    protected Location     us;
    protected StatusCode   waitingOnCreditCheck;
    protected StatusCode   waitingOnFee;
    protected StatusCode   waitingOnPricing;
    protected StatusCode   waitingOnPurchaseOrder;

    public OrderProcessingWorkspace() {
        super();
    }

    public WorkspaceAccessor createWorkspace(Model model) throws IllegalArgumentException,
                                                          IllegalAccessException {
        EditableWorkspace workspace = (EditableWorkspace) model.getWorkspaceModel()
                                                               .getScoped(orderEntryWorkspace)
                                                               .getWorkspace();
        for (Field field : OrderProcessingWorkspace.class.getDeclaredFields()) {
            ExistentialRuleform extRuleform = (ExistentialRuleform) field.get(this);
            workspace.put(extRuleform.getName(),
                          (ExistentialRecord) extRuleform);
        }
        return model.getWorkspaceModel()
                    .getScoped(orderEntryWorkspace)
                    .getWorkspace();
    }
}