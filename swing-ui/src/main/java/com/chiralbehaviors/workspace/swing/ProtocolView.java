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

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;

/**
 * @author hhildebrand
 *
 */
public class ProtocolView extends JPanel {
    private static final long                 serialVersionUID = 1L;

    private JTextField                        name;
    private JTextArea                         notes;
    private JComboBox<Agency>                 assignTo;
    private JComboBox<Attribute>              assignToAttribute;
    private JComboBox<Location>               deliverFrom;
    private JComboBox<WorkspaceAuthorization> keys;
    private JSpinner                          sequenceNumber;
    private AbstractProtocolView              abstractProtocol;
    private JComboBox<Attribute>              deliverFromAttribute;
    private JComboBox<Location>               deliverTo;
    private JComboBox<Attribute>              deliverToAttribute;
    private JComboBox<Product>                product;
    private JComboBox<Attribute>              productAttribute;
    private JComboBox<Product>                service;
    private JComboBox<Attribute>              serviceAttribute;

    /**
     * Create the panel.
     */
    public ProtocolView() {
        FlowLayout flowLayout = (FlowLayout) getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);

        JPanel protocol = new JPanel();
        add(protocol);
        GridBagLayout gbl_protocol = new GridBagLayout();
        gbl_protocol.columnWidths = new int[] { 0, 0, 0, 0, 0, 100 };
        gbl_protocol.rowHeights = new int[] { 0, 0, 0, 0, 0 };
        gbl_protocol.columnWeights = new double[] { 0.0, 1.0, 1.0, 0.0, 0.0,
                Double.MIN_VALUE };
        gbl_protocol.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
        protocol.setLayout(gbl_protocol);

        JLabel lblKey = new JLabel("Key");
        GridBagConstraints gbc_lblKey = new GridBagConstraints();
        gbc_lblKey.anchor = GridBagConstraints.EAST;
        gbc_lblKey.insets = new Insets(0, 0, 5, 5);
        gbc_lblKey.gridx = 1;
        gbc_lblKey.gridy = 1;
        protocol.add(lblKey, gbc_lblKey);

        keys = new JComboBox<>();
        GridBagConstraints gbc_keys = new GridBagConstraints();
        gbc_keys.gridwidth = 3;
        gbc_keys.insets = new Insets(0, 0, 5, 5);
        gbc_keys.fill = GridBagConstraints.HORIZONTAL;
        gbc_keys.gridx = 2;
        gbc_keys.gridy = 1;
        protocol.add(keys, gbc_keys);

        JLabel lblName = new JLabel("Name");
        lblName.setHorizontalAlignment(SwingConstants.RIGHT);
        GridBagConstraints gbc_lblName = new GridBagConstraints();
        gbc_lblName.anchor = GridBagConstraints.EAST;
        gbc_lblName.insets = new Insets(0, 0, 5, 5);
        gbc_lblName.gridx = 1;
        gbc_lblName.gridy = 2;
        protocol.add(lblName, gbc_lblName);

        name = new JTextField();
        GridBagConstraints gbc_name = new GridBagConstraints();
        gbc_name.insets = new Insets(0, 0, 5, 5);
        gbc_name.fill = GridBagConstraints.HORIZONTAL;
        gbc_name.gridx = 2;
        gbc_name.gridy = 2;
        protocol.add(name, gbc_name);
        name.setColumns(10);

        JLabel lblSequence = new JLabel("Sequence #");
        GridBagConstraints gbc_lblSequence = new GridBagConstraints();
        gbc_lblSequence.insets = new Insets(0, 0, 5, 5);
        gbc_lblSequence.gridx = 3;
        gbc_lblSequence.gridy = 2;
        protocol.add(lblSequence, gbc_lblSequence);

        sequenceNumber = new JSpinner();
        GridBagConstraints gbc_sequenceNumber = new GridBagConstraints();
        gbc_sequenceNumber.insets = new Insets(0, 0, 5, 0);
        gbc_sequenceNumber.gridx = 4;
        gbc_sequenceNumber.gridy = 2;
        protocol.add(sequenceNumber, gbc_sequenceNumber);

        JLabel lblDescription = new JLabel("Notes");
        lblDescription.setHorizontalAlignment(SwingConstants.RIGHT);
        GridBagConstraints gbc_lblDescription = new GridBagConstraints();
        gbc_lblDescription.anchor = GridBagConstraints.EAST;
        gbc_lblDescription.insets = new Insets(0, 0, 5, 5);
        gbc_lblDescription.gridx = 1;
        gbc_lblDescription.gridy = 3;
        protocol.add(lblDescription, gbc_lblDescription);

        notes = new JTextArea();
        GridBagConstraints gbc_notes = new GridBagConstraints();
        gbc_notes.gridwidth = 3;
        gbc_notes.gridheight = 2;
        gbc_notes.fill = GridBagConstraints.BOTH;
        gbc_notes.gridx = 2;
        gbc_notes.gridy = 3;
        protocol.add(notes, gbc_notes);

        JPanel match = new JPanel();
        match.setBorder(new TitledBorder(null, "Match", TitledBorder.LEADING,
                                         TitledBorder.TOP, null, null));
        add(match);

        abstractProtocol = new AbstractProtocolView();
        match.add(abstractProtocol);

        JPanel childJob = new JPanel();
        childJob.setBorder(new TitledBorder(null, "Child Job",
                                            TitledBorder.LEADING,
                                            TitledBorder.TOP, null, null));
        add(childJob);
        GridBagLayout gbl_childJob = new GridBagLayout();
        gbl_childJob.rowHeights = new int[] { 0 };
        gbl_childJob.columnWidths = new int[] { 0, 100, 0, 100 };
        gbl_childJob.columnWeights = new double[] { 0.0, 1.0 };
        gbl_childJob.rowWeights = new double[] { 0.0 };
        childJob.setLayout(gbl_childJob);

        JLabel lblNewLabel = new JLabel("Assign To");
        lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
        gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 0;
        childJob.add(lblNewLabel, gbc_lblNewLabel);

        assignTo = new JComboBox<>();
        GridBagConstraints gbc_assignTo = new GridBagConstraints();
        gbc_assignTo.insets = new Insets(0, 0, 5, 5);
        gbc_assignTo.fill = GridBagConstraints.HORIZONTAL;
        gbc_assignTo.gridx = 1;
        gbc_assignTo.gridy = 0;
        childJob.add(assignTo, gbc_assignTo);

        JLabel lblAssignToAttribute = new JLabel("Assign To Attribute");
        GridBagConstraints gbc_lblAssignToAttribute = new GridBagConstraints();
        gbc_lblAssignToAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_lblAssignToAttribute.gridx = 2;
        gbc_lblAssignToAttribute.gridy = 0;
        childJob.add(lblAssignToAttribute, gbc_lblAssignToAttribute);

        assignToAttribute = new JComboBox<>();
        GridBagConstraints gbc_assignToAttribute = new GridBagConstraints();
        gbc_assignToAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_assignToAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_assignToAttribute.gridx = 3;
        gbc_assignToAttribute.gridy = 0;
        childJob.add(assignToAttribute, gbc_assignToAttribute);

        JLabel lblDeliverFrom = new JLabel("Deliver From");
        lblDeliverFrom.setHorizontalAlignment(SwingConstants.RIGHT);
        GridBagConstraints gbc_lblDeliverFrom = new GridBagConstraints();
        gbc_lblDeliverFrom.anchor = GridBagConstraints.EAST;
        gbc_lblDeliverFrom.insets = new Insets(0, 0, 5, 5);
        gbc_lblDeliverFrom.gridx = 0;
        gbc_lblDeliverFrom.gridy = 1;
        childJob.add(lblDeliverFrom, gbc_lblDeliverFrom);

        deliverFrom = new JComboBox<>();
        GridBagConstraints gbc_deliverFrom = new GridBagConstraints();
        gbc_deliverFrom.insets = new Insets(0, 0, 5, 5);
        gbc_deliverFrom.fill = GridBagConstraints.HORIZONTAL;
        gbc_deliverFrom.gridx = 1;
        gbc_deliverFrom.gridy = 1;
        childJob.add(deliverFrom, gbc_deliverFrom);

        JLabel lblDeliverFromAttribute = new JLabel("Deliver From Attribute");
        GridBagConstraints gbc_lblDeliverFromAttribute = new GridBagConstraints();
        gbc_lblDeliverFromAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_lblDeliverFromAttribute.gridx = 2;
        gbc_lblDeliverFromAttribute.gridy = 1;
        childJob.add(lblDeliverFromAttribute, gbc_lblDeliverFromAttribute);

        deliverFromAttribute = new JComboBox<>();
        GridBagConstraints gbc_deliverFromAttribute = new GridBagConstraints();
        gbc_deliverFromAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_deliverFromAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_deliverFromAttribute.gridx = 3;
        gbc_deliverFromAttribute.gridy = 1;
        childJob.add(deliverFromAttribute, gbc_deliverFromAttribute);

        JLabel lblDeliverTo = new JLabel("Deliver To");
        lblDeliverTo.setHorizontalAlignment(SwingConstants.RIGHT);
        GridBagConstraints gbc_lblDeliverTo = new GridBagConstraints();
        gbc_lblDeliverTo.anchor = GridBagConstraints.EAST;
        gbc_lblDeliverTo.insets = new Insets(0, 0, 5, 5);
        gbc_lblDeliverTo.gridx = 0;
        gbc_lblDeliverTo.gridy = 2;
        childJob.add(lblDeliverTo, gbc_lblDeliverTo);

        deliverTo = new JComboBox<>();
        GridBagConstraints gbc_deliverTo = new GridBagConstraints();
        gbc_deliverTo.insets = new Insets(0, 0, 5, 5);
        gbc_deliverTo.fill = GridBagConstraints.HORIZONTAL;
        gbc_deliverTo.gridx = 1;
        gbc_deliverTo.gridy = 2;
        childJob.add(deliverTo, gbc_deliverTo);

        JLabel lblDeliverToAttribute = new JLabel("Deliver To Attribute");
        GridBagConstraints gbc_lblDeliverToAttribute = new GridBagConstraints();
        gbc_lblDeliverToAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_lblDeliverToAttribute.gridx = 2;
        gbc_lblDeliverToAttribute.gridy = 2;
        childJob.add(lblDeliverToAttribute, gbc_lblDeliverToAttribute);

        deliverToAttribute = new JComboBox<>();
        GridBagConstraints gbc_deliverToAttribute = new GridBagConstraints();
        gbc_deliverToAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_deliverToAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_deliverToAttribute.gridx = 3;
        gbc_deliverToAttribute.gridy = 2;
        childJob.add(deliverToAttribute, gbc_deliverToAttribute);

        JLabel lblProduct = new JLabel("Product");
        lblProduct.setHorizontalAlignment(SwingConstants.RIGHT);
        GridBagConstraints gbc_lblProduct = new GridBagConstraints();
        gbc_lblProduct.anchor = GridBagConstraints.EAST;
        gbc_lblProduct.insets = new Insets(0, 0, 5, 5);
        gbc_lblProduct.gridx = 0;
        gbc_lblProduct.gridy = 3;
        childJob.add(lblProduct, gbc_lblProduct);

        product = new JComboBox<>();
        GridBagConstraints gbc_product = new GridBagConstraints();
        gbc_product.insets = new Insets(0, 0, 5, 5);
        gbc_product.fill = GridBagConstraints.HORIZONTAL;
        gbc_product.gridx = 1;
        gbc_product.gridy = 3;
        childJob.add(product, gbc_product);

        JLabel lblProductAttribute = new JLabel("Product Attribute");
        GridBagConstraints gbc_lblProductAttribute = new GridBagConstraints();
        gbc_lblProductAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_lblProductAttribute.gridx = 2;
        gbc_lblProductAttribute.gridy = 3;
        childJob.add(lblProductAttribute, gbc_lblProductAttribute);

        productAttribute = new JComboBox<>();
        GridBagConstraints gbc_productAttribute = new GridBagConstraints();
        gbc_productAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_productAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_productAttribute.gridx = 3;
        gbc_productAttribute.gridy = 3;
        childJob.add(productAttribute, gbc_productAttribute);

        JLabel lblService = new JLabel("Service");
        lblService.setHorizontalAlignment(SwingConstants.RIGHT);
        GridBagConstraints gbc_lblService = new GridBagConstraints();
        gbc_lblService.anchor = GridBagConstraints.EAST;
        gbc_lblService.insets = new Insets(0, 0, 5, 5);
        gbc_lblService.gridx = 0;
        gbc_lblService.gridy = 4;
        childJob.add(lblService, gbc_lblService);

        service = new JComboBox<>();
        GridBagConstraints gbc_service = new GridBagConstraints();
        gbc_service.insets = new Insets(0, 0, 5, 5);
        gbc_service.fill = GridBagConstraints.HORIZONTAL;
        gbc_service.gridx = 1;
        gbc_service.gridy = 4;
        childJob.add(service, gbc_service);

        JLabel lblServiceAttribute = new JLabel("Service Attribute");
        GridBagConstraints gbc_lblServiceAttribute = new GridBagConstraints();
        gbc_lblServiceAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_lblServiceAttribute.gridx = 2;
        gbc_lblServiceAttribute.gridy = 4;
        childJob.add(lblServiceAttribute, gbc_lblServiceAttribute);

        serviceAttribute = new JComboBox<>();
        GridBagConstraints gbc_serviceAttribute = new GridBagConstraints();
        gbc_serviceAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_serviceAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_serviceAttribute.gridx = 3;
        gbc_serviceAttribute.gridy = 4;
        childJob.add(serviceAttribute, gbc_serviceAttribute);

    }

}
