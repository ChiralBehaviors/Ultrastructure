package com.chiralbehaviors.CoRE.occular;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.ws.rs.client.ClientBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class Occular extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public static ObservableValue<String> path(ObjectNode in, String path) {
        JsonNode node = in;
        for (String segment : path.split("/")) {
            node = node.get(segment);
        }
        return new SimpleStringProperty(node.asText());
    }

    private OccularController controller;
    private Stage             primaryStage;
    private TabPane           rootLayout;

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void initRootLayout(GraphQlApi api, URL url) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Occular.class.getResource("view/OccularView.fxml"));
        rootLayout = (TabPane) loader.load();

        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
        controller = loader.getController();
        controller.setApi(api, url);
    }

    @Override
    public void start(Stage primaryStage) {
        Parameters params = getParameters();
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Workspace View");
        String encoded;
        try {
            encoded = URLEncoder.encode("urn:uuid:00000000-0000-0004-0000-000000000003",
                                        "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
        String baseUrl = params.getRaw()
                               .get(0);
        GraphQlApi api = new GraphQlApi(ClientBuilder.newClient()
                                                     .target(String.format("%s/workspace/%s/meta",
                                                                           baseUrl,
                                                                           encoded)),
                                        null);
        URL url;
        try {
            url = new URL(new URL(baseUrl),
                          String.format("ide?workspace=%s", encoded));
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
        try {
            initRootLayout(api, url);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
