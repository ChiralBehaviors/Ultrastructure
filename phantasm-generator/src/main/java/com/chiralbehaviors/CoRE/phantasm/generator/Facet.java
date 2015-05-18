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

import java.util.List;

/**
 * @author hhildebrand
 *
 */
public class Facet {
    private final String       className;
    private final List<String> imports;
    private final List<Getter> inferredRelationshipGetters;
    private final String       packageName;
    private final List<Getter> primitiveGetters;
    private final List<Setter> primitiveSetters;
    private final List<Getter> relationshipGetters;
    private final List<Setter> relationshipSetters;
    private final String       ruleformType;

    public Facet(List<String> imports, String packageName, String className,
                 String ruleformType, List<Getter> primitiveGetters,
                 List<Setter> primitiveSetters,
                 List<Getter> inferredRelationshipGetters,
                 List<Getter> relationshipGetters,
                 List<Setter> relationshipSetters) {
        this.imports = imports;
        this.packageName = packageName;
        this.className = className;
        this.ruleformType = ruleformType;
        this.primitiveGetters = primitiveGetters;
        this.primitiveSetters = primitiveSetters;
        this.inferredRelationshipGetters = inferredRelationshipGetters;
        this.relationshipGetters = relationshipGetters;
        this.relationshipSetters = relationshipSetters;
    }

    public String getClassName() {
        return className;
    }

    public List<String> getImports() {
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

}
