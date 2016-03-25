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

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Location;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.enums.ValueType;
import com.chiralbehaviors.CoRE.jooq.tables.records.ChildSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.MetaProtocolRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.NetworkInferenceRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ParentSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ProtocolRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.SiblingSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.StatusCodeSequencingRecord;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.meta.Model;

/**
 * @author hhildebrand
 *
 */
public class OrderProcessingLoader extends OrderProcessingWorkspace {

    private Agency       anyAgency;
    private Location     anyLocation;
    private Product      anyProduct;
    private Relationship anyRelationship;
    private final Kernel kernel;
    private final Model  model;
    private Product      sameProduct;

    public OrderProcessingLoader(Model model) throws Exception {
        this.model = model;
        kernel = model.getKernel();
        sameProduct = kernel.getSameProduct();
        anyProduct = kernel.getAnyProduct();
        anyAgency = kernel.getAnyAgency();
        anyLocation = kernel.getAnyLocation();
        anyRelationship = kernel.getAnyRelationship();
    }

    public void createAgencyNetworks() {
        model.getPhantasmModel()
             .link(georgeTownUniversity, customerType, externalCust);
        model.getPhantasmModel()
             .link(georgeTownUniversity, salesTaxStatus, exempt);
        model.getPhantasmModel()
             .link(orgA, customerType, externalCust);
        model.getPhantasmModel()
             .link(cafleurBon, customerType, externalCust);
        model.getPhantasmModel()
             .link(orgA, salesTaxStatus, nonExemptAgency);
    }

    public void createAgencys() {
        billingComputer = model.records()
                               .newAgency("BillingComputer",
                                          "The Billing Computer");
        billingComputer.insert();

        cpu = model.records()
                   .newAgency("CPU", "Computer");
        cpu.insert();

        creditDept = model.records()
                          .newAgency("Credit", "Credit Department");
        creditDept.insert();

        exempt = model.records()
                      .newAgency("Exempt", "Exempt from sales taxes");
        exempt.insert();

        externalCust = model.records()
                            .newAgency("Ext Customer",
                                       "External (Paying) Customer");
        externalCust.insert();

        factory1Agency = model.records()
                              .newAgency("Factory1Agency", "Factory #1");
        factory1Agency.insert();

        georgeTownUniversity = model.records()
                                    .newAgency("GeorgetownUniversity",
                                               "Georgetown University");
        georgeTownUniversity.insert();

        cafleurBon = model.records()
                          .newAgency("CarfleurBon", "Carfleur Bon");
        cafleurBon.insert();

        manufacturer = model.records()
                            .newAgency("MNFR", "Manufacturer");
        manufacturer.insert();

        nonExemptAgency = model.records()
                               .newAgency("NonExemptAgency",
                                          "Subject to sales taxes");
        nonExemptAgency.insert();

        orgA = model.records()
                    .newAgency("OrgA", "Organization A");
        orgA.insert();

        orderFullfillment = model.records()
                                 .newAgency("OrderFullfillment",
                                            "Order Fullfillment");
        orderFullfillment.insert();
    }

    public void createAttributes() {
        priceAttribute = model.records()
                              .newAttribute("price", "price",
                                            ValueType.Integer);
        priceAttribute.insert();

        taxRateAttribute = model.records()
                                .newAttribute("tax rate", "tax rate",
                                              ValueType.Integer);
        taxRateAttribute.insert();

        discountAttribute = model.records()
                                 .newAttribute("discount", "discount",
                                               ValueType.Integer);
        discountAttribute.insert();
    }

    public void createLocationNetworks() {
        model.getPhantasmModel()
             .link(bin1, area, factory1);
        model.getPhantasmModel()
             .link(bin15, area, factory1);
        model.getPhantasmModel()
             .link(factory1, street, bht37);
        model.getPhantasmModel()
             .link(rsb225, city, dc);
        model.getPhantasmModel()
             .link(bht37, city, dc);
        model.getPhantasmModel()
             .link(rc31, city, paris);
        model.getPhantasmModel()
             .link(dc, region, east_coast);
        model.getPhantasmModel()
             .link(east_coast, area, us);
        model.getPhantasmModel()
             .link(paris, region, france);
        model.getPhantasmModel()
             .link(france, area, euro);
    }

    public void createLocations() {
        rsb225 = model.records()
                      .newLocation("RSB225", "225 Reiss Science Bldg");
        rsb225.insert();
        bht37 = model.records()
                     .newLocation("BHT37", "37 Bret Harte Terrace");
        bht37.insert();

        rc31 = model.records()
                    .newLocation("RC31", "31 Rue Cambon");
        rc31.insert();

        bin1 = model.records()
                    .newLocation("BIN01", "Bin #1");
        bin1.insert();
        bin15 = model.records()
                     .newLocation("BIN15", "Bin #15");
        bin15.insert();
        dc = model.records()
                  .newLocation("DC", "District of Columbia");
        dc.insert();
        east_coast = model.records()
                          .newLocation("EAST_COAST", "East Coast");
        east_coast.insert();
        factory1 = model.records()
                        .newLocation("Factory1", "Factory 1");
        factory1.insert();
        france = model.records()
                      .newLocation("FRANCE", "France");
        france.insert();
        paris = model.records()
                     .newLocation("PARIS", "Paris");
        paris.insert();
        us = model.records()
                  .newLocation("US", "U.S. Locations");
        us.insert();
        euro = model.records()
                    .newLocation("Euro", "European locations");
        euro.insert();
    }

    public void createMetaProtocol() {
        MetaProtocolRecord m1 = model.getJobModel()
                                     .newInitializedMetaProtocol(deliver);
        m1.setSequenceNumber(1);
        m1.setProduct(anyRelationship.getId());
        m1.setDeliverTo(state.getId());
        m1.setDeliverFrom(area.getId());

        m1.update();

        MetaProtocolRecord m2 = model.getJobModel()
                                     .newInitializedMetaProtocol(pick);
        m2.setSequenceNumber(1);
        m2.setProduct(anyRelationship.getId());
        m2.setRequester(customerType.getId());
        m2.setDeliverTo(area.getId());
        m2.setDeliverFrom(area.getId());

        m2.update();

        MetaProtocolRecord m3 = model.getJobModel()
                                     .newInitializedMetaProtocol(ship);
        m3.setSequenceNumber(1);
        m3.setProduct(anyRelationship.getId());
        m3.setRequester(customerType.getId());
        m3.setDeliverTo(area.getId());
        m3.setDeliverFrom(area.getId());

        MetaProtocolRecord m5 = model.getJobModel()
                                     .newInitializedMetaProtocol(fee);
        m5.setSequenceNumber(1);
        m5.setProduct(anyRelationship.getId());
        m5.setRequester(salesTaxStatus.getId());
        m5.setDeliverTo(city.getId());

        m5.update();

        MetaProtocolRecord m6 = model.getJobModel()
                                     .newInitializedMetaProtocol(printPurchaseOrder);
        m6.setSequenceNumber(1);
        m6.setProduct(anyRelationship.getId());
        m6.setRequester(anyRelationship.getId());
        m6.setDeliverTo(anyRelationship.getId());
        m6.setDeliverFrom(area.getId());

        m6.update();
    }

    public void createNetworkNetworkInferences() {
        NetworkInferenceRecord areaToRegion = model.records()
                                                   .newNetworkNetworkInference(areaOf,
                                                                               regionOf,
                                                                               areaOf);
        areaToRegion.insert();

        NetworkInferenceRecord areaToState = model.records()
                                                  .newNetworkNetworkInference(areaOf,
                                                                              stateOf,
                                                                              areaOf);
        areaToState.insert();

        NetworkInferenceRecord areaToCity = model.records()
                                                 .newNetworkNetworkInference(areaOf,
                                                                             cityOf,
                                                                             areaOf);
        areaToCity.insert();

        NetworkInferenceRecord areaToStreet = model.records()
                                                   .newNetworkNetworkInference(areaOf,
                                                                               streetOf,
                                                                               areaOf);
        areaToStreet.insert();

        NetworkInferenceRecord regionToState = model.records()
                                                    .newNetworkNetworkInference(regionOf,
                                                                                stateOf,
                                                                                regionOf);
        regionToState.insert();

        NetworkInferenceRecord regionToCity = model.records()
                                                   .newNetworkNetworkInference(regionOf,
                                                                               cityOf,
                                                                               regionOf);
        regionToCity.insert();

        NetworkInferenceRecord regionToStreet = model.records()
                                                     .newNetworkNetworkInference(regionOf,
                                                                                 streetOf,
                                                                                 regionOf);
        regionToStreet.insert();

        NetworkInferenceRecord stateToCity = model.records()
                                                  .newNetworkNetworkInference(stateOf,
                                                                              cityOf,
                                                                              stateOf);
        stateToCity.insert();

        NetworkInferenceRecord stateToStreet = model.records()
                                                    .newNetworkNetworkInference(stateOf,
                                                                                streetOf,
                                                                                stateOf);
        stateToStreet.insert();

        NetworkInferenceRecord cityToStreet = model.records()
                                                   .newNetworkNetworkInference(cityOf,
                                                                               streetOf,
                                                                               cityOf);
        cityToStreet.insert();
    }

    public void createProductNetworks() {
        model.getPhantasmModel()
             .link(abc486, storageType, roomTemp);
        model.getPhantasmModel()
             .link(abc486, salesTaxStatus, nonExempt);
        model.getPhantasmModel()
             .link(chemB, storageType, frozen);
    }

    public void createProducts() {
        abc486 = model.records()
                      .newProduct("ABC486", "Laptop Computer");
        abc486.insert();
        frozen = model.records()
                      .newProduct("Frozen", "Frozen products");
        frozen.insert();
        nonExempt = model.records()
                         .newProduct("NonExempt", "Subject to sales tax");
        nonExempt.insert();
        chemB = model.records()
                     .newProduct("ChemB", "Chemical B");
        chemB.insert();
        roomTemp = model.records()
                        .newProduct("RoomTemp", "Room temperature products");
        roomTemp.insert();
        orderEntryWorkspace = model.records()
                                   .newProduct("defining product for order entry");
        orderEntryWorkspace.insert();
    }

    public void createProductSequencingAuthorizations() {

        SiblingSequencingAuthorizationRecord activatePrintCustomsDeclaration = model.records()
                                                                                    .newSiblingSequencingAuthorization();
        activatePrintCustomsDeclaration.setParent(printPurchaseOrder.getId());
        activatePrintCustomsDeclaration.setStatusCode(completed.getId());
        activatePrintCustomsDeclaration.setNextSibling(printCustomsDeclaration.getId());
        activatePrintCustomsDeclaration.setNextSiblingStatus(available.getId());
        activatePrintCustomsDeclaration.insert();

        ParentSequencingAuthorizationRecord productPicked = model.records()
                                                                 .newParentSequencingAuthorization();
        productPicked.setService(pick.getId());
        productPicked.setStatusCode(completed.getId());
        productPicked.setParent(deliver.getId());
        productPicked.setParentStatusToSet(completed.getId());
        productPicked.setSetIfActiveSiblings(false);
        productPicked.insert();

        ParentSequencingAuthorizationRecord checkCreditCompleted = model.records()
                                                                        .newParentSequencingAuthorization();
        checkCreditCompleted.setService(checkCredit.getId());
        checkCreditCompleted.setStatusCode(completed.getId());
        checkCreditCompleted.setParent(pick.getId());
        checkCreditCompleted.setParentStatusToSet(available.getId());
        checkCreditCompleted.insert();

        SiblingSequencingAuthorizationRecord activateShip = model.records()
                                                                 .newSiblingSequencingAuthorization();
        activateShip.setParent(pick.getId());
        activateShip.setStatusCode(completed.getId());
        activateShip.setNextSibling(ship.getId());
        activateShip.setNextSiblingStatus(waitingOnPurchaseOrder.getId());
        activateShip.insert();

        ParentSequencingAuthorizationRecord activateShipFromPrintCustomsDeclaration = model.records()
                                                                                           .newParentSequencingAuthorization();
        activateShipFromPrintCustomsDeclaration.setService(printCustomsDeclaration.getId());
        activateShipFromPrintCustomsDeclaration.setStatusCode(completed.getId());
        activateShipFromPrintCustomsDeclaration.setParent(ship.getId());
        activateShipFromPrintCustomsDeclaration.setParentStatusToSet(available.getId());
        activateShipFromPrintCustomsDeclaration.setSetIfActiveSiblings(false);
        activateShipFromPrintCustomsDeclaration.insert();

        ParentSequencingAuthorizationRecord activateShipFromPrintPurchaseOrder = model.records()
                                                                                      .newParentSequencingAuthorization();
        activateShipFromPrintPurchaseOrder.setService(printPurchaseOrder.getId());
        activateShipFromPrintPurchaseOrder.setStatusCode(completed.getId());
        activateShipFromPrintPurchaseOrder.setParent(ship.getId());
        activateShipFromPrintPurchaseOrder.setParentStatusToSet(available.getId());
        activateShipFromPrintPurchaseOrder.setSetIfActiveSiblings(false);
        activateShipFromPrintPurchaseOrder.insert();

        ChildSequencingAuthorizationRecord activatePrintPurchaseOrder = model.records()
                                                                             .newChildSequencingAuthorization();
        activatePrintPurchaseOrder.setParent(ship.getId());
        activatePrintPurchaseOrder.setStatusCode(waitingOnPurchaseOrder.getId());
        activatePrintPurchaseOrder.setNextChild(printPurchaseOrder.getId());
        activatePrintPurchaseOrder.setNextChildStatus(waitingOnFee.getId());
        activatePrintPurchaseOrder.insert();

        ChildSequencingAuthorizationRecord activateCreditCheck = model.records()
                                                                      .newChildSequencingAuthorization();
        activateCreditCheck.setParent(pick.getId());
        activateCreditCheck.setStatusCode(waitingOnCreditCheck.getId());
        activateCreditCheck.setNextChild(checkCredit.getId());
        activateCreditCheck.setNextChildStatus(available.getId());
        activateCreditCheck.insert();

        ChildSequencingAuthorizationRecord activateFee = model.records()
                                                              .newChildSequencingAuthorization();
        activateFee.setParent(printPurchaseOrder.getId());
        activateFee.setStatusCode(waitingOnFee.getId());
        activateFee.setNextChild(fee.getId());
        activateFee.setNextChildStatus(available.getId());
        activateFee.insert();

        ChildSequencingAuthorizationRecord activatePick = model.records()
                                                               .newChildSequencingAuthorization();
        activatePick.setParent(deliver.getId());
        activatePick.setStatusCode(available.getId());
        activatePick.setNextChild(pick.getId());
        activatePick.setNextChildStatus(waitingOnCreditCheck.getId());
        activatePick.insert();

        SiblingSequencingAuthorizationRecord activateDiscount = model.records()
                                                                     .newSiblingSequencingAuthorization();
        activateDiscount.setParent(fee.getId());
        activateDiscount.setStatusCode(completed.getId());
        activateDiscount.setNextSibling(discount.getId());
        activateDiscount.setNextSiblingStatus(available.getId());
        activateDiscount.insert();

        ParentSequencingAuthorizationRecord activatePrintPurchaseOrderFromFee = model.records()
                                                                                     .newParentSequencingAuthorization();
        activatePrintPurchaseOrderFromFee.setService(fee.getId());
        activatePrintPurchaseOrderFromFee.setStatusCode(completed.getId());
        activatePrintPurchaseOrderFromFee.setParent(printPurchaseOrder.getId());
        activatePrintPurchaseOrderFromFee.setParentStatusToSet(available.getId());
        activatePrintPurchaseOrderFromFee.setSetIfActiveSiblings(false);
        activatePrintPurchaseOrderFromFee.insert();

        ParentSequencingAuthorizationRecord activatePrintPurchaseOrderFromDiscount = model.records()
                                                                                          .newParentSequencingAuthorization();
        activatePrintPurchaseOrderFromDiscount.setService(discount.getId());
        activatePrintPurchaseOrderFromDiscount.setStatusCode(completed.getId());
        activatePrintPurchaseOrderFromDiscount.setParent(printPurchaseOrder.getId());
        activatePrintPurchaseOrderFromDiscount.setParentStatusToSet(available.getId());
        activatePrintPurchaseOrderFromDiscount.setSetIfActiveSiblings(false);
        activatePrintPurchaseOrderFromDiscount.insert();

        ParentSequencingAuthorizationRecord completeDeliverFromShip = model.records()
                                                                           .newParentSequencingAuthorization();
        completeDeliverFromShip.setService(ship.getId());
        completeDeliverFromShip.setStatusCode(completed.getId());
        completeDeliverFromShip.setParent(deliver.getId());
        completeDeliverFromShip.setParentStatusToSet(completed.getId());
        completeDeliverFromShip.setSetIfActiveSiblings(false);
        completeDeliverFromShip.insert();
    }

    public void createProtocols() {

        ProtocolRecord pickProtocol = model.getJobModel()
                                           .newInitializedProtocol(deliver);
        pickProtocol.setService(deliver.getId());
        pickProtocol.setRequester(anyAgency.getId());
        pickProtocol.setProduct(anyProduct.getId());
        pickProtocol.setDeliverTo(anyLocation.getId());
        pickProtocol.setDeliverFrom(anyLocation.getId());
        pickProtocol.setChildAssignTo(factory1Agency.getId());
        pickProtocol.setChildService(pick.getId());
        pickProtocol.setChildProduct(sameProduct.getId());
        pickProtocol.update();

        ProtocolRecord chkCreditProtocol = model.getJobModel()
                                                .newInitializedProtocol(pick);
        chkCreditProtocol.setService(pick.getId());
        chkCreditProtocol.setRequester(externalCust.getId());
        chkCreditProtocol.setProduct(anyProduct.getId());
        chkCreditProtocol.setDeliverTo(us.getId());
        chkCreditProtocol.setDeliverFrom(us.getId());
        chkCreditProtocol.setChildAssignTo(cpu.getId());
        chkCreditProtocol.setChildService(checkCredit.getId());
        chkCreditProtocol.setChildProduct(sameProduct.getId());
        chkCreditProtocol.update();

        ProtocolRecord chkLtrCrdtProtocol = model.getJobModel()
                                                 .newInitializedProtocol(pick);
        chkLtrCrdtProtocol.setService(pick.getId());
        chkLtrCrdtProtocol.setRequester(externalCust.getId());
        chkLtrCrdtProtocol.setProduct(anyProduct.getId());
        chkLtrCrdtProtocol.setDeliverTo(euro.getId());
        chkLtrCrdtProtocol.setDeliverFrom(us.getId());
        chkLtrCrdtProtocol.setChildAssignTo(creditDept.getId());
        chkLtrCrdtProtocol.setChildService(checkLetterOfCredit.getId());
        chkLtrCrdtProtocol.setChildProduct(sameProduct.getId());
        chkLtrCrdtProtocol.update();

        ProtocolRecord shipProtocol = model.getJobModel()
                                           .newInitializedProtocol(deliver);
        shipProtocol.setService(deliver.getId());
        shipProtocol.setRequester(anyAgency.getId());
        shipProtocol.setProduct(anyProduct.getId());
        shipProtocol.setDeliverTo(anyLocation.getId());
        shipProtocol.setDeliverFrom(anyLocation.getId());
        shipProtocol.setChildAssignTo(factory1Agency.getId());
        shipProtocol.setChildService(ship.getId());
        shipProtocol.setChildProduct(sameProduct.getId());
        shipProtocol.update();

        ProtocolRecord printCustDeclProtocol = model.getJobModel()
                                                    .newInitializedProtocol(ship);
        printCustDeclProtocol.setService(ship.getId());
        printCustDeclProtocol.setRequester(externalCust.getId());
        printCustDeclProtocol.setProduct(abc486.getId());
        printCustDeclProtocol.setDeliverTo(euro.getId());
        printCustDeclProtocol.setDeliverFrom(us.getId());
        printCustDeclProtocol.setChildAssignTo(cpu.getId());
        printCustDeclProtocol.setChildService(printCustomsDeclaration.getId());
        printCustDeclProtocol.setChildProduct(sameProduct.getId());
        printCustDeclProtocol.update();

        ProtocolRecord printPoProtocol = model.getJobModel()
                                              .newInitializedProtocol(ship);
        printPoProtocol.setService(ship.getId());
        printPoProtocol.setRequester(externalCust.getId());
        printPoProtocol.setProduct(abc486.getId());
        printPoProtocol.setDeliverTo(anyLocation.getId());
        printPoProtocol.setDeliverFrom(us.getId());
        printPoProtocol.setChildAssignTo(cpu.getId());
        printPoProtocol.setChildService(printPurchaseOrder.getId());
        printPoProtocol.setChildProduct(sameProduct.getId());
        printPoProtocol.update();

        ProtocolRecord feeProtocol = model.getJobModel()
                                          .newInitializedProtocol(printPurchaseOrder);
        feeProtocol.setService(printPurchaseOrder.getId());
        feeProtocol.setRequester(anyAgency.getId());
        feeProtocol.setProduct(abc486.getId());
        feeProtocol.setDeliverTo(anyLocation.getId());
        feeProtocol.setDeliverFrom(us.getId());
        feeProtocol.setChildAssignTo(billingComputer.getId());
        feeProtocol.setChildService(fee.getId());
        feeProtocol.setChildProduct(sameProduct.getId());
        feeProtocol.update();

        ProtocolRecord salesTaxProtocol = model.getJobModel()
                                               .newInitializedProtocol(fee);
        salesTaxProtocol.setService(fee.getId());
        salesTaxProtocol.setRequester(nonExemptAgency.getId());
        salesTaxProtocol.setProduct(nonExempt.getId());
        salesTaxProtocol.setDeliverTo(dc.getId());
        salesTaxProtocol.setDeliverFrom(anyLocation.getId());
        salesTaxProtocol.setChildAssignTo(billingComputer.getId());
        salesTaxProtocol.setChildService(salesTax.getId());
        salesTaxProtocol.setChildProduct(sameProduct.getId());
        salesTaxProtocol.update();

        ProtocolRecord discountProtocol = model.getJobModel()
                                               .newInitializedProtocol(fee);
        discountProtocol.setService(fee.getId());
        discountProtocol.setRequester(externalCust.getId());
        discountProtocol.setProduct(abc486.getId());
        discountProtocol.setDeliverTo(euro.getId());
        discountProtocol.setDeliverFrom(us.getId());
        discountProtocol.setChildAssignTo(billingComputer.getId());
        discountProtocol.setChildService(discount.getId());
        discountProtocol.setChildProduct(sameProduct.getId());
        discountProtocol.update();

        ProtocolRecord gtuDiscountedPriceProtocol = model.getJobModel()
                                                         .newInitializedProtocol(fee);
        gtuDiscountedPriceProtocol.setService(fee.getId());
        gtuDiscountedPriceProtocol.setRequester(georgeTownUniversity.getId());
        gtuDiscountedPriceProtocol.setProduct(abc486.getId());
        gtuDiscountedPriceProtocol.setDeliverTo(dc.getId());
        gtuDiscountedPriceProtocol.setDeliverFrom(us.getId());
        gtuDiscountedPriceProtocol.setChildAssignTo(billingComputer.getId());
        gtuDiscountedPriceProtocol.setChildService(salesTax.getId());
        gtuDiscountedPriceProtocol.setChildProduct(sameProduct.getId());
        gtuDiscountedPriceProtocol.update();
    }

    public void createRelationships() {
        area = model.records()
                    .newRelationship("Area",
                                     "A is a member of the economic community B");
        areaOf = model.records()
                      .newRelationship("Area Of",
                                       "A is economic community of B", area);
        area.insert();
        area.setInverse(areaOf.getId());
        areaOf.insert();

        city = model.records()
                    .newRelationship("City", "A is located in the City B");
        city.insert();
        cityOf = model.records()
                      .newRelationship("City Of", "A is the city of B", city);
        city.setInverse(cityOf.getId());
        cityOf.insert();

        customerType = model.records()
                            .newRelationship("Customer Type",
                                             "A has customer type of B");
        customerType.insert();
        customerTypeOf = model.records()
                              .newRelationship("Customer Type Of",
                                               "A is the customer type of B",
                                               customerType);
        customerType.setInverse(customerTypeOf.getId());
        customerTypeOf.insert();

        region = model.records()
                      .newRelationship("Region", "A's general region is B");
        region.insert();
        regionOf = model.records()
                        .newRelationship("Region Of", "A is the region of B",
                                         region);
        region.setInverse(regionOf.getId());
        regionOf.insert();

        state = model.records()
                     .newRelationship("State", "The State of A is B");
        state.insert();
        stateOf = model.records()
                       .newRelationship("State Of", "A is the state of B",
                                        state);
        state.setInverse(stateOf.getId());
        stateOf.insert();

        salesTaxStatus = model.records()
                              .newRelationship("SalesTaxStatus",
                                               "The sales tax status of A is B");
        salesTaxStatus.insert();
        salesTaxStatusOf = model.records()
                                .newRelationship("SalesTaxStatus Of",
                                                 "A is the sales tax status of B",
                                                 salesTaxStatus);
        salesTaxStatus.setInverse(salesTaxStatusOf.getId());
        salesTaxStatusOf.insert();

        storageType = model.records()
                           .newRelationship("StorageType",
                                            "The type of storage required for A is B");
        storageType.insert();
        storageTypeOf = model.records()
                             .newRelationship("StorageType Of",
                                              "A is the storage type of B",
                                              storageType);
        storageType.setInverse(storageTypeOf.getId());
        storageTypeOf.insert();

        street = model.records()
                      .newRelationship("Street", "The street of A is B");
        street.insert();
        streetOf = model.records()
                        .newRelationship("Street of", "A is the street of B",
                                         street);
        street.setInverse(streetOf.getId());
        streetOf.insert();
    }

    public void createServices() {
        deliver = model.records()
                       .newProduct("Deliver", "Deliver product");
        deliver.insert();

        pick = model.records()
                    .newProduct("Pick", "Pick inventory");
        pick.insert();

        ship = model.records()
                    .newProduct("Ship", "Ship inventory");
        ship.insert();

        checkCredit = model.records()
                           .newProduct("CheckCredit",
                                       "Check customer inhouse credit");
        checkCredit.insert();

        checkLetterOfCredit = model.records()
                                   .newProduct("CheckLetterOfCredit",
                                               "Check customer letter of credit");
        checkLetterOfCredit.insert();

        discount = model.records()
                        .newProduct("Discount", "Compute fee discount ");
        discount.insert();

        fee = model.records()
                   .newProduct("Fee", "Compute fee");
        fee.insert();

        printCustomsDeclaration = model.records()
                                       .newProduct("PrintCustomsDeclaration",
                                                   "Print the customs declaration");
        printCustomsDeclaration.insert();

        printPurchaseOrder = model.records()
                                  .newProduct("PrintPurchaseOrder",
                                              "Print the purchase order");
        printPurchaseOrder.insert();

        salesTax = model.records()
                        .newProduct("SalesTax", "Compute sales tax");
        salesTax.insert();
    }

    public void createStatusCodes() {
        available = model.records()
                         .newStatusCode("Available",
                                        "The job is available for execution");
        available.insert();

        active = model.records()
                      .newStatusCode("Active", "Working on it now");
        active.insert();

        waitingOnCreditCheck = model.records()
                                    .newStatusCode("Waiting on Credit Check",
                                                   "Waiting for credit check to be completed");
        waitingOnCreditCheck.insert();

        completed = model.records()
                         .newStatusCode("Completed", "Completed Job");
        completed.setPropagateChildren(true); // This is done to test the dup logic in the job animation
        completed.insert();

        failure = model.records()
                       .newStatusCode("Failure", "Something went wrong");
        failure.setFailParent(true);
        failure.insert();

        pickCompleted = model.records()
                             .newStatusCode("Pick Completed",
                                            "Pick product has been completed");
        pickCompleted.insert();

        waitingOnPurchaseOrder = model.records()
                                      .newStatusCode("WaitingOnPurchaseOrder",
                                                     "Waiting for purchase order to be completed");
        waitingOnPurchaseOrder.insert();

        waitingOnPricing = model.records()
                                .newStatusCode("Waiting on pricing",
                                               "Waiting for pricing to be completed");
        waitingOnPricing.insert();

        waitingOnFee = model.records()
                            .newStatusCode("Waiting on fee calculation",
                                           "Waiting for fee calculation to be completed");
        waitingOnFee.insert();

        abandoned = model.records()
                         .newStatusCode("Abandoned",
                                        "We were going to do it, something happened in earlier processing that will prevent us.  This can be garbage-collected now");
        abandoned.insert();
    }

    public void createStatusCodeSequencing() {
        StatusCodeSequencingRecord s = model.records()
                                            .newStatusCodeSequencing(pick,
                                                                     waitingOnCreditCheck,
                                                                     available);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(pick, available, active);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(pick, active, completed);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(ship, waitingOnPurchaseOrder,
                                          available);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(ship, available, active);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(ship, active, completed);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(deliver, available, active);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(deliver, active, completed);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(checkCredit, available, active);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(checkCredit, active, completed);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(checkLetterOfCredit, available,
                                          active);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(checkLetterOfCredit, active,
                                          completed);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(discount, available, active);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(discount, active, completed);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(fee, available, active);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(fee, available, waitingOnPricing);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(fee, waitingOnPricing, active);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(fee, active, completed);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(printCustomsDeclaration, waitingOnFee,
                                          available);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(printCustomsDeclaration, available,
                                          active);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(printCustomsDeclaration, active,
                                          completed);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(printPurchaseOrder, waitingOnFee,
                                          available);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(printPurchaseOrder, available,
                                          active);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(printPurchaseOrder, active,
                                          completed);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(salesTax, available, active);
        s.insert();

        s = model.records()
                 .newStatusCodeSequencing(salesTax, active, completed);
        s.insert();
    }

    public void load() {
        createAgencys();
        createAttributes();
        createProducts();
        createServices();
        createLocations();
        createRelationships();
        createNetworkNetworkInferences();
        createProductNetworks();
        createAgencyNetworks();
        createLocationNetworks();
        createProtocols();
        createMetaProtocol();
        createStatusCodes();
        createStatusCodeSequencing();
        createProductSequencingAuthorizations();
    }
}
