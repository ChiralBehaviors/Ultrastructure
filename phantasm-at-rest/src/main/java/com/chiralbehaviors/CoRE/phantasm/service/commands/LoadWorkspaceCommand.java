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

import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import com.chiralbehaviors.CoRE.utils.CoreDbConfiguration;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;
import com.hellblazer.utils.Utils;

import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

/**
 * @author hhildebrand
 *
 */
public class LoadWorkspaceCommand extends Command {

    public static void loadWorkspaces(List<String> list,
                                      DSLContext create) throws Exception {
        create.transaction(c -> WorkspaceSnapshot.load(DSL.using(c),
                                                       list.stream()
                                                           .map(file -> {
                                                               try {
                                                                   return Utils.resolveResourceURL(LoadWorkspaceCommand.class,
                                                                                                   file);
                                                               } catch (Exception e) {
                                                                   throw new IllegalArgumentException(String.format("Cannot resolve URL for %s",
                                                                                                                    file),
                                                                                                      e);
                                                               }
                                                           })
                                                           .filter(n -> n != null)
                                                           .collect(Collectors.toList())));
    }

    public LoadWorkspaceCommand() {
        super("load", "load workspace snapshots into the CoRE instance");
    }

    @Override
    public void configure(Subparser subparser) {
        subparser.addArgument("files")
                 .nargs("+")
                 .help("Workspace snapshot json files");
    }

    @Override
    public void run(Bootstrap<?> bootstrap,
                    Namespace namespace) throws Exception {
        CoreDbConfiguration config = new CoreDbConfiguration();
        config.initializeFromEnvironment();
        List<String> list = namespace.getList("files");
        loadWorkspaces(list, DSL.using(config.getCoreConnection()));
    }
}
