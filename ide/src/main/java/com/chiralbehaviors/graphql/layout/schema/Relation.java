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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.chiralbehaviors.graphql.layout.ListViewWithVisibleRowCount;
import com.chiralbehaviors.graphql.layout.TableViewWithVisibleRowCount;
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

    private static final int SCROLL_WIDTH = 27; // hate this

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
    private Relation               fold;
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

    public void autoLayout(float width) {
        layout(width);
        justify(width);
    }

    @Override
    public Control buildControl() {
        if (isFold()) {
            return fold.buildControl();
        }
        return useTable ? buildNestedTable(n -> n) : buildOutline();
    }

    public List<SchemaNode> getChildren() {
        return children;
    }

    @Override
    public float getTableColumnWidth() {
        if (isFold()) {
            return fold.getTableColumnWidth();
        }
        return super.getTableColumnWidth();
    }

    public boolean isFold() {
        return fold != null;
    }

    @Override
    public boolean isRelation() {
        return true;
    }

    public boolean isUseTable() {
        if (isFold()) {
            return fold.isUseTable();
        }
        return useTable;
    }

    @Override
    public boolean isVariableLength() {
        if (isFold()) {
            return fold.isVariableLength();
        }
        return super.isVariableLength();
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

    public void setFold(boolean fold) {
        this.fold = (fold && children.size() == 1 && children.get(0)
                                                             .isRelation()) ? (Relation) children.get(0)
                                                                            : null;
    }

    public void setItems(Control control, JsonNode data) {
        if (data == null) {
            data = JsonNodeFactory.instance.arrayNode();
        }
        if (isFold()) {
            fold.setItems(control, flatten(data));
            return;
        }
        if (control instanceof ListView) {
            @SuppressWarnings("unchecked")
            ListView<JsonNode> listView = (ListView<JsonNode>) control;
            listView.setItems(new ObservableListWrapper<>(asList(data)));
        } else if (control instanceof TableView) {
            @SuppressWarnings("unchecked")
            TableView<JsonNode> tableView = (TableView<JsonNode>) control;
            tableView.setItems(new ObservableListWrapper<>(asList(data)));
        } else {
            throw new IllegalArgumentException(String.format("Unknown control %s",
                                                             control));
        }
    }

    public void setUseTable(boolean useTable) {
        this.useTable = useTable;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    @Override
    public String toString(int indent) {
        StringBuffer buf = new StringBuffer();
        buf.append(String.format("Relation [%s:%.2f(%.2f) x %s]", label,
                                 justifiedWidth, tableColumnWidth,
                                 averageCardinality));
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
    TableColumn<JsonNode, ?> buildTableColumn(Function<JsonNode, JsonNode> extractor) {
        if (isFold()) {
            //            return fold.buildTableColumn(extractor);
            return fold.buildTableColumn(n -> {
                JsonNode resolved = extractor.apply(n);
                return resolved == null ? null : resolved.get(field);
            });
        }
        TableColumn<JsonNode, JsonNode> column = new TableColumn<>(label);
        column.setMinWidth(justifiedWidth);
        column.setMaxWidth(justifiedWidth);
        //        column.setPrefWidth(justifiedWidth);
        column.getProperties()
              .put("deferToParentPrefWidth", Boolean.TRUE);
        column.setCellValueFactory(cellData -> new ObjectBinding<JsonNode>() {
            @Override
            protected JsonNode computeValue() {
                JsonNode data = cellData.getValue();
                if (data == null) {
                    return JsonNodeFactory.instance.arrayNode();
                }
                JsonNode resolved = extractor.apply(data);
                return resolved == null ? null : resolved.get(field);
            }
        });
        column.setCellFactory(c -> new TableCell<JsonNode, JsonNode>() {
            Control table = buildNestedTable(extractor);

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
                item = item == null ? JsonNodeFactory.instance.arrayNode()
                                    : item;
                setItems(table, item);
                super.setGraphic(table);
            }
        });
        return column;
    }

    @Override
    void justify(float width) {
        if (isFold()) {
            fold.justify(width);
            return;
        }
        if (width <= 0)
            return;

        if (useTable) {
            justifyTable(width);
        } else {
            justifyOutline(width);
        }
    }

    /**
     * Layout of the receiver
     * 
     * @param width
     *            - the width alloted to the relation
     * @return
     */
    @Override
    float layout(float width) {
        if (isFold()) {
            return fold.layout(width);
        }
        useTable = false;
        float available = width - outlineLabelWidth - indentWidth();
        float outlineWidth = children.stream()
                                     .map(child -> child.layout(available))
                                     .max((a, b) -> Float.compare(a, b))
                                     .get();
        outlineWidth += outlineLabelWidth;
        if (tableColumnWidth <= outlineWidth) {
            nestTables();
            return tableColumnWidth;
        }
        return outlineWidth;
    }

    @Override
    float measure(ArrayNode data) {
        if (isFold()) {
            return fold.measure(flatten(data));
        }
        if (data.isNull() || children.size() == 0) {
            return 0;
        }
        tableColumnWidth = SCROLL_WIDTH + indentWidth();
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
            variableLength |= child.isVariableLength();
        }
        averageCardinality = Math.round(sum / children.size()) + 1;
        justifiedWidth = tableColumnWidth;
        return tableColumnWidth;
    }

    @Override
    NodeMaster outlineElement(float labelWidth) {
        Control control = useTable ? buildNestedTable(n -> n) : buildOutline();
        TextArea labelText = new TextArea(label);
        labelText.setStyle("-fx-background-color: red;");
        Pane element;
        if (useTable) {
            element = new HBox(2);
            labelText.setMinWidth(labelWidth);
            labelText.setMaxWidth(labelWidth);
        } else {
            element = new VBox(2);
            labelText.setMinWidth(justifiedWidth);
            labelText.setMaxWidth(justifiedWidth);
        }
        labelText.setWrapText(true);
        labelText.setPrefRowCount(1);
        element.getChildren()
               .add(labelText);
        element.getChildren()
               .add(control);
        element.setMinWidth(justifiedWidth);
        return new NodeMaster(item -> setItems(control, item), element);
    }

    private TableColumn<JsonNode, ?> buildIndentColumn() {
        TableColumn<JsonNode, String> column = new TableColumn<>("");
        column.setMinWidth(indentWidth());
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

    private Control buildNestedTable(Function<JsonNode, JsonNode> extractor) {
        if (isFold()) {
            return fold.buildNestedTable(n -> {
                JsonNode resolved = extractor.apply(n);
                return resolved == null ? null : resolved.get(field);
            });
        }
        TableViewWithVisibleRowCount<JsonNode> table = new TableViewWithVisibleRowCount<>();
        ObservableList<TableColumn<JsonNode, ?>> columns = table.getColumns();
        columns.add(buildIndentColumn());
        children.forEach(node -> {
            columns.add(node.buildTableColumn(extractor));
        });
        table.setPrefWidth(justifiedWidth);
        table.visibleRowCountProperty()
             .set(averageCardinality);
        table.getProperties()
             .put("deferToParentPrefWidth", Boolean.TRUE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private ListView<JsonNode> buildOutline() {
        if (isFold()) {
            return fold.buildOutline();
        }
        ListViewWithVisibleRowCount<JsonNode> list = new ListViewWithVisibleRowCount<>();
        list.setCellFactory(c -> new ListCell<JsonNode>() {
            HBox                        cell     = new HBox(2);
            Map<SchemaNode, NodeMaster> controls = new HashMap<>();
            {
                cell.getChildren()
                    .add(new Text(INDENT));
                VBox box = new VBox(2);
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
                children.forEach(child -> {
                    controls.get(child).items.accept(item.get(child.field));
                });
                super.setGraphic(cell);
            }
        });
        list.setPrefWidth(justifiedWidth);
        //        list.setMinWidth(justifiedWidth);
        list.visibleRowCountProperty()
            .set(averageCardinality);
        list.getProperties()
            .put("deferToParentPrefWidth", Boolean.TRUE);
        return list;
    }

    private ArrayNode flatten(JsonNode data) {
        ArrayNode flattened = JsonNodeFactory.instance.arrayNode();
        if (data != null) {
            if (data.isArray()) {
                data.forEach(item -> {
                    flattened.addAll(asArray(item.get(fold.getField())));
                });
            } else {
                flattened.addAll(asArray(data.get(fold.getField())));
            }
        }
        return flattened;
    }

    private void justifyOutline(float width) {
        if (variableLength) {
            justifiedWidth = width;
            float available = width - outlineLabelWidth - indentWidth()
                              - SCROLL_WIDTH;
            float tableWidth = width - indentWidth() - SCROLL_WIDTH;
            children.forEach(child -> {
                if (child.isRelation()) {
                    if (((Relation) child).isUseTable()) {
                        child.justify(available);
                    } else {
                        child.justify(tableWidth);
                    }
                } else {
                    child.justify(available);
                }
            });
        }
    }

    private void justifyTable(float width) {
        justifiedWidth = width;
        float slack = width - tableColumnWidth;
        assert slack >= 0 : String.format("Negative slack: %.2f (%.2f) \n%s",
                                          slack, width, this);
        float total = children.stream()
                              .filter(child -> child.isVariableLength())
                              .map(child -> child.getTableColumnWidth())
                              .reduce((a, b) -> a + b)
                              .orElse(0.0f);
        children.stream()
                .filter(child -> child.isVariableLength())
                .forEach(child -> child.justify(slack
                                                * (child.getTableColumnWidth()
                                                   / total)
                                                + child.getTableColumnWidth()));
    }

    private void nestTables() {
        useTable = true;
        children.forEach(child -> {
            if (child.isRelation()) {
                ((Relation) child).nestTables();
            }
        });
    }
}
