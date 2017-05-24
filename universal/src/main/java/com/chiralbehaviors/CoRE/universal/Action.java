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
 * @author halhildebrand
 *
 */
public class Action {
    private ObjectNode extract;
    private String     frameBy;
    private boolean    meta;
    private String     query;

    public Action() {
    }

    public Action(ObjectNode action) {
        this(textOrNull(action.get("query")), textOrNull(action.get("frameBy")),
             ((BooleanNode) action.get("meta") == null ? JsonNodeFactory.instance.booleanNode(false)
                                                       : action.get("meta")).asBoolean(),
             (ObjectNode) action.get("extract"));
    }

    public Action(String query, String frameBy, boolean meta,
                  ObjectNode extract) {
        this.query = query;
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

    public String getQuery() {
        return query;
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

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return String.format("Action [extract=%s, frameBy=%s]", extract,
                             frameBy);
    }
}
