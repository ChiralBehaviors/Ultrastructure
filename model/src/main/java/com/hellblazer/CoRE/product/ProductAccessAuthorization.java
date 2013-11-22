/**
 * Copyright (C) 2013 Halloran Parry. All rights reserved.
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

import static com.hellblazer.CoRE.product.ProductAccessAuthorization.GET_ALL_AUTHORIZATIONS_FOR_PARENT_AND_RELATIONSHIP;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.authorization.AccessAuthorization;

/**
 * @author hparry
 * 
 */
@NamedQueries({

@NamedQuery(name = GET_ALL_AUTHORIZATIONS_FOR_PARENT_AND_RELATIONSHIP, query = "SELECT auth "
                                                                               + "FROM ProductAccessAuthorization auth "
                                                                               + "WHERE auth.relationship = :r "
                                                                               + "AND auth.parent = :rf ") })
@Entity
public abstract class ProductAccessAuthorization extends AccessAuthorization {

    private static final long  serialVersionUID                                   = 1L;

    public static final String PRODUCT_ACCESS_AUTHORIZATION_PREFIX                = "productAccessAuthorization";
    public static final String GET_ALL_AUTHORIZATIONS_FOR_PARENT_AND_RELATIONSHIP = PRODUCT_ACCESS_AUTHORIZATION_PREFIX
                                                                                    + GET_ALL_AUTHORIZATIONS_FOR_PARENT_AND_RELATIONSHIP_SUFFIX;

    @ManyToOne
    @JoinColumn(name = "product1")
    private Product            parent;

    /**
     * @return the parent
     */
    @Override
    public Product getParent() {
        return parent;
    }

    /**
     * @param parent
     *            the parent to set
     */
    public void setParent(Product parent) {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.
     * EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (parent != null) {
            parent = (Product) parent.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);
    }

}
