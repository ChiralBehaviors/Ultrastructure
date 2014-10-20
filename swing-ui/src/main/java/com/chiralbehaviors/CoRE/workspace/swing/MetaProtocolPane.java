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
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;

import com.chiralbehaviors.CoRE.event.MetaProtocol;
import com.chiralbehaviors.CoRE.event.Protocol;
import com.chiralbehaviors.CoRE.swing.MetaProtocolView;
import com.chiralbehaviors.CoRE.workspace.Workspace;

/**
 * @author hhildebrand
 *
 */
public class MetaProtocolPane extends JPanel {

    private static final long       serialVersionUID = 1L;
    private JComboBox<MetaProtocol> keys;
    private JList<Protocol>         matchingProtocols;
    private MetaProtocolView        metaProtocol;
    private Workspace               workspace;

    /**
     * Create the panel.
     */
    public MetaProtocolPane() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 225, 0, 245, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0,
                Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0,
                Double.MIN_VALUE };
        setLayout(gridBagLayout);

        JLabel lblKey = new JLabel("key");
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
        gbc_btnNew.insets = new Insets(0, 0, 5, 5);
        gbc_btnNew.gridx = 2;
        gbc_btnNew.gridy = 0;
        add(btnNew, gbc_btnNew);

        metaProtocol = new MetaProtocolView();
        metaProtocol.setBorder(new TitledBorder(null, "MetaProtocol",
                                                TitledBorder.LEADING,
                                                TitledBorder.TOP, null, null));
        GridBagConstraints gbc_metaProtocol = new GridBagConstraints();
        gbc_metaProtocol.gridheight = 2;
        gbc_metaProtocol.gridwidth = 3;
        gbc_metaProtocol.insets = new Insets(0, 0, 0, 5);
        gbc_metaProtocol.fill = GridBagConstraints.BOTH;
        gbc_metaProtocol.gridx = 0;
        gbc_metaProtocol.gridy = 1;
        add(metaProtocol, gbc_metaProtocol);

        matchingProtocols = new JList<>();
        matchingProtocols.setBorder(new TitledBorder(
                                                     new EtchedBorder(
                                                                      EtchedBorder.LOWERED,
                                                                      null,
                                                                      null),
                                                     "Matching Protocols",
                                                     TitledBorder.LEADING,
                                                     TitledBorder.TOP, null,
                                                     new Color(0, 0, 0)));
        GridBagConstraints gbc_matchingProtocols = new GridBagConstraints();
        gbc_matchingProtocols.gridheight = 2;
        gbc_matchingProtocols.fill = GridBagConstraints.BOTH;
        gbc_matchingProtocols.gridx = 3;
        gbc_matchingProtocols.gridy = 1;
        add(matchingProtocols, gbc_matchingProtocols);
        initDataBindings();

    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    protected void initDataBindings() {
        BeanProperty<JComboBox<MetaProtocol>, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
        BeanProperty<MetaProtocolView, MetaProtocol> metaProtocolViewBeanProperty = BeanProperty.create("metaProtocol");
        AutoBinding<JComboBox<MetaProtocol>, Object, MetaProtocolView, MetaProtocol> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                              keys,
                                                                                                                              jComboBoxBeanProperty,
                                                                                                                              metaProtocol,
                                                                                                                              metaProtocolViewBeanProperty);
        autoBinding.bind();
        //
        BeanProperty<MetaProtocolView, Workspace> metaProtocolViewBeanProperty_1 = BeanProperty.create("workspace");
        AutoBinding<Workspace, Workspace, MetaProtocolView, Workspace> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                  workspace,
                                                                                                                  metaProtocol,
                                                                                                                  metaProtocolViewBeanProperty_1);
        autoBinding_1.bind();
    }
}
