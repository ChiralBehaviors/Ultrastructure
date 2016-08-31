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

import javafx.scene.text.Font;

/**
 * @author hhildebrand
 *
 */
public class RelationConstraints extends LayoutConstraints {

    private Font outlineBulletStyle;
    private int  outlineIndentWidth;
    private int  outlineTupleSpaceHeight;
    private int  tableNestSpaceSideWidth;
    private int  outlineMaxLabelWidth;
    private int  outlineColumnMinWidth;

    public RelationConstraints(Font labelTextStyle, Font outlineBulletStyle,
                               int outlineIndentWidth,
                               int outlineTupleSpaceHeight,
                               int tableNestSpaceSideWidth) {
        super(labelTextStyle);
        this.outlineBulletStyle = outlineBulletStyle;
        this.outlineIndentWidth = outlineIndentWidth;
        this.outlineTupleSpaceHeight = outlineTupleSpaceHeight;
        this.tableNestSpaceSideWidth = tableNestSpaceSideWidth;
    }

    public float bulleWidth() {
        return FONT_LOADER.computeStringWidth("*", outlineBulletStyle);
    }
}
