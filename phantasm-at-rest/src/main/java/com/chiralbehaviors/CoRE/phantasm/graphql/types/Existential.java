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

package com.chiralbehaviors.CoRE.phantasm.graphql.types;

import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.resolve;

import java.util.UUID;

import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ExistentialResolver;

import graphql.annotations.GraphQLDataFetcher;
import graphql.annotations.GraphQLDescription;
import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLTypeResolver;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;

/**
 * @author hhildebrand
 *
 */
@SuppressWarnings("unused")
@GraphQLTypeResolver(ExistentialResolver.class)
public interface Existential {

    @GraphQLDescription("The Agency existential ruleform")
    public class Agency extends ExistentialCommon {

        public Agency(ExistentialRecord record) {
            super(record);
        }
    }

    @GraphQLDescription("The Attribute existential ruleform")
    public class Attribute extends ExistentialCommon {

        public Attribute(ExistentialRecord record) {
            super(record);
        }

        @GraphQLField
        public Boolean getIndexed() {
            return record.getIndexed();
        }

        @GraphQLField
        public Boolean getKeyed() {
            return record.getKeyed();
        }

        @GraphQLField
        public String getValueType() {
            return record.getValueType()
                         .toString();
        }

    }

    public class AttributeState extends ExistentialState {
        @GraphQLField
        public Boolean indexed;

        @GraphQLField
        public Boolean keyed;

        @GraphQLField
        public String  valueType;
    }

    public class AttributeUpdateState extends AttributeState {
        @GraphQLField
        public String id;
    }

    public abstract class ExistentialCommon implements Existential, Phantasm {
        protected final ExistentialRecord record;

        public ExistentialCommon(ExistentialRecord record) {
            assert record != null;
            this.record = record;
        }

        @Override
        public void delete() {
            record.delete();
        }

        @Override
        public String getDescription() {
            return record.getDescription();
        }

        @Override
        public ExistentialDomain getDomain() {
            return record.getDomain();
        }

        @Override
        public String getId() {
            return record.getId()
                         .toString();
        }

        @Override
        public String getName() {
            return record.getName();
        }

        @Override
        public ExistentialRuleform getRecord() {
            return (ExistentialRuleform) record;
        }

        @Override
        public Agency getUpdatedBy(DataFetchingEnvironment env) {
            return new Agency((ExistentialRecord) WorkspaceSchema.ctx(env)
                                                                 .records()
                                                                 .resolve(record.getUpdatedBy()));
        }
    }

    public class ExistentialResolver implements TypeResolver {
        @Override
        public GraphQLObjectType getType(Object object) {
            Existential record = (Existential) object; // If this doesn't succeed, it's a bug, so no catch
            return WorkspaceSchema.existentialType(record.getDomain());
        }

    }

    public class ExistentialState {
        @GraphQLField
        public String name;

        @GraphQLField
        public String notes;

        public void update(ExistentialRecord record) {
            if (name != null) {
                record.setName(name);
            }
            if (notes != null) {
                record.setNotes(notes);
            }
        }
    }

    public class ExistentialUpdateState extends ExistentialState {
        @GraphQLField
        public String id;
    }

    @GraphQLDescription("The Interval existential ruleform")
    public class Interval extends ExistentialCommon {

        public Interval(ExistentialRecord record) {
            super(record);
        }
    }

    @GraphQLDescription("The Location existential ruleform")
    public class Location extends ExistentialCommon {

        public Location(ExistentialRecord record) {
            super(record);
        }
    }

    @GraphQLDescription("The Product existential ruleform")
    public class Product extends ExistentialCommon {

        public Product(ExistentialRecord record) {
            super(record);
        }
    }

    @GraphQLDescription("The Relationship existential ruleform")
    public class Relationship extends ExistentialCommon {

        public Relationship(ExistentialRecord record) {
            super(record);
        }

        @GraphQLField
        public Relationship getInverse(DataFetchingEnvironment env) {
            return new Relationship(resolve(env, record.getInverse()));
        }
    }

    public class RelationshipState extends ExistentialState {
        @GraphQLField
        public String inverse;
    }

    public class RelationshipUpdateState extends RelationshipState {
        @GraphQLField
        public String id;
    }

    @GraphQLDescription("The StatusCode existential ruleform")
    public class StatusCode extends ExistentialCommon {

        public StatusCode(ExistentialRecord record) {
            super(record);
        }

        @GraphQLField
        public boolean getFailParent() {
            return record.getFailParent();
        }

        @GraphQLField
        public Boolean getPropagateChildren() {
            return record.getPropagateChildren();
        }
    }

    public class StatusCodeState extends ExistentialState {
        @GraphQLField
        public Boolean failParent;

        @GraphQLField
        public Boolean propagateChildren;
    }

    public class StatusCodeUpdateState extends StatusCodeState {
        @GraphQLField
        public String id;
    }

    @GraphQLDescription("The Unit existential ruleform")
    public class Unit extends ExistentialCommon {

        public Unit(ExistentialRecord record) {
            super(record);
        }
    }

    public class UpdatedByFetcher implements DataFetcher {
        @Override
        public Object get(DataFetchingEnvironment environment) {
            return WorkspaceSchema.ctx(environment)
                                  .records()
                                  .resolve(((ExistentialRecord) environment.getSource()).getUpdatedBy());
        }

    }

    public static <T extends ExistentialRecord> T resolve(DataFetchingEnvironment env,
                                                          UUID id) {
        return WorkspaceSchema.ctx(env)
                              .records()
                              .resolve(id);
    }

    public static Existential wrap(ExistentialRecord record) throws IllegalStateException {
        switch (record.getDomain()) {
            case Agency:
                return new Agency(record);
            case Attribute:
                return new Attribute(record);
            case Interval:
                return new Interval(record);
            case Location:
                return new Location(record);
            case Product:
                return new Product(record);
            case Relationship:
                return new Relationship(record);
            case StatusCode:
                return new StatusCode(record);
            case Unit:
                return new Unit(record);
            default:
                throw new IllegalStateException(String.format("Unknown domain: %s",
                                                              record.getDomain()));
        }
    }

    void delete();

    @GraphQLField
    String getDescription();

    ExistentialDomain getDomain();

    @GraphQLField
    String getId();

    @GraphQLField
    String getName();

    ExistentialRuleform getRecord();

    @GraphQLField
    @GraphQLDataFetcher(UpdatedByFetcher.class)
    Agency getUpdatedBy(DataFetchingEnvironment env);

}
