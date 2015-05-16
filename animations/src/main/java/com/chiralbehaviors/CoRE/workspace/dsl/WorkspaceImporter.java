/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
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

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.UUID;

import javax.management.openmbean.InvalidKeyException;
import javax.persistence.EntityManager;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyAttributeAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.agency.AgencyNetworkAuthorization;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeMetaAttribute;
import com.chiralbehaviors.CoRE.attribute.AttributeMetaAttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.AttributeNetwork;
import com.chiralbehaviors.CoRE.attribute.AttributeNetworkAuthorization;
import com.chiralbehaviors.CoRE.attribute.ValueType;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.attribute.unit.UnitAttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.unit.UnitNetwork;
import com.chiralbehaviors.CoRE.attribute.unit.UnitNetworkAuthorization;
import com.chiralbehaviors.CoRE.job.MetaProtocol;
import com.chiralbehaviors.CoRE.job.ProductChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.job.ProductParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.job.ProductSelfSequencingAuthorization;
import com.chiralbehaviors.CoRE.job.ProductSiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.job.Protocol;
import com.chiralbehaviors.CoRE.job.status.StatusCode;
import com.chiralbehaviors.CoRE.job.status.StatusCodeAttributeAuthorization;
import com.chiralbehaviors.CoRE.job.status.StatusCodeNetwork;
import com.chiralbehaviors.CoRE.job.status.StatusCodeNetworkAuthorization;
import com.chiralbehaviors.CoRE.job.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationAttributeAuthorization;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.location.LocationNetworkAuthorization;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace;
import com.chiralbehaviors.CoRE.meta.workspace.Workspace;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.network.NetworkInference;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductAttributeAuthorization;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.chiralbehaviors.CoRE.product.ProductNetworkAuthorization;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.relationship.RelationshipAttributeAuthorization;
import com.chiralbehaviors.CoRE.relationship.RelationshipNetwork;
import com.chiralbehaviors.CoRE.relationship.RelationshipNetworkAuthorization;
import com.chiralbehaviors.CoRE.time.Interval;
import com.chiralbehaviors.CoRE.time.IntervalAttributeAuthorization;
import com.chiralbehaviors.CoRE.time.IntervalNetwork;
import com.chiralbehaviors.CoRE.time.IntervalNetworkAuthorization;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.AttributeRuleformContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.AttributeValueContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.AttributedExistentialRuleformContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ChildSequencingContext;
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
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.UnitContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.WorkspaceContext;

/**
 * @author hparry
 *
 */
public class WorkspaceImporter {

    private static final String STATUS_CODE_SEQUENCING_FORMAT = "%s: %s -> %s";
    private static final String THIS                          = "this";

    public static WorkspaceImporter createWorkspace(InputStream source,
                                                    Model model)
                                                                throws IOException {
        WorkspaceLexer l = new WorkspaceLexer(new ANTLRInputStream(source));
        WorkspaceParser p = new WorkspaceParser(new CommonTokenStream(l));
        p.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer,
                                    Object offendingSymbol, int line,
                                    int charPositionInLine, String msg,
                                    RecognitionException e) {
                throw new IllegalStateException("failed to parse at line "
                                                + line + " due to " + msg, e);
            }
        });
        WorkspaceContext ctx = p.workspace();

        WorkspaceImporter importer = new WorkspaceImporter(
                                                           new WorkspacePresentation(
                                                                                     ctx),
                                                           model);
        importer.loadWorkspace();
        return importer;
    }

    private final EntityManager         em;
    private final Model                 model;
    private WorkspaceScope              scope;
    private EditableWorkspace           workspace;
    private final WorkspacePresentation wsp;

    public WorkspaceImporter(WorkspacePresentation wsp, Model model) {
        this.wsp = wsp;
        this.model = model;
        this.em = model.getEntityManager();
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public Workspace loadWorkspace() {
        scope = model.getWorkspaceModel().createWorkspace(createWorkspaceProduct(),
                                                          model.getCurrentPrincipal().getPrincipal());
        workspace = (EditableWorkspace) scope.getWorkspace();
        processImports();
        loadRelationships();
        loadAgencies();
        loadAttributes();
        loadLocations();
        loadProducts();
        loadStatusCodes();
        loadStatusCodeSequencings();
        loadUnits();
        loadIntervals();
        loadEdges();
        loadFacets();
        loadSequencingAuths();
        loadInferences();
        loadProtocols();
        loadMetaprotocols();
        return workspace;
    }

    public void setScope(WorkspaceScope scope) {
        this.scope = scope;
    }

    /**
     * @param facet
     */
    private void agencyAuthorizations(FacetContext facet) {
        // TODO Auto-generated method stub

    }

    private void agencyFacets() {
        for (FacetContext facet : wsp.getAgencyFacets()) {
            classifiedAgencyAttributes(facet);
            agencyNetworkConstraints(facet);
            agencyAuthorizations(facet);
        }
    }

    /**
     * @param facet
     */
    private void agencyNetworkConstraints(FacetContext facet) {
        NetworkConstraintsContext networkConstraints = facet.networkConstraints();
        if (networkConstraints == null) {
            return;
        }
        networkConstraints.constraint().forEach(constraint -> {
                                                    AgencyNetworkAuthorization authorization = new AgencyNetworkAuthorization(
                                                                                                                              model.getCurrentPrincipal().getPrincipal());
                                                    authorization.setClassifier(resolve(facet.classifier));
                                                    authorization.setClassification(resolve(facet.classification));
                                                    authorization.setChildRelationship(resolve(constraint.childRelationship));
                                                    authorization.setAuthorizedParent(resolve(constraint.authorizedParent));
                                                    authorization.setAuthorizedRelationship(resolve(constraint.authorizedRelationship));
                                                    em.persist(authorization);
                                                });
    }

    private void attributeFacets() {
        for (FacetContext facet : wsp.getAttributeFacets()) {
            classifiedMetaAttributes(facet);
            attributeNetworkConstraints(facet);
        }
    }

    private void attributeNetworkConstraints(FacetContext facet) {
        NetworkConstraintsContext networkConstraints = facet.networkConstraints();
        if (networkConstraints == null) {
            return;
        }
        networkConstraints.constraint().forEach(constraint -> {
                                                    AttributeNetworkAuthorization authorization = new AttributeNetworkAuthorization(
                                                                                                                                    model.getCurrentPrincipal().getPrincipal());
                                                    authorization.setClassifier(resolve(facet.classifier));
                                                    authorization.setClassification(resolve(facet.classification));
                                                    authorization.setChildRelationship(resolve(constraint.childRelationship));
                                                    authorization.setAuthorizedParent(resolve(constraint.authorizedParent));
                                                    authorization.setAuthorizedRelationship(resolve(constraint.authorizedRelationship));
                                                    em.persist(authorization);
                                                });
    }

    private void classifiedAgencyAttributes(FacetContext facet) {
        AgencyNetworkAuthorization authorization = new AgencyNetworkAuthorization(
                                                                                  model.getCurrentPrincipal().getPrincipal());
        authorization.setClassification(resolve(facet.classification));
        authorization.setClassifier(resolve(facet.classifier));
        model.getEntityManager().persist(authorization);
        workspace.add(authorization);
        facet.classifiedAttributes().qualifiedName().forEach(attribute -> {
                                                                 AgencyAttributeAuthorization auth = new AgencyAttributeAuthorization(
                                                                                                                                      resolve(attribute),
                                                                                                                                      model.getCurrentPrincipal().getPrincipal());
                                                                 auth.setNetworkAuthorization(authorization);
                                                                 model.getEntityManager().persist(auth);
                                                                 workspace.add(auth);

                                                             });
    }

    private void classifiedIntervalAttributes(FacetContext facet) {
        IntervalNetworkAuthorization authorization = new IntervalNetworkAuthorization(
                                                                                      model.getCurrentPrincipal().getPrincipal());
        authorization.setClassification(resolve(facet.classification));
        authorization.setClassifier(resolve(facet.classifier));
        model.getEntityManager().persist(authorization);
        workspace.add(authorization);
        facet.classifiedAttributes().qualifiedName().forEach(attribute -> {
                                                                 IntervalAttributeAuthorization auth = new IntervalAttributeAuthorization(
                                                                                                                                          resolve(attribute),
                                                                                                                                          model.getCurrentPrincipal().getPrincipal());
                                                                 auth.setNetworkAuthorization(authorization);
                                                                 model.getEntityManager().persist(auth);
                                                                 workspace.add(auth);

                                                             });
    }

    private void classifiedLocationAttributes(FacetContext facet) {
        LocationNetworkAuthorization authorization = new LocationNetworkAuthorization(
                                                                                      model.getCurrentPrincipal().getPrincipal());
        authorization.setClassification(resolve(facet.classification));
        authorization.setClassifier(resolve(facet.classifier));
        model.getEntityManager().persist(authorization);
        workspace.add(authorization);
        facet.classifiedAttributes().qualifiedName().forEach(attribute -> {
                                                                 LocationAttributeAuthorization auth = new LocationAttributeAuthorization(
                                                                                                                                          resolve(attribute),
                                                                                                                                          model.getCurrentPrincipal().getPrincipal());
                                                                 auth.setNetworkAuthorization(authorization);
                                                                 model.getEntityManager().persist(auth);
                                                                 workspace.add(auth);

                                                             });
    }

    private void classifiedMetaAttributes(FacetContext facet) {
        AttributeNetworkAuthorization authorization = new AttributeNetworkAuthorization(
                                                                                        model.getCurrentPrincipal().getPrincipal());
        authorization.setClassification(resolve(facet.classification));
        authorization.setClassifier(resolve(facet.classifier));
        model.getEntityManager().persist(authorization);
        workspace.add(authorization);
        facet.classifiedAttributes().qualifiedName().forEach(attribute -> {
                                                                 AttributeMetaAttributeAuthorization auth = new AttributeMetaAttributeAuthorization(
                                                                                                                                                    resolve(attribute),
                                                                                                                                                    model.getCurrentPrincipal().getPrincipal());
                                                                 auth.setNetworkAuthorization(authorization);
                                                                 model.getEntityManager().persist(auth);
                                                                 workspace.add(auth);

                                                             });
    }

    private void classifiedProductAttributes(FacetContext facet) {
        ProductNetworkAuthorization authorization = new ProductNetworkAuthorization(
                                                                                    model.getCurrentPrincipal().getPrincipal());
        authorization.setClassification(resolve(facet.classification));
        authorization.setClassifier(resolve(facet.classifier));
        model.getEntityManager().persist(authorization);
        workspace.add(authorization);
        facet.classifiedAttributes().qualifiedName().forEach(attribute -> {
                                                                 ProductAttributeAuthorization auth = new ProductAttributeAuthorization(
                                                                                                                                        resolve(attribute),
                                                                                                                                        model.getCurrentPrincipal().getPrincipal());
                                                                 auth.setNetworkAuthorization(authorization);
                                                                 model.getEntityManager().persist(auth);
                                                                 workspace.add(auth);

                                                             });
    }

    private void classifiedRelationshipAttributes(FacetContext facet) {
        RelationshipNetworkAuthorization authorization = new RelationshipNetworkAuthorization(
                                                                                              model.getCurrentPrincipal().getPrincipal());
        authorization.setClassification(resolve(facet.classification));
        authorization.setClassifier(resolve(facet.classifier));
        model.getEntityManager().persist(authorization);
        workspace.add(authorization);
        facet.classifiedAttributes().qualifiedName().forEach(attribute -> {
                                                                 RelationshipAttributeAuthorization auth = new RelationshipAttributeAuthorization(
                                                                                                                                                  resolve(attribute),
                                                                                                                                                  model.getCurrentPrincipal().getPrincipal());
                                                                 auth.setNetworkAuthorization(authorization);
                                                                 model.getEntityManager().persist(auth);
                                                                 workspace.add(auth);

                                                             });
    }

    private void classifiedStatusCodeAttributes(FacetContext facet) {
        StatusCodeNetworkAuthorization authorization = new StatusCodeNetworkAuthorization(
                                                                                          model.getCurrentPrincipal().getPrincipal());
        authorization.setClassification(resolve(facet.classification));
        authorization.setClassifier(resolve(facet.classifier));
        model.getEntityManager().persist(authorization);
        workspace.add(authorization);
        facet.classifiedAttributes().qualifiedName().forEach(attribute -> {
                                                                 StatusCodeAttributeAuthorization auth = new StatusCodeAttributeAuthorization(
                                                                                                                                              resolve(attribute),
                                                                                                                                              model.getCurrentPrincipal().getPrincipal());
                                                                 auth.setNetworkAuthorization(authorization);
                                                                 model.getEntityManager().persist(auth);
                                                                 workspace.add(auth);

                                                             });
    }

    private Product createWorkspaceProduct() {
        String uri = stripQuotes(wsp.getWorkspaceDefinition().uri.getText());
        Token description = wsp.getWorkspaceDefinition().description;
        Product workspaceProduct = new Product(
                                               stripQuotes(wsp.getWorkspaceDefinition().name.getText()),
                                               description == null ? null
                                                                  : stripQuotes(description.getText()),
                                               model.getCurrentPrincipal().getPrincipal());
        workspaceProduct.setId(Workspace.uuidOf(uri));
        em.persist(workspaceProduct);
        return workspaceProduct;
    }

    private void intervalFacets() {
        for (FacetContext facet : wsp.getIntervalFacets()) {
            classifiedIntervalAttributes(facet);
            intervalNetworkConstraints(facet);
        }
    }

    private void intervalNetworkConstraints(FacetContext facet) {
        NetworkConstraintsContext networkConstraints = facet.networkConstraints();
        if (networkConstraints == null) {
            return;
        }
        networkConstraints.constraint().forEach(constraint -> {
                                                    IntervalNetworkAuthorization authorization = new IntervalNetworkAuthorization(
                                                                                                                                  model.getCurrentPrincipal().getPrincipal());
                                                    authorization.setClassifier(resolve(facet.classifier));
                                                    authorization.setClassification(resolve(facet.classification));
                                                    authorization.setChildRelationship(resolve(constraint.childRelationship));
                                                    authorization.setAuthorizedParent(resolve(constraint.authorizedParent));
                                                    authorization.setAuthorizedRelationship(resolve(constraint.authorizedRelationship));
                                                    em.persist(authorization);
                                                });
    }

    private void loadAgencies() {
        for (AttributedExistentialRuleformContext ruleform : wsp.getAgencies()) {
            Agency agency = new Agency(
                                       stripQuotes(ruleform.existentialRuleform().name.getText()),
                                       ruleform.existentialRuleform().description == null ? null
                                                                                         : stripQuotes(ruleform.existentialRuleform().description.getText()),
                                       model.getCurrentPrincipal().getPrincipal());
            em.persist(agency);
            workspace.put(ruleform.existentialRuleform().workspaceName.getText(),
                          agency);
        }

    }

    private void loadAgencyNetworks() {
        for (EdgeContext edge : wsp.getAgencyNetworks()) {
            AgencyNetwork network = model.getAgencyModel().link(resolve(edge.parent),
                                                                resolve(edge.relationship),
                                                                resolve(edge.child),
                                                                model.getCurrentPrincipal().getPrincipal());
            workspace.add(network);
        }
    }

    private void loadAttributeNetworks() {
        for (EdgeContext edge : wsp.getAttributeNetworks()) {
            AttributeNetwork network = model.getAttributeModel().link(resolve(edge.parent),
                                                                      resolve(edge.relationship),
                                                                      resolve(edge.child),
                                                                      model.getCurrentPrincipal().getPrincipal());
            workspace.add(network);
        }
    }

    private void loadAttributes() {
        for (AttributeRuleformContext ruleform : wsp.getAttributes()) {
            Attribute attr = new Attribute(
                                           stripQuotes(ruleform.existentialRuleform().name.getText()),
                                           ruleform.existentialRuleform().description == null ? null
                                                                                             : stripQuotes(ruleform.existentialRuleform().description.getText()),
                                           model.getCurrentPrincipal().getPrincipal());
            setValueType(attr, ruleform.valueType);
            attr.setIndexed(ruleform.indexed == null ? false
                                                    : ruleform.indexed.getText().equals("true"));
            attr.setKeyed(ruleform.keyed == null ? false
                                                : ruleform.keyed.getText().equals("true"));
            em.persist(attr);
            workspace.put(ruleform.existentialRuleform().workspaceName.getText(),
                          attr);
            for (AttributeValueContext av : ruleform.attributeValue()) {
                AttributeMetaAttribute ama = new AttributeMetaAttribute();
                ama.setAttribute(attr);
                Attribute metaAttribute = resolve(av.attribute);
                ama.setMetaAttribute(metaAttribute);
                ama.setUpdatedBy(model.getCurrentPrincipal().getPrincipal());
                ama.setSequenceNumber(Integer.parseInt(av.sequenceNumber.getText()));
                ama.setValueFromString(stripQuotes(av.value.getText()));
                em.persist(ama);
            }
        }
    }

    private void loadChildSequencing() {
        for (ChildSequencingContext seq : wsp.getChildSequencings()) {
            ProductChildSequencingAuthorization auth = new ProductChildSequencingAuthorization(
                                                                                               resolve(seq.parent),
                                                                                               resolve(seq.status),
                                                                                               resolve(seq.child),
                                                                                               resolve(seq.next),
                                                                                               model.getCurrentPrincipal().getPrincipal());
            em.persist(auth);
            workspace.add(auth);
        }
    }

    private void loadEdges() {
        loadAgencyNetworks();
        loadAttributeNetworks();
        loadIntervalNetworks();
        loadLocationNetworks();
        loadProductNetworks();
        loadRelationshipNetworks();
        loadStatusCodeNetworks();
        loadUnitNetworks();
    }

    private void loadFacets() {
        agencyFacets();
        attributeFacets();
        intervalFacets();
        locationFacets();
        productFacets();
        relationshipFacets();
        statusCodeFacets();
        unitFacets();
    }

    private void loadInferences() {
        for (EdgeContext edge : wsp.getInferences()) {
            NetworkInference inference = new NetworkInference(
                                                              resolve(edge.parent),
                                                              resolve(edge.relationship),
                                                              resolve(edge.child),
                                                              model.getCurrentPrincipal().getPrincipal());
            em.persist(inference);
            workspace.add(inference);
        }
    }

    private void loadIntervalNetworks() {
        for (EdgeContext edge : wsp.getIntervalNetworks()) {
            IntervalNetwork network = model.getIntervalModel().link(resolve(edge.parent),
                                                                    resolve(edge.relationship),
                                                                    resolve(edge.child),
                                                                    model.getCurrentPrincipal().getPrincipal());
            workspace.add(network);
        }
    }

    private void loadIntervals() {
        for (AttributedExistentialRuleformContext rf : wsp.getIntervals()) {
            Interval ruleform = new Interval(
                                             stripQuotes(rf.existentialRuleform().name.getText()),
                                             rf.existentialRuleform().description == null ? null
                                                                                         : stripQuotes(rf.existentialRuleform().description.getText()),
                                             model.getCurrentPrincipal().getPrincipal());
            em.persist(ruleform);
            workspace.put(rf.existentialRuleform().workspaceName.getText(),
                          ruleform);
        }
    }

    private void loadLocationNetworks() {
        for (EdgeContext edge : wsp.getLocationNetworks()) {
            LocationNetwork network = model.getLocationModel().link(resolve(edge.parent),
                                                                    resolve(edge.relationship),
                                                                    resolve(edge.child),
                                                                    model.getCurrentPrincipal().getPrincipal());
            workspace.add(network);
        }
    }

    private void loadLocations() {
        for (AttributedExistentialRuleformContext rf : wsp.getLocations()) {
            Location ruleform = new Location(
                                             stripQuotes(rf.existentialRuleform().name.getText()),
                                             rf.existentialRuleform().description == null ? null
                                                                                         : stripQuotes(rf.existentialRuleform().description.getText()),
                                             model.getCurrentPrincipal().getPrincipal());
            em.persist(ruleform);
            workspace.put(rf.existentialRuleform().workspaceName.getText(),
                          ruleform);
        }
    }

    private void loadMetaprotocols() {
        for (MetaProtocolContext mpc : wsp.getMetaProtocols()) {
            MetaProtocol metaProtocol = model.getJobModel().newInitializedMetaProtocol(resolve(mpc.service),
                                                                                       model.getCurrentPrincipal().getPrincipal());
            if (mpc.product != null)
                metaProtocol.setProduct(resolve(mpc.product));
            if (mpc.from != null)
                metaProtocol.setDeliverFrom(resolve(mpc.from));
            if (mpc.to != null)
                metaProtocol.setDeliverTo(resolve(mpc.to));
            if (mpc.quantityUnit != null)
                metaProtocol.setQuantityUnit(resolve(mpc.quantityUnit));
            if (mpc.requester != null)
                metaProtocol.setRequester(resolve(mpc.requester));
            if (mpc.assignTo != null)
                metaProtocol.setAssignTo(resolve(mpc.assignTo));
            if (mpc.match != null && mpc.match.getText().equals("stop")) {
                metaProtocol.setStopOnMatch(true);
            }
        }
    }

    private void loadParentSequencing() {
        for (ParentSequencingContext seq : wsp.getParentSequencings()) {
            ProductParentSequencingAuthorization auth = new ProductParentSequencingAuthorization(
                                                                                                 resolve(seq.service),
                                                                                                 resolve(seq.status),
                                                                                                 resolve(seq.parent),
                                                                                                 resolve(seq.next),
                                                                                                 model.getCurrentPrincipal().getPrincipal());
            em.persist(auth);
            workspace.add(auth);
        }
    }

    private void loadProductNetworks() {
        for (EdgeContext edge : wsp.getProductNetworks()) {
            ProductNetwork network = model.getProductModel().link(resolve(edge.parent),
                                                                  resolve(edge.relationship),
                                                                  resolve(edge.child),
                                                                  model.getCurrentPrincipal().getPrincipal());
            workspace.add(network);
        }
    }

    private void loadProducts() {
        for (AttributedExistentialRuleformContext rf : wsp.getProducts()) {
            Product ruleform = new Product(
                                           stripQuotes(rf.existentialRuleform().name.getText()),
                                           rf.existentialRuleform().description == null ? null
                                                                                       : stripQuotes(rf.existentialRuleform().description.getText()),
                                           model.getCurrentPrincipal().getPrincipal());
            em.persist(ruleform);
            workspace.put(rf.existentialRuleform().workspaceName.getText(),
                          ruleform);
        }
    }

    private void loadProtocols() {
        for (ProtocolContext pc : wsp.getProtocols()) {
            Protocol protocol = model.getJobModel().newInitializedProtocol(resolve(pc.matchJob().service),
                                                                           model.getCurrentPrincipal().getPrincipal());
            if (pc.matchJob().product != null)
                protocol.setProduct(resolve(pc.matchJob().product));
            if (pc.matchJob().from != null)
                protocol.setDeliverFrom(resolve(pc.matchJob().from));
            if (pc.matchJob().to != null)
                protocol.setDeliverTo(resolve(pc.matchJob().to));
            if (pc.matchJob().quantity != null)
                protocol.setQuantity(BigDecimal.valueOf(Long.parseLong(pc.matchJob().quantity.getText())));
            if (pc.matchJob().quantityUnit != null)
                protocol.setQuantityUnit(resolve(pc.matchJob().quantityUnit));
            if (pc.matchJob().requester != null)
                protocol.setRequester(resolve(pc.matchJob().requester));
            if (pc.matchJob().assignTo != null)
                protocol.setAssignTo(resolve(pc.matchJob().assignTo));
            if (pc.matchJob().sequence != null)
                protocol.setSequenceNumber(Integer.parseInt(pc.matchJob().sequence.getText()));

            if (pc.childJob().service != null)
                protocol.setChildService(resolve(pc.childJob().service));
            if (pc.childJob().product != null)
                protocol.setChildProduct(resolve(pc.childJob().product));
            if (pc.childJob().from != null)
                protocol.setChildDeliverFrom(resolve(pc.childJob().from));
            if (pc.childJob().to != null)
                protocol.setChildDeliverTo(resolve(pc.childJob().to));
            if (pc.childJob().quantity != null)
                protocol.setChildQuantity(BigDecimal.valueOf(Long.parseLong(pc.childJob().quantity.getText())));
            if (pc.childJob().quantityUnit != null)
                protocol.setChildQuantityUnit(resolve(pc.childJob().quantityUnit));
            if (pc.childJob().assignTo != null)
                protocol.setChildAssignTo(resolve(pc.childJob().assignTo));
            workspace.add(protocol);
        }

    }

    private void loadRelationshipNetworks() {
        for (EdgeContext edge : wsp.getRelationshipNetworks()) {
            RelationshipNetwork network = model.getRelationshipModel().link(resolve(edge.parent),
                                                                            resolve(edge.relationship),
                                                                            resolve(edge.child),
                                                                            model.getCurrentPrincipal().getPrincipal());
            workspace.add(network);
        }
    }

    private void loadRelationships() {
        for (RelationshipPairContext ctx : wsp.getRelationships()) {
            Relationship relA = model.getRelationshipModel().create(stripQuotes(ctx.primary.existentialRuleform().name.getText()),
                                                                    ctx.primary.existentialRuleform().description == null ? null
                                                                                                                         : stripQuotes(ctx.primary.existentialRuleform().description.getText()),

                                                                    stripQuotes(ctx.inverse.existentialRuleform().name.getText()),
                                                                    ctx.inverse.existentialRuleform().description == null ? null
                                                                                                                         : stripQuotes(ctx.inverse.existentialRuleform().description.getText()));
            workspace.put(ctx.primary.existentialRuleform().workspaceName.getText(),
                          relA);
            workspace.put(ctx.inverse.existentialRuleform().workspaceName.getText(),
                          relA.getInverse());
        }

    }

    private void loadSelfSequencing() {
        for (SelfSequencingContext seq : wsp.getSelfSequencings()) {
            ProductSelfSequencingAuthorization auth = new ProductSelfSequencingAuthorization(
                                                                                             resolve(seq.service),
                                                                                             resolve(seq.status),
                                                                                             resolve(seq.next),
                                                                                             model.getCurrentPrincipal().getPrincipal());
            em.persist(auth);
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
            ProductSiblingSequencingAuthorization auth = new ProductSiblingSequencingAuthorization(
                                                                                                   resolve(seq.parent),
                                                                                                   resolve(seq.status),
                                                                                                   resolve(seq.sibling),
                                                                                                   resolve(seq.next),
                                                                                                   model.getCurrentPrincipal().getPrincipal());
            em.persist(auth);
            workspace.add(auth);
        }
    }

    private void loadStatusCodeNetworks() {
        for (EdgeContext edge : wsp.getStatusCodeNetworks()) {
            StatusCodeNetwork network = model.getStatusCodeModel().link(resolve(edge.parent),
                                                                        resolve(edge.relationship),
                                                                        resolve(edge.child),
                                                                        model.getCurrentPrincipal().getPrincipal());
            workspace.add(network);
        }
    }

    private void loadStatusCodes() {
        for (AttributedExistentialRuleformContext rf : wsp.getStatusCodes()) {
            StatusCode ruleform = new StatusCode(
                                                 stripQuotes(rf.existentialRuleform().name.getText()),
                                                 rf.existentialRuleform().description == null ? null
                                                                                             : stripQuotes(rf.existentialRuleform().description.getText()),
                                                 model.getCurrentPrincipal().getPrincipal());
            em.persist(ruleform);
            workspace.put(rf.existentialRuleform().workspaceName.getText(),
                          ruleform);
        }
    }

    private void loadStatusCodeSequencings() {
        for (StatusCodeSequencingSetContext entry : wsp.getStatusCodeSequencings()) {
            Product service = resolve(entry.service);
            for (SequencePairContext pair : entry.sequencePair()) {
                StatusCode first = resolve(pair.first);
                StatusCode second = resolve(pair.second);
                StatusCodeSequencing sequence = new StatusCodeSequencing(
                                                                         service,
                                                                         first,
                                                                         second,
                                                                         service.getUpdatedBy());
                em.persist(sequence);
                String key = String.format(STATUS_CODE_SEQUENCING_FORMAT,
                                           service.getName(), first.getName(),
                                           second.getName());
                workspace.put(key, sequence);
            }
        }

    }

    private void loadUnitNetworks() {
        for (EdgeContext edge : wsp.getUnitNetworks()) {
            UnitNetwork network = model.getUnitModel().link(resolve(edge.parent),
                                                            resolve(edge.relationship),
                                                            resolve(edge.child),
                                                            model.getCurrentPrincipal().getPrincipal());
            workspace.add(network);
        }
    }

    private void loadUnits() {
        for (UnitContext unit : wsp.getUnits()) {
            Token description = unit.existentialRuleform().description;
            Unit ruleform = new Unit(
                                     stripQuotes(unit.existentialRuleform().name.getText()),
                                     description == null ? null
                                                        : stripQuotes(description.getText()),
                                     model.getCurrentPrincipal().getPrincipal());
            ruleform.setEnumerated(unit.enumerated == null ? null
                                                          : Boolean.valueOf(unit.enumerated.getText()));
            ruleform.setDatatype(unit.datatype.getText());
            ruleform.setMin(unit.min == null ? null
                                            : BigDecimal.valueOf(Long.valueOf(unit.min.getText())));
            ruleform.setMax(unit.max == null ? null
                                            : BigDecimal.valueOf(Long.valueOf(unit.max.getText())));
            em.persist(ruleform);
            workspace.put(unit.existentialRuleform().workspaceName.getText(),
                          ruleform);
        }
    }

    /**
     * @param facet
     */
    private void locationAuthorizations(FacetContext facet) {
        // TODO Auto-generated method stub

    }

    private void locationFacets() {
        for (FacetContext facet : wsp.getLocationFacets()) {
            classifiedLocationAttributes(facet);
            locationNetworkConstraints(facet);
            locationAuthorizations(facet);
        }
    }

    private void locationNetworkConstraints(FacetContext facet) {
        NetworkConstraintsContext networkConstraints = facet.networkConstraints();
        if (networkConstraints == null) {
            return;
        }
        networkConstraints.constraint().forEach(constraint -> {
                                                    LocationNetworkAuthorization authorization = new LocationNetworkAuthorization(
                                                                                                                                  model.getCurrentPrincipal().getPrincipal());
                                                    authorization.setClassifier(resolve(facet.classifier));
                                                    authorization.setClassification(resolve(facet.classification));
                                                    authorization.setChildRelationship(resolve(constraint.childRelationship));
                                                    authorization.setAuthorizedParent(resolve(constraint.authorizedParent));
                                                    authorization.setAuthorizedRelationship(resolve(constraint.authorizedRelationship));
                                                    em.persist(authorization);
                                                });
    }

    private void processImports() {
        for (ImportedWorkspaceContext w : wsp.getImports()) {
            String uri = stripQuotes(w.uri.getText());
            UUID uuid = Workspace.uuidOf(uri);
            workspace.addImport(w.namespace.getText(),
                                model.getEntityManager().find(Product.class,
                                                              uuid),
                                model.getCurrentPrincipal().getPrincipal());
        }
    }

    /**
     * @param facet
     */
    private void productAuthorizations(FacetContext facet) {
        // TODO Auto-generated method stub

    }

    private void productFacets() {
        for (FacetContext facet : wsp.getProductFacets()) {
            classifiedProductAttributes(facet);
            productNetworkConstraints(facet);
            productAuthorizations(facet);
        }
    }

    private void productNetworkConstraints(FacetContext facet) {
        NetworkConstraintsContext networkConstraints = facet.networkConstraints();
        if (networkConstraints == null) {
            return;
        }
        networkConstraints.constraint().forEach(constraint -> {
                                                    ProductNetworkAuthorization authorization = new ProductNetworkAuthorization(
                                                                                                                                model.getCurrentPrincipal().getPrincipal());
                                                    authorization.setClassifier(resolve(facet.classifier));
                                                    authorization.setClassification(resolve(facet.classification));
                                                    QualifiedNameContext temp = constraint.childRelationship;
                                                    if (temp != null) {
                                                        authorization.setChildRelationship(resolve(temp));
                                                    }
                                                    temp = constraint.authorizedParent;
                                                    if (temp != null) {
                                                        authorization.setAuthorizedParent(resolve(temp));
                                                    }
                                                    temp = constraint.authorizedRelationship;
                                                    if (temp != null) {
                                                        authorization.setAuthorizedRelationship(resolve(temp));
                                                    }
                                                    em.persist(authorization);
                                                });
    }

    private void relationshipFacets() {
        for (FacetContext facet : wsp.getRelationshipFacets()) {
            classifiedRelationshipAttributes(facet);
            relationshipNetworkConstraints(facet);
        }
    }

    private void relationshipNetworkConstraints(FacetContext facet) {
        NetworkConstraintsContext networkConstraints = facet.networkConstraints();
        if (networkConstraints == null) {
            return;
        }
        networkConstraints.constraint().forEach(constraint -> {
                                                    RelationshipNetworkAuthorization authorization = new RelationshipNetworkAuthorization(
                                                                                                                                          model.getCurrentPrincipal().getPrincipal());
                                                    authorization.setClassifier(resolve(facet.classifier));
                                                    authorization.setClassification(resolve(facet.classification));
                                                    authorization.setChildRelationship(resolve(constraint.childRelationship));
                                                    authorization.setAuthorizedParent(resolve(constraint.authorizedParent));
                                                    authorization.setAuthorizedRelationship(resolve(constraint.authorizedRelationship));
                                                    em.persist(authorization);
                                                });
    }

    @SuppressWarnings("unchecked")
    private <T extends Ruleform> T resolve(QualifiedNameContext qualifiedName) {
        if (qualifiedName.namespace != null) {
            T ruleform = (T) scope.lookup(qualifiedName.namespace.getText(),
                                          qualifiedName.member.getText());
            if (ruleform == null) {
                throw new InvalidKeyException(
                                              String.format("Cannot resolve %s:%s",
                                                            qualifiedName.namespace.getText(),
                                                            qualifiedName.member.getText()));
            }
            return ruleform;
        }
        T ruleform;
        if (qualifiedName.member.getText().equals(THIS)) {
            ruleform = (T) workspace.getDefiningProduct();
        } else {
            ruleform = workspace.get(qualifiedName.member.getText());
            if (ruleform == null) {
                throw new InvalidKeyException(
                                              String.format("Cannot find workspace key: %s",
                                                            qualifiedName.member.getText()));
            }
        }
        return ruleform;
    }

    /**
     * @param attr
     * @param valueType
     */
    private void setValueType(Attribute attr, Token valueType) {
        switch (valueType.getText()) {
            case "int":
                attr.setValueType(ValueType.INTEGER);
                return;
            case "bool":
                attr.setValueType(ValueType.BOOLEAN);
                return;
            case "text":
                attr.setValueType(ValueType.TEXT);
                return;
            case "binary":
                attr.setValueType(ValueType.BINARY);
                return;
            case "numeric":
                attr.setValueType(ValueType.NUMERIC);
                return;
            case "timestamp":
                attr.setValueType(ValueType.TIMESTAMP);
                return;
            default:
                throw new IllegalArgumentException(
                                                   String.format("Invalid attribute value type: %s for %s",
                                                                 valueType.getText(),
                                                                 attr));
        }
    }

    private void statusCodeFacets() {
        for (FacetContext facet : wsp.getStatusCodeFacets()) {
            classifiedStatusCodeAttributes(facet);
            statusCodeNetworkConstraints(facet);
        }
    }

    private void statusCodeNetworkConstraints(FacetContext facet) {
        NetworkConstraintsContext networkConstraints = facet.networkConstraints();
        if (networkConstraints == null) {
            return;
        }
        networkConstraints.constraint().forEach(constraint -> {
                                                    StatusCodeNetworkAuthorization authorization = new StatusCodeNetworkAuthorization(
                                                                                                                                      model.getCurrentPrincipal().getPrincipal());
                                                    authorization.setClassifier(resolve(facet.classifier));
                                                    authorization.setClassification(resolve(facet.classification));
                                                    authorization.setChildRelationship(resolve(constraint.childRelationship));
                                                    authorization.setAuthorizedParent(resolve(constraint.authorizedParent));
                                                    authorization.setAuthorizedRelationship(resolve(constraint.authorizedRelationship));
                                                    em.persist(authorization);
                                                });
    }

    private String stripQuotes(String original) {
        return original.substring(1, original.length() - 1);
    }

    private void unitFacets() {
        for (FacetContext facet : wsp.getUnitFacets()) {
            unitFacets(facet);
            unitNetworkConstraints(facet);
        }
    }

    private void unitFacets(FacetContext facet) {
        UnitNetworkAuthorization authorization = new UnitNetworkAuthorization(
                                                                              model.getCurrentPrincipal().getPrincipal());
        authorization.setClassification(resolve(facet.classification));
        authorization.setClassifier(resolve(facet.classifier));
        model.getEntityManager().persist(authorization);
        workspace.add(authorization);
        facet.classifiedAttributes().qualifiedName().forEach(attribute -> {
                                                                 UnitAttributeAuthorization auth = new UnitAttributeAuthorization(
                                                                                                                                  resolve(attribute),
                                                                                                                                  model.getCurrentPrincipal().getPrincipal());
                                                                 auth.setNetworkAuthorization(authorization);
                                                                 model.getEntityManager().persist(auth);
                                                                 workspace.add(auth);

                                                             });
    }

    private void unitNetworkConstraints(FacetContext facet) {
        NetworkConstraintsContext networkConstraints = facet.networkConstraints();
        if (networkConstraints == null) {
            return;
        }
        networkConstraints.constraint().forEach(constraint -> {
                                                    UnitNetworkAuthorization authorization = new UnitNetworkAuthorization(
                                                                                                                          model.getCurrentPrincipal().getPrincipal());
                                                    authorization.setClassifier(resolve(facet.classifier));
                                                    authorization.setClassification(resolve(facet.classification));
                                                    authorization.setChildRelationship(resolve(constraint.childRelationship));
                                                    authorization.setAuthorizedParent(resolve(constraint.authorizedParent));
                                                    authorization.setAuthorizedRelationship(resolve(constraint.authorizedRelationship));
                                                    em.persist(authorization);
                                                });
    }
}
