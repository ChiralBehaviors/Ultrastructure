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
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;

import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeNetwork;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.attribute.unit.UnitNetwork;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeNetwork;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.network.RelationshipNetwork;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.chiralbehaviors.CoRE.swing.ChildAuthSeqView;
import com.chiralbehaviors.CoRE.swing.ExistentialRuleformView;
import com.chiralbehaviors.CoRE.swing.JobView;
import com.chiralbehaviors.CoRE.swing.MetaProtocolView;
import com.chiralbehaviors.CoRE.swing.ParentSeqAuthView;
import com.chiralbehaviors.CoRE.swing.ProtocolView;
import com.chiralbehaviors.CoRE.swing.SelfAuthSeqView;
import com.chiralbehaviors.CoRE.swing.SiblingSeqAuthView;
import com.chiralbehaviors.CoRE.swing.StatusCodeSequencingView;
import com.chiralbehaviors.CoRE.time.Interval;
import com.chiralbehaviors.CoRE.time.IntervalNetwork;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceView {

    private JFrame                                                     frame;
    private ExistentialRuleformView<Attribute, AttributeNetwork>       attributes;
    private ExistentialRuleformView<Interval, IntervalNetwork>         intervals;
    private ExistentialRuleformView<Location, LocationNetwork>         locations;
    private ExistentialRuleformView<Product, ProductNetwork>           products;
    private ExistentialRuleformView<Relationship, RelationshipNetwork> relationhips;
    private ExistentialRuleformView<StatusCode, StatusCodeNetwork>     statusCodes;
    private ExistentialRuleformView<Unit, UnitNetwork>                 units;
    private ExistentialRuleformView<Attribute, AttributeNetwork>       agencies;
    private JTabbedPane                                                constellations;
    private JTabbedPane                                                sequencing;
    private JLabel                                                     lblWorkspace;
    private JComboBox<WorkspaceEditor>                                 workspaces;
    private JList<WorkspaceAuthorization>                              keys;
    private ProtocolView                                               protocols;
    private JobView                                                    jobs;
    private StatusCodeSequencingView                                   statusCodeSequencing;
    private ParentSeqAuthView                                          parentSequencing;
    private SiblingSeqAuthView                                         siblingSequencing;
    private ChildAuthSeqView                                           childSequencing;
    private SelfAuthSeqView                                            selfSequencing;
    private JTabbedPane                                                events;
    private MetaProtocolView                                           metaProtocols;

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
        frame.setBounds(0, 0, 1024, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        constellations = new JTabbedPane(JTabbedPane.TOP);
        frame.getContentPane().add(constellations, BorderLayout.CENTER);

        JTabbedPane existentialRuleforms = new JTabbedPane(JTabbedPane.TOP);
        constellations.addTab("Existential Ruleforms", null,
                              existentialRuleforms, null);

        agencies = new ExistentialRuleformView<Attribute, AttributeNetwork>();
        existentialRuleforms.addTab("Agencies", null, agencies, null);
        existentialRuleforms.setEnabledAt(0, true);

        attributes = new ExistentialRuleformView<>();
        existentialRuleforms.addTab("Attributes", null, attributes, null);
        existentialRuleforms.setEnabledAt(1, true);

        intervals = new ExistentialRuleformView<>();
        existentialRuleforms.addTab("Intervals", null, intervals, null);
        existentialRuleforms.setEnabledAt(2, true);

        locations = new ExistentialRuleformView<>();
        existentialRuleforms.addTab("Locations", null, locations, null);
        existentialRuleforms.setEnabledAt(3, true);

        products = new ExistentialRuleformView<>();
        existentialRuleforms.addTab("Products", null, products, null);
        existentialRuleforms.setEnabledAt(4, true);

        relationhips = new ExistentialRuleformView<>();
        existentialRuleforms.addTab("Relationships", null, relationhips, null);
        existentialRuleforms.setEnabledAt(5, true);

        statusCodes = new ExistentialRuleformView<>();
        existentialRuleforms.addTab("Status Codes", null, statusCodes, null);
        existentialRuleforms.setEnabledAt(6, true);

        units = new ExistentialRuleformView<>();
        existentialRuleforms.addTab("Units", null, units, null);
        existentialRuleforms.setEnabledAt(7, true);

        events = new JTabbedPane(JTabbedPane.TOP);
        constellations.addTab("Events", null, events, null);

        protocols = new ProtocolView();
        events.addTab("Protocols", null, protocols, null);

        metaProtocols = new MetaProtocolView();
        events.addTab("Meta Protocols", null, metaProtocols, null);
        events.setEnabledAt(1, true);

        jobs = new JobView();
        events.addTab("Jobs", null, jobs, null);

        sequencing = new JTabbedPane(JTabbedPane.TOP);
        constellations.addTab("Sequencing", null, sequencing, null);

        statusCodeSequencing = new StatusCodeSequencingView();
        sequencing.addTab("Sequencing", null, statusCodeSequencing, null);
        sequencing.setEnabledAt(0, true);

        parentSequencing = new ParentSeqAuthView();
        sequencing.addTab("Parent Sequencing", null, parentSequencing, null);
        sequencing.setEnabledAt(1, true);

        siblingSequencing = new SiblingSeqAuthView();
        sequencing.addTab("Sibling Sequencing", null, siblingSequencing, null);
        sequencing.setEnabledAt(2, true);

        childSequencing = new ChildAuthSeqView();
        sequencing.addTab("Child Sequencing", null, childSequencing, null);
        sequencing.setEnabledAt(3, true);

        selfSequencing = new SelfAuthSeqView();
        sequencing.addTab("Self Sequencing", null, selfSequencing, null);
        sequencing.setEnabledAt(3, true);

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

        JPanel keysPanel = new JPanel();
        keysPanel.setBorder(new TitledBorder(null, "Keys",
                                             TitledBorder.LEADING,
                                             TitledBorder.TOP, null, null));
        frame.getContentPane().add(keysPanel, BorderLayout.WEST);
        GridBagLayout gbl_keysPanel = new GridBagLayout();
        gbl_keysPanel.columnWidths = new int[] { 100 };
        gbl_keysPanel.rowHeights = new int[] { 0 };
        gbl_keysPanel.columnWeights = new double[] { 1.0 };
        gbl_keysPanel.rowWeights = new double[] { 1.0 };
        keysPanel.setLayout(gbl_keysPanel);

        keys = new JList<>();
        GridBagConstraints gbc_keys = new GridBagConstraints();
        gbc_keys.fill = GridBagConstraints.BOTH;
        gbc_keys.gridx = 0;
        gbc_keys.gridy = 0;
        keysPanel.add(keys, gbc_keys);
    }

}
