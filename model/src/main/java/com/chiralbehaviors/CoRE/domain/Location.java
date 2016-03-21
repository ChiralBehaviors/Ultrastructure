/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
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

package com.chiralbehaviors.CoRE.domain;

import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownLocation;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;

/**
 * @author hhildebrand
 *
 */
public class Location extends ExistentialRecord implements ExistentialRuleform {

    private static final long serialVersionUID = 1L;

    @Override
    public Location getRuleform() {
        return this;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.domain.ExistentialRuleform#isAny()
     */
    @Override
    public boolean isAny() {
        return WellKnownLocation.ANY.id()
                                    .equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.domain.ExistentialRuleform#isCopy()
     */
    @Override
    public boolean isCopy() {
        return WellKnownLocation.COPY.id()
                                     .equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.domain.ExistentialRuleform#isNotApplicable()
     */
    @Override
    public boolean isNotApplicable() {
        return WellKnownLocation.NOT_APPLICABLE.id()
                                               .equals(getId());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.domain.ExistentialRuleform#isSame()
     */
    @Override
    public boolean isSame() {
        return WellKnownLocation.SAME.id()
                                     .equals(getId());
    }

}
