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
import com.chiralbehaviors.graphql.layout.NestedTableView;
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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

    private int                    averageCardinality = 1;
    private final List<SchemaNode> children           = new ArrayList<>();
    private Relation               fold;
    private float                  outlineLabelWidth  = 0;
    private boolean                useTable           = false;

    public Relation(String label) {
        super(label);
    }

    public void addChild(SchemaNode child) {
        children.add(child);
        outlineLabelWidth = Math.max(child.labelWidth(), outlineLabelWidth);
    }

    public void autoLayout(float width) {
        layout(width);
        justify(width);
    }

    public Control buildControl() {
        if (isFold()) {
            return fold.buildControl();
        }
        Function<JsonNode, JsonNode> extractor = n -> {
            JsonNode temp = n;
            return temp;
        };
        return useTable ? buildTable(extractor, -1) : buildOutline(n -> n, -1);
    }

    public int getAverageCardinality() {
        return averageCardinality;
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

    public void setAverageCardinality(int averageCardinality) {
        this.averageCardinality = averageCardinality;
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
        } else {
            List<JsonNode> dataList = asList(data);
            ObservableListWrapper<JsonNode> observedData = new ObservableListWrapper<>(dataList);
            if (control instanceof ListView) {
                @SuppressWarnings("unchecked")
                ListView<JsonNode> listView = (ListView<JsonNode>) control;
                listView.setItems(observedData);
            } else if (control instanceof TableView) {
                @SuppressWarnings("unchecked")
                TableView<JsonNode> tableView = (TableView<JsonNode>) control;
                tableView.setItems(observedData);
            } else if (control instanceof NestedTableView) {
                @SuppressWarnings("unchecked")
                NestedTableView<JsonNode> tableView = (NestedTableView<JsonNode>) control;
                tableView.setItems(observedData);
            } else {
                throw new IllegalArgumentException(String.format("Unknown control %s",
                                                                 control));
            }
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
    TableColumn<String, ?> buildHeader() {
        if (isFold()) {
            return fold.buildHeader();
        }
        TableColumn<String, Object> header = new TableColumn<>(label);
        header.setCellFactory(c -> new TableCell<String, Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                setMinWidth(justifiedWidth);
            }
        });
        header.setPrefWidth(justifiedWidth);
        header.setStyle("-fx-padding: 0 0 0 0;");
//        header.setMaxWidth(justifiedWidth);
        header.setMinWidth(justifiedWidth);
        header.getProperties()
              .put("deferToParentPrefWidth", Boolean.TRUE);

        children.forEach(node -> {
            header.getColumns()
                  .add(node.buildHeader());
        });
        return header;
    }

    @Override
    TableColumn<JsonNode, ?> buildTableColumn(Function<JsonNode, JsonNode> extractor,
                                              int cardinality, boolean last) {
        if (isFold()) {
            return fold.buildTableColumn(n -> {
                JsonNode resolved = extractor.apply(n);
                return resolved == null ? null : resolved.get(field);
            }, averageCardinality, last);
        }

        TableColumn<JsonNode, JsonNode> column = new TableColumn<>(label);
        constrain(column, last);
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
            Control table = buildNestedTable(n -> n, cardinality);

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
                super.setGraphic(table);
                setItems(table, item);
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
        float available = width - outlineLabelWidth;
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
        tableColumnWidth = 0;
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
            sum += data.size() == 0 ? 1 : Math.round(cardSum / data.size());
            tableColumnWidth += child.measure(aggregate);
            variableLength |= child.isVariableLength();
        }
        averageCardinality = Math.max(2, Math.round(sum / children.size()));
        justifiedWidth = tableColumnWidth;
        return tableColumnWidth;
    }

    @Override
    NodeMaster outlineElement(float labelWidth,
                              Function<JsonNode, JsonNode> extractor,
                              int cardinality) {
        if (isFold()) {
            //            return fold.outlineElement(labelWidth, extractor);
            return fold.outlineElement(labelWidth, n -> {
                JsonNode resolved = extractor.apply(n);
                return resolved == null ? null : resolved.get(field);
            }, averageCardinality);
        }
        Control control = useTable ? buildTable(n -> n, cardinality)
                                   : buildOutline(n -> n, cardinality);
        TextArea labelText = new TextArea(label);
        labelText.setStyle("-fx-background-color: red;");
        Pane element;
        if (useTable) {
            element = new HBox(2);
            //            labelText.setMinWidth(labelWidth);
            labelText.setMaxWidth(labelWidth);
        } else {
            element = new VBox(2);
            //            labelText.setMinWidth(justifiedWidth);
            labelText.setMaxWidth(justifiedWidth);
        }
        labelText.setWrapText(true);
        labelText.setPrefRowCount(1);
        element.getChildren()
               .add(labelText);
        element.getChildren()
               .add(control);
        element.setMinWidth(justifiedWidth);
        return new NodeMaster(item -> {
            if (item == null) {
                return;
            }
            JsonNode extracted = extractor.apply(item);
            setItems(control, extracted == null ? null : extracted.get(field));
        }, element);
    }

    @Override
    float outlineWidth() {
        if (isFold()) {
            return fold.outlineWidth();
        }
        return super.outlineWidth();
    }

    /**
     * Recursive case
     */
    private TableView<JsonNode> buildNestedTable(Function<JsonNode, JsonNode> extractor,
                                                 int cardinality) {
        if (isFold()) {
            return fold.buildNestedTable(n -> {
                JsonNode resolved = extractor.apply(n);
                return resolved == null ? null : resolved.get(field);
            }, averageCardinality);
        }

        TableViewWithVisibleRowCount<JsonNode> table = new TableViewWithVisibleRowCount<>();
        ObservableList<TableColumn<JsonNode, ?>> columns = table.getColumns();
        SchemaNode last = children.isEmpty() ? null
                                             : children.get(children.size()
                                                            - 1);
        children.forEach(node -> {
            columns.add(node.buildTableColumn(extractor, averageCardinality,
                                              last == node));
        });
        table.setPlaceholder(new Text());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.visibleRowCountProperty()
             .set(cardinality);
        table.widthProperty()
             .addListener(new ChangeListener<Number>() {
                 @Override
                 public void changed(ObservableValue<? extends Number> ov,
                                     Number t, Number t1) {
                     // Get the table header
                     Pane header = (Pane) table.lookup("TableHeaderRow");
                     if (header != null && header.isVisible()) {
                         header.setMaxHeight(0);
                         header.setMinHeight(0);
                         header.setPrefHeight(0);
                         header.setVisible(false);
                         header.setManaged(false);
                         header.getChildren()
                               .clear();
                     }
                 }
             });
        return table;
    }

    private ListView<JsonNode> buildOutline(Function<JsonNode, JsonNode> extractor,
                                            int cardinality) {
        if (isFold()) {
            return fold.buildOutline(n -> {
                JsonNode resolved = extractor.apply(n);
                return resolved == null ? null : resolved.get(field);
            }, averageCardinality);
        }
        ListViewWithVisibleRowCount<JsonNode> list = new ListViewWithVisibleRowCount<>();
        list.setCellFactory(c -> new ListCell<JsonNode>() {
            HBox                        cell     = new HBox(2);
            Map<SchemaNode, NodeMaster> controls = new HashMap<>();
            {
                cell.getChildren()
                    .add(new Text(""));
                VBox box = new VBox(2);
                children.forEach(child -> {
                    NodeMaster master = child.outlineElement(outlineLabelWidth,
                                                             extractor,
                                                             averageCardinality);
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
                    controls.get(child).items.accept(item);
                });
                super.setGraphic(cell);
            }
        });
        list.setPrefWidth(justifiedWidth);
        list.visibleRowCountProperty()
            .set(cardinality);
        list.getProperties()
            .put("deferToParentPrefWidth", Boolean.TRUE);
        return list;
    }

    /**
     * Builds the top level nested table
     */
    private Control buildTable(Function<JsonNode, JsonNode> extractor,
                               int cardinality) {
        if (isFold()) {
            return fold.buildTable(n -> {
                JsonNode resolved = extractor.apply(n);
                return resolved == null ? null : resolved.get(field);
            }, averageCardinality);
        }
        TableViewWithVisibleRowCount<String> header = new TableViewWithVisibleRowCount<>();
        header.setFixedCellSize(0);
        header.visibleRowCountProperty()
              .set(0);
        header.setPlaceholder(new Text());
        header.getColumns()
              .add(buildHeader());
        header.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        header.setPrefWidth(justifiedWidth);
        TableView<JsonNode> table = buildNestedTable(extractor, cardinality);
        return new NestedTableView<JsonNode>(header, table);
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
            float available = width - outlineLabelWidth;
            float tableWidth = width;
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
