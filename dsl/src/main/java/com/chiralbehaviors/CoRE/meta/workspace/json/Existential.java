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

import com.chiralbehaviors.CoRE.meta.workspace.json.Rel.NamedRel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author halhildebrand
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "domain", visible = true, defaultImpl = NamedRel.class)
@JsonSubTypes({ @JsonSubTypes.Type(value = Existential.class, name = "Agency"),
                @JsonSubTypes.Type(value = Existential.class, name = "Interval"),
                @JsonSubTypes.Type(value = Existential.class, name = "Location"),
                @JsonSubTypes.Type(value = Existential.class, name = "Product"),
                @JsonSubTypes.Type(value = Rel.class, name = "Relationship"),
                @JsonSubTypes.Type(value = Existential.class, name = "StatusCode") })
public class Existential {
    public enum Domain {
        Agency,
        Interval,
        Location,
        Product,
        Relationship,
        StatusCode;
    }

    @Override
    public String toString() {
        return String.format("Existential [domain=%s, description=%s]", domain,
                             description);
    }

    public String description;
    public Domain domain;
}
