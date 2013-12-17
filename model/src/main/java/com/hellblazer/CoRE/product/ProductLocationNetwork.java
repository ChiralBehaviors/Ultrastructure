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