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

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.reflections.Reflections;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.phantasm.jsonld.RuleformContext;
import com.chiralbehaviors.CoRE.phantasm.jsonld.RuleformNode;

/**
 * @author hhildebrand
 *
 */
@Path("json-ld/ruleform")
@Produces({ "application/json", "text/json" })
public class RuleformResource extends TransactionalResource {

    private final Map<String, Class<? extends Ruleform>> entityMap = new HashMap<>();

    @Context
    private UriInfo uriInfo;

    private final ArrayList<String> sortedRuleformTypes;

    public RuleformResource(EntityManagerFactory emf) {
        super(emf);
        Reflections reflections = new Reflections(Ruleform.class.getPackage().getName());
        for (Class<? extends Ruleform> form : reflections.getSubTypesOf(Ruleform.class)) {
            if (!Modifier.isAbstract(form.getModifiers())) {
                entityMap.put(form.getSimpleName(), form);
            }
        }
        sortedRuleformTypes = new ArrayList<>(entityMap.keySet());
        Collections.sort(sortedRuleformTypes);
    }

    @Path("context/{ruleform-type}")
    public RuleformContext getContext(@PathParam("ruleform-type") String ruleformType) {
        return new RuleformContext(entityMap.get(ruleformType), uriInfo);
    }

    @Path("node/{ruleform-type}/{uuid}")
    public RuleformNode getNode(@PathParam("ruleform-type") String ruleformType,
                                @PathParam("uuid") String ruleformId) {
        UUID uuid = toUuid(ruleformId);
        Ruleform ruleform = readOnlyModel.getEntityManager().find(entityMap.get(ruleformType),
                                                                  uuid);
        if (ruleform == null) {
            throw new WebApplicationException(String.format("%s:%s does not exist",
                                                            ruleformType,
                                                            ruleformId));
        }
        return new RuleformNode(ruleform, uriInfo);
    }

    @GET
    public List<String> getRuleformTypes() {
        return sortedRuleformTypes;
    }
}
