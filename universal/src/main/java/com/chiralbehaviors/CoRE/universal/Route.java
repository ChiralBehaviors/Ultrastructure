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

import static com.chiralbehaviors.CoRE.universal.Page.extract;
import static com.chiralbehaviors.CoRE.universal.Universal.textOrNull;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * @author halhildebrand
 *
 */
public class Route {

    private Map<String, String> extract;
    private String              frameBy;
    private String              path;

    public Route() {
        extract = new HashMap<>();
    }

    public Route(ObjectNode route) {
        this(textOrNull(route.get("frameBy")), textOrNull(route.get("path")),
             extract(textOrNull(route.get("extract"))));
    }

    public Route(String frameBy, String path, Map<String, String> extract) {
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

    public void setExtract(Map<String, String> extract) {
        this.extract = extract;
    }

    public void setFrameBy(String frameBy) {
        this.frameBy = frameBy;
    }

    public void setPath(String path) {
        this.path = path;
    }

}