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

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author halhildebrand
 *
 */
public class Edge {
    public String   c;
    public String   facet;
    public String   p;
    public JsonNode properties;
    public String   r;

    @Override
    public String toString() {
        return String.format("Edge [p=%s, r=%s, c=%s, facet=%s, properties=%s]",
                             p, r, c, facet == null ? "-" : facet, properties);
    }

}
