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

import com.fasterxml.jackson.databind.node.ObjectNode;

import javafx.fxml.FXML;
import javafx.scene.control.TreeTableColumn;

/**
 * @author hhildebrand
 *
 */
public class JobsController {

    @FXML
    private TreeTableColumn<ObjectNode, String> assignTo;
    @FXML
    private TreeTableColumn<ObjectNode, String> deliverFrom;
    @FXML
    private TreeTableColumn<ObjectNode, String> deliverTo;
    @FXML
    private TreeTableColumn<ObjectNode, String> job;
    @FXML
    private TreeTableColumn<ObjectNode, String> product;
    @FXML
    private TreeTableColumn<ObjectNode, String> quantity;
    @FXML
    private TreeTableColumn<ObjectNode, String> requester;
    @FXML
    private TreeTableColumn<ObjectNode, String> service;
    @FXML
    private TreeTableColumn<ObjectNode, String> status;
    @FXML
    private TreeTableColumn<ObjectNode, String> unit;

    @FXML
    public void intialize() {

        assignTo.setCellValueFactory(cellData -> path(cellData.getValue()
                                                              .getValue(),
                                                      "assignTo/name"));
        assignTo.setCellValueFactory(cellData -> path(cellData.getValue()
                                                              .getValue(),
                                                      "deliverFrom/name"));
        assignTo.setCellValueFactory(cellData -> path(cellData.getValue()
                                                              .getValue(),
                                                      "deliverTo/name"));
        assignTo.setCellValueFactory(cellData -> path(cellData.getValue()
                                                              .getValue(),
                                                      "id"));
        assignTo.setCellValueFactory(cellData -> path(cellData.getValue()
                                                              .getValue(),
                                                      "product/name"));
        assignTo.setCellValueFactory(cellData -> path(cellData.getValue()
                                                              .getValue(),
                                                      "quantity"));
        assignTo.setCellValueFactory(cellData -> path(cellData.getValue()
                                                              .getValue(),
                                                      "requester/name"));
        assignTo.setCellValueFactory(cellData -> path(cellData.getValue()
                                                              .getValue(),
                                                      "service/name"));
        assignTo.setCellValueFactory(cellData -> path(cellData.getValue()
                                                              .getValue(),
                                                      "status/name"));
        assignTo.setCellValueFactory(cellData -> path(cellData.getValue()
                                                              .getValue(),
                                                      "unit/name"));
    }
}
