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

import java.util.List;

import com.chiralbehaviors.CoRE.existential.domain.Agency;
import com.chiralbehaviors.CoRE.existential.domain.AgencyAttribute;
import com.chiralbehaviors.CoRE.existential.domain.AgencyAttributeAuthorization;
import com.chiralbehaviors.CoRE.existential.domain.AgencyLocationAuthorization;
import com.chiralbehaviors.CoRE.existential.domain.AgencyNetwork;
import com.chiralbehaviors.CoRE.existential.domain.AgencyProductAuthorization;

/**
 * @author hhildebrand
 *
 */
public interface AgencyModel extends
        NetworkedModel<Agency, AgencyNetwork, AgencyAttributeAuthorization, AgencyAttribute> {
    List<AgencyLocationAuthorization> getAgencyLocationAuths(Aspect<Agency> aspect,
                                                             boolean includeGrouping);

    List<AgencyProductAuthorization> getAgencyProductAuths(Aspect<Agency> aspect,
                                                           boolean includeGrouping);
}
