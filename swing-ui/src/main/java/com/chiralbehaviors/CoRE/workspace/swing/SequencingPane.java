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

package com.chiralbehaviors.CoRE.workspace.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.swing.ChildAuthSeqView;
import com.chiralbehaviors.CoRE.swing.ParentSeqAuthView;
import com.chiralbehaviors.CoRE.swing.SelfAuthSeqView;
import com.chiralbehaviors.CoRE.swing.SiblingSeqAuthView;

/**
 * @author hhildebrand
 *
 */
public class SequencingPane extends JPanel {

    private static final long     serialVersionUID = 1L;
    private JComboBox<StatusCode> statusCode;
    private JList<StatusCode>     parentStatusCodes;
    private JComboBox<Product>    service;
    private JList<StatusCode>     childStatusCodes;

    /**
     * Create the panel.
     */
    public SequencingPane() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0,
                Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, Double.MIN_VALUE };
        setLayout(gridBagLayout);

        JLabel lblStatusCode = new JLabel("Status Code");
        GridBagConstraints gbc_lblStatusCode = new GridBagConstraints();
        gbc_lblStatusCode.insets = new Insets(0, 0, 5, 5);
        gbc_lblStatusCode.gridx = 0;
        gbc_lblStatusCode.gridy = 0;
        add(lblStatusCode, gbc_lblStatusCode);

        statusCode = new JComboBox<>();
        GridBagConstraints gbc_statusCode = new GridBagConstraints();
        gbc_statusCode.insets = new Insets(0, 0, 5, 5);
        gbc_statusCode.fill = GridBagConstraints.HORIZONTAL;
        gbc_statusCode.gridx = 1;
        gbc_statusCode.gridy = 0;
        add(statusCode, gbc_statusCode);

        JLabel lblService = new JLabel("Service");
        GridBagConstraints gbc_lblService = new GridBagConstraints();
        gbc_lblService.insets = new Insets(0, 0, 5, 5);
        gbc_lblService.gridx = 2;
        gbc_lblService.gridy = 0;
        add(lblService, gbc_lblService);

        service = new JComboBox<>();
        GridBagConstraints gbc_service = new GridBagConstraints();
        gbc_service.insets = new Insets(0, 0, 5, 0);
        gbc_service.fill = GridBagConstraints.HORIZONTAL;
        gbc_service.gridx = 3;
        gbc_service.gridy = 0;
        add(service, gbc_service);

        JLabel lblParentStatusCodes = new JLabel("Parent Status Codes");
        GridBagConstraints gbc_lblParentStatusCodes = new GridBagConstraints();
        gbc_lblParentStatusCodes.gridwidth = 2;
        gbc_lblParentStatusCodes.insets = new Insets(0, 0, 5, 5);
        gbc_lblParentStatusCodes.gridx = 0;
        gbc_lblParentStatusCodes.gridy = 1;
        add(lblParentStatusCodes, gbc_lblParentStatusCodes);

        parentStatusCodes = new JList<StatusCode>();
        GridBagConstraints gbc_parentStatusCodes = new GridBagConstraints();
        gbc_parentStatusCodes.gridwidth = 2;
        gbc_parentStatusCodes.gridheight = 2;
        gbc_parentStatusCodes.insets = new Insets(0, 0, 5, 5);
        gbc_parentStatusCodes.fill = GridBagConstraints.BOTH;
        gbc_parentStatusCodes.gridx = 0;
        gbc_parentStatusCodes.gridy = 2;
        add(parentStatusCodes, gbc_parentStatusCodes);

        SelfAuthSeqView selfAuthSeqView = new SelfAuthSeqView();
        selfAuthSeqView.setBorder(new TitledBorder(null, "Self Sequencing",
                                                   TitledBorder.LEADING,
                                                   TitledBorder.TOP, null, null));
        GridBagConstraints gbc_selfAuthSeqView = new GridBagConstraints();
        gbc_selfAuthSeqView.gridwidth = 2;
        gbc_selfAuthSeqView.gridheight = 2;
        gbc_selfAuthSeqView.insets = new Insets(0, 0, 5, 0);
        gbc_selfAuthSeqView.fill = GridBagConstraints.BOTH;
        gbc_selfAuthSeqView.gridx = 2;
        gbc_selfAuthSeqView.gridy = 1;
        add(selfAuthSeqView, gbc_selfAuthSeqView);

        ParentSeqAuthView parentSeqAuthView = new ParentSeqAuthView();
        parentSeqAuthView.setBorder(new TitledBorder(null, "Parent Sequencing",
                                                     TitledBorder.LEADING,
                                                     TitledBorder.TOP, null,
                                                     null));
        GridBagConstraints gbc_parentSeqAuthView = new GridBagConstraints();
        gbc_parentSeqAuthView.gridwidth = 2;
        gbc_parentSeqAuthView.insets = new Insets(0, 0, 5, 0);
        gbc_parentSeqAuthView.fill = GridBagConstraints.BOTH;
        gbc_parentSeqAuthView.gridx = 2;
        gbc_parentSeqAuthView.gridy = 3;
        add(parentSeqAuthView, gbc_parentSeqAuthView);

        JLabel lblChildStatusCodes = new JLabel("Child Status Codes");
        GridBagConstraints gbc_lblChildStatusCodes = new GridBagConstraints();
        gbc_lblChildStatusCodes.gridwidth = 2;
        gbc_lblChildStatusCodes.insets = new Insets(0, 0, 5, 5);
        gbc_lblChildStatusCodes.gridx = 0;
        gbc_lblChildStatusCodes.gridy = 4;
        add(lblChildStatusCodes, gbc_lblChildStatusCodes);

        childStatusCodes = new JList<>();
        GridBagConstraints gbc_childStatusCodes = new GridBagConstraints();
        gbc_childStatusCodes.gridwidth = 2;
        gbc_childStatusCodes.gridheight = 2;
        gbc_childStatusCodes.insets = new Insets(0, 0, 0, 5);
        gbc_childStatusCodes.fill = GridBagConstraints.BOTH;
        gbc_childStatusCodes.gridx = 0;
        gbc_childStatusCodes.gridy = 5;
        add(childStatusCodes, gbc_childStatusCodes);

        SiblingSeqAuthView siblingSeqAuthView = new SiblingSeqAuthView();
        siblingSeqAuthView.setBorder(new TitledBorder(null,
                                                      "Sibling Sequencing",
                                                      TitledBorder.LEADING,
                                                      TitledBorder.TOP, null,
                                                      null));
        GridBagConstraints gbc_siblingSeqAuthView = new GridBagConstraints();
        gbc_siblingSeqAuthView.gridwidth = 2;
        gbc_siblingSeqAuthView.gridheight = 2;
        gbc_siblingSeqAuthView.insets = new Insets(0, 0, 5, 0);
        gbc_siblingSeqAuthView.fill = GridBagConstraints.BOTH;
        gbc_siblingSeqAuthView.gridx = 2;
        gbc_siblingSeqAuthView.gridy = 4;
        add(siblingSeqAuthView, gbc_siblingSeqAuthView);

        ChildAuthSeqView childAuthSeqView = new ChildAuthSeqView();
        childAuthSeqView.setBorder(new TitledBorder(null, "Child Sequencing",
                                                    TitledBorder.LEADING,
                                                    TitledBorder.TOP, null,
                                                    null));
        GridBagConstraints gbc_childAuthSeqView = new GridBagConstraints();
        gbc_childAuthSeqView.gridwidth = 2;
        gbc_childAuthSeqView.fill = GridBagConstraints.BOTH;
        gbc_childAuthSeqView.gridx = 2;
        gbc_childAuthSeqView.gridy = 6;
        add(childAuthSeqView, gbc_childAuthSeqView);

    }

}
