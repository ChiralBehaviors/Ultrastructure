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

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Ruleform> T get(String key) {
        return (T) cache.get(key);
    }

}
