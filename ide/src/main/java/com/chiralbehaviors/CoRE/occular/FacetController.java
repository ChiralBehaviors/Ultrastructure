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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.chiralbehaviors.CoRE.occular.GraphQlApi.QueryException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hellblazer.utils.Utils;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * @author hhildebrand
 *
 */
public class FacetController {
    static {
        try {
            QUERY = Utils.getDocument(FacetController.class.getResourceAsStream("facet.query"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    private static String                   QUERY;

    private GraphQlApi                      api;

    @FXML
    private TableColumn<ObjectNode, String> attributeNameColumn;

    @FXML
    private TableView<ObjectNode>           attributes;

    @FXML
    private TableColumn<ObjectNode, String> attributeTypeColumn;

    @FXML
    private TableColumn<ObjectNode, String> cardinalityColumn;

    @FXML
    private TableColumn<ObjectNode, String> childColumn;

    @FXML
    private TableColumn<ObjectNode, String> childNameColumn;

    @FXML
    private TableView<ObjectNode>           children;

    @FXML
    private ComboBox<ObjectNode>            classification;

    @FXML
    private ComboBox<ObjectNode>            classifier;

    @FXML
    private TableColumn<ObjectNode, String> defaultValueColumn;

    @FXML
    private TextField                       name;

    @FXML
    private TableColumn<ObjectNode, String> relationshipColumn;

    public GraphQlApi getApi() {
        return api;
    }

    public void setApi(GraphQlApi api) {
        this.api = api;
    }

    public void setFacet(String id) {

        Map<String, Object> variables = new HashMap<>();
        variables.put("id", id);
        ObjectNode facet;
        try {
            facet = api.query(QUERY, variables);
        } catch (QueryException e) {
            throw new IllegalStateException(e);
        }
        name.setText(facet.get("name")
                          .asText());
    }

}
