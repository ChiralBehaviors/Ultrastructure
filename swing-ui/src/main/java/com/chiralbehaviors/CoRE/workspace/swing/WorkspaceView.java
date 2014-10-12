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

/**
 * @author hhildebrand
 *
 */
public class WorkspaceView {

    private JFrame                     frame;
    private JTabbedPane                constellations;
    private JLabel                     lblWorkspace;
    private JComboBox<WorkspaceEditor> workspaces;
    private JTabbedPane                events;
    private JobPane                    jobs;
    private MetaProtocolPane           metaProtocols;
    private ProtocolPane               protocols;
    private SequencingPane             sequencing;
    private ExistentialRuleformPane    agencies;
    private ExistentialRuleformPane    attributes;
    private ExistentialRuleformPane    intervals;
    private ExistentialRuleformPane    locations;
    private ExistentialRuleformPane    products;
    private ExistentialRuleformPane    statusCodes;
    private ExistentialRuleformPane    units;
    private RelationshipPane           relationships;

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
    }

}
