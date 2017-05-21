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
public class Launch {
    private String  frame;
    private String  frameBy;
    private String  immediate;
    private String  launchBy;
    private boolean meta;

    public boolean isMeta() {
        return meta;
    }

    public Launch() {
    }

    public Launch(ObjectNode launch) {
        this(textOrNull(launch.get("launchBy")),
             textOrNull(launch.get("frameBy")), textOrNull(launch.get("frame")),
             ((BooleanNode) launch.get("meta") == null ? JsonNodeFactory.instance.booleanNode(false)
                                                       : launch.get("meta")).asBoolean(),
             launch.get("immediate") == null ? null
                                             : textOrNull(launch.get("immediate")
                                                                .get("id")));
    }

    public Launch(String launchBy, String frameBy, String frame, boolean meta,
                  String immediate) {
        this.frameBy = frameBy;
        this.launchBy = launchBy;
        this.frame = frame;
        this.meta = meta;
        this.immediate = immediate;
    }

    public String getFrame() {
        return frame;
    }

    public String getFrameBy() {
        return frameBy;
    }

    public String getImmediate() {
        return immediate;
    }

    public String getLaunchBy() {
        return launchBy;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public void setFrameBy(String frameBy) {
        this.frameBy = frameBy;
    }

    public void setImmediate(String immediate) {
        this.immediate = immediate;
    }

    public void setLaunchBy(String launchBy) {
        this.launchBy = launchBy;
    }

    @Override
    public String toString() {
        return String.format("Launch [frame=%s, frameBy=%s, immediate=%s, launchBy=%s]",
                             frame, frameBy, immediate, launchBy);
    }

    public void setMeta(boolean meta) {
        this.meta = meta;
    }
}
