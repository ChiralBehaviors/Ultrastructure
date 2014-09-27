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
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * @author hhildebrand
 *
 */
public class ExistentialRuleformView<T extends ExistentialRuleform<?, ?>>
        extends JFrame {
    private static final long       serialVersionUID = 1L;

    private JTextField              textField;
    private JTextField              name;
    private JTextArea               notes;
    private JComboBox<Relationship> childRelationship;
    private JList<T>                children;
    private JComboBox<Relationship> parentRelationship;
    private JList<T>                parents;
    private JComboBox<String>       key;
    private JPanel                  contentPane;
    private JComboBox<Relationship> classifier;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    @SuppressWarnings("rawtypes")
                    ExistentialRuleformView frame = new ExistentialRuleformView();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public ExistentialRuleformView() {
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        BorderLayout borderLayout = (BorderLayout) getContentPane().getLayout();
        borderLayout.setVgap(1);
        borderLayout.setHgap(1);
        setBounds(100, 100, 532, 367);

        JPanel parentPanel = new JPanel();
        parentPanel.setBorder(new TitledBorder(
                                               new LineBorder(
                                                              new Color(0, 0, 0)),
                                               "Parents", TitledBorder.CENTER,
                                               TitledBorder.TOP, null, null));
        contentPane.add(parentPanel, BorderLayout.WEST);
        GridBagLayout gbl_parentPanel = new GridBagLayout();
        gbl_parentPanel.columnWidths = new int[] { 145 };
        gbl_parentPanel.rowHeights = new int[] { 0, 0, 0 };
        gbl_parentPanel.columnWeights = new double[] { 1.0 };
        gbl_parentPanel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        parentPanel.setLayout(gbl_parentPanel);

        parentRelationship = new JComboBox<>();
        parentRelationship.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectParentRelationship(e);
            }
        });
        GridBagConstraints gbc_parentRelationship = new GridBagConstraints();
        gbc_parentRelationship.insets = new Insets(0, 0, 5, 0);
        gbc_parentRelationship.fill = GridBagConstraints.HORIZONTAL;
        gbc_parentRelationship.gridx = 0;
        gbc_parentRelationship.gridy = 0;
        parentPanel.add(parentRelationship, gbc_parentRelationship);

        parents = new JList<>();

        JPopupMenu popupMenu_1 = new JPopupMenu();
        addPopup(parents, popupMenu_1);

        JMenuItem mntmNew_1 = new JMenuItem("New");
        popupMenu_1.add(mntmNew_1);

        JMenuItem mntmRemove_1 = new JMenuItem("Remove");
        popupMenu_1.add(mntmRemove_1);

        JMenuItem mntmPick_1 = new JMenuItem("Select");
        popupMenu_1.add(mntmPick_1);
        GridBagConstraints gbc_parents = new GridBagConstraints();
        gbc_parents.fill = GridBagConstraints.BOTH;
        gbc_parents.gridx = 0;
        gbc_parents.gridy = 1;
        parentPanel.add(parents, gbc_parents);

        JPanel ruleformPanel = new JPanel();
        contentPane.add(ruleformPanel, BorderLayout.CENTER);
        GridBagLayout gbl_ruleformPanel = new GridBagLayout();
        gbl_ruleformPanel.columnWidths = new int[] { 216 };
        gbl_ruleformPanel.rowHeights = new int[] { 14, 28, 16, 28, 16, 94, 0 };
        gbl_ruleformPanel.columnWeights = new double[] { 1.0 };
        gbl_ruleformPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
                1.0, Double.MIN_VALUE };
        ruleformPanel.setLayout(gbl_ruleformPanel);

        JLabel lblNewLabel_1 = new JLabel("Name");
        GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
        gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
        gbc_lblNewLabel_1.fill = GridBagConstraints.VERTICAL;
        gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
        gbc_lblNewLabel_1.gridx = 0;
        gbc_lblNewLabel_1.gridy = 0;
        ruleformPanel.add(lblNewLabel_1, gbc_lblNewLabel_1);

        name = new JTextField();
        name.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeName(e);
            }
        });
        GridBagConstraints gbc_name = new GridBagConstraints();
        gbc_name.fill = GridBagConstraints.HORIZONTAL;
        gbc_name.anchor = GridBagConstraints.NORTH;
        gbc_name.insets = new Insets(0, 0, 5, 0);
        gbc_name.gridx = 0;
        gbc_name.gridy = 1;
        ruleformPanel.add(name, gbc_name);
        name.setColumns(10);

        JLabel lblNewLabel_3 = new JLabel("Description");
        GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
        gbc_lblNewLabel_3.anchor = GridBagConstraints.NORTHWEST;
        gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 0);
        gbc_lblNewLabel_3.gridx = 0;
        gbc_lblNewLabel_3.gridy = 2;
        ruleformPanel.add(lblNewLabel_3, gbc_lblNewLabel_3);

        textField = new JTextField();
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeDescription(e);
            }
        });
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.anchor = GridBagConstraints.NORTH;
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.insets = new Insets(0, 0, 5, 0);
        gbc_textField.gridx = 0;
        gbc_textField.gridy = 3;
        ruleformPanel.add(textField, gbc_textField);
        textField.setColumns(10);

        JLabel lblNewLabel_4 = new JLabel("Notes");
        GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
        gbc_lblNewLabel_4.anchor = GridBagConstraints.NORTHWEST;
        gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 0);
        gbc_lblNewLabel_4.gridx = 0;
        gbc_lblNewLabel_4.gridy = 4;
        ruleformPanel.add(lblNewLabel_4, gbc_lblNewLabel_4);

        notes = new JTextArea();
        GridBagConstraints gbc_notes = new GridBagConstraints();
        gbc_notes.fill = GridBagConstraints.BOTH;
        gbc_notes.gridx = 0;
        gbc_notes.gridy = 5;
        ruleformPanel.add(notes, gbc_notes);

        JPanel childrenPanel = new JPanel();
        childrenPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0,
                                                                          0)),
                                                 "Children",
                                                 TitledBorder.CENTER,
                                                 TitledBorder.TOP, null,
                                                 new Color(0, 0, 0)));
        contentPane.add(childrenPanel, BorderLayout.EAST);
        GridBagLayout gbl_childrenPanel = new GridBagLayout();
        gbl_childrenPanel.columnWidths = new int[] { 145 };
        gbl_childrenPanel.rowHeights = new int[] { 0, 0, 0 };
        gbl_childrenPanel.columnWeights = new double[] { 1.0 };
        gbl_childrenPanel.rowWeights = new double[] { 0.0, 1.0,
                Double.MIN_VALUE };
        childrenPanel.setLayout(gbl_childrenPanel);

        childRelationship = new JComboBox<>();
        childRelationship.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectChildRelationship(e);
            }
        });
        GridBagConstraints gbc_childRelationship = new GridBagConstraints();
        gbc_childRelationship.insets = new Insets(0, 0, 5, 0);
        gbc_childRelationship.fill = GridBagConstraints.HORIZONTAL;
        gbc_childRelationship.gridx = 0;
        gbc_childRelationship.gridy = 0;
        childrenPanel.add(childRelationship, gbc_childRelationship);

        children = new JList<>();

        JPopupMenu popupMenu = new JPopupMenu();
        addPopup(children, popupMenu);

        JMenuItem mntmNew = new JMenuItem("New");
        popupMenu.add(mntmNew);

        JMenuItem mntmRemove = new JMenuItem("Remove");
        popupMenu.add(mntmRemove);

        JMenuItem mntmPick = new JMenuItem("Select");
        popupMenu.add(mntmPick);
        GridBagConstraints gbc_children = new GridBagConstraints();
        gbc_children.fill = GridBagConstraints.BOTH;
        gbc_children.gridx = 0;
        gbc_children.gridy = 1;
        childrenPanel.add(children, gbc_children);

        JPanel workspacePanel = new JPanel();
        contentPane.add(workspacePanel, BorderLayout.NORTH);
        workspacePanel.setLayout(new BoxLayout(workspacePanel, BoxLayout.X_AXIS));

        JLabel lblNewLabel_5 = new JLabel("Key");
        lblNewLabel_5.setAlignmentX(Component.CENTER_ALIGNMENT);
        workspacePanel.add(lblNewLabel_5);

        key = new JComboBox<>();
        key.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectFromWorkspace(e);
            }
        });
        workspacePanel.add(key);

        JButton btnNewButton = new JButton("New");
        btnNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newRuleform(e);
            }
        });
        workspacePanel.add(btnNewButton);

        JButton btnNewButton_1 = new JButton("Save");
        btnNewButton_1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveRuleform(e);
            }
        });
        workspacePanel.add(btnNewButton_1);

        JPanel attributesPanel = new JPanel();
        contentPane.add(attributesPanel, BorderLayout.SOUTH);
        GridBagLayout gbl_attributesPanel = new GridBagLayout();
        gbl_attributesPanel.columnWidths = new int[] { 261, 261, 0 };
        gbl_attributesPanel.rowHeights = new int[] { 75 };
        gbl_attributesPanel.columnWeights = new double[] { 1.0, 0.0,
                Double.MIN_VALUE };
        gbl_attributesPanel.rowWeights = new double[] { 1.0 };
        attributesPanel.setLayout(gbl_attributesPanel);

        JPanel panel = new JPanel();
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.insets = new Insets(0, 0, 0, 5);
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 0;
        attributesPanel.add(panel, gbc_panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblNewLabel = new JLabel("Classification");
        panel.add(lblNewLabel);

        classifier = new JComboBox<>();
        classifier.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectClassifier(e);
            }
        });
        panel.add(classifier);

        JList<Attribute> authorizedAttributes = new JList<>();

        JPopupMenu popupMenu_2 = new JPopupMenu();
        addPopup(authorizedAttributes, popupMenu_2);

        JMenuItem mntmNew_2 = new JMenuItem("New");
        popupMenu_2.add(mntmNew_2);

        JMenuItem mntmRemove_2 = new JMenuItem("Remove");
        popupMenu_2.add(mntmRemove_2);

        JMenuItem mntmPick_2 = new JMenuItem("Select");
        popupMenu_2.add(mntmPick_2);
        GridBagConstraints gbc_authorizedAttributes = new GridBagConstraints();
        gbc_authorizedAttributes.fill = GridBagConstraints.BOTH;
        gbc_authorizedAttributes.gridx = 1;
        gbc_authorizedAttributes.gridy = 0;
        attributesPanel.add(authorizedAttributes, gbc_authorizedAttributes);

    }

    /**
     * @param e
     */
    protected void selectClassifier(ActionEvent e) {
        // TODO Auto-generated method stub

    }

    /**
     * @param e
     */
    protected void selectParentRelationship(ActionEvent e) {
        // TODO Auto-generated method stub

    }

    /**
     * @param e
     */
    protected void changeDescription(ActionEvent e) {
        // TODO Auto-generated method stub

    }

    /**
     * @param e
     */
    protected void changeName(ActionEvent e) {
        // TODO Auto-generated method stub

    }

    /**
     * @param e
     */
    protected void saveRuleform(ActionEvent e) {
        // TODO Auto-generated method stub

    }

    /**
     * @param e
     */
    protected void selectChildRelationship(ActionEvent e) {
        // TODO Auto-generated method stub

    }

    /**
     * @param e
     * 
     */
    protected void selectFromWorkspace(ActionEvent e) {
        // TODO Auto-generated method stub

    }

    /**
     * @param e
     * 
     */
    protected void newRuleform(ActionEvent e) {
        // TODO Auto-generated method stub

    }

    private static void addPopup(Component component, final JPopupMenu popup) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showMenu(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showMenu(e);
                }
            }

            private void showMenu(MouseEvent e) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });
    }
}
