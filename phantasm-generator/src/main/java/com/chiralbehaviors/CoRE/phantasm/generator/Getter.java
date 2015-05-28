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

/**
 * @author hhildebrand
 *
 */
public class Getter {
    private final ScopedName key;
    private final String     methodName;
    private final String     returnType;

    public Getter(ScopedName key, MappedAttribute mappedAttribute) {
        this(key, String.format("get%s", mappedAttribute.getName()),
             mappedAttribute.getType());
    }

    public Getter(ScopedName key, String methodName, String returnType) {
        this.key = key;
        this.methodName = methodName;
        this.returnType = returnType;
    }

    public ScopedName getKey() {
        return key;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getReturnType() {
        return returnType;
    }
}
