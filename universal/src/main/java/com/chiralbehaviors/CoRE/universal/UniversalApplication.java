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

import static com.chiralbehaviors.layout.cell.control.SelectionEvent.DOUBLE_SELECT;
import static javafx.scene.layout.AnchorPane.setBottomAnchor;
import static javafx.scene.layout.AnchorPane.setLeftAnchor;
import static javafx.scene.layout.AnchorPane.setRightAnchor;
import static javafx.scene.layout.AnchorPane.setTopAnchor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.client.ClientBuilder;

import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.layout.AutoLayout;
import com.chiralbehaviors.layout.cell.LayoutCell;
import com.chiralbehaviors.layout.flowless.VirtualFlow;
import com.chiralbehaviors.layout.graphql.GraphQlUtil.QueryException;
import com.chiralbehaviors.layout.schema.Relation;
import com.chiralbehaviors.layout.style.Layout;
import com.chiralbehaviors.layout.style.Layout.LayoutObserver;
import com.fasterxml.jackson.databind.JsonNode;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * @author halhildebrand
 *
 */
public class UniversalApplication extends Application
        implements LayoutObserver {
    private static final Logger log = LoggerFactory.getLogger(UniversalApplication.class);

    public static void main(String[] args) {
        launch(args);
    }

    private Button     backButton;
    private Button     forwardButton;
    private AutoLayout layout;
    private Stage      primaryStage;
    private Button     reloadButton;
    private Universal  universal;
    private BorderPane root;

    public UniversalApplication() {
    }

    public UniversalApplication(Universal universal) {
        setUniversal(universal);
    }

    @Override
    public <T extends LayoutCell<?>> void apply(VirtualFlow<T> list,
                                                Relation relation) {
        Nodes.addInputMap(list, InputMap.consume(DOUBLE_SELECT, e -> {
            doubleClick(list.getSelectionModel()
                            .getSelectedItem(),
                        relation);
        }));
    }

    @Override
    public void start(Stage primaryStage) throws IOException,
                                          URISyntaxException, QueryException {
        this.primaryStage = primaryStage;
        root = new BorderPane();
        HBox locationBar = locationBar();
        root.setBottom(locationBar);
        primaryStage.setScene(new Scene(root, 800, 600));
        if (universal == null) {
            String spaFile = getParameters().getNamed()
                                            .get("spa");
            if (spaFile != null) {
                initialize(spaFile);
            } else {
                initialize();
            }
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
        AutoLayout old = layout;

        try {
            layout = layout(context.getRoot(), node);
        } catch (QueryException e) {
            log.error("Unable to display page", e);
            return;
        }

        layout.setMinSize(0, 0);
        layout.setPrefSize(1, 1);

        root.setCenter(layout);
        if (old != null) {
            old.dispose();
        }

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

    private void initialize() throws URISyntaxException {
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

    private void initialize(String spaFile) throws IOException {
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
        setUniversal(new Universal(frame, Spa.manifest(spaFile),
                                   ClientBuilder.newClient()
                                                .target(endpoint)));
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

    private AutoLayout layout(Relation root,
                              JsonNode node) throws QueryException {
        AutoLayout layout = new AutoLayout(root, new Layout(this));
        layout.measure(node);
        layout.updateItem(node);
        setTopAnchor(layout, 0.0);
        setLeftAnchor(layout, 0.0);
        setBottomAnchor(layout, 0.0);
        setRightAnchor(layout, 0.0);
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
