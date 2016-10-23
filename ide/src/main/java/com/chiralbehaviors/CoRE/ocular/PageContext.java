package com.chiralbehaviors.CoRE.ocular;

import java.util.Collections;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import com.chiralbehaviors.graphql.layout.schema.Relation;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PageContext {
    public static class QueryException extends Exception {
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

    public static class QueryRequest {
        public String              query;
        public Map<String, Object> variables = Collections.emptyMap();

        public QueryRequest(String query, Map<String, Object> variables) {
            this.query = query;
            this.variables = variables;
        }
    }

    private final Page          page;

    private final Relation      root;

    private Map<String, Object> variables;

    public PageContext(Page page) {
        this.page = page;
        this.root = Relation.buildSchema(page.getQuery());
    }

    public ObjectNode evaluate(Map<String, Object> variables,
                               WebTarget endpoint) throws QueryException {
        this.variables = variables;
        return evaluate(endpoint);
    }

    public ObjectNode evaluate(WebTarget endpoint) throws QueryException {
        Builder invocationBuilder = endpoint.request(MediaType.APPLICATION_JSON_TYPE);

        ObjectNode result = invocationBuilder.post(Entity.entity(new QueryRequest(page.getQuery(),
                                                                                  variables),
                                                                 MediaType.APPLICATION_JSON_TYPE),
                                                   ObjectNode.class);
        ArrayNode errors = result.withArray("errors");
        if (errors.size() > 0) {
            throw new QueryException(errors);
        }
        return (ObjectNode) result.get("data")
                                  .get(root.getField());
    }

    public Page getPage() {
        return page;
    }

    public Relation getRoot() {
        return root;
    }
}
