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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import com.chiralbehaviors.CoRE.event.Job;
import com.chiralbehaviors.CoRE.event.MetaProtocol;
import com.chiralbehaviors.CoRE.event.Protocol;

/**
 * @author hhildebrand
 *
 */
public class JobExplorer extends JPanel {

    private static final long       serialVersionUID = 1L;
    private JComboBox<MetaProtocol> metaProtocol;
    private JComboBox<Protocol>     protocol;
    private JButton                 generate;
    private JButton                 generateAll;
    private JList<Job>              generated;

    /**
     * Create the panel.
     */
    public JobExplorer() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 30, 0, 30, 100 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 };
        setLayout(gridBagLayout);

        JLabel lblProtocol = new JLabel("Meta Protocol");
        GridBagConstraints gbc_lblProtocol = new GridBagConstraints();
        gbc_lblProtocol.insets = new Insets(0, 0, 5, 5);
        gbc_lblProtocol.anchor = GridBagConstraints.EAST;
        gbc_lblProtocol.gridx = 0;
        gbc_lblProtocol.gridy = 0;
        add(lblProtocol, gbc_lblProtocol);

        metaProtocol = new JComboBox<>();
        GridBagConstraints gbc_metaProtocol = new GridBagConstraints();
        gbc_metaProtocol.insets = new Insets(0, 0, 5, 0);
        gbc_metaProtocol.fill = GridBagConstraints.HORIZONTAL;
        gbc_metaProtocol.gridx = 1;
        gbc_metaProtocol.gridy = 0;
        add(metaProtocol, gbc_metaProtocol);

        JLabel lblProtocol_1 = new JLabel("Protocol");
        GridBagConstraints gbc_lblProtocol_1 = new GridBagConstraints();
        gbc_lblProtocol_1.anchor = GridBagConstraints.EAST;
        gbc_lblProtocol_1.insets = new Insets(0, 0, 5, 5);
        gbc_lblProtocol_1.gridx = 0;
        gbc_lblProtocol_1.gridy = 1;
        add(lblProtocol_1, gbc_lblProtocol_1);

        protocol = new JComboBox<>();
        GridBagConstraints gbc_protocol = new GridBagConstraints();
        gbc_protocol.insets = new Insets(0, 0, 5, 0);
        gbc_protocol.fill = GridBagConstraints.HORIZONTAL;
        gbc_protocol.gridx = 1;
        gbc_protocol.gridy = 1;
        add(protocol, gbc_protocol);

        generate = new JButton("Generate");
        GridBagConstraints gbc_generate = new GridBagConstraints();
        gbc_generate.insets = new Insets(0, 0, 5, 5);
        gbc_generate.gridx = 0;
        gbc_generate.gridy = 2;
        add(generate, gbc_generate);

        generateAll = new JButton("Generate All");
        GridBagConstraints gbc_generateAll = new GridBagConstraints();
        gbc_generateAll.anchor = GridBagConstraints.WEST;
        gbc_generateAll.insets = new Insets(0, 0, 5, 0);
        gbc_generateAll.gridx = 1;
        gbc_generateAll.gridy = 2;
        add(generateAll, gbc_generateAll);

        generated = new JList<>();
        generated.setBorder(new LineBorder(new Color(0, 0, 0)));
        GridBagConstraints gbc_list = new GridBagConstraints();
        gbc_list.gridwidth = 2;
        gbc_list.insets = new Insets(0, 0, 0, 5);
        gbc_list.gridheight = 4;
        gbc_list.fill = GridBagConstraints.BOTH;
        gbc_list.gridx = 0;
        gbc_list.gridy = 3;
        add(generated, gbc_list);

    }

}
