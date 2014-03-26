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

package com.chiralbehaviors.CoRE.event;

import static com.chiralbehaviors.CoRE.ExistentialRuleform.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.FIND_CLASSIFIED_ATTRIBUTE_VALUES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_FOR_ATTRIBUTE_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.FIND_GROUPED_ATTRIBUTE_VALUES_SUFFIX;
import static com.chiralbehaviors.CoRE.event.ProtocolAttributeAuthorization.FIND_CLASSIFIED_ATTRIBUTE_VALUES;
import static com.chiralbehaviors.CoRE.event.ProtocolAttributeAuthorization.FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS;
import static com.chiralbehaviors.CoRE.event.ProtocolAttributeAuthorization.FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE;
import static com.chiralbehaviors.CoRE.event.ProtocolAttributeAuthorization.FIND_GROUPED_ATTRIBUTE_VALUES;

import java.util.Map;

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
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 * 
 */
@NamedQueries({
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_VALUES, query = "SELECT "
                                                                            + "  attrValue "
                                                                            + "FROM "
                                                                            + "       ProtocolAttribute attrValue, "
                                                                            + "       ProtocolAttributeAuthorization auth, "
                                                                            + "       ProductNetwork network "
                                                                            + "WHERE "
                                                                            + "        auth.authorizedAttribute = attrValue.attribute AND "
                                                                            + "        network.relationship = auth.serviceClassification AND "
                                                                            + "        network.parent = auth.service AND"
                                                                            + "        network.relationship = auth.productClassification AND "
                                                                            + "        network.parent = auth.product AND"
                                                                            + "        attrValue.protocol = :ruleform "),
               @NamedQuery(name = FIND_GROUPED_ATTRIBUTE_VALUES, query = "select attr from ProtocolAttributeAuthorization attr where "
                                                                         + "attr.product = :ruleform "
                                                                         + "AND attr.id IN ("
                                                                         + "select ea.authorizedAttribute from ProductAttributeAuthorization ea "
                                                                         + "WHERE ea.groupingAgency = :agency)"),
               @NamedQuery(name = FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS, query = "select ea from ProductAttributeAuthorization ea "
                                                                                 + "WHERE ea.groupingAgency = :groupingAgency"),
               @NamedQuery(name = FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE, query = "select ea from ProtocolAttributeAuthorization ea "
                                                                                               + "WHERE ea.groupingAgency = :groupingAgency AND ea.authorizedAttribute = :attribute") })
@Entity
@Table(name = "protocol_attribute_authorization", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "protocol_attribute_authorization_id_seq", sequenceName = "protocol_attribute_authorization_id_seq")
public class ProtocolAttributeAuthorization extends AttributeAuthorization {
    public static final String FIND_ATTRIBUTE_AUTHORIZATIONS                          = "protocolAttributeAuthorization.findAttributeAuthorizations";
    public static final String FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS               = "protocolAttributeAuthorization"
                                                                                        + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
    public static final String FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE = "protocolAttributeAuthorization"
                                                                                        + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE_SUFFIX;
    public static final String FIND_CLASSIFIED_ATTRIBUTE_VALUES                       = "protocolAttributeAuthorization"
                                                                                        + FIND_CLASSIFIED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS                  = "protocolAttributeAuthorization"
                                                                                        + FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_SUFFIX;
    public static final String FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE    = "protocolAttributeAuthorization"
                                                                                        + FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_FOR_ATTRIBUTE_SUFFIX;
    public static final String FIND_GROUPED_ATTRIBUTE_VALUES                          = "protocolAttributeAuthorization"
                                                                                        + FIND_GROUPED_ATTRIBUTE_VALUES_SUFFIX;

    private static final long  serialVersionUID                                       = 1L;

    @ManyToOne
    @JoinColumn(name = "deliver_from")
    private Location           deliverFrom;

    @ManyToOne
    @JoinColumn(name = "deliver_from_classification")
    private Relationship       deliverFromClassification;

    @ManyToOne
    @JoinColumn(name = "deliver_to")
    private Location           deliverTo;

    @ManyToOne
    @JoinColumn(name = "deliver_to_classification")
    private Relationship       deliverToClassification;

    @Id
    @GeneratedValue(generator = "protocol_attribute_authorization_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    @ManyToOne
    @JoinColumn(name = "product")
    private Product            product;

    @ManyToOne
    @JoinColumn(name = "product_classification")
    private Relationship       productClassification;

    @ManyToOne
    @JoinColumn(name = "requester")
    private Agency             requester;

    @ManyToOne
    @JoinColumn(name = "requester_classification")
    private Relationship       requesterClassification;

    @ManyToOne
    @JoinColumn(name = "service")
    private Product            service;

    @ManyToOne
    @JoinColumn(name = "service_classification")
    private Relationship       serviceClassification;

    /**
     * 
     */
    public ProtocolAttributeAuthorization() {
        super();
    }

    /**
     * @param updatedBy
     */
    public ProtocolAttributeAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param authorized
     * @param updatedBy
     */
    public ProtocolAttributeAuthorization(Attribute authorized, Agency updatedBy) {
        super(authorized, updatedBy);
    }

    /**
     * @param id
     */
    public ProtocolAttributeAuthorization(Long id) {
        super(id);
    }

    /**
     * @param id
     * @param updatedBy
     */
    public ProtocolAttributeAuthorization(Long id, Agency updatedBy) {
        super(id, updatedBy);
    }

    public Location getDeliverFrom() {
        return deliverFrom;
    }

    public Relationship getDeliverFromClassification() {
        return deliverFromClassification;
    }

    public Location getDeliverTo() {
        return deliverTo;
    }

    public Relationship getDeliverToClassification() {
        return deliverToClassification;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Relationship getProductClassification() {
        return productClassification;
    }

    public Agency getRequester() {
        return requester;
    }

    public Relationship getRequesterClassification() {
        return requesterClassification;
    }

    public Product getService() {
        return service;
    }

    public Relationship getServiceClassification() {
        return serviceClassification;
    }

    public void setDeliverFrom(Location deliverFrom) {
        this.deliverFrom = deliverFrom;
    }

    public void setDeliverFromClassification(Relationship deliverFromClassification) {
        this.deliverFromClassification = deliverFromClassification;
    }

    public void setDeliverTo(Location deliverTo) {
        this.deliverTo = deliverTo;
    }

    public void setDeliverToClassification(Relationship deliverToClassification) {
        this.deliverToClassification = deliverToClassification;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setProductClassification(Relationship procuctClassification) {
        productClassification = procuctClassification;
    }

    public void setRequester(Agency requester) {
        this.requester = requester;
    }

    public void setRequesterClassification(Relationship requesterClassification) {
        this.requesterClassification = requesterClassification;
    }

    public void setService(Product service) {
        this.service = service;
    }

    public void setServiceClassification(Relationship serviceClassification) {
        this.serviceClassification = serviceClassification;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (deliverFrom != null) {
            deliverFrom = (Location) deliverFrom.manageEntity(em, knownObjects);
        }
        if (deliverFromClassification != null) {
            deliverFromClassification = (Relationship) deliverFromClassification.manageEntity(em,
                                                                                              knownObjects);
        }
        if (deliverTo != null) {
            deliverTo = (Location) deliverTo.manageEntity(em, knownObjects);
        }
        if (deliverToClassification != null) {
            deliverToClassification = (Relationship) deliverToClassification.manageEntity(em,
                                                                                          knownObjects);
        }
        if (productClassification != null) {
            productClassification = (Relationship) productClassification.manageEntity(em,
                                                                                      knownObjects);
        }
        if (product != null) {
            product = (Product) product.manageEntity(em, knownObjects);
        }
        if (requester != null) {
            requester = (Agency) requester.manageEntity(em, knownObjects);
        }
        if (requesterClassification != null) {
            requesterClassification = (Relationship) requesterClassification.manageEntity(em,
                                                                                          knownObjects);
        }
        if (service != null) {
            service = (Product) service.manageEntity(em, knownObjects);
        }
        if (serviceClassification != null) {
            serviceClassification = (Relationship) serviceClassification.manageEntity(em,
                                                                                      knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }

}
