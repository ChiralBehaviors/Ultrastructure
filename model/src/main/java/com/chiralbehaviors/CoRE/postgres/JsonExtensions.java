/**
 * Copyright (c) 2018 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.postgres;

import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDataType;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author halhildebrand
 *
 */
public interface JsonExtensions {
    final PostgresJSONJacksonJsonNodeConverter CONVERTER = new PostgresJSONJacksonJsonNodeConverter();
    final DefaultDataType<JsonNode>            JSON_B    = new DefaultDataType<>(SQLDialect.POSTGRES_9_5,
                                                                                 JsonNode.class,
                                                                                 "jsonb");

    public static Field<JsonNode> concatenate(Field<JsonNode> field,
                                              JsonNode value) {
        return DSL.field("{0}||{1}", JSON_B, field, CONVERTER.to(value));
    }

    public static Field<Boolean> containedIn(Field<JsonNode> field,
                                             JsonNode parent) {
        return DSL.field("{0}<@{1}", Boolean.class, field,
                         CONVERTER.to(parent));
    }

    public static Field<Boolean> contains(Field<JsonNode> field,
                                          JsonNode value) {
        return DSL.field("{0}@>{1}", Boolean.class, field, CONVERTER.to(value));
    }

    public static Field<JsonNode> delete(Field<JsonNode> field, int index) {
        return DSL.field("{0}-{1}", JSON_B, field, index);
    }

    public static Field<JsonNode> delete(Field<JsonNode> field, String value) {
        return DSL.field("{0}-{1}", JSON_B, field, value);
    }

    public static Field<JsonNode> delete(Field<JsonNode> field,
                                         String value[]) {
        return DSL.field("{0}-{1}", JSON_B, field, value);
    }

    public static Field<JsonNode> deletePath(Field<JsonNode> field,
                                             String value[]) {
        return DSL.field("{0}#-{1}", JSON_B, field, value);
    }

    public static Field<JsonNode> jsonAt(Field<?> field, int index) {
        return DSL.field("{0}->{1}", JSON_B, field, DSL.inline(index));
    }

    public static Field<JsonNode> jsonAt(Field<?> field, String name) {
        return DSL.field("{0}->{1}", JSON_B, field, DSL.inline(name));
    }

    public static Field<JsonNode> jsonPath(Field<?> field, String name) {
        return DSL.field("{0}#>{1}", JSON_B, field, DSL.inline(name));
    }

    public static Field<Boolean> topLevelKeyExist(Field<JsonNode> field,
                                                  String key) {
        return DSL.field("{0}?{1}", Boolean.class, field, key);
    }

    public static Field<Boolean> topLevelKeysAllExist(Field<JsonNode> field,
                                                      String[] key) {
        return DSL.field("{0}?&{1}", Boolean.class, field, key);
    }

    public static Field<Boolean> topLevelKeysExist(Field<JsonNode> field,
                                                   String[] key) {
        return DSL.field("{0}?|{1}", Boolean.class, field, key);
    }
}
