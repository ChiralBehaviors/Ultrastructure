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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ExistentialRuleformContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ImportedWorkspaceContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.IntervalContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.RelationshipPairContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.SequencePairContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.StatusCodeSequencingSetContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.UnitContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.WorkspaceContext;
import com.hellblazer.utils.Tuple;

/**
 * @author hhildebrand
 *
 */
public class WorkspacePresentation {
    public static class RelationshipPair {
        public final Tuple<String, String> inverse;

        public final String                inverseWsName;
        public final Tuple<String, String> primary;
        public final String                primaryWsName;

        public RelationshipPair(String primaryWsName,
                                Tuple<String, String> primary,
                                String inverseWsName,
                                Tuple<String, String> inverse) {
            this.primaryWsName = primaryWsName;
            this.primary = primary;
            this.inverseWsName = inverseWsName;
            this.inverse = inverse;
        }
    }

    public static class Interval {
        public final String     wsName;
        public final String     name;
        public final String     description;
        public final BigDecimal start;
        public final String     startUnit;
        public final BigDecimal duration;
        public final String     durationUnit;

        public Interval(String wsName, String name, String description,
                        BigDecimal start, String startUnit,
                        BigDecimal duration, String durationUnit) {
            this.wsName = wsName;
            this.name = name;
            this.description = description;
            this.start = start;
            this.startUnit = startUnit;
            this.duration = duration;
            this.durationUnit = durationUnit;
        }

    }

    private final WorkspaceContext context;

    public WorkspacePresentation(WorkspaceContext context) {
        this.context = context;
    }

    public Map<String, Tuple<String, String>> getAgencies() {
        if (context.agencies == null) {
            return Collections.emptyMap();
        }
        return getRuleforms(context.agencies.existentialRuleform());
    }

    public Map<String, Tuple<String, String>> getAttributes() {
        if (context.attributes == null) {
            return Collections.emptyMap();
        }
        return getRuleforms(context.attributes.existentialRuleform());
    }

    public List<Tuple<String, Tuple<String, String>>> getEdges() {
        List<Tuple<String, Tuple<String, String>>> edges = new ArrayList<>();
        return edges;
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

    public List<Interval> getIntervals() {
        if (context.intervals == null) {
            return Collections.emptyList();
        }
        List<Interval> intervals = new ArrayList<>();

        for (IntervalContext ctx : context.intervals.interval()) {
            Interval interval = new Interval(
                                             ctx.existentialRuleform().workspaceName.getText(),
                                             ctx.existentialRuleform().name.getText(),
                                             ctx.existentialRuleform().description.getText(),
                                             ctx.start == null ? null
                                                              : BigDecimal.valueOf(Double.parseDouble(ctx.start.getText())),
                                             ctx.startUnit == null ? null
                                                                  : ctx.startUnit.getText(),
                                             ctx.duration == null ? null
                                                                 : BigDecimal.valueOf(Double.parseDouble(ctx.duration.getText())),
                                             ctx.durationUnit == null ? null
                                                                     : ctx.durationUnit.getText());
            intervals.add(interval);
        }

        return intervals;
    }

    public Map<String, Tuple<String, String>> getLocations() {
        if (context.locations == null) {
            return Collections.emptyMap();
        }
        return getRuleforms(context.locations.existentialRuleform());
    }

    public Map<String, Tuple<String, String>> getProducts() {
        if (context.products == null) {
            return Collections.emptyMap();
        }
        return getRuleforms(context.products.existentialRuleform());
    }

    public List<RelationshipPair> getRelationships() {
        if (context.relationships == null) {
            return Collections.emptyList();
        }
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

    public Map<String, Tuple<String, String>> getStatusCodes() {
        if (context.statusCodes == null) {
            return Collections.emptyMap();
        }
        return getRuleforms(context.statusCodes.existentialRuleform());
    }

    public Map<String, List<Tuple<String, String>>> getStatusCodeSequencings() {
        if (context.statusCodeSequencings == null) {
            return Collections.emptyMap();
        }

        Map<String, List<Tuple<String, String>>> sequencings = new HashMap<>();
        for (StatusCodeSequencingSetContext ctx : context.statusCodeSequencings.statusCodeSequencingSet()) {
            List<Tuple<String, String>> sequencePairs = new ArrayList<>();
            for (SequencePairContext pairCtx : ctx.sequencePair()) {
                sequencePairs.add(new Tuple<String, String>(
                                                            pairCtx.first.getText(),
                                                            pairCtx.second.getText()));
            }
            sequencings.put(ctx.service.getText(), sequencePairs);
        }

        return sequencings;
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

    private Map<String, Tuple<String, String>> getRuleforms(List<ExistentialRuleformContext> rfContext) {
        Map<String, Tuple<String, String>> ruleforms = new HashMap<>();
        for (ExistentialRuleformContext ruleform : rfContext) {
            ruleforms.put(ruleform.workspaceName.getText(),
                          new Tuple<String, String>(
                                                    ruleform.name.getText(),
                                                    ruleform.description == null ? null
                                                                                : ruleform.description.getText()));
        }
        return ruleforms;
    }
}
