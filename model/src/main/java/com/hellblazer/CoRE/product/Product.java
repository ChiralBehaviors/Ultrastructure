/** 
 * (C) Copyright 2014 Chiral Behaviors, LLC, All Rights Reserved
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
package com.hellblazer.CoRE.product;

import static com.hellblazer.CoRE.product.Product.FIND_ALL;
import static com.hellblazer.CoRE.product.Product.FIND_ATTRIBUTE_AUTHORIZATIONS;
import static com.hellblazer.CoRE.product.Product.FIND_BY_ID;
import static com.hellblazer.CoRE.product.Product.FIND_BY_NAME;
import static com.hellblazer.CoRE.product.Product.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS;
import static com.hellblazer.CoRE.product.Product.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE;
import static com.hellblazer.CoRE.product.Product.FIND_CLASSIFIED_ATTRIBUTE_VALUES;
import static com.hellblazer.CoRE.product.Product.FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS;
import static com.hellblazer.CoRE.product.Product.FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE;
import static com.hellblazer.CoRE.product.Product.FIND_GROUPED_ATTRIBUTE_VALUES;
import static com.hellblazer.CoRE.product.Product.GET_ALL_PARENT_RELATIONSHIPS;
import static com.hellblazer.CoRE.product.Product.GET_CHILDREN;
import static com.hellblazer.CoRE.product.Product.GET_CHILD_RULES_BY_RELATIONSHIP;
import static com.hellblazer.CoRE.product.Product.NAME_SEARCH;
import static com.hellblazer.CoRE.product.Product.SUBSUMING_ENTITIES;
import static com.hellblazer.CoRE.product.Product.UPDATED_BY;
import static com.hellblazer.CoRE.product.Product.UPDATED_BY_NAME;

import java.util.Collections;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
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
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.attribute.Attributable;
import com.hellblazer.CoRE.network.Relationship;

/**
 * A Thing. A product, or an artifact.
 * 
 * @author hhildebrand
 * 
 */
@NamedQueries({
               @NamedQuery(name = FIND_BY_ID, query = "select e from Product e where e.id = :id"),
               @NamedQuery(name = FIND_BY_NAME, query = "select e from Product e where e.name = :name"),
               @NamedQuery(name = FIND_ALL, query = "select e from Product e"),
               @NamedQuery(name = UPDATED_BY, query = "select e from Product e where e.updatedBy= :agency"),
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
                                                                         + "WHERE ea.groupingAgency = :agency)"),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS, query = "select ea from ProductAttributeAuthorization ea "
                                                                                    + "WHERE ea.classification = :classification "
                                                                                    + "AND ea.classifier = :classifier"),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE, query = "select ea from ProductAttributeAuthorization ea "
                                                                                                  + "WHERE ea.classification = :classification "
                                                                                                  + "AND ea.classifier = :classifier AND ea.authorizedAttribute = :attribute"),
               @NamedQuery(name = FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS, query = "select ea from ProductAttributeAuthorization ea "
                                                                                 + "WHERE ea.groupingAgency = :groupingAgency"),
               @NamedQuery(name = FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE, query = "select ea from ProductAttributeAuthorization ea "
                                                                                               + "WHERE ea.groupingAgency = :groupingAgency AND ea.authorizedAttribute = :attribute"),
               @NamedQuery(name = FIND_ATTRIBUTE_AUTHORIZATIONS, query = "select ea from ProductAttributeAuthorization ea "
                                                                         + "WHERE ea.classification = :classification "
                                                                         + "AND ea.classifier = :classifier "
                                                                         + "AND ea.groupingAgency = :groupingAgency"),
               @NamedQuery(name = GET_CHILDREN, query = "SELECT n.child "
                                                        + "FROM ProductNetwork n "
                                                        + "WHERE n.parent = :p "
                                                        + "AND n.relationship = :r"),
               @NamedQuery(name = GET_ALL_PARENT_RELATIONSHIPS, query = "SELECT n "
                                                                        + "FROM ProductNetwork n "
                                                                        + "WHERE n.child = :c"),
               @NamedQuery(name = GET_CHILD_RULES_BY_RELATIONSHIP, query = "SELECT n FROM ProductNetwork n "
                                                                           + "WHERE n.parent = :product "
                                                                           + "AND n.relationship IN :relationships "
                                                                           + "ORDER by n.parent.name, n.relationship.name, n.child.name") })
@NamedNativeQueries({
// ?1 = #queryString, ?2 = #numberOfMatches
@NamedNativeQuery(name = NAME_SEARCH, query = "SELECT id, name, description FROM ruleform.existential_name_search('product', ?1, ?2)", resultClass = NameSearchResult.class) })
@Entity
@Table(name = "product", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "product_id_seq", sequenceName = "product_id_seq", allocationSize = 1)
public class Product extends ExistentialRuleform<Product, ProductNetwork>
        implements Attributable<ProductAttribute> {

    public static final String    CREATE_ENTITY_FROM_GROUP                               = "product.createEntityFromGroup";
    public static final String    FIND_ALL                                               = "product" + Ruleform.FIND_ALL_SUFFIX;
    public static final String    FIND_ATTRIBUTE_AUTHORIZATIONS                          = "product.findAttributeAuthorizations";
    public static final String    FIND_BY_ID                                             = "product.findById";
    public static final String    FIND_BY_NAME                                           = "product"
                                                                                           + FIND_BY_NAME_SUFFIX;
    public static final String    FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS               = "product"
                                                                                           + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
    public static final String    FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE = "product"
                                                                                           + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE_SUFFIX;
    public static final String    FIND_CLASSIFIED_ATTRIBUTE_VALUES                       = "product"
                                                                                           + FIND_CLASSIFIED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String    FIND_FLAGGED                                           = "product.findFlagged";
    public static final String    FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS                  = "product"
                                                                                           + FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_SUFFIX;
    public static final String    FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS_FOR_ATTRIBUTE    = "product"
                                                                                           + FIND_GROUPED_ATTRIBUTE_ATHORIZATIONS_FOR_ATTRIBUTE_SUFFIX;
    public static final String    FIND_GROUPED_ATTRIBUTE_VALUES                          = "product"
                                                                                           + FIND_GROUPED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String    GET_ALL_PARENT_RELATIONSHIPS                           = "product"
                                                                                           + GET_ALL_PARENT_RELATIONSHIPS_SUFFIX;
    public static final String    GET_CHILDREN                                           = "product"
                                                                                           + GET_CHILDREN_SUFFIX;
    public static final String    GET_CHILD_RULES_BY_RELATIONSHIP                        = "product"
                                                                                           + GET_CHILD_RULES_BY_RELATIONSHIP_SUFFIX;
    public static final String    NAME_SEARCH                                            = "product"
                                                                                           + NAME_SEARCH_SUFFIX;
    public static final String    SUBSUMING_ENTITIES                                     = "product.subsumingEntities";
    public static final String    UNIQUE_ENTITY_BY_ATTRIBUTE_VALUE                       = "product.uniqueEntityByAttributeValue";
    public static final String    UPDATED_BY                                             = "product"
                                                                                           + GET_UPDATED_BY_SUFFIX;
    public static final String    UPDATED_BY_NAME                                        = "product.getUpdatedByName";

    private static final long     serialVersionUID                                       = 1L;

    // bi-directional many-to-one association to ProductAttribute
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ProductAttribute> attributes;

    @Id
    @GeneratedValue(generator = "product_id_seq", strategy = GenerationType.SEQUENCE)
    private Long                  id;

    // bi-directional many-to-one association to ProductLocation
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ProductLocation>  locations;

    // bi-directional many-to-one association to ProductNetwork
    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ProductNetwork>   networkByChild;

    // bi-directional many-to-one association to ProductNetwork
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ProductNetwork>   networkByParent;

    public Product() {
    }

    /**
     * @param updatedBy
     */
    public Product(Agency updatedBy) {
        super(updatedBy);
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
     * @param name
     */
    public Product(String name) {
        super(name);
    }

    /**
     * @param name
     * @param updatedBy
     */
    public Product(String name, Agency updatedBy) {
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
    public Product(String name, String description, Agency updatedBy) {
        super(name, description, updatedBy);
    }

    public void addAttribute(ProductAttribute attribute) {
        attribute.setProduct(this);
        attributes.add(attribute);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.Networked#addChildRelationship(com.hellblazer.CoRE
     * .NetworkRuleform)
     */
    @Override
    public void addChildRelationship(ProductNetwork relationship) {
        relationship.setChild(this);
        networkByChild.add(relationship);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.Networked#addParentRelationship(com.hellblazer.CoRE
     * .NetworkRuleform)
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

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.attribute.Attributable#getAttributeType()
     */
    @Override
    public Class<ProductAttribute> getAttributeType() {
        return ProductAttribute.class;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Set<ProductLocation> getLocations() {
        return locations;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.Networked#getNetworkByChild()
     */
    @Override
    public Set<ProductNetwork> getNetworkByChild() {
        if (networkByChild == null) {
            return Collections.emptySet();
        }
        return networkByChild;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.Networked#getNetworkByParent()
     */
    @Override
    public Set<ProductNetwork> getNetworkByParent() {
        if (networkByParent == null) {
            return Collections.emptySet();
        }
        return networkByParent;
    }

    @Override
    public void link(Relationship r, Product child, Agency updatedBy,
                     Agency inverseSoftware, EntityManager em) {
        ProductNetwork link = new ProductNetwork(this, r, child, updatedBy);
        em.persist(link);
        ProductNetwork inverse = new ProductNetwork(child, r.getInverse(),
                                                    this, inverseSoftware);
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

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.Networked#setNetworkByChild(java.util.Set)
     */
    @Override
    public void setNetworkByChild(Set<ProductNetwork> theNetworkByChild) {
        networkByChild = theNetworkByChild;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.Networked#setNetworkByParent(java.util.Set)
     */
    @Override
    public void setNetworkByParent(Set<ProductNetwork> theNetworkByParent) {
        networkByParent = theNetworkByParent;
    }
}