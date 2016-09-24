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

import com.chiralbehaviors.graphql.layout.AutoLayoutController;
import com.chiralbehaviors.graphql.layout.AutoLayoutView;

import javafx.application.Application;
import javafx.scene.Scene;
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

    public void initRootLayout(Stage primaryStage) throws IOException {
        String source = "allFilms";

        AutoLayoutView layout = new AutoLayoutView();

        AutoLayoutController controller = new AutoLayoutController(layout,
                                                                   source);

        Scene scene = new Scene(controller.getRoot(), 800, 800);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        initRootLayout(primaryStage);
    }

}
