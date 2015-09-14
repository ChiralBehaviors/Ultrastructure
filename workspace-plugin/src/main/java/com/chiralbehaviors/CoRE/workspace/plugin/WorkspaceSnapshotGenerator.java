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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;

/**
 * @author hhildebrand
 *
 * @goal generate-snapshot
 * 
 * @phase process-classes
 */
public class WorkspaceSnapshotGenerator extends AbstractMojo {

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

    public WorkspaceSnapshotGenerator(Configuration database,
                                      List<Export> exports) {
        this.database = database;
        this.exports = exports;
    }

    /* (non-Javadoc)
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Generating workspace snapshots from database state ");
        EntityManagerFactory emf;
        try {
            emf = database.getEmf();
        } catch (IOException e) {
            throw new MojoExecutionException("An error has occurred while initilizing the JPA required infrastructure",
                                             e);
        }
        try {
            for (Export export : exports) {
                UUID uuid = WorkspaceAccessor.uuidOf(export.iri);
                getLog().warn(String.format("Processing workspace: %s:%s", uuid,
                                            export.iri));
                Model model = new ModelImpl(emf);
                EntityManager em = model.getEntityManager();
                try {
                    em.getTransaction()
                      .begin();
                    WorkspaceScope scope = model.getWorkspaceModel()
                                                .getScoped(uuid);
                    if (scope == null) {
                        getLog().warn("Could not find workspace");
                        continue;
                    }
                    getLog().warn(String.format("Serializing workspace: %s to:%s",
                                                uuid, export.output));
                    try (FileOutputStream os = new FileOutputStream(export.output)) {
                        scope.getWorkspace()
                             .getSnapshot()
                             .serializeTo(os);
                    } catch (IOException e) {
                        throw new MojoFailureException("An error occurred while serializing the workspace",
                                                       e);
                    }
                } finally {
                    em.getTransaction()
                      .rollback();
                }
            }
        } finally {
            emf.close();
        }
    }

}
