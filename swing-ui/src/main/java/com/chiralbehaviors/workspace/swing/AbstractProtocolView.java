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
import javax.swing.JTextField;

/**
 * @author hhildebrand
 *
 */
public class AbstractProtocolView extends JPanel {

    private static final long serialVersionUID = 1L;
    private JComboBox         assignTo;
    private JComboBox         assignToAttribute;
    private JLabel            lblDeliverFrom;
    private JComboBox         deliverFrom;
    private JLabel            lblDeliverFromAttribute;
    private JComboBox         deliverFromAttribute;
    private JLabel            lblDeliverTo;
    private JLabel            lblDeliverToAttribute;
    private JLabel            lblRequester;
    private JLabel            lblProductAttribute;
    private JLabel            lblRequester_1;
    private JLabel            lblRequesterAttribute;
    private JComboBox         deliverTo;
    private JComboBox         deliverToAttribute;
    private JComboBox         product;
    private JComboBox         productAttribute;
    private JComboBox         requester;
    private JComboBox         requesterAttribute;
    private JLabel            lblQuantity;
    private JTextField        quantity;
    private JLabel            lblQuantityUnit;
    private JComboBox         quantityUnit;
    private JLabel            lblService;
    private JComboBox         service;
    private JLabel            lblServiceAttribute;
    private JComboBox         comboBox;

    /**
     * Create the panel.
     */
    public AbstractProtocolView() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 100, 0, 100 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0 };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0 };
        setLayout(gridBagLayout);

        JLabel lblAssignTo = new JLabel("Assign To");
        GridBagConstraints gbc_lblAssignTo = new GridBagConstraints();
        gbc_lblAssignTo.anchor = GridBagConstraints.EAST;
        gbc_lblAssignTo.fill = GridBagConstraints.VERTICAL;
        gbc_lblAssignTo.insets = new Insets(0, 0, 5, 5);
        gbc_lblAssignTo.gridx = 0;
        gbc_lblAssignTo.gridy = 0;
        add(lblAssignTo, gbc_lblAssignTo);

        assignTo = new JComboBox();
        GridBagConstraints gbc_assignTo = new GridBagConstraints();
        gbc_assignTo.insets = new Insets(0, 0, 5, 5);
        gbc_assignTo.fill = GridBagConstraints.BOTH;
        gbc_assignTo.gridx = 1;
        gbc_assignTo.gridy = 0;
        add(assignTo, gbc_assignTo);

        JLabel lblAssignToAttribute = new JLabel("Assign To Attribute");
        GridBagConstraints gbc_lblAssignToAttribute = new GridBagConstraints();
        gbc_lblAssignToAttribute.anchor = GridBagConstraints.EAST;
        gbc_lblAssignToAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_lblAssignToAttribute.gridx = 2;
        gbc_lblAssignToAttribute.gridy = 0;
        add(lblAssignToAttribute, gbc_lblAssignToAttribute);

        assignToAttribute = new JComboBox();
        GridBagConstraints gbc_assignToAttribute = new GridBagConstraints();
        gbc_assignToAttribute.insets = new Insets(0, 0, 5, 0);
        gbc_assignToAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_assignToAttribute.gridx = 3;
        gbc_assignToAttribute.gridy = 0;
        add(assignToAttribute, gbc_assignToAttribute);

        lblDeliverFrom = new JLabel("Deliver From");
        GridBagConstraints gbc_lblDeliverFrom = new GridBagConstraints();
        gbc_lblDeliverFrom.anchor = GridBagConstraints.EAST;
        gbc_lblDeliverFrom.insets = new Insets(0, 0, 5, 5);
        gbc_lblDeliverFrom.gridx = 0;
        gbc_lblDeliverFrom.gridy = 1;
        add(lblDeliverFrom, gbc_lblDeliverFrom);

        deliverFrom = new JComboBox();
        GridBagConstraints gbc_deliverFrom = new GridBagConstraints();
        gbc_deliverFrom.insets = new Insets(0, 0, 5, 5);
        gbc_deliverFrom.fill = GridBagConstraints.HORIZONTAL;
        gbc_deliverFrom.gridx = 1;
        gbc_deliverFrom.gridy = 1;
        add(deliverFrom, gbc_deliverFrom);

        lblDeliverFromAttribute = new JLabel("Deliver From Attribute");
        GridBagConstraints gbc_lblDeliverFromAttribute = new GridBagConstraints();
        gbc_lblDeliverFromAttribute.anchor = GridBagConstraints.EAST;
        gbc_lblDeliverFromAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_lblDeliverFromAttribute.gridx = 2;
        gbc_lblDeliverFromAttribute.gridy = 1;
        add(lblDeliverFromAttribute, gbc_lblDeliverFromAttribute);

        deliverFromAttribute = new JComboBox();
        GridBagConstraints gbc_deliverFromAttribute = new GridBagConstraints();
        gbc_deliverFromAttribute.insets = new Insets(0, 0, 5, 0);
        gbc_deliverFromAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_deliverFromAttribute.gridx = 3;
        gbc_deliverFromAttribute.gridy = 1;
        add(deliverFromAttribute, gbc_deliverFromAttribute);

        lblDeliverTo = new JLabel("Deliver To");
        GridBagConstraints gbc_lblDeliverTo = new GridBagConstraints();
        gbc_lblDeliverTo.anchor = GridBagConstraints.EAST;
        gbc_lblDeliverTo.insets = new Insets(0, 0, 5, 5);
        gbc_lblDeliverTo.gridx = 0;
        gbc_lblDeliverTo.gridy = 2;
        add(lblDeliverTo, gbc_lblDeliverTo);

        deliverTo = new JComboBox();
        GridBagConstraints gbc_deliverTo = new GridBagConstraints();
        gbc_deliverTo.insets = new Insets(0, 0, 5, 5);
        gbc_deliverTo.fill = GridBagConstraints.HORIZONTAL;
        gbc_deliverTo.gridx = 1;
        gbc_deliverTo.gridy = 2;
        add(deliverTo, gbc_deliverTo);

        lblDeliverToAttribute = new JLabel("Deliver To Attribute");
        GridBagConstraints gbc_lblDeliverToAttribute = new GridBagConstraints();
        gbc_lblDeliverToAttribute.anchor = GridBagConstraints.EAST;
        gbc_lblDeliverToAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_lblDeliverToAttribute.gridx = 2;
        gbc_lblDeliverToAttribute.gridy = 2;
        add(lblDeliverToAttribute, gbc_lblDeliverToAttribute);

        deliverToAttribute = new JComboBox();
        GridBagConstraints gbc_deliverToAttribute = new GridBagConstraints();
        gbc_deliverToAttribute.insets = new Insets(0, 0, 5, 0);
        gbc_deliverToAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_deliverToAttribute.gridx = 3;
        gbc_deliverToAttribute.gridy = 2;
        add(deliverToAttribute, gbc_deliverToAttribute);

        lblRequester = new JLabel("Product");
        GridBagConstraints gbc_lblRequester = new GridBagConstraints();
        gbc_lblRequester.anchor = GridBagConstraints.EAST;
        gbc_lblRequester.insets = new Insets(0, 0, 5, 5);
        gbc_lblRequester.gridx = 0;
        gbc_lblRequester.gridy = 3;
        add(lblRequester, gbc_lblRequester);

        product = new JComboBox();
        GridBagConstraints gbc_product = new GridBagConstraints();
        gbc_product.insets = new Insets(0, 0, 5, 5);
        gbc_product.fill = GridBagConstraints.HORIZONTAL;
        gbc_product.gridx = 1;
        gbc_product.gridy = 3;
        add(product, gbc_product);

        lblProductAttribute = new JLabel("Product Attribute");
        GridBagConstraints gbc_lblProductAttribute = new GridBagConstraints();
        gbc_lblProductAttribute.anchor = GridBagConstraints.EAST;
        gbc_lblProductAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_lblProductAttribute.gridx = 2;
        gbc_lblProductAttribute.gridy = 3;
        add(lblProductAttribute, gbc_lblProductAttribute);

        productAttribute = new JComboBox();
        GridBagConstraints gbc_productAttribute = new GridBagConstraints();
        gbc_productAttribute.insets = new Insets(0, 0, 5, 0);
        gbc_productAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_productAttribute.gridx = 3;
        gbc_productAttribute.gridy = 3;
        add(productAttribute, gbc_productAttribute);

        lblRequester_1 = new JLabel("Requester");
        GridBagConstraints gbc_lblRequester_1 = new GridBagConstraints();
        gbc_lblRequester_1.anchor = GridBagConstraints.EAST;
        gbc_lblRequester_1.insets = new Insets(0, 0, 5, 5);
        gbc_lblRequester_1.gridx = 0;
        gbc_lblRequester_1.gridy = 4;
        add(lblRequester_1, gbc_lblRequester_1);

        requester = new JComboBox();
        GridBagConstraints gbc_requester = new GridBagConstraints();
        gbc_requester.insets = new Insets(0, 0, 5, 5);
        gbc_requester.fill = GridBagConstraints.HORIZONTAL;
        gbc_requester.gridx = 1;
        gbc_requester.gridy = 4;
        add(requester, gbc_requester);

        lblRequesterAttribute = new JLabel("Requester Attribute");
        GridBagConstraints gbc_lblRequesterAttribute = new GridBagConstraints();
        gbc_lblRequesterAttribute.anchor = GridBagConstraints.EAST;
        gbc_lblRequesterAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_lblRequesterAttribute.gridx = 2;
        gbc_lblRequesterAttribute.gridy = 4;
        add(lblRequesterAttribute, gbc_lblRequesterAttribute);

        requesterAttribute = new JComboBox();
        GridBagConstraints gbc_requesterAttribute = new GridBagConstraints();
        gbc_requesterAttribute.insets = new Insets(0, 0, 5, 0);
        gbc_requesterAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_requesterAttribute.gridx = 3;
        gbc_requesterAttribute.gridy = 4;
        add(requesterAttribute, gbc_requesterAttribute);

        lblQuantity = new JLabel("Quantity");
        GridBagConstraints gbc_lblQuantity = new GridBagConstraints();
        gbc_lblQuantity.anchor = GridBagConstraints.EAST;
        gbc_lblQuantity.insets = new Insets(0, 0, 5, 5);
        gbc_lblQuantity.gridx = 0;
        gbc_lblQuantity.gridy = 5;
        add(lblQuantity, gbc_lblQuantity);

        quantity = new JTextField();
        GridBagConstraints gbc_quantity = new GridBagConstraints();
        gbc_quantity.insets = new Insets(0, 0, 5, 5);
        gbc_quantity.fill = GridBagConstraints.HORIZONTAL;
        gbc_quantity.gridx = 1;
        gbc_quantity.gridy = 5;
        add(quantity, gbc_quantity);
        quantity.setColumns(10);

        lblQuantityUnit = new JLabel("Quantity Unit");
        GridBagConstraints gbc_lblQuantityUnit = new GridBagConstraints();
        gbc_lblQuantityUnit.anchor = GridBagConstraints.EAST;
        gbc_lblQuantityUnit.insets = new Insets(0, 0, 5, 5);
        gbc_lblQuantityUnit.gridx = 2;
        gbc_lblQuantityUnit.gridy = 5;
        add(lblQuantityUnit, gbc_lblQuantityUnit);

        quantityUnit = new JComboBox();
        GridBagConstraints gbc_quantityUnit = new GridBagConstraints();
        gbc_quantityUnit.insets = new Insets(0, 0, 5, 0);
        gbc_quantityUnit.fill = GridBagConstraints.HORIZONTAL;
        gbc_quantityUnit.gridx = 3;
        gbc_quantityUnit.gridy = 5;
        add(quantityUnit, gbc_quantityUnit);

        lblService = new JLabel("Service");
        GridBagConstraints gbc_lblService = new GridBagConstraints();
        gbc_lblService.anchor = GridBagConstraints.EAST;
        gbc_lblService.insets = new Insets(0, 0, 0, 5);
        gbc_lblService.gridx = 0;
        gbc_lblService.gridy = 6;
        add(lblService, gbc_lblService);

        service = new JComboBox();
        GridBagConstraints gbc_service = new GridBagConstraints();
        gbc_service.insets = new Insets(0, 0, 0, 5);
        gbc_service.fill = GridBagConstraints.HORIZONTAL;
        gbc_service.gridx = 1;
        gbc_service.gridy = 6;
        add(service, gbc_service);

        lblServiceAttribute = new JLabel("Service Attribute");
        GridBagConstraints gbc_lblServiceAttribute = new GridBagConstraints();
        gbc_lblServiceAttribute.anchor = GridBagConstraints.EAST;
        gbc_lblServiceAttribute.insets = new Insets(0, 0, 0, 5);
        gbc_lblServiceAttribute.gridx = 2;
        gbc_lblServiceAttribute.gridy = 6;
        add(lblServiceAttribute, gbc_lblServiceAttribute);

        comboBox = new JComboBox();
        GridBagConstraints gbc_comboBox = new GridBagConstraints();
        gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox.gridx = 3;
        gbc_comboBox.gridy = 6;
        add(comboBox, gbc_comboBox);

    }

}
