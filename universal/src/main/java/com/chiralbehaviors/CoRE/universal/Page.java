/**
 * Copyright (c) 2017 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.universal;

import java.util.Map;

import com.chiralbehaviors.layout.schema.Relation;

/**
 * 
 * @author hhildebrand
 *
 */
public class Page {
    public static class Route {
        private final Map<String, String> extract;
        private final String              frameBy;
        private final String              path;

        public Route(String path, String frameBy, Map<String, String> extract) {
            this.path = path;
            this.frameBy = frameBy;
            this.extract = extract;
        }

        public Map<String, String> getExtract() {
            return extract;
        }

        public String getFrameBy() {
            return frameBy;
        }

        public String getPath() {
            return path;
        }
    }

    private final String             description;
    private final String             name;
    private final String             query;
    private final Map<String, Route> routes;
    private final String             title;

    public Page(String name, String description, String title, String query,
                Map<String, Route> routes) {
        this.name = name;
        this.description = description;
        this.title = title;
        this.query = query;
        this.routes = routes;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getQuery() {
        return query;
    }

    public Route getRoute(Relation relation) {
        return routes.get(relation.getField());
    }

    public String getTitle() {
        return title;
    }
}
