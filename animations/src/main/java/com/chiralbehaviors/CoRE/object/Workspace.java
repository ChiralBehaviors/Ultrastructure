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
package com.chiralbehaviors.CoRE.object;

import java.util.List;

import javax.persistence.EntityManager;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.authorization.AccessAuthorization;
import com.chiralbehaviors.CoRE.meta.graph.query.AccessAuthorizationGraphQuery;
import com.chiralbehaviors.CoRE.meta.graph.query.NetworkGraphQuery;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;

/**
 * The object that gets de/serialized as a workspace in JSON
 * 
 * @author hparry
 * 
 */
public class Workspace {

    /**
     * Use this to create a workspace object in memory and load relevant
     * components from the db.
     * 
     * @param workspace
     * @param workspaceOf
     * @param em
     */
    public static Workspace loadWorkspace(Product workspace,
                                          Relationship workspaceOf,
                                          EntityManager em) {
        Workspace ws = new Workspace();

        ws.workspace = workspace;
        ws.workspaceOf = workspaceOf;
        ws.em = em;
        ws.products = ws.loadWorkspaceProducts();
        ws.accessAuths = ws.loadWorkspaceAccessAuthorizations();

        return ws;
    }

    private Product                         workspace;
    private Relationship                    workspaceOf;
    private EntityManager                   em;
    private List<Product>                   products;
    private List<AccessAuthorization<?, ?>> accessAuths;

    /**
     * An empty constructor for JSON serialization.
     */
    public Workspace() {
        // empty constructor for JSON
    }

    public void addToWorkspace(ExistentialRuleform<?, ?> rf) {
        //TODO HPARRY generalize
        Product p = (Product) rf;
        p.setUpdatedBy(em.merge(p.getUpdatedBy()));
        ProductNetwork pn = new ProductNetwork(workspace, workspaceOf, p,
                                               p.getUpdatedBy());
        em.persist(p);
        em.persist(pn);
    }

    /**
     * @return the auths
     */
    public List<AccessAuthorization<?, ?>> getAccessAuths() {
        return accessAuths;
    }

    /**
     * @return
     */
    public Product getParentProduct() {
        return workspace;
    }

    /**
     * @return the products
     */
    public List<Product> getProducts() {
        return products;
    }

    /**
     * @return the workspace
     */
    public Product getWorkspace() {
        return workspace;
    }

    /**
     * @return the workspaceOf
     */
    public Relationship getWorkspaceOf() {
        return workspaceOf;
    }

    public void removeFromWorkspace(ExistentialRuleform<?, ?> rf) {
        //TODO HPARRY

    }

    /**
     * @param auths
     *            the auths to set
     */
    public void setAuths(List<AccessAuthorization<?, ?>> auths) {
        accessAuths = auths;
    }

    /**
     * @param products
     *            the products to set
     */
    public void setProducts(List<Product> products) {
        this.products = products;
    }

    /**
     * @param workspace
     *            the workspace to set
     */
    public void setWorkspace(Product workspace) {
        this.workspace = workspace;
    }

    /**
     * @param workspaceOf
     *            the workspaceOf to set
     */
    public void setWorkspaceOf(Relationship workspaceOf) {
        this.workspaceOf = workspaceOf;
    }

    private List<AccessAuthorization<?, ?>> loadWorkspaceAccessAuthorizations() {
        AccessAuthorizationGraphQuery query = new AccessAuthorizationGraphQuery(
                                                                                workspace,
                                                                                workspaceOf,
                                                                                em);
        return query.getResults();
    }

    private List<Product> loadWorkspaceProducts() {
        NetworkGraphQuery<Product> queryAgency = new NetworkGraphQuery<Product>(
                                                                                workspace,
                                                                                workspaceOf,
                                                                                em);
        return queryAgency.getNodes();

    }

}
