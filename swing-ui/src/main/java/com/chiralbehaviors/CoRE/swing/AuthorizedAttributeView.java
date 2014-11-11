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
import javax.swing.JTextPane;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.ClassifiedAttributeAuthorization;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.workspace.swing.WorkspaceBackedView;

/**
 * @author hhildebrand
 *
 */
public class AuthorizedAttributeView extends WorkspaceBackedView {

    private static final long                    serialVersionUID = 1L;
    private JComboBox<Attribute>                 attribute;
    private JComboBox<Relationship>              classification;
    private JComboBox<ExistentialRuleform<?, ?>> classifier;
    private JTextField                           defaultValue;
    private JTextPane                            notes;
    private JSpinner                             sequenceNumber;
    private ClassifiedAttributeAuthorization<?>  attributeAuth;

    /**
     * Create the panel.
     */
    public AuthorizedAttributeView() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0,
                Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0,
                Double.MIN_VALUE };
        setLayout(gridBagLayout);

        JLabel lblClassifier = new JLabel("Classifier");
        GridBagConstraints gbc_lblClassifier = new GridBagConstraints();
        gbc_lblClassifier.anchor = GridBagConstraints.EAST;
        gbc_lblClassifier.insets = new Insets(0, 0, 5, 5);
        gbc_lblClassifier.gridx = 0;
        gbc_lblClassifier.gridy = 0;
        add(lblClassifier, gbc_lblClassifier);

        classifier = new JComboBox<>();
        GridBagConstraints gbc_classifier = new GridBagConstraints();
        gbc_classifier.gridwidth = 3;
        gbc_classifier.insets = new Insets(0, 0, 5, 0);
        gbc_classifier.fill = GridBagConstraints.HORIZONTAL;
        gbc_classifier.gridx = 1;
        gbc_classifier.gridy = 0;
        add(classifier, gbc_classifier);

        JLabel lblClassification = new JLabel("Classification");
        GridBagConstraints gbc_lblClassification = new GridBagConstraints();
        gbc_lblClassification.insets = new Insets(0, 0, 5, 5);
        gbc_lblClassification.anchor = GridBagConstraints.EAST;
        gbc_lblClassification.gridx = 0;
        gbc_lblClassification.gridy = 1;
        add(lblClassification, gbc_lblClassification);

        classification = new JComboBox<>();
        GridBagConstraints gbc_classification = new GridBagConstraints();
        gbc_classification.gridwidth = 3;
        gbc_classification.insets = new Insets(0, 0, 5, 0);
        gbc_classification.fill = GridBagConstraints.HORIZONTAL;
        gbc_classification.gridx = 1;
        gbc_classification.gridy = 1;
        add(classification, gbc_classification);

        JLabel lblAttribute = new JLabel("Attribute");
        GridBagConstraints gbc_lblAttribute = new GridBagConstraints();
        gbc_lblAttribute.anchor = GridBagConstraints.EAST;
        gbc_lblAttribute.insets = new Insets(0, 0, 5, 5);
        gbc_lblAttribute.gridx = 0;
        gbc_lblAttribute.gridy = 2;
        add(lblAttribute, gbc_lblAttribute);

        attribute = new JComboBox<>();
        GridBagConstraints gbc_attribute = new GridBagConstraints();
        gbc_attribute.insets = new Insets(0, 0, 5, 5);
        gbc_attribute.fill = GridBagConstraints.HORIZONTAL;
        gbc_attribute.gridx = 1;
        gbc_attribute.gridy = 2;
        add(attribute, gbc_attribute);

        JLabel lblSequence = new JLabel("Sequence #");
        GridBagConstraints gbc_lblSequence = new GridBagConstraints();
        gbc_lblSequence.anchor = GridBagConstraints.EAST;
        gbc_lblSequence.insets = new Insets(0, 0, 5, 5);
        gbc_lblSequence.gridx = 2;
        gbc_lblSequence.gridy = 2;
        add(lblSequence, gbc_lblSequence);

        sequenceNumber = new JSpinner();
        GridBagConstraints gbc_sequenceNumber = new GridBagConstraints();
        gbc_sequenceNumber.insets = new Insets(0, 0, 5, 0);
        gbc_sequenceNumber.anchor = GridBagConstraints.WEST;
        gbc_sequenceNumber.gridx = 3;
        gbc_sequenceNumber.gridy = 2;
        add(sequenceNumber, gbc_sequenceNumber);

        JLabel lblDefaultValue = new JLabel("Default Value");
        GridBagConstraints gbc_lblDefaultValue = new GridBagConstraints();
        gbc_lblDefaultValue.anchor = GridBagConstraints.EAST;
        gbc_lblDefaultValue.insets = new Insets(0, 0, 5, 5);
        gbc_lblDefaultValue.gridx = 0;
        gbc_lblDefaultValue.gridy = 3;
        add(lblDefaultValue, gbc_lblDefaultValue);

        defaultValue = new JTextField();
        GridBagConstraints gbc_defaultValue = new GridBagConstraints();
        gbc_defaultValue.gridwidth = 3;
        gbc_defaultValue.insets = new Insets(0, 0, 5, 0);
        gbc_defaultValue.fill = GridBagConstraints.HORIZONTAL;
        gbc_defaultValue.gridx = 1;
        gbc_defaultValue.gridy = 3;
        add(defaultValue, gbc_defaultValue);
        defaultValue.setColumns(10);

        JLabel lblNotes = new JLabel("Notes");
        GridBagConstraints gbc_lblNotes = new GridBagConstraints();
        gbc_lblNotes.anchor = GridBagConstraints.EAST;
        gbc_lblNotes.insets = new Insets(0, 0, 0, 5);
        gbc_lblNotes.gridx = 0;
        gbc_lblNotes.gridy = 4;
        add(lblNotes, gbc_lblNotes);

        notes = new JTextPane();
        GridBagConstraints gbc_notes = new GridBagConstraints();
        gbc_notes.gridwidth = 3;
        gbc_notes.fill = GridBagConstraints.BOTH;
        gbc_notes.gridx = 1;
        gbc_notes.gridy = 4;
        add(notes, gbc_notes);

    }

    public ClassifiedAttributeAuthorization<?> getAttributeAuth() {
        return attributeAuth;
    }

    public void setAttributeAuth(ClassifiedAttributeAuthorization<?> attributeAuth) {
        this.attributeAuth = attributeAuth;
    }

}
