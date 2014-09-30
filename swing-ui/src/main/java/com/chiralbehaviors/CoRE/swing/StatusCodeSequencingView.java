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

package com.chiralbehaviors.CoRE.swing;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 *
 */
public class StatusCodeSequencingView extends JPanel {

    private static final long     serialVersionUID = 1L;
    private JComboBox<StatusCode> parents;
    private JComboBox<Product>    products;
    private JList<StatusCode>     children;

    /**
     * Create the panel.
     */
    public StatusCodeSequencingView() {
        setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        add(panel, BorderLayout.NORTH);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 0, 0, 200 };
        gbl_panel.rowHeights = new int[] { 30 };
        gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0 };
        gbl_panel.rowWeights = new double[] { 0.0 };
        panel.setLayout(gbl_panel);

        JLabel lblProduct = new JLabel("Product");
        GridBagConstraints gbc_lblProduct = new GridBagConstraints();
        gbc_lblProduct.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblProduct.insets = new Insets(0, 0, 0, 5);
        gbc_lblProduct.gridx = 0;
        gbc_lblProduct.gridy = 0;
        panel.add(lblProduct, gbc_lblProduct);

        products = new JComboBox<>();
        GridBagConstraints gbc_products = new GridBagConstraints();
        gbc_products.gridwidth = 5;
        gbc_products.fill = GridBagConstraints.HORIZONTAL;
        gbc_products.insets = new Insets(0, 0, 0, 5);
        gbc_products.anchor = GridBagConstraints.NORTH;
        gbc_products.gridx = 1;
        gbc_products.gridy = 0;
        panel.add(products, gbc_products);

        JButton btnNew = new JButton("new");
        GridBagConstraints gbc_btnNew = new GridBagConstraints();
        gbc_btnNew.gridx = 6;
        gbc_btnNew.gridy = 0;
        panel.add(btnNew, gbc_btnNew);

        JPanel panel_1 = new JPanel();
        add(panel_1, BorderLayout.CENTER);
        GridBagLayout gbl_panel_1 = new GridBagLayout();
        gbl_panel_1.columnWidths = new int[] { 0, 0, 0, 0 };
        gbl_panel_1.rowHeights = new int[] { 0, 0, 0 };
        gbl_panel_1.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        gbl_panel_1.rowWeights = new double[] { 0.0, 1.0 };
        panel_1.setLayout(gbl_panel_1);

        JLabel lblParent = new JLabel("Parent");
        GridBagConstraints gbc_lblParent = new GridBagConstraints();
        gbc_lblParent.insets = new Insets(0, 0, 5, 5);
        gbc_lblParent.anchor = GridBagConstraints.EAST;
        gbc_lblParent.gridx = 0;
        gbc_lblParent.gridy = 0;
        panel_1.add(lblParent, gbc_lblParent);

        parents = new JComboBox<StatusCode>();
        GridBagConstraints gbc_parents = new GridBagConstraints();
        gbc_parents.insets = new Insets(0, 0, 5, 5);
        gbc_parents.fill = GridBagConstraints.HORIZONTAL;
        gbc_parents.gridx = 1;
        gbc_parents.gridy = 0;
        panel_1.add(parents, gbc_parents);

        JLabel lblChildren = new JLabel("Children");
        GridBagConstraints gbc_lblChildren = new GridBagConstraints();
        gbc_lblChildren.insets = new Insets(0, 0, 5, 5);
        gbc_lblChildren.gridx = 0;
        gbc_lblChildren.gridy = 1;
        panel_1.add(lblChildren, gbc_lblChildren);

        children = new JList<>();
        GridBagConstraints gbc_children = new GridBagConstraints();
        gbc_children.insets = new Insets(0, 0, 5, 5);
        gbc_children.fill = GridBagConstraints.BOTH;
        gbc_children.gridx = 1;
        gbc_children.gridy = 1;
        panel_1.add(children, gbc_children);

    }

}
