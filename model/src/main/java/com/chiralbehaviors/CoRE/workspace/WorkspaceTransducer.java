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

package com.chiralbehaviors.CoRE.workspace;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.Record;

import com.chiralbehaviors.CoRE.Ruleform;

/**
 * A state trasnsducer that gathers state in a workspace
 * 
 * @author hhildebrand
 *
 */
public class WorkspaceTransducer extends StateTransducer {
    private final UUID definingProduct;

    public WorkspaceTransducer(UUID definingProduct) {
        if (definingProduct == null) {
            throw new IllegalArgumentException("Defining product cannot be null");
        }
        this.definingProduct = definingProduct;
    }

    @Override
    public UUID slice(Record record, UUID id, UUID workspace, DSLContext create,
                      Collection<UUID> traversed,
                      Map<UUID, UUID> replacements) {
        if (!definingProduct.equals(definingProduct(create, workspace))) {
            UUID exit = Ruleform.GENERATOR.generate();
            replacements.put(id, exit);
            return exit;
        }
        record(record);
        return null;
    }

    @Override
    protected boolean sameWorkspace(UUID workspace, DSLContext create) {
        return definingProduct.equals(definingProduct(create, workspace));
    }
}
