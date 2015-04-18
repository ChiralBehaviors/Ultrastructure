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
package com.chiralbehaviors.CoRE.networkInference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

/**
 * A class for generating JPA native queries for network inference for
 * existential ruleforms.
 * 
 * @author hhildebrand
 * 
 * @goal generate
 * 
 * @phase generate-sources
 * 
 * 
 */
public class NetworkInferenceQueryGenerator extends AbstractMojo {

    private static final String     ENTITY_NAME                     = "entityName";
    private static final String     ENTITY_PACKAGE                  = "entityPackage";
    private static final String[][] GENERATED                       = {
            { "AgencyNetwork", "com.chiralbehaviors.CoRE.agency",
            "agency_network", "agencyNetwork" },
            { "AttributeNetwork", "com.chiralbehaviors.CoRE.attribute",
            "attribute_network", "attributeNetwork" },
            { "UnitNetwork", "com.chiralbehaviors.CoRE.attribute.unit",
            "unit_network", "unitNetwork" },
            { "StatusCodeNetwork", "com.chiralbehaviors.CoRE.event.status",
            "status_code_network", "statusCodeNetwork" },
            { "LocationNetwork", "com.chiralbehaviors.CoRE.location",
            "location_network", "locationNetwork" },
            { "RelationshipNetwork", "com.chiralbehaviors.CoRE.relationship",
            "relationship_network", "relationshipNetwork" },
            { "ProductNetwork", "com.chiralbehaviors.CoRE.product",
            "product_network", "productNetwork" },
            { "IntervalNetwork", "com.chiralbehaviors.CoRE.time",
            "interval_network", "intervalNetwork" }                };
    private static final String     NETWORK_INFERENCE               = "networkInference";
    private static final String     QUERY_PREFIX                    = "queryPrefix";
    private static final String     TABLE_NAME                      = "tableName";
    private static final String     TEMPLATES_NETWORK_INFERENCE_STG = "templates/network-inference.stg";

    /**
     * @parameter
     */
    private File                    outputDirectory;

    public NetworkInferenceQueryGenerator() {
    }

    public NetworkInferenceQueryGenerator(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info(String.format("output directory %s", outputDirectory));
        try {
            Files.createDirectories(outputDirectory.toPath());
        } catch (IOException e) {
            throw new MojoExecutionException(
                                             String.format("Cannot create output directory %s",
                                                           outputDirectory.getAbsoluteFile()),
                                             e);
        }
        for (String[] info : GENERATED) {
            File output = new File(outputDirectory,
                                   String.format("%s/%s.xml",
                                                 info[1].replace('.', '/'),
                                                 info[3]));
            generate(info[0], info[1], info[2], info[3], output);
        }
    }

    private void generate(String entityName, String entityPackage,
                          String tableName, String queryPrefix, File output)
                                                                            throws MojoExecutionException {
        getLog().info(String.format("Generating %s to %s", entityName, output));
        try {
            Files.createDirectories(output.getParentFile().toPath());
        } catch (IOException e) {
            throw new MojoExecutionException(
                                             String.format("Cannot create generated file parent directories %s",
                                                           output.getParentFile().getAbsoluteFile()),
                                             e);
        }

        try {
            Files.deleteIfExists(output.toPath());
        } catch (IOException e) {
            throw new MojoExecutionException(
                                             String.format("Cannot delete generated file %s",
                                                           output.getAbsoluteFile()),
                                             e);
        }
        STGroup group = new STGroupFile(TEMPLATES_NETWORK_INFERENCE_STG, '%',
                                        '%');
        ST inference = group.getInstanceOf(NETWORK_INFERENCE);
        inference.add(ENTITY_NAME, entityName);
        inference.add(ENTITY_PACKAGE, entityPackage);
        inference.add(TABLE_NAME, tableName);
        inference.add(QUERY_PREFIX, queryPrefix);
        try (OutputStream os = new FileOutputStream(output)) {
            os.write(inference.render().getBytes());
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException(
                                             String.format("Cannot find generated file for write %s",
                                                           output.getAbsoluteFile()),
                                             e);
        } catch (IOException e) {
            throw new MojoExecutionException(
                                             String.format("Error generating file %s",
                                                           output.getAbsoluteFile()),
                                             e);
        }
    }
}
