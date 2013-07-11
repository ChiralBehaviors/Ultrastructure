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

import static com.hellblazer.CoRE.network.NetworkRuleform.INFERENCE_STEP_FROM_LAST_PASS;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.resource.Resource;

/**
 * An existential ruleform that can form directed graphs.
 * 
 * @author hhildebrand
 * 
 */

@NamedNativeQueries({ @NamedNativeQuery(name = INFERENCE_STEP_FROM_LAST_PASS, query = "INSERT INTO working_memory(parent, relationship, child, premise1, premise2) "
                                                                                      + "     SELECT "
                                                                                      + "         premise1.parent, "
                                                                                      + "         deduction.inference, "
                                                                                      + "         premise2.child, "
                                                                                      + "         premise1.id, "
                                                                                      + "         premise2.id "
                                                                                      + "     FROM  (SELECT n.id, n.parent, n.relationship, n.child"
                                                                                      + "              FROM last_pass_rules AS n) as premise1 "
                                                                                      + "     JOIN  (SELECT n.id, n.parent, n.relationship, n.child "
                                                                                      + "            FROM ruleform.resource_network AS n "
                                                                                      + "            WHERE n.inferred = FALSE) as premise2  "
                                                                                      + "         ON premise2.parent = premise1.child "
                                                                                      + "         AND premise2.child <> premise1.parent "
                                                                                      + "     JOIN ruleform.network_inference AS deduction "
                                                                                      + "         ON premise1.relationship = deduction.premise1 "
                                                                                      + "         AND premise2.relationship = deduction.premise2 ") })
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Access(AccessType.FIELD)
abstract public class NetworkRuleform<E extends Networked<E, ?>> extends
        Ruleform {
    private static final long  serialVersionUID              = 1L;
    public static final String INFERENCE_STEP_FROM_LAST_PASS = "networkRuleform.inferenceStepFromLastPass";

    private boolean            inferred                      = false;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "relationship")
    private Relationship       relationship;

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

    abstract public E getParent();

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
        return inferred;
    }

    abstract public void setChild(E child);

    public void setInferred(boolean inferred) {
        this.inferred = inferred;
    }

    abstract public void setParent(E parent);

    /**
     * @param relationship
     *            the relationship to set
     */
    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public String toString() {
        return String.format("LocationNetwork[%s] %s >> %s >> %s: %s", getId(),
                             getParent().getName(),
                             getRelationship().getName(), getChild().getName(),
                             isInferred());
    }
}
