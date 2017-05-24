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
import static com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.wrap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.chiralbehaviors.CoRE.RecordsFactory;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.enums.ValueType;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.phantasm.Phantasm;
import com.chiralbehaviors.CoRE.phantasm.graphql.schemas.WorkspaceSchema;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.Product;

import graphql.annotations.GraphQLDescription;
import graphql.annotations.GraphQLField;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author hhildebrand
 *
 */
@SuppressWarnings("unused")
public interface Existential extends Phantasm {

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
        private static final String INDEXED    = "indexed";
        private static final String KEYED      = "keyed";
        private static final String VALUE_TYPE = "valueType";

        public AttributeState(HashMap<String, Object> state) {
            super(state);
        }

        @GraphQLField
        public Boolean getIndexed() {
            return (Boolean) state.get(INDEXED);
        }

        @GraphQLField
        public Boolean getKeyed() {
            return (Boolean) state.get(KEYED);
        }

        @GraphQLField
        public ValueType getValueType() {
            return (ValueType) state.get(VALUE_TYPE);
        }

        @Override
        public void update(ExistentialRecord record) {
            super.update(record);
            if (state.containsKey(INDEXED)) {
                record.setIndexed((Boolean) state.get(INDEXED));
            }
            if (state.containsKey(KEYED)) {
                record.setKeyed((Boolean) state.get(KEYED));
            }
            if (state.containsKey(VALUE_TYPE)) {
                record.setValueType((ValueType) state.get(VALUE_TYPE));
            }
        }
    }

    public class AttributeUpdateState extends AttributeState {
        public AttributeUpdateState(HashMap<String, Object> state) {
            super(state);
        }

        @GraphQLField
        public UUID getId() {
            return (UUID) state.get(ID);
        }
    }

    public class ExistentialCommon implements Existential {
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
        public Existential getAuthority(DataFetchingEnvironment env) {
            return wrap((ExistentialRecord) WorkspaceSchema.ctx(env)
                                                           .records()
                                                           .resolve(record.getUpdatedBy()));
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
        public UUID getId() {
            return record.getId();
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
        public ExistentialRuleform getRuleform() {
            return RecordsFactory.resolveRecord(record);
        }

        @Override
        public Agency getUpdatedBy(DataFetchingEnvironment env) {
            return new Agency((ExistentialRecord) WorkspaceSchema.ctx(env)
                                                                 .records()
                                                                 .resolve(record.getUpdatedBy()));
        }

        @Override
        public int getVersion(DataFetchingEnvironment env) {
            return WorkspaceSchema.ctx(env)
                                  .records()
                                  .resolve(record.getWorkspace())
                                  .getVersion();
        }

        @Override
        public Product getWorkspace(DataFetchingEnvironment env) {
            return (Product) wrap(WorkspaceSchema.ctx(env)
                                                 .records()
                                                 .resolve(record.getWorkspace()));
        }
    }

    public class ExistentialState {
        static final String         ID        = "id";
        private static final String AUTHORITY = "authority";
        private static final String NAME      = "name";
        private static final String NOTES     = "notes";
        final Map<String, Object>   state;

        public ExistentialState(HashMap<String, Object> state) {
            this.state = state;
        }

        @GraphQLField
        public UUID getAuthority() {
            return (UUID) state.get(AUTHORITY);
        }

        @GraphQLField
        public String getName() {
            return (String) state.get(NAME);
        }

        @GraphQLField
        public String getNotes() {
            return (String) state.get(NOTES);
        }

        public void update(ExistentialRecord record) {
            if (state.containsKey(AUTHORITY)) {
                record.setAuthority((UUID) state.get(AUTHORITY));
            }
            if (state.containsKey(NAME)) {
                record.setName((String) state.get(NAME));
            }
            if (state.containsKey(NOTES)) {
                record.setNotes((String) state.get(NOTES));
            }
        }
    }

    public class ExistentialUpdateState extends ExistentialState {

        public ExistentialUpdateState(HashMap<String, Object> state) {
            super(state);
        }

        @GraphQLField
        public UUID getId() {
            return (UUID) state.get(ID);
        }
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
        private static final String INVERSE = "inverse";

        public RelationshipState(HashMap<String, Object> state) {
            super(state);
        }

        @GraphQLField
        public UUID getInverse() {
            return (UUID) state.get(INVERSE);
        }

        @Override
        public void update(ExistentialRecord record) {
            super.update(record);
            if (state.containsKey(INVERSE)) {
                record.setInverse((UUID) state.get(INVERSE));
            }
        }
    }

    public class RelationshipUpdateState extends RelationshipState {
        public RelationshipUpdateState(HashMap<String, Object> state) {
            super(state);
        }

        @GraphQLField
        public UUID getId() {
            return (UUID) state.get(ID);
        }
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
        private static final String FAIL_PARENT        = "failParent";
        private static final String PROPAGATE_CHILDREN = "propagateChildren";

        public StatusCodeState(HashMap<String, Object> state) {
            super(state);
        }

        @GraphQLField
        public Boolean getFailParent() {
            return (Boolean) state.get(FAIL_PARENT);
        }

        @GraphQLField
        public Boolean getPropagateChildren() {
            return (Boolean) state.get(PROPAGATE_CHILDREN);
        }

        @Override
        public void update(ExistentialRecord record) {
            super.update(record);
            if (state.containsKey(FAIL_PARENT)) {
                record.setFailParent((Boolean) state.get(FAIL_PARENT));
            }
            if (state.containsKey(PROPAGATE_CHILDREN)) {
                record.setPropagateChildren((Boolean) state.get(PROPAGATE_CHILDREN));
            }
        }
    }

    public class StatusCodeUpdateState extends StatusCodeState {
        public StatusCodeUpdateState(HashMap<String, Object> state) {
            super(state);
        }

        @GraphQLField
        public UUID getId() {
            return (UUID) state.get(ID);
        }
    }

    @GraphQLDescription("The Unit existential ruleform")
    public class Unit extends ExistentialCommon {

        public Unit(ExistentialRecord record) {
            super(record);
        }
    }

    public static <T extends ExistentialRecord> T resolve(DataFetchingEnvironment env,
                                                          UUID id) {
        return WorkspaceSchema.ctx(env)
                              .records()
                              .resolve(id);
    }

    public static Existential wrap(ExistentialRecord record) throws IllegalStateException {
        if (record == null) {
            return null;
        }
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
    Existential getAuthority(DataFetchingEnvironment env);

    @GraphQLField
    String getDescription();

    @GraphQLField
    ExistentialDomain getDomain();

    @GraphQLField
    UUID getId();

    @GraphQLField
    String getName();

    ExistentialRuleform getRecord();

    @GraphQLField
    Agency getUpdatedBy(DataFetchingEnvironment env);

    @GraphQLField
    int getVersion(DataFetchingEnvironment env);

    @GraphQLField
    Product getWorkspace(DataFetchingEnvironment env);

}
