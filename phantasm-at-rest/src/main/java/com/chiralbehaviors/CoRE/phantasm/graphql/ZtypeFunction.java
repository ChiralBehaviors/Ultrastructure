/**
 * Copyright (c) 2017 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.phantasm.graphql;

import java.lang.reflect.AnnotatedType;
import java.util.Collection;
import java.util.Collections;

import graphql.annotations.TypeFunction;
import graphql.schema.GraphQLType;

/**
 * @author halhildebrand
 *
 */
public class ZtypeFunction implements TypeFunction {
    private final GraphQLType graphQlType;
    private final Class<?>    type;

    public ZtypeFunction(Class<?> type, GraphQLType graphQlType) {
        super();
        this.graphQlType = graphQlType;
        this.type = type;
    }

    /* (non-Javadoc)
     * @see java.util.function.BiFunction#apply(java.lang.Object, java.lang.Object)
     */
    @Override
    public GraphQLType apply(Class<?> t, AnnotatedType u) {
        if (type.isAssignableFrom(t)) {
            return graphQlType;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see graphql.annotations.TypeFunction#getAcceptedTypes()
     */
    @Override
    public Collection<Class<?>> getAcceptedTypes() {
        return Collections.singletonList(type);
    }

}
