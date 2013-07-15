/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
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

package com.hellblazer.CoRE.capability;

import static com.hellblazer.CoRE.capability.Action.FIND_BY_NAME;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.resource.Resource;

/**
 * 
 * Represents an action verb. A "can create" B, for example.
 * 
 * @author hhildebrand
 * 
 */
@NamedQueries({ @NamedQuery(name = FIND_BY_NAME, query = "select a from Action a where a.name = :name") })
@SequenceGenerator(schema = "ruleform", name = "action_id_seq", sequenceName = "action_id_seq")
@Table(name = "action", schema = "ruleform")
@Entity
public class Action extends ExistentialRuleform {

    private static final long  serialVersionUID = 1L;
    public static final String FIND_BY_NAME     = "action"
                                                  + FIND_BY_NAME_SUFFIX;

    @Id
    @GeneratedValue(generator = "action_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    public Action() {
        super();
    }

    /**
     * @param id
     */
    public Action(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public Action(Resource updatedBy) {
        super(updatedBy);
    }

    /**
     * @param notes
     */
    public Action(String notes) {
        super(notes);
    }

    /**
     * @param notes
     * @param updatedBy
     */
    public Action(String notes, Resource updatedBy) {
        super(notes, updatedBy);
    }

    /**
     * @param name
     * @param description
     */
    public Action(String name, String description) {
        super(name, description);
    }

    /**
     * @param name
     * @param description
     * @param updatedBy
     */
    public Action(String name, String description, Resource updatedBy) {
        super(name, description, updatedBy);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#getId()
     */
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

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
	 */
	@Override
	public void traverseForeignKeys(EntityManager em,
			Map<Ruleform, Ruleform> knownObjects) {
		return;
		
	}

}
