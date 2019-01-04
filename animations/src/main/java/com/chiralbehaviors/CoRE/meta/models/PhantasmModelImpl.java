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

import static com.chiralbehaviors.CoRE.jooq.Tables.*;
import static com.chiralbehaviors.CoRE.jooq.Tables.EXISTENTIAL;
import static com.chiralbehaviors.CoRE.jooq.Tables.EDGE;
import static com.chiralbehaviors.CoRE.jooq.Tables.EDGE_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.jooq.Tables.FACET;
import static com.chiralbehaviors.CoRE.jooq.Tables.FACET_PROPERTY;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectOnConditionStep;

import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.domain.Relationship;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.EdgePropertyRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.EdgeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.EdgeRecord;
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
        SelectOnConditionStep<Record> statement = create.selectDistinct(EXISTENTIAL.fields())
                                                        .from(EXISTENTIAL)
                                                        .join(EDGE)
                                                        .on(EDGE.PARENT.equal(parent.getId()))
                                                        .and(EDGE.RELATIONSHIP.equal(relationship.getId()))
                                                        .and(EDGE.CHILD.equal(EXISTENTIAL.ID));
        if (domain != null) {
            statement = statement.and(EXISTENTIAL.DOMAIN.equal(domain));
        }
        Record result = statement.fetchOne();
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
    public List<EdgeRecord> getChildrenLinks(ExistentialRuleform parent,
                                             Relationship relationship) {

        return create.selectFrom(EDGE)
                     .where(EDGE.PARENT.equal(parent.getId()))
                     .and(EDGE.RELATIONSHIP.equal(relationship.getId()))
                     .fetch();
    }

    @Override
    public List<ExistentialRuleform> getChildrenUuid(UUID parent,
                                                     UUID relationship,
                                                     ExistentialDomain domain) {
        SelectOnConditionStep<Record> statement = create.selectDistinct(EXISTENTIAL.fields())
                                                        .from(EXISTENTIAL)
                                                        .join(EDGE)
                                                        .on(EDGE.PARENT.equal(parent))
                                                        .and(EDGE.RELATIONSHIP.equal(relationship))
                                                        .and(EDGE.CHILD.equal(EXISTENTIAL.ID));
        if (domain != null) {
            statement = statement.and(EXISTENTIAL.DOMAIN.equal(domain));
        }
        return statement.fetch()
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
    public EdgePropertyRecord getEdgeProperties(EdgeAuthorizationRecord auth,
                                                EdgeRecord edge) {
        return create.selectFrom(EDGE_PROPERTY)
                     .where(EDGE_PROPERTY.EDGE.equal(edge.getId()))
                     .and(EDGE_PROPERTY.AUTH.equal(auth.getId()))
                     .fetchOne();
    }

    @Override
    public EdgePropertyRecord getEdgeProperties(ExistentialRuleform parent,
                                                EdgeAuthorizationRecord auth,
                                                ExistentialRuleform child) {
        Record fetched = create.select(EDGE_PROPERTY.fields())
                               .from(EDGE_PROPERTY)
                               .join(EDGE)
                               .on(EDGE_PROPERTY.EDGE.equal(EDGE.ID))
                               .and(EDGE_PROPERTY.AUTH.equal(auth.getId()))
                               .where(EDGE.PARENT.equal(parent.getId()))
                               .and(EDGE.RELATIONSHIP.equal(auth.getRelationship()))
                               .and(EDGE.CHILD.equal(child.getId()))
                               .fetchOne();
        return fetched == null ? null : fetched.into(EdgePropertyRecord.class);
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
        return create.selectFrom(FACET_PROPERTY)
                     .where(FACET_PROPERTY.FACET.equal(facet.getId()))
                     .and(FACET_PROPERTY.EXISTENTIAL.equal(existential.getId()))
                     .fetchOne();
    }

    @Override
    public List<FacetRecord> getFacets(Product workspace) {
        return create.selectDistinct(FACET.fields())
                     .from(FACET)
                     .join(WORKSPACE_LABEL)
                     .on(WORKSPACE_LABEL.REFERENCE.equal(FACET.ID))
                     .where(WORKSPACE_LABEL.WORKSPACE.equal(workspace.getId()))
                     .fetch()
                     .into(FacetRecord.class);
    }

    @Override
    public ExistentialRuleform getImmediateChild(ExistentialRuleform parent,
                                                 Relationship relationship,
                                                 ExistentialDomain domain) {
        Record result = domain != null ? create.selectDistinct(EXISTENTIAL.fields())
                                               .from(EXISTENTIAL)
                                               .join(EDGE)
                                               .on(EDGE.PARENT.equal(parent.getId()))
                                               .and(EDGE.RELATIONSHIP.equal(relationship.getId()))
                                               .and(EDGE.CHILD.equal(EXISTENTIAL.ID))
                                               .and(EXISTENTIAL.DOMAIN.equal(domain))
                                               .fetchOne()
                                       : create.selectDistinct(EXISTENTIAL.fields())
                                               .from(EXISTENTIAL)
                                               .join(EDGE)
                                               .on(EDGE.PARENT.equal(parent.getId()))
                                               .and(EDGE.RELATIONSHIP.equal(relationship.getId()))
                                               .and(EDGE.CHILD.equal(EXISTENTIAL.ID))
                                               .fetchOne();
        ;
        if (result == null) {
            return null;
        }
        return model.records()
                    .resolve(result.into(ExistentialRecord.class));
    }

    @Override
    public EdgeRecord getImmediateChildLink(ExistentialRuleform parent,
                                            Relationship relationship,
                                            ExistentialRuleform child) {
        EdgeRecord result = create.selectFrom(EDGE)
                                  .where(EDGE.PARENT.equal(parent.getId()))
                                  .and(EDGE.RELATIONSHIP.equal(relationship.getId()))
                                  .and(EDGE.CHILD.equal(child.getId()))
                                  .fetchOne();
        if (result == null) {
            return null;
        }
        return result.into(EdgeRecord.class);
    }

    @Override
    public List<ExistentialRuleform> getImmediateChildren(ExistentialRuleform parent,
                                                          Relationship relationship,
                                                          ExistentialDomain domain) {
        return getImmediateChildren(parent.getId(), relationship.getId(),
                                    domain);
    }

    @Override
    public List<EdgeRecord> getImmediateChildrenLinks(ExistentialRuleform parent,
                                                      Relationship relationship,
                                                      ExistentialDomain domain) {
        Result<Record> result = domain != null ? create.selectDistinct(EDGE.fields())
                                                       .from(EDGE)
                                                       .join(EXISTENTIAL)
                                                       .on(EXISTENTIAL.DOMAIN.equal(domain))
                                                       .and(EXISTENTIAL.ID.equal(EDGE.CHILD))
                                                       .where(EDGE.PARENT.equal(parent.getId()))
                                                       .and(EDGE.RELATIONSHIP.equal(relationship.getId()))
                                                       .fetch()
                                               : create.selectDistinct(EDGE.fields())
                                                       .from(EDGE)
                                                       .join(EXISTENTIAL)
                                                       .on(EXISTENTIAL.ID.equal(EDGE.CHILD))
                                                       .where(EDGE.PARENT.equal(parent.getId()))
                                                       .and(EDGE.RELATIONSHIP.equal(relationship.getId()))
                                                       .fetch();
        if (result == null) {
            return null;
        }
        return result.into(EdgeRecord.class);
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
    public EdgeRecord getImmediateLink(ExistentialRuleform parent,
                                       Relationship relationship,
                                       ExistentialRuleform child) {
        EdgeRecord result = create.selectFrom(EDGE)
                                  .where(EDGE.PARENT.equal(parent.getId()))
                                  .and(EDGE.RELATIONSHIP.equal(relationship.getId()))
                                  .and(EDGE.CHILD.equal(child.getId()))
                                  .fetchOne();
        if (result == null) {
            return null;
        }
        return result.into(EdgeRecord.class);
    }

    @Override
    public EdgeAuthorizationRecord getNetworkAuthorization(FacetRecord aspect,
                                                           Relationship relationship,
                                                           boolean includeGrouping) {

        SelectConditionStep<EdgeAuthorizationRecord> and = create.selectFrom(EDGE_AUTHORIZATION)
                                                                 .where(EDGE_AUTHORIZATION.PARENT.equal(aspect.getId()))
                                                                 .and(EDGE_AUTHORIZATION.RELATIONSHIP.equal(relationship.getId()));

        if (!includeGrouping) {
            and = and.and(EDGE_AUTHORIZATION.AUTHORITY.isNull());
        }
        return and.fetchSingle();
    }

    @Override
    public List<EdgeAuthorizationRecord> getNetworkAuthorizations(FacetRecord aspect,
                                                                  boolean includeGrouping) {

        SelectConditionStep<EdgeAuthorizationRecord> and = create.selectFrom(EDGE_AUTHORIZATION)
                                                                 .where(EDGE_AUTHORIZATION.PARENT.equal(aspect.getId()));

        if (!includeGrouping) {
            and = and.and(EDGE_AUTHORIZATION.AUTHORITY.isNull());
        }
        return and.fetch()
                  .into(EdgeAuthorizationRecord.class);
    }

    @Override
    public List<ExistentialRuleform> getNotInGroup(ExistentialRuleform parent,
                                                   Relationship relationship,
                                                   ExistentialDomain domain) {

        SelectConditionStep<ExistentialRecord> statement = create.selectFrom(EXISTENTIAL)
                                                                 .whereNotExists(create.selectFrom(EDGE)
                                                                                       .where(EDGE.PARENT.equal(parent.getId()))
                                                                                       .and(EDGE.RELATIONSHIP.equal(relationship.getId()))
                                                                                       .and(EDGE.CHILD.equal(EXISTENTIAL.ID)));
        if (domain != null) {
            statement = statement.and(EXISTENTIAL.DOMAIN.equal(domain));
        }
        return statement.fetch()
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
                     .fetchOne()
                     .into(FacetPropertyRecord.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ExistentialRuleform> T getSingleChild(ExistentialRuleform parent,
                                                            Relationship relationship,
                                                            ExistentialDomain domain) {
        SelectConditionStep<Record> statement = create.selectDistinct(EXISTENTIAL.fields())
                                                      .from(EXISTENTIAL)
                                                      .join(EDGE)
                                                      .on(EDGE.PARENT.equal(parent.getId()))
                                                      .and(EDGE.RELATIONSHIP.equal(relationship.getId()))
                                                      .and(EDGE.CHILD.equal(EXISTENTIAL.ID))
                                                      .where(EXISTENTIAL.ID.equal(EDGE.CHILD));
        if (domain != null) {
            statement = statement.and(EXISTENTIAL.DOMAIN.equal(domain));
        }
        Record result = statement.fetchOne();
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
            Tuple<EdgeRecord, EdgeRecord> links = link(ruleform.getId(),
                                                       aspect.getClassifier(),
                                                       aspect.getClassification(),
                                                       inverseRelationship);
            if (workspace != null) {
                workspace.add(links.a);
                workspace.add(links.b);
            }
        }
    }

    @Override
    public boolean isAccessible(UUID parent, UUID relationship, UUID child) {
        assert parent != null && relationship != null && child != null;
        return !ZERO.equals(create.selectCount()
                                  .from(EDGE)
                                  .where(EDGE.PARENT.equal(parent))
                                  .and(EDGE.RELATIONSHIP.equal(relationship))
                                  .and(EDGE.CHILD.equal(child))
                                  .and(EDGE.CHILD.notEqual(parent))
                                  .fetchOne()
                                  .value1());
    }

    @Override
    public Tuple<EdgeRecord, EdgeRecord> link(ExistentialRuleform parent,
                                              Relationship r,
                                              ExistentialRuleform child) {
        return link(parent.getId(), r.getId(), child.getId(), r.getInverse());
    }

    public Tuple<EdgeRecord, EdgeRecord> link(UUID parent, UUID r, UUID child,
                                              UUID inverseR) {
        EdgeRecord forward = model.records()
                                  .newExistentialNetwork();
        forward.setParent(parent);
        forward.setRelationship(r);
        forward.setChild(child);
        forward.insert();

        EdgeRecord inverse = model.records()
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
        create.deleteFrom(EDGE)
              .where(EDGE.PARENT.equal(parent.getId()))
              .and(EDGE.RELATIONSHIP.equal(relationship.getId()))
              .and(EDGE.CHILD.equal(child.getId()))
              .execute();
        create.deleteFrom(EDGE)
              .where(EDGE.PARENT.equal(child.getId()))
              .and(EDGE.RELATIONSHIP.equal(relationship.getInverse()))
              .and(EDGE.CHILD.equal(parent.getId()))
              .execute();
    }

    @Override
    public void unlinkImmediate(ExistentialRuleform parent,
                                Relationship relationship) {
        create.deleteFrom(EDGE)
              .where(EDGE.PARENT.equal(parent.getId()))
              .and(EDGE.RELATIONSHIP.equal(relationship.getId()))
              .execute();
        create.deleteFrom(EDGE)
              .where(EDGE.CHILD.equal(parent.getId()))
              .and(EDGE.RELATIONSHIP.equal(relationship.getInverse()))
              .execute();
    }

    private List<ExistentialRuleform> getConstrainedChildren(UUID parent,
                                                             UUID relationship,
                                                             UUID classifier,
                                                             UUID classification,
                                                             ExistentialDomain domain) {
        SelectConditionStep<Record> statement = create.select(EXISTENTIAL.fields())
                                                      .from(EXISTENTIAL, EDGE)
                                                      .where(EDGE.PARENT.equal(parent))
                                                      .and(EDGE.RELATIONSHIP.equal(relationship))
                                                      .and(EXISTENTIAL.ID.equal(EDGE.CHILD));
        if (domain != null) {
            statement = statement.and(EXISTENTIAL.DOMAIN.equal(domain));
        }
        return statement.fetch()
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
        SelectConditionStep<Record> statement = create.select(EXISTENTIAL.fields())
                                                      .from(EXISTENTIAL, EDGE)
                                                      .where(EDGE.PARENT.equal(parent))
                                                      .and(EDGE.RELATIONSHIP.equal(relationship))
                                                      .and(EXISTENTIAL.ID.equal(EDGE.CHILD));
        if (domain != null) {
            statement = statement.and(EXISTENTIAL.DOMAIN.equal(domain));
        }
        return statement.fetch()
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
        SelectConditionStep<Record> statement = create.select(EXISTENTIAL.fields())
                                                      .from(EXISTENTIAL, EDGE)
                                                      .where(EDGE.PARENT.equal(parent))
                                                      .and(EDGE.RELATIONSHIP.equal(relationship))
                                                      .and(EXISTENTIAL.ID.equal(EDGE.CHILD));
        if (domain != null) {
            statement = statement.and(EXISTENTIAL.DOMAIN.equal(domain));
        }
        return statement.fetch()
                        .into(ExistentialRecord.class)
                        .stream()
                        .map(r -> model.records()
                                       .resolve(r))
                        .filter(r -> isAccessible(r.getId(), classifier,
                                                  classification))
                        .collect(Collectors.toList());
    }
}
