/**
 * (C) Copyright 2016 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE.existential.network;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.MetaValue;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.existential.ExistentialRuleform;
import com.chiralbehaviors.CoRE.existential.domain.Agency;
import com.chiralbehaviors.CoRE.existential.domain.Attribute;
import com.chiralbehaviors.CoRE.existential.domain.Interval;
import com.chiralbehaviors.CoRE.existential.domain.Location;
import com.chiralbehaviors.CoRE.existential.domain.Product;
import com.chiralbehaviors.CoRE.existential.domain.Relationship;
import com.chiralbehaviors.CoRE.existential.domain.StatusCode;
import com.chiralbehaviors.CoRE.existential.domain.Unit;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * An existential ruleform that can form directed graphs.
 *
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "existential_network", schema = "ruleform")
public class ExistentialNetwork<P extends ExistentialRuleform<P>, C extends ExistentialRuleform<C>>
        extends Ruleform {
    private static final long serialVersionUID = 1L;

    @Column(insertable = false, updatable = false)
    private char              c_domain;
    @Any(metaColumn = @Column(name = "c_domain"))
    @AnyMetaDef(idType = "pg-uuid", metaType = "char", metaValues = { @MetaValue(targetEntity = Agency.class, value = "A"),
                                                                      @MetaValue(targetEntity = Attribute.class, value = "T"),
                                                                      @MetaValue(targetEntity = Interval.class, value = "I"),
                                                                      @MetaValue(targetEntity = Location.class, value = "L"),
                                                                      @MetaValue(targetEntity = Product.class, value = "P"),
                                                                      @MetaValue(targetEntity = Relationship.class, value = "R"),
                                                                      @MetaValue(targetEntity = StatusCode.class, value = "S"),
                                                                      @MetaValue(targetEntity = Unit.class, value = "U") })
    @Cascade({ org.hibernate.annotations.CascadeType.ALL })
    @JoinColumn(name = "child")
    private C                 child;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "inference")
    private NetworkInference  inference;

    @Column(insertable = false, updatable = false)
    private char              p_domain;
    @Any(metaColumn = @Column(name = "p_domain"))
    @AnyMetaDef(idType = "pg-uuid", metaType = "char", metaValues = { @MetaValue(targetEntity = Agency.class, value = "A"),
                                                                      @MetaValue(targetEntity = Attribute.class, value = "T"),
                                                                      @MetaValue(targetEntity = Interval.class, value = "I"),
                                                                      @MetaValue(targetEntity = Location.class, value = "L"),
                                                                      @MetaValue(targetEntity = Product.class, value = "P"),
                                                                      @MetaValue(targetEntity = Relationship.class, value = "R"),
                                                                      @MetaValue(targetEntity = StatusCode.class, value = "S"),
                                                                      @MetaValue(targetEntity = Unit.class, value = "U") })
    @Cascade({ org.hibernate.annotations.CascadeType.ALL })
    @JoinColumn(name = "parent")
    private P                 parent;

    @Column(name = "premise1", insertable = false, updatable = false)
    private UUID              premise1;

    @Column(name = "premise2", insertable = false, updatable = false)
    private UUID              premise2;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "relationship")
    private Relationship      relationship;

    public ExistentialNetwork() {
        super();
    }

    public ExistentialNetwork(P parent, Relationship r, C child,
                              Agency updatedBy) {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ExistentialNetwork<?, ?> other = (ExistentialNetwork<?, ?>) obj;
        return getParent().equals(other.getParent())
               && getRelationship().equals(other.getRelationship())
               && getChild().equals(other.getChild());
    }

    @JsonGetter
    public C getChild() {
        return child;
    }

    /**
     * @return the inference
     */
    @JsonGetter
    public NetworkInference getInference() {
        return inference;
    }

    @JsonGetter
    public P getParent() {
        return parent;
    }

    @JsonGetter
    public UUID getPremise1() {
        return premise1;
    }

    @JsonGetter
    public UUID getPremise2() {
        return premise2;
    }

    /**
     * @return the relationship
     */
    public Relationship getRelationship() {
        return relationship;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                 + (getParent() == null ? 0 : getParent().hashCode());
        result = prime * result
                 + (relationship == null ? 0 : relationship.hashCode());
        result = prime * result
                 + (getChild() == null ? 0 : getChild().hashCode());
        return result;
    }

    @JsonIgnore
    public boolean isInferred() {
        return getInference() != null;
    }

    public void setChild(C child) {
        this.child = child;
    }

    public void setInference(NetworkInference inference) {
        this.inference = inference;
    }

    public void setParent(P parent) {
        this.parent = parent;
    }

    public void setPremise1(UUID premise1) {
        this.premise1 = premise1;
    }

    public void setPremise2(UUID premise2) {
        this.premise2 = premise2;
    }

    /**
     * @param relationship
     *            the relationship to set
     */
    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    @Override
    public String toString() {
        return String.format("%s[%s] %s >> %s >> %s: %s", this.getClass()
                                                              .getSimpleName(),
                             getId(), getParent().getName(),
                             getRelationship().getName(), getChild().getName(),
                             isInferred());
    }
}
