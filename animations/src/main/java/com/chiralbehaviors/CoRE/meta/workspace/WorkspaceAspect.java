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

package com.chiralbehaviors.CoRE.meta.workspace;

import java.util.List;

import com.chiralbehaviors.CoRE.annotations.Edge;
import com.chiralbehaviors.CoRE.annotations.Key;
import com.chiralbehaviors.CoRE.annotations.State;

/**
 * @author hhildebrand
 *
 */
@State(workspace = Workspace.KERNEL_URN)
public interface WorkspaceAspect {
    String getURI();

    void setURI(String uri);

    @Edge(@Key(name = "Imports"))
    List<WorkspaceAspect> getImports();

    @Edge(@Key(name = "Imports"))
    void setImports(List<WorkspaceAspect> imports);

    @Edge(@Key(name = "Imports"))
    void addImport(WorkspaceAspect workspace);

    @Edge(@Key(name = "Imports"))
    void removeImport(WorkspaceAspect workspace);
}
