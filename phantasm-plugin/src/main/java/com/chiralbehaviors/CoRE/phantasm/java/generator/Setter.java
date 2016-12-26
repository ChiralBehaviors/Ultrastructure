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

/**
 * @author hhildebrand
 *
 */
public class Setter {
    private final String     childParameterName;
    private final String     childType;
    private final String     fieldName;
    private final ScopedName key;
    private final String     methodName;
    private final String     parameterName;
    private final String     parameterType;

    public Setter(ScopedName key, MappedAttribute attribute) {
        this.key = key;
        String name = attribute.getName();
        this.methodName = String.format("set%s", name);
        this.parameterType = attribute.getType();
        this.parameterName = Character.toLowerCase(name.charAt(0))
                             + (name.length() == 1 ? "" : name.substring(1));
        this.fieldName = PhantasmGenerator.toFieldName(name);
        this.childType = null;
        this.childParameterName = null;
    }

    public Setter(ScopedName key, String methodName, String parameterType,
                  String parameterName, String childType, String baseName) {
        this(key, methodName, parameterType, parameterName, childType, baseName,
             null);
    }

    public Setter(ScopedName key, String methodName, String parameterType,
                  String parameterName, String childType, String baseName,
                  String childParameterName) {
        this.key = key;
        this.methodName = methodName;
        this.parameterType = parameterType;
        this.parameterName = parameterName;
        this.childType = childType;
        this.fieldName = PhantasmGenerator.toFieldName(baseName);
        this.childParameterName = childParameterName;
    }

    public ScopedName getAttribute() {
        return key;
    }

    public String getChildParameterName() {
        return childParameterName;
    }

    public String getChildType() {
        return childType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getParameterName() {
        return parameterName;
    }

    public String getParameterType() {
        return parameterType;
    }

    public ScopedName getRelationship() {
        return key;
    }
}
