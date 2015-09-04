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

import java.beans.Introspector;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import javax.management.openmbean.InvalidKeyException;
import javax.persistence.EntityManager;

import org.antlr.v4.runtime.Token;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyAttributeAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyLocationAttributeAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyLocationAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyNetworkAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyProductAttributeAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyProductAuthorization;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.AttributeMetaAttribute;
import com.chiralbehaviors.CoRE.attribute.AttributeMetaAttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.AttributeNetworkAuthorization;
import com.chiralbehaviors.CoRE.attribute.ValueType;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.attribute.unit.UnitAttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.unit.UnitNetworkAuthorization;
import com.chiralbehaviors.CoRE.job.MetaProtocol;
import com.chiralbehaviors.CoRE.job.ProductChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.job.ProductParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.job.ProductSelfSequencingAuthorization;
import com.chiralbehaviors.CoRE.job.ProductSiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.job.Protocol;
import com.chiralbehaviors.CoRE.job.status.StatusCode;
import com.chiralbehaviors.CoRE.job.status.StatusCodeAttributeAuthorization;
import com.chiralbehaviors.CoRE.job.status.StatusCodeNetworkAuthorization;
import com.chiralbehaviors.CoRE.job.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationAttributeAuthorization;
import com.chiralbehaviors.CoRE.location.LocationNetworkAuthorization;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceAccessor;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.network.Cardinality;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkInference;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.XDomainNetworkAuthorization;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductAttributeAuthorization;
import com.chiralbehaviors.CoRE.product.ProductLocationAttributeAuthorization;
import com.chiralbehaviors.CoRE.product.ProductLocationAuthorization;
import com.chiralbehaviors.CoRE.product.ProductNetworkAuthorization;
import com.chiralbehaviors.CoRE.product.ProductRelationshipAttributeAuthorization;
import com.chiralbehaviors.CoRE.product.ProductRelationshipAuthorization;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.relationship.RelationshipAttributeAuthorization;
import com.chiralbehaviors.CoRE.relationship.RelationshipNetworkAuthorization;
import com.chiralbehaviors.CoRE.time.Interval;
import com.chiralbehaviors.CoRE.time.IntervalAttributeAuthorization;
import com.chiralbehaviors.CoRE.time.IntervalNetworkAuthorization;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.AttributeRuleformContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.AttributeValueContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.AttributedExistentialRuleformContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ChildSequencingContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ClassifiedAttributesContext;
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
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.UnitContext;

/**
 * @author hparry
 *
 */
public class WorkspaceImporter {
    private static final String STATUS_CODE_SEQUENCING_FORMAT = "%s: %s -> %s";
    private static final String THIS                          = "this";

    public static WorkspaceImporter createWorkspace(InputStream source,
                                                    Model model) throws IOException {
        WorkspaceImporter importer = new WorkspaceImporter(source, model);
        importer.createWorkspace();
        return importer;
    }

    public static String networkAuthNameOf(ConstraintContext constraint) {
        String name;
        if (constraint.name != null) {
            name = constraint.name.getText();
        } else if (constraint.anyType != null) {
            name = constraint.childRelationship.member.getText();
        } else if (constraint.methodType != null) {
            switch (constraint.methodType.getText()) {
                case "named by relationship":
                    name = constraint.childRelationship.member.getText();
                    break;
                case "named by entity":
                    name = constraint.authorizedParent.member.getText();
                    break;
                default:
                    throw new IllegalStateException(String.format("Invalid syntax for network authorization name: %s",
                                                                  constraint.methodType.getText()));
            }
        } else {
            name = constraint.authorizedParent.member.getText();
        }
        return Introspector.decapitalize(name);
    }

    private final EntityManager em;
    private final Model         model;
    private WorkspaceScope      scope;
    private UUID                uuid;

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
        this.em = model.getEntityManager();
    }

    public WorkspaceAccessor addToWorkspace() {
        scope = model.getWorkspaceModel()
                     .getScoped(getWorkspaceProduct());
        workspace = (EditableWorkspace) scope.getWorkspace();
        loadWorkspace();
        return workspace;
    }

    public WorkspaceAccessor createWorkspace() {
        scope = model.getWorkspaceModel()
                     .createWorkspace(createWorkspaceProduct(),
                                      model.getCurrentPrincipal()
                                           .getPrincipal());
        workspace = (EditableWorkspace) scope.getWorkspace();
        loadWorkspace();
        return workspace;
    }

    public WorkspaceAccessor getWorkspace() {
        return workspace;
    }

    public void setScope(WorkspaceScope scope) {
        this.scope = scope;
    }

    private void agencyFacets() {
        for (FacetContext facet : wsp.getAgencyFacets()) {
            classifiedAttributes(facet,
                                 new AgencyNetworkAuthorization(model.getCurrentPrincipal()
                                                                     .getPrincipal()),
                                 agency -> new AgencyAttributeAuthorization(model.getCurrentPrincipal()
                                                                                 .getPrincipal()));
            agencyNetworkConstraints(facet);
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
        networkConstraints.constraint()
                          .forEach(constraint -> {
                              Class<?> authParentClass = resolveAuthParent(constraint);
                              if (authParentClass.equals(Agency.class)) {
                                  createNetworkAuth(facet, constraint,
                                                    new AgencyNetworkAuthorization(model.getCurrentPrincipal()
                                                                                        .getPrincipal()),
                                                    agency -> new AgencyAttributeAuthorization(model.getCurrentPrincipal()
                                                                                                    .getPrincipal()));
                              } else
                                  if (authParentClass.equals(Location.class)) {
                                  createAgencyLocationAuth(facet, constraint);
                              } else if (authParentClass.equals(Product.class)) {
                                  createAgencyProductAuth(facet, constraint);
                              }
                          });
    }

    private void attributeFacets() {
        for (FacetContext facet : wsp.getAttributeFacets()) {
            classifiedAttributes(facet,
                                 new AttributeNetworkAuthorization(model.getCurrentPrincipal()
                                                                        .getPrincipal()),
                                 agency -> new AttributeMetaAttributeAuthorization(model.getCurrentPrincipal()
                                                                                        .getPrincipal()));
            attributeNetworkConstraints(facet);
        }
    }

    private void attributeNetworkConstraints(FacetContext facet) {
        NetworkConstraintsContext networkConstraints = facet.networkConstraints();
        if (networkConstraints == null) {
            return;
        }
        networkConstraints.constraint()
                          .forEach(constraint -> {
                              createNetworkAuth(facet, constraint,
                                                new AttributeNetworkAuthorization(model.getCurrentPrincipal()
                                                                                       .getPrincipal()),
                                                agency -> new AttributeMetaAttributeAuthorization(model.getCurrentPrincipal()
                                                                                                       .getPrincipal()));
                          });
    }

    private <T extends ExistentialRuleform<T, Network>, Network extends NetworkRuleform<T>> void classifiedAttributes(FacetContext facet,
                                                                                                                      NetworkAuthorization<T> authorization,
                                                                                                                      Function<Agency, AttributeAuthorization<T, Network>> attrAuth) {
        authorization.setClassifier(resolve(facet.classifier));
        authorization.setClassification(resolve(facet.classification));
        if (facet.name != null) {
            authorization.setName(stripQuotes(facet.name.getText()));
        } else {
            authorization.setName(authorization.getClassification()
                                               .getName());
        }
        if (facet.description != null) {
            authorization.setNotes(stripQuotes(facet.name.getText()));
        }
        model.getEntityManager()
             .persist(authorization);
        workspace.add(authorization);
        ClassifiedAttributesContext classifiedAttributes = facet.classifiedAttributes();
        if (classifiedAttributes == null) {
            return;
        }
        classifiedAttributes.qualifiedName()
                            .forEach(attribute -> {
                                AttributeAuthorization<T, Network> auth = attrAuth.apply(model.getCurrentPrincipal()
                                                                                              .getPrincipal());
                                auth.setNetworkAuthorization(authorization);
                                auth.setAuthorizedAttribute(resolve(attribute));
                                model.getEntityManager()
                                     .persist(auth);
                                workspace.add(auth);

                            });
    }

    /**
     * @param facet
     * @param constraint
     */
    private void createAgencyLocationAuth(FacetContext facet,
                                          ConstraintContext constraint) {
        AgencyLocationAuthorization authorization = new AgencyLocationAuthorization(model.getCurrentPrincipal()
                                                                                         .getPrincipal());
        authorization.setFromParent(resolve(facet.classification));
        authorization.setFromRelationship(resolve(facet.classifier));
        authorization.setConnection(resolve(constraint.childRelationship));
        authorization.setName(networkAuthNameOf(constraint));
        resolveTo(constraint, authorization);
        authorization.setCardinality(Cardinality.valueOf(constraint.cardinality.getText()
                                                                               .toUpperCase()));
        workspace.add(authorization);
        em.persist(authorization);
        ClassifiedAttributesContext classifiedAttributes = constraint.classifiedAttributes();
        if (classifiedAttributes == null) {
            return;
        }
        classifiedAttributes.qualifiedName()
                            .forEach(attribute -> {
                                AgencyLocationAttributeAuthorization auth = new AgencyLocationAttributeAuthorization(model.getCurrentPrincipal()
                                                                                                                          .getPrincipal());
                                auth.setAuthorizedAttribute(resolve(attribute));
                                auth.setNetworkAuthorization(authorization);
                                model.getEntityManager()
                                     .persist(auth);
                                workspace.add(auth);
                            });
    }

    /**
     * @param facet
     * @param constraint
     */
    private void createAgencyProductAuth(FacetContext facet,
                                         ConstraintContext constraint) {
        AgencyProductAuthorization authorization = new AgencyProductAuthorization(model.getCurrentPrincipal()
                                                                                       .getPrincipal());
        authorization.setFromParent(resolve(facet.classification));
        authorization.setFromRelationship(resolve(facet.classifier));
        authorization.setConnection(resolve(constraint.childRelationship));
        authorization.setName(networkAuthNameOf(constraint));
        resolveTo(constraint, authorization);
        authorization.setCardinality(Cardinality.valueOf(constraint.cardinality.getText()
                                                                               .toUpperCase()));
        workspace.add(authorization);
        em.persist(authorization);
        ClassifiedAttributesContext classifiedAttributes = constraint.classifiedAttributes();
        if (classifiedAttributes == null) {
            return;
        }
        classifiedAttributes.qualifiedName()
                            .forEach(attribute -> {
                                AgencyProductAttributeAuthorization auth = new AgencyProductAttributeAuthorization(model.getCurrentPrincipal()
                                                                                                                        .getPrincipal());
                                auth.setAuthorizedAttribute(resolve(attribute));
                                auth.setNetworkAuthorization(authorization);
                                model.getEntityManager()
                                     .persist(auth);
                                workspace.add(auth);
                            });
    }

    /**
     * @param facet
     * @param constraint
     */
    private void createLocationAgencyAuth(FacetContext facet,
                                          ConstraintContext constraint) {
        AgencyLocationAuthorization authorization = new AgencyLocationAuthorization(model.getCurrentPrincipal()
                                                                                         .getPrincipal());
        resolveFrom(constraint, authorization);
        authorization.setConnection(resolve(constraint.childRelationship));
        authorization.setToParent(resolve(facet.classification));
        authorization.setToRelationship(resolve(facet.classifier));
        authorization.setName(networkAuthNameOf(constraint));
        authorization.setCardinality(Cardinality.valueOf(constraint.cardinality.getText()
                                                                               .toUpperCase()));
        authorization.setForward(false);
        em.persist(authorization);
        workspace.add(authorization);
        ClassifiedAttributesContext classifiedAttributes = constraint.classifiedAttributes();
        if (classifiedAttributes == null) {
            return;
        }
        classifiedAttributes.qualifiedName()
                            .forEach(attribute -> {
                                AgencyLocationAttributeAuthorization auth = new AgencyLocationAttributeAuthorization(model.getCurrentPrincipal()
                                                                                                                          .getPrincipal());
                                auth.setAuthorizedAttribute(resolve(attribute));
                                auth.setNetworkAuthorization(authorization);
                                model.getEntityManager()
                                     .persist(auth);
                                workspace.add(auth);
                            });
    }

    /**
     * @param facet
     * @param constraint
     */
    private void createLocationProductAuth(FacetContext facet,
                                           ConstraintContext constraint) {
        ProductLocationAuthorization authorization = new ProductLocationAuthorization(model.getCurrentPrincipal()
                                                                                           .getPrincipal());
        resolveFrom(constraint, authorization);
        authorization.setConnection(resolve(constraint.childRelationship));
        authorization.setToParent(resolve(facet.classification));
        authorization.setToRelationship(resolve(facet.classifier));
        authorization.setName(networkAuthNameOf(constraint));
        authorization.setCardinality(Cardinality.valueOf(constraint.cardinality.getText()
                                                                               .toUpperCase()));
        authorization.setForward(false);
        workspace.add(authorization);
        em.persist(authorization);
        ClassifiedAttributesContext classifiedAttributes = constraint.classifiedAttributes();
        if (classifiedAttributes == null) {
            return;
        }
        classifiedAttributes.qualifiedName()
                            .forEach(attribute -> {
                                ProductLocationAttributeAuthorization auth = new ProductLocationAttributeAuthorization(model.getCurrentPrincipal()
                                                                                                                            .getPrincipal());
                                auth.setAuthorizedAttribute(resolve(attribute));
                                auth.setNetworkAuthorization(authorization);
                                model.getEntityManager()
                                     .persist(auth);
                                workspace.add(auth);
                            });
    }

    private <T extends ExistentialRuleform<T, Network>, Network extends NetworkRuleform<T>> void createNetworkAuth(FacetContext facet,
                                                                                                                   ConstraintContext constraint,
                                                                                                                   NetworkAuthorization<T> authorization,
                                                                                                                   Function<Agency, AttributeAuthorization<T, Network>> attrAuth) {
        authorization.setName(networkAuthNameOf(constraint));
        authorization.setClassifier(resolve(facet.classifier));
        authorization.setClassification(resolve(facet.classification));
        authorization.setChildRelationship(resolve(constraint.childRelationship));
        resolveAuthorized(constraint, authorization);
        authorization.setCardinality(Cardinality.valueOf(constraint.cardinality.getText()
                                                                               .toUpperCase()));
        workspace.add(authorization);
        em.persist(authorization);
        ClassifiedAttributesContext classifiedAttributes = constraint.classifiedAttributes();
        if (classifiedAttributes == null) {
            return;
        }
        classifiedAttributes.qualifiedName()
                            .forEach(attribute -> {
                                AttributeAuthorization<T, Network> auth = attrAuth.apply(model.getCurrentPrincipal()
                                                                                              .getPrincipal());
                                auth.setAuthorizedNetworkAttribute(resolve(attribute));
                                auth.setNetworkAuthorization(authorization);
                                model.getEntityManager()
                                     .persist(auth);
                                workspace.add(auth);
                            });
    }

    /**
     * @param facet
     * @param constraint
     */
    private void createProductAgencyAuth(FacetContext facet,
                                         ConstraintContext constraint) {
        AgencyProductAuthorization authorization = new AgencyProductAuthorization(model.getCurrentPrincipal()
                                                                                       .getPrincipal());
        resolveFrom(constraint, authorization);
        authorization.setConnection(resolve(constraint.childRelationship));
        authorization.setToParent(resolve(facet.classification));
        authorization.setToRelationship(resolve(facet.classifier));
        authorization.setCardinality(Cardinality.valueOf(constraint.cardinality.getText()
                                                                               .toUpperCase()));
        authorization.setName(networkAuthNameOf(constraint));
        authorization.setForward(false);
        workspace.add(authorization);
        em.persist(authorization);
        ClassifiedAttributesContext classifiedAttributes = constraint.classifiedAttributes();
        if (classifiedAttributes == null) {
            return;
        }
        classifiedAttributes.qualifiedName()
                            .forEach(attribute -> {
                                AgencyProductAttributeAuthorization auth = new AgencyProductAttributeAuthorization(model.getCurrentPrincipal()
                                                                                                                        .getPrincipal());
                                auth.setAuthorizedAttribute(resolve(attribute));
                                auth.setNetworkAuthorization(authorization);
                                model.getEntityManager()
                                     .persist(auth);
                                workspace.add(auth);
                            });
    }

    /**
     * @param facet
     * @param constraint
     */
    private void createProductLocationAuth(FacetContext facet,
                                           ConstraintContext constraint) {
        ProductLocationAuthorization authorization = new ProductLocationAuthorization(model.getCurrentPrincipal()
                                                                                           .getPrincipal());
        authorization.setFromParent(resolve(facet.classification));
        authorization.setFromRelationship(resolve(facet.classifier));
        authorization.setConnection(resolve(constraint.childRelationship));
        authorization.setName(networkAuthNameOf(constraint));
        resolveTo(constraint, authorization);
        authorization.setCardinality(Cardinality.valueOf(constraint.cardinality.getText()
                                                                               .toUpperCase()));
        workspace.add(authorization);
        em.persist(authorization);
        ClassifiedAttributesContext classifiedAttributes = constraint.classifiedAttributes();
        if (classifiedAttributes == null) {
            return;
        }
        classifiedAttributes.qualifiedName()
                            .forEach(attribute -> {
                                ProductLocationAttributeAuthorization auth = new ProductLocationAttributeAuthorization(model.getCurrentPrincipal()
                                                                                                                            .getPrincipal());
                                auth.setAuthorizedAttribute(resolve(attribute));
                                auth.setNetworkAuthorization(authorization);
                                model.getEntityManager()
                                     .persist(auth);
                                workspace.add(auth);
                            });
    }

    private void createProductRelationshipAuth(FacetContext facet,
                                               ConstraintContext constraint) {
        ProductRelationshipAuthorization authorization = new ProductRelationshipAuthorization(model.getCurrentPrincipal()
                                                                                                   .getPrincipal());
        authorization.setFromParent(resolve(facet.classification));
        authorization.setFromRelationship(resolve(facet.classifier));
        authorization.setConnection(resolve(constraint.childRelationship));
        authorization.setName(networkAuthNameOf(constraint));
        resolveTo(constraint, authorization);
        authorization.setCardinality(Cardinality.valueOf(constraint.cardinality.getText()
                                                                               .toUpperCase()));
        workspace.add(authorization);
        em.persist(authorization);
        ClassifiedAttributesContext classifiedAttributes = constraint.classifiedAttributes();
        if (classifiedAttributes == null) {
            return;
        }
        classifiedAttributes.qualifiedName()
                            .forEach(attribute -> {
                                ProductRelationshipAttributeAuthorization auth = new ProductRelationshipAttributeAuthorization(model.getCurrentPrincipal()
                                                                                                                                    .getPrincipal());
                                auth.setAuthorizedAttribute(resolve(attribute));
                                auth.setNetworkAuthorization(authorization);
                                model.getEntityManager()
                                     .persist(auth);
                                workspace.add(auth);
                            });
    }

    private void createRelationshipProductAuth(FacetContext facet,
                                               ConstraintContext constraint) {
        ProductRelationshipAuthorization authorization = new ProductRelationshipAuthorization(model.getCurrentPrincipal()
                                                                                                   .getPrincipal());
        resolveFrom(constraint, authorization);
        authorization.setConnection(resolve(constraint.childRelationship));
        authorization.setToParent(resolve(facet.classification));
        authorization.setToRelationship(resolve(facet.classifier));
        authorization.setCardinality(Cardinality.valueOf(constraint.cardinality.getText()
                                                                               .toUpperCase()));
        authorization.setName(networkAuthNameOf(constraint));
        authorization.setForward(false);
        workspace.add(authorization);
        em.persist(authorization);
        ClassifiedAttributesContext classifiedAttributes = constraint.classifiedAttributes();
        if (classifiedAttributes == null) {
            return;
        }
        classifiedAttributes.qualifiedName()
                            .forEach(attribute -> {
                                ProductRelationshipAttributeAuthorization auth = new ProductRelationshipAttributeAuthorization(model.getCurrentPrincipal()
                                                                                                                                    .getPrincipal());
                                auth.setAuthorizedAttribute(resolve(attribute));
                                auth.setNetworkAuthorization(authorization);
                                model.getEntityManager()
                                     .persist(auth);
                                workspace.add(auth);
                            });
    }

    private Product createWorkspaceProduct() {
        workspaceUri = stripQuotes(wsp.getWorkspaceDefinition().uri.getText());
        Token description = wsp.getWorkspaceDefinition().description;
        Product workspaceProduct = new Product(stripQuotes(wsp.getWorkspaceDefinition().name.getText()),
                                               description == null ? null
                                                                   : stripQuotes(description.getText()),
                                               model.getCurrentPrincipal()
                                                    .getPrincipal());
        uuid = WorkspaceAccessor.uuidOf(workspaceUri);
        workspaceProduct.setId(uuid);
        em.persist(workspaceProduct);
        return workspaceProduct;
    }

    private Product getWorkspaceProduct() {
        workspaceUri = stripQuotes(wsp.getWorkspaceDefinition().uri.getText());
        uuid = WorkspaceAccessor.uuidOf(workspaceUri);
        Product product = model.getProductModel()
                               .find(uuid);
        if (product == null) {
            throw new IllegalArgumentException(String.format("Unknown workspace: %s",
                                                             workspaceUri));
        }
        return product;
    }

    private void defineFacets(@SuppressWarnings("rawtypes") NetworkedModel networkedModel,
                              List<FacetContext> facets) {
        for (FacetContext facet : facets) {
            if (facet.classification.namespace == null) {
                if (scope.lookup(facet.classification.member.getText()) == null) {
                    @SuppressWarnings("rawtypes")
                    ExistentialRuleform erf = networkedModel.create(facet.name == null ? facet.classification.member.getText()
                                                                                       : stripQuotes(facet.name.getText()),
                                                                    facet.description == null ? null
                                                                                              : stripQuotes(facet.description.getText()));
                    em.persist(erf);
                    workspace.put(facet.classification.member.getText(), erf);
                }
            }
        }
    }

    private void intervalFacets() {
        for (FacetContext facet : wsp.getIntervalFacets()) {
            classifiedAttributes(facet,
                                 new IntervalNetworkAuthorization(model.getCurrentPrincipal()
                                                                       .getPrincipal()),
                                 agency -> new IntervalAttributeAuthorization(model.getCurrentPrincipal()
                                                                                   .getPrincipal()));
            intervalNetworkConstraints(facet);
        }
    }

    private void intervalNetworkConstraints(FacetContext facet) {
        NetworkConstraintsContext networkConstraints = facet.networkConstraints();
        if (networkConstraints == null) {
            return;
        }
        networkConstraints.constraint()
                          .forEach(constraint -> {
                              createNetworkAuth(facet, constraint,
                                                new IntervalNetworkAuthorization(model.getCurrentPrincipal()
                                                                                      .getPrincipal()),
                                                agency -> new IntervalAttributeAuthorization(model.getCurrentPrincipal()
                                                                                                  .getPrincipal()));
                          });
    }

    private void loadAgencies() {
        for (AttributedExistentialRuleformContext ruleform : wsp.getAgencies()) {
            Agency agency = new Agency(stripQuotes(ruleform.existentialRuleform().name.getText()),
                                       ruleform.existentialRuleform().description == null ? null
                                                                                          : stripQuotes(ruleform.existentialRuleform().description.getText()),
                                       model.getCurrentPrincipal()
                                            .getPrincipal());
            em.persist(agency);
            workspace.put(ruleform.existentialRuleform().workspaceName.getText(),
                          agency);
        }
        defineFacets(model.getAgencyModel(), wsp.getAgencyFacets());
    }

    private void loadAttributes() {
        for (AttributeRuleformContext ruleform : wsp.getAttributes()) {
            Attribute attr = new Attribute(stripQuotes(ruleform.existentialRuleform().name.getText()),
                                           ruleform.existentialRuleform().description == null ? null
                                                                                              : stripQuotes(ruleform.existentialRuleform().description.getText()),
                                           model.getCurrentPrincipal()
                                                .getPrincipal());
            setValueType(attr, ruleform.valueType);
            attr.setIndexed(ruleform.indexed == null ? false
                                                     : ruleform.indexed.getText()
                                                                       .equals("true"));
            attr.setKeyed(ruleform.keyed == null ? false
                                                 : ruleform.keyed.getText()
                                                                 .equals("true"));
            em.persist(attr);
            workspace.put(ruleform.existentialRuleform().workspaceName.getText(),
                          attr);
            for (AttributeValueContext av : ruleform.attributeValue()) {
                AttributeMetaAttribute ama = new AttributeMetaAttribute();
                ama.setAttribute(attr);
                Attribute metaAttribute = resolve(av.attribute);
                ama.setMetaAttribute(metaAttribute);
                ama.setUpdatedBy(model.getCurrentPrincipal()
                                      .getPrincipal());
                ama.setSequenceNumber(Integer.parseInt(av.sequenceNumber.getText()));
                ama.setValueFromString(stripQuotes(av.value.getText()));
                workspace.add(ama);
                em.persist(ama);
            }
            if (ruleform.type != null) {
                AttributeMetaAttribute ama = new AttributeMetaAttribute();
                ama.setAttribute(model.getKernel()
                                      .getJsonldType());
                ama.setMetaAttribute(attr);
                ama.setUpdatedBy(model.getCurrentPrincipal()
                                      .getPrincipal());
                ama.setTextValue(stripQuotes(ruleform.type.getText()));
                em.persist(ama);
                workspace.add(ama);
            }
        }
    }

    private void loadChildSequencing() {
        for (ChildSequencingContext seq : wsp.getChildSequencings()) {
            ProductChildSequencingAuthorization auth = new ProductChildSequencingAuthorization(resolve(seq.parent),
                                                                                               resolve(seq.status),
                                                                                               resolve(seq.child),
                                                                                               resolve(seq.next),
                                                                                               model.getCurrentPrincipal()
                                                                                                    .getPrincipal());
            em.persist(auth);
            workspace.add(auth);
        }
    }

    private void loadEdges() {
        loadNetworks(wsp.getAgencyNetworks(), model.getAgencyModel());
        loadNetworks(wsp.getAttributeNetworks(), model.getAttributeModel());
        loadNetworks(wsp.getIntervalNetworks(), model.getIntervalModel());
        loadLocationNetworks();
        loadNetworks(wsp.getProductNetworks(), model.getProductModel());
        loadNetworks(wsp.getRelationshipNetworks(),
                     model.getRelationshipModel());
        loadNetworks(wsp.getStatusCodeNetworks(), model.getStatusCodeModel());
        loadNetworks(wsp.getUnitNetworks(), model.getUnitModel());
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
            NetworkInference inference = new NetworkInference(resolve(edge.parent),
                                                              resolve(edge.relationship),
                                                              resolve(edge.child),
                                                              model.getCurrentPrincipal()
                                                                   .getPrincipal());
            em.persist(inference);
            workspace.add(inference);
        }
    }

    private void loadIntervals() {
        for (AttributedExistentialRuleformContext rf : wsp.getIntervals()) {
            Interval ruleform = new Interval(stripQuotes(rf.existentialRuleform().name.getText()),
                                             rf.existentialRuleform().description == null ? null
                                                                                          : stripQuotes(rf.existentialRuleform().description.getText()),
                                             model.getCurrentPrincipal()
                                                  .getPrincipal());
            em.persist(ruleform);
            workspace.put(rf.existentialRuleform().workspaceName.getText(),
                          ruleform);
        }
        defineFacets(model.getIntervalModel(), wsp.getIntervalFacets());
    }

    private void loadLocationNetworks() {
        loadNetworks(wsp.getLocationNetworks(), model.getLocationModel());
    }

    private void loadLocations() {
        for (AttributedExistentialRuleformContext rf : wsp.getLocations()) {
            Location ruleform = new Location(stripQuotes(rf.existentialRuleform().name.getText()),
                                             rf.existentialRuleform().description == null ? null
                                                                                          : stripQuotes(rf.existentialRuleform().description.getText()),
                                             model.getCurrentPrincipal()
                                                  .getPrincipal());
            em.persist(ruleform);
            workspace.put(rf.existentialRuleform().workspaceName.getText(),
                          ruleform);
        }
        defineFacets(model.getLocationModel(), wsp.getLocationFacets());
    }

    private void loadMetaprotocols() {
        for (MetaProtocolContext mpc : wsp.getMetaProtocols()) {
            MetaProtocol metaProtocol = model.getJobModel()
                                             .newInitializedMetaProtocol(resolve(mpc.service),
                                                                         model.getCurrentPrincipal()
                                                                              .getPrincipal());
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
            if (mpc.match != null && mpc.match.getText()
                                              .equals("stop")) {
                metaProtocol.setStopOnMatch(true);
            }
        }
    }

    private <T extends ExistentialRuleform<T, Network>, Network extends NetworkRuleform<T>> void loadNetworks(List<EdgeContext> edges,
                                                                                                              NetworkedModel<T, ?, ?, ?> networkedModel) {
        for (EdgeContext edge : edges) {
            networkedModel.initialize(resolve(edge.parent),
                                      new Aspect<T>(resolve(edge.relationship),
                                                    resolve(edge.child)),
                                      workspace);
        }
    }

    private void loadParentSequencing() {
        for (ParentSequencingContext seq : wsp.getParentSequencings()) {
            ProductParentSequencingAuthorization auth = new ProductParentSequencingAuthorization(resolve(seq.service),
                                                                                                 resolve(seq.status),
                                                                                                 resolve(seq.parent),
                                                                                                 resolve(seq.next),
                                                                                                 model.getCurrentPrincipal()
                                                                                                      .getPrincipal());
            em.persist(auth);
            workspace.add(auth);
        }
    }

    private void loadProducts() {
        for (AttributedExistentialRuleformContext rf : wsp.getProducts()) {
            Product ruleform = new Product(stripQuotes(rf.existentialRuleform().name.getText()),
                                           rf.existentialRuleform().description == null ? null
                                                                                        : stripQuotes(rf.existentialRuleform().description.getText()),
                                           model.getCurrentPrincipal()
                                                .getPrincipal());
            em.persist(ruleform);
            workspace.put(rf.existentialRuleform().workspaceName.getText(),
                          ruleform);
        }
        defineFacets(model.getProductModel(), wsp.getProductFacets());
    }

    private void loadProtocols() {
        for (ProtocolContext pc : wsp.getProtocols()) {
            Protocol protocol = model.getJobModel()
                                     .newInitializedProtocol(resolve(pc.matchJob().service),
                                                             model.getCurrentPrincipal()
                                                                  .getPrincipal());
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

    private void loadRelationships() {
        for (RelationshipPairContext ctx : wsp.getRelationships()) {
            Relationship relA = model.getRelationshipModel()
                                     .create(stripQuotes(ctx.primary.existentialRuleform().name.getText()),
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
            ProductSelfSequencingAuthorization auth = new ProductSelfSequencingAuthorization(resolve(seq.service),
                                                                                             resolve(seq.status),
                                                                                             resolve(seq.next),
                                                                                             model.getCurrentPrincipal()
                                                                                                  .getPrincipal());
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
            ProductSiblingSequencingAuthorization auth = new ProductSiblingSequencingAuthorization(resolve(seq.parent),
                                                                                                   resolve(seq.status),
                                                                                                   resolve(seq.sibling),
                                                                                                   resolve(seq.next),
                                                                                                   model.getCurrentPrincipal()
                                                                                                        .getPrincipal());
            em.persist(auth);
            workspace.add(auth);
        }
    }

    private void loadStatusCodes() {
        for (AttributedExistentialRuleformContext rf : wsp.getStatusCodes()) {
            StatusCode ruleform = new StatusCode(stripQuotes(rf.existentialRuleform().name.getText()),
                                                 rf.existentialRuleform().description == null ? null
                                                                                              : stripQuotes(rf.existentialRuleform().description.getText()),
                                                 model.getCurrentPrincipal()
                                                      .getPrincipal());
            em.persist(ruleform);
            workspace.put(rf.existentialRuleform().workspaceName.getText(),
                          ruleform);
        }
        defineFacets(model.getStatusCodeModel(), wsp.getStatusCodeFacets());
    }

    private void loadStatusCodeSequencings() {
        for (StatusCodeSequencingSetContext entry : wsp.getStatusCodeSequencings()) {
            Product service = resolve(entry.service);
            for (SequencePairContext pair : entry.sequencePair()) {
                StatusCode first = resolve(pair.first);
                StatusCode second = resolve(pair.second);
                StatusCodeSequencing sequence = new StatusCodeSequencing(service,
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

    private void loadUnits() {
        for (UnitContext unit : wsp.getUnits()) {
            Token description = unit.existentialRuleform().description;
            Unit ruleform = new Unit(stripQuotes(unit.existentialRuleform().name.getText()),
                                     description == null ? null
                                                         : stripQuotes(description.getText()),
                                     model.getCurrentPrincipal()
                                          .getPrincipal());
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

    private void loadWorkspace() {
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
    }

    private void locationFacets() {
        for (FacetContext facet : wsp.getLocationFacets()) {
            classifiedAttributes(facet,
                                 new LocationNetworkAuthorization(model.getCurrentPrincipal()
                                                                       .getPrincipal()),
                                 agency -> new LocationAttributeAuthorization(model.getCurrentPrincipal()
                                                                                   .getPrincipal()));
            locationNetworkConstraints(facet);
        }
    }

    private void locationNetworkConstraints(FacetContext facet) {
        NetworkConstraintsContext networkConstraints = facet.networkConstraints();
        if (networkConstraints == null) {
            return;
        }
        networkConstraints.constraint()
                          .forEach(constraint -> {
                              Class<?> authParentClass = resolveAuthParent(constraint);
                              if (authParentClass.equals(Location.class)) {
                                  createNetworkAuth(facet, constraint,
                                                    new LocationNetworkAuthorization(model.getCurrentPrincipal()
                                                                                          .getPrincipal()),
                                                    agency -> new LocationAttributeAuthorization(model.getCurrentPrincipal()
                                                                                                      .getPrincipal()));
                              } else if (authParentClass.equals(Agency.class)) {
                                  createLocationAgencyAuth(facet, constraint);
                              } else
                                  if (authParentClass.equals(Product.class)) {
                                  createLocationProductAuth(facet, constraint);
                              } else if (authParentClass.equals(Relationship.class)) {
                                  createRelationshipProductAuth(facet,
                                                                constraint);
                              }
                          });
    }

    private void processImports() {
        for (ImportedWorkspaceContext w : wsp.getImports()) {
            String uri = stripQuotes(w.uri.getText());
            UUID uuid = WorkspaceAccessor.uuidOf(uri);
            workspace.addImport(w.namespace.getText(), model.getEntityManager()
                                                            .find(Product.class,
                                                                  uuid));
        }
    }

    private void productFacets() {
        for (FacetContext facet : wsp.getProductFacets()) {
            classifiedAttributes(facet,
                                 new ProductNetworkAuthorization(model.getCurrentPrincipal()
                                                                      .getPrincipal()),
                                 agency -> new ProductAttributeAuthorization(model.getCurrentPrincipal()
                                                                                  .getPrincipal()));
            productNetworkConstraints(facet);
        }
    }

    private void productNetworkConstraints(FacetContext facet) {
        NetworkConstraintsContext networkConstraints = facet.networkConstraints();
        if (networkConstraints == null) {
            return;
        }
        networkConstraints.constraint()
                          .forEach(constraint -> {
                              Class<?> authParentClass = resolveAuthParent(constraint);
                              if (authParentClass.equals(Product.class)) {
                                  createNetworkAuth(facet, constraint,
                                                    new ProductNetworkAuthorization(model.getCurrentPrincipal()
                                                                                         .getPrincipal()),
                                                    agency -> new ProductAttributeAuthorization(model.getCurrentPrincipal()
                                                                                                     .getPrincipal()));
                              } else if (authParentClass.equals(Agency.class)) {
                                  createProductAgencyAuth(facet, constraint);
                              } else
                                  if (authParentClass.equals(Location.class)) {
                                  createProductLocationAuth(facet, constraint);
                              } else if (authParentClass.equals(Relationship.class)) {
                                  createProductRelationshipAuth(facet,
                                                                constraint);
                              }
                          });
    }

    private void relationshipFacets() {
        for (FacetContext facet : wsp.getRelationshipFacets()) {
            classifiedAttributes(facet,
                                 new RelationshipNetworkAuthorization(model.getCurrentPrincipal()
                                                                           .getPrincipal()),
                                 agency -> new RelationshipAttributeAuthorization(model.getCurrentPrincipal()
                                                                                       .getPrincipal()));
            relationshipNetworkConstraints(facet);
        }
    }

    private void relationshipNetworkConstraints(FacetContext facet) {
        NetworkConstraintsContext networkConstraints = facet.networkConstraints();
        if (networkConstraints == null) {
            return;
        }
        networkConstraints.constraint()
                          .forEach(constraint -> {
                              Class<?> authParentClass = resolveAuthParent(constraint);
                              if (authParentClass.equals(Product.class)) {
                                  createRelationshipProductAuth(facet,
                                                                constraint);
                              } else {
                                  createNetworkAuth(facet, constraint,
                                                    new RelationshipNetworkAuthorization(model.getCurrentPrincipal()
                                                                                              .getPrincipal()),
                                                    agency -> new RelationshipAttributeAuthorization(model.getCurrentPrincipal()
                                                                                                          .getPrincipal()));
                              }
                          });
    }

    @SuppressWarnings("unchecked")
    private <T extends Ruleform> T resolve(QualifiedNameContext qualifiedName) {
        if (qualifiedName == null) {
            throw new IllegalArgumentException("Qualified name is null");
        }
        T ruleform;
        if (qualifiedName.namespace != null) {
            ruleform = (T) scope.lookup(qualifiedName.namespace.getText(),
                                        qualifiedName.member.getText());
            return ruleform;
        } else if (qualifiedName.member.getText()
                                       .equals(THIS)) {
            ruleform = (T) workspace.getDefiningProduct();
        } else {
            ruleform = workspace.get(qualifiedName.member.getText());
        }
        if (ruleform == null) {
            if (ruleform == null) {
                throw new InvalidKeyException(String.format("Cannot resolve %s:%s",
                                                            qualifiedName.namespace == null ? ""
                                                                                            : qualifiedName.namespace.getText(),
                                                            qualifiedName.member.getText()));
            }
        }
        return ruleform;
    }

    private Class<? extends Ruleform> resolveAny(String anyType) {
        switch (anyType) {
            case "*Agency":
                return Agency.class;
            case "*Attribute":
                return Attribute.class;
            case "*Interval":
                return Interval.class;
            case "*Location":
                return Location.class;
            case "*Product":
                return Product.class;
            case "*Relationship":
                return Relationship.class;
            case "*StatusCode":
                return StatusCode.class;
            case "*Unit":
                return Unit.class;
            default:
                throw new IllegalArgumentException(String.format("Invalid *Any type: %s",
                                                                 anyType));
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends ExistentialRuleform<T, Network>, Network extends NetworkRuleform<T>> T resolveAnyEntity(String anyType) {
        switch (anyType) {
            case "*Agency":
                return (T) model.getKernel()
                                .getAnyAgency();
            case "*Attribute":
                return (T) model.getKernel()
                                .getAnyAttribute();
            case "*Interval":
                return (T) model.getKernel()
                                .getAnyInterval();
            case "*Location":
                return (T) model.getKernel()
                                .getAnyLocation();
            case "*Product":
                return (T) model.getKernel()
                                .getAnyProduct();
            case "*Relationship":
                return (T) model.getKernel()
                                .getAnyRelationship();
            case "*StatusCode":
                return (T) model.getKernel()
                                .getAnyStatusCode();
            case "*Unit":
                return (T) model.getKernel()
                                .getAnyUnit();
            default:
                throw new IllegalArgumentException(String.format("Invalid *Any type: %s",
                                                                 anyType));
        }
    }

    private <T extends ExistentialRuleform<T, Network>, Network extends NetworkRuleform<T>> void resolveAuthorized(ConstraintContext constraint,
                                                                                                                   NetworkAuthorization<T> authorization) {
        if (constraint.anyType == null) {
            authorization.setAuthorizedParent(resolve(constraint.authorizedParent));
            authorization.setAuthorizedRelationship(resolve(constraint.authorizedRelationship));
        } else {
            authorization.setAuthorizedParent(resolveAnyEntity(constraint.anyType.getText()));
            authorization.setAuthorizedRelationship(model.getKernel()
                                                         .getAnyRelationship());
        }
    }

    private Class<? extends Ruleform> resolveAuthParent(ConstraintContext constraint) {
        return constraint.anyType == null ? resolve(constraint.authorizedParent).getClass()
                                          : resolveAny(constraint.anyType.getText());
    }

    private <T extends ExistentialRuleform<T, Network>, Network extends NetworkRuleform<T>> void resolveFrom(ConstraintContext constraint,
                                                                                                             XDomainNetworkAuthorization<T, ?> authorization) {

        authorization.setName(networkAuthNameOf(constraint));
        if (constraint.anyType == null) {
            authorization.setFromParent(resolve(constraint.authorizedParent));
            authorization.setFromRelationship(resolve(constraint.authorizedRelationship));
        } else {
            authorization.setFromParent(resolveAnyEntity(constraint.anyType.getText()));
            authorization.setFromRelationship(model.getKernel()
                                                   .getAnyRelationship());
        }
    }

    private <T extends ExistentialRuleform<T, Network>, Network extends NetworkRuleform<T>> void resolveTo(ConstraintContext constraint,
                                                                                                           XDomainNetworkAuthorization<?, T> authorization) {
        if (constraint.anyType == null) {
            authorization.setToParent(resolve(constraint.authorizedParent));
            authorization.setToRelationship(resolve(constraint.authorizedRelationship));
        } else {
            authorization.setToParent(resolveAnyEntity(constraint.anyType.getText()));
            authorization.setToRelationship(model.getKernel()
                                                 .getAnyRelationship());
        }
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
                throw new IllegalArgumentException(String.format("Invalid attribute value type: %s for %s",
                                                                 valueType.getText(),
                                                                 attr));
        }
    }

    private void statusCodeFacets() {
        for (FacetContext facet : wsp.getStatusCodeFacets()) {
            classifiedAttributes(facet,
                                 new StatusCodeNetworkAuthorization(model.getCurrentPrincipal()
                                                                         .getPrincipal()),
                                 agency -> new StatusCodeAttributeAuthorization(model.getCurrentPrincipal()
                                                                                     .getPrincipal()));
            statusCodeNetworkConstraints(facet);
        }
    }

    private void statusCodeNetworkConstraints(FacetContext facet) {
        NetworkConstraintsContext networkConstraints = facet.networkConstraints();
        if (networkConstraints == null) {
            return;
        }
        networkConstraints.constraint()
                          .forEach(constraint -> {
                              createNetworkAuth(facet, constraint,
                                                new StatusCodeNetworkAuthorization(model.getCurrentPrincipal()
                                                                                        .getPrincipal()),
                                                agency -> new StatusCodeAttributeAuthorization(model.getCurrentPrincipal()
                                                                                                    .getPrincipal()));
                          });
    }

    private String stripQuotes(String original) {
        return original.substring(1, original.length() - 1);
    }

    private void unitFacets() {
        for (FacetContext facet : wsp.getUnitFacets()) {
            classifiedAttributes(facet,
                                 new UnitNetworkAuthorization(model.getCurrentPrincipal()
                                                                   .getPrincipal()),
                                 unit -> new UnitAttributeAuthorization(model.getCurrentPrincipal()
                                                                             .getPrincipal()));
            unitNetworkConstraints(facet);
        }
    }

    private void unitNetworkConstraints(FacetContext facet) {
        NetworkConstraintsContext networkConstraints = facet.networkConstraints();
        if (networkConstraints == null) {
            return;
        }
        networkConstraints.constraint()
                          .forEach(constraint -> {
                              createNetworkAuth(facet, constraint,
                                                new UnitNetworkAuthorization(model.getCurrentPrincipal()
                                                                                  .getPrincipal()),
                                                agency -> new UnitAttributeAuthorization(model.getCurrentPrincipal()
                                                                                              .getPrincipal()));
                          });
    }
}
