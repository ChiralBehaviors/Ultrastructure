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

package com.chiralbehaviors.CoRE.occular;

import static com.chiralbehaviors.CoRE.occular.Occular.path;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.chiralbehaviors.CoRE.occular.GraphQlApi.QueryException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hellblazer.utils.Utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
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
            if (!empty && (item != null)) {
                setText(item.get("name")
                            .asText());
            } else {
                setText(null);
            }
        }
    }

    static {
        try {
            QUERY = Utils.getDocument(FacetController.class.getResourceAsStream("facet.query"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    private static String                     QUERY;

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
    protected ComboBox<ObjectNode>            classification;

    @FXML
    protected ComboBox<ObjectNode>            classifier;

    @FXML
    protected TableColumn<ObjectNode, String> defaultValueColumn;

    @FXML
    protected TextField                       name;

    @FXML
    protected TableColumn<ObjectNode, String> relationshipColumn;

    public GraphQlApi getApi() {
        return api;
    }

    public void setApi(GraphQlApi api) {
        this.api = api;
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
        classifier.setCellFactory(l -> new ExistentialCell());
        classifier.setButtonCell(new ExistentialCell());

        classification.setCellFactory(l -> new ExistentialCell());
        classification.setButtonCell(new ExistentialCell());
    }

    public void setFacet(String id) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", id);
        ObjectNode result;
        try {
            result = api.query(QUERY, variables);
        } catch (QueryException e) {
            throw new IllegalStateException(e);
        }
        update(result);
    }

    private void update(ObjectNode result) {
        JsonNode facet = result.get("Facet");
        name.setText(facet.get("name")
                          .asText());

        ObservableList<ObjectNode> existentials = FXCollections.observableArrayList();
        result.withArray("InstancesOfAgency")
              .forEach(r -> existentials.add((ObjectNode) r));
        result.withArray("InstancesOfAttribute")
              .forEach(r -> existentials.add((ObjectNode) r));
        result.withArray("InstancesOfInterval")
              .forEach(r -> existentials.add((ObjectNode) r));
        result.withArray("InstancesOfLocation")
              .forEach(r -> existentials.add((ObjectNode) r));
        result.withArray("InstancesOfProduct")
              .forEach(r -> existentials.add((ObjectNode) r));
        result.withArray("InstancesOfRelationship")
              .forEach(r -> existentials.add((ObjectNode) r));
        result.withArray("InstancesOfStatusCode")
              .forEach(r -> existentials.add((ObjectNode) r));
        result.withArray("InstancesOfUnits")
              .forEach(r -> existentials.add((ObjectNode) r));

        ObservableList<ObjectNode> attributeList = FXCollections.observableArrayList();
        facet.withArray("attributes")
             .forEach(a -> attributeList.add((ObjectNode) a));
        attributes.setItems(attributeList);

        ObservableList<ObjectNode> childrenList = FXCollections.observableArrayList();
        facet.withArray("children")
             .forEach(c -> childrenList.add((ObjectNode) c));
        children.setItems(childrenList);

        ObservableList<ObjectNode> relationships = FXCollections.observableArrayList();
        result.withArray("InstancesOfRelationship")
              .forEach(r -> relationships.add((ObjectNode) r));
        classifier.setItems(relationships);
        classifier.setValue((ObjectNode) facet.get("classifier"));
        classification.setItems(existentials);
        classification.setValue((ObjectNode) facet.get("classification"));
    }

}
