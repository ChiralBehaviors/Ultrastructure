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

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;

/**
 * @author hhildebrand
 *
 */
public class OccularController {

    @FXML
    private Tab              workspace;

    @FXML
    private Tab              facets;

    @FXML
    private Tab              sandbox;

    private FacetsController facetsController;

    @FXML
    private void initialize() throws Exception {
        initializeFacetsView();
    }

    public void initializeFacetsView() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Occular.class.getResource("view/FacetsView.fxml"));
        AnchorPane workspaceView = (AnchorPane) loader.load();
        facetsController = (FacetsController) loader.getController();
        facets.setContent(workspaceView);
    }

    public void setApi(GraphQlApi api) {
        facetsController.setApi(api);
        facetsController.update();
    }
}
