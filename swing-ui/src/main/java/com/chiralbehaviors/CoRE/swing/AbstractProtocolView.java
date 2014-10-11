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

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 *
 */
public class AbstractProtocolView extends JPanel {

    private static final long    serialVersionUID = 1L;
    private JLabel               lblService;
    private JComboBox<Product>   service;
    private JComboBox<Attribute> serviceAttribute;
    private JComboBox<Agency>    requester;
    private JComboBox<Location>  deliverFrom;
    private JComboBox<Attribute> deliverToAttribute;
    private JComboBox<Location>  deliverTo;
    private JComboBox<Attribute> productAttribute;
    private JComboBox<Product>   product;
    private JComboBox<Attribute> assignToAttribute;
    private JComboBox<Agency>    assignTo;
    private JComboBox<Attribute> requesterAttribute;
    private JComboBox<Location>  deliverFromAttribute;
    private JTextField           quantity;
    private JComboBox<Unit>      quantityUnit;

    /**
     * Create the panel.
     */
    public AbstractProtocolView() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 3, 3, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 2.0,
                Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, Double.MIN_VALUE };
        setLayout(gridBagLayout);

        JLabel lblEntities = new JLabel("Entities");
        GridBagConstraints gbc_lblEntities = new GridBagConstraints();
        gbc_lblEntities.insets = new Insets(0, 0, 5, 5);
        gbc_lblEntities.gridx = 1;
        gbc_lblEntities.gridy = 0;
        add(lblEntities, gbc_lblEntities);

        JLabel lblAttributes = new JLabel("Attributes");
        GridBagConstraints gbc_lblAttributes = new GridBagConstraints();
        gbc_lblAttributes.insets = new Insets(0, 0, 5, 0);
        gbc_lblAttributes.gridx = 2;
        gbc_lblAttributes.gridy = 0;
        add(lblAttributes, gbc_lblAttributes);

        lblService = new JLabel("Service");
        GridBagConstraints gbc_lblService = new GridBagConstraints();
        gbc_lblService.anchor = GridBagConstraints.EAST;
        gbc_lblService.insets = new Insets(0, 0, 5, 5);
        gbc_lblService.gridx = 0;
        gbc_lblService.gridy = 1;
        add(lblService, gbc_lblService);

        service = new JComboBox<>();
        GridBagConstraints gbc_service = new GridBagConstraints();
        gbc_service.insets = new Insets(0, 0, 5, 5);
        gbc_service.fill = GridBagConstraints.HORIZONTAL;
        gbc_service.gridx = 1;
        gbc_service.gridy = 1;
        add(service, gbc_service);

        serviceAttribute = new JComboBox<>();
        GridBagConstraints gbc_serviceAttribute = new GridBagConstraints();
        gbc_serviceAttribute.insets = new Insets(0, 0, 5, 0);
        gbc_serviceAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_serviceAttribute.gridx = 2;
        gbc_serviceAttribute.gridy = 1;
        add(serviceAttribute, gbc_serviceAttribute);

        JLabel lblRequester = new JLabel("Requester");
        GridBagConstraints gbc_lblRequester = new GridBagConstraints();
        gbc_lblRequester.anchor = GridBagConstraints.EAST;
        gbc_lblRequester.insets = new Insets(0, 0, 5, 5);
        gbc_lblRequester.gridx = 0;
        gbc_lblRequester.gridy = 2;
        add(lblRequester, gbc_lblRequester);

        requester = new JComboBox<>();
        GridBagConstraints gbc_requester = new GridBagConstraints();
        gbc_requester.insets = new Insets(0, 0, 5, 5);
        gbc_requester.fill = GridBagConstraints.HORIZONTAL;
        gbc_requester.gridx = 1;
        gbc_requester.gridy = 2;
        add(requester, gbc_requester);

        requesterAttribute = new JComboBox<>();
        GridBagConstraints gbc_requesterAttribute = new GridBagConstraints();
        gbc_requesterAttribute.insets = new Insets(0, 0, 5, 0);
        gbc_requesterAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_requesterAttribute.gridx = 2;
        gbc_requesterAttribute.gridy = 2;
        add(requesterAttribute, gbc_requesterAttribute);

        JLabel lblAssignTo = new JLabel("Assign To");
        GridBagConstraints gbc_lblAssignTo = new GridBagConstraints();
        gbc_lblAssignTo.anchor = GridBagConstraints.EAST;
        gbc_lblAssignTo.insets = new Insets(0, 0, 5, 5);
        gbc_lblAssignTo.gridx = 0;
        gbc_lblAssignTo.gridy = 3;
        add(lblAssignTo, gbc_lblAssignTo);

        assignTo = new JComboBox<>();
        GridBagConstraints gbc_assignTo = new GridBagConstraints();
        gbc_assignTo.insets = new Insets(0, 0, 5, 5);
        gbc_assignTo.fill = GridBagConstraints.HORIZONTAL;
        gbc_assignTo.gridx = 1;
        gbc_assignTo.gridy = 3;
        add(assignTo, gbc_assignTo);

        assignToAttribute = new JComboBox<>();
        GridBagConstraints gbc_assignToAttribute = new GridBagConstraints();
        gbc_assignToAttribute.insets = new Insets(0, 0, 5, 0);
        gbc_assignToAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_assignToAttribute.gridx = 2;
        gbc_assignToAttribute.gridy = 3;
        add(assignToAttribute, gbc_assignToAttribute);

        JLabel lblAssignTo_1 = new JLabel("Product");
        GridBagConstraints gbc_lblAssignTo_1 = new GridBagConstraints();
        gbc_lblAssignTo_1.anchor = GridBagConstraints.EAST;
        gbc_lblAssignTo_1.insets = new Insets(0, 0, 5, 5);
        gbc_lblAssignTo_1.gridx = 0;
        gbc_lblAssignTo_1.gridy = 4;
        add(lblAssignTo_1, gbc_lblAssignTo_1);

        product = new JComboBox<>();
        GridBagConstraints gbc_product = new GridBagConstraints();
        gbc_product.insets = new Insets(0, 0, 5, 5);
        gbc_product.fill = GridBagConstraints.HORIZONTAL;
        gbc_product.gridx = 1;
        gbc_product.gridy = 4;
        add(product, gbc_product);

        productAttribute = new JComboBox<>();
        GridBagConstraints gbc_productAttribute = new GridBagConstraints();
        gbc_productAttribute.insets = new Insets(0, 0, 5, 0);
        gbc_productAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_productAttribute.gridx = 2;
        gbc_productAttribute.gridy = 4;
        add(productAttribute, gbc_productAttribute);

        JLabel lblDeliverTo = new JLabel("Deliver To");
        GridBagConstraints gbc_lblDeliverTo = new GridBagConstraints();
        gbc_lblDeliverTo.anchor = GridBagConstraints.EAST;
        gbc_lblDeliverTo.insets = new Insets(0, 0, 5, 5);
        gbc_lblDeliverTo.gridx = 0;
        gbc_lblDeliverTo.gridy = 5;
        add(lblDeliverTo, gbc_lblDeliverTo);

        deliverTo = new JComboBox<>();
        GridBagConstraints gbc_deliverTo = new GridBagConstraints();
        gbc_deliverTo.insets = new Insets(0, 0, 5, 5);
        gbc_deliverTo.fill = GridBagConstraints.HORIZONTAL;
        gbc_deliverTo.gridx = 1;
        gbc_deliverTo.gridy = 5;
        add(deliverTo, gbc_deliverTo);

        deliverToAttribute = new JComboBox<>();
        GridBagConstraints gbc_deliverToAttribute = new GridBagConstraints();
        gbc_deliverToAttribute.insets = new Insets(0, 0, 5, 0);
        gbc_deliverToAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_deliverToAttribute.gridx = 2;
        gbc_deliverToAttribute.gridy = 5;
        add(deliverToAttribute, gbc_deliverToAttribute);

        JLabel lblDeliverFrom = new JLabel("Deliver From");
        GridBagConstraints gbc_lblDeliverFrom = new GridBagConstraints();
        gbc_lblDeliverFrom.anchor = GridBagConstraints.EAST;
        gbc_lblDeliverFrom.insets = new Insets(0, 0, 5, 5);
        gbc_lblDeliverFrom.gridx = 0;
        gbc_lblDeliverFrom.gridy = 6;
        add(lblDeliverFrom, gbc_lblDeliverFrom);

        deliverFrom = new JComboBox<>();
        GridBagConstraints gbc_deliverFrom = new GridBagConstraints();
        gbc_deliverFrom.insets = new Insets(0, 0, 5, 5);
        gbc_deliverFrom.fill = GridBagConstraints.HORIZONTAL;
        gbc_deliverFrom.gridx = 1;
        gbc_deliverFrom.gridy = 6;
        add(deliverFrom, gbc_deliverFrom);

        deliverFromAttribute = new JComboBox<>();
        GridBagConstraints gbc_deliverFromAttribute = new GridBagConstraints();
        gbc_deliverFromAttribute.insets = new Insets(0, 0, 5, 0);
        gbc_deliverFromAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_deliverFromAttribute.gridx = 2;
        gbc_deliverFromAttribute.gridy = 6;
        add(deliverFromAttribute, gbc_deliverFromAttribute);

        JLabel lblUnit = new JLabel("Quantity Unit");
        GridBagConstraints gbc_lblUnit = new GridBagConstraints();
        gbc_lblUnit.insets = new Insets(0, 0, 5, 0);
        gbc_lblUnit.gridx = 2;
        gbc_lblUnit.gridy = 7;
        add(lblUnit, gbc_lblUnit);

        JLabel lblQuantity = new JLabel("Quantity");
        GridBagConstraints gbc_lblQuantity = new GridBagConstraints();
        gbc_lblQuantity.anchor = GridBagConstraints.EAST;
        gbc_lblQuantity.insets = new Insets(0, 0, 0, 5);
        gbc_lblQuantity.gridx = 0;
        gbc_lblQuantity.gridy = 8;
        add(lblQuantity, gbc_lblQuantity);

        quantity = new JTextField();
        GridBagConstraints gbc_quantity = new GridBagConstraints();
        gbc_quantity.insets = new Insets(0, 0, 0, 5);
        gbc_quantity.fill = GridBagConstraints.HORIZONTAL;
        gbc_quantity.gridx = 1;
        gbc_quantity.gridy = 8;
        add(quantity, gbc_quantity);
        quantity.setColumns(10);

        quantityUnit = new JComboBox<>();
        GridBagConstraints gbc_quantityUnit = new GridBagConstraints();
        gbc_quantityUnit.fill = GridBagConstraints.HORIZONTAL;
        gbc_quantityUnit.gridx = 2;
        gbc_quantityUnit.gridy = 8;
        add(quantityUnit, gbc_quantityUnit);

    }

}
