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
package com.chiralbehaviors.CoRE.json;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.chiralbehaviors.CoRE.Ruleform;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module.Feature;

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
        ObjectMapper objectMapper = (ObjectMapper) context.getOwner();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Hibernate4Module module = new Hibernate4Module();
        module.enable(Feature.FORCE_LAZY_LOADING);
        objectMapper.registerModule(module);
        List<Class<? extends Ruleform>> subTypes = new ArrayList<>();
        for (Class<? extends Ruleform> form : Ruleform.CONCRETE_SUBCLASSES.values()) {
            if (!Modifier.isAbstract(form.getModifiers())) {
                subTypes.add(form);
            }
        }
        registerSubtypes(subTypes.toArray(new Class<?>[subTypes.size()]));
        super.setupModule(context);
    }
}
