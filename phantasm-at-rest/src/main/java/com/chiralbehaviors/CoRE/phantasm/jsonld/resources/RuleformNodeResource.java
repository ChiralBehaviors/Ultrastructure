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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.reflections.Reflections;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform;

/**
 * @author hhildebrand
 *
 */
@Path("json-ld/ruleform/node")
@Produces({ "application/json", "text/json" })
public class RuleformNodeResource extends TransactionalResource {

    private final Map<String, Class<? extends Ruleform>> entityMap = new HashMap<String, Class<? extends Ruleform>>();

    public RuleformNodeResource(EntityManagerFactory emf) {
        super(emf);
        Reflections reflections = new Reflections(Ruleform.class.getPackage().getName());
        for (@SuppressWarnings("rawtypes")
        Class<? extends ExistentialRuleform> form : reflections.getSubTypesOf(ExistentialRuleform.class)) {
            if (!Modifier.isAbstract(form.getModifiers())) {
                Class<?> prev = entityMap.put(form.getSimpleName(), form);
                assert prev == null : String.format("Found previous mapping %s of: %s",
                                                    prev, form);
            }
        }
    }

    @GET
    public Set<String> getRuleformTypes() {
        return entityMap.keySet();
    }

    @Path("{ruleform-type}/{uuid}")
    public Object resolve(@PathParam("ruleform-type") String ruleformType,
                          @PathParam("uuid") String ruleformId) {
        return null;
    }
}
