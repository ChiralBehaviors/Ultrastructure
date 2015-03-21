/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chiralbehaviors.CoRE.workspace.dsl;

import java.util.Map;

import javax.persistence.EntityManager;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.product.Product;
import com.hellblazer.utils.Tuple;

/**
 * @author hparry
 *
 */
public class WorkspaceImporter {

    private final WorkspacePresentation wsp;
    private final Model                 model;
    private final EntityManager         em;
    private Product                     workspace;

    public WorkspaceImporter(WorkspacePresentation wsp, Model model) {
        this.wsp = wsp;
        this.model = model;
        this.em = model.getEntityManager();
    }

    public Product loadWorkspace() {
        workspace = createWorkspaceProduct();
        loadAgencies();
        loadAttributes();
        loadIntervals();
        loadLocations();
        loadProducts();
        loadRelationships();
        loadStatusCodes();
        loadUnits();
        loadEdges();

        return workspace;
    }

    /**
     * @return
     */
    private Product createWorkspaceProduct() {
        Product workspace = new Product(wsp.getWorkspaceDefinition().a,
                                        wsp.getWorkspaceDefinition().b,
                                        model.getKernel().getCore());
        em.persist(workspace);
        return workspace;
    }

    /**
     * 
     */
    private void loadAgencies() {
        for (Map.Entry<String, Tuple<String, String>> a : wsp.getAgencies().entrySet()) {
            Agency agency = new Agency(a.getValue().a, a.getValue().b,
                                       model.getKernel().getCore());
            em.persist(agency);
        }

    }

    /**
     * 
     */
    private void loadAttributes() {
        for (Map.Entry<String, Tuple<String, String>> a : wsp.getAttributes().entrySet()) {
            Attribute ruleform = new Attribute(a.getValue().a, a.getValue().b,
                                               model.getKernel().getCore());
            em.persist(ruleform);
        }
    }

    /**
     * 
     */
    private void loadEdges() {
        // TODO Auto-generated method stub

    }

    /**
     * 
     */
    private void loadIntervals() {
        //TODO make this work for actual values and stuff.
        for (Map.Entry<String, Tuple<String, String>> a : wsp.getIntervals().entrySet()) {
            model.getIntervalModel().create(a.getValue().a, a.getValue().b,
                                            null);

        }
    }

    /**
     * 
     */
    private void loadLocations() {
        for (Map.Entry<String, Tuple<String, String>> a : wsp.getLocations().entrySet()) {
            Location ruleform = new Location(a.getValue().a, a.getValue().b,
                                             model.getKernel().getCore());
            em.persist(ruleform);
        }
    }

    /**
     * 
     */
    private void loadProducts() {
        for (Map.Entry<String, Tuple<String, String>> a : wsp.getProducts().entrySet()) {
            Product ruleform = new Product(a.getValue().a, a.getValue().b,
                                           model.getKernel().getCore());
            em.persist(ruleform);
        }
    }

    /**
     * 
     */
    private void loadRelationships() {
        // TODO Auto-generated method stub

    }

    /**
     * 
     */
    private void loadStatusCodes() {
        for (Map.Entry<String, Tuple<String, String>> a : wsp.getStatusCodes().entrySet()) {
            StatusCode ruleform = new StatusCode(a.getValue().a,
                                                 a.getValue().b,
                                                 model.getKernel().getCore());
            em.persist(ruleform);
        }
    }

    /**
     * 
     */
    private void loadUnits() {
        for (Map.Entry<String, Tuple<String, String>> a : wsp.getUnits().entrySet()) {
            Unit ruleform = new Unit(a.getValue().a, a.getValue().b,
                                     model.getKernel().getCore());
            em.persist(ruleform);
        }
    }

}
