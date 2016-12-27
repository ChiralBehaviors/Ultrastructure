/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chiralbehaviors.layout.toy;

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

import com.chiralbehaviors.layout.AutoLayoutView;
import com.chiralbehaviors.layout.Layout.LayoutModel;
import com.chiralbehaviors.layout.graphql.GraphQlUtil.QueryException;
import com.chiralbehaviors.layout.schema.Relation;
import com.chiralbehaviors.layout.toy.Page.Route;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.hellblazer.utils.Utils;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableRow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    private final Stack<PageContext> back    = new Stack<>();
    private Button                   backButton;
    private WebTarget                endpoint;
    private final Stack<PageContext> forward = new Stack<>();
    private Button                   forwardButton;
    private AutoLayoutView           layout;
    private Stage                    primaryStage;
    private Button                   reloadButton;

    @Override
    public void apply(ListView<JsonNode> list, Relation relation) {
        list.setOnMouseClicked(event -> {
            Route route = back.peek()
                              .getRoute(relation);
            if (route == null) {
                return;
            }
            if (!list.getItems()
                     .isEmpty()
                && event.getButton() == MouseButton.PRIMARY
                && event.getClickCount() >= 2) {
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
            Route route = back.peek()
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

    @Override
    public void start(Stage primaryStage) throws IOException,
                                          URISyntaxException, QueryException {
        this.primaryStage = primaryStage;
        anchor = new AnchorPane();
        VBox vbox = new VBox(locationBar(), anchor);
        primaryStage.setScene(new Scene(vbox, 800, 600));
        Map<String, String> parameters = getParameters().getNamed();
        application = new ObjectMapper(new YAMLFactory()).readValue(Utils.resolveResource(getClass(),
                                                                                          parameters.get("app")),
                                                                    GraphqlApplication.class);
        endpoint = ClientBuilder.newClient()
                                .target(application.getEndpoint()
                                                   .toURI());
        push(new PageContext(application.getRoot()));
        primaryStage.show();
    }

    private JsonNode apply(JsonNode node, String path) {
        StringTokenizer tokens = new StringTokenizer(path, "/");
        JsonNode current = node;
        while (tokens.hasMoreTokens()) {
            node = current.get(tokens.nextToken());
        }
        return node;
    }

    private void back() {
        forward.push(back.pop());
        displayCurrentPage();
    }

    private Button button(String imageResource) {
        Button button = new Button();
        Image image = new Image(getClass().getResourceAsStream(imageResource));
        button.graphicProperty()
              .set(new ImageView(image));
        return button;
    }

    private void displayCurrentPage() {
        updateLocationBar();
        PageContext pageContext = back.peek();
        primaryStage.setTitle(pageContext.getPage()
                                         .getTitle());
        try {
            layout = layout(pageContext);
        } catch (QueryException e) {
            log.error("Unable to display page", e);
            return;
        }
        anchor.getChildren()
              .setAll(layout);
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

    private void forward() {
        back.push(forward.pop());
        displayCurrentPage();
    }

    private AutoLayoutView layout(PageContext pageContext) throws QueryException {
        AutoLayoutView layout = new AutoLayoutView(pageContext.getRoot(), this);
        layout.getStylesheets()
              .add(getClass().getResource("/non-nested.css")
                             .toExternalForm());
        ObjectNode data = pageContext.evaluate(endpoint);
        layout.setData(data);
        layout.measure(data);
        AnchorPane.setTopAnchor(layout, 0.0);
        AnchorPane.setLeftAnchor(layout, 0.0);
        AnchorPane.setBottomAnchor(layout, 0.0);
        AnchorPane.setRightAnchor(layout, 0.0);
        return layout;
    }

    private HBox locationBar() {
        HBox hbox = new HBox();

        backButton = button("/back.png");
        forwardButton = button("/forward.png");
        reloadButton = button("/reload.png");

        backButton.setOnAction(e -> back());
        forwardButton.setOnAction(e -> forward());
        reloadButton.setOnAction(e -> reload());

        hbox.getChildren()
            .addAll(backButton, forwardButton, reloadButton);

        return hbox;
    }

    private void push(PageContext pageContext) throws QueryException {
        back.push(pageContext);
        forward.clear();
        displayCurrentPage();
    }

    private void reload() {
        displayCurrentPage();
    }

    private void updateLocationBar() {
        backButton.setDisable(back.size() <= 1);
        forwardButton.setDisable(forward.isEmpty());
    }
}
