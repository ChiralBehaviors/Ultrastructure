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

package com.chiralbehaviors.CoRE.attribute.unit;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;

/**
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "unit_network_authorization", schema = "ruleform")
public class UnitNetworkAuthorization extends NetworkAuthorization<Unit> {

    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to Event
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.EAGER)
    @JoinColumn(name = "authorized_parent")
    private Unit authorizedParent;

    // bi-directional many-to-one association to Event
    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.EAGER)
    @JoinColumn(name = "classification")
    private Unit classification;

    public UnitNetworkAuthorization() {
        super();
    }

    public UnitNetworkAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    public UnitNetworkAuthorization(UUID id) {
        super(id);
    }

    @Override
    public Unit getAuthorizedParent() {
        return authorizedParent;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.network.NetworkAuthorization#getAuthorizedParentAttribute()
     */
    @Override
    public SingularAttribute<? extends NetworkAuthorization<Unit>, ? extends Unit> getAuthorizedParentAttribute() {
        return UnitNetworkAuthorization_.authorizedParent;
    }

    @Override
    public Unit getClassification() {
        return classification;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.network.NetworkAuthorization#getClassifierAttribute()
     */
    @Override
    public SingularAttribute<? extends NetworkAuthorization<Unit>, ? extends Unit> getClassifierAttribute() {
        return UnitNetworkAuthorization_.classification;
    }

    @Override
    public void setAuthorizedParent(Unit authorizedParent) {
        this.authorizedParent = authorizedParent;
    }

    @Override
    public void setClassification(Unit classification) {
        this.classification = classification;
    }

}
