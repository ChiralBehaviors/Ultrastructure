package com.chiralbehaviors.graphql.layout;

import com.chiralbehaviors.graphql.layout.schema.SchemaNode;
import com.fasterxml.jackson.databind.JsonNode;

import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class NestedColumnView extends Control { 
    private Control control;
    private SchemaNode relation;

    public void manifest(int cardinality) {   
        this.getChildren()
            .add(control);
    }
    
    public void setTop(SchemaNode relation, Control control) {
        this.relation = relation;
        this.control = control;
    }

    public void setItem(JsonNode item) { 
        JsonNode extracted = relation.extractFrom(item);
        SchemaNode.setItemsOf(control, extracted);
    }

    public String toString() {
        return String.format("NestedView %s", relation);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new NestedColumnViewSkin(this);
    }
}
