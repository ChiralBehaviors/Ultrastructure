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
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.javafx.collections.ObservableListWrapper;

import javafx.beans.binding.ObjectBinding;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * @author hhildebrand
 *
 */
public class Relation extends SchemaNode implements Cloneable {
    private int                    averageCardinality;
    private final List<SchemaNode> children          = new ArrayList<>();
    private RelationConstraints    constraints;
    private float                  outlineLabelWidth = 0;
    private boolean                useTable          = false;

    public Relation(String label) {
        super(label);
    }

    public void addChild(SchemaNode child) {
        children.add(child);
        outlineLabelWidth = Math.max(child.labelWidth(), outlineLabelWidth);
    }

    public Control buildControl() {
        return useTable ? buildNestedTable() : buildOutline();
    }

    public int getAverageCardinality() {
        return averageCardinality;
    }

    public List<SchemaNode> getChildren() {
        return children;
    }

    public RelationConstraints getConstraints() {
        return constraints;
    }

    public float getOutlineLabelWidth() {
        return outlineLabelWidth;
    }

    public boolean isUseTable() {
        return useTable;
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

    public void setConstraints(RelationConstraints constraints) {
        this.constraints = constraints;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    @Override
    public String toString(int indent) {
        StringBuffer buf = new StringBuffer();
        buf.append(String.format("Relation [%s:%s x %s]", getLabel(),
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
    protected TableColumn<ObjectNode, ?> buildTableColumn() {
        TableColumn<ObjectNode, List<ObjectNode>> column = new TableColumn<>(label);
        column.setPrefWidth(tableColumnWidth);
        column.setCellValueFactory(cellData -> new ObjectBinding<List<ObjectNode>>() {
            @Override
            protected List<ObjectNode> computeValue() {
                return asList(cellData.getValue()
                                      .get(field));
            }

        });
        column.setCellFactory(c -> new TableCell<ObjectNode, List<ObjectNode>>() {
            @Override
            protected void updateItem(List<ObjectNode> item, boolean empty) {
                if (item == getItem())
                    return;

                TableView<ObjectNode> table = buildNestedTable();
                table.setItems(new ObservableListWrapper<>(item));

                super.updateItem(item, empty);
                super.setText(null);
                super.setGraphic(table);
            }
        });
        return column;
    }

    @Override
    protected void measure(ArrayNode data) {
        if (data.isNull()) {
            return;
        }
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
            sum += data.size() == 0 ? 0 : cardSum / data.size();
            child.measure(aggregate);
            tableColumnWidth += child.tableColumnWidth;
        }
        averageCardinality = Math.max(1, sum / children.size());
    }

    private List<ObjectNode> asList(JsonNode jsonNode) {
        List<ObjectNode> nodes = new ArrayList<>();
        if (jsonNode.isArray()) {
            jsonNode.forEach(node -> nodes.add((ObjectNode) node));
        } else {
            return Collections.singletonList((ObjectNode) jsonNode);
        }
        return nodes;
    }

    private TableView<ObjectNode> buildNestedTable() {
        TableView<ObjectNode> table = new TableView<>();
        children.forEach(node -> {
            node.buildTableColumn();
        });
        return table;
    }

    private ListView<ObjectNode> buildOutline() {
        // TODO Auto-generated method stub
        return null;
    }
}
