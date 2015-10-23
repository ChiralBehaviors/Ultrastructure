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

import java.util.Collections;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.chiralbehaviors.CoRE.phantasm.service.PhantasmBundle;
import com.chiralbehaviors.CoRE.phantasm.service.config.JpaConfiguration;
import com.hellblazer.utils.Utils;

import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

/**
 * @author hhildebrand
 *
 */
public class ManifestCommand extends Command {

    public ManifestCommand() {
        super("manifest",
              "Manifest workspace dsl files into the CoRE instance");
    }

    /* (non-Javadoc)
     * @see io.dropwizard.cli.Command#configure(net.sourceforge.argparse4j.inf.Subparser)
     */
    @Override
    public void configure(Subparser subparser) {
        subparser.addArgument("file")
                 .nargs("+")
                 .help("Workspace dsl files");
    }

    /* (non-Javadoc)
     * @see io.dropwizard.cli.Command#run(io.dropwizard.setup.Bootstrap, net.sourceforge.argparse4j.inf.Namespace)
     */
    @Override
    public void run(Bootstrap<?> bootstrap,
                    Namespace namespace) throws Exception {
        EntityManagerFactory emf = PhantasmBundle.getEmfFromEnvironment(Collections.emptyMap(),
                                                                        JpaConfiguration.PERSISTENCE_UNIT);
        try (Model model = new ModelImpl(emf)) {
            EntityTransaction t = model.getEntityManager()
                                       .getTransaction();
            t.begin();
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
            t.commit();
        }
    }

}
