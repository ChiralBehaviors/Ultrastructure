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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import com.chiralbehaviors.graphql.layout.NestedColumnView.Nested;

import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Skin;
import javafx.scene.control.TableRow;

public class NestedTableRow<T> extends TableRow<T> {
    private final List<NestedColumnView> nested = new ArrayList<>();

    public List<NestedColumnView> getNested() {
        return nested;
    }

    public void register(NestedColumnView nestedView) {
        nested.add(nestedView);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new NestedTableRowSkin<>(this);
    }

    public void link() {
        List<Stack<Nested>> stacks = nested.stream()
                                           .map(n -> n.getNested())
                                           .collect(Collectors.toList());
        do {
            List<Nested> link = stacks.stream()
                                      .filter(s -> !s.isEmpty())
                                      .map(s -> s.pop())
                                      .collect(Collectors.toList());
            double max = link.stream()
                             .mapToDouble(p -> p.height)
                             .max()
                             .orElse(-1);
            Stack<ScrollBar> scrolls = new Stack<>();
            link.forEach(p -> {
                p.control.setPrefHeight(p.cardinality * max);
                p.control.setFixedCellSize(max);
                for (Node node : p.control.lookupAll(".scroll-bar:vertical")) {
                    scrolls.add((ScrollBar) node);
                    break;
                }

            });
            ScrollBar master = scrolls.size() > 1 ? scrolls.pop() : null;
            if (master != null) {
                master.valueProperty()
                      .addListener((o, p, c) -> {
                          scrolls.forEach(scrollbar -> {
                              scrollbar.setValue(c.doubleValue());
                          });
                      });
            }
            stacks = stacks.stream()
                           .filter(s -> !s.isEmpty())
                           .collect(Collectors.toList());
        } while (!stacks.isEmpty());
    }
}
