/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hellblazer.CoRE.product;

import static com.hellblazer.CoRE.network.Networked.USED_RELATIONSHIPS_SUFFIX;
import static com.hellblazer.CoRE.product.Product.IMMEDIATE_CHILDREN_NETWORK_RULES;
import static com.hellblazer.CoRE.product.ProductNetwork.GET_USED_RELATIONSHIPS;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hellblazer.CoRE.attribute.Attributable;
import com.hellblazer.CoRE.network.NetworkRuleform;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The persistent class for the product_network database table.
 * 
 */
@javax.persistence.Entity
@Table(name = "product_network", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "product_network_id_seq", sequenceName = "product_network_id_seq", allocationSize = 1)
@NamedQueries({
               @NamedQuery(name = IMMEDIATE_CHILDREN_NETWORK_RULES, query = "select n from ProductNetwork n "
                                                                            + "where n.parent = :product "
                                                                            + "and n.distance = 1 "
                                                                            + "and n.relationship.preferred = FALSE "
                                                                            + "ORDER by n.parent.name, n.relationship.name, n.child.name"),
               @NamedQuery(name = GET_USED_RELATIONSHIPS, query = "select distinct n.relationship from ProductNetwork n") })
public class ProductNetwork extends NetworkRuleform<Product> implements
        Attributable<ProductNetworkAttribute> {
    private static final long  serialVersionUID       = 1L;
    public static final String GET_USED_RELATIONSHIPS = "productNetwork"
                                                        + USED_RELATIONSHIPS_SUFFIX;

    public static List<Relationship> getUsedRelationships(EntityManager em) {
        return em.createNamedQuery(GET_USED_RELATIONSHIPS, Relationship.class).getResultList();
    }

    //bi-directional many-to-one association to ProductNetworkAttribute
    @OneToMany(mappedBy = "productNetwork", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ProductNetworkAttribute> attributes;

    //bi-directional many-to-one association to Product
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "child")
    private Product                      child;

    @Id
    @GeneratedValue(generator = "product_network_id_seq", strategy = GenerationType.SEQUENCE)
    private Long                         id;

    //bi-directional many-to-one association to Product
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent")
    private Product                      parent;

    public ProductNetwork() {
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public ProductNetwork(Product parent, Relationship relationship,
                          Product child, Resource updatedBy) {
        super(relationship, updatedBy);
        this.parent = parent;
        this.child = child;
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
    public ProductNetwork(Relationship relationship, Resource updatedBy) {
        super(relationship, updatedBy);
    }

    /**
     * @param updatedBy
     */
    public ProductNetwork(Resource updatedBy) {
        super(updatedBy);
    }

    @Override
    public Set<ProductNetworkAttribute> getAttributes() {
        return attributes;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.Attributable#getAttributeType()
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
}