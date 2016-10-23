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
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Stack;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import com.chiralbehaviors.CoRE.ocular.PageContext.QueryException;
import com.chiralbehaviors.graphql.layout.AutoLayoutView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

    private GraphqlApplication       application;
    private final Stack<PageContext> history = new Stack<>();
    private AnchorPane               anchor;
    private WebTarget                endpoint;
    private AutoLayoutView           layout;
    private Stage                    primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    public void initRootLayout(Stage ps) throws IOException, URISyntaxException,
                                         QueryException {
        primaryStage = ps;
        anchor = new AnchorPane();
        primaryStage.setScene(new Scene(anchor, 800, 600));
        Map<String, String> parameters = getParameters().getNamed();
        application = new ObjectMapper(new YAMLFactory()).readValue(getClass().getResourceAsStream(parameters.get("app")),
                                                                    GraphqlApplication.class);
        endpoint = ClientBuilder.newClient()
                                .target(application.getEndpoint()
                                                   .toURI());
        push(new PageContext(application.getRoot()));
        primaryStage.show();
    }

    private void push(PageContext pageContext) throws QueryException {
        history.push(pageContext);
        anchor.getChildren()
              .clear();
        layout = layout(pageContext);
        AnchorPane.setTopAnchor(layout, 0.0);
        AnchorPane.setLeftAnchor(layout, 0.0);
        AnchorPane.setBottomAnchor(layout, 0.0);
        AnchorPane.setRightAnchor(layout, 0.0);
        anchor.getChildren()
              .add(layout);
        ObjectNode data = pageContext.evaluate(endpoint);
        layout.measure(data);
        layout.setData(data);
        primaryStage.setTitle(pageContext.getPage()
                                         .getTitle());
    }

    private AutoLayoutView layout(PageContext pageContext) throws QueryException {
        AutoLayoutView layout = new AutoLayoutView(pageContext.getRoot());
        layout.getStylesheets()
              .add(getClass().getResource("/nested.css")
                             .toExternalForm());
        ObjectNode data = pageContext.evaluate(endpoint);
        layout.setData(data);
        layout.measure(data);
        return layout;
    }

    @Override
    public void start(Stage primaryStage) throws IOException,
                                          URISyntaxException, QueryException {
        initRootLayout(primaryStage);
    }

}
