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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.postgresql.pljava.TriggerData;

import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.event.status.StatusCode;
import com.hellblazer.CoRE.event.status.StatusCodeSequencing;
import com.hellblazer.CoRE.jsp.JSP;
import com.hellblazer.CoRE.jsp.StoredProcedure;
import com.hellblazer.CoRE.kernel.Kernel;
import com.hellblazer.CoRE.kernel.KernelImpl;
import com.hellblazer.CoRE.meta.Model;
import com.hellblazer.CoRE.meta.ProductModel;
import com.hellblazer.CoRE.meta.StatusCodeModel;
import com.hellblazer.CoRE.network.Aspect;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.product.ProductAttribute;
import com.hellblazer.CoRE.product.ProductAttributeAuthorization;
import com.hellblazer.CoRE.product.ProductNetwork;
import com.hellblazer.CoRE.product.access.ProductAgencyAccessAuthorization;
import com.hellblazer.CoRE.product.access.ProductAttributeAccessAuthorization;
import com.hellblazer.CoRE.product.access.ProductLocationAccessAuthorization;
import com.hellblazer.CoRE.product.access.ProductRelationshipAccessAuthorization;
import com.hellblazer.CoRE.product.access.ProductStatusCodeAccessAuthorization;
import com.hellblazer.CoRE.product.access.ProductUnitAccessAuthorization;

/**
 * @author hhildebrand
 * 
 */
public class ProductModelImpl
        extends
        AbstractNetworkedModel<Product, ProductNetwork, ProductAttributeAuthorization, ProductAttribute>
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

    private final StatusCodeModel statusCodeModel;

    /**
     * @param em
     */
    public ProductModelImpl(EntityManager em) {
        this(em, new KernelImpl(em));
    }

    /**
     * @param em
     */
    public ProductModelImpl(EntityManager em, Kernel kernel) {
        super(em, kernel);
        statusCodeModel = new StatusCodeModelImpl(em, kernel);
    }

    public ProductModelImpl(Model model) {
        super(model.getEntityManager(), model.getKernel());
        statusCodeModel = model.getStatusCodeModel();
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
     * com.hellblazer.CoRE.meta.ProductModel#findStatusCodeSequences(com.hellblazer
     * .CoRE.product.Product, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public Collection<StatusCodeSequencing> findStatusCodeSequences(Product parent,
                                                                    Relationship relationship) {
        Set<StatusCodeSequencing> sequences = new HashSet<StatusCodeSequencing>();
        for (ProductStatusCodeAccessAuthorization auth : getStatusCodeAccessAuths(parent,
                                                                                  relationship)) {
            for (StatusCode code : statusCodeModel.getChildren(auth.getChild(),
                                                               auth.getChildTransitiveRelationship())) {
                sequences.addAll(statusCodeModel.getStatusCodeSequencingChild(code));
                sequences.addAll(statusCodeModel.getStatusCodeSequencingParent(code));
            }
        }
        return sequences;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.ProductModel#getAgencyAccessAuths(com.hellblazer.CoRE.product.Product, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public List<ProductAgencyAccessAuthorization> getAgencyAccessAuths(Product parent,
                                                                       Relationship relationship) {
        return em.createNamedQuery(ProductAgencyAccessAuthorization.FIND_AUTHORIZATION,
                                   ProductAgencyAccessAuthorization.class).getResultList();
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.ProductModel#getAttributeAccessAuths(com.hellblazer.CoRE.product.Product, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public List<ProductAttributeAccessAuthorization> getAttributeAccessAuths(Product parent,
                                                                             Relationship relationship) {
        return em.createNamedQuery(ProductAttributeAccessAuthorization.FIND_AUTHORIZATION,
                                   ProductAttributeAccessAuthorization.class).getResultList();
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.ProductModel#getLocationAccessAuths(com.hellblazer.CoRE.product.Product, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public List<ProductLocationAccessAuthorization> getLocationAccessAuths(Product parent,
                                                                           Relationship relationship) {
        return em.createNamedQuery(ProductLocationAccessAuthorization.FIND_AUTHORIZATION,
                                   ProductLocationAccessAuthorization.class).getResultList();
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.ProductModel#getRelationshipAccessAuths(com.hellblazer.CoRE.product.Product, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public List<ProductRelationshipAccessAuthorization> getRelationshipAccessAuths(Product parent,
                                                                                   Relationship relationship) {
        return em.createNamedQuery(ProductRelationshipAccessAuthorization.FIND_AUTHORIZATION,
                                   ProductRelationshipAccessAuthorization.class).getResultList();
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.ProductModel#getStatusCodeAccessAuths(com.hellblazer.CoRE.product.Product, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public List<ProductStatusCodeAccessAuthorization> getStatusCodeAccessAuths(Product parent,
                                                                               Relationship relationship) {
        return em.createNamedQuery(ProductStatusCodeAccessAuthorization.FIND_AUTHORIZATION,
                                   ProductStatusCodeAccessAuthorization.class).getResultList();
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.ProductModel#getUnitAccessAuths(com.hellblazer.CoRE.product.Product, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public List<ProductUnitAccessAuthorization> getUnitAccessAuths(Product parent,
                                                                   Relationship relationship) {
        return em.createNamedQuery(ProductUnitAccessAuthorization.FIND_AUTHORIZATION,
                                   ProductUnitAccessAuthorization.class).getResultList();
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
}
