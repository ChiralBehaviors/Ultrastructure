/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.phantasm.graphql.mutations;

import static com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceContext.getWorkspace;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.jooq.Tables;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.graphql.GraphQLInterface;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.AttributeValue;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.AttributeValue.AttributeValueState;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.AttributeValue.AttributeValueUpdateState;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
@GraphQLInterface
public interface AttributeValueMutations {

    @GraphQLField
    default AttributeValue createAttributeValue(@NotNull @GraphQLName("state") AttributeValueState state,
                                                DataFetchingEnvironment env) {
        Model model = WorkspaceSchema.ctx(env);
        ExistentialAttributeRecord record = model.records()
                                                 .newExistentialAttribute();
        if (!model.checkCreateMeta(getWorkspace(env))) {
            return null;
        }
        state.update(record);
        record.insert();
        return new AttributeValue(record);
    }

    @GraphQLField
    default Boolean removeAttributeValue(@NotNull @GraphQLName("id") String id,
                                         DataFetchingEnvironment env) {
        ExistentialAttributeRecord fetch = AttributeValue.fetch(env,
                                                                UUID.fromString(id));
        Model model = WorkspaceSchema.ctx(env);
        if (!model.checkDelete(fetch)) {
            return null;
        }
        fetch.delete();
        return true;
    }

    @GraphQLField
    default AttributeValue updateAttributeValue(@NotNull @GraphQLName("state") AttributeValueUpdateState state,
                                                DataFetchingEnvironment env) {
        Model model = WorkspaceSchema.ctx(env);
        ExistentialAttributeRecord record = model.create()
                                                 .selectFrom(Tables.EXISTENTIAL_ATTRIBUTE)
                                                 .where(Tables.EXISTENTIAL_ATTRIBUTE.ID.equal(UUID.fromString(state.id)))
                                                 .fetchOne();
        if (!model.checkUpdate(record)) {
            return null;
        }
        state.update(record);
        record.update();
        return new AttributeValue(record);
    }
}
