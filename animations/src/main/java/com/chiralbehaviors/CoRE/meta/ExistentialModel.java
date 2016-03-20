/**
 * Copyright (c) 2016 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.meta;

import java.util.List;
import java.util.UUID;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.ExistentialRuleform;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;

/**
 * @author hhildebrand
 *
 */
public interface ExistentialModel<RuleForm extends ExistentialRuleform> {

    /**
     * Create a new instance of the RuleForm based on the provided prototype
     *
     * @param prototype
     *            - the model for the new instance
     * @return the new instance
     */
    RuleForm create(RuleForm prototype);

    /**
     * Create a new instance with the supplied aspects
     *
     * @param name
     *            The name of the new instance
     * @param description
     *            the description of the new instance
     * @return the new instance
     */
    RuleForm create(String name, String description);

    /**
     * Create a new instance with the supplied aspects
     *
     * @param name
     *            The name of the new instance
     * @param description
     *            the description of the new instance
     * @param updatedBy
     * @param aspects
     *            - the initial aspects of the instance
     * @return the new instance
     */
    RuleForm create(String name, String description, Aspect<RuleForm> aspect,
                    Agency updatedBy,
                    @SuppressWarnings("unchecked") Aspect<RuleForm>... aspects);

    /**
     * @param id
     * @return the ruleform with the specified id
     */
    RuleForm find(UUID id);

    /**
     *
     * @return all existential ruleforms that exist for this model
     */
    List<RuleForm> findAll();

    /**
     * @return the list of aspects representing all facets for the RuleForm
     */
    List<Aspect<RuleForm>> getAllFacets();

    /**
     * Answer the list of network authorizations that represent a facet defined
     * in the workspace.
     * 
     * @param workspace
     * @return the list of facet network authorizations in the workspace
     */
    List<ExistentialNetworkAuthorizationRecord> getFacets(Product workspace);

}
