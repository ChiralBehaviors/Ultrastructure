/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.phantasm.java.generator;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.AttributeRuleformContext;

/**
 * @author hhildebrand
 *
 */
public class MappedAttribute {
    public static String normalize(String text) {
        StringBuffer buff = new StringBuffer();
        buff.append(Character.toUpperCase(text.charAt(0)));
        boolean capitalizeNext = false;
        for (int i = 1; i < text.length(); i++) {
            if (Character.isWhitespace(text.charAt(i))) {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    buff.append(Character.toUpperCase(text.charAt(i)));
                    capitalizeNext = false;
                } else {
                    buff.append(text.charAt(i));
                }
            }
        }
        return buff.toString();
    }

    public static String stripQuotes(String original) {
        return original.substring(1, original.length() - 1);
    }

    private final List<String> imports = new ArrayList<>();

    private final String name;

    private final String type;

    public MappedAttribute(AttributeRuleformContext attribute) {
        this.name = normalize(stripQuotes(attribute.existentialRuleform().name.getText()));
        this.type = getType(attribute);
    }

    public List<String> getImports() {
        return imports;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    private String baseTypeOf(String valueType) {
        switch (valueType) {
            case "int":
                return "Integer";
            case "text":
                return "String";
            case "bool":
                return "Boolean";
            case "numeric":
                imports.add("java.math.BigDecimal");
                return "BigDecimal";
            case "binary":
                return "byte[]";
            case "timestamp":
                imports.add("java.sql.Timestamp");
                return "Timestamp";
            case "json":
                imports.add("com.fasterxml.jackson.databind.JsonNode");
                return "JsonNode";
            default:
                throw new IllegalArgumentException();
        }
    }

    private String getType(AttributeRuleformContext attribute) {
        StringBuffer rt = new StringBuffer();
        if (isTrue(attribute.keyed)) {
            imports.add("java.util.Map");
            rt.append("Map<String, ");
            rt.append(baseTypeOf(attribute.valueType.getText()));
            rt.append('>');

        } else if (isTrue(attribute.indexed)) {
            rt.append(baseTypeOf(attribute.valueType.getText()));
            rt.append("[]");
        } else {
            rt.append(baseTypeOf(attribute.valueType.getText()));
        }
        return rt.toString();
    }

    protected boolean isTrue(Token indexed) {
        return indexed != null && "true".equals(indexed.getText());
    }
}
