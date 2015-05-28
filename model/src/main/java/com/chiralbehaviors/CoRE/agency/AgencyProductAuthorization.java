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

package com.chiralbehaviors.CoRE.agency;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.network.XDomainNetworkAuthorization;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;

/**
 * @author hhildebrand
 *
 */
@Table(name = "agency_product_authorization", schema = "ruleform")
@Entity
public class AgencyProductAuthorization extends
        XDomainNetworkAuthorization<Agency, Product> {
    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to Agency
    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "from_parent")
    private Agency            fromParent;

    // bi-directional many-to-one association to AgencyProduct
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @NotNull
    @JoinColumn(name = "to_parent")
    private Product           toParent;

    public AgencyProductAuthorization() {
        super();
    }

    public AgencyProductAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    public AgencyProductAuthorization(UUID id) {
        super(id);
    }

    @Override
    public Agency getFromParent() {
        return fromParent;
    }

    @Override
    public Product getToParent() {
        return toParent;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    public SingularAttribute<WorkspaceAuthorization, ? extends Ruleform> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.agencyProductAuthorization;
    }

    @Override
    public void setFromParent(Agency fromParent) {
        this.fromParent = fromParent;
    }

    @Override
    public void setToParent(Product toParent) {
        this.toParent = toParent;
    }

}
