/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.generators;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.reflections.Reflections;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import com.chiralbehaviors.CoRE.Ruleform;

/**
 * A class for generating a file for deserializing ruleforms. If you want your
 * ruleform deserialized polymorphically, it MUST descend from Ruleform in order
 * to be correctly deserialized.
 * 
 * For now, we're only deserializing to concrete types, but if you need an
 * abstract type, just add it in.
 * 
 * @author hparry
 * 
 * @goal generate
 * 
 * @phase generate-sources
 * 
 *        //
 */
//@Mojo(name = "mixin-generator")
//@Execute(goal = "generate", phase = LifecyclePhase.GENERATE_SOURCES)
public class PolymorphicMixinGenerator extends AbstractMojo {

    /**
     * @parameter
     */
    private String className;

    /**
     * @parameter
     */
    private File   outputDirectory;

    /**
     * @parameter
     */
    private String packageName;

    public PolymorphicMixinGenerator() {
    }

    public PolymorphicMixinGenerator(String packageName, File outputDirectory,
                                     String className) {
        this.packageName = packageName;
        this.outputDirectory = outputDirectory;
        this.className = className;
    }

    public void execute() throws MojoExecutionException {
        System.out.println(String.format("args: %s, %s, %s", packageName,
                                         outputDirectory, className));
        File file = new File(outputDirectory,
                             String.format("%s/%s.java",
                                           packageName.replace('.', '/'),
                                           className));
        File parentDir = new File(outputDirectory,
                                  packageName.replace('.', '/'));
        try {
            Files.createDirectories(parentDir.toPath());
        } catch (IOException e) {
            throw new MojoExecutionException(
                                             String.format("Cannot create parent directories %s",
                                                           file.getParent()), e);
        }
        final Map<String, Class<?>> entityMap = new HashMap<String, Class<?>>();
        Set<String> imports = new HashSet<String>();
        List<AnnotationValue> annotations = new ArrayList<AnnotationValue>();

        Reflections reflections = new Reflections(
                                                  Ruleform.class.getPackage().getName());

        for (Class<? extends Ruleform> form : reflections.getSubTypesOf(Ruleform.class)) {
            if (!Modifier.isAbstract(form.getModifiers())) {
                Class<?> prev = entityMap.put(form.getSimpleName(), form);
                assert prev == null : String.format("Found previous mapping %s of: %s",
                                                    prev, form);
                imports.add(form.getCanonicalName());
                annotations.add(new AnnotationValue(
                                                    form.getSimpleName(),
                                                    form.getSimpleName().toLowerCase()));
            }
        }

        STGroup group = new STGroupFile("templates/polymorphicmixin.stg");
        ST mixin = group.getInstanceOf("mixinclass");
        mixin.add("importdecs", imports);
        mixin.add("annotations", annotations);

        FileOutputStream os;
        try {
            Files.deleteIfExists(file.toPath());
            os = new FileOutputStream(Files.createFile(file.toPath()).toFile());
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException(
                                             String.format("Cannot find file for create %s",
                                                           file.getAbsolutePath(),
                                                           e));
        } catch (IOException e) {

            throw new MojoExecutionException(
                                             String.format("Error creating file %s\nCause: %s",
                                                           file.getAbsolutePath(),
                                                           e.getMessage()));
        }
        try {
            os.write(mixin.render().getBytes());
            os.close();
        } catch (IOException e) {
            throw new MojoExecutionException(
                                             String.format("Error writing file %s",
                                                           file.getAbsolutePath(),
                                                           e));
        }
    }
}
