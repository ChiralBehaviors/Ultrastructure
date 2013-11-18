/**
 * Copyright (C) 2013 Halloran Parry. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hellblazer.CoRE.access;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.access.resource.PolymorphicRuleformMixin;

/**
 * A jackson module for registering serializers and deserializers.
 * 
 * @author hparry
 * 
 */
public class CoREModule extends SimpleModule {

    private static final long serialVersionUID = 1L;

    public CoREModule() {
        super("CoREModule");
    }

    @Override
    public void setupModule(SetupContext context) {

        context.setMixInAnnotations(Ruleform.class,
                                    PolymorphicRuleformMixin.class);
        super.setupModule(context);
    }
}
