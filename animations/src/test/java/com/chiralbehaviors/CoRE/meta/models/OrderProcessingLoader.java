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
import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.Location;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.domain.StatusCode;
import com.chiralbehaviors.CoRE.jooq.enums.ValueType;
import com.chiralbehaviors.CoRE.jooq.tables.ChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.MetaProtocol;
import com.chiralbehaviors.CoRE.jooq.tables.ParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.Protocol;
import com.chiralbehaviors.CoRE.jooq.tables.SiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.StatusCodeSequencing;
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
    private Agency       core;
    private final Kernel kernel;
    private final Model  model;
    private Product      sameProduct;

    public OrderProcessingLoader(Model model) throws Exception {
        this.model = model;
        kernel = model.getKernel();
        core = kernel.getCore();
        sameProduct = kernel.getSameProduct();
        anyProduct = kernel.getAnyProduct();
        anyAgency = kernel.getAnyAgency();
        anyLocation = kernel.getAnyLocation();
        anyRelationship = kernel.getAnyRelationship();
    }

    public void createAgencyNetworks() {
        model.getAgencyModel()
             .link(georgeTownUniversity, customerType, externalCust);
        model.getAgencyModel()
             .link(georgeTownUniversity, salesTaxStatus, exempt);
        model.getAgencyModel()
             .link(orgA, customerType, externalCust);
        model.getAgencyModel()
             .link(cafleurBon, customerType, externalCust);
        model.getAgencyModel()
             .link(orgA, salesTaxStatus, nonExemptAgency);
    }

    public void createAgencys() {
        billingComputer = new Agency("BillingComputer", "The Billing Computer",
                                     core);
        em.persist(billingComputer);

        cpu = new Agency("CPU", "Computer");
        em.persist(cpu);

        creditDept = new Agency("Credit", "Credit Department");
        em.persist(creditDept);

        exempt = new Agency("Exempt", "Exempt from sales taxes");
        em.persist(exempt);

        externalCust = new Agency("Ext Customer", "External (Paying) Customer",
                                  core);
        em.persist(externalCust);

        factory1Agency = new Agency("Factory1Agency", "Factory #1");
        em.persist(factory1Agency);

        georgeTownUniversity = new Agency("GeorgetownUniversity",
                                          "Georgetown University");
        em.persist(georgeTownUniversity);

        cafleurBon = new Agency("CarfleurBon", "Carfleur Bon");
        em.persist(cafleurBon);

        manufacturer = new Agency("MNFR", "Manufacturer");
        em.persist(manufacturer);

        nonExemptAgency = new Agency("NonExemptAgency",
                                     "Subject to sales taxes");
        em.persist(nonExemptAgency);

        orgA = new Agency("OrgA", "Organization A");
        em.persist(orgA);

        orderFullfillment = new Agency("OrderFullfillment",
                                       "Order Fullfillment");
        em.persist(orderFullfillment);
    }

    public void createAttributes() {
        priceAttribute = new Attribute("price", "price", ValueType.INTEGER);
        em.persist(priceAttribute);

        taxRateAttribute = new Attribute("tax rate", "tax rate",
                                         ValueType.INTEGER);
        em.persist(taxRateAttribute);

        discountAttribute = new Attribute("discount", "discount",
                                          ValueType.INTEGER);
        em.persist(discountAttribute);
    }

    public void createLocationNetworks() {
        model.getLocationModel()
             .link(bin1, area, factory1);
        model.getLocationModel()
             .link(bin15, area, factory1);
        model.getLocationModel()
             .link(factory1, street, bht37);
        model.getLocationModel()
             .link(rsb225, city, dc);
        model.getLocationModel()
             .link(bht37, city, dc);
        model.getLocationModel()
             .link(rc31, city, paris);
        model.getLocationModel()
             .link(dc, region, east_coast);
        model.getLocationModel()
             .link(east_coast, area, us);
        model.getLocationModel()
             .link(paris, region, france);
        model.getLocationModel()
             .link(france, area, euro);
    }

    public void createLocations() {
        rsb225 = new Location("RSB225", "225 Reiss Science Bldg");
        em.persist(rsb225);
        bht37 = new Location("BHT37", "37 Bret Harte Terrace");
        em.persist(bht37);

        rc31 = new Location("RC31", "31 Rue Cambon");
        em.persist(rc31);

        bin1 = new Location("BIN01", "Bin #1");
        em.persist(bin1);
        bin15 = new Location("BIN15", "Bin #15");
        em.persist(bin15);
        dc = new Location("DC", "District of Columbia");
        em.persist(dc);
        east_coast = new Location("EAST_COAST", "East Coast");
        em.persist(east_coast);
        factory1 = new Location("Factory1", "Factory 1");
        em.persist(factory1);
        france = new Location("FRANCE", "France");
        em.persist(france);
        paris = new Location("PARIS", "Paris");
        em.persist(paris);
        us = new Location("US", "U.S. Locations");
        em.persist(us);
        euro = new Location("Euro", "European locations");
        em.persist(euro);
    }

    public void createMetaProtocols() {
        MetaProtocol m1 = model.getJobModel()
                               .newInitializedMetaProtocol(deliver);
        m1.setSequenceNumber(1);
        m1.setProduct(anyRelationship);
        m1.setDeliverTo(state);
        m1.setDeliverFrom(area);

        em.persist(m1);

        MetaProtocol m2 = model.getJobModel()
                               .newInitializedMetaProtocol(pick);
        m2.setSequenceNumber(1);
        m2.setProduct(anyRelationship);
        m2.setRequester(customerType);
        m2.setDeliverTo(area);
        m2.setDeliverFrom(area);

        em.persist(m2);

        MetaProtocol m3 = model.getJobModel()
                               .newInitializedMetaProtocol(ship);
        m3.setSequenceNumber(1);
        m3.setProduct(anyRelationship);
        m3.setRequester(customerType);
        m3.setDeliverTo(area);
        m3.setDeliverFrom(area);

        MetaProtocol m5 = model.getJobModel()
                               .newInitializedMetaProtocol(fee);
        m5.setSequenceNumber(1);
        m5.setProduct(anyRelationship);
        m5.setRequester(salesTaxStatus);
        m5.setDeliverTo(city);

        em.persist(m5);

        MetaProtocol m6 = model.getJobModel()
                               .newInitializedMetaProtocol(printPurchaseOrder,
                                                           core);
        m6.setSequenceNumber(1);
        m6.setProduct(anyRelationship);
        m6.setRequester(anyRelationship);
        m6.setDeliverTo(anyRelationship);
        m6.setDeliverFrom(area);

        em.persist(m6);
    }

    public void createNetworkInferences() {
        Inference areaToRegion = new Inference(areaOf, regionOf, areaOf);
        em.persist(areaToRegion);

        Inference areaToState = new Inference(areaOf, stateOf, areaOf);
        em.persist(areaToState);

        Inference areaToCity = new Inference(areaOf, cityOf, areaOf);
        em.persist(areaToCity);

        Inference areaToStreet = new Inference(areaOf, streetOf, areaOf);
        em.persist(areaToStreet);

        Inference regionToState = new Inference(regionOf, stateOf, regionOf,
                                                core);
        em.persist(regionToState);

        Inference regionToCity = new Inference(regionOf, cityOf, regionOf,
                                               core);
        em.persist(regionToCity);

        Inference regionToStreet = new Inference(regionOf, streetOf, regionOf,
                                                 core);
        em.persist(regionToStreet);

        Inference stateToCity = new Inference(stateOf, cityOf, stateOf);
        em.persist(stateToCity);

        Inference stateToStreet = new Inference(stateOf, streetOf, stateOf,
                                                core);
        em.persist(stateToStreet);

        Inference cityToStreet = new Inference(cityOf, streetOf, cityOf);
        em.persist(cityToStreet);
    }

    public void createProductNetworks() {
        model.getProductModel()
             .link(abc486, storageType, roomTemp);
        model.getProductModel()
             .link(abc486, salesTaxStatus, nonExempt);
        model.getProductModel()
             .link(chemB, storageType, frozen);
    }

    public void createProducts() {
        abc486 = new Product("ABC486", "Laptop Computer");
        em.persist(abc486);
        frozen = new Product("Frozen", "Frozen products");
        em.persist(frozen);
        nonExempt = new Product("NonExempt", "Subject to sales tax");
        em.persist(nonExempt);
        chemB = new Product("ChemB", "Chemical B");
        em.persist(chemB);
        roomTemp = new Product("RoomTemp", "Room temperature products");
        em.persist(roomTemp);
        orderEntryWorkspace = new Product("defining product for order entry",
                                          core);
        em.persist(orderEntryWorkspace);
    }

    public void createProductSequencingAuthorizations() {

        SiblingSequencingAuthorization activatePrintCustomsDeclaration = new SiblingSequencingAuthorization(core);
        activatePrintCustomsDeclaration.setParent(printPurchaseOrder);
        activatePrintCustomsDeclaration.setStatusCode(completed);
        activatePrintCustomsDeclaration.setNextSibling(printCustomsDeclaration);
        activatePrintCustomsDeclaration.setNextSiblingStatus(available);
        em.persist(activatePrintCustomsDeclaration);

        ParentSequencingAuthorization productPicked = new ParentSequencingAuthorization(core);
        productPicked.setService(pick);
        productPicked.setStatusCode(completed);
        productPicked.setParent(deliver);
        productPicked.setParentStatusToSet(completed);
        productPicked.setSetIfActiveSiblings(false);
        em.persist(productPicked);

        ParentSequencingAuthorization checkCreditCompleted = new ParentSequencingAuthorization(core);
        checkCreditCompleted.setService(checkCredit);
        checkCreditCompleted.setStatusCode(completed);
        checkCreditCompleted.setParent(pick);
        checkCreditCompleted.setParentStatusToSet(available);
        em.persist(checkCreditCompleted);

        SiblingSequencingAuthorization activateShip = new SiblingSequencingAuthorization(core);
        activateShip.setParent(pick);
        activateShip.setStatusCode(completed);
        activateShip.setNextSibling(ship);
        activateShip.setNextSiblingStatus(waitingOnPurchaseOrder);
        em.persist(activateShip);

        ParentSequencingAuthorization activateShipFromPrintCustomsDeclaration = new ParentSequencingAuthorization(core);
        activateShipFromPrintCustomsDeclaration.setService(printCustomsDeclaration);
        activateShipFromPrintCustomsDeclaration.setStatusCode(completed);
        activateShipFromPrintCustomsDeclaration.setParent(ship);
        activateShipFromPrintCustomsDeclaration.setParentStatusToSet(available);
        activateShipFromPrintCustomsDeclaration.setSetIfActiveSiblings(false);
        em.persist(activateShipFromPrintCustomsDeclaration);

        ParentSequencingAuthorization activateShipFromPrintPurchaseOrder = new ParentSequencingAuthorization(core);
        activateShipFromPrintPurchaseOrder.setService(printPurchaseOrder);
        activateShipFromPrintPurchaseOrder.setStatusCode(completed);
        activateShipFromPrintPurchaseOrder.setParent(ship);
        activateShipFromPrintPurchaseOrder.setParentStatusToSet(available);
        activateShipFromPrintPurchaseOrder.setSetIfActiveSiblings(false);
        em.persist(activateShipFromPrintPurchaseOrder);

        ChildSequencingAuthorization activatePrintPurchaseOrder = new ChildSequencingAuthorization(core);
        activatePrintPurchaseOrder.setParent(ship);
        activatePrintPurchaseOrder.setStatusCode(waitingOnPurchaseOrder);
        activatePrintPurchaseOrder.setNextChild(printPurchaseOrder);
        activatePrintPurchaseOrder.setNextChildStatus(waitingOnFee);
        em.persist(activatePrintPurchaseOrder);

        ChildSequencingAuthorization activateCreditCheck = new ChildSequencingAuthorization(core);
        activateCreditCheck.setParent(pick);
        activateCreditCheck.setStatusCode(waitingOnCreditCheck);
        activateCreditCheck.setNextChild(checkCredit);
        activateCreditCheck.setNextChildStatus(available);
        em.persist(activateCreditCheck);

        ChildSequencingAuthorization activateFee = new ChildSequencingAuthorization(core);
        activateFee.setParent(printPurchaseOrder);
        activateFee.setStatusCode(waitingOnFee);
        activateFee.setNextChild(fee);
        activateFee.setNextChildStatus(available);
        em.persist(activateFee);

        ChildSequencingAuthorization activatePick = new ChildSequencingAuthorization(core);
        activatePick.setParent(deliver);
        activatePick.setStatusCode(available);
        activatePick.setNextChild(pick);
        activatePick.setNextChildStatus(waitingOnCreditCheck);
        em.persist(activatePick);

        SiblingSequencingAuthorization activateDiscount = new SiblingSequencingAuthorization(core);
        activateDiscount.setParent(fee);
        activateDiscount.setStatusCode(completed);
        activateDiscount.setNextSibling(discount);
        activateDiscount.setNextSiblingStatus(available);
        em.persist(activateDiscount);

        ParentSequencingAuthorization activatePrintPurchaseOrderFromFee = new ParentSequencingAuthorization(core);
        activatePrintPurchaseOrderFromFee.setService(fee);
        activatePrintPurchaseOrderFromFee.setStatusCode(completed);
        activatePrintPurchaseOrderFromFee.setParent(printPurchaseOrder);
        activatePrintPurchaseOrderFromFee.setParentStatusToSet(available);
        activatePrintPurchaseOrderFromFee.setSetIfActiveSiblings(false);
        em.persist(activatePrintPurchaseOrderFromFee);

        ParentSequencingAuthorization activatePrintPurchaseOrderFromDiscount = new ParentSequencingAuthorization(core);
        activatePrintPurchaseOrderFromDiscount.setService(discount);
        activatePrintPurchaseOrderFromDiscount.setStatusCode(completed);
        activatePrintPurchaseOrderFromDiscount.setParent(printPurchaseOrder);
        activatePrintPurchaseOrderFromDiscount.setParentStatusToSet(available);
        activatePrintPurchaseOrderFromDiscount.setSetIfActiveSiblings(false);
        em.persist(activatePrintPurchaseOrderFromDiscount);

        ParentSequencingAuthorization completeDeliverFromShip = new ParentSequencingAuthorization(core);
        completeDeliverFromShip.setService(ship);
        completeDeliverFromShip.setStatusCode(completed);
        completeDeliverFromShip.setParent(deliver);
        completeDeliverFromShip.setParentStatusToSet(completed);
        completeDeliverFromShip.setSetIfActiveSiblings(false);
        em.persist(completeDeliverFromShip);
    }

    public void createProtocols() {

        Protocol pickProtocol = model.getJobModel()
                                     .newInitializedProtocol(deliver);
        pickProtocol.setService(deliver);
        pickProtocol.setRequester(anyAgency);
        pickProtocol.setProduct(anyProduct);
        pickProtocol.setDeliverTo(anyLocation);
        pickProtocol.setDeliverFrom(anyLocation);
        pickProtocol.setChildAssignTo(factory1Agency);
        pickProtocol.setChildService(pick);
        pickProtocol.setChildProduct(sameProduct);
        em.persist(pickProtocol);

        Protocol chkCreditProtocol = model.getJobModel()
                                          .newInitializedProtocol(pick);
        chkCreditProtocol.setService(pick);
        chkCreditProtocol.setRequester(externalCust);
        chkCreditProtocol.setProduct(anyProduct);
        chkCreditProtocol.setDeliverTo(us);
        chkCreditProtocol.setDeliverFrom(us);
        chkCreditProtocol.setChildAssignTo(cpu);
        chkCreditProtocol.setChildService(checkCredit);
        chkCreditProtocol.setChildProduct(sameProduct);
        em.persist(chkCreditProtocol);

        Protocol chkLtrCrdtProtocol = model.getJobModel()
                                           .newInitializedProtocol(pick);
        chkLtrCrdtProtocol.setService(pick);
        chkLtrCrdtProtocol.setRequester(externalCust);
        chkLtrCrdtProtocol.setProduct(anyProduct);
        chkLtrCrdtProtocol.setDeliverTo(euro);
        chkLtrCrdtProtocol.setDeliverFrom(us);
        chkLtrCrdtProtocol.setChildAssignTo(creditDept);
        chkLtrCrdtProtocol.setChildService(checkLetterOfCredit);
        chkLtrCrdtProtocol.setChildProduct(sameProduct);
        em.persist(chkLtrCrdtProtocol);

        Protocol shipProtocol = model.getJobModel()
                                     .newInitializedProtocol(deliver);
        shipProtocol.setService(deliver);
        shipProtocol.setRequester(anyAgency);
        shipProtocol.setProduct(anyProduct);
        shipProtocol.setDeliverTo(anyLocation);
        shipProtocol.setDeliverFrom(anyLocation);
        shipProtocol.setChildAssignTo(factory1Agency);
        shipProtocol.setChildService(ship);
        shipProtocol.setChildProduct(sameProduct);
        em.persist(shipProtocol);

        Protocol printCustDeclProtocol = model.getJobModel()
                                              .newInitializedProtocol(ship,
                                                                      core);
        printCustDeclProtocol.setService(ship);
        printCustDeclProtocol.setRequester(externalCust);
        printCustDeclProtocol.setProduct(abc486);
        printCustDeclProtocol.setDeliverTo(euro);
        printCustDeclProtocol.setDeliverFrom(us);
        printCustDeclProtocol.setChildAssignTo(cpu);
        printCustDeclProtocol.setChildService(printCustomsDeclaration);
        printCustDeclProtocol.setChildProduct(sameProduct);
        em.persist(printCustDeclProtocol);

        Protocol printPoProtocol = model.getJobModel()
                                        .newInitializedProtocol(ship);
        printPoProtocol.setService(ship);
        printPoProtocol.setRequester(externalCust);
        printPoProtocol.setProduct(abc486);
        printPoProtocol.setDeliverTo(anyLocation);
        printPoProtocol.setDeliverFrom(us);
        printPoProtocol.setChildAssignTo(cpu);
        printPoProtocol.setChildService(printPurchaseOrder);
        printPoProtocol.setChildProduct(sameProduct);
        em.persist(printPoProtocol);

        Protocol feeProtocol = model.getJobModel()
                                    .newInitializedProtocol(printPurchaseOrder,
                                                            core);
        feeProtocol.setService(printPurchaseOrder);
        feeProtocol.setRequester(anyAgency);
        feeProtocol.setProduct(abc486);
        feeProtocol.setDeliverTo(anyLocation);
        feeProtocol.setDeliverFrom(us);
        feeProtocol.setChildAssignTo(billingComputer);
        feeProtocol.setChildService(fee);
        feeProtocol.setChildProduct(sameProduct);
        em.persist(feeProtocol);

        Protocol salesTaxProtocol = model.getJobModel()
                                         .newInitializedProtocol(fee);
        salesTaxProtocol.setService(fee);
        salesTaxProtocol.setRequester(nonExemptAgency);
        salesTaxProtocol.setProduct(nonExempt);
        salesTaxProtocol.setDeliverTo(dc);
        salesTaxProtocol.setDeliverFrom(anyLocation);
        salesTaxProtocol.setChildAssignTo(billingComputer);
        salesTaxProtocol.setChildService(salesTax);
        salesTaxProtocol.setChildProduct(sameProduct);
        em.persist(salesTaxProtocol);

        Protocol discountProtocol = model.getJobModel()
                                         .newInitializedProtocol(fee);
        discountProtocol.setService(fee);
        discountProtocol.setRequester(externalCust);
        discountProtocol.setProduct(abc486);
        discountProtocol.setDeliverTo(euro);
        discountProtocol.setDeliverFrom(us);
        discountProtocol.setChildAssignTo(billingComputer);
        discountProtocol.setChildService(discount);
        discountProtocol.setChildProduct(sameProduct);
        em.persist(discountProtocol);

        Protocol gtuDiscountedPriceProtocol = model.getJobModel()
                                                   .newInitializedProtocol(fee,
                                                                           core);
        gtuDiscountedPriceProtocol.setService(fee);
        gtuDiscountedPriceProtocol.setRequester(georgeTownUniversity);
        gtuDiscountedPriceProtocol.setProduct(abc486);
        gtuDiscountedPriceProtocol.setDeliverTo(dc);
        gtuDiscountedPriceProtocol.setDeliverFrom(us);
        gtuDiscountedPriceProtocol.setChildAssignTo(billingComputer);
        gtuDiscountedPriceProtocol.setChildService(salesTax);
        gtuDiscountedPriceProtocol.setChildProduct(sameProduct);
        em.persist(gtuDiscountedPriceProtocol);
    }

    public void createRelationships() {
        area = model.records()
                    .newRelationship("Area",
                                     "A is a member of the economic community B",
                                     core);
        em.persist(area);
        areaOf = new Relationship("Area Of", "A is economic community of B",
                                  core, area);
        area.setInverse(areaOf);
        em.persist(areaOf);

        city = new Relationship("City", "A is located in the City B");
        em.persist(city);
        cityOf = new Relationship("City Of", "A is the city of B", city);
        city.setInverse(cityOf);
        em.persist(cityOf);

        customerType = new Relationship("Customer Type",
                                        "A has customer type of B");
        em.persist(customerType);
        customerTypeOf = new Relationship("Customer Type Of",
                                          "A is the customer type of B",
                                          customerType);
        customerType.setInverse(customerTypeOf);
        em.persist(customerTypeOf);

        region = new Relationship("Region", "A's general region is B");
        em.persist(region);
        regionOf = new Relationship("Region Of", "A is the region of B",
                                    region);
        region.setInverse(regionOf);
        em.persist(regionOf);

        state = new Relationship("State", "The State of A is B");
        em.persist(state);
        stateOf = new Relationship("State Of", "A is the state of B", state);
        state.setInverse(stateOf);
        em.persist(stateOf);

        salesTaxStatus = new Relationship("SalesTaxStatus",
                                          "The sales tax status of A is B",
                                          core);
        em.persist(salesTaxStatus);
        salesTaxStatusOf = new Relationship("SalesTaxStatus Of",
                                            "A is the sales tax status of B",
                                            core, salesTaxStatus);
        salesTaxStatus.setInverse(salesTaxStatusOf);
        em.persist(salesTaxStatusOf);

        storageType = new Relationship("StorageType",
                                       "The type of storage required for A is B",
                                       core);
        em.persist(storageType);
        storageTypeOf = new Relationship("StorageType Of",
                                         "A is the storage type of B",
                                         storageType);
        storageType.setInverse(storageTypeOf);
        em.persist(storageTypeOf);

        street = new Relationship("Street", "The street of A is B");
        em.persist(street);
        streetOf = new Relationship("Street of", "A is the street of B",
                                    street);
        street.setInverse(streetOf);
        em.persist(streetOf);
    }

    public void createServices() {
        deliver = new Product("Deliver", "Deliver product");
        em.persist(deliver);

        pick = new Product("Pick", "Pick inventory");
        em.persist(pick);

        ship = new Product("Ship", "Ship inventory");
        em.persist(ship);

        checkCredit = new Product("CheckCredit",
                                  "Check customer inhouse credit");
        em.persist(checkCredit);

        checkLetterOfCredit = new Product("CheckLetterOfCredit",
                                          "Check customer letter of credit",
                                          core);
        em.persist(checkLetterOfCredit);

        discount = new Product("Discount", "Compute fee discount ");
        em.persist(discount);

        fee = new Product("Fee", "Compute fee");
        em.persist(fee);

        printCustomsDeclaration = new Product("PrintCustomsDeclaration",
                                              "Print the customs declaration",
                                              core);
        em.persist(printCustomsDeclaration);

        printPurchaseOrder = new Product("PrintPurchaseOrder",
                                         "Print the purchase order");
        em.persist(printPurchaseOrder);

        salesTax = new Product("SalesTax", "Compute sales tax");
        em.persist(salesTax);
    }

    public void createStatusCodes() {
        available = new StatusCode("Available",
                                   "The job is available for execution");
        em.persist(available);

        active = new StatusCode("Active", "Working on it now");
        em.persist(active);

        waitingOnCreditCheck = new StatusCode("Waiting on Credit Check",
                                              "Waiting for credit check to be completed",
                                              core);
        em.persist(waitingOnCreditCheck);

        completed = new StatusCode("Completed", "Completed Job");
        completed.setPropagateChildren(true); // This is done to test the dup logic in the job animation
        em.persist(completed);

        failure = new StatusCode("Failure", "Something went wrong");
        failure.setFailParent(true);
        em.persist(failure);

        pickCompleted = new StatusCode("Pick Completed",
                                       "Pick product has been completed");
        em.persist(pickCompleted);

        waitingOnPurchaseOrder = new StatusCode("WaitingOnPurchaseOrder",
                                                "Waiting for purchase order to be completed",
                                                core);
        em.persist(waitingOnPurchaseOrder);

        waitingOnPricing = new StatusCode("Waiting on pricing",
                                          "Waiting for pricing to be completed",
                                          core);
        em.persist(waitingOnPricing);

        waitingOnFee = new StatusCode("Waiting on fee calculation",
                                      "Waiting for fee calculation to be completed",
                                      core);
        em.persist(waitingOnFee);

        abandoned = new StatusCode("Abandoned",
                                   "We were going to do it, something happened in earlier processing that will prevent us.  This can be garbage-collected now",
                                   core);
        em.persist(abandoned);
    }

    public void createStatusCodeSequencing() {
        StatusCodeSequencing s = new StatusCodeSequencing(pick,
                                                          waitingOnCreditCheck,
                                                          available);
        em.persist(s);

        s = new StatusCodeSequencing(pick, available, active);
        em.persist(s);

        s = new StatusCodeSequencing(pick, active, completed);
        em.persist(s);

        s = new StatusCodeSequencing(ship, waitingOnPurchaseOrder, available,
                                     core);
        em.persist(s);

        s = new StatusCodeSequencing(ship, available, active);
        em.persist(s);

        s = new StatusCodeSequencing(ship, active, completed);
        em.persist(s);

        s = new StatusCodeSequencing(deliver, available, active);
        em.persist(s);

        s = new StatusCodeSequencing(deliver, active, completed);
        em.persist(s);

        s = new StatusCodeSequencing(checkCredit, available, active);
        em.persist(s);

        s = new StatusCodeSequencing(checkCredit, active, completed);
        em.persist(s);

        s = new StatusCodeSequencing(checkLetterOfCredit, available, active,
                                     core);
        em.persist(s);

        s = new StatusCodeSequencing(checkLetterOfCredit, active, completed,
                                     core);
        em.persist(s);

        s = new StatusCodeSequencing(discount, available, active);
        em.persist(s);

        s = new StatusCodeSequencing(discount, active, completed);
        em.persist(s);

        s = new StatusCodeSequencing(fee, available, active);
        em.persist(s);

        s = new StatusCodeSequencing(fee, available, waitingOnPricing);
        em.persist(s);

        s = new StatusCodeSequencing(fee, waitingOnPricing, active);
        em.persist(s);

        s = new StatusCodeSequencing(fee, active, completed);
        em.persist(s);

        s = new StatusCodeSequencing(printCustomsDeclaration, waitingOnFee,
                                     available);
        em.persist(s);

        s = new StatusCodeSequencing(printCustomsDeclaration, available, active,
                                     core);
        em.persist(s);

        s = new StatusCodeSequencing(printCustomsDeclaration, active, completed,
                                     core);
        em.persist(s);

        s = new StatusCodeSequencing(printPurchaseOrder, waitingOnFee,
                                     available);
        em.persist(s);

        s = new StatusCodeSequencing(printPurchaseOrder, available, active,
                                     core);
        em.persist(s);

        s = new StatusCodeSequencing(printPurchaseOrder, active, completed,
                                     core);
        em.persist(s);

        s = new StatusCodeSequencing(salesTax, available, active);
        em.persist(s);

        s = new StatusCodeSequencing(salesTax, active, completed);
        em.persist(s);
    }

    public void load() {
        createAgencys();
        createAttributes();
        createProducts();
        createServices();
        createLocations();
        createRelationships();
        createNetworkInferences();
        createProductNetworks();
        createAgencyNetworks();
        createLocationNetworks();
        createProtocols();
        createMetaProtocols();
        createStatusCodes();
        createStatusCodeSequencing();
        createProductSequencingAuthorizations();
    }
}
