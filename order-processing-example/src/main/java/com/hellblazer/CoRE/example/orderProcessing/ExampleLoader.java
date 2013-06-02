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
import com.hellblazer.CoRE.event.ProductSequencingAuthorization;
import com.hellblazer.CoRE.event.Protocol;
import com.hellblazer.CoRE.event.ProtocolAttribute;
import com.hellblazer.CoRE.event.StatusCode;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.location.LocationContext;
import com.hellblazer.CoRE.meta.Kernel;
import com.hellblazer.CoRE.meta.Model;
import com.hellblazer.CoRE.meta.models.ModelImpl;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.resource.Resource;
import com.hellblazer.CoRE.resource.ResourceNetwork;

/**
 * @author hhildebrand
 * 
 */
public class ExampleLoader {
    private final EntityManager em;

    public Relationship         area;
    public Relationship         city;
    public Relationship         customerType;
    public Relationship         state;
    public Relationship         salesTaxStatus;
    public Relationship         storageType;
    public Relationship         street;
    public Relationship         region;

    public LocationContext      binCtxt;
    public LocationContext      containmentCtxt;

    public Location             rsb225;
    public Location             bht378;
    public Location             bin1;
    public Location             bin15;
    public Location             dc;
    public Location             east_coast;
    public Location             factory1;
    public Location             france;
    public Location             paris;
    public Location             us;
    public Location             wash;
    public Location             euro;

    public Product              abc486;
    public Product              checkLetterOfCredit;
    public Product              checkCredit;
    public Product              deliver;
    public Product              discount;
    public Product              fee;
    public Product              frozen;
    public Product              nonExempt;
    public Product              pick;
    public Product              chemB;
    public Product              roomTemp;
    public Product              printCustomsDeclaration;
    public Product              ship;
    public Product              salesTax;

    public Resource             billingComputer;
    public Resource             cpu;
    public Resource             creditDept;
    public Resource             exempt;
    public Resource             externalCust;
    public Resource             factory1Resource;
    public Resource             georgeTownUniversity;
    public Resource             manufacturer;
    public Resource             nonExemptResource;
    public Resource             orgA;
    public Resource             core;

    public StatusCode           available;
    public StatusCode           active;
    public StatusCode           completed;
    public StatusCode           failure;
    public StatusCode           abandoned;

    private final Kernel        kernel;
    private final Model         model;
    private Product             notApplicableProduct;
    private Product             anyProduct;
    private Resource            anyResource;
    private Location            anyLocation;
    private Product             sameProduct;
    private Attribute           priceAttribute;
    private Attribute           taxRateAttribute;
    private Attribute           discountAttribute;
    private Relationship        sameRelationship;
    private Relationship        anyRelationship;

    public ExampleLoader(EntityManager em) throws Exception {
        this.em = em;
        model = new ModelImpl(em);
        kernel = model.getKernel();
        core = kernel.getCore();
        notApplicableProduct = kernel.getNotApplicableProduct();
        sameProduct = kernel.getSameProduct();
        anyProduct = kernel.getAnyProduct();
        anyResource = kernel.getAnyResource();
        anyLocation = kernel.getAnyLocation();
        sameRelationship = kernel.getSameRelationship();
        anyRelationship = kernel.getAnyRelationship();
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

    public void createEntities() {
        abc486 = new Product("ABC486", "Laptop Computer", kernel.getCore());
        em.persist(abc486);
        checkCredit = new Product("CheckCredit",
                                  "Check customer inhouse credit",
                                  kernel.getCore());
        em.persist(checkCredit);
        checkLetterOfCredit = new Product("CheckLetterOfCredit",
                                          "Check customer letter of credit",
                                          kernel.getCore());
        em.persist(checkLetterOfCredit);
        deliver = new Product("Deliver", "Deliver product", kernel.getCore());
        em.persist(deliver);
        discount = new Product("Discount", "Compute fee discount ",
                               kernel.getCore());
        em.persist(discount);
        fee = new Product("Fee", "Compute fee", kernel.getCore());
        em.persist(fee);
        frozen = new Product("Frozen", "Frozen products", kernel.getCore());
        em.persist(frozen);
        nonExempt = new Product("NonExempt", "Subject to sales tax",
                                kernel.getCore());
        em.persist(nonExempt);
        pick = new Product("Pick", "Pick inventory", kernel.getCore());
        em.persist(pick);
        chemB = new Product("ChemB", "Chemical B", kernel.getCore());
        em.persist(chemB);
        printCustomsDeclaration = new Product("PrintCustomsDeclaration",
                                              "Print the customs declaration",
                                              kernel.getCore());
        em.persist(printCustomsDeclaration);
        roomTemp = new Product("RoomTemp", "Room temperature products",
                               kernel.getCore());
        em.persist(roomTemp);
        ship = new Product("Ship", "Ship inventory", kernel.getCore());
        em.persist(ship);
        salesTax = new Product("SalesTax", "Compute sales tax",
                               kernel.getCore());
        em.persist(salesTax);
    }

    public void createLocationContexts() {
        containmentCtxt = new LocationContext(
                                              "Geographical Containment",
                                              "The geographical containment hierarchy ",
                                              kernel.getCore());
        em.persist(containmentCtxt);
        binCtxt = new LocationContext("Parts Bin",
                                      "The bin location of an product.",
                                      kernel.getCore());
        em.persist(binCtxt);
    }

    public void createLocationNetworks() {
        model.getLocationModel().link(bin1, area, factory1, kernel.getCore());
        model.getLocationModel().link(bin15, area, bin15, kernel.getCore());
        model.getLocationModel().link(factory1, street, bht378,
                                      kernel.getCore());
        model.getLocationModel().link(rsb225, city, wash, kernel.getCore());
        model.getLocationModel().link(bht378, city, wash, kernel.getCore());
        model.getLocationModel().link(wash, state, dc, kernel.getCore());
        model.getLocationModel().link(dc, region, east_coast, kernel.getCore());
        model.getLocationModel().link(east_coast, area, us, kernel.getCore());
        model.getLocationModel().link(paris, region, france, kernel.getCore());
        model.getLocationModel().link(france, area, euro, billingComputer);
    }

    public void createLocations() {
        rsb225 = new Location("225RSB", "225 Reiss Science Bldg",
                              containmentCtxt, core);
        em.persist(rsb225);
        bht378 = new Location("37BHT", "37 Bret Harte Terrace",
                              containmentCtxt, core);
        em.persist(bht378);

        bin1 = new Location("BIN01", "Bin #1", binCtxt, core);
        em.persist(bin1);
        bin15 = new Location("BIN15", "Bin #15", binCtxt, core);
        em.persist(bin15);
        dc = new Location("DC", "District of Columbia", containmentCtxt, core);
        em.persist(dc);
        east_coast = new Location("EAST_COAST", "East Coast", containmentCtxt,
                                  core);
        em.persist(east_coast);
        factory1 = new Location("FACTORY1", "Factory 1", containmentCtxt, core);
        em.persist(factory1);
        france = new Location("FRANCE", "France", containmentCtxt, core);
        em.persist(france);
        paris = new Location("PARIS", "Paris", containmentCtxt, core);
        em.persist(paris);
        us = new Location("US", "U.S. Locations", containmentCtxt, core);
        em.persist(us);
        wash = new Location("WASH", "Washington", containmentCtxt, core);
        em.persist(wash);
        euro = new Location("Euro", "European locations", containmentCtxt, core);
        em.persist(euro);
    }

    public void createMetaProtocols() {
        MetaProtocol m1 = new MetaProtocol(deliver, 1, sameRelationship,
                                           sameRelationship, state, area, core);
        em.persist(m1);
        MetaProtocol m2 = new MetaProtocol(deliver, 2, customerType,
                                           sameRelationship, area, area, core);
        em.persist(m2);
        MetaProtocol m3 = new MetaProtocol(deliver, 3, customerType,
                                           anyRelationship, area, area, core);
        em.persist(m3);
        MetaProtocol m4 = new MetaProtocol(deliver, 4, customerType,
                                           sameRelationship, anyRelationship,
                                           area, core);
        em.persist(m4);
        MetaProtocol m5 = new MetaProtocol(deliver, 5, salesTaxStatus,
                                           sameRelationship, state,
                                           anyRelationship, core);
        em.persist(m5);
        MetaProtocol m6 = new MetaProtocol(deliver, 6, anyRelationship,
                                           anyRelationship, anyRelationship,
                                           anyRelationship, core);
        em.persist(m6);
    }

    public void createProductNetworks() {
        model.getProductModel().link(abc486, storageType, roomTemp,
                                     kernel.getCore());
        model.getProductModel().link(abc486, salesTaxStatus, nonExempt,
                                     kernel.getCore());
        model.getProductModel().link(chemB, storageType, frozen,
                                     kernel.getCore());
    }

    public void createProtocols() {
        Protocol p1 = new Protocol(deliver, externalCust, anyProduct, us, us,
                                   cpu, checkCredit, notApplicableProduct, core);
        em.persist(p1);

        em.getTransaction().commit();
        em.getTransaction().begin();

        Protocol p2 = new Protocol(deliver, externalCust, anyProduct, euro, us,
                                   creditDept, checkLetterOfCredit,
                                   notApplicableProduct, core);
        em.persist(p2);

        em.getTransaction().commit();
        em.getTransaction().begin();

        Protocol p3 = new Protocol(deliver, externalCust, abc486, euro, us,
                                   cpu, printCustomsDeclaration,
                                   notApplicableProduct, core);
        em.persist(p3);

        em.getTransaction().commit();
        em.getTransaction().begin();

        Protocol p4 = new Protocol(deliver, anyResource, anyProduct,
                                   anyLocation, anyLocation, factory1Resource,
                                   pick, sameProduct, core);
        em.persist(p4);

        em.getTransaction().commit();
        em.getTransaction().begin();

        Protocol p5 = new Protocol(deliver, anyResource, anyProduct,
                                   anyLocation, anyLocation, factory1Resource,
                                   deliver, sameProduct, true, core);
        em.persist(p5);

        em.getTransaction().commit();
        em.getTransaction().begin();

        Protocol p6 = new Protocol(deliver, externalCust, abc486, anyLocation,
                                   us, billingComputer, fee, sameProduct, core);
        em.persist(p6);

        em.getTransaction().commit();
        em.getTransaction().begin();

        ProtocolAttribute price = new ProtocolAttribute(priceAttribute, core);
        price.setNumericValue(1500);
        price.setProtocol(p6);
        em.persist(price);

        em.getTransaction().commit();
        em.getTransaction().begin();

        Protocol p7 = new Protocol(deliver, nonExemptResource, nonExempt, dc,
                                   anyLocation, billingComputer, salesTax,
                                   sameProduct, core);
        em.persist(p7);

        em.getTransaction().commit();
        em.getTransaction().begin();

        ProtocolAttribute taxRate = new ProtocolAttribute(taxRateAttribute,
                                                          core);
        taxRate.setNumericValue(0.0575);
        taxRate.setProtocol(p7);
        em.persist(taxRate);

        em.getTransaction().commit();
        em.getTransaction().begin();

        Protocol p8 = new Protocol(deliver, externalCust, abc486, euro, us,
                                   billingComputer, discount, sameProduct, core);
        em.persist(p8);

        em.getTransaction().commit();
        em.getTransaction().begin();

        ProtocolAttribute discount = new ProtocolAttribute(discountAttribute,
                                                           core);
        discount.setNumericValue(0.05);
        discount.setProtocol(p7);
        em.persist(discount);

        em.getTransaction().commit();
        em.getTransaction().begin();

        Protocol p9 = new Protocol(deliver, georgeTownUniversity, abc486, dc,
                                   us, billingComputer, fee, sameProduct, core);
        em.persist(p9);

        em.getTransaction().commit();
        em.getTransaction().begin();

        ProtocolAttribute discountedPrice = new ProtocolAttribute(
                                                                  priceAttribute,
                                                                  core);
        discountedPrice.setNumericValue(1250);
        discountedPrice.setProtocol(p9);
        em.persist(discountedPrice);

        em.getTransaction().commit();
        em.getTransaction().begin();
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
                                      externalCust, kernel.getCore());
        model.getResourceModel().link(georgeTownUniversity, salesTaxStatus,
                                      exempt, kernel.getCore());
        model.getResourceModel().link(orgA, customerType, externalCust,
                                      kernel.getCore());
        model.getResourceModel().link(orgA, salesTaxStatus, nonExemptResource,
                                      kernel.getCore());
    }

    public void createResources() {
        billingComputer = new Resource("Billing CPU", "The Billing Computer",
                                       kernel.getCore());
        em.persist(billingComputer);

        cpu = new Resource("CPU", "Computer", kernel.getCore());
        em.persist(cpu);

        creditDept = new Resource("Credit", "Credit Department",
                                  kernel.getCore());
        em.persist(creditDept);

        exempt = new Resource("Exempt", "Exempt from sales taxes",
                              kernel.getCore());
        em.persist(exempt);

        externalCust = new Resource("Ext Customer",
                                    "External (Paying) Customer",
                                    kernel.getCore());
        em.persist(externalCust);

        factory1Resource = new Resource("Factory1", "Factory #1",
                                        kernel.getCore());
        em.persist(factory1Resource);

        georgeTownUniversity = new Resource("GU", "Georgetown University",
                                            kernel.getCore());
        em.persist(georgeTownUniversity);

        manufacturer = new Resource("MNFR", "Manufacturer", kernel.getCore());
        em.persist(manufacturer);

        nonExemptResource = new Resource("NonExempt", "Subject to sales taxes",
                                         kernel.getCore());
        em.persist(nonExemptResource);

        orgA = new Resource("OrgA", "Organization A", kernel.getCore());
        em.persist(orgA);
    }

    public void createStatusCodes() {

        available = new StatusCode("Available",
                                   "The job is available for execution", core);
        em.persist(available);

        active = new StatusCode("Active", "Working on it now", core);
        em.persist(active);

        completed = new StatusCode("Completed", "Completed Job", core);
        completed.setPropagateChildren(true);
        em.persist(completed);

        failure = new StatusCode("Failure", "Something went wrong", core);
        failure.setFailParent(true);
        em.persist(failure);

        abandoned = new StatusCode(
                                   "Abandoned",
                                   "We were going to do it, something happened in earlier processing that will prevent us.  This can be garbage-collected now",
                                   core);
        em.persist(abandoned);
    }

    public void createSequencingAuthorizations() {
        ProductSequencingAuthorization psa1 = new ProductSequencingAuthorization(
                                                                                 core);
        psa1.setParent(deliver);
        psa1.setStatusCode(active);
        psa1.setSequenceNumber(1);
        psa1.setNextChild(fee);
        psa1.setNextChildStatus(available);
        em.persist(psa1);

        ProductSequencingAuthorization psa2 = new ProductSequencingAuthorization(
                                                                                 core);
        psa2.setParent(deliver);
        psa2.setStatusCode(active);
        psa2.setSequenceNumber(2);
        psa2.setNextChild(checkCredit);
        psa2.setNextChildStatus(available);
        em.persist(psa2);

        ProductSequencingAuthorization psa3 = new ProductSequencingAuthorization(
                                                                                 core);
        psa3.setParent(deliver);
        psa3.setStatusCode(active);
        psa3.setSequenceNumber(3);
        psa3.setNextChild(pick);
        psa3.setNextChildStatus(available);
        em.persist(psa3);

        ProductSequencingAuthorization psa4 = new ProductSequencingAuthorization(
                                                                                 core);
        psa4.setParent(pick);
        psa4.setStatusCode(completed);
        psa4.setSequenceNumber(4);
        psa4.setNextSibling(ship);
        psa4.setNextSiblingStatus(available);
        em.persist(psa4);

        ProductSequencingAuthorization psa5 = new ProductSequencingAuthorization(
                                                                                 core);
        psa5.setParent(deliver);
        psa5.setStatusCode(active);
        psa5.setSequenceNumber(5);
        psa5.setNextSibling(printCustomsDeclaration);
        psa5.setNextSiblingStatus(available);
        em.persist(psa5);
    }

    public void initEntityManager() throws Exception {
    }

    public void load() {
        createResources();
        createAttributes();
        createEntities();
        createLocationContexts();
        createLocations();
        createRelationships();
        createProductNetworks();
        createResourceNetworks();
        createLocationNetworks();
        createProtocols();
        createMetaProtocols();
        createStatusCodes();
        // createSequencingAuthorizations();
    }
}
