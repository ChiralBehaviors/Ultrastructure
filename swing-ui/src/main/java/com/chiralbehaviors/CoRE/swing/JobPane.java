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

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * @author hhildebrand
 *
 */
public class JobPane extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Create the panel.
     */
    public JobPane() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0 };
        gridBagLayout.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
        setLayout(gridBagLayout);

        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(null, "Job", TitledBorder.LEADING,
                                           TitledBorder.TOP, null, null));
        GridBagConstraints gbc_panel_1 = new GridBagConstraints();
        gbc_panel_1.insets = new Insets(0, 0, 0, 5);
        gbc_panel_1.fill = GridBagConstraints.BOTH;
        gbc_panel_1.gridx = 0;
        gbc_panel_1.gridy = 0;
        add(panel_1, gbc_panel_1);

        JobView jobView = new JobView();
        jobView.setBorder(null);
        panel_1.add(jobView);

        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Explore", TitledBorder.LEADING,
                                         TitledBorder.TOP, null, null));
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 1;
        gbc_panel.gridy = 0;
        add(panel, gbc_panel);

        JobExplorer jobExplorer = new JobExplorer();
        GridBagLayout gridBagLayout_1 = (GridBagLayout) jobExplorer.getLayout();
        gridBagLayout_1.columnWidths = new int[] { 0, 200, 0 };
        gridBagLayout_1.rowHeights = new int[] { 30, 30, 100 };
        panel.add(jobExplorer);

    }

}
