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

package com.chiralbehaviors.CoRE.phantasm.jsonld.resources;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.jsonld.FacetContext;

/**
 * @author hhildebrand
 *
 */
@Path("json-ld/facet/context")
@Produces({ "application/json", "text/json" })
public class FacetContextResource extends TransactionalResource {

    @Context
    private UriInfo uriInfo;

    public FacetContextResource(EntityManagerFactory emf) {
        super(emf);
    }

    public FacetContextResource(EntityManagerFactory emf, UriInfo uriInfo) {
        this(emf);
        this.uriInfo = uriInfo;
    }

    @SuppressWarnings("unchecked")
    @Path("{ruleform-type}/{classifier}/{classification}")
    @GET
    public FacetContext<?, ?> getFacet(@PathParam("ruleform-type") String ruleformType,
                                       @PathParam("classifier") String relationship,
                                       @PathParam("classification") String ruleform) {
        return createContext(getAspect(ruleformType, relationship, ruleform));
    }

    @SuppressWarnings("unchecked")
    @Path("{ruleform-type}/{classifier}/{classification}/instances")
    @GET
    public FacetContext<?, ?> getFacetInstances(@PathParam("ruleform-type") String ruleformType,
                                                @PathParam("classifier") String relationship,
                                                @PathParam("classification") String ruleform) {
        return createContext(getAspect(ruleformType, relationship, ruleform));
    }

    @Path("{ruleform-type}")
    @GET
    public List<FacetContext<?, ?>> getFacets(@PathParam("ruleform-type") String ruleformType) {
        switch (ruleformType) {
            case "Agency":
                return getFacets(readOnlyModel.getAgencyModel());
            case "Attribute":
                return getFacets(readOnlyModel.getAttributeModel());
            case "Interval":
                return getFacets(readOnlyModel.getIntervalModel());
            case "Location":
                return getFacets(readOnlyModel.getLocationModel());
            case "Product":
                return getFacets(readOnlyModel.getProductModel());
            case "Relationship":
                return getFacets(readOnlyModel.getRelationshipModel());
            case "StatusCode":
                return getFacets(readOnlyModel.getStatusCodeModel());
            case "Unit":
                return getFacets(readOnlyModel.getAgencyModel());
        }
        throw new WebApplicationException(String.format("%s does not exist",
                                                        ruleformType),
                                          Status.NOT_FOUND);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> FacetContext<RuleForm, Network> createContext(Aspect<RuleForm> aspect) {
        return new FacetContext(aspect, readOnlyModel, uriInfo);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<FacetContext<?, ?>> getFacets(NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        List<FacetContext<?, ?>> facets = new ArrayList<>();
        for (Aspect<RuleForm> aspect : networkedModel.getAllFacets()) {
            facets.add(new FacetContext(aspect, readOnlyModel, uriInfo, true));
        }
        return facets;
    }
}
