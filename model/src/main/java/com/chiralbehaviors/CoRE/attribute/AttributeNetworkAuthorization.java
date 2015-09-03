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

/**
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "attribute_network_authorization", schema = "ruleform")
public class AttributeNetworkAuthorization
        extends NetworkAuthorization<Attribute> {

    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to Event
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "authorized_parent")
    private Attribute authorizedParent;

    // bi-directional many-to-one association to Event
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "classification")
    private Attribute classification;

    public AttributeNetworkAuthorization() {
        super();
    }

    public AttributeNetworkAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    public AttributeNetworkAuthorization(UUID id) {
        super(id);
    }

    @Override
    public Attribute getAuthorizedParent() {
        return authorizedParent;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.network.NetworkAuthorization#getAuthorizedParentAttribute()
     */
    @Override
    public SingularAttribute<? extends NetworkAuthorization<Attribute>, Attribute> getAuthorizedParentAttribute() {
        return AttributeNetworkAuthorization_.authorizedParent;
    }

    @Override
    public Attribute getClassification() {
        return classification;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.network.NetworkAuthorization#getClassifierAttribute()
     */
    @Override
    public SingularAttribute<? extends NetworkAuthorization<Attribute>, Attribute> getClassifierAttribute() {
        return AttributeNetworkAuthorization_.classification;
    }

    @Override
    public void setAuthorizedParent(Attribute authorizedParent) {
        this.authorizedParent = authorizedParent;
    }

    @Override
    public void setClassification(Attribute classification) {
        this.classification = classification;
    }

}
