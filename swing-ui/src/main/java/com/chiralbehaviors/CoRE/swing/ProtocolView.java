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
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;

/**
 * @author hhildebrand
 *
 */
public class ProtocolView extends JPanel {
    private static final long    serialVersionUID = 1L;
    private AbstractProtocolView match;
    private AbstractProtocolView childJob;
    private JPanel               panel;
    private JLabel               lblName;
    private JTextField           textField;
    private JLabel               lblSequence;
    private JSpinner             spinner;
    private JTextPane            textPane;
    private JLabel               lblNotes;

    /**
     * Create the panel.
     */
    public ProtocolView() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 1.0, 0.0, 1.0,
                Double.MIN_VALUE };
        setLayout(gridBagLayout);

        panel = new JPanel();
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.insets = new Insets(0, 0, 5, 0);
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 0;
        add(panel, gbc_panel);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 0, 0, 0, 0, 0 };
        gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
        gbl_panel.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0,
                Double.MIN_VALUE };
        gbl_panel.rowWeights = new double[] { 0.0, 1.0, 0.0, 0.0,
                Double.MIN_VALUE };
        panel.setLayout(gbl_panel);

        lblName = new JLabel("Name");
        GridBagConstraints gbc_lblName = new GridBagConstraints();
        gbc_lblName.insets = new Insets(0, 0, 5, 5);
        gbc_lblName.anchor = GridBagConstraints.EAST;
        gbc_lblName.gridx = 0;
        gbc_lblName.gridy = 0;
        panel.add(lblName, gbc_lblName);

        textField = new JTextField();
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.insets = new Insets(0, 0, 5, 5);
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.gridx = 1;
        gbc_textField.gridy = 0;
        panel.add(textField, gbc_textField);
        textField.setColumns(10);

        lblSequence = new JLabel("Sequence #");
        GridBagConstraints gbc_lblSequence = new GridBagConstraints();
        gbc_lblSequence.insets = new Insets(0, 0, 5, 5);
        gbc_lblSequence.gridx = 2;
        gbc_lblSequence.gridy = 0;
        panel.add(lblSequence, gbc_lblSequence);

        spinner = new JSpinner();
        GridBagConstraints gbc_spinner = new GridBagConstraints();
        gbc_spinner.insets = new Insets(0, 0, 5, 0);
        gbc_spinner.gridx = 3;
        gbc_spinner.gridy = 0;
        panel.add(spinner, gbc_spinner);

        lblNotes = new JLabel("Notes");
        GridBagConstraints gbc_lblNotes = new GridBagConstraints();
        gbc_lblNotes.insets = new Insets(0, 0, 5, 5);
        gbc_lblNotes.gridx = 0;
        gbc_lblNotes.gridy = 1;
        panel.add(lblNotes, gbc_lblNotes);

        textPane = new JTextPane();
        GridBagConstraints gbc_textPane = new GridBagConstraints();
        gbc_textPane.gridheight = 3;
        gbc_textPane.gridwidth = 3;
        gbc_textPane.fill = GridBagConstraints.BOTH;
        gbc_textPane.gridx = 1;
        gbc_textPane.gridy = 1;
        panel.add(textPane, gbc_textPane);

        match = new AbstractProtocolView();
        match.setBorder(new TitledBorder(null, "Match", TitledBorder.LEADING,
                                         TitledBorder.TOP, null, null));
        GridBagConstraints gbc_match = new GridBagConstraints();
        gbc_match.fill = GridBagConstraints.HORIZONTAL;
        gbc_match.insets = new Insets(0, 0, 5, 0);
        gbc_match.gridx = 0;
        gbc_match.gridy = 1;
        add(match, gbc_match);

        childJob = new AbstractProtocolView();
        childJob.setBorder(new TitledBorder(null, "Child Job",
                                            TitledBorder.LEADING,
                                            TitledBorder.TOP, null, null));
        GridBagConstraints gbc_childJob = new GridBagConstraints();
        gbc_childJob.fill = GridBagConstraints.BOTH;
        gbc_childJob.gridx = 0;
        gbc_childJob.gridy = 2;
        add(childJob, gbc_childJob);

    }

}
