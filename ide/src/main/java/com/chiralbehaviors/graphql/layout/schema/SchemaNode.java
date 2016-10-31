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
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.chiralbehaviors.graphql.layout.Layout;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.sun.javafx.collections.ObservableListWrapper;

import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.util.Pair;

/**
 * @author hhildebrand
 *
 */
@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE)
abstract public class SchemaNode {
    public static final String AUTO_LAYOUT_LABEL              = "auto-layout-label";
    public static final String AUTO_LAYOUT_NEST_KEY_LIST      = "auto-layout-nest-key-list";
    public static final String AUTO_LAYOUT_NEST_KEY_LIST_CELL = "auto-layout-nest-key-list-cell";
    public static final String AUTO_LAYOUT_NEST_LIST          = "auto-layout-nest-list";
    public static final String AUTO_LAYOUT_NEST_LIST_CELL     = "auto-layout-nest-list-cell";
    public static final String AUTO_LAYOUT_OUTLINE_LIST       = "auto-layout-outline-list";
    public static final String AUTO_LAYOUT_OUTLINE_LIST_CELL  = "auto-layout-outline-list-cell";
    public static final String AUTO_LAYOUT_TABLE              = "auto-layout-table";
    public static final String AUTO_LAYOUT_TABLE_CELL         = "auto-layout-table-cell";
    public static final String AUTO_LAYOUT_TABLE_COLUMN       = "auto-layout-table-column";
    public static final String AUTO_LAYOUT_TABLE_KEY_CELL     = "auto-layout-table-key-cell";
    public static final String AUTO_LAYOUT_VALUE              = "auto-layout-value";
    public static final String NO_SCROLL_LIST                 = "no-scroll-list";

    public static ArrayNode asArray(JsonNode node) {
        if (node == null) {
            return JsonNodeFactory.instance.arrayNode();
        }
        if (node.isArray()) {
            return (ArrayNode) node;
        }

        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        array.add(node);
        return array;
    }

    public static List<JsonNode> asList(JsonNode jsonNode) {
        List<JsonNode> nodes = new ArrayList<>();
        if (jsonNode == null) {
            return nodes;
        }
        if (jsonNode.isArray()) {
            jsonNode.forEach(node -> nodes.add(node));
        } else {
            return Collections.singletonList(jsonNode);
        }
        return nodes;
    }

    public static String asText(JsonNode node) {
        if (node == null) {
            return "";
        }
        boolean first = true;
        if (node.isArray()) {
            StringBuilder builder = new StringBuilder();
            for (JsonNode row : ((ArrayNode) node)) {
                if (first) {
                    first = false;
                } else {
                    builder.append('\n');
                }
                builder.append(row.asText());
            }
            return builder.toString();
        }
        return node.asText();
    }

    public static ArrayNode extractField(JsonNode node, String field) {
        if (node == null) {
            return JsonNodeFactory.instance.arrayNode();
        }
        if (!node.isArray()) {
            JsonNode resolved = node.get(field);
            if (resolved == null) {
                return JsonNodeFactory.instance.arrayNode();
            }
            if (resolved.isArray()) {
                return (ArrayNode) resolved;
            }
            ArrayNode array = JsonNodeFactory.instance.arrayNode();
            array.add(resolved);
            return array;
        }
        ArrayNode extracted = JsonNodeFactory.instance.arrayNode();
        node.forEach(element -> {
            JsonNode resolved = element.get(field);
            if (resolved.isArray()) {
                extracted.addAll((ArrayNode) resolved);
            } else {
                extracted.add(resolved);
            }
        });
        return extracted;
    }

    public static List<JsonNode> extractList(JsonNode jsonNode, String field) {
        List<JsonNode> nodes = new ArrayList<>();
        if (jsonNode == null) {
            return nodes;
        }
        if (jsonNode.isArray()) {
            jsonNode.forEach(node -> nodes.add(node.get(field)));
        } else {
            return Collections.singletonList(jsonNode);
        }
        return nodes;
    }

    public static double labelHeight(Layout layout) {
        return Math.min(43, Layout.snap(layout.getValueLineHeight() * 2)
                            + layout.getValueVerticalInset());
    }

    public static void setItemsOf(Control control, JsonNode data) {
        if (data == null) {
            data = JsonNodeFactory.instance.arrayNode();
        }
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
        } else if (control instanceof Label) {
            Label label = (Label) control;
            label.setText(asText(data));
        } else if (control instanceof TextArea) {
            TextArea label = (TextArea) control;
            label.setText(asText(data));
        } else {
            throw new IllegalArgumentException(String.format("Unknown control %s",
                                                             control));
        }
    }

    final String field;
    double       justifiedWidth = 0;
    String       label;

    public SchemaNode(String field) {
        this(field, field);
    }

    public SchemaNode(String field, String label) {
        this.label = label;
        this.field = field;
    }

    public JsonNode extractFrom(JsonNode jsonNode) {
        return extractField(jsonNode, field);
    }

    public String getField() {
        return field;
    }

    public String getLabel() {
        return label;
    }

    public boolean isRelation() {
        return false;
    }

    public void setItems(Control control, JsonNode data) {
        setItemsOf(control, data);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    abstract public String toString(int indent);

    TableColumn<JsonNode, JsonNode> buildColumn() {
        TableColumn<JsonNode, JsonNode> column = new TableColumn<>(label);
        column.getStyleClass()
              .add(AUTO_LAYOUT_TABLE_COLUMN);
        column.setPrefWidth(justifiedWidth);
        column.setMinWidth(justifiedWidth);
        column.setMaxWidth(justifiedWidth);
        return column;
    }

    abstract TableColumn<JsonNode, JsonNode> buildTableColumn(int cardinality,
                                                              NestingFunction nesting,
                                                              Layout layout,
                                                              boolean key);

    Function<JsonNode, JsonNode> extract(Function<JsonNode, JsonNode> extractor) {
        return n -> {
            JsonNode extracted = extractor.apply(n);
            return extracted == null ? null : extracted.get(field);
        };
    }

    abstract List<Primitive> gatherLeaves();

    Function<JsonNode, JsonNode> getFoldExtractor(Function<JsonNode, JsonNode> extractor) {
        return extract(extractor);
    }

    double getLabelWidth(Layout layout) {
        return layout.labelWidth(label);
    }

    abstract double getTableColumnWidth();

    abstract boolean isJusifiable();

    boolean isUseTable() {
        return false;
    }

    abstract void justify(int cardinality, double width, Layout layout);

    abstract double layout(double width, Layout layout);

    abstract double layoutOutline(int cardinality, Layout layout);

    abstract double layoutRow(int cardinality, Layout layout);

    abstract double measure(ArrayNode data, Layout layout, boolean key);

    abstract Pair<Consumer<JsonNode>, Parent> outlineElement(double labelWidth,
                                                             Function<JsonNode, JsonNode> extractor,
                                                             int cardinality,
                                                             Layout layout);

    String outlineLabelStyleClass() {
        return String.format("%s-outline-label", field);
    }
}
