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

import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Initializer;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Plugin;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;
import com.chiralbehaviors.CoRE.phantasm.plugin.test.product.Thing1;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
@Plugin(Thing1.class)
public interface Thing1_Plugin {
    static final AtomicReference<String> passThrough = new AtomicReference<>();

    @Initializer
    default void constructor(DataFetchingEnvironment env, Thing1 instance) {
        ExistentialRuleform ruleform = instance.getRuleform();
        ruleform.setDescription(passThrough.get());
        ruleform.update();
    }

    @GraphQLField
    default String instanceMethod(DataFetchingEnvironment env, Model model,
                                  Thing1 instance) {
        return instance.getThing2()
                       .getName();
    }

    @GraphQLField
    default String instanceMethodWithArgument(DataFetchingEnvironment env,
                                              PhantasmCRUD crud,
                                              Thing1 instance,
                                              @GraphQLName("arg1") String arg1) {
        crud.getModel(); // ensure it isn't null;
        env.getArguments(); // not null;
        passThrough.set(arg1);
        return instance.getThing2()
                       .getName();
    }
}
