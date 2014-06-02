/** 
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.object;

import java.util.LinkedList;
import java.util.List;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.access.ProductAgencyAccessAuthorization;
import com.chiralbehaviors.CoRE.product.access.ProductAttributeAccessAuthorization;
import com.chiralbehaviors.CoRE.product.access.ProductLocationAccessAuthorization;
import com.chiralbehaviors.CoRE.product.access.ProductRelationshipAccessAuthorization;
import com.chiralbehaviors.CoRE.product.access.ProductStatusCodeAccessAuthorization;
import com.chiralbehaviors.CoRE.product.access.ProductUnitAccessAuthorization;
import com.chiralbehaviors.CoRE.workspace.Workspace;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;

/**
 * Creates a workspace object from data in the database
 * 
 * @author hparry
 * 
 */
public class WorkspaceLoader {

    private Product      workspaceProduct;
    private Relationship workspaceOf;
    private Model        model;
    private Workspace    workspace;

    public WorkspaceLoader(Product workspaceProduct, Relationship workspaceOf,
                           Model model) {
        this.workspaceProduct = workspaceProduct;
        this.workspaceOf = workspaceOf;
        this.model = model;
        workspace = new WorkspaceSnapshot();
        load();
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    /**
     * Reloads the workspace data from the database. Useful for refreshing the
     * entity map if you're changed something.
     */
    public void load() {
        workspace.setWorkspaceProduct(workspaceProduct);
        workspace.setWorkspaceRelationship(workspaceOf);
        workspace.setProducts(model.getProductModel().getChildren(workspaceProduct,
                                                                  workspaceOf));

        loadAgencies();
        loadAttributes();
        loadLocations();
        loadRelationships();
        loadStatusCodes();
        loadUnits();
        loadAgencyNetworks();
        loadAttributeNetworks();
        loadRelationshipNetworks();
        loadProductNetworks();
        loadUnitNetworks();
    }

    /**
     * 
     */
    private void loadAgencies() {
        List<Agency> agencies = new LinkedList<>();
        for (ProductAgencyAccessAuthorization auth : model.getProductModel().getAgencyAccessAuths(workspaceProduct,
                                                                                                  workspaceOf)) {
            if (!agencies.contains(auth.getChild())) {
                agencies.add(auth.getChild());
            }
        }
        workspace.setAgencies(agencies);

    }

    /**
     * 
     */
    private void loadAgencyNetworks() {
        workspace.setAgencyNetworks(model.getAgencyModel().getInterconnections(workspace.getAgencies(),
                                                                               workspace.getRelationships(),
                                                                               workspace.getAgencies()));

    }

    /**
     * 
     */
    private void loadAttributeNetworks() {
        workspace.setAttributeNetworks(model.getAttributeModel().getInterconnections(workspace.getAttributes(),
                                                                                     workspace.getRelationships(),
                                                                                     workspace.getAttributes()));

    }

    /**
     * 
     */
    private void loadAttributes() {
        List<Attribute> attributes = new LinkedList<>();
        for (ProductAttributeAccessAuthorization auth : model.getProductModel().getAttributeAccessAuths(workspaceProduct,
                                                                                                        workspaceOf)) {
            if (!attributes.contains(auth.getChild())) {
                attributes.add(auth.getChild());
            }
        }
        workspace.setAttributes(attributes);
    }

    /**
     * 
     */
    private void loadLocations() {
        List<Location> locations = new LinkedList<>();
        for (ProductLocationAccessAuthorization auth : model.getProductModel().getLocationAccessAuths(workspaceProduct,
                                                                                                      workspaceOf)) {
            if (!locations.contains(auth.getChild())) {
                locations.add(auth.getChild());
            }
        }
        workspace.setLocations(locations);

    }

    /**
     * 
     */
    private void loadProductNetworks() {
        workspace.setProductNetworks(model.getProductModel().getInterconnections(workspace.getProducts(),
                                                                                 workspace.getRelationships(),
                                                                                 workspace.getProducts()));
    }

    /**
     * 
     */
    private void loadRelationshipNetworks() {
        workspace.setRelationshipNetworks(model.getRelationshipModel().getInterconnections(workspace.getRelationships(),
                                                                                           workspace.getRelationships(),
                                                                                           workspace.getRelationships()));

    }

    private void loadRelationships() {
        List<Relationship> relationships = new LinkedList<Relationship>();
        for (ProductRelationshipAccessAuthorization auth : model.getProductModel().getRelationshipAccessAuths(workspaceProduct,
                                                                                                              workspaceOf)) {
            if (!relationships.contains(auth.getChild())) {
                relationships.add(auth.getChild());
            }
        }
        workspace.setRelationships(relationships);
    }

    /**
     * 
     */
    private void loadStatusCodes() {
        List<StatusCode> statusCodes = new LinkedList<>();
        for (ProductStatusCodeAccessAuthorization auth : model.getProductModel().getStatusCodeAccessAuths(workspaceProduct,
                                                                                                          workspaceOf)) {
            if (!statusCodes.contains(auth.getChild())) {
                statusCodes.add(auth.getChild());
            }
        }
        workspace.setStatusCodes(statusCodes);
    }

    /**
     * 
     */
    private void loadUnitNetworks() {
        workspace.setUnitNetworks(model.getUnitModel().getInterconnections(workspace.getUnits(),
                                                                           workspace.getRelationships(),
                                                                           workspace.getUnits()));

    }

    /**
     * 
     */
    private void loadUnits() {
        List<Unit> units = new LinkedList<>();
        for (ProductUnitAccessAuthorization auth : model.getProductModel().getUnitAccessAuths(workspaceProduct,
                                                                                              workspaceOf)) {
            if (!units.contains(auth.getChild())) {
                units.add(auth.getChild());
            }
        }
        workspace.setUnits(units);

    }

}
