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

package com.chiralbehaviors.workspace.swing;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author hhildebrand
 *
 */
public class JobView extends JPanel {

    private static final long    serialVersionUID = 1L;
    private AbstractProtocolView abstractProtocol;
    private JComboBox<Object>    parent;
    private JLabel               lblNewLabel;
    private JComboBox<Object>    keys;

    /**
     * Create the panel.
     */
    public JobView() {
        setLayout(new BorderLayout(0, 0));

        abstractProtocol = new AbstractProtocolView();
        add(abstractProtocol, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        add(panel, BorderLayout.NORTH);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 0, 200 };
        gbl_panel.rowHeights = new int[] { 0, 0 };
        gbl_panel.columnWeights = new double[] { Double.MIN_VALUE };
        gbl_panel.rowWeights = new double[] { 0.0, 0.0 };
        panel.setLayout(gbl_panel);

        lblNewLabel = new JLabel("Key");
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
        gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 0;
        panel.add(lblNewLabel, gbc_lblNewLabel);

        keys = new JComboBox<>();
        GridBagConstraints gbc_keys = new GridBagConstraints();
        gbc_keys.gridwidth = 3;
        gbc_keys.insets = new Insets(0, 0, 5, 0);
        gbc_keys.fill = GridBagConstraints.HORIZONTAL;
        gbc_keys.gridx = 1;
        gbc_keys.gridy = 0;
        panel.add(keys, gbc_keys);

        JLabel lblParent = new JLabel("Parent");
        GridBagConstraints gbc_lblParent = new GridBagConstraints();
        gbc_lblParent.anchor = GridBagConstraints.EAST;
        gbc_lblParent.insets = new Insets(0, 0, 5, 5);
        gbc_lblParent.gridx = 0;
        gbc_lblParent.gridy = 1;
        panel.add(lblParent, gbc_lblParent);

        parent = new JComboBox<>();
        GridBagConstraints gbc_parent = new GridBagConstraints();
        gbc_parent.gridwidth = 3;
        gbc_parent.insets = new Insets(0, 0, 5, 0);
        gbc_parent.fill = GridBagConstraints.HORIZONTAL;
        gbc_parent.gridx = 1;
        gbc_parent.gridy = 1;
        panel.add(parent, gbc_parent);

    }

}
