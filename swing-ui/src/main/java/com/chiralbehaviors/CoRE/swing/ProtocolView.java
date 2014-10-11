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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

/**
 * @author hhildebrand
 *
 */
public class ProtocolView extends JPanel {
    private static final long    serialVersionUID = 1L;
    private JTabbedPane          tabbedPane;
    private AbstractProtocolView match;
    private AbstractProtocolView child;
    private JLabel               lblName;
    private JTextField           textField;
    private JLabel               lblSequenceNumber;
    private JSpinner             spinner;
    private JLabel               lblNotes;
    private JTextPane            textPane;

    /**
     * Create the panel.
     */
    public ProtocolView() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0,
                Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 1.0,
                Double.MIN_VALUE };
        setLayout(gridBagLayout);

        lblName = new JLabel("Name");
        GridBagConstraints gbc_lblName = new GridBagConstraints();
        gbc_lblName.anchor = GridBagConstraints.EAST;
        gbc_lblName.insets = new Insets(0, 0, 5, 5);
        gbc_lblName.gridx = 0;
        gbc_lblName.gridy = 0;
        add(lblName, gbc_lblName);

        textField = new JTextField();
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.insets = new Insets(0, 0, 5, 5);
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.gridx = 1;
        gbc_textField.gridy = 0;
        add(textField, gbc_textField);
        textField.setColumns(10);

        lblSequenceNumber = new JLabel("Sequence Number");
        GridBagConstraints gbc_lblSequenceNumber = new GridBagConstraints();
        gbc_lblSequenceNumber.insets = new Insets(0, 0, 5, 5);
        gbc_lblSequenceNumber.gridx = 2;
        gbc_lblSequenceNumber.gridy = 0;
        add(lblSequenceNumber, gbc_lblSequenceNumber);

        spinner = new JSpinner();
        GridBagConstraints gbc_spinner = new GridBagConstraints();
        gbc_spinner.anchor = GridBagConstraints.WEST;
        gbc_spinner.insets = new Insets(0, 0, 5, 0);
        gbc_spinner.gridx = 3;
        gbc_spinner.gridy = 0;
        add(spinner, gbc_spinner);

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
        gbc_tabbedPane.gridwidth = 4;
        gbc_tabbedPane.insets = new Insets(0, 0, 5, 0);
        gbc_tabbedPane.fill = GridBagConstraints.BOTH;
        gbc_tabbedPane.gridx = 0;
        gbc_tabbedPane.gridy = 1;
        add(tabbedPane, gbc_tabbedPane);

        match = new AbstractProtocolView();
        tabbedPane.addTab("Match", null, match, null);

        child = new AbstractProtocolView();
        tabbedPane.addTab("Child", null, child, null);

        lblNotes = new JLabel("Notes");
        GridBagConstraints gbc_lblNotes = new GridBagConstraints();
        gbc_lblNotes.insets = new Insets(0, 0, 0, 5);
        gbc_lblNotes.gridx = 0;
        gbc_lblNotes.gridy = 2;
        add(lblNotes, gbc_lblNotes);

        textPane = new JTextPane();
        GridBagConstraints gbc_textPane = new GridBagConstraints();
        gbc_textPane.gridwidth = 3;
        gbc_textPane.insets = new Insets(0, 0, 0, 5);
        gbc_textPane.fill = GridBagConstraints.BOTH;
        gbc_textPane.gridx = 1;
        gbc_textPane.gridy = 2;
        add(textPane, gbc_textPane);

    }

}
