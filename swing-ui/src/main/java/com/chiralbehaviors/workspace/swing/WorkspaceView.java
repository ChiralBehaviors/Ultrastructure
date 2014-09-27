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

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
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
import com.chiralbehaviors.CoRE.time.Interval;
import com.chiralbehaviors.CoRE.time.IntervalNetwork;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceView {

    private JFrame                                                     frame;
    private ExistentialRuleformView<Agency, AgencyNetwork>             agencies;
    private ExistentialRuleformView<Attribute, AttributeNetwork>       attributes;
    private ExistentialRuleformView<Interval, IntervalNetwork>         intervals;
    private ExistentialRuleformView<Location, LocationNetwork>         locations;
    private ExistentialRuleformView<Product, ProductNetwork>           products;
    private ExistentialRuleformView<Relationship, RelationshipNetwork> relationhips;
    private ExistentialRuleformView<StatusCode, StatusCodeNetwork>     statusCodes;
    private ExistentialRuleformView<Unit, UnitNetwork>                 units;

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
        frame.setBounds(0, 0, 1024, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

        agencies = new ExistentialRuleformView<>();
        tabbedPane.addTab("Agencies", null, agencies, null);
        tabbedPane.setEnabledAt(0, true);
        tabbedPane.setVisible(true);
        agencies.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        attributes = new ExistentialRuleformView<>();
        tabbedPane.addTab("Attributes", null, attributes, null);
        tabbedPane.setEnabledAt(1, true);

        intervals = new ExistentialRuleformView<>();
        tabbedPane.addTab("Intervals", null, intervals, null);
        tabbedPane.setEnabledAt(2, true);

        locations = new ExistentialRuleformView<>();
        tabbedPane.addTab("Locations", null, locations, null);
        tabbedPane.setEnabledAt(3, true);

        products = new ExistentialRuleformView<>();
        tabbedPane.addTab("Products", null, products, null);
        tabbedPane.setEnabledAt(4, true);

        relationhips = new ExistentialRuleformView<>();
        tabbedPane.addTab("Relationships", null, relationhips, null);
        tabbedPane.setEnabledAt(5, true);

        statusCodes = new ExistentialRuleformView<>();
        tabbedPane.addTab("Status Codes", null, statusCodes, null);
        tabbedPane.setEnabledAt(6, true);

        units = new ExistentialRuleformView<>();
        tabbedPane.addTab("Units", null, units, null);
        tabbedPane.setEnabledAt(7, true);
    }

}
