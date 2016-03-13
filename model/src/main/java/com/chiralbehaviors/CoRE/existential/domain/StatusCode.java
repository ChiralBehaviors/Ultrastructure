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
package com.chiralbehaviors.CoRE.existential.domain;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Triggers;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownStatusCode;
import com.chiralbehaviors.CoRE.existential.ExistentialRuleform;

/**
 * The persistent class for the status_code database table.
 *
 */
@Entity
@DiscriminatorValue("S")
@Table(name = "existential", schema = "ruleform")
public class StatusCode extends ExistentialRuleform<StatusCode> {
    public StatusCode(String name, Agency updatedBy) {
        super(name, updatedBy);
    }

    private static final long serialVersionUID  = 1L;

    @Column(name = "fail_parent")
    private boolean           failParent        = true;

    @Column(name = "propagate_children")
    private boolean           propagateChildren = false;

    public StatusCode() {
    }

    public StatusCode(String name) {
        super(name);
    }

    public StatusCode(String name, String description) {
        super(name, description);
    }

    public StatusCode(String name, String description, Agency updatedBy) {
        super(name, description, updatedBy);
    }

    @Override
    public void delete(Triggers triggers) {
        triggers.delete(this);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getAnyId()
     */
    @Override
    public UUID getAnyId() {
        return WellKnownStatusCode.ANY.id();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getCopyId()
     */
    @Override
    public UUID getCopyId() {
        return WellKnownStatusCode.COPY.id();
    }

    public Boolean getFailParent() {
        return failParent;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNotApplicableId()
     */
    @Override
    public UUID getNotApplicableId() {
        return WellKnownStatusCode.NOT_APPLICABLE.id();
    }

    public Boolean getPropagateChildren() {
        return propagateChildren;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getSameId()
     */
    @Override
    public UUID getSameId() {
        return WellKnownStatusCode.SAME.id();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isAny()
     */
    @Override
    public boolean isAny() {
        return WellKnownStatusCode.ANY.id()
                                      .equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isAnyOrSame()
     */
    @Override
    public boolean isAnyOrSame() {
        return WellKnownStatusCode.ANY.id()
                                      .equals(getId())
               || WellKnownStatusCode.SAME.id()
                                          .equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isCopy()
     */
    @Override
    public boolean isCopy() {
        return WellKnownStatusCode.COPY.id()
                                       .equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isNotApplicable()
     */
    @Override
    public boolean isNotApplicable() {
        return WellKnownStatusCode.NOT_APPLICABLE.id()
                                                 .equals(getId());
    }

    @Override
    public boolean isSame() {
        return WellKnownStatusCode.SAME.id()
                                       .equals(getId());
    }

    public void setFailParent(Boolean failParent) {
        this.failParent = failParent;
    }

    public void setPropagateChildren(Boolean propagateChildren) {
        this.propagateChildren = propagateChildren;
    }
}
