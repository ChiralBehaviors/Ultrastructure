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

import com.chiralbehaviors.CoRE.event.ProductSelfSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.workspace.swing.WorkspaceBackedView;

/**
 * @author hhildebrand
 *
 */
public class SelfAuthSeqView extends WorkspaceBackedView {

    private static final long                  serialVersionUID = 1L;
    private JComboBox<StatusCode>              statusToSet;
    private ProductSelfSequencingAuthorization auth;

    /**
     * Create the panel.
     */
    public SelfAuthSeqView() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        setLayout(gridBagLayout);

        JLabel lblNewLabel = new JLabel("Status To Set");
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
        gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 0;
        add(lblNewLabel, gbc_lblNewLabel);

        statusToSet = new JComboBox<>();
        GridBagConstraints gbc_statusToSet = new GridBagConstraints();
        gbc_statusToSet.fill = GridBagConstraints.HORIZONTAL;
        gbc_statusToSet.gridx = 1;
        gbc_statusToSet.gridy = 0;
        add(statusToSet, gbc_statusToSet);
        initDataBindings();

    }

    public ProductSelfSequencingAuthorization getAuth() {
        return auth;
    }

    public void setAuth(ProductSelfSequencingAuthorization auth) {
        this.auth = auth;
    }

    @SuppressWarnings("rawtypes")
    protected void initDataBindings() {
        BeanProperty<ProductSelfSequencingAuthorization, StatusCode> productSelfSequencingAuthorizationBeanProperty = BeanProperty.create("statusToSet");
        BeanProperty<JComboBox, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
        AutoBinding<ProductSelfSequencingAuthorization, StatusCode, JComboBox, Object> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
                                                                                                                                auth,
                                                                                                                                productSelfSequencingAuthorizationBeanProperty,
                                                                                                                                statusToSet,
                                                                                                                                jComboBoxBeanProperty);
        autoBinding.bind();
    }
}
