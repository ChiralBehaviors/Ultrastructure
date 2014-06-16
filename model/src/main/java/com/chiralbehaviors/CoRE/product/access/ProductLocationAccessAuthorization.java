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

import static com.chiralbehaviors.CoRE.product.access.ProductLocationAccessAuthorization.FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD;
import static com.chiralbehaviors.CoRE.product.access.ProductLocationAccessAuthorization.FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD_MATCH_ON_ALL_RELATIONSHIPS;
import static com.chiralbehaviors.CoRE.product.access.ProductLocationAccessAuthorization.FIND_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.product.access.ProductLocationAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_CHILD;
import static com.chiralbehaviors.CoRE.product.access.ProductLocationAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_PARENT;
import static com.chiralbehaviors.CoRE.product.access.ProductLocationAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_PARENT_AND_CHILD;
import static com.chiralbehaviors.CoRE.product.access.ProductLocationAccessAuthorization.FIND_PRODUCTS_IN_LOCATION;

import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.authorization.AccessAuthorization;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hparry
 * 
 */
@NamedQueries({
               @NamedQuery(name = FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD_MATCH_ON_ALL_RELATIONSHIPS, query = "SELECT auth "
                                                                                                                   + "FROM ProductLocationAccessAuthorization auth "
                                                                                                                   + "WHERE auth.parent = :parent "
                                                                                                                   + "AND auth.relationship = :relationship "
                                                                                                                   + "AND auth.child = :child "
                                                                                                                   + "AND auth.parentTransitiveRelationship = :parentRelationship "
                                                                                                                   + "AND auth.childTransitiveRelationship = :childRelationship"),
               @NamedQuery(name = FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD, query = "SELECT auth "
                                                                                        + "FROM ProductLocationAccessAuthorization auth "
                                                                                        + "WHERE auth.parent = :parent "
                                                                                        + "AND auth.relationship = :relationship "
                                                                                        + "AND auth.child = :child "),
               @NamedQuery(name = FIND_AUTHS_FOR_INDIRECT_PARENT, query = "SELECT auth "
                                                                          + "FROM ProductLocationAccessAuthorization auth, ProductNetwork net "
                                                                          + "WHERE auth.relationship = :relationship "
                                                                          + "AND auth.child = :child "
                                                                          + "AND net.relationship = :netRelationship "
                                                                          + "AND net.child = :netChild "
                                                                          + "AND auth.parent = net.parent "),
               @NamedQuery(name = FIND_AUTHS_FOR_INDIRECT_CHILD, query = "SELECT auth "
                                                                         + "FROM ProductLocationAccessAuthorization auth, LocationNetwork net "
                                                                         + "WHERE auth.relationship = :relationship "
                                                                         + "AND auth.parent = :parent "
                                                                         + "AND net.relationship = :netRelationship "
                                                                         + "AND net.child = :netChild "
                                                                         + "AND auth.child = net.parent "),
               @NamedQuery(name = FIND_AUTHS_FOR_INDIRECT_PARENT_AND_CHILD, query = "SELECT auth "
                                                                                    + "FROM ProductLocationAccessAuthorization auth, ProductNetwork parentNet, LocationNetwork childNet "
                                                                                    + "WHERE auth.relationship = :relationship "
                                                                                    + "AND parentNet.relationship = :parentNetRelationship "
                                                                                    + "AND parentNet.child = :parentNetChild "
                                                                                    + "AND childNet.relationship = :childNetRelationship "
                                                                                    + "AND childNet.child = :childNetChild "
                                                                                    + "AND auth.parent = parentNet.parent "
                                                                                    + "AND auth.child = childNet.parent "),
               @NamedQuery(name = FIND_AUTHORIZATION, query = "SELECT auth "
                                                              + "FROM ProductLocationAccessAuthorization auth "
                                                              + "WHERE auth.parent = :parent "
                                                              + "AND auth.relationship = :relationship "),
               @NamedQuery(name = FIND_PRODUCTS_IN_LOCATION, query = "SELECT auth.parent "
                                                                     + "FROM ProductLocationAccessAuthorization auth "
                                                                     + "WHERE auth.child = :location "
                                                                     + "AND auth.relationship = :relationship") })
@Entity
@DiscriminatorValue(AccessAuthorization.PRODUCT_LOCATION)
public class ProductLocationAccessAuthorization extends
        ProductAccessAuthorization<Location> {
    public static final String PRODUCT_LOCATION_ACCESS_AUTH_PREFIX                                     = "productLocationAccessAuthorization";
    public static final String FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD                            = PRODUCT_LOCATION_ACCESS_AUTH_PREFIX
                                                                                                         + FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD_SUFFIX;

    public static final String FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD_MATCH_ON_ALL_RELATIONSHIPS = PRODUCT_LOCATION_ACCESS_AUTH_PREFIX
                                                                                                         + FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD_MATCH_ON_ALL_RELATIONSHIPS_SUFFIX;
    public static final String FIND_AUTHORIZATION                                                      = PRODUCT_LOCATION_ACCESS_AUTH_PREFIX
                                                                                                         + FIND_AUTHORIZATION_SUFFIX;
    public static final String FIND_AUTHS_FOR_INDIRECT_CHILD                                           = PRODUCT_LOCATION_ACCESS_AUTH_PREFIX
                                                                                                         + FIND_AUTHS_FOR_INDIRECT_CHILD_SUFFIX;
    public static final String FIND_AUTHS_FOR_INDIRECT_PARENT                                          = PRODUCT_LOCATION_ACCESS_AUTH_PREFIX
                                                                                                         + FIND_AUTHS_FOR_INDIRECT_PARENT_SUFFIX;
    public static final String FIND_AUTHS_FOR_INDIRECT_PARENT_AND_CHILD                                = PRODUCT_LOCATION_ACCESS_AUTH_PREFIX
                                                                                                         + FIND_AUTHS_FOR_INDIRECT_PARENT_AND_CHILD_SUFFIX;
    public static final String FIND_PRODUCTS_IN_LOCATION                                               = PRODUCT_ACCESS_AUTHORIZATION_PREFIX
                                                                                                         + ".findProductsInLocation";
    private static final long  serialVersionUID                                                        = 1L;

    @ManyToOne
    @JoinColumn(name = "location2")
    private Location           child;

    {
        setAuthorizationType(AccessAuthorization.PRODUCT_LOCATION);
    }

    public ProductLocationAccessAuthorization() {
        super();
    }

    /**
     * @param Agency
     * @param Relationship
     * @param Product
     * @param updatedBy
     */
    public ProductLocationAccessAuthorization(Product parent,
                                              Relationship relationship,
                                              Location child, Agency updatedBy) {
        this();
        setParent(parent);
        setRelationship(relationship);
        setChild(child);
        setUpdatedBy(updatedBy);
    }

    /**
     * @return the child
     */
    @Override
    public Location getChild() {
        return child;
    }

    /**
     * @param child
     *            the child to set
     */
    public void setChild(Location child) {
        this.child = child;
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
        if (child != null) {
            child = (Location) child.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);
    }

}
