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

package com.chiralbehaviors.CoRE.workspace.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.chiralbehaviors.CoRE.swing.ChildAuthSeqView;
import com.chiralbehaviors.CoRE.swing.ParentSeqAuthView;
import com.chiralbehaviors.CoRE.swing.SelfAuthSeqView;
import com.chiralbehaviors.CoRE.swing.SiblingSeqAuthView;

/**
 * @author hhildebrand
 *
 */
public class SequencingPane extends JPanel {

    private static final long serialVersionUID = 1L;
    private ParentSeqAuthView parentSeqAuths;
    private SelfAuthSeqView   selfAuthSeq;

    /**
     * Create the panel.
     */
    public SequencingPane() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 277, 261, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
        setLayout(gridBagLayout);

        parentSeqAuths = new ParentSeqAuthView();
        parentSeqAuths.setBorder(new TitledBorder(null, "Parent Sequencing",
                                                  TitledBorder.LEADING,
                                                  TitledBorder.TOP, null, null));
        GridBagConstraints gbc_parentSeqAuths = new GridBagConstraints();
        gbc_parentSeqAuths.insets = new Insets(0, 0, 5, 5);
        gbc_parentSeqAuths.fill = GridBagConstraints.BOTH;
        gbc_parentSeqAuths.gridx = 0;
        gbc_parentSeqAuths.gridy = 0;
        add(parentSeqAuths, gbc_parentSeqAuths);

        selfAuthSeq = new SelfAuthSeqView();
        selfAuthSeq.setBorder(new TitledBorder(null, "Self Sequencing",
                                               TitledBorder.LEADING,
                                               TitledBorder.TOP, null, null));
        GridBagConstraints gbc_selfAuthSeq = new GridBagConstraints();
        gbc_selfAuthSeq.insets = new Insets(0, 0, 5, 0);
        gbc_selfAuthSeq.fill = GridBagConstraints.BOTH;
        gbc_selfAuthSeq.gridx = 1;
        gbc_selfAuthSeq.gridy = 0;
        add(selfAuthSeq, gbc_selfAuthSeq);

        ChildAuthSeqView childAuthSeqView = new ChildAuthSeqView();
        childAuthSeqView.setBorder(new TitledBorder(null, "Child Sequencing",
                                                    TitledBorder.LEADING,
                                                    TitledBorder.TOP, null,
                                                    null));
        GridBagConstraints gbc_childAuthSeqView = new GridBagConstraints();
        gbc_childAuthSeqView.insets = new Insets(0, 0, 0, 5);
        gbc_childAuthSeqView.fill = GridBagConstraints.BOTH;
        gbc_childAuthSeqView.gridx = 0;
        gbc_childAuthSeqView.gridy = 1;
        add(childAuthSeqView, gbc_childAuthSeqView);

        SiblingSeqAuthView siblingSeqAuthView = new SiblingSeqAuthView();
        siblingSeqAuthView.setBorder(new TitledBorder(null,
                                                      "Sibling Sequencing",
                                                      TitledBorder.LEADING,
                                                      TitledBorder.TOP, null,
                                                      null));
        GridBagConstraints gbc_siblingSeqAuthView = new GridBagConstraints();
        gbc_siblingSeqAuthView.fill = GridBagConstraints.BOTH;
        gbc_siblingSeqAuthView.gridx = 1;
        gbc_siblingSeqAuthView.gridy = 1;
        add(siblingSeqAuthView, gbc_siblingSeqAuthView);

    }

}
