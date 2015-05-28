/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.attribute;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The authorization for attributes on attributes
 *
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "attr_meta_attr_auth", schema = "ruleform")
public class AttributeMetaAttributeAuthorization extends
        AttributeAuthorization<Attribute, AttributeNetwork> {

    private static final long             serialVersionUID = 1L;

    // bi-directional many-to-one association to Agency
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "network_authorization")
    private AttributeNetworkAuthorization networkAuthorization;

    /**
     *
     */
    public AttributeMetaAttributeAuthorization() {
        super();
    }

    /**
     * @param updatedBy
     */
    public AttributeMetaAttributeAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param attribute
     * @param coreModel
     */
    public AttributeMetaAttributeAuthorization(Attribute attribute,
                                               Agency agency) {
        super(attribute, agency);
    }

    /**
     * @param id
     */
    public AttributeMetaAttributeAuthorization(UUID id) {
        super(id);
    }

    @Override
    public NetworkAuthorization<Attribute> getNetworkAuthorization() {
        return networkAuthorization;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, AttributeMetaAttributeAuthorization> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.attributeMetaAttributeAuthorization;
    }

    @Override
    public void setNetworkAuthorization(NetworkAuthorization<Attribute> auth) {
        networkAuthorization = (AttributeNetworkAuthorization) auth;
    }
}
