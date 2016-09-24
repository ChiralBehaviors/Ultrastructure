package com.chiralbehaviors.graphql.layout;

import java.io.IOException;

import org.controlsfx.control.HiddenSidesPane;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class AutoLayoutController {
    @FXML
    public Button             edit;
    public GraphiqlController graphiql;
    @FXML
    public HiddenSidesPane   masterDetail;
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
        masterDetail.setTop(detail);
        
        edit.setOnAction(e -> masterDetail.setPinnedSide(Side.TOP));
        graphiql.close.setOnAction(e -> masterDetail.setPinnedSide(null));
    }
}
