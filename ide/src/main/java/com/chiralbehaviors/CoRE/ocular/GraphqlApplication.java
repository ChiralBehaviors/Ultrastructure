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

package com.chiralbehaviors.CoRE.ocular;

import java.net.URL;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author hhildebrand
 *
 */
public class GraphqlApplication {
    @JsonProperty
    private URL               endpoint;
    @JsonProperty
    private String            root;
    @JsonProperty
    private Map<String, Page> routes;

    public URL getEndpoint() {
        return endpoint;
    }

    public Page getRoot() {
        return routes.get(root);
    }

    public Page route(String path) {
        return routes.get(path);
    }
}