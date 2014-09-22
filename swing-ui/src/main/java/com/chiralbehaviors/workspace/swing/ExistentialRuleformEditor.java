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
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.workspace.swing.model.WorkspaceEditor;

/**
 * @author hhildebrand
 *
 */
public class ExistentialRuleformEditor extends JFrame {

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    ExistentialRuleformEditor frame = new ExistentialRuleformEditor();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static final long                          serialVersionUID = 1L;

    private JPanel                                     contentPane;
    private ExistentialRuleform<?, ?>                  ruleform;
    @SuppressWarnings("unused")
    private WorkspaceEditor                            workspace;
    private JButton                                    updatedBy;
    private JComboBox<String>                          workspaceKey;
    private JTextArea                                  description;
    private JLabel                                     updateDate;
    private JTextField                                 name;
    @SuppressWarnings("unused")
    private Class<? extends ExistentialRuleform<?, ?>> ruleformClass;

    /**
     * Create the frame.
     */
    public ExistentialRuleformEditor() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.NORTH);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JLabel ruleformName = new JLabel("Ruleform Name");
        panel.add(ruleformName);

        workspaceKey = new JComboBox<String>();
        panel.add(workspaceKey);

        JButton newButton = new JButton("New");
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newEntry();
            }
        });
        panel.add(newButton);

        JPanel panel_1 = new JPanel();
        contentPane.add(panel_1, BorderLayout.WEST);
        GridBagLayout gbl_panel_1 = new GridBagLayout();
        gbl_panel_1.columnWidths = new int[] { 0, 0 };
        gbl_panel_1.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        gbl_panel_1.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, Double.MIN_VALUE };
        panel_1.setLayout(gbl_panel_1);

        JLabel lblName = new JLabel("Name");
        GridBagConstraints gbc_lblName = new GridBagConstraints();
        gbc_lblName.insets = new Insets(0, 0, 5, 0);
        gbc_lblName.gridx = 0;
        gbc_lblName.gridy = 0;
        panel_1.add(lblName, gbc_lblName);

        name = new JTextField();
        name.addInputMethodListener(new InputMethodListener() {
            @Override
            public void caretPositionChanged(InputMethodEvent event) {
                // do nothing
            }

            @Override
            public void inputMethodTextChanged(InputMethodEvent event) {
                setGuessKey(event);
            }
        });
        name.setText("Some Awesome Name");
        GridBagConstraints gbc_name_1 = new GridBagConstraints();
        gbc_name_1.insets = new Insets(0, 0, 5, 0);
        gbc_name_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_name_1.gridx = 0;
        gbc_name_1.gridy = 1;
        panel_1.add(name, gbc_name_1);
        name.setColumns(10);

        JLabel lblUpdatedby = new JLabel("UpdatedBy");
        GridBagConstraints gbc_lblUpdatedby = new GridBagConstraints();
        gbc_lblUpdatedby.insets = new Insets(0, 0, 5, 0);
        gbc_lblUpdatedby.gridx = 0;
        gbc_lblUpdatedby.gridy = 2;
        panel_1.add(lblUpdatedby, gbc_lblUpdatedby);

        updatedBy = new JButton("Updating Agency");
        GridBagConstraints gbc_updatedBy = new GridBagConstraints();
        gbc_updatedBy.insets = new Insets(0, 0, 5, 0);
        gbc_updatedBy.gridx = 0;
        gbc_updatedBy.gridy = 3;
        panel_1.add(updatedBy, gbc_updatedBy);

        JLabel lblLastUpdated = new JLabel("Last Updated");
        GridBagConstraints gbc_lblLastUpdated = new GridBagConstraints();
        gbc_lblLastUpdated.insets = new Insets(0, 0, 5, 0);
        gbc_lblLastUpdated.gridx = 0;
        gbc_lblLastUpdated.gridy = 4;
        panel_1.add(lblLastUpdated, gbc_lblLastUpdated);

        updateDate = new JLabel("December 7, 1942");
        GridBagConstraints gbc_updateDate = new GridBagConstraints();
        gbc_updateDate.insets = new Insets(0, 0, 5, 0);
        gbc_updateDate.gridx = 0;
        gbc_updateDate.gridy = 5;
        panel_1.add(updateDate, gbc_updateDate);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        GridBagConstraints gbc_saveButton = new GridBagConstraints();
        gbc_saveButton.gridx = 0;
        gbc_saveButton.gridy = 7;
        panel_1.add(saveButton, gbc_saveButton);

        JPanel panel_2 = new JPanel();
        contentPane.add(panel_2, BorderLayout.CENTER);
        panel_2.setLayout(new BorderLayout(0, 0));

        JLabel lblDescription = new JLabel("Description");
        lblDescription.setHorizontalAlignment(SwingConstants.CENTER);
        panel_2.add(lblDescription, BorderLayout.NORTH);

        description = new JTextArea();
        panel_2.add(description);
    }

    @SuppressWarnings("deprecation")
    public ExistentialRuleformEditor(ExistentialRuleform<?, ?> ruleform,
                                     WorkspaceEditor workspace) {
        this();
        this.ruleform = ruleform;
        this.workspace = workspace;
        name.setText(this.ruleform.getName());
        description.setText(this.ruleform.getDescription());
        updateDate.setText(this.ruleform.getUpdateDate().toGMTString());
    }

    private void newEntry() {
    }

    private void save() {
        ruleform.setName(name.getText());
        ruleform.setDescription(ruleform.getDescription());
    }

    private void setGuessKey(InputMethodEvent event) {
        // TODO Auto-generated method stub

    }

}
