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

package com.chiralbehaviors.CoRE.workspace;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 *
 */
public class CachedWorkspace extends DatabaseBackedWorkspace {

    public final Map<String, Ruleform> cache = new HashMap<>();

    /**
     * @param definingProduct
     * @param em
     */
    public CachedWorkspace(Product definingProduct, EntityManager em) {
        super(definingProduct, em);
        cache();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Ruleform> T get(String key) {
        return (T) cache.get(key);
    }

    private void cache() {
        cache.clear();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<WorkspaceAuthorization> query = cb.createQuery(WorkspaceAuthorization.class);
        Root<WorkspaceAuthorization> from = query.from(WorkspaceAuthorization.class);
        query.select(from).where(cb.and(cb.notEqual(from.get(WorkspaceAuthorization_.key),
                                                    null),
                                        cb.equal(from.get(WorkspaceAuthorization_.definingProduct),
                                                 definingProduct)));
        for (WorkspaceAuthorization auth : em.createQuery(query).getResultList()) {
            cache.put(auth.getKey(), auth.getRuleform());
        }
    }

}
