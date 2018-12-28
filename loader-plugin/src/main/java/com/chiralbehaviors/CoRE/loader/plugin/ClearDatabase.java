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

package com.chiralbehaviors.CoRE.loader.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.postgresql.Driver;

import com.chiralbehaviors.CoRE.loader.Loader;
import com.chiralbehaviors.CoRE.utils.DbaConfiguration;

/**
 * @author hhildebrand
 * 
 * @goal clear
 * 
 * @phase compile
 */
public class ClearDatabase extends AbstractMojo {

    static {
        Driver.class.getCanonicalName();
    }

    /**
     * the loading configuration
     * 
     * @parameter
     */
    private DbaConfiguration loader;

    /* (non-Javadoc)
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (loader.corePassword == null) {
            loader.initializeFromEnvironment();
        }
        try {
            new Loader(loader).clear();
        } catch (Exception e) {
            throw new MojoFailureException(String.format("Unable to clear %s",
                                                         loader.getCoreJdbcURL()), e);
        }
    }
}
