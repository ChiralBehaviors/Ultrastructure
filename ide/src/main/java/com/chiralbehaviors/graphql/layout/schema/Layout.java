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
import static com.chiralbehaviors.graphql.layout.schema.SchemaNode.tableViewStyleClass;

import java.util.List;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Border;
import javafx.scene.text.Font;

public class Layout {
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
        TextArea valueText = new TextArea("lorus ipsum");
        TextArea labelText = new TextArea("lorus ipsum");
        TableView<String> table = new TableView<>();
        table.getStyleClass()
             .add(tableViewStyleClass());
        TableRow<String> row = new TableRow<String>();
        TableColumn<String, String> column = new TableColumn<>("");
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
        Group root = new Group(table, listView, valueText, labelText);
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
        labelText.applyCss();
        labelText.layout();
        valueText.applyCss();
        valueText.layout();
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
        border = table.getBorder();
        tableInsets = add(border == null ? ZERO_INSETS : border.getOutsets(),
                          add(border == null ? ZERO_INSETS : border.getInsets(),
                              add(table.getInsets(),
                                  table.getBackground() == null ? ZERO_INSETS
                                                                : table.getBackground()
                                                                       .getOutsets())));
        border = valueText.getBorder();
        valueInsets = add(border == null ? ZERO_INSETS : border.getOutsets(),
                          add(border == null ? ZERO_INSETS : border.getInsets(),
                              add(valueText.getInsets(),
                                  valueText.getBackground() == null ? ZERO_INSETS
                                                                    : valueText.getBackground()
                                                                               .getOutsets())));
        border = labelText.getBorder();
        labelInsets = add(border == null ? ZERO_INSETS : border.getOutsets(),
                          add(border == null ? ZERO_INSETS : border.getInsets(),
                              add(labelText.getInsets(),
                                  labelText.getBackground() == null ? ZERO_INSETS
                                                                    : labelText.getBackground()
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

    public double labelHeight(double ratio) {
        double height = ratio * labelLineHeight;
        return Math.max(labelLineHeight, height) + labelInsets.getTop()
               + labelInsets.getBottom() + 18;
    }

    public double labelWidth(String label) {
        return FONT_LOADER.computeStringWidth(label, labelFont);
    }

    public double valueHeight(double ratio) {
        double height = ratio * valueLineHeight;
        return Math.max(valueLineHeight, height) + valueInsets.getTop()
               + valueInsets.getBottom() + 18;
    }

    public double valueWidth(String value) {
        return FONT_LOADER.computeStringWidth(value, valueFont);
    }
}
