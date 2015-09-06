/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 *

 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.product;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.XDomainNetworkAuthorization;
import com.chiralbehaviors.CoRE.relationship.Relationship;

/**
 * @author hhildebrand
 *
 */
@Table(name = "product_relationship_authorization", schema = "ruleform")
@Entity
public class ProductRelationshipAuthorization
        extends XDomainNetworkAuthorization<Product, Relationship> {
    private static final long serialVersionUID = 1L;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "from_parent")
    private Product fromParent;

    // bi-directional many-to-one association to AgencyProduct
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "to_parent")
    private Relationship toParent;

    public ProductRelationshipAuthorization() {
        super();
    }

    public ProductRelationshipAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    public ProductRelationshipAuthorization(UUID id) {
        super(id);
    }

    @Override
    public Product getFromParent() {
        return fromParent;
    }

    @Override
    public Relationship getToParent() {
        return toParent;
    }

    @Override
    public void setFromParent(Product fromParent) {
        this.fromParent = fromParent;
    }

    @Override
    public void setToParent(Relationship toParent) {
        this.toParent = toParent;
    }

}
