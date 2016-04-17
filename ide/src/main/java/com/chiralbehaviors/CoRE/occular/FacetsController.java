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

import com.fasterxml.jackson.databind.node.ObjectNode;

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

    @FXML
    private ListView<ObjectNode> facets;

    @FXML
    private AnchorPane           facetAnchor;

    @SuppressWarnings("unused")
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
}
