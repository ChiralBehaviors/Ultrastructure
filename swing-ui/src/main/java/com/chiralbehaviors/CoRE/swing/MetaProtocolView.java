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
import javax.swing.JSpinner;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;

import com.chiralbehaviors.CoRE.event.MetaProtocol;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.swing.WorkspaceBackedView;

/**
 * @author hhildebrand
 *
 */
public class MetaProtocolView extends WorkspaceBackedView {

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
    private MetaProtocol            metaProtocol;

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
        initDataBindings();

    }

    public MetaProtocol getMetaProtocol() {
        return metaProtocol;
    }

    public void setMetaProtocol(MetaProtocol metaProtocol) {
        this.metaProtocol = metaProtocol;
    }

    @SuppressWarnings("rawtypes")
    protected void initDataBindings() {
        BeanProperty<MetaProtocol, Product> metaProtocolBeanProperty = BeanProperty.create("service");
        BeanProperty<JComboBox, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
        AutoBinding<MetaProtocol, Product, JComboBox, Object> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                       metaProtocol,
                                                                                                       metaProtocolBeanProperty,
                                                                                                       service,
                                                                                                       jComboBoxBeanProperty);
        autoBinding.bind();
        //
        BeanProperty<MetaProtocol, Integer> metaProtocolBeanProperty_1 = BeanProperty.create("sequenceNumber");
        BeanProperty<JSpinner, Object> jSpinnerBeanProperty = BeanProperty.create("value");
        AutoBinding<MetaProtocol, Integer, JSpinner, Object> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                        metaProtocol,
                                                                                                        metaProtocolBeanProperty_1,
                                                                                                        sequenceNumber,
                                                                                                        jSpinnerBeanProperty);
        autoBinding_1.bind();
        //
        BeanProperty<MetaProtocol, Boolean> metaProtocolBeanProperty_2 = BeanProperty.create("stopOnMatch");
        BeanProperty<JCheckBox, Boolean> jCheckBoxBeanProperty = BeanProperty.create("selected");
        AutoBinding<MetaProtocol, Boolean, JCheckBox, Boolean> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                          metaProtocol,
                                                                                                          metaProtocolBeanProperty_2,
                                                                                                          stopOnMatch,
                                                                                                          jCheckBoxBeanProperty);
        autoBinding_2.bind();
        //
        BeanProperty<MetaProtocol, Relationship> metaProtocolBeanProperty_3 = BeanProperty.create("serviceType");
        AutoBinding<MetaProtocol, Relationship, JComboBox, Object> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                              metaProtocol,
                                                                                                              metaProtocolBeanProperty_3,
                                                                                                              serviceType,
                                                                                                              jComboBoxBeanProperty);
        autoBinding_3.bind();
        //
        BeanProperty<MetaProtocol, Relationship> metaProtocolBeanProperty_4 = BeanProperty.create("serviceAttribute");
        AutoBinding<MetaProtocol, Relationship, JComboBox, Object> autoBinding_4 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                              metaProtocol,
                                                                                                              metaProtocolBeanProperty_4,
                                                                                                              serviceAttribute,
                                                                                                              jComboBoxBeanProperty);
        autoBinding_4.bind();
        //
        BeanProperty<MetaProtocol, Relationship> metaProtocolBeanProperty_5 = BeanProperty.create("requester");
        AutoBinding<MetaProtocol, Relationship, JComboBox, Object> autoBinding_5 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                              metaProtocol,
                                                                                                              metaProtocolBeanProperty_5,
                                                                                                              requester,
                                                                                                              jComboBoxBeanProperty);
        autoBinding_5.bind();
        //
        BeanProperty<MetaProtocol, Relationship> metaProtocolBeanProperty_6 = BeanProperty.create("requesterAttribute");
        AutoBinding<MetaProtocol, Relationship, JComboBox, Object> autoBinding_6 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                              metaProtocol,
                                                                                                              metaProtocolBeanProperty_6,
                                                                                                              requesterAttribute,
                                                                                                              jComboBoxBeanProperty);
        autoBinding_6.bind();
        //
        BeanProperty<MetaProtocol, Relationship> metaProtocolBeanProperty_7 = BeanProperty.create("assignTo");
        AutoBinding<MetaProtocol, Relationship, JComboBox, Object> autoBinding_7 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                              metaProtocol,
                                                                                                              metaProtocolBeanProperty_7,
                                                                                                              assignTo,
                                                                                                              jComboBoxBeanProperty);
        autoBinding_7.bind();
        //
        BeanProperty<MetaProtocol, Relationship> metaProtocolBeanProperty_8 = BeanProperty.create("assignToAttribute");
        AutoBinding<MetaProtocol, Relationship, JComboBox, Object> autoBinding_8 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                              metaProtocol,
                                                                                                              metaProtocolBeanProperty_8,
                                                                                                              assignToAttribute,
                                                                                                              jComboBoxBeanProperty);
        autoBinding_8.bind();
        //
        BeanProperty<MetaProtocol, Relationship> metaProtocolBeanProperty_9 = BeanProperty.create("product");
        AutoBinding<MetaProtocol, Relationship, JComboBox, Object> autoBinding_9 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                              metaProtocol,
                                                                                                              metaProtocolBeanProperty_9,
                                                                                                              product,
                                                                                                              jComboBoxBeanProperty);
        autoBinding_9.bind();
        //
        BeanProperty<MetaProtocol, Relationship> metaProtocolBeanProperty_10 = BeanProperty.create("productAttribute");
        AutoBinding<MetaProtocol, Relationship, JComboBox, Object> autoBinding_10 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                               metaProtocol,
                                                                                                               metaProtocolBeanProperty_10,
                                                                                                               productAttribute,
                                                                                                               jComboBoxBeanProperty);
        autoBinding_10.bind();
        //
        BeanProperty<MetaProtocol, Relationship> metaProtocolBeanProperty_11 = BeanProperty.create("deliverFrom");
        AutoBinding<MetaProtocol, Relationship, JComboBox, Object> autoBinding_11 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                               metaProtocol,
                                                                                                               metaProtocolBeanProperty_11,
                                                                                                               deliverFrom,
                                                                                                               jComboBoxBeanProperty);
        autoBinding_11.bind();
        //
        BeanProperty<MetaProtocol, Relationship> metaProtocolBeanProperty_12 = BeanProperty.create("deliverFromAttribute");
        AutoBinding<MetaProtocol, Relationship, JComboBox, Object> autoBinding_12 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                               metaProtocol,
                                                                                                               metaProtocolBeanProperty_12,
                                                                                                               deliverFromAttribute,
                                                                                                               jComboBoxBeanProperty);
        autoBinding_12.bind();
        //
        BeanProperty<MetaProtocol, Relationship> metaProtocolBeanProperty_13 = BeanProperty.create("deliverTo");
        BeanProperty<JComboBox, Integer> jComboBoxBeanProperty_1 = BeanProperty.create("selectedIndex");
        AutoBinding<MetaProtocol, Relationship, JComboBox, Integer> autoBinding_13 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                                metaProtocol,
                                                                                                                metaProtocolBeanProperty_13,
                                                                                                                deliverTo,
                                                                                                                jComboBoxBeanProperty_1);
        autoBinding_13.bind();
        //
        BeanProperty<MetaProtocol, Relationship> metaProtocolBeanProperty_14 = BeanProperty.create("deliverToAttribute");
        AutoBinding<MetaProtocol, Relationship, JComboBox, Integer> autoBinding_14 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                                metaProtocol,
                                                                                                                metaProtocolBeanProperty_14,
                                                                                                                deliverToAttribute,
                                                                                                                jComboBoxBeanProperty_1);
        autoBinding_14.bind();
        //
        BeanProperty<MetaProtocol, Relationship> metaProtocolBeanProperty_15 = BeanProperty.create("quantityUnit");
        AutoBinding<MetaProtocol, Relationship, JComboBox, Object> autoBinding_15 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                               metaProtocol,
                                                                                                               metaProtocolBeanProperty_15,
                                                                                                               quantityUnit,
                                                                                                               jComboBoxBeanProperty);
        autoBinding_15.bind();
    }
}
