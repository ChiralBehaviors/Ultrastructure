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

import java.math.BigDecimal;
import java.util.UUID;

import javax.management.openmbean.InvalidKeyException;
import javax.persistence.EntityManager;

import org.antlr.v4.runtime.Token;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeNetwork;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.attribute.unit.UnitNetwork;
import com.chiralbehaviors.CoRE.event.MetaProtocol;
import com.chiralbehaviors.CoRE.event.ProductChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductSelfSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductSiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.Protocol;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeNetwork;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace;
import com.chiralbehaviors.CoRE.meta.workspace.Workspace;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.network.NetworkInference;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.network.RelationshipNetwork;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.chiralbehaviors.CoRE.time.Interval;
import com.chiralbehaviors.CoRE.time.IntervalNetwork;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ChildSequencingContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.EdgeContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ExistentialRuleformContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ImportedWorkspaceContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.IntervalContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.MetaProtocolContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ParentSequencingContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ProtocolContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.QualifiedNameContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.RelationshipPairContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.SelfSequencingContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.SequencePairContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.SiblingSequencingContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.StatusCodeSequencingSetContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.UnitContext;

/**
 * @author hparry
 *
 */
public class WorkspaceImporter {

    private static final String         STATUS_CODE_SEQUENCING_FORMAT = "%s: %s -> %s";
    private static final String         URN_UUID                      = "urn:uuid:";
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

    public Workspace loadWorkspace() {
        scope = model.getWorkspaceModel().createWorkspace(createWorkspaceProduct(),
                                                          model.getKernel().getCoreAnimationSoftware());
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
        loadSequencingAuths();
        loadInferences();
        loadProtocols();
        loadMetaprotocols();
        return workspace;
    }

    private Product createWorkspaceProduct() {
        Product workspaceProduct = new Product(
                                               wsp.getWorkspaceDefinition().name.getText(),
                                               wsp.getWorkspaceDefinition().description.getText(),
                                               model.getKernel().getCore());
        em.persist(workspaceProduct);
        return workspaceProduct;
    }

    private void loadAgencies() {
        for (ExistentialRuleformContext ruleform : wsp.getAgencies()) {
            Agency agency = new Agency(
                                       ruleform.name.getText(),
                                       ruleform.description == null ? null
                                                                   : ruleform.description.getText(),
                                       model.getKernel().getCore());
            em.persist(agency);
            workspace.put(ruleform.workspaceName.getText(), agency);
        }

    }

    private void loadAgencyNetworks() {
        for (EdgeContext edge : wsp.getAgencyNetworks()) {
            AgencyNetwork network = new AgencyNetwork(
                                                      resolve(edge.parent),
                                                      resolve(edge.relationship),
                                                      resolve(edge.child),
                                                      model.getKernel().getCore());
            em.persist(network);
            workspace.add(network);
        }
    }

    private void loadAttributeNetworks() {
        for (EdgeContext edge : wsp.getAttributeNetworks()) {
            AttributeNetwork network = new AttributeNetwork(
                                                            resolve(edge.parent),
                                                            resolve(edge.relationship),
                                                            resolve(edge.child),
                                                            model.getKernel().getCore());
            em.persist(network);
            workspace.add(network);
        }
    }

    private void loadAttributes() {
        for (ExistentialRuleformContext ruleform : wsp.getAttributes()) {
            Attribute attr = new Attribute(
                                           ruleform.name.getText(),
                                           ruleform.description == null ? null
                                                                       : ruleform.description.getText(),
                                           model.getKernel().getCore());
            em.persist(attr);
            workspace.put(ruleform.workspaceName.getText(), attr);
        }
    }

    private void loadChildSequencing() {
        for (ChildSequencingContext seq : wsp.getChildSequencings()) {
            ProductChildSequencingAuthorization auth = new ProductChildSequencingAuthorization(
                                                                                               resolve(seq.parent),
                                                                                               resolve(seq.status),
                                                                                               resolve(seq.child),
                                                                                               resolve(seq.next),
                                                                                               model.getKernel().getCore());
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

    private void loadInferences() {
        for (EdgeContext edge : wsp.getInferences()) {
            NetworkInference inference = new NetworkInference(
                                                              resolve(edge.parent),
                                                              resolve(edge.relationship),
                                                              resolve(edge.child),
                                                              model.getKernel().getCore());
            em.persist(inference);
            workspace.add(inference);
        }
    }

    private void loadIntervalNetworks() {
        for (EdgeContext edge : wsp.getIntervalNetworks()) {
            IntervalNetwork network = new IntervalNetwork(
                                                          resolve(edge.parent),
                                                          resolve(edge.relationship),
                                                          resolve(edge.child),
                                                          model.getKernel().getCore());
            em.persist(network);
            workspace.add(network);
        }
    }

    private void loadIntervals() {
        for (IntervalContext ivl : wsp.getIntervals()) {
            Interval interval = new Interval(
                                             ivl.existentialRuleform().name.getText(),
                                             ivl.start == null ? BigDecimal.valueOf(0)
                                                              : BigDecimal.valueOf(Long.valueOf(ivl.start.getText())),
                                             ivl.startUnit == null ? model.getKernel().getNotApplicableUnit()
                                                                  : workspace.get(ivl.startUnit.getText()),
                                             ivl.duration == null ? BigDecimal.valueOf(0)
                                                                 : BigDecimal.valueOf(Long.valueOf(ivl.duration.getText())),
                                             ivl.durationUnit == null ? model.getKernel().getNotApplicableUnit()
                                                                     : workspace.get(ivl.durationUnit.getText()),
                                             ivl.existentialRuleform().description.getText(),
                                             model.getKernel().getCore());
            em.persist(interval);
            workspace.put(ivl.existentialRuleform().workspaceName.getText(),
                          interval);
        }
    }

    private void loadLocationNetworks() {
        for (EdgeContext edge : wsp.getLocationNetworks()) {
            LocationNetwork network = new LocationNetwork(
                                                          resolve(edge.parent),
                                                          resolve(edge.relationship),
                                                          resolve(edge.child),
                                                          model.getKernel().getCore());
            em.persist(network);
            workspace.add(network);
        }
    }

    private void loadLocations() {
        for (ExistentialRuleformContext rf : wsp.getLocations()) {
            Location ruleform = new Location(
                                             rf.name.getText(),
                                             rf.description == null ? null
                                                                   : rf.description.getText(),
                                             model.getKernel().getCore());
            em.persist(ruleform);
            workspace.put(rf.workspaceName.getText(), ruleform);
        }
    }

    private void loadMetaprotocols() {
        for (MetaProtocolContext mpc : wsp.getMetaProtocols()) {
            MetaProtocol metaProtocol = model.getJobModel().newInitializedMetaProtocol(resolve(mpc.service),
                                                                                       model.getKernel().getCore());
            if (mpc.serviceAttribute != null) {
                metaProtocol.setServiceAttribute(resolve(mpc.serviceAttribute));
            }
            if (mpc.product != null)
                metaProtocol.setProduct(resolve(mpc.product));
            if (mpc.productAttribute != null)
                metaProtocol.setProductAttribute(resolve(mpc.productAttribute));
            if (mpc.from != null)
                metaProtocol.setDeliverFrom(resolve(mpc.from));
            if (mpc.fromAttribute != null)
                metaProtocol.setDeliverFromAttribute(resolve(mpc.fromAttribute));
            if (mpc.to != null)
                metaProtocol.setDeliverTo(resolve(mpc.to));
            if (mpc.toAttribute != null)
                metaProtocol.setDeliverToAttribute(resolve(mpc.toAttribute));
            if (mpc.quantityUnit != null)
                metaProtocol.setQuantityUnit(resolve(mpc.quantityUnit));
            if (mpc.requester != null)
                metaProtocol.setRequester(resolve(mpc.requester));
            if (mpc.requesterAttribute != null)
                metaProtocol.setRequesterAttribute(resolve(mpc.requesterAttribute));
            if (mpc.assignTo != null)
                metaProtocol.setAssignTo(resolve(mpc.assignTo));
            if (mpc.assignToAttribute != null)
                metaProtocol.setAssignToAttribute(resolve(mpc.assignToAttribute));
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
                                                                                                 model.getKernel().getCore());
            em.persist(auth);
            workspace.add(auth);
        }
    }

    private void loadProductNetworks() {
        for (EdgeContext edge : wsp.getProductNetworks()) {
            ProductNetwork network = new ProductNetwork(
                                                        resolve(edge.parent),
                                                        resolve(edge.relationship),
                                                        resolve(edge.child),
                                                        model.getKernel().getCore());
            em.persist(network);
            workspace.add(network);
        }
    }

    private void loadProducts() {
        for (ExistentialRuleformContext rf : wsp.getProducts()) {
            Product ruleform = new Product(
                                           rf.name.getText(),
                                           rf.description == null ? null
                                                                 : rf.description.getText(),
                                           model.getKernel().getCore());
            em.persist(ruleform);
            workspace.put(rf.workspaceName.getText(), ruleform);
        }
    }

    private void loadProtocols() {
        for (ProtocolContext pc : wsp.getProtocols()) {
            Protocol protocol = model.getJobModel().newInitializedProtocol(resolve(pc.matchJob().service),
                                                                           model.getKernel().getCore());
            if (pc.matchJob().serviceAttribute != null) {
                protocol.setServiceAttribute(resolve(pc.matchJob().serviceAttribute));
            }
            if (pc.matchJob().product != null)
                protocol.setProduct(resolve(pc.matchJob().product));
            if (pc.matchJob().productAttribute != null)
                protocol.setProductAttribute(resolve(pc.matchJob().productAttribute));
            if (pc.matchJob().from != null)
                protocol.setDeliverFrom(resolve(pc.matchJob().from));
            if (pc.matchJob().fromAttribute != null)
                protocol.setDeliverFromAttribute(resolve(pc.matchJob().fromAttribute));
            if (pc.matchJob().to != null)
                protocol.setDeliverTo(resolve(pc.matchJob().to));
            if (pc.matchJob().toAttribute != null)
                protocol.setDeliverToAttribute(resolve(pc.matchJob().toAttribute));
            if (pc.matchJob().quantity != null)
                protocol.setQuantity(BigDecimal.valueOf(Long.parseLong(pc.matchJob().quantity.getText())));
            if (pc.matchJob().quantityUnit != null)
                protocol.setQuantityUnit(resolve(pc.matchJob().quantityUnit));
            if (pc.matchJob().requester != null)
                protocol.setRequester(resolve(pc.matchJob().requester));
            if (pc.matchJob().requesterAttribute != null)
                protocol.setRequesterAttribute(resolve(pc.matchJob().requesterAttribute));
            if (pc.matchJob().assignTo != null)
                protocol.setAssignTo(resolve(pc.matchJob().assignTo));
            if (pc.matchJob().assignToAttribute != null)
                protocol.setAssignToAttribute(resolve(pc.matchJob().assignToAttribute));
            if (pc.matchJob().sequence != null)
                protocol.setSequenceNumber(Integer.parseInt(pc.matchJob().sequence.getText()));

            if (pc.childJob().service != null)
                protocol.setChildService(resolve(pc.childJob().service));
            if (pc.childJob().serviceAttribute != null)
                protocol.setChildServiceAttribute(resolve(pc.childJob().serviceAttribute));
            if (pc.childJob().product != null)
                protocol.setChildProduct(resolve(pc.childJob().product));
            if (pc.childJob().productAttribute != null)
                protocol.setChildProductAttribute(resolve(pc.childJob().productAttribute));
            if (pc.childJob().from != null)
                protocol.setChildDeliverFrom(resolve(pc.childJob().from));
            if (pc.childJob().fromAttribute != null)
                protocol.setChildDeliverFromAttribute(resolve(pc.childJob().fromAttribute));
            if (pc.childJob().to != null)
                protocol.setChildDeliverTo(resolve(pc.childJob().to));
            if (pc.childJob().toAttribute != null)
                protocol.setChildDeliverToAttribute(resolve(pc.childJob().toAttribute));
            if (pc.childJob().quantity != null)
                protocol.setChildQuantity(BigDecimal.valueOf(Long.parseLong(pc.childJob().quantity.getText())));
            if (pc.childJob().quantityUnit != null)
                protocol.setChildQuantityUnit(resolve(pc.childJob().quantityUnit));
            if (pc.childJob().assignTo != null)
                protocol.setChildAssignTo(resolve(pc.childJob().assignTo));
            if (pc.childJob().assignToAttribute != null)
                protocol.setChildAssignToAttribute(resolve(pc.childJob().assignToAttribute));
            workspace.add(protocol);
        }

    }

    private void loadRelationshipNetworks() {
        for (EdgeContext edge : wsp.getRelationshipNetworks()) {
            RelationshipNetwork network = new RelationshipNetwork(
                                                                  resolve(edge.parent),
                                                                  resolve(edge.relationship),
                                                                  resolve(edge.child),
                                                                  model.getKernel().getCore());
            em.persist(network);
            workspace.add(network);
        }
    }

    private void loadRelationships() {
        for (RelationshipPairContext ctx : wsp.getRelationships()) {
            Relationship relA = model.getRelationshipModel().create(ctx.primary.name.getText(),
                                                                    ctx.primary.description == null ? null
                                                                                                   : ctx.primary.description.getText(),
                                                                    ctx.inverse.name.getText(),
                                                                    ctx.inverse.description == null ? null
                                                                                                   : ctx.primary.description.getText());
            workspace.put(ctx.primary.workspaceName.getText(), relA);
            workspace.put(ctx.inverse.workspaceName.getText(),
                          relA.getInverse());
        }

    }

    private void loadSelfSequencing() {
        for (SelfSequencingContext seq : wsp.getSelfSequencings()) {
            ProductSelfSequencingAuthorization auth = new ProductSelfSequencingAuthorization(
                                                                                             resolve(seq.service),
                                                                                             resolve(seq.status),
                                                                                             resolve(seq.next),
                                                                                             model.getKernel().getCore());
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
                                                                                                   model.getKernel().getCore());
            em.persist(auth);
            workspace.add(auth);
        }
    }

    private void loadStatusCodeNetworks() {
        for (EdgeContext edge : wsp.getStatusCodeNetworks()) {
            StatusCodeNetwork network = new StatusCodeNetwork(
                                                              resolve(edge.parent),
                                                              resolve(edge.relationship),
                                                              resolve(edge.child),
                                                              model.getKernel().getCore());
            em.persist(network);
            workspace.add(network);
        }
    }

    private void loadStatusCodes() {
        for (ExistentialRuleformContext rf : wsp.getStatusCodes()) {
            StatusCode ruleform = new StatusCode(
                                                 rf.name.getText(),
                                                 rf.description == null ? null
                                                                       : rf.description.getText(),
                                                 model.getKernel().getCore());
            em.persist(ruleform);
            workspace.put(rf.workspaceName.getText(), ruleform);
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
            UnitNetwork network = new UnitNetwork(resolve(edge.parent),
                                                  resolve(edge.relationship),
                                                  resolve(edge.child),
                                                  model.getKernel().getCore());
            em.persist(network);
            workspace.add(network);
        }
    }

    private void loadUnits() {
        for (UnitContext unit : wsp.getUnits()) {
            Token description = unit.existentialRuleform().description;
            Unit ruleform = new Unit(
                                     unit.existentialRuleform().name.getText(),
                                     description == null ? null
                                                        : description.getText(),
                                     model.getKernel().getCore());
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

    private void processImports() {
        for (ImportedWorkspaceContext workspace : wsp.getImports()) {
            String uri = stripQuotes(workspace.uri.getText());
            if (!uri.startsWith(URN_UUID)) {
                throw new IllegalStateException(
                                                String.format("Only support import URIs of form urn:uuid:<uuid>: %s",
                                                              uri));
            }
            UUID uuid = UUID.fromString(uri.substring(URN_UUID.length()));
            scope.add(workspace.namespace.getText(),
                      model.getWorkspaceModel().getScoped(model.getEntityManager().find(Product.class,
                                                                                        uuid)).getWorkspace());
        }
    }

    private String stripQuotes(String original) {
        return original.substring(1, original.length() - 1);
    }

    @SuppressWarnings("unchecked")
    <T extends Ruleform> T resolve(QualifiedNameContext qualifiedName) {
        if (qualifiedName.namespace != null) {
            return (T) scope.lookup(qualifiedName.namespace.getText(),
                                    qualifiedName.member.getText());
        }
        T ruleform = workspace.get(qualifiedName.member.getText());
        if (ruleform == null) {
            throw new InvalidKeyException(
                                          String.format("Cannot find workspace key: %s",
                                                        qualifiedName.member.getText()));
        }
        return ruleform;
    }
}
