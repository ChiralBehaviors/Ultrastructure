/**
 * Copyright (c) 2014 Halloran Parry, all rights reserved.
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
package com.chiralbehaviors.workspace.swing;

import javax.swing.JFrame;

import org.junit.BeforeClass;
import org.junit.Test;

import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.object.painter.PainterWorkspaceImpl;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.workspace.swing.model.impl.DatabaseEditor;

/**
 * @author hparry
 *
 */
public class ExistentialRuleformViewTest extends AbstractModelTest {

    private static PainterWorkspaceImpl workspace;

    @BeforeClass
    public static void init() {
        workspace = new PainterWorkspaceImpl(em, model);
        em.getTransaction().begin();
        workspace.load();
        em.getTransaction().commit();
    }

    @Test
    public void testWorkspaceAuthorizations() {
        em.getTransaction().begin();
        Product painterSpace = new Product("Painter's Workspace", null,
                                           kernel.getCore());
        em.persist(painterSpace);

        WorkspaceAuthorization auth = new WorkspaceAuthorization();
        auth.setDefiningProduct(painterSpace);
        auth.setAgency(workspace.getArtist());
        auth.setUpdatedBy(kernel.getCore());
        auth.setKey(workspace.getArtist().getName());
        em.persist(auth);

        WorkspaceAuthorization auth2 = new WorkspaceAuthorization();
        auth2.setDefiningProduct(painterSpace);
        auth2.setProduct(workspace.getBrush());
        auth2.setUpdatedBy(kernel.getCore());
        auth2.setKey(workspace.getBrush().getName());
        em.persist(auth2);
        WorkspaceAuthorization auth3 = new WorkspaceAuthorization();
        auth3.setDefiningProduct(painterSpace);
        auth3.setProduct(workspace.getCanvas());
        auth3.setUpdatedBy(kernel.getCore());
        auth3.setKey(workspace.getCanvas().getName());
        em.persist(auth3);
        WorkspaceAuthorization auth4 = new WorkspaceAuthorization();
        auth4.setDefiningProduct(painterSpace);
        auth4.setProduct(workspace.getPaint());
        auth4.setUpdatedBy(kernel.getCore());
        auth4.setKey(workspace.getPaint().getName());
        em.persist(auth4);
        em.getTransaction().commit();

        DatabaseEditor workspaceEditor = new DatabaseEditor(model, painterSpace);
        ExistentialRuleformView<Product, ProductNetwork> view = new ExistentialRuleformView<>(
                                                                              workspaceEditor,
                                                                              workspace.getBrush());
        view.setVisible(true);
        JFrame enclosure = new JFrame();
        enclosure.setBounds(0, 0, 600, 400);
        enclosure.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        enclosure.getContentPane().add(view);
        enclosure.setVisible(true);
        System.out.println("View");
    }

}
