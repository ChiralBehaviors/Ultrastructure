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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.kernel.Bootstrap;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.KernelImpl;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.chiralbehaviors.CoRE.product.access.ProductAgencyAccessAuthorization;
import com.chiralbehaviors.CoRE.product.access.ProductLocationAccessAuthorization;
import com.chiralbehaviors.CoRE.test.DatabaseTest;
import com.chiralbehaviors.CoRE.workspace.Workspace;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;

/**
 * @author hparry
 * 
 */
public class WorkspaceTest extends DatabaseTest {

    private static Kernel kernel;

    @Before
    public void initKernel() throws SQLException {
        beginTransaction();
        Bootstrap bt = new Bootstrap(connection);
        bt.clear();
        bt.bootstrap();
        commitTransaction();
        kernel = new KernelImpl(em);
    }

    @Test
    public void testLoadWorkspace() {
        em.clear();
        beginTransaction();
        Product workspace = kernel.getWorkspace();
        Relationship workspaceOf = kernel.getWorkspaceOf();
        Model model = new ModelImpl(em);
        Agency core = kernel.getCore();
        Product p1 = new Product("MyProduct", core);
        em.persist(p1);
        model.getProductModel().link(workspace, workspaceOf, p1, core);
        ProductAgencyAccessAuthorization coreAuth = new ProductAgencyAccessAuthorization(
                                                                                         workspace,
                                                                                         workspaceOf,
                                                                                         core,
                                                                                         core);
        ProductLocationAccessAuthorization locAuth = new ProductLocationAccessAuthorization(
                                                                                            workspace,
                                                                                            workspaceOf,
                                                                                            kernel.getAnyLocation(),
                                                                                            core);
        em.persist(coreAuth);
        em.persist(locAuth);
        em.getTransaction().commit();
       

        Workspace w = new WorkspaceLoader(workspace, workspaceOf,
                                          new ModelImpl(em)).getWorkspace();
        assertEquals(1, w.getProducts().size());
        assertTrue(w.getProducts().contains(p1));
    }

    @Test
    public void testJavaReferences() {
        em.clear();
        //create ws with agencies, agency networks. See what happens
        Product workspace = new Product("Workspace", "workspace",
                                        kernel.getCoreAnimationSoftware());
        Relationship workspaceOf = new Relationship(
                                                    "workspace of",
                                                    null,
                                                    kernel.getCoreAnimationSoftware());
        Relationship hasWorkspace = new Relationship("has workspace", null,
                                                     kernel.getCore());
        workspaceOf.setInverse(hasWorkspace);
        hasWorkspace.setInverse(workspaceOf);
        Agency a = new Agency("A", null, kernel.getCore());
        Agency b = new Agency("B", null, kernel.getCore());
        List<Agency> agencies = new LinkedList<>();
        agencies.add(a);
        agencies.add(b);
        Relationship r1 = new Relationship("r1", null, kernel.getCore());
        Relationship r2 = new Relationship("r2", null, kernel.getCore());
        r1.setInverse(r2);
        r2.setInverse(r1);
        List<Relationship> rels = new LinkedList<>();
        rels.add(r1);
        rels.add(r2);
        AgencyNetwork net = new AgencyNetwork(a, r1, b, kernel.getCore());
        AgencyNetwork net2 = new AgencyNetwork(b, r2, a, kernel.getCore());
        List<AgencyNetwork> nets = new LinkedList<>();
        nets.add(net);
        nets.add(net2);

        Workspace w = new WorkspaceSnapshot();
        w.setWorkspaceProduct(workspace);
        w.setWorkspaceRelationship(workspaceOf);
        w.setAgencies(agencies);
        w.setRelationships(rels);
        w.setAgencyNetworks(nets);
        em.clear();
        em.getTransaction().begin();
        Model model = new ModelImpl(em);
        Aspect<Product> aspect = model.getWorkspaceModel().importWorkspace(w);

        em.getTransaction().commit();
        assertNotNull(aspect.getClassifier().getId());

    }

    @Test
    public void testEntityManagerCrap() {
        em.clear();
        beginTransaction();
        Product a = new Product("Workspace", "workspace",
                                kernel.getCoreAnimationSoftware());
        Relationship workspaceOf = new Relationship(
                                                    "workspace of",
                                                    null,
                                                    kernel.getCoreAnimationSoftware());
        Relationship hasWorkspace = new Relationship("has workspace", null,
                                                     kernel.getCore());
        workspaceOf.setInverse(hasWorkspace);
        hasWorkspace.setInverse(workspaceOf);
        Product b = new Product("B", null, kernel.getCore());

        em.persist(a);
        em.persist(workspaceOf);
        em.persist(hasWorkspace);
        em.persist(b);
        ProductNetwork net = new ProductNetwork(a, workspaceOf, b,
                                                kernel.getCore());
        em.persist(net);
        Agency agency = new Agency("agency", null, kernel.getCore());
        em.persist(agency);
        ProductAgencyAccessAuthorization auth = new ProductAgencyAccessAuthorization(
                                                                                     a,
                                                                                     workspaceOf,
                                                                                     agency,
                                                                                     kernel.getCore());
        em.persist(auth);
        commitTransaction();
    }

}
