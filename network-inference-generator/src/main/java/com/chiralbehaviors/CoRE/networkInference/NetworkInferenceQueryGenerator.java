/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
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
            { "RelationshipNetwork", "com.chiralbehaviors.CoRE.network",
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
