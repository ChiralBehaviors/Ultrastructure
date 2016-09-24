package com.chiralbehaviors.graphql.layout;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;

public class AutoLayoutController {
    @FXML
    public Button     toggle;
    public Node       graphiql;
    @FXML
    public AnchorPane anchor;
    @FXML
    public Button     refresh;
    @FXML
    public BorderPane root;
    @FXML
    public Button     showSchema;
    private WebEngine webEngine;

    public AutoLayoutController(AutoLayoutView layout) throws IOException {
        AnchorPane.setTopAnchor(layout, 0.0);
        AnchorPane.setLeftAnchor(layout, 0.0);
        AnchorPane.setBottomAnchor(layout, 0.0);
        AnchorPane.setRightAnchor(layout, 0.0);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);
        loader.setLocation(getClass().getResource("/com/chiralbehaviors/graphql/layout/autolayout.fxml"));
        loader.load();
        anchor.getChildren()
              .add(layout);

        loader = new FXMLLoader(getClass().getResource("/com/chiralbehaviors/graphql/layout/graphiql.fxml"));
        graphiql = loader.load();
        AnchorPane.setTopAnchor(graphiql, 0.0);
        AnchorPane.setLeftAnchor(graphiql, 0.0);
        AnchorPane.setBottomAnchor(graphiql, 0.0);
        AnchorPane.setRightAnchor(graphiql, 0.0);
        GraphiqlController controller = loader.getController();
        webEngine = controller.webview.getEngine();

        AtomicBoolean queryHidden = new AtomicBoolean(true);
        toggle.setOnAction(e -> {
            anchor.getChildren()
                  .clear();
            if (queryHidden.get()) {
                queryHidden.set(false);
                toggle.setText("Hide Query");
                anchor.getChildren()
                      .add(graphiql);
            } else {
                queryHidden.set(true);
                toggle.setText("Show Query");
                anchor.getChildren()
                      .add(layout);
            }
        });
    }

    public WebEngine getEngine() {
        return webEngine;
    }
}
