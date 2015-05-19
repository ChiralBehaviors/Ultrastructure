/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.phantasm.generator;

import static com.chiralbehaviors.CoRE.meta.workspace.Workspace.uuidOf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspacePresentation;
import com.hellblazer.utils.Utils;

/**
 * @author hhildebrand
 *
 */
public class PhantasmGenerator {
    private static final String FACET               = "facet";
    private static final String TEMPLATES_FACET_STG = "templates/facet.stg";
    private final Configuration configuration;
    private final Model         model;

    public PhantasmGenerator(Model model, Configuration configuration) {
        this.configuration = configuration;
        this.model = model;
    }

    public void generate() throws IOException {
        List<Facet> facets;
        try {
            facets = generateFacets();
        } catch (IOException e) {
            throw new IOException(String.format("Unable to load workspace: %s",
                                                configuration.resource));
        }
        for (Facet facet : facets) {
            File file = getOutputFile(facet);
            STGroup group = new STGroupFile(TEMPLATES_FACET_STG);
            ST template = group.getInstanceOf(FACET);
            template.add(FACET, facet);
            generate(template, file);
        }
    }

    private void generate(ST template, File file) {
        FileOutputStream os;
        try {
            Files.deleteIfExists(file.toPath());
            os = new FileOutputStream(Files.createFile(file.toPath()).toFile());
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(
                                            String.format("Cannot find file for create %s",
                                                          file.getAbsolutePath(),
                                                          e));
        } catch (IOException e) {

            throw new IllegalStateException(
                                            String.format("Error creating file %s\nCause: %s",
                                                          file.getAbsolutePath(),
                                                          e.getMessage()));
        }
        try {
            os.write(template.render().getBytes());
            os.close();
        } catch (IOException e) {
            throw new IllegalStateException(
                                            String.format("Error writing file %s",
                                                          file.getAbsolutePath(),
                                                          e));
        }
    }

    private List<Facet> generateFacets() throws IOException {
        @SuppressWarnings("unused")
        WorkspaceScope workspace = getWorkspace();
        return Collections.emptyList();
    }

    private File getOutputFile(Facet facet) {
        String packageDirectory = facet.getPackageName().replace('.', '/');
        File file = new File(configuration.outputDirectory,
                             String.format("%s/%s.java", packageDirectory,
                                           facet.getClassName()));
        File parentDir = new File(configuration.outputDirectory,
                                  packageDirectory);
        try {
            Files.createDirectories(parentDir.toPath());
        } catch (IOException e) {
            throw new IllegalStateException(
                                            String.format("Cannot create parent directories %s",
                                                          file.getParent()), e);
        }
        return file;
    }

    private WorkspaceScope getWorkspace() throws IOException {
        WorkspacePresentation wsp = new WorkspacePresentation(
                                                              Utils.resolveResource(getClass(),
                                                                                    configuration.resource));
        return model.getWorkspaceModel().getScoped(uuidOf(stripQuotes(wsp.getWorkspaceDefinition().uri.getText())));
    }

    private String stripQuotes(String original) {
        return original.substring(1, original.length() - 1);
    }
}
