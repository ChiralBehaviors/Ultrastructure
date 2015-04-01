/**
 * (C) Copyright 2015 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.network;

import java.util.UUID;

public class Edge {
    public String parent;
    public String relationship;
    public String child;
    public String inference;
    public String premise1;
    public String premise2;

    public Edge(UUID parent, UUID relationship, UUID child, UUID inference,
                UUID premise1, UUID premise2) {
        this.parent = parent.toString();
        this.relationship = relationship.toString();
        this.child = child.toString();
        this.inference = inference.toString();
        this.premise1 = premise1.toString();
        this.premise2 = premise2.toString();
    }

    public String toString(String id) {
        return String.format("Edge [id=%s, parent=%s, relationship=%s, child=%s, inference=%s, premise1=%s, premise2=%s]",
                             id, parent, relationship, child, inference,
                             premise1, premise2);
    }
}