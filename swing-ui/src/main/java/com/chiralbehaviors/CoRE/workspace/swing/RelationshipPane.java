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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.swing.RelationshipView;
import com.chiralbehaviors.CoRE.workspace.Workspace;

/**
 * @author hhildebrand
 *
 */
public class RelationshipPane extends JPanel {

    private static final long                serialVersionUID = 1L;
    private JComboBox<Relationship>          parentRelationship;
    private JComboBox<Relationship>          keys;
    private JList<ExistentialRuleform<?, ?>> parents;
    private JComboBox<Relationship>          childrenRelationship;
    private JList<ExistentialRuleform<?, ?>> children;
    private RelationshipView                 relationshipView;
    private Workspace                        workspace;

    /**
     * Create the panel.
     */
    public RelationshipPane() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 37, 175, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0,
                Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 1.0,
                Double.MIN_VALUE };
        setLayout(gridBagLayout);

        JLabel lblKey = new JLabel("Key");
        GridBagConstraints gbc_lblKey = new GridBagConstraints();
        gbc_lblKey.anchor = GridBagConstraints.EAST;
        gbc_lblKey.insets = new Insets(0, 0, 5, 5);
        gbc_lblKey.gridx = 0;
        gbc_lblKey.gridy = 0;
        add(lblKey, gbc_lblKey);

        keys = new JComboBox<>();
        GridBagConstraints gbc_keys = new GridBagConstraints();
        gbc_keys.insets = new Insets(0, 0, 5, 5);
        gbc_keys.fill = GridBagConstraints.HORIZONTAL;
        gbc_keys.gridx = 1;
        gbc_keys.gridy = 0;
        add(keys, gbc_keys);

        JButton btnNew = new JButton("New");
        GridBagConstraints gbc_btnNew = new GridBagConstraints();
        gbc_btnNew.insets = new Insets(0, 0, 5, 0);
        gbc_btnNew.gridx = 2;
        gbc_btnNew.gridy = 0;
        add(btnNew, gbc_btnNew);

        JPanel parentsPanel = new JPanel();
        parentsPanel.setBorder(new TitledBorder(null, "Parents",
                                                TitledBorder.LEADING,
                                                TitledBorder.TOP, null, null));
        GridBagConstraints gbc_parentsPanel = new GridBagConstraints();
        gbc_parentsPanel.gridwidth = 2;
        gbc_parentsPanel.insets = new Insets(0, 0, 5, 5);
        gbc_parentsPanel.fill = GridBagConstraints.BOTH;
        gbc_parentsPanel.gridx = 0;
        gbc_parentsPanel.gridy = 1;
        add(parentsPanel, gbc_parentsPanel);
        GridBagLayout gbl_parentsPanel = new GridBagLayout();
        gbl_parentsPanel.columnWidths = new int[] { 0, 0, 0 };
        gbl_parentsPanel.rowHeights = new int[] { 0, 0, 0 };
        gbl_parentsPanel.columnWeights = new double[] { 1.0, 0.0,
                Double.MIN_VALUE };
        gbl_parentsPanel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        parentsPanel.setLayout(gbl_parentsPanel);

        parentRelationship = new JComboBox<>();
        GridBagConstraints gbc_parentRelationship = new GridBagConstraints();
        gbc_parentRelationship.fill = GridBagConstraints.HORIZONTAL;
        gbc_parentRelationship.insets = new Insets(0, 0, 5, 5);
        gbc_parentRelationship.gridx = 0;
        gbc_parentRelationship.gridy = 0;
        parentsPanel.add(parentRelationship, gbc_parentRelationship);

        JRadioButton rdbtnImmediate = new JRadioButton("Immediate");
        GridBagConstraints gbc_rdbtnImmediate = new GridBagConstraints();
        gbc_rdbtnImmediate.insets = new Insets(0, 0, 5, 0);
        gbc_rdbtnImmediate.gridx = 1;
        gbc_rdbtnImmediate.gridy = 0;
        parentsPanel.add(rdbtnImmediate, gbc_rdbtnImmediate);

        parents = new JList<>();
        GridBagConstraints gbc_parents = new GridBagConstraints();
        gbc_parents.gridwidth = 2;
        gbc_parents.insets = new Insets(0, 0, 0, 5);
        gbc_parents.fill = GridBagConstraints.BOTH;
        gbc_parents.gridx = 0;
        gbc_parents.gridy = 1;
        parentsPanel.add(parents, gbc_parents);

        relationshipView = new RelationshipView();
        GridBagConstraints gbc_relationshipView = new GridBagConstraints();
        gbc_relationshipView.gridheight = 2;
        gbc_relationshipView.fill = GridBagConstraints.BOTH;
        gbc_relationshipView.gridx = 2;
        gbc_relationshipView.gridy = 1;
        add(relationshipView, gbc_relationshipView);

        JPanel childrenPanel = new JPanel();
        childrenPanel.setBorder(new TitledBorder(
                                                 new EtchedBorder(
                                                                  EtchedBorder.LOWERED,
                                                                  null, null),
                                                 "Children",
                                                 TitledBorder.LEADING,
                                                 TitledBorder.TOP, null,
                                                 new Color(0, 0, 0)));
        GridBagConstraints gbc_childrenPanel = new GridBagConstraints();
        gbc_childrenPanel.gridwidth = 2;
        gbc_childrenPanel.insets = new Insets(0, 0, 0, 5);
        gbc_childrenPanel.fill = GridBagConstraints.BOTH;
        gbc_childrenPanel.gridx = 0;
        gbc_childrenPanel.gridy = 2;
        add(childrenPanel, gbc_childrenPanel);
        GridBagLayout gbl_childrenPanel = new GridBagLayout();
        gbl_childrenPanel.columnWidths = new int[] { 0, 0, 0 };
        gbl_childrenPanel.rowHeights = new int[] { 0, 0, 0 };
        gbl_childrenPanel.columnWeights = new double[] { 1.0, 0.0,
                Double.MIN_VALUE };
        gbl_childrenPanel.rowWeights = new double[] { 0.0, 1.0,
                Double.MIN_VALUE };
        childrenPanel.setLayout(gbl_childrenPanel);

        childrenRelationship = new JComboBox<Relationship>();
        GridBagConstraints gbc_childrenRelationship = new GridBagConstraints();
        gbc_childrenRelationship.fill = GridBagConstraints.HORIZONTAL;
        gbc_childrenRelationship.insets = new Insets(0, 0, 5, 5);
        gbc_childrenRelationship.gridx = 0;
        gbc_childrenRelationship.gridy = 0;
        childrenPanel.add(childrenRelationship, gbc_childrenRelationship);

        JRadioButton radioButton = new JRadioButton("Immediate");
        GridBagConstraints gbc_radioButton = new GridBagConstraints();
        gbc_radioButton.insets = new Insets(0, 0, 5, 0);
        gbc_radioButton.gridx = 1;
        gbc_radioButton.gridy = 0;
        childrenPanel.add(radioButton, gbc_radioButton);

        children = new JList<>();
        GridBagConstraints gbc_children = new GridBagConstraints();
        gbc_children.insets = new Insets(0, 0, 0, 5);
        gbc_children.fill = GridBagConstraints.BOTH;
        gbc_children.gridwidth = 2;
        gbc_children.gridx = 0;
        gbc_children.gridy = 1;
        childrenPanel.add(children, gbc_children);
        initDataBindings();

    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    protected void initDataBindings() {
        BeanProperty<JComboBox<Relationship>, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
        BeanProperty<RelationshipView, Relationship> relationshipViewBeanProperty = BeanProperty.create("relationship");
        AutoBinding<JComboBox<Relationship>, Object, RelationshipView, Relationship> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                              keys,
                                                                                                                              jComboBoxBeanProperty,
                                                                                                                              relationshipView,
                                                                                                                              relationshipViewBeanProperty);
        autoBinding.bind();
        //
        BeanProperty<RelationshipView, Workspace> relationshipViewBeanProperty_1 = BeanProperty.create("workspace");
        AutoBinding<Workspace, Workspace, RelationshipView, Workspace> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                  workspace,
                                                                                                                  relationshipView,
                                                                                                                  relationshipViewBeanProperty_1);
        autoBinding_1.bind();
    }
}
