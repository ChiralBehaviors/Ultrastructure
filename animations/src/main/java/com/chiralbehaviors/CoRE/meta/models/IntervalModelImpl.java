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

import org.postgresql.pljava.TriggerData;

import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.jsp.JSP;
import com.chiralbehaviors.CoRE.jsp.StoredProcedure;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.kernel.KernelUtil;
import com.chiralbehaviors.CoRE.meta.IntervalModel;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Facet;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.time.Interval;
import com.chiralbehaviors.CoRE.time.IntervalAttribute;
import com.chiralbehaviors.CoRE.time.IntervalAttributeAuthorization;
import com.chiralbehaviors.CoRE.time.IntervalNetwork;

/**
 * @author hhildebrand
 *
 */
public class IntervalModelImpl
        extends
        AbstractNetworkedModel<Interval, IntervalNetwork, IntervalAttributeAuthorization, IntervalAttribute>
        implements IntervalModel {

    private static class Call<T> implements StoredProcedure<T> {
        private final Procedure<T> procedure;

        public Call(Procedure<T> procedure) {
            this.procedure = procedure;
        }

        @Override
        public T call(EntityManager em) throws Exception {
            return procedure.call(new IntervalModelImpl(em, JSP.getKernel()));
        }

        @Override
        public String toString() {
            return "Call [" + procedure + "]";
        }
    }

    private static interface Procedure<T> {
        T call(IntervalModelImpl productModel) throws Exception;
    }

    public static void propagate_deductions(final TriggerData data)
                                                                   throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(IntervalModelImpl agencyModel) throws Exception {
                agencyModel.propagate();
                return null;
            }

            @Override
            public String toString() {
                return "IntervalModel.propagate";
            }
        });
    }

    private static <T> T execute(Procedure<T> procedure) throws SQLException {
        return JSP.call(new Call<T>(procedure));
    }

    /**
     * @param em
     */
    public IntervalModelImpl(EntityManager em) {
        super(em, KernelUtil.getKernel());
    }

    /**
     * @param em
     */
    public IntervalModelImpl(EntityManager em, Kernel kernel) {
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
    public void authorize(Aspect<Interval> aspect, Attribute... attributes) {
        for (Attribute attribute : attributes) {
            IntervalAttributeAuthorization authorization = new IntervalAttributeAuthorization(
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
    public Interval create(Interval prototype) {
        Interval copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(kernel.getCoreModel());
        for (IntervalNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(), copy,
                                     kernel.getCoreModel(),
                                     kernel.getInverseSoftware(), em);
        }
        for (IntervalAttribute attribute : prototype.getAttributes()) {
            IntervalAttribute clone = (IntervalAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setInterval(copy);
            clone.setUpdatedBy(kernel.getCoreModel());
        }
        return copy;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#create(java.lang.String, java.lang.String, com.chiralbehaviors.CoRE.network.Aspect)
     */
    @Override
    public Facet<Interval, IntervalAttribute> create(String name,
                                                     String description,
                                                     Aspect<Interval> aspect) {
        Interval interval = new Interval(name, description,
                                         kernel.getCoreModel());
        em.persist(interval);
        return new Facet<Interval, IntervalAttribute>(aspect, interval,
                                                      initialize(interval,
                                                                 aspect)) {
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
    public final Interval create(String name, String description,
                                 Aspect<Interval> aspect,
                                 Aspect<Interval>... aspects) {
        Interval agency = new Interval(name, description, kernel.getCoreModel());
        em.persist(agency);
        initialize(agency, aspect);
        if (aspects != null) {
            for (Aspect<Interval> a : aspects) {
                initialize(agency, a);
            }
        }
        return agency;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getInterconnections(java.util.List, java.util.List, java.util.List)
     */
    @Override
    public List<IntervalNetwork> getInterconnections(Collection<Interval> parents,
                                                     Collection<Relationship> relationships,
                                                     Collection<Interval> children) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param agency
     * @param aspect
     */
    protected List<IntervalAttribute> initialize(Interval agency,
                                                 Aspect<Interval> aspect) {
        agency.link(aspect.getClassification(), aspect.getClassifier(),
                    kernel.getCoreModel(), kernel.getInverseSoftware(), em);
        List<IntervalAttribute> attributes = new ArrayList<>();
        for (IntervalAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
            IntervalAttribute attribute = new IntervalAttribute(
                                                                authorization.getAuthorizedAttribute(),
                                                                kernel.getCoreModel());
            attributes.add(attribute);
            attribute.setInterval(agency);
            defaultValue(attribute);
            em.persist(attribute);
        }
        return attributes;
    }
}
