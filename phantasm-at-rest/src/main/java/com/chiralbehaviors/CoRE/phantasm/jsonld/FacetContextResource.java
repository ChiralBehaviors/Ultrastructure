/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
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

package com.chiralbehaviors.CoRE.phantasm.jsonld;

import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author hhildebrand
 *
 */
@Path("json-ld/facet/context")
public class FacetContextResource extends TransactionalResource {

    @Context
    private UriInfo                   uriInfo;
    private final FacetContextBuilder builder;

    public FacetContextResource(EntityManagerFactory emf) {
        super(emf);
        builder = new FacetContextBuilder(readOnlyModel);
    }

    public FacetContextResource(EntityManagerFactory emf, UriInfo uriInfo) {
        this(emf);
        this.uriInfo = uriInfo;
    }

    @Produces({ "application/json", "text/json" })
    @Path("agency/{classifier}/{classification}")
    @GET
    public JsonNode getAgency(@PathParam("classifier") String relationship,
                              @PathParam("classification") String ruleform) {
        return builder.buildContainer(getAspect(relationship, ruleform,
                                                readOnlyModel.getAgencyModel()),
                                      uriInfo);
    }

    @Produces({ "application/json", "text/json" })
    @Path("attribute/{classifier}/{classification}")
    @GET
    public JsonNode getAttribute(@PathParam("classifier") String relationship,
                                 @PathParam("classification") String ruleform) {
        return builder.buildContainer(getAspect(relationship, ruleform,
                                                readOnlyModel.getAttributeModel()),
                                      uriInfo);
    }

    @Produces({ "application/json", "text/json" })
    @Path("interval/{classifier}/{classification}")
    @GET
    public JsonNode getInterval(@PathParam("classifier") String relationship,
                                @PathParam("classification") String ruleform) {
        return builder.buildContainer(getAspect(relationship, ruleform,
                                                readOnlyModel.getIntervalModel()),
                                      uriInfo);
    }

    @Produces({ "application/json", "text/json" })
    @Path("location/{classifier}/{classification}")
    @GET
    public JsonNode getLocation(@PathParam("classifier") String relationship,
                                @PathParam("classification") String ruleform) {
        return builder.buildContainer(getAspect(relationship, ruleform,
                                                readOnlyModel.getLocationModel()),
                                      uriInfo);
    }

    @Produces({ "application/json", "text/json" })
    @Path("product/{classifier}/{classification}")
    @GET
    public JsonNode getProduct(@PathParam("classifier") String relationship,
                               @PathParam("classification") String ruleform) {
        return builder.buildContainer(getAspect(relationship, ruleform,
                                                readOnlyModel.getProductModel()),
                                      uriInfo);
    }

    @Produces({ "application/json", "text/json" })
    @Path("relationship/{classifier}/{classification}")
    @GET
    public JsonNode getRelationship(@PathParam("classifier") String relationship,
                                    @PathParam("classification") String ruleform) {
        return builder.buildContainer(getAspect(relationship, ruleform,
                                                readOnlyModel.getRelationshipModel()),
                                      uriInfo);
    }

    @Produces({ "application/json", "text/json" })
    @Path("statusCode/{classifier}/{classification}")
    @GET
    public JsonNode getStatusCode(@PathParam("classifier") String relationship,
                                  @PathParam("classification") String ruleform) {
        return builder.buildContainer(getAspect(relationship, ruleform,
                                                readOnlyModel.getStatusCodeModel()),
                                      uriInfo);
    }

    @Produces({ "application/json", "text/json" })
    @Path("unit/{classifier}/{classification}")
    @GET
    public JsonNode getUnit(@PathParam("classifier") String relationship,
                            @PathParam("classification") String ruleform) {
        return builder.buildContainer(getAspect(relationship, ruleform,
                                                readOnlyModel.getUnitModel()),
                                      uriInfo);
    }

    private UUID toUuid(String ruleform) {
        UUID classification;
        try {
            classification = UUID.fromString(ruleform);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        return classification;
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> Aspect<RuleForm> getAspect(String relationship,
                                                                                           String ruleform,
                                                                                           NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        UUID classifier = toUuid(relationship);
        UUID classification = toUuid(ruleform);
        try {
            return networkedModel.getAspect(classifier, classification);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        }
    }
}
