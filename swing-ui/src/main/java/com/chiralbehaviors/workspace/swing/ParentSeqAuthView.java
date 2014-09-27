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

package com.chiralbehaviors.workspace.swing;

import java.awt.BorderLayout;
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
public class ParentSeqAuthView extends JPanel {

    private static final long     serialVersionUID = 1L;
    private JComboBox<StatusCode> statusCode;
    private JComboBox<Product>    service;
    private JComboBox<Product>    parent;
    private JComboBox<StatusCode> statusToSet;
    private JCheckBox             setIfActiveSiblings;
    private JCheckBox             replaceProduct;

    /**
     * Create the panel.
     */
    public ParentSeqAuthView() {
        setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        add(panel, BorderLayout.CENTER);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 0, 0, 0 };
        gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
        gbl_panel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                Double.MIN_VALUE };
        panel.setLayout(gbl_panel);

        JLabel lblStatusCode = new JLabel("Status Code");
        GridBagConstraints gbc_lblStatusCode = new GridBagConstraints();
        gbc_lblStatusCode.anchor = GridBagConstraints.EAST;
        gbc_lblStatusCode.insets = new Insets(0, 0, 5, 5);
        gbc_lblStatusCode.gridx = 0;
        gbc_lblStatusCode.gridy = 0;
        panel.add(lblStatusCode, gbc_lblStatusCode);

        statusCode = new JComboBox<>();
        GridBagConstraints gbc_statusCode = new GridBagConstraints();
        gbc_statusCode.insets = new Insets(0, 0, 5, 0);
        gbc_statusCode.fill = GridBagConstraints.HORIZONTAL;
        gbc_statusCode.gridx = 1;
        gbc_statusCode.gridy = 0;
        panel.add(statusCode, gbc_statusCode);

        JLabel lblNewLabel = new JLabel("Service");
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
        gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 1;
        panel.add(lblNewLabel, gbc_lblNewLabel);

        service = new JComboBox<>();
        GridBagConstraints gbc_service = new GridBagConstraints();
        gbc_service.insets = new Insets(0, 0, 5, 0);
        gbc_service.fill = GridBagConstraints.HORIZONTAL;
        gbc_service.gridx = 1;
        gbc_service.gridy = 1;
        panel.add(service, gbc_service);

        JLabel lblParent = new JLabel("Parent");
        GridBagConstraints gbc_lblParent = new GridBagConstraints();
        gbc_lblParent.anchor = GridBagConstraints.EAST;
        gbc_lblParent.insets = new Insets(0, 0, 5, 5);
        gbc_lblParent.gridx = 0;
        gbc_lblParent.gridy = 2;
        panel.add(lblParent, gbc_lblParent);

        parent = new JComboBox<>();
        GridBagConstraints gbc_parent = new GridBagConstraints();
        gbc_parent.insets = new Insets(0, 0, 5, 0);
        gbc_parent.fill = GridBagConstraints.HORIZONTAL;
        gbc_parent.gridx = 1;
        gbc_parent.gridy = 2;
        panel.add(parent, gbc_parent);

        JLabel lblStatusToSet = new JLabel("Status To Set");
        GridBagConstraints gbc_lblStatusToSet = new GridBagConstraints();
        gbc_lblStatusToSet.anchor = GridBagConstraints.EAST;
        gbc_lblStatusToSet.insets = new Insets(0, 0, 5, 5);
        gbc_lblStatusToSet.gridx = 0;
        gbc_lblStatusToSet.gridy = 3;
        panel.add(lblStatusToSet, gbc_lblStatusToSet);

        statusToSet = new JComboBox<>();
        GridBagConstraints gbc_statusToSet = new GridBagConstraints();
        gbc_statusToSet.insets = new Insets(0, 0, 5, 0);
        gbc_statusToSet.fill = GridBagConstraints.HORIZONTAL;
        gbc_statusToSet.gridx = 1;
        gbc_statusToSet.gridy = 3;
        panel.add(statusToSet, gbc_statusToSet);

        replaceProduct = new JCheckBox("Replace Product");
        GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
        gbc_chckbxNewCheckBox.insets = new Insets(0, 0, 5, 0);
        gbc_chckbxNewCheckBox.anchor = GridBagConstraints.WEST;
        gbc_chckbxNewCheckBox.gridx = 1;
        gbc_chckbxNewCheckBox.gridy = 4;
        panel.add(replaceProduct, gbc_chckbxNewCheckBox);

        setIfActiveSiblings = new JCheckBox("Set If Active Siblings");
        GridBagConstraints gbc_chckbxSetIfActive = new GridBagConstraints();
        gbc_chckbxSetIfActive.anchor = GridBagConstraints.WEST;
        gbc_chckbxSetIfActive.gridx = 1;
        gbc_chckbxSetIfActive.gridy = 5;
        panel.add(setIfActiveSiblings, gbc_chckbxSetIfActive);

    }

}
