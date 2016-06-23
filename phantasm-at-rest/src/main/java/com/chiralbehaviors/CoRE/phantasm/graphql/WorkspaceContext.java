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

package com.chiralbehaviors.CoRE.phantasm.graphql;

import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.MetaMutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.MetaQueries;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.Mutations;
import com.chiralbehaviors.CoRE.phantasm.graphql.WorkspaceSchema.Queries;
import com.chiralbehaviors.CoRE.phantasm.graphql.mutations.CoreUserAdmin;
import com.chiralbehaviors.CoRE.phantasm.model.PhantasmCRUD;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceContext extends PhantasmCRUD implements Queries,
        Mutations, MetaQueries, MetaMutations, CoreUserAdmin {

    private final Product workspace;

    public WorkspaceContext(Model model, Product workspace) {
        super(model);
        this.workspace = workspace;
    }

    public Product getWorkspace() {
        return workspace;
    }
}
