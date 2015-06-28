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

package com.chiralbehaviors.CoRE.phantasm.jsonld;

import javax.ws.rs.core.UriInfo;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author hhildebrand
 *
 */
public class FacetNodeBuilder {

    private final Model readOnlyModel;

    public FacetNodeBuilder(Model readOnlyModel) {
        this.readOnlyModel = readOnlyModel;
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> JsonNode buildContainer(Aspect<RuleForm> aspect,
                                                                                                                                        UriInfo uriInfo) {
        ObjectNode container = new ObjectNode(JsonNodeFactory.withExactBigDecimals(true));
        container.set(Constants.CONTEXT, buildNode(aspect, uriInfo));
        return container;
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> JsonNode buildNode(Aspect<RuleForm> aspect,
                                                                                                                                   UriInfo uriInfo) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = readOnlyModel.getNetworkedModel(aspect.getClassification());
        ObjectNode node = new ObjectNode(JsonNodeFactory.withExactBigDecimals(true));
        addAttributes(node, aspect, networkedModel);
        addNetworks(node, aspect, networkedModel,
                    aspect.getClassification().getClass().getSimpleName().toLowerCase(),
                    uriInfo);
        addXdomains(node, aspect, uriInfo);

        return null;
    }

    /**
     * @param node
     * @param aspect
     * @param networkedModel
     */
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> void addAttributes(ObjectNode node,
                                                                                                                                    Aspect<RuleForm> aspect,
                                                                                                                                    NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        // TODO Auto-generated method stub

    }

    /**
     * @param node
     * @param aspect
     * @param networkedModel
     * @param lowerCase
     * @param uriInfo
     */
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> void addNetworks(ObjectNode node,
                                                                                                                                  Aspect<RuleForm> aspect,
                                                                                                                                  NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                                                                                                                  String lowerCase,
                                                                                                                                  UriInfo uriInfo) {
        // TODO Auto-generated method stub

    }

    /**
     * @param node
     * @param aspect
     * @param uriInfo
     */
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> void addXdomains(ObjectNode node,
                                                                                                                                  Aspect<RuleForm> aspect,
                                                                                                                                  UriInfo uriInfo) {
        // TODO Auto-generated method stub

    }
}
