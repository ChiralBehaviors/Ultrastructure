//TODO HPARRY add copywrite notice here
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
