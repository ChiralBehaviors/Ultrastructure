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

package com.chiralbehaviors.CoRE.tasks;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.chiralbehaviors.CoRE.phantasm.jsonld.resources.TransactionalResource;

/**
 * @author hhildebrand
 *
 */
@Path("job")
@Produces({ "application/json", "text/json" })
public class JobResource extends TransactionalResource {

    public JobResource(EntityManagerFactory emf) {
        super(emf);
    }

    @GET
    public Map<String, Object> getContext() {
        return null;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Map<String, Object> insert(Map<String, String> job) {
        return null;
    }
}
