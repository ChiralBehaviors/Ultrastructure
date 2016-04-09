/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.phantasm.resources;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.ObjectMapper;

public class QueryRequest {
    private String              query;
    private Map<String, Object> variables = Collections.emptyMap();
    static final String         VARIABLES = "variables";
    static final String         QUERY     = "query";

    public QueryRequest() {
    }

    public QueryRequest(String query) {
        this(query, Collections.emptyMap());
    }

    public QueryRequest(String query, Map<String, Object> variables) {
        this.query = query;
        this.variables = variables;
    }

    public String getQuery() {
        return query;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    @SuppressWarnings("serial")
    public Map<String, Object> toMap() {
        return new HashMap<String, Object>() {
            {
                put(QueryRequest.QUERY, query);
                put(QueryRequest.VARIABLES, variables);
            }
        };
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map<String, Object> getVariables(Map request) {
        Map<String, Object> variables = Collections.emptyMap();
        Object provided = request.get(VARIABLES);
        if (provided != null) {
            if (provided instanceof Map) {
                variables = (Map<String, Object>) provided;
            } else if (provided instanceof String) {
                try {
                    String variableString = ((String) provided).trim();
                    if (!variableString.isEmpty()) {
                        variables = new ObjectMapper().readValue(variableString,
                                                                 Map.class);
                    }
                } catch (Exception e) {
                    throw new WebApplicationException(String.format("Cannot deserialize variables: %s",
                                                                    e.getMessage()),
                                                      Status.BAD_REQUEST);
                }
            } else {
                throw new WebApplicationException("Invalid variables parameter",
                                                  Status.BAD_REQUEST);
            }
        }
        return variables;
    }
}