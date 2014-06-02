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

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeMetaAttribute;
import com.chiralbehaviors.CoRE.attribute.AttributeMetaAttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.AttributeNetwork;
import com.chiralbehaviors.CoRE.attribute.Transformation;
import com.chiralbehaviors.CoRE.attribute.TransformationMetarule;
import com.chiralbehaviors.CoRE.jsp.JSP;
import com.chiralbehaviors.CoRE.jsp.StoredProcedure;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.KernelImpl;
import com.chiralbehaviors.CoRE.meta.AttributeModel;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Facet;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductAttribute;

/**
 * @author hhildebrand
 * 
 */
public class AttributeModelImpl
        extends
        AbstractNetworkedModel<Attribute, AttributeNetwork, AttributeMetaAttributeAuthorization, AttributeMetaAttribute>
        implements AttributeModel {

    private static class Call<T> implements StoredProcedure<T> {
        private final Procedure<T> procedure;

        public Call(Procedure<T> procedure) {
            this.procedure = procedure;
        }

        @Override
        public T call(EntityManager em) throws Exception {
            return procedure.call(new AttributeModelImpl(em));
        }

        @Override
        public String toString() {
            return "Call [" + procedure + "]";
        }
    }

    private static interface Procedure<T> {
        T call(AttributeModel attributeModel) throws Exception;
    }

    public static void propagate_deductions(final TriggerData data)
                                                                   throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(AttributeModel attributeModel) throws Exception {
                attributeModel.propagate();
                return null;
            }

            @Override
            public String toString() {
                return "AttributeModel.propagate";
            }
        });
    }

    private static <T> T execute(Procedure<T> procedure) throws SQLException {
        return JSP.call(new Call<T>(procedure));
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#authorize(com.chiralbehaviors.CoRE
     * .meta.Aspect, com.chiralbehaviors.CoRE.attribute.Attribute[])
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(com.chiralbehaviors.CoRE.network
     * .Networked)
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

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#create(java.lang.String, java.lang.String, com.chiralbehaviors.CoRE.network.Aspect)
     */
    @Override
    public Facet<Attribute, AttributeMetaAttribute> create(String name,
                                                           String description,
                                                           Aspect<Attribute> aspect) {

        Attribute attribute = new Attribute(name, description,
                                            kernel.getCoreModel());
        em.persist(attribute);
        return new Facet<Attribute, AttributeMetaAttribute>(
                                                            aspect,
                                                            attribute,
                                                            initialize(attribute,
                                                                       aspect)) {
        };
    }

    @SafeVarargs
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

    @Override
    public List<AttributeNetwork> getInterconnections(List<Attribute> parents,
                                                      List<Relationship> relationships,
                                                      List<Attribute> children) {
        if (parents == null || parents.size() == 0 || relationships == null
            || relationships.size() == 0 || children == null
            || children.size() == 0) {
            return null;
        }
        TypedQuery<AttributeNetwork> query = em.createNamedQuery(AttributeNetwork.GET_NETWORKS,
                                                                 AttributeNetwork.class);
        query.setParameter("parents", parents);
        query.setParameter("relationships", relationships);
        query.setParameter("children", children);
        return query.getResultList();
    }

    public Attribute transform(Product service, Agency agency, Product product) {

        Attribute txfmd = null;
        for (TransformationMetarule transfromationMetarule : getTransformationMetarules(service)) {
            Agency mappedAgency;
            if (kernel.getSameAgency().equals(transfromationMetarule.getRelationshipMap())) {
                mappedAgency = kernel.getSameAgency();
            } else {
                mappedAgency = getMappedAgency(transfromationMetarule, agency);
            }
            Product mappedProduct;
            if (kernel.getSameProduct().equals(transfromationMetarule.getProductMap())) {
                mappedProduct = kernel.getSameProduct();
            } else {
                mappedProduct = getMappedProduct(transfromationMetarule,
                                                 product);
            }
            for (Transformation transformation : getTransformations(service,
                                                                    mappedAgency,
                                                                    mappedProduct)) {
                txfmd = null;
                Agency txfmAgency;
                if (kernel.getOriginalAgency().equals(transformation.getAgencyKey())) {
                    txfmAgency = agency;
                } else {
                    txfmAgency = transformation.getAgencyKey();
                }
                Product txfmProduct;
                if (kernel.getOriginalProduct().equals(transformation.getProductKey())) {
                    txfmProduct = product;
                } else {
                    txfmProduct = transformation.getProductKey();
                }
                Product foundProduct = findProduct(transformation, txfmAgency,
                                                   txfmProduct);

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
        TypedQuery<Attribute> attrQuery = em.createNamedQuery(ProductAttribute.FIND_ATTRIBUTE_VALUE_FROM_AGENCY,
                                                              Attribute.class);
        attrQuery.setParameter("agency",
                               transformation.getProductAttributeAgency());
        attrQuery.setParameter("product", product);
        attrQuery.setParameter("attribute", transformation.getAttribute());
        return attrQuery.getSingleResult();
    }

    /**
     * @param transformation
     * @param agency
     * @param product
     * @return
     */
    private Product findProduct(Transformation transformation, Agency agency,
                                Product product) {
        TypedQuery<Product> productNetworkQuery = em.createQuery(Product.GET_CHILDREN,
                                                                 Product.class);
        productNetworkQuery.setParameter("parent", product);
        productNetworkQuery.setParameter("relationship",
                                         transformation.getRelationshipKey());
        return productNetworkQuery.getSingleResult();
    }

    /**
     * @param transfromationMetarule
     * @param agency
     * @return
     */
    private Agency getMappedAgency(TransformationMetarule transfromationMetarule,
                                   Agency agency) {
        TypedQuery<Agency> agencyNetworkQuery = em.createQuery(Agency.GET_CHILD,
                                                               Agency.class);
        agencyNetworkQuery.setParameter("parent", agency);
        agencyNetworkQuery.setParameter("relationship",
                                        transfromationMetarule.getRelationshipMap());
        return agencyNetworkQuery.getSingleResult();
    }

    /**
     * @param transfromationMetarule
     * @param product
     * @return
     */
    private Product getMappedProduct(TransformationMetarule transfromationMetarule,
                                     Product product) {
        TypedQuery<Product> productNetworkQuery = em.createQuery(Product.GET_CHILDREN,
                                                                 Product.class);
        productNetworkQuery.setParameter("parent", product);
        productNetworkQuery.setParameter("relationship",
                                         transfromationMetarule.getRelationshipMap());
        return productNetworkQuery.getSingleResult();
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
     * @param mappedAgency
     * @param mappedProduct
     * @return
     */
    private List<Transformation> getTransformations(Product service,
                                                    Agency mappedAgency,
                                                    Product mappedProduct) {
        TypedQuery<Transformation> txfmQuery = em.createQuery(Transformation.GET,
                                                              Transformation.class);
        txfmQuery.setParameter("event", service);
        txfmQuery.setParameter("product", mappedProduct);
        txfmQuery.setParameter("agency", mappedAgency);

        return txfmQuery.getResultList();
    }

    /**
     * @param attribute
     * @param aspect
     */
    protected List<AttributeMetaAttribute> initialize(Attribute attribute,
                                                      Aspect<Attribute> aspect) {
        List<AttributeMetaAttribute> attrs = new ArrayList<>();
        attribute.link(aspect.getClassification(), aspect.getClassifier(),
                       kernel.getCoreModel(), kernel.getInverseSoftware(), em);
        for (AttributeMetaAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
            AttributeMetaAttribute attr = new AttributeMetaAttribute(
                                                                     authorization.getAuthorizedAttribute(),
                                                                     kernel.getCoreModel());
            attrs.add(attr);
            attr.setAttribute(attribute);
            defaultValue(attr);
            em.persist(attr);
        }
        return attrs;
    }
}
