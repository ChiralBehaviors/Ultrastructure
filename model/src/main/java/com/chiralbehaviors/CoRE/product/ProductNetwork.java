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
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GATHER_EXISTING_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GENERATE_NETWORK_INVERSES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.GET_CHILDREN_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INFERENCE_STEP_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.INSERT_NEW_NETWORK_RULES_SUFFIX;
import static com.chiralbehaviors.CoRE.ExistentialRuleform.USED_RELATIONSHIPS_SUFFIX;
import static com.chiralbehaviors.CoRE.product.ProductNetwork.GET_CHILDREN;
import static com.chiralbehaviors.CoRE.product.ProductNetwork.GET_USED_RELATIONSHIPS;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
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
                                                        + "AND n.relationship = :relationship") })
@Entity
@Table(name = "product_network", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "product_network_id_seq", sequenceName = "product_network_id_seq", allocationSize = 1)
public class ProductNetwork extends NetworkRuleform<Product> implements
        Attributable<ProductNetworkAttribute> {
    public static final String DEDUCE_NEW_NETWORK_RULES      = "productNetwork"
                                                               + DEDUCE_NEW_NETWORK_RULES_SUFFIX;
    public static final String GATHER_EXISTING_NETWORK_RULES = "productNetwork"
                                                               + GATHER_EXISTING_NETWORK_RULES_SUFFIX;
    public static final String GENERATE_NETWORK_INVERSES     = "productNetwork"
                                                               + GENERATE_NETWORK_INVERSES_SUFFIX;
    public static final String GET_CHILDREN                  = "productNetwork"
                                                               + GET_CHILDREN_SUFFIX;
    public static final String GET_USED_RELATIONSHIPS        = "productNetwork"
                                                               + USED_RELATIONSHIPS_SUFFIX;
    public static final String INFERENCE_STEP                = "productNetwork"
                                                               + INFERENCE_STEP_SUFFIX;
    public static final String INFERENCE_STEP_FROM_LAST_PASS = "productNetwork"
                                                               + INFERENCE_STEP_FROM_LAST_PASS_SUFFIX;
    public static final String INSERT_NEW_NETWORK_RULES      = "productNetwork"
                                                               + INSERT_NEW_NETWORK_RULES_SUFFIX;
    private static final long  serialVersionUID              = 1L;

    public static List<Relationship> getUsedRelationships(EntityManager em) {
        return em.createNamedQuery(GET_USED_RELATIONSHIPS, Relationship.class).getResultList();
    }

    // bi-directional many-to-one association to ProductNetworkAttribute
    @OneToMany(mappedBy = "productNetwork", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ProductNetworkAttribute> attributes;

    // bi-directional many-to-one association to Product
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "child")
    private Product                      child;

    @Id
    @GeneratedValue(generator = "product_network_id_seq", strategy = GenerationType.SEQUENCE)
    private Long                         id;

    // bi-directional many-to-one association to Product
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent")
    private Product                      parent;

    public ProductNetwork() {
    }

    /**
     * @param updatedBy
     */
    public ProductNetwork(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param id
     */
    public ProductNetwork(Long id) {
        super(id);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public ProductNetwork(Long id, Product parent, Relationship relationship,
                          Product child) {
        super(id);
        this.parent = parent;
        this.child = child;
        setRelationship(relationship);
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
    public Long getId() {
        return id;
    }

    @Override
    public Product getParent() {
        return parent;
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
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setParent(Product parent) {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.Ruleform#traverseForeignKeys(javax.persistence
     * .EntityManager, java.util.Map)
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
