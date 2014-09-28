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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 *
 */
public class SelfAuthSeqView extends JPanel {

    private static final long     serialVersionUID = 1L;
    private JComboBox<StatusCode> statusCode;
    private JComboBox<Product>    service;
    private JComboBox<StatusCode> statusToSet;

    /**
     * Create the panel.
     */
    public SelfAuthSeqView() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0,
                Double.MIN_VALUE };
        setLayout(gridBagLayout);

        JLabel lblService = new JLabel("Service");
        GridBagConstraints gbc_lblService = new GridBagConstraints();
        gbc_lblService.insets = new Insets(0, 0, 5, 5);
        gbc_lblService.anchor = GridBagConstraints.EAST;
        gbc_lblService.gridx = 0;
        gbc_lblService.gridy = 0;
        add(lblService, gbc_lblService);

        service = new JComboBox<>();
        GridBagConstraints gbc_service = new GridBagConstraints();
        gbc_service.insets = new Insets(0, 0, 5, 0);
        gbc_service.fill = GridBagConstraints.HORIZONTAL;
        gbc_service.gridx = 1;
        gbc_service.gridy = 0;
        add(service, gbc_service);

        JLabel lblStatusCode = new JLabel("Status Code");
        GridBagConstraints gbc_lblStatusCode = new GridBagConstraints();
        gbc_lblStatusCode.anchor = GridBagConstraints.EAST;
        gbc_lblStatusCode.insets = new Insets(0, 0, 5, 5);
        gbc_lblStatusCode.gridx = 0;
        gbc_lblStatusCode.gridy = 1;
        add(lblStatusCode, gbc_lblStatusCode);

        statusCode = new JComboBox<>();
        GridBagConstraints gbc_statusCode = new GridBagConstraints();
        gbc_statusCode.insets = new Insets(0, 0, 5, 0);
        gbc_statusCode.fill = GridBagConstraints.HORIZONTAL;
        gbc_statusCode.gridx = 1;
        gbc_statusCode.gridy = 1;
        add(statusCode, gbc_statusCode);

        JLabel lblNewLabel = new JLabel("Status To Set");
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
        gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 2;
        add(lblNewLabel, gbc_lblNewLabel);

        statusToSet = new JComboBox<>();
        GridBagConstraints gbc_statusToSet = new GridBagConstraints();
        gbc_statusToSet.fill = GridBagConstraints.HORIZONTAL;
        gbc_statusToSet.gridx = 1;
        gbc_statusToSet.gridy = 2;
        add(statusToSet, gbc_statusToSet);

    }

}
