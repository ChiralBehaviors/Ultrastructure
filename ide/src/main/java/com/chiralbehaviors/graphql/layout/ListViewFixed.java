package com.chiralbehaviors.graphql.layout;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Skin;

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
        return new ListViewFixedSkin<T>(this);
    }
}