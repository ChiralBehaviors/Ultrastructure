package com.chiralbehaviors.graphql.layout;

import java.io.IOException;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final String ERRORS = "errors";
    private static final String DATA   = "data";
    private static final Logger log    = LoggerFactory.getLogger(AutoLayoutController.class);

    public class GraphiqlState {
        private String data;
        private String operationName;
        private String query;
        private String targetURL = "http://graphql-swapi.parseapp.com/";
        private String variables;

        public String getData() {
            return data;
        }

        public String getOperationName() {
            return operationName;
        }

        public String getQuery() {
            return query;
        }

        public String getTargetURL() {
            return targetURL;
        }

        public String getVariables() {
            return variables;
        }

        public void setData(String data) {
            this.data = data;
            setData.accept(data);
        }

        public void setOperationName(String operationName) {
            this.operationName = operationName;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public void setTargetURL(String targetURL) {
            this.targetURL = targetURL;
        }

        public void setVariables(String variables) {
            this.variables = variables;
        }

    }

    @FXML
    ToggleGroup                 page;
    @FXML
    private AnchorPane          anchor;
    private final GraphiqlState queryState = new GraphiqlState();
    @FXML
    private BorderPane          root;
    private Consumer<String>    setData;
    @FXML
    private RadioButton         showLayout;
    @FXML
    private RadioButton         showQuery;
    @FXML
    private RadioButton         showSchema;
    private String              source;

    public AutoLayoutController(AutoLayoutView layout,
                                String source) throws IOException {
        this.source = source;
        SchemaView schema = constructSchema();
        initialize(layout, schema);
        load();
        anchor.getChildren()
              .add(layout);

        Node graphiql = constructGraphiql();

        showLayout.setSelected(true);
        page.selectedToggleProperty()
            .addListener((o, p, c) -> {
                anchor.getChildren()
                      .clear();
                RadioButton chk = (RadioButton) c.getToggleGroup()
                                                 .getSelectedToggle();
                if (chk == showLayout) {
                    anchor.getChildren()
                          .add(layout);
                } else if (chk == showSchema) {
                    anchor.getChildren()
                          .add(schema);
                } else if (chk == showQuery) {
                    anchor.getChildren()
                          .add(graphiql);
                } else {
                    throw new IllegalStateException(String.format("Invalid radio button: %s",
                                                                  chk));
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

    private void initialize(AutoLayoutView layout, SchemaView schemaView) {
        AnchorPane.setTopAnchor(layout, 0.0);
        AnchorPane.setLeftAnchor(layout, 0.0);
        AnchorPane.setBottomAnchor(layout, 0.0);
        AnchorPane.setRightAnchor(layout, 0.0);
        setData = dataString -> {
            try {
                setData(layout, schemaView, dataString);
            } catch (Exception e) {
                log.error("cannot set data", e);
            }
        };
    }

    private void setData(AutoLayoutView layout, SchemaView schemaView,
                         String dataString) {
        JsonNode data;
        try {
            data = new ObjectMapper().readTree(dataString);
        } catch (IOException e) {
            log.warn("Cannot deserialize json data {}", dataString);
            layout.setData(JsonNodeFactory.instance.arrayNode());
            return;
        }

        if (data.has(ERRORS) || !data.get(DATA)
                                     .has(source)) {
            layout.setData(JsonNodeFactory.instance.arrayNode());
            return;
        }
        Relation schema = (Relation) Relation.buildSchema(queryState.getQuery(),
                                                          source);
        schemaView.setRoot(schema);
        data = data.get(DATA).get(source);
        layout.setRoot(schema);
        layout.measure(data);
        layout.setData(data);
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
}
