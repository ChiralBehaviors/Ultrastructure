/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.layout.toy;

import java.util.Map;

import com.chiralbehaviors.layout.schema.Relation;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author hhildebrand
 *
 */
public class Page {
    public static class Route {
        @JsonProperty
        private String              path;
        @JsonProperty
        private Map<String, String> extract;

        public String getPath() {
            return path;
        }

        public Map<String, String> getExtract() {
            return extract;
        }
    }

    @JsonProperty
    private String             query;
    @JsonProperty
    private Map<String, Route> routing;
    @JsonProperty
    private String             title;

    public String getQuery() {
        return query;
    }

    public Route getRoute(Relation relation) {
        return routing.get(relation.getField());
    }

    public String getTitle() {
        return title;
    }
}
