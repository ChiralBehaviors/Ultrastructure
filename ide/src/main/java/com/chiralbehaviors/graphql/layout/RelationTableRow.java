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

import java.util.function.Consumer;

import com.fasterxml.jackson.databind.JsonNode;

import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TableRow;

/**
 * 
 * The tableRow which will holds the top level relation and primitives
 */
public class RelationTableRow extends TableRow<JsonNode> {
    public final Consumer<JsonNode> manager;

    public RelationTableRow(Consumer<JsonNode> manager, Control row) {
        this.manager = manager;
        getChildren().setAll(row);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new RelationTableRowSkin(this);
    }

    @Override
    protected void updateItem(JsonNode item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            return;
        }
        manager.accept(item);
    }
}
