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

package com.chiralbehaviors.CoRE.handiNavi;

import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The HandiNAVI JavaFX application
 * 
 * @author halhildebrand
 *
 */
public class HandiNaviApp extends JFrame {
    private static final long serialVersionUID = 4110897631836483138L;

    public static void main(String[] args) {

        System.setProperty("apple.laf.useScreenMenuBar", "true");
        SwingUtilities.invokeLater(() -> {
            HandiNaviApp app = new HandiNaviApp();
            app.setBounds(10, 10, 1024, 768);
            app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            app.setVisible(true);
        });

    }

    public static void runAnotherApp(Class<? extends Application> anotherAppClass) throws Exception {
        Application app2 = anotherAppClass.newInstance();
        Stage anotherStage = new Stage();
        Platform.setImplicitExit(false);
        app2.start(anotherStage);
    }

    @FXML
    MenuBar menubar;

    @FXML
    VBox    vbox;

    public HandiNaviApp() {

        this.setLayout(new FlowLayout(FlowLayout.CENTER));

        JMenuBar menubar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu helpMenu = new JMenu("Help");

        JMenuItem closeItem = new JMenuItem("Close");
        closeItem.addActionListener((evt) -> {
            Platform.exit();
            System.exit(0);
        });

        fileMenu.add(closeItem);

        menubar.add(fileMenu);
        menubar.add(editMenu);
        menubar.add(helpMenu);

        this.setJMenuBar(menubar);
        try {
            createMainFXWindow();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @FXML
    public void close() {
        Platform.exit();
    }

    @FXML
    public void initialize() {

        String macMenu = System.getProperty("apple.laf.useScreenMenuBar");

        if (macMenu != null && macMenu == "true") {
            vbox.getChildren()
                .remove(menubar);
        }
    }

    private JFXPanel createMainFXWindow() throws Exception {
        JFXPanel jfxPanel = new JFXPanel(); //  initializes the toolkit
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass()
                                                   .getResource("HandiNaviIde.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.load();
        Parent p = fxmlLoader.getRoot();
        Scene scene = new Scene(p);
        jfxPanel.setScene(scene);
        return jfxPanel;
    }
}
