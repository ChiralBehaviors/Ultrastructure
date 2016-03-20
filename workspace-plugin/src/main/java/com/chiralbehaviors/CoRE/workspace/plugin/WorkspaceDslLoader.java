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
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.models.ModelImpl;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspaceImporter;
import com.hellblazer.utils.Utils;

/**
 * @author hhildebrand
 *
 * @goal load-dsl
 * 
 * @phase compile
 */
public class WorkspaceDslLoader extends AbstractMojo {

    /**
     * the database configuration
     * 
     * @parameter
     */
    private Configuration database  = new Configuration();

    /**
     * the workspace dsl resources
     * 
     * @parameter
     */
    private List<String>  resources = new ArrayList<>();

    public WorkspaceDslLoader() {
    }

    public WorkspaceDslLoader(Configuration database, List<String> resources) {
        this.database = database;
        this.resources = resources;
    }

    /* (non-Javadoc)
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Creating workspaces from dsl resources ");
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
        EntityManagerFactory emf;
        try {
            emf = database.getEmf();
        } catch (IOException e) {
            throw new MojoExecutionException("An error has occurred while initilizing the required JPA infrastructure",
                                             e);
        }
        Model model = new ModelImpl(emf);
        EntityManager em = model.getDSLContext();
        em.getTransaction()
          .begin();
        try {
            for (URL url : toLoad) {
                try {
                    try (InputStream is = url.openStream()) {
                        getLog().info(String.format("Loading dsl from: %s",
                                                    url.toExternalForm()));
                        try {
                            WorkspaceImporter.manifest(is, model);
                        } catch (IllegalStateException e) {
                            getLog().warn(String.format("Could not load : %s",
                                                        e.getMessage()));
                        }
                    }
                } catch (IOException e) {
                    throw new MojoExecutionException(String.format("An error has occurred while creating workspace from resource: %s",
                                                                   url.toExternalForm()),
                                                     e);
                }
            }
            em.getTransaction()
              .commit();
        } finally {
            if (em.getTransaction()
                  .isActive()) {
                em.getTransaction()
                  .rollback();
            }
            em.close();
            emf.close();
        }
    }

}
