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

import static com.chiralbehaviors.graphql.layout.schema.SchemaNode.labelStyleClass;
import static com.chiralbehaviors.graphql.layout.schema.SchemaNode.nestedListCellClass;
import static com.chiralbehaviors.graphql.layout.schema.SchemaNode.nestedListClass;
import static com.chiralbehaviors.graphql.layout.schema.SchemaNode.outlineListCellClass;
import static com.chiralbehaviors.graphql.layout.schema.SchemaNode.outlineListStyleClass;
import static com.chiralbehaviors.graphql.layout.schema.SchemaNode.tableCellClass;
import static com.chiralbehaviors.graphql.layout.schema.SchemaNode.tableColumnStyleClass;
import static com.chiralbehaviors.graphql.layout.schema.SchemaNode.tableStyleClass;
import static com.chiralbehaviors.graphql.layout.schema.SchemaNode.valueStyleClass;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;

public class Layout {
    private static FontLoader FONT_LOADER = Toolkit.getToolkit()
                                                   .getFontLoader();
    private static Insets     ZERO_INSETS = new Insets(0);

    public static Insets add(Insets a, Insets b) {
        return new Insets(a.getTop() + b.getTop(), a.getRight() + b.getRight(),
                          a.getBottom() + b.getBottom(),
                          a.getRight() + b.getRight());
    }

    public static double snap(double y) {
        return ((int) y) + .5;
    }

    private Font         labelFont               = Font.getDefault();
    private Insets       labelInsets             = ZERO_INSETS;
    private double       labelLineHeight         = 0;
    private LayoutModel  model;
    private Insets       nestedKeyListCellInsets = ZERO_INSETS;
    private Insets       nestedKeyListInsets     = ZERO_INSETS;
    private Insets       nestedListCellInsets    = ZERO_INSETS;
    private Insets       nestedListInsets        = ZERO_INSETS;
    private Insets       outlineListCellInsets   = ZERO_INSETS;
    private Insets       outlineListInsets       = ZERO_INSETS;
    private List<String> styleSheets;
    private Insets       tableCellInsets         = ZERO_INSETS;
    private Insets       tableInsets             = ZERO_INSETS;
    private Insets       tableKeyCellInsets      = ZERO_INSETS;
    private Font         valueFont               = Font.getDefault();
    private Insets       valueInsets             = ZERO_INSETS;
    private double       valueLineHeight         = 0;

    public Layout(List<String> styleSheets) {
        this(styleSheets, new LayoutModel() {
        });
    }

    public Layout(List<String> styleSheets, LayoutModel model) {
        this.model = model;
        this.styleSheets = styleSheets;
        AtomicReference<TextArea> labelText = new AtomicReference<>();
        AtomicReference<TextArea> valueText = new AtomicReference<>();
        AtomicReference<ListCell<String>> nestedListCell = new AtomicReference<>();
        AtomicReference<ListView<String>> nestedList = new AtomicReference<>();
        AtomicReference<ListCell<String>> outlineListCell = new AtomicReference<>();
        AtomicReference<ListCell<String>> nested1stListCell = new AtomicReference<>();
        AtomicReference<ListView<String>> nested1stList = new AtomicReference<>();

        ListView<String> topLevel = new ListView<>();
        topLevel.getStyleClass()
                .add(outlineListStyleClass());
        topLevel.setCellFactory(s -> new ListCell<String>() {
            {
                getStyleClass().add(outlineListCellClass());
                outlineListCell.set(this);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                ListView<String> nested = new ListView<String>();
                nested.getStyleClass()
                      .add(nestedListClass());
                nestedList.set(nested);
                nested.setCellFactory(v -> new ListCell<String>() {
                    {
                        nestedListCell.set(this);
                        getStyleClass().add(nestedListCellClass());
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        TextArea value = new TextArea("lorum ipsum");
                        value.getStyleClass()
                             .add(valueStyleClass());
                        valueText.set(value);
                        TextArea label = new TextArea("lorum ipsum");
                        label.getStyleClass()
                             .add(labelStyleClass());
                        labelText.set(value);
                        ListView<String> frist = new ListView<String>();
                        frist.getStyleClass()
                             .add(nestedListCellClass());
                        frist.setCellFactory(l -> new ListCell<String>() {
                            {
                                nested1stListCell.set(this);
                            }

                            @Override
                            protected void updateItem(String item,
                                                      boolean empty) {
                                super.updateItem(item, empty);
                                setText(item);
                            }
                        });
                        nested1stList.set(frist);
                        Group group = new Group(label, value, frist);
                        setGraphic(group);
                        ObservableList<String> listItems = frist.getItems();
                        listItems.add("Lorem ipsum");
                        frist.setItems(null);
                        frist.setItems(listItems);
                    }
                });
                setGraphic(nested);
                ObservableList<String> listItems = nested.getItems();
                listItems.add("Lorem ipsum");
                nested.setItems(null);
                nested.setItems(listItems);
            };
        });
        TableCell<String, String> tableCell = new TableCell<String, String>() {
            {
                getStyleClass().add(tableCellClass());
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
            }
        };
        TableView<String> table = new TableView<>();
        table.getStyleClass()
             .add(tableStyleClass());
        TableColumn<String, String> column = new TableColumn<>("");
        column.setCellFactory(c -> tableCell);
        column.getStyleClass()
              .add(tableColumnStyleClass());
        table.getColumns()
             .add(column);
        Group root = new Group(table, topLevel);
        Scene scene = new Scene(root);
        if (styleSheets != null) {
            scene.getStylesheets()
                 .addAll(styleSheets);
        }
        ObservableList<String> tableItems = table.getItems();
        tableItems.add("Lorem ipsum");
        table.setItems(null);
        table.setItems(tableItems);
        root.applyCss();
        root.layout();
        table.applyCss();
        table.layout();
        table.refresh();

        ObservableList<String> listItems = topLevel.getItems();
        listItems.add("Lorem ipsum");
        topLevel.setItems(null);
        topLevel.setItems(listItems);
        topLevel.applyCss();
        topLevel.layout();
        topLevel.refresh();

        valueFont = valueText.get()
                             .getFont();

        labelFont = valueFont;

        nestedListCellInsets = new Insets(nestedListCell.get()
                                                        .snappedTopInset(),
                                          nestedListCell.get()
                                                        .snappedRightInset(),
                                          nestedListCell.get()
                                                        .snappedBottomInset(),
                                          nestedListCell.get()
                                                        .snappedLeftInset());

        nestedListInsets = new Insets(nestedList.get()
                                                .snappedTopInset(),
                                      nestedList.get()
                                                .snappedRightInset(),
                                      nestedList.get()
                                                .snappedBottomInset(),
                                      nestedList.get()
                                                .snappedLeftInset());

        nestedKeyListCellInsets = new Insets(nested1stListCell.get()
                                                              .snappedTopInset(),
                                             nested1stListCell.get()
                                                              .snappedRightInset(),
                                             nested1stListCell.get()
                                                              .snappedBottomInset(),
                                             nested1stListCell.get()
                                                              .snappedLeftInset());

        nestedKeyListInsets = new Insets(nested1stList.get()
                                                      .snappedTopInset(),
                                         nested1stList.get()
                                                      .snappedRightInset(),
                                         nested1stList.get()
                                                      .snappedBottomInset(),
                                         nested1stList.get()
                                                      .snappedLeftInset());

        outlineListCellInsets = new Insets(outlineListCell.get()
                                                          .snappedTopInset(),
                                           outlineListCell.get()
                                                          .snappedRightInset(),
                                           outlineListCell.get()
                                                          .snappedBottomInset(),
                                           outlineListCell.get()
                                                          .snappedLeftInset());

        outlineListInsets = new Insets(topLevel.snappedTopInset(),
                                       topLevel.snappedRightInset(),
                                       topLevel.snappedBottomInset(),
                                       topLevel.snappedLeftInset());

        tableCellInsets = new Insets(tableCell.snappedTopInset(),
                                     tableCell.snappedRightInset(),
                                     tableCell.snappedBottomInset(),
                                     tableCell.snappedLeftInset());
        tableKeyCellInsets = tableCellInsets;

        tableInsets = new Insets(table.snappedTopInset(),
                                 table.snappedRightInset(),
                                 table.snappedBottomInset(),
                                 table.snappedLeftInset());

        valueInsets = new Insets(valueText.get()
                                          .snappedTopInset(),
                                 valueText.get()
                                          .snappedRightInset(),
                                 valueText.get()
                                          .snappedBottomInset(),
                                 valueText.get()
                                          .snappedLeftInset());

        labelInsets = valueInsets;
        valueLineHeight = FONT_LOADER.getFontMetrics(valueFont)
                                     .getLineHeight();
        labelLineHeight = FONT_LOADER.getFontMetrics(labelFont)
                                     .getLineHeight();
    }

    public Insets getLabelInsets() {
        return labelInsets;
    }

    public double getLabelLineHeight() {
        return labelLineHeight;
    }

    public LayoutModel getModel() {
        return model;
    }

    public Insets getNestedKeyListCellInsets() {
        return nestedKeyListCellInsets;
    }

    public Insets getNestedKeyListInsets() {
        return nestedKeyListInsets;
    }

    public Insets getNestedListCellInsets() {
        return nestedListCellInsets;
    }

    public Insets getNestedListInsets() {
        return nestedListInsets;
    }

    public Insets getOutlineListCellInsets() {
        return outlineListCellInsets;
    }

    public Insets getOutlineListInsets() {
        return outlineListInsets;
    }

    public Insets getTableCellInsets() {
        return tableCellInsets;
    }

    public Insets getTableInsets() {
        return tableInsets;
    }

    public Insets getTableKeyCellInsets() {
        return tableKeyCellInsets;
    }

    public Insets getValueInsets() {
        return valueInsets;
    }

    public double getValueLineHeight() {
        return valueLineHeight;
    }

    public double labelWidth(String label) {
        return FONT_LOADER.computeStringWidth(String.format("W%sW", label),
                                              labelFont);
    }

    public double measureHeader(TableView<?> table) {
        Group root = new Group(table);
        Scene scene = new Scene(root);
        if (styleSheets != null) {
            scene.getStylesheets()
                 .addAll(styleSheets);
        }
        root.applyCss();
        root.layout();
        table.applyCss();
        table.layout();
        @SuppressWarnings("rawtypes")
        TableHeaderRow headerRow = ((TableViewSkinBase) table.getSkin()).getTableHeaderRow();
        root.getChildren()
            .clear();
        return headerRow.getHeight();
    }
 
    public double valueDoubleSpaceWidth() {
        return FONT_LOADER.computeStringWidth("WW",
                                              valueFont);
    }
    public double valueWidth(String value) {
        return FONT_LOADER.computeStringWidth(String.format("W%sW", value),
                                              valueFont);
    }
}
