/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
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
