/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.meta.workspace;

import java.util.HashMap;
import java.util.Map;

import com.chiralbehaviors.CoRE.Ruleform;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceScope {
    private final Map<String, Workspace> imports = new HashMap<>();
    private final Workspace              workspace;

    public WorkspaceScope(Map<String, Workspace> imports, Workspace workspace) {
        if (imports != null)
            this.imports.putAll(imports);
        this.workspace = workspace;
    }

    @Override
    public String toString() {
        return String.format("WorkspaceScope[%s]",
                             workspace.getDefiningProduct().getName());
    }

    public WorkspaceScope(Workspace workspace) {
        this.workspace = workspace;
    }

    /**
     * @param workspace
     */
    public void add(String name, Workspace workspace) {
        imports.put(name, workspace);
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    /**
     * Lookup the key in the hierarchical scope of the receiver, searching first
     * in the receiver, then through the ordered list of imported scopes
     * 
     * @param key
     * @return the value associated with the key in the reciever scope, or null
     */
    public Ruleform lookup(String key) {
        Ruleform ruleform = workspace.get(key);
        if (ruleform == null) {
            for (Workspace workspace : imports.values()) {
                ruleform = workspace.get(key);
                if (ruleform != null) {
                    return ruleform;
                }
            }
        }
        return ruleform;
    }

    /**
     * Lookup the key in the named scope. The first scope found matching the
     * name in a traversal of ordered parents is returned.
     * 
     * @param scope
     * @param key
     * @return the value associated with the key in the named scope, or null
     */
    public Ruleform lookup(String scope, String key) {
        if (scope == null) {
            return lookup(key);
        }
        Workspace workspace = imports.get(scope);
        if (workspace == null) {
            return null;
        }
        return workspace.get(key);
    }

    public Workspace remove(String key) {
        return imports.remove(key);
    }

    public void remove(Workspace scope) {
        String key = null;
        for (Map.Entry<String, Workspace> entry : imports.entrySet()) {
            if (entry.getValue().equals(scope)) {
                key = entry.getKey();
                break;
            }
        }
        if (key == null) {
            return;
        }
        imports.remove(key);
    }

    protected Ruleform localLookup(String key) {
        return workspace.get(key);
    }
}
