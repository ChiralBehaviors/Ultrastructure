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
import javax.swing.JSpinner;
import javax.swing.JTextField;

import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.workspace.swing.WorkspaceBackedView;

/**
 * @author hhildebrand
 *
 */
public class AttributeValuePane extends WorkspaceBackedView {

    private static final long serialVersionUID = 1L;
    private AttributeValue<?> attributeValue;
    private JTextField        key;
    private JSpinner          sequenceNumber;
    private JComboBox<Unit>   unit;
    private JTextField        value;

    /**
     * Create the panel.
     */
    public AttributeValuePane() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0,
                Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0,
                Double.MIN_VALUE };
        setLayout(gridBagLayout);

        JLabel lblKey = new JLabel("Key");
        GridBagConstraints gbc_lblKey = new GridBagConstraints();
        gbc_lblKey.anchor = GridBagConstraints.EAST;
        gbc_lblKey.insets = new Insets(0, 0, 5, 5);
        gbc_lblKey.gridx = 0;
        gbc_lblKey.gridy = 0;
        add(lblKey, gbc_lblKey);

        key = new JTextField();
        GridBagConstraints gbc_key = new GridBagConstraints();
        gbc_key.insets = new Insets(0, 0, 5, 5);
        gbc_key.fill = GridBagConstraints.HORIZONTAL;
        gbc_key.gridx = 1;
        gbc_key.gridy = 0;
        add(key, gbc_key);
        key.setColumns(10);

        JLabel lblSequence = new JLabel("Sequence #");
        GridBagConstraints gbc_lblSequence = new GridBagConstraints();
        gbc_lblSequence.insets = new Insets(0, 0, 5, 5);
        gbc_lblSequence.gridx = 2;
        gbc_lblSequence.gridy = 0;
        add(lblSequence, gbc_lblSequence);

        sequenceNumber = new JSpinner();
        GridBagConstraints gbc_sequenceNumber = new GridBagConstraints();
        gbc_sequenceNumber.insets = new Insets(0, 0, 5, 0);
        gbc_sequenceNumber.gridx = 3;
        gbc_sequenceNumber.gridy = 0;
        add(sequenceNumber, gbc_sequenceNumber);

        JLabel lblUnit = new JLabel("Unit");
        GridBagConstraints gbc_lblUnit = new GridBagConstraints();
        gbc_lblUnit.anchor = GridBagConstraints.EAST;
        gbc_lblUnit.insets = new Insets(0, 0, 5, 5);
        gbc_lblUnit.gridx = 0;
        gbc_lblUnit.gridy = 1;
        add(lblUnit, gbc_lblUnit);

        unit = new JComboBox<>();
        GridBagConstraints gbc_unit = new GridBagConstraints();
        gbc_unit.insets = new Insets(0, 0, 5, 0);
        gbc_unit.gridwidth = 3;
        gbc_unit.fill = GridBagConstraints.HORIZONTAL;
        gbc_unit.gridx = 1;
        gbc_unit.gridy = 1;
        add(unit, gbc_unit);

        JLabel lblValueType = new JLabel("Value");
        GridBagConstraints gbc_lblValueType = new GridBagConstraints();
        gbc_lblValueType.anchor = GridBagConstraints.EAST;
        gbc_lblValueType.insets = new Insets(0, 0, 0, 5);
        gbc_lblValueType.gridx = 0;
        gbc_lblValueType.gridy = 2;
        add(lblValueType, gbc_lblValueType);

        value = new JTextField();
        GridBagConstraints gbc_value = new GridBagConstraints();
        gbc_value.gridwidth = 3;
        gbc_value.fill = GridBagConstraints.HORIZONTAL;
        gbc_value.gridx = 1;
        gbc_value.gridy = 2;
        add(value, gbc_value);
        value.setColumns(10);

    }

    public AttributeValue<?> getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(AttributeValue<?> attributeValue) {
        this.attributeValue = attributeValue;
    }

}
