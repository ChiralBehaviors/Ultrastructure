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

import static graphql.Scalars.GraphQLString;

import java.lang.reflect.AnnotatedType;
import java.util.UUID;

import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.graphql.types.Existential.ExistentialResolver;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;

import graphql.annotations.GraphQLAnnotations;
import graphql.annotations.GraphQLDataFetcher;
import graphql.annotations.GraphQLDescription;
import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;
import graphql.annotations.GraphQLTypeResolver;
import graphql.annotations.TypeFunction;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;

/**
 * @author hhildebrand
 *
 */
@GraphQLTypeResolver(ExistentialResolver.class)
public interface Existential {

    @GraphQLDescription("The Agency existential ruleform")
    public class Agency extends ExistentialCommon {

        public Agency(ExistentialRecord record) {
            super(record);
        }
    }

    public class AgencyTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return AgencyType;
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
        Boolean indexed;

        @GraphQLField
        Boolean keyed;

        @GraphQLField
        String  valueType;
    }

    public class AttributeTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return AttributeType;
        }
    }

    public class AttributeUpdateState extends AttributeState {
        @GraphQLField
        public String id;
    }

    public abstract class ExistentialCommon implements Existential {
        protected final ExistentialRecord record;

        public ExistentialCommon(ExistentialRecord record) {
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
        public @GraphQLType(AgencyTypeFunction.class) Agency getUpdatedBy(DataFetchingEnvironment env) {
            return new Agency((ExistentialRecord) Existential.ctx(env)
                                                             .records()
                                                             .resolve(record.getUpdatedBy()));
        }
    }

    public class ExistentialResolver implements TypeResolver {
        @Override
        public GraphQLObjectType getType(Object object) {
            Existential record = (Existential) object; // If this doesn't succeed, it's a bug, so no catch
            switch (record.getDomain()) {
                case Agency:
                    return AgencyType;
                case Attribute:
                    return AttributeType;
                case Interval:
                    return IntervalType;
                case Location:
                    return LocationType;
                case Product:
                    return ProductType;
                case Relationship:
                    return RelationshipType;
                case StatusCode:
                    return StatusCodeType;
                case Unit:
                    return UnitType;
                default:
                    throw new IllegalStateException(String.format("invalid domain: %s",
                                                                  record.getDomain()));
            }
        }

    }

    public class ExistentialState {
        @GraphQLField
        String name;

        @GraphQLField
        String notes;

        public void update(ExistentialRecord record) {
            if (name != null) {
                record.setName(name);
            }
            if (notes != null) {
                record.setNotes(notes);
            }
        }
    }

    public class ExistentialTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return ExistentialType;
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

    public class IntervalTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return IntervalType;
        }
    }

    @GraphQLDescription("The Location existential ruleform")
    public class Location extends ExistentialCommon {

        public Location(ExistentialRecord record) {
            super(record);
        }
    }

    public class LocationTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return LocationType;
        }
    }

    @GraphQLDescription("The Product existential ruleform")
    public class Product extends ExistentialCommon {

        public Product(ExistentialRecord record) {
            super(record);
        }
    }

    public class ProductTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return ProductType;
        }
    }

    @GraphQLDescription("The Relationship existential ruleform")
    public class Relationship extends ExistentialCommon {

        public Relationship(ExistentialRecord record) {
            super(record);
        }

        @GraphQLField
        public Relationship getInverse(DataFetchingEnvironment env) {
            return new Relationship(Existential.resolve(env,
                                                        record.getInverse()));
        }
    }

    public class RelationshipState extends ExistentialState {
        @GraphQLField
        String inverse;
    }

    public class RelationshipTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return RelationshipType;
        }
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
        Boolean failParent;

        @GraphQLField
        Boolean propagateChildren;
    }

    public class StatusCodeTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return StatusCodeType;
        }
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

    public class UnitTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return UnitType;
        }
    }

    public class UpdatedByFetcher implements DataFetcher {
        @Override
        public Object get(DataFetchingEnvironment environment) {
            return ctx(environment).records()
                                   .resolve(((ExistentialRecord) environment.getSource()).getUpdatedBy());
        }

    }

    public class UuidTypeFunction implements TypeFunction {
        @Override
        public graphql.schema.GraphQLType apply(Class<?> t, AnnotatedType u) {
            return GraphQLString;
        }
    }

    public final GraphQLObjectType    AgencyType       = objectTypeOf(Agency.class);

    public final GraphQLObjectType    AttributeType    = objectTypeOf(Attribute.class);

    public final GraphQLInterfaceType ExistentialType  = interfaceTypeOf(Existential.class);

    public final GraphQLObjectType    IntervalType     = objectTypeOf(Interval.class);

    public final GraphQLObjectType    LocationType     = objectTypeOf(Location.class);

    public final GraphQLObjectType    ProductType      = objectTypeOf(Product.class);

    public final GraphQLObjectType    RelationshipType = objectTypeOf(Relationship.class);

    public final GraphQLObjectType    StatusCodeType   = objectTypeOf(StatusCode.class);

    public final GraphQLObjectType    UnitType         = objectTypeOf(Unit.class);

    public static Model ctx(DataFetchingEnvironment env) {
        return ((PhantasmCRUD) env.getContext()).getModel();
    }

    public static GraphQLInterfaceType interfaceTypeOf(Class<?> clazz) {
        try {
            return GraphQLAnnotations.iface(clazz);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException(String.format("Unable to create interface  type for %s",
                                                          clazz.getSimpleName()));
        }
    }

    public static GraphQLObjectType objectTypeOf(Class<?> clazz) {
        try {
            return GraphQLAnnotations.object(clazz);
        } catch (IllegalAccessException | InstantiationException
                | NoSuchMethodException e) {
            throw new IllegalStateException(String.format("Unable to create object type for %s",
                                                          clazz.getSimpleName()));
        }
    }

    public static <T extends ExistentialRecord> T resolve(DataFetchingEnvironment env,
                                                          UUID id) {
        return ctx(env).records()
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

    @GraphQLField
    @GraphQLType(AgencyTypeFunction.class)
    @GraphQLDataFetcher(UpdatedByFetcher.class)
    Agency getUpdatedBy(DataFetchingEnvironment env);

}
