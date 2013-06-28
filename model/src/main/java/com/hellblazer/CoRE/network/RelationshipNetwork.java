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
package com.hellblazer.CoRE.network;

import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.Ruleform;

/**
 * @author Halloran Parry
 *
 */
@javax.persistence.Entity
@Table(name="relationship_network", schema="ruleform")
@SequenceGenerator(schema = "ruleform", name = "relationship_network_id_seq", sequenceName = "relationship_network_id_seq", allocationSize = 1)
public class RelationshipNetwork extends Ruleform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private long id;
	
	private Relationship parent;
	
	private Relationship child;
	
	private boolean isTransitive;
	
	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.Ruleform#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
		
	}

	/**
	 * @return the parent
	 */
	public Relationship getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(Relationship parent) {
		this.parent = parent;
	}

	/**
	 * @return the child
	 */
	public Relationship getChild() {
		return child;
	}

	/**
	 * @param child the child to set
	 */
	public void setChild(Relationship child) {
		this.child = child;
	}

	/**
	 * @return the isTransitive
	 */
	public boolean isTransitive() {
		return isTransitive;
	}

	/**
	 * @param isTransitive the isTransitive to set
	 */
	public void setTransitive(boolean isTransitive) {
		this.isTransitive = isTransitive;
	}

}
