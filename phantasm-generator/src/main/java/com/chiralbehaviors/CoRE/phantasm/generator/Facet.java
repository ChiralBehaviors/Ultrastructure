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

import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.FacetContext;
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

    public ScopedName getClassifier() {
        return new ScopedName(facet.classifier);
    }

    public ScopedName getClassification() {
        return new ScopedName(facet.classification);
    }

    public void resolve(Map<FacetKey, Facet> facets,
                        WorkspacePresentation presentation,
                        Map<ScopedName, MappedAttribute> mapped) {
        resolveAttributes(presentation, mapped);
    }

    private void resolveAttributes(WorkspacePresentation presentation,
                                   Map<ScopedName, MappedAttribute> mapped) {
        facet.classifiedAttributes().qualifiedName().forEach(name -> {
            ScopedName key = new ScopedName(name);
            MappedAttribute attribute = mapped.get(key);
            primitiveGetters.add(new Getter(key, attribute));
            primitiveSetters.add(new Setter(key, attribute));
            imports.addAll(attribute.getImports());
        });
    }
}
