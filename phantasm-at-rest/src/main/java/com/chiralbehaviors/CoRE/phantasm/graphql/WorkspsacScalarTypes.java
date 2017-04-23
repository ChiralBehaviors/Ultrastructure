/**
 * Copyright (c) 2017 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 *  This file is part of Ultrastructure.
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

package com.chiralbehaviors.CoRE.phantasm.graphql;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Base64;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import graphql.language.ArrayValue;
import graphql.language.BooleanValue;
import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.ObjectField;
import graphql.language.ObjectValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;

/**
 * @author halhildebrand
 *
 */
public interface WorkspsacScalarTypes {

    static GraphQLScalarType GraphQLJson      = new GraphQLScalarType("JSON",
                                                                      "Built-in JSON",
                                                                      jsonCoercing());

    static GraphQLScalarType GraphQLTimestamp = new GraphQLScalarType("TIMESTAMP",
                                                                      "Built-in TIMESTAMP",
                                                                      timestampCoercing());

    static GraphQLScalarType GraphQLBinary    = new GraphQLScalarType("BINARY",
                                                                      "Built-in Base 64 encoded BINARY",
                                                                      binaryCoercing());

    static Coercing jsonCoercing() {
        return new Coercing() {
            @Override
            public Object parseLiteral(Object input) {
                if (input instanceof StringValue) {
                    return ((StringValue) input).getValue();
                }
                if (input instanceof BooleanValue) {
                    return ((BooleanValue) input).isValue();
                }
                if (input instanceof IntValue) {
                    return ((IntValue) input).getValue();
                }
                if (input instanceof FloatValue) {
                    return ((FloatValue) input).getValue();
                }
                if (input instanceof ObjectValue) {
                    ObjectValue objValue = (ObjectValue) input;
                    ObjectNode value = JsonNodeFactory.instance.objectNode();
                    objValue.getObjectFields()
                            .forEach(f -> {
                                set(value, f, input);
                            });
                    return value;
                }
                if (input instanceof ArrayValue) {
                    return ((ArrayValue) input).getValues()
                                               .stream()
                                               .map(v -> parseLiteral(v));
                }
                return null;
            }

            @Override
            public Object parseValue(Object input) {
                return serialize(input);
            }

            @Override
            public Object serialize(Object input) {
                if (input instanceof String) {
                    return Integer.parseInt((String) input);
                } else if (input instanceof Integer) {
                    return input;
                } else {
                    return null;
                }
            }

            private void set(ObjectNode object, ObjectField field,
                             Object value) {
                Object literal = parseLiteral(field.getValue());
                if (literal instanceof String) {
                    object.put(field.getName(), (String) literal);
                } else if (literal instanceof Float) {
                    object.put(field.getName(), (Float) literal);
                } else if (literal instanceof Integer) {
                    object.put(field.getName(), (Integer) literal);
                } else if (literal instanceof Boolean) {
                    object.put(field.getName(), (Boolean) literal);
                } else if (literal instanceof ObjectNode) {
                    object.set(field.getName(), (ObjectNode) literal);
                } else {
                    throw new IllegalArgumentException(String.format("%s is an invalid JSON type",
                                                                     value));
                }
            }
        };
    }

    static Coercing timestampCoercing() {
        return new Coercing() {

            @Override
            public Timestamp parseLiteral(Object input) {
                if (input instanceof StringValue) {
                    return new Timestamp(Long.parseUnsignedLong(((StringValue) input).getValue()));
                } else if (input instanceof IntValue) {
                    BigInteger value = ((IntValue) input).getValue();
                    return new Timestamp(value.longValue());
                }
                return null;
            }

            @Override
            public Timestamp parseValue(Object input) {
                return serialize(input);
            }

            @Override
            public Timestamp serialize(Object input) {
                if (input instanceof String) {
                    return new Timestamp(Long.parseLong((String) input));
                } else if (input instanceof Number) {
                    return new Timestamp(((Number) input).longValue());
                }
                return null;
            }
        };
    }

    static Coercing binaryCoercing() {
        return new Coercing() {

            @Override
            public byte[] parseLiteral(Object input) {
                if (input instanceof StringValue) {
                    return Base64.getDecoder()
                                 .decode(((StringValue) input).getValue());
                }
                return null;
            }

            @Override
            public byte[] parseValue(Object input) {
                return serialize(input);
            }

            @Override
            public byte[] serialize(Object input) {
                if (input instanceof String) {
                    return Base64.getDecoder()
                                 .decode((String) input);
                }
                return null;
            }
        };
    }

}
