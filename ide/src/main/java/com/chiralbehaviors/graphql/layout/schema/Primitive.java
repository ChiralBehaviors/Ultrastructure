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

package com.chiralbehaviors.graphql.layout.schema;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.chiralbehaviors.graphql.layout.NestedColumnView;
import com.chiralbehaviors.graphql.layout.NestedTableRow;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import javafx.beans.binding.ObjectBinding;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;

/**
 * @author hhildebrand
 *
 */
public class Primitive extends SchemaNode {

    private float valueDefaultWidth = 0;
    private Font  valueFont         = Font.getDefault();

    public Primitive(String label) {
        super(label);
    }

    @Override
    public String toString() {
        return String.format("Primitive [%s:%.2f(%.2f)]", label, justifiedWidth,
                             tableColumnWidth);
    }

    @Override
    public String toString(int indent) {
        return toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    TableColumn<JsonNode, JsonNode> buildTableColumn(int cardinality,
                                                     NestingFunction nesting) {

        TableColumn<JsonNode, JsonNode> column = new TableColumn<>(label);
        constrain(column);

        column.setCellValueFactory(cellData -> new ObjectBinding<JsonNode>() {
            @Override
            protected JsonNode computeValue() {
                return cellData.getValue();
            }
        });

        column.setCellFactory(c -> new TableCell<JsonNode, JsonNode>() {

            @Override
            protected void updateItem(JsonNode item, boolean empty) {
                if (item == getItem())
                    return;
                super.updateItem(item, empty);
                super.setText(null);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }
                NestedColumnView view = (NestedColumnView) nesting.apply(node -> {
                    return buildControl(cardinality);
                }, (cardinality * FONT_LOADER.getFontMetrics(valueFont)
                                             .getLineHeight()), (NestedTableRow<JsonNode>) getTableRow(),
                                                                         Primitive.this);
                setGraphic(view);
                view.setItem(item);
            }
        });
        return column;
    }

    @Override
    List<Primitive> gatherLeaves() { 
        return Collections.singletonList(this);
    }

    @Override
    float layout(float width) {
        return variableLength ? width : Math.min(width, tableColumnWidth);
    }

    @Override
    float measure(ArrayNode data) {
        float sum = 0;
        float maxWidth = 0;
        for (JsonNode prim : data) {
            List<JsonNode> rows = asList(prim);
            float width = 0;
            for (JsonNode row : rows) {
                width += valueWidth(toString(row));
                maxWidth = Math.max(maxWidth, width);
            }
            sum += rows.isEmpty() ? 1 : width / rows.size();
        }
        float averageWidth = data.size() == 0 ? 0 : (sum / data.size());

        if (maxWidth > valueDefaultWidth && maxWidth > averageWidth) {
            variableLength = true;
        }
        tableColumnWidth = Math.max(labelWidth(),
                                    Math.max(valueDefaultWidth, averageWidth))
                           + 4;
        justifiedWidth = tableColumnWidth;
        return tableColumnWidth;
    }

    @Override
    NodeMaster outlineElement(float labelWidth,
                              Function<JsonNode, JsonNode> extractor,
                              int cardinality) {
        HBox box = new HBox();
        TextArea labelText = new TextArea(label);
        labelText.setStyle("-fx-background-color: red;");
        labelText.setMinWidth(0);
        labelText.setPrefWidth(labelWidth);
        labelText.setPrefRowCount(cardinality);
        HBox.setHgrow(labelText, Priority.ALWAYS);
        box.getChildren()
           .add(labelText);
        Control control = buildControl(cardinality);
        HBox.setHgrow(control, Priority.ALWAYS);
        control.setPrefWidth(justifiedWidth);
        box.getChildren()
           .add(control);
        //        box.setPrefWidth(justifiedWidth);
        return new NodeMaster(item -> {
            JsonNode extracted = extractor.apply(item);
            JsonNode extractedField = extracted.get(field);
            setItemsOf(control, extractedField);
        }, box, 1);
    }

    private Control buildControl(int cardinality) {
        TextArea text = new TextArea();
        text.setWrapText(true);
        text.setMinWidth(0);
        text.setPrefWidth(1);
        //        text.setMaxWidth(Double.MAX_VALUE);
        text.setPrefHeight((cardinality * FONT_LOADER.getFontMetrics(valueFont)
                                                     .getLineHeight()));
        //        text.setStyle("-fx-background-insets: 0 ;");
        text.setFont(valueFont);
        return (Control) text;
    }

    private String toString(JsonNode value) {
        if (value == null) {
            return "";
        }
        if (value instanceof ArrayNode) {
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for (JsonNode e : value) {
                if (first) {
                    first = false;
                    builder.append('[');
                } else {
                    builder.append(", ");
                }
                builder.append(e.asText());
            }
            builder.append(']');
            return builder.toString();
        } else {
            return value.asText();
        }
    }

    private float valueWidth(String text) {
        return FONT_LOADER.computeStringWidth(text, valueFont);
    }
}
