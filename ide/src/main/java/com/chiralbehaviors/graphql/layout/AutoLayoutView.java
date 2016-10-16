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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.graphql.layout.schema.Layout;
import com.chiralbehaviors.graphql.layout.schema.Relation;
import com.chiralbehaviors.graphql.layout.schema.SchemaNode;
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
    private static final Logger                  log         = LoggerFactory.getLogger(AutoLayoutView.class);

    private SimpleObjectProperty<JsonNode>       data        = new SimpleObjectProperty<>();
    private Control                              layout;
    private final SimpleObjectProperty<Relation> root        = new SimpleObjectProperty<>();
    private double                               layoutWidth = 0.0;

    public AutoLayoutView() {
        this(null);
    }

    public AutoLayoutView(Relation root) {
        this.root.set(root);
        widthProperty().addListener((o, p, c) -> resize(c.doubleValue()));
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

    public SchemaNode getRoot() {
        return root.get();
    }

    public void autoLayout() {
        layoutWidth = 0.0;
        resize(getWidth());
    }

    public void measure(JsonNode data) {
        Relation top = root.get();
        if (top == null) {
            return;
        }
        try { 
            top.measure(data, new Layout(getStylesheets()));
        } catch (Throwable e) {
            log.error("cannot measure data", e);
        }
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
        try {
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
            layout = relation.buildControl(new Layout(getStylesheets()));
            relation.setItems(layout, data.get());
            AnchorPane.setTopAnchor(layout, 0.0);
            AnchorPane.setLeftAnchor(layout, 0.0);
            AnchorPane.setRightAnchor(layout, 0.0);
            AnchorPane.setBottomAnchor(layout, 0.0);
            getChildren().add(layout);
        } catch (Throwable e) {
            log.error("Unable to resize to {}", width, e);
        }
    }

    private void setContent() {
        try {
            if (layout != null) {
                SchemaNode relation = root.get();
                if (relation != null) {
                    relation.setItems(layout, data.get());
                }
            }
        } catch (Throwable e) {
            log.error("cannot set content", e);
        }
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new AutoLayoutSkin(this);
    }
}
