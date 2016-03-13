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

package com.chiralbehaviors.CoRE.phantasm.resources;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.existential.Attribute;
import com.chiralbehaviors.CoRE.existential.ExistentialRuleform;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.phantasm.jsonld.Constants;
import com.chiralbehaviors.CoRE.phantasm.jsonld.FacetContext;
import com.chiralbehaviors.CoRE.phantasm.jsonld.RuleformContext;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.codahale.metrics.annotation.Timed;

import io.dropwizard.auth.Auth;

/**
 * @author hhildebrand
 *
 */
@Path("json-ld/facet")
@Produces({ "application/json", "text/json" })
public class FacetResource extends TransactionalResource {

    public static URI facetResourceIri(UriInfo uriInfo) {
        UriBuilder ub = uriInfo.getBaseUriBuilder();
        ub.path(FacetResource.class);
        return ub.build();
    }

    public static URI getTypeContextIri() {
        UriBuilder ub = UriBuilder.fromResource(FacetResource.class);
        try {
            ub.path(FacetResource.class.getMethod("getContext"));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Unable to retrieve getContext method",
                                            e);
        }
        return ub.build();
    }

    @Context
    private UriInfo uriInfo;

    public FacetResource(EntityManagerFactory emf) {
        super(emf);
    }

    public FacetResource(EntityManagerFactory emf, UriInfo uriInfo) {
        this(emf);
        this.uriInfo = uriInfo;
    }

    @Timed
    @Path("{ruleform-type}/{classifier}/{classification}/{instance}")
    @POST
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Response apply(@Auth(required = false) AuthorizedPrincipal principal,
                                                                                                                               @PathParam("ruleform-type") String ruleformType,
                                                                                                                               @PathParam("classifier") UUID relationship,
                                                                                                                               @PathParam("classification") UUID ruleform,
                                                                                                                               @PathParam("instance") UUID existential,
                                                                                                                               @QueryParam("select") List<String> selection) {
        return readOnly(principal, readOnlyModel -> {
            Aspect<RuleForm> aspect = getAspect(ruleformType, relationship,
                                                ruleform, readOnlyModel);
            @SuppressWarnings("unchecked")
            FacetContext<RuleForm, Network> facet = FacetContext.getFacet(aspect,
                                                                          readOnlyModel,
                                                                          uriInfo);
            RuleForm instance = (RuleForm) readOnlyModel.getNetworkedModel(facet.facet.getClassification())
                                                        .find(existential);
            new PhantasmCRUD<RuleForm, Network>(readOnlyModel).apply(facet.facet,
                                                                     instance,
                                                                     s -> s);
            return Response.ok()
                           .build();
        });
    }

    @Timed
    @Path("{ruleform-type}/{classifier}/{classification}/{instance}")
    @DELETE
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Response delete(@Auth(required = false) AuthorizedPrincipal principal,
                                                                                                                                @PathParam("ruleform-type") String ruleformType,
                                                                                                                                @PathParam("classifier") UUID relationship,
                                                                                                                                @PathParam("classification") UUID ruleform,
                                                                                                                                @PathParam("instance") UUID existential,
                                                                                                                                @QueryParam("select") List<String> selection) {
        return readOnly(principal, readOnlyModel -> {
            Aspect<RuleForm> aspect = getAspect(ruleformType, relationship,
                                                ruleform, readOnlyModel);
            @SuppressWarnings("unchecked")
            FacetContext<RuleForm, Network> facet = FacetContext.getFacet(aspect,
                                                                          readOnlyModel,
                                                                          uriInfo);
            RuleForm instance = (RuleForm) readOnlyModel.getNetworkedModel(facet.facet.getClassification())
                                                        .find(existential);
            new PhantasmCRUD<RuleForm, Network>(readOnlyModel).remove(facet.facet,
                                                                      instance,
                                                                      false);
            return Response.ok()
                           .build();
        });
    }

    @Timed
    @Path("{ruleform-type}/{classifier}/{classification}/instances")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> getAllInstances(@Auth(required = false) AuthorizedPrincipal principal,
                                                                                                                                                    @PathParam("ruleform-type") String ruleformType,
                                                                                                                                                    @PathParam("classifier") UUID classifier,
                                                                                                                                                    @PathParam("classification") UUID classification) {
        return readOnly(principal, readOnlyModel -> {
            Aspect<RuleForm> aspect = getAspect(ruleformType, classifier,
                                                classification, readOnlyModel);

            Map<String, Object> returned = new TreeMap<>();

            @SuppressWarnings("unchecked")
            FacetContext<RuleForm, Network> facet = FacetContext.getFacet(aspect,
                                                                          readOnlyModel,
                                                                          uriInfo);

            returned.put(Constants.CONTEXT,
                         FacetContext.getContextIri(facet.facet, uriInfo));
            returned.put(Constants.ID,
                         FacetContext.getAllInstancesIri(facet.facet, uriInfo));

            List<Map<String, Object>> facets = new ArrayList<>();
            returned.put(Constants.GRAPH, facets);
            NetworkedModel<RuleForm, ?, ?, ?> networkedModel = readOnlyModel.getNetworkedModel(aspect.getClassification());
            for (RuleForm ruleform : networkedModel.getChildren(aspect.getClassification(),
                                                                aspect.getClassifier()
                                                                      .getInverse())) {
                facets.add(facet.toCompactInstance(ruleform, readOnlyModel,
                                                   uriInfo));
            }
            return returned;
        });
    }

    @Timed
    @Path("context")
    @GET
    public Map<String, Object> getContext() {
        Map<String, Object> node = new TreeMap<>();
        Map<String, Object> context = new TreeMap<>();
        node.put(Constants.CONTEXT, context);
        context.put(Constants.ID, getTypeContextIri().toASCIIString());
        return node;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Path("{ruleform-type}/{classifier}/{classification}/context")
    @GET
    public Map<String, Object> getContext(@Auth(required = false) AuthorizedPrincipal principal,
                                          @PathParam("ruleform-type") String ruleformType,
                                          @PathParam("classifier") UUID relationship,
                                          @PathParam("classification") UUID ruleform) {
        return readOnly(principal, readOnlyModel -> {
            Aspect aspect = getAspect(ruleformType, relationship, ruleform,
                                      readOnlyModel);
            return FacetContext.getFacet(aspect, readOnlyModel, uriInfo)
                               .toContext(uriInfo);
        });
    }

    @Timed
    @Path("{ruleform-type}/{classifier}/{classification}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> getFacet(@Auth(required = false) AuthorizedPrincipal principal,
                                                                                                                                             @PathParam("ruleform-type") String ruleformType,
                                                                                                                                             @PathParam("classifier") UUID relationship,
                                                                                                                                             @PathParam("classification") UUID ruleform) {

        return readOnly(principal, readOnlyModel -> {
            Aspect<RuleForm> aspect = getAspect(ruleformType, relationship,
                                                ruleform, readOnlyModel);
            @SuppressWarnings("unchecked")
            FacetContext<RuleForm, Network> facet = FacetContext.getFacet(aspect,
                                                                          readOnlyModel,
                                                                          uriInfo);
            Map<String, Object> type = facet.toContext(uriInfo);
            type.put(Constants.ID, "");
            type.put(Constants.TYPE, "http://ultrastructure.me#Facet");
            return type;
        });
    }

    @Timed
    @Path("{ruleform-type}/{classifier}/{classification}/@facet:{instance}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> getFacetQualifiedInstance(@Auth(required = false) AuthorizedPrincipal principal,
                                                                                                                                                              @PathParam("ruleform-type") String ruleformType,
                                                                                                                                                              @PathParam("classifier") UUID relationship,
                                                                                                                                                              @PathParam("classification") UUID ruleform,
                                                                                                                                                              @PathParam("instance") UUID existential) {
        return getInstance(principal, ruleformType, relationship, ruleform,
                           existential);
    }

    @Timed
    @GET
    public Map<String, Object> getFacetRuleforms() {

        List<String> ruleforms = new ArrayList<>();
        ruleforms.add("Agency");
        ruleforms.add("Attribute");
        ruleforms.add("Interval");
        ruleforms.add("Location");
        ruleforms.add("Product");
        ruleforms.add("Relationship");
        ruleforms.add("Status Code");
        ruleforms.add("Unit");

        Map<String, Object> context = new TreeMap<>();
        Map<String, Object> keyTerm = new TreeMap<>();
        keyTerm.put(Constants.ID, facetResourceIri(uriInfo));
        keyTerm.put(Constants.TYPE, Constants.ID);
        keyTerm.put(Constants.CONTAINER, Constants.LIST);
        context.put("ruleforms", keyTerm);
        Map<String, Object> returned = new TreeMap<>();
        returned.put(Constants.CONTEXT, context);
        returned.put(Constants.ID, facetResourceIri(uriInfo));
        returned.put("ruleforms", ruleforms);
        return returned;
    }

    @Timed
    @Path("{ruleform-type}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> getFacets(@Auth(required = false) AuthorizedPrincipal principal,
                                                                                                                                              @PathParam("ruleform-type") String ruleformType) {
        return readOnly(principal, readOnlyModel -> {
            Class<?> clazz = RuleformResource.entityMap.get(ruleformType);
            switch (ruleformType) {
                case "Agency":
                    return getFacets(readOnlyModel.getAgencyModel(), clazz,
                                     readOnlyModel);
                case "Attribute":
                    return getFacets(readOnlyModel.getAttributeModel(), clazz,
                                     readOnlyModel);
                case "Interval":
                    return getFacets(readOnlyModel.getIntervalModel(), clazz,
                                     readOnlyModel);
                case "Location":
                    return getFacets(readOnlyModel.getLocationModel(), clazz,
                                     readOnlyModel);
                case "Product":
                    return getFacets(readOnlyModel.getProductModel(), clazz,
                                     readOnlyModel);
                case "Relationship":
                    return getFacets(readOnlyModel.getRelationshipModel(),
                                     clazz, readOnlyModel);
                case "StatusCode":
                    return getFacets(readOnlyModel.getStatusCodeModel(), clazz,
                                     readOnlyModel);
                case "Unit":
                    return getFacets(readOnlyModel.getAgencyModel(), clazz,
                                     readOnlyModel);
            }
            throw new WebApplicationException(String.format("%s does not exist",
                                                            ruleformType),
                                              Status.NOT_FOUND);
        });
    }

    @Timed
    @Path("{ruleform-type}/{classifier}/{classification}/{instance}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> getInstance(@Auth(required = false) AuthorizedPrincipal principal,
                                                                                                                                                @PathParam("ruleform-type") String ruleformType,
                                                                                                                                                @PathParam("classifier") UUID relationship,
                                                                                                                                                @PathParam("classification") UUID ruleform,
                                                                                                                                                @PathParam("instance") UUID existential) {
        return readOnly(principal, readOnlyModel -> {
            Aspect<RuleForm> aspect = getAspect(ruleformType, relationship,
                                                ruleform, readOnlyModel);
            NetworkedModel<RuleForm, ?, ?, ?> networkedModel = readOnlyModel.getNetworkedModel(aspect.getClassification());
            RuleForm instance = Ruleform.initializeAndUnproxy(networkedModel.find(existential));
            if (instance == null) {
                throw new WebApplicationException(String.format("node %s is not found %s (%s)",
                                                                instance, this,
                                                                aspect),
                                                  Status.NOT_FOUND);
            }
            @SuppressWarnings("unchecked")
            FacetContext<RuleForm, Network> node = FacetContext.getFacet(aspect,
                                                                         readOnlyModel,
                                                                         uriInfo);
            Map<String, Object> object = node.toCompactInstance(instance,
                                                                readOnlyModel,
                                                                uriInfo);
            object.put(Constants.CONTEXT,
                       FacetContext.getContextIri(node.facet, uriInfo));
            object.put(Constants.TYPE, Constants.FACET);
            object.put(Constants.TYPENAME, node.getTypeName());
            return object;
        });
    }

    @Timed
    @Path("{ruleform-type}/{classifier}/{classification}/@ruleform:{instance}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> getRuleformQualifiedInstance(@Auth(required = false) AuthorizedPrincipal principal,
                                                                                                                                                                 @PathParam("ruleform-type") String ruleformType,
                                                                                                                                                                 @PathParam("classifier") UUID relationship,
                                                                                                                                                                 @PathParam("classification") UUID ruleform,
                                                                                                                                                                 @PathParam("instance") UUID existential) {
        return getInstance(principal, ruleformType, relationship, ruleform,
                           existential);
    }

    @Timed
    @Path("{ruleform-type}/{classifier}/{classification}/term/{term}")
    @GET
    public <RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>> Map<String, Object> getTerm(@Auth(required = false) AuthorizedPrincipal principal,
                                                                                                                                            @PathParam("ruleform-type") String ruleformType,
                                                                                                                                            @PathParam("classifier") UUID relationship,
                                                                                                                                            @PathParam("classification") UUID ruleform,
                                                                                                                                            @PathParam("term") String term) {
        return readOnly(principal, readOnlyModel -> {
            Aspect<RuleForm> aspect = getAspect(ruleformType, relationship,
                                                ruleform, readOnlyModel);
            @SuppressWarnings("unchecked")
            FacetContext<RuleForm, Network> facet = FacetContext.getFacet(aspect,
                                                                          readOnlyModel,
                                                                          uriInfo);
            Map<String, Object> clazz = new TreeMap<>();
            clazz.put(Constants.ID, FacetContext.getTermIri(term));
            Attribute attribute = facet.attributes.get(term)
                                                  .getAuthorizedAttribute();
            if (attribute != null) {
                clazz.put(Constants.TYPE, RuleformContext.getIri(attribute));
            } else if (facet.getChild(term) != null) {
                NetworkAuthorization<?> target = facet.getChild(term);
                clazz.put(Constants.TYPE,
                          FacetContext.getFullFacetIri(target, uriInfo));

            } else if (facet.getRuleformTerm(term) != null) {
                clazz.put(Constants.TYPE, facet.getRuleformTerm(term).type);
            } else {
                throw new WebApplicationException(String.format("term %s does not exist on %s",
                                                                term, aspect),
                                                  Status.NOT_FOUND);
            }
            return clazz;
        });
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> Map<String, Object> getFacets(NetworkedModel<RuleForm, ?, ?, ?> networkedModel,
                                                                                              Class<?> clazz,
                                                                                              Model readOnlyModel) {

        Map<String, Object> context = new TreeMap<>();
        context.put(Constants.VOCAB, uriInfo.getBaseUriBuilder()
                                            .path(FacetResource.class)
                                            .build());

        Map<String, Object> returned = new TreeMap<>();

        returned.put(Constants.CONTEXT, context);
        returned.put(Constants.ID, FacetContext.getFacetsIri(clazz, uriInfo));

        List<Map<String, Object>> facets = new ArrayList<>();
        returned.put(Constants.GRAPH, facets);

        for (Aspect<RuleForm> aspect : networkedModel.getAllFacets()) {
            aspect = new Aspect<>(Ruleform.initializeAndUnproxy(aspect.getClassifier()),
                                  Ruleform.initializeAndUnproxy(aspect.getClassification()));
            @SuppressWarnings({ "unchecked" })
            FacetContext<RuleForm, ?> facet = FacetContext.getFacet(aspect,
                                                                    readOnlyModel,
                                                                    uriInfo);
            Map<String, Object> obj = new TreeMap<>();
            obj.put(Constants.ID, FacetContext.getFacetIri(facet.facet));
            obj.put(Constants.TYPENAME, facet.getTypeName());
            facets.add(obj);
        }

        return returned;
    }
}
