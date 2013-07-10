/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
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
package com.hellblazer.CoRE.example.orderProcessing;

import javax.persistence.EntityManager;

import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.ValueType;
import com.hellblazer.CoRE.event.MetaProtocol;
import com.hellblazer.CoRE.event.ProductChildSequencingAuthorization;
import com.hellblazer.CoRE.event.ProductParentSequencingAuthorization;
import com.hellblazer.CoRE.event.ProductSiblingSequencingAuthorization;
import com.hellblazer.CoRE.event.Protocol;
import com.hellblazer.CoRE.event.ProtocolAttribute;
import com.hellblazer.CoRE.event.StatusCode;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.meta.Kernel;
import com.hellblazer.CoRE.meta.Model;
import com.hellblazer.CoRE.meta.models.ModelImpl;
import com.hellblazer.CoRE.network.NetworkInference;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.resource.Resource;
import com.hellblazer.CoRE.resource.ResourceNetwork;

/**
 * @author hhildebrand
 * 
 */
public class ExampleLoader {
    public StatusCode           abandoned;

    public Product              abc486;
    public StatusCode           active;
    public Relationship         area;
    public StatusCode           available;
    public Location             bht378;
    public Resource             billingComputer;
    public Location             bin1;
    public Location             bin15;

    public Product              checkCredit;

    public Product              checkLetterOfCredit;
    public Product              chemB;
    public Relationship         city;
    public StatusCode           completed;
    public Resource             core;
    public Resource             cpu;
    public Resource             creditDept;
    public Relationship         customerType;
    public Location             dc;
    public Product              deliver;
    public Product              discount;

    public Location             east_coast;
    public Location             euro;
    public Resource             exempt;
    public Resource             externalCust;
    public Location             factory1;
    public Resource             factory1Resource;
    public StatusCode           failure;
    public Product              fee;
    public Location             france;
    public Product              frozen;
    public Resource             georgeTownUniversity;
    public Resource             manufacturer;
    public Product              nonExempt;
    public Resource             nonExemptResource;

    public Resource             orderFullfillment;
    public Resource             orgA;
    public Location             paris;
    public Product              pick;
    public StatusCode           pickCompleted;
    public Attribute            priceAttribute;
    public Product              printCustomsDeclaration;
    public Product              printPurchaseOrder;
    public Relationship         region;
    public Product              roomTemp;
    public Location             rsb225;

    public Product              salesTax;
    public Relationship         salesTaxStatus;
    public Product              ship;
    public Relationship         state;
    public Relationship         storageType;
    public Relationship         street;

    public Attribute            taxRateAttribute;
    public StatusCode           unset;
    public Location             us;
    public StatusCode           waitingOnFee;
    public StatusCode           waitingOnPricing;
    public StatusCode           waitingOnPurchaseOrder;
    public Location             wash;
    private Location            anyLocation;
    private Product             anyProduct;
    private Relationship        anyRelationship;
    private Resource            anyResource;
    private StatusCode          creditChecked;
    private Attribute           discountAttribute;

    private final EntityManager em;
    private final Kernel        kernel;
    private final Model         model;
    private Relationship        notApplicableRelationship;
    private Product             sameProduct;
    private Relationship        sameRelationship;

    public ExampleLoader(EntityManager em) throws Exception {
        this.em = em;
        model = new ModelImpl(em);
        kernel = model.getKernel();
        core = kernel.getCore();
        sameProduct = kernel.getSameProduct();
        anyProduct = kernel.getAnyProduct();
        anyResource = kernel.getAnyResource();
        anyLocation = kernel.getAnyLocation();
        sameRelationship = kernel.getSameRelationship();
        anyRelationship = kernel.getAnyRelationship();
        notApplicableRelationship = kernel.getNotApplicableRelationship();
        unset = kernel.getUnset();
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

    public void createLocationNetworks() {
        model.getLocationModel().link(bin1, area, factory1, core);
        model.getLocationModel().link(bin15, area, factory1, core);
        model.getLocationModel().link(factory1, street, bht378, core);
        model.getLocationModel().link(rsb225, city, wash, core);
        model.getLocationModel().link(bht378, city, wash, core);
        model.getLocationModel().link(wash, state, dc, core);
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
        wash = new Location("WASH", "Washington", core);
        em.persist(wash);
        euro = new Location("Euro", "European locations", core);
        em.persist(euro);
    }

    public void createMetaProtocols() {
        MetaProtocol m1 = new MetaProtocol(deliver, 1,
                                           notApplicableRelationship,
                                           sameRelationship, sameRelationship,
                                           state, area, core);
        em.persist(m1);
        MetaProtocol m2 = new MetaProtocol(deliver, 2,
                                           notApplicableRelationship,
                                           customerType, sameRelationship,
                                           area, area, core);
        em.persist(m2);
        MetaProtocol m3 = new MetaProtocol(deliver, 3,
                                           notApplicableRelationship,
                                           customerType, anyRelationship, area,
                                           area, core);
        em.persist(m3);
        MetaProtocol m4 = new MetaProtocol(deliver, 4,
                                           notApplicableRelationship,
                                           customerType, sameRelationship,
                                           anyRelationship, area, core);
        em.persist(m4);
        MetaProtocol m5 = new MetaProtocol(deliver, 5,
                                           notApplicableRelationship,
                                           salesTaxStatus, sameRelationship,
                                           state, anyRelationship, core);
        em.persist(m5);
        MetaProtocol m6 = new MetaProtocol(deliver, 6,
                                           notApplicableRelationship,
                                           anyRelationship, anyRelationship,
                                           anyRelationship, anyRelationship,
                                           core);
        em.persist(m6);
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
        ProductChildSequencingAuthorization activatePick = new ProductChildSequencingAuthorization(
                                                                                                   core);
        activatePick.setParent(deliver);
        activatePick.setStatusCode(active);
        activatePick.setNextChild(pick);
        activatePick.setNextChildStatus(available);
        em.persist(activatePick);

        ProductSiblingSequencingAuthorization activatePrintCustomsDeclaration = new ProductSiblingSequencingAuthorization(
                                                                                                                          core);
        activatePrintCustomsDeclaration.setParent(printPurchaseOrder);
        activatePrintCustomsDeclaration.setStatusCode(completed);
        activatePrintCustomsDeclaration.setNextSibling(printCustomsDeclaration);
        activatePrintCustomsDeclaration.setNextSiblingStatus(available);
        em.persist(activatePrintCustomsDeclaration);

        ProductParentSequencingAuthorization productPicked = new ProductParentSequencingAuthorization(
                                                                                                      core);
        productPicked.setParent(pick);
        productPicked.setStatusCode(completed);
        productPicked.setMyParent(deliver);
        productPicked.setParentStatusToSet(completed);
        productPicked.setSetIfActiveSiblings(false);
        em.persist(productPicked);

        ProductParentSequencingAuthorization checkCreditCompleted = new ProductParentSequencingAuthorization(
                                                                                                             core);
        checkCreditCompleted.setParent(checkCredit);
        checkCreditCompleted.setStatusCode(completed);
        checkCreditCompleted.setMyParent(pick);
        checkCreditCompleted.setParentStatusToSet(creditChecked);
        em.persist(checkCreditCompleted);

        ProductParentSequencingAuthorization checkLetterOfCreditCompleted = new ProductParentSequencingAuthorization(
                                                                                                                     core);
        checkLetterOfCreditCompleted.setParent(checkLetterOfCredit);
        checkLetterOfCreditCompleted.setStatusCode(completed);
        checkLetterOfCreditCompleted.setMyParent(pick);
        checkLetterOfCreditCompleted.setParentStatusToSet(creditChecked);
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
        activateShipFromPrintCustomsDeclaration.setParent(printCustomsDeclaration);
        activateShipFromPrintCustomsDeclaration.setStatusCode(completed);
        activateShipFromPrintCustomsDeclaration.setMyParent(ship);
        activateShipFromPrintCustomsDeclaration.setParentStatusToSet(available);
        activateShipFromPrintCustomsDeclaration.setSetIfActiveSiblings(false);
        em.persist(activateShipFromPrintCustomsDeclaration);

        ProductParentSequencingAuthorization activateShipFromPrintPurchaseOrder = new ProductParentSequencingAuthorization(
                                                                                                                           core);
        activateShipFromPrintPurchaseOrder.setParent(printPurchaseOrder);
        activateShipFromPrintPurchaseOrder.setStatusCode(completed);
        activateShipFromPrintPurchaseOrder.setMyParent(ship);
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
        activateFee.setNextChildStatus(active);
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
        activatePrintPurchaseOrderFromFee.setParent(fee);
        activatePrintPurchaseOrderFromFee.setStatusCode(completed);
        activatePrintPurchaseOrderFromFee.setMyParent(printPurchaseOrder);
        activatePrintPurchaseOrderFromFee.setParentStatusToSet(available);
        activatePrintPurchaseOrderFromFee.setSetIfActiveSiblings(false);
        em.persist(activatePrintPurchaseOrderFromFee);

        ProductParentSequencingAuthorization activatePrintPurchaseOrderFromDiscount = new ProductParentSequencingAuthorization(
                                                                                                                               core);
        activatePrintPurchaseOrderFromDiscount.setParent(discount);
        activatePrintPurchaseOrderFromDiscount.setStatusCode(completed);
        activatePrintPurchaseOrderFromDiscount.setMyParent(printPurchaseOrder);
        activatePrintPurchaseOrderFromDiscount.setParentStatusToSet(available);
        activatePrintPurchaseOrderFromDiscount.setSetIfActiveSiblings(false);
        em.persist(activatePrintPurchaseOrderFromDiscount);
    }

    public void createProtocols() {

        Protocol pickProtocol = new Protocol(deliver, anyResource, anyProduct,
                                             anyLocation, anyLocation,
                                             factory1Resource, pick,
                                             sameProduct, core);
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

        Protocol shipProtocol = new Protocol(deliver, anyResource, anyProduct,
                                             anyLocation, anyLocation,
                                             factory1Resource, ship,
                                             sameProduct, true, core);
        em.persist(shipProtocol);

        Protocol printCustDeclProtocol = new Protocol(ship, externalCust,
                                                      abc486, euro, us, cpu,
                                                      printCustomsDeclaration,
                                                      sameProduct, core);
        em.persist(printCustDeclProtocol);

        Protocol printPoProtocol = new Protocol(ship, externalCust, abc486,
                                                euro, us, cpu,
                                                printPurchaseOrder,
                                                sameProduct, core);
        em.persist(printPoProtocol);

        Protocol feeProtocol = new Protocol(printPurchaseOrder, externalCust,
                                            abc486, anyLocation, us,
                                            billingComputer, fee, sameProduct,
                                            core);
        em.persist(feeProtocol);

        ProtocolAttribute price = new ProtocolAttribute(priceAttribute, core);
        price.setNumericValue(1500);
        price.setProtocol(feeProtocol);
        em.persist(price);

        Protocol salesTaxProtocol = new Protocol(fee, nonExemptResource,
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
        Relationship areaOf = new Relationship("Area Of",
                                               "A is economic community of B",
                                               core, area);
        area.setInverse(areaOf);
        em.persist(areaOf);

        city = new Relationship("City", "A is located in the City B", core,
                                true);
        em.persist(city);
        Relationship cityOf = new Relationship("City Of", "A is the city of B",
                                               core, city);
        city.setInverse(cityOf);
        em.persist(cityOf);

        customerType = new Relationship("Customer Type",
                                        "A has customer type of B", core, true);
        em.persist(customerType);
        Relationship customerTypeOf = new Relationship(
                                                       "Customer Type Of",
                                                       "A is the customer type of B",
                                                       core, customerType);
        customerType.setInverse(customerTypeOf);
        em.persist(customerTypeOf);

        region = new Relationship("Region", "A's general region is B", core,
                                  true);
        em.persist(region);
        Relationship regionOf = new Relationship("Region Of",
                                                 "A is the region of B", core,
                                                 region);
        region.setInverse(regionOf);
        em.persist(regionOf);

        state = new Relationship("State", "The State of A is B", core, true);
        em.persist(state);
        Relationship stateOf = new Relationship("State Of",
                                                "A is the state of B", core,
                                                state);
        state.setInverse(stateOf);
        em.persist(stateOf);

        salesTaxStatus = new Relationship("SalesTaxStatus",
                                          "The sales tax status of A is B",
                                          core, true);
        em.persist(salesTaxStatus);
        Relationship salesTaxStatusOf = new Relationship(
                                                         "SalesTaxStatus Of",
                                                         "A is the sales tax status of B",
                                                         core, salesTaxStatus);
        salesTaxStatus.setInverse(salesTaxStatusOf);
        em.persist(salesTaxStatusOf);

        storageType = new Relationship(
                                       "StorageType",
                                       "The type of storage required for A is B",
                                       core, true);
        em.persist(storageType);
        Relationship storageTypeOf = new Relationship(
                                                      "StorageType Of",
                                                      "A is the storage type of B",
                                                      core, storageType);
        storageType.setInverse(storageTypeOf);
        em.persist(storageTypeOf);

        street = new Relationship("Street", "The street of A is B", core, true);
        em.persist(street);
        Relationship streetOf = new Relationship("Street of",
                                                 "A is the street of B", core,
                                                 street);
        street.setInverse(streetOf);
        em.persist(streetOf);
    }

    public void createResourceNetwork() {
        em.persist(new ResourceNetwork());
    }

    public void createResourceNetworks() {
        model.getResourceModel().link(georgeTownUniversity, customerType,
                                      externalCust, core);
        model.getResourceModel().link(georgeTownUniversity, salesTaxStatus,
                                      exempt, core);
        model.getResourceModel().link(orgA, customerType, externalCust, core);
        model.getResourceModel().link(orgA, salesTaxStatus, nonExemptResource,
                                      core);
    }

    public void createResources() {
        billingComputer = new Resource("Billing CPU", "The Billing Computer",
                                       core);
        em.persist(billingComputer);

        cpu = new Resource("CPU", "Computer", core);
        em.persist(cpu);

        creditDept = new Resource("Credit", "Credit Department", core);
        em.persist(creditDept);

        exempt = new Resource("Exempt", "Exempt from sales taxes", core);
        em.persist(exempt);

        externalCust = new Resource("Ext Customer",
                                    "External (Paying) Customer", core);
        em.persist(externalCust);

        factory1Resource = new Resource("Factory1", "Factory #1", core);
        em.persist(factory1Resource);

        georgeTownUniversity = new Resource("GU", "Georgetown University", core);
        em.persist(georgeTownUniversity);

        manufacturer = new Resource("MNFR", "Manufacturer", core);
        em.persist(manufacturer);

        nonExemptResource = new Resource("NonExempt", "Subject to sales taxes",
                                         core);
        em.persist(nonExemptResource);

        orgA = new Resource("OrgA", "Organization A", core);
        em.persist(orgA);

        orderFullfillment = new Resource("Order Fullfillment",
                                         "Order Fullfillment", core);
        em.persist(orderFullfillment);
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

        creditChecked = new StatusCode("Credit Checked",
                                       "Credit has been checked", core);
        em.persist(active);

        completed = new StatusCode("Credit Check Completed",
                                   "Completed Credit Check", core);
        completed.setPropagateChildren(true);
        em.persist(completed);

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

    public void load() {
        createResources();
        createAttributes();
        createProducts();
        createServices();
        createLocations();
        createRelationships();
        createNetworkInferences();
        createProductNetworks();
        createResourceNetworks();
        createLocationNetworks();
        createProtocols();
        createMetaProtocols();
        createStatusCodes();
        // createSequencingAuthorizations();
    }

    public void createNetworkInferences() {
        NetworkInference areaToRegion = new NetworkInference(area, region,
                                                             region, core);
        em.persist(areaToRegion);
        NetworkInference regionToState = new NetworkInference(region, state,
                                                              region, core);
        em.persist(regionToState);
        NetworkInference stateToCity = new NetworkInference(state, city, state,
                                                            core);
        em.persist(stateToCity);
        NetworkInference cityToStreet = new NetworkInference(city, street,
                                                             city, core);
        em.persist(cityToStreet);
    }
}
