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
import javafx.geometry.Pos;
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
        column.setCellValueFactory(cellData -> new SimpleStringProperty(toString(cellData.getValue()
                                                                                         .get(field))));
        column.setCellFactory(c -> new TableCell<JsonNode, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                if (item == getItem())
                    return;
                super.updateItem(item, empty);
                super.setText(item);
                setAlignment(Pos.CENTER_LEFT);
            }
        });
        return column;
    }

    @Override
    protected void measure(ArrayNode data) {
        float sum = 0;
        float max = 0;
        for (JsonNode prim : data) {
            float width = valueWidth(toString(prim));
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
    protected NodeMaster outlineElement() {
        HBox box = new HBox(5);
        box.getChildren()
           .add(new Text(label));
        TextField control = buildControl();
        control.setAlignment(Pos.CENTER_LEFT);
        box.getChildren()
           .add(control);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setVisible(true);
        AnchorPane.setTopAnchor(box, 0.0);
        AnchorPane.setBottomAnchor(box, 0.0);
        AnchorPane.setLeftAnchor(box, 0.0);
        AnchorPane.setRightAnchor(box, 0.0);
        return new NodeMaster(item -> control.setText(item.toString()), box);
    }

    private float valueWidth(String text) {
        return FONT_LOADER.computeStringWidth(text, valueFont);
    }

    private String toString(JsonNode value) {
        if (value == null) {
            return "";
        }
        if (value instanceof ArrayNode) {
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for (JsonNode e : value) {
                if (first) {
                    first = false;
                    builder.append('[');
                } else {
                    builder.append(", ");
                }
                builder.append(e.asText());
            }
            ;
            return builder.toString();
        } else {
            return value.asText();
        }
    }
}
