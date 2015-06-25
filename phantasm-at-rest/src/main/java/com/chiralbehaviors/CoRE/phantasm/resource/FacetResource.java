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

package com.chiralbehaviors.CoRE.phantasm.resource;

import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.chiralbehaviors.CoRE.meta.NetworkedModel;

/**
 * REST API for Ultrastructure Facets using JSON-LD
 * 
 * @author hhildebrand
 *
 */
@Path("/v{version : \\d+}/facet")
public class FacetResource extends TransactionalResource {

    /**
     * @param emf
     */
    public FacetResource(EntityManagerFactory emf) {
        super(emf);
    }

    @GET
    @Path("agency/state/{classifier}/{classification}")
    @Produces({ "text/json", " application/json" })
    public Facet<?> getAgency(@PathParam("classifier") String classifierUuid,
                              @PathParam("classification") String classificationUuid) {
        return getPhantasm(classifierUuid, classificationUuid, readOnlyModel.getAgencyModel());
    }

    @GET
    @Path("attribute/state/{classifier}/{classification}")
    @Produces({ "text/json", " application/json" })
    public Facet<?> getAttribute(@PathParam("classifier") String classifierUuid,
                                 @PathParam("classification") String classificationUuid) {
        return getPhantasm(classifierUuid, classificationUuid, readOnlyModel.getAttributeModel());
    }

    @GET
    @Path("interval/state/{classifier}/{classification}")
    @Produces({ "text/json", " application/json" })
    public Facet<?> getInterval(@PathParam("classifier") String classifierUuid,
                                @PathParam("classification") String classificationUuid) {
        return getPhantasm(classifierUuid, classificationUuid, readOnlyModel.getIntervalModel());
    }

    @GET
    @Path("location/state/{classifier}/{classification}")
    @Produces({ "text/json", " application/json" })
    public Facet<?> getLocation(@PathParam("classifier") String classifierUuid,
                                @PathParam("classification") String classificationUuid) {
        return getPhantasm(classifierUuid, classificationUuid, readOnlyModel.getLocationModel());
    }

    @GET
    @Path("product/state/{classifier}/{classification}")
    @Produces({ "text/json", " application/json" })
    public Facet<?> getProduct(@PathParam("classifier") String classifierUuid,
                               @PathParam("classification") String classificationUuid) {
        return getPhantasm(classifierUuid, classificationUuid, readOnlyModel.getProductModel());
    }

    @GET
    @Path("relationship/state/{classifier}/{classification}")
    @Produces({ "text/json", " application/json" })
    public Facet<?> getRelationship(@PathParam("classifier") String classifierUuid,
                                    @PathParam("classification") String classificationUuid) {
        return getPhantasm(classifierUuid, classificationUuid, readOnlyModel.getRelationshipModel());
    }

    @GET
    @Path("statusCode/state/{classifier}/{classification}")
    @Produces({ "text/json", " application/json" })
    public Facet<?> getStatusCode(@PathParam("classifier") String classifierUuid,
                                  @PathParam("classification") String classificationUuid) {
        return getPhantasm(classifierUuid, classificationUuid, readOnlyModel.getStatusCodeModel());
    }

    @GET
    @Path("unit/state/{classifier}/{classification}")
    @Produces({ "text/json", " application/json" })
    public Facet<?> getUnit(@PathParam("classifier") String classifierUuid,
                            @PathParam("classification") String classificationUuid) {
        return getPhantasm(classifierUuid, classificationUuid, readOnlyModel.getUnitModel());
    }

    private Facet<?> getPhantasm(String classifier, String classification, NetworkedModel<?, ?, ?, ?> networkedModel) {
        UUID classifiernUuid;
        UUID classificationUuid;
        try {
            classifiernUuid = UUID.fromString(classifier);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e, Response.Status.NOT_ACCEPTABLE);
        }
        try {
            classificationUuid = UUID.fromString(classification);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e, Response.Status.NOT_ACCEPTABLE);
        }

        return null;
    }
}
