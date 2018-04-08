/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.json;

import java.io.IOException;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.tools.StringUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

/**
 * @author hhildebrand
 *
 */
public class RecordSerializer extends JsonSerializer<Record> {
    @SuppressWarnings("deprecation")
    @Override
    public void serializeWithType(Record value, JsonGenerator gen,
                                  SerializerProvider provider,
                                  TypeSerializer typeSer) throws IOException,
                                                          JsonProcessingException {

        typeSer.writeTypePrefixForObject(value, gen);
        serialize(value, gen, provider); // call your customized serialize method
        typeSer.writeTypeSuffixForObject(value, gen);
    }

    @Override
    public Class<Record> handledType() {
        return Record.class;
    }

    @Override
    public void serialize(Record record, JsonGenerator jgen,
                          SerializerProvider provider) throws IOException,
                                                       JsonProcessingException {
        for (Field<?> field : record.fields()) {
            Object value = record.getValue(field);
            if (value != null) {
                jgen.writeFieldName(StringUtils.toCamelCaseLC(field.getName()));
                jgen.writeObject(value);
            }
        }
    }
}
