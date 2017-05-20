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
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;

/**
 * @author halhildebrand
 *
 */
public interface WorkspsacScalarTypes {

    static GraphQLScalarType GraphQLBinary    = new GraphQLScalarType("BINARY",
                                                                      "Built-in Base 64 encoded BINARY",
                                                                      binaryCoercing());

    static GraphQLScalarType GraphQLJson      = new GraphQLScalarType("JSON",
                                                                      "Built-in JSON",
                                                                      jsonCoercing());

    static GraphQLScalarType GraphQLTimestamp = new GraphQLScalarType("TIMESTAMP",
                                                                      "Built-in TIMESTAMP",
                                                                      timestampCoercing());
    static GraphQLScalarType GraphQLUuid      = new GraphQLScalarType("ID",
                                                                      "Built-in ID",
                                                                      uuidCoercing());

    SimpleDateFormat         rfc3339          = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

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
                return parseLiteral(input);
            }

            @Override
            public String serialize(Object input) {
                return Base64.getEncoder()
                             .withoutPadding()
                             .encodeToString((byte[]) input);
            }
        };
    }

    static Coercing jsonCoercing() {
        return new Coercing() {
            @Override
            public JsonNode parseLiteral(Object input) {
                if (input instanceof JsonNode) {
                    return (JsonNode) input;
                }
                return null;
            }

            @Override
            public JsonNode parseValue(Object input) {
                if (input instanceof JsonNode) {
                    return (JsonNode) input;
                }
                return null;
            }

            @Override
            public Object serialize(Object input) {
                if (input instanceof ObjectNode) {
                    return input;
                }
                return null;
            }
        };
    }

    static Coercing timestampCoercing() {
        return new Coercing() {

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

    static String toRFC3339(Date d) {
        return rfc3339.format(d)
                      .replaceAll("(\\d\\d)(\\d\\d)$", "$1:$2");
    }

    static Coercing uuidCoercing() {
        return new Coercing() {
            @Override
            public Object parseLiteral(Object input) {
                if (input instanceof String) {
                    return UuidUtil.decode((String) input);
                }
                if (input instanceof StringValue) {
                    return UuidUtil.decode(((StringValue) input).getValue());
                }
                return null;
            }

            @Override
            public Object parseValue(Object input) {
                return parseLiteral(input);
            }

            @Override
            public String serialize(Object input) {
                if (input instanceof UUID) {
                    return UuidUtil.encode((UUID) input);
                }
                return null;
            }
        };
    }

}
