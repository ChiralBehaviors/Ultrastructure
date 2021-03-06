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

import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * @author halhildebrand
 *
 */
public class Route {

    private ObjectNode extract;
    private String     frameBy;
    private boolean    meta = false;
    private String     path;

    public Route() {
        extract = JsonNodeFactory.instance.objectNode();
    }

    public Route(ObjectNode route) {
        this(textOrNull(route.get("frameBy")), textOrNull(route.get("path")),
             (ObjectNode) route.get("extract"),
             ((BooleanNode) route.get("meta") == null ? JsonNodeFactory.instance.booleanNode(false)
                                                      : route.get("meta")).asBoolean());
    }

    public Route(String frameBy, String path, ObjectNode extract,
                 boolean meta) {
        this.path = path;
        this.frameBy = frameBy;
        this.extract = extract;
        this.meta = meta;
    }

    public ObjectNode getExtract() {
        return extract;
    }

    public String getFrameBy() {
        return frameBy;
    }

    public String getPath() {
        return path;
    }

    public boolean isMeta() {
        return meta;
    }

    public void setExtract(ObjectNode extract) {
        this.extract = extract;
    }

    public void setFrameBy(String frameBy) {
        this.frameBy = frameBy;
    }

    public void setMeta(boolean meta) {
        this.meta = meta;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return String.format("Route [path=%s, meta=%s, frameBy=%s, extract=%s]",
                             path, meta, frameBy, extract);
    }

}