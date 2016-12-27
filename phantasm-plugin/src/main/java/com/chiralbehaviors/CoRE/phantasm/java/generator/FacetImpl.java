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

import static com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspacePresentation.stripQuotes;
import static com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspacePresentation.toTypeName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.Token;

import com.chiralbehaviors.CoRE.jooq.enums.Cardinality;
import com.chiralbehaviors.CoRE.meta.workspace.dsl.WorkspacePresentation;
import com.chiralbehaviors.CoRE.utils.English;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ClassifiedAttributeContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ConstraintContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.FacetContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.NetworkConstraintsContext;

/**
 * @author hhildebrand
 *
 */
public class FacetImpl implements Facet {

    private final String       className;
    private final FacetContext context;
    private final List<Getter> edgePrimitiveGetters        = new ArrayList<>();
    private final List<Setter> edgePrimitiveSetters        = new ArrayList<>();
    private final Set<String>  imports                     = new HashSet<>();
    private final List<Getter> inferredRelationshipGetters = new ArrayList<>();
    private final String       packageName;
    private final List<Getter> primitiveGetters            = new ArrayList<>();
    private final List<Setter> primitiveSetters            = new ArrayList<>();
    private final List<Getter> relationshipGetters         = new ArrayList<>();
    private final List<Setter> relationshipSetters         = new ArrayList<>();
    private final String       ruleformType;
    private final String       uri;

    public FacetImpl(String packageName, String ruleformType,
                     FacetContext context, String uri) {
        this.packageName = packageName;
        this.context = context;
        this.ruleformType = ruleformType;
        this.uri = uri;
        className = context.name != null ? toTypeName(stripQuotes(context.name.getText()))
                                         : context.classification.member.getText();
    }

    @Override
    public ScopedName getClassification() {
        return new ScopedName(context.classification);
    }

    @Override
    public ScopedName getClassifier() {
        return new ScopedName(context.classifier);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.generator.Facet#getClassName()
     */
    @Override
    public String getClassName() {
        return className;
    }

    public List<Getter> getEdgePrimitiveGetters() {
        return edgePrimitiveGetters;
    }

    public List<Setter> getEdgePrimitiveSetters() {
        return edgePrimitiveSetters;
    }

    @Override
    public String getImport() {
        return String.format("%s.%s", packageName, getClassName());
    }

    public Set<String> getImports() {
        return imports;
    }

    public List<Getter> getInferredRelationshipGetters() {
        return inferredRelationshipGetters;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.generator.Facet#getPackageName()
     */
    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public String getParameterName() {
        return getClassName();
    }

    public List<Getter> getPrimitiveGetters() {
        return primitiveGetters;
    }

    public List<Setter> getPrimitiveSetters() {
        return primitiveSetters;
    }

    public List<Getter> getRelationshipGetters() {
        return relationshipGetters;
    }

    public List<Setter> getRelationshipSetters() {
        return relationshipSetters;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.java.generator.Facet#getRuleformClass()
     */
    @Override
    public String getRuleformType() {
        return ruleformType;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public void resolve(Map<FacetKey, Facet> facets,
                        WorkspacePresentation presentation,
                        Map<ScopedName, MappedAttribute> mapped) {
        resolveAttributes(mapped);
        resolveRelationships(presentation, facets, mapped);
    }

    @Override
    public String toString() {
        return String.format("FacetImpl [%s]", getClassName());
    }

    private void resolveConstraint(ConstraintContext constraint,
                                   Map<FacetKey, Facet> facets,
                                   Map<ScopedName, MappedAttribute> mapped) {
        Facet type = null;
        if (constraint.anyType == null) {
            FacetKey facetKey = new FacetKey(constraint.authorizedRelationship,
                                             constraint.authorizedParent);
            type = facets.get(facetKey);
            if (type == null) {
                throw new IllegalStateException(String.format("%s refers to non existent facet: %s",
                                                              getClassName(),
                                                              facetKey));
            }
        } else {
            type = resolveAnyFacet(constraint.anyType);
        }
        if (!packageName.equals(type.getPackageName())) {
            imports.add(type.getImport());
        }
        ScopedName key = new ScopedName(constraint.childRelationship);
        Cardinality cardinality;
        switch (constraint.cardinality.getText()
                                      .toUpperCase()) {
            case "ONE":
                cardinality = Cardinality._1;
                break;
            case "ZERO":
                cardinality = Cardinality.Zero;
                break;
            case "N":
                cardinality = Cardinality.N;
                break;
            default:
                throw new IllegalStateException(String.format("Unknown cardinality: %s",
                                                              constraint.cardinality.getText()));
        }
        String className = type.getClassName();
        String baseName = WorkspacePresentation.networkAuthNameOf(constraint);
        baseName = Character.toUpperCase(baseName.charAt(0))
                   + (baseName.length() == 1 ? "" : baseName.substring(1));

        String parameterName = type.getParameterName();
        parameterName = Character.toLowerCase(parameterName.charAt(0))
                        + (parameterName.length() == 1 ? ""
                                                       : parameterName.substring(1));
        switch (cardinality) {
            case _1: {
                relationshipGetters.add(new Getter(key,
                                                   String.format("get%s",
                                                                 baseName),
                                                   className, className,
                                                   baseName));
                relationshipSetters.add(new Setter(key,
                                                   String.format("set%s",
                                                                 baseName),
                                                   className, parameterName,
                                                   className, baseName));
                break;
            }
            case N: {
                resolveList(key, baseName, parameterName, className,
                            constraint.inferredGet == null ? false
                                                           : constraint.inferredGet.getText()
                                                                                   .equals("inferred"));
                break;
            }
            default:
                break;
        }
        resolveAttributes(constraint, mapped, className, parameterName);
    }

    /**
     * @param anyType
     * @return
     */
    private Facet resolveAnyFacet(Token anyType) {
        switch (anyType.getText()) {
            case "*Agency":
                return ANY_AGENCY;
            case "*Attribute":
                return ANY_ATTRIBUTE;
            case "*Interval":
                return ANY_INTERVAL;
            case "*Location":
                return ANY_LOCATION;
            case "*Product":
                return ANY_PRODUCT;
            case "*Relationship":
                return ANY_RELATIONSHIP;
            case "*StatusCode":
                return ANY_STATUS_CODE;
            case "*Unit":
                return ANY_UNIT;
            default:
                throw new IllegalArgumentException(String.format("%s is not a valid *Any",
                                                                 anyType.getText()));
        }
    }

    private void resolveAttributes(ConstraintContext constraint,
                                   Map<ScopedName, MappedAttribute> mapped,
                                   String childType,
                                   String childParameterName) {
        List<ClassifiedAttributeContext> classifiedAttributes = constraint.classifiedAttribute();
        if (classifiedAttributes == null) {
            return;
        }
        classifiedAttributes.forEach(attr -> {
            ScopedName key = new ScopedName(attr.key);
            MappedAttribute attribute = mapped.get(key);
            if (attribute == null) {
                throw new IllegalStateException(String.format("attribute not found: %s",
                                                              key));
            }
            String fieldName = String.format("%sOf%s", attribute.getName(),
                                             childType);
            edgePrimitiveGetters.add(new Getter(key,
                                                String.format("get%s",
                                                              fieldName),
                                                attribute.getType(), childType,
                                                fieldName, childParameterName));
            edgePrimitiveSetters.add(new Setter(key,
                                                String.format("set%s",
                                                              fieldName),
                                                attribute.getType(),
                                                attribute.getName(), childType,
                                                fieldName, childParameterName));
            imports.addAll(attribute.getImports());
        });
    }

    private void resolveAttributes(Map<ScopedName, MappedAttribute> mapped) {
        List<ClassifiedAttributeContext> classifiedAttributes = context.classifiedAttribute();
        if (classifiedAttributes == null) {
            return;
        }
        classifiedAttributes.forEach(attr -> {
            ScopedName key = new ScopedName(attr.key);
            MappedAttribute attribute = mapped.get(key);
            if (attribute == null) {
                throw new IllegalStateException(String.format("attribute not found: %s",
                                                              key));
            }
            primitiveGetters.add(new Getter(key, attribute));
            primitiveSetters.add(new Setter(key, attribute));
            imports.addAll(attribute.getImports());
        });
    }

    private void resolveList(ScopedName key, String baseName,
                             String parameterName, String className,
                             boolean inferredGet) {
        String plural = English.plural(baseName);
        String pluralParameter = English.plural(parameterName);
        String listClassName = String.format("List<%s>", className);
        imports.add("java.util.List");
        if (inferredGet) {
            inferredRelationshipGetters.add(new Getter(key,
                                                       String.format("get%s",
                                                                     plural),
                                                       listClassName, className,
                                                       plural));
            relationshipGetters.add(new Getter(key,
                                               String.format("getImmediate%s",
                                                             plural),
                                               listClassName, className,
                                               plural));
            relationshipSetters.add(new Setter(key,
                                               String.format("setImmediate%s",
                                                             plural),
                                               listClassName,
                                               String.format("%s",
                                                             pluralParameter),
                                               className, plural));
        } else {
            relationshipGetters.add(new Getter(key,
                                               String.format("get%s", plural),
                                               listClassName, className,
                                               plural));
            relationshipSetters.add(new Setter(key,
                                               String.format("set%s", plural),
                                               listClassName, pluralParameter,
                                               className, plural));
        }

        relationshipSetters.add(new Setter(key,
                                           String.format("add%s", baseName),
                                           className, parameterName, className,
                                           plural));
        relationshipSetters.add(new Setter(key,
                                           String.format("remove%s", baseName),
                                           className, parameterName, className,
                                           plural));
        relationshipSetters.add(new Setter(key, String.format("add%s", plural),
                                           listClassName, pluralParameter,
                                           className, plural));
        relationshipSetters.add(new Setter(key,
                                           String.format("remove%s", plural),
                                           listClassName, pluralParameter,
                                           className, plural));
    }

    private void resolveRelationships(WorkspacePresentation presentation,
                                      Map<FacetKey, Facet> facets,
                                      Map<ScopedName, MappedAttribute> mapped) {
        NetworkConstraintsContext networkConstraints = context.networkConstraints();
        if (networkConstraints == null) {
            return;
        }
        networkConstraints.constraint()
                          .forEach(constraint -> {
                              resolveConstraint(constraint, facets, mapped);
                          });
    }
}
