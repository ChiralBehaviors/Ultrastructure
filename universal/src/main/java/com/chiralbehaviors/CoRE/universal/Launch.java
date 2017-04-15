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
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author halhildebrand
 *
 */
public class Launch {
    private final String frame;
    private final String frameBy;
    private final String immediate;
    private final String launchBy;

    public Launch(ObjectNode launch) {
        this(textOrNull(launch.get("frameBy")),
             textOrNull(launch.get("launchBy")),
             textOrNull(launch.get("frame")),
             textOrNull(launch.get("immediate")));
    }

    public Launch(String launchBy, String frameBy, String frame,
                  String immediate) {
        this.frameBy = frameBy;
        this.launchBy = launchBy;
        this.frame = frame;
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
}
