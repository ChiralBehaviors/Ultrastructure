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

import java.util.HashMap;
import java.util.Map;

import com.chiralbehaviors.CoRE.ocular.GraphQlApi;
import com.chiralbehaviors.CoRE.ocular.GraphQlApi.QueryException;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.fxdiagram.core.model.CachedDomainObjectDescriptor;
import de.fxdiagram.core.model.DomainObjectProvider;

/**
 * @author hhildebrand
 *
 */
public class QueryDescriptor
        extends CachedDomainObjectDescriptor<ObjectNode> {

    public static final String        TYPE = "@type";
    private static final String       ID   = "id";
    private static final String       NAME = "name";

    private final GraphQlApi          api;
    private final String              objectKey;
    private final String              query;
    private final String              type;
    private final Map<String, Object> variables;

    public QueryDescriptor(ObjectNode domainObject,
                                   DomainObjectProvider provider,
                                   String objectKey, String query,
                                   Map<String, Object> variables,
                                   GraphQlApi api, String type) {
        super(domainObject, domainObject.get(ID)
                                        .asText(),
              provider);
        this.objectKey = objectKey;
        this.query = query;
        this.variables = variables;
        this.api = api;
        this.type = type;
    }

    @Override
    public String getName() {
        return getDomainObject().get(NAME)
                                .asText();
    }

    @Override
    public ObjectNode resolveDomainObject() {
        Map<String, Object> parameters = new HashMap<>(variables);
        parameters.put(ID, getId());
        ObjectNode result;
        try {
            result = api.query(query, parameters);
        } catch (QueryException e) {
            throw new IllegalStateException(String.format("Cannot complete query: ",
                                                          e.getMessage()));
        }
        ObjectNode object = (ObjectNode) result.get(objectKey);
        if (object.isNull()) {
            return object;
        }
        object.put(TYPE, type);
        return object;
    }

}
