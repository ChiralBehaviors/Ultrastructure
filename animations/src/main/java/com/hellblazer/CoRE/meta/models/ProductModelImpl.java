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

package com.hellblazer.CoRE.meta.models;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.postgresql.pljava.TriggerData;

import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.event.StatusCode;
import com.hellblazer.CoRE.event.StatusCodeSequencing;
import com.hellblazer.CoRE.jsp.JSP;
import com.hellblazer.CoRE.jsp.StoredProcedure;
import com.hellblazer.CoRE.kernel.Kernel;
import com.hellblazer.CoRE.kernel.KernelImpl;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.meta.ProductModel;
import com.hellblazer.CoRE.meta.graph.Graph;
import com.hellblazer.CoRE.network.Aspect;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.product.ProductAgencyAccessAuthorization;
import com.hellblazer.CoRE.product.ProductAttribute;
import com.hellblazer.CoRE.product.ProductAttributeAuthorization;
import com.hellblazer.CoRE.product.ProductLocationAccessAuthorization;
import com.hellblazer.CoRE.product.ProductNetwork;

/**
 * @author hhildebrand
 * 
 */
public class ProductModelImpl
        extends
        AbstractNetworkedModel<Product, ProductAttributeAuthorization, ProductAttribute>
        implements ProductModel {

    private static class Call<T> implements StoredProcedure<T> {
        private final Procedure<T> procedure;

        public Call(Procedure<T> procedure) {
            this.procedure = procedure;
        }

        @Override
        public T call(EntityManager em) throws Exception {
            return procedure.call(new ProductModelImpl(em));
        }
    }

    private static interface Procedure<T> {
        T call(ProductModelImpl productModel) throws Exception;
    }

    private static final String PRODUCT_NETWORK_PROPAGATE = "ProductNetwork.propagate";

    public static void propagate_deductions(TriggerData data) throws Exception {
        if (!markPropagated(PRODUCT_NETWORK_PROPAGATE)) {
            return; // We be done
        }
        execute(new Procedure<Void>() {
            @Override
            public Void call(ProductModelImpl productModel) throws Exception {
                productModel.propagate();
                return null;
            }
        });
    }

    public static void track_network_deleted(final TriggerData data)
                                                                    throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(ProductModelImpl productModel) throws Exception {
                productModel.networkEdgeDeleted(data.getOld().getLong("parent"),
                                                data.getOld().getLong("relationship"));
                return null;
            }
        });
    }

    private static <T> T execute(Procedure<T> procedure) throws SQLException {
        return JSP.call(new Call<T>(procedure));
    }

    /**
     * @param em
     */
    public ProductModelImpl(EntityManager em) {
        super(em, new KernelImpl(em));
    }

    /**
     * @param em
     */
    public ProductModelImpl(EntityManager em, Kernel kernel) {
        super(em, kernel);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.NetworkedModel#authorize(com.hellblazer.CoRE
     * .meta.Aspect, com.hellblazer.CoRE.attribute.Attribute[])
     */
    @Override
    public void authorize(Aspect<Product> aspect, Attribute... attributes) {
        for (Attribute attribute : attributes) {
            ProductAttributeAuthorization authorization = new ProductAttributeAuthorization(
                                                                                            aspect.getClassification(),
                                                                                            aspect.getClassifier(),
                                                                                            attribute,
                                                                                            kernel.getCoreModel());
            em.persist(authorization);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.NetworkedModel#create(com.hellblazer.CoRE.network
     * .Networked)
     */
    @Override
    public Product create(Product prototype) {
        Product copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(kernel.getCoreModel());
        for (ProductNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(), copy,
                                     kernel.getCoreModel(),
                                     kernel.getInverseSoftware(), em);
        }
        for (ProductAttribute attribute : prototype.getAttributes()) {
            ProductAttribute clone = (ProductAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setProduct(copy);
            clone.setUpdatedBy(kernel.getCoreModel());
        }
        return copy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.NetworkedModel#create(com.hellblazer.CoRE.meta
     * .Aspect<RuleForm>[])
     */
    @Override
    final public Product create(String name, String description,
                                Aspect<Product> aspect,
                                Aspect<Product>... aspects) {
        Product agency = new Product(name, description, kernel.getCoreModel());
        em.persist(agency);
        initialize(agency, aspect);
        if (aspects != null) {
            for (Aspect<Product> a : aspects) {
                initialize(agency, a);
            }
        }
        return agency;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.ProductModel#findStatusCodes(com.hellblazer.
     * CoRE.product.Product)
     */
    @Override
    public List<StatusCode> findStatusCodes(Product parent) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.ProductModel#findStatusCodeSequences(com.hellblazer
     * .CoRE.product.Product, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public List<StatusCodeSequencing> findStatusCodeSequences(Product parent,
                                                              Relationship relationship) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.meta.NetworkedModel#findUnlinkedNodes()
     */
    @Override
    public List<Product> findUnlinkedNodes() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.NetworkedModel#getImmediateRelationships(com
     * .hellblazer.CoRE.network.Networked)
     */
    @Override
    public List<Relationship> getImmediateRelationships(Product parent) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.NetworkedModel#getNetwork(com.hellblazer.CoRE
     * .network.Networked, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public Graph getNetwork(Product parent, Relationship relationship) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.NetworkedModel#getTransitiveRelationships(com
     * .hellblazer.CoRE.network.Networked)
     */
    @Override
    public List<Relationship> getTransitiveRelationships(Product parent) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.NetworkedModel#isAccessible(com.hellblazer.CoRE
     * .network.Networked, com.hellblazer.CoRE.network.Relationship,
     * com.hellblazer.CoRE.network.Relationship,
     * com.hellblazer.CoRE.network.Networked,
     * com.hellblazer.CoRE.network.Relationship)
     */

    @Override
    public boolean isAccessible(Product parent,
                                Relationship parentRelationship,
                                Relationship authorizingRelationship,
                                ExistentialRuleform<?, ?> child,
                                Relationship childRelationship) {

        if (parent == null || child == null || authorizingRelationship == null) {
            throw new IllegalArgumentException(
                                               "parent, authorizingRelationship, and child cannot be null");
        }
        if ("Agency".equals(child.getClass().getSimpleName())) {

            return isAgencyAccessible(parent, parentRelationship,
                                      authorizingRelationship, (Agency) child,
                                      childRelationship);
        } else if ("Location".equals(child.getClass().getSimpleName())) {
            return isLocationAccessible(parent, parentRelationship,
                                        authorizingRelationship,
                                        (Location) child, childRelationship);
        } else {
            throw new IllegalArgumentException(
                                               "child type is not supported for this query");
        }

    }

    /**
     * @param parent
     * @param parentRelationship
     * @param authorizingRelationship
     * @param child
     * @param childRelationship
     * @return
     */
    private boolean isAgencyAccessible(Product parent,
                                       Relationship parentRelationship,
                                       Relationship authorizingRelationship,
                                       Agency child,
                                       Relationship childRelationship) {
        Query query;

        if (parentRelationship == null && childRelationship == null) {
            query = em.createNamedQuery(ProductAgencyAccessAuthorization.FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD);
            query.setParameter("parent", parent);
            query.setParameter("relationship", authorizingRelationship);
            query.setParameter("child", child);
        } else if (childRelationship == null) {
            query = em.createNamedQuery(ProductAgencyAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_PARENT);
            query.setParameter("relationship", authorizingRelationship);
            query.setParameter("child", child);
            query.setParameter("netRelationship", parentRelationship);
            query.setParameter("netChild", parent);

        } else if (parentRelationship == null) {
            query = em.createNamedQuery(ProductAgencyAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_CHILD);
            query.setParameter("relationship", authorizingRelationship);
            query.setParameter("parent", parent);
            query.setParameter("netRelationship", childRelationship);
            query.setParameter("netChild", child);

        } else {
            query = em.createNamedQuery(ProductAgencyAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_PARENT_AND_CHILD);
            query.setParameter("relationship", authorizingRelationship);
            query.setParameter("parentNetRelationship", parentRelationship);
            query.setParameter("parentNetChild", parent);
            query.setParameter("childNetRelationship", childRelationship);
            query.setParameter("childNetChild", child);

        }
        List<?> results = query.getResultList();

        return results.size() > 0;

    }

    /**
     * @param parent
     * @param parentRelationship
     * @param authorizingRelationship
     * @param child
     * @param childRelationship
     * @return
     */
    private boolean isLocationAccessible(Product parent,
                                         Relationship parentRelationship,
                                         Relationship authorizingRelationship,
                                         Location child,
                                         Relationship childRelationship) {
        Query query;

        if (parentRelationship == null && childRelationship == null) {
            query = em.createNamedQuery(ProductLocationAccessAuthorization.FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD);
            query.setParameter("parent", parent);
            query.setParameter("relationship", authorizingRelationship);
            query.setParameter("child", child);
        } else if (childRelationship == null) {
            query = em.createNamedQuery(ProductLocationAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_PARENT);
            query.setParameter("relationship", authorizingRelationship);
            query.setParameter("child", child);
            query.setParameter("netRelationship", parentRelationship);
            query.setParameter("netChild", parent);

        } else if (parentRelationship == null) {
            query = em.createNamedQuery(ProductLocationAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_CHILD);
            query.setParameter("relationship", authorizingRelationship);
            query.setParameter("parent", parent);
            query.setParameter("netRelationship", childRelationship);
            query.setParameter("netChild", child);

        } else {
            query = em.createNamedQuery(ProductLocationAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_PARENT_AND_CHILD);
            query.setParameter("relationship", authorizingRelationship);
            query.setParameter("parentNetRelationship", parentRelationship);
            query.setParameter("parentNetChild", parent);
            query.setParameter("childNetRelationship", childRelationship);
            query.setParameter("childNetChild", child);

        }
        List<?> results = query.getResultList();

        return results.size() > 0;

    }

    /**
     * @param product
     * @param aspect
     */
    protected void initialize(Product product, Aspect<Product> aspect) {
        product.link(aspect.getClassification(), aspect.getClassifier(),
                     kernel.getCoreModel(), kernel.getInverseSoftware(), em);
        for (ProductAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
            ProductAttribute attribute = new ProductAttribute(
                                                              authorization.getAuthorizedAttribute(),
                                                              kernel.getCoreModel());
            attribute.setProduct(product);
            defaultValue(attribute);
            em.persist(attribute);
        }
    }
    
    public List<?> getLeaves(Product product, Relationship relationship) {
        Query query = em.createNamedQuery(ProductAgencyAccessAuthorization.FIND_RULEFORMS_REFERENCED_BY_AUTH);
        query.setParameter("parent", product);
        query.setParameter("relationship", relationship);
        return query.getResultList();
    }
    
    public List<?> getNetworks(Product product, Relationship relationship) {
        Query query = em.createNamedQuery(ProductAgencyAccessAuthorization.FIND_PARENT_CHILD_NETWORKS);
        query.setParameter("parent", product);
        query.setParameter("relationship", relationship);
        return query.getResultList();
    }
}
