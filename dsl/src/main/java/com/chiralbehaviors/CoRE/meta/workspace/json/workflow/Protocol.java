/**
 * Copyright (c) 2018 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.meta.workspace.json.workflow;

import java.math.BigDecimal;

/**
 * @author halhildebrand
 *
 */
public class Protocol {
    public static class Child {
        public String     assign;
        public String     children;
        public String     from;
        public String     product;
        public BigDecimal quantity;
        public String     service;
        public String     to;
    }

    public static class Match {
        public String     assign;
        public String     from;
        public String     product;
        public BigDecimal quantity;
        public String     requester;
        public String     service;
        public String     to;
    }

    public Child  child;
    public String description;
    public Match  match;
}
