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

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.resource.Resource;

/**
 * 
 * The abstract super class of all network authorizations.
 * 
 * @author hhildebrand
 * 
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract public class NetworkAuthorization<RuleForm extends Networked<RuleForm, ?>>
        extends Ruleform {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "authorized_relationship")
    private Relationship      authorizedRelationship;

    @ManyToOne
    @JoinColumn(name = "classification")
    private Relationship      classification;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "grouping_resource")
    private Resource          groupingResource;

    @Column(name = "sequence_number")
    private Integer           sequenceNumber;

    public NetworkAuthorization() {
        super();
    }

    /**
     * @param id
     */
    public NetworkAuthorization(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public NetworkAuthorization(Resource updatedBy) {
        super(updatedBy);
    }

    abstract public RuleForm getAuthorizedParent();

    public Relationship getAuthorizedRelationship() {
        return authorizedRelationship;
    }

    public Relationship getClassification() {
        return classification;
    }

    abstract public RuleForm getClassifier();

    public Resource getGroupingResource() {
        return groupingResource;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    abstract public void setAuthorizedParent(RuleForm parent);

    public void setAuthorizedRelationship(Relationship authorizedRelationship) {
        this.authorizedRelationship = authorizedRelationship;
    }

    public void setClassification(Relationship classification) {
        this.classification = classification;
    }

    abstract public void setClassifier(RuleForm classifier);

    public void setGroupingResource(Resource groupingResource) {
        this.groupingResource = groupingResource;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}
