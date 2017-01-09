/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 *

 * This file is part of Ultrastructure.
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

package com.chiralbehaviors.CoRE.phantasm.service.commands;

import java.io.InputStream;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.utils.CoreDbConfiguration;
import com.chiralbehaviors.CoRE.workspace.StateSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellblazer.utils.Utils;

import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

/**
 * @author hhildebrand
 *
 */
public class LoadSnapshotCommand extends Command {

    public static void loadSnapshots(List<String> list,
                                     DSLContext create) throws Exception {
        create.transaction(c -> {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new CoREModule());

            list.forEach(file -> {
                try (InputStream is = Utils.resolveResource(LoadSnapshotCommand.class,
                                                            file)) {
                    StateSnapshot snapshot = objectMapper.readValue(is,
                                                                    StateSnapshot.class);
                    snapshot.load(DSL.using(c));
                } catch (Exception e) {
                    LoggerFactory.getLogger(LoadSnapshotCommand.class)
                                 .error(String.format("unable to load snaphot: %s",
                                                      file),
                                        e);
                }
            });
        });
    }

    public LoadSnapshotCommand() {
        super("load-snap", "load snapsot state into the CoRE instance");
    }

    @Override
    public void configure(Subparser subparser) {
        subparser.addArgument("files")
                 .nargs("+")
                 .help("State snapshot files");
    }

    @Override
    public void run(Bootstrap<?> bootstrap,
                    Namespace namespace) throws Exception {
        CoreDbConfiguration config = new CoreDbConfiguration();
        config.initializeFromEnvironment();
        List<String> list = namespace.getList("files");
        loadSnapshots(list, DSL.using(config.getCoreConnection()));
    }

}
