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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;

/**
 * @author hhildebrand
 *
 */
public class JobView extends JPanel {

    private static final long    serialVersionUID = 1L;
    private AbstractProtocolView abstractProtocol;
    private JComboBox<Object>    parent;
    private JLabel               lblSequenceNumber;
    private JSpinner             spinner;
    private JPanel               panel;

    /**
     * Create the panel.
     */
    public JobView() {
        setLayout(new BorderLayout(0, 0));

        abstractProtocol = new AbstractProtocolView();
        GridBagLayout gridBagLayout = (GridBagLayout) abstractProtocol.getLayout();
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 30, 0, 30 };
        add(abstractProtocol, BorderLayout.CENTER);

        panel = new JPanel();
        add(panel, BorderLayout.NORTH);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 88, 254, 42, 113, 37, 0 };
        gbl_panel.rowHeights = new int[] { 28, 0 };
        gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
                Double.MIN_VALUE };
        gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        panel.setLayout(gbl_panel);

        JLabel lblParent = new JLabel("Parent");
        GridBagConstraints gbc_lblParent = new GridBagConstraints();
        gbc_lblParent.anchor = GridBagConstraints.EAST;
        gbc_lblParent.insets = new Insets(0, 0, 0, 5);
        gbc_lblParent.gridx = 0;
        gbc_lblParent.gridy = 0;
        panel.add(lblParent, gbc_lblParent);

        parent = new JComboBox<>();
        GridBagConstraints gbc_parent = new GridBagConstraints();
        gbc_parent.fill = GridBagConstraints.HORIZONTAL;
        gbc_parent.anchor = GridBagConstraints.NORTH;
        gbc_parent.insets = new Insets(0, 0, 0, 5);
        gbc_parent.gridx = 1;
        gbc_parent.gridy = 0;
        panel.add(parent, gbc_parent);

        lblSequenceNumber = new JLabel("Sequence Number");
        GridBagConstraints gbc_lblSequenceNumber = new GridBagConstraints();
        gbc_lblSequenceNumber.anchor = GridBagConstraints.EAST;
        gbc_lblSequenceNumber.insets = new Insets(0, 0, 0, 5);
        gbc_lblSequenceNumber.gridx = 3;
        gbc_lblSequenceNumber.gridy = 0;
        panel.add(lblSequenceNumber, gbc_lblSequenceNumber);

        spinner = new JSpinner();
        GridBagConstraints gbc_spinner = new GridBagConstraints();
        gbc_spinner.anchor = GridBagConstraints.NORTHWEST;
        gbc_spinner.gridx = 4;
        gbc_spinner.gridy = 0;
        panel.add(spinner, gbc_spinner);

    }

    @SuppressWarnings("unused")
    private static void addPopup(Component component, final JPopupMenu popup) {
    }
}
