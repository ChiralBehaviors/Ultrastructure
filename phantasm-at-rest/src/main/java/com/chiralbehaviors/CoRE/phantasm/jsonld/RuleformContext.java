/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.phantasm.jsonld;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.attribute.ValueType;
import com.chiralbehaviors.CoRE.network.Cardinality;
import com.chiralbehaviors.CoRE.phantasm.jsonld.resources.RuleformResource;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

/**
 * @author hhildebrand
 *
 */
public class RuleformContext implements JsonSerializable {
    private static final Map<Class<?>, String> TYPES = new HashMap<>();

    static {
        // initialize primitive types
        TYPES.put(String.class, "http://www.w3.org/2001/XMLSchema#text");
        TYPES.put(Integer.class, "http://www.w3.org/2001/XMLSchema#int");
        TYPES.put(Integer.TYPE, "http://www.w3.org/2001/XMLSchema#int");
        TYPES.put(BigDecimal.class, "http://www.w3.org/2001/XMLSchema#number");
        TYPES.put(Boolean.class, "http://www.w3.org/2001/XMLSchema#boolean");
        TYPES.put(Boolean.TYPE, "http://www.w3.org/2001/XMLSchema#boolean");
        TYPES.put(Timestamp.class,
                  "http://www.w3.org/2001/XMLSchema#date-dateTime");
        TYPES.put(UUID.class, "http://www.w3.org/2001/XMLSchema#uuid");
        TYPES.put(ValueType.class, "http://www.w3.org/2001/XMLSchema#text");
        TYPES.put(Cardinality.class, "http://www.w3.org/2001/XMLSchema#text");
    }

    public static String getContextIri(Class<? extends Ruleform> ruleformClass,
                                       UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(RuleformResource.class);
        try {
            ub.path(RuleformResource.class.getMethod("getContext",
                                                     String.class));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Cannot get getContext method", e);
        }
        ub.resolveTemplate("ruleform-type", ruleformClass.getSimpleName());
        return ub.build().toASCIIString();
    }

    public static List<Field> getInheritedFields(Class<?> type) {
        List<Field> fields = new ArrayList<Field>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                if (field.getName().contains("$")
                    || Modifier.isStatic(field.getModifiers())
                    || field.getAnnotation(OneToMany.class) != null) {
                    continue;
                }
                fields.add(field);
            }
        }
        return fields;
    }

    public static String getTermIri(Class<? extends Ruleform> ruleformClass,
                                    String term, UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(RuleformResource.class);
        try {
            ub.path(RuleformResource.class.getMethod("getType", String.class));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Cannot get getType method", e);
        }
        ub.resolveTemplate("ruleform-type", ruleformClass.getSimpleName());
        ub.fragment(term);
        return ub.build().toASCIIString();
    }

    public static String getTypeIri(Class<? extends Ruleform> ruleformClass,
                                    UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(RuleformResource.class);
        try {
            ub.path(RuleformResource.class.getMethod("getType", String.class));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Cannot get getType method", e);
        }
        ub.resolveTemplate("ruleform-type", ruleformClass.getSimpleName());
        return ub.build().toASCIIString();
    }

    private final Class<? extends Ruleform> ruleformClass;

    private final UriInfo uriInfo;

    public RuleformContext(Class<? extends Ruleform> ruleformClass,
                           UriInfo uriInfo) {
        this.ruleformClass = ruleformClass;
        this.uriInfo = uriInfo;
    }

    /* (non-Javadoc)
     * @see com.fasterxml.jackson.databind.JsonSerializable#serialize(com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
     */
    @Override
    public void serialize(JsonGenerator gen,
                          SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeObjectFieldStart(Constants.CONTEXT);
        writeContext(gen);
        gen.writeEndObject();
        gen.writeEndObject();
    }

    /* (non-Javadoc)
     * @see com.fasterxml.jackson.databind.JsonSerializable#serializeWithType(com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider, com.fasterxml.jackson.databind.jsontype.TypeSerializer)
     */
    @Override
    public void serializeWithType(JsonGenerator gen,
                                  SerializerProvider serializers,
                                  TypeSerializer typeSer) throws IOException {
        serialize(gen, serializers);
    }

    public void writeContext(JsonGenerator gen) throws IOException {
        for (Field field : getInheritedFields(ruleformClass)) {
            if (field.getAnnotation(JoinColumn.class) == null) {
                writePrimitiveTerm(field, gen);
            } else {
                writeRuleformTerm(field, gen);
            }
        }
    }

    private void writePrimitiveTerm(Field field,
                                    JsonGenerator gen) throws IOException {
        gen.writeObjectFieldStart(field.getName());
        gen.writeStringField(Constants.ID,
                             getTermIri(ruleformClass, field.getName(),
                                        uriInfo));
        gen.writeStringField(Constants.TYPE, TYPES.get(field.getType()));
        gen.writeEndObject();
    }

    private void writeRuleformTerm(Field field,
                                   JsonGenerator gen) throws IOException {
        gen.writeObjectFieldStart(field.getName());
        gen.writeStringField(Constants.ID,
                             getTermIri(ruleformClass, field.getName(),
                                        uriInfo));
        gen.writeStringField(Constants.TYPE, Constants.ID);
        gen.writeEndObject();
    }

}
