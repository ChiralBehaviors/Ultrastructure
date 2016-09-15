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

import java.util.function.Consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;

import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.TableColumn;
import javafx.scene.text.Font;

/**
 * @author hhildebrand
 *
 */
abstract public class SchemaNode {

    public class NodeMaster {
        public final Node               node;
        public final Consumer<JsonNode> items;

        public NodeMaster(Consumer<JsonNode> items, Node node) {
            this.items = items;
            this.node = node;
        }
    }

    protected static FontLoader FONT_LOADER              = Toolkit.getToolkit()
                                                                  .getFontLoader();
    protected final String      field;
    protected final String      label;
    protected Font              labelFont                = Font.getDefault();
    protected boolean           startNewOutlineColumn    = false;
    protected boolean           startNewOutlineColumnSet = false;
    protected float             tableColumnWidth         = 0;
    protected boolean           useVerticalTableHeader   = false;

    public SchemaNode(String field) {
        this(field, field);
    }

    public SchemaNode(String field, String label) {
        this.label = label;
        this.field = field;
    }

    abstract public Control buildControl();

    public String getField() {
        return field;
    }

    public String getLabel() {
        return label;
    }

    public float getTableColumnWidth() {
        return tableColumnWidth;
    }

    abstract public String toString(int indent);

    abstract protected TableColumn<JsonNode, ?> buildTableColumn();

    protected float labelWidth() {
        return FONT_LOADER.computeStringWidth(label, labelFont);
    }

    abstract protected float measure(ArrayNode data);

    abstract protected NodeMaster outlineElement();
}
