/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.meta.models;

import static com.chiralbehaviors.CoRE.jooq.Tables.EDGE_PROPERTY;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL_NETWORK_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.FACET;
import static com.chiralbehaviors.CoRE.jooq.Tables.FACET_PROPERTY;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;

import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.EdgePropertyRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetPropertyRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.PhantasmModel;
import com.chiralbehaviors.CoRE.meta.workspace.EditableWorkspace;
import com.hellblazer.utils.Tuple;

/**
 * @author hhildebrand
 *
 */
public class PhantasmModelImpl implements PhantasmModel {
    private static final Integer ZERO = Integer.valueOf(0);

    private final DSLContext     create;
    private final Model          model;

    public PhantasmModelImpl(Model model) {
        this.model = model;
        create = model.create();
    }

    @SafeVarargs
    @Override
    public final <T extends ExistentialRuleform> T create(ExistentialDomain domain,
                                                          String name,
                                                          String description,
                                                          FacetRecord aspect,
                                                          FacetRecord... aspects) {
        ExistentialRuleform instance = model.records()
                                            .newExistential(domain);
        model.getPhantasmModel()
             .initialize(instance, aspect);
        for (FacetRecord additional : aspects) {
            model.getPhantasmModel()
                 .initialize(instance, additional);
        }
        @SuppressWarnings("unchecked")
        T cazt = (T) instance;
        return cazt;
    }

    @Override
    public ExistentialRuleform getChild(ExistentialRuleform parent,
                                        Relationship relationship,
                                        ExistentialDomain domain) {
        Record result = create.selectDistinct(EXISTENTIAL.fields())
                              .from(EXISTENTIAL)
                              .join(EXISTENTIAL_NETWORK)
                              .on(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                              .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                              .and(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL.ID))
                              .and(EXISTENTIAL.DOMAIN.equal(domain))
                              .fetchOne();
        if (result == null) {
            return null;
        }
        return model.records()
                    .resolve(result.into(ExistentialRecord.class));
    }

    @Override
    public List<ExistentialRuleform> getChildren(ExistentialRuleform parent,
                                                 Relationship relationship,
                                                 ExistentialDomain domain) {
        return getChildrenUuid(parent.getId(), relationship.getId(), domain);
    }

    @Override
    public List<ExistentialNetworkRecord> getChildrenLinks(ExistentialRuleform parent,
                                                           Relationship relationship) {

        return create.selectFrom(EXISTENTIAL_NETWORK)
                     .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                     .fetch();
    }

    @Override
    public List<ExistentialRuleform> getChildrenUuid(UUID parent,
                                                     UUID relationship,
                                                     ExistentialDomain domain) {
        return create.selectDistinct(EXISTENTIAL.fields())
                     .from(EXISTENTIAL)
                     .join(EXISTENTIAL_NETWORK)
                     .on(EXISTENTIAL_NETWORK.PARENT.equal(parent))
                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship))
                     .and(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL.ID))
                     .and(EXISTENTIAL.DOMAIN.equal(domain))
                     .fetch()
                     .into(ExistentialRecord.class)
                     .stream()
                     .map(r -> model.records()
                                    .resolve(r))
                     .collect(Collectors.toList());
    }

    @Override
    public List<ExistentialRuleform> getConstrainedChildren(ExistentialRuleform parent,
                                                            Relationship relationship,
                                                            Relationship classifier,
                                                            ExistentialRuleform classification,
                                                            ExistentialDomain existentialDomain) {
        return getConstrainedChildren(parent.getId(), relationship.getId(),
                                      classifier.getId(),
                                      classification.getId(),
                                      existentialDomain);
    }

    @Override
    public EdgePropertyRecord getEdgeProperties(ExistentialNetworkAuthorizationRecord auth,
                                                ExistentialNetworkRecord edge) {
        return create.select(EDGE_PROPERTY.fields())
                     .from(EDGE_PROPERTY)
                     .where(EDGE_PROPERTY.EDGE.equal(edge.getId()))
                     .and(EDGE_PROPERTY.AUTH.equal(auth.getId()))
                     .fetchSingle()
                     .into(EdgePropertyRecord.class);
    }

    @Override
    public EdgePropertyRecord getEdgeProperties(ExistentialRuleform parent,
                                                ExistentialNetworkAuthorizationRecord auth,
                                                ExistentialRuleform child) {
        return create.select(EDGE_PROPERTY.fields())
                     .from(EDGE_PROPERTY)
                     .join(EXISTENTIAL_NETWORK)
                     .on(EDGE_PROPERTY.EDGE.equal(EXISTENTIAL_NETWORK.ID))
                     .and(EDGE_PROPERTY.AUTH.equal(auth.getId()))
                     .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(auth.getRelationship()))
                     .and(EXISTENTIAL_NETWORK.CHILD.equal(parent.getId()))
                     .fetchSingle()
                     .into(EdgePropertyRecord.class);
    }

    @Override
    public FacetRecord getFacetDeclaration(Relationship classifier,
                                           ExistentialRuleform classification) {
        return create.selectFrom(FACET)
                     .where(FACET.CLASSIFIER.equal(classifier.getId()))
                     .and(FACET.CLASSIFICATION.equal(classification.getId()))
                     .fetchOne();
    }

    @Override
    public FacetPropertyRecord getFacetProperties(FacetRecord facet,
                                                  ExistentialRuleform existential) {
        return create.select(FACET_PROPERTY.fields())
                     .from(FACET_PROPERTY)
                     .where(FACET_PROPERTY.FACET.equal(facet.getId()))
                     .and(FACET_PROPERTY.EXISTENTIAL.equal(existential.getId()))
                     .fetchOne()
                     .into(FacetPropertyRecord.class);
    }

    @Override
    public List<FacetRecord> getFacets(Product workspace) {
        return create.selectDistinct(FACET.fields())
                     .from(FACET)
                     .join(EXISTENTIAL)
                     .on(FACET.CLASSIFICATION.equal(EXISTENTIAL.ID))
                     .and(FACET.AUTHORITY.isNull())
                     .where(FACET.WORKSPACE.equal(workspace.getId()))
                     .fetch()
                     .into(FacetRecord.class);
    }

    @Override
    public ExistentialRuleform getImmediateChild(ExistentialRuleform parent,
                                                 Relationship relationship,
                                                 ExistentialDomain domain) {
        Record result = create.selectDistinct(EXISTENTIAL.fields())
                              .from(EXISTENTIAL)
                              .join(EXISTENTIAL_NETWORK)
                              .on(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                              .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                              .and(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL.ID))
                              .and(EXISTENTIAL.DOMAIN.equal(domain))
                              .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
                              .fetchOne();
        if (result == null) {
            return null;
        }
        return model.records()
                    .resolve(result.into(ExistentialRecord.class));
    }

    @Override
    public ExistentialNetworkRecord getImmediateChildLink(ExistentialRuleform parent,
                                                          Relationship relationship,
                                                          ExistentialRuleform child) {
        ExistentialNetworkRecord result = create.selectFrom(EXISTENTIAL_NETWORK)
                                                .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                                                .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                                                .and(EXISTENTIAL_NETWORK.CHILD.equal(child.getId()))
                                                .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
                                                .fetchOne();
        if (result == null) {
            return null;
        }
        return result.into(ExistentialNetworkRecord.class);
    }

    @Override
    public List<ExistentialRuleform> getImmediateChildren(ExistentialRuleform parent,
                                                          Relationship relationship,
                                                          ExistentialDomain domain) {
        return getImmediateChildren(parent.getId(), relationship.getId(),
                                    domain);
    }

    @Override
    public List<ExistentialNetworkRecord> getImmediateChildrenLinks(ExistentialRuleform parent,
                                                                    Relationship relationship,
                                                                    ExistentialDomain domain) {
        Result<Record> result = create.selectDistinct(EXISTENTIAL_NETWORK.fields())
                                      .from(EXISTENTIAL_NETWORK)
                                      .join(EXISTENTIAL)
                                      .on(EXISTENTIAL.DOMAIN.equal(domain))
                                      .and(EXISTENTIAL.ID.equal(EXISTENTIAL_NETWORK.CHILD))
                                      .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                                      .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                                      .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
                                      .fetch();
        if (result == null) {
            return null;
        }
        return result.into(ExistentialNetworkRecord.class);
    }

    @Override
    public List<ExistentialRuleform> getImmediateConstrainedChildren(ExistentialRuleform parent,
                                                                     Relationship relationship,
                                                                     Relationship classifier,
                                                                     ExistentialRuleform classification,
                                                                     ExistentialDomain existentialDomain) {
        return getImmediateConstrainedChildren(parent.getId(),
                                               relationship.getId(),
                                               classifier.getId(),
                                               classification.getId(),
                                               existentialDomain);
    }

    @Override
    public ExistentialNetworkRecord getImmediateLink(ExistentialRuleform parent,
                                                     Relationship relationship,
                                                     ExistentialRuleform child) {
        ExistentialNetworkRecord result = create.selectFrom(EXISTENTIAL_NETWORK)
                                                .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                                                .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                                                .and(EXISTENTIAL_NETWORK.CHILD.equal(child.getId()))
                                                .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
                                                .fetchOne();
        if (result == null) {
            return null;
        }
        return result.into(ExistentialNetworkRecord.class);
    }

    @Override
    public ExistentialNetworkAuthorizationRecord getNetworkAuthorization(FacetRecord aspect,
                                                                         Relationship relationship,
                                                                         boolean includeGrouping) {

        SelectConditionStep<ExistentialNetworkAuthorizationRecord> and = create.selectFrom(EXISTENTIAL_NETWORK_AUTHORIZATION)
                                                                               .where(EXISTENTIAL_NETWORK_AUTHORIZATION.PARENT.equal(aspect.getId()))
                                                                               .and(EXISTENTIAL_NETWORK_AUTHORIZATION.RELATIONSHIP.equal(relationship.getId()));

        if (!includeGrouping) {
            and = and.and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORITY.isNull());
        }
        return and.fetchSingle();
    }

    @Override
    public List<ExistentialNetworkAuthorizationRecord> getNetworkAuthorizations(FacetRecord aspect,
                                                                                boolean includeGrouping) {

        SelectConditionStep<ExistentialNetworkAuthorizationRecord> and = create.selectFrom(EXISTENTIAL_NETWORK_AUTHORIZATION)
                                                                               .where(EXISTENTIAL_NETWORK_AUTHORIZATION.PARENT.equal(aspect.getId()));

        if (!includeGrouping) {
            and = and.and(EXISTENTIAL_NETWORK_AUTHORIZATION.AUTHORITY.isNull());
        }
        return and.fetch()
                  .into(ExistentialNetworkAuthorizationRecord.class);
    }

    @Override
    public List<ExistentialRuleform> getNotInGroup(ExistentialRuleform parent,
                                                   Relationship relationship,
                                                   ExistentialDomain domain) {

        return create.selectFrom(EXISTENTIAL)
                     .whereNotExists(create.selectFrom(EXISTENTIAL_NETWORK)
                                           .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                                           .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                                           .and(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL.ID)))
                     .and(EXISTENTIAL.DOMAIN.equal(domain))
                     .fetch()
                     .into(ExistentialRecord.class)
                     .stream()
                     .map(r -> model.records()
                                    .resolve(r))
                     .collect(Collectors.toList());
    }

    @Override
    public FacetPropertyRecord getProperties(ExistentialRuleform existential,
                                             FacetRecord facet) {
        return create.selectDistinct(FACET_PROPERTY.fields())
              .from(FACET_PROPERTY)
              .where(FACET_PROPERTY.EXISTENTIAL.eq(existential.getId()))
              .and(FACET_PROPERTY.FACET.eq(facet.getId()))
              .fetchOne().into(FacetPropertyRecord.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ExistentialRuleform> T getSingleChild(ExistentialRuleform parent,
                                                            Relationship relationship,
                                                            ExistentialDomain domain) {
        Record result = create.selectDistinct(EXISTENTIAL.fields())
                              .from(EXISTENTIAL)
                              .join(EXISTENTIAL_NETWORK)
                              .on(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
                              .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
                              .and(EXISTENTIAL_NETWORK.CHILD.equal(EXISTENTIAL.ID))
                              .where(EXISTENTIAL.ID.equal(EXISTENTIAL_NETWORK.CHILD))
                              .and(EXISTENTIAL.DOMAIN.equal(domain))
                              .fetchOne();
        if (result == null) {
            return null;
        }
        return (T) model.records()
                        .resolve(result.into(ExistentialRecord.class));
    }

    @Override
    public final void initialize(ExistentialRuleform ruleform,
                                 FacetRecord aspect) {
        initialize(ruleform, aspect, null);
    }

    @Override
    public final void initialize(ExistentialRuleform ruleform,
                                 FacetRecord aspect,
                                 EditableWorkspace workspace) {
        if (!isAccessible(ruleform.getId(), aspect.getClassifier(),
                          aspect.getClassification())) {
            UUID inverseRelationship = ((Relationship) model.records()
                                                            .resolve(aspect.getClassifier())).getInverse();
            Tuple<ExistentialNetworkRecord, ExistentialNetworkRecord> links = link(ruleform.getId(),
                                                                                   aspect.getClassifier(),
                                                                                   aspect.getClassification(),
                                                                                   inverseRelationship);
            if (workspace != null) {
                workspace.add(links.a);
                workspace.add(links.b);
            }
        }
        FacetPropertyRecord properties = model.records()
                                              .newFacetProperty();
        properties.setExistential(ruleform.getId());
        properties.setFacet(aspect.getId());
        properties.setProperties(aspect.getDefaultProperties());
        properties.insert();
    }

    @Override
    public boolean isAccessible(UUID parent, UUID relationship, UUID child) {
        assert parent != null && relationship != null && child != null;
        return !ZERO.equals(create.selectCount()
                                  .from(EXISTENTIAL_NETWORK)
                                  .where(EXISTENTIAL_NETWORK.PARENT.equal(parent))
                                  .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship))
                                  .and(EXISTENTIAL_NETWORK.CHILD.equal(child))
                                  .and(EXISTENTIAL_NETWORK.CHILD.notEqual(parent))
                                  .fetchOne()
                                  .value1());
    }

    @Override
    public Tuple<ExistentialNetworkRecord, ExistentialNetworkRecord> link(ExistentialRuleform parent,
                                                                          Relationship r,
                                                                          ExistentialRuleform child) {
        return link(parent.getId(), r.getId(), child.getId(), r.getInverse());
    }

    public Tuple<ExistentialNetworkRecord, ExistentialNetworkRecord> link(UUID parent,
                                                                          UUID r,
                                                                          UUID child,
                                                                          UUID inverseR) {
        ExistentialNetworkRecord forward = model.records()
                                                .newExistentialNetwork();
        forward.setParent(parent);
        forward.setRelationship(r);
        forward.setChild(child);
        forward.insert();

        ExistentialNetworkRecord inverse = model.records()
                                                .newExistentialNetwork();
        inverse.setParent(child);
        inverse.setRelationship(inverseR);
        inverse.setChild(parent);
        inverse.insert();
        return new Tuple<>(forward, inverse);
    }

    @Override
    public void setImmediateChild(ExistentialRuleform parent,
                                  Relationship relationship,
                                  ExistentialRuleform child) {

        unlinkImmediate(parent, relationship);
        link(parent, relationship, child);

    }

    @Override
    public void unlink(ExistentialRuleform parent, Relationship relationship,
                       ExistentialRuleform child) {
        create.deleteFrom(EXISTENTIAL_NETWORK)
              .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
              .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
              .and(EXISTENTIAL_NETWORK.CHILD.equal(child.getId()))
              .execute();
        create.deleteFrom(EXISTENTIAL_NETWORK)
              .where(EXISTENTIAL_NETWORK.PARENT.equal(child.getId()))
              .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getInverse()))
              .and(EXISTENTIAL_NETWORK.CHILD.equal(parent.getId()))
              .execute();
    }

    @Override
    public void unlinkImmediate(ExistentialRuleform parent,
                                Relationship relationship) {
        create.deleteFrom(EXISTENTIAL_NETWORK)
              .where(EXISTENTIAL_NETWORK.PARENT.equal(parent.getId()))
              .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getId()))
              .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
              .execute();
        create.deleteFrom(EXISTENTIAL_NETWORK)
              .where(EXISTENTIAL_NETWORK.CHILD.equal(parent.getId()))
              .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship.getInverse()))
              .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
              .execute();
    }

    private List<ExistentialRuleform> getConstrainedChildren(UUID parent,
                                                             UUID relationship,
                                                             UUID classifier,
                                                             UUID classification,
                                                             ExistentialDomain domain) {
        return create.select(EXISTENTIAL.fields())
                     .from(EXISTENTIAL, EXISTENTIAL_NETWORK)
                     .where(EXISTENTIAL_NETWORK.PARENT.equal(parent))
                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship))
                     .and(EXISTENTIAL.ID.equal(EXISTENTIAL_NETWORK.CHILD))
                     .and(EXISTENTIAL.DOMAIN.equal(domain))
                     .fetch()
                     .into(ExistentialRecord.class)
                     .stream()
                     .map(r -> model.records()
                                    .resolve(r))
                     .filter(r -> isAccessible(r.getId(), classifier,
                                               classification))
                     .collect(Collectors.toList());
    }

    private List<ExistentialRuleform> getImmediateChildren(UUID parent,
                                                           UUID relationship,
                                                           ExistentialDomain domain) {
        return create.select(EXISTENTIAL.fields())
                     .from(EXISTENTIAL, EXISTENTIAL_NETWORK)
                     .where(EXISTENTIAL_NETWORK.PARENT.equal(parent))
                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship))
                     .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
                     .and(EXISTENTIAL.ID.equal(EXISTENTIAL_NETWORK.CHILD))
                     .and(EXISTENTIAL.DOMAIN.equal(domain))
                     .fetch()
                     .into(ExistentialRecord.class)
                     .stream()
                     .map(r -> model.records()
                                    .resolve(r))
                     .collect(Collectors.toList());
    }

    private List<ExistentialRuleform> getImmediateConstrainedChildren(UUID parent,
                                                                      UUID relationship,
                                                                      UUID classifier,
                                                                      UUID classification,
                                                                      ExistentialDomain domain) {
        return create.select(EXISTENTIAL.fields())
                     .from(EXISTENTIAL, EXISTENTIAL_NETWORK)
                     .where(EXISTENTIAL_NETWORK.PARENT.equal(parent))
                     .and(EXISTENTIAL_NETWORK.RELATIONSHIP.equal(relationship))
                     .and(EXISTENTIAL_NETWORK.INFERENCE.isNull())
                     .and(EXISTENTIAL.ID.equal(EXISTENTIAL_NETWORK.CHILD))
                     .and(EXISTENTIAL.DOMAIN.equal(domain))
                     .fetch()
                     .into(ExistentialRecord.class)
                     .stream()
                     .map(r -> model.records()
                                    .resolve(r))
                     .filter(r -> isAccessible(r.getId(), classifier,
                                               classification))
                     .collect(Collectors.toList());
    }
}
