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
package com.chiralbehaviors.CoRE.product;

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

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;

/**
 * The authorization for product networks
 * 
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "product_network_authorization", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "product_network_authorization_id_seq", sequenceName = "product_network_authorization_id_seq")
public class ProductNetworkAuthorization extends NetworkAuthorization<Product> {
    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "authorized_parent")
    private Product           authorizedParent;

    // bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "classifier")
    private Product           classifier;

    @Id
    @GeneratedValue(generator = "product_network_authorization_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    /**
     * 
     */
    public ProductNetworkAuthorization() {
        super();
    }

    /**
     * @param updatedBy
     */
    public ProductNetworkAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param id
     */
    public ProductNetworkAuthorization(Long id) {
        super(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.network.NetworkAuthorization#getAuthorizedParent
     * ()
     */
    @Override
    public Product getAuthorizedParent() {
        return authorizedParent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.network.NetworkAuthorization#getClassifier()
     */
    @Override
    public Product getClassifier() {
        return classifier;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.network.NetworkAuthorization#setAuthorizedParent
     * (com.chiralbehaviors.CoRE.network.Networked)
     */
    @Override
    public void setAuthorizedParent(Product parent) {
        authorizedParent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.network.NetworkAuthorization#setClassifier(com
     * .chiralbehaviors.CoRE.network.Networked)
     */
    @Override
    public void setClassifier(Product classifier) {
        this.classifier = classifier;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.Ruleform#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
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
        if (authorizedParent != null) {
            authorizedParent = (Product) authorizedParent.manageEntity(em,
                                                                       knownObjects);
        }
        if (classifier != null) {
            classifier = (Product) classifier.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}
