/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.workspace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.Ruleform;

/**
 * @author hhildebrand
 *
 */
public class SnapshotBackedWorkspace implements Workspace {

    private final Map<String, Ruleform> cache = new HashMap<>();
    private final WorkspaceSnapshot     snapshot;

    public SnapshotBackedWorkspace(WorkspaceSnapshot snapshot) {
        this.snapshot = snapshot;
        for (WorkspaceAuthorization auth : snapshot.getAuths()) {
            if (auth.getKey() != null) {
                cache.put(auth.getKey(), auth.getEntity());
            }
        }
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.Workspace#get(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Ruleform> T get(String key) {
        return (T) cache.get(key);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.Workspace#getAccesor(java.lang.Class)
     */
    @Override
    public <T> T getAccesor(Class<T> accessorInterface) {
        return WorkspaceAccessHandler.getAccesor(accessorInterface, this);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.Workspace#getCollection(java.lang.Class)
     */
    @Override
    public <T extends Ruleform> List<T> getCollection(Class<T> ruleformClass) {
        throw new UnsupportedOperationException("currently unsupported");
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.Workspace#getSnapshot()
     */
    @Override
    public WorkspaceSnapshot getSnapshot() {
        return snapshot;
    }
}
