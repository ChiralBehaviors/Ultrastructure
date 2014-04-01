/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package com.chiralbehaviors.CoRE.meta.graph.query;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * Class for representing the graphs created by networked ruleforms. The nodes
 * are existential ruleforms and the edges are created by relationships. This
 * means that the edges are a) directed and b) typed.
 * 
 * @author hparry
 * 
 */
public final class NetworkGraphQuery<RuleForm extends ExistentialRuleform<RuleForm, ?>> {

    private List<NetworkRuleform<RuleForm>> edges;
    private EntityManager                   em;
    private List<RuleForm>                  nodes;
    private RuleForm                        origin;
    private List<Relationship>              relationships;

    public NetworkGraphQuery(List<RuleForm> nodes,
                             List<Relationship> relationships, EntityManager em) {
        this.origin = nodes.get(0);
        this.relationships = relationships;
        this.nodes = nodes;
        this.em = em;
        findNeighbors();

    }

    public NetworkGraphQuery(RuleForm node, Relationship r, EntityManager em) {
        List<RuleForm> nodes = new LinkedList<RuleForm>();
        nodes.add(node);
        List<Relationship> relationships = new LinkedList<Relationship>();
        relationships.add(r);
        this.nodes = nodes;
        this.relationships = relationships;
        this.em = em;
        this.origin = node;
        findNeighbors();
    }

    /**
     * Gets the "edges" of the graph. The source and target properties of the
     * edge object are indexes that refer to values in the node array. They are
     * NOT ids.
     * 
     * @return the compound network ruleforms that represent graph edges
     */
    public List<NetworkRuleform<RuleForm>> getEdges() {
        return edges;
    }

    /**
     * Returns the set of nodes in the graph, starting with the origin. These
     * are existential ruleforms.
     * 
     * @return
     */
    public List<RuleForm> getNodes() {
        return nodes;
    }

    public RuleForm getOrigin() {
        return this.origin;
    }

    /**
     * @return the list of relationships that appear in the graph. This
     *         information is used for typifying edges.
     */
    public List<Relationship> getRelationships() {
        return relationships;
    }

    @SuppressWarnings("unchecked")
    private void findNeighbors() {
        Query q = em.createNamedQuery(origin.getClass().getSimpleName().toLowerCase()
                                      + ExistentialRuleform.GET_CHILD_RULES_BY_RELATIONSHIP_SUFFIX);
        q.setParameter(origin.getClass().getSimpleName().toLowerCase(), origin);
        q.setParameter("relationships", relationships);
        edges = q.getResultList();

        nodes = new LinkedList<RuleForm>();
        nodes.add(origin);
        for (NetworkRuleform<RuleForm> n : edges) {
            if (!nodes.contains(n.getChild())) {
                nodes.add(n.getChild());
            }
        }

    }

}
