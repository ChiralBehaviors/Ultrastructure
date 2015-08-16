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

package com.chiralbehaviors.CoRE.phantasm.graphql;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceContext {
    private final Supplier<Model> model;

    public WorkspaceContext(Supplier<Model> model) {
        this.model = model;
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm getInstance(DataFetchingEnvironment env,
                                                                                                                                     NetworkAuthorization<RuleForm> facet) {
        return model.get().getNetworkedModel(facet.getClassification()).find(UUID.fromString(env.getArgument("id")));

    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<RuleForm> getInstances(DataFetchingEnvironment context,
                                                                                                                                            NetworkAuthorization<RuleForm> facet) {

        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = model.get().getNetworkedModel(facet.getClassification());
        return networkedModel.getChildren(facet.getClassification(),
                                          facet.getClassifier().getInverse());
    }
}