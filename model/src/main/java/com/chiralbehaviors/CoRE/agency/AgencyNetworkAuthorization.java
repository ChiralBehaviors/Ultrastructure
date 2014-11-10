/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chiralbehaviors.CoRE.agency;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The authorized network relationshps of agencies
 *
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "agency_network_authorization", schema = "ruleform")
public class AgencyNetworkAuthorization extends NetworkAuthorization<Agency> {
    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to Event
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
            CascadeType.DETACH })
    @JoinColumn(name = "authorized_parent")
    private Agency            authorizedParent;

    // bi-directional many-to-one association to Event
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
            CascadeType.DETACH })
    @JoinColumn(name = "classifier")
    private Agency            classifier;

    public AgencyNetworkAuthorization() {
        super();
    }

    /**
     * @param updatedBy
     */
    public AgencyNetworkAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param id
     */
    public AgencyNetworkAuthorization(UUID id) {
        super(id);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.network.NetworkAuthorization#getAuthorizedParent
     * ()
     */
    @Override
    @JsonGetter
    public Agency getAuthorizedParent() {
        return authorizedParent;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.network.NetworkAuthorization#getClassifier()
     */
    @Override
    @JsonGetter
    public Agency getClassifier() {
        return classifier;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, AgencyNetworkAuthorization> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.agencyNetworkAuthorization;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.network.NetworkAuthorization#setAuthorizedParent
     * (com.chiralbehaviors.CoRE.network.Networked)
     */
    @Override
    public void setAuthorizedParent(Agency parent) {
        authorizedParent = parent;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.network.NetworkAuthorization#setClassifier(com
     * .chiralbehaviors.CoRE.network.Networked)
     */
    @Override
    public void setClassifier(Agency classifier) {
        this.classifier = classifier;
    }
}
