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

package com.chiralbehaviors.CoRE.meta.models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.postgresql.pljava.TriggerData;

import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.jsp.JSP;
import com.chiralbehaviors.CoRE.jsp.StoredProcedure;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.KernelImpl;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.ProductModel;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Facet;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductAttribute;
import com.chiralbehaviors.CoRE.product.ProductAttributeAuthorization;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.chiralbehaviors.CoRE.product.access.ProductAgencyAccessAuthorization;
import com.chiralbehaviors.CoRE.product.access.ProductAttributeAccessAuthorization;
import com.chiralbehaviors.CoRE.product.access.ProductLocationAccessAuthorization;
import com.chiralbehaviors.CoRE.product.access.ProductRelationshipAccessAuthorization;
import com.chiralbehaviors.CoRE.product.access.ProductStatusCodeAccessAuthorization;
import com.chiralbehaviors.CoRE.product.access.ProductUnitAccessAuthorization;

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

        @Override
        public String toString() {
            return "Call [" + procedure + "]";
        }
    }

    private static interface Procedure<T> {
        T call(ProductModel productModel) throws Exception;
    }

    public static void propagate_deductions(final TriggerData data)
                                                                   throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(ProductModel productModel) throws Exception {
                productModel.propagate();
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
        this(em, new KernelImpl(em));
    }

    /**
     * @param em
     */
    public ProductModelImpl(EntityManager em, Kernel kernel) {
        super(em, kernel);
    }

    public ProductModelImpl(Model model) {
        super(model.getEntityManager(), model.getKernel());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#authorize(com.chiralbehaviors.CoRE
     * .meta.Aspect, com.chiralbehaviors.CoRE.attribute.Attribute[])
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
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(com.chiralbehaviors.CoRE.network
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

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#create(java.lang.String, java.lang.String, com.chiralbehaviors.CoRE.network.Aspect)
     */
    @Override
    public Facet<Product, ProductAttribute> create(String name,
                                                   String description,
                                                   Aspect<Product> aspect) {
        Product product = new Product(name, description, kernel.getCoreModel());
        em.persist(product);
        return new Facet<Product, ProductAttribute>(aspect, product,
                                                    initialize(product, aspect)) {
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(com.chiralbehaviors.CoRE.meta
     * .Aspect<RuleForm>[])
     */
    @SafeVarargs
    @Override
    final public Product create(String name, String description,
                                Aspect<Product> aspect,
                                Aspect<Product>... aspects) {
        Product product = new Product(name, description, kernel.getCoreModel());
        em.persist(product);
        initialize(product, aspect);
        if (aspects != null) {
            for (Aspect<Product> a : aspects) {
                initialize(product, a);
            }
        }
        return product;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.ProductModel#getAgencyAccessAuths(com.chiralbehaviors.CoRE.product.Product, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public List<ProductAgencyAccessAuthorization> getAgencyAccessAuths(Product parent,
                                                                       Relationship relationship) {
        return em.createNamedQuery(ProductAgencyAccessAuthorization.FIND_AUTHORIZATION,
                                   ProductAgencyAccessAuthorization.class).getResultList();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.ProductModel#getAttributeAccessAuths(com.chiralbehaviors.CoRE.product.Product, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public List<ProductAttributeAccessAuthorization> getAttributeAccessAuths(Product parent,
                                                                             Relationship relationship) {
        return em.createNamedQuery(ProductAttributeAccessAuthorization.FIND_AUTHORIZATION,
                                   ProductAttributeAccessAuthorization.class).getResultList();
    }

    @Override
    public List<ProductNetwork> getInterconnections(List<Product> parents,
                                                    List<Relationship> relationships,
                                                    List<Product> children) {
        if (parents == null || parents.size() == 0 || relationships == null
            || relationships.size() == 0 || children == null
            || children.size() == 0) {
            return null;
        }
        TypedQuery<ProductNetwork> query = em.createNamedQuery(ProductNetwork.GET_NETWORKS,
                                                               ProductNetwork.class);
        query.setParameter("parents", parents);
        query.setParameter("relationships", relationships);
        query.setParameter("children", children);
        return query.getResultList();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.ProductModel#getLocationAccessAuths(com.chiralbehaviors.CoRE.product.Product, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public List<ProductLocationAccessAuthorization> getLocationAccessAuths(Product parent,
                                                                           Relationship relationship) {
        return em.createNamedQuery(ProductLocationAccessAuthorization.FIND_AUTHORIZATION,
                                   ProductLocationAccessAuthorization.class).getResultList();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.ProductModel#getRelationshipAccessAuths(com.chiralbehaviors.CoRE.product.Product, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public List<ProductRelationshipAccessAuthorization> getRelationshipAccessAuths(Product parent,
                                                                                   Relationship relationship) {
        return em.createNamedQuery(ProductRelationshipAccessAuthorization.FIND_AUTHORIZATION,
                                   ProductRelationshipAccessAuthorization.class).getResultList();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.ProductModel#getStatusCodeAccessAuths(com.chiralbehaviors.CoRE.product.Product, com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public List<ProductStatusCodeAccessAuthorization> getStatusCodeAccessAuths(Product parent,
                                                                               Relationship relationship) {
        return em.createNamedQuery(ProductStatusCodeAccessAuthorization.FIND_AUTHORIZATION,
                                   ProductStatusCodeAccessAuthorization.class).getResultList();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.ProductModel#getUnitAccessAuths(com.chiralbehaviors.CoRE.product.Product, com.chiralbehaviors.CoRE.network.Relationship)
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
    protected List<ProductAttribute> initialize(Product product,
                                                Aspect<Product> aspect) {
        product.link(aspect.getClassification(), aspect.getClassifier(),
                     kernel.getCoreModel(), kernel.getInverseSoftware(), em);
        List<ProductAttribute> attributes = new ArrayList<>();
        for (ProductAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
            ProductAttribute attribute = new ProductAttribute(
                                                              authorization.getAuthorizedAttribute(),
                                                              kernel.getCoreModel());
            attributes.add(attribute);
            attribute.setProduct(product);
            defaultValue(attribute);
            em.persist(attribute);
        }
        return attributes;
    }
}
