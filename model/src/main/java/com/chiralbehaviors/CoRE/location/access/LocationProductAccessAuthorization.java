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
package com.chiralbehaviors.CoRE.location.access;

import static com.chiralbehaviors.CoRE.location.access.LocationProductAccessAuthorization.FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD;
import static com.chiralbehaviors.CoRE.location.access.LocationProductAccessAuthorization.FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD_MATCH_ON_ALL_RELATIONSHIPS;
import static com.chiralbehaviors.CoRE.location.access.LocationProductAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_CHILD;
import static com.chiralbehaviors.CoRE.location.access.LocationProductAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_PARENT;
import static com.chiralbehaviors.CoRE.location.access.LocationProductAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_PARENT_AND_CHILD;

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
                                                                                                                   + "FROM LocationProductAccessAuthorization auth "
                                                                                                                   + "WHERE auth.parent = :parent "
                                                                                                                   + "AND auth.relationship = :relationship "
                                                                                                                   + "AND auth.child = :child "
                                                                                                                   + "AND auth.parentTransitiveRelationship = :parentRelationship "
                                                                                                                   + "AND auth.childTransitiveRelationship = :childRelationship"),
               @NamedQuery(name = FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD, query = "SELECT auth "
                                                                                        + "FROM LocationProductAccessAuthorization auth "
                                                                                        + "WHERE auth.parent = :parent "
                                                                                        + "AND auth.relationship = :relationship "
                                                                                        + "AND auth.child = :child "),
               @NamedQuery(name = FIND_AUTHS_FOR_INDIRECT_PARENT, query = "SELECT auth "
                                                                          + "FROM LocationProductAccessAuthorization auth, LocationNetwork net "
                                                                          + "WHERE auth.relationship = :relationship "
                                                                          + "AND auth.child = :child "
                                                                          + "AND net.relationship = :netRelationship "
                                                                          + "AND net.child = :netChild "
                                                                          + "AND auth.parent = net.parent "),
               @NamedQuery(name = FIND_AUTHS_FOR_INDIRECT_CHILD, query = "SELECT auth "
                                                                         + "FROM LocationProductAccessAuthorization auth, ProductNetwork net "
                                                                         + "WHERE auth.relationship = :relationship "
                                                                         + "AND auth.parent = :parent "
                                                                         + "AND net.relationship = :netRelationship "
                                                                         + "AND net.child = :netChild "
                                                                         + "AND auth.child = net.parent "),
               @NamedQuery(name = FIND_AUTHS_FOR_INDIRECT_PARENT_AND_CHILD, query = "SELECT auth "
                                                                                    + "FROM LocationProductAccessAuthorization auth, LocationNetwork parentNet, ProductNetwork childNet "
                                                                                    + "WHERE auth.relationship = :relationship "
                                                                                    + "AND parentNet.relationship = :parentNetRelationship "
                                                                                    + "AND parentNet.child = :parentNetChild "
                                                                                    + "AND childNet.relationship = :childNetRelationship "
                                                                                    + "AND childNet.child = :childNetChild "
                                                                                    + "AND auth.parent = parentNet.parent "
                                                                                    + "AND auth.child = childNet.parent ") })
@Entity
@DiscriminatorValue(AccessAuthorization.LOCATION_PRODUCT)
public class LocationProductAccessAuthorization extends
        LocationAccessAuthorization<Product> {
    public static final String LOCATION_PRODUCT_ACCESS_AUTH_PREFIX                                     = "locationProductAccessAuthorization";
    public static final String FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD                            = LOCATION_PRODUCT_ACCESS_AUTH_PREFIX
                                                                                                         + FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD_SUFFIX;

    public static final String FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD_MATCH_ON_ALL_RELATIONSHIPS = LOCATION_PRODUCT_ACCESS_AUTH_PREFIX
                                                                                                         + FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD_MATCH_ON_ALL_RELATIONSHIPS_SUFFIX;

    public static final String FIND_AUTHS_FOR_INDIRECT_CHILD                                           = LOCATION_PRODUCT_ACCESS_AUTH_PREFIX
                                                                                                         + FIND_AUTHS_FOR_INDIRECT_CHILD_SUFFIX;
    public static final String FIND_AUTHS_FOR_INDIRECT_PARENT                                          = LOCATION_PRODUCT_ACCESS_AUTH_PREFIX
                                                                                                         + FIND_AUTHS_FOR_INDIRECT_PARENT_SUFFIX;
    public static final String FIND_AUTHS_FOR_INDIRECT_PARENT_AND_CHILD                                = LOCATION_PRODUCT_ACCESS_AUTH_PREFIX
                                                                                                         + FIND_AUTHS_FOR_INDIRECT_PARENT_AND_CHILD_SUFFIX;

    private static final long  serialVersionUID                                                        = 1L;

    @ManyToOne
    @JoinColumn(name = "product2")
    private Product            child;

    {
        setAuthorizationType(AccessAuthorization.LOCATION_PRODUCT);
    }

    public LocationProductAccessAuthorization() {
        super();
    }

    /**
     * @param Agency
     * @param Relationship
     * @param Product
     * @param updatedBy
     */
    public LocationProductAccessAuthorization(Location parent,
                                              Relationship relationship,
                                              Product child, Agency updatedBy) {
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
    public Product getChild() {
        return child;
    }

    /**
     * @param child
     *            the child to set
     */
    public void setChild(Product child) {
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
            child = (Product) child.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);
    }
}
