/**
 * (C) Copyright 2015 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.workspace.dsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ExistentialRuleformContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ImportedWorkspaceContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.RelationshipPairContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.WorkspaceContext;
import com.hellblazer.utils.Tuple;

/**
 * @author hhildebrand
 *
 */
public class WorkspacePresentation {
    public static class RelationshipPair {
        public RelationshipPair(String primaryWsName,
                                Tuple<String, String> primary,
                                String inverseWsName,
                                Tuple<String, String> inverse) {
            this.primaryWsName = primaryWsName;
            this.primary = primary;
            this.inverseWsName = inverseWsName;
            this.inverse = inverse;
        }

        public final String                primaryWsName;
        public final Tuple<String, String> primary;
        public final String                inverseWsName;
        public final Tuple<String, String> inverse;
    }

    private final WorkspaceContext context;

    public WorkspacePresentation(WorkspaceContext context) {
        this.context = context;
    }

    public Tuple<String, String> getWorkspaceDefinition() {
        return new Tuple<String, String>(
                                         context.definition.name.getText(),
                                         context.definition.description.getText());
    }

    public List<Tuple<String, String>> getImports() {
        List<Tuple<String, String>> imports = new ArrayList<>();
        for (ImportedWorkspaceContext wsp : context.imports.importedWorkspace()) {
            imports.add(new Tuple<String, String>(wsp.uri.getText(),
                                                  wsp.namespace.getText()));
        }
        return imports;
    }

    public Map<String, Tuple<String, String>> getAgencies() {
        return getRuleforms(context.agencies.existentialRuleform());
    }

    public Map<String, Tuple<String, String>> getAttributes() {
        return getRuleforms(context.attributes.existentialRuleform());
    }

    public Map<String, Tuple<String, String>> getIntervals() {
        return getRuleforms(context.intervals.existentialRuleform());
    }

    public Map<String, Tuple<String, String>> getLocations() {
        return getRuleforms(context.locations.existentialRuleform());
    }

    public Map<String, Tuple<String, String>> getProducts() {
        return getRuleforms(context.products.existentialRuleform());
    }

    public Map<String, Tuple<String, String>> getStatusCodes() {
        return getRuleforms(context.statusCodes.existentialRuleform());
    }

    public Map<String, Tuple<String, String>> getUnits() {
        return getRuleforms(context.units.existentialRuleform());
    }

    public List<RelationshipPair> getRelationships() {
        List<RelationshipPair> pairs = new ArrayList<>();
        for (RelationshipPairContext pair : context.relationships.relationshipPair()) {
            pairs.add(new RelationshipPair(
                                           pair.primary.getText(),
                                           new Tuple<String, String>(
                                                                     pair.primary.name.getText(),
                                                                     pair.primary.description.getText()),
                                           pair.inverse.getText(),
                                           new Tuple<String, String>(
                                                                     pair.inverse.name.getText(),
                                                                     pair.inverse.description.getText())));
        }
        return pairs;
    }

    private Map<String, Tuple<String, String>> getRuleforms(List<ExistentialRuleformContext> rfContext) {
        Map<String, Tuple<String, String>> ruleforms = new HashMap<>();
        for (ExistentialRuleformContext ruleform : rfContext) {
            ruleforms.put(ruleform.workspaceName.getText(),
                          new Tuple<String, String>(
                                                    ruleform.name.getText(),
                                                    ruleform.description.getText()));
        }
        return ruleforms;
    }
}
