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

package com.chiralbehaviors.CoRE.phantasm.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.Token;

import com.chiralbehaviors.CoRE.network.Cardinality;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ClassifiedAttributesContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ConstraintContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.FacetContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.NetworkConstraintsContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspacePresentation;

/**
 * @author hhildebrand
 *
 */
public class FacetImpl implements Facet {

    private final FacetContext context;
    private final Set<String>  imports                     = new HashSet<>();
    private final List<Getter> inferredRelationshipGetters = new ArrayList<>();
    private final String       packageName;
    private final List<Getter> primitiveGetters            = new ArrayList<>();
    private final List<Setter> primitiveSetters            = new ArrayList<>();
    private final List<Getter> relationshipGetters         = new ArrayList<>();
    private final List<Setter> relationshipSetters         = new ArrayList<>();
    private final String       ruleformType;
    private final String       uri;

    public FacetImpl(String packageName, String ruleformType, FacetContext context, String uri) {
        this.packageName = packageName;
        this.context = context;
        this.ruleformType = ruleformType;
        this.uri = uri;
    }

    public ScopedName getClassification() {
        return new ScopedName(context.classification);
    }

    public ScopedName getClassifier() {
        return new ScopedName(context.classifier);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.phantasm.generator.Facet#getClassName()
     */
    @Override
    public String getClassName() {
        return context.classification.member.getText();
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

    public String getRuleformType() {
        return ruleformType;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public void resolve(Map<FacetKey, Facet> facets, WorkspacePresentation presentation,
                        Map<ScopedName, MappedAttribute> mapped) {
        resolveAttributes(presentation, mapped);
        resolveRelationships(presentation, facets);
    }

    private void resolve(ConstraintContext constraint, Map<FacetKey, Facet> facets) {
        Facet type = null;
        if (constraint.anyType == null) {
            FacetKey facetKey = new FacetKey(constraint.authorizedRelationship, constraint.authorizedParent);
            type = facets.get(facetKey);
            if (type == null) {
                throw new IllegalStateException(String.format("%s refers to non existent facet: %s", getClassName(),
                                                              facetKey));
            }
        } else {
            type = resolveAnyFacet(constraint.anyType);
        }
        if (!packageName.equals(type.getPackageName())) {
            imports.add(type.getImport());
        }
        ScopedName key = new ScopedName(constraint.childRelationship);
        Cardinality cardinality = Enum.valueOf(Cardinality.class, constraint.cardinality.getText().toUpperCase());
        Token methodName = constraint.name;
        String className = type.getClassName();
        String baseName;
        if (methodName != null) {
            baseName = methodName.getText();
        } else {
            baseName = constraint.childRelationship.member.getText();
        }
        baseName = Character.toUpperCase(baseName.charAt(0)) + (baseName.length() == 1 ? "" : baseName.substring(1));

        String parameterName = type.getParameterName();
        parameterName = Character.toLowerCase(parameterName.charAt(0))
                        + (parameterName.length() == 1 ? "" : parameterName.substring(1));
        switch (cardinality) {
            case ONE: {
                relationshipGetters.add(new Getter(key, String.format("get%s", baseName), className));
                relationshipSetters.add(new Setter(key, String.format("set%s", baseName), className, parameterName));
                break;
            }
            case N: {
                resolveList(key, baseName, parameterName, className,
                            constraint.inferredGet == null ? false
                                                           : constraint.inferredGet.getText().equals("inferred"));
                break;
            }
            default:
                break;
        }
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
                throw new IllegalArgumentException(String.format("%s is not a valid *Any", anyType.getText()));
        }
    }

    private void resolveAttributes(WorkspacePresentation presentation, Map<ScopedName, MappedAttribute> mapped) {
        ClassifiedAttributesContext classifiedAttributes = context.classifiedAttributes();
        if (classifiedAttributes == null) {
            return;
        }
        classifiedAttributes.qualifiedName().forEach(name -> {
            ScopedName key = new ScopedName(name);
            MappedAttribute attribute = mapped.get(key);
            primitiveGetters.add(new Getter(key, attribute));
            primitiveSetters.add(new Setter(key, attribute));
            imports.addAll(attribute.getImports());
        });
    }

    private void resolveList(ScopedName key, String baseName, String parameterName, String className,
                             boolean inferredGet) {
        String plural = English.plural(baseName);
        String pluralParameter = English.plural(parameterName);
        String listClassName = String.format("List<%s>", className);
        imports.add("java.util.List");
        if (inferredGet) {
            inferredRelationshipGetters.add(new Getter(key, String.format("get%s", plural), listClassName));
            relationshipGetters.add(new Getter(key, String.format("getImmediate%s", plural), listClassName));
            relationshipSetters.add(new Setter(key, String.format("setImmediate%s", plural), listClassName,
                                               String.format("%s", pluralParameter)));
        } else {
            relationshipGetters.add(new Getter(key, String.format("get%s", plural), listClassName));
            relationshipSetters.add(new Setter(key, String.format("set%s", plural), listClassName, pluralParameter));
        }

        relationshipSetters.add(new Setter(key, String.format("add%s", baseName), className, parameterName));
        relationshipSetters.add(new Setter(key, String.format("remove%s", baseName), className, parameterName));
        relationshipSetters.add(new Setter(key, String.format("add%s", plural), listClassName, pluralParameter));
        relationshipSetters.add(new Setter(key, String.format("remove%s", plural), listClassName, pluralParameter));
    }

    private void resolveRelationships(WorkspacePresentation presentation, Map<FacetKey, Facet> facets) {
        NetworkConstraintsContext networkConstraints = context.networkConstraints();
        if (networkConstraints == null) {
            return;
        }
        networkConstraints.constraint().forEach(constraint -> {
            resolve(constraint, facets);
        });
    }
}
