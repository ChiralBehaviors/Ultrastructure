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

package com.chiralbehaviors.graphql.layout;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.graphql.layout.schema.Relation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.sun.javafx.webkit.WebConsoleListener;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class AutoLayoutController {

    private static final String DATA   = "data";
    private static final String ERRORS = "errors";
    private static final Logger log    = LoggerFactory.getLogger(AutoLayoutController.class);

    @FXML
    ToggleGroup                 page;
    @FXML
    private AnchorPane          anchor;
    private final QueryState    queryState;
    @FXML
    private BorderPane          root;
    @FXML
    private RadioButton         showLayout;
    @FXML
    private RadioButton         showQuery;
    @FXML
    private RadioButton         showSchema;

    public AutoLayoutController(QueryState queryState) throws IOException {
        this.queryState = queryState;
        SchemaView schema = constructSchema();
        AutoLayoutView layout = initialize(schema);
        load();
        anchor.getChildren()
              .add(layout);
        Node graphiql = constructGraphiql();
        showLayout.setSelected(true);
        page.selectedToggleProperty()
            .addListener((o, p, c) -> {
                anchor.getChildren()
                      .clear();
                RadioButton prev = (RadioButton) p;
                RadioButton current = (RadioButton) c;

                if (prev == showSchema) {
                    layout.autoLayout();
                } else if (prev == showQuery) {
                    setData(layout, schema);
                }

                if (current == showLayout) {
                    anchor.getChildren()
                          .add(layout);
                } else if (current == showSchema) {
                    anchor.getChildren()
                          .add(schema);
                } else if (current == showQuery) {
                    anchor.getChildren()
                          .add(graphiql);
                } else {
                    throw new IllegalStateException(String.format("Invalid radio button: %s",
                                                                  current));
                }
            });
    }

    public Parent getRoot() {
        return root;
    }

    private Node constructGraphiql() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chiralbehaviors/graphql/layout/graphiql.fxml"));
        Node graphiql = loader.load();
        AnchorPane.setTopAnchor(graphiql, 0.0);
        AnchorPane.setLeftAnchor(graphiql, 0.0);
        AnchorPane.setBottomAnchor(graphiql, 0.0);
        AnchorPane.setRightAnchor(graphiql, 0.0);
        GraphiqlController controller = loader.getController();
        initialize(controller.webview.getEngine());
        return graphiql;
    }

    private SchemaView constructSchema() {
        SchemaView schema = new SchemaView();
        AnchorPane.setTopAnchor(schema, 0.0);
        AnchorPane.setLeftAnchor(schema, 0.0);
        AnchorPane.setBottomAnchor(schema, 0.0);
        AnchorPane.setRightAnchor(schema, 0.0);
        return schema;
    }

    private AutoLayoutView initialize(SchemaView schemaView) {
        AutoLayoutView layout = new AutoLayoutView();
        AnchorPane.setTopAnchor(layout, 0.0);
        AnchorPane.setLeftAnchor(layout, 0.0);
        AnchorPane.setBottomAnchor(layout, 0.0);
        AnchorPane.setRightAnchor(layout, 0.0);
        return layout;
    }

    private void initialize(WebEngine engine) {
        WebConsoleListener.setDefaultListener(new WebConsoleListener() {
            @Override
            public void messageAdded(WebView webView, String message,
                                     int lineNumber, String sourceId) {
                System.out.println("Console: [" + sourceId + ":" + lineNumber
                                   + "] " + message);

            }
        });
        JSObject jsobj = (JSObject) engine.executeScript("window");
        jsobj.setMember("app", queryState);
        engine.load(getClass().getResource("/com/chiralbehaviors/graphql/layout/ide.html")
                              .toExternalForm());
    }

    private void load() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);
        loader.setLocation(getClass().getResource("/com/chiralbehaviors/graphql/layout/autolayout.fxml"));
        loader.load();
    }

    private void setData(AutoLayoutView layout, SchemaView schemaView) {
        if (queryState.getData() == null || queryState.getQuery() == null) {
            layout.setData(JsonNodeFactory.instance.arrayNode());
            return;
        }
        JsonNode data;
        try {
            data = new ObjectMapper().readTree(queryState.getData());
        } catch (IOException e) {
            log.warn("Cannot deserialize json data {}", queryState.getData());
            layout.setData(JsonNodeFactory.instance.arrayNode());
            return;
        }

        if (data.has(ERRORS) || !data.get(DATA)
                                     .has(queryState.getSource())) {
            layout.setData(JsonNodeFactory.instance.arrayNode());
            return;
        }
        Relation schema = (Relation) Relation.buildSchema(queryState.getQuery(),
                                                          queryState.getSource());
        schemaView.setRoot(schema);
        data = data.get(DATA)
                   .get(queryState.getSource());
        layout.setRoot(schema);
        layout.measure(data);
        layout.setData(data);
    }
}
