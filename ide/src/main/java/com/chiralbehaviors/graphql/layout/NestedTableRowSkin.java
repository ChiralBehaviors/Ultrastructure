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

import com.sun.javafx.scene.control.skin.TableRowSkin;

public class NestedTableRowSkin<S> extends TableRowSkin<S> {

    public NestedTableRowSkin(NestedTableRow<S> tableRow) {
        super(tableRow);
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);
    }

    @Override
    protected void updateCells(boolean resetChildren) {
        super.updateCells(resetChildren);
        link();
    }

    private void link() {
//        ((NestedTableRow<S>) getSkinnable()).link();
    }
}
