/**
 * Copyright (c) 2018 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 *  This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.meta.workspace.json;

/**
 * @author halhildebrand
 *
 */
public class Rel extends Existential {

    public static class NamedRel extends Rel {
        public String name;
    }
    
    @Override
    public String toString() {
        return String.format("Rel [domain=%s, description=%s, inverse=%s]",
                             domain, description, inverse);
    }

    public NamedRel inverse;

    public Rel() {
        domain = Domain.Relationship;
    }
}
