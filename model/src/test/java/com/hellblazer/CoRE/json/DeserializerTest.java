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
package com.hellblazer.CoRE.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

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
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.test.DatabaseTest;

/**
 * @author hparry
 * 
 */
public class DeserializerTest extends DatabaseTest {

    Agency core;

    @Before
    public void initData() {
        beginTransaction();
        core = new Agency("CoRE");
        core.setUpdatedBy(core);
        em.persist(core);
        commitTransaction();
        em.clear();
    }

    @Test
    public void testDeserializer() throws JsonParseException,
                                  JsonMappingException, IOException {
        Product p = new Product("P", "P", core);

        SimpleModule module = new SimpleModule();
        module.setMixInAnnotation(Ruleform.class,
                                  PolymorphicRuleformMixin.class);
        module.setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config,
                                                          BeanDescription beanDesc,
                                                          JsonDeserializer<?> deserializer) {
                if (Ruleform.class.isAssignableFrom(beanDesc.getBeanClass()))
                    return new RuleformDeserializer(deserializer, em);
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

        Agency p2 = mapper.readValue(is, Agency.class);
        beginTransaction();
        //em.persist(p2);
        commitTransaction();
    }

}
