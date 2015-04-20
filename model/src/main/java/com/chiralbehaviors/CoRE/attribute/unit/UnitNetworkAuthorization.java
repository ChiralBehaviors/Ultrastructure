/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 *

 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.attribute.unit;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;

/**
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "unit_network_authorization", schema = "ruleform")
public class UnitNetworkAuthorization extends NetworkAuthorization<Unit> {

    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to Event
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "authorized_parent")
    private Unit              authorizedParent;

    // bi-directional many-to-one association to Event
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "classifier")
    private Unit              classifier;

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
    public Unit getClassifier() {
        return classifier;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.network.NetworkAuthorization#getClassifierAttribute()
     */
    @Override
    public SingularAttribute<? extends NetworkAuthorization<Unit>, ? extends Unit> getClassifierAttribute() {
        return UnitNetworkAuthorization_.classifier;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    public SingularAttribute<WorkspaceAuthorization, ? extends Ruleform> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.unitNetworkAuthorization;
    }

    @Override
    public void setAuthorizedParent(Unit authorizedParent) {
        this.authorizedParent = authorizedParent;
    }

    @Override
    public void setClassifier(Unit classifier) {
        this.classifier = classifier;
    }

}
