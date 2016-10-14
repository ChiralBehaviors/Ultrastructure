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

import com.chiralbehaviors.graphql.layout.NestedColumnView;
import com.chiralbehaviors.graphql.layout.NestedTableRow;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import graphql.language.Definition;
import graphql.language.Field;
import graphql.language.FragmentSpread;
import graphql.language.InlineFragment;
import graphql.language.OperationDefinition;
import graphql.language.OperationDefinition.Operation;
import graphql.language.Selection;
import graphql.parser.Parser;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Border;
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
    private Insets                 listCellInsets     = ZERO_INSETS;
    private Insets                 listInsets         = ZERO_INSETS;
    private double                 outlineLabelWidth  = 0;
    private Insets                 tableCellInsets    = ZERO_INSETS;
    private boolean                useTable           = false;

    public Relation(String label) {
        super(label);
    }

    public void addChild(SchemaNode child) {
        children.add(child);
        outlineLabelWidth = Math.max(child.labelWidth(), outlineLabelWidth);
    }

    public void autoLayout(double width) {
        layout(width);
        justify(width);
    }

    public Control buildControl() {
        if (isFold()) {
            return fold.buildControl();
        }
        return useTable ? buildTable(n -> n, -1) : buildOutline(n -> n, -1);
    }

    public JsonNode extractFrom(JsonNode jsonNode) {
        if (isFold()) {
            return fold.extractFrom(super.extractFrom(jsonNode));
        }
        return super.extractFrom(jsonNode);
    }

    public int getAverageCardinality() {
        return averageCardinality;
    }

    public List<SchemaNode> getChildren() {
        return children;
    }

    @Override
    public double getTableColumnWidth() {
        if (isFold()) {
            return fold.getTableColumnWidth();
        }
        return super.getTableColumnWidth() + tableCellInsets.getLeft()
               + tableCellInsets.getRight();
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

    public void measure(JsonNode jsonNode, List<String> styleSheets) {
        if (jsonNode.isArray()) {
            ArrayNode array = (ArrayNode) jsonNode;
            measure(array, styleSheets);
        } else {
            ArrayNode singleton = JsonNodeFactory.instance.arrayNode();
            singleton.add(jsonNode);
            measure(singleton, styleSheets);
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

    @Override
    public void setItems(Control control, JsonNode data) {
        if (data == null) {
            data = JsonNodeFactory.instance.arrayNode();
        }
        if (isFold()) {
            fold.setItems(control, flatten(data));
        } else {
            super.setItems(control, data);
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
                                 justifiedWidth, getTableColumnWidth(),
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
    TableColumn<JsonNode, JsonNode> buildTableColumn(int cardinality,
                                                     NestingFunction nesting) {
        if (isFold()) {
            return fold.buildTableColumn(cardinality, nesting);
        }

        TableColumn<JsonNode, JsonNode> column = new TableColumn<>(label);
        column.getStyleClass()
              .add(tableColumnStyleClass());
        column.setPrefWidth(justifiedWidth);

        Map<Primitive, Integer> leaves = new HashMap<>();
        int index = 0;
        for (Primitive leaf : gatherLeaves()) {
            leaves.put(leaf, index);
            index++;
        }
        for (SchemaNode child : children) {
            column.getColumns()
                  .add(child.buildTableColumn(averageCardinality,
                                              nest(child, leaves, nesting,
                                                   cardinality)));
        }

        return column;
    }

    List<Primitive> gatherLeaves() {
        List<Primitive> leaves = new ArrayList<>();
        for (SchemaNode child : children) {
            leaves.addAll(child.gatherLeaves());
        }
        return leaves;
    }

    @Override
    Function<JsonNode, JsonNode> getFoldExtractor(Function<JsonNode, JsonNode> extractor) {
        if (isFold()) {
            return fold.getFoldExtractor(extract(extractor));
        }
        return super.getFoldExtractor(extractor);
    }

    double getHeight(int cardinality) {
        return children.stream()
                       .map(child -> child.getHeight(averageCardinality))
                       .reduce((a, b) -> a + b)
                       .get();
    }

    @Override
    void justify(double width) {
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
    double layout(double width) {
        if (isFold()) {
            return fold.layout(width);
        }
        useTable = false;
        double available = width - outlineLabelWidth;
        double outlineWidth = children.stream()
                                      .map(child -> child.layout(available))
                                      .max((a, b) -> Double.compare(a, b))
                                      .get();
        outlineWidth += outlineLabelWidth + listCellInsets.getLeft()
                        + listCellInsets.getRight();
        if (getTableColumnWidth() <= outlineWidth) {
            nestTables();
            return getTableColumnWidth();
        }
        return outlineWidth;
    }

    @Override
    double measure(ArrayNode data, List<String> styleSheets) {
        if (isFold()) {
            return fold.measure(flatten(data), styleSheets);
        }
        if (data.isNull() || children.size() == 0) {
            return 0;
        }
        measure(styleSheets);

        setTableColumnWidth(0);
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
            setTableColumnWidth(getTableColumnWidth()
                                + child.measure(aggregate, styleSheets));
            variableLength |= child.isVariableLength();
        }
        averageCardinality = Math.max(1, Math.round(sum / children.size()));
        setTableColumnWidth(Math.max(labelWidth(), getTableColumnWidth()));
        justifiedWidth = getTableColumnWidth();
        return getTableColumnWidth() + tableCellInsets.getLeft()
               + tableCellInsets.getRight();
    }

    @Override
    NodeMaster outlineElement(double labelWidth,
                              Function<JsonNode, JsonNode> extractor,
                              int cardinality) {
        if (isFold()) {
            return fold.outlineElement(labelWidth, extract(extractor),
                                       averageCardinality);
        }
        Control control = useTable ? buildTable(n -> n, cardinality)
                                   : buildOutline(n -> n, cardinality);
        Parent element;
        TextArea labelText = new TextArea(label);
        labelText.getStyleClass()
                 .add(outlineLabelStyleClass());
        labelText.setWrapText(true);
        labelText.setMinWidth(labelWidth);
        labelText.setPrefWidth(labelWidth);
        labelText.setPrefHeight(FONT_LOADER.getFontMetrics(labelFont)
                                           .getLineHeight());
        double calculatedHeight = getHeight(averageCardinality);
        Pane box;
        if (useTable) {
            box = new HBox();
            control.setPrefWidth(justifiedWidth);
        } else {
            box = new VBox();
        }
        box.getChildren()
           .add(labelText);
        //        box.setMinWidth(justifiedWidth);
        box.getChildren()
           .add(control);
        element = box;

        return new NodeMaster(item -> {
            if (item == null) {
                return;
            }
            JsonNode extracted = extractor.apply(item);
            JsonNode extractedField = extracted == null ? null
                                                        : extracted.get(field);
            setItems(control, extractedField);
        }, element, calculatedHeight);
    }

    private ListView<JsonNode> buildOutline(Function<JsonNode, JsonNode> extractor,
                                            int cardinality) {
        if (isFold()) {
            return fold.buildOutline(extract(extractor), averageCardinality);
        }
        ListView<JsonNode> list = new ListView<>();
        list.getStyleClass()
            .add(outlineListStyleClass());
        list.setCellFactory(c -> new ListCell<JsonNode>() {
            HBox                        cell     = new HBox();
            Map<SchemaNode, NodeMaster> controls = new HashMap<>();
            {
                cell.setMinWidth(0);
                cell.setPrefWidth(1);
            }
            {
                VBox box = new VBox();
                box.setPrefWidth(justifiedWidth);
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
        list.setMinWidth(0);
        list.setPrefWidth(1);
        list.setPlaceholder(new Text());
        return list;
    }

    /**
     * Builds the top level nested table
     */
    private Control buildTable(Function<JsonNode, JsonNode> extractor,
                               int cardinality) {
        if (isFold()) {
            return fold.buildTable(extract(extractor), averageCardinality);
        }

        TableView<JsonNode> table = new TableView<>();
        table.getStyleClass()
             .add(tableViewStyleClass());
        table.setRowFactory(tableView -> new NestedTableRow<JsonNode>());
        children.forEach(child -> {
            table.getColumns()
                 .add(child.buildTableColumn(averageCardinality,
                                             (p, height, row, primitive) -> {
                                                 NestedColumnView view = new NestedColumnView();
                                                 view.manifest(child,
                                                               p.apply(0));
                                                 return view;
                                             }));
        });
        table.setPlaceholder(new Text());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setMinWidth(0);
        table.setPrefWidth(1);
        return table;
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

    private void justifyOutline(double width) {
        if (variableLength) {
            justifiedWidth = width;
            double available = width - outlineLabelWidth;
            double tableWidth = width;
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

    private void justifyTable(double width) {
        justifiedWidth = width;
        double slack = width - getTableColumnWidth();
        assert slack >= 0 : String.format("Negative slack: %.2f (%.2f) \n%s",
                                          slack, width, this);
        double total = children.stream()
                               .filter(child -> child.isVariableLength())
                               .map(child -> child.getTableColumnWidth())
                               .reduce((a, b) -> a + b)
                               .orElse(0.0d);
        children.stream()
                .filter(child -> child.isVariableLength())
                .forEach(child -> child.justify(slack
                                                * (child.getTableColumnWidth()
                                                   / total)
                                                + child.getTableColumnWidth()));
    }

    private void measure(List<String> styleSheets) {
        TableView<String> table = new TableView<>();
        table.getStyleClass()
             .add(tableViewStyleClass());
        TableRow<String> row = new TableRow<String>();
        TableColumn<String, String> column = new TableColumn<>(label);
        column.getStyleClass()
              .add(tableColumnStyleClass());
        table.getColumns()
             .add(column);
        TableCell<String, String> tableCell = new TableCell<>();
        tableCell.updateTableColumn(column);
        tableCell.updateTableRow(row);
        row.updateTableView(table);
        ListView<String> listView = new ListView<>();
        listView.getStyleClass()
                .add(outlineListStyleClass());
        ListCell<String> listCell = new ListCell<>();
        listCell.getStyleClass()
                .add(outlineListCellClass());
        listCell.updateListView(listView);
        Group root = new Group(table, listView);
        Scene scene = new Scene(root);
        if (styleSheets != null) {
            scene.getStylesheets()
                 .addAll(styleSheets);
        }
        root.applyCss();
        root.layout();
        table.applyCss();
        table.layout();
        row.applyCss();
        row.layout();
        tableCell.applyCss();
        tableCell.layout();
        listView.applyCss();
        listView.layout();
        listCell.applyCss();
        listCell.layout();
        Border border = listCell.getBorder();
        listCellInsets = add(border == null ? ZERO_INSETS : border.getOutsets(),
                             add(border == null ? ZERO_INSETS
                                                : border.getInsets(),
                                 add(listCell.getInsets(),
                                     listCell.getBackground() == null ? ZERO_INSETS
                                                                      : listCell.getBackground()
                                                                                .getOutsets())));
        border = listView.getBorder();
        listInsets = add(border == null ? ZERO_INSETS : border.getOutsets(),
                         add(border == null ? ZERO_INSETS : border.getInsets(),
                             add(listView.getInsets(),
                                 listView.getBackground() == null ? ZERO_INSETS
                                                                  : listView.getBackground()
                                                                            .getOutsets())));
        border = tableCell.getBorder();
        tableCellInsets = add(border == null ? ZERO_INSETS
                                             : border.getOutsets(),
                              add(border == null ? ZERO_INSETS
                                                 : border.getInsets(),
                                  add(tableCell.getInsets(),
                                      tableCell.getBackground() == null ? ZERO_INSETS
                                                                        : tableCell.getBackground()
                                                                                   .getOutsets())));
    }

    private NestingFunction nest(SchemaNode child,
                                 Map<Primitive, Integer> leaves,
                                 NestingFunction nesting, int cardinality) {
        return (p, height, row, primitive) -> {
            double extendedHeight = height + listCellInsets.getBottom()
                                    + listCellInsets.getTop();
            return nesting.apply(index -> {
                ListView<JsonNode> split = split(row, primitive, leaves,
                                                 cardinality, extendedHeight,
                                                 index);
                split.setCellFactory(c -> new ListCell<JsonNode>() {
                    {
                        getStyleClass().add(nestedListCellClass());
                    }

                    @Override
                    protected void updateItem(JsonNode item, boolean empty) {
                        if (item == getItem()) {
                            return;
                        }
                        super.updateItem(item, empty);
                        super.setText(null);
                        if (empty || item == null) {
                            setGraphic(null);
                            return;
                        }
                        Control childControl = p.apply(getIndex());
                        setGraphic(childControl);
                        JsonNode extracted = child.extractFrom(item);
                        setItemsOf(childControl, extracted);
                    }
                });
                return split;
            }, (cardinality * extendedHeight) + listInsets.getBottom()
               + listInsets.getTop(), row, primitive);
        };
    }

    private String nestedListCellClass() {
        return String.format("%s-nest-list-cell", field);
    }

    private String nestedListScrollStyleClass() {
        return String.format("%s-nested-list-scroll", field);
    }

    private String nestedListStyleClass() {
        return String.format("%s-nested-list", field);
    }

    private void nestTables() {
        useTable = true;
        children.forEach(child -> {
            if (child.isRelation()) {
                ((Relation) child).nestTables();
            }
        });
    }

    private String outlineListStyleClass() {
        return String.format("%s-outline-list", field);
    }

    private ListView<JsonNode> split(NestedTableRow<JsonNode> row,
                                     Primitive primitive,
                                     Map<Primitive, Integer> leaves,
                                     int cardinality, double height,
                                     Integer index) {
        Integer column = leaves.get(primitive);
        ListView<JsonNode> content = new ListView<JsonNode>() {
            boolean registered = false;

            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                if (!registered) {
                    registered = true;
                    row.register(index, Relation.this, column, this,
                                 cardinality, height, leaves.size());
                }
            }
        };
        content.getStyleClass()
               .add(nestedListStyleClass());
        if (column != leaves.size() - 1) {
            content.getStylesheets()
                   .add(getClass().getResource("hide-scrollbar.css")
                                  .toExternalForm());
        } else {
            content.getStyleClass()
                   .add(nestedListScrollStyleClass());
        }
        content.setPlaceholder(new Text());
        content.setMinWidth(0);
        content.setPrefWidth(1);
        return content;
    }

    private String tableViewStyleClass() {
        return String.format("%s-table", field);
    }
}
