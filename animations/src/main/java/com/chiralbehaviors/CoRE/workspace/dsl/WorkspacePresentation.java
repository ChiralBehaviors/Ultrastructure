/**
 * (C) Copyright 2015 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.workspace.dsl;

import java.util.Collections;
import java.util.List;

import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.AttributeRuleformContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.AttributedExistentialRuleformContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ChildSequencingContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ChildSequencingsContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ClassifiedAttributeContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.EdgeContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ImportedWorkspaceContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.MetaProtocolContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.NetworkAuthorizationContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.NetworkAuthorizationsContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ParentSequencingContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ProtocolContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.RelationshipPairContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.SelfSequencingContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.SelfSequencingsContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.SiblingSequencingContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.SiblingSequencingsContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.StatusCodeSequencingSetContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.UnitContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.WorkspaceContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.WorkspaceDefinitionContext;

/**
 * @author hhildebrand
 *
 */
public class WorkspacePresentation {

    private final WorkspaceContext context;

    public WorkspacePresentation(WorkspaceContext context) {
        this.context = context;
    }

    public List<AttributedExistentialRuleformContext> getAgencies() {
        if (context.agencies == null) {
            return Collections.emptyList();
        }
        List<AttributedExistentialRuleformContext> ruleforms = context.agencies.attributedExistentialRuleform();
        return ruleforms == null ? Collections.emptyList() : ruleforms;
    }

    public List<ClassifiedAttributeContext> getAgencyAttributeClassifications() {
        if (context.agencies == null) {
            return Collections.emptyList();
        }
        if (context.agencies.classifiedAttributes() == null) {
            return Collections.emptyList();
        }
        return context.agencies.classifiedAttributes().classifiedAttribute();
    }

    public List<NetworkAuthorizationContext> getAgencyNetworkAuthorizations() {
        if (context.agencies == null) {
            return Collections.emptyList();
        }

        NetworkAuthorizationsContext networkAuthorizations = context.agencies.networkAuthorizations();
        if (networkAuthorizations == null) {
            return Collections.emptyList();
        }
        List<NetworkAuthorizationContext> ruleforms = networkAuthorizations.networkAuthorization();

        return ruleforms == null ? Collections.emptyList() : ruleforms;
    }

    public List<EdgeContext> getAgencyNetworks() {
        if (context.agencies == null) {
            return Collections.emptyList();
        }
        if (context.agencies.edges() == null) {
            return Collections.emptyList();
        }
        return context.agencies.edges().edge();
    }

    public List<ClassifiedAttributeContext> getAttributeAttributeClassifications() {
        if (context.attributes == null) {
            return Collections.emptyList();
        }
        if (context.attributes.classifiedAttributes() == null) {
            return Collections.emptyList();
        }
        return context.attributes.classifiedAttributes().classifiedAttribute();
    }

    public List<NetworkAuthorizationContext> getAttributeNetworkAuthorizations() {
        if (context.attributes == null) {
            return Collections.emptyList();
        }

        NetworkAuthorizationsContext networkAuthorizations = context.attributes.networkAuthorizations();
        if (networkAuthorizations == null) {
            return Collections.emptyList();
        }
        List<NetworkAuthorizationContext> ruleforms = networkAuthorizations.networkAuthorization();

        return ruleforms == null ? Collections.emptyList() : ruleforms;
    }

    public List<EdgeContext> getAttributeNetworks() {
        if (context.attributes == null) {
            return Collections.emptyList();
        }
        if (context.attributes.edges() == null) {
            return Collections.emptyList();
        }
        return context.attributes.edges().edge();
    }

    public List<AttributeRuleformContext> getAttributes() {
        if (context.attributes == null) {
            return Collections.emptyList();
        }
        List<AttributeRuleformContext> ruleforms = context.attributes.attributeRuleform();
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

    public List<ImportedWorkspaceContext> getImports() {
        if (context.imports == null) {
            return Collections.emptyList();
        }
        return context.imports.importedWorkspace();
    }

    public List<EdgeContext> getInferences() {
        if (context.inferences == null) {
            return Collections.emptyList();
        }
        if (context.inferences.edge() == null) {
            return Collections.emptyList();
        }
        return context.inferences.edge();
    }

    public List<ClassifiedAttributeContext> getIntervalAttributeClassifications() {
        if (context.intervals == null) {
            return Collections.emptyList();
        }
        if (context.intervals.classifiedAttributes() == null) {
            return Collections.emptyList();
        }
        return context.intervals.classifiedAttributes().classifiedAttribute();
    }

    public List<NetworkAuthorizationContext> getIntervalNetworkAuthorizations() {
        if (context.intervals == null
            || context.intervals.networkAuthorizations() == null) {
            return Collections.emptyList();
        }

        List<NetworkAuthorizationContext> ruleforms = context.intervals.networkAuthorizations().networkAuthorization();

        return ruleforms == null ? Collections.emptyList() : ruleforms;
    }

    public List<EdgeContext> getIntervalNetworks() {
        if (context.intervals == null) {
            return Collections.emptyList();
        }
        if (context.intervals.edges() == null) {
            return Collections.emptyList();
        }
        return context.intervals.edges().edge();
    }

    public List<AttributedExistentialRuleformContext> getIntervals() {
        if (context.intervals == null) {
            return Collections.emptyList();
        }
        return context.intervals.attributedExistentialRuleform();
    }

    public List<ClassifiedAttributeContext> getLocationAttributeClassifications() {
        if (context.locations == null) {
            return Collections.emptyList();
        }
        if (context.locations.classifiedAttributes() == null) {
            return Collections.emptyList();
        }
        return context.locations.classifiedAttributes().classifiedAttribute();
    }

    public List<NetworkAuthorizationContext> getLocationNetworkAuthorizations() {
        if (context.locations == null) {
            return Collections.emptyList();
        }

        NetworkAuthorizationsContext networkAuthorizations = context.locations.networkAuthorizations();
        if (networkAuthorizations == null) {
            return Collections.emptyList();
        }
        List<NetworkAuthorizationContext> ruleforms = networkAuthorizations.networkAuthorization();

        return ruleforms == null ? Collections.emptyList() : ruleforms;
    }

    public List<EdgeContext> getLocationNetworks() {
        if (context.locations == null) {
            return Collections.emptyList();
        }
        if (context.locations.edges() == null) {
            return Collections.emptyList();
        }
        return context.locations.edges().edge();
    }

    public List<AttributedExistentialRuleformContext> getLocations() {
        if (context.locations == null) {
            return Collections.emptyList();
        }
        List<AttributedExistentialRuleformContext> ruleforms = context.locations.attributedExistentialRuleform();
        return ruleforms == null ? Collections.emptyList() : ruleforms;
    }

    public List<MetaProtocolContext> getMetaProtocols() {
        if (context.metaProtocols == null) {
            return Collections.emptyList();
        }
        return context.metaProtocols.metaProtocol();
    }

    public List<ParentSequencingContext> getParentSequencings() {
        if (context.sequencingAuthorizations == null) {
            return Collections.emptyList();
        }
        return context.sequencingAuthorizations.parentSequencings().parentSequencing();
    }

    public List<ClassifiedAttributeContext> getProductAttributeClassifications() {
        if (context.products == null) {
            return Collections.emptyList();
        }
        if (context.products.classifiedAttributes() == null) {
            return Collections.emptyList();
        }
        return context.products.classifiedAttributes().classifiedAttribute();
    }

    public List<NetworkAuthorizationContext> getProductNetworkAuthorizations() {
        if (context.products == null) {
            return Collections.emptyList();
        }

        NetworkAuthorizationsContext networkAuthorizations = context.products.networkAuthorizations();
        if (networkAuthorizations == null) {
            return Collections.emptyList();
        }
        List<NetworkAuthorizationContext> ruleforms = networkAuthorizations.networkAuthorization();

        return ruleforms == null ? Collections.emptyList() : ruleforms;
    }

    public List<EdgeContext> getProductNetworks() {
        if (context.products == null) {
            return Collections.emptyList();
        }
        if (context.products.edges() == null) {
            return Collections.emptyList();
        }
        return context.products.edges().edge();
    }

    public List<AttributedExistentialRuleformContext> getProducts() {
        if (context.products == null) {
            return Collections.emptyList();
        }
        List<AttributedExistentialRuleformContext> ruleforms = context.products.attributedExistentialRuleform();
        return ruleforms == null ? Collections.emptyList() : ruleforms;
    }

    public List<ProtocolContext> getProtocols() {
        if (context.protocols == null) {
            return Collections.emptyList();
        }
        return context.protocols.protocol();
    }

    public List<ClassifiedAttributeContext> getRelationshipAttributeClassifications() {
        if (context.relationships == null) {
            return Collections.emptyList();
        }
        if (context.relationships.classifiedAttributes() == null) {
            return Collections.emptyList();
        }
        return context.relationships.classifiedAttributes().classifiedAttribute();
    }

    public List<NetworkAuthorizationContext> getRelationshipNetworkAuthorizations() {
        if (context.relationships == null
            || context.relationships.networkAuthorizations() == null) {
            return Collections.emptyList();
        }

        List<NetworkAuthorizationContext> ruleforms = context.relationships.networkAuthorizations().networkAuthorization();

        return ruleforms == null ? Collections.emptyList() : ruleforms;
    }

    public List<EdgeContext> getRelationshipNetworks() {
        if (context.relationships == null) {
            return Collections.emptyList();
        }
        if (context.relationships.edges() == null) {
            return Collections.emptyList();
        }
        return context.relationships.edges().edge();
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

    public List<ClassifiedAttributeContext> getStatusCodeAttributeClassifications() {
        if (context.statusCodes == null) {
            return Collections.emptyList();
        }
        if (context.statusCodes.classifiedAttributes() == null) {
            return Collections.emptyList();
        }
        return context.statusCodes.classifiedAttributes().classifiedAttribute();
    }

    public List<NetworkAuthorizationContext> getStatusCodeNetworkAuthorizations() {
        if (context.statusCodes == null
            || context.statusCodes.networkAuthorizations() == null) {
            return Collections.emptyList();
        }

        List<NetworkAuthorizationContext> ruleforms = context.statusCodes.networkAuthorizations().networkAuthorization();

        return ruleforms == null ? Collections.emptyList() : ruleforms;
    }

    public List<EdgeContext> getStatusCodeNetworks() {
        if (context.statusCodes == null) {
            return Collections.emptyList();
        }
        if (context.statusCodes.edges() == null) {
            return Collections.emptyList();
        }
        return context.statusCodes.edges().edge();
    }

    public List<AttributedExistentialRuleformContext> getStatusCodes() {
        if (context.statusCodes == null) {
            return Collections.emptyList();
        }
        List<AttributedExistentialRuleformContext> ruleforms = context.statusCodes.attributedExistentialRuleform();
        return ruleforms == null ? Collections.emptyList() : ruleforms;
    }

    public List<StatusCodeSequencingSetContext> getStatusCodeSequencings() {
        if (context.statusCodeSequencings == null) {
            return Collections.emptyList();
        }
        return context.statusCodeSequencings.statusCodeSequencingSet();
    }

    public List<ClassifiedAttributeContext> getUnitAttributeClassifications() {
        if (context.units == null) {
            return Collections.emptyList();
        }
        if (context.units.classifiedAttributes() == null) {
            return Collections.emptyList();
        }
        return context.units.classifiedAttributes().classifiedAttribute();
    }

    public List<NetworkAuthorizationContext> getUnitNetworkAuthorizations() {
        if (context.units == null
            || context.units.networkAuthorizations() == null) {
            return Collections.emptyList();
        }

        List<NetworkAuthorizationContext> ruleforms = context.units.networkAuthorizations().networkAuthorization();

        return ruleforms == null ? Collections.emptyList() : ruleforms;
    }

    public List<EdgeContext> getUnitNetworks() {
        if (context.units == null) {
            return Collections.emptyList();
        }
        if (context.units.edges() == null) {
            return Collections.emptyList();
        }
        return context.units.edges().edge();
    }

    public List<UnitContext> getUnits() {
        if (context.units == null) {
            return Collections.emptyList();
        }

        return context.units.unit();
    }

    public WorkspaceDefinitionContext getWorkspaceDefinition() {
        return context.definition;
    }
}
