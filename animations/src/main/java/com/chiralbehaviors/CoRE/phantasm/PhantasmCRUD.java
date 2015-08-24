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

package com.chiralbehaviors.CoRE.phantasm;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.XDomainNetworkAuthorization;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.relationship.Relationship;

/**
 * @author hhildebrand
 *
 */
public class PhantasmCRUD {
    private final Supplier<Model> model;

    public PhantasmCRUD(Supplier<Model> model) {
        this.model = model;
    }

    /**
     * @param attribute
     * @param facet
     * @return
     */
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Object getAttributeValue(RuleForm instance,
                                                                                                                                         Attribute attribute,
                                                                                                                                         NetworkAuthorization<RuleForm> facet) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.get()
                                                                .getNetworkedModel(facet.getClassification());
        Object value = networkedModel.getAttributeValue(instance, attribute)
                                     .getValue();
        if (value instanceof BigDecimal) {
            value = ((BigDecimal) value).floatValue();
        }
        return value;
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<RuleForm> getChildren(RuleForm instance,
                                                                                                                                           NetworkAuthorization<RuleForm> auth) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.get()
                                                                .getNetworkedModel(auth.getClassification());
        return networkedModel.getChildren(instance,
                                          auth.getChildRelationship());
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm getInstance(String id,
                                                                                                                                     NetworkAuthorization<RuleForm> facet) {
        return model.get()
                    .getNetworkedModel(facet.getClassification())
                    .find(UUID.fromString(id));

    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<RuleForm> getInstances(NetworkAuthorization<RuleForm> facet) {

        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.get()
                                                                .getNetworkedModel(facet.getClassification());
        return networkedModel.getChildren(facet.getClassification(),
                                          facet.getClassifier()
                                               .getInverse());
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm getSingularChild(RuleForm instance,
                                                                                                                                          NetworkAuthorization<RuleForm> auth) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.get()
                                                                .getNetworkedModel(auth.getClassification());
        return networkedModel.getImmediateChild(instance,
                                                auth.getChildRelationship());
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Object getSingularXdChild(RuleForm instance,
                                                                                                                                          NetworkAuthorization<RuleForm> facet,
                                                                                                                                          XDomainNetworkAuthorization<?, ?> auth,
                                                                                                                                          NetworkAuthorization<?> child) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.get()
                                                                .getNetworkedModel(facet.getClassification());
        if (child.getClassification() instanceof Agency) {
            return networkedModel.getAuthorizedAgency(instance,
                                                      auth.getConnection());
        } else if (child.getClassification() instanceof Location) {
            return networkedModel.getAuthorizedLocation(instance,
                                                        auth.getConnection());
        } else if (child.getClassification() instanceof Product) {
            return networkedModel.getAuthorizedProduct(instance,
                                                       auth.getConnection());
        } else if (child.getClassification() instanceof Relationship) {
            return networkedModel.getAuthorizedRelationship(instance,
                                                            auth.getConnection());
        } else {
            throw new IllegalArgumentException(String.format("Invalid XdAuth %s -> %s",
                                                             facet.getClassification(),
                                                             child.getClassification()));
        }
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<?> getXdChildren(RuleForm instance,
                                                                                                                                      NetworkAuthorization<RuleForm> facet,
                                                                                                                                      XDomainNetworkAuthorization<?, ?> auth,
                                                                                                                                      NetworkAuthorization<?> child) {
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.get()
                                                                .getNetworkedModel(facet.getClassification());
        if (child.getClassification() instanceof Agency) {
            return networkedModel.getAuthorizedAgencies(instance,
                                                        auth.getConnection());
        } else if (child.getClassification() instanceof Location) {
            return networkedModel.getAuthorizedLocations(instance,
                                                         auth.getConnection());
        } else if (child.getClassification() instanceof Product) {
            return networkedModel.getAuthorizedProducts(instance,
                                                        auth.getConnection());
        } else if (child.getClassification() instanceof Relationship) {
            return networkedModel.getAuthorizedRelationships(instance,
                                                             auth.getConnection());
        } else {
            throw new IllegalArgumentException(String.format("Invalid XdAuth %s -> %s",
                                                             facet.getClassification(),
                                                             child.getClassification()));
        }
    }
}
