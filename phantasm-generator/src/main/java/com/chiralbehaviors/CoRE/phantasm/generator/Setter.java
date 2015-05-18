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

/**
 * @author hhildebrand
 *
 */
public class Setter {
    private final ScopedName key;
    private final String     methodName;
    private final String     parameterName;
    private final String     parameterType;

    public Setter(ScopedName key, String methodName, String parameterType,
                  String parameterName) {
        this.key = key;
        this.methodName = methodName;
        this.parameterType = parameterType;
        this.parameterName = parameterName;
    }

    public ScopedName getKey() {
        return key;
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
}
