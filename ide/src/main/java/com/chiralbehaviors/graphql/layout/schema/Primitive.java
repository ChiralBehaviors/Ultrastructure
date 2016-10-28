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
import java.util.function.Consumer;
import java.util.function.Function;

import com.chiralbehaviors.graphql.layout.Layout;
import com.chiralbehaviors.graphql.layout.controls.NestedColumnView;
import com.chiralbehaviors.graphql.layout.controls.NestedTableRow;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import javafx.beans.binding.ObjectBinding;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.util.Pair;

/**
 * @author hhildebrand
 *
 */
public class Primitive extends SchemaNode {
    private double  columnWidth       = 0;
    private double  maxWidth          = 0;
    private double  nestingInset      = 0;
    private double  valueDefaultWidth = 0;
    private double  valueHeight       = 0;
    private boolean variableLength    = false;

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
    TableColumn<JsonNode, JsonNode> buildTableColumn(int cardinality,
                                                     NestingFunction nesting,
                                                     Layout layout,
                                                     boolean key) {
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
                                              key));
        return column;
    }

    @Override
    List<Primitive> gatherLeaves() {
        return Collections.singletonList(this);
    }

    TableColumn<JsonNode, JsonNode> getColumn() {
        TableColumn<JsonNode, JsonNode> column = new TableColumn<>(label);
        column.getStyleClass()
              .add(tableColumnStyleClass());
        return column;
    }

    double getLabelWidth(Layout layout) {
        return layout.labelWidth(label);
    }

    @Override
    double getTableColumnWidth() {
        return columnWidth + nestingInset;
    }

    double getValueHeight(int cardinality, Layout layout) {
        return getValueHeight(layout);
    }

    @Override
    boolean isJusifiable() {
        return true;
        //        return variableLength || justifiedWidth < maxWidth;
    }

    void justify(double width, Layout layout) {
        valueHeight = 0;
        justifiedWidth = width;
    }

    @Override
    double layout(double width, Layout layout) {
        return variableLength ? width : Math.min(width, columnWidth);
    }

    @Override
    double layoutRow(Layout layout) {
        valueHeight = Layout.snap(getValueHeight(layout)
                                  + (layout.getValueInsets()
                                           .getTop()
                                     + layout.getValueInsets()
                                             .getBottom()));
        return valueHeight;
    }

    @Override
    double measure(ArrayNode data, Layout layout, boolean key) {
        double labelWidth = getLabelWidth(layout);
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

        columnWidth = Math.max(labelWidth,
                               Math.max(valueDefaultWidth, averageWidth));
        if (maxWidth > averageWidth) {
            variableLength = true;
        }

        nestingInset = layout.getValueInsets()
                             .getLeft()
                       + layout.getValueInsets()
                               .getRight();
        justifiedWidth = columnWidth;
        return columnWidth + nestingInset;
    }

    @Override
    Pair<Consumer<JsonNode>, Parent> outlineElement(double labelWidth,
                                                    Function<JsonNode, JsonNode> extractor,
                                                    int cardinality,
                                                    Layout layout) {
        HBox box = new HBox();
        TextArea labelText = new TextArea(label);
        labelText.getStyleClass()
                 .add(outlineLabelStyleClass());
        labelText.setMinWidth(labelWidth);
        labelText.setPrefWidth(labelWidth);
        labelText.setMaxWidth(labelWidth);
        labelText.setPrefRowCount(1);
        box.getChildren()
           .add(labelText);
        Control control = buildControl(cardinality, layout);
        control.setPrefWidth(justifiedWidth);
        double valueHeight = getValueHeight(cardinality, layout);
        control.setPrefHeight(valueHeight);
        box.getChildren()
           .add(control);
        box.setPrefHeight(valueHeight);
        box.setPrefWidth(justifiedWidth);
        //        VBox.setVgrow(labelText, Priority.NEVER);
        //        VBox.setVgrow(control, Priority.ALWAYS);
        return new Pair<>(item -> {
            JsonNode extracted = extractor.apply(item);
            JsonNode extractedField = extracted.get(field);
            setItemsOf(control, extractedField);
        }, box);
    }

    private TextArea buildControl(int cardinality, Layout layout) {
        TextArea text = new TextArea();
        text.getStyleClass()
            .add(valueClass());
        text.setWrapText(true);
        text.setMinWidth(0);
        text.setPrefWidth(1);
        text.setPrefHeight(getValueHeight(cardinality, layout));
        return text;
    }

    private TableCell<JsonNode, JsonNode> createCell(int cardinality,
                                                     NestingFunction nesting,
                                                     Layout layout,
                                                     boolean key) {
        return new TableCell<JsonNode, JsonNode>() {
            NestedColumnView view;
            {
                if (key) {
                    getStyleClass().add(tableKeyCellClass());
                } else {
                    getStyleClass().add(tableCellClass());
                }
                emptyProperty().addListener((obs, wasEmpty, isEmpty) -> {
                    if (isEmpty) {
                        setGraphic(null);
                    } else {
                        setGraphic(view);
                    }
                });
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setAlignment(Pos.CENTER);
            }

            @Override
            public void updateIndex(int i) {
                int prev = getIndex();
                if (prev != i) {
                    if (i < 0) {
                        setGraphic(null);
                        view = null;
                    } else {
                        @SuppressWarnings("unchecked")
                        NestedTableRow<JsonNode> row = (NestedTableRow<JsonNode>) getTableRow();
                        if (row != null) {
                            view = (NestedColumnView) nesting.apply((label,
                                                                     height) -> {
                                TextArea control = buildControl(cardinality,
                                                                layout);
                                control.setMinHeight(height);
                                control.setPrefHeight(height);
                                layout.getModel()
                                      .apply(control, Primitive.this);
                                return control;
                            }, row, Primitive.this);
                        }
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

    private double getValueHeight(Layout layout) {
        return layout.getValueLineHeight()
               * (Math.ceil((maxWidth + layout.valueDoubleSpaceWidth())
                            / justifiedWidth)
                  + 1);
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
