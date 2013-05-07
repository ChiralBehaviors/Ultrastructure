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

import com.hellblazer.CoRE.event.Protocol;
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

    private final Kernel        kernel;

    private Relationship        area;
    private Relationship        city;
    private Relationship        customerType;
    private Relationship        state;
    private Relationship        salesTaxStatus;
    private Relationship        storageType;
    private Relationship        street;
    private Relationship        region;

    private LocationContext     binCtxt;
    private LocationContext     containmentCtxt;

    private Location            rsb225;
    private Location            bht378;
    private Location            bin1;
    private Location            bin15;
    private Location            dc;
    private Location            east_coast;
    private Location            factory1;
    private Location            france;
    private Location            paris;
    private Location            us;
    private Location            wash;
    private Location            euro;

    private Product             abc486;
    private Product             checkLetterOfCredit;
    private Product             checkCredit;
    private Product             deliver;
    private Product             discount;
    private Product             fee;
    private Product             frozen;
    private Product             nonExempt;
    private Product             pick;
    private Product             chemB;
    private Product             roomTemp;
    private Product             printCustomsDeclaration;
    private Product             ship;
    private Product             salesTax;

    private Resource            billingComputer;
    private Resource            cpu;
    private Resource            creditDept;
    private Resource            exempt;
    private Resource            externalCust;
    private Resource            factory1Resource;
    private Resource            georgeTownUniversity;
    private Resource            manufacturer;
    private Resource            nonExemptResource;
    private Resource            orgA;

    private final Model         model;

    private Resource            core;

    public ExampleLoader(EntityManager em) throws Exception {
        this.em = em;
        model = new ModelImpl(em);
        kernel = model.getKernel();
        core = kernel.getCore();
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

    public void createProductNetworks() {
        model.getProductModel().link(abc486, storageType, roomTemp,
                                     kernel.getCore());
        model.getProductModel().link(abc486, salesTaxStatus, nonExempt,
                                     kernel.getCore());
        model.getProductModel().link(chemB, storageType, frozen,
                                     kernel.getCore());
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
        // TODO Auto-generated method stub

    }

    public void createProtocols() {
        Protocol p1 = new Protocol(core);
        p1.setService(checkCredit);
        p1.setRequester(externalCust);
        p1.setRequestedService(deliver);
        p1.setDeliverTo(us);
        p1.setDeliverFrom(us);
        p1.setProduct(kernel.getAnyProduct());
        em.persist(p1);

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

    public void initEntityManager() throws Exception {
    }

    public void load() {
        createResources();
        createEntities();
        createLocationContexts();
        createLocations();
        createRelationships();
        createProductNetworks();
        createResourceNetworks();
        createLocationNetworks();
        /*
        createProtocols();
        createMetaProtocols();
        */
    }
}
