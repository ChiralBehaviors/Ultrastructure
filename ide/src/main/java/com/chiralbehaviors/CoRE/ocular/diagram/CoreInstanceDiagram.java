/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 *  This file is part of Ultrastructure.
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

package com.chiralbehaviors.CoRE.ocular.diagram;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.fasterxml.jackson.databind.node.ArrayNode;

import de.fxdiagram.lib.simple.OpenableDiagramNode;
import de.fxdiagram.mapping.shapes.BaseDiagram;

/**
 * Entry into the core instance
 * 
 * @author hhildebrand
 *
 */
public class CoreInstanceDiagram extends BaseDiagram<String> {

    public CoreInstanceDiagram(String baseUrl) throws MalformedURLException,
                                               URISyntaxException {
        UriBuilder wspEndpoint = UriBuilder.fromUri(new URL(baseUrl).toURI());
        WebTarget endpoint = ClientBuilder.newClient()
                                          .target(wspEndpoint)
                                          .path("api/workspace");
        Builder invocationBuilder = endpoint.request(MediaType.APPLICATION_JSON_TYPE);
        invocationBuilder.get(ArrayNode.class)
                         .forEach(wsp -> {
                             OpenableDiagramNode node = new OpenableDiagramNode(wsp.get("name")
                                                                                   .asText());
                             WorkspaceDiagram diagram;
                             try {
                                 diagram = new WorkspaceDiagram(wsp);
                             } catch (Exception e) {
                                 throw new IllegalStateException(e);
                             }
                             node.setInnerDiagram(diagram);
                             getNodes().add(node);
                         });
    }
}
