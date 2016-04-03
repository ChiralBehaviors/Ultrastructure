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

import java.util.stream.Collectors;

import org.jooq.DSLContext;

import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.phantasm.service.config.PhantasmConfiguration;
import com.hellblazer.utils.Utils;

import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

/**
 * @author hhildebrand
 *
 */
public class ManifestCommand extends ConfiguredCommand<PhantasmConfiguration> {

    public ManifestCommand() {
        super("manifest",
              "Manifest workspace dsl files into the CoRE instance");
    }

    @Override
    public void configure(Subparser subparser) {
        subparser.addArgument("files")
                 .nargs("+")
                 .help("Workspace dsl files");
    }

    @Override
    public void run(Bootstrap<PhantasmConfiguration> bootstrap,
                    Namespace namespace,
                    PhantasmConfiguration configuration) throws Exception {
        DSLContext create = configuration.create();
        create.transaction(c -> {
            try (Model model = new ModelImpl(create)) {
                WorkspaceImporter.manifest(namespace.getList("files")
                                                    .stream()
                                                    .map(file -> {
                                                        try {
                                                            return Utils.resolveResourceURL(getClass(),
                                                                                            (String) file);
                                                        } catch (Exception e) {
                                                            throw new IllegalArgumentException(String.format("Cannot resolve URL for %s",
                                                                                                             file),
                                                                                               e);
                                                        }
                                                    })
                                                    .collect(Collectors.toList()),
                                           model);
            }
        });
    }

}
