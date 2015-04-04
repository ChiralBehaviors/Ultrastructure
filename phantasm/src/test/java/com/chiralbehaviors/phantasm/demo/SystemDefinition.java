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

package com.chiralbehaviors.phantasm.demo;

import java.util.List;

import com.chiralbehaviors.CoRE.phantasm.annotations.Aspect;
import com.chiralbehaviors.CoRE.phantasm.annotations.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.annotations.Relationship;

/**
 * @author hhildebrand
 *
 */
@Phantasm()
public interface SystemDefinition {
    @Relationship(name = "inDeployment")
    DeploymentDefinition getDeployment();

    @Relationship(name = "systemOf", intersect = { @Aspect(classifier = "isA", classification = "FunctionDefinition") })
    List<FunctionDefinition> getFunctions();

    @Relationship(name = "systemOf")
    void addFunction(FunctionDefinition function);
}
