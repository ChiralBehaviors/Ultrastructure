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

package com.chiralbehaviors.CoRE.workspace.plugin;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;

import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;
import com.hellblazer.utils.Utils;

/**
 * @author hhildebrand
 *
 * @goal load-snapshot
 * 
 * @phase compile
 */
public class WorkspaceSnapshotLoader extends AbstractMojo {

    /**
     * the database configuration
     * 
     * @parameter
     */
    private Configuration database;

    /**
     * the workspace snapshot resources
     * 
     * @parameter
     */
    private List<String>  resources = new ArrayList<>();

    public WorkspaceSnapshotLoader() {
    }

    public WorkspaceSnapshotLoader(Configuration database,
                                   List<String> resources) {
        this.database = database;
        this.resources = resources;
    }

    /* (non-Javadoc)
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("loading workspace snapshots resources ");
        List<URL> toLoad = new ArrayList<>();
        for (String resource : resources) {
            URL url;
            try {
                url = Utils.resolveResourceURL(getClass(), resource);
            } catch (Exception e) {
                throw new MojoExecutionException(String.format("An error has occurred while resolving resource: %s",
                                                               resource),
                                                 e);
            }
            if (url == null) {
                throw new MojoExecutionException(String.format("Cannot resolve resource: %s",
                                                               resource));
            }
            toLoad.add(url);
        }
        try (DSLContext create = database.getCreate()) {
            create.transaction(c -> {
                WorkspaceSnapshot.load(create, toLoad);
            });
        } catch (DataAccessException | IOException | SQLException e) {
            throw new MojoExecutionException("An error has occurred while loading snapshots",
                                             e);
        }
    }

}
