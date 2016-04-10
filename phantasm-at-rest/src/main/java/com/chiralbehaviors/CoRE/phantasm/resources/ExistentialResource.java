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

package com.chiralbehaviors.CoRE.phantasm.resources;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.jooq.DSLContext;

import com.chiralbehaviors.CoRE.phantasm.graphql.ExistentialSchema;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.codahale.metrics.annotation.Timed;

import graphql.ExecutionResult;
import io.dropwizard.auth.Auth;

/**
 * @author hhildebrand
 *
 */
@Path("existential")
@Produces({ MediaType.APPLICATION_JSON, "text/json" })
@Consumes({ MediaType.APPLICATION_JSON, "text/json" })
public class ExistentialResource extends SchemaResource {
    public ExistentialResource() {
        super(ExistentialSchema.build());
    }

    @Override
    @Timed
    @POST
    public ExecutionResult query(@Auth AuthorizedPrincipal principal,
                                 @SuppressWarnings("rawtypes") Map request,
                                 @Context DSLContext create) {
        return super.query(principal, request, create);
    }
}
