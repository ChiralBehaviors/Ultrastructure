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

package com.chiralbehaviors.CoRE.phantasm.plugin.test;

import java.util.concurrent.atomic.AtomicReference;

import com.chiralbehaviors.CoRE.phantasm.java.annotations.Initializer;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Plugin;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;
import com.chiralbehaviors.CoRE.phantasm.plugin.test.thing1Properties.Thing1Properties;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
@Plugin(Thing1.class)
public class Thing1_Plugin {
    public static final AtomicReference<String> passThrough = new AtomicReference<>();

    @Initializer
    public static void constructor(DataFetchingEnvironment env) {
        ((PhantasmCRUD) env.getContext()).getModel(); // ensure it isn't null;
        env.getArguments(); // not null;
        Thing1Properties props = new Thing1Properties();
        props.setDescription(passThrough.get()); 
        ((Thing1) env.getSource()).set_Properties(props);
    }

    @GraphQLField
    public static String instanceMethod(DataFetchingEnvironment env) {
        return ((Thing1) env.getSource()).getThing2()
                                         .get_Properties()
                                         .getName();
    }

    @GraphQLField
    public static String instanceMethodWithArgument(@GraphQLName("arg1") String arg1,
                                                    DataFetchingEnvironment env) {
        ((PhantasmCRUD) env.getContext()).getModel(); // ensure it isn't null;
        env.getArguments(); // not null;
        passThrough.set(arg1);
        return ((Thing1) env.getSource()).getThing2()
                                         .get_Properties()
                                         .getName();
    }
}
