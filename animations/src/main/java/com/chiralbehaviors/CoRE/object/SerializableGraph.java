package com.chiralbehaviors.CoRE.object;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.UuidGenerator;
import com.chiralbehaviors.CoRE.meta.graph.query.NetworkGraphQuery;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;

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
        private String source;
        private String target;
        private String relationship;

        GraphEdge(String source, String relationship, String target) {
            this.source = source;
            this.target = target;
            this.relationship = relationship;
        }

        /**
         * @return the relationship id
         */
        public String getRelationship() {
            return relationship;
        }

        /**
         * @return the source id
         */
        public String getSource() {
            return source;
        }

        /**
         * @return the target id
         */
        public String getTarget() {
            return target;
        }
    }

    private T                  origin;
    private List<Relationship> relationships;
    private List<GraphEdge>    edges;

    private List<T>            nodes;

    public SerializableGraph(List<NetworkRuleform<T>> net) {

        Map<T, String> indices = new HashMap<>();
        populateGraphFromNetwork(net, indices);
        this.origin = indices.entrySet().iterator().next().getKey();
    }

    public SerializableGraph(NetworkGraphQuery<T> ng) {
        this.relationships = ng.getRelationships();
        this.nodes = ng.getNodes();
        this.origin = ng.getOrigin();
        Map<T, String> indices = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            indices.put(nodes.get(i), UuidGenerator.nextId());
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
                                          Map<T, String> indices) {
        for (NetworkRuleform<T> n : net) {
            String source, target;
            String relationship;
            if (indices.get(n.getParent()) == null) {
                source = UuidGenerator.nextId();
                nodes.add(n.getParent());
                indices.put(n.getParent(), source);
            } else {
                source = indices.get(n.getParent());
            }

            if (indices.get(n.getChild()) == null) {
                target = UuidGenerator.nextId();
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
