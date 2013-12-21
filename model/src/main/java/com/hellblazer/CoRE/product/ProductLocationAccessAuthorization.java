/**
 * Copyright (C) 2013 Halloran Parry. All rights reserved.
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

import static com.hellblazer.CoRE.product.ProductLocationAccessAuthorization.FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD;
import static com.hellblazer.CoRE.product.ProductLocationAccessAuthorization.FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD_MATCH_ON_ALL_RELATIONSHIPS;
import static com.hellblazer.CoRE.product.ProductLocationAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_CHILD;
import static com.hellblazer.CoRE.product.ProductLocationAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_PARENT;
import static com.hellblazer.CoRE.product.ProductLocationAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_PARENT_AND_CHILD;

import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.authorization.AccessAuthorization;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.network.Relationship;

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
                                                                                    + "AND auth.child = childNet.parent ") })
@Entity
@DiscriminatorValue(AccessAuthorization.PRODUCT_LOCATION)
public class ProductLocationAccessAuthorization extends
        ProductAccessAuthorization<Location> {
    public static final String PRODUCT_LOCATION_ACCESS_AUTH_PREFIX                                     = "productLocationAccessAuthorization";

    public static final String FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD                            = PRODUCT_LOCATION_ACCESS_AUTH_PREFIX
                                                                                                         + FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD_SUFFIX;
    public static final String FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD_MATCH_ON_ALL_RELATIONSHIPS = PRODUCT_LOCATION_ACCESS_AUTH_PREFIX
                                                                                                         + FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD_MATCH_ON_ALL_RELATIONSHIPS_SUFFIX;
    public static final String FIND_AUTHS_FOR_INDIRECT_CHILD                                           = PRODUCT_LOCATION_ACCESS_AUTH_PREFIX
                                                                                                         + FIND_AUTHS_FOR_INDIRECT_CHILD_SUFFIX;
    public static final String FIND_AUTHS_FOR_INDIRECT_PARENT                                          = PRODUCT_LOCATION_ACCESS_AUTH_PREFIX
                                                                                                         + FIND_AUTHS_FOR_INDIRECT_PARENT_SUFFIX;

    public static final String FIND_AUTHS_FOR_INDIRECT_PARENT_AND_CHILD                                = PRODUCT_LOCATION_ACCESS_AUTH_PREFIX
                                                                                                         + FIND_AUTHS_FOR_INDIRECT_PARENT_AND_CHILD_SUFFIX;

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
     * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.
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
