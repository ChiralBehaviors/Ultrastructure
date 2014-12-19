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
package com.chiralbehaviors.CoRE.event.status;

import static com.chiralbehaviors.CoRE.event.status.StatusCodeAttribute.GET_ATTRIBUTE;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "status_code_attribute", schema = "ruleform")
@NamedQueries({ @NamedQuery(name = GET_ATTRIBUTE, query = "select ra from StatusCodeAttribute ra where ra.statusCode = :inteval and ra.attribute = :attribute") })
public class StatusCodeAttribute extends AttributeValue<StatusCode> {
    public static final String GET_ATTRIBUTE    = "statusCodeAttribute.intervalAttribute";
    private static final long  serialVersionUID = 1L;

    // bi-directional many-to-one association to Attribute
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "attribute")
    private Attribute          attribute;

    // bi-directional many-to-one association to StatusCode
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "status_code")
    private StatusCode         statusCode;

    public StatusCodeAttribute() {
        super();
    }

    public StatusCodeAttribute(Agency updatedBy) {
        super(updatedBy);
    }

    public StatusCodeAttribute(Attribute attribute) {
        super(attribute);
    }

    public StatusCodeAttribute(Attribute attribute, Agency updatedBy) {
        super(attribute, updatedBy);
    }

    public StatusCodeAttribute(Attribute attribute, BigDecimal value,
                               Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public StatusCodeAttribute(Attribute attribute, boolean value,
                               Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public StatusCodeAttribute(Attribute attribute, int value, Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public StatusCodeAttribute(Attribute attribute, String value,
                               Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public StatusCodeAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    public StatusCodeAttribute(UUID id) {
        super(id);
    }

    @Override
    public Attribute getAttribute() {
        return attribute;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<? extends AttributeValue<StatusCode>, StatusCode> getRuleformAttribute() {
        return StatusCodeAttribute_.statusCode;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<StatusCode> getRuleformClass() {
        return StatusCode.class;
    }

    @JsonGetter
    public StatusCode getStatusCode() {
        return statusCode;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, StatusCodeAttribute> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.statusCodeAttribute;
    }

    @Override
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public void setStatusCode(StatusCode interval) {
        statusCode = interval;
    }
}
