/**
 * (C) Copyright 2015 Chiral Behaviors, LLC. All Rights Reserved
 *
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.workspace.dsl;

import java.util.HashMap;
import java.util.Map;

import javax.management.openmbean.InvalidKeyException;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.workspace.DatabaseBackedWorkspace;
import com.chiralbehaviors.CoRE.workspace.Workspace;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceScope {
    private final Map<String, Workspace> workspaces = new HashMap<>();

    public WorkspaceScope(Model model) {
        workspaces.put("kernel",
                       new DatabaseBackedWorkspace(
                                                   model.getKernel().getKernelWorkspace(),
                                                   model.getEntityManager()));
    }

    public <T extends Ruleform> T resolve(String alias, String key) {
        Workspace wsp = workspaces.get(alias);
        if (wsp == null) {
            throw new IllegalArgumentException(
                                               String.format("Cannot find the workspace aliased: %s",
                                                             alias));
        }
        T result = wsp.get(key);
        if (result == null) {
            throw new InvalidKeyException(
                                          String.format("Cannot find key: %s in workspace: %s",
                                                        key, alias));
        }
        return result;
    }

}
