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

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.attribute.AttributeAuthorization;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The authorization for product location attributes
 * 
 * @author hhildebrand
 * 
 */
@javax.persistence.Entity
@Table(name = "product_location_attribute_authorization", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "product_location_attribute_authorization_id_seq", sequenceName = "product_location_attribute_authorization_id_seq")
public class ProductLocationAttributeAuthorization extends
        AttributeAuthorization {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "product_location_attribute_authorization_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    public ProductLocationAttributeAuthorization() {
    }

    /**
     * @param id
     */
    public ProductLocationAttributeAuthorization(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public ProductLocationAttributeAuthorization(Resource updatedBy) {
        super(updatedBy);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}