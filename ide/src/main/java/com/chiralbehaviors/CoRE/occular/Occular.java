package com.chiralbehaviors.CoRE.occular;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.ws.rs.client.ClientBuilder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class Occular extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Stage             primaryStage;
    private TabPane           rootLayout;
    private OccularController controller;

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void initRootLayout(GraphQlApi api) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Occular.class.getResource("view/OccularView.fxml"));
        rootLayout = (TabPane) loader.load();

        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
        controller = loader.getController();
        controller.setApi(api);
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
        GraphQlApi api = new GraphQlApi(ClientBuilder.newClient()
                                                     .target(String.format("%s/workspace/%s/meta",
                                                                           params.getRaw()
                                                                                 .get(0),
                                                                           encoded)),
                                        null);
        try {
            initRootLayout(api);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
