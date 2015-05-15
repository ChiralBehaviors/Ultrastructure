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

import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.annotations.Edge;
import com.chiralbehaviors.annotations.Inferred;
import com.chiralbehaviors.annotations.Key;
import com.chiralbehaviors.annotations.State;

/**
 * @author hhildebrand
 *
 */
@State(workspace = "uri:http://ultrastructure.me/ontology/com.chiralbehaviors/demo/phantasm/v1")
public interface Thing2 extends Phantasm<Product> {
    @Edge(@Key(name = "thing2Of"))
    void add(List<Thing3> thing3s);

    // 
    @Edge(@Key(name = "thing2Of"))
    void add(Thing3 thing3);

    @Edge(@Key(name = "derivedFrom"))
    void addArtifact(MavenArtifact artifact);

    @Edge(@Key(name = "derivedFrom"))
    void addArtifacts(List<MavenArtifact> artifacts);

    @Edge(@Key(name = "derivedFrom"))
    List<MavenArtifact> getArtifacts();

    @Edge(@Key(name = "inThing1"))
    Thing1 getThing1();

    // Get immediate or inferred thing3s of this thing2
    @Inferred
    @Edge(@Key(name = "thing2Of"))
    List<Thing3> getThing3s();

    @Edge(@Key(name = "thing2Of"))
    void remove(List<Thing3> thing3s);

    @Edge(@Key(name = "thing2Of"))
    void remove(Thing3 thing3);

    @Edge(@Key(name = "derivedFrom"))
    void removeArtifact(MavenArtifact artifact);

    @Edge(@Key(name = "derivedFrom"))
    void setArtifacts(List<MavenArtifact> artifacts);
}
