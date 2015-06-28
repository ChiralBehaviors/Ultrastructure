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

import java.util.List;

import javax.ws.rs.core.UriInfo;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.Cardinality;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> void addTo(RuleForm existential,
                                                                                                                           Aspect<RuleForm> aspect,
                                                                                                                           ObjectNode node,
                                                                                                                           NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                                                                                                           UriInfo uriInfo) {
        addAttributes(node, existential, aspect, networkedModel);
        addNetworks(node, existential, aspect, networkedModel,
                    aspect.getClassification().getClass().getSimpleName().toLowerCase(),
                    uriInfo);
        addXdomains(node, existential, aspect, uriInfo);
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> JsonNode buildContainer(RuleForm existential,
                                                                                                                                        Aspect<RuleForm> aspect,
                                                                                                                                        UriInfo uriInfo) {
        ObjectNode container = new ObjectNode(JsonNodeFactory.withExactBigDecimals(true));
        container.set(Constants.CONTEXT,
                      buildNode(existential, aspect, uriInfo));
        return container;
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> JsonNode buildNode(RuleForm existential,
                                                                                                                                   Aspect<RuleForm> aspect,
                                                                                                                                   UriInfo uriInfo) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = readOnlyModel.getNetworkedModel(aspect.getClassification());
        ObjectNode node = new ObjectNode(JsonNodeFactory.withExactBigDecimals(true));
        addTo(existential, aspect, node, networkedModel, uriInfo);

        return null;
    }

    /**
     * @param node
     * @param aspect
     * @param networkedModel
     */
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> void addAttributes(ObjectNode node,
                                                                                                                                    RuleForm existential,
                                                                                                                                    Aspect<RuleForm> aspect,
                                                                                                                                    NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        for (AttributeAuthorization<RuleForm, ?> auth : networkedModel.getAttributeAuthorizations(aspect)) {
            String term = auth.getAuthorizedAttribute().getName();
            node.put(term, networkedModel.getAttributeValue(existential,
                                                            null).toString());
        }
    }

    /**
     * @param node
     * @param aspect
     * @param networkedModel
     * @param lowerCase
     * @param uriInfo
     */
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> void addNetworks(ObjectNode node,
                                                                                                                                  RuleForm existential,
                                                                                                                                  Aspect<RuleForm> aspect,
                                                                                                                                  NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                                                                                                                  String lowerCase,
                                                                                                                                  UriInfo uriInfo) {
        for (NetworkAuthorization<RuleForm> auth : networkedModel.getNetworkAuthorizations(aspect)) {
            List<RuleForm> children = networkedModel.getChildren(existential,
                                                                 auth.getChildRelationship());
            if (auth.getCardinality() == Cardinality.N) {

            } else {
                ArrayNode childrenNode = node.putArray(lowerCase);
                children.forEach(child -> {
                    childrenNode.add(getIri(existential, aspect));
                });
            }
        }
    }

    /**
     * @param ruleform
     * @return
     */
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> String getIri(RuleForm ruleform,
                                                                                                                               Aspect<RuleForm> aspect) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param node
     * @param aspect
     * @param uriInfo
     */
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> void addXdomains(ObjectNode node,
                                                                                                                                  RuleForm existential,
                                                                                                                                  Aspect<RuleForm> aspect,
                                                                                                                                  UriInfo uriInfo) {
        // TODO Auto-generated method stub

    }
}
