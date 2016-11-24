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

import java.lang.reflect.Field;
import java.util.List;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;
import com.sun.javafx.scene.control.skin.TextAreaSkin;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

@SuppressWarnings("restriction")
public class Layout {
    private static FontLoader FONT_LOADER = Toolkit.getToolkit()
                                                   .getFontLoader();
    private static Insets     ZERO_INSETS = new Insets(0);

    public static Insets add(Insets a, Insets b) {
        return new Insets(a.getTop() + b.getTop(), a.getRight() + b.getRight(),
                          a.getBottom() + b.getBottom(),
                          a.getLeft() + b.getLeft());
    }

    public static double snap(double value) {
        return Math.ceil(value);
    }

    private Insets            listCellInsets  = ZERO_INSETS;
    private Insets            listInsets      = ZERO_INSETS;
    private final LayoutModel model;
    private List<String>      styleSheets;
    private Insets            tableCellInsets = ZERO_INSETS;
    private Insets            tableInsets     = ZERO_INSETS;
    private Insets            tableRowInsets;
    private Font              textFont        = Font.getDefault();
    private Insets            textInsets      = ZERO_INSETS;
    private double            textLineHeight  = 0;

    public Layout(List<String> styleSheets) {
        this(styleSheets, new LayoutModel() {
        });
    }

    public Layout(List<String> styleSheets, LayoutModel model) {
        this.model = model;
        this.styleSheets = styleSheets;
        TextArea text = new TextArea("Lorem Ipsum");
        TextArea labelText = new TextArea("Lorem Ipsum");

        ListView<String> outlineList = new ListView<>();

        ListCell<String> outlineListCell = new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
            };
        };
        outlineList.setCellFactory(s -> outlineListCell);

        TableCell<String, String> tableCell = new TableCell<String, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
            }
        };

        TableView<String> table = new TableView<>();

        TableRow<String> tableRow = new TableRow<>();
        table.setRowFactory(v -> tableRow);

        TableColumn<String, String> column = new TableColumn<>("");
        column.setCellFactory(c -> tableCell);
        column.setCellValueFactory(s -> new SimpleStringProperty(s.getValue()));

        table.getColumns()
             .add(column);

        VBox root = new VBox();
        root.getChildren()
            .addAll(table, outlineList, text, labelText);
        Scene scene = new Scene(root, 800, 600);
        if (styleSheets != null) {
            scene.getStylesheets()
                 .addAll(styleSheets);
        }
        text.applyCss();
        text.layout();

        labelText.applyCss();
        labelText.layout();

        ObservableList<String> tableItems = table.getItems();
        tableItems.add("Lorem ipsum");
        table.setItems(null);
        table.setItems(tableItems);

        ObservableList<String> listItems = outlineList.getItems();
        listItems.add("Lorem ipsum");
        outlineList.setItems(null);
        outlineList.setItems(listItems);
        outlineList.requestLayout();

        root.applyCss();
        root.layout();
        table.applyCss();
        table.layout();
        table.refresh();
        tableRow.applyCss();
        tableRow.layout();

        outlineList.applyCss();
        outlineList.layout();
        outlineList.refresh();
        outlineListCell.applyCss();
        outlineListCell.layout();

        textFont = text.getFont();

        listCellInsets = new Insets(outlineListCell.snappedTopInset(),
                                    outlineListCell.snappedRightInset(),
                                    outlineListCell.snappedBottomInset(),
                                    outlineListCell.snappedLeftInset());

        listInsets = new Insets(outlineList.snappedTopInset(),
                                outlineList.snappedRightInset(),
                                outlineList.snappedBottomInset(),
                                outlineList.snappedLeftInset());

        tableCellInsets = new Insets(tableCell.snappedTopInset(),
                                     tableCell.snappedRightInset(),
                                     tableCell.snappedBottomInset(),
                                     tableCell.snappedLeftInset());

        tableInsets = new Insets(table.snappedTopInset(),
                                 table.snappedRightInset(),
                                 table.snappedBottomInset(),
                                 table.snappedLeftInset());

        tableRowInsets = new Insets(tableRow.snappedTopInset(),
                                    tableRow.snappedRightInset(),
                                    tableRow.snappedBottomInset(),
                                    tableRow.snappedLeftInset());
        Field contentField;
        try {
            contentField = TextAreaSkin.class.getDeclaredField("contentView");
        } catch (NoSuchFieldException | SecurityException e) {
            throw new IllegalStateException(e);
        }
        contentField.setAccessible(true);
        Region content;
        try {
            content = (Region) contentField.get(text.getSkin());
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        textInsets = new Insets(content.snappedTopInset(),
                                content.snappedRightInset(),
                                content.snappedBottomInset(),
                                content.snappedLeftInset());
    }

    public double getListCellHorizontalInset() {
        return listCellInsets.getLeft() + listCellInsets.getRight();
    }

    public double getListCellVerticalInset() {
        return listCellInsets.getTop() + listCellInsets.getBottom();
    }

    public double getListHorizontalInset() {
        return listInsets.getLeft() + listInsets.getRight();
    }

    public double getListVerticalInset() {
        return listInsets.getTop() + listInsets.getBottom();
    }

    public LayoutModel getModel() {
        return model;
    }

    public double getNestedInset() {
        return getNestedLeftInset() + getNestedRightInset();
    }

    public double getNestedLeftInset() {
        return listInsets.getLeft() + listCellInsets.getLeft();
    }

    public double getNestedRightInset() {
        return listInsets.getRight() + listCellInsets.getRight();
    }

    public double getTableCellHorizontalInset() {
        return tableCellInsets.getLeft() + tableCellInsets.getRight();
    }

    public double getTableCellVerticalInset() {
        return tableCellInsets.getTop() + tableCellInsets.getBottom();
    }

    public double getTableHorizontalInset() {
        return tableInsets.getLeft() + tableInsets.getRight();
    }

    public double getTableRowHorizontalInset() {
        return tableRowInsets.getLeft() + tableRowInsets.getRight();
    }

    public double getTableRowVerticalInset() {
        return tableRowInsets.getTop() + tableRowInsets.getBottom();
    }

    public double getTableVerticalInset() {
        return tableInsets.getTop() + tableInsets.getBottom();
    }

    public double getTextHorizontalInset() {
        return textInsets.getLeft() + textInsets.getRight();
    }

    public double getTextLineHeight() {
        return textLineHeight;
    }

    public double getTextVerticalInset() {
        return textInsets.getTop() + textInsets.getBottom();
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

    public double textDoubleSpaceWidth() {
        return FONT_LOADER.computeStringWidth("WW", textFont);
    }

    public double textWidth(String text) {
        return snap(FONT_LOADER.computeStringWidth(String.format("W%sW\n",
                                                                 text),
                                                   textFont));
    }

    @Override
    public String toString() {
        return String.format("Layout [model=%s\n listCellInsets=%s\n listInsets=%s\n styleSheets=%s\n tableCellInsets=%s\n tableInsets=%s\n tableRowInsets=%s\n textFont=%s\n textInsets=%s\n textLineHeight=%s]",
                             model, listCellInsets, listInsets, styleSheets,
                             tableCellInsets, tableInsets, tableRowInsets,
                             textFont, textInsets, textLineHeight);
    }
}
