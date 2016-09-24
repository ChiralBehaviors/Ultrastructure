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

import java.io.IOException;

import com.chiralbehaviors.graphql.layout.GraphiqlController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;

/**
 * A class to allow me to explore the autolayout. I hate UIs.
 * 
 * @author hhildebrand
 *
 */
public class Test extends Application { 

    public static void main(String[] args) {
        launch(args);
    }

    public void initRootLayout(Stage primaryStage) throws IOException { 
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/chiralbehaviors/graphql/layout/graphiql.fxml"));
        
        StackPane p = new StackPane();
        p.getChildren().add(loader.load());
        Scene scene = new Scene(p, 800, 800);
        primaryStage.setScene(scene);

        GraphiqlController controller = (GraphiqlController) loader.getController();
        WebEngine webEngine = controller.webview.getEngine();
        webEngine.load(getClass().getResource("/com/chiralbehaviors/graphql/layout/ide.html")
                       .toExternalForm());

        primaryStage.show();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        initRootLayout(primaryStage);
    }
}
