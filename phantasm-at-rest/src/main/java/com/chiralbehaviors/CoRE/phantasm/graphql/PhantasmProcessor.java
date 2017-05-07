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

import java.lang.reflect.Method;

import graphql.annotations.GraphQLAnnotations;
import graphql.annotations.TypeFunction;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import graphql.schema.TypeResolver;

/**
 * @author halhildebrand
 *
 */
public class PhantasmProcessor extends GraphQLAnnotations
        implements TypeResolver {
    public static PhantasmProcessor singleton = new PhantasmProcessor();

    public static GraphQLFieldDefinition field(Method method) throws InstantiationException,
                                                              IllegalAccessException {
        return singleton.getField(method);

    }

    public static void register(TypeFunction typeFunction) {
        singleton.registerType(typeFunction);
    }

    /* (non-Javadoc)
     * @see graphql.schema.TypeResolver#getType(java.lang.Object)
     */
    @Override
    public GraphQLObjectType getType(Object object) {
        return (GraphQLObjectType) defaultTypeFunction.apply(object.getClass(),
                                                             null);
    }

    public GraphQLType typeFor(Class<?> clazz) {
        return  defaultTypeFunction.apply(clazz, null);
    }

    public TypeFunction typeResolver() {
        return defaultTypeFunction;
    }
}
