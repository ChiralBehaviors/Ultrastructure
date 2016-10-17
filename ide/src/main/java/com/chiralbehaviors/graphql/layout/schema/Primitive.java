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
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * @author hhildebrand
 *
 */
public class Primitive extends SchemaNode {

    private double columnWidth;
    private double maxWidth          = 0;
    private double nestingInset;
    private double valueDefaultWidth = 0;

    public Primitive(String label) {
        super(label);
    }

    @Override
    public String toString() {
        return String.format("Primitive [%s:%.2f(%.2f)]", label, justifiedWidth,
                             getTableColumnWidth());
    }

    @Override
    public String toString(int indent) {
        return toString();
    }
 
    @Override
    TableColumn<JsonNode, JsonNode> buildTableColumn(boolean topLevel,
                                                     int cardinality,
                                                     NestingFunction nesting,
                                                     Layout layout) {
        double height = getValueHeight(cardinality, layout);

        TableColumn<JsonNode, JsonNode> column = new TableColumn<>(label);
        column.getStyleClass()
              .add(tableColumnStyleClass());

        column.setCellValueFactory(cellData -> new ObjectBinding<JsonNode>() {
            @Override
            protected JsonNode computeValue() {
                return cellData.getValue();
            }
        });

        column.setCellFactory(c -> createCell(cardinality, nesting, layout,
                                              height));
        return column;
    }

    private TableCell<JsonNode, JsonNode> createCell(int cardinality,
                                                     NestingFunction nesting,
                                                     Layout layout,
                                                     double height) {
        return new TableCell<JsonNode, JsonNode>() {
            NestedColumnView view;
            {
                getStyleClass().add(tableCellClass());
                emptyProperty().addListener((obs, wasEmpty, isEmpty) -> {
                    if (isEmpty) {
                        setGraphic(null);
                    } else {
                        setGraphic(view);
                    }
                });
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }

            @Override
            public void updateIndex(int i) {
                int prev = getIndex();
                if (prev != i) {
                    @SuppressWarnings("unchecked")
                    NestedTableRow<JsonNode> row = (NestedTableRow<JsonNode>) getTableRow();
                    if (row != null) {
                        view = (NestedColumnView) nesting.apply(label -> {
                            return buildControl(cardinality, layout);
                        }, height, row, Primitive.this);
                    }
                }
                super.updateIndex(i);
            }

            @Override
            protected void updateItem(JsonNode item, boolean empty) {
                if (item == getItem()) {
                    return;
                }
                super.updateItem(item, empty);
                super.setText(null);
                if (empty || item == null) {
                    return;
                }
                if (view != null) {
                    view.setItem(item);
                }
            }
        };
    }

    @Override
    List<Primitive> gatherLeaves() {
        return Collections.singletonList(this);
    }

    double getValueHeight(int cardinality, Layout layout) {
        return layout.valueHeight(maxWidth, justifiedWidth);
    }

    @Override
    double getTableColumnWidth() {
        return columnWidth + nestingInset;
    }

    @Override
    double layout(double width) {
        return variableLength ? width : Math.min(width, getTableColumnWidth());
    }

    void justify(double width) {
        if (variableLength) {
            justifiedWidth = Math.max(maxWidth, width);
        }
    }

    @Override
    double measure(ArrayNode data, int nestingLevel, Layout layout) {
        labelWidth = layout.labelWidth(label) + layout.labelWidth(" ") * 2;
        double sum = 0;
        maxWidth = 0;
        columnWidth = 0;
        for (JsonNode prim : data) {
            List<JsonNode> rows = asList(prim);
            double width = 0;
            for (JsonNode row : rows) {
                width += layout.valueWidth(toString(row));
                maxWidth = Math.max(maxWidth, width);
            }
            sum += rows.isEmpty() ? 1 : width / rows.size();
        }
        double averageWidth = data.size() == 0 ? 0 : (sum / data.size());

        maxWidth += layout.valueWidth(" ") * 2;
        if (maxWidth > valueDefaultWidth && maxWidth > averageWidth) {
            variableLength = true;
        }
        columnWidth = Math.max(labelWidth,
                               Math.max(valueDefaultWidth, averageWidth));
        columnWidth += layout.getValueInsets()
                             .getLeft()
                       + layout.getValueInsets()
                               .getRight();

        Insets listInsets = layout.getListInsets();
        Insets tableCellInsets = layout.getTableCellInsets();
        nestingInset = nestingLevel
                       * (listInsets.getLeft() + listInsets.getRight())
                       + (tableCellInsets.getLeft()
                          + tableCellInsets.getRight());
        justifiedWidth = getTableColumnWidth();
        return justifiedWidth;
    }

    @Override
    NodeMaster outlineElement(double labelWidth,
                              Function<JsonNode, JsonNode> extractor,
                              int cardinality, Layout layout) {
        HBox box = new HBox();
        TextArea labelText = new TextArea(label);
        labelText.getStyleClass()
                 .add(outlineLabelStyleClass());
        labelText.setMinWidth(0);
        labelText.setPrefWidth(labelWidth);
        labelText.setPrefRowCount(1);
        box.getChildren()
           .add(labelText);
        Control control = buildControl(cardinality, layout);
        HBox.setHgrow(control, Priority.ALWAYS);
        box.getChildren()
           .add(control);
        return new NodeMaster(item -> {
            JsonNode extracted = extractor.apply(item);
            JsonNode extractedField = extracted.get(field);
            setItemsOf(control, extractedField);
        }, box, getValueHeight(cardinality, layout));
    }

    private TextArea buildControl(int cardinality, Layout layout) {
        TextArea text = new TextArea();
        text.getStyleClass()
            .add(valueClass());
        text.setWrapText(true);
        text.setMinWidth(0);
        text.setPrefWidth(1);
        double height = getValueHeight(cardinality, layout);
        text.setMinHeight(height);
        text.setPrefHeight(height);
        return text;
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

    private String valueClass() {
        return String.format("%s-value", field);
    }
}
