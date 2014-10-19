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

package com.chiralbehaviors.CoRE.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;

import com.chiralbehaviors.CoRE.event.AbstractProtocol;
import com.chiralbehaviors.CoRE.workspace.swing.WorkspaceBackedView;

/**
 * @author hhildebrand
 *
 */
public class ProtocolView extends WorkspaceBackedView {
    private static final long    serialVersionUID = 1L;
    private JTabbedPane          tabbedPane;
    private AbstractProtocolView match;
    private AbstractProtocolView child;
    private JLabel               lblName;
    private JTextField           name;
    private JLabel               lblSequenceNumber;
    private JSpinner             sequenceNumber;
    private JLabel               lblNotes;
    private JTextPane            notes;
    private AbstractProtocol     abstractProtocol;

    /**
     * Create the panel.
     */
    public ProtocolView() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0,
                Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 1.0,
                Double.MIN_VALUE };
        setLayout(gridBagLayout);

        lblName = new JLabel("Name");
        GridBagConstraints gbc_lblName = new GridBagConstraints();
        gbc_lblName.anchor = GridBagConstraints.EAST;
        gbc_lblName.insets = new Insets(0, 0, 5, 5);
        gbc_lblName.gridx = 0;
        gbc_lblName.gridy = 0;
        add(lblName, gbc_lblName);

        name = new JTextField();
        GridBagConstraints gbc_name = new GridBagConstraints();
        gbc_name.insets = new Insets(0, 0, 5, 5);
        gbc_name.fill = GridBagConstraints.HORIZONTAL;
        gbc_name.gridx = 1;
        gbc_name.gridy = 0;
        add(name, gbc_name);
        name.setColumns(10);

        lblSequenceNumber = new JLabel("Sequence Number");
        GridBagConstraints gbc_lblSequenceNumber = new GridBagConstraints();
        gbc_lblSequenceNumber.insets = new Insets(0, 0, 5, 5);
        gbc_lblSequenceNumber.gridx = 2;
        gbc_lblSequenceNumber.gridy = 0;
        add(lblSequenceNumber, gbc_lblSequenceNumber);

        sequenceNumber = new JSpinner();
        GridBagConstraints gbc_sequenceNumber = new GridBagConstraints();
        gbc_sequenceNumber.anchor = GridBagConstraints.WEST;
        gbc_sequenceNumber.insets = new Insets(0, 0, 5, 0);
        gbc_sequenceNumber.gridx = 3;
        gbc_sequenceNumber.gridy = 0;
        add(sequenceNumber, gbc_sequenceNumber);

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
        gbc_tabbedPane.gridwidth = 4;
        gbc_tabbedPane.insets = new Insets(0, 0, 5, 0);
        gbc_tabbedPane.fill = GridBagConstraints.BOTH;
        gbc_tabbedPane.gridx = 0;
        gbc_tabbedPane.gridy = 1;
        add(tabbedPane, gbc_tabbedPane);

        match = new AbstractProtocolView();
        tabbedPane.addTab("Match", null, match, null);

        child = new AbstractProtocolView();
        tabbedPane.addTab("Child", null, child, null);

        lblNotes = new JLabel("Notes");
        GridBagConstraints gbc_lblNotes = new GridBagConstraints();
        gbc_lblNotes.insets = new Insets(0, 0, 0, 5);
        gbc_lblNotes.gridx = 0;
        gbc_lblNotes.gridy = 2;
        add(lblNotes, gbc_lblNotes);

        notes = new JTextPane();
        GridBagConstraints gbc_notes = new GridBagConstraints();
        gbc_notes.gridwidth = 3;
        gbc_notes.insets = new Insets(0, 0, 0, 5);
        gbc_notes.fill = GridBagConstraints.BOTH;
        gbc_notes.gridx = 1;
        gbc_notes.gridy = 2;
        add(notes, gbc_notes);
        initDataBindings();

    }

    public AbstractProtocol getAbstractProtocol() {
        return abstractProtocol;
    }

    public void setAbstractProtocol(AbstractProtocol abstractProtocol) {
        this.abstractProtocol = abstractProtocol;
    }

    protected void initDataBindings() {
        BeanProperty<AbstractProtocol, String> protocolBeanProperty = BeanProperty.create("name");
        BeanProperty<JTextField, String> jTextFieldBeanProperty = BeanProperty.create("text");
        AutoBinding<AbstractProtocol, String, JTextField, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                           abstractProtocol,
                                                                                                           protocolBeanProperty,
                                                                                                           name,
                                                                                                           jTextFieldBeanProperty);
        autoBinding.bind();
        //
        BeanProperty<AbstractProtocol, Integer> protocolBeanProperty_1 = BeanProperty.create("sequenceNumber");
        BeanProperty<JSpinner, Object> jSpinnerBeanProperty = BeanProperty.create("value");
        AutoBinding<AbstractProtocol, Integer, JSpinner, Object> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                            abstractProtocol,
                                                                                                            protocolBeanProperty_1,
                                                                                                            sequenceNumber,
                                                                                                            jSpinnerBeanProperty);
        autoBinding_1.bind();
        //
        BeanProperty<AbstractProtocol, String> protocolBeanProperty_2 = BeanProperty.create("notes");
        BeanProperty<JTextPane, String> jTextPaneBeanProperty = BeanProperty.create("text");
        AutoBinding<AbstractProtocol, String, JTextPane, String> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                            abstractProtocol,
                                                                                                            protocolBeanProperty_2,
                                                                                                            notes,
                                                                                                            jTextPaneBeanProperty);
        autoBinding_2.bind();
    }
}
