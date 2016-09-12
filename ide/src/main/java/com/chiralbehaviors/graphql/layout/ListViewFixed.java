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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Skin;

/**
 * @author hhildebrand
 *
 */
public class ListViewFixed<T> extends javafx.scene.control.ListView<T> {
    private final BooleanProperty fillWidth = new SimpleBooleanProperty(this,
                                                                        "fillWidth");

    public final BooleanProperty fillWidthProperty() {
        return fillWidth;
    }

    public final boolean isFillWidth() {
        return fillWidth.get();
    }

    public final void setFillWidth(boolean fillWidth) {
        this.fillWidth.set(fillWidth);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ListViewFixedSkin<>(this);
    }
}
