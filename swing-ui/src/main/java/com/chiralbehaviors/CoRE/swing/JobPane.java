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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * @author hhildebrand
 *
 */
public class JobPane extends JPanel {

    private static final long serialVersionUID = 1L;
    private JComboBox<String> keys;
    private JobView           job;
    private JobExplorer       match;

    /**
     * Create the panel.
     */
    public JobPane() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 264, 264, 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0, 1.0, 1.0,
                1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0,
                Double.MIN_VALUE };
        setLayout(gridBagLayout);

        JLabel lblKey = new JLabel("Key");
        GridBagConstraints gbc_lblKey = new GridBagConstraints();
        gbc_lblKey.insets = new Insets(0, 0, 5, 5);
        gbc_lblKey.anchor = GridBagConstraints.EAST;
        gbc_lblKey.gridx = 1;
        gbc_lblKey.gridy = 1;
        add(lblKey, gbc_lblKey);

        keys = new JComboBox<>();
        GridBagConstraints gbc_keys = new GridBagConstraints();
        gbc_keys.insets = new Insets(0, 0, 5, 5);
        gbc_keys.fill = GridBagConstraints.HORIZONTAL;
        gbc_keys.gridx = 2;
        gbc_keys.gridy = 1;
        add(keys, gbc_keys);

        JButton btnNew = new JButton("New");
        GridBagConstraints gbc_btnNew = new GridBagConstraints();
        gbc_btnNew.insets = new Insets(0, 0, 5, 5);
        gbc_btnNew.gridx = 3;
        gbc_btnNew.gridy = 1;
        add(btnNew, gbc_btnNew);

        job = new JobView();
        job.setBorder(new TitledBorder(null, "Job", TitledBorder.LEADING,
                                       TitledBorder.TOP, null, null));
        GridBagConstraints gbc_job = new GridBagConstraints();
        gbc_job.insets = new Insets(0, 0, 5, 5);
        gbc_job.gridwidth = 2;
        gbc_job.fill = GridBagConstraints.BOTH;
        gbc_job.gridx = 2;
        gbc_job.gridy = 3;
        add(job, gbc_job);

        match = new JobExplorer();
        match.setBorder(new TitledBorder(null, "Match", TitledBorder.LEADING,
                                         TitledBorder.TOP, null, null));
        GridBagConstraints gbc_match = new GridBagConstraints();
        gbc_match.insets = new Insets(0, 0, 5, 5);
        gbc_match.fill = GridBagConstraints.BOTH;
        gbc_match.gridx = 4;
        gbc_match.gridy = 3;
        add(match, gbc_match);

    }

}
