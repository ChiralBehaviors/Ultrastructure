package com.chiralbehaviors.graphql.layout;

import java.io.IOException;

import org.controlsfx.control.MasterDetailPane;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class AutoLayoutController {
    @FXML
    public Button             edit;
    public GraphiqlController graphiql;
    @FXML
    public MasterDetailPane   masterDetail;
    @FXML
    public Button             refresh;
    @FXML
    public BorderPane         root;
    @FXML
    public Button             showSchema;

    public AutoLayoutController() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);
        loader.setLocation(getClass().getResource("/com/chiralbehaviors/graphql/layout/autolayout.fxml"));
        loader.load();

        loader = new FXMLLoader(getClass().getResource("/com/chiralbehaviors/graphql/layout/graphiql.fxml"));
        Node detail = loader.load();
        graphiql = loader.getController();

        AnchorPane.setTopAnchor(graphiql.webview, 0.0);
        AnchorPane.setLeftAnchor(graphiql.webview, 0.0);
        AnchorPane.setRightAnchor(graphiql.webview, 0.0);
        AnchorPane.setBottomAnchor(graphiql.webview, 0.0);
        masterDetail.setDetailNode(detail);
        
        edit.setOnAction(e -> masterDetail.setShowDetailNode(true));
        graphiql.close.setOnAction(e -> masterDetail.setShowDetailNode(false));
    }
}
