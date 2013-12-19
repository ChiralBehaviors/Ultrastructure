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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author hparry
 * 
 */
public class TatusTest {

    @Test
    public void testDeserialize() throws JsonGenerationException,
                                 JsonMappingException, IOException {
        ConcreteObject c = new ConcreteObject(0, "Rock Hard", null, "Concrete");
        c.setUpdatedBy(c);

        SimpleModule module = new SimpleModule();
        module.setMixInAnnotation(AbstractObject.class, PolymorphMixin.class);
        //        module.setDeserializerModifier(new BeanDeserializerModifier() {
        //            @Override
        //            public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config,
        //                                                          BeanDescription beanDesc,
        //                                                          JsonDeserializer<?> deserializer) {
        //                if (Ruleform.class.isAssignableFrom(beanDesc.getBeanClass()))
        //                    return new RuleformDeserializer(deserializer, em);
        //                return deserializer;
        //            }
        //        });

        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
        mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        mapper.registerModule(module);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mapper.writeValue(baos, c);
        System.out.println(baos.toString());
        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        ConcreteObject c2 = mapper.readValue(is, ConcreteObject.class);
        assertEquals(c.value, c2.value);
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
            @Type(value = AbstractObject.class, name = "abstractObject"),
            @Type(value = ConcreteObject.class, name = "concreteObject") })
    public abstract class PolymorphMixin {
    }

}
