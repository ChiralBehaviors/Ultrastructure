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

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;

import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.swing.WorkspaceBackedView;

/**
 * @author hhildebrand
 *
 */
public class StatusCodeSequencingView extends WorkspaceBackedView {

    private static final long     serialVersionUID = 1L;
    private JComboBox<Product>    service;
    private JComboBox<StatusCode> parent;
    private JComboBox<StatusCode> child;
    private StatusCodeSequencing  sequencing;

    /**
     * Create the panel.
     */
    public StatusCodeSequencingView() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0,
                Double.MIN_VALUE };
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
        gbc_service.insets = new Insets(0, 0, 5, 0);
        gbc_service.fill = GridBagConstraints.HORIZONTAL;
        gbc_service.gridx = 1;
        gbc_service.gridy = 0;
        add(service, gbc_service);

        JLabel lblParent = new JLabel("Parent");
        GridBagConstraints gbc_lblParent = new GridBagConstraints();
        gbc_lblParent.anchor = GridBagConstraints.EAST;
        gbc_lblParent.insets = new Insets(0, 0, 5, 5);
        gbc_lblParent.gridx = 0;
        gbc_lblParent.gridy = 1;
        add(lblParent, gbc_lblParent);

        parent = new JComboBox<>();
        GridBagConstraints gbc_parent = new GridBagConstraints();
        gbc_parent.insets = new Insets(0, 0, 5, 0);
        gbc_parent.fill = GridBagConstraints.HORIZONTAL;
        gbc_parent.gridx = 1;
        gbc_parent.gridy = 1;
        add(parent, gbc_parent);

        JLabel lblChild = new JLabel("Child");
        GridBagConstraints gbc_lblChild = new GridBagConstraints();
        gbc_lblChild.anchor = GridBagConstraints.EAST;
        gbc_lblChild.insets = new Insets(0, 0, 0, 5);
        gbc_lblChild.gridx = 0;
        gbc_lblChild.gridy = 2;
        add(lblChild, gbc_lblChild);

        child = new JComboBox<>();
        GridBagConstraints gbc_child = new GridBagConstraints();
        gbc_child.fill = GridBagConstraints.HORIZONTAL;
        gbc_child.gridx = 1;
        gbc_child.gridy = 2;
        add(child, gbc_child);
        initDataBindings();

    }

    public StatusCodeSequencing getSequencing() {
        return sequencing;
    }

    public void setSequencing(StatusCodeSequencing sequencing) {
        this.sequencing = sequencing;
    }

    protected void initDataBindings() {
        BeanProperty<StatusCodeSequencing, Product> statusCodeSequencingBeanProperty = BeanProperty.create("service");
        BeanProperty<JComboBox<?>, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
        AutoBinding<StatusCodeSequencing, Product, JComboBox<?>, Object> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                  sequencing,
                                                                                                                  statusCodeSequencingBeanProperty,
                                                                                                                  service,
                                                                                                                  jComboBoxBeanProperty);
        autoBinding.bind();
        //
        BeanProperty<StatusCodeSequencing, StatusCode> statusCodeSequencingBeanProperty_1 = BeanProperty.create("parentCode");
        AutoBinding<StatusCodeSequencing, StatusCode, JComboBox<?>, Object> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                       sequencing,
                                                                                                                       statusCodeSequencingBeanProperty_1,
                                                                                                                       parent,
                                                                                                                       jComboBoxBeanProperty);
        autoBinding_1.bind();
        //
        BeanProperty<StatusCodeSequencing, StatusCode> statusCodeSequencingBeanProperty_2 = BeanProperty.create("childCode");
        AutoBinding<StatusCodeSequencing, StatusCode, JComboBox<?>, Object> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                       sequencing,
                                                                                                                       statusCodeSequencingBeanProperty_2,
                                                                                                                       child,
                                                                                                                       jComboBoxBeanProperty);
        autoBinding_2.bind();
    }
}
