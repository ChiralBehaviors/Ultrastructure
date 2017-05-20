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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.layout.AutoLayoutView;
import com.chiralbehaviors.layout.Layout.LayoutModel;
import com.chiralbehaviors.layout.graphql.GraphQlUtil.QueryException;
import com.chiralbehaviors.layout.schema.Relation;
import com.fasterxml.jackson.databind.JsonNode;

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
 * @author halhildebrand
 *
 */
public class UniversalApplication extends Application implements LayoutModel {
    private static final Logger log = LoggerFactory.getLogger(UniversalApplication.class);

    public static void main(String[] args) {
        launch(args);
    }

    private AnchorPane     anchor;
    private Button         backButton;
    private Button         forwardButton;
    private AutoLayoutView layout;
    private Stage          primaryStage;
    private Button         reloadButton;
    private Universal      universal;

    public UniversalApplication() {
    }

    public UniversalApplication(Universal universal) {
        setUniversal(universal);
    }

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

    @Override
    public void start(Stage primaryStage) throws IOException,
                                          URISyntaxException, QueryException {
        this.primaryStage = primaryStage;
        anchor = new AnchorPane();
        VBox vbox = new VBox(locationBar(), anchor);
        primaryStage.setScene(new Scene(vbox, 800, 600));
        if (universal == null) {
            String endpoint = getParameters().getNamed()
                                             .get("endpoint");
            if (endpoint == null) {
                log.error("No universal endpoint defined");
                throw new IllegalStateException("No universal endpoint defined");
            }

            String frame = getParameters().getNamed()
                                          .get("frame");
            if (frame == null) {
                log.info("No frame defined, using single page app workspace frame");
                frame = Universal.SPA_WSP;
            }
            setUniversal(new Universal(frame, getParameters().getNamed()
                                                             .get("app"),
                                       new URI(endpoint)));
        }
        universal.places();
        universal.display();
        primaryStage.show();
    }

    private void back() {
        universal.back();
    }

    private Button button(String imageResource) {
        Button button = new Button();
        Image image = new Image(getClass().getResourceAsStream(imageResource));
        button.graphicProperty()
              .set(new ImageView(image));
        return button;
    }

    private void displayCurrentPage(JsonNode node, Context context) {
        updateLocationBar();
        primaryStage.setTitle(context.getPage()
                                     .getTitle());
        try {
            layout = layout(context.getRoot(), node);
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
        universal.navigate(item, relation);
    }

    private void forward() {
        universal.forward();
    }

    private void launch(Universal unitard) {
        Platform.runLater(() -> {
            Stage stageLeft = new Stage();
            Platform.setImplicitExit(false);
            try {
                new UniversalApplication(unitard).start(stageLeft);
            } catch (Exception e) {
                log.error("Unable to launch: %s", unitard.getApplication()
                                                         .getName(),
                          e);
            }
        });
    }

    private AutoLayoutView layout(Relation root,
                                  JsonNode node) throws QueryException {
        AutoLayoutView layout = new AutoLayoutView(root, this);
        layout.getStylesheets()
              .add(getClass().getResource("/non-nested.css")
                             .toExternalForm());
        layout.setData(node);
        layout.measure(node);
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

    private void reload() {
        universal.display();
    }

    private void setUniversal(Universal universal) {
        this.universal = universal;
        this.universal.setLauncher(u -> launch(u));
        this.universal.setDisplay((c, n) -> displayCurrentPage(n, c));
    }

    private void updateLocationBar() {
        backButton.setDisable(!universal.backwardContexts());
        forwardButton.setDisable(!universal.forwardContexts());
    }
}
