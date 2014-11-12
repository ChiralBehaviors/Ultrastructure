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

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;

import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.swing.ChildAuthSeqView;
import com.chiralbehaviors.CoRE.swing.ParentSeqAuthView;
import com.chiralbehaviors.CoRE.swing.SelfAuthSeqView;
import com.chiralbehaviors.CoRE.swing.SiblingSeqAuthView;
import com.chiralbehaviors.CoRE.workspace.Workspace;

/**
 * @author hhildebrand
 *
 */
public class SequencingPane extends JPanel {

    private static final long     serialVersionUID = 1L;
    private JComboBox<StatusCode> statusCode;
    private JComboBox<Product>    service;
    private JList<StatusCode>     parentStatusCodes;
    private JList<StatusCode>     childrenStatusCodes;
    private SelfAuthSeqView       selfAuthSeq;
    private ParentSeqAuthView     parentSeqAuth;
    private SiblingSeqAuthView    siblingSeqAuth;
    private ChildAuthSeqView      childAuthSeq;
    private Workspace        workspace;

    /**
     * Create the panel.
     */
    public SequencingPane() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0,
                Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 1.0, 0.0,
                0.0, Double.MIN_VALUE };
        setLayout(gridBagLayout);

        JLabel lblStatusCode = new JLabel("Status Code");
        GridBagConstraints gbc_lblStatusCode = new GridBagConstraints();
        gbc_lblStatusCode.insets = new Insets(0, 0, 5, 5);
        gbc_lblStatusCode.gridx = 0;
        gbc_lblStatusCode.gridy = 0;
        add(lblStatusCode, gbc_lblStatusCode);

        statusCode = new JComboBox<>();
        GridBagConstraints gbc_statusCode = new GridBagConstraints();
        gbc_statusCode.insets = new Insets(0, 0, 5, 5);
        gbc_statusCode.fill = GridBagConstraints.HORIZONTAL;
        gbc_statusCode.gridx = 1;
        gbc_statusCode.gridy = 0;
        add(statusCode, gbc_statusCode);

        JLabel lblService = new JLabel("Service");
        GridBagConstraints gbc_lblService = new GridBagConstraints();
        gbc_lblService.insets = new Insets(0, 0, 5, 5);
        gbc_lblService.gridx = 2;
        gbc_lblService.gridy = 0;
        add(lblService, gbc_lblService);

        service = new JComboBox<>();
        GridBagConstraints gbc_service = new GridBagConstraints();
        gbc_service.insets = new Insets(0, 0, 5, 0);
        gbc_service.fill = GridBagConstraints.HORIZONTAL;
        gbc_service.gridx = 3;
        gbc_service.gridy = 0;
        add(service, gbc_service);

        selfAuthSeq = new SelfAuthSeqView();
        selfAuthSeq.setBorder(new TitledBorder(null, "Self Sequencing",
                                               TitledBorder.LEADING,
                                               TitledBorder.TOP, null, null));
        GridBagConstraints gbc_selfAuthSeq = new GridBagConstraints();
        gbc_selfAuthSeq.gridwidth = 2;
        gbc_selfAuthSeq.gridheight = 2;
        gbc_selfAuthSeq.insets = new Insets(0, 0, 5, 0);
        gbc_selfAuthSeq.fill = GridBagConstraints.BOTH;
        gbc_selfAuthSeq.gridx = 2;
        gbc_selfAuthSeq.gridy = 1;
        add(selfAuthSeq, gbc_selfAuthSeq);

        JPanel parents = new JPanel();
        parents.setBorder(new TitledBorder(null, "Parent Status Codes",
                                           TitledBorder.LEADING,
                                           TitledBorder.TOP, null, null));
        GridBagConstraints gbc_parents = new GridBagConstraints();
        gbc_parents.gridwidth = 2;
        gbc_parents.gridheight = 3;
        gbc_parents.insets = new Insets(0, 0, 5, 5);
        gbc_parents.fill = GridBagConstraints.BOTH;
        gbc_parents.gridx = 0;
        gbc_parents.gridy = 1;
        add(parents, gbc_parents);
        GridBagLayout gbl_parents = new GridBagLayout();
        gbl_parents.columnWidths = new int[] { 0, 0 };
        gbl_parents.rowHeights = new int[] { 0, 0 };
        gbl_parents.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_parents.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
        parents.setLayout(gbl_parents);

        parentStatusCodes = new JList<>();
        GridBagConstraints gbc_parentStatusCodes = new GridBagConstraints();
        gbc_parentStatusCodes.fill = GridBagConstraints.BOTH;
        gbc_parentStatusCodes.gridx = 0;
        gbc_parentStatusCodes.gridy = 0;
        parents.add(parentStatusCodes, gbc_parentStatusCodes);

        parentSeqAuth = new ParentSeqAuthView();
        parentSeqAuth.setBorder(new TitledBorder(null, "Parent Sequencing",
                                                 TitledBorder.LEADING,
                                                 TitledBorder.TOP, null, null));
        GridBagConstraints gbc_parentSeqAuth = new GridBagConstraints();
        gbc_parentSeqAuth.gridwidth = 2;
        gbc_parentSeqAuth.insets = new Insets(0, 0, 5, 0);
        gbc_parentSeqAuth.fill = GridBagConstraints.BOTH;
        gbc_parentSeqAuth.gridx = 2;
        gbc_parentSeqAuth.gridy = 3;
        add(parentSeqAuth, gbc_parentSeqAuth);

        JPanel children = new JPanel();
        children.setBorder(new TitledBorder(null, "Child Status Codes",
                                            TitledBorder.LEADING,
                                            TitledBorder.TOP, null, null));
        GridBagConstraints gbc_children = new GridBagConstraints();
        gbc_children.gridwidth = 2;
        gbc_children.gridheight = 3;
        gbc_children.insets = new Insets(0, 0, 5, 5);
        gbc_children.fill = GridBagConstraints.BOTH;
        gbc_children.gridx = 0;
        gbc_children.gridy = 4;
        add(children, gbc_children);
        GridBagLayout gbl_children = new GridBagLayout();
        gbl_children.columnWidths = new int[] { 0, 0 };
        gbl_children.rowHeights = new int[] { 0, 0 };
        gbl_children.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_children.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
        children.setLayout(gbl_children);

        childrenStatusCodes = new JList<>();
        GridBagConstraints gbc_childrenStatusCodes = new GridBagConstraints();
        gbc_childrenStatusCodes.fill = GridBagConstraints.BOTH;
        gbc_childrenStatusCodes.gridx = 0;
        gbc_childrenStatusCodes.gridy = 0;
        children.add(childrenStatusCodes, gbc_childrenStatusCodes);

        siblingSeqAuth = new SiblingSeqAuthView();
        siblingSeqAuth.setBorder(new TitledBorder(null, "Sibling Sequencing",
                                                  TitledBorder.LEADING,
                                                  TitledBorder.TOP, null, null));
        GridBagConstraints gbc_siblingSeqAuth = new GridBagConstraints();
        gbc_siblingSeqAuth.gridwidth = 2;
        gbc_siblingSeqAuth.gridheight = 2;
        gbc_siblingSeqAuth.insets = new Insets(0, 0, 5, 0);
        gbc_siblingSeqAuth.fill = GridBagConstraints.BOTH;
        gbc_siblingSeqAuth.gridx = 2;
        gbc_siblingSeqAuth.gridy = 4;
        add(siblingSeqAuth, gbc_siblingSeqAuth);

        childAuthSeq = new ChildAuthSeqView();
        childAuthSeq.setBorder(new TitledBorder(null, "Child Sequencing",
                                                TitledBorder.LEADING,
                                                TitledBorder.TOP, null, null));
        GridBagConstraints gbc_childAuthSeq = new GridBagConstraints();
        gbc_childAuthSeq.gridwidth = 2;
        gbc_childAuthSeq.fill = GridBagConstraints.BOTH;
        gbc_childAuthSeq.gridx = 2;
        gbc_childAuthSeq.gridy = 6;
        add(childAuthSeq, gbc_childAuthSeq);
        initDataBindings();

    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    protected void initDataBindings() {
        BeanProperty<SelfAuthSeqView, Workspace> selfAuthSeqViewBeanProperty = BeanProperty.create("workspace");
        AutoBinding<Workspace, Workspace, SelfAuthSeqView, Workspace> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                              workspace,
                                                                                                                              selfAuthSeq,
                                                                                                                              selfAuthSeqViewBeanProperty);
        autoBinding.bind();
        //
        BeanProperty<ParentSeqAuthView, Workspace> parentSeqAuthViewBeanProperty = BeanProperty.create("workspace");
        AutoBinding<Workspace, Workspace, ParentSeqAuthView, Workspace> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                                  workspace,
                                                                                                                                  parentSeqAuth,
                                                                                                                                  parentSeqAuthViewBeanProperty);
        autoBinding_1.bind();
        //
        BeanProperty<SiblingSeqAuthView, Workspace> siblingSeqAuthViewBeanProperty = BeanProperty.create("workspace");
        AutoBinding<Workspace, Workspace, SiblingSeqAuthView, Workspace> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                                   workspace,
                                                                                                                                   siblingSeqAuth,
                                                                                                                                   siblingSeqAuthViewBeanProperty);
        autoBinding_2.bind();
        //
        BeanProperty<ChildAuthSeqView, Workspace> childAuthSeqViewBeanProperty = BeanProperty.create("workspace");
        AutoBinding<Workspace, Workspace, ChildAuthSeqView, Workspace> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ,
                                                                                                                                 workspace,
                                                                                                                                 childAuthSeq,
                                                                                                                                 childAuthSeqViewBeanProperty);
        autoBinding_3.bind();
    }
}
