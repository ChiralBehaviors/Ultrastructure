/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
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
	private String rootResource;
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
		return Response.ok(getClass().getResourceAsStream("images/" + image))
				.build();
	}

	@GET
	@Path("/{resource}")
	@Produces(MediaType.WILDCARD)
	public Response getRoot(@PathParam("version") int version,
			@PathParam("resource") String resource) {
		if (resource == null) {
			return Response.ok(getClass().getResourceAsStream("jest.html"))
					.build();
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
