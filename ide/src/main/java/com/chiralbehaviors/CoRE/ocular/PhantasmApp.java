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

import java.net.URL;
import java.util.Arrays;

import javax.ws.rs.core.UriBuilder;

import com.chiralbehaviors.CoRE.ocular.diagram.WorkspaceDomainObjectProvider;

import de.fxdiagram.core.XDiagram;
import de.fxdiagram.core.XNode;
import de.fxdiagram.core.XRoot;
import de.fxdiagram.core.layout.LayoutType;
import de.fxdiagram.core.tools.actions.CenterAction;
import de.fxdiagram.core.tools.actions.CloseAction;
import de.fxdiagram.core.tools.actions.DeleteAction;
import de.fxdiagram.core.tools.actions.ExitAction;
import de.fxdiagram.core.tools.actions.ExportSvgAction;
import de.fxdiagram.core.tools.actions.FullScreenAction;
import de.fxdiagram.core.tools.actions.LayoutAction;
import de.fxdiagram.core.tools.actions.LoadAction;
import de.fxdiagram.core.tools.actions.NavigateNextAction;
import de.fxdiagram.core.tools.actions.NavigatePreviousAction;
import de.fxdiagram.core.tools.actions.OpenAction;
import de.fxdiagram.core.tools.actions.RedoAction;
import de.fxdiagram.core.tools.actions.RevealAction;
import de.fxdiagram.core.tools.actions.SaveAction;
import de.fxdiagram.core.tools.actions.SelectAllAction;
import de.fxdiagram.core.tools.actions.UndoAction;
import de.fxdiagram.core.tools.actions.ZoomToFitAction;
import de.fxdiagram.examples.LazyExampleDiagram;
import de.fxdiagram.lib.actions.UndoRedoPlayerAction;
import de.fxdiagram.lib.simple.OpenableDiagramNode;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author hhildebrand
 *
 */
public class PhantasmApp extends Application {
    public static void main(final String... args) {
        Application.launch(args);
    }

    private XRoot      root;
    private UriBuilder base;

    @Override
    public void start(Stage primaryStage) throws Exception {
        String baseUrl = getParameters().getRaw()
                                        .get(0);
        base = UriBuilder.fromUri(new URL(baseUrl).toURI());
        root = new XRoot();
        Scene scene = new Scene(root, 1024, 768);
        scene.setCamera(new PerspectiveCamera());
        root.activate();
        XDiagram diagram = new XDiagram();
        root.setRootDiagram(diagram);

        root.domainObjectProvidersProperty()
            .add(new WorkspaceDomainObjectProvider(null, null));

        root.getDiagramActionRegistry()
            .operator_add(Arrays.asList(new CenterAction(), new ExitAction(),
                                        new DeleteAction(),
                                        new LayoutAction(LayoutType.DOT),
                                        new ExportSvgAction(), new RedoAction(),
                                        new UndoAction(), new RevealAction(),
                                        new LoadAction(), new SaveAction(),
                                        new SelectAllAction(),
                                        new ZoomToFitAction(),
                                        new NavigatePreviousAction(),
                                        new NavigateNextAction(),
                                        new OpenAction(), new CloseAction(),
                                        new FullScreenAction(),
                                        new UndoRedoPlayerAction()));
        OpenableDiagramNode node = new OpenableDiagramNode("Basic");
        node.setInnerDiagram(new LazyExampleDiagram(""));
        diagram.getNodes()
               .add(node);

        ObservableList<XNode> allNodes = diagram.getNodes();
        double deltaX = scene.getWidth() / (allNodes.size() + 2);
        double deltaY = scene.getHeight() / (allNodes.size() + 2);
        node.setLayoutX(5 * deltaX - node.getLayoutBounds()
                                         .getWidth()
                                     / 2);
        node.setLayoutY(5 * deltaY - node.getLayoutBounds()
                                         .getHeight()
                                     / 2);
        // new Layouter();
        Platform.runLater(() -> diagram.centerDiagram(true));
        primaryStage.setTitle("FXDiagram Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
