/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
 *
 *
 * This file is part of Ultrastructure.
 *
 * Ultrastructure is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * ULtrastructure is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Ultrastructure.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.postgres;

import com.fasterxml.jackson.databind.JsonNode;
import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;

/**
 * @author hhildebrand
 *
 * We're binding <T> = JSONB (or JSONB), and <U> = JsonNode (user type)
 * Alternatively, extend org.jooq.impl.AbstractBinding to implement fewer methods.
 *
 */
public class PostgresJSONJacksonJsonNodeBinding implements Binding<JSONB, JsonNode> {

    // The converter does all the work
    @Override
    public Converter<JSONB, JsonNode> converter() {
        return new PostgresJSONJacksonJsonNodeConverter();
    }

    // Getting a String value from a JDBC ResultSet and converting that to a JsonNode
    @Override
    public void get(BindingGetResultSetContext<JsonNode> ctx) throws SQLException {
        ctx.convert(converter()).value(JSONB.valueOf(ctx.resultSet().getString(ctx.index())));
    }

    // Getting a String value from a JDBC CallableStatement and converting that to a JsonNode
    @Override
    public void get(BindingGetStatementContext<JsonNode> ctx) throws SQLException {
        ctx.convert(converter()).value(JSONB.valueOf(ctx.statement().getString(ctx.index())));
    }

    // Getting a value from a JDBC SQLInput (useful for Oracle OBJECT types)
    @Override
    public void get(BindingGetSQLInputContext<JsonNode> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    // Registering VARCHAR types for JDBC CallableStatement OUT parameters
    @Override
    public void register(BindingRegisterContext<JsonNode> ctx) throws SQLException {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR);
    }

    // Setting a value on a JDBC SQLOutput (useful for Oracle OBJECT types)
    @Override
    public void set(BindingSetSQLOutputContext<JsonNode> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    // Converting the JsonNode to a String value and setting that on a JDBC PreparedStatement
    @Override
    public void set(BindingSetStatementContext<JsonNode> ctx) throws SQLException {
        JSONB json = ctx.convert(converter()).value();
        ctx.statement().setString(ctx.index(), json == null ? null : json.data());
    }

    // Rending a bind variable for the binding context's value and casting it to the json type
    @Override
    public void sql(BindingSQLContext<JsonNode> ctx) throws SQLException {
        // Depending on how you generate your SQL, you may need to explicitly distinguish
        // between jOOQ generating bind variables or inlined literals.
        if (ctx.render().paramType() == ParamType.INLINED)
            ctx.render().visit(DSL.inline(ctx.convert(converter()).value())).sql("::json");
        else
            ctx.render().sql(ctx.variable()).sql("::json");
    }
}
