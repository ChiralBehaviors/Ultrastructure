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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.ws.rs.client.ClientBuilder;

import com.chiralbehaviors.CoRE.ocular.GraphQlApi;
import com.chiralbehaviors.CoRE.ocular.GraphQlApi.QueryException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hellblazer.utils.Utils;

import de.fxdiagram.core.model.DomainObjectDescriptor;
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
    public static final String  TYPE      = "@type";
    public static final String  WORKSPACE = "@workspace";

    static {
        try {
            FACET_QUERY = Utils.getDocument(WorkspaceDomainObjectProvider.class.getResourceAsStream("facet.query"));
            FACETS_QUERY = Utils.getDocument(WorkspaceDomainObjectProvider.class.getResourceAsStream("facets.query"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unused")
    private final ConcurrentMap<String, GraphQlApi> api  = new ConcurrentHashMap<>();
    private final String                            instance;
    private final ConcurrentMap<String, GraphQlApi> meta = new ConcurrentHashMap<>();

    public WorkspaceDomainObjectProvider(String instanceURI) {
        this.instance = instanceURI;
    }

    @Override
    public DomainObjectDescriptor createDescriptor(Object domainObject) {
        ObjectNode object = (ObjectNode) domainObject;
        String workspace = object.get(WORKSPACE)
                                 .asText();

        switch (object.get(TYPE)
                      .asText()) {
            case FACET: {
                GraphQlApi metaApi = meta.get(workspace);
                return new QueryDescriptor(object, this, FACET, FACET_QUERY,
                                           Collections.emptyMap(), metaApi,
                                           FACET);
            }
            default:
                return null;
        }
    }

    public ObjectNode getFacet(String facet, String workspace) {
        GraphQlApi metaApi = getMeta(workspace);
        ObjectNode result;
        try {
            result = (ObjectNode) metaApi.query(FACET_QUERY,
                                                Collections.emptyMap())
                                         .get("facet");
        } catch (QueryException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }

    public ArrayNode getFacets(String workspace) {
        GraphQlApi metaApi = getMeta(workspace);
        ArrayNode result;
        try {
            result = (ArrayNode) metaApi.query(FACETS_QUERY,
                                               Collections.emptyMap())
                                        .get("facets");
        } catch (QueryException e) {
            throw new IllegalStateException(e);
        }
        result.forEach(n -> {
            ((ObjectNode) n).put(TYPE, FACET);
            ((ObjectNode) n).put(WORKSPACE, workspace);
        });
        return result;
    }

    @Override
    public void populate(ModelElementImpl element) {
        // TODO Auto-generated method stub
    }

    private GraphQlApi getMeta(String workspace) {
        return meta.computeIfAbsent(workspace, id -> {
            return new GraphQlApi(ClientBuilder.newClient()
                                               .target(instance)
                                               .path("api")
                                               .path("workspace")
                                               .path(String.format("urn:uuid:%s",
                                                                   workspace))
                                               .path("meta"),
                                  null);
        });
    }
}
