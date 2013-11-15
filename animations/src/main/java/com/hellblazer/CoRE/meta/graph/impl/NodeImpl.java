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
package com.hellblazer.CoRE.meta.graph.impl;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.meta.graph.Node;

/**
 * Wrapper class to turn a Ruleform into a node. Yes, it would be nice if the Ruleform
 * class just implemented the INode interface directly, but the dependency management 
 * doesn't work out.
 * @author hparry
 *
 */
public class NodeImpl implements Node<Ruleform> {
	
	private Ruleform node;
	
	public NodeImpl(Ruleform node) {
		this.node = node;
	}
	
	public Ruleform getNode() {
		return this.node;
	}

}
