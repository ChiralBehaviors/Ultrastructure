package com.hellblazer.CoRE.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.attribute.AttributeValue;

public class AttributeValueSerializer<T extends AttributeValue<? extends Ruleform>>
        extends StdSerializer<AttributeValue<? extends Ruleform>> {

    public AttributeValueSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
        // TODO Auto-generated constructor stub
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
