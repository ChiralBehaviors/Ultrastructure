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

package com.chiralbehaviors.CoRE.occular;

import java.util.Collections;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author hhildebrand
 *
 */
public class GraphQlApi {
    public class QueryException extends Exception {
        private static final long serialVersionUID = 1L;
        private final ArrayNode   errors;

        public QueryException(ArrayNode errors) {
            super(errors.toString());
            this.errors = errors;
        }

        public ArrayNode getErrors() {
            return errors;
        }
    }

    public class QueryRequest {
        private String              query;
        private Map<String, Object> variables = Collections.emptyMap();

        public QueryRequest() {
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
    }

    private final String    authToken;
    private final WebTarget endpoint;

    public GraphQlApi(WebTarget endpoint, String authToken) {
        this.endpoint = endpoint;
        this.authToken = authToken;
    }

    public ObjectNode query(String query,
                            Map<String, Object> parameters) throws QueryException {
        Builder invocationBuilder = endpoint.request(MediaType.APPLICATION_JSON_TYPE);
        if (authToken != null) {
            invocationBuilder.header(HttpHeaders.AUTHORIZATION, authToken);
        }

        ObjectNode result = invocationBuilder.post(Entity.entity(new QueryRequest(query,
                                                                                  parameters),
                                                                 MediaType.APPLICATION_JSON_TYPE),
                                                   ObjectNode.class);
        ArrayNode errors = result.withArray("errors");
        if (errors.size() > 0) {
            throw new QueryException(errors);
        }
        return (ObjectNode) result.get("data");
    }
}
