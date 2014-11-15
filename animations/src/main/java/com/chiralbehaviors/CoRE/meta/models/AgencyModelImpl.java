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
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.postgresql.pljava.TriggerData;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyAttribute;
import com.chiralbehaviors.CoRE.agency.AgencyAttributeAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.jsp.JSP;
import com.chiralbehaviors.CoRE.jsp.StoredProcedure;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.KernelImpl;
import com.chiralbehaviors.CoRE.meta.AgencyModel;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Facet;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * @author hhildebrand
 *
 */
public class AgencyModelImpl
        extends
        AbstractNetworkedModel<Agency, AgencyNetwork, AgencyAttributeAuthorization, AgencyAttribute>
        implements AgencyModel {

    private static class Call<T> implements StoredProcedure<T> {
        private final Procedure<T> procedure;

        public Call(Procedure<T> procedure) {
            this.procedure = procedure;
        }

        @Override
        public T call(EntityManager em) throws Exception {
            return procedure.call(new AgencyModelImpl(em));
        }

        @Override
        public String toString() {
            return "Call [" + procedure + "]";
        }
    }

    private static interface Procedure<T> {
        T call(AgencyModel productModel) throws Exception;
    }

    public static void propagate_deductions(final TriggerData data)
                                                                   throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(AgencyModel agencyModel) throws Exception {
                agencyModel.propagate();
                return null;
            }

            @Override
            public String toString() {
                return "AgencyModel.propagate";
            }
        });
    }

    private static <T> T execute(Procedure<T> procedure) throws SQLException {
        return JSP.call(new Call<T>(procedure));
    }

    /**
     * @param em
     */
    public AgencyModelImpl(EntityManager em) {
        super(em, KernelImpl.getKernel(em));
    }

    /**
     * @param em
     */
    public AgencyModelImpl(EntityManager em, Kernel kernel) {
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
    public void authorize(Aspect<Agency> aspect, Attribute... attributes) {
        for (Attribute attribute : attributes) {
            AgencyAttributeAuthorization authorization = new AgencyAttributeAuthorization(
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
    public Agency create(Agency prototype) {
        Agency copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(kernel.getCoreModel());
        for (AgencyNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(), copy,
                                     kernel.getCoreModel(),
                                     kernel.getInverseSoftware(), em);
        }
        for (AttributeValue<Agency> attribute : prototype.getAttributes()) {
            AgencyAttribute clone = (AgencyAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setAgency(copy);
            clone.setUpdatedBy(kernel.getCoreModel());
        }
        return copy;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#create(java.lang.String, java.lang.String, com.chiralbehaviors.CoRE.network.Aspect)
     */
    @Override
    public Facet<Agency, AgencyAttribute> create(String name,
                                                 String description,
                                                 Aspect<Agency> aspect) {
        Agency agency = new Agency(name, description, kernel.getCoreModel());
        em.persist(agency);
        return new Facet<Agency, AgencyAttribute>(aspect, agency,
                                                  initialize(agency, aspect)) {
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
    public final Agency create(String name, String description,
                               Aspect<Agency> aspect, Aspect<Agency>... aspects) {
        Agency agency = new Agency(name, description, kernel.getCoreModel());
        em.persist(agency);
        initialize(agency, aspect);
        if (aspects != null) {
            for (Aspect<Agency> a : aspects) {
                initialize(agency, a);
            }
        }
        return agency;
    }

    @Override
    public List<AgencyNetwork> getInterconnections(Collection<Agency> parents,
                                                   Collection<Relationship> relationships,
                                                   Collection<Agency> children) {
        if (parents == null || parents.size() == 0 || relationships == null
            || relationships.size() == 0 || children == null
            || children.size() == 0) {
            return null;
        }
        TypedQuery<AgencyNetwork> query = em.createNamedQuery(AgencyNetwork.GET_NETWORKS,
                                                              AgencyNetwork.class);
        query.setParameter("parents", parents);
        query.setParameter("relationships", relationships);
        query.setParameter("children", children);
        return query.getResultList();
    }

    /**
     * @param agency
     * @param aspect
     */
    protected List<AgencyAttribute> initialize(Agency agency,
                                               Aspect<Agency> aspect) {
        List<AgencyAttribute> attributes = new ArrayList<>();
        agency.link(aspect.getClassification(), aspect.getClassifier(),
                    kernel.getCoreModel(), kernel.getInverseSoftware(), em);
        for (AgencyAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
            AgencyAttribute attribute = new AgencyAttribute(
                                                            authorization.getAuthorizedAttribute(),
                                                            kernel.getCoreModel());
            attributes.add(attribute);
            attribute.setAgency(agency);
            defaultValue(attribute);
            em.persist(attribute);
        }
        return attributes;
    }
}
