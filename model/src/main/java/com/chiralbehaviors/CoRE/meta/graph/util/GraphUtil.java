/** 
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.meta.graph.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.chiralbehaviors.CoRE.meta.graph.Edge;
import com.chiralbehaviors.CoRE.meta.graph.Graph;
import com.chiralbehaviors.CoRE.meta.graph.Node;
import com.chiralbehaviors.CoRE.meta.graph.impl.EdgeImpl;
import com.chiralbehaviors.CoRE.meta.graph.impl.GraphImpl;
import com.chiralbehaviors.CoRE.meta.graph.impl.NodeImpl;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;

/**
 * Put all the fucking graph boilerplate in one fucking place so I don't have to
 * fucking type this shit anymore.
 * @author hparry
 *
 */
public class GraphUtil {


	/**
	 * @param networks
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Graph<?, NetworkRuleform<?>> graphFromNetworks(
			Collection<NetworkRuleform<?>> networks) {
		
		//use sets to ensure no duplicates
		Set<Node<?>> nodes = new HashSet<Node<?>>();
		Set<Edge<NetworkRuleform<?>>> edges = new HashSet<Edge<NetworkRuleform<?>>>();
		Iterator<NetworkRuleform<?>> i = networks.iterator();
		
		while (i.hasNext()) {
			NetworkRuleform<?> net = i.next();
			NodeImpl<?> parent = new NodeImpl(net.getParent());
			nodes.add(parent);
			NodeImpl<?> child = new NodeImpl(net.getChild());
			nodes.add(child);
			edges.add(new EdgeImpl<NetworkRuleform<?>>(parent, net, child));
		}
		
		List<Node<?>> nodeList = new LinkedList<Node<?>>();
		nodeList.addAll(nodes);
		List<Edge<NetworkRuleform<?>>> edgeList = new LinkedList<Edge<NetworkRuleform<?>>>();
		edgeList.addAll(edges);
		return new GraphImpl(nodeList, edgeList);
	}

}
