package com.chiralbehaviors.CoRE.phantasm.graphql;

import java.util.HashMap;
import java.util.Map;

import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceContext.Traversal;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.NetworkAuthorization;

import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;

/**
 * 
 * @author halhildebrand
 *
 */
public class EdgeTypeResolver implements TypeResolver {

    private final Map<NetworkAuthorization, GraphQLObjectType> edges = new HashMap<>();

    @Override
    public GraphQLObjectType getType(Object object) {
        if (object == null) {
            return null;
        }
        Traversal edge = (Traversal) object;
        return edges.get(edge.auth);
    }
}
