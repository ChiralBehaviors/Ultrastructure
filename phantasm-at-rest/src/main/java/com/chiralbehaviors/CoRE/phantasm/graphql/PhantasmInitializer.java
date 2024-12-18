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

package com.chiralbehaviors.CoRE.phantasm.graphql;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.chiralbehaviors.CoRE.phantasm.Phantasm;

import graphql.schema.DataFetchingEnvironment;

public class PhantasmInitializer {
    private final Method method;

    public PhantasmInitializer(Method method) {
        this.method = method;
    }

    public Object get(DataFetchingEnvironment environment, Phantasm phantasm) {
        if (environment.getSource() == null) {
            return null;
        }
        environment = new DataFetchingEnvironment(phantasm,
                                                  environment.getArguments(),
                                                  environment.getContext(),
                                                  environment.getFields(),
                                                  environment.getFieldType(),
                                                  environment.getParentType(),
                                                  environment.getGraphQLSchema());
        try {
            method.invoke(null, environment);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getTargetException());
        }
        return phantasm;
    }
}
