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
package com.hellblazer.CoRE.product;

import static com.hellblazer.CoRE.product.ProductLocationNetwork.LOCATION_RULES;

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

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.coordinate.Coordinate;

/**
 * The network relationships of product locations
 * 
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "product_location_network", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "product_location_network_id_seq", sequenceName = "product_location_network_id_seq")
@NamedQueries({ @NamedQuery(name = LOCATION_RULES, query = "select n from ProductLocationNetwork n where n.product = :product") })
public class ProductLocationNetwork extends Ruleform {
    public static final String LOCATION_RULES   = "productLocationNetwork.locationRules";
    private static final long  serialVersionUID = 1L;

    //bi-directional many-to-one association to Agency
    @ManyToOne
    @JoinColumn(name = "agency")
    private Agency             agency;                                                    ;

    //bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "contextual_product")
    private Product            contextualProduct;

    //bi-directional many-to-one association to Coordinate
    @ManyToOne
    @JoinColumn(name = "coordinate")
    private Coordinate         coordinate;

    @Id
    @GeneratedValue(generator = "product_location_network_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    //bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "product")
    private Product            product;

    public ProductLocationNetwork() {
    }

    /**
     * @param updatedBy
     */
    public ProductLocationNetwork(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param id
     */
    public ProductLocationNetwork(Long id) {
        super(id);
    }

    public Agency getAgency() {
        return agency;
    }

    public Product getContextualEntity() {
        return contextualProduct;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Product getProdut() {
        return product;
    }

    public void setAgency(Agency agency2) {
        agency = agency2;
    }

    public void setContextualProduct(Product product1) {
        contextualProduct = product1;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setProduct(Product product2) {
        product = product2;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (contextualProduct != null) {
            contextualProduct = (Product) contextualProduct.manageEntity(em,
                                                                         knownObjects);
        }
        if (coordinate != null) {
            coordinate = (Coordinate) coordinate.manageEntity(em, knownObjects);
        }
        if (product != null) {
            product = (Product) product.manageEntity(em, knownObjects);
        }
        if (agency != null) {
            agency = (Agency) agency.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}