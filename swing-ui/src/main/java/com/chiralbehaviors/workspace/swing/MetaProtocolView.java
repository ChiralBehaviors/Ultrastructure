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

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * @author hhildebrand
 *
 */
public class MetaProtocolView extends JPanel {

    private static final long       serialVersionUID = 1L;
    private JComboBox<Relationship> assignToAttribute;
    private JComboBox<Relationship> serviceType;
    private JComboBox<Relationship> deliverFromAttribute;
    private JComboBox<Relationship> deliverFrom;
    private JComboBox<Relationship> deliverTo;
    @SuppressWarnings("unused")
    private AbstractProtocolView    abstractProtocol;
    private JComboBox<Relationship> serviceAttribute;
    private JComboBox<Relationship> deliverToAttribute;
    private JComboBox<Relationship> assignTo;
    private JComboBox<Object>       service;
    private JComboBox<Object>       keys;
    private JComboBox<Object>       requester;
    private JComboBox<Object>       requesterAttribute;
    private JComboBox<Object>       quantityUnit;

    /**
     * Create the panel.
     */
    public MetaProtocolView() {
        setLayout(new BorderLayout(0, 0));

        abstractProtocol = new AbstractProtocolView();

        JPanel panel = new JPanel();
        add(panel, BorderLayout.WEST);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 0, 0, 0, 0, 0 };
        gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
        gbl_panel.columnWeights = new double[] { Double.MIN_VALUE };
        gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
        panel.setLayout(gbl_panel);

        JLabel lblKey = new JLabel("Key");
        GridBagConstraints gbc_lblKey = new GridBagConstraints();
        gbc_lblKey.anchor = GridBagConstraints.EAST;
        gbc_lblKey.insets = new Insets(0, 0, 5, 5);
        gbc_lblKey.gridx = 0;
        gbc_lblKey.gridy = 0;
        panel.add(lblKey, gbc_lblKey);

        keys = new JComboBox<>();
        GridBagConstraints gbc_keys = new GridBagConstraints();
        gbc_keys.gridwidth = 3;
        gbc_keys.insets = new Insets(0, 0, 5, 5);
        gbc_keys.fill = GridBagConstraints.HORIZONTAL;
        gbc_keys.gridx = 1;
        gbc_keys.gridy = 0;
        panel.add(keys, gbc_keys);

        JLabel lblServicetype = new JLabel("ServiceType");
        GridBagConstraints gbc_lblServicetype = new GridBagConstraints();
        gbc_lblServicetype.anchor = GridBagConstraints.EAST;
        gbc_lblServicetype.insets = new Insets(0, 0, 5, 5);
        gbc_lblServicetype.gridx = 0;
        gbc_lblServicetype.gridy = 1;
        panel.add(lblServicetype, gbc_lblServicetype);

        serviceType = new JComboBox<>();
        GridBagConstraints gbc_serviceType = new GridBagConstraints();
        gbc_serviceType.insets = new Insets(0, 0, 5, 5);
        gbc_serviceType.fill = GridBagConstraints.HORIZONTAL;
        gbc_serviceType.gridx = 1;
        gbc_serviceType.gridy = 1;
        panel.add(serviceType, gbc_serviceType);

        JLabel lblQuantityUnit = new JLabel("Quantity Unit");
        GridBagConstraints gbc_lblQuantityUnit = new GridBagConstraints();
        gbc_lblQuantityUnit.insets = new Insets(0, 0, 5, 5);
        gbc_lblQuantityUnit.gridx = 2;
        gbc_lblQuantityUnit.gridy = 1;
        panel.add(lblQuantityUnit, gbc_lblQuantityUnit);

        quantityUnit = new JComboBox<>();
        GridBagConstraints gbc_quantityUnit = new GridBagConstraints();
        gbc_quantityUnit.insets = new Insets(0, 0, 5, 5);
        gbc_quantityUnit.fill = GridBagConstraints.HORIZONTAL;
        gbc_quantityUnit.gridx = 3;
        gbc_quantityUnit.gridy = 1;
        panel.add(quantityUnit, gbc_quantityUnit);

        JLabel lblAssignTo = new JLabel("Assign To");
        GridBagConstraints gbc_lblAssignTo = new GridBagConstraints();
        gbc_lblAssignTo.anchor = GridBagConstraints.EAST;
        gbc_lblAssignTo.insets = new Insets(0, 0, 5, 5);
        gbc_lblAssignTo.gridx = 0;
        gbc_lblAssignTo.gridy = 2;
        panel.add(lblAssignTo, gbc_lblAssignTo);

        assignTo = new JComboBox<>();
        GridBagConstraints gbc_assignTo = new GridBagConstraints();
        gbc_assignTo.insets = new Insets(0, 0, 5, 5);
        gbc_assignTo.fill = GridBagConstraints.HORIZONTAL;
        gbc_assignTo.gridx = 1;
        gbc_assignTo.gridy = 2;
        panel.add(assignTo, gbc_assignTo);

        JLabel lblAssignToAttribute = new JLabel("Assign To Attribute");
        GridBagConstraints gbc_lblAssignToAttribute = new GridBagConstraints();
        gbc_lblAssignToAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_lblAssignToAttribute.anchor = GridBagConstraints.EAST;
        gbc_lblAssignToAttribute.gridx = 2;
        gbc_lblAssignToAttribute.gridy = 2;
        panel.add(lblAssignToAttribute, gbc_lblAssignToAttribute);

        assignToAttribute = new JComboBox<>();
        GridBagConstraints gbc_assignToAttribute = new GridBagConstraints();
        gbc_assignToAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_assignToAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_assignToAttribute.gridx = 3;
        gbc_assignToAttribute.gridy = 2;
        panel.add(assignToAttribute, gbc_assignToAttribute);

        JLabel lblDeliverFrom = new JLabel("Deliver From");
        GridBagConstraints gbc_lblDeliverFrom = new GridBagConstraints();
        gbc_lblDeliverFrom.anchor = GridBagConstraints.EAST;
        gbc_lblDeliverFrom.insets = new Insets(0, 0, 5, 5);
        gbc_lblDeliverFrom.gridx = 0;
        gbc_lblDeliverFrom.gridy = 3;
        panel.add(lblDeliverFrom, gbc_lblDeliverFrom);

        deliverFrom = new JComboBox<>();
        GridBagConstraints gbc_deliverFrom = new GridBagConstraints();
        gbc_deliverFrom.insets = new Insets(0, 0, 5, 5);
        gbc_deliverFrom.fill = GridBagConstraints.HORIZONTAL;
        gbc_deliverFrom.gridx = 1;
        gbc_deliverFrom.gridy = 3;
        panel.add(deliverFrom, gbc_deliverFrom);

        JLabel lblDeliverFromAttribute = new JLabel("Deliver From Attribute");
        GridBagConstraints gbc_lblDeliverFromAttribute = new GridBagConstraints();
        gbc_lblDeliverFromAttribute.anchor = GridBagConstraints.EAST;
        gbc_lblDeliverFromAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_lblDeliverFromAttribute.gridx = 2;
        gbc_lblDeliverFromAttribute.gridy = 3;
        panel.add(lblDeliverFromAttribute, gbc_lblDeliverFromAttribute);

        deliverFromAttribute = new JComboBox<>();
        GridBagConstraints gbc_deliverFromAttribute = new GridBagConstraints();
        gbc_deliverFromAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_deliverFromAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_deliverFromAttribute.gridx = 3;
        gbc_deliverFromAttribute.gridy = 3;
        panel.add(deliverFromAttribute, gbc_deliverFromAttribute);

        JLabel lblDeliverTo = new JLabel("Deliver To");
        GridBagConstraints gbc_lblDeliverTo = new GridBagConstraints();
        gbc_lblDeliverTo.anchor = GridBagConstraints.EAST;
        gbc_lblDeliverTo.insets = new Insets(0, 0, 5, 5);
        gbc_lblDeliverTo.gridx = 0;
        gbc_lblDeliverTo.gridy = 4;
        panel.add(lblDeliverTo, gbc_lblDeliverTo);

        deliverTo = new JComboBox<>();
        GridBagConstraints gbc_deliverTo = new GridBagConstraints();
        gbc_deliverTo.insets = new Insets(0, 0, 5, 5);
        gbc_deliverTo.fill = GridBagConstraints.HORIZONTAL;
        gbc_deliverTo.gridx = 1;
        gbc_deliverTo.gridy = 4;
        panel.add(deliverTo, gbc_deliverTo);

        JLabel lblDeliverToAttribute = new JLabel("Deliver To Attribute");
        GridBagConstraints gbc_lblDeliverToAttribute = new GridBagConstraints();
        gbc_lblDeliverToAttribute.anchor = GridBagConstraints.EAST;
        gbc_lblDeliverToAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_lblDeliverToAttribute.gridx = 2;
        gbc_lblDeliverToAttribute.gridy = 4;
        panel.add(lblDeliverToAttribute, gbc_lblDeliverToAttribute);

        deliverToAttribute = new JComboBox<>();
        GridBagConstraints gbc_deliverToAttribute = new GridBagConstraints();
        gbc_deliverToAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_deliverToAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_deliverToAttribute.gridx = 3;
        gbc_deliverToAttribute.gridy = 4;
        panel.add(deliverToAttribute, gbc_deliverToAttribute);

        JLabel lblService = new JLabel("Service");
        GridBagConstraints gbc_lblService = new GridBagConstraints();
        gbc_lblService.anchor = GridBagConstraints.EAST;
        gbc_lblService.insets = new Insets(0, 0, 5, 5);
        gbc_lblService.gridx = 0;
        gbc_lblService.gridy = 5;
        panel.add(lblService, gbc_lblService);

        service = new JComboBox<>();
        GridBagConstraints gbc_service = new GridBagConstraints();
        gbc_service.insets = new Insets(0, 0, 5, 5);
        gbc_service.fill = GridBagConstraints.HORIZONTAL;
        gbc_service.gridx = 1;
        gbc_service.gridy = 5;
        panel.add(service, gbc_service);

        JLabel lblServiceAttribute = new JLabel("Service Attribute");
        GridBagConstraints gbc_lblServiceAttribute = new GridBagConstraints();
        gbc_lblServiceAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_lblServiceAttribute.gridx = 2;
        gbc_lblServiceAttribute.gridy = 5;
        panel.add(lblServiceAttribute, gbc_lblServiceAttribute);

        serviceAttribute = new JComboBox<>();
        GridBagConstraints gbc_serviceAttribute = new GridBagConstraints();
        gbc_serviceAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_serviceAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_serviceAttribute.gridx = 3;
        gbc_serviceAttribute.gridy = 5;
        panel.add(serviceAttribute, gbc_serviceAttribute);

        JLabel lblRequester = new JLabel("Requester");
        GridBagConstraints gbc_lblRequester = new GridBagConstraints();
        gbc_lblRequester.anchor = GridBagConstraints.EAST;
        gbc_lblRequester.insets = new Insets(0, 0, 0, 5);
        gbc_lblRequester.gridx = 0;
        gbc_lblRequester.gridy = 6;
        panel.add(lblRequester, gbc_lblRequester);

        requester = new JComboBox<>();
        GridBagConstraints gbc_requester = new GridBagConstraints();
        gbc_requester.insets = new Insets(0, 0, 0, 5);
        gbc_requester.fill = GridBagConstraints.HORIZONTAL;
        gbc_requester.gridx = 1;
        gbc_requester.gridy = 6;
        panel.add(requester, gbc_requester);

        JLabel lblRequesterAttribute = new JLabel("Requester Attribute");
        GridBagConstraints gbc_lblRequesterAttribute = new GridBagConstraints();
        gbc_lblRequesterAttribute.insets = new Insets(0, 0, 0, 5);
        gbc_lblRequesterAttribute.gridx = 2;
        gbc_lblRequesterAttribute.gridy = 6;
        panel.add(lblRequesterAttribute, gbc_lblRequesterAttribute);

        requesterAttribute = new JComboBox<>();
        GridBagConstraints gbc_requesterAttribute = new GridBagConstraints();
        gbc_requesterAttribute.insets = new Insets(0, 0, 0, 5);
        gbc_requesterAttribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_requesterAttribute.gridx = 3;
        gbc_requesterAttribute.gridy = 6;
        panel.add(requesterAttribute, gbc_requesterAttribute);

    }
}
