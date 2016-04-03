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

import com.chiralbehaviors.CoRE.loader.Loader;
import com.chiralbehaviors.CoRE.phantasm.service.config.PhantasmConfiguration;
import com.chiralbehaviors.CoRE.utils.DbaConfiguration;

import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

/**
 * @author hhildebrand
 *
 */
public class BootstrapCommand extends ConfiguredCommand<PhantasmConfiguration> {

    public BootstrapCommand() {
        super("bootstrap", "Bootstraps the CoRE instance");
    }

    /* (non-Javadoc)
     * @see io.dropwizard.cli.Command#configure(net.sourceforge.argparse4j.inf.Subparser)
     */
    @Override
    public void configure(Subparser subparser) {
    }

    /* (non-Javadoc)
     * @see io.dropwizard.cli.Command#run(io.dropwizard.setup.Bootstrap, net.sourceforge.argparse4j.inf.Namespace)
     */
    @Override
    public void run(Bootstrap<PhantasmConfiguration> bootstrap,
                    Namespace namespace,
                    PhantasmConfiguration configuration) throws Exception {
        DbaConfiguration config = new DbaConfiguration();
        config.initializeFromEnvironment();
        new Loader(config).bootstrap();
    }

}
