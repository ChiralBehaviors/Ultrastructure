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

import static com.chiralbehaviors.CoRE.universal.Universal.textOrNull;

import java.util.HashMap;
import java.util.Map;

import com.chiralbehaviors.layout.schema.Relation;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * @author hhildebrand
 *
 */
public class Page {
    private static Map<String, Action> actions(ArrayNode array) {
        Map<String, Action> actions = new HashMap<>();
        if (array == null) {
            return actions;
        }
        array.forEach(p -> {
            actions.put(p.get("_edge")
                         .get("relation")
                         .asText(),
                        new Action((ObjectNode) p));
        });
        return actions;
    }

    private static Map<String, Launch> launches(ArrayNode array) {
        Map<String, Launch> launches = new HashMap<>();
        if (array == null) {
            return launches;
        }
        array.forEach(p -> {
            launches.put(p.get("_edge")
                          .get("relation")
                          .asText(),
                         new Launch((ObjectNode) p));
        });
        return launches;
    }

    private static Map<String, Route> navigations(ArrayNode array) {
        Map<String, Route> navigations = new HashMap<>();
        if (array == null) {
            return navigations;
        }
        array.forEach(p -> {
            navigations.put(p.get("_edge")
                             .get("relation")
                             .asText(),
                            new Route((ObjectNode) p));
        });
        return navigations;
    }

    private final Map<String, Action> creates;
    private final Map<String, Action> deletes;
    private final String              description;
    private final String              frame;
    private final Map<String, Launch> launches;
    private final String              name;
    private final Map<String, Route>  navigations;
    private final String              query;
    private final String              title;
    private final Map<String, Action> updates;

    public Page(ObjectNode page) {
        this(textOrNull(page.get("name")), textOrNull(page.get("description")),
             textOrNull(page.get("title")), textOrNull(page.get("frame")),
             textOrNull(page.get("query")),
             actions((ArrayNode) page.get("creates")),
             actions((ArrayNode) page.get("updates")),
             actions((ArrayNode) page.get("deletes")),
             navigations((ArrayNode) page.get("navigations")),
             launches((ArrayNode) page.get("launches")));
    }

    public Page(String name, String description, String title, String frame,
                String query, Map<String, Action> creates,
                Map<String, Action> updates, Map<String, Action> deletes,
                Map<String, Route> navigations, Map<String, Launch> launches) {
        this.name = name;
        this.description = description;
        this.title = title;
        this.frame = frame;
        this.query = query;
        this.navigations = navigations;
        this.creates = creates;
        this.updates = updates;
        this.deletes = deletes;
        this.launches = launches;
    }

    public Action getCreate(Relation relation) {
        return creates.get(relation.getField());
    }

    public Action getDeletes(Relation relation) {
        return deletes.get(relation.getField());
    }

    public String getDescription() {
        return description;
    }

    public String getFrame() {
        return frame;
    }

    public Map<String, Launch> getLaunches() {
        return launches;
    }

    public String getName() {
        return name;
    }

    public Route getNavigation(Relation relation) {
        return navigations.get(relation.getField());
    }

    public String getQuery() {
        return query;
    }

    public String getTitle() {
        return title;
    }

    public Action getUpdate(Relation relation) {
        return updates.get(relation.getField());
    }
}
