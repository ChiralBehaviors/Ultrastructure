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

package com.chiralbehaviors.CoRE.job.status;

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
@Table(name = "status_code_network_authorization", schema = "ruleform")
public class StatusCodeNetworkAuthorization extends
        NetworkAuthorization<StatusCode> {

    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to Event
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "authorized_parent")
    private StatusCode        authorizedParent;

    // bi-directional many-to-one association to Event
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "classification")
    private StatusCode        classification;

    public StatusCodeNetworkAuthorization() {
        super();
    }

    public StatusCodeNetworkAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    public StatusCodeNetworkAuthorization(UUID id) {
        super(id);
    }

    @Override
    public StatusCode getAuthorizedParent() {
        return authorizedParent;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.network.NetworkAuthorization#getAuthorizedParentAttribute()
     */
    @Override
    public SingularAttribute<? extends NetworkAuthorization<StatusCode>, ? extends StatusCode> getAuthorizedParentAttribute() {
        return StatusCodeNetworkAuthorization_.authorizedParent;
    }

    @Override
    public StatusCode getClassification() {
        return classification;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.network.NetworkAuthorization#getClassifierAttribute()
     */
    @Override
    public SingularAttribute<? extends NetworkAuthorization<StatusCode>, ? extends StatusCode> getClassifierAttribute() {
        return StatusCodeNetworkAuthorization_.classification;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    public SingularAttribute<WorkspaceAuthorization, ? extends Ruleform> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.statusCodeNetworkAuthorization;
    }

    @Override
    public void setAuthorizedParent(StatusCode authorizedParent) {
        this.authorizedParent = authorizedParent;
    }

    @Override
    public void setClassification(StatusCode classification) {
        this.classification = classification;
    }

}
