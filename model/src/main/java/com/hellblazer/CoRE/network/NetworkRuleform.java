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

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.resource.Resource;

/**
 * An existential ruleform that can form directed graphs.
 * 
 * @author hhildebrand
 * 
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Access(AccessType.FIELD)
abstract public class NetworkRuleform<E extends Networked<E, ?>> extends
        Ruleform {
    private static final long serialVersionUID = 1L;

    private Integer           distance         = 1;

    @ManyToOne
    @JoinColumn(name = "relationship")
    private Relationship      relationship;

    public NetworkRuleform() {
        super();
    }

    /**
     * @param id
     */
    public NetworkRuleform(Long id) {
        super(id);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public NetworkRuleform(Relationship relationship, Resource updatedBy) {
        super(updatedBy);
        this.relationship = relationship;
    }

    /**
     * @param updatedBy
     */
    public NetworkRuleform(Resource updatedBy) {
        super(updatedBy);
    }

    abstract public E getChild();

    /**
     * @return the distance
     */
    public Integer getDistance() {
        return distance;
    }

    abstract public E getParent();

    /**
     * @return the relationship
     */
    public Relationship getRelationship() {
        return relationship;
    }

    abstract public void setChild(E child);

    /**
     * @param distance
     *            the distance to set
     */
    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    abstract public void setParent(E parent);

    /**
     * @param relationship
     *            the relationship to set
     */
    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

}
