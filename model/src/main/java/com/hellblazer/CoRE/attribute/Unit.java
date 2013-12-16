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
package com.hellblazer.CoRE.attribute;

import java.math.BigDecimal;
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
import com.hellblazer.CoRE.network.Relationship;

/**
 * The attribute unit.
 * 
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "unit", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "unit_id_seq", sequenceName = "unit_id_seq")
public class Unit extends ExistentialRuleform<Unit, UnitNetwork> {
    public static final String IMMEDIATE_CHILDREN_NETWORK_RULES = "unit.immediateChildrenNetworkRules";
    private static final long  serialVersionUID                 = 1L;

    private String             abbreviation;

    private String             datatype;

    private Boolean            enumerated                       = false;

    @Id
    @GeneratedValue(generator = "unit_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    private BigDecimal         max;

    private BigDecimal         min;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<UnitNetwork>   networkByChild;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<UnitNetwork>   networkByParent;

    public Unit() {
    }

    public Unit(Agency updatedBy) {
        super(updatedBy);
    }

    public Unit(Long id) {
        super(id);
    }

    public Unit(Long id, Agency updatedBy) {
        super(id, updatedBy);
    }

    public Unit(String name) {
        setName(name);
    }

    public Unit(String name, Agency updatedBy) {
        super(updatedBy);
        setName(name);
    }

    public Unit(String name, String description, Agency updatedBy) {
        super(updatedBy);
        setName(name);
        setDescription(description);
    }

    public Unit(String name, String description, String notes, Agency updatedBy) {
        super(notes, updatedBy);
        setName(name);
        setDescription(description);
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getDatatype() {
        return datatype;
    }

    public Boolean getEnumerated() {
        return enumerated;
    }

    @Override
    public Long getId() {
        return id;
    }

    public BigDecimal getMax() {
        return max;
    }

    public BigDecimal getMin() {
        return min;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public void setEnumerated(Boolean enumerated) {
        this.enumerated = enumerated;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setMax(BigDecimal max) {
        this.max = max;
    }

    public void setMin(BigDecimal min) {
        this.min = min;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.ExistentialRuleform#addChildRelationship(com.hellblazer
     * .CoRE.network.NetworkRuleform)
     */
    @Override
    public void addChildRelationship(UnitNetwork relationship) {
        relationship.setChild(this);
        networkByChild.add(relationship);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.ExistentialRuleform#addParentRelationship(com.hellblazer
     * .CoRE.network.NetworkRuleform)
     */
    @Override
    public void addParentRelationship(UnitNetwork relationship) {
        relationship.setParent(this);
        networkByParent.add(relationship);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.ExistentialRuleform#getImmediateChildren(javax.
     * persistence.EntityManager)
     */
    @Override
    public List<UnitNetwork> getImmediateChildren(EntityManager em) {
        return em.createNamedQuery(IMMEDIATE_CHILDREN_NETWORK_RULES,
                                   UnitNetwork.class).setParameter("interval",
                                                                   this).getResultList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.ExistentialRuleform#getNetworkByChild()
     */
    @Override
    public Set<UnitNetwork> getNetworkByChild() {
        return networkByChild;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.ExistentialRuleform#getNetworkByParent()
     */
    @Override
    public Set<UnitNetwork> getNetworkByParent() {
        return networkByParent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.ExistentialRuleform#link(com.hellblazer.CoRE.network
     * .Relationship, com.hellblazer.CoRE.ExistentialRuleform,
     * com.hellblazer.CoRE.agency.Agency, com.hellblazer.CoRE.agency.Agency,
     * javax.persistence.EntityManager)
     */
    @Override
    public void link(Relationship r, Unit child, Agency updatedBy,
                     Agency inverseSoftware, EntityManager em) {
        assert r != null : "Relationship cannot be null";
        assert child != null;
        assert updatedBy != null;
        assert em != null;

        UnitNetwork link = new UnitNetwork(this, r, child, updatedBy);
        em.persist(link);
        UnitNetwork inverse = new UnitNetwork(child, r.getInverse(), this,
                                              inverseSoftware);
        em.persist(inverse);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.ExistentialRuleform#setNetworkByChild(java.util.Set)
     */
    @Override
    public void setNetworkByChild(Set<UnitNetwork> theNetworkByChild) {
        this.networkByChild = theNetworkByChild;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.ExistentialRuleform#setNetworkByParent(java.util.Set)
     */
    @Override
    public void setNetworkByParent(Set<UnitNetwork> theNetworkByParent) {
        this.networkByParent = theNetworkByParent;
    }
}