/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 *
 *
 * This file is part of Ultrastructure.
 *
 * Ultrastructure is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * ULtrastructure is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Ultrastructure.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.workspace.plugin;

import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author hhildebrand
 *
 * @goal generate-snapshot
 *
 * @phase process-classes
 */
public class WorkspaceSnapshotGenerator extends AbstractMojo {

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
    private List<Export> exports = new ArrayList<>();

    public WorkspaceSnapshotGenerator() {
    }

    public WorkspaceSnapshotGenerator(Configuration database, List<Export> exports) {
        this.database = database;
        this.exports = exports;
    }

    /* (non-Javadoc)
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Generating workspace snapshots from database state ");

        try {
            DSLContext create = database.getCreate();
            for (Export export : exports) {
                UUID uuid = WorkspaceAccessor.uuidOf(export.iri);
                getLog().warn(String.format("Processing workspace: %s:%s", uuid, export.iri));

                try (Model model = new ModelImpl(create)) {
                    WorkspaceScope scope = model.getWorkspaceModel().getScoped(uuid);
                    if (scope == null) {
                        getLog().warn("Could not find workspace");
                        continue;
                    }
                    getLog().warn(String.format("Serializing workspace: %s to:%s", uuid, export.output));
                    try (FileOutputStream os = new FileOutputStream(export.output)) {
                        scope.getWorkspace().getSnapshot().serializeTo(os);
                    } catch (IOException e) {
                        throw new MojoFailureException("An error occurred while serializing the workspace", e);
                    }
                } catch (DataAccessException e) {
                    throw new MojoFailureException("An error occurred while serializing the workspace", e);
                }
            }
        } catch (IOException | SQLException e) {
            throw new MojoExecutionException("An error has occurred while initilizing the required DB infrastructure",
                                             e);
        }
    }

    public static class Export {
        /**
         * @parameter
         */
        public String iri;

        /**
         * @parameter
         */
        public File output;

        public Export() {
        }

        public Export(String iri, File output) {
            this.iri = iri;
            this.output = output;
        }
    }

}
