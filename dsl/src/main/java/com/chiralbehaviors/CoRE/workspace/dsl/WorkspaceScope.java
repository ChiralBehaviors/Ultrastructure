/**
 * (C) Copyright 2015 Chiral Behaviors, LLC. All Rights Reserved
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
