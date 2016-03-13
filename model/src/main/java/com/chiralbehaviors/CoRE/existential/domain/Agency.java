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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Triggers;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownAgency;
import com.chiralbehaviors.CoRE.existential.ExistentialRuleform;

/**
 * An agency that can provide information, produce products, or perform work.
 *
 * Examples are people, lab groups, software, books, bank accounts, output files
 * of computational analyses, etc.
 *
 * @author hhildebrand
 *
 */
@Entity
@DiscriminatorValue("A")
@Table(name = "existential", schema = "ruleform")
public class Agency extends ExistentialRuleform<Agency> {

    public Agency(String name, Agency updatedBy) {
        super(name, updatedBy);
    }

    private static final long serialVersionUID = -1L;

    public Agency() {
    }

    public Agency(String name) {
        super(name);
    }

    public Agency(String name, String description) {
        super(name, description);
    }

    public Agency(String name, String description, Agency updatedBy) {
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
        return WellKnownAgency.ANY.id();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getCopyId()
     */
    @Override
    public UUID getCopyId() {
        return WellKnownAgency.COPY.id();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNotApplicableId()
     */
    @Override
    public UUID getNotApplicableId() {
        return WellKnownAgency.NOT_APPLICABLE.id();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getSameId()
     */
    @Override
    public UUID getSameId() {
        return WellKnownAgency.SAME.id();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isAny()
     */
    @Override
    public boolean isAny() {
        return WellKnownAgency.ANY.id()
                                  .equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isAnyOrSame()
     */
    @Override
    public boolean isAnyOrSame() {
        return WellKnownAgency.ANY.id()
                                  .equals(getId())
               || WellKnownAgency.SAME.id()
                                      .equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isCopy()
     */
    @Override
    public boolean isCopy() {
        return WellKnownAgency.COPY.id()
                                   .equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isNotApplicable()
     */
    @Override
    public boolean isNotApplicable() {
        return WellKnownAgency.NOT_APPLICABLE.id()
                                             .equals(getId());
    }

    @Override
    public boolean isSame() {
        return WellKnownAgency.SAME.id()
                                   .equals(getId());
    }
}