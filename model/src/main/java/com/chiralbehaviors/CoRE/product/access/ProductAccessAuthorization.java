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
package com.chiralbehaviors.CoRE.product.access;

import static com.chiralbehaviors.CoRE.product.access.ProductAccessAuthorization.FIND_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.product.access.ProductAccessAuthorization.GET_ALL_AUTHORIZATIONS_FOR_PARENT_AND_RELATIONSHIP;

import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.authorization.AccessAuthorization;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author hparry
 *
 */
@NamedQueries({

               @NamedQuery(name = GET_ALL_AUTHORIZATIONS_FOR_PARENT_AND_RELATIONSHIP, query = "SELECT auth "
                                                                                              + "FROM ProductAccessAuthorization auth "
                                                                                              + "WHERE auth.relationship = :r "
                                                                                              + "AND auth.parent = :rf "),
               @NamedQuery(name = FIND_AUTHORIZATION, query = "SELECT auth "
                                                              + "FROM ProductAccessAuthorization auth "
                                                              + "WHERE auth.parent = :parent "
                                                              + "AND auth.relationship = :relationship ") })
@Entity
public abstract class ProductAccessAuthorization<Child extends ExistentialRuleform<Child, ?>>
        extends AccessAuthorization<Product, Child> {
    public static final String  PRODUCT_ACCESS_AUTHORIZATION_PREFIX                = "productAccessAuthorization";
    public static final String  FIND_AUTHORIZATION                                 = PRODUCT_ACCESS_AUTHORIZATION_PREFIX
                                                                                     + FIND_AUTHORIZATION_SUFFIX;

    public static final String  GET_ALL_AUTHORIZATIONS_FOR_PARENT_AND_RELATIONSHIP = PRODUCT_ACCESS_AUTHORIZATION_PREFIX
                                                                                     + GET_ALL_AUTHORIZATIONS_FOR_PARENT_AND_RELATIONSHIP_SUFFIX;

    private static final long   serialVersionUID                                   = 1L;

    // bi-directional many-to-one association to ProductNetwork
    @OneToMany(mappedBy = "parent")
    @JsonIgnore
    private Set<ProductNetwork> networkByParent;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "product1")
    private Product             parent;

    /**
     * @return the networkByParent
     */
    public Set<ProductNetwork> getNetworkByParent() {
        return networkByParent;
    }

    /**
     * @return the parent
     */
    @Override
    public Product getParent() {
        return parent;
    }

    /**
     * @param networkByParent
     *            the networkByParent to set
     */
    public void setNetworkByParent(Set<ProductNetwork> networkByParent) {
        this.networkByParent = networkByParent;
    }

    /**
     * @param parent
     *            the parent to set
     */
    public void setParent(Product parent) {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.Ruleform#traverseForeignKeys(javax.persistence.
     * EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (parent != null) {
            parent = (Product) parent.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);
    }

}
