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
import javax.swing.JSpinner;

import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 *
 */
public class MetaProtocolView extends JPanel {

    private static final long       serialVersionUID = 1L;
    private JComboBox<Relationship> serviceType;
    private JComboBox<Relationship> requester;
    private JSpinner                sequenceNumber;
    private JComboBox<Relationship> serviceAttribute;
    private JComboBox<Relationship> requesterAttribute;
    private JComboBox<Relationship> assignTo;
    private JComboBox<Relationship> assignToAttribute;
    private JComboBox<Relationship> product;
    private JComboBox<Relationship> productAttribute;
    private JComboBox<Relationship> deliverFrom;
    private JComboBox<Relationship> deliverFromAttribute;
    private JComboBox<Relationship> deliverTo;
    private JComboBox<Relationship> deliverToAttribute;
    private JComboBox<Relationship> quantityUnit;
    private JComboBox<Product>      service;
    private JCheckBox               stopOnMatch;

    /**
     * Create the panel.
     */
    public MetaProtocolView() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 158, 42, 0, 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0,
                1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        setLayout(gridBagLayout);

        JLabel lblService = new JLabel("Service");
        GridBagConstraints gbc_lblService = new GridBagConstraints();
        gbc_lblService.anchor = GridBagConstraints.EAST;
        gbc_lblService.insets = new Insets(0, 0, 5, 5);
        gbc_lblService.gridx = 0;
        gbc_lblService.gridy = 0;
        add(lblService, gbc_lblService);

        service = new JComboBox<>();
        GridBagConstraints gbc_service = new GridBagConstraints();
        gbc_service.insets = new Insets(0, 0, 5, 5);
        gbc_service.fill = GridBagConstraints.HORIZONTAL;
        gbc_service.gridx = 1;
        gbc_service.gridy = 0;
        add(service, gbc_service);

        JLabel lblSequence = new JLabel("Sequence #");
        GridBagConstraints gbc_lblSequence = new GridBagConstraints();
        gbc_lblSequence.insets = new Insets(0, 0, 5, 5);
        gbc_lblSequence.gridx = 3;
        gbc_lblSequence.gridy = 0;
        add(lblSequence, gbc_lblSequence);

        sequenceNumber = new JSpinner();
        GridBagConstraints gbc_sequenceNumber = new GridBagConstraints();
        gbc_sequenceNumber.insets = new Insets(0, 0, 5, 5);
        gbc_sequenceNumber.gridx = 4;
        gbc_sequenceNumber.gridy = 0;
        add(sequenceNumber, gbc_sequenceNumber);

        stopOnMatch = new JCheckBox("Stop On Match");
        GridBagConstraints gbc_stopOnMatch = new GridBagConstraints();
        gbc_stopOnMatch.insets = new Insets(0, 0, 5, 5);
        gbc_stopOnMatch.gridx = 1;
        gbc_stopOnMatch.gridy = 1;
        add(stopOnMatch, gbc_stopOnMatch);

        JLabel lblEntities = new JLabel("Entities");
        GridBagConstraints gbc_lblEntities = new GridBagConstraints();
        gbc_lblEntities.insets = new Insets(0, 0, 5, 5);
        gbc_lblEntities.gridx = 1;
        gbc_lblEntities.gridy = 2;
        add(lblEntities, gbc_lblEntities);

        JLabel lblAttributes = new JLabel("Attributes");
        GridBagConstraints gbc_lblAttributes = new GridBagConstraints();
        gbc_lblAttributes.gridwidth = 3;
        gbc_lblAttributes.insets = new Insets(0, 0, 5, 0);
        gbc_lblAttributes.gridx = 3;
        gbc_lblAttributes.gridy = 2;
        add(lblAttributes, gbc_lblAttributes);

        JLabel lblServicetype = new JLabel("Service Type");
        GridBagConstraints gbc_lblServicetype = new GridBagConstraints();
        gbc_lblServicetype.anchor = GridBagConstraints.EAST;
        gbc_lblServicetype.insets = new Insets(0, 0, 5, 5);
        gbc_lblServicetype.gridx = 0;
        gbc_lblServicetype.gridy = 3;
        add(lblServicetype, gbc_lblServicetype);

        serviceType = new JComboBox<>();
        GridBagConstraints gbc_serviceType = new GridBagConstraints();
        gbc_serviceType.insets = new Insets(0, 0, 5, 5);
        gbc_serviceType.fill = GridBagConstraints.HORIZONTAL;
        gbc_serviceType.gridx = 1;
        gbc_serviceType.gridy = 3;
        add(serviceType, gbc_serviceType);

        serviceAttribute = new JComboBox<>();
        GridBagConstraints gbc_serviceAttribute = new GridBagConstraints();
        gbc_serviceAttribute.gridwidth = 3;
        gbc_serviceAttribute.insets = new Insets(0, 0, 5, 0);
        gbc_serviceAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_serviceAttribute.gridx = 3;
        gbc_serviceAttribute.gridy = 3;
        add(serviceAttribute, gbc_serviceAttribute);

        JLabel lblRequester = new JLabel("Requester");
        GridBagConstraints gbc_lblRequester = new GridBagConstraints();
        gbc_lblRequester.anchor = GridBagConstraints.EAST;
        gbc_lblRequester.insets = new Insets(0, 0, 5, 5);
        gbc_lblRequester.gridx = 0;
        gbc_lblRequester.gridy = 4;
        add(lblRequester, gbc_lblRequester);

        requester = new JComboBox<>();
        GridBagConstraints gbc_requester = new GridBagConstraints();
        gbc_requester.insets = new Insets(0, 0, 5, 5);
        gbc_requester.fill = GridBagConstraints.HORIZONTAL;
        gbc_requester.gridx = 1;
        gbc_requester.gridy = 4;
        add(requester, gbc_requester);

        requesterAttribute = new JComboBox<>();
        GridBagConstraints gbc_requesterAttribute = new GridBagConstraints();
        gbc_requesterAttribute.gridwidth = 3;
        gbc_requesterAttribute.insets = new Insets(0, 0, 5, 0);
        gbc_requesterAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_requesterAttribute.gridx = 3;
        gbc_requesterAttribute.gridy = 4;
        add(requesterAttribute, gbc_requesterAttribute);

        JLabel lblAssignTo = new JLabel("Assign To");
        GridBagConstraints gbc_lblAssignTo = new GridBagConstraints();
        gbc_lblAssignTo.anchor = GridBagConstraints.EAST;
        gbc_lblAssignTo.insets = new Insets(0, 0, 5, 5);
        gbc_lblAssignTo.gridx = 0;
        gbc_lblAssignTo.gridy = 5;
        add(lblAssignTo, gbc_lblAssignTo);

        assignTo = new JComboBox<>();
        GridBagConstraints gbc_assignTo = new GridBagConstraints();
        gbc_assignTo.insets = new Insets(0, 0, 5, 5);
        gbc_assignTo.fill = GridBagConstraints.HORIZONTAL;
        gbc_assignTo.gridx = 1;
        gbc_assignTo.gridy = 5;
        add(assignTo, gbc_assignTo);

        assignToAttribute = new JComboBox<>();
        GridBagConstraints gbc_assignToAttribute = new GridBagConstraints();
        gbc_assignToAttribute.gridwidth = 3;
        gbc_assignToAttribute.insets = new Insets(0, 0, 5, 0);
        gbc_assignToAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_assignToAttribute.gridx = 3;
        gbc_assignToAttribute.gridy = 5;
        add(assignToAttribute, gbc_assignToAttribute);

        JLabel lblProduct = new JLabel("Product");
        GridBagConstraints gbc_lblProduct = new GridBagConstraints();
        gbc_lblProduct.anchor = GridBagConstraints.EAST;
        gbc_lblProduct.insets = new Insets(0, 0, 5, 5);
        gbc_lblProduct.gridx = 0;
        gbc_lblProduct.gridy = 6;
        add(lblProduct, gbc_lblProduct);

        product = new JComboBox<>();
        GridBagConstraints gbc_product = new GridBagConstraints();
        gbc_product.insets = new Insets(0, 0, 5, 5);
        gbc_product.fill = GridBagConstraints.HORIZONTAL;
        gbc_product.gridx = 1;
        gbc_product.gridy = 6;
        add(product, gbc_product);

        productAttribute = new JComboBox<>();
        GridBagConstraints gbc_productAttribute = new GridBagConstraints();
        gbc_productAttribute.gridwidth = 3;
        gbc_productAttribute.insets = new Insets(0, 0, 5, 0);
        gbc_productAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_productAttribute.gridx = 3;
        gbc_productAttribute.gridy = 6;
        add(productAttribute, gbc_productAttribute);

        JLabel lblDeliverFrom = new JLabel("Deliver From");
        GridBagConstraints gbc_lblDeliverFrom = new GridBagConstraints();
        gbc_lblDeliverFrom.anchor = GridBagConstraints.EAST;
        gbc_lblDeliverFrom.insets = new Insets(0, 0, 5, 5);
        gbc_lblDeliverFrom.gridx = 0;
        gbc_lblDeliverFrom.gridy = 7;
        add(lblDeliverFrom, gbc_lblDeliverFrom);

        deliverFrom = new JComboBox<>();
        GridBagConstraints gbc_deliverFrom = new GridBagConstraints();
        gbc_deliverFrom.insets = new Insets(0, 0, 5, 5);
        gbc_deliverFrom.fill = GridBagConstraints.HORIZONTAL;
        gbc_deliverFrom.gridx = 1;
        gbc_deliverFrom.gridy = 7;
        add(deliverFrom, gbc_deliverFrom);

        deliverFromAttribute = new JComboBox<>();
        GridBagConstraints gbc_deliverFromAttribute = new GridBagConstraints();
        gbc_deliverFromAttribute.gridwidth = 3;
        gbc_deliverFromAttribute.insets = new Insets(0, 0, 5, 0);
        gbc_deliverFromAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_deliverFromAttribute.gridx = 3;
        gbc_deliverFromAttribute.gridy = 7;
        add(deliverFromAttribute, gbc_deliverFromAttribute);

        JLabel lblDeliverTo = new JLabel("Deliver To");
        GridBagConstraints gbc_lblDeliverTo = new GridBagConstraints();
        gbc_lblDeliverTo.anchor = GridBagConstraints.EAST;
        gbc_lblDeliverTo.insets = new Insets(0, 0, 5, 5);
        gbc_lblDeliverTo.gridx = 0;
        gbc_lblDeliverTo.gridy = 8;
        add(lblDeliverTo, gbc_lblDeliverTo);

        deliverTo = new JComboBox<>();
        GridBagConstraints gbc_deliverTo = new GridBagConstraints();
        gbc_deliverTo.insets = new Insets(0, 0, 5, 5);
        gbc_deliverTo.fill = GridBagConstraints.HORIZONTAL;
        gbc_deliverTo.gridx = 1;
        gbc_deliverTo.gridy = 8;
        add(deliverTo, gbc_deliverTo);

        deliverToAttribute = new JComboBox<>();
        GridBagConstraints gbc_deliverToAttribute = new GridBagConstraints();
        gbc_deliverToAttribute.gridwidth = 3;
        gbc_deliverToAttribute.insets = new Insets(0, 0, 5, 0);
        gbc_deliverToAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_deliverToAttribute.gridx = 3;
        gbc_deliverToAttribute.gridy = 8;
        add(deliverToAttribute, gbc_deliverToAttribute);

        JLabel lblQuantityUnit = new JLabel("Quantity Unit");
        GridBagConstraints gbc_lblQuantityUnit = new GridBagConstraints();
        gbc_lblQuantityUnit.anchor = GridBagConstraints.EAST;
        gbc_lblQuantityUnit.insets = new Insets(0, 0, 0, 5);
        gbc_lblQuantityUnit.gridx = 0;
        gbc_lblQuantityUnit.gridy = 9;
        add(lblQuantityUnit, gbc_lblQuantityUnit);

        quantityUnit = new JComboBox<>();
        GridBagConstraints gbc_quantityUnit = new GridBagConstraints();
        gbc_quantityUnit.insets = new Insets(0, 0, 0, 5);
        gbc_quantityUnit.fill = GridBagConstraints.HORIZONTAL;
        gbc_quantityUnit.gridx = 1;
        gbc_quantityUnit.gridy = 9;
        add(quantityUnit, gbc_quantityUnit);

    }
}
