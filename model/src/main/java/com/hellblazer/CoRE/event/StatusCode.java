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
package com.hellblazer.CoRE.event;

import static com.hellblazer.CoRE.Ruleform.NAME_SEARCH_SUFFIX;
import static com.hellblazer.CoRE.event.StatusCode.FIND_BY_NAME;
import static com.hellblazer.CoRE.event.StatusCode.IS_TERMINAL_STATE;

import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.NameSearchResult;
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.network.NetworkRuleform;
import com.hellblazer.CoRE.network.Relationship;

/**
 * The persistent class for the status_code database table.
 * 
 */

@Entity
@Table(name = "status_code", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "status_code_id_seq", sequenceName = "status_code_id_seq")
@NamedQueries({ @NamedQuery(name = FIND_BY_NAME, query = "select sc from StatusCode sc where sc.name = :name"), })
// ?1 = :queryString, ?2 = :numberOfMatches
@NamedNativeQueries({
                     @NamedNativeQuery(name = "statusCode" + NAME_SEARCH_SUFFIX, query = "SELECT id, name, description FROM ruleform.existential_name_search('status_code', ?1, ?2)", resultClass = NameSearchResult.class),
                     @NamedNativeQuery(name = IS_TERMINAL_STATE, query = "SELECT EXISTS( "
                                                                         + "SELECT sc.id "
                                                                         + "FROM ruleform.status_code_sequencing AS seq "
                                                                         + "    JOIN ruleform.status_code AS sc ON seq.child_code = sc.id "
                                                                         + " WHERE "
                                                                         + "  NOT EXISTS ( "
                                                                         + "    SELECT parent_code FROM ruleform.status_code_sequencing "
                                                                         + "    WHERE service = seq.service "
                                                                         + "      AND parent_code = seq.child_code "
                                                                         + "  ) "
                                                                         + "  AND service = ? "
                                                                         + "  AND sc.id = ? "
                                                                         + " )") })
public class StatusCode extends ExistentialRuleform {
    private static final long  serialVersionUID  = 1L;
    public static final String FIND_BY_NAME      = "statusCode"
                                                   + FIND_BY_NAME_SUFFIX;
    public static final String IS_TERMINAL_STATE = "statusCode.isTerminalState";

    @Column(name = "fail_parent")
    private Boolean            failParent        = true;

    @Id
    @GeneratedValue(generator = "status_code_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    @Column(name = "propagate_children")
    private Boolean            propagateChildren = true;

    public StatusCode() {
    }

    /**
     * @param updatedBy
     */
    public StatusCode(Agency updatedBy) {
        super(updatedBy);
    }

    public StatusCode(long l, String name) {
        super(name);
        id = l;
    }

    /**
     * @param id
     */
    public StatusCode(Long id) {
        super(id);
    }

    /**
     * @param name
     */
    public StatusCode(String name) {
        super(name);
    }

    /**
     * @param name
     * @param updatedBy
     */
    public StatusCode(String name, Agency updatedBy) {
        super(name, updatedBy);
    }

    /**
     * @param name
     * @param description
     */
    public StatusCode(String name, String description) {
        super(name, description);
    }

    /**
     * @param name
     * @param description
     * @param updatedBy
     */
    public StatusCode(String name, String description, Agency updatedBy) {
        super(name, description, updatedBy);
    }

    public Boolean getFailParent() {
        return failParent;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Boolean getPropagateChildren() {
        return propagateChildren;
    }

    public void setFailParent(Boolean failParent) {
        this.failParent = failParent;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setPropagateChildren(Boolean propagateChildren) {
        this.propagateChildren = propagateChildren;
    }

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.ExistentialRuleform#addChildRelationship(com.hellblazer.CoRE.network.NetworkRuleform)
	 */
	@Override
	public void addChildRelationship(NetworkRuleform relationship) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.ExistentialRuleform#addParentRelationship(com.hellblazer.CoRE.network.NetworkRuleform)
	 */
	@Override
	public void addParentRelationship(NetworkRuleform relationship) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.ExistentialRuleform#getImmediateChildren(javax.persistence.EntityManager)
	 */
	@Override
	public List getImmediateChildren(EntityManager em) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.ExistentialRuleform#getNetworkByChild()
	 */
	@Override
	public Set getNetworkByChild() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.ExistentialRuleform#getNetworkByParent()
	 */
	@Override
	public Set getNetworkByParent() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.ExistentialRuleform#link(com.hellblazer.CoRE.network.Relationship, com.hellblazer.CoRE.ExistentialRuleform, com.hellblazer.CoRE.agency.Agency, com.hellblazer.CoRE.agency.Agency, javax.persistence.EntityManager)
	 */
	@Override
	public void link(Relationship r, ExistentialRuleform child,
			Agency updatedBy, Agency inverseSoftware, EntityManager em) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.ExistentialRuleform#setNetworkByChild(java.util.Set)
	 */
	@Override
	public void setNetworkByChild(Set theNetworkByChild) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.ExistentialRuleform#setNetworkByParent(java.util.Set)
	 */
	@Override
	public void setNetworkByParent(Set theNetworkByParent) {
		// TODO Auto-generated method stub
		
	}
}