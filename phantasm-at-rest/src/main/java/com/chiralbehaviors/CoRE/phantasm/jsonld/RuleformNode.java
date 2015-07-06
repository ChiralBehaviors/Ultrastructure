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
import java.math.BigDecimal;

import javax.persistence.JoinColumn;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.phantasm.jsonld.resources.RuleformResource;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

/**
 * @author hhildebrand
 *
 */
public class RuleformNode implements JsonSerializable {

    public static String getIri(Ruleform ruleform, UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(RuleformResource.class);
        try {
            ub.path(RuleformResource.class.getMethod("getInstance",
                                                     String.class,
                                                     String.class));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Cannot get getType method", e);
        }
        ub.resolveTemplate("ruleform-type",
                           ruleform.getClass().getSimpleName());
        ub.resolveTemplate("instance", ruleform.getId().toString());
        if (ruleform instanceof ExistentialRuleform) {
            ub.fragment(((ExistentialRuleform<?, ?>) ruleform).getName());
        }
        return ub.build().toASCIIString();
    }

    private final Ruleform ruleform;
    private final UriInfo  uriInfo;

    public RuleformNode(Ruleform ruleform, UriInfo uriInfo) {
        this.ruleform = ruleform;
        this.uriInfo = uriInfo;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RuleformNode)) {
            return false;
        }
        RuleformNode other = (RuleformNode) obj;
        if (ruleform == null) {
            if (other.ruleform != null) {
                return false;
            }
        } else if (!ruleform.equals(other.ruleform)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                 + ((ruleform == null) ? 0 : ruleform.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see com.fasterxml.jackson.databind.JsonSerializable#serialize(com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
     */
    @Override
    public void serialize(JsonGenerator gen,
                          SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        writeValue(gen);
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

    /**
     * @param gen
     * @throws IOException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    private void writeValue(JsonGenerator gen) throws IOException {
        gen.writeStringField(Constants.CONTEXT,
                             RuleformContext.getContextIri(ruleform.getClass(),
                                                           uriInfo));
        gen.writeStringField(Constants.ID, getIri(ruleform, uriInfo));
        gen.writeStringField(Constants.TYPE,
                             RuleformContext.getTypeIri(ruleform.getClass(),
                                                        uriInfo));
        for (Field field : RuleformContext.getInheritedFields(ruleform.getClass())) {
            field.setAccessible(true);
            if (field.getAnnotation(JoinColumn.class) == null) {
                field.setAccessible(true);
                Object value;
                try {
                    value = field.get(ruleform);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
                if (value instanceof String) {
                    gen.writeStringField(field.getName(), (String) value);
                } else if (value instanceof Integer) {
                    gen.writeNumberField(field.getName(), (Integer) value);
                } else if (value instanceof Boolean) {
                    gen.writeBooleanField(field.getName(), (Boolean) value);
                } else if (value instanceof BigDecimal) {
                    gen.writeNumberField(field.getName(), (BigDecimal) value);
                } else if (value instanceof byte[]) {
                    gen.writeBinaryField(field.getName(), (byte[]) value);
                } else {
                    gen.writeStringField(field.getName(),
                                         String.valueOf(value));
                }
            } else {
                Ruleform fk;
                try {
                    fk = (Ruleform) field.get(ruleform);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
                gen.writeStringField(field.getName(), getIri(fk, uriInfo));
            }
        }
        gen.writeStringField(Constants.ID, getIri(ruleform, uriInfo));
    }
}
