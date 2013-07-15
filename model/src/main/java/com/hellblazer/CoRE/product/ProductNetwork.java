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

import static com.hellblazer.CoRE.network.Networked.DEDUCE_NEW_NETWORK_RULES_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.GATHER_EXISTING_NETWORK_RULES_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.INFERENCE_STEP_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.INSERT_NEW_NETWORK_RULES_SUFFIX;
import static com.hellblazer.CoRE.network.Networked.USED_RELATIONSHIPS_SUFFIX;
import static com.hellblazer.CoRE.product.Product.IMMEDIATE_CHILDREN_NETWORK_RULES;
import static com.hellblazer.CoRE.product.Product.ALL_CHILDREN_NETWORK_RULES;
import static com.hellblazer.CoRE.product.ProductNetwork.DEDUCE_NEW_NETWORK_RULES;
import static com.hellblazer.CoRE.product.ProductNetwork.GATHER_EXISTING_NETWORK_RULES;
import static com.hellblazer.CoRE.product.ProductNetwork.GET_USED_RELATIONSHIPS;
import static com.hellblazer.CoRE.product.ProductNetwork.INFERENCE_STEP;
import static com.hellblazer.CoRE.product.ProductNetwork.INSERT_NEW_NETWORK_RULES;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.attribute.Attributable;
import com.hellblazer.CoRE.network.NetworkRuleform;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

/**
 * The network relationships of products.
 * 
 * @author hhildebrand
 * 
 */
@NamedNativeQueries({
                     @NamedNativeQuery(name = INFERENCE_STEP, query = "INSERT INTO working_memory(parent, relationship, child, premise1, premise2) "
                                                                      + "     SELECT "
                                                                      + "         premise1.parent, "
                                                                      + "         deduction.inference, "
                                                                      + "         premise2.child, "
                                                                      + "         premise1.id, "
                                                                      + "         premise2.id "
                                                                      + "     FROM  (SELECT n.id, n.parent, n.relationship, n.child "
                                                                      + "              FROM ruleform.product_network AS n) as premise1 "
                                                                      + "     JOIN  (SELECT n.id, n.parent, n.relationship, n.child "
                                                                      + "            FROM ruleform.product_network AS n "
                                                                      + "            WHERE n.inferred = FALSE) as premise2  "
                                                                      + "         ON premise2.parent = premise1.child "
                                                                      + "         AND premise2.child <> premise1.parent "
                                                                      + "     JOIN ruleform.network_inference AS deduction "
                                                                      + "         ON premise1.relationship = deduction.premise1 "
                                                                      + "         AND premise2.relationship = deduction.premise2 "),
                     @NamedNativeQuery(name = GATHER_EXISTING_NETWORK_RULES, query = "INSERT INTO current_pass_existing_rules "
                                                                                     + "SELECT exist.id, wm.* "
                                                                                     + "FROM working_memory AS wm "
                                                                                     + "JOIN ruleform.product_network AS exist "
                                                                                     + "    ON wm.parent = exist.parent "
                                                                                     + "    AND wm.relationship = exist.relationship "
                                                                                     + "    AND wm.child = exist.child"),
                     @NamedNativeQuery(name = DEDUCE_NEW_NETWORK_RULES, query = "INSERT INTO current_pass_rules "
                                                                                + "    SELECT nextval('ruleform.product_network_id_seq'), wm.* "
                                                                                + "    FROM (SELECT parent, relationship, child"
                                                                                + "          FROM working_memory GROUP BY parent, relationship, child) AS wm "
                                                                                + "    LEFT OUTER JOIN ruleform.product_network AS exist "
                                                                                + "         ON wm.parent = exist.parent "
                                                                                + "         AND wm.relationship = exist.relationship "
                                                                                + "         AND wm.child = exist.child "
                                                                                + "     WHERE exist.parent IS NULL "
                                                                                + "     AND exist.relationship IS NULL "
                                                                                + "     AND exist.child IS NULL"),
                     @NamedNativeQuery(name = INSERT_NEW_NETWORK_RULES, query = "WITH upsert AS "
                                                                                + "       (UPDATE ruleform.product_network n  "
                                                                                + "        SET id = n.id, parent = n.parent, child= n.child "
                                                                                + "        FROM current_pass_rules cpr "
                                                                                + "        WHERE n.parent = cpr.parent "
                                                                                + "          AND n.relationship = cpr.relationship "
                                                                                + "          AND n.child = cpr.child "
                                                                                + "        RETURNING n.*) "
                                                                                + "INSERT INTO ruleform.product_network(id, parent, relationship, child, inferred, updated_by) "
                                                                                + "        SELECT cpr.id, cpr.parent, cpr.relationship, cpr.child, TRUE, ?1 "
                                                                                + "    FROM current_pass_rules cpr "
                                                                                + "    LEFT OUTER JOIN upsert AS exist "
                                                                                + "        ON cpr.parent = exist.parent "
                                                                                + "        AND cpr.relationship = exist.relationship "
                                                                                + "        AND cpr.child = exist.child "
                                                                                + "     WHERE exist.parent IS NULL "
                                                                                + "     AND exist.relationship IS NULL "
                                                                                + "     AND exist.child IS NULL") })
@javax.persistence.Entity
@Table(name = "product_network", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "product_network_id_seq", sequenceName = "product_network_id_seq", allocationSize = 1)
@NamedQueries({
               @NamedQuery(name = IMMEDIATE_CHILDREN_NETWORK_RULES, query = "select n from ProductNetwork n "
                                                                            + "where n.parent = :product "
                                                                            + "and n.inferred = FALSE "
                                                                            + "and n.relationship.preferred = FALSE "
                                                                            + "ORDER by n.parent.name, n.relationship.name, n.child.name"),
               @NamedQuery(name = ALL_CHILDREN_NETWORK_RULES, query = "select n from ProductNetwork n "
                                                                            + "where n.parent = :product "
                                                                            + "and n.relationship = :relationship "
                                                                            + "ORDER by n.parent.name, n.relationship.name, n.child.name"),
               @NamedQuery(name = GET_USED_RELATIONSHIPS, query = "select distinct n.relationship from ProductNetwork n") })
public class ProductNetwork extends NetworkRuleform<Product> implements
        Attributable<ProductNetworkAttribute> {
    private static final long  serialVersionUID              = 1L;
    public static final String GET_USED_RELATIONSHIPS        = "productNetwork"
                                                               + USED_RELATIONSHIPS_SUFFIX;
    public static final String INFERENCE_STEP                = "productNetwork"
                                                               + INFERENCE_STEP_SUFFIX;
    public static final String GATHER_EXISTING_NETWORK_RULES = "productNetwork"
                                                               + GATHER_EXISTING_NETWORK_RULES_SUFFIX;
    public static final String DEDUCE_NEW_NETWORK_RULES      = "productNetwork"
                                                               + DEDUCE_NEW_NETWORK_RULES_SUFFIX;
    public static final String INSERT_NEW_NETWORK_RULES      = "productNetwork"
                                                               + INSERT_NEW_NETWORK_RULES_SUFFIX;

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
                          Product child, Resource updatedBy) {
        super(relationship, updatedBy);
        setRelationship(relationship);
        this.parent = parent;
        this.child = child;
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

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
	 */
	@Override
	public void traverseForeignKeys(EntityManager em,
			Map<Ruleform, Ruleform> knownObjects) {
		if (child != null) child = (Product) child.manageEntity(em, knownObjects);
		if (parent != null) parent = (Product) parent.manageEntity(em, knownObjects);
		super.traverseForeignKeys(em, knownObjects);
		
	}
}