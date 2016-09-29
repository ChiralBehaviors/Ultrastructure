package com.chiralbehaviors.graphql.layout;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class NestedTableView<T> extends Control {

    private final TableView<String> header;
    private final TableView<T>      table;

    public NestedTableView(TableView<String> header, TableView<T> table) {
        this.header = header;
        this.table = table;
        VBox box = new VBox();
        VBox.setVgrow(header, Priority.NEVER);
        VBox.setVgrow(table, Priority.ALWAYS);
        box.getChildren()
           .add(header);
        box.getChildren()
           .add(table);
        getChildren().add(box);
    }

    public TableView<String> getHeader() {
        return header;
    }

    public TableView<T> getTable() {
        return table;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new NestedTableViewSkin<T>(this);
    }
    
    public ObjectProperty<ObservableList<T>> itemsProperty() {
        return table.itemsProperty();
    }

    public void setItems(ObservableList<T> items) {
        table.setItems(items);
    }
}
