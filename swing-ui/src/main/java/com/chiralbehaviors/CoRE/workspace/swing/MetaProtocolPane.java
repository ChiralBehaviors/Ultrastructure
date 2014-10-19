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

/**
 * @author hhildebrand
 *
 */
public class MetaProtocolPane extends JPanel {

    private static final long       serialVersionUID = 1L;
    private JComboBox<MetaProtocol> keys;
    private JList<Protocol>         matchingProtocols;
    private MetaProtocolView        metaProtocolView;

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

        metaProtocolView = new MetaProtocolView();
        metaProtocolView.setBorder(new TitledBorder(null, "MetaProtocol",
                                                    TitledBorder.LEADING,
                                                    TitledBorder.TOP, null,
                                                    null));
        GridBagConstraints gbc_metaProtocolView = new GridBagConstraints();
        gbc_metaProtocolView.gridheight = 2;
        gbc_metaProtocolView.gridwidth = 3;
        gbc_metaProtocolView.insets = new Insets(0, 0, 0, 5);
        gbc_metaProtocolView.fill = GridBagConstraints.BOTH;
        gbc_metaProtocolView.gridx = 0;
        gbc_metaProtocolView.gridy = 1;
        add(metaProtocolView, gbc_metaProtocolView);

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

    protected void initDataBindings() {
        BeanProperty<JComboBox<MetaProtocol>, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
        BeanProperty<MetaProtocolView, MetaProtocol> metaProtocolViewBeanProperty = BeanProperty.create("metaProtocol");
        AutoBinding<JComboBox<MetaProtocol>, Object, MetaProtocolView, MetaProtocol> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                              keys,
                                                                                                                              jComboBoxBeanProperty,
                                                                                                                              metaProtocolView,
                                                                                                                              metaProtocolViewBeanProperty);
        autoBinding.bind();
    }
}
