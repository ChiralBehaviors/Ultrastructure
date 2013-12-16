/**
 * Copyright (C) 2013 Hal Hildebrand. All rights reserved.
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
package com.hellblazer.CoRE.attribute;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.network.NetworkRuleform;
import com.hellblazer.CoRE.network.Relationship;

/**
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "unit_network", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "unit_network_id_seq", sequenceName = "unit_network_id_seq")
public class UnitNetwork extends NetworkRuleform<Unit> {

    /**
     * @param relationship
     * @param updatedBy
     */
    public UnitNetwork(Unit parent, Relationship relationship,
                             Unit child, Agency updatedBy) {
        super(relationship, updatedBy);
        this.parent = parent;
        this.child = child;
    }

    /**
     * 
     */
    public UnitNetwork() {
        super();
    }

    /**
     * @param updatedBy
     */
    public UnitNetwork(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param id
     */
    public UnitNetwork(Long id) {
        super(id);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public UnitNetwork(Relationship relationship, Agency updatedBy) {
        super(relationship, updatedBy);
    }

    private static final long serialVersionUID = 1L; //bi-directional many-to-one association to Agency
    @ManyToOne
    @JoinColumn(name = "child")
    private Unit        child;

    @Id
    @GeneratedValue(generator = "unit_network_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    //bi-directional many-to-one association to Agency
    @ManyToOne
    @JoinColumn(name = "parent")
    private Unit        parent;

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.NetworkRuleform#getChild()
     */
    @Override
    public Unit getChild() {
        return child;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.NetworkRuleform#getParent()
     */
    @Override
    public Unit getParent() {
        return parent;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.NetworkRuleform#setChild(com.hellblazer.CoRE.ExistentialRuleform)
     */
    @Override
    public void setChild(Unit child) {
        this.child = child;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.NetworkRuleform#setParent(com.hellblazer.CoRE.ExistentialRuleform)
     */
    @Override
    public void setParent(Unit parent) {
        this.parent = parent;
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
}
