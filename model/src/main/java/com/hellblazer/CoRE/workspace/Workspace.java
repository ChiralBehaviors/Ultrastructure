/**
 * Copyright (C) 2013 Hal Hildebrand. All rights reserved.
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

package com.hellblazer.CoRE.workspace;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.resource.Resource;

/**
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "workspace", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "workspace_id_seq", sequenceName = "workspace_id_seq")
public class Workspace extends Ruleform {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "workspace_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    private String            name;

    private Relationship      definingRelationship;

    private Resource          resourceRoot;

    private Location          locationRoot;

    private Product           productRoot;

    private Attribute         attributeRoot;

    public Attribute getAttributeRoot() {
        return attributeRoot;
    }

    public Relationship getDefiningRelationship() {
        return definingRelationship;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    public Location getLocationRoot() {
        return locationRoot;
    }

    public String getName() {
        return name;
    }

    public Product getProductRoot() {
        return productRoot;
    }

    public Resource getResourceRoot() {
        return resourceRoot;
    }

    public void setAttributeRoot(Attribute attributeRoot) {
        this.attributeRoot = attributeRoot;
    }

    public void setDefiningRelationship(Relationship definingRelationship) {
        this.definingRelationship = definingRelationship;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setLocationRoot(Location locationRoot) {
        this.locationRoot = locationRoot;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProductRoot(Product productRoot) {
        this.productRoot = productRoot;
    }

    public void setResourceRoot(Resource resourceRoot) {
        this.resourceRoot = resourceRoot;
    }

}
