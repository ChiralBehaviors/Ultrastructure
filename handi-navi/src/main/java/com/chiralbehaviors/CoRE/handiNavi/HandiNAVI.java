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
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.RecordsFactory;
import com.chiralbehaviors.CoRE.kernel.KernelUtil;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.phantasm.service.commands.LoadSnapshotCommand;
import com.chiralbehaviors.CoRE.phantasm.service.commands.LoadWorkspaceCommand;
import com.chiralbehaviors.CoRE.phantasm.service.config.PhantasmConfiguration;
import com.chiralbehaviors.CoRE.utils.CoreDbConfiguration;

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
public class HandiNAVI extends JFrame {
    private static final Logger log              = LoggerFactory.getLogger(HandiNAVI.class);
    private static final long   serialVersionUID = 1L;

    public static void main(String[] args) {

        System.setProperty("apple.laf.useScreenMenuBar", "true");
        SwingUtilities.invokeLater(() -> {
            HandiNAVI app = new HandiNAVI();
            app.setBounds(10, 10, 1024, 768);
            app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            app.setVisible(true);
        });

    }

    public static void runAnotherApp(Class<? extends Application> anotherAppClass) throws Exception {
        @SuppressWarnings("deprecation")
        Application app2 = anotherAppClass.newInstance();
        Stage anotherStage = new Stage();
        Platform.setImplicitExit(false);
        app2.start(anotherStage);
    }

    @FXML
    MenuBar                     menubar;

    @FXML
    VBox                        vbox;

    private LocalNAVI           navi;

    private final AtomicBoolean naviRunning = new AtomicBoolean();

    public HandiNAVI() {

        this.setLayout(new FlowLayout(FlowLayout.CENTER));

        JMenuBar menubar = new JMenuBar();

        JMenu localNavi = new JMenu("Local Navi");
        JMenu helpMenu = new JMenu("Help");

        JMenuItem loginItem = new JMenuItem("Login");
        loginItem.addActionListener((evt) -> {
            Platform.runLater(() -> login());
        });

        JMenuItem closeItem = new JMenuItem("Close");
        closeItem.addActionListener((evt) -> {
            Platform.exit();
            System.exit(0);
        });

        JMenuItem resetItem = new JMenuItem("Reset Instance");
        resetItem.addActionListener((evt) -> {
            Platform.runLater(() -> resetInstance());
        });

        localNavi.add(loginItem);
        localNavi.add(resetItem);
        localNavi.add(closeItem);

        menubar.add(localNavi);
        menubar.add(helpMenu);

        this.setJMenuBar(menubar);
        URL iconURL = getClass().getResource("handiNAVI.ico");
        // iconURL is null when not found
        ImageIcon icon = new ImageIcon(iconURL);
        this.setIconImage(icon.getImage());
        this.setName("HandiNAVI");
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
        JFXPanel jfxPanel = new JFXPanel();
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass()
                                                   .getResource("HandiNavi.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.load();
        Parent p = fxmlLoader.getRoot();
        Scene scene = new Scene(p);
        jfxPanel.setScene(scene);
        return jfxPanel;
    }

    private void login() {
        if (!naviRunning.compareAndSet(false, true)) {
            return;
        }
        PasswordDialog pd = new PasswordDialog();
        Optional<String> result = pd.showAndWait();
        if (!result.isPresent()) {
            naviRunning.set(false);
        }
        System.setProperty(EmbeddedConfiguration.NAVI_PASSWORD, result.get());
        Platform.runLater(() -> {
            try {
                navi = LocalNAVI.runLocal(new String[0]);
            } catch (Exception e) {
                e.printStackTrace();
                close();
            }
        });
    }

    private void reset() {
        PhantasmConfiguration configuration = navi.getConfiguration();
        log.info("Reinitializing database state");
        try (DSLContext create = configuration.create()) {
            create.transaction(config -> RecordsFactory.clear(DSL.using(config)));
        }
        try (DSLContext create = configuration.create()) {
            create.transaction(config -> {
                log.info("Loading kernel");
                DSLContext txnlCreate = DSL.using(config);
                KernelUtil.loadKernel(txnlCreate);
                log.info("Initializing instance");
                KernelUtil.initializeInstance(new ModelImpl(txnlCreate),
                                              CoreDbConfiguration.CORE,
                                              "CoRE instance");
            });
            log.info("Loading workspace state: {}",
                     configuration.getWorkspaces());
            LoadWorkspaceCommand.loadWorkspaces(configuration.getWorkspaces(),
                                                create);
            log.info("Loading snapshot state: {}",
                     configuration.getSnapshots());
            LoadSnapshotCommand.loadSnapshots(configuration.getSnapshots(),
                                              create);
        } catch (Exception e) {
            log.error("Unable to reinitalize instanvce", e);
        }
    }

    private void resetInstance() {
        if (!naviRunning.get()) {
            return;
        }
        Platform.runLater(() -> reset());
    }
}
