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
package com.chiralbehaviors.CoRE.product;

import static com.chiralbehaviors.CoRE.ExistentialRuleform.DEDUCE_NEW_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GENERATE_NETWORK_INVERSES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GET_CHILDREN_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GET_NETWORKS_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INFERENCE_STEP_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INSERT_NEW_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.USED_RELATIONSHIPS_SUFFIX;
import static com.chiralbehaviors.CoRE.product.ProductNetwork.GET_CHILDREN;
import static com.chiralbehaviors.CoRE.product.ProductNetwork.GET_NETWORKS;
import static com.chiralbehaviors.CoRE.product.ProductNetwork.GET_USED_RELATIONSHIPS;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attributable;
import com.chiralbehaviors.CoRE.network.NetworkRuleform;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The network relationships of products.
 *
 * @author hhildebrand
 *
 */
@NamedQueries({
               @NamedQuery(name = GET_USED_RELATIONSHIPS, query = "select distinct n.relationship from ProductNetwork n"),
               @NamedQuery(name = GET_CHILDREN, query = "SELECT n.child FROM ProductNetwork n "
                                                        + "WHERE n.parent = :parent "
                                                        + "AND n.relationship = :relationship"),
               @NamedQuery(name = GET_NETWORKS, query = "SELECT n FROM ProductNetwork n "
                                                        + "WHERE n.parent = :parent "
                                                        + "AND n.relationship = :relationship "
                                                        + "AND n.child = :child") })
@Entity
@Table(name = "product_network", schema = "ruleform")
public class ProductNetwork extends NetworkRuleform<Product> implements
        Attributable<ProductNetworkAttribute> {
    public static List<Relationship> getUsedRelationships(EntityManager em) {
        return em.createNamedQuery(GET_USED_RELATIONSHIPS, Relationship.class).getResultList();
    }

    public static final String           DEDUCE_NEW_NETWORK_RULES      = "productNetwork"
                                                                         + DEDUCE_NEW_NETWORK_RULES_SUFFIX;
    public static final String           GENERATE_NETWORK_INVERSES     = "productNetwork"
                                                                         + GENERATE_NETWORK_INVERSES_SUFFIX;
    public static final String           GET_CHILDREN                  = "productNetwork"
                                                                         + GET_CHILDREN_SUFFIX;
    public static final String           GET_NETWORKS                  = "productNetwork"
                                                                         + GET_NETWORKS_SUFFIX;
    public static final String           GET_USED_RELATIONSHIPS        = "productNetwork"
                                                                         + USED_RELATIONSHIPS_SUFFIX;
    public static final String           INFERENCE_STEP                = "productNetwork"
                                                                         + INFERENCE_STEP_SUFFIX;
    public static final String           INFERENCE_STEP_FROM_LAST_PASS = "productNetwork"
                                                                         + INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
    public static final String           INSERT_NEW_NETWORK_RULES      = "productNetwork"
                                                                         + INSERT_NEW_NETWORK_RULES_SUFFIX;

    private static final long            serialVersionUID              = 1L;

    // bi-directional many-to-one association to ProductNetworkAttribute
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "productNetwork")
    @JsonIgnore
    private Set<ProductNetworkAttribute> attributes;

    // bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "child")
    private Product                      child;

    //bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "parent")
    private Product                      parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(insertable = false, name = "premise1")
    private ProductNetwork               premise1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(insertable = false, name = "premise2")
    private ProductNetwork               premise2;

    public ProductNetwork() {
    }

    /**
     * @param updatedBy
     */
    public ProductNetwork(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public ProductNetwork(Product parent, Relationship relationship,
                          Product child, Agency updatedBy) {
        super(relationship, updatedBy);
        setRelationship(relationship);
        this.parent = parent;
        this.child = child;
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public ProductNetwork(Relationship relationship, Agency updatedBy) {
        super(relationship, updatedBy);
    }

    /**
     * @param id
     */
    public ProductNetwork(UUID id) {
        super(id);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public ProductNetwork(UUID id, Product parent, Relationship relationship,
                          Product child) {
        super(id);
        this.parent = parent;
        this.child = child;
        setRelationship(relationship);
    }

    @Override
    public Set<ProductNetworkAttribute> getAttributes() {
        return attributes;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.attribute.Attributable#getAttributeType()
     */
    @Override
    public Class<ProductNetworkAttribute> getAttributeType() {
        return ProductNetworkAttribute.class;
    }

    @Override
    public Product getChild() {
        return child;
    }

    @Override
    public Product getParent() {
        return parent;
    }

    /**
     * @return the premise1
     */
    @Override
    public ProductNetwork getPremise1() {
        return premise1;
    }

    /**
     * @return the premise2
     */
    @Override
    public ProductNetwork getPremise2() {
        return premise2;
    }

    @Override
    public void setAttributes(Set<ProductNetworkAttribute> productNetworkAttributes) {
        attributes = productNetworkAttributes;
    }

    @Override
    public void setChild(Product child) {
        this.child = child;
    }

    @Override
    public void setParent(Product parent) {
        this.parent = parent;
    }

    /**
     * @param premise1
     *            the premise1 to set
     */
    @Override
    public void setPremise1(NetworkRuleform<Product> premise1) {
        this.premise1 = (ProductNetwork) premise1;
    }

    /**
     * @param premise2
     *            the premise2 to set
     */
    @Override
    public void setPremise2(NetworkRuleform<Product> premise2) {
        this.premise2 = (ProductNetwork) premise2;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
    =======
    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.Ruleform#traverseForeignKeys(javax.persistence
     * .EntityManager, java.util.Map)
    >>>>>>> refs/heads/master
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (child != null) {
            child = (Product) child.manageEntity(em, knownObjects);
        }
        if (parent != null) {
            parent = (Product) parent.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}
