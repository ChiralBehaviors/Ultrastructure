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
import com.hellblazer.CoRE.network.Relationship;

/**
 * @author hparry
 *
 */
public abstract class AbstractNetworkGraph<T extends ExistentialRuleform> {
	
	public abstract T getOrigin();
	
	public abstract Relationship getRelationship();
	
	//TODO HPARRY does this need to store relationship type as well?
	//can it just be a list of nodes?
	public abstract T[] getNeighborNodes();

}
