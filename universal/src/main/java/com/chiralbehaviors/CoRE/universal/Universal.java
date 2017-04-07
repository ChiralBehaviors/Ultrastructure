/**
 * Copyright (c) 2017 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.universal;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.universal.Page.Route;
import com.chiralbehaviors.layout.AutoLayoutView;
import com.chiralbehaviors.layout.Layout.LayoutModel;
import com.chiralbehaviors.layout.graphql.GraphQlUtil.QueryException;
import com.chiralbehaviors.layout.schema.Relation;
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
public class Universal extends Application implements LayoutModel {

    public static final String  UNIVERSAL_ENDPOINT                       = "universal.endpoint";
    private static final String ALLOW_RESTRICTED_HEADERS_SYSTEM_PROPERTY = "sun.net.http.allowRestrictedHeaders";
    private static final Logger log                                      = LoggerFactory.getLogger(Universal.class);

    static {
        System.setProperty(ALLOW_RESTRICTED_HEADERS_SYSTEM_PROPERTY, "true");
    }

    public static void main(String[] args) {
        launch(args);
    }

    private AnchorPane           anchor;
    private Spa                  application;
    private final Stack<Context> back    = new Stack<>();
    private Button               backButton;
    private WebTarget            endpoint;
    private final Stack<Context> forward = new Stack<>();
    private Button               forwardButton;
    private AutoLayoutView       layout;
    private Stage                primaryStage;
    private Button               reloadButton;

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
                                                                    Spa.class);
        endpoint = ClientBuilder.newClient()
                                .target(endpointUri(parameters.get("endpoint")));
        push(new Context(application.getRoot()));
        primaryStage.show();
    }

    private JsonNode apply(JsonNode node, String path) {
        StringTokenizer tokens = new StringTokenizer(path, "/");
        JsonNode current = node;
        while (tokens.hasMoreTokens()) {
            node = current.get(tokens.nextToken());
            current = node;
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
        Context pageContext = back.peek();
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

    private URI endpointUri(String endpoint) throws URISyntaxException {
        if (endpoint != null) {
            return new URI(endpoint);
        }
        if (System.getProperty(UNIVERSAL_ENDPOINT) == null) {
            log.error("No universal endpoint defined");
            throw new IllegalStateException("No universal endpoint defined");
        }
        return new URI(System.getProperty(UNIVERSAL_ENDPOINT));
    }

    private Context extract(Route route, JsonNode item) {
        Map<String, Object> variables = new HashMap<>();
        route.getExtract()
             .entrySet()
             .stream()
             .forEach(entry -> {
                 variables.put(entry.getKey(), apply(item, entry.getValue()));
             });

        Page target = application.route(route.getPath());
        return new Context(target, variables);
    }

    private void forward() {
        back.push(forward.pop());
        displayCurrentPage();
    }

    private AutoLayoutView layout(Context pageContext) throws QueryException {
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

    private void push(Context pageContext) throws QueryException {
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
