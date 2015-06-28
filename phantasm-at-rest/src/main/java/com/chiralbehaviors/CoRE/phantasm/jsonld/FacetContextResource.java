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

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author hhildebrand
 *
 */
@Path("json-ld/facet/context")
@Produces({ "application/json", "text/json" })
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

    @Path("agency/{classifier}/{classification}")
    @GET
    public JsonNode getAgency(@PathParam("classifier") String relationship,
                              @PathParam("classification") String ruleform) {
        return builder.buildContainer(getAspect(relationship, ruleform,
                                                readOnlyModel.getAgencyModel()),
                                      uriInfo);
    }

    @Path("attribute/{classifier}/{classification}")
    @GET
    public JsonNode getAttribute(@PathParam("classifier") String relationship,
                                 @PathParam("classification") String ruleform) {
        return builder.buildContainer(getAspect(relationship, ruleform,
                                                readOnlyModel.getAttributeModel()),
                                      uriInfo);
    }

    @Path("interval/{classifier}/{classification}")
    @GET
    public JsonNode getInterval(@PathParam("classifier") String relationship,
                                @PathParam("classification") String ruleform) {
        return builder.buildContainer(getAspect(relationship, ruleform,
                                                readOnlyModel.getIntervalModel()),
                                      uriInfo);
    }

    @Path("location/{classifier}/{classification}")
    @GET
    public JsonNode getLocation(@PathParam("classifier") String relationship,
                                @PathParam("classification") String ruleform) {
        return builder.buildContainer(getAspect(relationship, ruleform,
                                                readOnlyModel.getLocationModel()),
                                      uriInfo);
    }

    @Path("product/{classifier}/{classification}")
    @GET
    public JsonNode getProduct(@PathParam("classifier") String relationship,
                               @PathParam("classification") String ruleform) {
        return builder.buildContainer(getAspect(relationship, ruleform,
                                                readOnlyModel.getProductModel()),
                                      uriInfo);
    }

    @Path("relationship/{classifier}/{classification}")
    @GET
    public JsonNode getRelationship(@PathParam("classifier") String relationship,
                                    @PathParam("classification") String ruleform) {
        return builder.buildContainer(getAspect(relationship, ruleform,
                                                readOnlyModel.getRelationshipModel()),
                                      uriInfo);
    }

    @Path("statusCode/{classifier}/{classification}")
    @GET
    public JsonNode getStatusCode(@PathParam("classifier") String relationship,
                                  @PathParam("classification") String ruleform) {
        return builder.buildContainer(getAspect(relationship, ruleform,
                                                readOnlyModel.getStatusCodeModel()),
                                      uriInfo);
    }

    @Path("unit/{classifier}/{classification}")
    @GET
    public JsonNode getUnit(@PathParam("classifier") String relationship,
                            @PathParam("classification") String ruleform) {
        return builder.buildContainer(getAspect(relationship, ruleform,
                                                readOnlyModel.getUnitModel()),
                                      uriInfo);
    }
}
