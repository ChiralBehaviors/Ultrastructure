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

package com.chiralbehaviors.graphql.layout;

import com.chiralbehaviors.graphql.layout.schema.Relation;
import com.fasterxml.jackson.databind.JsonNode;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.AnchorPane;

/**
 * @author hhildebrand
 *
 */
public class AutoLayoutView extends Control {

    private SimpleObjectProperty<JsonNode>       data = new SimpleObjectProperty<>();
    private Control                              layout;
    private final SimpleObjectProperty<Relation> root = new SimpleObjectProperty<>();
    private double layoutWidth = 0.0;

    public AutoLayoutView() {
        this(null);
    }

    public AutoLayoutView(Relation root) {
        this.root.set(root);
        widthProperty().addListener((o, p, c) -> resize(c.floatValue()));
        data.addListener((o, p, c) -> setContent());
    }

    public Property<JsonNode> dataProperty() {
        return data;
    }

    public JsonNode getData() {
        return data.get();
    }

    public SimpleObjectProperty<Relation> root() {
        return root;
    }

    public Relation getRoot() {
        return root.get();
    }

    public void autoLayout() {
        resize(getWidth());
    }

    public void measure(JsonNode data) {
        Relation relation = root.get();
        relation.measure(data);
    }

    public Property<Relation> rootProperty() {
        return root;
    }

    public void setData(JsonNode node) {
        data.set(node);
    }

    public void setRoot(Relation rootRelationship) {
        root.set(rootRelationship);
    }

    private void resize(double width) {
        if (layoutWidth == width) {
            return; 
        }
        layoutWidth = width;
        getChildren().clear();
        Relation relation = root.get();
        if (relation == null) {
            return;
        }
        relation.autoLayout((float) width);
        layout = relation.buildControl();
        relation.setItems(layout, data.get());
        AnchorPane.setTopAnchor(layout, 0.0);
        AnchorPane.setLeftAnchor(layout, 0.0);
        AnchorPane.setRightAnchor(layout, 0.0);
        AnchorPane.setBottomAnchor(layout, 0.0);
        getChildren().add(layout);
    }

    private void setContent() {
        if (layout != null) {
            Relation relation = root.get();
            if (relation != null) {
                relation.setItems(layout, data.get());
            }
        }
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new AutoLayoutSkin(this);
    }
}
