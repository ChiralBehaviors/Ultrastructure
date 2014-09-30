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
public class SiblingSeqAuthView extends JPanel {

    private static final long  serialVersionUID = 1L;
    private JComboBox<Product> parent;
    private JComboBox<Product> nextSibling;
    private JCheckBox          replaceProduct;

    /**
     * Create the panel.
     */
    public SiblingSeqAuthView() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0,
                Double.MIN_VALUE };
        setLayout(gridBagLayout);

        JLabel lblParent = new JLabel("Parent");
        GridBagConstraints gbc_lblParent = new GridBagConstraints();
        gbc_lblParent.insets = new Insets(0, 0, 5, 5);
        gbc_lblParent.anchor = GridBagConstraints.EAST;
        gbc_lblParent.gridx = 0;
        gbc_lblParent.gridy = 0;
        add(lblParent, gbc_lblParent);

        parent = new JComboBox<>();
        GridBagConstraints gbc_parent = new GridBagConstraints();
        gbc_parent.insets = new Insets(0, 0, 5, 0);
        gbc_parent.fill = GridBagConstraints.HORIZONTAL;
        gbc_parent.gridx = 1;
        gbc_parent.gridy = 0;
        add(parent, gbc_parent);

        JLabel lblNextSibling = new JLabel("Next Sibling");
        GridBagConstraints gbc_lblNextSibling = new GridBagConstraints();
        gbc_lblNextSibling.anchor = GridBagConstraints.EAST;
        gbc_lblNextSibling.insets = new Insets(0, 0, 5, 5);
        gbc_lblNextSibling.gridx = 0;
        gbc_lblNextSibling.gridy = 1;
        add(lblNextSibling, gbc_lblNextSibling);

        nextSibling = new JComboBox<>();
        GridBagConstraints gbc_nextSibling = new GridBagConstraints();
        gbc_nextSibling.insets = new Insets(0, 0, 5, 0);
        gbc_nextSibling.fill = GridBagConstraints.HORIZONTAL;
        gbc_nextSibling.gridx = 1;
        gbc_nextSibling.gridy = 1;
        add(nextSibling, gbc_nextSibling);

        JLabel lblStatusToSet = new JLabel("Status To Set");
        GridBagConstraints gbc_lblStatusToSet = new GridBagConstraints();
        gbc_lblStatusToSet.anchor = GridBagConstraints.EAST;
        gbc_lblStatusToSet.insets = new Insets(0, 0, 5, 5);
        gbc_lblStatusToSet.gridx = 0;
        gbc_lblStatusToSet.gridy = 2;
        add(lblStatusToSet, gbc_lblStatusToSet);

        JComboBox<StatusCode> statusToSet = new JComboBox<>();
        GridBagConstraints gbc_comboBox = new GridBagConstraints();
        gbc_comboBox.insets = new Insets(0, 0, 5, 0);
        gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox.gridx = 1;
        gbc_comboBox.gridy = 2;
        add(statusToSet, gbc_comboBox);

        replaceProduct = new JCheckBox("Replace Product");
        GridBagConstraints gbc_replaceProduct = new GridBagConstraints();
        gbc_replaceProduct.anchor = GridBagConstraints.WEST;
        gbc_replaceProduct.gridx = 1;
        gbc_replaceProduct.gridy = 3;
        add(replaceProduct, gbc_replaceProduct);

    }

}
