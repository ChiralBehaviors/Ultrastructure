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

package com.chiralbehaviors.CoRE.universal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author halhildebrand
 *
 */
public class Action {
    private static Map<String, String> extract(String json) {
        Map<String, String> extract = new HashMap<>();
        ObjectNode node;
        try {
            node = (ObjectNode) new ObjectMapper().readTree(new ByteArrayInputStream(json.getBytes()));
        } catch (IOException e) {
            throw new IllegalStateException("", e);
        }
        for (Iterator<Entry<String, JsonNode>> fields = node.fields(); fields.hasNext();) {
            Entry<String, JsonNode> field = fields.next();
            extract.put(field.getKey(), field.getValue()
                                             .asText());
        }
        return extract;
    }

    private final Map<String, String> extract;
    private final String              frameBy;
    private final String              query;

    public Action(ObjectNode action) {
        this(action.get("frameBy")
                   .asText(),
             action.get("query")
                   .asText(),
             extract(action.get("extract")
                           .asText()));
    }

    public Action(String query, String frameBy, Map<String, String> extract) {
        this.query = query;
        this.frameBy = frameBy;
        this.extract = extract;
    }

    public Map<String, String> getExtract() {
        return extract;
    }

    public String getFrameBy() {
        return frameBy;
    }

    public String getQuery() {
        return query;
    }
}
