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
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
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
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;

/**
 * The persistent class for the transformation_metarule database table.
 * 
 */
@NamedQueries({ @NamedQuery(name = GET_BY_EVENT, query = "SELECT tm FROM TransformationMetarule tm "
                                                         + "WHERE tm.service = :service "
                                                         + "ORDER BY tm.sequenceNumber") })
@Entity
@Table(name = "transformation_metarule", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "transformation_metarule_id_seq", sequenceName = "transformation_metarule_id_seq")
public class TransformationMetarule extends Ruleform implements Serializable {
    public static final String GET_BY_EVENT     = "tranformationMetarule.getByEvent";
    private static final long  serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "transformation_metarule_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    //bi-directional many-to-one association to Relationship
    @ManyToOne
    @JoinColumn(name = "product_map")
    private Relationship       productMap;

    //bi-directional many-to-one association to Agency
    @ManyToOne
    @JoinColumn(name = "product_network_agency")
    private Agency             productNetworkAgency;

    //bi-directional many-to-one association to Relationship
    @ManyToOne
    @JoinColumn(name = "agency_map")
    private Relationship       relationshipMap;

    @Column(name = "sequence_number")
    private Integer            sequenceNumber;

    /**
     * The service performed
     */
    @ManyToOne
    @JoinColumn(name = "service")
    private Product            service;

    @Column(name = "stop_on_match")
    private Boolean            stopOnMatch;

    public TransformationMetarule() {
    }

    public Agency getEntityNetworkAgency() {
        return productNetworkAgency;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Relationship getProductMap() {
        return productMap;
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
    public Product getService() {
        return service;
    }

    public Boolean getStopOnMatch() {
        return stopOnMatch;
    }

    public void setEntityMap(Relationship relationship2) {
        productMap = relationship2;
    }

    public void setEntityNetworkAgency(Agency agency) {
        productNetworkAgency = agency;
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
    public void setService(Product service) {
        this.service = service;
    }

    public void setStopOnMatch(Boolean stopOnMatch) {
        this.stopOnMatch = stopOnMatch;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (productMap != null) {
            productMap = (Relationship) productMap.manageEntity(em,
                                                                knownObjects);
        }
        if (productNetworkAgency != null) {
            productNetworkAgency = (Agency) productNetworkAgency.manageEntity(em,
                                                                              knownObjects);
        }
        if (relationshipMap != null) {
            relationshipMap = (Relationship) relationshipMap.manageEntity(em,
                                                                          knownObjects);
        }
        if (service != null) {
            service = (Product) service.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }

}