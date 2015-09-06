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

package com.chiralbehaviors.CoRE.network;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;

/**
 * @author hhildebrand
 *
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract public class NetworkAttribute<T extends Ruleform>
        extends AttributeValue<T> {
    private static final long serialVersionUID = 1L;

    public NetworkAttribute() {
        super();
    }

    public NetworkAttribute(Agency updatedBy) {
        super(updatedBy);
    }

    public NetworkAttribute(Attribute attribute) {
        super(attribute);
    }

    public NetworkAttribute(Attribute attribute, Agency updatedBy) {
        super(attribute, updatedBy);
    }

    public NetworkAttribute(Attribute attribute, BigDecimal value,
                            Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public NetworkAttribute(Attribute attribute, Boolean value,
                            Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public NetworkAttribute(Attribute attribute, int value, Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public NetworkAttribute(Attribute attribute, String value,
                            Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    public NetworkAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    public NetworkAttribute(UUID id) {
        super(id);
    }
}
