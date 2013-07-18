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

import java.util.concurrent.Callable;

import javax.persistence.EntityManager;

import org.postgresql.pljava.TriggerData;

import com.hellblazer.CoRE.animation.JSP;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.meta.Kernel;
import com.hellblazer.CoRE.meta.ProductModel;
import com.hellblazer.CoRE.network.Aspect;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.product.ProductAttribute;
import com.hellblazer.CoRE.product.ProductAttributeAuthorization;
import com.hellblazer.CoRE.product.ProductNetwork;

/**
 * @author hhildebrand
 * 
 */
public class ProductModelImpl
        extends
        AbstractNetworkedModel<Product, ProductAttributeAuthorization, ProductAttribute>
        implements ProductModel {

    private static class InDatabase {
        private static final ProductModel SINGLETON;

        static {
            SINGLETON = new ProductModelImpl(JSP.getEm());
        }

        public static ProductModel get() {
            return SINGLETON;
        }
    }

    private static final String PRODUCT_NETWORK_PROPAGATE = "ProductNetwork.propagate";

    public static void propagate_deductions(TriggerData data) throws Exception {
        if (!markPropagated(PRODUCT_NETWORK_PROPAGATE)) {
            return; // We be done
        }
        JSP.execute(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                InDatabase.get().propagate();
                return null;
            }
        });
    }

    public static void track_network_deleted(final TriggerData data)
                                                                    throws Exception {
        JSP.execute(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                InDatabase.get().networkEdgeDeleted(data.getOld().getLong("parent"),
                                                    data.getOld().getLong("relationship"));
                return null;
            }
        });
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

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.NetworkedModel#authorize(com.hellblazer.CoRE.meta.Aspect, com.hellblazer.CoRE.attribute.Attribute[])
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

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.NetworkedModel#create(com.hellblazer.CoRE.network.Networked)
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
     * @see com.hellblazer.CoRE.meta.NetworkedModel#create(com.hellblazer.CoRE.meta.Aspect<RuleForm>[])
     */
    @Override
    final public Product create(String name, String description,
                                Aspect<Product> aspect,
                                Aspect<Product>... aspects) {
        Product resource = new Product(name, description, kernel.getCoreModel());
        em.persist(resource);
        initialize(resource, aspect);
        if (aspects != null) {
            for (Aspect<Product> a : aspects) {
                initialize(resource, a);
            }
        }
        return resource;
    }

    /**
     * @param resource
     * @param aspect
     */
    protected void initialize(Product resource, Aspect<Product> aspect) {
        resource.link(aspect.getClassification(), aspect.getClassifier(),
                      kernel.getCoreModel(), kernel.getInverseSoftware(), em);
        for (ProductAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
            ProductAttribute attribute = new ProductAttribute(
                                                              authorization.getAuthorizedAttribute(),
                                                              kernel.getCoreModel());
            attribute.setProduct(resource);
            defaultValue(attribute);
            em.persist(attribute);
        }
    }
}
