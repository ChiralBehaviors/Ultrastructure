package com.chiralbehaviors.graphql.layout;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanFloatPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

public class SchemaView extends Control {
    private final ObjectProperty<SchemaNode> root = new SimpleObjectProperty<>();

    public SchemaView(SchemaNode root) {
        this.root.set(root);
        this.root.addListener((o, p, c) -> rebuild(c));
        rebuild(root);
    }

    public ObjectProperty<SchemaNode> root() {
        return root;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new SchemaViewSkin(this);
    }

    private TreeTableColumn<SchemaNode, Number> buildWidth() {
        TreeTableColumn<SchemaNode, Number> width = new TreeTableColumn<>("Width");
        width.setPrefWidth(100);
        width.setCellValueFactory(f -> {
            try {
                return new JavaBeanFloatPropertyBuilder().bean(f.getValue()
                                                                .getValue())
                                                         .name("tableColumnWidth")
                                                         .build();
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(e);
            }
        });
        width.setCellFactory(c -> new TreeTableCell<SchemaNode, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setGraphic(null);
                    return;
                }
                setText(item.toString());
            }

        });
        return width;
    }

    private TreeTableColumn<SchemaNode, String> buildLabel() {
        TreeTableColumn<SchemaNode, String> label = new TreeTableColumn<>("Label");
        label.setPrefWidth(100);
        label.setCellValueFactory(f -> {
            try {
                return new JavaBeanStringPropertyBuilder().bean(f.getValue()
                                                                 .getValue())
                                                          .name("label")
                                                          .build();
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(e);
            }
        });
        label.setCellFactory(c -> new TreeTableCell<SchemaNode, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setText(null);
                    return;
                }
                setText(item);
            }

        });
        return label;
    }

    private TreeTableColumn<SchemaNode, Boolean> buildFolding() {
        TreeTableColumn<SchemaNode, Boolean> folding = new TreeTableColumn<>("Fold");
        folding.setPrefWidth(30);
        folding.setCellValueFactory(f -> {
            SchemaNode node = f.getValue()
                               .getValue();
            if (node instanceof Relation) {
                try {
                    return new JavaBeanBooleanPropertyBuilder().bean(f.getValue()
                                                                      .getValue())
                                                               .name("fold")
                                                               .build();
                } catch (NoSuchMethodException e) {
                    throw new IllegalStateException(e);
                }
            }
            return null;
        });
        folding.setCellFactory(c -> new TreeTableCell<SchemaNode, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setGraphic(null);
                    return;
                }
                CheckBox check = new CheckBox();
                setGraphic(check);
            }

        });
        return folding;
    }

    private TreeItem<SchemaNode> buildItem(SchemaNode node) {
        final TreeItem<SchemaNode> item = new TreeItem<SchemaNode>(node);
        if (node instanceof Relation) {
            ((Relation) node).getChildren()
                             .forEach(c -> {
                                 item.getChildren()
                                     .add(buildItem(c));
                             });
        }
        return item;
    }

    @SuppressWarnings({ "unchecked" })
    private TreeTableView<SchemaNode> buildSchemaTree(SchemaNode root) {
        TreeTableView<SchemaNode> view = new TreeTableView<>();
        view.setPrefWidth(USE_COMPUTED_SIZE);
        view.getColumns()
            .addAll(buildLabel(), buildFolding(), buildWidth());
        view.setRoot(buildItem(root));
        return view;
    }

    private Object rebuild(SchemaNode relation) {
        if (relation == null) {
            getChildren().clear();
        } else {
            getChildren().add(buildSchemaTree(relation));
        }
        return null;
    }
}
