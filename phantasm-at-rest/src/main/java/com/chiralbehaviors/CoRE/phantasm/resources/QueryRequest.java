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

public class QueryRequest {
    private String              query;
    private Map<String, Object> variables = Collections.emptyMap();

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
                put(GraphQlResource.QUERY, query);
                put(GraphQlResource.VARIABLES, variables);
            }
        };
    }
}