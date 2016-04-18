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

import com.chiralbehaviors.CoRE.occular.GraphQlApi.QueryException;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

/**
 * @author hhildebrand
 *
 */
public class FacetsController {
    private static String        QUERY = "{ Facets { id name } }";
    private GraphQlApi           api;

    @FXML
    private ListView<ObjectNode> facets;

    @FXML
    private AnchorPane           facetAnchor;

    private FacetController      facetController;

    @FXML
    private void initialize() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Occular.class.getResource("view/FacetView.fxml"));
        GridPane facetsView = (GridPane) loader.load();
        AnchorPane.setTopAnchor(facetsView, 10.0);
        AnchorPane.setBottomAnchor(facetsView, 10.0);
        AnchorPane.setLeftAnchor(facetsView, 10.0);
        AnchorPane.setRightAnchor(facetsView, 10.0);
        facetAnchor.getChildren()
                   .add(facetsView);
        facetController = loader.getController();
    }

    public GraphQlApi getApi() {
        return api;
    }

    public void setApi(GraphQlApi api) {
        this.api = api;
        facetController.setApi(api);
    }

    public void update() {
        ObjectNode f;
        try {
            f = api.query(QUERY, null);
        } catch (QueryException e) {
            throw new IllegalStateException(e);
        }
        ObservableList<ObjectNode> facetList = FXCollections.observableArrayList();
        f.withArray("Facets")
         .forEach(o -> facetList.add((ObjectNode) o));
        facets.setItems(facetList);
    }
}
