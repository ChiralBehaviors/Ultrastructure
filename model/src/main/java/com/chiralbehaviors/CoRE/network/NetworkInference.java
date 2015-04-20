/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE.network;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.Triggers;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A chain of relationships.
 *
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "network_inference", schema = "ruleform")
public class NetworkInference extends Ruleform {
    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to Relationship
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "inference")
    private Relationship      inference;

    // bi-directional many-to-one association to Relationship
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "premise1")
    private Relationship      premise1;

    // bi-directional many-to-one association to Relationship
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "premise2")
    private Relationship      premise2;

    public NetworkInference() {
    }

    /**
     * @param updatedBy
     */
    public NetworkInference(Agency updatedBy) {
        super(updatedBy);
    }

    public NetworkInference(Relationship premise1, Relationship premise2,
                            Relationship inference) {
        super();
        this.premise1 = premise1;
        this.premise2 = premise2;
        this.inference = inference;
    }

    public NetworkInference(Relationship premise1, Relationship premise2,
                            Relationship inference, Agency updatedBy) {
        super(updatedBy);
        this.premise1 = premise1;
        this.premise2 = premise2;
        this.inference = inference;
    }

    /**
     * @param notes
     */
    public NetworkInference(String notes) {
        super(notes);
    }

    /**
     * @param notes
     * @param updatedBy
     */
    public NetworkInference(String notes, Agency updatedBy) {
        super(notes, updatedBy);
    }

    /**
     * @param id
     */
    public NetworkInference(UUID id) {
        super(id);
    }

    /**
     * @param id
     * @param updatedBy
     */
    public NetworkInference(UUID id, Agency updatedBy) {
        super(id, updatedBy);
    }

    @Override
    public void delete(Triggers triggers) {
        triggers.delete(this);
    }

    public Relationship getInference() {
        return inference;
    }

    public Relationship getPremise1() {
        return premise1;
    }

    public Relationship getPremise2() {
        return premise2;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, NetworkInference> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.networkInference;
    }

    public void setInference(Relationship inference) {
        this.inference = inference;
    }

    public void setPremise1(Relationship premise1) {
        this.premise1 = premise1;
    }

    public void setPremise2(Relationship premise2) {
        this.premise2 = premise2;
    }
}