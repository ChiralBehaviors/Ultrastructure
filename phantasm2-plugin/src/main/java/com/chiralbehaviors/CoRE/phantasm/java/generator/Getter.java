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
public class Getter {
    private final String     childParameterName;
    private final String     childType;
    private final String     fieldName;
    private final ScopedName key;
    private final String     methodName;
    private final String     returnType;

    public Getter(ScopedName key, MappedAttribute mappedAttribute) {
        this(key, String.format("get%s", mappedAttribute.getName()),
             mappedAttribute.getType(), null, mappedAttribute.getName(), null);
    }

    public Getter(ScopedName key, String methodName, String returnType,
                  String childType, String fieldName) {
        this(key, methodName, returnType, childType, fieldName, null);
    }

    public Getter(ScopedName key, String methodName, String returnType,
                  String childType, String fieldName,
                  String childParameterName) {
        this.key = key;
        this.methodName = methodName;
        this.returnType = returnType;
        this.childType = childType;
        this.fieldName = PhantasmGenerator.toFieldName(fieldName);
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

    public ScopedName getRelationship() {
        return key;
    }

    public String getReturnType() {
        return returnType;
    }
}
