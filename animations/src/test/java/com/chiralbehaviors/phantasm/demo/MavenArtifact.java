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

import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.phantasm.PhantasmBase;
import com.chiralbehaviors.CoRE.phantasm.annotations.Aspect;
import com.chiralbehaviors.CoRE.phantasm.annotations.State;

/**
 * @author hhildebrand
 *
 */
@State(facets = { @Aspect(classifier = "MavenArtifact") })
public interface MavenArtifact extends PhantasmBase<Location> {
    String getArtifactId();

    String getClassifier();

    String getGroupId();

    String getType();

    String getVersion();

    void setArtifactId(String artifactId);

    void setClassifier(String classifier);

    void setGroupId(String groupId);

    void setType(String type);

    void setVersion(String version);
}
