/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.test.DatabaseTest;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author hparry
 * 
 */
public class DeserializerTest extends DatabaseTest {

    Agency core;

    @Before
    public void initData() {
        core = new Agency("CoRE");
        core.setUpdatedBy(core);
        em.persist(core);
        em.flush();
        em.clear();
    }

    //@Test
    public void testDeserializer() throws JsonParseException,
                                  JsonMappingException, IOException {
        new Product("P", "P", core);

        SimpleModule module = new SimpleModule();
        module.setMixInAnnotation(Ruleform.class,
                                  PolymorphicRuleformMixin.class);
        module.setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config,
                                                          BeanDescription beanDesc,
                                                          JsonDeserializer<?> deserializer) {
                if (Ruleform.class.isAssignableFrom(beanDesc.getBeanClass())) {
                    return new RuleformDeserializer(deserializer, em);
                }
                return deserializer;
            }
        });

        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
        mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        mapper.registerModule(module);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mapper.writeValue(baos, core);
        System.out.println(baos.toString());
        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        mapper.readValue(is, Agency.class);
    }

}
