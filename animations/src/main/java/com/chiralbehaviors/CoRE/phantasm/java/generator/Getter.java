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
    private final String     childType;
    private final ScopedName key;
    private final String     methodName;
    private final String     returnType;
    private final String     fieldName;

    public Getter(ScopedName key, MappedAttribute mappedAttribute) {
        this(key, String.format("get%s", mappedAttribute.getName()),
             mappedAttribute.getType(), null, mappedAttribute.getName());
    }

    public Getter(ScopedName key, String methodName, String returnType,
                  String childType, String fieldName) {
        this.key = key;
        this.methodName = methodName;
        this.returnType = returnType;
        this.childType = childType;
        this.fieldName = PhantasmGenerator.toFieldName(fieldName);
    }

    public ScopedName getAttribute() {
        return key;
    }

    public String getChildType() {
        return childType;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getReturnType() {
        return returnType;
    }

    public ScopedName getRelationship() {
        return key;
    }

    public String getFieldName() {
        return fieldName;
    }
}
