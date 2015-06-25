/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.phantasm.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.XDomainNetworkAuthorization;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.relationship.Relationship;

/**
 * @author hhildebrand
 *
 */
public class Facet<RuleForm extends ExistentialRuleform<RuleForm, NetworkRuleform<RuleForm>>> {
    private final Aspect<RuleForm>                                                       aspect;
    private final Map<Attribute, List<AttributeValue<RuleForm>>>                         attributes    = new HashMap<>();
    private final Map<Relationship, NetworkAuthorization<RuleForm>>                      networks      = new HashMap<>();
    private final Map<Relationship, XDomainNetworkAuthorization<RuleForm, Agency>>       agencies      = new HashMap();
    private final Map<Relationship, XDomainNetworkAuthorization<RuleForm, Location>>     loations      = new HashMap();
    private final Map<Relationship, XDomainNetworkAuthorization<RuleForm, Product>>      products      = new HashMap();
    private final Map<Relationship, XDomainNetworkAuthorization<RuleForm, Relationship>> relationships = new HashMap();

    public Facet(Aspect<RuleForm> aspect) {
        this.aspect = aspect;
    }

}
