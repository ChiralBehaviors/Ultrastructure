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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.sun.javafx.collections.ObservableListWrapper;

import graphql.language.Definition;
import graphql.language.Field;
import graphql.language.FragmentSpread;
import graphql.language.InlineFragment;
import graphql.language.OperationDefinition;
import graphql.language.OperationDefinition.Operation;
import graphql.language.Selection;
import graphql.parser.Parser;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * @author hhildebrand
 *
 */
public class Relation extends SchemaNode implements Cloneable {

    private static final int SCROLL_WIDTH = 22;

    public static SchemaNode buildSchema(String query, String source) {
        for (Definition definition : new Parser().parseDocument(query)
                                                 .getDefinitions()) {
            if (definition instanceof OperationDefinition) {
                OperationDefinition operation = (OperationDefinition) definition;
                if (operation.getOperation()
                             .equals(Operation.QUERY)) {
                    for (Selection selection : operation.getSelectionSet()
                                                        .getSelections()) {
                        if (selection instanceof Field) {
                            Field field = (Field) selection;
                            if (source.equals(field.getName())) {
                                return Relation.buildSchema(field);
                            }
                        }
                    }
                }
            }
        }
        throw new IllegalStateException(String.format("Invalid query, cannot find source: %s",
                                                      source));
    }

    private static SchemaNode buildSchema(Field parentField) {
        Relation parent = new Relation(parentField.getName());
        for (Selection selection : parentField.getSelectionSet()
                                              .getSelections()) {
            if (selection instanceof Field) {
                Field field = (Field) selection;
                if (field.getSelectionSet() == null) {
                    parent.addChild(new Primitive(field.getName()));
                } else {
                    parent.addChild(buildSchema(field));
                }
            } else if (selection instanceof InlineFragment) {

            } else if (selection instanceof FragmentSpread) {

            }
        }
        return parent;
    }

    private final List<SchemaNode> children          = new ArrayList<>();
    private float                  outlineLabelWidth = 0;
    private boolean                useTable          = false;

    public Relation(String label) {
        super(label);
    }

    public void addChild(SchemaNode child) {
        incrementNesting();
        children.add(child);
        outlineLabelWidth = Math.max(child.labelWidth() + SCROLL_WIDTH,
                                     outlineLabelWidth);
    }

    @Override
    public Control buildControl() {
        return useTable ? buildNestedTable() : buildOutline();
    }

    public List<SchemaNode> getChildren() {
        return children;
    }

    /**
     * Layout of the receiver
     * 
     * @param width
     *            - the width alloted to the relation
     * @return
     */
    @Override
    public float layout(float width) {
        Float outlineWidth = children.stream()
                                     .map(child -> child.layout(width))
                                     .reduce((max, layout) -> Math.max(max,
                                                                       layout))
                                     .get();
        if (tableColumnWidth < outlineWidth) {
            nestTables();
        }
        return 0;
    }

    public void measure(JsonNode jsonNode) {
        if (jsonNode.isArray()) {
            ArrayNode array = (ArrayNode) jsonNode;
            measure(array);
        } else {
            ArrayNode singleton = JsonNodeFactory.instance.arrayNode();
            singleton.add(jsonNode);
            measure(singleton);
        }
    }

    public void nestTables() {
        useTable = true;
        children.forEach(child -> {
            if (child instanceof Relation) {
                ((Relation) child).nestTables();
            }
        });
    }

    public void setItems(Control control, JsonNode item) {
        if (control instanceof ListView) {
            @SuppressWarnings("unchecked")
            ListView<JsonNode> listView = (ListView<JsonNode>) control;
            listView.setItems(new ObservableListWrapper<>(asList(item)));
        } else if (control instanceof TableView) {
            @SuppressWarnings("unchecked")
            TableView<JsonNode> tableView = (TableView<JsonNode>) control;
            tableView.setItems(new ObservableListWrapper<>(asList(item)));
        } else {
            throw new IllegalArgumentException(String.format("Unknown control %s",
                                                             control));
        }
    }

    @Override
    public String toString() {
        return toString(0);
    }

    @Override
    public String toString(int indent) {
        StringBuffer buf = new StringBuffer();
        buf.append(String.format("Relation [%s:%s x %s]", label,
                                 tableColumnWidth, averageCardinality));
        buf.append('\n');
        children.forEach(c -> {
            for (int i = 0; i < indent; i++) {
                buf.append("    ");
            }
            buf.append("  - ");
            buf.append(c.toString(indent + 1));
            buf.append('\n');
        });
        return buf.toString();
    }

    @Override
    TableColumn<JsonNode, ?> buildTableColumn() {
        TableColumn<JsonNode, List<JsonNode>> column = new TableColumn<>(label);
        column.setMinWidth(tableColumnWidth);
        column.setMaxWidth(tableColumnWidth);
        column.setCellValueFactory(cellData -> new ObjectBinding<List<JsonNode>>() {
            @Override
            protected List<JsonNode> computeValue() {
                return asList(cellData.getValue()
                                      .get(field));
            }
        });
        column.setCellFactory(c -> new TableCell<JsonNode, List<JsonNode>>() {
            TableView<JsonNode> table = buildNestedTable();

            @Override
            protected void updateItem(List<JsonNode> item, boolean empty) {
                if (item == getItem()) {
                    return;
                }
                super.updateItem(item, empty);
                super.setText(null);
                if (empty) {
                    super.setGraphic(null);
                    return;
                }
                item = item == null ? Collections.emptyList() : item;
                table.setItems(new ObservableListWrapper<>(item));
                super.setGraphic(table);
                if (item.isEmpty()) {

                }
                setAlignment(Pos.CENTER);
            }
        });
        return column;
    }

    TableColumn<JsonNode, ?> buildIndentColumn() {
        TableColumn<JsonNode, String> column = new TableColumn<>("");
        column.setMinWidth(indentWidth());
        column.setMaxWidth(indentWidth());
        column.getProperties()
              .put("deferToParentPrefWidth", Boolean.TRUE);
        column.setCellValueFactory(cellData -> new ObjectBinding<String>() {
            @Override
            protected String computeValue() {
                return INDENT;
            }
        });
        column.setCellFactory(c -> new TableCell<JsonNode, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                setGraphic(new Text(item));
            }
        });
        return column;
    }

    @Override
    void justify(float width) {
        if (!useTable) {
            return;
        }
        float slack = width - tableColumnWidth;
        if (slack <= 0) {
            return;
        }
        justifiedWidth = width;
        children.stream()
                .filter(child -> child.isVariableLength)
                .forEach(child -> child.justify(width * (child.tableColumnWidth
                                                         / tableColumnWidth)));
    }

    @Override
    float measure(ArrayNode data) {
        if (data.isNull() || children.size() == 0) {
            return 0;
        }
        tableColumnWidth = SCROLL_WIDTH;
        int sum = 0;
        for (SchemaNode child : children) {
            ArrayNode aggregate = JsonNodeFactory.instance.arrayNode();
            int cardSum = 0;
            for (JsonNode node : data) {
                JsonNode sub = node.get(child.field);
                if (sub instanceof ArrayNode) {
                    aggregate.addAll((ArrayNode) sub);
                    cardSum += sub.size();
                } else {
                    aggregate.add(sub);
                    cardSum += 1;
                }
            }
            sum += Math.round(cardSum / data.size()) + 1;
            tableColumnWidth += child.measure(aggregate);
            isVariableLength |= child.isVariableLength;
        }
        averageCardinality = Math.round(sum / children.size());
        return tableColumnWidth;
    }

    @Override
    NodeMaster outlineElement(float labelWidth) {
        float outlineWidth = outlineWidth();
        Control control = useTable ? buildNestedTable() : buildOutline();
        Pane element;
        if (useTable) {
            element = new HBox();
        } else {
            element = new VBox();
        }
        TextArea labelText = new TextArea(label);
        labelText.setWrapText(true);
        labelText.setPrefRowCount(1);
        if (useTable) {
            labelText.setMinWidth(labelWidth);
            labelText.setMaxWidth(labelWidth);
        } else {
            labelText.setMinWidth(outlineWidth);
            labelText.setMaxWidth(outlineWidth);
        }
        element.getChildren()
               .add(labelText);
        element.getChildren()
               .add(control);
        element.setVisible(true);
        element.setMinWidth(outlineWidth);
        element.setMaxWidth(outlineWidth);
        return new NodeMaster(item -> setItems(control, item), element);
    }

    @Override
    float outlineWidth() {
        if (useTable) {
            return tableColumnWidth;
        }
        float outlineWidth = 0;
        for (SchemaNode child : children) {
            outlineWidth = Math.max(outlineWidth, child.outlineWidth());
        }
        outlineWidth += indentWidth() + outlineLabelWidth + SCROLL_WIDTH;
        return Math.max(labelWidth(), outlineWidth);
    }

    private TableViewWithVisibleRowCount<JsonNode> buildNestedTable() {
        TableViewWithVisibleRowCount<JsonNode> table = new TableViewWithVisibleRowCount<>();
        ObservableList<TableColumn<JsonNode, ?>> columns = table.getColumns();
        //        columns.add(buildIndentColumn());
        children.forEach(node -> {
            columns.add(node.buildTableColumn());
        });
        table.setMaxWidth(tableColumnWidth);
        table.setMinWidth(tableColumnWidth);
        table.visibleRowCountProperty()
             .set(averageCardinality);
        table.getProperties()
             .put("deferToParentPrefWidth", Boolean.TRUE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private ListView<JsonNode> buildOutline() {
        float outlineWidth = outlineWidth();
        ListView<JsonNode> list = new ListViewFixed<>();
        Map<SchemaNode, NodeMaster> controls = new HashMap<>();
        list.setCellFactory(c -> new ListCell<JsonNode>() {
            HBox cell = new HBox();

            {
                cell.getChildren()
                    .add(new Text(INDENT));
                VBox box = new VBox(5);
                children.forEach(child -> {
                    NodeMaster master = child.outlineElement(outlineLabelWidth);
                    controls.put(child, master);
                    box.getChildren()
                       .add(master.node);
                });
                cell.getChildren()
                    .add(box);
                cell.getProperties()
                    .put("deferToParentPrefWidth", Boolean.TRUE);
                cell.setVisible(true);
            }

            @Override
            protected void updateItem(JsonNode item, boolean empty) {
                if (item == getItem()) {
                    return;
                }
                super.updateItem(item, empty);
                super.setText(null);
                if (empty) {
                    super.setGraphic(null);
                    return;
                }
                super.setGraphic(cell);
                children.forEach(child -> {
                    controls.get(child).items.accept(item.get(child.field));
                });
            }
        });
        list.setMaxWidth(outlineWidth);
        list.setMinWidth(outlineWidth);
        list.setVisible(true);
        list.getProperties()
            .put("deferToParentPrefWidth", Boolean.TRUE);
        return list;
    }
}
