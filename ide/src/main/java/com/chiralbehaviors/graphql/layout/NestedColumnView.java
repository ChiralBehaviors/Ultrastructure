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

import java.util.Stack;

import com.chiralbehaviors.graphql.layout.schema.SchemaNode;
import com.fasterxml.jackson.databind.JsonNode;

import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.util.Pair;

public class NestedColumnView extends Control {
    public static class Nested {
        public final ListView<JsonNode> control;
        public final double             height;
        public final int                cardinality;

        public Nested(int cardinality, ListView<JsonNode> control,
                      double height) {
            this.cardinality = cardinality;
            this.control = control;
            this.height = height;
        }

        @Override
        public String toString() {
            return String.format("Nested [cardinality=%s, height=%s]", cardinality,
                                 height);
        }
    }

    private Stack<Nested>             nested = new Stack<>();
    private Pair<SchemaNode, Control> top;

    public Stack<Nested> getNested() {
        return nested;
    }

    public void manifest(SchemaNode node, Control control, int cardinality) {
        top = new Pair<>(node, control);
        this.getChildren()
            .add(top.getValue());
    }

    public void push(int cardinality, ListView<JsonNode> control, double height) {
        nested.push(new Nested(cardinality, control, height));
    }

    public void setItem(JsonNode item) {
        JsonNode extracted = top.getKey()
                                .extractFrom(item);
        SchemaNode.setItemsOf(top.getValue(), extracted);
    }

    public void setRow(NestedTableRow<?> row) {
        if (row != null) {
            row.register(this);
        }
    }

    public String toString() {
        return String.format("NestedView[%s]", top.getKey().getLabel());
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new NestedColumnViewSkin(this);
    }
}
