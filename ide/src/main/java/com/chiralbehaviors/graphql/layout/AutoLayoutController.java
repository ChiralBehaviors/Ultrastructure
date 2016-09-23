package com.chiralbehaviors.graphql.layout;

import java.io.IOException;

import org.controlsfx.control.MasterDetailPane;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class AutoLayoutController {
    @FXML
    public BorderPane       root;
    @FXML
    public MasterDetailPane masterDetail;
    @FXML
    public Button           edit;
    @FXML
    public Button           refresh;

    public AutoLayoutController() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);
        loader.setLocation(getClass().getResource("/com/chiralbehaviors/graphql/layout/autolayout.fxml"));
        loader.load();

        loader = new FXMLLoader(getClass().getResource("/com/chiralbehaviors/graphql/layout/graphiql.fxml"));
        Node detail = loader.load();
        GraphiqlController graphiql = loader.getController();
        masterDetail.setDetailNode(detail);

        edit.setOnAction(e -> masterDetail.setShowDetailNode(true));
        graphiql.close.setOnAction(e -> masterDetail.setShowDetailNode(false));
    }
}
