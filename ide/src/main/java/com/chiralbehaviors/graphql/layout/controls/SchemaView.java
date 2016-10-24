/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 *  This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.graphql.layout.controls;

import org.controlsfx.control.CheckTreeView;

import com.chiralbehaviors.graphql.layout.schema.Relation;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class SchemaView extends Control {
    private final ObjectProperty<Relation> root;

    public SchemaView() {
        this((Relation) null);
    }

    public SchemaView(Relation root) {
        this(new SimpleObjectProperty<Relation>(root));
    }

    public SchemaView(SimpleObjectProperty<Relation> root) {
        this.root = root;
        this.root.addListener((o, p, c) -> rebuild(c));
        rebuild(root.get());
    }

    public ObjectProperty<Relation> root() {
        return root;
    }

    public void setRoot(Relation root) {
        this.root.set(root);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new SchemaViewSkin(this);
    }

    private CheckBoxTreeItem<String> buildItem(Relation node) {
        final CheckBoxTreeItem<String> item = new CheckBoxTreeItem<String>(node.getLabel());
        item.setExpanded(true);
        item.setIndependent(true);
        item.setSelected(!node.isFold());
        item.selectedProperty()
            .addListener((o, p, c) -> {
                node.setFold(!c);
            });
        node.getChildren()
            .stream()
            .filter(c -> c instanceof Relation)
            .map(c -> (Relation) c)
            .forEach(c -> {
                item.getChildren()
                    .add(buildItem(c));
            });
        return item;
    }

    private CheckTreeView<String> buildSchemaTree(Relation root) {
        CheckTreeView<String> view = new CheckTreeView<>();
        view.setRoot(buildItem(root));
        return view;
    }

    private Object rebuild(Relation relation) {
        if (relation == null) {
            getChildren().clear();
        } else {
            getChildren().add(buildSchemaTree(relation));
        }
        return null;
    }
}
