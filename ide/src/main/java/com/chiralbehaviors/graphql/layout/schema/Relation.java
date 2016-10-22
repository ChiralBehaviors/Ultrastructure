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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.glassfish.jersey.internal.util.Producer;

import com.chiralbehaviors.graphql.layout.NestedColumnView;
import com.chiralbehaviors.graphql.layout.NestedTableRow;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.util.concurrent.AtomicDouble;

import graphql.language.Definition;
import graphql.language.Field;
import graphql.language.FragmentSpread;
import graphql.language.InlineFragment;
import graphql.language.OperationDefinition;
import graphql.language.OperationDefinition.Operation;
import graphql.language.Selection;
import graphql.parser.Parser;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;

/**
 * @author hhildebrand
 *
 */
public class Relation extends SchemaNode implements Cloneable {

    private static final String ZERO = "0";

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
    private double                 tableColumnWidth   = 0;
    private boolean                useTable           = false;

    public Relation(String label) {
        super(label);
    }

    public void addChild(SchemaNode child) {
        children.add(child);
    }

    public void autoLayout(double width) {
        layout(width);
        justify(width);
    }

    public Control buildControl(Layout layout) {
        if (isFold()) {
            return fold.buildControl(layout);
        }
        return useTable ? buildTable(n -> n, -1, layout)
                        : buildOutline(n -> n, -1, layout);
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
        return tableColumnWidth;
    }

    @JsonProperty
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

    public void measure(JsonNode jsonNode, Layout layout) {
        if (jsonNode.isArray()) {
            ArrayNode array = (ArrayNode) jsonNode;
            measure(array, 0, layout);
        } else {
            ArrayNode singleton = JsonNodeFactory.instance.arrayNode();
            singleton.add(jsonNode);
            measure(singleton, 0, layout);
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
    double buildTableColumn(boolean topLevel, int cardinality,
                            NestingFunction nesting, Layout layout,
                            ObservableList<TableColumn<JsonNode, ?>> parent,
                            int columnIndex) {
        if (isFold()) {
            return fold.buildTableColumn(topLevel, averageCardinality, nesting,
                                         layout, parent, columnIndex);
        }

        TableColumn<JsonNode, JsonNode> column = new TableColumn<>(label);
        parent.add(column);
        column.getStyleClass()
              .add(tableColumnStyleClass());
        column.setPrefWidth(justifiedWidth);
        column.setMinWidth(justifiedWidth);

        Map<Primitive, Integer> leaves = new HashMap<>();
        int index = 0;
        for (Primitive leaf : gatherLeaves()) {
            leaves.put(leaf, index);
            index++;
        }
        AtomicDouble childHeight = new AtomicDouble();
        Producer<Double> childHeightF = () -> childHeight.get();
        AtomicDouble extendedHeight = new AtomicDouble();
        AtomicInteger childColumnIndex = new AtomicInteger(0);
        childHeight.set(children.stream()
                                .mapToDouble(child -> child.buildTableColumn(false,
                                                                             averageCardinality,
                                                                             nest(child,
                                                                                  leaves,
                                                                                  nesting,
                                                                                  childHeightF,
                                                                                  layout,
                                                                                  cardinality,
                                                                                  extendedHeight,
                                                                                  childColumnIndex.get()),
                                                                             layout,
                                                                             column.getColumns(),
                                                                             childColumnIndex.getAndIncrement()))
                                .max()
                                .getAsDouble());
        extendedHeight.set(nestedHeight(layout, cardinality,
                                        childHeight.get()));
        return extendedHeight.get();

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

    double getLabelWidth() {
        if (isFold()) {
            return fold.getLabelWidth();
        }
        return labelWidth;
    }

    double getValueHeight(int cardinality, Layout layout) {
        return useTable ? buildTable(n -> n, cardinality, layout)
                                                                 .getPrefHeight()
                        : (children.stream()
                                   .mapToDouble(child -> child.getValueHeight(averageCardinality,
                                                                              layout))
                                   .reduce((a, b) -> a + b)
                                   .getAsDouble()
                           * cardinality)
                          + layout.getOutlineListInsets()
                                  .getTop()
                          + layout.getOutlineListInsets()
                                  .getBottom()
                          + (layout.getLabelLineHeight() * 2);
    }

    @Override
    boolean isJusifiable() {
        if (isFold()) {
            return fold.isJusifiable();
        }
        return children.stream()
                       .map(child -> child.isJusifiable())
                       .reduce((a, b) -> a & b)
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
        double available = width - children.stream()
                                           .mapToDouble(child -> child.getLabelWidth())
                                           .max()
                                           .getAsDouble();
        double outlineWidth = children.stream()
                                      .mapToDouble(child -> child.layout(available))
                                      .max()
                                      .getAsDouble();
        if (getTableColumnWidth() <= outlineWidth) {
            nestTables();
            return getTableColumnWidth();
        }
        return outlineWidth;
    }

    @Override
    double measure(ArrayNode data, int nestingLevel, Layout layout) {
        if (isFold()) {
            return fold.measure(flatten(data), nestingLevel, layout);
        }
        if (data.isNull() || children.size() == 0) {
            return 0;
        }
        labelWidth = layout.labelWidth(label);
        labelWidth += layout.getLabelInsets()
                            .getLeft()
                      + layout.getLabelInsets()
                              .getRight();
        int sum = 0;
        Insets listCellInsets = layout.getNestedListCellInsets();
        double columnWidth = listCellInsets.getLeft()
                             + listCellInsets.getRight();
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
            columnWidth += child.measure(aggregate, nestingLevel + 1, layout);
        }
        averageCardinality = Math.max(1, Math.round(sum / children.size()));
        tableColumnWidth = Math.max(labelWidth, columnWidth);
        justifiedWidth = getTableColumnWidth();
        return getTableColumnWidth();
    }

    @Override
    Pair<Consumer<JsonNode>, Parent> outlineElement(double labelWidth,
                                                    Function<JsonNode, JsonNode> extractor,
                                                    int cardinality,
                                                    Layout layout) {
        if (isFold()) {
            return fold.outlineElement(labelWidth, extract(extractor),
                                       averageCardinality, layout);
        }
        Control control = useTable ? buildTable(n -> n, cardinality, layout)
                                   : buildOutline(n -> n, cardinality, layout);
        Parent element;
        TextArea labelText = new TextArea(label);
        labelText.getStyleClass()
                 .add(outlineLabelStyleClass());
        labelText.setWrapText(true);
        labelText.setPrefColumnCount(1);
        labelText.setMinWidth(labelWidth);
        labelText.setPrefWidth(labelWidth);
        double labelHeight = 2 * layout.getLabelLineHeight();
        labelText.setPrefHeight(labelHeight);
        Pane box;
        if (useTable) {
            box = new HBox();
            control.setPrefWidth(justifiedWidth);
        } else {
            box = new VBox();
            VBox.setVgrow(labelText, Priority.NEVER);
            VBox.setVgrow(control, Priority.ALWAYS);
        }
        box.getChildren()
           .add(labelText);
        box.getChildren()
           .add(control);
        element = box;

        return new Pair<>(item -> {
            if (item == null) {
                return;
            }
            JsonNode extracted = extractor.apply(item);
            JsonNode extractedField = extracted == null ? null
                                                        : extracted.get(field);
            setItems(control, extractedField);
        }, element);
    }

    private ListView<JsonNode> buildOutline(Function<JsonNode, JsonNode> extractor,
                                            int cardinality, Layout layout) {
        if (isFold()) {
            return fold.buildOutline(extract(extractor), averageCardinality,
                                     layout);
        }

        double outlineLabelWidth = children.stream()
                                           .mapToDouble(child -> child.getLabelWidth())
                                           .max()
                                           .getAsDouble();
        ListView<JsonNode> list = new ListView<>();
        list.setPrefHeight(getValueHeight(cardinality, layout));
        list.getStyleClass()
            .add(outlineListStyleClass());
        list.setCellFactory(c -> outlineListCell(outlineLabelWidth, extractor,
                                                 layout));
        list.setMinWidth(0);
        list.setPrefWidth(1);
        list.setPlaceholder(new Text());
        return list;
    }

    /**
     * Builds the top level nested table
     */
    private Control buildTable(Function<JsonNode, JsonNode> extractor,
                               int cardinality, Layout layout) {
        if (isFold()) {
            return fold.buildTable(extract(extractor), averageCardinality,
                                   layout);
        }

        TableView<JsonNode> table = new TableView<>();
        table.getStyleClass()
             .add(tableStyleClass());
        table.setRowFactory(tableView -> new NestedTableRow<JsonNode>());
        Map<Primitive, Integer> leaves = new HashMap<>();
        int index = 0;
        for (Primitive leaf : gatherLeaves()) {
            leaves.put(leaf, index);
            index++;
        }

        AtomicDouble childHeight = new AtomicDouble();
        childHeight.set(children.stream()
                                .mapToDouble(child -> child.buildTableColumn(true,
                                                                             averageCardinality,
                                                                             (p,
                                                                              row, primitive) -> {
                                                                                 Double height = childHeight.get();
                                                                                 NestedColumnView view = new NestedColumnView();
                                                                                 view.manifest(child,
                                                                                               p.apply(() -> ZERO,
                                                                                                       () -> height));
                                                                                 return view;
                                                                             },
                                                                             layout,
                                                                             table.getColumns(),
                                                                             0))
                                .max()
                                .getAsDouble());
        table.setPlaceholder(new Text());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefWidth(justifiedWidth);
        Double prefHeight = -1d;
        if (cardinality > 0) {
            double headerHeight = layout.measureHeader(table);
            double tableCellInset = layout.getTableCellInsets()
                                          .getTop()
                                    + layout.getTableCellInsets()
                                            .getBottom();
            double tableInset = layout.getTableInsets()
                                      .getTop()
                                + layout.getTableInsets()
                                        .getBottom();
            prefHeight = (cardinality * (childHeight.get() + tableCellInset))
                         + headerHeight + tableInset;
            table.setPrefHeight(prefHeight);
        }
        return table;
    }

    private ListCell<JsonNode> createListCell(SchemaNode child,
                                              BiFunction<Producer<String>, Producer<Double>, Control> p,
                                              Producer<Double> childHeight) {
        return new ListCell<JsonNode>() {
            Control childControl;
            {
                getStyleClass().add(nestedListCellClass());
                emptyProperty().addListener((obs, wasEmpty, isEmpty) -> {
                    if (isEmpty) {
                        setGraphic(null);
                    } else {
                        setGraphic(childControl);
                    }
                });
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setAlignment(Pos.CENTER);
            }

            @Override
            public void updateIndex(int i) {
                int oldIndex = getIndex();
                if (i != oldIndex) {
                    if (i < 0) {
                        setGraphic(null);
                        childControl = null;
                    } else {
                        childControl = p.apply(() -> String.format("%s.%s",
                                                                   label, i),
                                               childHeight);
                        setGraphic(childControl);
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
                if (empty || item == null) {
                    return;
                }
                setItemsOf(childControl, child.extractFrom(item));
            }
        };
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
        if (isJusifiable()) {
            double outlineLabelWidth = children.stream()
                                               .mapToDouble(child -> child.getLabelWidth())
                                               .max()
                                               .getAsDouble();
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
                               .filter(child -> child.isJusifiable())
                               .map(child -> child.getTableColumnWidth())
                               .reduce((a, b) -> a + b)
                               .orElse(0.0d);
        children.stream()
                .filter(child -> child.isJusifiable())
                .forEach(child -> child.justify(slack
                                                * (child.getTableColumnWidth()
                                                   / total)
                                                + child.getTableColumnWidth()));
    }

    private NestingFunction nest(SchemaNode child,
                                 Map<Primitive, Integer> leaves,
                                 NestingFunction nesting,
                                 Producer<Double> childHeightF, Layout layout,
                                 int cardinality, AtomicDouble calcHeight,
                                 int columnIndex) {

        return (p, row, primitive) -> {
            return nesting.apply((id, renderedF) -> {
                final double renderedHeight = renderedF.call();
                final double childHeight = childHeightF.call();

                final double listInset = layout.getNestedListInsets()
                                               .getTop()
                                         + layout.getNestedListInsets()
                                                 .getBottom();
                final double listCellInset = layout.getNestedListCellInsets()
                                                   .getTop()
                                             + layout.getNestedListCellInsets()
                                                     .getBottom();
                final double deficit = Math.max(0, renderedHeight
                                                   - calcHeight.get());

                final double childDeficit = Math.max(0, deficit / cardinality);
                final double childLayoutHeight = childHeight + childDeficit
                                                 - listInset;

                Integer primitiveColumn = leaves.get(primitive);
                String label = id.call();

                ListView<JsonNode> split = split(columnIndex == 0, label,
                                                 primitiveColumn, row,
                                                 leaves.size());
                split.setMinHeight(renderedHeight);
                split.setPrefHeight(renderedHeight);
                split.setMaxHeight(renderedHeight);
                split.setFixedCellSize(childLayoutHeight + listCellInset);
                split.setCellFactory(c -> createListCell(child, p,
                                                         () -> childLayoutHeight));
                return split;
            }, row, primitive);
        };
    }

    private double nestedHeight(Layout layout, int cardinality,
                                double childHeight) {
        double listCellInset = layout.getNestedListCellInsets()
                                     .getTop()
                               + layout.getNestedListCellInsets()
                                       .getBottom();
        double listInset = layout.getNestedListInsets()
                                 .getTop()
                           + layout.getNestedListInsets()
                                   .getBottom();
        return ((childHeight + listCellInset) * cardinality) + listInset;
    }

    private void nestTables() {
        useTable = true;
        children.forEach(child -> {
            if (child.isRelation()) {
                ((Relation) child).nestTables();
            }
        });
    }

    private ListCell<JsonNode> outlineListCell(double outlineLabelWidth,
                                               Function<JsonNode, JsonNode> extractor,
                                               Layout layout) {
        return new ListCell<JsonNode>() {
            HBox                                              cell     = new HBox();
            Map<SchemaNode, Pair<Consumer<JsonNode>, Parent>> controls = new HashMap<>();
            {
                cell.setMinWidth(0);
                cell.setPrefWidth(1);
            }
            {
                VBox box = new VBox();
                box.setPrefWidth(justifiedWidth);
                children.forEach(child -> {
                    Pair<Consumer<JsonNode>, Parent> master = child.outlineElement(outlineLabelWidth,
                                                                                   extractor,
                                                                                   averageCardinality,
                                                                                   layout);
                    controls.put(child, master);
                    box.getChildren()
                       .add(master.getValue());
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
                    controls.get(child)
                            .getKey()
                            .accept(item);
                });
                super.setGraphic(cell);
            }
        };
    }

    private ListView<JsonNode> split(boolean frist, String id,
                                     Integer primitiveColumn,
                                     NestedTableRow<JsonNode> row, int count) {
        ListView<JsonNode> content = new ListView<JsonNode>() {

            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                if (count > 1) {
                    row.link(id, primitiveColumn, this, count);
                }
            }

        };
        ObservableList<String> styleClass = content.getStyleClass();

        if (frist) {
            styleClass.add(nested1stListClass());
            content.getStylesheets()
                   .add(getClass().getResource("hide-scrollbar.css")
                                  .toExternalForm());
        } else if (!primitiveColumn.equals(count - 1)) {
            content.getStylesheets()
                   .add(getClass().getResource("hide-scrollbar.css")
                                  .toExternalForm());
        } else {
            styleClass.add(nestedListScrollStyleClass());
        }
        styleClass.add(nestedListClass());
        content.setPlaceholder(new Text());
        content.setMinWidth(0);
        content.setPrefWidth(1);
        return content;
    }
}
