/**
 * Copyright (c) 2018 Chiral Behaviors, LLC, all rights reserved.
 *

 *  This file is part of Ultrastructure.
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

package com.chiralbehaviors.CoRE.phantasm.generator.json;

import java.beans.Introspector;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jsonschema2pojo.AnnotationStyle;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.rules.RuleFactory;

import com.chiralbehaviors.CoRE.meta.workspace.json.Constraint;
import com.chiralbehaviors.CoRE.meta.workspace.json.Facet;
import com.chiralbehaviors.CoRE.meta.workspace.json.JsonWorkspace;
import com.chiralbehaviors.CoRE.utils.English;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hellblazer.utils.Utils;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;

/**
 * @author halhildebrand
 *
 */
public class PhantasmGenerator {
    private static final String ADD_S              = "add%s";
    private static final String ANNOTATIONS        = "com.chiralbehaviors.CoRE.phantasm.java.annotations.";
    private static final String EDGE_ANNOTATION;
    private static final String FACET_ANNOTATION;
    private static final String FIELD_NAME         = "fieldName";
    private static final String GET_IMMEDIATE_S    = "getImmediate%s";
    private static final String GET_S              = "get%s";
    private static final String GET_PROPERTIES     = "get_Properties";
    private static final String SET_PROPERTIES     = "set_Properties";
    private static final String INFERED_ANNOTATION;
    private static final String PROPERTIES_ANNOTATION;
    private static final String REMOVE_S           = "remove%s";
    private static final String SET_IMMEDIATE_S    = "setImmediate%s";
    private static final String SET_S              = "set%s";
    private static final String WRAPPED_CHILD_TYPE = "wrappedChildType";

    static {
        INFERED_ANNOTATION = ANNOTATIONS + "Infered";
        PROPERTIES_ANNOTATION = ANNOTATIONS + "Properties";
        EDGE_ANNOTATION = ANNOTATIONS + "Edge";
        FACET_ANNOTATION = ANNOTATIONS + "Facet2";
    }

    public static String capitalized(String baseName) {
        return Character.toUpperCase(baseName.charAt(0))
               + (baseName.length() == 1 ? "" : baseName.substring(1));
    }

    public static String toFieldName(String name) {
        return Introspector.decapitalize(name.replaceAll("\\s", ""));
    }

    public static String toTypeName(String name) {
        char chars[] = toValidName(name).toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static String toValidName(String name) {
        name = name.replaceAll("\\s", "");
        StringBuilder sb = new StringBuilder();
        if (!Character.isJavaIdentifierStart(name.charAt(0))) {
            sb.append("_");
        }
        for (char c : name.toCharArray()) {
            if (!Character.isJavaIdentifierPart(c)) {
                sb.append("_");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private final Configuration config;

    private final JsonWorkspace workspace;

    public PhantasmGenerator(Configuration config) {
        InputStream input;
        try {
            input = Utils.resolveResource(getClass(), config.resource);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot resolve resource: "
                                               + config.resource, e);
        }
        if (input == null) {

        }
        try {
            this.workspace = new ObjectMapper().readerFor(JsonWorkspace.class)
                                               .readValue(input);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot deserialize json resource: "
                                               + config.resource, e);
        }
        this.config = config;
    }

    public void generate() throws IOException {
        JCodeModel codeModel = new JCodeModel();
        generate(codeModel);
        config.outputDirectory.mkdirs();
        codeModel.build(config.outputDirectory);
    }

    public void generate(JCodeModel codeModel) {
        JPackage jpackage = codeModel._package(config.packageName);
        workspace.facets.forEach((name, facet) -> generate(facet, name,
                                                           jpackage,
                                                           codeModel));
    }

    private void generate(Constraint constraint, String name,
                          JDefinedClass jClass, JCodeModel codeModel) {
        switch (constraint.card) {
            case MANY:
                generateListMethods(name, constraint, jClass, codeModel);
                break;
            case ONE:
                generateSingularMethods(name, constraint, jClass, codeModel);
                break;
            default:

        }
    }

    private void generate(Facet facet, String name, JPackage jpackage,
                          JCodeModel codeModel) {
        JDefinedClass jClass;
        try {
            jClass = jpackage.owner()
                             ._class(JMod.PUBLIC,
                                     config.packageName + "." + name,
                                     ClassType.INTERFACE);
        } catch (JClassAlreadyExistsException e) {
            throw new IllegalStateException(String.format("Facet %s has already been defined",
                                                          name));
        }
        JAnnotationUse facetAnnotation = jClass.annotate(codeModel.ref(FACET_ANNOTATION));
        facetAnnotation.param("key", name);
        facetAnnotation.param("workspace", workspace.uri);
        facet.constraints.forEach((n, constraint) -> generate(constraint, n,
                                                              jClass,
                                                              codeModel));
        generateAttributes(jClass, name, facet, jpackage, codeModel);
    }

    private void generateAttributes(JDefinedClass jClass, String name,
                                    Facet facet, JPackage jpackage,
                                    JCodeModel codeModel) {
        JPackage propPackage = jpackage.subPackage(toFieldName(name
                                                               + "Properties"));
        JClass propType;
        if (facet.schema == null) {
            propType = jClass.owner()
                             .ref(JsonNode.class.getCanonicalName());
        } else {
            GenerationConfig config = new DefaultGenerationConfig() {
                @Override
                public boolean isGenerateBuilders() { // set config option by overriding method
                    return true;
                }

                @Override
                public AnnotationStyle getAnnotationStyle() {
                    return AnnotationStyle.JACKSON2;
                }
            };
            String propName = toValidName(facet.schema.get("name") == null ? name
                                                                             + "Properties"
                                                                           : facet.schema.get("name")
                                                                                         .asText());
            new RuleFactory(config, new Jackson2Annotator(config),
                            new SchemaStore()).getSchemaRule()
                                              .apply(propName, facet.schema,
                                                     propPackage,
                                                     new Schema(null,
                                                                facet.schema,
                                                                facet.schema));
            propType = jClass.owner()
                             .ref(propPackage.name() + "." + propName);
        }
        JMethod get = jClass.method(JMod.PUBLIC, propType, GET_PROPERTIES);
        get.annotate(codeModel.ref(PROPERTIES_ANNOTATION));

        JMethod set = jClass.method(JMod.PUBLIC, jClass.owner().VOID,
                                    SET_PROPERTIES);
        set.annotate(codeModel.ref(PROPERTIES_ANNOTATION));
        set.param(propType, "properties");
    }

    private void generateInferedList(String fieldName, JClass listType,
                                     JDefinedClass jClass, String plural,
                                     String pluralParameter, JClass childType,
                                     JCodeModel codeModel) {
        JMethod getInfered = jClass.method(JMod.PUBLIC, listType,
                                           String.format(GET_S, plural));
        getInfered.annotate(codeModel.ref(EDGE_ANNOTATION))
                  .param(FIELD_NAME, fieldName)
                  .param(WRAPPED_CHILD_TYPE, childType);
        getInfered.annotate(codeModel.ref(INFERED_ANNOTATION));

        jClass.method(JMod.PUBLIC, listType,
                      String.format(GET_IMMEDIATE_S, plural))
              .annotate(codeModel.ref(EDGE_ANNOTATION))
              .param(FIELD_NAME, fieldName)
              .param(WRAPPED_CHILD_TYPE, childType);

        JMethod setter = jClass.method(JMod.PUBLIC, jClass.owner().VOID,
                                       String.format(SET_IMMEDIATE_S, plural));
        setter.annotate(codeModel.ref(EDGE_ANNOTATION))
              .param(FIELD_NAME, fieldName)
              .param(WRAPPED_CHILD_TYPE, childType);
        setter.param(listType, pluralParameter);
    }

    private void generateList(String fieldName, JDefinedClass jClass,
                              JClass listType, String plural,
                              String pluralParameter, JClass childType,
                              JCodeModel codeModel) {
        jClass.method(JMod.PUBLIC, listType, String.format(GET_S, plural))
              .annotate(codeModel.ref(EDGE_ANNOTATION))
              .param(FIELD_NAME, fieldName)
              .param(WRAPPED_CHILD_TYPE, childType);

        JMethod setter = jClass.method(JMod.PUBLIC, jClass.owner().VOID,
                                       String.format(SET_S, plural));
        setter.annotate(codeModel.ref(EDGE_ANNOTATION))
              .param(FIELD_NAME, fieldName)
              .param(WRAPPED_CHILD_TYPE, childType);
        setter.param(listType, pluralParameter);
    }

    private void generateListCommon(String fieldName, JClass listType,
                                    JClass childType, String plural,
                                    String pluralParameter, String normalized,
                                    JDefinedClass jClass,
                                    JCodeModel codeModel) {
        JMethod add = jClass.method(JMod.PUBLIC, jClass.owner().VOID,
                                    String.format(ADD_S, normalized));
        add.annotate(codeModel.ref(EDGE_ANNOTATION))
           .param(FIELD_NAME, fieldName)
           .param(WRAPPED_CHILD_TYPE, childType);
        add.param(childType, fieldName);

        JMethod remove = jClass.method(JMod.PUBLIC, jClass.owner().VOID,
                                       String.format(REMOVE_S, normalized));
        remove.annotate(codeModel.ref(EDGE_ANNOTATION))
              .param(FIELD_NAME, fieldName)
              .param(WRAPPED_CHILD_TYPE, childType);
        remove.param(childType, fieldName);

        JMethod addList = jClass.method(JMod.PUBLIC, jClass.owner().VOID,
                                        String.format(ADD_S, plural));
        addList.annotate(codeModel.ref(EDGE_ANNOTATION))
               .param(FIELD_NAME, fieldName)
               .param(WRAPPED_CHILD_TYPE, childType);
        addList.param(listType, pluralParameter);

        JMethod removeList = jClass.method(JMod.PUBLIC, jClass.owner().VOID,
                                           String.format(REMOVE_S, plural));
        removeList.annotate(codeModel.ref(EDGE_ANNOTATION))
                  .param(FIELD_NAME, fieldName)
                  .param(WRAPPED_CHILD_TYPE, childType);
        removeList.param(listType, pluralParameter);
    }

    private void generateListMethods(String name, Constraint constraint,
                                     JDefinedClass jClass,
                                     JCodeModel codeModel) {
        String fieldName = toFieldName(name);
        String normalized = capitalized(fieldName);
        String plural = English.plural(normalized);
        String pluralParameter = English.plural(fieldName);
        JClass childType = jClass.owner()
                                 .ref(resolveChild(constraint.child));
        JClass listType = codeModel.ref(List.class)
                                   .narrow(childType);

        if (constraint.infered) {
            generateInferedList(fieldName, listType, jClass, plural,
                                pluralParameter, childType, codeModel);
        } else {
            generateList(fieldName, jClass, listType, plural, pluralParameter,
                         childType, codeModel);
        }
        generateListCommon(fieldName, listType, childType, plural,
                           pluralParameter, normalized, jClass, codeModel);
    }

    private void generateSingularMethods(String name, Constraint constraint,
                                         JDefinedClass jClass,
                                         JCodeModel codeModel) {
        String fieldName = toFieldName(name);
        String normalized = capitalized(fieldName);
        JClass childType = jClass.owner()
                                 .ref(resolveChild(constraint.child));
        jClass.method(JMod.PUBLIC, childType, String.format(GET_S, normalized))
              .annotate(codeModel.ref(EDGE_ANNOTATION))
              .param(FIELD_NAME, fieldName)
              .param(WRAPPED_CHILD_TYPE, childType);

        JMethod set = jClass.method(JMod.PUBLIC, jClass.owner().VOID,
                                    String.format(SET_S, normalized));
        set.annotate(codeModel.ref(EDGE_ANNOTATION))
           .param(FIELD_NAME, fieldName)
           .param(WRAPPED_CHILD_TYPE, childType);
        set.param(childType, fieldName);
    }

    private String resolveChild(String child) {
        String[] split = child.split("::");
        String namespace = split.length == 2 ? split[0] : null;
        String name = split.length == 2 ? split[1] : split[0];

        if (namespace != null) {
            String pkg = config.namespacePackages.get(split[0]);
            if (pkg != null) {
                return pkg + "." + toTypeName(name);
            }
            throw new IllegalArgumentException("Cannot resolve facet: " + child
                                               + " in workspace");
        }
        return config.packageName + "." + toTypeName(name);
    }
}
