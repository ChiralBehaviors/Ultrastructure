/**
 * Copyright (C) 2013 Halloran Parry. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hellblazer.CoRE.meta.graph;

import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.network.NetworkRuleform;

/**
 * Abstract class for representing the graphs created by networked ruleforms.
 * The nodes are existential ruleforms and the edges are created by relationships.
 * This means that the edges are a) directed and b) typed.
 * @author hparry
 *
 */
public abstract class AbstractNetworkGraph<T extends ExistentialRuleform> {
	
	/**
	 * 
	 * @return the node at the center of the graph
	 */
	public abstract T getOrigin();
	
	/**
	 * Gets the "edges" of the graph. Note that these are relationships and authorizations, 
	 * not foreign keys.
	 * @return the compound network ruleforms that represent graph edges
	 */
	public abstract NetworkRuleform[] getEdges();
	
	/**
	 * Returns the set of nodes in the graph. These are existential ruleforms.
	 * @return
	 */
	public abstract ExistentialRuleform[] getNodes();

}
