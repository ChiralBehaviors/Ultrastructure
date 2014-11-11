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

import com.chiralbehaviors.CoRE.event.ProductParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.swing.WorkspaceBackedView;

/**
 * @author hhildebrand
 *
 */
public class ParentSeqAuthView extends WorkspaceBackedView {

    private static final long                    serialVersionUID = 1L;
    private JLabel                               lblSequence;
    private JSpinner                             sequenceNumber;
    private JCheckBox                            setIfActiveSiblings;
    private JLabel                               lblParent;
    private JLabel                               lblNextStatus;
    private JCheckBox                            ReplaceProduct;
    private JComboBox<Product>                   parent;
    private JComboBox<StatusCode>                parentStatusToSet;
    private ProductParentSequencingAuthorization auth;

    /**
     * Create the panel.
     */
    public ParentSeqAuthView() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 1.0,
                Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0,
                Double.MIN_VALUE };
        setLayout(gridBagLayout);

        lblParent = new JLabel("Parent");
        GridBagConstraints gbc_lblParent = new GridBagConstraints();
        gbc_lblParent.anchor = GridBagConstraints.EAST;
        gbc_lblParent.insets = new Insets(0, 0, 5, 5);
        gbc_lblParent.gridx = 0;
        gbc_lblParent.gridy = 0;
        add(lblParent, gbc_lblParent);

        parent = new JComboBox<>();
        GridBagConstraints gbc_parent = new GridBagConstraints();
        gbc_parent.gridwidth = 2;
        gbc_parent.insets = new Insets(0, 0, 5, 0);
        gbc_parent.fill = GridBagConstraints.HORIZONTAL;
        gbc_parent.gridx = 1;
        gbc_parent.gridy = 0;
        add(parent, gbc_parent);

        lblNextStatus = new JLabel("Status To Set");
        GridBagConstraints gbc_lblNextStatus = new GridBagConstraints();
        gbc_lblNextStatus.anchor = GridBagConstraints.EAST;
        gbc_lblNextStatus.insets = new Insets(0, 0, 5, 5);
        gbc_lblNextStatus.gridx = 0;
        gbc_lblNextStatus.gridy = 1;
        add(lblNextStatus, gbc_lblNextStatus);

        parentStatusToSet = new JComboBox<>();
        GridBagConstraints gbc_parentStatusToSet = new GridBagConstraints();
        gbc_parentStatusToSet.gridwidth = 2;
        gbc_parentStatusToSet.insets = new Insets(0, 0, 5, 0);
        gbc_parentStatusToSet.fill = GridBagConstraints.HORIZONTAL;
        gbc_parentStatusToSet.gridx = 1;
        gbc_parentStatusToSet.gridy = 1;
        add(parentStatusToSet, gbc_parentStatusToSet);

        lblSequence = new JLabel("Sequence #");
        GridBagConstraints gbc_lblSequence = new GridBagConstraints();
        gbc_lblSequence.anchor = GridBagConstraints.EAST;
        gbc_lblSequence.insets = new Insets(0, 0, 5, 5);
        gbc_lblSequence.gridx = 0;
        gbc_lblSequence.gridy = 2;
        add(lblSequence, gbc_lblSequence);

        sequenceNumber = new JSpinner();
        GridBagConstraints gbc_sequenceNumber = new GridBagConstraints();
        gbc_sequenceNumber.anchor = GridBagConstraints.WEST;
        gbc_sequenceNumber.insets = new Insets(0, 0, 5, 5);
        gbc_sequenceNumber.gridx = 1;
        gbc_sequenceNumber.gridy = 2;
        add(sequenceNumber, gbc_sequenceNumber);

        setIfActiveSiblings = new JCheckBox("Set If Active Siblings");
        GridBagConstraints gbc_setIfActiveSiblings = new GridBagConstraints();
        gbc_setIfActiveSiblings.anchor = GridBagConstraints.WEST;
        gbc_setIfActiveSiblings.insets = new Insets(0, 0, 5, 0);
        gbc_setIfActiveSiblings.gridx = 2;
        gbc_setIfActiveSiblings.gridy = 2;
        add(setIfActiveSiblings, gbc_setIfActiveSiblings);

        ReplaceProduct = new JCheckBox("Replace Product");
        GridBagConstraints gbc_ReplaceProduct = new GridBagConstraints();
        gbc_ReplaceProduct.anchor = GridBagConstraints.WEST;
        gbc_ReplaceProduct.gridx = 2;
        gbc_ReplaceProduct.gridy = 3;
        add(ReplaceProduct, gbc_ReplaceProduct);
        initDataBindings();

    }

    public ProductParentSequencingAuthorization getAuth() {
        return auth;
    }

    public void setAuth(ProductParentSequencingAuthorization auth) {
        this.auth = auth;
    }

    @SuppressWarnings("rawtypes")
    protected void initDataBindings() {
        BeanProperty<ProductParentSequencingAuthorization, Product> productParentSequencingAuthorizationBeanProperty = BeanProperty.create("parent");
        BeanProperty<JComboBox, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
        AutoBinding<ProductParentSequencingAuthorization, Product, JComboBox, Object> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                                               auth,
                                                                                                                               productParentSequencingAuthorizationBeanProperty,
                                                                                                                               parent,
                                                                                                                               jComboBoxBeanProperty);
        autoBinding.bind();
        //
        BeanProperty<ProductParentSequencingAuthorization, StatusCode> productParentSequencingAuthorizationBeanProperty_1 = BeanProperty.create("parentStatusToSet");
        AutoBinding<ProductParentSequencingAuthorization, StatusCode, JComboBox, Object> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                                                    auth,
                                                                                                                                    productParentSequencingAuthorizationBeanProperty_1,
                                                                                                                                    parentStatusToSet,
                                                                                                                                    jComboBoxBeanProperty);
        autoBinding_1.bind();
        //
        BeanProperty<ProductParentSequencingAuthorization, Integer> productParentSequencingAuthorizationBeanProperty_2 = BeanProperty.create("sequenceNumber");
        BeanProperty<JSpinner, Object> jSpinnerBeanProperty = BeanProperty.create("value");
        AutoBinding<ProductParentSequencingAuthorization, Integer, JSpinner, Object> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                                                auth,
                                                                                                                                productParentSequencingAuthorizationBeanProperty_2,
                                                                                                                                sequenceNumber,
                                                                                                                                jSpinnerBeanProperty);
        autoBinding_2.bind();
        //
        BeanProperty<ProductParentSequencingAuthorization, Boolean> productParentSequencingAuthorizationBeanProperty_3 = BeanProperty.create("setIfActiveSiblings");
        BeanProperty<JCheckBox, Boolean> jCheckBoxBeanProperty = BeanProperty.create("selected");
        AutoBinding<ProductParentSequencingAuthorization, Boolean, JCheckBox, Boolean> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                                                  auth,
                                                                                                                                  productParentSequencingAuthorizationBeanProperty_3,
                                                                                                                                  setIfActiveSiblings,
                                                                                                                                  jCheckBoxBeanProperty);
        autoBinding_3.bind();
        //
        BeanProperty<ProductParentSequencingAuthorization, Boolean> productParentSequencingAuthorizationBeanProperty_4 = BeanProperty.create("replaceProduct");
        AutoBinding<ProductParentSequencingAuthorization, Boolean, JCheckBox, Boolean> autoBinding_4 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                                                  auth,
                                                                                                                                  productParentSequencingAuthorizationBeanProperty_4,
                                                                                                                                  ReplaceProduct,
                                                                                                                                  jCheckBoxBeanProperty);
        autoBinding_4.bind();
    }
}
