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
package com.hellblazer.CoRE.workspace.api;

import javax.persistence.EntityManager;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.ValueType;
import com.hellblazer.CoRE.event.MetaProtocol;
import com.hellblazer.CoRE.event.ProductChildSequencingAuthorization;
import com.hellblazer.CoRE.event.ProductParentSequencingAuthorization;
import com.hellblazer.CoRE.event.ProductSiblingSequencingAuthorization;
import com.hellblazer.CoRE.event.Protocol;
import com.hellblazer.CoRE.event.ProtocolAttribute;
import com.hellblazer.CoRE.event.status.StatusCode;
import com.hellblazer.CoRE.event.status.StatusCodeSequencing;
import com.hellblazer.CoRE.kernel.Kernel;
import com.hellblazer.CoRE.kernel.KernelImpl;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.network.NetworkInference;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;

/**
 * @author hhildebrand
 * 
 */
public class WorkspaceLoader {

    public Relationship         area;
    public Relationship         areaOf;
    public Relationship         city;
    public Relationship         cityOf;
    public Relationship         customerType;
    public Relationship         customerTypeOf;
    public Relationship         region;
    public Relationship         regionOf;
    public Relationship         state;
    public Relationship         stateOf;
    public Relationship         salesTaxStatus;
    public Relationship         salesTaxStatusOf;
    public Relationship         storageType;
    public Relationship         storageTypeOf;
    public Relationship         street;
    public Relationship         streetOf;
    public Relationship         inWorkspace;
    public Relationship         workspaceOf;
    private Relationship        notApplicableRelationship;
    private Relationship        sameRelationship;
    private Relationship        anyRelationship;

    public StatusCode           unset;
    public StatusCode           abandoned;
    public StatusCode           completed;
    public StatusCode           failure;
    public StatusCode           active;
    public StatusCode           available;
    public StatusCode           pickCompleted;
    public StatusCode           waitingOnFee;
    public StatusCode           waitingOnPricing;
    public StatusCode           waitingOnPurchaseOrder;
    public StatusCode           waitingOnCreditCheck;

    public Product              abc486;
    public Product              checkCredit;
    public Product              checkLetterOfCredit;
    public Product              chemB;
    public Product              deliver;
    public Product              discount;
    public Product              frozen;
    public Product              fee;
    public Product              printCustomsDeclaration;
    public Product              printPurchaseOrder;
    public Product              roomTemp;
    public Product              pick;
    public Product              salesTax;
    public Product              ship;
    public Product              nonExempt;
    public Product              workspace;
    private Product             anyProduct;
    private Product             sameProduct;

    public Location             bht378;
    public Location             bin1;
    public Location             bin15;
    public Location             dc;
    public Location             east_coast;
    public Location             euro;
    public Location             france;
    public Location             paris;
    public Location             rsb225;
    public Location             factory1;
    public Location             us;
    private Location            anyLocation;

    public Agency               billingComputer;
    public Agency               core;
    public Agency               cpu;
    public Agency               creditDept;
    public Agency               exempt;
    public Agency               externalCust;
    public Agency               factory1Agency;
    public Agency               georgeTownUniversity;
    public Agency               manufacturer;
    public Agency               nonExemptAgency;
    public Agency               orderFullfillment;
    public Agency               orgA;
    private Agency              anyAgency;

    public Attribute            priceAttribute;
    public Attribute            taxRateAttribute;
    public Attribute            discountAttribute;

    private final EntityManager em;
    private final Kernel        kernel;
    public ProtocolAttribute    price;

    public WorkspaceLoader(EntityManager em) throws Exception {
        this.em = em;
        kernel = new KernelImpl(em);
        core = kernel.getCore();
        sameProduct = kernel.getSameProduct();
        anyProduct = kernel.getAnyProduct();
        anyAgency = kernel.getAnyAgency();
        anyLocation = kernel.getAnyLocation();
        sameRelationship = kernel.getSameRelationship();
        anyRelationship = kernel.getAnyRelationship();
        notApplicableRelationship = kernel.getNotApplicableRelationship();
        workspace = kernel.getWorkspace();
        inWorkspace = kernel.getInWorkspace();
        workspaceOf = kernel.getWorkspaceOf();
        unset = kernel.getUnset();
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
                                       ValueType.NUMERIC);
        em.persist(priceAttribute);

        taxRateAttribute = new Attribute("tax rate", "tax rate", core,
                                         ValueType.NUMERIC);
        em.persist(taxRateAttribute);

        discountAttribute = new Attribute("discount", "discount", core,
                                          ValueType.NUMERIC);
        em.persist(discountAttribute);
    }

    public void createLocations() {
        rsb225 = new Location("225RSB", "225 Reiss Science Bldg", core);
        em.persist(rsb225);
        bht378 = new Location("37BHT", "37 Bret Harte Terrace", core);
        em.persist(bht378);

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
        MetaProtocol m1 = new MetaProtocol(deliver, 1,
                                           notApplicableRelationship,
                                           sameRelationship, sameRelationship,
                                           state, area, core);
        em.persist(m1);
        MetaProtocol m2 = new MetaProtocol(pick, 1, notApplicableRelationship,
                                           customerType, sameRelationship,
                                           area, area, core);
        em.persist(m2);
        MetaProtocol m3 = new MetaProtocol(ship, 1, notApplicableRelationship,
                                           customerType, anyRelationship, area,
                                           area, core);
        em.persist(m3);
        MetaProtocol m5 = new MetaProtocol(salesTax, 1,
                                           notApplicableRelationship,
                                           salesTaxStatus, sameRelationship,
                                           state, anyRelationship, core);
        em.persist(m5);
        MetaProtocol m6 = new MetaProtocol(printPurchaseOrder, 1,
                                           notApplicableRelationship,
                                           anyRelationship, anyRelationship,
                                           anyRelationship, anyRelationship,
                                           core);
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
    }

    public void createProtocols() {

        Protocol pickProtocol = new Protocol(deliver, anyAgency, anyProduct,
                                             anyLocation, anyLocation,
                                             factory1Agency, pick, sameProduct,
                                             core);
        em.persist(pickProtocol);

        Protocol chkCreditProtocol = new Protocol(pick, externalCust,
                                                  anyProduct, us, us, cpu,
                                                  checkCredit, sameProduct,
                                                  core);
        em.persist(chkCreditProtocol);

        Protocol chkLtrCrdtProtocol = new Protocol(pick, externalCust,
                                                   anyProduct, euro, us,
                                                   creditDept,
                                                   checkLetterOfCredit,
                                                   sameProduct, core);
        em.persist(chkLtrCrdtProtocol);

        Protocol shipProtocol = new Protocol(deliver, anyAgency, anyProduct,
                                             anyLocation, anyLocation,
                                             factory1Agency, ship, sameProduct,
                                             true, core);
        em.persist(shipProtocol);

        Protocol printCustDeclProtocol = new Protocol(ship, externalCust,
                                                      abc486, euro, us, cpu,
                                                      printCustomsDeclaration,
                                                      sameProduct, core);
        em.persist(printCustDeclProtocol);

        Protocol printPoProtocol = new Protocol(ship, externalCust, abc486,
                                                anyLocation, us, cpu,
                                                printPurchaseOrder,
                                                sameProduct, core);
        em.persist(printPoProtocol);

        Protocol feeProtocol = new Protocol(printPurchaseOrder, externalCust,
                                            abc486, anyLocation, us,
                                            billingComputer, fee, sameProduct,
                                            core);
        em.persist(feeProtocol);

        price = new ProtocolAttribute(priceAttribute, core);
        price.setNumericValue(1500);
        price.setProtocol(feeProtocol);
        em.persist(price);

        Protocol salesTaxProtocol = new Protocol(fee, nonExemptAgency,
                                                 nonExempt, dc, anyLocation,
                                                 billingComputer, salesTax,
                                                 sameProduct, core);
        em.persist(salesTaxProtocol);

        ProtocolAttribute taxRate = new ProtocolAttribute(taxRateAttribute,
                                                          core);
        taxRate.setNumericValue(0.0575);
        taxRate.setProtocol(salesTaxProtocol);
        em.persist(taxRate);

        Protocol discountProtocol = new Protocol(fee, externalCust, abc486,
                                                 euro, us, billingComputer,
                                                 discount, sameProduct, core);
        em.persist(discountProtocol);

        ProtocolAttribute euroDiscount = new ProtocolAttribute(
                                                               discountAttribute,
                                                               core);
        euroDiscount.setNumericValue(0.05);
        euroDiscount.setProtocol(salesTaxProtocol);
        em.persist(euroDiscount);

        Protocol gtuDiscountedPriceProtocol = new Protocol(
                                                           fee,
                                                           georgeTownUniversity,
                                                           abc486, dc, us,
                                                           billingComputer,
                                                           fee, sameProduct,
                                                           core);
        em.persist(gtuDiscountedPriceProtocol);

        ProtocolAttribute discountedPrice = new ProtocolAttribute(
                                                                  priceAttribute,
                                                                  core);
        discountedPrice.setNumericValue(1250);
        discountedPrice.setProtocol(gtuDiscountedPriceProtocol);
        em.persist(discountedPrice);
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
        createProtocols();
        createMetaProtocols();
        createStatusCodes();
        createStatusCodeSequencing();
        createProductSequencingAuthorizations();
    }
}
