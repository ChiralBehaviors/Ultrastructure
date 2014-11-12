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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;

import com.chiralbehaviors.CoRE.event.AbstractProtocol;
import com.chiralbehaviors.CoRE.event.MetaProtocol;
import com.chiralbehaviors.CoRE.event.Protocol;
import com.chiralbehaviors.CoRE.swing.ProtocolView;
import com.chiralbehaviors.CoRE.workspace.Workspace;

/**
 * @author hhildebrand
 *
 */
public class ProtocolPane extends JPanel {

    private static final long   serialVersionUID = 1L;
    private JComboBox<Protocol> keys;
    private ProtocolView        protocol;
    private JList<MetaProtocol> matchingMetaProtocols;
    private Workspace      workspace;

    /**
     * Create the panel.
     */
    public ProtocolPane() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 53, 402, 0, 218, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0,
                Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
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
        gbc_btnNew.insets = new Insets(0, 0, 5, 5);
        gbc_btnNew.gridx = 2;
        gbc_btnNew.gridy = 0;
        add(btnNew, gbc_btnNew);

        protocol = new ProtocolView();
        protocol.setBorder(new TitledBorder(null, "Protocol",
                                            TitledBorder.LEADING,
                                            TitledBorder.TOP, null, null));
        GridBagConstraints gbc_protocol = new GridBagConstraints();
        gbc_protocol.gridwidth = 3;
        gbc_protocol.insets = new Insets(0, 0, 0, 5);
        gbc_protocol.fill = GridBagConstraints.BOTH;
        gbc_protocol.gridx = 0;
        gbc_protocol.gridy = 1;
        add(protocol, gbc_protocol);

        matchingMetaProtocols = new JList<>();
        matchingMetaProtocols.setBorder(new TitledBorder(
                                                         null,
                                                         "Matching MetaProtocols",
                                                         TitledBorder.LEADING,
                                                         TitledBorder.TOP,
                                                         null, null));
        GridBagConstraints gbc_matchingMetaProtocols = new GridBagConstraints();
        gbc_matchingMetaProtocols.fill = GridBagConstraints.BOTH;
        gbc_matchingMetaProtocols.gridx = 3;
        gbc_matchingMetaProtocols.gridy = 1;
        add(matchingMetaProtocols, gbc_matchingMetaProtocols);
        initDataBindings();

    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    protected void initDataBindings() {
        BeanProperty<JComboBox<Protocol>, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
        BeanProperty<ProtocolView, AbstractProtocol> protocolViewBeanProperty = BeanProperty.create("abstractProtocol");
        AutoBinding<JComboBox<Protocol>, Object, ProtocolView, AbstractProtocol> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                          keys,
                                                                                                                          jComboBoxBeanProperty,
                                                                                                                          protocol,
                                                                                                                          protocolViewBeanProperty);
        autoBinding.bind();
        //
        BeanProperty<ProtocolView, Workspace> protocolViewBeanProperty_1 = BeanProperty.create("workspace");
        AutoBinding<Workspace, Workspace, ProtocolView, Workspace> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                             workspace,
                                                                                                                             protocol,
                                                                                                                             protocolViewBeanProperty_1);
        autoBinding_1.bind();
    }
}
