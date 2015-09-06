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
import java.util.UUID;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;

/**
 * @author hhildebrand
 *
 */
public interface WorkspaceModel {

    /**
     * Create the workspace and return the Workspace scope defined by that
     * workspace
     * 
     * @param definingProduct
     * @param updatedBy
     * @return
     */
    WorkspaceScope createWorkspace(Product definingProduct, Agency updatedBy);

    void flush();

    WorkspaceAuthorization get(Product definingProduct, String key);

    List<WorkspaceAuthorization> getByType(Product definingProduct,
                                           String type);

    WorkspaceScope getScoped(Product definingProduct);

    WorkspaceScope getScoped(UUID definingProduct);

    List<WorkspaceAuthorization> getWorkspace(Product definingProduct);
}
