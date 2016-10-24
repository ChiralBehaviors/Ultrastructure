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

import com.chiralbehaviors.graphql.layout.controls.NestedTableRow;
import com.chiralbehaviors.graphql.layout.schema.Primitive;
import com.chiralbehaviors.graphql.layout.schema.Relation;
import com.chiralbehaviors.graphql.layout.schema.SchemaNode;
import com.fasterxml.jackson.databind.JsonNode;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

/**
 * 
 * @author hhildebrand
 *
 */
public interface LayoutModel {

    default void apply(ListCell<JsonNode> cell, Relation relation) {
    }

    default void apply(ListView<JsonNode> list, Relation relation) {
    }

    default void apply(ListView<JsonNode> list, Relation relation,
                       SchemaNode child) {
    }

    default void apply(NestedTableRow<JsonNode> row, Relation relation) {
    }

    default void apply(TableView<JsonNode> table, Relation relation) {
    }

    default void apply(TextArea control, Primitive primitive) {
    }
}
