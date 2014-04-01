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
package com.chiralbehaviors.CoRE.attribute;

import static com.chiralbehaviors.CoRE.attribute.Transformation.GET;

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

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * The persistent class for the transformation database table.
 * 
 */
@NamedQueries({ @NamedQuery(name = GET, query = "SELECT t FROM Transformation t "
                                                + "WHERE t.service = :service "
                                                + "AND t.product = :product "
                                                + "AND t.agency = :agency "
                                                + "ORDER BY t.sequenceNumber") })
@Entity
@Table(name = "transformation", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "transformation_id_seq", sequenceName = "transformation_id_seq")
public class Transformation extends Ruleform implements Serializable {
    public final static String GET              = "transformation.get";
    private static final long  serialVersionUID = 1L;

    // bi-directional many-to-one association to Agency
    @ManyToOne
    @JoinColumn(name = "agency")
    private Agency             agency;

    // bi-directional many-to-one association to Agency
    @ManyToOne
    @JoinColumn(name = "agency_key")
    private Agency             agencyKey;

    // bi-directional many-to-one association to Agency
    @ManyToOne
    @JoinColumn(name = "assign_to")
    private Agency             assignTo;

    // bi-directional many-to-one association to Attribute
    @ManyToOne
    @JoinColumn(name = "attribute")
    private Attribute          attribute;

    @Id
    @GeneratedValue(generator = "transformation_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    // bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "product")
    private Product            product;

    // bi-directional many-to-one association to Agency
    @ManyToOne
    @JoinColumn(name = "product_attribute_agency")
    private Agency             productAttributeAgency;

    // bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "product_key")
    private Product            productKey;

    // bi-directional many-to-one association to Relationship
    @ManyToOne
    @JoinColumn(name = "relationship_key")
    private Relationship       relationshipKey;

    @Column(name = "sequence_number")
    private Integer            sequenceNumber;

    // bi-directional many-to-one association to Event
    @ManyToOne
    @JoinColumn(name = "service")
    private Product            service;

    public Transformation() {
    }

    public Agency getAgency() {
        return agency;
    }

    public Agency getAgencyKey() {
        return agencyKey;
    }

    public Agency getAssignTo() {
        return assignTo;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public Product getEntity() {
        return product;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Agency getProductAttributeAgency() {
        return productAttributeAgency;
    }

    public Product getProductKey() {
        return productKey;
    }

    public Relationship getRelationshipKey() {
        return relationshipKey;
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

    public void setAgency(Agency agency2) {
        agency = agency2;
    }

    public void setAgencyKey(Agency agency4) {
        agencyKey = agency4;
    }

    public void setAssignTo(Agency agency1) {
        assignTo = agency1;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public void setEntity(Product product) {
        this.product = product;
    }

    public void setEntityAttributeAgency(Agency agency3) {
        productAttributeAgency = agency3;
    }

    public void setEntityKey(Product productKey) {
        this.productKey = productKey;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setRelationshipKey(Relationship relationship) {
        relationshipKey = relationship;
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.Ruleform#traverseForeignKeys(javax.persistence
     * .EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (assignTo != null) {
            assignTo = (Agency) assignTo.manageEntity(em, knownObjects);
        }
        if (attribute != null) {
            attribute = (Attribute) attribute.manageEntity(em, knownObjects);
        }
        if (product != null) {
            product = (Product) product.manageEntity(em, knownObjects);
        }
        if (productAttributeAgency != null) {
            productAttributeAgency = (Agency) productAttributeAgency.manageEntity(em,
                                                                                  knownObjects);
        }
        if (productKey != null) {
            productKey = (Product) productKey.manageEntity(em, knownObjects);
        }
        if (relationshipKey != null) {
            relationshipKey = (Relationship) relationshipKey.manageEntity(em,
                                                                          knownObjects);
        }
        if (agency != null) {
            agency = (Agency) agency.manageEntity(em, knownObjects);
        }
        if (agencyKey != null) {
            agencyKey = (Agency) agencyKey.manageEntity(em, knownObjects);
        }
        if (service != null) {
            service = (Product) service.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }

}