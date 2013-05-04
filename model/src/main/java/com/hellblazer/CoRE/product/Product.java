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

import static com.hellblazer.CoRE.product.Product.FIND_ALL;
import static com.hellblazer.CoRE.product.Product.FIND_ATTRIBUTE_AUTHORIZATIONS;
import static com.hellblazer.CoRE.product.Product.FIND_BY_ID;
import static com.hellblazer.CoRE.product.Product.FIND_BY_NAME;
import static com.hellblazer.CoRE.product.Product.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS;
import static com.hellblazer.CoRE.product.Product.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE;
import static com.hellblazer.CoRE.product.Product.FIND_CLASSIFIED_ATTRIBUTE_VALUES;
import static com.hellblazer.CoRE.product.Product.FIND_FLAGGED;
import static com.hellblazer.CoRE.product.Product.FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS;
import static com.hellblazer.CoRE.product.Product.FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE;
import static com.hellblazer.CoRE.product.Product.FIND_GROUPED_ATTRIBUTE_VALUES;
import static com.hellblazer.CoRE.product.Product.NAME_SEARCH;
import static com.hellblazer.CoRE.product.Product.SUBSUMING_ENTITIES;
import static com.hellblazer.CoRE.product.Product.UPDATED_BY;
import static com.hellblazer.CoRE.product.Product.UPDATED_BY_NAME;
import static com.hellblazer.CoRE.product.ProductLocationNetwork.LOCATION_RULES;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.NameSearchResult;
import com.hellblazer.CoRE.attribute.Attributable;
import com.hellblazer.CoRE.coordinate.CoordinateBundle;
import com.hellblazer.CoRE.coordinate.CoordinateKind;
import com.hellblazer.CoRE.location.LocationCalculator;
import com.hellblazer.CoRE.network.Networked;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.resource.Resource;

/**
 * A Thing. A product, or an artifact.
 * 
 */
@NamedQueries({
               @NamedQuery(name = FIND_FLAGGED, query = "select e from Product e where e.research is not null"),
               @NamedQuery(name = FIND_BY_ID, query = "select e from Product e where e.id = :id"),
               @NamedQuery(name = FIND_BY_NAME, query = "select e from Product e where e.name = :name"),
               @NamedQuery(name = FIND_ALL, query = "select e from Product e"),
               @NamedQuery(name = UPDATED_BY, query = "select e from Product e where e.updatedBy= :resource"),
               @NamedQuery(name = UPDATED_BY_NAME, query = "select e from Product e where e.updatedBy.name= :name"),
               @NamedQuery(name = SUBSUMING_ENTITIES, query = "SELECT distinct(bn.child) "
                                                              + "FROM ProductNetwork AS bn "
                                                              + "WHERE bn.relationship = :relationship "
                                                              + "AND bn.parent = :product"),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_VALUES, query = "SELECT "
                                                                            + "  attrValue "
                                                                            + "FROM "
                                                                            + "       ProductAttribute attrValue, "
                                                                            + "       ProductAttributeAuthorization auth, "
                                                                            + "       ProductNetwork network "
                                                                            + "WHERE "
                                                                            + "        auth.authorizedAttribute = attrValue.attribute AND "
                                                                            + "        network.relationship = auth.classification AND "
                                                                            + "        network.child = auth.classifier AND"
                                                                            + "        attrValue.product = :ruleform AND "
                                                                            + "        auth.classification = :classification AND "
                                                                            + "        auth.classifier = :classifier "),
               @NamedQuery(name = FIND_GROUPED_ATTRIBUTE_VALUES, query = "select attr from ProductAttribute attr where "
                                                                         + "attr.product = :ruleform "
                                                                         + "AND attr.id IN ("
                                                                         + "select ea.authorizedAttribute from ProductAttributeAuthorization ea "
                                                                         + "WHERE ea.groupingResource = :resource)"),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS, query = "select ea from ProductAttributeAuthorization ea "
                                                                                    + "WHERE ea.classification = :classification "
                                                                                    + "AND ea.classifier = :classifier"),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE, query = "select ea from ProductAttributeAuthorization ea "
                                                                                                  + "WHERE ea.classification = :classification "
                                                                                                  + "AND ea.classifier = :classifier AND ea.authorizedAttribute = :attribute"),
               @NamedQuery(name = FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS, query = "select ea from ProductAttributeAuthorization ea "
                                                                                 + "WHERE ea.groupingResource = :groupingResource"),
               @NamedQuery(name = FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE, query = "select ea from ProductAttributeAuthorization ea "
                                                                                               + "WHERE ea.groupingResource = :groupingResource AND ea.authorizedAttribute = :attribute"),
               @NamedQuery(name = FIND_ATTRIBUTE_AUTHORIZATIONS, query = "select ea from ProductAttributeAuthorization ea "
                                                                         + "WHERE ea.classification = :classification "
                                                                         + "AND ea.classifier = :classifier "
                                                                         + "AND ea.groupingResource = :groupingResource") })
@NamedNativeQueries({
// ?1 = #queryString, ?2 = #numberOfMatches
@NamedNativeQuery(name = NAME_SEARCH, query = "SELECT id, name, description FROM ruleform.existential_name_search('product', ?1, ?2)", resultClass = NameSearchResult.class) })
@javax.persistence.Entity
@Table(name = "product", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "product_id_seq", sequenceName = "product_id_seq", allocationSize = 1)
public class Product extends ExistentialRuleform implements
        Networked<Product, ProductNetwork>, Attributable<ProductAttribute> {

    public static final String   FIND_GROUPED_ATTRIBUTE_VALUES                          = "product"
                                                                                          + FIND_GROUPED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String   CREATE_ENTITY_FROM_GROUP                               = "product.createEntityFromGroup";
    public static final String   FIND_ALL                                               = "product.findAll";
    public static final String   FIND_ATTRIBUTE_AUTHORIZATIONS                          = "product.findAttributeAuthorizations";
    public static final String   FIND_BY_ID                                             = "product.findById";
    public static final String   FIND_BY_NAME                                           = "product"
                                                                                          + FIND_BY_NAME_SUFFIX;
    public static final String   FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE = "product"
                                                                                          + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE_SUFFIX;
    public static final String   FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS               = "product"
                                                                                          + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
    public static final String   FIND_CLASSIFIED_ATTRIBUTE_VALUES                       = "product"
                                                                                          + FIND_CLASSIFIED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String   FIND_FLAGGED                                           = "product.findFlagged";
    public static final String   FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS                  = "product"
                                                                                          + FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_SUFFIX;
    public static final String   FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE    = "product"
                                                                                          + FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_FOR_ATTRIBUTE_SUFFIX;
    public static final String   IMMEDIATE_CHILDREN_NETWORK_RULES                       = "product.immediateChildrenNetworkRules";
    public static final String   NAME_SEARCH                                            = "product"
                                                                                          + NAME_SEARCH_SUFFIX;
    public static final String   SUBSUMING_ENTITIES                                     = "product.subsumingEntities";
    public static final String   UNIQUE_ENTITY_BY_ATTRIBUTE_VALUE                       = "product.uniqueEntityByAttributeValue";
    public static final String   UPDATED_BY                                             = "product"
                                                                                          + GET_UPDATED_BY_SUFFIX;
    public static final String   UPDATED_BY_NAME                                        = "product.getUpdatedByName";

    private static final long    serialVersionUID                                       = 1L;

    //bi-directional many-to-one association to ProductAttribute
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ProductAttribute> attributes;

    @Id
    @GeneratedValue(generator = "product_id_seq", strategy = GenerationType.SEQUENCE)
    private Long                 id;

    //bi-directional many-to-one association to ProductLocation
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ProductLocation>  locations;

    //bi-directional many-to-one association to ProductNetwork
    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ProductNetwork>   networkByChild;

    //bi-directional many-to-one association to ProductNetwork
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ProductNetwork>   networkByParent;

    public Product() {
    }

    /**
     * @param i
     * @param string
     */
    public Product(long id, String name) {
        setId(id);
        setName(name);
    }

    /**
     * @param id
     */
    public Product(Long id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public Product(Resource updatedBy) {
        super(updatedBy);
    }

    /**
     * @param name
     */
    public Product(String name) {
        super(name);
    }

    /**
     * @param name
     * @param updatedBy
     */
    public Product(String name, Resource updatedBy) {
        super(name, updatedBy);
    }

    /**
     * @param name
     * @param description
     */
    public Product(String name, String description) {
        super(name, description);
    }

    /**
     * @param name
     * @param description
     * @param updatedBy
     */
    public Product(String name, String description, Resource updatedBy) {
        super(name, description, updatedBy);
    }

    public void addAttribute(ProductAttribute attribute) {
        attribute.setProduct(this);
        attributes.add(attribute);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#addChildRelationship(com.hellblazer.CoRE.NetworkRuleform)
     */
    @Override
    public void addChildRelationship(ProductNetwork relationship) {
        relationship.setChild(this);
        networkByChild.add(relationship);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#addParentRelationship(com.hellblazer.CoRE.NetworkRuleform)
     */
    @Override
    public void addParentRelationship(ProductNetwork relationship) {
        relationship.setParent(this);
        networkByParent.add(relationship);
    }

    @Override
    public Product clone() {
        Product clone = (Product) super.clone();
        clone.networkByChild = null;
        clone.networkByChild = null;
        clone.attributes = null;
        clone.locations = null;
        return clone;
    }

    @Override
    public Set<ProductAttribute> getAttributes() {
        return attributes;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.attribute.Attributable#getAttributeType()
     */
    @Override
    public Class<ProductAttribute> getAttributeType() {
        return ProductAttribute.class;
    }

    /**
     * Retrieves the fully-specified coordinates of a given kind for the
     * receiver
     * 
     * @param kind
     *            the kind of locations you want
     * @return a List of Lists of CoordinateAttributes, since the receiver may
     *         have multiple locations of a specified kind (e.g. many gene
     *         copies in a genome)
     */
    public List<CoordinateBundle> getFullCoordinates(EntityManager em,
                                                     CoordinateKind kind) {
        LocationCalculator lc = new LocationCalculator();
        return lc.getFullCoordinates(em, this, kind);
    }

    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#getImmediateChildren()
     */
    @Override
    public List<ProductNetwork> getImmediateChildren(EntityManager em) {
        return em.createNamedQuery(IMMEDIATE_CHILDREN_NETWORK_RULES,
                                   ProductNetwork.class).setParameter("product",
                                                                     this).getResultList();
    }

    public List<ProductLocationNetwork> getLocationRules(EntityManager em,
                                                        CoordinateKind kind) {
        return em.createNamedQuery(LOCATION_RULES, ProductLocationNetwork.class).setParameter("product",
                                                                                             this).setParameter("kind",
                                                                                                                kind).getResultList();
    }

    public Set<ProductLocation> getLocations() {
        return locations;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#getNetworkByChild()
     */
    @Override
    public Set<ProductNetwork> getNetworkByChild() {
        return networkByChild;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#getNetworkByParent()
     */
    @Override
    public Set<ProductNetwork> getNetworkByParent() {
        return networkByParent;
    }

    @Override
    public void link(Relationship r, Product child, Resource updatedBy,
                     Resource inverseSoftware, EntityManager em) {
        ProductNetwork link = new ProductNetwork(this, r, child, updatedBy);
        em.persist(link);
        ProductNetwork inverse = new ProductNetwork(child, r.getInverse(), this,
                                                  inverseSoftware);
        em.persist(inverse);
    }

    @Override
    public void setAttributes(Set<ProductAttribute> productAttributes1) {
        attributes = productAttributes1;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setLocations(Set<ProductLocation> productLocations) {
        locations = productLocations;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#setNetworkByChild(java.util.Set)
     */
    @Override
    public void setNetworkByChild(Set<ProductNetwork> theNetworkByChild) {
        networkByChild = theNetworkByChild;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Networked#setNetworkByParent(java.util.Set)
     */
    @Override
    public void setNetworkByParent(Set<ProductNetwork> theNetworkByParent) {
        networkByParent = theNetworkByParent;
    }
}