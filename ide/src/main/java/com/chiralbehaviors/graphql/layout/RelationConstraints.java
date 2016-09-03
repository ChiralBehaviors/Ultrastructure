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
public class RelationConstraints {
    private int outlineColumnMinWidth;
    private int outlineIndentWidth;
    private int outlineMaxLabelWidth;
    private int outlineTupleSpaceHeight;
    private int tableNestSpaceSideWidth;

    public RelationConstraints(int outlineIndentWidth,
                               int outlineTupleSpaceHeight,
                               int tableNestSpaceSideWidth) {
        this.outlineIndentWidth = outlineIndentWidth;
        this.outlineTupleSpaceHeight = outlineTupleSpaceHeight;
        this.tableNestSpaceSideWidth = tableNestSpaceSideWidth;
    }

    public int getOutlineColumnMinWidth() {
        return outlineColumnMinWidth;
    }

    public int getOutlineIndentWidth() {
        return outlineIndentWidth;
    }

    public int getOutlineMaxLabelWidth() {
        return outlineMaxLabelWidth;
    }

    public int getOutlineTupleSpaceHeight() {
        return outlineTupleSpaceHeight;
    }

    public int getTableNestSpaceSideWidth() {
        return tableNestSpaceSideWidth;
    }

    public void setOutlineColumnMinWidth(int outlineColumnMinWidth) {
        this.outlineColumnMinWidth = outlineColumnMinWidth;
    }

    public void setOutlineIndentWidth(int outlineIndentWidth) {
        this.outlineIndentWidth = outlineIndentWidth;
    }

    public void setOutlineMaxLabelWidth(int outlineMaxLabelWidth) {
        this.outlineMaxLabelWidth = outlineMaxLabelWidth;
    }

    public void setOutlineTupleSpaceHeight(int outlineTupleSpaceHeight) {
        this.outlineTupleSpaceHeight = outlineTupleSpaceHeight;
    }

    public void setTableNestSpaceSideWidth(int tableNestSpaceSideWidth) {
        this.tableNestSpaceSideWidth = tableNestSpaceSideWidth;
    }
}
