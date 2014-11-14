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

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;

import com.chiralbehaviors.CoRE.workspace.EditableWorkspace;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceView {

    private JFrame                    frame;
    private JTabbedPane               constellations;
    private JLabel                    lblWorkspace;
    private JComboBox<EditableWorkspace> workspaces;
    private JTabbedPane               events;
    private JobPane                   jobs;
    private MetaProtocolPane          metaProtocols;
    private ProtocolPane              protocols;
    private SequencingPane            sequencing;
    private ExistentialRuleformPane   agencies;
    private ExistentialRuleformPane   attributes;
    private ExistentialRuleformPane   intervals;
    private ExistentialRuleformPane   locations;
    private ExistentialRuleformPane   products;
    private ExistentialRuleformPane   statusCodes;
    private ExistentialRuleformPane   units;
    private RelationshipPane          relationships;
    private EditableWorkspace            workspace;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    WorkspaceView window = new WorkspaceView();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public WorkspaceView() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(0, 0, 1024, 698);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        constellations = new JTabbedPane(JTabbedPane.TOP);
        frame.getContentPane().add(constellations, BorderLayout.CENTER);

        JTabbedPane existentialRuleforms = new JTabbedPane(JTabbedPane.TOP);
        constellations.addTab("Existential Ruleforms", null,
                              existentialRuleforms, null);

        agencies = new ExistentialRuleformPane();
        existentialRuleforms.addTab("Agencies", null, agencies, null);

        attributes = new ExistentialRuleformPane();
        existentialRuleforms.addTab("Attributes", null, attributes, null);

        intervals = new ExistentialRuleformPane();
        existentialRuleforms.addTab("Intervals", null, intervals, null);

        locations = new ExistentialRuleformPane();
        existentialRuleforms.addTab("Locations", null, locations, null);

        products = new ExistentialRuleformPane();
        existentialRuleforms.addTab("Products", null, products, null);

        relationships = new RelationshipPane();
        existentialRuleforms.addTab("Relationships", null, relationships, null);

        statusCodes = new ExistentialRuleformPane();
        existentialRuleforms.addTab("Status Codes", null, statusCodes, null);

        units = new ExistentialRuleformPane();
        existentialRuleforms.addTab("Units", null, units, null);

        events = new JTabbedPane(JTabbedPane.TOP);
        constellations.addTab("Events", null, events, null);

        sequencing = new SequencingPane();
        events.addTab("Sequencing", null, sequencing, null);

        protocols = new ProtocolPane();
        events.addTab("Protocols", null, protocols, null);

        metaProtocols = new MetaProtocolPane();
        events.addTab("Meta Protocols", null, metaProtocols, null);

        jobs = new JobPane();
        events.addTab("Jobs", null, jobs, null);

        JPanel workspace = new JPanel();
        frame.getContentPane().add(workspace, BorderLayout.NORTH);
        GridBagLayout gbl_workspace = new GridBagLayout();
        gbl_workspace.columnWidths = new int[] { 0, 0, 0, 0 };
        gbl_workspace.rowHeights = new int[] { 0 };
        gbl_workspace.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        gbl_workspace.rowWeights = new double[] { 0.0 };
        workspace.setLayout(gbl_workspace);

        lblWorkspace = new JLabel("Workspace");
        GridBagConstraints gbc_lblWorkspace = new GridBagConstraints();
        gbc_lblWorkspace.insets = new Insets(0, 0, 0, 5);
        gbc_lblWorkspace.anchor = GridBagConstraints.EAST;
        gbc_lblWorkspace.gridx = 0;
        gbc_lblWorkspace.gridy = 0;
        workspace.add(lblWorkspace, gbc_lblWorkspace);

        workspaces = new JComboBox<>();
        GridBagConstraints gbc_workspaces = new GridBagConstraints();
        gbc_workspaces.fill = GridBagConstraints.HORIZONTAL;
        gbc_workspaces.gridx = 1;
        gbc_workspaces.gridy = 0;
        workspace.add(workspaces, gbc_workspaces);
        initDataBindings();
    }

    public EditableWorkspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(EditableWorkspace workspace) {
        this.workspace = workspace;
    }

    protected void initDataBindings() {
        BeanProperty<ExistentialRuleformPane, EditableWorkspace> existentialRuleformPaneBeanProperty = BeanProperty.create("workspace");
        AutoBinding<EditableWorkspace, EditableWorkspace, ExistentialRuleformPane, EditableWorkspace> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                                      workspace,
                                                                                                                                      agencies,
                                                                                                                                      existentialRuleformPaneBeanProperty);
        autoBinding.bind();
        //
        AutoBinding<EditableWorkspace, EditableWorkspace, ExistentialRuleformPane, EditableWorkspace> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                                        workspace,
                                                                                                                                        attributes,
                                                                                                                                        existentialRuleformPaneBeanProperty);
        autoBinding_1.bind();
        //
        AutoBinding<EditableWorkspace, EditableWorkspace, ExistentialRuleformPane, EditableWorkspace> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                                        workspace,
                                                                                                                                        intervals,
                                                                                                                                        existentialRuleformPaneBeanProperty);
        autoBinding_2.bind();
        //
        AutoBinding<EditableWorkspace, EditableWorkspace, ExistentialRuleformPane, EditableWorkspace> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                                        workspace,
                                                                                                                                        locations,
                                                                                                                                        existentialRuleformPaneBeanProperty);
        autoBinding_3.bind();
        //
        AutoBinding<EditableWorkspace, EditableWorkspace, ExistentialRuleformPane, EditableWorkspace> autoBinding_4 = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                                        workspace,
                                                                                                                                        products,
                                                                                                                                        existentialRuleformPaneBeanProperty);
        autoBinding_4.bind();
        //
        BeanProperty<RelationshipPane, EditableWorkspace> relationshipPaneBeanProperty = BeanProperty.create("workspace");
        AutoBinding<EditableWorkspace, EditableWorkspace, RelationshipPane, EditableWorkspace> autoBinding_5 = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                                 workspace,
                                                                                                                                 relationships,
                                                                                                                                 relationshipPaneBeanProperty);
        autoBinding_5.bind();
        //
        AutoBinding<EditableWorkspace, EditableWorkspace, ExistentialRuleformPane, EditableWorkspace> autoBinding_6 = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                                        workspace,
                                                                                                                                        statusCodes,
                                                                                                                                        existentialRuleformPaneBeanProperty);
        autoBinding_6.bind();
        //
        AutoBinding<EditableWorkspace, EditableWorkspace, ExistentialRuleformPane, EditableWorkspace> autoBinding_7 = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                                        workspace,
                                                                                                                                        units,
                                                                                                                                        existentialRuleformPaneBeanProperty);
        autoBinding_7.bind();
        //
        BeanProperty<SequencingPane, EditableWorkspace> sequencingPaneBeanProperty = BeanProperty.create("workspace");
        AutoBinding<EditableWorkspace, EditableWorkspace, SequencingPane, EditableWorkspace> autoBinding_8 = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                               workspace,
                                                                                                                               sequencing,
                                                                                                                               sequencingPaneBeanProperty);
        autoBinding_8.bind();
        //
        BeanProperty<ProtocolPane, EditableWorkspace> protocolPaneBeanProperty = BeanProperty.create("workspace");
        AutoBinding<EditableWorkspace, EditableWorkspace, ProtocolPane, EditableWorkspace> autoBinding_9 = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                             workspace,
                                                                                                                             protocols,
                                                                                                                             protocolPaneBeanProperty);
        autoBinding_9.bind();
        //
        BeanProperty<MetaProtocolPane, EditableWorkspace> metaProtocolPaneBeanProperty = BeanProperty.create("workspace");
        AutoBinding<EditableWorkspace, EditableWorkspace, MetaProtocolPane, EditableWorkspace> autoBinding_10 = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                                  workspace,
                                                                                                                                  metaProtocols,
                                                                                                                                  metaProtocolPaneBeanProperty);
        autoBinding_10.bind();
        //
        BeanProperty<JobPane, EditableWorkspace> jobPaneBeanProperty = BeanProperty.create("workspace");
        AutoBinding<EditableWorkspace, EditableWorkspace, JobPane, EditableWorkspace> autoBinding_11 = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                         workspace,
                                                                                                                         jobs,
                                                                                                                         jobPaneBeanProperty);
        autoBinding_11.bind();
    }
}
