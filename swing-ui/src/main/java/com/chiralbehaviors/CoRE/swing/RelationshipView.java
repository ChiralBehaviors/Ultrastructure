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

import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * @author hhildebrand
 *
 */
public class RelationshipView extends JPanel {

    private static final long serialVersionUID = 1L;

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

        JCheckBox chckbxPreferred = new JCheckBox("Preferred");
        GridBagConstraints gbc_chckbxPreferred = new GridBagConstraints();
        gbc_chckbxPreferred.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxPreferred.gridx = 0;
        gbc_chckbxPreferred.gridy = 0;
        add(chckbxPreferred, gbc_chckbxPreferred);

        JLabel lblInverse = new JLabel("Inverse");
        GridBagConstraints gbc_lblInverse = new GridBagConstraints();
        gbc_lblInverse.anchor = GridBagConstraints.EAST;
        gbc_lblInverse.insets = new Insets(0, 0, 5, 5);
        gbc_lblInverse.gridx = 1;
        gbc_lblInverse.gridy = 0;
        add(lblInverse, gbc_lblInverse);

        JComboBox<Relationship> comboBox = new JComboBox<>();
        GridBagConstraints gbc_comboBox = new GridBagConstraints();
        gbc_comboBox.insets = new Insets(0, 0, 5, 0);
        gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox.gridx = 2;
        gbc_comboBox.gridy = 0;
        add(comboBox, gbc_comboBox);

        ExistentialRuleformView existentialRuleformView = new ExistentialRuleformView();
        GridBagConstraints gbc_existentialRuleformView = new GridBagConstraints();
        gbc_existentialRuleformView.gridwidth = 3;
        gbc_existentialRuleformView.fill = GridBagConstraints.BOTH;
        gbc_existentialRuleformView.gridx = 0;
        gbc_existentialRuleformView.gridy = 1;
        add(existentialRuleformView, gbc_existentialRuleformView);

    }

}
