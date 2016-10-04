package com.chiralbehaviors.graphql.layout;

import java.util.Stack;

import com.chiralbehaviors.graphql.layout.schema.SchemaNode;
import com.fasterxml.jackson.databind.JsonNode;

import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class NestedColumnView extends Control {
    private static class Nesting {
        private final SchemaNode child;
        private final Control    view;
        private final int        cardinality;

        public Nesting(SchemaNode child, Control view, int cardinality) {
            this.child = child;
            this.view = view;
            this.cardinality = cardinality;
        }

        @Override
        public String toString() {
            return String.format("Nesting[%s]", child.getField());
        }
    }

    private final Stack<Nesting> nestings = new Stack<>();

    public void manifest(int cardinality) {
        Nesting inner = nestings.get(0);
        double height = inner.view.getPrefHeight();
        inner.view.setPrefHeight(height);
        for (int i = 1; i < nestings.size() - 1; i++) {
            Nesting outer = nestings.get(i);
            height = outer.cardinality * height;
            outer.view.setPrefHeight(height);
            outer.view.setMaxHeight(height);
            outer.view.setMinHeight(height);
            inner = outer;
        }
        Nesting top = nestings.peek();
        if (cardinality > 0) {
            height = cardinality * height;
            top.view.setPrefHeight(height);
        }
        //        top.view.setMaxHeight(height);
        //        top.view.setMinHeight(height);
        Control view = top.view;
        this.getChildren()
            .add(view);
    }

    public void push(SchemaNode child, Control view, int cardinality) {
        nestings.push(new Nesting(child, view, cardinality));
    }

    public void setItem(JsonNode item) {
        Nesting top = nestings.peek();
        JsonNode extracted = top.child.extractFrom(item);
        SchemaNode.setItemsOf(top.view, extracted);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new NestedColumnViewSkin(this);
    }
}
