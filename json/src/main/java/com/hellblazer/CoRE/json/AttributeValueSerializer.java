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

package com.hellblazer.CoRE.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.attribute.AttributeValue;

/**
 * 
 * @author hhildebrand
 * 
 * @param <T>
 */
public class AttributeValueSerializer<T extends Ruleform>
        extends StdSerializer<AttributeValue<? extends Ruleform>> {

    public AttributeValueSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    @Override
    public void serialize(AttributeValue<? extends Ruleform> value,
                          JsonGenerator jgen, SerializerProvider provider)
                                                                          throws IOException,
                                                                          JsonGenerationException {
        jgen.writeStartObject();
        jgen.writeStringField("attribute", value.getAttribute().getName());
        jgen.writeStringField("value", value.getTextValue());
        jgen.writeEndObject();

    }

    //    @Override
    //    public void serialize(AttributeValue<? extends Ruleform> value,
    //                          JsonGenerator jgen, SerializerProvider provider)
    //                                                                          throws IOException,
    //                                                                          JsonProcessingException {
    //        System.out.println("SERIALIZE");
    //        
    //    }

}
