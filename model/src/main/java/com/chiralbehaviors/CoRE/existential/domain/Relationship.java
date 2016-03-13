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

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Triggers;
import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownRelationship;
import com.chiralbehaviors.CoRE.existential.ExistentialRuleform;
import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * The existential rule form that defines relationships between existential rule
 * form instances, providing the edge connecting two nodes in a directed graph.
 *
 * @author hhildebrand
 *
 */
@Entity
@DiscriminatorValue("R")
@Table(name = "existential", schema = "ruleform")
public class Relationship extends ExistentialRuleform<Relationship> {
    public Relationship(String name, Agency updatedBy) {
        super(name, updatedBy);
    }

    private static final long serialVersionUID = 1L;

    @OneToOne(cascade = { CascadeType.PERSIST,
                          CascadeType.DETACH }, fetch = FetchType.EAGER)
    @JoinColumn(name = "inverse", unique = true)
    private Relationship      inverse;

    public Relationship() {
    }

    public Relationship(String name) {
        super(name);
    }

    public Relationship(String name, String description) {
        super(name, description);
    }

    public Relationship(String name, String description, Agency updatedBy) {
        super(name, description, updatedBy);
    }

    public Relationship(String name, String description, Agency updatedBy,
                        Relationship inverse) {
        this(name, description, updatedBy);
        this.inverse = inverse;
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
        return WellKnownRelationship.ANY.id();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getCopyId()
     */
    @Override
    public UUID getCopyId() {
        return WellKnownRelationship.COPY.id();
    }

    @JsonGetter
    public Relationship getInverse() {
        return inverse;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getNotApplicableId()
     */
    @Override
    public UUID getNotApplicableId() {
        return WellKnownRelationship.NOT_APPLICABLE.id();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#getSameId()
     */
    @Override
    public UUID getSameId() {
        return WellKnownRelationship.SAME.id();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isAny()
     */
    @Override
    public boolean isAny() {
        return WellKnownRelationship.ANY.id()
                                        .equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isAnyOrSame()
     */
    @Override
    public boolean isAnyOrSame() {
        return WellKnownRelationship.ANY.id()
                                        .equals(getId())
               || WellKnownRelationship.SAME.id()
                                            .equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isCopy()
     */
    @Override
    public boolean isCopy() {
        return WellKnownRelationship.COPY.id()
                                         .equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.ExistentialRuleform#isNotApplicable()
     */
    @Override
    public boolean isNotApplicable() {
        return WellKnownRelationship.NOT_APPLICABLE.id()
                                                   .equals(getId());
    }

    @Override
    public boolean isSame() {
        return WellKnownRelationship.SAME.id()
                                         .equals(getId());
    }

    public void setInverse(Relationship inverse) {
        this.inverse = inverse;
    }
}
