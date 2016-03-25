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
import com.chiralbehaviors.CoRE.jooq.tables.ChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.NetworkInference;
import com.chiralbehaviors.CoRE.jooq.tables.ParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.SiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.jooq.tables.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.jooq.tables.records.MetaProtocolRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ProtocolRecord;
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
        em.persist(billingComputer);

        cpu = model.records()
                   .newAgency("CPU", "Computer");
        em.persist(cpu);

        creditDept = model.records()
                          .newAgency("Credit", "Credit Department");
        em.persist(creditDept);

        exempt = model.records()
                      .newAgency("Exempt", "Exempt from sales taxes");
        em.persist(exempt);

        externalCust = model.records()
                            .newAgency("Ext Customer",
                                       "External (Paying) Customer");
        em.persist(externalCust);

        factory1Agency = model.records()
                              .newAgency("Factory1Agency", "Factory #1");
        em.persist(factory1Agency);

        georgeTownUniversity = model.records()
                                    .newAgency("GeorgetownUniversity",
                                               "Georgetown University");
        em.persist(georgeTownUniversity);

        cafleurBon = model.records()
                          .newAgency("CarfleurBon", "Carfleur Bon");
        em.persist(cafleurBon);

        manufacturer = model.records()
                            .newAgency("MNFR", "Manufacturer");
        em.persist(manufacturer);

        nonExemptAgency = model.records()
                               .newAgency("NonExemptAgency",
                                          "Subject to sales taxes");
        em.persist(nonExemptAgency);

        orgA = model.records()
                    .newAgency("OrgA", "Organization A");
        em.persist(orgA);

        orderFullfillment = model.records()
                                 .newAgency("OrderFullfillment",
                                            "Order Fullfillment");
        em.persist(orderFullfillment);
    }

    public void createAttributes() {
        priceAttribute = model.records()
                              .newAttribute("price", "price",
                                            ValueType.Integer);
        em.persist(priceAttribute);

        taxRateAttribute = model.records()
                                .newAttribute("tax rate", "tax rate",
                                              ValueType.Integer);
        em.persist(taxRateAttribute);

        discountAttribute = model.records()
                                 .newAttribute("discount", "discount",
                                               ValueType.Integer);
        em.persist(discountAttribute);
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
        em.persist(rsb225);
        bht37 = model.records()
                     .newLocation("BHT37", "37 Bret Harte Terrace");
        em.persist(bht37);

        rc31 = model.records()
                    .newLocation("RC31", "31 Rue Cambon");
        em.persist(rc31);

        bin1 = model.records()
                    .newLocation("BIN01", "Bin #1");
        em.persist(bin1);
        bin15 = model.records()
                     .newLocation("BIN15", "Bin #15");
        em.persist(bin15);
        dc = model.records()
                  .newLocation("DC", "District of Columbia");
        em.persist(dc);
        east_coast = model.records()
                          .newLocation("EAST_COAST", "East Coast");
        em.persist(east_coast);
        factory1 = model.records()
                        .newLocation("Factory1", "Factory 1");
        em.persist(factory1);
        france = model.records()
                      .newLocation("FRANCE", "France");
        em.persist(france);
        paris = model.records()
                     .newLocation("PARIS", "Paris");
        em.persist(paris);
        us = model.records()
                  .newLocation("US", "U.S. Locations");
        em.persist(us);
        euro = model.records()
                    .newLocation("Euro", "European locations");
        em.persist(euro);
    }

    public void createMetaProtocol() {
        MetaProtocolRecord m1 = model.getJobModel()
                                     .newInitializedMetaProtocolRecord(deliver);
        m1.setSequenceNumber(1);
        m1.setProduct(anyRelationship);
        m1.setDeliverTo(state);
        m1.setDeliverFrom(area);

        em.persist(m1);

        MetaProtocolRecord m2 = model.getJobModel()
                                     .newInitializedMetaProtocolRecord(pick);
        m2.setSequenceNumber(1);
        m2.setProduct(anyRelationship);
        m2.setRequester(customerType);
        m2.setDeliverTo(area);
        m2.setDeliverFrom(area);

        em.persist(m2);

        MetaProtocolRecord m3 = model.getJobModel()
                                     .newInitializedMetaProtocolRecord(ship);
        m3.setSequenceNumber(1);
        m3.setProduct(anyRelationship);
        m3.setRequester(customerType);
        m3.setDeliverTo(area);
        m3.setDeliverFrom(area);

        MetaProtocolRecord m5 = model.getJobModel()
                                     .newInitializedMetaProtocolRecord(fee);
        m5.setSequenceNumber(1);
        m5.setProduct(anyRelationship);
        m5.setRequester(salesTaxStatus);
        m5.setDeliverTo(city);

        em.persist(m5);

        MetaProtocolRecord m6 = model.getJobModel()
                                     .newInitializedMetaProtocolRecord(printPurchaseOrder,
                                                                       core);
        m6.setSequenceNumber(1);
        m6.setProduct(anyRelationship);
        m6.setRequester(anyRelationship);
        m6.setDeliverTo(anyRelationship);
        m6.setDeliverFrom(area);

        em.persist(m6);
    }

    public void createNetworkNetworkInferences() {
        NetworkInference areaToRegion = model.records()
                                             .newNetworkNetworkInference(areaOf,
                                                                         regionOf,
                                                                         areaOf);
        em.persist(areaToRegion);

        NetworkInference areaToState = model.records()
                                            .newNetworkNetworkInference(areaOf,
                                                                        stateOf,
                                                                        areaOf);
        em.persist(areaToState);

        NetworkInference areaToCity = model.records()
                                           .newNetworkNetworkInference(areaOf,
                                                                       cityOf,
                                                                       areaOf);
        em.persist(areaToCity);

        NetworkInference areaToStreet = model.records()
                                             .newNetworkNetworkInference(areaOf,
                                                                         streetOf,
                                                                         areaOf);
        em.persist(areaToStreet);

        NetworkInference regionToState = model.records()
                                              .newNetworkNetworkInference(regionOf,
                                                                          stateOf,
                                                                          regionOf);
        em.persist(regionToState);

        NetworkInference regionToCity = model.records()
                                             .newNetworkNetworkInference(regionOf,
                                                                         cityOf,
                                                                         regionOf);
        em.persist(regionToCity);

        NetworkInference regionToStreet = model.records()
                                               .newNetworkNetworkInference(regionOf,
                                                                           streetOf,
                                                                           regionOf);
        em.persist(regionToStreet);

        NetworkInference stateToCity = model.records()
                                            .newNetworkNetworkInference(stateOf,
                                                                        cityOf,
                                                                        stateOf);
        em.persist(stateToCity);

        NetworkInference stateToStreet = model.records()
                                              .newNetworkNetworkInference(stateOf,
                                                                          streetOf,
                                                                          stateOf);
        em.persist(stateToStreet);

        NetworkInference cityToStreet = model.records()
                                             .newNetworkNetworkInference(cityOf,
                                                                         streetOf,
                                                                         cityOf);
        em.persist(cityToStreet);
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
        em.persist(abc486);
        frozen = model.records()
                      .newProduct("Frozen", "Frozen products");
        em.persist(frozen);
        nonExempt = model.records()
                         .newProduct("NonExempt", "Subject to sales tax");
        em.persist(nonExempt);
        chemB = model.records()
                     .newProduct("ChemB", "Chemical B");
        em.persist(chemB);
        roomTemp = model.records()
                        .newProduct("RoomTemp", "Room temperature products");
        em.persist(roomTemp);
        orderEntryWorkspace = model.records()
                                   .newProduct("defining product for order entry",
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

        ProtocolRecord pickProtocol = model.getJobModel()
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

        ProtocolRecord chkCreditProtocol = model.getJobModel()
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

        ProtocolRecord chkLtrCrdtProtocol = model.getJobModel()
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

        ProtocolRecord shipProtocol = model.getJobModel()
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

        ProtocolRecord printCustDeclProtocol = model.getJobModel()
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

        ProtocolRecord printPoProtocol = model.getJobModel()
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

        ProtocolRecord feeProtocol = model.getJobModel()
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

        ProtocolRecord salesTaxProtocol = model.getJobModel()
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

        ProtocolRecord discountProtocol = model.getJobModel()
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

        ProtocolRecord gtuDiscountedPriceProtocol = model.getJobModel()
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
                                     "A is a member of the economic community B");
        areaOf = model.records()
                      .newRelationship("Area Of",
                                       "A is economic community of B", area);
        area.insert();
        area.setInverse(areaOf.getId());
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
                                            salesTaxStatus);
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
        deliver = model.records()
                       .newProduct("Deliver", "Deliver product");
        em.persist(deliver);

        pick = model.records()
                    .newProduct("Pick", "Pick inventory");
        em.persist(pick);

        ship = model.records()
                    .newProduct("Ship", "Ship inventory");
        em.persist(ship);

        checkCredit = model.records()
                           .newProduct("CheckCredit",
                                       "Check customer inhouse credit");
        em.persist(checkCredit);

        checkLetterOfCredit = model.records()
                                   .newProduct("CheckLetterOfCredit",
                                               "Check customer letter of credit",
                                               core);
        em.persist(checkLetterOfCredit);

        discount = model.records()
                        .newProduct("Discount", "Compute fee discount ");
        em.persist(discount);

        fee = model.records()
                   .newProduct("Fee", "Compute fee");
        em.persist(fee);

        printCustomsDeclaration = model.records()
                                       .newProduct("PrintCustomsDeclaration",
                                                   "Print the customs declaration",
                                                   core);
        em.persist(printCustomsDeclaration);

        printPurchaseOrder = model.records()
                                  .newProduct("PrintPurchaseOrder",
                                              "Print the purchase order");
        em.persist(printPurchaseOrder);

        salesTax = model.records()
                        .newProduct("SalesTax", "Compute sales tax");
        em.persist(salesTax);
    }

    public void createStatusCodes() {
        available = model.records()
                         .newStatusCode("Available",
                                        "The job is available for execution");
        em.persist(available);

        active = model.records()
                      .newStatusCode("Active", "Working on it now");
        em.persist(active);

        waitingOnCreditCheck = model.records()
                                    .newStatusCode("Waiting on Credit Check",
                                                   "Waiting for credit check to be completed",
                                                   core);
        em.persist(waitingOnCreditCheck);

        completed = model.records()
                         .newStatusCode("Completed", "Completed Job");
        completed.setPropagateChildren(true); // This is done to test the dup logic in the job animation
        em.persist(completed);

        failure = model.records()
                       .newStatusCode("Failure", "Something went wrong");
        failure.setFailParent(true);
        em.persist(failure);

        pickCompleted = model.records()
                             .newStatusCode("Pick Completed",
                                            "Pick product has been completed");
        em.persist(pickCompleted);

        waitingOnPurchaseOrder = model.records()
                                      .newStatusCode("WaitingOnPurchaseOrder",
                                                     "Waiting for purchase order to be completed",
                                                     core);
        em.persist(waitingOnPurchaseOrder);

        waitingOnPricing = model.records()
                                .newStatusCode("Waiting on pricing",
                                               "Waiting for pricing to be completed",
                                               core);
        em.persist(waitingOnPricing);

        waitingOnFee = model.records()
                            .newStatusCode("Waiting on fee calculation",
                                           "Waiting for fee calculation to be completed",
                                           core);
        em.persist(waitingOnFee);

        abandoned = model.records()
                         .newStatusCode("Abandoned",
                                        "We were going to do it, something happened in earlier processing that will prevent us.  This can be garbage-collected now",
                                        core);
        em.persist(abandoned);
    }

    public void createStatusCodeSequencing() {
        StatusCodeSequencing s = model.records()
                                      .newStatusCodeSequencing(pick,
                                                               waitingOnCreditCheck,
                                                               available);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(pick, available, active);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(pick, active, completed);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(ship, waitingOnPurchaseOrder,
                                          available, core);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(ship, available, active);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(ship, active, completed);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(deliver, available, active);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(deliver, active, completed);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(checkCredit, available, active);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(checkCredit, active, completed);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(checkLetterOfCredit, available,
                                          active, core);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(checkLetterOfCredit, active,
                                          completed, core);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(discount, available, active);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(discount, active, completed);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(fee, available, active);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(fee, available, waitingOnPricing);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(fee, waitingOnPricing, active);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(fee, active, completed);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(printCustomsDeclaration, waitingOnFee,
                                          available);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(printCustomsDeclaration, available,
                                          active, core);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(printCustomsDeclaration, active,
                                          completed, core);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(printPurchaseOrder, waitingOnFee,
                                          available);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(printPurchaseOrder, available, active,
                                          core);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(printPurchaseOrder, active, completed,
                                          core);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(salesTax, available, active);
        em.persist(s);

        s = model.records()
                 .newStatusCodeSequencing(salesTax, active, completed);
        em.persist(s);
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
