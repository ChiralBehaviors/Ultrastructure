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

package com.chiralbehaviors.CoRE.ocular;

import static com.chiralbehaviors.CoRE.ocular.Ocular.path;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.chiralbehaviors.CoRE.ocular.GraphQlApi.QueryException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hellblazer.utils.Utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.util.Callback;

/**
 * @author hhildebrand
 *
 */
public class FacetController {

    static class ExistentialCell extends ComboBoxListCell<ObjectNode> {
        @Override
        public void updateItem(ObjectNode item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(item.get("name")
                            .asText());
            } else {
                setText(null);
            }
        }
    }

    private static String FACET_QUERY;
    static {
        try {
            FACET_QUERY = Utils.getDocument(FacetController.class.getResourceAsStream("facet.query"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    protected GraphQlApi                      api;

    @FXML
    protected TableColumn<ObjectNode, String> attributeNameColumn;

    @FXML
    protected TableView<ObjectNode>           attributes;

    @FXML
    protected TableColumn<ObjectNode, String> attributeTypeColumn;

    @FXML
    protected TableColumn<ObjectNode, String> cardinalityColumn;

    @FXML
    protected TableColumn<ObjectNode, String> childColumn;

    @FXML
    protected TableColumn<ObjectNode, String> childNameColumn;

    @FXML
    protected TableView<ObjectNode>           children;

    @FXML
    protected MenuButton                      classification;

    @FXML
    protected MenuButton                      classifier;

    @FXML
    protected TableColumn<ObjectNode, String> defaultValueColumn;

    @FXML
    protected TextField                       name;

    @FXML
    protected TableColumn<ObjectNode, String> relationshipColumn;

    public GraphQlApi getApi() {
        return api;
    }

    @FXML
    public void initialize() {
        Callback<CellDataFeatures<ObjectNode, String>, ObservableValue<String>> cellValue = cellData -> new SimpleStringProperty(cellData.getValue()
                                                                                                                                         .get("authorizedAttribute")
                                                                                                                                         .get("name")
                                                                                                                                         .asText());
        attributeNameColumn.setCellValueFactory(cellValue);
        attributeTypeColumn.setCellValueFactory(cellData -> path(cellData.getValue(),
                                                                 "authorizedAttribute/valueType"));
        cardinalityColumn.setCellValueFactory(cellData -> path(cellData.getValue(),
                                                               "cardinality"));
        childNameColumn.setCellValueFactory(cellData -> path(cellData.getValue(),
                                                             "name"));

        childColumn.setCellValueFactory(cellData -> path(cellData.getValue(),
                                                         "child/name"));
        relationshipColumn.setCellValueFactory(cellData -> path(cellData.getValue(),
                                                                "relationship/name"));
    }

    public void setApi(GraphQlApi api, ObjectNode existentials) {
        this.api = api;
        ObservableList<ObjectNode> all = FXCollections.observableArrayList();
        existentials.withArray("agencies")
                    .forEach(r -> all.add((ObjectNode) r));
        existentials.withArray("attributes")
                    .forEach(r -> all.add((ObjectNode) r));
        existentials.withArray("intervals")
                    .forEach(r -> all.add((ObjectNode) r));
        existentials.withArray("locations")
                    .forEach(r -> all.add((ObjectNode) r));
        existentials.withArray("products")
                    .forEach(r -> all.add((ObjectNode) r));
        existentials.withArray("relationships")
                    .forEach(r -> all.add((ObjectNode) r));
        existentials.withArray("statusCodes")
                    .forEach(r -> all.add((ObjectNode) r));
        existentials.withArray("units")
                    .forEach(r -> all.add((ObjectNode) r));
        classifier.getItems()
                  .clear();

        ObservableList<ObjectNode> relationships = FXCollections.observableArrayList();
        existentials.withArray("relationships")
                    .forEach(r -> relationships.add((ObjectNode) r));
        classifier.getItems()
                  .addAll(relationships.stream()
                                       .map(o -> new MenuItem(o.get("name")
                                                               .asText()))
                                       .collect(Collectors.toList()));

        classification.getItems()
                      .clear();
        classification.getItems()
                      .addAll(all.stream()
                                 .map(o -> new MenuItem(o.get("name")
                                                         .asText()))
                                 .collect(Collectors.toList()));
    }

    public void setFacet(String id) {
        if (id == null) {
            return;
        }
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", id);
        ObjectNode result;
        try {
            result = api.query(FACET_QUERY, variables);
        } catch (QueryException e) {
            throw new IllegalStateException(e);
        }
        update(result);
    }

    private void update(ObjectNode result) {
        JsonNode facet = result.get("facet");
        name.setText(facet.get("name")
                          .asText());

        ObservableList<ObjectNode> attributeList = FXCollections.observableArrayList();
        facet.withArray("attributes")
             .forEach(a -> attributeList.add((ObjectNode) a));
        attributes.setItems(attributeList);

        ObservableList<ObjectNode> childrenList = FXCollections.observableArrayList();
        facet.withArray("children")
             .forEach(c -> childrenList.add((ObjectNode) c));
        children.setItems(childrenList);

        classifier.setText(facet.get("classifier")
                                .get("name")
                                .asText());
        classification.setText(facet.get("classification")
                                    .get("name")
                                    .asText());
    }
}
