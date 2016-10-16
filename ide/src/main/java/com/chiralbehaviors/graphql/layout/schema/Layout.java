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

import static com.chiralbehaviors.graphql.layout.schema.SchemaNode.add;
import static com.chiralbehaviors.graphql.layout.schema.SchemaNode.outlineListCellClass;
import static com.chiralbehaviors.graphql.layout.schema.SchemaNode.outlineListStyleClass;
import static com.chiralbehaviors.graphql.layout.schema.SchemaNode.tableColumnStyleClass;
import static com.chiralbehaviors.graphql.layout.schema.SchemaNode.tableStyleClass;
import static com.chiralbehaviors.graphql.layout.schema.SchemaNode.valueStyleClass;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
import javafx.scene.layout.Border;
import javafx.scene.text.Font;

public class Layout {
    private static final int    SCROLL_WIDTH    = 28;
    private static FontLoader   FONT_LOADER     = Toolkit.getToolkit()
                                                         .getFontLoader();
    private static final Insets ZERO_INSETS     = new Insets(0);

    private Font                labelFont       = Font.getDefault();
    private Insets              labelInsets     = ZERO_INSETS;
    private double              labelLineHeight;
    private Insets              listCellInsets  = ZERO_INSETS;
    private Insets              listInsets      = ZERO_INSETS;
    private Insets              tableCellInsets = ZERO_INSETS;
    private Insets              tableInsets     = ZERO_INSETS;
    private Font                valueFont       = Font.getDefault();
    private Insets              valueInsets     = ZERO_INSETS;
    private double              valueLineHeight;

    public Layout(List<String> styleSheets) {
        AtomicReference<TextArea> valueText = new AtomicReference<>();
        AtomicReference<ListCell<String>> listCell = new AtomicReference<>();
        AtomicReference<ListView<String>> nestedList = new AtomicReference<>();

        ListView<String> topLevel = new ListView<>();
        topLevel.setCellFactory(s -> new ListCell<String>() {

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                ListView<String> nested = new ListView<String>();
                nested.getStyleClass()
                      .add(outlineListStyleClass());
                nestedList.set(nested);
                nested.setCellFactory(v -> new ListCell<String>() {
                    {
                        listCell.set(this);
                        getStyleClass().add(outlineListCellClass());
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        TextArea value = new TextArea("lorum ipsum");
                        value.getStyleClass()
                             .add(valueStyleClass());
                        valueText.set(value);
                        setGraphic(value);
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

        Border border = listCell.get()
                                .getBorder();
        listCellInsets = add(border == null ? ZERO_INSETS : border.getOutsets(),
                             add(border == null ? ZERO_INSETS
                                                : border.getInsets(),
                                 add(listCell.get()
                                             .getInsets(),
                                     listCell.get()
                                             .getBackground() == null ? ZERO_INSETS
                                                                      : listCell.get()
                                                                                .getBackground()
                                                                                .getOutsets())));
        border = nestedList.get()
                           .getBorder();
        listInsets = add(border == null ? ZERO_INSETS : border.getOutsets(),
                         add(border == null ? ZERO_INSETS : border.getInsets(),
                             add(nestedList.get()
                                           .getInsets(),
                                 nestedList.get()
                                           .getBackground() == null ? ZERO_INSETS
                                                                    : nestedList.get()
                                                                                .getBackground()
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
        border = table.getBorder();
        tableInsets = add(border == null ? ZERO_INSETS : border.getOutsets(),
                          add(border == null ? ZERO_INSETS : border.getInsets(),
                              add(table.getInsets(),
                                  table.getBackground() == null ? ZERO_INSETS
                                                                : table.getBackground()
                                                                       .getOutsets())));
        border = valueText.get()
                          .getBorder();
        valueInsets = add(border == null ? ZERO_INSETS : border.getOutsets(),
                          add(border == null ? ZERO_INSETS : border.getInsets(),
                              add(valueText.get()
                                           .getInsets(),
                                  valueText.get()
                                           .getBackground() == null ? ZERO_INSETS
                                                                    : valueText.get()
                                                                               .getBackground()
                                                                               .getOutsets())));
        border = valueText.get()
                          .getBorder();
        labelInsets = add(border == null ? ZERO_INSETS : border.getOutsets(),
                          add(border == null ? ZERO_INSETS : border.getInsets(),
                              add(valueText.get()
                                           .getInsets(),
                                  valueText.get()
                                           .getBackground() == null ? ZERO_INSETS
                                                                    : valueText.get()
                                                                               .getBackground()
                                                                               .getOutsets())));
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

    public Insets getListCellInsets() {
        return listCellInsets;
    }

    public Insets getListInsets() {
        return listInsets;
    }

    public Insets getTableCellInsets() {
        return tableCellInsets;
    }

    public Insets getTableInsets() {
        return tableInsets;
    }

    public Insets getValueInsets() {
        return valueInsets;
    }

    public double getValueLineHeight() {
        return valueLineHeight;
    }

    public double labelHeight(double maxWidth, double justifiedWidth) {
        double height = (Math.ceil(maxWidth / (justifiedWidth - scrollWidth()))
                         + 2)
                        * labelLineHeight;
        return height + labelInsets.getTop() + labelInsets.getBottom();
    }

    public double labelWidth(String label) {
        return FONT_LOADER.computeStringWidth(label, labelFont);
    }

    @Override
    public String toString() {
        return String.format("Layout\n  labelFont=%s\n  labelInsets=%s\n  labelLineHeight=%s\n  listCellInsets=%s\n  listInsets=%s\n  tableCellInsets=%s\n  tableInsets=%s\n  valueFont=%s\n  valueInsets=%s\n  valueLineHeight=%s]",
                             labelFont, labelInsets, labelLineHeight,
                             listCellInsets, listInsets, tableCellInsets,
                             tableInsets, valueFont, valueInsets,
                             valueLineHeight);
    }

    public double valueHeight(double maxWidth, double justifiedWidth) {
        double height = (Math.ceil(maxWidth / (justifiedWidth - scrollWidth()))
                         + 2)
                        * valueLineHeight;
        return height + valueInsets.getTop() + valueInsets.getBottom();
    }

    public double valueWidth(String value) {
        return FONT_LOADER.computeStringWidth(value, valueFont);
    }

    public double scrollWidth() {
        return SCROLL_WIDTH;
    }
}
