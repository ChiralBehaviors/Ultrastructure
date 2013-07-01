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

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.resource.Resource;

/**
 * A chain of relationships.
 * 
 * @author hhildebrand
 * 
 */
@javax.persistence.Entity
@Table(name = "network_inferrence", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "network_inferrence_id_seq", sequenceName = "network_inferrence_id_seq")
public class NetworkInferrence extends Ruleform {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "network_inferrence_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    //bi-directional many-to-one association to Relationship
    @ManyToOne
    @JoinColumn(name = "inferrence")
    private Relationship      inferrence;

    //bi-directional many-to-one association to Relationship
    @ManyToOne
    @JoinColumn(name = "premise1")
    private Relationship      premise1;

    //bi-directional many-to-one association to Relationship
    @ManyToOne
    @JoinColumn(name = "premise2")
    private Relationship      premise2;

    public NetworkInferrence() {
    }

    /**
     * @param id
     */
    public NetworkInferrence(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public NetworkInferrence(Resource updatedBy) {
        super(updatedBy);
    }

    @Override
    public Long getId() {
        return id;
    }

    public Relationship getInferrence() {
        return inferrence;
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

    public void setInferrence(Relationship inferrence) {
        this.inferrence = inferrence;
    }

    public void setPremise1(Relationship premise1) {
        this.premise1 = premise1;
    }

    public void setPremise2(Relationship premise2) {
        this.premise2 = premise2;
    }
}