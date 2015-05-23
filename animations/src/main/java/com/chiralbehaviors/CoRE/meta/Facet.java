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

package com.chiralbehaviors.CoRE.meta;

import java.util.List;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;

/**
 * @author hhildebrand
 *
 */
public class Facet<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> {
    private final Aspect<RuleForm>                          aspect;

    private final List<AttributeAuthorization<RuleForm, ?>> attributes;
    private final List<NetworkAuthorization<RuleForm>>      constraints;

    public Facet(Aspect<RuleForm> aspect,
                 List<AttributeAuthorization<RuleForm, ?>> attributes,
                 List<NetworkAuthorization<RuleForm>> constraints) {
        this.aspect = aspect;
        this.attributes = attributes;
        this.constraints = constraints;
    }

    public Aspect<RuleForm> getAspect() {
        return aspect;
    }

    public List<AttributeAuthorization<RuleForm, ?>> getAttributes() {
        return attributes;
    }

    public List<NetworkAuthorization<RuleForm>> getConstraints() {
        return constraints;
    }
}
