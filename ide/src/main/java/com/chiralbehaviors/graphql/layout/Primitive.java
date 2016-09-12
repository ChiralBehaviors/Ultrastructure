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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author hhildebrand
 *
 */
public class Primitive extends SchemaNode {

    private PrimitiveConstraints constraints;
    private boolean              isVariableLength = false;
    private float                valueDefaultWidth;
    private Font                 valueFont        = Font.getDefault();

    public Primitive(String label) {
        super(label);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.graphql.layout.SchemaNode#buildControl()
     */
    @Override
    public TextField buildControl() {
        TextField textArea = new TextField();
        textArea.setPrefWidth(tableColumnWidth);
        return textArea;
    }

    public PrimitiveConstraints getConstraints() {
        return constraints;
    }

    public float getValueDefaultWidth() {
        return valueDefaultWidth;
    }

    public boolean isVariableLength() {
        return isVariableLength;
    }

    public void setConstraints(PrimitiveConstraints constraints) {
        this.constraints = constraints;
    }

    @Override
    public String toString() {
        return String.format("Primitive [%s:%s]", getLabel(),
                             valueDefaultWidth);
    }

    @Override
    public String toString(int indent) {
        return toString();
    }

    @Override
    protected TableColumn<JsonNode, ?> buildTableColumn() {
        TableColumn<JsonNode, String> column = new TableColumn<>(label);
        column.setPrefWidth(tableColumnWidth);
        column.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("%s",
                                                                                      cellData.getValue()
                                                                                              .get(field))));
        column.setCellFactory(c -> new TableCell<JsonNode, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                if (item == getItem())
                    return;
                super.updateItem(item, empty);
                super.setText(item);
            }
        });
        return column;
    }

    @Override
    protected void measure(ArrayNode data) {
        float sum = 0;
        float max = 0;
        for (JsonNode prim : data) {
            float width = valueWidth(prim.toString());
            sum += width;
            max = Math.max(max, width);
        }
        valueDefaultWidth = data.size() == 0 ? 0 : sum / data.size();
        if (max > valueDefaultWidth) {
            isVariableLength = true;
        }
        tableColumnWidth = Math.max(label.length(), valueDefaultWidth);
    }

    @Override
    protected ControlMaster outlineElement() {
        AnchorPane anchor = new AnchorPane();
        HBox box = new HBox(5);
        box.getChildren()
           .add(new Text(label));
        TextField control = buildControl();
        box.getChildren()
           .add(control);
        box.setVisible(true);
        anchor.getChildren()
              .add(box);
        AnchorPane.setTopAnchor(box, 0.0);
        AnchorPane.setBottomAnchor(box, 0.0);
        AnchorPane.setLeftAnchor(box, 0.0);
        AnchorPane.setRightAnchor(box, 0.0);
        return new ControlMaster(item -> control.setText(item.toString()),
                                 anchor);
    }

    private float valueWidth(String text) {
        return FONT_LOADER.computeStringWidth(text, valueFont);
    }
}
