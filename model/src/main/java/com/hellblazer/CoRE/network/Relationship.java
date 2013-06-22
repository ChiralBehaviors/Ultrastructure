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
package com.hellblazer.CoRE.network;
import static com.hellblazer.CoRE.Ruleform.FIND_BY_NAME_SUFFIX;
import static com.hellblazer.CoRE.Ruleform.NAME_SEARCH_SUFFIX;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.NameSearchResult;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The existential rule form that defines relationships between existential rule
 * form instances, providing the edge connecting two nodes in a directed graph.
 * 
 * @author hhildebrand
 * 
 */
@javax.persistence.Entity
@Table(name = "relationship", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "relationship_id_seq", sequenceName = "relationship_id_seq", allocationSize = 1)
@NamedQueries({ @NamedQuery(name = "relationship" +  FIND_BY_NAME_SUFFIX, query = "select e from Relationship e where e.name = :name") })
// ?1 = :queryString, ?2 = :numberOfMatches
@NamedNativeQueries({ @NamedNativeQuery(name = "relationship"
                                               +  NAME_SEARCH_SUFFIX, query = "SELECT id, name, description FROM ruleform.existential_name_search('relationship', ?1, ?2)", resultClass = NameSearchResult.class) })
public class Relationship extends ExistentialRuleform {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "relationship_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    //bi-directional many-to-one association to Relationship
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "inverse")
    private Relationship      inverse;

    private String            operator;

    private Boolean           preferred        = Boolean.FALSE;
    
    /*
     * If true, allows direct edges to be created between nodes by inference. So if a R1 b R2 c,
     * then we could derive a R1 c if this is true. If not, we will not derive that relationship
     */
    @Column(name="is_transient")
    private Boolean			  isTransient = Boolean.TRUE;

    public Relationship() {
    }

    /**
     * @param id
     */
    public Relationship(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public Relationship(Resource updatedBy) {
        super(updatedBy);
    }

    /**
     * @param name
     */
    public Relationship(String name) {
        super(name);
    }

    /**
     * @param name
     * @param updatedBy
     */
    public Relationship(String name, Resource updatedBy) {
        super(name, updatedBy);
    }

    /**
     * @param name
     * @param description
     */
    public Relationship(String name, String description) {
        super(name, description);
    }

    /**
     * @param name
     * @param description
     * @param updatedBy
     */
    public Relationship(String name, String description, Resource updatedBy) {
        super(name, description, updatedBy);
    }

    /**
     * @param name
     * @param description
     * @param updatedBy
     * @param preferred
     */
    public Relationship(String name, String description, Resource updatedBy,
                        boolean preferred) {
        super(name, description, updatedBy);
        setPreferred(preferred);
    }

    /**
     * @param name
     * @param description
     * @param updatedBy
     * @param inverse
     */
    public Relationship(String name, String description, Resource updatedBy,
                        Relationship inverse) {
        super(name, description, updatedBy);
        setInverse(inverse);
    }

    @Override
    public Long getId() {
        return id;
    }

    public Relationship getInverse() {
        return inverse;
    }

    public String getOperator() {
        return operator;
    }

    public Boolean getPreferred() {
        return preferred;
    }
    
    public Boolean getIsTransient() {
    	return isTransient;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setInverse(Relationship relationship) {
        inverse = relationship;
        relationship.inverse = this;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setPreferred(Boolean preferred) {
        this.preferred = preferred;
    }
    
	public void setIsTransient(Boolean isTransient) {
		this.isTransient = isTransient;
	}
}