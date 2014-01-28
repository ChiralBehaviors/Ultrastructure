/** 
 * (C) Copyright 2014 Chiral Behaviors, LLC, All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package com.hellblazer.CoRE.access;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.json.PolymorphicRuleformMixin;

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
