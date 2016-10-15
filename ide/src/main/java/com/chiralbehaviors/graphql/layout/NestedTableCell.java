package com.chiralbehaviors.graphql.layout;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.chiralbehaviors.graphql.layout.schema.Relation;
import com.chiralbehaviors.graphql.layout.schema.SchemaNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.javafx.scene.control.skin.VirtualScrollBar;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableCell;

public class NestedTableCell<S, T> extends TableCell<S, T> {
    public static class Nested {
        public final int                cardinality;
        public final ListView<JsonNode> control;
        public final double             height;
        public final SchemaNode           relation;

        public Nested(SchemaNode relation, int cardinality,
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

        public void register(int column, Nested child) {
            nested[column] = child;
            for (Nested n : nested) {
                if (n == null) {
                    return;
                }
            }
            link();
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

    public Nesting getNesting(Relation relation, int count, Integer index) {
        Map<Integer, Nesting> nested = nestings.computeIfAbsent(relation,
                                                                k -> new HashMap<>(3));
        return nested.computeIfAbsent(index, k -> new Nesting(count));
    }
}
