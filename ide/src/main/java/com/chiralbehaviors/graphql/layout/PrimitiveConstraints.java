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

/**
 * @author hhildebrand
 *
 */
public class PrimitiveConstraints {

    private int outlineMinValueWidth;
    private int outlineSnapValueWidth;
    private int tableMaxPrimitiveWidth;

    public int getOutlineMinValueWidth() {
        return outlineMinValueWidth;
    }

    public int getOutlineSnapValueWidth() {
        return outlineSnapValueWidth;
    }

    public int getTableMaxPrimitiveWidth() {
        return tableMaxPrimitiveWidth;
    }

    public void setOutlineMinValueWidth(int outlineMinValueWidth) {
        this.outlineMinValueWidth = outlineMinValueWidth;
    }

    public void setOutlineSnapValueWidth(int outlineSnapValueWidth) {
        this.outlineSnapValueWidth = outlineSnapValueWidth;
    }

    public void setTableMaxPrimitiveWidth(int tableMaxPrimitiveWidth) {
        this.tableMaxPrimitiveWidth = tableMaxPrimitiveWidth;
    }
}
