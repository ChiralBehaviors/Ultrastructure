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
import java.util.Collections;
import java.util.List;

import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ChildSequencingContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ChildSequencingsContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ExistentialRuleformContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ImportedWorkspaceContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.IntervalContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ParentSequencingContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.RelationshipPairContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.SelfSequencingContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.SelfSequencingsContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.SiblingSequencingContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.SiblingSequencingsContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.StatusCodeSequencingSetContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.UnitContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.WorkspaceContext;
import com.hellblazer.utils.Tuple;

/**
 * @author hhildebrand
 *
 */
public class WorkspacePresentation {

    private final WorkspaceContext context;

    public WorkspacePresentation(WorkspaceContext context) {
        this.context = context;
    }

    public List<ExistentialRuleformContext> getAgencies() {
        if (context.agencies == null) {
            return Collections.emptyList();
        }
        List<ExistentialRuleformContext> ruleforms = context.agencies.existentialRuleform();
        return ruleforms == null ? Collections.emptyList() : ruleforms;
    }

    public List<ExistentialRuleformContext> getAttributes() {
        if (context.attributes == null) {
            return Collections.emptyList();
        }
        List<ExistentialRuleformContext> ruleforms = context.attributes.existentialRuleform();
        return ruleforms == null ? Collections.emptyList() : ruleforms;
    }

    public List<ChildSequencingContext> getChildSequencings() {
        if (context.sequencingAuthorizations == null) {
            return Collections.emptyList();
        }
        ChildSequencingsContext children = context.sequencingAuthorizations.childSequencings();
        return children == null ? Collections.emptyList()
                               : children.childSequencing();
    }

    public List<Tuple<String, String>> getImports() {
        if (context.imports == null) {
            return Collections.emptyList();
        }
        List<Tuple<String, String>> imports = new ArrayList<>();
        for (ImportedWorkspaceContext wsp : context.imports.importedWorkspace()) {
            imports.add(new Tuple<String, String>(wsp.uri.getText(),
                                                  wsp.namespace.getText()));
        }
        return imports;
    }

    public List<IntervalContext> getIntervals() {
        if (context.intervals == null) {
            return Collections.emptyList();
        }

        return context.intervals.interval();
    }

    public List<ExistentialRuleformContext> getLocations() {
        if (context.locations == null) {
            return Collections.emptyList();
        }
        List<ExistentialRuleformContext> ruleforms = context.locations.existentialRuleform();
        return ruleforms == null ? Collections.emptyList() : ruleforms;
    }

    public List<ParentSequencingContext> getParentSequencings() {
        if (context.sequencingAuthorizations == null) {
            return Collections.emptyList();
        }
        return context.sequencingAuthorizations.parentSequencings().parentSequencing();
    }

    public List<ExistentialRuleformContext> getProducts() {
        if (context.products == null) {
            return Collections.emptyList();
        }
        List<ExistentialRuleformContext> ruleforms = context.products.existentialRuleform();
        return ruleforms == null ? Collections.emptyList() : ruleforms;
    }

    public List<RelationshipPairContext> getRelationships() {
        if (context.relationships == null) {
            return Collections.emptyList();
        }
        return context.relationships.relationshipPair();
    }

    public List<SelfSequencingContext> getSelfSequencings() {
        if (context.sequencingAuthorizations == null) {
            return Collections.emptyList();
        }
        SelfSequencingsContext selfSequencings = context.sequencingAuthorizations.selfSequencings();
        return selfSequencings == null ? Collections.emptyList()
                                      : selfSequencings.selfSequencing();
    }

    public List<SiblingSequencingContext> getSiblingSequencings() {
        if (context.sequencingAuthorizations == null) {
            return Collections.emptyList();
        }
        SiblingSequencingsContext siblings = context.sequencingAuthorizations.siblingSequencings();
        return siblings == null ? Collections.emptyList()
                               : siblings.siblingSequencing();
    }

    public List<ExistentialRuleformContext> getStatusCodes() {
        if (context.statusCodes == null) {
            return Collections.emptyList();
        }
        List<ExistentialRuleformContext> ruleforms = context.statusCodes.existentialRuleform();
        return ruleforms == null ? Collections.emptyList() : ruleforms;
    }

    public List<StatusCodeSequencingSetContext> getStatusCodeSequencings() {
        if (context.statusCodeSequencings == null) {
            return Collections.emptyList();
        }
        return context.statusCodeSequencings.statusCodeSequencingSet();
    }

    public List<UnitContext> getUnits() {
        if (context.units == null) {
            return Collections.emptyList();
        }

        return context.units.unit();
    }

    public Tuple<String, String> getWorkspaceDefinition() {
        return new Tuple<String, String>(
                                         context.definition.name.getText(),
                                         context.definition.description.getText());
    }
}
