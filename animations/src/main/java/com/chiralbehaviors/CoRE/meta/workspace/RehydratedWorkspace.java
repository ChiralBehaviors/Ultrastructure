/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;

/**
 * @author hhildebrand
 *
 */
public class RehydratedWorkspace extends WorkspaceSnapshot implements Workspace {

    private final Map<String, Ruleform> cache = new HashMap<>();
    private final WorkspaceScope        scope = new WorkspaceScope(this);

    public void cache() {
        for (WorkspaceAuthorization auth : auths) {
            if (auth.getKey() != null) {
                cache.put(auth.getKey(), auth.getEntity());
            }
        }
    }

    /**
     * @param em
     */
    public void detach(EntityManager em) {
        for (WorkspaceAuthorization auth : auths) {
            em.detach(auth);
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
    public <T> T getAccessor(Class<T> accessorInterface) {
        return WorkspaceAccessHandler.getAccesor(accessorInterface,
                                                 this.getScope());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.Workspace#getCollection(java.lang.Class)
     */
    @Override
    public <T extends Ruleform> List<T> getCollection(Class<T> ruleformClass) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.Workspace#getImports()
     */
    @Override
    public Map<String, Product> getImports() {
        return Collections.emptyMap();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.workspace.Workspace#getScope()
     */
    @Override
    public WorkspaceScope getScope() {
        return scope;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.workspace.Workspace#getSnapshot()
     */
    @Override
    public WorkspaceSnapshot getSnapshot() {
        return this;
    }

    @Override
    public void replaceFrom(EntityManager em) {
        List<WorkspaceAuthorization> oldAuths = new ArrayList<WorkspaceAuthorization>(
                                                                                      auths);
        auths.clear();
        for (WorkspaceAuthorization auth : oldAuths) {
            auths.add(em.merge(auth));
        }
        cache();
    }
}
