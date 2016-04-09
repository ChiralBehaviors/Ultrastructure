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

import static com.chiralbehaviors.CoRE.phantasm.resources.QueryRequest.QUERY;
import static com.chiralbehaviors.CoRE.phantasm.resources.QueryRequest.getVariables;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.graphql.JobSchema;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.codahale.metrics.annotation.Timed;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.dropwizard.auth.Auth;

/**
 * @author hhildebrand
 *
 */
@Path("job")
@Produces({ MediaType.APPLICATION_JSON, "text/json" })
@Consumes({ MediaType.APPLICATION_JSON, "text/json" })
public class JobResource extends TransactionalResource {
    private static final Logger log    = LoggerFactory.getLogger(JobResource.class);

    private final GraphQLSchema schema = JobSchema.build();

    @Timed
    @POST
    public ExecutionResult query(@Auth AuthorizedPrincipal principal,
                                 @SuppressWarnings("rawtypes") Map request,
                                 @Context DSLContext create) {
        if (request == null) {
            throw new WebApplicationException("Query cannot be null",
                                              Status.BAD_REQUEST);
        }
        if (request.get(QUERY) == null) {
            throw new WebApplicationException("Query cannot be null",
                                              Status.BAD_REQUEST);
        }
        return mutate(principal, model -> {
            if (!checkQueryCapability(model)) {
                return null;
            }
            Map<String, Object> variables = getVariables(request);
            ExecutionResult result = new GraphQL(schema).execute((String) request.get(QUERY),
                                                                 model,
                                                                 variables);
            if (result.getErrors()
                      .isEmpty()) {
                return result;
            }
            log.info("Query: {} Errors: {}", request.get(QUERY),
                     result.getErrors());
            return result;
        }, create);
    }

    private boolean checkQueryCapability(Model model) {
        //        if (!model.getPhantasmModel()
        //                  .checkCapability(definingProduct, crud.getREAD())
        //            || !model.getPhantasmModel()
        //                     .checkCapability(definingProduct, model.getKernel()
        //                                                            .getEXECUTE_QUERY())) {
        //            Agency p = model.getCurrentPrincipal()
        //                            .getPrincipal();
        //            log.info(String.format("Failed executing query on workspace [%s:%s] by: %s:%s",
        //                                   definingProduct.getName(), uuid,
        //                                   p.getName(), p.getId()));
        //            return false;
        //        }
        return true;
    }
}
