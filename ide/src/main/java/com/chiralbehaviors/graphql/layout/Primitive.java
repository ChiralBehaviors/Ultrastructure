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

/**
 * @author hhildebrand
 *
 */
public class Primitive extends SchemaNode {

    private boolean isVariableLength = false;
    private int     valueDefaultWidth;

    public Primitive(String label) {
        super(label);
    }

    @Override
    public String toString() {
        return String.format("Primitive [%s]", getLabel());
    }

    @Override
    protected void measure(ArrayNode data) {
        int sum = 0;
        int max = 0;
        for (JsonNode prim : data) {
            int width = prim.asText()
                            .length();
            sum += width;
            max = Math.max(max, width);
        }
        valueDefaultWidth = data.size() == 0 ? 0 : sum / data.size();
        if (max > valueDefaultWidth) {
            isVariableLength = true;
        }
        tableColumnWidth = Math.max(label.length(), valueDefaultWidth);
    }
}
