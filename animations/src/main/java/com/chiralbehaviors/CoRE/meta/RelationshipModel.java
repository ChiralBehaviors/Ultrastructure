/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.meta;

import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.network.RelationshipAttribute;
import com.chiralbehaviors.CoRE.network.RelationshipAttributeAuthorization;
import com.chiralbehaviors.CoRE.network.RelationshipNetwork;

/**
 * @author hhildebrand
 *
 */
public interface RelationshipModel
        extends
        NetworkedModel<Relationship, RelationshipNetwork, RelationshipAttributeAuthorization, RelationshipAttribute> {

    /**
     * Creates two relationships and sets them as inverses of each other
     *
     * @param rel1Name
     * @param rel1Description
     * @param rel2Name
     * @param rel2Description
     * @return The relationship created with rel1Name. The second relationship
     *         can be retrieved by calling getInverse() on the return value.
     */
    Relationship create(String rel1Name, String rel1Description,
                        String rel2Name, String rel2Description);

}
