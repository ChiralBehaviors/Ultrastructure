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

import com.chiralbehaviors.graphql.layout.AutoLayoutController;
import com.chiralbehaviors.graphql.layout.AutoLayoutView;
import com.chiralbehaviors.graphql.layout.Relation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellblazer.utils.Utils;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;
import netscape.javascript.JSObject; 

/**
 * A class to allow me to explore the autolayout. I hate UIs.
 * 
 * @author hhildebrand
 *
 */
public class AutoLayoutExplorer extends Application {

    public class App {
        public void onEditOperationName(String newOperationName) {
            System.out.println(String.format("New operation name: %s",
                                             newOperationName));
        }

        public void onEditQuery(String newQuery) {
            System.out.println(String.format("New query: %s", newQuery));
        }

        public void onEditVariables(String newVariables) {
            System.out.println(String.format("New variables: %s",
                                             newVariables));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void initRootLayout(Stage primaryStage) throws IOException {
        String input = Utils.getDocument(new FileInputStream("src/test/resources/testQuery.gql"));
        String source = "films";
        Relation schema = (Relation) Relation.buildSchema(input, source);
        JsonNode data = new ObjectMapper().readTree(new FileInputStream("src/test/resources/testQuery.data"));
        data = data.get("data")
                   .get(source);

        AutoLayoutView layout = new AutoLayoutView(schema);

        layout.measure(data);
        layout.setData(data);

        AutoLayoutController controller = new AutoLayoutController();
        controller.masterDetail.setContent(layout);
        WebEngine webEngine = controller.graphiql.webview.getEngine();
        webEngine.getLoadWorker()
                 .stateProperty()
                 .addListener((ChangeListener<State>) (ov, oldState,
                                                       newState) -> {
                     if (newState == Worker.State.SUCCEEDED) {
                         JSObject jsobj = (JSObject) webEngine.executeScript("window");
                         jsobj.setMember("app", new App());
                     }
                 });

        primaryStage.setTitle(schema.getLabel());
        Scene scene = new Scene(controller.root, 800, 800);
        primaryStage.setScene(scene);

        primaryStage.show();
        webEngine.load(getClass().getResource("/com/chiralbehaviors/graphql/layout/ide.html")
                       .toExternalForm());
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        initRootLayout(primaryStage);
    }
}
