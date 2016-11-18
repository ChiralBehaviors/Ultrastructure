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
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.ocular.Page.Route;
import com.chiralbehaviors.CoRE.ocular.PageContext.QueryException;
import com.chiralbehaviors.graphql.layout.AutoLayoutView;
import com.chiralbehaviors.graphql.layout.LayoutModel;
import com.chiralbehaviors.graphql.layout.schema.Relation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * 
 * @author hhildebrand
 *
 */
public class SinglePageApp extends Application implements LayoutModel {
    private static final Logger log = LoggerFactory.getLogger(SinglePageApp.class);

    public static void main(String[] args) {
        launch(args);
    }

    private AnchorPane               anchor;
    private GraphqlApplication       application;
    private WebTarget                endpoint;
    private final Stack<PageContext> history = new Stack<>();
    private AutoLayoutView           layout;
    private Stage                    primaryStage;

    @Override
    public void apply(ListView<JsonNode> list, Relation relation) {
        list.setOnMouseClicked(event -> {
            Route route = history.peek()
                                 .getRoute(relation);
            if (route == null) {
                return;
            }
            if (!list.getItems()
                     .isEmpty()
                && event.getButton() == MouseButton.PRIMARY
                && event.getClickCount() == 2) {
                JsonNode item = list.getSelectionModel()
                                    .getSelectedItem();
                if (item == null) {
                    return;
                }
                try {
                    push(extract(route, item));
                } catch (QueryException e) {
                    log.error("Unable to push page: %s", route.getPath(), e);
                }
            }
        });
    }

    @Override
    public void apply(TableRow<JsonNode> row, Relation relation) {
        row.setOnMouseClicked(event -> {
            Route route = history.peek()
                                 .getRoute(relation);
            if (route == null) {
                return;
            }
            if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY
                && event.getClickCount() == 2) {
                JsonNode item = row.getItem();
                if (item == null) {
                    return;
                }
                try {
                    push(extract(route, item));
                } catch (QueryException e) {
                    log.error("Unable to push page: %s", route.getPath(), e);
                }
            }
        });
    }

    public void initRootLayout(Stage ps) throws IOException, URISyntaxException,
                                         QueryException {
        primaryStage = ps;
        anchor = new AnchorPane();
        primaryStage.setScene(new Scene(anchor, 800, 600));
        Map<String, String> parameters = getParameters().getNamed();
        application = new ObjectMapper(new YAMLFactory()).readValue(getClass().getResourceAsStream(parameters.get("app")),
                                                                    GraphqlApplication.class);
        endpoint = ClientBuilder.newClient()
                                .target(application.getEndpoint()
                                                   .toURI());
        push(new PageContext(application.getRoot()));
        primaryStage.show();
    }

    @Override
    public void start(Stage primaryStage) throws IOException,
                                          URISyntaxException, QueryException {
        initRootLayout(primaryStage);
    }

    private JsonNode apply(JsonNode node, String path) {
        StringTokenizer tokens = new StringTokenizer(path, "/");
        JsonNode current = node;
        while (tokens.hasMoreTokens()) {
            node = current.get(tokens.nextToken());
        }
        return node;
    }

    private void displayCurrentPage() throws QueryException {
        PageContext pageContext = history.peek();
        anchor.getChildren()
              .clear();
        layout = layout(pageContext);
        AnchorPane.setTopAnchor(layout, 0.0);
        AnchorPane.setLeftAnchor(layout, 0.0);
        AnchorPane.setBottomAnchor(layout, 0.0);
        AnchorPane.setRightAnchor(layout, 0.0);
        anchor.getChildren()
              .add(layout);
        ObjectNode data = pageContext.evaluate(endpoint);
        layout.measure(data);
        layout.setData(data);
        primaryStage.setTitle(pageContext.getPage()
                                         .getTitle());
    }

    private PageContext extract(Route route, JsonNode item) {
        Map<String, Object> variables = new HashMap<>();
        route.getExtract()
             .entrySet()
             .stream()
             .forEach(entry -> {
                 variables.put(entry.getKey(), apply(item, entry.getValue()));
             });

        Page target = application.route(route.getPath());
        return new PageContext(target, variables);
    }

    private AutoLayoutView layout(PageContext pageContext) throws QueryException {
        AutoLayoutView layout = new AutoLayoutView(pageContext.getRoot(), this);
        layout.getStylesheets()
              .add(getClass().getResource("/non-nested.css")
                             .toExternalForm());
        ObjectNode data = pageContext.evaluate(endpoint);
        layout.setData(data);
        layout.measure(data);
        return layout;
    }

    @SuppressWarnings("unused")
    private void pop() throws QueryException {
        if (history.size() == 1) {
            return;
        }
        history.pop();
        displayCurrentPage();
    }

    private void push(PageContext pageContext) throws QueryException {
        history.push(pageContext);
        displayCurrentPage();
    }
}
