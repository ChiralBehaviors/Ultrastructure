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

import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.Agency;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.Attribute;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.AttributeState;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.AttributeUpdateState;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.ExistentialState;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.ExistentialUpdateState;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.Interval;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.Location;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.Product;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.Relationship;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.RelationshipState;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.RelationshipUpdateState;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.StatusCode;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.StatusCodeState;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.StatusCodeUpdateState;
import com.chiralbehaviors.CoRE.phantasm.graphql.Existential.Unit;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLName;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
public interface ExistentialMutations {

    @GraphQLField
    Agency createAgency(@NotNull @GraphQLName("state") ExistentialState state,
                        DataFetchingEnvironment env);

    @GraphQLField
    Attribute createAttribute(@NotNull @GraphQLName("state") AttributeState state,
                              DataFetchingEnvironment env);

    @GraphQLField
    Interval createInterval(@NotNull @GraphQLName("state") ExistentialState state,
                            DataFetchingEnvironment env);

    @GraphQLField
    Location createLocation(@NotNull @GraphQLName("state") ExistentialState state,
                            DataFetchingEnvironment env);

    @GraphQLField
    Product createProduct(@NotNull @GraphQLName("state") ExistentialState state,
                          DataFetchingEnvironment env);

    @GraphQLField
    Relationship createRelationship(@NotNull @GraphQLName("state") RelationshipState state,
                                    DataFetchingEnvironment env);

    @GraphQLField
    StatusCode createStatusCode(@NotNull @GraphQLName("state") StatusCodeState state,
                                DataFetchingEnvironment env);

    @GraphQLField
    Unit createUnit(@NotNull @GraphQLName("state") ExistentialState state,
                    DataFetchingEnvironment env);

    @GraphQLField
    Boolean removeAgency(@NotNull @GraphQLName("id") UUID id,
                         DataFetchingEnvironment env);

    @GraphQLField
    Boolean removeAttribute(@NotNull @GraphQLName("id") UUID id,
                            DataFetchingEnvironment env);

    @GraphQLField
    Boolean removeInterval(@NotNull @GraphQLName("id") UUID id,
                           DataFetchingEnvironment env);

    @GraphQLField
    Boolean removeLocation(@NotNull @GraphQLName("id") UUID id,
                           DataFetchingEnvironment env);

    @GraphQLField
    Boolean removeProduct(@NotNull @GraphQLName("id") UUID id,
                          DataFetchingEnvironment env);

    @GraphQLField
    Boolean removeRelationship(@NotNull @GraphQLName("id") UUID id,
                               DataFetchingEnvironment env);

    @GraphQLField
    Boolean removeStatusCode(@NotNull @GraphQLName("id") UUID id,
                             DataFetchingEnvironment env);

    @GraphQLField
    Boolean removeUnit(@NotNull @GraphQLName("id") UUID id,
                       DataFetchingEnvironment env);

    @GraphQLField
    Agency updateAgency(@NotNull @GraphQLName("state") ExistentialUpdateState state,
                        DataFetchingEnvironment env);

    @GraphQLField
    Attribute updateAttribute(@NotNull @GraphQLName("state") AttributeUpdateState state,
                              DataFetchingEnvironment env);

    @GraphQLField
    Interval updateInterval(@NotNull @GraphQLName("state") ExistentialUpdateState state,
                            DataFetchingEnvironment env);

    @GraphQLField
    Location updateLocation(@NotNull @GraphQLName("state") ExistentialUpdateState state,
                            DataFetchingEnvironment env);

    @GraphQLField
    Product updateProduct(@NotNull @GraphQLName("state") ExistentialUpdateState state,
                          DataFetchingEnvironment env);

    @GraphQLField
    Relationship updateRelationship(@NotNull @GraphQLName("state") RelationshipUpdateState state,
                                    DataFetchingEnvironment env);

    @GraphQLField
    StatusCode updateStatusCode(@NotNull @GraphQLName("state") StatusCodeUpdateState state,
                                DataFetchingEnvironment env);

    @GraphQLField
    Unit updateUnit(@NotNull @GraphQLName("state") ExistentialUpdateState state,
                    DataFetchingEnvironment env);
}
