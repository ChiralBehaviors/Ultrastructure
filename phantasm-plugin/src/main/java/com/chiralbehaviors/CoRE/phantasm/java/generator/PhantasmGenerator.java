/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.phantasm.java.generator;

import java.beans.Introspector;
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

import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspacePresentation;
import com.hellblazer.utils.Utils;

/**
 * @author hhildebrand
 *
 */
public class PhantasmGenerator {
    private static final String FACET               = "facet";
    private static final String TEMPLATES_FACET_STG = "templates/facet.stg";

    public static Map<ScopedName, MappedAttribute> mapAttributes(WorkspacePresentation workspace) {
        Map<ScopedName, MappedAttribute> mapped = new HashMap<>();
        workspace.getAttributes()
                 .forEach(attribute -> {
                     mapped.put(new ScopedName("",
                                               attribute.existentialRuleform().workspaceName.getText()),
                                new MappedAttribute(attribute));
                 });
        return mapped;
    }

    public static String toFieldName(String name) {
        return Introspector.decapitalize(name.replaceAll("\\s", ""));
    }

    private final Configuration        configuration;

    private final Map<FacetKey, Facet> facets  = new HashMap<>();
    private final Map<FacetKey, Facet> imports = new HashMap<>();

    public PhantasmGenerator(Configuration configuration) {
        this.configuration = configuration;
    }

    public void generate() throws IOException {
        generateFacets();
        for (Facet facet : facets.values()) {
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
            os = new FileOutputStream(Files.createFile(file.toPath())
                                           .toFile());
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(String.format("Cannot find file for create %s",
                                                          file.getAbsolutePath(),
                                                          e));
        } catch (IOException e) {

            throw new IllegalStateException(String.format("Error creating file %s\nCause: %s",
                                                          file.getAbsolutePath(),
                                                          e.getMessage()));
        }
        try {
            os.write(template.render()
                             .getBytes());
            os.close();
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Error writing file %s",
                                                          file.getAbsolutePath(),
                                                          e));
        }
    }

    private void generateFacets() throws IOException {
        WorkspacePresentation wsp = new WorkspacePresentation(Utils.resolveResource(getClass(),
                                                                                    configuration.resource));

        importFacets(wsp);
        String uri = wsp.getWorkspaceDefinition().uri.getText();
        wsp.getAgencyFacets()
           .forEach(facet -> {
               facets.put(new FacetKey(facet),
                          (Facet) new DefinedFacet(configuration.packageName,
                                                facet, uri));
           });
        wsp.getAttributeFacets()
           .forEach(facet -> {
               facets.put(new FacetKey(facet),
                          (Facet) new DefinedFacet(configuration.packageName,
                                                facet, uri));
           });
        wsp.getIntervalFacets()
           .forEach(facet -> {
               facets.put(new FacetKey(facet),
                          (Facet) new DefinedFacet(configuration.packageName,
                                                facet, uri));
           });
        wsp.getLocationFacets()
           .forEach(facet -> {
               facets.put(new FacetKey(facet),
                          (Facet) new DefinedFacet(configuration.packageName,
                                                facet, uri));
           });
        wsp.getProductFacets()
           .forEach(facet -> {
               facets.put(new FacetKey(facet),
                          (Facet) new DefinedFacet(configuration.packageName,
                                                facet, uri));
           });
        wsp.getRelationshipFacets()
           .forEach(facet -> {
               facets.put(new FacetKey(facet),
                          (Facet) new DefinedFacet(configuration.packageName,
                                                facet, uri));
           });
        wsp.getStatusCodeFacets()
           .forEach(facet -> {
               facets.put(new FacetKey(facet),
                          (Facet) new DefinedFacet(configuration.packageName,
                                                facet, uri));
           });
        wsp.getUnitFacets()
           .forEach(facet -> {
               facets.put(new FacetKey(facet),
                          (Facet) new DefinedFacet(configuration.packageName,
                                                facet, uri));
           });
        resolve(wsp);
    }

    private void importFacets(WorkspacePresentation wsp) {
        wsp.getImports()
           .forEach(imp -> {
               String namespace = imp.namespace.getText();
               if (imp.importedFacets() != null) {
                   imp.importedFacets()
                      .facetImport()
                      .forEach(facet -> {
                          String packageName = configuration.namespacePackages.get(namespace);
                          FacetKey key = new FacetKey(facet.classifier,
                                                      facet.classification);
                          if (packageName == null) {
                              throw new IllegalStateException(String.format("No package translation for namespace: %s [%s]",
                                                                            namespace,
                                                                            key));
                          }
                          imports.put(key,
                                      new ImportedFacet(packageName, facet,
                                                        imp.uri.getText()));
                      });
               }
           });
    }

    private File getOutputFile(Facet facet) {
        String packageDirectory = facet.getPackageName()
                                       .replace('.', '/');
        File file = new File(configuration.outputDirectory,
                             String.format("%s/%s.java", packageDirectory,
                                           facet.getClassName()));
        File parentDir = new File(configuration.outputDirectory,
                                  packageDirectory);
        try {
            Files.createDirectories(parentDir.toPath());
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Cannot create parent directories %s",
                                                          file.getParent()),
                                            e);
        }
        return file;
    }

    private void resolve(WorkspacePresentation presentation) {
        Map<ScopedName, MappedAttribute> mapped = mapAttributes(presentation);
        for (Facet facet : facets.values()) {
            facet.resolve(this, presentation, mapped);
        }
    }

    public Facet resolve(FacetKey facetKey) {
        Facet facet = facets.get(facetKey);
        if (facet != null) {
            return facet;
        }

        facet = imports.get(facetKey);
        if (facet != null) {
            return facet;
        }
        return null;
    }
}
