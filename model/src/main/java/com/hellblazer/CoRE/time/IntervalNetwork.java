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
package com.hellblazer.CoRE.time;

import java.math.BigDecimal;

import javax.persistence.Column;
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
@Table(name = "interval_network", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "interval_network_id_seq", sequenceName = "interval_network_id_seq")
public class IntervalNetwork extends NetworkRuleform<Interval> {

    private static final long serialVersionUID = 1L;

    //bi-directional many-to-one association to Interval
    @ManyToOne
    @JoinColumn(name = "child")
    private Interval          child;

    @Id
    @GeneratedValue(generator = "interval_network_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    //bi-directional many-to-one association to Agency
    @ManyToOne
    @JoinColumn(name = "parent")
    private Interval          parent;

    @Column(name = "resolved_duration")
    private BigDecimal        resolvedDuration;

    @Column(name = "resolved_start")
    private BigDecimal        resolvedStart;

    public IntervalNetwork() {
        super();
    }

    public IntervalNetwork(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public IntervalNetwork(Interval parent, Relationship relationship,
                           Interval child, Agency updatedBy) {
        super(relationship, updatedBy);
        this.parent = parent;
        this.child = child;
    }

    public IntervalNetwork(Long id) {
        super(id);
    }

    public IntervalNetwork(Relationship relationship, Agency updatedBy) {
        super(relationship, updatedBy);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.NetworkRuleform#getChild()
     */
    @Override
    public Interval getChild() {
        return child;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.NetworkRuleform#getParent()
     */
    @Override
    public Interval getParent() {
        return parent;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.NetworkRuleform#setChild(com.hellblazer.CoRE.network.Networked)
     */
    @Override
    public void setChild(Interval child) {
        this.child = child;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.NetworkRuleform#setParent(com.hellblazer.CoRE.network.Networked)
     */
    @Override
    public void setParent(Interval parent) {
        this.parent = parent;
    }

    protected BigDecimal getResolvedDuration() {
        return resolvedDuration;
    }

    protected BigDecimal getResolvedStart() {
        return resolvedStart;
    }

    protected void setResolvedDuration(BigDecimal resolvedDuration) {
        this.resolvedDuration = resolvedDuration;
    }

    protected void setResolvedStart(BigDecimal resolvedStart) {
        this.resolvedStart = resolvedStart;
    }
}
