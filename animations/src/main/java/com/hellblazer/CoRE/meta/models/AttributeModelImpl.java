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

import java.util.List;
import java.util.concurrent.Callable;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.postgresql.pljava.TriggerData;

import com.hellblazer.CoRE.animation.JSP;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.AttributeMetaAttribute;
import com.hellblazer.CoRE.attribute.AttributeMetaAttributeAuthorization;
import com.hellblazer.CoRE.attribute.AttributeNetwork;
import com.hellblazer.CoRE.attribute.Transformation;
import com.hellblazer.CoRE.attribute.TransformationMetarule;
import com.hellblazer.CoRE.meta.AttributeModel;
import com.hellblazer.CoRE.meta.Kernel;
import com.hellblazer.CoRE.network.Aspect;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.product.ProductAttribute;
import com.hellblazer.CoRE.resource.Resource;

/**
 * @author hhildebrand
 * 
 */
public class AttributeModelImpl
        extends
        AbstractNetworkedModel<Attribute, AttributeMetaAttributeAuthorization, AttributeMetaAttribute>
        implements AttributeModel {

    /**
     * Used to initialize a singleton for use within the DB
     * 
     * @author hhildebrand
     * 
     */
    private static class InDatabase {
        private static final AttributeModelImpl SINGLETON;
        static {
            SINGLETON = new AttributeModelImpl(JSP.getEm());
        }

        public static AttributeModelImpl get() {
            return SINGLETON;
        }
    }

    private static final String ATTRIBUTE_NETWORK_PROPAGATE = "AttributeNetwork.propagate";

    public static void network_edge_deleted(final TriggerData data)
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

    public static void propagate_deductions(final TriggerData data)
                                                                   throws Exception {
        if (!markPropagated(ATTRIBUTE_NETWORK_PROPAGATE)) {
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

    /**
     * @param em
     */
    public AttributeModelImpl(EntityManager em) {
        this(em, new KernelImpl(em));
    }

    /**
     * @param em
     */
    public AttributeModelImpl(EntityManager em, Kernel kernel) {
        super(em, kernel);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.NetworkedModel#authorize(com.hellblazer.CoRE.meta.Aspect, com.hellblazer.CoRE.attribute.Attribute[])
     */
    @Override
    public void authorize(Aspect<Attribute> aspect, Attribute... attributes) {
        for (Attribute attribute : attributes) {
            AttributeMetaAttributeAuthorization authorization = new AttributeMetaAttributeAuthorization(
                                                                                                        aspect.getClassifier(),
                                                                                                        aspect.getClassification(),
                                                                                                        attribute,
                                                                                                        kernel.getCoreModel());
            em.persist(authorization);
        }
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.NetworkedModel#create(com.hellblazer.CoRE.network.Networked)
     */
    @Override
    public Attribute create(Attribute prototype) {
        Attribute copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(kernel.getCoreModel());
        for (AttributeNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(), copy,
                                     kernel.getCoreModel(),
                                     kernel.getInverseSoftware(), em);
        }
        for (AttributeMetaAttribute attribute : prototype.getAttributes()) {
            AttributeMetaAttribute clone = (AttributeMetaAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setAttribute(copy);
            clone.setUpdatedBy(kernel.getCoreModel());
        }
        return copy;
    }

    @Override
    public final Attribute create(String name, String description,
                                  Aspect<Attribute> aspect,
                                  Aspect<Attribute>... aspects) {
        Attribute attribute = new Attribute(name, description,
                                            kernel.getCoreModel());
        em.persist(attribute);
        initialize(attribute, aspect);
        if (aspects != null) {
            for (Aspect<Attribute> a : aspects) {
                initialize(attribute, a);
            }
        }
        return attribute;
    }

    public Attribute transform(Product service, Resource resource,
                               Product product) {

        Attribute txfmd = null;
        for (TransformationMetarule transfromationMetarule : getTransformationMetarules(service)) {
            Resource mappedResource;
            if (kernel.getSameResource().equals(transfromationMetarule.getRelationshipMap())) {
                mappedResource = kernel.getSameResource();
            } else {
                mappedResource = getMappedResource(transfromationMetarule,
                                                   resource);
            }
            Product mappedProduct;
            if (kernel.getSameProduct().equals(transfromationMetarule.getProductMap())) {
                mappedProduct = kernel.getSameProduct();
            } else {
                mappedProduct = getMappedProduct(transfromationMetarule,
                                                 product);
            }
            for (Transformation transformation : getTransformations(service,
                                                                    mappedResource,
                                                                    mappedProduct)) {
                txfmd = null;
                Resource txfmResource;
                if (kernel.getOriginalResource().equals(transformation.getResourceKey())) {
                    txfmResource = resource;
                } else {
                    txfmResource = transformation.getResourceKey();
                }
                Product txfmProduct;
                if (kernel.getOriginalProduct().equals(transformation.getProductKey())) {
                    txfmProduct = product;
                } else {
                    txfmProduct = transformation.getProductKey();
                }
                Product foundProduct = findProduct(transformation,
                                                   txfmResource, txfmProduct);

                txfmd = findAttribute(transformation, foundProduct);
                if (txfmd != null) {
                    break;
                }
            }
            if (txfmd != null && transfromationMetarule.getStopOnMatch()) {
                break;
            }
        }
        return txfmd;
    }

    /**
     * @param transformation
     * @param product
     * @return
     */
    private Attribute findAttribute(Transformation transformation,
                                    Product product) {
        TypedQuery<Attribute> attrQuery = em.createNamedQuery(ProductAttribute.FIND_ATTRIBUTE_VALUE_FROM_RESOURCE,
                                                              Attribute.class);
        attrQuery.setParameter("resource",
                               transformation.getProductAttributeResource());
        attrQuery.setParameter("product", product);
        attrQuery.setParameter("attribute", transformation.getAttribute());
        return attrQuery.getSingleResult();
    }

    /**
     * @param transformation
     * @param resource
     * @param product
     * @return
     */
    private Product findProduct(Transformation transformation,
                                Resource resource, Product product) {
        TypedQuery<Product> productNetworkQuery = em.createQuery(Product.GET_CHILD,
                                                                 Product.class);
        productNetworkQuery.setParameter("parent", product);
        productNetworkQuery.setParameter("relationship",
                                         transformation.getRelationshipKey());
        // productNetworkQuery.setParameter("resource", resource);
        return productNetworkQuery.getSingleResult();
    }

    /**
     * @param transfromationMetarule
     * @param product
     * @return
     */
    private Product getMappedProduct(TransformationMetarule transfromationMetarule,
                                     Product product) {
        TypedQuery<Product> productNetworkQuery = em.createQuery(Product.GET_CHILD,
                                                                 Product.class);
        productNetworkQuery.setParameter("parent", product);
        productNetworkQuery.setParameter("relationship",
                                         transfromationMetarule.getRelationshipMap());
        // productNetworkQuery.setParameter("resource", transfromationMetarule.getProductNetworkResource());
        return productNetworkQuery.getSingleResult();
    }

    /**
     * @param transfromationMetarule
     * @param resource
     * @return
     */
    private Resource getMappedResource(TransformationMetarule transfromationMetarule,
                                       Resource resource) {
        TypedQuery<Resource> resourceNetworkQuery = em.createQuery(Resource.GET_CHILD,
                                                                   Resource.class);
        resourceNetworkQuery.setParameter("parent", resource);
        resourceNetworkQuery.setParameter("relationship",
                                          transfromationMetarule.getRelationshipMap());
        return resourceNetworkQuery.getSingleResult();
    }

    /**
     * @param service
     * @return
     */
    private List<TransformationMetarule> getTransformationMetarules(Product service) {
        TypedQuery<TransformationMetarule> txfmMetaruleQuery = em.createQuery(TransformationMetarule.GET_BY_EVENT,
                                                                              TransformationMetarule.class);
        txfmMetaruleQuery.setParameter("event", service);
        return txfmMetaruleQuery.getResultList();
    }

    /**
     * @param service
     * @param mappedResource
     * @param mappedProduct
     * @return
     */
    private List<Transformation> getTransformations(Product service,
                                                    Resource mappedResource,
                                                    Product mappedProduct) {
        TypedQuery<Transformation> txfmQuery = em.createQuery(Transformation.GET,
                                                              Transformation.class);
        txfmQuery.setParameter("event", service);
        txfmQuery.setParameter("product", mappedProduct);
        txfmQuery.setParameter("resource", mappedResource);

        return txfmQuery.getResultList();
    }

    /**
     * @param attribute
     * @param aspect
     */
    protected void initialize(Attribute attribute, Aspect<Attribute> aspect) {
        attribute.link(aspect.getClassification(), aspect.getClassifier(),
                       kernel.getCoreModel(), kernel.getInverseSoftware(), em);
        for (AttributeMetaAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
            AttributeMetaAttribute attr = new AttributeMetaAttribute(
                                                                     authorization.getAuthorizedAttribute(),
                                                                     kernel.getCoreModel());
            attr.setAttribute(attribute);
            defaultValue(attr);
            em.persist(attr);
        }
    }
}
