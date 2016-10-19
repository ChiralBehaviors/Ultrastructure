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

import static com.chiralbehaviors.graphql.layout.schema.SchemaNode.outlineListCellClass;
import static com.chiralbehaviors.graphql.layout.schema.SchemaNode.outlineListStyleClass;
import static com.chiralbehaviors.graphql.layout.schema.SchemaNode.tableColumnStyleClass;
import static com.chiralbehaviors.graphql.layout.schema.SchemaNode.tableStyleClass;
import static com.chiralbehaviors.graphql.layout.schema.SchemaNode.valueStyleClass;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.sun.javafx.scene.text.TextLayout;
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
import javafx.scene.text.TextBoundsType;

public class Layout {
    private final static FontLoader FONT_LOADER = Toolkit.getToolkit()
                                                         .getFontLoader();
    private static final TextLayout layout      = Toolkit.getToolkit()
                                                         .getTextLayoutFactory()
                                                         .createLayout();
    private static final Insets     ZERO_INSETS = new Insets(0);

    public static Insets add(Insets a, Insets b) {
        return new Insets(a.getTop() + b.getTop(), a.getRight() + b.getRight(),
                          a.getBottom() + b.getBottom(),
                          a.getRight() + b.getRight());
    }

    public static double snap(double y) {
        return ((int) y) + .5;
    }

    @SuppressWarnings("deprecation")
    static double computeTextHeight(Font font, String text,
                                    double wrappingWidth, double lineSpacing,
                                    TextBoundsType boundsType) {
        layout.setContent(text != null ? text : "", font.impl_getNativeFont());
        layout.setWrapWidth((float) wrappingWidth);
        layout.setLineSpacing((float) lineSpacing);
        if (boundsType == TextBoundsType.LOGICAL_VERTICAL_CENTER) {
            layout.setBoundsType(TextLayout.BOUNDS_CENTER);
        } else {
            layout.setBoundsType(0);
        }
        return layout.getBounds()
                     .getHeight();
    }

    private Font   labelFont       = Font.getDefault();
    private Insets labelInsets     = ZERO_INSETS;
    private double labelLineHeight;
    private Insets listCellInsets  = ZERO_INSETS;
    private Insets listInsets      = ZERO_INSETS;
    private Insets tableCellInsets = ZERO_INSETS;
    private Insets tableInsets     = ZERO_INSETS;
    private Font   valueFont       = Font.getDefault();
    private Insets valueInsets     = ZERO_INSETS;
    private double valueLineHeight;

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

        valueFont = valueText.get()
                             .getFont();

        labelFont = valueFont;

        listCellInsets = new Insets(listCell.get()
                                            .snappedTopInset(),
                                    listCell.get()
                                            .snappedRightInset(),
                                    listCell.get()
                                            .snappedBottomInset(),
                                    listCell.get()
                                            .snappedLeftInset());

        listInsets = new Insets(nestedList.get()
                                          .snappedTopInset(),
                                nestedList.get()
                                          .snappedRightInset(),
                                nestedList.get()
                                          .snappedBottomInset(),
                                nestedList.get()
                                          .snappedLeftInset());

        tableCellInsets = new Insets(tableCell.snappedTopInset(),
                                     tableCell.snappedRightInset(),
                                     tableCell.snappedBottomInset(),
                                     tableCell.snappedLeftInset());

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

    public double computeLabelHeight(String text, double wrappingWidth) {
        return snap(computeTextHeight(labelFont, text, wrappingWidth, 0,
                                 TextBoundsType.LOGICAL)
               + labelLineHeight) + 3;
    }

    public double computeValueHeight(String text, double wrappingWidth) {
        return snap(computeTextHeight(valueFont, text, wrappingWidth, 0,
                                 TextBoundsType.LOGICAL)
               + valueLineHeight)+ 3;
    }

    public double valueWidth(String value) {
        return FONT_LOADER.computeStringWidth(value, valueFont)
               + FONT_LOADER.computeStringWidth(" ", valueFont);
    }

}
