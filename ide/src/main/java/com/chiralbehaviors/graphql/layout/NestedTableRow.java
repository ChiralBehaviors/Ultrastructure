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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.chiralbehaviors.graphql.layout.schema.Relation;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.javafx.scene.control.skin.VirtualScrollBar;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableRow;

public class NestedTableRow<T> extends TableRow<T> {
    public static class Nested {
        public final int                cardinality;
        public final ListView<JsonNode> control;
        public final double             height;
        public final Relation           relation;

        public Nested(Relation relation, int cardinality,
                      ListView<JsonNode> control, double height) {
            this.relation = relation;
            this.cardinality = cardinality;
            this.control = control;
            this.height = height;
        }

        @Override
        public String toString() {
            return String.format("Nested [relation=%s, cardinality=%s, height=%s]",
                                 relation.getLabel(), cardinality, height);
        }
    }

    public static class Nesting {
        private final Nested[] nested;

        public Nesting(int count) {
            nested = new Nested[count];
        }

        public boolean register(int column, Nested child) {
            nested[column] = child;
            for (Nested n : nested) {
                if (n == null) {
                    return false;
                }
            }
            link();
            return true;
        }

        private void link() {
            List<Nested> link = Arrays.asList(nested);
            double max = link.stream()
                             .mapToDouble(p -> p.height)
                             .max()
                             .orElse(-1);
            linkScrollBars(link, max);
        }

        private void linkScrollBars(List<Nested> link, double max) {
            Stack<ScrollBar> scrolls = new Stack<>();
            link.forEach(p -> {
                p.control.setPrefHeight(p.cardinality * max);
                p.control.setFixedCellSize(max);
                Set<Node> deadSeaScrolls = p.control.lookupAll(".scroll-bar:vertical");
                VirtualScrollBar scrollbar = deadSeaScrolls.stream()
                                                           .filter(n -> n instanceof VirtualScrollBar)
                                                           .map(n -> (VirtualScrollBar) n)
                                                           .filter(n -> n.getOrientation()
                                                                         .equals(Orientation.VERTICAL))
                                                           .reduce((a, b) -> b)
                                                           .orElse(null);
                scrollbar.setUnitIncrement(max);
                scrolls.push(scrollbar);
                p.control.getSelectionModel()
                         .selectedIndexProperty()
                         .addListener((o, pr, c) -> {
                             link.forEach(sibling -> {
                                 if (sibling.control != p.control) {
                                     sibling.control.getSelectionModel()
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

    private final Map<Relation, Map<Integer, Nesting>> nestings = new HashMap<>(3);

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
    }

    public void register(Integer index, Relation relation, Integer column,
                         ListView<JsonNode> child, int cardinality,
                         double height, int count) {
        Map<Integer, Nesting> list = nestings.computeIfAbsent(relation,
                                                              k -> new HashMap<>(3));
        Nesting nesting = list.computeIfAbsent(index, k -> new Nesting(count));
        if (nesting.register(column, new Nested(relation, cardinality, child,
                                               height))) {
            list.remove(column);
            if (list.isEmpty()) {
                nestings.remove(relation);
            }
        }
    }
}
