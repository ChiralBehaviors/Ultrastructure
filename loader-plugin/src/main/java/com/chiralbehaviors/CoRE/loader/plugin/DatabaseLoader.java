/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import com.chiralbehaviors.CoRE.loader.Configuration;
import com.chiralbehaviors.CoRE.loader.Loader;

/**
 * @author hhildebrand
 * 
 * @goal load
 * 
 * @phase package
 */
public class DatabaseLoader extends AbstractMojo {
    /**
     * the loading configuration
     * 
     * @parameter
     */
    private Configuration loader;

    /* (non-Javadoc)
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Loading database");
        if (loader == null) {
            throw new MojoFailureException("No loader configuration supplied");
        }
        try {
            new Loader(loader).createDatabase()
                              .bootstrap();
        } catch (Exception e) {
            MojoFailureException ex = new MojoFailureException("Unable to load database");
            ex.initCause(e);
            throw ex;
        }
    }
}
