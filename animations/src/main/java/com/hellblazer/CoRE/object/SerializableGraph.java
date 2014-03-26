package com.hellblazer.CoRE.object;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.hellblazer.CoRE.meta.graph.query.NetworkGraphQuery;

/**
 * Representation of a Network intended to be serialized by Jackson. Jackson
 * will only fully serialize the first appearance of an object in a response,
 * and thereafter it will confine itself to the object type and ID. This makes
 * the generated JSON structures inconsistent and very hard for JS clients to
 * consume.
 * 
 * To make life easier, this class contains no duplicate instances. It's a
 * unique list of nodes in the graph, a unique list of Relationship types
 * (a.k.a. edge types) and a GraphEdge data structure that is nothing more than
 * array index references for the parent and child nodes as well as the
 * Relationship the edge represents.
 * 
 * @author hparry
 * 
 */
public class SerializableGraph<T extends ExistentialRuleform<T, ?>> {

    public class GraphEdge {
        private long source;
        private long target;
        private long relationship;

        GraphEdge(long source, long relationship, long target) {
            this.source = source;
            this.target = target;
            this.relationship = relationship;
        }

        /**
         * @return the relationship id
         */
        public long getRelationship() {
            return relationship;
        }

        /**
         * @return the source id
         */
        public long getSource() {
            return source;
        }

        /**
         * @return the target id
         */
        public long getTarget() {
            return target;
        }
    }

    private T                  origin;
    private List<Relationship> relationships;
    private List<GraphEdge>    edges;

    private List<T>            nodes;

    public SerializableGraph(List<NetworkRuleform<T>> net) {

        Map<T, Integer> indices = new HashMap<T, Integer>();
        populateGraphFromNetwork(net, indices);
        this.origin = indices.entrySet().iterator().next().getKey();
    }

    public SerializableGraph(NetworkGraphQuery<T> ng) {
        this.relationships = ng.getRelationships();
        this.nodes = ng.getNodes();
        this.origin = ng.getOrigin();
        Map<T, Integer> indices = new HashMap<T, Integer>();
        for (int i = 0; i < nodes.size(); i++) {
            indices.put(nodes.get(i), i);
        }

        populateGraphFromNetwork(ng.getEdges(), indices);

    }

    public List<GraphEdge> getEdges() {
        return this.edges;
    }

    public List<T> getNodes() {
        return this.nodes;
    }

    public T getOrigin() {
        return this.origin;
    }

    public List<Relationship> getRelationships() {
        return this.relationships;
    }

    /**
     * @param ng
     * @param indices
     */
    private void populateGraphFromNetwork(List<NetworkRuleform<T>> net,
                                          Map<T, Integer> indices) {
        for (NetworkRuleform<T> n : net) {
            int source, target;
            long relationship;
            if (indices.get(n.getParent()) == null) {
                source = nodes.size();
                nodes.add(n.getParent());
                indices.put(n.getParent(), source);
            } else {
                source = indices.get(n.getParent());
            }

            if (indices.get(n.getChild()) == null) {
                target = nodes.size();
                nodes.add(n.getChild());
                indices.put(n.getChild(), target);
            } else {
                target = indices.get(n.getChild());
            }

            relationship = n.getRelationship().getId();

            edges.add(new GraphEdge(source, relationship, target));
        }
    }

}
