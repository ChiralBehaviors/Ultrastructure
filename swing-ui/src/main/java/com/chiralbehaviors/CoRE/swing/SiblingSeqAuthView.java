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

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;

import com.chiralbehaviors.CoRE.event.ProductSiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.swing.WorkspaceBackedView;

/**
 * @author hhildebrand
 *
 */
public class SiblingSeqAuthView extends WorkspaceBackedView {

    private static final long                     serialVersionUID = 1L;
    private JComboBox<Product>                    nextSibling;
    private JCheckBox                             replaceProduct;
    private ProductSiblingSequencingAuthorization auth;
    private JComboBox<StatusCode>                 statusToSet;

    /**
     * Create the panel.
     */
    public SiblingSeqAuthView() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0,
                Double.MIN_VALUE };
        setLayout(gridBagLayout);

        JLabel lblNextSibling = new JLabel("Next Sibling");
        GridBagConstraints gbc_lblNextSibling = new GridBagConstraints();
        gbc_lblNextSibling.anchor = GridBagConstraints.EAST;
        gbc_lblNextSibling.insets = new Insets(0, 0, 5, 5);
        gbc_lblNextSibling.gridx = 0;
        gbc_lblNextSibling.gridy = 0;
        add(lblNextSibling, gbc_lblNextSibling);

        nextSibling = new JComboBox<>();
        GridBagConstraints gbc_nextSibling = new GridBagConstraints();
        gbc_nextSibling.insets = new Insets(0, 0, 5, 0);
        gbc_nextSibling.fill = GridBagConstraints.HORIZONTAL;
        gbc_nextSibling.gridx = 1;
        gbc_nextSibling.gridy = 0;
        add(nextSibling, gbc_nextSibling);

        JLabel lblStatusToSet = new JLabel("Status To Set");
        GridBagConstraints gbc_lblStatusToSet = new GridBagConstraints();
        gbc_lblStatusToSet.anchor = GridBagConstraints.EAST;
        gbc_lblStatusToSet.insets = new Insets(0, 0, 5, 5);
        gbc_lblStatusToSet.gridx = 0;
        gbc_lblStatusToSet.gridy = 1;
        add(lblStatusToSet, gbc_lblStatusToSet);

        statusToSet = new JComboBox<>();
        GridBagConstraints gbc_statusToSet = new GridBagConstraints();
        gbc_statusToSet.insets = new Insets(0, 0, 5, 0);
        gbc_statusToSet.fill = GridBagConstraints.HORIZONTAL;
        gbc_statusToSet.gridx = 1;
        gbc_statusToSet.gridy = 1;
        add(statusToSet, gbc_statusToSet);

        replaceProduct = new JCheckBox("Replace Product");
        GridBagConstraints gbc_replaceProduct = new GridBagConstraints();
        gbc_replaceProduct.anchor = GridBagConstraints.WEST;
        gbc_replaceProduct.gridx = 1;
        gbc_replaceProduct.gridy = 2;
        add(replaceProduct, gbc_replaceProduct);
        initDataBindings();

    }

    public ProductSiblingSequencingAuthorization getAuth() {
        return auth;
    }

    public void setAuth(ProductSiblingSequencingAuthorization auth) {
        this.auth = auth;
    }

    @SuppressWarnings("rawtypes")
    protected void initDataBindings() {
        BeanProperty<ProductSiblingSequencingAuthorization, Product> productSiblingSequencingAuthorizationBeanProperty = BeanProperty.create("nextSibling");
        BeanProperty<JComboBox, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
        AutoBinding<ProductSiblingSequencingAuthorization, Product, JComboBox, Object> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                                                auth,
                                                                                                                                productSiblingSequencingAuthorizationBeanProperty,
                                                                                                                                nextSibling,
                                                                                                                                jComboBoxBeanProperty);
        autoBinding.bind();
        //
        BeanProperty<ProductSiblingSequencingAuthorization, StatusCode> productSiblingSequencingAuthorizationBeanProperty_1 = BeanProperty.create("nextSiblingStatus");
        AutoBinding<ProductSiblingSequencingAuthorization, StatusCode, JComboBox, Object> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                                                     auth,
                                                                                                                                     productSiblingSequencingAuthorizationBeanProperty_1,
                                                                                                                                     statusToSet,
                                                                                                                                     jComboBoxBeanProperty);
        autoBinding_1.bind();
        //
        BeanProperty<ProductSiblingSequencingAuthorization, Boolean> productSiblingSequencingAuthorizationBeanProperty_2 = BeanProperty.create("replaceProduct");
        BeanProperty<JCheckBox, Boolean> jCheckBoxBeanProperty = BeanProperty.create("selected");
        AutoBinding<ProductSiblingSequencingAuthorization, Boolean, JCheckBox, Boolean> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                                                   auth,
                                                                                                                                   productSiblingSequencingAuthorizationBeanProperty_2,
                                                                                                                                   replaceProduct,
                                                                                                                                   jCheckBoxBeanProperty);
        autoBinding_2.bind();
    }
}
