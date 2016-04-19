package com.chiralbehaviors.CoRE.occular;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.ws.rs.client.ClientBuilder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Occular extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Stage      primaryStage;
    private BorderPane rootLayout;

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Occular.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showView(GraphQlApi api) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Occular.class.getResource("view/FacetsView.fxml"));
            AnchorPane workspaceView = (AnchorPane) loader.load();

            rootLayout.setCenter(workspaceView);
            FacetsController facetsController = (FacetsController) loader.getController();
            facetsController.setApi(api);
            facetsController.update();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Workspace View");
        String encoded;
        try {
            encoded = URLEncoder.encode("urn:uuid:00000000-0000-0004-0000-000000000003",
                                        "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
        GraphQlApi api = new GraphQlApi(ClientBuilder.newClient()
                                                     .target(String.format("http://localhost:5000/workspace/%s/meta",
                                                                           encoded)),
                                        null);
        initRootLayout();
        showView(api);
    }
}
