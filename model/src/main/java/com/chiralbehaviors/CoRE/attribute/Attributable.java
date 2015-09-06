/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.attribute;

import java.util.Set;

import com.chiralbehaviors.CoRE.Ruleform;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The interface for data types that have attributes
 *
 * @author hhildebrand
 *
 */
public interface Attributable<AttributeType extends Ruleform> {
    @JsonIgnore
    <A extends AttributeType> Set<A> getAttributes();

    @JsonIgnore
    <A extends AttributeType> void setAttributes(Set<A> attributes);
}
