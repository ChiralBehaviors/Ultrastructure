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

package com.chiralbehaviors.CoRE.network;

import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;

/**
 * @author hhildebrand
 *
 */
public class StatusCodeNetworkAuthorization extends
        NetworkAuthorization<StatusCode> {

    private static final long serialVersionUID = 1L;

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.network.NetworkAuthorization#getAuthorizedParent()
     */
    @Override
    public StatusCode getAuthorizedParent() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.network.NetworkAuthorization#getClassifier()
     */
    @Override
    public StatusCode getClassifier() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.network.NetworkAuthorization#setAuthorizedParent(com.chiralbehaviors.CoRE.ExistentialRuleform)
     */
    @Override
    public void setAuthorizedParent(StatusCode parent) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.network.NetworkAuthorization#setClassifier(com.chiralbehaviors.CoRE.ExistentialRuleform)
     */
    @Override
    public void setClassifier(StatusCode classifier) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    public SingularAttribute<WorkspaceAuthorization, ? extends Ruleform> getWorkspaceAuthAttribute() {
        // TODO Auto-generated method stub
        return null;
    }

}
