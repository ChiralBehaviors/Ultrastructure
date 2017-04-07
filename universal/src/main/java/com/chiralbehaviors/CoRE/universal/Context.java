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

import java.util.Collections;
import java.util.Map;

import javax.ws.rs.client.WebTarget;

import com.chiralbehaviors.CoRE.universal.Page.Route;
import com.chiralbehaviors.layout.graphql.GraphQlUtil;
import com.chiralbehaviors.layout.graphql.GraphQlUtil.QueryException;
import com.chiralbehaviors.layout.graphql.GraphQlUtil.QueryRequest;
import com.chiralbehaviors.layout.schema.Relation;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * @author halhildebrand
 *
 */
public class Context {
    private final Page          page;
    private final Relation      root;
    private Map<String, Object> variables;

    public Context(Page page) {
        this(page, Collections.emptyMap());
    }

    public Context(Page page, Map<String, Object> variables) {
        this.page = page;
        this.variables = variables;
        this.root = GraphQlUtil.buildSchema(page.getQuery());
    }

    public ObjectNode evaluate(WebTarget endpoint) throws QueryException {
        return (ObjectNode) GraphQlUtil.evaluate(endpoint,
                                                 new QueryRequest(page.getQuery(),
                                                                  variables))
                                       .get(root.getField());
    }

    public Page getPage() {
        return page;
    }

    public Relation getRoot() {
        return root;
    }

    public Route getRoute(Relation relation) {
        return page.getRoute(relation);
    }
}
