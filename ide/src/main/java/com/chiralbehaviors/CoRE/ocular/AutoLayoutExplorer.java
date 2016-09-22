/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.ocular;

import java.io.FileInputStream;
import java.io.IOException;

import com.chiralbehaviors.graphql.layout.AutoLayoutView;
import com.chiralbehaviors.graphql.layout.Relation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellblazer.utils.Utils;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * A class to allow me to explore the autolayout. I hate UIs.
 * 
 * @author hhildebrand
 *
 */
public class AutoLayoutExplorer extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        initRootLayout(primaryStage);
    }

    public void initRootLayout(Stage primaryStage) throws IOException {
        String input = Utils.getDocument(new FileInputStream("src/test/resources/testQuery.gql"));
        String source = "films";
        Relation schema = (Relation) Relation.buildSchema(input, source);
        JsonNode data = new ObjectMapper().readTree(new FileInputStream("src/test/resources/testQuery.data"));
        data = data.get("data")
                   .get(source);

        SplitPane root = new SplitPane();
        root.setOrientation(Orientation.VERTICAL);
        root.setDividerPositions(0.4f, 0.6f);

        WebView webView = new WebView();
        AnchorPane.setTopAnchor(webView, 0.0);
        AnchorPane.setLeftAnchor(webView, 0.0);
        AnchorPane.setRightAnchor(webView, 0.0);
        AnchorPane.setBottomAnchor(webView, 0.0);
        AnchorPane wvAnchor = new AnchorPane();
        wvAnchor.getChildren()
                .add(webView);

        AutoLayoutView layout = new AutoLayoutView(schema);

        layout.measure(data);
        layout.setData(data);

        root.getItems()
            .add(wvAnchor);
        root.getItems()
            .add(layout);
        primaryStage.setTitle(schema.getLabel());
        Scene scene = new Scene(root, 800, 800);
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}
