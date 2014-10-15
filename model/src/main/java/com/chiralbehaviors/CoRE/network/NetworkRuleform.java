/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chiralbehaviors.CoRE.network;

import java.util.Map;
import java.util.UUID;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;

/**
 * An existential ruleform that can form directed graphs.
 *
 * @author hhildebrand
 *
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Access(AccessType.FIELD)
abstract public class NetworkRuleform<E extends ExistentialRuleform<?, ?>>
        extends Ruleform {
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "inference", insertable = false)
    private NetworkInference  inference;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "relationship")
    private Relationship      relationship;

    public NetworkRuleform() {
        super();
    }

    /**
     * @param updatedBy
     */
    public NetworkRuleform(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public NetworkRuleform(Relationship relationship, Agency updatedBy) {
        super(updatedBy);
        this.relationship = relationship;
    }

    /**
     * @param id
     */
    public NetworkRuleform(UUID id) {
        super(id);
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
        @SuppressWarnings("unchecked")
        NetworkRuleform<E> other = (NetworkRuleform<E>) obj;
        return getParent().equals(other.getParent())
               && getRelationship().equals(other.getRelationship())
               && getChild().equals(other.getChild());
    }

    abstract public E getChild();

    /**
     * @return the inference
     */
    public NetworkInference getInference() {
        return inference;
    }

    abstract public E getParent();

    abstract public NetworkRuleform<E> getPremise1();

    abstract public NetworkRuleform<E> getPremise2();

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

    public boolean isInferred() {
        if (inference == null) {
            return false;
        }
        return !ZERO.equals(inference.getId());
    }

    abstract public void setChild(E child);

    /**
     * @param inference
     *            the inference to set
     */
    public void setInference(NetworkInference inference) {
        this.inference = inference;
    }

    abstract public void setParent(E parent);

    abstract public void setPremise1(NetworkRuleform<E> premise1);

    abstract public void setPremise2(NetworkRuleform<E> premise2);

    /**
     * @param relationship
     *            the relationship to set
     */
    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    @Override
    public String toString() {
        return String.format("%s[%s] %s >> %s >> %s: %s",
                             this.getClass().getSimpleName(), getId(),
                             getParent().getName(),
                             getRelationship().getName(), getChild().getName(),
                             isInferred());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.Ruleform#traverseForeignKeys(javax.persistence.
     * EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (relationship != null) {
            relationship = (Relationship) relationship.manageEntity(em,
                                                                    knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}
