package com.chiralbehaviors.graphql.layout;

import java.util.Stack;

import com.chiralbehaviors.graphql.layout.schema.SchemaNode;
import com.fasterxml.jackson.databind.JsonNode;

import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;

public class NestedColumnView extends Control {
    private static class Nesting {
        private final SchemaNode         child;
        private final ListView<JsonNode> view;

        public Nesting(SchemaNode child, ListView<JsonNode> view) {
            super();
            this.child = child;
            this.view = view;
        }

        @Override
        public String toString() {
            return String.format("Nesting[%s]", child.getField());
        }
    }

    private final Stack<Nesting> nestings = new Stack<>();

    public void manifest() {
        Nesting top = nestings.peek();
        ListView<JsonNode> view = top.view;
        this.getChildren().add(view);
    }

    public void push(SchemaNode child, ListView<JsonNode> view) {
        nestings.push(new Nesting(child, view));
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
