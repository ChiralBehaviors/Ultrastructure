/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.generators;

public class AnnotationValue {
    final String className;
    final String propName;

    public AnnotationValue(String className, String propName) {
        this.className = className;
        this.propName = propName;
    }

    public String getClassName() {
        return this.className;
    }

    public String getPropName() {
        return this.propName;
    }

    @Override
    public String toString() {
        return String.format("AnnotationValue [%s, %s]", className, propName);
    }
}