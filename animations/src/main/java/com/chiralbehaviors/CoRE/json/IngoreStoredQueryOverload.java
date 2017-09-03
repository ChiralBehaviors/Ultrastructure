/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.json;

import java.util.Collection;

import org.jooq.Field;
import org.jooq.Identity;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author hhildebrand
 *
 */
public abstract class IngoreStoredQueryOverload {
    @JsonIgnore
    public abstract void setReturning();

    @JsonIgnore
    public abstract void setReturning(Identity<?, ?> identity);

    @JsonIgnore
    public abstract void setReturning(Field<?>... fields);

    @JsonIgnore
    public abstract void setReturning(Collection<? extends Field<?>> fields);
}
