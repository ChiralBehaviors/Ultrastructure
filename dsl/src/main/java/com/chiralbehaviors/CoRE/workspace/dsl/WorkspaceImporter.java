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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.time.Interval;
import com.chiralbehaviors.CoRE.workspace.DatabaseBackedWorkspace;
import com.chiralbehaviors.CoRE.workspace.Workspace;
import com.hellblazer.utils.Tuple;

/**
 * @author hparry
 *
 */
public class WorkspaceImporter {

    private final WorkspacePresentation wsp;
    private final Model                 model;
    private final EntityManager         em;
    private static final String         STATUS_CODE_SEQUENCING_FORMAT = "%s: %s -> %s";
    private DatabaseBackedWorkspace     workspace;

    public WorkspaceImporter(WorkspacePresentation wsp, Model model) {
        this.wsp = wsp;
        this.model = model;
        this.em = model.getEntityManager();
    }

    public Workspace loadWorkspace() {
        workspace = new DatabaseBackedWorkspace(createWorkspaceProduct(), em);
        loadAgencies();
        loadAttributes();
        loadLocations();
        loadProducts();
        loadRelationships();
        loadStatusCodes();
        loadStatusCodeSequencings();
        loadUnits();
        loadIntervals();
        loadEdges();

        return workspace;
    }

    /**
     * 
     */
    private void loadStatusCodeSequencings() {
        for (Map.Entry<String, List<Tuple<String, String>>> entry : wsp.getStatusCodeSequencings().entrySet()) {
            Product service = workspace.get(entry.getKey());
            for (Tuple<String, String> sequencePair : entry.getValue()) {
                StatusCode first = workspace.get(sequencePair.a);
                StatusCode second = workspace.get(sequencePair.b);
                StatusCodeSequencing sequence = new StatusCodeSequencing(
                                                                         service,
                                                                         first,
                                                                         second,
                                                                         service.getUpdatedBy());
                em.persist(sequence);
                String key = String.format(STATUS_CODE_SEQUENCING_FORMAT,
                                           service.getName(), first.getName(),
                                           second.getName());
                workspace.put(key, sequence);
            }
        }

    }

    /**
     * @return
     */
    private Product createWorkspaceProduct() {
        Product workspaceProduct = new Product(wsp.getWorkspaceDefinition().a,
                                               wsp.getWorkspaceDefinition().b,
                                               model.getKernel().getCore());
        em.persist(workspaceProduct);
        return workspaceProduct;
    }

    /**
     * 
     */
    private void loadAgencies() {
        for (Map.Entry<String, Tuple<String, String>> a : wsp.getAgencies().entrySet()) {
            Agency agency = new Agency(a.getValue().a, a.getValue().b,
                                       model.getKernel().getCore());
            em.persist(agency);
            workspace.put(a.getKey(), agency);
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
            workspace.put(a.getKey(), ruleform);
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
        for (WorkspacePresentation.Interval ivl : wsp.getIntervals()) {
            Interval interval = new Interval(
                                             ivl.name,
                                             ivl.start == null ? BigDecimal.valueOf(0)
                                                              : ivl.start,
                                             ivl.startUnit == null ? model.getKernel().getNotApplicableUnit()
                                                                  : workspace.get(ivl.startUnit),
                                             ivl.duration == null ? BigDecimal.valueOf(0)
                                                                 : ivl.duration,
                                             ivl.durationUnit == null ? model.getKernel().getNotApplicableUnit()
                                                                     : workspace.get(ivl.durationUnit),
                                             ivl.description,
                                             model.getKernel().getCore());
            em.persist(interval);
            workspace.put(ivl.name, interval);
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
            workspace.put(a.getKey(), ruleform);
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
            workspace.put(a.getKey(), ruleform);
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
            workspace.put(a.getKey(), ruleform);
        }
    }

    /**
     * 
     */
    private void loadUnits() {
        for (WorkspacePresentation.Unit a : wsp.getUnits()) {
            Unit ruleform = new Unit(a.name, a.description,
                                     model.getKernel().getCore());
            ruleform.setEnumerated(a.enumerated);
            ruleform.setDatatype(a.datatype);
            ruleform.setMin(a.min);
            ruleform.setMax(a.max);
            em.persist(ruleform);
            workspace.put(a.wsName, ruleform);
        }
    }

}
