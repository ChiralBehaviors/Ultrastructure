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
import com.hellblazer.CoRE.coordinate.Coordinate;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The network relationships of product locations
 * 
 * @author hhildebrand
 * 
 */
@javax.persistence.Entity
@Table(name = "product_location_network", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "product_location_network_id_seq", sequenceName = "product_location_network_id_seq")
@NamedQueries({ @NamedQuery(name = LOCATION_RULES, query = "select n from ProductLocationNetwork n where n.product = :product and n.coordinate.kind = :coordinateKind") })
public class ProductLocationNetwork extends Ruleform {
    private static final long  serialVersionUID = 1L;
    public static final String LOCATION_RULES   = "productLocationNetwork.locationRules";

    //bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "contextual_product")
    private Product            contextualProduct;                                         ;

    //bi-directional many-to-one association to Coordinate
    @ManyToOne
    @JoinColumn(name = "coordinate")
    private Coordinate         coordinate;

    //bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "product")
    private Product            product;

    @Id
    @GeneratedValue(generator = "product_location_network_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    //bi-directional many-to-one association to Resource
    @ManyToOne
    @JoinColumn(name = "resource")
    private Resource           resource;

    public ProductLocationNetwork() {
    }

    /**
     * @param id
     */
    public ProductLocationNetwork(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public ProductLocationNetwork(Resource updatedBy) {
        super(updatedBy);
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

    public Resource getResource() {
        return resource;
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

    public void setResource(Resource resource2) {
        resource = resource2;
    }
}