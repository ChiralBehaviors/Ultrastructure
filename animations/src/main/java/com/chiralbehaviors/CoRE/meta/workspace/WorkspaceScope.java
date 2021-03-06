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
import java.util.UUID;

import com.chiralbehaviors.CoRE.jooq.enums.ReferenceType;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.phantasm.java.annotations.Key;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceScope {
    private final Map<String, WorkspaceAccessor> imports       = new HashMap<>();
    private final WorkspaceAccessor              workspace;
    private List<WorkspaceAccessor>              sortedImports = new ArrayList<>();

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
     * 
     * @return the value associated with the key in the named scope, or null
     */
    @SuppressWarnings("unchecked")
    public <T> T lookup(Key key) {
        String namespace = key.namespace();
        String name = key.name();
        return (T) lookup(namespace, key.type(), name);
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
    public <T> T lookup(ReferenceType type, String key) {
        T ruleform = workspace.get(type, key);
        if (ruleform != null) {
            return ruleform;
        }
        for (WorkspaceAccessor accessor : sortedImports) {
            ruleform = accessor.getScope()
                               .lookup(type, key);
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
     * @param type
     * @param name
     * @return the value of the indicate type associated with the key in the
     *         named scope, or null
     */
    @SuppressWarnings("unchecked")
    public <T> T lookup(String namespace, ReferenceType type, String name) {
        // null and empty string is alias for null scoped lookup in the workspace
        if (namespace == null || namespace.length() == 0) {
            return (T) lookup(type, name);
        }
        WorkspaceAccessor workspace = imports.get(namespace);
        if (workspace == null) {
            throw new IllegalArgumentException(String.format("Namespace %s does not exist",
                                                             namespace));
        }
        T member = workspace.get(type, name);
        if (member == null) {
            throw new IllegalArgumentException(String.format("Member %s::%s of type: %s does not exist",
                                                             namespace, name,
                                                             type));
        }
        return member;
    }

    /**
     * @param name
     * @return
     */
    public UUID lookupId(ReferenceType type, String name) {
        UUID ruleform = workspace.getId(type, name);
        if (ruleform != null) {
            return ruleform;
        }
        for (WorkspaceAccessor accessor : sortedImports) {
            ruleform = accessor.getScope()
                               .lookupId(type, name);
            if (ruleform != null) {
                return ruleform;
            }
        }
        return null;
    }

    /**
     * Answer the reference id
     *
     */
    public UUID lookupId(String namespace, ReferenceType type, String name) {
        // null and empty string is alias for null scoped lookup in the workspace
        if (namespace == null || namespace.length() == 0) {
            return lookupId(type, name);
        }
        WorkspaceAccessor workspace = imports.get(namespace);
        if (workspace == null) {
            throw new IllegalArgumentException(String.format("Namespace %s does not exist",
                                                             namespace));
        }
        UUID member = workspace.getId(type, name);
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

    protected <T> T localLookup(String key, ReferenceType type, Model model) {
        return workspace.get(type, key);
    }
}
