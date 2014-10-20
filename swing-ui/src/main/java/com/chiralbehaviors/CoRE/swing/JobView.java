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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTextPane;

import com.chiralbehaviors.CoRE.event.Job;

/**
 * @author hhildebrand
 *
 */
public class JobView extends JPanel {

    private static final long    serialVersionUID = 1L;
    private JSpinner             sequenceNumber;
    private JComboBox<Job>       parent;
    private AbstractProtocolView abstractProtocol;
    private JLabel               lblNotes;
    private JTextPane            textPane;

    /**
     * Create the panel.
     */
    public JobView() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0,
                Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0,
                Double.MIN_VALUE };
        setLayout(gridBagLayout);

        JLabel lblParent = new JLabel("Parent");
        GridBagConstraints gbc_lblParent = new GridBagConstraints();
        gbc_lblParent.anchor = GridBagConstraints.EAST;
        gbc_lblParent.insets = new Insets(0, 0, 5, 5);
        gbc_lblParent.gridx = 0;
        gbc_lblParent.gridy = 0;
        add(lblParent, gbc_lblParent);

        parent = new JComboBox<>();
        GridBagConstraints gbc_parent = new GridBagConstraints();
        gbc_parent.insets = new Insets(0, 0, 5, 5);
        gbc_parent.fill = GridBagConstraints.HORIZONTAL;
        gbc_parent.gridx = 1;
        gbc_parent.gridy = 0;
        add(parent, gbc_parent);

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

        abstractProtocol = new AbstractProtocolView();
        GridBagConstraints gbc_abstractProtocol = new GridBagConstraints();
        gbc_abstractProtocol.insets = new Insets(0, 0, 5, 0);
        gbc_abstractProtocol.gridwidth = 4;
        gbc_abstractProtocol.fill = GridBagConstraints.BOTH;
        gbc_abstractProtocol.gridx = 0;
        gbc_abstractProtocol.gridy = 1;
        add(abstractProtocol, gbc_abstractProtocol);

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

    @SuppressWarnings("unused")
    private static void addPopup(Component component, final JPopupMenu popup) {
    }
}
