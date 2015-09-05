/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
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

package com.chiralbehaviors.CoRE.phantasm.resources.plugin;

import java.util.concurrent.atomic.AtomicReference;

import com.chiralbehaviors.CoRE.phantasm.resource.test.product.Thing1;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public class Thing1_Plugin {
    public static final AtomicReference<String> passThrough = new AtomicReference<>();

    public static void constructor(DataFetchingEnvironment env,
                                   Thing1 instance) {
        instance.getRuleform()
                .setDescription(passThrough.get());
    }

    public static String instanceMethod(DataFetchingEnvironment env,
                                        Thing1 instance) {
        return instance.getThing2()
                       .getName();
    }

    public static String instanceMethodWithArgument(DataFetchingEnvironment env,
                                                    Thing1 instance) {
        passThrough.set(env.getArgument("arg1"));
        return instance.getThing2()
                       .getName();
    }

    public static String staticMethod(DataFetchingEnvironment env) {
        return passThrough.get();
    }
}
