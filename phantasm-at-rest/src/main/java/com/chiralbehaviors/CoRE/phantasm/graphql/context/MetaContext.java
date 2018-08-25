/**
 * Copyright (c) 2017 Chiral Behaviors, LLC, all rights reserved.
 *

 *  This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.phantasm.graphql.context;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.jooq.UpdatableRecord;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.enums.ReferenceType;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.phantasm.graphql.schemas.WorkspaceSchema;
import com.chiralbehaviors.CoRE.phantasm.graphql.schemas.WorkspaceSchema.MetaMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.schemas.WorkspaceSchema.MetaQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Facet;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Facet.FacetState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Facet.FacetUpdateState;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author halhildebrand
 *
 */
public class MetaContext extends ExistentialContext
        implements MetaMutations, MetaQueries {

    public MetaContext(Model model, Product workspace) {
        super(model, workspace);
    }

    @Override
    @GraphQLField
    public Facet createFacet(@NotNull @GraphQLName("state") FacetState state,
                             DataFetchingEnvironment env) {
        if (!WorkspaceSchema.ctx(env)
                            .checkCreateMeta(getWorkspace(env))) {
            return null;
        }
        FacetRecord record = WorkspaceSchema.ctx(env)
                                            .records()
                                            .newFacet();
        state.update(record);
        record.insert();
        return new Facet(record);
    }

    @Override
    public Facet facet(UUID id, DataFetchingEnvironment env) {
        return Facet.fetch(env, id);
    }

    @Override
    public List<Facet> facets(List<UUID> ids, DataFetchingEnvironment env) {
        if (ids == null) {
            Model model = WorkspaceSchema.ctx(env);
            return model.getPhantasmModel()
                        .getFacets(((MetaContext) env.getContext()).getWorkspace())
                        .stream()
                        .filter(r -> model.checkRead(r))
                        .map(r -> new Facet(r))
                        .collect(Collectors.toList());
        }
        return ids.stream()
                  .map(id -> Facet.fetch(env, id))
                  .collect(Collectors.toList());
    }

    @Override
    public UUID lookup(String namespace, String name,
                       DataFetchingEnvironment env) {

        Model model = WorkspaceSchema.ctx(env);
        Product wspProduct = getWorkspace(env);
        if (!model.checkRead(wspProduct)) {
            return null;
        }
        WorkspaceScope scoped = model.getWorkspaceModel()
                                     .getScoped(wspProduct);
        return namespace == null ? scoped.lookupId(ReferenceType.Existential,
                                                   name)
                                 : scoped.lookupId(namespace,
                                                   ReferenceType.Existential,
                                                   name);
    }

    @Override
    public Boolean deleteFacet(UUID id, DataFetchingEnvironment env) {
        Facet fetch = Facet.fetch(env, id);
        if (fetch == null || !WorkspaceSchema.ctx(env)
                                             .checkRemove((UpdatableRecord<?>) fetch)) {
            return false;
        }
        fetch.getRecord()
             .delete();
        return true;
    }

    @Override
    public Facet updateFacet(FacetUpdateState state,
                             DataFetchingEnvironment env) {
        Model model = WorkspaceSchema.ctx(env);
        FacetRecord record = model.create()
                                  .selectFrom(Tables.FACET)
                                  .where(Tables.FACET.ID.equal(state.getId()))
                                  .fetchOne();
        if (record == null) {
            return null;
        }
        if (!model.checkUpdate(record)) {
            return null;
        }
        state.update(record);
        record.update();
        return new Facet(record);
    }
}
