/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
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

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.hellblazer.CoRE.access.formatting.TokenReplacedStream;

/**
 * @author hhildebrand
 * 
 */
@Path("/v{version : \\d+}/services/data/crud-gui")
public class CrudGuiResource {
    private String       rootResource;
    private final String unitName;

    /**
     * @param unitName
     */
    public CrudGuiResource(String unitName) {
        this.unitName = unitName;
    }

    @GET
    @Path("/images/{image}")
    @Produces("image/*")
    public Response getImage(@PathParam("version") int version,
                             @PathParam("image") String image) {
        return Response.ok(getClass().getResourceAsStream("images/" + image)).build();
    }

    @GET
    @Path("/{resource}")
    @Produces(MediaType.WILDCARD)
    public Response getRoot(@PathParam("version") int version,
                            @PathParam("resource") String resource) {
        if (resource == null) {
            return Response.ok(getClass().getResourceAsStream("jest.html")).build();
        }
        return Response.ok(getClass().getResourceAsStream(resource)).build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    public Response getRoot(@PathParam("version") int version,
                            @Context UriInfo uriInfo) throws IOException {
        if (rootResource == null) {
            String base = uriInfo.getRequestUri().toString();
            base = base.endsWith("/") ? base : base + "/";
            String[] tokens = { "${persistence.unit}", unitName, "${jest.uri}",
                    base, "${webapp.name}", "/", "${servlet.name}", "crud",
                    "${server.name}", uriInfo.getRequestUri().getHost(),
                    "${server.port}", "" + uriInfo.getRequestUri().getPort(),

                    "${dojo.base}", Constants.DOJO_BASE_URL, "${dojo.theme}",
                    Constants.DOJO_THEME,

            };
            InputStream in = getClass().getResourceAsStream("jest.html");
            CharArrayWriter out = new CharArrayWriter();
            new TokenReplacedStream().replace(in, out, tokens);
            rootResource = out.toString();
        }
        return Response.ok(rootResource).build();
    }
}
