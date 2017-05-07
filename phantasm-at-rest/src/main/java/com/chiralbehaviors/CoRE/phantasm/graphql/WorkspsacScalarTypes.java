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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

    static Coercing<JsonNode, Object> jsonCoercing() {
        return new Coercing<JsonNode, Object>() {
            @Override
            public JsonNode parseLiteral(Object input) {
                if (input instanceof StringValue) {
                    return JsonNodeFactory.instance.textNode(((StringValue) input).getValue());
                }
                if (input instanceof BooleanValue) {
                    return JsonNodeFactory.instance.booleanNode(((BooleanValue) input).isValue());
                }
                if (input instanceof IntValue) {
                    return JsonNodeFactory.instance.numberNode(((IntValue) input).getValue());
                }
                if (input instanceof FloatValue) {
                    return JsonNodeFactory.instance.numberNode(((FloatValue) input).getValue());
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
                    ArrayNode array = JsonNodeFactory.instance.arrayNode();
                    ((ArrayValue) input).getValues()
                                        .stream()
                                        .map(v -> parseLiteral(v))
                                        .forEach(v -> array.add(v));
                    return array;
                }
                return null;
            }

            @Override
            public JsonNode parseValue(Object input) {
                return parseLiteral(input);
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

    SimpleDateFormat rfc3339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    static String toRFC3339(Date d) {
        return rfc3339.format(d)
                      .replaceAll("(\\d\\d)(\\d\\d)$", "$1:$2");
    }

    static Coercing<Timestamp, String> timestampCoercing() {
        return new Coercing<Timestamp, String>() {

            @Override
            public Timestamp parseLiteral(Object input) {
                if (input instanceof StringValue) {
                    try {
                        return new Timestamp(((Date) rfc3339.parseObject(((StringValue) input).getValue())).getTime());
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(String.format("invalid date: %s",
                                                                         input));
                    }
                }
                return null;
            }

            @Override
            public Timestamp parseValue(Object input) {
                return parseLiteral(input);
            }

            @Override
            public String serialize(Object input) {
                Timestamp t = (Timestamp) input;
                return rfc3339.format(t.getTime());
            }
        };
    }

    static Coercing<byte[], String> binaryCoercing() {
        return new Coercing<byte[], String>() {

            @Override
            public String serialize(Object input) {
                return Base64.getEncoder()
                             .withoutPadding()
                             .encodeToString((byte[]) input);
            }

            @Override
            public byte[] parseValue(Object input) {
                return parseLiteral(input);
            }

            @Override
            public byte[] parseLiteral(Object input) {
                if (input instanceof StringValue) {
                    return Base64.getDecoder()
                                 .decode(((StringValue) input).getValue());
                }
                return null;
            }
        };
    }

}
