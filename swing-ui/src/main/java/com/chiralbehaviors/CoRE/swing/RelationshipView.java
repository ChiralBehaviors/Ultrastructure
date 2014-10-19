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

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.workspace.swing.WorkspaceBackedView;

/**
 * @author hhildebrand
 *
 */
public class RelationshipView extends WorkspaceBackedView {

    private static final long       serialVersionUID = 1L;
    private Relationship            relationship;
    private JCheckBox               preferred;
    private ExistentialRuleformView ruleform;
    private JComboBox<Relationship> inverse;

    /**
     * Create the panel.
     */
    public RelationshipView() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 1.0,
                Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        setLayout(gridBagLayout);

        preferred = new JCheckBox("Preferred");
        GridBagConstraints gbc_preferred = new GridBagConstraints();
        gbc_preferred.insets = new Insets(0, 0, 5, 5);
        gbc_preferred.gridx = 0;
        gbc_preferred.gridy = 0;
        add(preferred, gbc_preferred);

        JLabel lblInverse = new JLabel("Inverse");
        GridBagConstraints gbc_lblInverse = new GridBagConstraints();
        gbc_lblInverse.anchor = GridBagConstraints.EAST;
        gbc_lblInverse.insets = new Insets(0, 0, 5, 5);
        gbc_lblInverse.gridx = 1;
        gbc_lblInverse.gridy = 0;
        add(lblInverse, gbc_lblInverse);

        inverse = new JComboBox<>();
        GridBagConstraints gbc_inverse = new GridBagConstraints();
        gbc_inverse.insets = new Insets(0, 0, 5, 0);
        gbc_inverse.fill = GridBagConstraints.HORIZONTAL;
        gbc_inverse.gridx = 2;
        gbc_inverse.gridy = 0;
        add(inverse, gbc_inverse);

        ruleform = new ExistentialRuleformView();
        GridBagConstraints gbc_ruleform = new GridBagConstraints();
        gbc_ruleform.gridwidth = 3;
        gbc_ruleform.fill = GridBagConstraints.BOTH;
        gbc_ruleform.gridx = 0;
        gbc_ruleform.gridy = 1;
        add(ruleform, gbc_ruleform);
        initDataBindings();

    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    @SuppressWarnings("rawtypes")
    protected void initDataBindings() {
        BeanProperty<Relationship, Boolean> relationshipBeanProperty = BeanProperty.create("preferred");
        BeanProperty<JCheckBox, Boolean> jCheckBoxBeanProperty = BeanProperty.create("selected");
        AutoBinding<Relationship, Boolean, JCheckBox, Boolean> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                        relationship,
                                                                                                        relationshipBeanProperty,
                                                                                                        preferred,
                                                                                                        jCheckBoxBeanProperty);
        autoBinding.bind();
        //
        BeanProperty<Relationship, Relationship> relationshipBeanProperty_1 = BeanProperty.create("inverse");
        BeanProperty<JComboBox, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
        AutoBinding<Relationship, Relationship, JComboBox, Object> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                              relationship,
                                                                                                              relationshipBeanProperty_1,
                                                                                                              inverse,
                                                                                                              jComboBoxBeanProperty);
        autoBinding_1.bind();
        //
        BeanProperty<ExistentialRuleformView, ExistentialRuleform<?, ?>> existentialRuleformViewBeanProperty = BeanProperty.create("ruleform");
        AutoBinding<Relationship, Relationship, ExistentialRuleformView, ExistentialRuleform<?, ?>> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                                               relationship,
                                                                                                                                               ruleform,
                                                                                                                                               existentialRuleformViewBeanProperty);
        autoBinding_2.bind();
    }
}
