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
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * 
 * @author hhildebrand
 *
 */
public class SinglePageApp extends Application {

    private GraphqlApplication application;
    

    public static void main(String[] args) {
        launch(args);
    }

    public void initRootLayout(Stage primaryStage) throws IOException {

        Map<String, String> parameters = getParameters().getNamed();
        application = new ObjectMapper(new YAMLFactory()).readValue(getClass().getResourceAsStream(parameters.get("app")),
                                                                    GraphqlApplication.class);
        AnchorPane anchor = new AnchorPane();
        primaryStage.setScene(new Scene(anchor, 800, 600));
        primaryStage.show();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        initRootLayout(primaryStage);
    }

}
