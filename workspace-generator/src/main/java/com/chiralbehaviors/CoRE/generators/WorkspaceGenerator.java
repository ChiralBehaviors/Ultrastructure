/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.generators;

import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

/**
 * @author hhildebrand
 *
 * 
 * @goal generate
 * 
 * @phase generate-sources
 */
public class WorkspaceGenerator extends AbstractMojo {
    private static final String   DEFAULT         = "target/generated-sources/workspace";
    private static final String   PREFIX          = "com.chiralbehaviors.CoRE.";
    private static final String   SOURCE_FILE     = "com/chiralbehaviors/CoRE/workspace/WorkspaceAuthorization.java";
    private static final String   CHANGESET_FILE  = "com/chiralbehaviors/CoRE/workspace/table.xml";
    private static final String[] imports         = { "agency.Agency",
            "agency.AgencyAttribute", "agency.AgencyAttributeAuthorization",
            "agency.AgencyLocation", "agency.AgencyLocationAttribute",
            "agency.AgencyNetwork", "agency.AgencyNetworkAttribute",
            "agency.AgencyNetworkAuthorization", "agency.AgencyProduct",
            "attribute.Attribute", "attribute.AttributeMetaAttribute",
            "attribute.AttributeMetaAttributeAuthorization",
            "attribute.AttributeNetwork",
            "attribute.AttributeNetworkAttribute", "attribute.unit.Unit",
            "attribute.unit.UnitAttribute",
            "attribute.unit.UnitAttributeAuthorization",
            "attribute.unit.UnitNetwork",
            "attribute.unit.UnitNetworkAttribute", "location.Location",
            "location.LocationAttribute",
            "location.LocationAttributeAuthorization",
            "location.LocationNetwork", "location.LocationNetworkAttribute",
            "location.LocationNetworkAuthorization", "product.Product",
            "product.ProductAttribute",
            "product.ProductAttributeAuthorization", "product.ProductNetwork",
            "product.ProductNetworkAttribute", "product.ProductLocation",
            "product.ProductLocationAttribute", "time.Interval",
            "time.IntervalAttribute", "time.IntervalAttributeAuthorization",
            "time.IntervalNetwork", "time.IntervalNetworkAttribute",
            "event.status.StatusCode", "event.status.StatusCodeAttribute",
            "event.status.StatusCodeAttributeAuthorization",
            "event.status.StatusCodeNetwork",
            "event.status.StatusCodeNetworkAttribute",
            "event.status.StatusCodeSequencing", "event.Job",
            "event.JobChronology", "event.MetaProtocol",
            "event.ProductChildSequencingAuthorization",
            "event.ProductParentSequencingAuthorization",
            "event.ProductSiblingSequencingAuthorization", "event.Protocol" };

    /**
     * Target generation directory
     * 
     * @parameter
     */
    private File                  outputDirectory = new File(DEFAULT);

    /* (non-Javadoc)
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        List<String> types = getTypes();
        List<String> variables = getVariables(types);
        List<String> columns = getColumns(variables);
        STGroup group = new STGroupFile("templates/workspace.stg");
        ST workspace = group.getInstanceOf("generate");
        workspace.add("imports", getImports());
        workspace.add("types", types);
        workspace.add("variables", variables);
        workspace.add("columns", columns);

        File sourceFile = new File(outputDirectory, SOURCE_FILE);
        try {
            Files.deleteIfExists(sourceFile.toPath());
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException(
                                             String.format("Cannot find file for create %s",
                                                           sourceFile.getAbsolutePath(),
                                                           e));
        } catch (IOException e) {

            throw new MojoExecutionException(
                                             String.format("Error creating file %s\nCause: %s",
                                                           sourceFile.getAbsolutePath(),
                                                           e.getMessage()));
        }
        sourceFile.getParentFile().mkdirs();
        try (OutputStream os = new FileOutputStream(
                                                    Files.createFile(sourceFile.toPath()).toFile())) {
            os.write(workspace.render().getBytes());
        } catch (IOException e) {
            throw new MojoExecutionException(
                                             String.format("Error writing file %s",
                                                           sourceFile.getAbsolutePath(),
                                                           e));
        }

        group = new STGroupFile("templates/ddl.stg");
        ST cs = group.getInstanceOf("generate");
        cs.add("columns", columns);
        sourceFile = new File(outputDirectory, CHANGESET_FILE);
        try {
            Files.deleteIfExists(sourceFile.toPath());
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException(
                                             String.format("Cannot find file for create %s",
                                                           sourceFile.getAbsolutePath(),
                                                           e));
        } catch (IOException e) {

            throw new MojoExecutionException(
                                             String.format("Error creating file %s\nCause: %s",
                                                           sourceFile.getAbsolutePath(),
                                                           e.getMessage()));
        }
        sourceFile.getParentFile().mkdirs();
        try (OutputStream os = new FileOutputStream(
                                                    Files.createFile(sourceFile.toPath()).toFile())) {
            os.write(cs.render().getBytes());
        } catch (IOException e) {
            throw new MojoExecutionException(
                                             String.format("Error writing file %s",
                                                           sourceFile.getAbsolutePath(),
                                                           e));
        }
    }

    /**
     * @return
     */
    private List<String> getImports() {
        List<String> declarations = new ArrayList<>();
        for (String decl : imports) {
            declarations.add(PREFIX + decl);
        }
        return declarations;
    }

    /**
     * @param variables
     * @return
     */
    private List<String> getColumns(List<String> variables) {
        List<String> columns = new ArrayList<>();
        for (String variable : variables) {
            StringWriter writer = new StringWriter(variable.length() + 3);
            for (char character : variable.toCharArray()) {
                if (isUpperCase(character)) {
                    writer.write('_');
                    writer.write(toLowerCase(character));
                } else {
                    writer.write(character);
                }
            }
            columns.add(writer.toString());
        }
        return columns;
    }

    private List<String> getTypes() {
        List<String> types = new ArrayList<>();
        for (String type : imports) {
            int pos = type.lastIndexOf(".");
            types.add(type.substring(pos + 1));
        }
        return types;
    }

    private List<String> getVariables(List<String> types) {
        List<String> variables = new ArrayList<>();
        for (String type : types) {
            variables.add(toLowerCase(type.charAt(0)) + type.substring(1));
        }
        return variables;
    }
}
