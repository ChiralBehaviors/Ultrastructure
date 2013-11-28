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

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.attribute.Attributable;
import com.hellblazer.CoRE.attribute.Unit;
import com.hellblazer.CoRE.network.Networked;
import com.hellblazer.CoRE.network.Relationship;

/**
 * An interval in time.
 * 
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "interval", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "interval_id_seq", sequenceName = "interval_id_seq")
public class Interval extends ExistentialRuleform implements
        Networked<Interval, IntervalNetwork>, Attributable<IntervalAttribute> {

    public static final String     IMMEDIATE_CHILDREN_NETWORK_RULES = "interval.immediateChildrenNetworkRules";

    private static final long      serialVersionUID                 = 1L;

    //bi-directional many-to-one association to IntervalAttribute
    @OneToMany(mappedBy = "interval", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<IntervalAttribute> attributes;

    private BigInteger             duration;

    @Id
    @GeneratedValue(generator = "interval_id_seq", strategy = GenerationType.SEQUENCE)
    private Long                   id;

    //bi-directional many-to-one association to IntervalNetwork
    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<IntervalNetwork>   networkByChild;

    //bi-directional many-to-one association to IntervalNetwork
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<IntervalNetwork>   networkByParent;

    private BigInteger             start;

    private Unit                   unit;

    public Interval() {
        super();
    }

    public Interval(Agency updatedBy) {
        super(updatedBy);
    }

    public Interval(Long id) {
        super(id);
    }

    public Interval(Long id, Agency updatedBy) {
        super(id, updatedBy);
    }

    public Interval(String name) {
        super(name);
    }

    public Interval(String name, Agency updatedBy) {
        super(name, updatedBy);
    }

    public Interval(String name, String description) {
        super(name, description);
    }

    public Interval(String name, String description, Agency updatedBy) {
        super(name, description, updatedBy);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.Networked#addChildRelationship(com.hellblazer.CoRE.network.NetworkRuleform)
     */
    @Override
    public void addChildRelationship(IntervalNetwork relationship) {
        relationship.setChild(this);
        networkByChild.add(relationship);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.Networked#addParentRelationship(com.hellblazer.CoRE.network.NetworkRuleform)
     */
    @Override
    public void addParentRelationship(IntervalNetwork relationship) {
        relationship.setParent(this);
        networkByParent.add(relationship);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.Attributable#getAttributes()
     */
    @Override
    public Set<IntervalAttribute> getAttributes() {
        return attributes;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.Attributable#getAttributeType()
     */
    @Override
    public Class<IntervalAttribute> getAttributeType() {
        return IntervalAttribute.class;
    }

    /**
     * @return the duration
     */
    public BigInteger getDuration() {
        return duration;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#getImmediateChildren()
     */
    @Override
    public List<IntervalNetwork> getImmediateChildren(EntityManager em) {
        return em.createNamedQuery(IMMEDIATE_CHILDREN_NETWORK_RULES,
                                   IntervalNetwork.class).setParameter("interval",
                                                                       this).getResultList();
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.Networked#getNetworkByChild()
     */
    @Override
    public Set<IntervalNetwork> getNetworkByChild() {
        return networkByChild;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.Networked#getNetworkByParent()
     */
    @Override
    public Set<IntervalNetwork> getNetworkByParent() {
        return networkByParent;
    }

    /**
     * @return the start
     */
    public BigInteger getStart() {
        return start;
    }

    /**
     * @return the unit
     */
    public Unit getUnit() {
        return unit;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.Networked#link(com.hellblazer.CoRE.network.Relationship, com.hellblazer.CoRE.network.Networked, com.hellblazer.CoRE.agency.Agency, com.hellblazer.CoRE.agency.Agency, javax.persistence.EntityManager)
     */
    @Override
    public void link(Relationship r, Interval child, Agency updatedBy,
                     Agency inverseSoftware, EntityManager em) {
        assert r != null : "Relationship cannot be null";
        assert child != null;
        assert updatedBy != null;
        assert em != null;

        IntervalNetwork link = new IntervalNetwork(this, r, child, updatedBy);
        em.persist(link);
        IntervalNetwork inverse = new IntervalNetwork(child, r.getInverse(),
                                                      this, inverseSoftware);
        em.persist(inverse);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.Attributable#setAttributes(java.util.Set)
     */
    @Override
    public void setAttributes(Set<IntervalAttribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * @param duration
     *            the duration to set
     */
    public void setDuration(BigInteger duration) {
        this.duration = duration;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.Networked#setNetworkByChild(java.util.Set)
     */
    @Override
    public void setNetworkByChild(Set<IntervalNetwork> networkByChild) {
        this.networkByChild = networkByChild;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.network.Networked#setNetworkByParent(java.util.Set)
     */
    @Override
    public void setNetworkByParent(Set<IntervalNetwork> networkByParent) {
        this.networkByParent = networkByParent;
    }

    /**
     * @param start
     *            the start to set
     */
    public void setStart(BigInteger start) {
        this.start = start;
    }

    /**
     * @param unit
     *            the unit to set
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

}
