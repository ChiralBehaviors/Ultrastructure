/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
package com.hellblazer.CoRE.meta.graph.query;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.authorization.AccessAuthorization;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.access.ProductAccessAuthorization;

/**
 * @author hparry
 * 
 */
public class AccessAuthorizationGraphQuery {

    private EntityManager                   em;
    private AccessAuthorization<?, ?>       authorization;
    private List<AccessAuthorization<?, ?>> auths;
    private ExistentialRuleform<?, ?>       parent;
    private Relationship                    relationship;

    public AccessAuthorizationGraphQuery(ExistentialRuleform<?, ?> parent,
                                         Relationship rel, EntityManager em) {
        this.parent = parent;
        relationship = rel;
        this.em = em;
        auths = new LinkedList<AccessAuthorization<?, ?>>();
        findAuthorizations();
    }

    /**
     * @return the authorization
     */
    public AccessAuthorization<?, ?> getAuthorization() {
        return authorization;
    }

    public List<AccessAuthorization<?, ?>> getResults() {
        return auths;
    }

    /**
     * @param authorization
     *            the authorization to set
     */
    public void setAuthorization(AccessAuthorization<?, ?> authorization) {
        this.authorization = authorization;
    }

    private void findAuthorizations() {
        findProductAuthorizations();
    }

    /**
	 * 
	 */
    @SuppressWarnings("unchecked")
    private void findProductAuthorizations() {
        Query q = em.createNamedQuery(ProductAccessAuthorization.GET_ALL_AUTHORIZATIONS_FOR_PARENT_AND_RELATIONSHIP,
                                      ProductAccessAuthorization.class);
        q.setParameter("rf", parent);
        q.setParameter("r", relationship);
        auths.addAll(q.getResultList());

    }

}
