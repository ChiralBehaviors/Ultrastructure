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

package com.hellblazer.CoRE.authorization;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.resource.Resource;

/**
 * @author hhildebrand
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "workspace_authorizations", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "workspace_authorizations_id_seq", sequenceName = "workspace_authorizations_id_seq")
@DiscriminatorColumn(name = "authorization_type")
abstract public class WorkspaceAuthorization extends Ruleform {

    public static final String PRODUCT_RESOURCE    = "0";

    public static final String PRODUCT_STATUS_CODE = "1";

    private static final long  serialVersionUID    = 1L;

    @Column(name = "authorization_type")
    private String             authorizationType;

    @Id
    @GeneratedValue(generator = "workspace_authorizations_id_seq", strategy = GenerationType.SEQUENCE)
    private Long               id;

    @ManyToOne
    @JoinColumn(name = "product")
    protected Product          product;

    public WorkspaceAuthorization() {
        super();
    }

    public WorkspaceAuthorization(Long id) {
        super(id);
    }

    public WorkspaceAuthorization(Long id, Resource updatedBy) {
        super(id, updatedBy);
    }

    public WorkspaceAuthorization(Resource updatedBy) {
        super(updatedBy);
    }

    public WorkspaceAuthorization(String notes) {
        super(notes);
    }

    public WorkspaceAuthorization(String notes, Resource updatedBy) {
        super(notes, updatedBy);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    protected void setAuthorizationType(String authorizationType) {
        this.authorizationType = authorizationType;
    }

}
