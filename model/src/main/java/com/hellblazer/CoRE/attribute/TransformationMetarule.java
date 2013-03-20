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

import static com.hellblazer.CoRE.attribute.TransformationMetarule.GET_BY_EVENT;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.entity.Entity;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The persistent class for the transformation_metarule database table.
 * 
 */
@NamedQueries({ @NamedQuery(name = GET_BY_EVENT, query = "SELECT tm FROM TransformationMetarule tm "
                                                         + "WHERE tm.service = :service "
                                                         + "ORDER BY tm.sequenceNumber") })
@javax.persistence.Entity
@Table(name = "transformation_metarule", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "transformation_metarule_id_seq", sequenceName = "transformation_metarule_id_seq")
public class TransformationMetarule extends Ruleform implements Serializable {
    public static final String GET_BY_EVENT     = "tranformationMetarule.getByEvent";
    private static final long  serialVersionUID = 1L;

    //bi-directional many-to-one association to Relationship
    @ManyToOne
    @JoinColumn(name = "entity_map")
    private Relationship       entityMap;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "entity_network_resource")
    private Resource           entityNetworkResource;

    @Id
    @GeneratedValue(generator = "transformation_metarule_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    //bi-directional many-to-one association to Relationship
    @ManyToOne
    @JoinColumn(name = "resource_map")
    private Relationship       relationshipMap;

    @Column(name = "sequence_number")
    private Integer            sequenceNumber;

    /**
     * The service performed
     */
    @ManyToOne
    @JoinColumn(name = "service")
    private Entity             service;

    @Column(name = "stop_on_match")
    private Boolean            stopOnMatch;

    public TransformationMetarule() {
    }

    public Relationship getEntityMap() {
        return entityMap;
    }

    public Resource getEntityNetworkResource() {
        return entityNetworkResource;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Relationship getRelationshipMap() {
        return relationshipMap;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * @return the service
     */
    public Entity getService() {
        return service;
    }

    public Boolean getStopOnMatch() {
        return stopOnMatch;
    }

    public void setEntityMap(Relationship relationship2) {
        entityMap = relationship2;
    }

    public void setEntityNetworkResource(Resource resource) {
        entityNetworkResource = resource;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setRelationshipMap(Relationship relationship1) {
        relationshipMap = relationship1;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * @param service
     *            the service to set
     */
    public void setService(Entity service) {
        this.service = service;
    }

    public void setStopOnMatch(Boolean stopOnMatch) {
        this.stopOnMatch = stopOnMatch;
    }

}