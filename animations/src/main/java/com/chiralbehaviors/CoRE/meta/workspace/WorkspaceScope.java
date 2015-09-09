/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.meta.workspace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.annotations.Key;
import com.chiralbehaviors.CoRE.meta.Model;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceScope {
    private final Map<String, WorkspaceAccessor> imports       = new HashMap<>();
    private final WorkspaceAccessor              workspace;
    private List<WorkspaceAccessor>              sortedImports = new ArrayList<>();

    public WorkspaceScope(Map<String, WorkspaceAccessor> imports,
                          WorkspaceAccessor workspace) {
        if (imports != null)
            this.imports.putAll(imports);
        this.workspace = workspace;
    }

    public WorkspaceScope(WorkspaceAccessor workspace) {
        this.workspace = workspace;
    }

    public WorkspaceAccessor getWorkspace() {
        return workspace;
    }

    /**
     * Lookup the key in the named scope of the receiver
     * 
     * @param key
     * @return the value associated with the key in the named scope, or null
     */
    public Ruleform lookup(Key key) {
        String namespace = key.namespace();
        String name = key.name();
        return lookup(namespace, name);
    }

    /**
     * Lookup the key in the hierarchical scope of the receiver, searching first
     * in the receiver, then through the ordered list of imported scopes
     * 
     * @param key
     *            the simple name of the ruleform
     * @return the value associated with the key in the reciever scope, or from
     *         an imported scope, or null if this simple name is not defined
     */
    public Ruleform lookup(String key) {
        Ruleform ruleform = workspace.get(key);
        if (ruleform != null) {
            return ruleform;
        }
        for (WorkspaceAccessor accessor : sortedImports) {
            ruleform = accessor.getScope()
                               .lookup(key);
            if (ruleform != null) {
                return ruleform;
            }
        }
        return null;
    }

    /**
     * Lookup the key in the named scope of the receiver
     * 
     * @param namespace
     * @param name
     * @return the value associated with the key in the named scope, or null
     */
    @SuppressWarnings("unchecked")
    public <T extends Ruleform> T lookup(String namespace, String name) {
        // null and empty string is alias for null scoped lookup in the workspace
        if (namespace == null || namespace.length() == 0) {
            return (T) lookup(name);
        }
        WorkspaceAccessor workspace = imports.get(namespace);
        if (workspace == null) {
            throw new IllegalArgumentException(String.format("Namespace %s does not exist",
                                                             namespace));
        }
        T member = workspace.get(name);
        if (member == null) {
            throw new IllegalArgumentException(String.format("Member %s::%s does not exist",
                                                             namespace, name));
        }
        return member;
    }

    public WorkspaceAccessor remove(String key) {
        return imports.remove(key);
    }

    public void remove(WorkspaceAccessor scope) {
        String key = null;
        for (Map.Entry<String, WorkspaceAccessor> entry : imports.entrySet()) {
            if (entry.getValue()
                     .equals(scope)) {
                key = entry.getKey();
                break;
            }
        }
        if (key == null) {
            return;
        }
        imports.remove(key);
    }

    @Override
    public String toString() {
        return String.format("WorkspaceScope[%s]",
                             workspace.getDefiningProduct()
                                      .getName());
    }

    /**
     * @param workspace
     */
    protected void add(String name, WorkspaceAccessor workspace) {
        imports.put(name, workspace);
        sortedImports.add(workspace);
    }

    protected Ruleform localLookup(String key, Model model) {
        return workspace.get(key);
    }
}
