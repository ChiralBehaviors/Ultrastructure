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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 *
 */
public class ChildAuthSeqView extends JPanel {

    private static final long     serialVersionUID = 1L;
    private JComboBox<Product>    child;
    private JCheckBox             replaceProduct;
    private JComboBox<StatusCode> statusToSet;

    /**
     * Create the panel.
     */
    public ChildAuthSeqView() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0,
                Double.MIN_VALUE };
        setLayout(gridBagLayout);

        JLabel lblNewLabel = new JLabel("Child");
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
        gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 0;
        add(lblNewLabel, gbc_lblNewLabel);

        child = new JComboBox<>();
        GridBagConstraints gbc_child = new GridBagConstraints();
        gbc_child.insets = new Insets(0, 0, 5, 0);
        gbc_child.fill = GridBagConstraints.HORIZONTAL;
        gbc_child.gridx = 1;
        gbc_child.gridy = 0;
        add(child, gbc_child);

        JLabel lblStatusToSet = new JLabel("Status To Set");
        GridBagConstraints gbc_lblStatusToSet = new GridBagConstraints();
        gbc_lblStatusToSet.anchor = GridBagConstraints.EAST;
        gbc_lblStatusToSet.insets = new Insets(0, 0, 5, 5);
        gbc_lblStatusToSet.gridx = 0;
        gbc_lblStatusToSet.gridy = 1;
        add(lblStatusToSet, gbc_lblStatusToSet);

        statusToSet = new JComboBox<>();
        GridBagConstraints gbc_statusToSet = new GridBagConstraints();
        gbc_statusToSet.insets = new Insets(0, 0, 5, 0);
        gbc_statusToSet.fill = GridBagConstraints.HORIZONTAL;
        gbc_statusToSet.gridx = 1;
        gbc_statusToSet.gridy = 1;
        add(statusToSet, gbc_statusToSet);

        replaceProduct = new JCheckBox("Replace Product");
        GridBagConstraints gbc_replaceProduct = new GridBagConstraints();
        gbc_replaceProduct.anchor = GridBagConstraints.WEST;
        gbc_replaceProduct.gridx = 1;
        gbc_replaceProduct.gridy = 2;
        add(replaceProduct, gbc_replaceProduct);

    }

}
