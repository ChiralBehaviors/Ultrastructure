/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE.meta;

import com.chiralbehaviors.CoRE.domain.Relationship;

/**
 * @author hhildebrand
 *
 */
public interface RelationshipModel extends ExistentialModel<Relationship> {

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
