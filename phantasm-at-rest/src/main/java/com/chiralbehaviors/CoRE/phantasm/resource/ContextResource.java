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

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.NetworkedModel;
import com.chiralbehaviors.CoRE.product.Product;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author hhildebrand
 *
 */
@Path("json-ld/context")
public class ContextResource extends TransactionalResource {

    public ContextResource(EntityManagerFactory emf) {
        super(emf);
    }

    @Produces({ "application/json", "text/json" })
    @Path("product/{classifier}/{classification}")
    @GET
    public JsonNode getProductContext(@PathParam("classifier") String relationship,
                                      @PathParam("classification") String ruleform) {
        UUID classifier;
        try {
            classifier = UUID.fromString(relationship);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        UUID classification;
        try {
            classification = UUID.fromString(ruleform);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        Aspect<Product> aspect;
        NetworkedModel<Product, ?, ?, ?> networkedModel = readOnlyModel.getProductModel();
        try {
            aspect = networkedModel.getAspect(classifier, classification);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        }

        return buildContext(aspect, networkedModel);
    }

    private <RuleForm extends ExistentialRuleform<RuleForm, ?>> JsonNode buildContext(Aspect<RuleForm> aspect,
                                                                                      NetworkedModel<RuleForm, ?, ?, ?> networkedModel) {
        ObjectNode container = new ObjectNode(JsonNodeFactory.withExactBigDecimals(true));
        ObjectNode context = new ObjectNode(JsonNodeFactory.withExactBigDecimals(true));
        container.set("@context", context);
        for (AttributeAuthorization<RuleForm, ?> auth : networkedModel.getAttributeAuthorizations(aspect)) {
            String irl = irlFrom(auth.getAuthorizedAttribute());
            String type = typeFrom(auth.getAuthorizedAttribute());
            if (type == null) {
                context.put(auth.getAuthorizedAttribute().getName(), irl);
            } else {
                ObjectNode term = new ObjectNode(JsonNodeFactory.withExactBigDecimals(true));
                term.put("@id", irl);
                term.put("@type", irl);
                context.set(auth.getAuthorizedAttribute().getName(), term);
            }
        }
        return container;
    }

    /**
     * @param authorizedAttribute
     * @return
     */
    private String typeFrom(Attribute authorizedAttribute) {
        AttributeValue<Attribute> irl = readOnlyModel.getAttributeModel().getAttributeValue(authorizedAttribute,
                                                                                            readOnlyModel.getKernel().getIRL());
        return irl != null ? irl.getTextValue() : null;
    }

    /**
     * @param authorizedAttribute
     * @return
     */
    private String irlFrom(Attribute authorizedAttribute) {
        AttributeValue<Attribute> irl = readOnlyModel.getAttributeModel().getAttributeValue(authorizedAttribute,
                                                                                            readOnlyModel.getKernel().getIRL());
        if (irl != null) {
            return irl.getTextValue();
        }
        switch (authorizedAttribute.getValueType()) {
            case TEXT:
                return "http://schema.org/text";
            case BINARY:
                return "http://schema.org/binary";
            case BOOLEAN:
                return "http://schema.org/boolean";
            case INTEGER:
                return "http://schema.org/integer";
            case NUMERIC:
                return "http://schema.org/numeric";
            case TIMESTAMP:
                return "http://schema.org/timestamp";
        }
        return null;
    }
}
