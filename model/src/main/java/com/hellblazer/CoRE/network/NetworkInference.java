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

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.agency.Agency;

/**
 * A chain of relationships.
 * 
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "network_inference", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "network_inference_id_seq", sequenceName = "network_inference_id_seq")
public class NetworkInference extends Ruleform {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "network_inference_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    //bi-directional many-to-one association to Relationship
    @ManyToOne
    @JoinColumn(name = "inference")
    private Relationship      inference;

    //bi-directional many-to-one association to Relationship
    @ManyToOne
    @JoinColumn(name = "premise1")
    private Relationship      premise1;

    //bi-directional many-to-one association to Relationship
    @ManyToOne
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

    /**
     * @param id
     */
    public NetworkInference(Long id) {
        super(id);
    }

    /**
     * @param id
     * @param updatedBy
     */
    public NetworkInference(Long id, Agency updatedBy) {
        super(id, updatedBy);
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

    @Override
    public Long getId() {
        return id;
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

    @Override
    public void setId(Long id) {
        this.id = id;
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

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (inference != null) {
            inference = (Relationship) inference.manageEntity(em, knownObjects);
        }
        if (premise1 != null) {
            premise1 = (Relationship) premise1.manageEntity(em, knownObjects);
        }
        if (premise2 != null) {
            premise2 = (Relationship) premise2.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}