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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;

/**
 * @author hhildebrand
 *
 */
public class Primitive extends SchemaNode {

    private PrimitiveConstraints constraints;
    private boolean              isVariableLength = false;
    private float                valueDefaultWidth;
    private Font                 valueFont        = Font.getDefault();

    public Primitive(String label) {
        super(label);
    }

    public PrimitiveConstraints getConstraints() {
        return constraints;
    }

    public float getValueDefaultWidth() {
        return valueDefaultWidth;
    }

    public boolean isVariableLength() {
        return isVariableLength;
    }

    public void setConstraints(PrimitiveConstraints constraints) {
        this.constraints = constraints;
    }

    @Override
    public String toString() {
        return String.format("Primitive [%s:%s]", getLabel(),
                             valueDefaultWidth);
    }

    @Override
    public String toString(int indent) {
        return toString();
    }

    @Override
    protected TableColumn<JsonNode, ?> buildTableColumn() {
        TableColumn<JsonNode, String> column = new TableColumn<>(label);
        column.setPrefWidth(tableColumnWidth);
        column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()
                                                                                .get(field)
                                                                                .asText()));
        column.setCellFactory(c -> new TableCell<JsonNode, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                if (item == getItem())
                    return;
                super.updateItem(item, empty);
                super.setText(item);
            }
        });
        return column;
    }

    @Override
    protected void measure(ArrayNode data) {
        float sum = 0;
        float max = 0;
        for (JsonNode prim : data) {
            float width = valueWidth(prim.asText());
            sum += width;
            max = Math.max(max, width);
        }
        valueDefaultWidth = data.size() == 0 ? 0 : sum / data.size();
        if (max > valueDefaultWidth) {
            isVariableLength = true;
        }
        tableColumnWidth = Math.max(label.length(), valueDefaultWidth);
    }

    private float valueWidth(String text) {
        return FONT_LOADER.computeStringWidth(text, valueFont);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.graphql.layout.SchemaNode#buildControl()
     */
    @Override
    public Control buildControl() {
        return new TextField();
    }
}
