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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.chiralbehaviors.CoRE.phantasm.jsonld.Constants;
import com.chiralbehaviors.CoRE.phantasm.jsonld.FacetContext;
import com.chiralbehaviors.CoRE.phantasm.jsonld.FacetNode;

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

    @Path("{ruleform-type}/{classifier}/{classification}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> FacetContext<RuleForm, Network> getFacet(@PathParam("ruleform-type") String ruleformType,
                                                                                                                                                         @PathParam("classifier") String relationship,
                                                                                                                                                         @PathParam("classification") String ruleform) {
        return createContext(getAspect(ruleformType, relationship, ruleform));
    }

    @Path("{ruleform-type}/{classifier}/{classification}/instances")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<Map<String, String>> getFacetInstances(@PathParam("ruleform-type") String ruleformType,
                                                                                                                                                            @PathParam("classifier") String relationship,
                                                                                                                                                            @PathParam("classification") String ruleform) {
        Aspect<RuleForm> aspect = getAspect(ruleformType, relationship,
                                            ruleform);
        return getFacetInstances(aspect);
    }

    /**
     * @param aspect
     * @return
     */
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<Map<String, String>> getFacetInstances(Aspect<RuleForm> aspect) {
        List<Map<String, String>> facets = new ArrayList<>();
        NetworkedModel<RuleForm, ?, ?, ?> networkedModel = readOnlyModel.getNetworkedModel(aspect.getClassification());

        for (RuleForm ruleform : networkedModel.getChildren(aspect.getClassification(),
                                                            aspect.getClassifier().getInverse())) {
            Map<String, String> ctx = new HashMap<>();
            FacetNode<RuleForm, Network> facet = new FacetNode<>(ruleform,
                                                                 aspect,
                                                                 readOnlyModel,
                                                                 uriInfo);
            ctx.put(Constants.CONTEXT, facet.getContext().getIri());
            ctx.put(Constants.ID, facet.getIri());
            facets.add(ctx);
        }
        return facets;
    }

    @Path("{ruleform-type}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<Map<String, String>> getFacets(@PathParam("ruleform-type") String ruleformType) {
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
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> FacetContext<RuleForm, Network> createContext(Aspect<?> aspect) {
        return new FacetContext(aspect, readOnlyModel, uriInfo);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> List<Map<String, String>> getFacets(NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        List<Map<String, String>> facets = new ArrayList<>();
        for (Aspect<RuleForm> aspect : networkedModel.getAllFacets()) {
            Map<String, String> ctx = new HashMap<>();
            FacetContext facetContext = new FacetContext(aspect, readOnlyModel,
                                                         uriInfo, true);
            ctx.put("Type Name",
                    String.format("%s:%s", aspect.getClassifier().getName(),
                                  aspect.getClassification().getName()));
            ctx.put(Constants.ID, facetContext.getIri());
            ctx.put("All Facet Instances", facetContext.getAllInstancesIri());
            facets.add(ctx);
        }
        return facets;
    }
}
