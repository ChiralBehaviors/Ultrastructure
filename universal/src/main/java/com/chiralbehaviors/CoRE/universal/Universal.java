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
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.layout.AutoLayoutView;
import com.chiralbehaviors.layout.Layout.LayoutModel;
import com.chiralbehaviors.layout.graphql.GraphQlUtil;
import com.chiralbehaviors.layout.graphql.GraphQlUtil.QueryException;
import com.chiralbehaviors.layout.graphql.GraphQlUtil.QueryRequest;
import com.chiralbehaviors.layout.schema.Relation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.StringArgGenerator;
import com.fasterxml.uuid.impl.NameBasedGenerator;
import com.hellblazer.utils.Utils;

import javafx.application.Application;
import javafx.application.Platform;
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
    public static final String              GET_APPLICATION_QUERY;
    public static final String              GET_APPLICATION_QUERY_RESOURCE           = "getApplication.query";
    public static final String              GET_APPLICATIONS_QUERY_RESOURCE          = "getApplications.query";
    public static final String              SINGLE_PAGE_APPLICATION                  = "singlePageApplication";
    public static final String              SINGLE_PAGE_APPLICATIONS                 = "singlePageApplications";
    public static final String              SINGLE_PAGE_URI                          = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/uaas/single-page";
    public static final UUID                SINGLE_PAGE_UUID;
    public static final String              UNIVERSAL_APP_ID                         = "universal.app.id";
    public static final String              UNIVERSAL_ENDPOINT                       = "universal.endpoint";

    private static final String             ALLOW_RESTRICTED_HEADERS_SYSTEM_PROPERTY = "sun.net.http.allowRestrictedHeaders";
    private static final Logger             log                                      = LoggerFactory.getLogger(Universal.class);
    private static String                   SPA_WSP;
    private static final StringArgGenerator URL_UUID_GENERATOR;
    private static final String             URN_UUID                                 = "urn:uuid:";

    static {
        URL_UUID_GENERATOR = Generators.nameBasedGenerator(NameBasedGenerator.NAMESPACE_URL);
        System.setProperty(ALLOW_RESTRICTED_HEADERS_SYSTEM_PROPERTY, "true");
        try {
            GET_APPLICATION_QUERY = Utils.getDocument(Universal.class.getResourceAsStream(GET_APPLICATION_QUERY_RESOURCE));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        SINGLE_PAGE_UUID = uuidOf(SINGLE_PAGE_URI);
        try {
            SPA_WSP = URLEncoder.encode(SINGLE_PAGE_UUID.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static String textOrNull(JsonNode node) {
        return node == null ? null : node.asText();
    }

    public static UUID uuidOf(String url) {
        if (url.startsWith(URN_UUID)) {
            return UUID.fromString(url.substring(URN_UUID.length()));
        }
        return URL_UUID_GENERATOR.generate(url);
    }

    private AnchorPane           anchor;
    private Spa                  application;
    private final Stack<Context> back    = new Stack<>();
    private Button               backButton;
    private WebTarget            endpoint;
    private final Stack<Context> forward = new Stack<>();
    private Button               forwardButton;
    private String               frame;
    private AutoLayoutView       layout;
    private Stage                primaryStage;

    private Button               reloadButton;

    @Override
    public void apply(ListView<JsonNode> list, Relation relation) {
        list.setOnMouseClicked(event -> {
            if (list.getItems()
                    .isEmpty()
                || event.getButton() != MouseButton.PRIMARY
                || event.getClickCount() < 2) {
                return;
            }
            doubleClick(list.getSelectionModel()
                            .getSelectedItem(),
                        relation);
        });
    }

    @Override
    public void apply(TableRow<JsonNode> row, Relation relation) {
        row.setOnMouseClicked(event -> {
            if (row.isEmpty() || event.getButton() != MouseButton.PRIMARY
                || event.getClickCount() < 2) {
                return;
            }
            doubleClick(row.getItem(), relation);
        });
    }

    public Spa getApplication() {
        return application;
    }

    public String getFrame() {
        return frame;
    }

    @Override
    public void start(Stage primaryStage) throws IOException,
                                          URISyntaxException, QueryException {
        this.primaryStage = primaryStage;
        anchor = new AnchorPane();
        VBox vbox = new VBox(locationBar(), anchor);
        primaryStage.setScene(new Scene(vbox, 800, 600));
        places();
        primaryStage.show();
    }

    private String appLauncherId() {
        WebTarget webTarget = endpoint.path(SPA_WSP)
                                      .path("meta");
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", "AppLauncher");
        try {
            return GraphQlUtil.evaluate(webTarget,
                                        new QueryRequest("query q($name: String!) { lookup(name: $name) }",
                                                         variables))
                              .get("lookup")
                              .asText();
        } catch (QueryException e) {
            throw new IllegalStateException(e);
        }
    }

    private Universal applicationFrom(JsonNode item, Launch launch) {
        String frame = back.peek()
                           .getFrame();
        if (launch.getFrame() != null) {
            frame = launch.getFrame();
        } else if (launch.getFrameBy() != null) {
            JsonNode launchFrame = apply(item, launch.getFrameBy());
            if (launchFrame == null) {
                log.warn("Invalid routing frame by {} : {}",
                         launch.getFrameBy(), item);
            } else {
                frame = launchFrame.asText();
            }
        }
        String application = launch.getImmediate();
        if (launch.getLaunchBy() != null) {
            JsonNode applicationNode = apply(item, launch.getLaunchBy());
            if (applicationNode != null) {
                application = applicationNode.asText();
            }
        }
        Universal unitard = new Universal();
        unitard.setFrame(frame);
        unitard.setApplication(resolve(application));
        unitard.setEndpoint(endpoint);
        return unitard;
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

    private void doubleClick(JsonNode item, Relation relation) {
        if (item == null) {
            return;
        }
        Context current = back.peek();

        // favor routes over launch
        Route route = current.getNavigation(relation);
        if (route != null) {
            try {
                push(extract(current.getFrame(), route, item));
            } catch (QueryException e) {
                log.error("Unable to push page: %s", route.getPath(), e);
            }
        }

        // try a launch if no routes
        Launch launch = current.getLaunch(relation);
        if (launch == null) {
            return;
        }

        try {
            launch(item, launch);
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Cannot launch %s",
                                                          relation.getField()),
                                            e);
        }
    }

    private URI endpointUri() throws URISyntaxException {

        String endpoint = System.getProperty(UNIVERSAL_ENDPOINT);
        if (endpoint != null) {
            return new URI(endpoint);
        }

        endpoint = getParameters().getNamed()
                                  .get("endpoint");
        if (endpoint == null) {
            log.error("No universal endpoint defined");
            throw new IllegalStateException("No universal endpoint defined");
        }
        return new URI(endpoint);
    }

    private Context extract(String workspace, Route route, JsonNode item) {
        Map<String, Object> variables = new HashMap<>();
        route.getExtract()
             .entrySet()
             .stream()
             .forEach(entry -> {
                 variables.put(entry.getKey(), apply(item, entry.getValue()));
             });

        Page target = application.route(route.getPath());
        String frame = workspace;
        if (target.getFrame() != null) {
            frame = target.getFrame();
        } else if (route.getFrameBy() != null) {
            JsonNode routedFrame = apply(item, route.getFrameBy());
            if (routedFrame == null) {
                log.warn("Invalid routing frame by {} route: {}",
                         route.getFrameBy(), route.getPath());
            }
            frame = routedFrame.asText();
        }
        return new Context(frame, target, variables);
    }

    private void forward() {
        back.push(forward.pop());
        displayCurrentPage();
    }

    private String initialFrame() {
        if (frame != null) {
            return frame;
        }
        String id = getParameters().getNamed()
                                   .get("frame");
        if (id != null) {
            return id;
        }
        id = application.getFrame();
        if (id != null) {
            return id;
        }
        log.info("No frame defined, using single page app workspace frame");
        return SPA_WSP;
    }

    private Spa initialSpa() {
        if (application != null) {
            return application;
        }
        String id = getParameters().getNamed()
                                   .get("app");
        if (id == null) {
            log.info("No application id defined, using default app launcher");
            return resolve(appLauncherId());
        }
        return resolve(id);
    }

    private void launch(JsonNode item, Launch launch) throws Exception {
        Universal unitard = applicationFrom(item, launch);
        Platform.runLater(() -> {
            Stage stageLeft = new Stage();
            Platform.setImplicitExit(false);
            try {
                unitard.start(stageLeft);
            } catch (Exception e) {
                log.error("Unable to launch: %s", launch, e);
            }
        });
    }

    private AutoLayoutView layout(Context pageContext) throws QueryException {
        AutoLayoutView layout = new AutoLayoutView(pageContext.getRoot(), this);
        layout.getStylesheets()
              .add(getClass().getResource("/non-nested.css")
                             .toExternalForm());
        JsonNode evaluated = pageContext.evaluate(endpoint);
        layout.setData(evaluated);
        layout.measure(evaluated);
        AnchorPane.setTopAnchor(layout, 0.0);
        AnchorPane.setLeftAnchor(layout, 0.0);
        AnchorPane.setBottomAnchor(layout, 0.0);
        AnchorPane.setRightAnchor(layout, 0.0);
        return layout;
    }

    private HBox locationBar() {
        HBox hbox = new HBox();

        backButton = button("back.png");
        forwardButton = button("forward.png");
        reloadButton = button("reload.png");

        backButton.setOnAction(e -> back());
        forwardButton.setOnAction(e -> forward());
        reloadButton.setOnAction(e -> reload());

        hbox.getChildren()
            .addAll(backButton, forwardButton, reloadButton);

        return hbox;
    }

    private void places() throws URISyntaxException, QueryException {
        if (endpoint == null) {
            endpoint = ClientBuilder.newClient()
                                    .target(endpointUri());
        }
        application = initialSpa();
        frame = initialFrame();
        push(new Context(frame, application.getRoot()));
    }

    private void push(Context pageContext) throws QueryException {
        back.push(pageContext);
        forward.clear();
        displayCurrentPage();
    }

    private void reload() {
        displayCurrentPage();
    }

    private Spa resolve(String application) {
        WebTarget webTarget = endpoint.path(SPA_WSP);
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", application);
        ObjectNode app;
        try {
            app = (ObjectNode) GraphQlUtil.evaluate(webTarget,
                                                    new QueryRequest(GET_APPLICATION_QUERY,
                                                                     variables))
                                          .get(SINGLE_PAGE_APPLICATION);
        } catch (QueryException e) {
            String msg = String.format("cannot resolve application: %s",
                                       application);
            log.error(msg, e);
            throw new IllegalStateException(msg, e);
        }
        return new Spa(app);
    }

    private void setApplication(Spa application) {
        this.application = application;
    }

    private void setEndpoint(WebTarget endpoint) {
        this.endpoint = endpoint;
    }

    private void setFrame(String frame) {
        this.frame = frame;
    }

    private void updateLocationBar() {
        backButton.setDisable(back.size() <= 1);
        forwardButton.setDisable(forward.isEmpty());
    }
}
