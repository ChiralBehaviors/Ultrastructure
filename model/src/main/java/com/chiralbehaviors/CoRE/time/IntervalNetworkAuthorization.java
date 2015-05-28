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

package com.chiralbehaviors.CoRE.time;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;

/**
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "interval_network_authorization", schema = "ruleform")
public class IntervalNetworkAuthorization extends
        NetworkAuthorization<Interval> {

    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to Event
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "authorized_parent")
    private Interval          authorizedParent;

    // bi-directional many-to-one association to Event
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "classification")
    private Interval          classification;

    public IntervalNetworkAuthorization() {
        super();
    }

    public IntervalNetworkAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    public IntervalNetworkAuthorization(UUID id) {
        super(id);
    }

    @Override
    public Interval getAuthorizedParent() {
        return authorizedParent;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.network.NetworkAuthorization#getAuthorizedParentAttribute()
     */
    @Override
    public SingularAttribute<? extends NetworkAuthorization<Interval>, ? extends Interval> getAuthorizedParentAttribute() {
        return IntervalNetworkAuthorization_.authorizedParent;
    }

    @Override
    public Interval getClassification() {
        return classification;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.network.NetworkAuthorization#getClassifierAttribute()
     */
    @Override
    public SingularAttribute<? extends NetworkAuthorization<Interval>, ? extends Interval> getClassifierAttribute() {
        return IntervalNetworkAuthorization_.classification;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    public SingularAttribute<WorkspaceAuthorization, ? extends Ruleform> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.intervalNetworkAuthorization;
    }

    @Override
    public void setAuthorizedParent(Interval authorizedParent) {
        this.authorizedParent = authorizedParent;
    }

    @Override
    public void setClassification(Interval classification) {
        this.classification = classification;
    }

}
