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
import java.util.StringTokenizer;

import com.chiralbehaviors.layout.schema.Relation;
import com.chiralbehaviors.layout.schema.SchemaNode;
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
    private String                    description;
    private String                    frame;
    private final Map<String, Launch> launches;
    private String                    name;
    private final Map<String, Route>  navigations;
    private String                    query;
    private ObjectNode                style;
    private String                    title;
    private final Map<String, Action> updates;

    public Page() {
        creates = new HashMap<>();
        deletes = new HashMap<>();
        launches = new HashMap<>();
        navigations = new HashMap<>();
        updates = new HashMap<>();
    }

    public Page(ObjectNode page) {
        this(textOrNull(page.get("name")), textOrNull(page.get("description")),
             textOrNull(page.get("title")), textOrNull(page.get("frame")),
             textOrNull(page.get("query")),
             actions((ArrayNode) page.get("creates")),
             actions((ArrayNode) page.get("updates")),
             actions((ArrayNode) page.get("deletes")),
             navigations((ArrayNode) page.get("navigates")),
             launches((ArrayNode) page.get("launches")),
             (ObjectNode) page.get("style"));
    }

    public Page(String name, String description, String title, String frame,
                String query, Map<String, Action> creates,
                Map<String, Action> updates, Map<String, Action> deletes,
                Map<String, Route> navigations, Map<String, Launch> launches,
                ObjectNode style) {
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

    public void applyStyle(Relation root) {
        if (style == null) {
            return;
        }
        ObjectNode labels = (ObjectNode) style.get("labels");
        if (labels == null || labels.isNull()) {
            return;
        }
        labels.fieldNames()
              .forEachRemaining(path -> label(path, root, labels.get(path)
                                                                .asText()));
    }

    public void create(String field, Action action) {
        creates.put(field, action);
    }

    public void delete(String field, Action action) {
        deletes.put(field, action);
    }

    public Action getCreate(Relation relation) {
        return creates.get(relation.getField());
    }

    public Action getDelete(Relation relation) {
        return deletes.get(relation.getField());
    }

    public String getDescription() {
        return description;
    }

    public String getFrame() {
        return frame;
    }

    public Launch getLaunch(Relation relation) {
        return launches.get(relation.getField());
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

    public ObjectNode getStyle() {
        return style;
    }

    public String getTitle() {
        return title;
    }

    public Action getUpdate(Relation relation) {
        return updates.get(relation.getField());
    }

    public void navigate(String field, Route route) {
        navigations.put(field, route);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setStyle(ObjectNode style) {
        this.style = style;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return String.format("Page [name=%s, description=%s, frame=%s, title=%s]",
                             name, description, frame, title);
    }

    public void update(String field, Action action) {
        updates.put(field, action);
    }

    private void label(String path, Relation root, String label) {
        StringTokenizer toks = new StringTokenizer(path, "/");
        Relation current = root;
        SchemaNode leaf = null;
        while (current != null && toks.hasMoreTokens()) {
            leaf = current.getChild(toks.nextToken());
            if (leaf == null) {
                return;
            }
            if (leaf instanceof Relation) {
                current = (Relation) leaf;
            }
        }
        if (toks.hasMoreTokens()) {
            return; // Hit primitive before field spath grounded
        }
        leaf.setLabel(label);
    }
}
