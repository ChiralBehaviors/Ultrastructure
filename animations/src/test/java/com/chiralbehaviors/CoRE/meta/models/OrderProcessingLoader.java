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

import javax.persistence.EntityManager;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.ValueType;
import com.chiralbehaviors.CoRE.event.MetaProtocol;
import com.chiralbehaviors.CoRE.event.ProductChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductSiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.Protocol;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.network.NetworkInference;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 * 
 */
public class OrderProcessingLoader extends OrderProcessingWorkspace {

    private final EntityManager em;
    private final Kernel        kernel;
    private final Model         model;

    public OrderProcessingLoader(EntityManager em) throws Exception {
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
        unset = kernel.getUnset();
    }

    public void createAgencyNetworks() {
        model.getAgencyModel().link(georgeTownUniversity, customerType,
                                    externalCust, core);
        model.getAgencyModel().link(georgeTownUniversity, salesTaxStatus,
                                    exempt, core);
        model.getAgencyModel().link(orgA, customerType, externalCust, core);
        model.getAgencyModel().link(cafleurBon, customerType, externalCust,
                                    core);
        model.getAgencyModel().link(orgA, salesTaxStatus, nonExemptAgency, core);
    }

    public void createAgencys() {
        billingComputer = new Agency("Billing CPU", "The Billing Computer",
                                     core);
        em.persist(billingComputer);

        cpu = new Agency("CPU", "Computer", core);
        em.persist(cpu);

        creditDept = new Agency("Credit", "Credit Department", core);
        em.persist(creditDept);

        exempt = new Agency("Exempt", "Exempt from sales taxes", core);
        em.persist(exempt);

        externalCust = new Agency("Ext Customer", "External (Paying) Customer",
                                  core);
        em.persist(externalCust);

        factory1Agency = new Agency("Factory1", "Factory #1", core);
        em.persist(factory1Agency);

        georgeTownUniversity = new Agency("GU", "Georgetown University", core);
        em.persist(georgeTownUniversity);

        cafleurBon = new Agency("carfleurBon", "Carfleur Bon", core);
        em.persist(cafleurBon);

        manufacturer = new Agency("MNFR", "Manufacturer", core);
        em.persist(manufacturer);

        nonExemptAgency = new Agency("NonExempt", "Subject to sales taxes",
                                     core);
        em.persist(nonExemptAgency);

        orgA = new Agency("OrgA", "Organization A", core);
        em.persist(orgA);

        orderFullfillment = new Agency("Order Fullfillment",
                                       "Order Fullfillment", core);
        em.persist(orderFullfillment);
    }

    public void createAttributes() {
        priceAttribute = new Attribute("price", "price", core,
                                       ValueType.INTEGER);
        em.persist(priceAttribute);

        taxRateAttribute = new Attribute("tax rate", "tax rate", core,
                                         ValueType.INTEGER);
        em.persist(taxRateAttribute);

        discountAttribute = new Attribute("discount", "discount", core,
                                          ValueType.INTEGER);
        em.persist(discountAttribute);
    }

    public void createLocationNetworks() {
        model.getLocationModel().link(bin1, area, factory1, core);
        model.getLocationModel().link(bin15, area, factory1, core);
        model.getLocationModel().link(factory1, street, bht378, core);
        model.getLocationModel().link(rsb225, city, dc, core);
        model.getLocationModel().link(bht378, city, dc, core);
        model.getLocationModel().link(rc31, city, paris, core);
        model.getLocationModel().link(dc, region, east_coast, core);
        model.getLocationModel().link(east_coast, area, us, core);
        model.getLocationModel().link(paris, region, france, core);
        model.getLocationModel().link(france, area, euro, core);
    }

    public void createLocations() {
        rsb225 = new Location("225RSB", "225 Reiss Science Bldg", core);
        em.persist(rsb225);
        bht378 = new Location("37BHT", "37 Bret Harte Terrace", core);
        em.persist(bht378);

        rc31 = new Location("31rc", "31 Rue Cambon", core);
        em.persist(rc31);

        bin1 = new Location("BIN01", "Bin #1", core);
        em.persist(bin1);
        bin15 = new Location("BIN15", "Bin #15", core);
        em.persist(bin15);
        dc = new Location("DC", "District of Columbia", core);
        em.persist(dc);
        east_coast = new Location("EAST_COAST", "East Coast", core);
        em.persist(east_coast);
        factory1 = new Location("FACTORY1", "Factory 1", core);
        em.persist(factory1);
        france = new Location("FRANCE", "France", core);
        em.persist(france);
        paris = new Location("PARIS", "Paris", core);
        em.persist(paris);
        us = new Location("US", "U.S. Locations", core);
        em.persist(us);
        euro = new Location("Euro", "European locations", core);
        em.persist(euro);
    }

    public void createMetaProtocols() {
        MetaProtocol m1 = model.getJobModel().newInitializedMetaProtocol(deliver,
                                                                         core);
        m1.setSequenceNumber(1);
        m1.setProductOrdered(anyRelationship);
        m1.setDeliverTo(state);
        m1.setDeliverFrom(area);

        em.persist(m1);

        MetaProtocol m2 = model.getJobModel().newInitializedMetaProtocol(pick,
                                                                         core);
        m2.setSequenceNumber(1);
        m2.setProductOrdered(anyRelationship);
        m2.setRequestingAgency(customerType);
        m2.setDeliverTo(area);
        m2.setDeliverFrom(area);

        em.persist(m2);

        MetaProtocol m3 = model.getJobModel().newInitializedMetaProtocol(ship,
                                                                         core);
        m3.setSequenceNumber(1);
        m3.setProductOrdered(anyRelationship);
        m3.setRequestingAgency(customerType);
        m3.setDeliverTo(area);
        m3.setDeliverFrom(area);

        em.persist(m3);

        MetaProtocol m5 = model.getJobModel().newInitializedMetaProtocol(fee,
                                                                         core);
        m5.setSequenceNumber(1);
        m5.setProductOrdered(anyRelationship);
        m5.setRequestingAgency(salesTaxStatus);
        m5.setDeliverTo(city);

        em.persist(m5);

        MetaProtocol m6 = model.getJobModel().newInitializedMetaProtocol(printPurchaseOrder,
                                                                         core);
        m6.setSequenceNumber(1);
        m6.setProductOrdered(anyRelationship);
        m6.setRequestingAgency(anyRelationship);
        m6.setDeliverTo(anyRelationship);
        m6.setDeliverFrom(area);

        em.persist(m6);
    }

    public void createNetworkInferences() {
        NetworkInference areaToRegion = new NetworkInference(areaOf, regionOf,
                                                             areaOf, core);
        em.persist(areaToRegion);

        NetworkInference areaToState = new NetworkInference(areaOf, stateOf,
                                                            areaOf, core);
        em.persist(areaToState);

        NetworkInference areaToCity = new NetworkInference(areaOf, cityOf,
                                                           areaOf, core);
        em.persist(areaToCity);

        NetworkInference areaToStreet = new NetworkInference(areaOf, streetOf,
                                                             areaOf, core);
        em.persist(areaToStreet);

        NetworkInference regionToState = new NetworkInference(regionOf,
                                                              stateOf,
                                                              regionOf, core);
        em.persist(regionToState);

        NetworkInference regionToCity = new NetworkInference(regionOf, cityOf,
                                                             regionOf, core);
        em.persist(regionToCity);

        NetworkInference regionToStreet = new NetworkInference(regionOf,
                                                               streetOf,
                                                               regionOf, core);
        em.persist(regionToStreet);

        NetworkInference stateToCity = new NetworkInference(stateOf, cityOf,
                                                            stateOf, core);
        em.persist(stateToCity);

        NetworkInference stateToStreet = new NetworkInference(stateOf,
                                                              streetOf,
                                                              stateOf, core);
        em.persist(stateToStreet);

        NetworkInference cityToStreet = new NetworkInference(cityOf, streetOf,
                                                             cityOf, core);
        em.persist(cityToStreet);
    }

    public void createProductNetworks() {
        model.getProductModel().link(abc486, storageType, roomTemp, core);
        model.getProductModel().link(abc486, salesTaxStatus, nonExempt, core);
        model.getProductModel().link(chemB, storageType, frozen, core);
    }

    public void createProducts() {
        abc486 = new Product("ABC486", "Laptop Computer", core);
        em.persist(abc486);
        frozen = new Product("Frozen", "Frozen products", core);
        em.persist(frozen);
        nonExempt = new Product("NonExempt", "Subject to sales tax", core);
        em.persist(nonExempt);
        chemB = new Product("ChemB", "Chemical B", core);
        em.persist(chemB);
        roomTemp = new Product("RoomTemp", "Room temperature products", core);
        em.persist(roomTemp);
    }

    public void createProductSequencingAuthorizations() {

        ProductSiblingSequencingAuthorization activatePrintCustomsDeclaration = new ProductSiblingSequencingAuthorization(
                                                                                                                          core);
        activatePrintCustomsDeclaration.setParent(printPurchaseOrder);
        activatePrintCustomsDeclaration.setStatusCode(completed);
        activatePrintCustomsDeclaration.setNextSibling(printCustomsDeclaration);
        activatePrintCustomsDeclaration.setNextSiblingStatus(available);
        em.persist(activatePrintCustomsDeclaration);

        ProductParentSequencingAuthorization productPicked = new ProductParentSequencingAuthorization(
                                                                                                      core);
        productPicked.setService(pick);
        productPicked.setStatusCode(completed);
        productPicked.setParent(deliver);
        productPicked.setParentStatusToSet(completed);
        productPicked.setSetIfActiveSiblings(false);
        em.persist(productPicked);

        ProductParentSequencingAuthorization checkCreditCompleted = new ProductParentSequencingAuthorization(
                                                                                                             core);
        checkCreditCompleted.setService(checkCredit);
        checkCreditCompleted.setStatusCode(completed);
        checkCreditCompleted.setParent(pick);
        checkCreditCompleted.setParentStatusToSet(available);
        em.persist(checkCreditCompleted);

        ProductParentSequencingAuthorization checkLetterOfCreditCompleted = new ProductParentSequencingAuthorization(
                                                                                                                     core);
        checkLetterOfCreditCompleted.setService(checkLetterOfCredit);
        checkLetterOfCreditCompleted.setStatusCode(completed);
        checkLetterOfCreditCompleted.setParent(pick);
        checkLetterOfCreditCompleted.setParentStatusToSet(available);
        em.persist(checkLetterOfCreditCompleted);

        ProductSiblingSequencingAuthorization activateShip = new ProductSiblingSequencingAuthorization(
                                                                                                       core);
        activateShip.setParent(pick);
        activateShip.setStatusCode(completed);
        activateShip.setNextSibling(ship);
        activateShip.setNextSiblingStatus(waitingOnPurchaseOrder);
        em.persist(activateShip);

        ProductParentSequencingAuthorization activateShipFromPrintCustomsDeclaration = new ProductParentSequencingAuthorization(
                                                                                                                                core);
        activateShipFromPrintCustomsDeclaration.setService(printCustomsDeclaration);
        activateShipFromPrintCustomsDeclaration.setStatusCode(completed);
        activateShipFromPrintCustomsDeclaration.setParent(ship);
        activateShipFromPrintCustomsDeclaration.setParentStatusToSet(available);
        activateShipFromPrintCustomsDeclaration.setSetIfActiveSiblings(false);
        em.persist(activateShipFromPrintCustomsDeclaration);

        ProductParentSequencingAuthorization activateShipFromPrintPurchaseOrder = new ProductParentSequencingAuthorization(
                                                                                                                           core);
        activateShipFromPrintPurchaseOrder.setService(printPurchaseOrder);
        activateShipFromPrintPurchaseOrder.setStatusCode(completed);
        activateShipFromPrintPurchaseOrder.setParent(ship);
        activateShipFromPrintPurchaseOrder.setParentStatusToSet(available);
        activateShipFromPrintPurchaseOrder.setSetIfActiveSiblings(false);
        em.persist(activateShipFromPrintPurchaseOrder);

        ProductChildSequencingAuthorization activatePrintPurchaseOrder = new ProductChildSequencingAuthorization(
                                                                                                                 core);
        activatePrintPurchaseOrder.setParent(ship);
        activatePrintPurchaseOrder.setStatusCode(waitingOnPurchaseOrder);
        activatePrintPurchaseOrder.setNextChild(printPurchaseOrder);
        activatePrintPurchaseOrder.setNextChildStatus(waitingOnFee);
        em.persist(activatePrintPurchaseOrder);

        ProductChildSequencingAuthorization activateFee = new ProductChildSequencingAuthorization(
                                                                                                  core);
        activateFee.setParent(printPurchaseOrder);
        activateFee.setStatusCode(waitingOnFee);
        activateFee.setNextChild(fee);
        activateFee.setNextChildStatus(available);
        em.persist(activateFee);

        ProductSiblingSequencingAuthorization activateDiscount = new ProductSiblingSequencingAuthorization(
                                                                                                           core);
        activateDiscount.setParent(fee);
        activateDiscount.setStatusCode(completed);
        activateDiscount.setNextSibling(discount);
        activateDiscount.setNextSiblingStatus(available);
        em.persist(activateDiscount);

        ProductParentSequencingAuthorization activatePrintPurchaseOrderFromFee = new ProductParentSequencingAuthorization(
                                                                                                                          core);
        activatePrintPurchaseOrderFromFee.setService(fee);
        activatePrintPurchaseOrderFromFee.setStatusCode(completed);
        activatePrintPurchaseOrderFromFee.setParent(printPurchaseOrder);
        activatePrintPurchaseOrderFromFee.setParentStatusToSet(available);
        activatePrintPurchaseOrderFromFee.setSetIfActiveSiblings(false);
        em.persist(activatePrintPurchaseOrderFromFee);

        ProductParentSequencingAuthorization activatePrintPurchaseOrderFromDiscount = new ProductParentSequencingAuthorization(
                                                                                                                               core);
        activatePrintPurchaseOrderFromDiscount.setService(discount);
        activatePrintPurchaseOrderFromDiscount.setStatusCode(completed);
        activatePrintPurchaseOrderFromDiscount.setParent(printPurchaseOrder);
        activatePrintPurchaseOrderFromDiscount.setParentStatusToSet(available);
        activatePrintPurchaseOrderFromDiscount.setSetIfActiveSiblings(false);
        em.persist(activatePrintPurchaseOrderFromDiscount);

        ProductParentSequencingAuthorization completeDeliverFromShip = new ProductParentSequencingAuthorization(
                                                                                                                core);
        completeDeliverFromShip.setService(ship);
        completeDeliverFromShip.setStatusCode(completed);
        completeDeliverFromShip.setParent(deliver);
        completeDeliverFromShip.setParentStatusToSet(completed);
        completeDeliverFromShip.setSetIfActiveSiblings(false);
        em.persist(completeDeliverFromShip);
    }

    public void createProtocols() {

        Protocol pickProtocol = newProtocol();
        pickProtocol.setRequestedService(deliver);
        pickProtocol.setRequester(anyAgency);
        pickProtocol.setRequestedProduct(anyProduct);
        pickProtocol.setDeliverTo(anyLocation);
        pickProtocol.setDeliverFrom(anyLocation);
        pickProtocol.setAssignTo(factory1Agency);
        pickProtocol.setService(pick);
        pickProtocol.setProduct(sameProduct);
        em.persist(pickProtocol);

        Protocol chkCreditProtocol = newProtocol();
        chkCreditProtocol.setRequestedService(pick);
        chkCreditProtocol.setRequester(externalCust);
        chkCreditProtocol.setRequestedProduct(anyProduct);
        chkCreditProtocol.setDeliverTo(us);
        chkCreditProtocol.setDeliverFrom(us);
        chkCreditProtocol.setAssignTo(cpu);
        chkCreditProtocol.setService(checkCredit);
        chkCreditProtocol.setProduct(sameProduct);
        em.persist(chkCreditProtocol);

        Protocol chkLtrCrdtProtocol = newProtocol();
        chkLtrCrdtProtocol.setRequestedService(pick);
        chkLtrCrdtProtocol.setRequester(externalCust);
        chkLtrCrdtProtocol.setRequestedProduct(anyProduct);
        chkLtrCrdtProtocol.setDeliverTo(euro);
        chkLtrCrdtProtocol.setDeliverFrom(us);
        chkLtrCrdtProtocol.setAssignTo(creditDept);
        chkLtrCrdtProtocol.setService(checkLetterOfCredit);
        chkLtrCrdtProtocol.setProduct(sameProduct);
        em.persist(chkLtrCrdtProtocol);

        Protocol shipProtocol = newProtocol();
        shipProtocol.setRequestedService(deliver);
        shipProtocol.setRequester(anyAgency);
        shipProtocol.setRequestedProduct(anyProduct);
        shipProtocol.setDeliverTo(anyLocation);
        shipProtocol.setDeliverFrom(anyLocation);
        shipProtocol.setAssignTo(factory1Agency);
        shipProtocol.setService(ship);
        shipProtocol.setProduct(sameProduct);
        em.persist(shipProtocol);

        Protocol printCustDeclProtocol = newProtocol();
        printCustDeclProtocol.setRequestedService(ship);
        printCustDeclProtocol.setRequester(externalCust);
        printCustDeclProtocol.setRequestedProduct(abc486);
        printCustDeclProtocol.setDeliverTo(euro);
        printCustDeclProtocol.setDeliverFrom(us);
        printCustDeclProtocol.setAssignTo(cpu);
        printCustDeclProtocol.setService(printCustomsDeclaration);
        printCustDeclProtocol.setProduct(sameProduct);
        em.persist(printCustDeclProtocol);

        Protocol printPoProtocol = newProtocol();
        printPoProtocol.setRequestedService(ship);
        printPoProtocol.setRequester(externalCust);
        printPoProtocol.setRequestedProduct(abc486);
        printPoProtocol.setDeliverTo(anyLocation);
        printPoProtocol.setDeliverFrom(us);
        printPoProtocol.setAssignTo(cpu);
        printPoProtocol.setService(printPurchaseOrder);
        printPoProtocol.setProduct(sameProduct);
        em.persist(printPoProtocol);

        Protocol feeProtocol = newProtocol();
        feeProtocol.setRequestedService(printPurchaseOrder);
        feeProtocol.setRequester(anyAgency);
        feeProtocol.setRequestedProduct(abc486);
        feeProtocol.setDeliverTo(anyLocation);
        feeProtocol.setDeliverFrom(us);
        feeProtocol.setAssignTo(billingComputer);
        feeProtocol.setService(fee);
        feeProtocol.setProduct(sameProduct);
        em.persist(feeProtocol);

        Protocol salesTaxProtocol = newProtocol();
        salesTaxProtocol.setRequestedService(fee);
        salesTaxProtocol.setRequester(nonExemptAgency);
        salesTaxProtocol.setRequestedProduct(nonExempt);
        salesTaxProtocol.setDeliverTo(dc);
        salesTaxProtocol.setDeliverFrom(anyLocation);
        salesTaxProtocol.setAssignTo(billingComputer);
        salesTaxProtocol.setService(salesTax);
        salesTaxProtocol.setProduct(sameProduct);
        em.persist(salesTaxProtocol);

        Protocol discountProtocol = newProtocol();
        discountProtocol.setRequestedService(fee);
        discountProtocol.setRequester(externalCust);
        discountProtocol.setRequestedProduct(abc486);
        discountProtocol.setDeliverTo(euro);
        discountProtocol.setDeliverFrom(us);
        discountProtocol.setAssignTo(billingComputer);
        discountProtocol.setService(discount);
        discountProtocol.setProduct(sameProduct);
        em.persist(discountProtocol);

        Protocol gtuDiscountedPriceProtocol = newProtocol();
        gtuDiscountedPriceProtocol.setRequestedService(fee);
        gtuDiscountedPriceProtocol.setRequester(georgeTownUniversity);
        gtuDiscountedPriceProtocol.setRequestedProduct(abc486);
        gtuDiscountedPriceProtocol.setDeliverTo(dc);
        gtuDiscountedPriceProtocol.setDeliverFrom(us);
        gtuDiscountedPriceProtocol.setAssignTo(billingComputer);
        gtuDiscountedPriceProtocol.setService(fee);
        gtuDiscountedPriceProtocol.setProduct(sameProduct);
        em.persist(gtuDiscountedPriceProtocol);
        em.persist(gtuDiscountedPriceProtocol);
    }

    public void createRelationships() {
        area = new Relationship("Area",
                                "A is a member of the economic community B",
                                core, true);
        em.persist(area);
        areaOf = new Relationship("Area Of", "A is economic community of B",
                                  core, area);
        area.setInverse(areaOf);
        em.persist(areaOf);

        city = new Relationship("City", "A is located in the City B", core,
                                true);
        em.persist(city);
        cityOf = new Relationship("City Of", "A is the city of B", core, city);
        city.setInverse(cityOf);
        em.persist(cityOf);

        customerType = new Relationship("Customer Type",
                                        "A has customer type of B", core, true);
        em.persist(customerType);
        customerTypeOf = new Relationship("Customer Type Of",
                                          "A is the customer type of B", core,
                                          customerType);
        customerType.setInverse(customerTypeOf);
        em.persist(customerTypeOf);

        region = new Relationship("Region", "A's general region is B", core,
                                  true);
        em.persist(region);
        regionOf = new Relationship("Region Of", "A is the region of B", core,
                                    region);
        region.setInverse(regionOf);
        em.persist(regionOf);

        state = new Relationship("State", "The State of A is B", core, true);
        em.persist(state);
        stateOf = new Relationship("State Of", "A is the state of B", core,
                                   state);
        state.setInverse(stateOf);
        em.persist(stateOf);

        salesTaxStatus = new Relationship("SalesTaxStatus",
                                          "The sales tax status of A is B",
                                          core, true);
        em.persist(salesTaxStatus);
        salesTaxStatusOf = new Relationship("SalesTaxStatus Of",
                                            "A is the sales tax status of B",
                                            core, salesTaxStatus);
        salesTaxStatus.setInverse(salesTaxStatusOf);
        em.persist(salesTaxStatusOf);

        storageType = new Relationship(
                                       "StorageType",
                                       "The type of storage required for A is B",
                                       core, true);
        em.persist(storageType);
        storageTypeOf = new Relationship("StorageType Of",
                                         "A is the storage type of B", core,
                                         storageType);
        storageType.setInverse(storageTypeOf);
        em.persist(storageTypeOf);

        street = new Relationship("Street", "The street of A is B", core, true);
        em.persist(street);
        streetOf = new Relationship("Street of", "A is the street of B", core,
                                    street);
        street.setInverse(streetOf);
        em.persist(streetOf);
    }

    public void createServices() {
        deliver = new Product("Deliver", "Deliver product", core);
        em.persist(deliver);

        pick = new Product("Pick", "Pick inventory", core);
        em.persist(pick);

        ship = new Product("Ship", "Ship inventory", core);
        em.persist(ship);

        checkCredit = new Product("CheckCredit",
                                  "Check customer inhouse credit", core);
        em.persist(checkCredit);

        checkLetterOfCredit = new Product("CheckLetterOfCredit",
                                          "Check customer letter of credit",
                                          core);
        em.persist(checkLetterOfCredit);

        discount = new Product("Discount", "Compute fee discount ", core);
        em.persist(discount);

        fee = new Product("Fee", "Compute fee", core);
        em.persist(fee);

        printCustomsDeclaration = new Product("PrintCustomsDeclaration",
                                              "Print the customs declaration",
                                              core);
        em.persist(printCustomsDeclaration);

        printPurchaseOrder = new Product("PrintPurchaseOrder",
                                         "Print the purchase order", core);
        em.persist(printPurchaseOrder);

        salesTax = new Product("SalesTax", "Compute sales tax", core);
        em.persist(salesTax);
    }

    public void createStatusCodes() {
        available = new StatusCode("Available",
                                   "The job is available for execution", core);
        em.persist(available);

        active = new StatusCode("Active", "Working on it now", core);
        em.persist(active);

        waitingOnCreditCheck = new StatusCode(
                                              "Waiting on Credit Check",
                                              "Waiting for credit check to be completed",
                                              core);
        em.persist(waitingOnCreditCheck);

        completed = new StatusCode("Completed", "Completed Job", core);
        completed.setPropagateChildren(true);
        em.persist(completed);

        failure = new StatusCode("Failure", "Something went wrong", core);
        failure.setFailParent(true);
        em.persist(failure);

        pickCompleted = new StatusCode("Pick Completed",
                                       "Pick product has been completed", core);
        em.persist(pickCompleted);

        waitingOnPurchaseOrder = new StatusCode(
                                                "Waiting on purchase order",
                                                "Waiting for purchase order to be completed",
                                                core);
        em.persist(waitingOnPurchaseOrder);

        waitingOnPricing = new StatusCode(
                                          "Waiting on pricing",
                                          "Waiting for pricing to be completed",
                                          core);
        em.persist(waitingOnPricing);

        waitingOnFee = new StatusCode(
                                      "Waiting on fee calculation",
                                      "Waiting for fee calculation to be completed",
                                      core);
        em.persist(waitingOnFee);

        abandoned = new StatusCode(
                                   "Abandoned",
                                   "We were going to do it, something happened in earlier processing that will prevent us.  This can be garbage-collected now",
                                   core);
        em.persist(abandoned);
    }

    public void createStatusCodeSequencing() {
        StatusCodeSequencing s = new StatusCodeSequencing(pick,
                                                          waitingOnCreditCheck,
                                                          available, core);
        em.persist(s);

        s = new StatusCodeSequencing(pick, available, active, core);
        em.persist(s);

        s = new StatusCodeSequencing(pick, active, completed, core);
        em.persist(s);

        s = new StatusCodeSequencing(ship, waitingOnPurchaseOrder, available,
                                     core);
        em.persist(s);

        s = new StatusCodeSequencing(ship, available, active, core);
        em.persist(s);

        s = new StatusCodeSequencing(ship, active, completed, core);
        em.persist(s);

        s = new StatusCodeSequencing(deliver, available, active, core);
        em.persist(s);

        s = new StatusCodeSequencing(deliver, active, completed, core);
        em.persist(s);

        s = new StatusCodeSequencing(checkCredit, available, active, core);
        em.persist(s);

        s = new StatusCodeSequencing(checkCredit, active, completed, core);
        em.persist(s);

        s = new StatusCodeSequencing(checkLetterOfCredit, available, active,
                                     core);
        em.persist(s);

        s = new StatusCodeSequencing(checkLetterOfCredit, active, completed,
                                     core);
        em.persist(s);

        s = new StatusCodeSequencing(discount, available, active, core);
        em.persist(s);

        s = new StatusCodeSequencing(discount, active, completed, core);
        em.persist(s);

        s = new StatusCodeSequencing(fee, waitingOnFee, available, core);
        em.persist(s);

        s = new StatusCodeSequencing(fee, available, active, core);
        em.persist(s);

        s = new StatusCodeSequencing(fee, active, completed, core);
        em.persist(s);

        s = new StatusCodeSequencing(printCustomsDeclaration, waitingOnFee,
                                     available, core);
        em.persist(s);

        s = new StatusCodeSequencing(printCustomsDeclaration, available,
                                     active, core);
        em.persist(s);

        s = new StatusCodeSequencing(printCustomsDeclaration, active,
                                     completed, core);
        em.persist(s);

        s = new StatusCodeSequencing(printPurchaseOrder, waitingOnFee,
                                     available, core);
        em.persist(s);

        s = new StatusCodeSequencing(printPurchaseOrder, available, active,
                                     core);
        em.persist(s);

        s = new StatusCodeSequencing(printPurchaseOrder, active, completed,
                                     core);
        em.persist(s);

        s = new StatusCodeSequencing(salesTax, available, active, core);
        em.persist(s);

        s = new StatusCodeSequencing(salesTax, active, completed, core);
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

    private Protocol newProtocol() {
        Protocol p = new Protocol(core);
        p.setRequesterAttribute(kernel.getAnyAttribute());
        p.setDeliverToAttribute(kernel.getAnyAttribute());
        p.setDeliverFromAttribute(kernel.getAnyAttribute());
        p.setAssignToAttribute(kernel.getAnyAttribute());
        p.setServiceAttribute(kernel.getAnyAttribute());
        p.setProductAttribute(kernel.getAnyAttribute());
        return p;
    }
}
