/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chiralbehaviors.CoRE.workspace.dsl;

import java.math.BigDecimal;

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
import com.chiralbehaviors.CoRE.event.ProductChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductSelfSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductSiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeNetwork;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.network.NetworkInference;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.network.RelationshipNetwork;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.chiralbehaviors.CoRE.time.Interval;
import com.chiralbehaviors.CoRE.time.IntervalNetwork;
import com.chiralbehaviors.CoRE.workspace.DatabaseBackedWorkspace;
import com.chiralbehaviors.CoRE.workspace.Workspace;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ChildSequencingContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.EdgeContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ExistentialRuleformContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.IntervalContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ParentSequencingContext;
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
    private final EntityManager         em;
    private final Model                 model;
    private DatabaseBackedWorkspace     workspace;
    private final WorkspacePresentation wsp;

    public WorkspaceImporter(WorkspacePresentation wsp, Model model) {
        this.wsp = wsp;
        this.model = model;
        this.em = model.getEntityManager();
    }

    public Workspace loadWorkspace() {
        workspace = new DatabaseBackedWorkspace(createWorkspaceProduct(), em);
        loadAgencies();
        loadAttributes();
        loadLocations();
        loadProducts();
        loadRelationships();
        loadStatusCodes();
        loadStatusCodeSequencings();
        loadUnits();
        loadIntervals();
        loadEdges();
        loadSequencingAuths();
        loadInferences();
        return workspace;
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

    private Product createWorkspaceProduct() {
        Product workspaceProduct = new Product(wsp.getWorkspaceDefinition().a,
                                               wsp.getWorkspaceDefinition().b,
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

    @SuppressWarnings("unchecked")
    <T> T resolve(QualifiedNameContext qualifiedName) {
        Ruleform ruleform = workspace.get(qualifiedName.member.getText());
        if (ruleform == null) {
            throw new InvalidKeyException(
                                          String.format("Cannot find workspace key: %s",
                                                        qualifiedName.member.getText()));
        }
        return (T) ruleform;
    }
}
