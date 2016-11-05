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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.javafx.scene.control.skin.VirtualScrollBar;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableRow;

public class NestedTableRow<T> extends TableRow<T> {

    private final Map<String, ListView<JsonNode>[]> links   = new HashMap<>(3);

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
    }

    public void link(String id, Integer column, ListView<JsonNode> child,
                     int count) {
        @SuppressWarnings("unchecked")
        ListView<JsonNode>[] link = links.computeIfAbsent(id,
                                                          k -> new ListView[count]);
        //        if (link[column] != null && link[column] != child) {
        //            System.out.println(String.format("%s[%s]:%s != %s", id, column,
        //                                             child, link[column]));
        //        }

        link[column] = child;
        for (Object v : link) {
            if (v == null) {
                return;
            }
        }
        link(Arrays.asList(link));
        links.remove(id);
    }

    private void link(List<ListView<JsonNode>> link) {
        Stack<ScrollBar> scrolls = new Stack<>();
        link.forEach(p -> {
            Set<Node> deadSeaScrolls = p.lookupAll(".scroll-bar:vertical");
            VirtualScrollBar scrollbar = deadSeaScrolls.stream()
                                                       .filter(n -> n instanceof VirtualScrollBar)
                                                       .map(n -> (VirtualScrollBar) n)
                                                       .filter(n -> n.getOrientation()
                                                                     .equals(Orientation.VERTICAL))
                                                       .reduce((a, b) -> b)
                                                       .orElse(null);
            scrolls.push(scrollbar);
            p.getSelectionModel()
             .selectedIndexProperty()
             .addListener((o, pr, c) -> {
                 link.forEach(sibling -> {
                     if (sibling != p) {
                         sibling.getSelectionModel()
                                .select(c.intValue());
                     }
                 });
             });
        });
        scrolls.forEach(scrollbar -> {
            scrollbar.setDisable(scrollbar != scrolls.lastElement());
            scrollbar.valueProperty()
                     .addListener((ChangeListener<? super Number>) (o, p,
                                                                    c) -> {
                         scrolls.forEach(sibling -> {
                             if (sibling != scrollbar) {
                                 sibling.setValue(c.doubleValue());
                             }
                         });
                     });
        });
    }
}
