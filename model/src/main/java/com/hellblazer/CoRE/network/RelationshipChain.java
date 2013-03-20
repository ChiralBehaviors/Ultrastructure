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
 * The persistent class for the relationship_chain database table.
 * 
 */
@javax.persistence.Entity
@Table(name = "relationship_chain", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "relationship_chain_id_seq", sequenceName = "relationship_chain_id_seq")
public class RelationshipChain extends Ruleform {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "relationship_chain_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    //bi-directional many-to-one association to Relationship
    @ManyToOne
    @JoinColumn(name = "input1")
    private Relationship      input1;

    //bi-directional many-to-one association to Relationship
    @ManyToOne
    @JoinColumn(name = "input2")
    private Relationship      input2;

    //bi-directional many-to-one association to Relationship
    @ManyToOne
    @JoinColumn(name = "result")
    private Relationship      result;

    public RelationshipChain() {
    }

    /**
     * @param id
     */
    public RelationshipChain(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public RelationshipChain(Resource updatedBy) {
        super(updatedBy);
    }

    @Override
    public Long getId() {
        return id;
    }

    public Relationship getInput1() {
        return input1;
    }

    public Relationship getInput2() {
        return input2;
    }

    public Relationship getRelationship1() {
        return result;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setInput1(Relationship input) {
        input1 = input;
    }

    public void setInput2(Relationship input) {
        input2 = input;
    }

    public void setR(Relationship result) {
        this.result = result;
    }
}