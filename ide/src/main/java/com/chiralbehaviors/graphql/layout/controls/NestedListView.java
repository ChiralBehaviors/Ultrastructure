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

package com.chiralbehaviors.graphql.layout.controls;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.javafx.scene.control.skin.VirtualScrollBar;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.control.ListView;

/**
 * 
 * @author hhildebrand
 *
 */
public class NestedListView extends ListView<JsonNode> {
    private final int                      column;
    private final int                      count;
    private final NestedTableRow<JsonNode> row;
    private ChangeListener<? super Number> scrollListener;
    private ChangeListener<? super Number> selectionListener;

    public NestedListView(NestedTableRow<JsonNode> row, int column, int count) {
        this.row = row;
        this.column = column;
        this.count = count;
    }

    public void link(NestedListView[] link, boolean last) {
        VirtualScrollBar scrollbar = getScrollBar();
        scrollbar.setDisable(!last);
        selectionListener = (o, pr, c) -> {
            for (NestedListView sibling : link) {
                if (sibling != this) {
                    sibling.getSelectionModel()
                           .select(c.intValue());
                }
            }
        };
        getSelectionModel().selectedIndexProperty()
                           .addListener(selectionListener);

        scrollListener = (ChangeListener<? super Number>) (o, p, c) -> {
            for (NestedListView sibling : link) {
                if (sibling != this) {
                    sibling.getScrollBar()
                           .setValue(c.doubleValue());
                }
            }
        };
        scrollbar.valueProperty()
                 .addListener(scrollListener);
    }

    public void setNodeId(String nodeId) {
        if (count > 1) {
            unlink();
            row.link(nodeId, column, this, count);
        }
    }

    public VirtualScrollBar getScrollBar() {
        return lookupAll(".scroll-bar:vertical").stream()
                                                .filter(n -> n instanceof VirtualScrollBar)
                                                .map(n -> (VirtualScrollBar) n)
                                                .filter(n -> n.getOrientation()
                                                              .equals(Orientation.VERTICAL))
                                                .reduce((a, b) -> b)
                                                .orElse(null);
    }

    private void unlink() {
        VirtualScrollBar scrollbar = getScrollBar();
        if (scrollbar != null && scrollListener != null) {
            scrollbar.valueProperty()
                     .removeListener(scrollListener);
            scrollListener = null;
        }
        if (selectionListener != null) {
            getSelectionModel().selectedIndexProperty()
                               .removeListener(selectionListener);
            selectionListener = null;
        }
    }
}
