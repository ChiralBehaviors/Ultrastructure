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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.hellblazer.CoRE.Ruleform;

/**
 * @author hparry
 *
 */
@Path("/v{version : \\d+}/services/data/ruleform")
public class RuleformResource {
    
    private EntityManager em;
    
    public RuleformResource(EntityManagerFactory emf) {
        this.em = emf.createEntityManager();
    }
    
    @GET
    @Path("/{ruleform}/{id}")
    @Produces({ MediaType.APPLICATION_JSON, "text/json" })
    public Ruleform getResource(@PathParam("ruleform") String type,
                        @PathParam("id") long id) throws ClassNotFoundException {
        String qualifiedName = "com.hellblazer.CoRE." + type.toLowerCase() + "." + type;
        Class<? extends Ruleform> c = (Class<? extends Ruleform>) Class.forName(qualifiedName);
        Ruleform rf = em.find(c, id);
        return rf;
    }

}
