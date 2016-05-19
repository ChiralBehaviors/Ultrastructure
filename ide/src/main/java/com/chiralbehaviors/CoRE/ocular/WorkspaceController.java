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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceController {
    private UriBuilder           base;
    @FXML
    private TextArea             description;
    private FacetsController     facetsController;
    @FXML
    private Label                instance;
    @FXML
    private TextField            iri;
    @FXML
    private TextField            name;
    @FXML
    private Label                version;
    private WebEngine            webEngine;
    private ObjectNode           workspace;
    @FXML
    private ListView<ObjectNode> workspaces;

    @FXML
    public void initialize() throws IOException {
        workspaces.setCellFactory(cellData -> new ListCell<ObjectNode>() {
            @Override
            protected void updateItem(ObjectNode item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && (item != null)) {
                    setText(item.get("name")
                                .asText());
                } else {
                    setText(null);
                }
            }
        });
        workspaces.getSelectionModel()
                  .selectedItemProperty()
                  .addListener(new ChangeListener<ObjectNode>() {
                      @Override
                      public void changed(ObservableValue<? extends ObjectNode> ov,
                                          ObjectNode old_val,
                                          ObjectNode new_val) {
                          setWorkspace(new_val);
                      }
                  });
    }

    public void set(FacetsController facetsController, WebEngine webEngine) {
        this.facetsController = facetsController;
        this.webEngine = webEngine;
    }

    public void setUrl(URL url) {
        try {
            base = UriBuilder.fromUri(url.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException();
        }
    }

    public void setWorkspace(ObjectNode wsp) {
        this.workspace = wsp;
        UriBuilder endpoint = base.clone();
        String encoded;
        try {
            encoded = URLEncoder.encode(String.format("urn:uuid:%s",
                                                      workspace.get("id")
                                                               .asText()),
                                        "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
        endpoint.path("api")
                .path("workspace")
                .path(encoded)
                .path("meta");
        GraphQlApi api = new GraphQlApi(ClientBuilder.newClient()
                                                     .target(endpoint),
                                        null);
        UriBuilder ideEndpoint = base.clone();
        ideEndpoint.path("api")
                   .path("ide");
        ideEndpoint.queryParam("workspace", encoded);
        try {
            webEngine.load(ideEndpoint.build()
                                      .toURL()
                                      .toExternalForm());
        } catch (MalformedURLException | IllegalArgumentException
                | UriBuilderException e) {
            throw new IllegalStateException(e);
        }
        facetsController.setApi(api);
        facetsController.update();
        version.setText(Integer.toString(workspace.get("version")
                                                  .asInt()));
        name.setText(workspace.get("name")
                              .asText());
        description.setText(workspace.get("description")
                                     .asText());
        iri.setText(workspace.get("IRI")
                             .asText());
    }

    public void update() {
        UriBuilder wspEndpoint = base.clone();
        wspEndpoint.path("api")
                   .path("workspace");
        WebTarget endpoint = ClientBuilder.newClient()
                                          .target(wspEndpoint);
        Builder invocationBuilder = endpoint.request(MediaType.APPLICATION_JSON_TYPE);
        ArrayNode result = invocationBuilder.get(ArrayNode.class);
        ObservableList<ObjectNode> workspaceList = FXCollections.observableArrayList();
        result.forEach(o -> workspaceList.add((ObjectNode) o));
        workspaces.setItems(workspaceList);
        instance.setText(base.toString());
    }
}
