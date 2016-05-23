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

package com.chiralbehaviors.CoRE.ocular.diagram;

import java.io.IOException;
import java.util.Collections;

import com.chiralbehaviors.CoRE.ocular.GraphQlApi;
import com.chiralbehaviors.CoRE.ocular.GraphQlApi.QueryException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hellblazer.utils.Utils;

import de.fxdiagram.core.model.DomainObjectProvider;
import de.fxdiagram.core.model.ModelElementImpl;

/**
 * Basically a domain type -> diagram
 * 
 * @author hhildebrand
 *
 */
public class WorkspaceDomainObjectProvider implements DomainObjectProvider {

    private static final String FACET     = "facet";
    private static final String FACET_QUERY;
    private static final String FACETS_QUERY;
    private static final String TYPE      = "@type";
    private static final String WORKSPACE = "@workspace";

    static {
        try {
            FACET_QUERY = Utils.getDocument(WorkspaceDomainObjectProvider.class.getResourceAsStream("facet.query"));
            FACETS_QUERY = Utils.getDocument(WorkspaceDomainObjectProvider.class.getResourceAsStream("facets.query"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unused")
    private final GraphQlApi api;
    private final GraphQlApi meta;

    public WorkspaceDomainObjectProvider(GraphQlApi api, GraphQlApi meta) {
        this.api = api;
        this.meta = meta;
    }

    @Override
    public QueryDescriptor createDescriptor(Object domainObject) {
        ObjectNode object = (ObjectNode) domainObject;
        switch (object.get(TYPE)
                      .asText()) {
            case FACET: {
                return new QueryDescriptor(object, this, FACET, FACET_QUERY,
                                           Collections.emptyMap(), meta, FACET);
            }
            default:
                return null;
        }
    }

    public ArrayNode getFacets(String product) {
        ArrayNode result;
        try {
            result = meta.query(FACETS_QUERY, Collections.emptyMap())
                         .withArray("facets");
        } catch (QueryException e) {
            throw new IllegalStateException(e);
        }
        result.forEach(a -> {
            ObjectNode facet = (ObjectNode) a;
            facet.put(TYPE, FACET);
            facet.put(WORKSPACE, product);

        });
        return result;
    }

    @Override
    public void populate(ModelElementImpl element) {
        // TODO Auto-generated method stub
    }
}
