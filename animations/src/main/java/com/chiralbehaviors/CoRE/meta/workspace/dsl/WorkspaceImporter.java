/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 *

 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE.meta.workspace.dsl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import javax.management.openmbean.InvalidKeyException;

import org.antlr.v4.runtime.Token;

import com.chiralbehaviors.CoRE.domain.Attribute;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.domain.StatusCode;
import com.chiralbehaviors.CoRE.jooq.enums.Cardinality;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.enums.ValueType;
import com.chiralbehaviors.CoRE.jooq.tables.records.ChildSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.MetaProtocolRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.NetworkInferenceRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ParentSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ProtocolRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.SelfSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.SiblingSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.StatusCodeSequencingRecord;
import com.chiralbehaviors.CoRE.meta.ExistentialModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.AttributeRuleformContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.AttributeValueContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.AttributedExistentialRuleformContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ChildSequencingContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ClassifiedAttributeContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ConstraintContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.EdgeContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.FacetContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ImportedWorkspaceContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.MetaProtocolContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.NetworkConstraintsContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ParentSequencingContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ProtocolContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.QualifiedNameContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.RelationshipPairContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.SelfSequencingContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.SequencePairContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.SiblingSequencingContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.StatusCodeSequencingSetContext;
import com.hellblazer.utils.Tuple;

/**
 * @author hparry
 *
 */
public class WorkspaceImporter {
    private static final String STATUS_CODE_SEQUENCING_FORMAT = "%s: %s -> %s";
    private static final String THIS                          = "this";

    public static WorkspaceImporter manifest(InputStream source,
                                             Model model) throws IOException {
        WorkspaceImporter importer = new WorkspaceImporter(source, model);
        importer.manifest();
        return importer;
    }

    public static void manifest(List<URL> wsps, Model model) {
        wsps.forEach(url -> {
            try (InputStream is = url.openStream()) {
                try {
                    manifest(is, model);
                } catch (Exception e) {
                    throw new IllegalStateException(String.format("Cannot load %s",
                                                                  url),
                                                    e);
                }
            } catch (IOException e) {
                throw new IllegalStateException(String.format("Cannot load %s",
                                                              url),
                                                e);
            }
        });
    }

    private final Model                 model;
    private WorkspaceScope              scope;
    private UUID                        uuid;
    private EditableWorkspace           workspace;
    private String                      workspaceUri;

    private final WorkspacePresentation wsp;

    public WorkspaceImporter(InputStream source,
                             Model model) throws IOException {
        this(new WorkspacePresentation(source), model);
    }

    public WorkspaceImporter(WorkspacePresentation wsp, Model model) {
        this.wsp = wsp;
        this.model = model;
    }

    public WorkspaceAccessor getWorkspace() {
        return workspace;
    }

    public WorkspaceImporter initialize() {
        workspaceUri = WorkspacePresentation.stripQuotes(wsp.getWorkspaceDefinition().uri.getText());
        uuid = WorkspaceAccessor.uuidOf(workspaceUri);
        return this;
    }

    /**
     * Used only to bootstrap the kernel. Public because of packaging, not API.
     *
     * @param definingProduct
     * @return
     */
    public WorkspaceAccessor load(Product definingProduct) {
        definingProduct.refresh();
        definingProduct.setName(WorkspacePresentation.stripQuotes(wsp.getWorkspaceDefinition().name.getText()));
        Token description = wsp.getWorkspaceDefinition().description;
        definingProduct.setDescription(description == null ? null
                                                           : WorkspacePresentation.stripQuotes(description.getText()));
        definingProduct.update();
        scope = model.getWorkspaceModel()
                     .getScoped(definingProduct);
        workspace = (EditableWorkspace) scope.getWorkspace();
        loadWorkspace();
        return workspace;
    }

    public WorkspaceAccessor manifest() {
        initialize();
        int version = Integer.parseInt(wsp.getWorkspaceDefinition().version.getText());
        return version == 1 ? createWorkspace() : addToWorkspace(version);
    }

    public void setScope(WorkspaceScope scope) {
        this.scope = scope;
    }

    private WorkspaceAccessor addToWorkspace(int version) {
        Product definingProduct = getWorkspaceProduct();
        if (definingProduct == null) {
            throw new IllegalStateException(String.format("Workspace %s does not exist, cannot update to version %s",
                                                          wsp.getWorkspaceDefinition().name.getText(),
                                                          version));
        }
        if (definingProduct.getVersion() >= version) {
            throw new IllegalStateException(String.format("Workspace %s is at version %s, unable to update to %s",
                                                          wsp.getWorkspaceDefinition().name.getText(),
                                                          definingProduct.getVersion(),
                                                          version));
        }
        // definingProduct.setVersion(version);
        return load(definingProduct);
    }

    private Cardinality cardinality(ConstraintContext constraint) {
        String card = constraint.cardinality.getText()
                                            .toUpperCase();
        switch (card) {
            case "ONE":
                return Cardinality._1;
            case "ZERO":
                return Cardinality.Zero;
            case "N":
                return Cardinality.N;
            default:
                throw new IllegalArgumentException(String.format("Invalid cardinality: %s",
                                                                 card));
        }
    }

    private void classifiedAttributes(FacetContext facet,
                                      FacetRecord authorization) {
        List<ClassifiedAttributeContext> classifiedAttributes = facet.classifiedAttribute();
        if (classifiedAttributes == null) {
            return;
        }
        classifiedAttributes.forEach(attribute -> {
            ExistentialAttributeAuthorizationRecord auth = model.records()
                                                                .newExistentialAttributeAuthorization();
            auth.setFacet(authorization.getId());
            Attribute authorizedAttribute = model.records()
                                                 .resolve(resolve(attribute.key));
            auth.setAuthorizedAttribute(authorizedAttribute.getId());
            if (attribute.defaultValue != null) {
                setValueFromString(authorizedAttribute, auth,
                                   WorkspacePresentation.stripQuotes(attribute.defaultValue.getText()));
            }
            auth.insert();
            workspace.add(auth);

        });
    }

    private void createNetworkAuth(FacetContext facet, FacetRecord facetAuth,
                                   ConstraintContext constraint) {
        ExistentialNetworkAuthorizationRecord authorization = model.records()
                                                                   .newExistentialNetworkAuthorization();
        authorization.setName(WorkspacePresentation.networkAuthNameOf(constraint));
        authorization.setParent(facetAuth.getId());
        authorization.setRelationship(resolve(constraint.childRelationship));
        resolveChild(constraint, authorization);
        Cardinality cardinality = cardinality(constraint);
        authorization.setCardinality(cardinality);
        authorization.insert();
        workspace.add(authorization);
        List<ClassifiedAttributeContext> classifiedAttributes = constraint.classifiedAttribute();
        if (classifiedAttributes == null) {
            return;
        }
        classifiedAttributes.forEach(attribute -> {
            ExistentialNetworkAttributeAuthorizationRecord attrAuth = model.records()
                                                                           .newExistentialNetworkAttributeAuthorization();
            Attribute authorizedAttribute = model.records()
                                                 .resolve(resolve(attribute.key));
            attrAuth.setAuthorizedAttribute(authorizedAttribute.getId());
            attrAuth.setNetworkAuthorization(authorization.getId());
            if (attribute.defaultValue != null) {
                setValueFromString(authorizedAttribute, attrAuth,
                                   WorkspacePresentation.stripQuotes(attribute.defaultValue.getText()));
            }
            attrAuth.insert();
            workspace.add(attrAuth);
        });
    }

    private WorkspaceAccessor createWorkspace() {
        Product existing = getWorkspaceProduct();
        if (existing != null) {
            throw new IllegalStateException(String.format("Workspace %s already exists at version %s, not created",
                                                          existing.getName(),
                                                          existing.getVersion()));
        }
        Product definingProduct = createWorkspaceProduct();
        scope = model.getWorkspaceModel()
                     .createWorkspace(definingProduct);
        workspace = (EditableWorkspace) scope.getWorkspace();
        ExistentialAttributeRecord attributeValue = model.getPhantasmModel()
                                                         .getAttributeValue(definingProduct,
                                                                            model.getKernel()
                                                                                 .getIRI());
        model.getPhantasmModel()
             .setValue(attributeValue,
                       WorkspacePresentation.stripQuotes(wsp.getWorkspaceDefinition().uri.getText()));
        loadWorkspace();
        return workspace;
    }

    private Product createWorkspaceProduct() {
        Token description = wsp.getWorkspaceDefinition().description;
        Product workspaceProduct = model.records()
                                        .newProduct(WorkspacePresentation.stripQuotes(wsp.getWorkspaceDefinition().name.getText()),
                                                    description == null ? null
                                                                        : WorkspacePresentation.stripQuotes(description.getText()));
        workspaceProduct.setId(uuid);
        workspaceProduct.setVersion(-1);
        workspaceProduct.insert();
        return workspaceProduct;
    }

    private void defineFacets() {
        defineFacets(model.getAgencyModel(), wsp.getAgencyFacets());
        defineFacets(model.getAttributeModel(), wsp.getAttributeFacets());
        defineFacets(model.getIntervalModel(), wsp.getIntervalFacets());
        defineFacets(model.getLocationModel(), wsp.getLocationFacets());
        defineFacets(model.getProductModel(), wsp.getProductFacets());
        defineFacets(model.getRelationshipModel(), wsp.getRelationshipFacets());
        defineFacets(model.getStatusCodeModel(), wsp.getStatusCodeFacets());
        defineFacets(model.getUnitModel(), wsp.getUnitFacets());
    }

    private void defineFacets(ExistentialModel<? extends ExistentialRuleform> networkedModel,
                              List<FacetContext> facets) {
        for (FacetContext facet : facets) {
            if (facet.classification.namespace == null) {
                if (scope.lookup(facet.classification.member.getText()) == null) {
                    ExistentialRecord erf = (ExistentialRecord) networkedModel.create(facet.name == null ? facet.classification.member.getText()
                                                                                                         : WorkspacePresentation.stripQuotes(facet.name.getText()),
                                                                                      facet.description == null ? null
                                                                                                                : WorkspacePresentation.stripQuotes(facet.description.getText()));
                    erf.insert();
                    workspace.put(facet.classification.member.getText(), erf);
                }
            }
            findOrCreateFacet(facet);
        }
    }

    private FacetRecord findOrCreateFacet(FacetContext facet) {
        Relationship classifier = model.records()
                                       .resolve(resolve(facet.classifier));
        ExistentialRuleform classification = model.records()
                                                  .resolve(resolve(facet.classification));

        FacetRecord authorization = model.getPhantasmModel()
                                         .getFacetDeclaration(classifier,
                                                              classification);
        if (authorization != null) {
            return authorization;
        }
        authorization = model.records()
                             .newFacet();
        authorization.setClassifier(classifier.getId());
        authorization.setClassification(classification.getId());
        if (facet.name != null) {
            authorization.setName(WorkspacePresentation.stripQuotes(facet.name.getText()));
        } else {
            authorization.setName(model.records()
                                       .resolve(authorization.getClassification())
                                       .getName());
        }
        if (facet.description != null) {
            authorization.setNotes(WorkspacePresentation.stripQuotes(facet.name.getText()));
        }
        authorization.insert();
        workspace.add(authorization);
        return authorization;
    }

    private Product getWorkspaceProduct() {
        return model.records()
                    .resolve(uuid);
    }

    private void load(ExistentialDomain domain,
                      List<AttributedExistentialRuleformContext> existentials) {
        for (AttributedExistentialRuleformContext ruleform : existentials) {
            ExistentialRecord record = model.records()
                                            .newExistential(domain,
                                                            WorkspacePresentation.stripQuotes(ruleform.existentialRuleform().name.getText()),
                                                            ruleform.existentialRuleform().description == null ? null
                                                                                                               : WorkspacePresentation.stripQuotes(ruleform.existentialRuleform().description.getText()));
            record.insert();
            workspace.put(ruleform.existentialRuleform().workspaceName.getText(),
                          record);
        }
    }

    private void loadAttributes() {
        for (AttributeRuleformContext ruleform : wsp.getAttributes()) {
            Attribute attr = model.records()
                                  .newAttribute(WorkspacePresentation.stripQuotes(ruleform.existentialRuleform().name.getText()),
                                                ruleform.existentialRuleform().description == null ? null
                                                                                                   : WorkspacePresentation.stripQuotes(ruleform.existentialRuleform().description.getText()));
            setValueType(attr, ruleform.valueType);
            attr.setIndexed(ruleform.indexed == null ? false
                                                     : ruleform.indexed.getText()
                                                                       .equals("true"));
            attr.setKeyed(ruleform.keyed == null ? false
                                                 : ruleform.keyed.getText()
                                                                 .equals("true"));
            attr.insert();
            workspace.put(ruleform.existentialRuleform().workspaceName.getText(),
                          attr);
            for (AttributeValueContext av : ruleform.attributeValue()) {
                ExistentialAttributeRecord ama = model.records()
                                                      .newExistentialAttribute();
                ama.setExistential(attr.getId());
                Attribute authorizedAttribute = model.records()
                                                     .resolve(resolve(av.attribute));
                ama.setAttribute(authorizedAttribute.getId());
                ama.setUpdatedBy(model.getCurrentPrincipal()
                                      .getPrincipal()
                                      .getId());
                ama.setSequenceNumber(Integer.parseInt(av.sequenceNumber.getText()));
                ama.insert();
                setValueFromString(authorizedAttribute, ama,
                                   WorkspacePresentation.stripQuotes(av.value.getText()));
                workspace.add(ama);
            }
        }
    }

    private void loadChildSequencing() {
        for (ChildSequencingContext seq : wsp.getChildSequencings()) {
            ChildSequencingAuthorizationRecord auth = model.records()
                                                           .newChildSequencingAuthorization(resolve(seq.parent),
                                                                                            resolve(seq.status),
                                                                                            resolve(seq.child),
                                                                                            resolve(seq.next));
            auth.insert();
            workspace.add(auth);
        }
    }

    private void loadEdges() {
        loadNetworks(wsp.getAgencyNetworks());
        loadNetworks(wsp.getAttributeNetworks());
        loadNetworks(wsp.getIntervalNetworks());
        loadNetworks(wsp.getLocationNetworks());
        loadNetworks(wsp.getProductNetworks());
        loadNetworks(wsp.getRelationshipNetworks());
        loadNetworks(wsp.getStatusCodeNetworks());
        loadNetworks(wsp.getUnitNetworks());
    }

    private void loadExistentials() {
        load(ExistentialDomain.Agency, wsp.getAgencies());
        load(ExistentialDomain.Location, wsp.getLocations());
        load(ExistentialDomain.Product, wsp.getProducts());
        load(ExistentialDomain.Interval, wsp.getIntervals());
        load(ExistentialDomain.Unit, wsp.getUnits());
        loadRelationships();
        loadAttributes();
        loadStatusCodes();
        loadStatusCodeSequencings();
    }

    private void loadFacets() {

        defineFacets();
        loadFacets(wsp.getAgencyFacets());
        loadFacets(wsp.getAttributeFacets());
        loadFacets(wsp.getIntervalFacets());
        loadFacets(wsp.getLocationFacets());
        loadFacets(wsp.getProductFacets());
        loadFacets(wsp.getRelationshipFacets());
        loadFacets(wsp.getStatusCodeFacets());
        loadFacets(wsp.getUnitFacets());
    }

    private void loadFacets(List<FacetContext> facets) {
        for (FacetContext facet : facets) {
            FacetRecord authorization = findOrCreateFacet(facet);
            classifiedAttributes(facet, authorization);
            networkConstraints(facet, authorization);
        }
    }

    private void loadInferences() {
        for (EdgeContext edge : wsp.getInferences()) {
            NetworkInferenceRecord inference = model.records()
                                                    .newNetworkInference(resolve(edge.parent),
                                                                         resolve(edge.relationship),
                                                                         resolve(edge.child));
            inference.insert();
            workspace.add(inference);
        }
    }

    private void loadMetaprotocols() {
        for (MetaProtocolContext mpc : wsp.getMetaProtocols()) {
            MetaProtocolRecord metaProtocol = model.getJobModel()
                                                   .newInitializedMetaProtocol(model.records()
                                                                                    .resolve(resolve(mpc.service)));
            if (mpc.product != null) {
                metaProtocol.setProduct(resolve(mpc.product));
            }
            if (mpc.from != null) {
                metaProtocol.setDeliverFrom(resolve(mpc.from));
            }
            if (mpc.to != null) {
                metaProtocol.setDeliverTo(resolve(mpc.to));
            }
            if (mpc.quantityUnit != null) {
                metaProtocol.setQuantityUnit(resolve(mpc.quantityUnit));
            }
            if (mpc.requester != null) {
                metaProtocol.setRequester(resolve(mpc.requester));
            }
            if (mpc.assignTo != null) {
                metaProtocol.setAssignTo(resolve(mpc.assignTo));
            }
            if (mpc.match != null && mpc.match.getText()
                                              .equals("stop")) {
                metaProtocol.setStopOnMatch(true);
            }
            workspace.add(metaProtocol);
        }
    }

    private void loadNetworks(List<EdgeContext> edges) {
        for (EdgeContext edge : edges) {
            ExistentialRuleform parent = model.records()
                                              .resolve(resolve(edge.parent));
            Relationship relationship = model.records()
                                             .resolve(resolve(edge.relationship));
            ExistentialRuleform child = model.records()
                                             .resolve(resolve(edge.child));
            FacetRecord facet = model.getPhantasmModel()
                                     .getFacetDeclaration(relationship, child);
            if (facet != null) {
                model.getPhantasmModel()
                     .initialize(parent, facet, workspace);
            } else {
                Tuple<ExistentialNetworkRecord, ExistentialNetworkRecord> link = model.getPhantasmModel()
                                                                                      .link(parent,
                                                                                            relationship,
                                                                                            child);
                workspace.add(link.a);
                workspace.add(link.b);
            }
        }
    }

    private void loadParentSequencing() {
        for (ParentSequencingContext seq : wsp.getParentSequencings()) {
            ParentSequencingAuthorizationRecord auth = model.records()
                                                            .newParentSequencingAuthorization(resolve(seq.service),
                                                                                              resolve(seq.status),
                                                                                              resolve(seq.parent),
                                                                                              resolve(seq.next));
            auth.insert();
            workspace.add(auth);
        }
    }

    private void loadProtocols() {
        for (ProtocolContext pc : wsp.getProtocols()) {
            ProtocolRecord protocol = model.getJobModel()
                                           .newInitializedProtocol(model.records()
                                                                        .resolve(resolve(pc.matchJob().service)));
            if (pc.matchJob().product != null) {
                protocol.setProduct(resolve(pc.matchJob().product));
            }
            if (pc.matchJob().from != null) {
                protocol.setDeliverFrom(resolve(pc.matchJob().from));
            }
            if (pc.matchJob().to != null) {
                protocol.setDeliverTo(resolve(pc.matchJob().to));
            }
            if (pc.matchJob().quantity != null) {
                protocol.setQuantity(BigDecimal.valueOf(Long.parseLong(pc.matchJob().quantity.getText())));
            }
            if (pc.matchJob().quantityUnit != null) {
                protocol.setQuantityUnit(resolve(pc.matchJob().quantityUnit));
            }
            if (pc.matchJob().requester != null) {
                protocol.setRequester(resolve(pc.matchJob().requester));
            }
            if (pc.matchJob().assignTo != null) {
                protocol.setAssignTo(resolve(pc.matchJob().assignTo));
            }
            if (pc.matchJob().sequence != null) {
                protocol.setSequenceNumber(Integer.parseInt(pc.matchJob().sequence.getText()));
            }

            if (pc.childJob().service != null) {
                protocol.setChildService(resolve(pc.childJob().service));
            }
            if (pc.childJob().product != null) {
                protocol.setChildProduct(resolve(pc.childJob().product));
            }
            if (pc.childJob().from != null) {
                protocol.setChildDeliverFrom(resolve(pc.childJob().from));
            }
            if (pc.childJob().to != null) {
                protocol.setChildDeliverTo(resolve(pc.childJob().to));
            }
            if (pc.childJob().quantity != null) {
                protocol.setChildQuantity(BigDecimal.valueOf(Long.parseLong(pc.childJob().quantity.getText())));
            }
            if (pc.childJob().quantityUnit != null) {
                protocol.setChildQuantityUnit(resolve(pc.childJob().quantityUnit));
            }
            if (pc.childJob().assignTo != null) {
                protocol.setChildAssignTo(resolve(pc.childJob().assignTo));
            }
            workspace.add(protocol);
        }

    }

    private void loadRelationships() {
        for (RelationshipPairContext ctx : wsp.getRelationships()) {
            if (ctx.inverse == null) {
                Relationship rel = model.records()
                                        .newRelationship(WorkspacePresentation.stripQuotes(ctx.primary.existentialRuleform().name.getText()),
                                                         ctx.primary.existentialRuleform().description == null ? null
                                                                                                               : WorkspacePresentation.stripQuotes(ctx.primary.existentialRuleform().description.getText()));
                rel.setInverse(rel.getId());
                rel.insert();
                workspace.put(ctx.primary.existentialRuleform().workspaceName.getText(),
                              rel);
            } else {
                Tuple<Relationship, Relationship> relationships = model.records()
                                                                       .newRelationship(WorkspacePresentation.stripQuotes(ctx.primary.existentialRuleform().name.getText()),
                                                                                        ctx.primary.existentialRuleform().description == null ? null
                                                                                                                                              : WorkspacePresentation.stripQuotes(ctx.primary.existentialRuleform().description.getText()),

                                                                                        WorkspacePresentation.stripQuotes(ctx.inverse.existentialRuleform().name.getText()),
                                                                                        ctx.inverse.existentialRuleform().description == null ? null
                                                                                                                                              : WorkspacePresentation.stripQuotes(ctx.inverse.existentialRuleform().description.getText()));

                relationships.a.insert();
                relationships.b.insert();
                workspace.put(ctx.primary.existentialRuleform().workspaceName.getText(),
                              relationships.a);
                workspace.put(ctx.inverse.existentialRuleform().workspaceName.getText(),
                              relationships.b);
            }
        }
    }

    private void loadSelfSequencing() {
        for (SelfSequencingContext seq : wsp.getSelfSequencings()) {
            SelfSequencingAuthorizationRecord auth = model.records()
                                                          .newSelfSequencingAuthorization(resolve(seq.service),
                                                                                          resolve(seq.status),
                                                                                          resolve(seq.next));
            auth.insert();
            workspace.add(auth);
        }
    }

    private void loadSequencingAuths() {
        loadParentSequencing();
        loadSiblingSequencing();
        loadChildSequencing();
        loadSelfSequencing();
    }

    private void loadSiblingSequencing() {
        for (SiblingSequencingContext seq : wsp.getSiblingSequencings()) {
            SiblingSequencingAuthorizationRecord auth = model.records()
                                                             .newSiblingSequencingAuthorization(resolve(seq.parent),
                                                                                                resolve(seq.status),
                                                                                                resolve(seq.sibling),
                                                                                                resolve(seq.next));
            auth.insert();
            workspace.add(auth);
        }
    }

    private void loadStatusCodes() {
        List<AttributedExistentialRuleformContext> statusCodes = wsp.getStatusCodes();
        for (AttributedExistentialRuleformContext rf : statusCodes) {
            StatusCode ruleform = model.records()
                                       .newStatusCode(WorkspacePresentation.stripQuotes(rf.existentialRuleform().name.getText()),
                                                      rf.existentialRuleform().description == null ? null
                                                                                                   : WorkspacePresentation.stripQuotes(rf.existentialRuleform().description.getText()));
            ruleform.insert();
            workspace.put(rf.existentialRuleform().workspaceName.getText(),
                          ruleform);
        }
        defineFacets(model.getStatusCodeModel(), wsp.getStatusCodeFacets());
    }

    private void loadStatusCodeSequencings() {
        for (StatusCodeSequencingSetContext entry : wsp.getStatusCodeSequencings()) {
            Product service = model.records()
                                   .resolve(resolve(entry.service));
            for (SequencePairContext pair : entry.sequencePair()) {
                StatusCode parent = model.records()
                                         .resolve(resolve(pair.first));
                StatusCode child = model.records()
                                        .resolve(resolve(pair.second));
                StatusCodeSequencingRecord sequence = model.records()
                                                           .newStatusCodeSequencing(service,
                                                                                    parent,
                                                                                    child);
                sequence.insert();
                String key = String.format(STATUS_CODE_SEQUENCING_FORMAT,
                                           service.getName(), parent.getName(),
                                           child.getName());
                workspace.put(key, sequence);
            }
        }

    }

    private void loadWorkspace() {
        processImports();
        loadExistentials();
        loadFacets();
        loadEdges();
        loadSequencingAuths();
        loadInferences();
        loadProtocols();
        loadMetaprotocols();
    }

    private void networkConstraints(FacetContext facet,
                                    FacetRecord authorization) {
        NetworkConstraintsContext networkConstraints = facet.networkConstraints();
        if (networkConstraints == null) {
            return;
        }
        networkConstraints.constraint()
                          .forEach(constraint -> {
                              createNetworkAuth(facet, authorization,
                                                constraint);
                          });
    }

    private void processImports() {
        for (ImportedWorkspaceContext w : wsp.getImports()) {
            String uri = WorkspacePresentation.stripQuotes(w.uri.getText());
            UUID uuid = WorkspaceAccessor.uuidOf(uri);
            Product imported = model.records()
                                    .resolve(uuid);
            if (imported == null) {
                throw new IllegalStateException(String.format("the import is not found: %s:%s",
                                                              uuid, uri));
            }
            workspace.addImport(w.namespace.getText(), imported);
        }
    }

    private UUID resolve(QualifiedNameContext qualifiedName) {
        if (qualifiedName == null) {
            throw new IllegalArgumentException("Qualified name is null");
        }
        UUID id;
        if (qualifiedName.namespace != null) {
            return scope.lookupId(qualifiedName.namespace.getText(),
                                  qualifiedName.member.getText());
        } else if (qualifiedName.member.getText()
                                       .equals(THIS)) {
            return workspace.getDefiningProduct()
                            .getId();
        } else {
            id = scope.lookupId(qualifiedName.member.getText());
            if (id != null) {
                return id;
            }
        }
        throw new InvalidKeyException(String.format("Cannot resolve %s:%s",
                                                    qualifiedName.namespace == null ? ""
                                                                                    : qualifiedName.namespace.getText(),
                                                    qualifiedName.member.getText()));
    }

    private ExistentialRuleform resolveAnyEntity(String anyType) {
        switch (anyType) {
            case "*Agency":
                return model.getKernel()
                            .getAnyAgency();
            case "*Attribute":
                return model.getKernel()
                            .getAnyAttribute();
            case "*Interval":
                return model.getKernel()
                            .getAnyInterval();
            case "*Location":
                return model.getKernel()
                            .getAnyLocation();
            case "*Product":
                return model.getKernel()
                            .getAnyProduct();
            case "*Relationship":
                return model.getKernel()
                            .getAnyRelationship();
            case "*StatusCode":
                return model.getKernel()
                            .getAnyStatusCode();
            case "*Unit":
                return model.getKernel()
                            .getAnyUnit();
            default:
                throw new IllegalArgumentException(String.format("Invalid *Any type: %s",
                                                                 anyType));
        }
    }

    private void resolveChild(ConstraintContext constraint,
                              ExistentialNetworkAuthorizationRecord authorization) {
        if (constraint.anyType == null) {
            authorization.setChild(model.getPhantasmModel()
                                        .getFacetDeclaration(model.records()
                                                                  .resolve(resolve(constraint.authorizedRelationship)),
                                                             model.records()
                                                                  .resolve(resolve(constraint.authorizedParent)))
                                        .getId());
        } else {
            authorization.setChild(model.getPhantasmModel()
                                        .getFacetDeclaration(model.getKernel()
                                                                  .getAnyRelationship(),
                                                             model.records()
                                                                  .resolve(resolveAnyEntity(constraint.anyType.getText()).getId()))
                                        .getId());
        }
    }

    private void setValueFromString(Attribute authorizedAttribute,
                                    ExistentialAttributeAuthorizationRecord auth,
                                    String value) {
        switch (authorizedAttribute.getValueType()) {
            case Binary:
                model.getPhantasmModel()
                     .setValue(auth, value.getBytes());
                return;
            case Boolean:
                model.getPhantasmModel()
                     .setValue(auth, Boolean.valueOf(value));
                return;
            case Integer:
                model.getPhantasmModel()
                     .setValue(auth, Integer.parseInt(value));
                return;
            case Numeric:
                model.getPhantasmModel()
                     .setValue(auth, BigDecimal.valueOf(Long.parseLong(value)));
                return;
            case Text:
                model.getPhantasmModel()
                     .setValue(auth, value);
                return;
            case JSON:
                model.getPhantasmModel()
                     .setValue(auth, value);
                return;
            case Timestamp:
                throw new UnsupportedOperationException("Timestamps are a PITA");
            default:
                throw new IllegalStateException(String.format("Invalid value type: %s",
                                                              authorizedAttribute.getValueType()));
        }
    }

    private void setValueFromString(Attribute authorizedAttribute,
                                    ExistentialAttributeRecord attributeValue,
                                    String value) {
        switch (authorizedAttribute.getValueType()) {
            case Binary:
                model.getPhantasmModel()
                     .setValue(attributeValue, value.getBytes());
                return;
            case Boolean:
                model.getPhantasmModel()
                     .setValue(attributeValue, Boolean.valueOf(value));
                return;
            case Integer:
                model.getPhantasmModel()
                     .setValue(attributeValue, Integer.parseInt(value));
                return;
            case Numeric:
                model.getPhantasmModel()
                     .setValue(attributeValue,
                               BigDecimal.valueOf(Long.parseLong(value)));
                return;
            case Text:
                model.getPhantasmModel()
                     .setValue(attributeValue, value);
                return;
            case JSON:
                model.getPhantasmModel()
                     .setValue(attributeValue, value);
                return;
            case Timestamp:
                throw new UnsupportedOperationException("Timestamps are a PITA");
            default:
                throw new IllegalStateException(String.format("Invalid value type: %s",
                                                              authorizedAttribute.getValueType()));
        }
    }

    private void setValueFromString(Attribute authorizedAttribute,
                                    ExistentialNetworkAttributeAuthorizationRecord auth,
                                    String value) {

        switch (authorizedAttribute.getValueType()) {
            case Binary:
                model.getPhantasmModel()
                     .setValue(auth, value.getBytes());
                return;
            case Boolean:
                model.getPhantasmModel()
                     .setValue(auth, Boolean.valueOf(value));
                return;
            case Integer:
                model.getPhantasmModel()
                     .setValue(auth, Integer.parseInt(value));
                return;
            case Numeric:
                model.getPhantasmModel()
                     .setValue(auth, BigDecimal.valueOf(Long.parseLong(value)));
                return;
            case Text:
                model.getPhantasmModel()
                     .setValue(auth, value);
                return;
            case JSON:
                model.getPhantasmModel()
                     .setValue(auth, value);
                return;
            case Timestamp:
                throw new UnsupportedOperationException("Timestamps are a PITA");
            default:
                throw new IllegalStateException(String.format("Invalid value type: %s",
                                                              authorizedAttribute.getValueType()));
        }
    }

    /**
     * @param attr
     * @param valueType
     */
    private void setValueType(Attribute attr, Token valueType) {
        switch (valueType.getText()) {
            case "int":
                attr.setValueType(ValueType.Integer);
                return;
            case "bool":
                attr.setValueType(ValueType.Boolean);
                return;
            case "text":
                attr.setValueType(ValueType.Text);
                return;
            case "binary":
                attr.setValueType(ValueType.Binary);
                return;
            case "numeric":
                attr.setValueType(ValueType.Numeric);
                return;
            case "timestamp":
                attr.setValueType(ValueType.Timestamp);
                return;
            case "json":
                attr.setValueType(ValueType.JSON);
                return;
            default:
                throw new IllegalArgumentException(String.format("Invalid attribute value type: %s for %s",
                                                                 valueType.getText(),
                                                                 attr));
        }
    }
}
