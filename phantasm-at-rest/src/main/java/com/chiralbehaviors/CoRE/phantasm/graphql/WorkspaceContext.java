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

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.XDomainNetworkAuthorization;
import com.chiralbehaviors.CoRE.phantasm.PhantasmCRUD;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceContext {
    private final PhantasmCRUD crud;

    public WorkspaceContext(PhantasmCRUD crud) {
        this.crud = crud;
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Object getAttributeValue(DataFetchingEnvironment env,
                                                                                                                                         AttributeAuthorization<RuleForm, Network> stateAuth) {
        @SuppressWarnings("unchecked")
        RuleForm instance = (RuleForm) env.getSource();
        return crud.getAttributeValue(instance, stateAuth);
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<RuleForm> getChildren(DataFetchingEnvironment env,
                                                                                                                                           NetworkAuthorization<RuleForm> auth) {
        @SuppressWarnings("unchecked")
        RuleForm instance = (RuleForm) env.getSource();
        return crud.getChildren(instance, auth);
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm getInstance(DataFetchingEnvironment env,
                                                                                                                                     NetworkAuthorization<RuleForm> facet) {
        return crud.getInstance(env.getArgument("id"), facet);

    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<RuleForm> getInstances(DataFetchingEnvironment env,
                                                                                                                                            NetworkAuthorization<RuleForm> facet) {

        return crud.getInstances(facet);
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> RuleForm getSingularChild(DataFetchingEnvironment env,
                                                                                                                                          NetworkAuthorization<RuleForm> auth) {
        @SuppressWarnings("unchecked")
        RuleForm instance = (RuleForm) env.getSource();
        return crud.getSingularChild(instance, auth);
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Object getSingularXdChild(DataFetchingEnvironment env,
                                                                                                                                          NetworkAuthorization<RuleForm> facet,
                                                                                                                                          XDomainNetworkAuthorization<?, ?> auth,
                                                                                                                                          NetworkAuthorization<?> child) {
        @SuppressWarnings("unchecked")
        RuleForm instance = (RuleForm) env.getSource();
        return crud.getSingularChild(instance, facet, auth, child);
    }

    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<?> getXdChildren(DataFetchingEnvironment env,
                                                                                                                                      NetworkAuthorization<RuleForm> facet,
                                                                                                                                      XDomainNetworkAuthorization<?, ?> auth,
                                                                                                                                      NetworkAuthorization<?> child) {
        @SuppressWarnings("unchecked")
        RuleForm instance = (RuleForm) env.getSource();
        return crud.getChildren(instance, facet, auth, child);
    }
}
