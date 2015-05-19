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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ClassifiedAttributesContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ConstraintContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.FacetContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.NetworkConstraintsContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspacePresentation;

/**
 * @author hhildebrand
 *
 */
public class Facet {
    private final FacetContext facet;
    private final Set<String>  imports                     = new HashSet<>();
    private final List<Getter> inferredRelationshipGetters = new ArrayList<>();
    private final String       packageName;
    private final List<Getter> primitiveGetters            = new ArrayList<>();
    private final List<Setter> primitiveSetters            = new ArrayList<>();
    private final List<Getter> relationshipGetters         = new ArrayList<>();
    private final List<Setter> relationshipSetters         = new ArrayList<>();
    private final String       ruleformType;

    public Facet(String packageName, String ruleformType, FacetContext facet) {
        this.packageName = packageName;
        this.facet = facet;
        this.ruleformType = ruleformType;
    }

    public ScopedName getClassification() {
        return new ScopedName(facet.classification);
    }

    public ScopedName getClassifier() {
        return new ScopedName(facet.classifier);
    }

    public String getClassName() {
        return facet.classification.member.getText();
    }

    public Set<String> getImports() {
        return imports;
    }

    public List<Getter> getInferredRelationshipGetters() {
        return inferredRelationshipGetters;
    }

    public String getPackageName() {
        return packageName;
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

    public void resolve(Map<FacetKey, Facet> facets,
                        WorkspacePresentation presentation,
                        Map<ScopedName, MappedAttribute> mapped) {
        resolveAttributes(presentation, mapped);
        resolveRelationships(presentation, facets);
    }

    private void resolveAttributes(WorkspacePresentation presentation,
                                   Map<ScopedName, MappedAttribute> mapped) {
        ClassifiedAttributesContext classifiedAttributes = facet.classifiedAttributes();
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

    private void resolveRelationships(WorkspacePresentation presentation,
                                      Map<FacetKey, Facet> facets) {
        NetworkConstraintsContext networkConstraints = facet.networkConstraints();
        if (networkConstraints == null) {
            return;
        }
        networkConstraints.constraint().forEach(constraint -> {
            resolve(constraint, facets);
        });
    }

    private void resolve(ConstraintContext constraint,
                         Map<FacetKey, Facet> facets) {
        FacetKey facetKey = new FacetKey(constraint.authorizedRelationship,
                                         constraint.authorizedParent);
        Facet type = facets.get(facetKey);
        if (type == null) {
            throw new IllegalStateException(
                                            String.format("%s refers to non existent facet: %s",
                                                          getClassName(),
                                                          facetKey));
        }
        if (!packageName.equals(type.packageName)) {
            imports.add(type.getImport());
        }
        ScopedName key = new ScopedName(constraint.childRelationship);
        int cardinality = Integer.parseInt(constraint.cardinality.getText());
        String className = type.getClassName();
        String parameterName = Character.toLowerCase(className.charAt(0))
                               + (className.length() == 1 ? ""
                                                         : className.substring(1));
        if (cardinality == 1) {
            relationshipGetters.add(new Getter(key, String.format("get%s",
                                                                  className),
                                               className));
            relationshipSetters.add(new Setter(key, String.format("set%s",
                                                                  className),
                                               className, parameterName));
        } else if (cardinality > 1) {
            resolveList(key,
                        parameterName,
                        className,
                        constraint.inferredGet == null ? false
                                                      : constraint.inferredGet.getText().equals("inferred"));
        }
    }

    private void resolveList(ScopedName key, String parameterName,
                             String className, boolean inferredGet) {
        if (inferredGet) {
            inferredRelationshipGetters.add(new Getter(
                                                       key,
                                                       String.format("get%ss",
                                                                     className),
                                                       className));
            relationshipGetters.add(new Getter(key,
                                               String.format("getImmediate%ss",
                                                             className),
                                               className));
            relationshipSetters.add(new Setter(key,
                                               String.format("setImmediate%ss",
                                                             className),
                                               className,
                                               String.format("%ss",
                                                             parameterName)));
        } else {
            relationshipGetters.add(new Getter(key, String.format("get%ss",
                                                                  className),
                                               className));
            relationshipSetters.add(new Setter(key, String.format("set%ss",
                                                                  className),
                                               className,
                                               String.format("%ss",
                                                             parameterName)));
        }

        relationshipSetters.add(new Setter(key, String.format("add%s",
                                                              className),
                                           className, parameterName));
        relationshipSetters.add(new Setter(key, String.format("remove%s",
                                                              className),
                                           className, parameterName));

        imports.add("java.util.List");
        relationshipSetters.add(new Setter(
                                           key,
                                           String.format("add%ss", className),
                                           String.format("List<%s>", className),
                                           String.format("%ss", parameterName)));
        relationshipSetters.add(new Setter(
                                           key,
                                           String.format("remove%ss", className),
                                           String.format("List<%s>", className),
                                           String.format("%ss", parameterName)));
    }

    private String getImport() {
        return String.format("%s.%s", packageName, getClassName());
    }
}
