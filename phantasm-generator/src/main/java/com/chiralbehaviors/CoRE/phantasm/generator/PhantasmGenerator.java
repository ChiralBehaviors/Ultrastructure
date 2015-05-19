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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.FacetContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspacePresentation;
import com.hellblazer.utils.Utils;

/**
 * @author hhildebrand
 *
 */
public class PhantasmGenerator {
    private static final String        FACET               = "facet";
    private static final String        TEMPLATES_FACET_STG = "templates/facet.stg";
    private final Configuration        configuration;
    private final Map<FacetKey, Facet> facets              = new HashMap<>();

    public PhantasmGenerator(Configuration configuration) {
        this.configuration = configuration;
    }

    public void generate() throws IOException {
        try {
            generateFacets();
        } catch (IOException e) {
            throw new IOException(String.format("Unable to load workspace: %s",
                                                configuration.resource));
        }
        for (Facet facet : facets.values()) {
            File file = getOutputFile(facet);
            STGroup group = new STGroupFile(TEMPLATES_FACET_STG);
            ST template = group.getInstanceOf(FACET);
            template.add(FACET, facet);
            generate(template, file);
        }
    }

    private void resolve(WorkspacePresentation presentation) {
        Map<ScopedName, MappedAttribute> mapped = mapAttributes(presentation);
        for (Facet facet : facets.values()) {
            facet.resolve(facets, presentation, mapped);
        }
    }

    private Facet constructFacet(FacetContext facet, String ruleformType) {
        String packageName = configuration.appendTypeToPackage ? String.format("%s.%s",
                                                                               configuration.packageName,
                                                                               ruleformType.toLowerCase())
                                                              : configuration.packageName;
        return new Facet(packageName, ruleformType, facet);
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

    private void generateFacets() throws IOException {
        WorkspacePresentation wsp = new WorkspacePresentation(
                                                              Utils.resolveResource(getClass(),
                                                                                    configuration.resource));
        wsp.getAgencyFacets().forEach(facet -> {
            facets.put(new FacetKey(facet), constructFacet(facet, "Agency"));
        });
        wsp.getAgencyFacets().forEach(facet -> {
                                          facets.put(new FacetKey(facet),
                                                     constructFacet(facet,
                                                                    "Attribute"));
                                      });
        wsp.getIntervalFacets().forEach(facet -> {
            facets.put(new FacetKey(facet), constructFacet(facet, "Interval"));
        });
        wsp.getLocationFacets().forEach(facet -> {
            facets.put(new FacetKey(facet), constructFacet(facet, "Location"));
        });
        wsp.getProductFacets().forEach(facet -> {
            facets.put(new FacetKey(facet), constructFacet(facet, "Product"));
        });
        wsp.getRelationshipFacets().forEach(facet -> {
                                                facets.put(new FacetKey(facet),
                                                           constructFacet(facet,
                                                                          "Relationship"));
                                            });
        wsp.getStatusCodeFacets().forEach(facet -> {
                                              facets.put(new FacetKey(facet),
                                                         constructFacet(facet,
                                                                        "StatusCode"));
                                          });
        wsp.getUnitFacets().forEach(facet -> {
            facets.put(new FacetKey(facet), constructFacet(facet, "Unit"));
        });
        resolve(wsp);
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

    public static Map<ScopedName, MappedAttribute> mapAttributes(WorkspacePresentation workspace) {
        Map<ScopedName, MappedAttribute> mapped = new HashMap<>();
        workspace.getAttributes().forEach(attribute -> {
                                              mapped.put(new ScopedName(
                                                                        "",
                                                                        attribute.existentialRuleform().workspaceName.getText()),
                                                         new MappedAttribute(
                                                                             attribute));
                                          });
        return mapped;
    }
}
