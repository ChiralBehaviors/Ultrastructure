/**
 * Copyright (C) 2014 Halloran Parry. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hellblazer.CoRE.access.resource;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.reflections.Reflections;

import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.Ruleform;

/**
 * @author hparry
 * 
 */
@Path("/v{version : \\d+}/services/data/ruleform")
public class RuleformResource {

    protected EntityManager                              em;
    private final Map<String, Class<? extends Ruleform>> entityMap = new HashMap<String, Class<? extends Ruleform>>();

    public RuleformResource(EntityManagerFactory emf) {
        Reflections reflections = new Reflections(
                                                  Ruleform.class.getPackage().getName());
        for (@SuppressWarnings("rawtypes") Class<? extends ExistentialRuleform> form : reflections.getSubTypesOf(ExistentialRuleform.class)) {
            if (!Modifier.isAbstract(form.getModifiers())) {
                Class<?> prev = entityMap.put(form.getSimpleName(), form);
                assert prev == null : String.format("Found previous mapping %s of: %s",
                                                    prev, form);
            }
        }
        this.em = emf.createEntityManager();
    }

    @GET
    @Path("/")
    @Produces({ MediaType.APPLICATION_JSON, "text/json" })
    public Set<String> getRuleformTypes() {
        return entityMap.keySet();
    }





}
